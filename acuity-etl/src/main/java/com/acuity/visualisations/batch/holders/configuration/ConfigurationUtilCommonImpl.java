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

package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.transform.entity.EntityBaseRule;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.entity.EntityFieldSetRule;
import com.acuity.visualisations.transform.entitytotable.EntityTableBaseRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRule;
import com.acuity.visualisations.transform.rule.ColumnRule;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.EntityRule;
import com.acuity.visualisations.transform.rule.FilterRule;
import com.acuity.visualisations.transform.rule.ProjectRule;
import com.acuity.visualisations.transform.rule.StudyRule;
import com.acuity.visualisations.transform.table.ForeignKeyRule;
import com.acuity.visualisations.transform.table.TableRule;
import com.acuity.visualisations.util.ETLUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ConfigurationUtilCommonImpl<T extends StudyMappingConfigurationHolder> implements ConfigurationUtil<T> {

	private static final String ENTITY_PROJECT_NAME_FIELD = "projectName";
	private static final String ENTITY_STUDY_NAME_FIELD = "studyName";
	private static final String ROOT_ENTITY_TABLE = "RESULT_PATIENT";

	private StaticConfigurationHolder staticConfigurationHolder;

	public void setStaticConfigurationHolder(StaticConfigurationHolder staticConfigurationHolder) {
		this.staticConfigurationHolder = staticConfigurationHolder;
	}

	public IStudyRule getStudy() {
		return getProjectRule().getStudy();
	}

	protected abstract ProjectRule getProjectRule();

	private StudyRule getStudyRule() {
		return getProjectRule().getStudy();
	}

	private DataFileRule getDataFileRule(String dataFileName) {
		for (DataFileRule fileRule : getStudyRule().getFile()) {
			if (fileRule.getName().equals(dataFileName)) {
				return fileRule;
			}
		}
		return null;
	}

    public List<String> getEntities(String dataFileName) {
        DataFileRule dataFileRule = getDataFileRule(dataFileName);
        if (dataFileRule == null) {
            return null;
        }
        Set<String> entities = new LinkedHashSet<>();
        for (EntityRule entityRule : dataFileRule.getEntity()) {
            if ("AdverseEvent".equalsIgnoreCase(entityRule.getName())) {
                addAdverseEventRelatedEntities(entities);
            }
            if ("MedDosingSchedule".equalsIgnoreCase(entityRule.getName())) {
                addDoseRelatedEntities(entities);
            }
            if ("Laboratory".equalsIgnoreCase(entityRule.getName()) || "PatientData".equalsIgnoreCase(entityRule.getName())) {
                addDeviceRelatedEntities(entities);
            }
            entities.add(entityRule.getName());

        }
        return new ArrayList<>(entities);
    }

	private EntityRule getEntityRule(String dataFileName, String entityName) {
		DataFileRule dataFileRule = getDataFileRule(dataFileName);
		for (EntityRule entityRule : dataFileRule.getEntity()) {
			if (entityRule.getName().equals(entityName)) {
				return entityRule;
			}
		}
		return null;
	}

	public List<? extends IColumnRule> getColumns(String dataFileName, String entityName) {
		EntityRule entityRule = getEntityRule(dataFileName, entityName);
		if (entityRule == null) {
			return null;
		}
		return entityRule.getColumn();
	}

	public List<String> getNotCumulativeEntities() {
		List<String> result = new ArrayList<String>();
		List<DataFileRule> dataFileRules = getStudyRule().getFile();
		for (DataFileRule dataFileRule : dataFileRules) {
			if (!ETLUtil.isCumulative(dataFileRule.getType())) {
				List<EntityRule> entities = dataFileRule.getEntity();
				for (EntityRule rule : entities) {
					result.add(rule.getName());
				}
			}
		}
		return result;
	}

	public String[] getDataFileNames() {
		List<DataFileRule> dataFileRules = getStudyRule().getFile();
		String[] toReturn = new String[dataFileRules.size()];
		for (int i = 0; i < dataFileRules.size(); i++) {
			toReturn[i] = dataFileRules.get(i).getName();
		}
		return toReturn;
	}

	public List<String> getDataFileNames(String entityName) {
		List<String> dataFileNames = new ArrayList<String>();
		for (DataFileRule dataFileRule : getStudyRule().getFile()) {
			for (EntityRule rule : dataFileRule.getEntity()) {
				if (rule.getName().equals(entityName)) {
					dataFileNames.add(dataFileRule.getName());
				}
			}
		}
		return dataFileNames;
	}

	@Override
	public String getEntityNameByColName(String fileName, String colName) {
		DataFileRule dataFileRule = getDataFileRule(fileName);
		for (EntityRule entityRule : dataFileRule.getEntity()) {
			for (ColumnRule columnRule : entityRule.getColumn()) {
				if (columnRule.getName().equalsIgnoreCase(colName)) {
					return entityRule.getName();
				}
			}
		}
		return null;
	}

	@Override
	public String getFieldNameByColName(String fileName, String colName) {
		DataFileRule dataFileRule = getDataFileRule(fileName);
		for (EntityRule entityRule : dataFileRule.getEntity()) {
			for (ColumnRule columnRule : entityRule.getColumn()) {
				if (columnRule.getName().equalsIgnoreCase(colName)) {
					return columnRule.getField();
				}
			}
		}
		return null; // To change body of implemented methods use File | Settings | File Templates.
	}

	public List<String> getColumnNames(String fileName) {
		List<String> result = new ArrayList<String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		for (EntityRule entityRule : entities) {
			for (ColumnRule columnRule : entityRule.getColumn()) {
				if (!columnRule.getName().isEmpty()) {
					result.add(columnRule.getName());
				}
			}
		}
		return result;
	}

	public Map<String, String> getFilters(String fileName) {
		HashMap<String, String> result = new HashMap<String, String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		for (EntityRule entityRule : entities) {
			for (FilterRule filterRule : entityRule.getFilter()) {
				if (!filterRule.getName().isEmpty()) {
					result.put(filterRule.getName(), filterRule.getEqual());
				}
			}
		}
		return result;
	}

	public Map<Integer, String> getEmptyColumnsDefaultValues(String fileName) {
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		int offSet = 0;
		for (EntityRule entityRule : entities) {
			for (int i = 0; i < entityRule.getColumn().size(); i++) {
				ColumnRule columnRule = entityRule.getColumn().get(i);
				if (columnRule.getName().isEmpty()) {
					result.put(i + offSet, columnRule.getDefault());
				}
			}
			offSet += entityRule.getColumn().size();
		}
		return result;
	}

	public Map<Integer, String> getEmptyColumnsDefaultValues(String fileName, Set<String> columnsToSkip) {
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		int offSet = 0;
		for (EntityRule entityRule : entities) {
			for (int i = 0; i < entityRule.getColumn().size(); i++) {
				ColumnRule columnRule = entityRule.getColumn().get(i);
				if (columnsToSkip.contains(columnRule.getName())) {
					offSet--;
					continue;
				}
				if (columnRule.getName().isEmpty()) {
					result.put(i + offSet, columnRule.getDefault());
				}
			}
			offSet += entityRule.getColumn().size();
		}
		return result;
	}

	public Map<Integer, String> getDefaultValues(String fileName) {
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		int offSet = 0;
		for (EntityRule entityRule : entities) {
			for (int i = 0; i < entityRule.getColumn().size(); i++) {
				IColumnRule columnRule = entityRule.getColumn().get(i);
				if (!Util.isEmpty(columnRule.getDefault())) {
					result.put(i + offSet, columnRule.getDefault());
				}
			}
			offSet += entityRule.getColumn().size();
		}
		return result;
	}

	public Map<Integer, String> getDefaultValues(String fileName, Set<String> columnsToSkip) {
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<EntityRule> entities = getDataFileRule(fileName).getEntity();
		int offSet = 0;
		for (EntityRule entityRule : entities) {
			for (int i = 0; i < entityRule.getColumn().size(); i++) {
				ColumnRule columnRule = entityRule.getColumn().get(i);
				if (columnsToSkip.contains(columnRule.getName())) {
					offSet--;
					continue;
				}
				if (!Util.isEmpty(columnRule.getDefault())) {
					result.put(i + offSet, columnRule.getDefault());
				}
			}
			offSet += entityRule.getColumn().size();
		}
		return result;
	}

	public List<String> getEntityNames() {
		Set<String> entityNames = new LinkedHashSet<>();
        for (DataFileRule dataFileRule : getStudyRule().getFile()) {
            for (EntityRule entityRule : dataFileRule.getEntity()) {
                //we need to add Source into database before PatientData or Laboratory because of FK constraint
                if ("Laboratory".equalsIgnoreCase(entityRule.getName()) || "PatientData".equalsIgnoreCase(entityRule.getName())) {
                    addDeviceRelatedEntities(entityNames);
                }
                entityNames.add(entityRule.getName());
            }
        }
        // entities that don't have mapping rules
		if (entityNames.contains("ECG")) {
			entityNames.remove("ECG");
			entityNames.add("EG");
		}

		if (entityNames.contains("WidePatientGroupings") && !entityNames.contains("PatientGroup")) {
			addPatientGroupRelatedEntities(entityNames);
		}
		entityNames.remove("WidePatientGroupings");

        if (entityNames.contains("AdverseEvent")) {
			addAdverseEventRelatedEntities(entityNames);
			entityNames.remove("AdverseEvent");
        }
        if (entityNames.contains("MedDosingSchedule")) {
			addDoseRelatedEntities(entityNames);
        }

		return new ArrayList<>(entityNames);
	}

	public Map<String, Map<String, List<String>>> getColumns(String entityName) {
		Map<String, Map<String, List<String>>> result = new HashMap<>();
		for (DataFileRule dataFileRule : getStudyRule().getFile()) {
			for (EntityRule entityRule : dataFileRule.getEntity()) {
				if (entityRule.getName().equals(entityName)) {
					if (!result.containsKey(dataFileRule.getName())) {
						result.put(dataFileRule.getName(), new HashMap<>());
					}
					Map<String, List<String>> resultByFile = result.get(dataFileRule.getName());
					for (ColumnRule columnRule : entityRule.getColumn()) {
						if (!resultByFile.containsKey(columnRule.getField())) {
							resultByFile.put(columnRule.getField(), new ArrayList<>());
						}
						resultByFile.get(columnRule.getField()).add(columnRule.getName());
					}
				}
			}
		}
		return result;
	}

	public boolean isUnique(String sasColumnName, String dataFileName) {
		Map<String, Map<String, String>> columnsToSasColumns = new HashMap<>();
		for (EntityRule rule : getDataFileRule(dataFileName).getEntity()) {
			for (ColumnRule columnRule : rule.getColumn()) {
				if (columnRule.getName().equals(sasColumnName)) {
					if (!columnsToSasColumns.containsKey(rule.getName())) {
						columnsToSasColumns.put(rule.getName(), new HashMap<>());
					}
					columnsToSasColumns.get(rule.getName()).put(columnRule.getField(), columnRule.getName());
				}
			}
		}

		for (Map.Entry<String, Map<String, String>> entry : columnsToSasColumns.entrySet()) {
			String entityName = entry.getKey();
			EntityDescriptionRule entityRule = staticConfigurationHolder.getEntityRule(entityName);
			EntityFieldSetRule uniqueFields = entityRule.getUniqueFields();
			if (uniqueFields == null) {
				continue;
			}
			Map<String, String> columns = entry.getValue();
			for (EntityBaseRule fieldRule : uniqueFields.getField()) {
				if (columns.containsKey(fieldRule.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public IColumnRule getStudyColumn(String fileName, String entityName) {
		for (IColumnRule columnRule : getEntityRule(fileName, entityName).getColumn()) {
			if (columnRule.getField().equals(ENTITY_STUDY_NAME_FIELD)) {
				return columnRule;
			}
		}
		return null;
	}

	public Map<String, String> getFKfields(OutputEntity childEntity, ForeignKeyRule fkRule) {
		Map<String, String> foreignKeyFields = new HashMap<String, String>();
		Map<String, List<String>> sasColumns = getColumns(childEntity.getClass().getSimpleName()).get(childEntity.getSourceName());
		String[] referFieldNames = getReferFieldNames(childEntity, fkRule);
		for (int i = 0; i < referFieldNames.length; i++) {
			String fieldName = referFieldNames[i];
			if (!fieldName.equals(ENTITY_STUDY_NAME_FIELD) && !fieldName.equals(ENTITY_PROJECT_NAME_FIELD)) {
				if (sasColumns.containsKey(fieldName)) {
					List<String> sasNames = sasColumns.get(fieldName);
					for (String sasName : sasNames) {
						String sasValue = childEntity.getSourceColumnValue(sasName);
						foreignKeyFields.put(sasName, sasValue);
					}
				}
			}
		}
		return foreignKeyFields;
	}

	public String[] getReferFieldNames(OutputEntity entity, ForeignKeyRule fkRule) {
		String entityName = entity.getClass().getSimpleName();
		EntityDescriptionRule entityRule = staticConfigurationHolder.getEntityRule(entityName);
		List<EntityFieldSetRule> referBy = entityRule.getReferBy();
		for (int i = 0; i < referBy.size(); i++) {
			EntityFieldSetRule referFieldObject = referBy.get(i);
			if (referFieldObject.getName().equals(fkRule.getSource())) {
				List<EntityBaseRule> fields = referFieldObject.getField();
				String[] referFieldNames = new String[fields.size()];
				for (int j = 0; j < fields.size(); j++) {
					referFieldNames[j] = fields.get(j).getName();
				}
				return referFieldNames;
			}
		}
		return null;
	}

	public String[] getParentEntity(ForeignKeyRule foreignKeyRule) {
		String targetTable = foreignKeyRule.getTargetTable();
		EntityTablesRule entityTablesRule = staticConfigurationHolder.getEntityTablesRule(targetTable);
		List<EntityTableBaseRule> parentEntitiesObjects = entityTablesRule.getEntity();
		String[] parentEntities = new String[parentEntitiesObjects.size()];
		for (int i = 0; i < parentEntitiesObjects.size(); i++) {
			EntityTableBaseRule parentObject = parentEntitiesObjects.get(i);
			parentEntities[i] = parentObject.getName();
		}
		return parentEntities;
	}

	public String[] getParentEntity(String childEntity) {
		EntityTablesRule entityTablesRule = staticConfigurationHolder.getEntityTablesRuleByEntityName(childEntity);
		String tableName = entityTablesRule.getName();
		TableRule tableRule = staticConfigurationHolder.getTableRule(tableName);

		for (ForeignKeyRule rule : tableRule.getForeignKey()) {
			if (isMainPath(rule)) {
				EntityTablesRule targetEntityTablesRule = staticConfigurationHolder.getEntityTablesRule(rule.getTargetTable());
				List<EntityTableBaseRule> entityTableBaseRules = targetEntityTablesRule.getEntity();
				String[] parentEntities = new String[entityTableBaseRules.size()];
				for (int i = 0; i < entityTableBaseRules.size(); i++) {
					parentEntities[i] = entityTableBaseRules.get(i).getName();
				}
				return parentEntities;
			}
		}
		return null;
	}

	private boolean isMainPath(ForeignKeyRule foreignKeyRule) {
		String targetTable = foreignKeyRule.getTargetTable();
		if (targetTable.equals(ROOT_ENTITY_TABLE)) {
			return true;
		}
		TableRule tableRule = staticConfigurationHolder.getTableRule(targetTable);
		if (tableRule == null) {
			return false;
		}
		for (ForeignKeyRule rule : staticConfigurationHolder.getTableRule(targetTable).getForeignKey()) {
			if (isMainPath(rule)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getParentEntities() {
		List<String> parentEntities = new ArrayList<String>();
		for (String entityName : getEntityNames()) {
			if (isParent(entityName)) {
				parentEntities.add(entityName);
			}
		}
		return parentEntities;
	}

	private boolean isParent(String entityName) {
		EntityTablesRule entityTablesRule = staticConfigurationHolder.getEntityTablesRuleByEntityName(entityName);
		if (entityTablesRule == null) {
			return false;
		}
		String tableName = entityTablesRule.getName();
		List<String> entityNames = getEntityNames();
		for (String curEntityName : entityNames) {
			EntityTablesRule curEntityTablesRule = staticConfigurationHolder.getEntityTablesRuleByEntityName(curEntityName);
			if (curEntityTablesRule == null) {
				continue;
			}
			String curTableName = curEntityTablesRule.getName();
			List<ForeignKeyRule> foreignKeys = staticConfigurationHolder.getTableRule(curTableName).getForeignKey();
			for (ForeignKeyRule foreignKeyRule : foreignKeys) {
				if (foreignKeyRule.getTargetTable().equals(tableName)) {
					return true;
				}
			}
		}
		return false;
	}

	private void addAdverseEventRelatedEntities(Collection<String> entities) {
		entities.add("AE");
		entities.add("Drug");
		entities.add("AeSeverity");
		entities.add("AeCausality");
		entities.add("AeActionTaken");
	}

	private void addDoseRelatedEntities(Collection<String> entities) {
		entities.add("AeNumActionTaken");
		entities.add("AeNumCycleDelayed");
	}

	private void addDeviceRelatedEntities(Collection<String> entities) {
		entities.add("Source");
	}

	private void addPatientGroupRelatedEntities(Collection<String> entities) {
		entities.add("PatientGroup");
	}

}
