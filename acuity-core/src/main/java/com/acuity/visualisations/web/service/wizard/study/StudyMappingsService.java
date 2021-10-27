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

package com.acuity.visualisations.web.service.wizard.study;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.entity.AggregationFunction;
import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileStandard;
import com.acuity.visualisations.mapping.entity.FileType;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.dto.FileRuleDTO;
import com.acuity.visualisations.web.dto.MapRuleDTO;
import com.acuity.visualisations.web.dto.MappingStatusDTO;
import com.acuity.visualisations.web.service.AdminService;
import com.acuity.visualisations.web.service.IStudyMappingsServicePartial;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class StudyMappingsService {

	@Autowired
	private AdminService adminService;

	@Autowired
	private IStudyMappingsServicePartial studyMappingsServicePartial;

	private DataProvider provider;

	@Autowired
	public void setProvider(DataProvider provider) {
		this.provider = provider;
	}

	public FileRule createFileRule(FileRuleDTO dto, StudyRule study) {
		FileRule fileRule = new FileRule();
		fileRule.setName(dto.getDataSourceLocation());
		fileRule.setStudyRule(study);
		fileRule.setAcuityEnabled(dto.getStudyAcuityEnabled());
		fileRule.setFileStandard(dto.getFileStandard());
		List<FileType> fileTypes = studyMappingsServicePartial.getFileTypes();
		fileRule.setFileType(fileTypes.get(0));
		List<FileDescription> dataTypes = studyMappingsServicePartial.getFileDescriptions();
		for (FileDescription desc : dataTypes) {
			if (desc.getId().equals(dto.getTypeId())) {
				fileRule.getDescriptions().add(desc);
			}
		}
		study.getFileRules().add(fileRule);
		studyMappingsServicePartial.generateFileMappings(fileRule);
		return fileRule;
	}

	public FileRule updateFileRule(FileRuleDTO dto, StudyRule study) {
		FileRule fileRule = study.getFileRule(dto.getId());
		fileRule.setName(dto.getDataSourceLocation());
		fileRule.setAcuityEnabled(dto.getStudyAcuityEnabled());
		fileRule.setFileStandard(dto.getFileStandard());
		return fileRule;
	}

	public MappingRule createMapRule(MapRuleDTO dto, FileRule fileRule) {
		MappingRule mappingRule = new MappingRule();
		mappingRule.setFileRule(fileRule);
		mappingRule.setFmtName(dto.getDecodingInfo());

		for (AggregationFunction func : studyMappingsServicePartial.getAggregationFunctions()) {
			if (func.getId().equals(dto.getAgrFunctions())) {
				mappingRule.setAggregationFunction(func);
				break;
			}
		}

		for (FieldRule field : studyMappingsServicePartial.getFields()) {
			if (field.getId().equals(dto.getDataField())) {
				mappingRule.getFieldRules().add(field);
				break;
			}
		}


		for (String columnName : StringUtils.split(dto.getSourceData(), ',')) {
			ColumnRule columnRule = new ColumnRule();
			columnRule.setName(columnName.trim());
			columnRule.setMappingRule(mappingRule);
			mappingRule.getColumnRules().add(columnRule);
		}
		return mappingRule;
	}

	public void upateMapRule(MapRuleDTO dto, MappingRule mappingRule) {
		mappingRule.setFmtName(dto.getDecodingInfo());
		mappingRule.setValue(dto.getDefaultValue());
		for (AggregationFunction func : studyMappingsServicePartial.getAggregationFunctions()) {
			if (func.getId().equals(dto.getAgrFunctions())) {
				mappingRule.setAggregationFunction(func);
				break;
			}
		}

		FieldRule fieldRule = mappingRule.getFieldRules().get(0);

		if (!fieldRule.getName().equals(dto.getDataField()) && fieldRule.isDynamic()) {
			studyMappingsServicePartial.deleteDynamicFieldRuleByMappingRule(mappingRule);
			studyMappingsServicePartial.saveDynamicFieldRule(mappingRule, dto.getDataField());
		}

		List<ColumnRule> columnRules = new ArrayList<ColumnRule>();
		for (String columnName : StringUtils.split(dto.getSourceData(), ',')) {
			ColumnRule columnRule = new ColumnRule();
			columnRule.setName(columnName.trim());
			columnRule.setMappingRule(mappingRule);
			columnRules.add(columnRule);
		}
		mappingRule.setColumnRules(columnRules);
	}

	public void deleteMapRule(MappingRule mappingRule) {
		studyMappingsServicePartial.deleteMappingRule(mappingRule);
	}

	@Transactional(rollbackFor = Throwable.class)
	public List<MappingStatusDTO> validateMappings(StudyRule study) {
		List<FileRule> fileRules = studyMappingsServicePartial.validateMappings(study);
		List<MappingStatusDTO> result = new ArrayList<MappingStatusDTO>();
		for (FileRule fileRule : fileRules) {
            FileDescription fd = fileRule.getDescriptions().get(0);
            result.add(new MappingStatusDTO(fileRule.getId(), fd.getId(), fd.getDisplayName(), fileRule.validate(),
					fileRule.isAcuityEnabled(), fileRule.getName()));
        }

		return result;
	}

	@Transactional(readOnly = false)
	public List<MappingStatusDTO> importMappings(InputStream inputStream, StudyRule studyRule, boolean replaceExisting) throws Exception {
		CSVReader reader = new CSVReader(new InputStreamReader(inputStream), ',', '"');
		ArrayList<String> columns = new ArrayList<String>();
		for (String s : Arrays.asList(reader.readNext())) {
			columns.add(s.toLowerCase());
		}
		if (replaceExisting) {
			for (FileRule fileRule : new ArrayList<>(studyRule.getFileRules())) {
				studyMappingsServicePartial.deleteFileRule(fileRule, studyRule);
			}
		}
		Map<Long, FileRule> fileRules = new HashMap<>();
		int idIdx = columns.indexOf("id_idx");
		int dataTypeIdx = columns.indexOf("data_type");
		int dataFieldIdx = columns.indexOf("data_field");
		int aggregationFunctionIdx = columns.indexOf("aggregation_function");
		int fileNameIdx = columns.indexOf("file_name");
		int decodingIdx = columns.indexOf("decoding");
		int defaultValueIdx = columns.indexOf("default_value");
		int sourceColumnIdx = columns.indexOf("source_column");
		int fileStandardIdx = columns.indexOf("file_standard");
		if (dataTypeIdx < 0
				|| idIdx < 0
				|| dataFieldIdx < 0
				|| aggregationFunctionIdx < 0
				|| fileNameIdx  < 0
				|| decodingIdx < 0
				|| defaultValueIdx < 0
				|| sourceColumnIdx  < 0
				|| fileStandardIdx < 0) {
			throw new Exception("Not all required columns found in CSV");
		}

		List<MappingRule> mapRulesWithDynamicFields = new ArrayList<>();

		String[] row = reader.readNext();

		while (row != null) {
			final Long id = Long.parseLong(row[idIdx]);
			final Long fileDescriptionId = Long.parseLong(row[dataTypeIdx]);
			final Long fieldDescriptionId = Long.parseLong(row[dataFieldIdx]);
			final Long aggregationFunction = Long.parseLong(row[aggregationFunctionIdx]);
			final String fileName = makeProperFileName(row[fileNameIdx]);
			final String decoding = row[decodingIdx];
			final String defaultValue = row[defaultValueIdx];
			final String sourceColumn = row[sourceColumnIdx];
			String standardFromFile = row[fileStandardIdx];
			final String standard = (standardFromFile == null || "".equals(standardFromFile) ? "AZRAW" : standardFromFile).toUpperCase();

			final boolean dynamic = fieldDescriptionId < 0;

			FileRule fileRule = fileRules.computeIfAbsent(id, p -> {
				FileRuleDTO fileRuleDTO = new FileRuleDTO();
				fileRuleDTO.setTypeId(fileDescriptionId);
				fileRuleDTO.setStudyAcuityEnabled(true);
				fileRuleDTO.setDataSourceLocation(fileName);
				fileRuleDTO.setFileStandard(FileStandard.valueOf(standard));
				return createFileRule(fileRuleDTO, studyRule);
			});

			for (MappingRule mappingRule: fileRule.getMappingRules()) {
				if (!dynamic && mappingRule.getFieldRules().get(0).getDescription().getId().equals(fieldDescriptionId)) {
					MapRuleDTO mapRuleDTO = new MapRuleDTO();
					mapRuleDTO.setAgrFunctions(aggregationFunction);
					mapRuleDTO.setDecodingInfo(decoding);
					mapRuleDTO.setDefaultValue(defaultValue);
					mapRuleDTO.setSourceData(sourceColumn);
					upateMapRule(mapRuleDTO, mappingRule);
				} else if (dynamic) {
					MapRuleDTO mapRuleDTO = new MapRuleDTO();
					mapRuleDTO.setAgrFunctions(aggregationFunction);
					mapRuleDTO.setDecodingInfo(decoding);
					mapRuleDTO.setDefaultValue(defaultValue);
					mapRuleDTO.setSourceData(sourceColumn);
					mapRuleDTO.setDynamic(true);

					MappingRule mapRule = createMapRule(mapRuleDTO, fileRule);
					fileRule.getMappingRules().add(mapRule);
					mapRulesWithDynamicFields.add(mapRule);

					break;
				}
			}
			row = reader.readNext();
		}
		for (FileRule fileRule : fileRules.values()) {
			studyMappingsServicePartial.saveFileRule(fileRule);
			studyMappingsServicePartial.saveMappingRules(fileRule);
		}

		for (MappingRule mappingRule : mapRulesWithDynamicFields) {
			mappingRule.getFieldRules().add(studyMappingsServicePartial
					.saveDynamicFieldRule(mappingRule, mappingRule.getColumnRules().get(0).getName()));
		}

		return validateMappings(studyRule);
	}

    String makeProperFileName(String fileNameFromFile) {
        return fileNameFromFile.contains("://") || fileNameFromFile.startsWith("\\\\")
                || provider.get(fileNameFromFile).isLocal() ? fileNameFromFile : "\\\\".concat(fileNameFromFile);
    }

	public byte[] exportMappings(List<FileRule> fileRules) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));
		writer.writeNext(new String[]{"id_idx", "data_type", "file_name", "file_standard", "data_field", "source_column",
				"decoding", "default_value", "aggregation_function"});
		for (FileRule fileRule : fileRules) {
			for (MappingRule mappingRule : fileRule.getMappingRules()) {
				FieldRule fieldRule = mappingRule.getFieldRules().get(0);
				writer.writeNext(new String[]{fileRule.getId().toString(), fileRule.getDescriptions().get(0).getId().toString(),
						fileRule.getName() == null ? null : fileRule.getName().replace("\\", "\\\\"),
						fileRule.getFileStandard() == null ? null : fileRule.getFileStandard().toString(),
						fieldRule.isDynamic() ? "-1" : fieldRule.getDescription().getId().toString(),
						getMappingColumns(mappingRule), mappingRule.getFmtName(),
						mappingRule.getValue(), mappingRule.getAggregationFunction().getId().toString()});
			}
		}
		writer.flush();
		writer.close();
		outputStream.close();
		return outputStream.toByteArray();
	}

	private String getMappingColumns(MappingRule mappingRule) {
		List<String> collect = mappingRule.getColumnRules().stream().map(ColumnRule::getName).collect(Collectors.toList());
		return String.join(",", collect.toArray(new String[collect.size()]));
	}

	@Transactional(readOnly = false, rollbackFor = Throwable.class)
	public void validateStudyEnabled(StudyRule study) {
		studyMappingsServicePartial.validateStudyEnabled(study);
	}

}
