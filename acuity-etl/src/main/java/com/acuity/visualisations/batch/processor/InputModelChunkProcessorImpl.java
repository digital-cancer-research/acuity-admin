/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.batch.processor;

import com.acuity.visualisations.batch.holders.CntlinDataHolder;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.IColumnRule;
import com.acuity.visualisations.batch.holders.configuration.SdtmDataHolder;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolder;
import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.model.input.InputModelChunk;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.model.output.OutputEntityUtil;
import com.acuity.visualisations.model.output.OutputModelChunk;
import com.acuity.visualisations.model.output.OutputModelChunkImpl;
import com.acuity.visualisations.model.output.PivotableEntity;
import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.output.SplitEntity;
import com.acuity.visualisations.model.output.entities.AdverseEvent;
import com.acuity.visualisations.model.output.entities.ConcomitantMedSchedule;
import com.acuity.visualisations.model.output.entities.Laboratory;
import com.acuity.visualisations.model.output.entities.MedDosingSchedule;
import com.acuity.visualisations.model.output.entities.PatientData;
import com.acuity.visualisations.model.output.entities.Source;
import com.acuity.visualisations.model.output.entities.TimestampedEntity;
import com.acuity.visualisations.sdtm.SdtmEntityProcessor;
import com.acuity.visualisations.service.IExecutionProfiler;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.function.AbstractFunction;
import com.acuity.visualisations.transform.function.DateAssemblerDefaultTime;
import com.acuity.visualisations.transform.function.Functions;
import com.acuity.visualisations.transform.parser.AbstractParser;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;
import com.acuity.visualisations.util.Pair;
import com.acuity.visualisations.util.ReflectionUtil;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.acuity.visualisations.data.util.Util.isEmpty;
import static com.acuity.visualisations.data.util.Util.isTrue;
import static com.acuity.visualisations.model.output.entities.Laboratory.SOURCE_PATIENT;

@Component("processor")
@Scope("step")
public class InputModelChunkProcessorImpl extends HoldersAware implements InputModelChunkProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputModelChunkProcessorImpl.class);

    private ConfigurationUtil<?> configurationUtil;

    private StaticConfigurationHolder staticConfigurationHolder;

    private CntlinDataHolder cntlinDataHolder;

    private DataCommonReport dataCommonReport;

    private Set<Integer> excludedRows = new HashSet<>();

    private SdtmDataHolder sdtmDataHolder;

    @Override
    protected void initHolders() {

        configurationUtil = getConfigurationUtil();
        staticConfigurationHolder = getStaticConfigurationHolder();
        cntlinDataHolder = getCntlinDataHolder();
        dataCommonReport = getDataCommonReport();
        sdtmDataHolder = getSdtmDataHolder();
    }

    /**
     * Profile performance
     */
    @Autowired
    private IExecutionProfiler executionProfiler;


    public OutputModelChunk process(InputModelChunk inputModelChunk) throws Exception {
        excludedRows = new HashSet<>();
        String file = inputModelChunk.getSourceName();

        if (sdtmDataHolder.containsMainData(file)) {
            try {
                List<String> entityNames = configurationUtil.getEntities(file);
                SdtmEntityProcessor sdtmEntityProcessor = new SdtmEntityProcessor(getSdtmDataHolder());
                OutputModelChunk output = sdtmEntityProcessor.processSdtm(file, entityNames);

                for (OutputEntity outputEntity : output.getEntities()) {
                    completeEntity(outputEntity, file, 0);
                }
                return output;
            } catch (Exception e) {
                LOGGER.error("Error processing SDTM entities", e);
            }
        }

        LOGGER.debug("Processing {} items chunk", inputModelChunk.getRecords().size());
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process");
        try {
            checkMandatoryEntitiesProperlyRead();
            return process1(inputModelChunk);
        } catch (Exception e) {
            throw new Exception(String.format("Error processing file %s : %s", inputModelChunk.getSourceName(), e.getMessage()), e);
        } finally {
            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process");
        }
    }

    private void checkMandatoryEntitiesProperlyRead() {
        final List<EntityDescriptionRule> skippedMandatoryEntities = configurationUtil
                .getSkippedEntities()
                .stream()
                .map(skippedEntity -> staticConfigurationHolder.getEntityRule(skippedEntity))
                .filter(Objects::nonNull)
                .filter(EntityDescriptionRule::isMandatory)
                .collect(Collectors.toList());
        if (!skippedMandatoryEntities.isEmpty()) {
            final Stream<String> skippedEntityNames = skippedMandatoryEntities.stream().map(EntityDescriptionRule::getName);
            throw new IllegalArgumentException("Mandatory info is not provided for entities: "
                    + String.join(",", skippedEntityNames.collect(Collectors.toList())));
        }
    }

    private void completeEntity(OutputEntity entity, String sourceName, int rowNumber) throws Exception {
        entity.setProjectName(getProjectName());
        entity.setStudyName(getStudyName());
        entity.setSourceName(sourceName);
        entity.setRowNumber(rowNumber);
        EntityDescriptionRule entityDescriptionRule = staticConfigurationHolder.getEntityRule(entity.getClass().getSimpleName());
        OutputEntityUtil.setSha1(entity, entityDescriptionRule);
    }

    public OutputModelChunk process1(InputModelChunk inputModelChunk) throws Exception {
        String sourceName = inputModelChunk.getSourceName();
        List<String> entityNames = configurationUtil.getEntities(sourceName);
        OutputModelChunk outputModelChunk = new OutputModelChunkImpl();
        int offSet = 0;
        List<OutputEntity> entities = new ArrayList<>();
        for (String entityName : entityNames) {
            List<? extends IColumnRule> columns = configurationUtil.getColumns(sourceName, entityName);
            if (columns == null) {
                continue;
            }
            dataCommonReport.getFileReport(sourceName).addAcuityEntity(entityName);
            Class<?> entityClass = ReflectionUtil.getEntityClass(entityName);
            for (Map.Entry<Integer, String[]> recordEntry : inputModelChunk.getRecords().entrySet()) {
                String[] record = recordEntry.getValue();
                List<OutputEntity> outputEntities = createOutputEntity(sourceName, entityName, entityClass, record, inputModelChunk.getColumnsToSkip(),
                        recordEntry.getKey(), offSet);
                executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-int1");
                if (outputEntities != null) {
                    for (OutputEntity entity : outputEntities) {
                        String entityStudyName = entity.getStudyName();
                        if ((entityStudyName == null || !entityStudyName.equals(getStudyName()))) {
                            String message = String.format("Study code (%s) of the record in doesn't match target study.", entityStudyName);
                            IColumnRule studyCol = configurationUtil.getStudyColumn(sourceName, entityName);
                            if (studyCol != null) {
                                dataCommonReport.getFileReport(sourceName).addValueError(studyCol.getName(),
                                        entityStudyName, true, message);
                            }
                        }
                        entity.setProjectName(getProjectName());
                        entity.setStudyName(getStudyName());
                        entity.setSourceName(sourceName);
                        entity.setRowNumber(recordEntry.getKey());

                        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-setSha");
                        entities.addAll(extractEntitiesIfRequired(entity, sourceName, recordEntry.getKey()));
                        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-setSha");
                    }
                }
                executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-int1");
            }
            offSet += columns == null ? 0 : columns.size();
        }
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-int2");
        for (OutputEntity entity : entities) {
            if (entity.isValid() && !excludedRows.contains(entity.getRowNumber())) {
                outputModelChunk.addEntity(entity);
            }
        }
        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.process-int2");
        return outputModelChunk;
    }

    /**
     * This methods does processing which is specific to some type of entities. Some files (like for {@link AdverseEvent})
     * can contain in one row information for several related entities.
     */
    private List<OutputEntity> extractEntitiesIfRequired(OutputEntity entity, String sourceName, Integer row) throws Exception {
        List<OutputEntity> entities = new LinkedList<>();
        if (entity instanceof AdverseEvent) {

            List<TimestampedEntity> miscEntities = AeSplitter.splitAdverseEventToEntities((AdverseEvent) entity);
            for (TimestampedEntity miscEntity : miscEntities) {
                completeEntity(miscEntity, sourceName, row);
                entities.add(miscEntity);
            }

        } else if (entity instanceof MedDosingSchedule) {
            addEntityWithSha1(entities, entity);

            List<TimestampedEntity> aeNumEntities = AeSplitter.splitAeNumValues((MedDosingSchedule) entity);
            for (TimestampedEntity e : aeNumEntities) {
                completeEntity(e, sourceName, row);
                entities.add(e);
            }
        } else if (entity instanceof ConcomitantMedSchedule) {
            List<TimestampedEntity> conmedEntities = AeSplitter.splitAeNumConmeds((ConcomitantMedSchedule) entity);
            for (TimestampedEntity e : conmedEntities) {
                addEntityWithSha1(entities, e);
            }
        } else if (entity instanceof Laboratory) {
            Laboratory e = (Laboratory) entity;
            addEntityWithSha1(entities, entity);
            addSource(e.getSourceType(),
                    e.getSourceDeviceName(),
                    e.getSourceDeviceType(),
                    e.getSourceDeviceVersion(),
                    sourceName,
                    row,
                    entities);

        } else if (entity instanceof PatientData) {
            PatientData e = (PatientData) entity;
            addEntityWithSha1(entities, entity);
            addSource(e.getSourceType(),
                    e.getSourceDeviceName(),
                    e.getSourceDeviceType(),
                    e.getSourceDeviceVersion(),
                    sourceName,
                    row,
                    entities);
        } else {
            addEntityWithSha1(entities, entity);
        }
        return entities;
    }

    private void addSource(String sourceType,
                           String sourceDeviceName,
                           String sourceDeviceType,
                           String sourceDeviceVersion,
                           String sourceName,
                           Integer row,
                           List<OutputEntity> entities) throws Exception {
        if (SOURCE_PATIENT.equals(sourceType)
                && !(sourceDeviceName == null && sourceDeviceType == null && sourceDeviceVersion == null)) {
            Source source = new Source(sourceDeviceName, sourceDeviceVersion, sourceDeviceType);
            completeEntity(source, sourceName, row);
            addEntityWithSha1(entities, source);
        }
    }

    private List<OutputEntity> createOutputEntity(String sourceName, String entityName, Class<?> entityClass, String[] record,
                                                  Set<String> columnsToSkip, int rowNumber, int offSet) throws Exception {

        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity");

        class CompoundFields {
            private List<IColumnRule> columnRules = new ArrayList<>();
            private List<Object> compoundValues = new ArrayList<>();

            void addEntry(IColumnRule columnRule, Object compoundValue) {
                columnRules.add(columnRule);
                compoundValues.add(compoundValue);
            }
        }

        Map<String, Pair<String, Object>> pivotedValues = new HashMap<>();

        OutputEntity entity = (OutputEntity) entityClass.newInstance();

        Map<String, CompoundFields> compoundFieldsParts = new HashMap<>();
        int missedColumnsOffset = 0;
        List<? extends IColumnRule> columns = configurationUtil.getColumns(sourceName, entityName);
        if (columns == null) {
            return null;
        }
        for (int i = offSet; i < columns.size() + offSet; i++) {
            IColumnRule columnRule = columns.get(i - offSet);
            if (columnsToSkip.contains(columnRule.getName())) {
                missedColumnsOffset++;
                continue;
            }

            int recordIndex = i - missedColumnsOffset;

            executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int1");
            String recValue = recordIndex >= 0 && recordIndex < record.length ? record[recordIndex] : null;
            for (String value : columnRule.getExcludingValues()) {
                if ((recValue != null && value != null
                        && value.toUpperCase().equals(recValue.toUpperCase()))
                        || ("NULL".equalsIgnoreCase(value) && (isEmpty(recValue)))) {
                    excludedRows.add(rowNumber);
                }
            }
            entity.addSourceColumn(columnRule.getName(), recValue);

            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int1");
            executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int2");
            Object merged = mergeWithCntlin(columnRule, recValue, sourceName, rowNumber, true);
            if (merged == null || "NULL".equalsIgnoreCase((String) merged)) {
                recValue = null;
            } else {
                recValue = (String) merged;
            }

            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int2");
            executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int3");
            Object value = null;
            String type = columnRule.getType();
            Mapper mapper = columnRule.getMapper();
            ParserRule helper = columnRule.getHelper();
            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int3");
            executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-report");
            if (columnRule.getSubjectField()) {
                dataCommonReport.getFileReport(sourceName).addParsedSubject(recValue);
            }
            dataCommonReport.getFileReport(sourceName).incColumnParsed(columnRule.getName());

            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-report");

            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int7");
            // try to use mapper function
            String functionName = columnRule.getAggrFunction();

            AbstractParser<?> parser = ReflectionUtil.getParser(getJobExecutionId(), sourceName, columnRule.getName(), type, mapper, helper);

            try {
                value = parser.parse(recValue);
            } catch (InvalidDataFormatException e) {
                dataCommonReport.getFileReport(sourceName).addValueError(columnRule.getName(), recValue, true,
                        "Error parsing value of expected type \"" + type + "\": " + e.getMessage());
            }

            if (!isTrue(columnRule.isPart())) {
                Object value1;
                if (value instanceof Temporal) {
                    if (value instanceof LocalDate) {
                        value1 = Date.from(((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    } else {
                        if (value instanceof LocalDateTime) {
                            value1 = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
                        } else {
                            value1 = null;
                        }
                    }
                } else {
                    value1 = value;
                }
                setFieldValue(entity, columnRule, value1, false, pivotedValues);
            } else {
                if (!compoundFieldsParts.containsKey(columnRule.getField())) {
                    compoundFieldsParts.put(columnRule.getField(), new CompoundFields());
                }
                compoundFieldsParts.get(columnRule.getField()).addEntry(columnRule, value);
            }

            if (functionName != null) {
                if (compoundFieldsParts.get(columnRule.getField()) != null) {
                    AbstractFunction aggregatorFunction = Functions.getAggregator(functionName);
                    List<Object> compoundValues = compoundFieldsParts.get(columnRule.getField()).compoundValues;
                    if (aggregatorFunction instanceof DateAssemblerDefaultTime && !isEmpty(columnRule.getDefault())) {
                        Object defTime = null;
                        try {
                            defTime = parser.parse(columnRule.getDefault());
                        } catch (InvalidDataFormatException ignored) {

                        }
                        compoundValues.add(defTime);
                    }
                    Object[] params = compoundValues.toArray();
                    Object functionedValue;
                    if (aggregatorFunction != null) {
                        functionedValue = aggregatorFunction.function(params);
                        functionedValue = mergeWithCntlinArray(columnRule, functionedValue, sourceName, rowNumber, false);
                        setFieldValue(entity, columnRule, functionedValue, true, pivotedValues);
                        compoundFieldsParts.remove(columnRule.getField());

                    }
                } else {
                    executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.setFieldValue-2");
                    setFieldValue(entity, columnRule, null, false, pivotedValues);
                    executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.setFieldValue-2");
                }
            }
            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity-int7");
        }

        for (Map.Entry<String, CompoundFields> entry : compoundFieldsParts.entrySet()) {
            CompoundFields fields = entry.getValue();
            IColumnRule columnRule = fields.columnRules.get(0);
            Object value = fields.compoundValues.get(0);
            value = mergeWithCntlin(columnRule, value, sourceName, rowNumber, false);
            setFieldValue(entity, columnRule, value, false, pivotedValues);
            debug("Unnecessary part attribute for Field: {}, Entity: {}, Study: {}, Project: {}. ", columnRule.getField(),
                    entityName, getStudyName(), getProjectName());
        }

        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkProcessorImpl.createOutputEntity");

        List<OutputEntity> outputEntities = new ArrayList<>(1);

        if (entity instanceof PivotableEntity && !pivotedValues.isEmpty()) {
            pivotedValues.entrySet().stream().forEach(entry -> {
                try {
                    PivotableEntity pivotedEntity = (PivotableEntity) entity.clone();
                    pivotedEntity.setPivotedCategoryValue(entry.getValue().getA(), entry.getKey(), entry.getValue().getB());
                    outputEntities.add((OutputEntity) pivotedEntity);
                } catch (Exception e) {
                    error("Exception caught when assigning pivoted entity value: {}", e.getMessage());
                    LOGGER.error("Exception caught when assigning pivoted entity value: ", e);
                }
            });
        } else if (entity instanceof SplitEntity) {
            if (entity instanceof SmartEntity) {
                ((SmartEntity) entity).complete();
            }
            outputEntities.addAll(((SplitEntity) entity).split());
        } else {
            outputEntities.add(entity);
        }

        for (OutputEntity outputEntity : outputEntities) {
            if (outputEntity instanceof SmartEntity) {
                ((SmartEntity) outputEntity).complete();
            }
        }
        return outputEntities;
    }

    private Object mergeWithCntlinArray(IColumnRule columnRule, Object value, String sourceName, int rowNumber, boolean isPart) {
        if (value != null && value.getClass().isArray()) {
            Object[] in = (Object[]) value;

            if (in.length > 0 && !isEmpty(columnRule.getFmtname())) {
                Object[] out = (Object[]) Array.newInstance(in.getClass().getComponentType(), in.length);
                for (int i = 0; i < in.length; i++) {
                    out[i] = mergeWithCntlin(columnRule, in[i], sourceName, rowNumber, isPart);
                }
                return out;
            } else {
                return value;
            }
        }
        return mergeWithCntlin(columnRule, value, sourceName, rowNumber, isPart);
    }

    private Object mergeWithCntlin(IColumnRule columnRule, Object value, String sourceName, int rowNumber, boolean isPart) {
        if (isEmpty(columnRule.getFmtname())) {
            return value;
        }
        if (isTrue(columnRule.isPart()) && isPart) {
            return value;
        }
        if (value == null) {
            return value;
        }
        Object result = value;
        if (!(value instanceof String)) {
            value = String.valueOf(value);
        }
        String valueStr = (String) value;
        String merged = cntlinDataHolder.getValue(columnRule.getFmtname(), valueStr);
        if (merged != null) {
            result = merged;
        } else if (!isEmpty(columnRule.getFmtdefault())) {
            result = columnRule.getFmtdefault().trim();
        }
        if (result != null) {
            result = String.valueOf(result);
        }

        return result;
    }

    private void setFieldValue(OutputEntity entity, IColumnRule columnRule, Object value, boolean isCompound,
                               Map<String, Pair<String, Object>> pivotedValues)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        if (entity instanceof PivotableEntity && ((PivotableEntity) entity).isPivotedField(columnRule.getField())) {
            if (value != null) {
                pivotedValues.put(columnRule.getDescription(), new Pair<>(columnRule.getField(), value));
            }
        } else {
            ReflectionUtil.setFieldValue(entity, columnRule.getField(), value, isCompound);
        }
    }

    private void addEntityWithSha1(List<OutputEntity> entities, OutputEntity entity) throws Exception {
        EntityDescriptionRule entityDescriptionRule = staticConfigurationHolder.getEntityRule(entity.getClass().getSimpleName());
        OutputEntityUtil.setSha1(entity, entityDescriptionRule);
        entities.add(entity);
    }
}
