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

package com.acuity.visualisations.batch.writer;

import com.acuity.visualisations.batch.holders.EOFEventHolder;
import com.acuity.visualisations.batch.holders.HashValuesHolder;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolder;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.model.output.OutputModelChunk;
import com.acuity.visualisations.service.IExecutionProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("writer")
@Scope("step")
public class InputModelChunkWriterImpl extends HoldersAware implements InputModelChunkWriter {

    private final class OutputEntityComparator implements Comparator<Class<?>> {

        private List<String> entityNames;

        private OutputEntityComparator() {
            entityNames = configurationUtil.getEntityNames();
        }

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            Integer index1 = entityNames.indexOf(o1.getSimpleName());
            Integer index2 = entityNames.indexOf(o2.getSimpleName());
            if (index1 == -1) {
                index1 = Integer.MAX_VALUE;
            }
            if (index2 == -1) {
                index2 = Integer.MAX_VALUE;
            }
            return index1.compareTo(index2);
        }

    }

    private EOFEventHolder eofEventHolder;

    @Autowired
    private EntityManager entityManager;

    private ForeignKeyService foreignKeyService;

    private HashValuesHolder hashValuesHolder;

    private ConfigurationUtil<?> configurationUtil;

    private StaticConfigurationHolder staticConfigurationHolder;

    private BDService bdService;

    @Override
    protected void initHolders() {
        configurationUtil = getConfigurationUtil();
        staticConfigurationHolder = getStaticConfigurationHolder();
        eofEventHolder = getEOFEventHolder();
        hashValuesHolder = getHashValuesHolder();
        foreignKeyService = new ForeignKeyService(entityManager, configurationUtil, staticConfigurationHolder, hashValuesHolder,
                new OutputEntityComparator());
        List<String> parentEntities = configurationUtil.getParentEntities();
        String[] dataFileNames = configurationUtil.getDataFileNames();
        bdService = new BDService(getJobExecutionId(), parentEntities, dataFileNames, new OutputEntityComparator(),
                hashValuesHolder, entityManager, executionProfiler);
    }

    /**
     * Profile performance
     */
    @Autowired
    private IExecutionProfiler executionProfiler;

    public void write(List<? extends OutputModelChunk> outputModelChunks) throws Exception {
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write");
        try {
            write1(outputModelChunks);
        } finally {
            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write");
        }
    }

    public void write1(List<? extends OutputModelChunk> outputModelChunks) throws Exception {
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-int1");
        List<OutputEntity> entitiesList = new ArrayList<>();
        for (OutputModelChunk chunk : outputModelChunks) {
            entitiesList.addAll(chunk.getEntities());
        }
        Map<Class<?>, Map<String, List<OutputEntity>>> groupedEntities = WriterUtil.groupByUniqueHash(entitiesList);
        Map<Class<?>, Map<String, OutputEntity>> mergeEntities = mergeEntities(groupedEntities);
        List<OutputEntity> toInsert = new ArrayList<>();
        Map<Class<?>, Map<String, OutputEntity>> toInsertMerged = new LinkedHashMap<>();
        List<OutputEntity> toUpdate = new ArrayList<>();
        List<OutputEntity> toUpdateState = new ArrayList<>();
        List<OutputEntity> allEntities = new ArrayList<>();
        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-int1");
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-int2");
        for (Map.Entry<Class<?>, Map<String, OutputEntity>> entry : mergeEntities.entrySet()) {
            try {
                Class<?> entityClass = entry.getKey();
                Map<String, OutputEntity> entitiesByHash = entry.getValue();

                executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-getHashValues");
                Map<OctetString, RowParameters> hashesByEntity = hashValuesHolder.getHashValues(entityClass);
                executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-getHashValues");

                for (Map.Entry<String, OutputEntity> entry2 : entitiesByHash.entrySet()) {
                    try {
                        OctetString hash = new OctetString(entry2.getKey());
                        OutputEntity entity = entry2.getValue();
                        if (hashesByEntity.containsKey(hash)) {
                            RowParameters rowParameters = hashesByEntity.get(hash);
                            int secondaryHash = rowParameters.getSecondaryHash();
                            int entitySecondaryHash = entity.getIntHashForSecondaryFields();

                            if (secondaryHash == entitySecondaryHash) {
                                entity.setId(rowParameters.getId());
                                if (rowParameters.getState() != null) {
                                    switch (rowParameters.getState()) {
                                        case OLD:
                                        case JUST_UPDATED:
                                        case SYNCHRONIZED:
                                        case JUST_INSERTED:
                                        case TMP_JUST_UPDATED:
                                            entity.setState(State.TMP_SYNCHRONIZED);
                                            toUpdateState.add(entity);
                                            break;
                                        case TMP_JUST_INSERTED:
                                        case TMP_SYNCHRONIZED:
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else {
                                entity.setId(rowParameters.getId());
                                if (rowParameters.getState() != null) {
                                    switch (rowParameters.getState()) {
                                        case TMP_JUST_INSERTED:
                                            entity.setState(State.TMP_JUST_INSERTED);
                                            break;
                                        default:
                                            entity.setState(State.TMP_JUST_UPDATED);
                                            break;
                                    }
                                }
                                toUpdate.add(entity);
                                getDataCommonReport().getFileReport(entity.getSourceName()).incRowsUploaded();
                            }
                        } else {
                            entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                            entity.setState(State.TMP_JUST_INSERTED);
                            toInsert.add(entity);
                            toInsertMerged.computeIfAbsent(entityClass, c -> new HashMap<>())
                                    .put(entity.getSha1ForUniqueFields(), entity);
                            getDataCommonReport().getFileReport(entity.getSourceName()).incRowsUploaded();
                        }
                        allEntities.add(entity);

                    } catch (Exception e) {
                        debug(String.format("Error caught preparing writing data for item %s : %s (%s)",
                                entry2.getValue().allFieldsToString(), e.getMessage(), e.getClass().getName()));
                        throw e;
                    }
                }
            } catch (Exception e) {
                debug(String.format("Error caught preparing writing data for entity %s : %s (%s)",
                        entry.getKey().getName(), e.getMessage(), e.getClass().getName()));
                throw e;
            }
        }
        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-int2");
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys");
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys-1");

        List<OutputEntity> fkFailedEntities = foreignKeyService.setForeignKeys(toInsert, toInsertMerged);
        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys-1");
        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys-2");
        Map<Class<?>, Map<String, List<OutputEntity>>> groupFailedByUniqueHash = WriterUtil.groupByUniqueHash(fkFailedEntities);
        Iterator<OutputEntity> toInsertIterator = toInsert.iterator();
        while (toInsertIterator.hasNext()) {
            OutputEntity outputEntity = toInsertIterator.next();
            Class<? extends OutputEntity> entityClass = outputEntity.getClass();
            if (groupFailedByUniqueHash.containsKey(entityClass)) {
                String hash = outputEntity.getSha1ForUniqueFields();
                if (groupFailedByUniqueHash.get(entityClass).containsKey(hash)) {
                    toInsertIterator.remove();
                }
            }
        }
        refreshHashValueHolder(toInsert);
        refreshHashValueHolder(toUpdate);
        refreshHashValueHolder(toUpdateState);

        //RCT-3687
        List<OutputEntity> failedFksToUpdate = foreignKeyService.validateFKsInHashes(toUpdate);
        toUpdate.removeAll(failedFksToUpdate);
        List<OutputEntity> failedFksToUpdateState = foreignKeyService.validateFKsInHashes(toUpdateState);
        toUpdateState.removeAll(failedFksToUpdateState);


        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys-2");
        executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkWriterImpl.write-setForeignKeys");

        debug("Inserting: {} , updating: {}, updating state: {}", toInsert.size(), toUpdate.size(), toUpdateState.size());
        bdService.addEntitiesToInsert(toInsert);
        bdService.addEntitiesToUpdate(toUpdate);
        bdService.persist();
        debug("Inserting completed: {} , updating: {}, updating state: {}", toInsert.size(), toUpdate.size(), toUpdateState.size());
    }

    private Map<Class<?>, Map<String, OutputEntity>> mergeEntities(Map<Class<?>, Map<String, List<OutputEntity>>> groupedEntities) {
        Map<Class<?>, Map<String, OutputEntity>> mergedEntities = new LinkedHashMap<>();
        for (Map.Entry<Class<?>, Map<String, List<OutputEntity>>> entry : groupedEntities.entrySet()) {
            Map<String, List<OutputEntity>> entitiesByHash = entry.getValue();
            Class<?> entityClass = entry.getKey();
            Map<String, OutputEntity> mergedEntitiesByHash = mergedEntities.computeIfAbsent(entityClass, c -> new HashMap<>());

            for (Map.Entry<String, List<OutputEntity>> entry2 : entitiesByHash.entrySet()) {
                List<OutputEntity> entities = entry2.getValue();
                String hash = entry2.getKey();
                OutputEntity result = MaxRowUpdatePolicy.applyUpdatePolicySingle(entities, configurationUtil);
                mergedEntitiesByHash.put(hash, result);
            }
        }
        return mergedEntities;
    }
    private void refreshHashValueHolder(List<OutputEntity> entities) {
        executionProfiler.startOperation(getJobExecutionId(), "refreshHashValueHolder-writer");
        Map<Class<?>, List<OutputEntity>> groupedByClass = entities.stream().collect(Collectors.groupingBy(Object::getClass));
        for (Class<?> entityClass : groupedByClass.keySet()) {
            for (OutputEntity entity : groupedByClass.get(entityClass)) {
                String uniqueHash = entity.getSha1ForUniqueFields();
                int secondaryHash = entity.getIntHashForSecondaryFields();
                String id = entity.getId();
                State state = entity.getState();
                hashValuesHolder.addHashValuesForTable(entityClass, uniqueHash, secondaryHash, id, state, true);
            }
        }
        executionProfiler.stopOperation(getJobExecutionId(), "refreshHashValueHolder-writer");
    }
}
