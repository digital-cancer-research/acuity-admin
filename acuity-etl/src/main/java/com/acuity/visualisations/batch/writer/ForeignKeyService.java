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

import com.acuity.visualisations.batch.holders.HashValuesHolder;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolder;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.transform.entitytotable.EntityTableBaseRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRule;
import com.acuity.visualisations.transform.table.ForeignKeyRule;
import com.acuity.visualisations.transform.table.TableRule;
import com.acuity.visualisations.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ForeignKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForeignKeyService.class);

    private StaticConfigurationHolder staticConfigurationHolder;

    private ConfigurationUtil<?> configurationUtil;

    private HashValuesHolder hashValuesHolder;

    private Comparator<Class<?>> entityComparator;

    private class HashHolder {
        private OutputEntity childEntity;
        private ForeignKeyRule foreignKeyRule;
        private OctetString hash;
    }

    public ForeignKeyService(EntityManager entityManager, ConfigurationUtil<?> configurationUtil,
                             StaticConfigurationHolder staticConfigurationHolder, HashValuesHolder hashValuesHolder,
                             Comparator<Class<?>> entityComparator) {
        this.configurationUtil = configurationUtil;
        this.staticConfigurationHolder = staticConfigurationHolder;
        this.hashValuesHolder = hashValuesHolder;
        this.entityComparator = entityComparator;
    }

    public List<OutputEntity> setForeignKeys(List<OutputEntity> entitiesList, Map<Class<?>, Map<String, OutputEntity>> mergeEntities) throws Exception {
        Map<Class<?>, List<HashHolder>> parentEntities = new TreeMap<>(entityComparator);

        for (OutputEntity entity : entitiesList) {
            String entityName = entity.getClass().getSimpleName();
            String tableName = staticConfigurationHolder.getEntityTablesRuleByEntityName(entityName).getName();
            List<ForeignKeyRule> foreignKeys = staticConfigurationHolder.getTableRule(tableName).getForeignKey();
            for (ForeignKeyRule fkRule : foreignKeys) {
                if (fkRule.getTargetTable().equals("RESULT_STUDY")) {
                    ReflectionUtil.setFieldValue(entity, fkRule.getFkField(), hashValuesHolder.getStudyGuid());
                    continue;
                }
                String hash = entity.getSha1ForReferFields().get(fkRule.getSource());
                EntityTablesRule targetEntityTablesRule = staticConfigurationHolder.getEntityTablesRule(fkRule.getTargetTable());
                List<EntityTableBaseRule> parentEntitiesNames = targetEntityTablesRule.getEntity();
                String parentEntityName = null;
                List<String> entityNames = configurationUtil.getEntityNames();
                for (EntityTableBaseRule entityTableBaseRule : parentEntitiesNames) {
                    String name = entityTableBaseRule.getName();
                    if (entityNames.contains(name)) {
                        parentEntityName = name;
                        break;
                    }
                }
                if (parentEntityName == null) {
                    parentEntityName = parentEntitiesNames.get(0).getName();
                }
                Class<?> parentEntity = ReflectionUtil.getEntityClass(parentEntityName);
                if (!parentEntities.containsKey(parentEntity)) {
                    parentEntities.put(parentEntity, new ArrayList<>());
                }
                HashHolder hashHolder = new HashHolder();
                hashHolder.childEntity = entity;
                hashHolder.foreignKeyRule = fkRule;
                if (hash != null) {
                    hashHolder.hash = new OctetString(hash);
                }
                parentEntities.get(parentEntity).add(hashHolder);
            }
        }

        for (Map.Entry<Class<?>, List<HashHolder>> entry : parentEntities.entrySet()) {
            Set<OctetString> accumulatedHashes = new HashSet<>();
            for (HashHolder hashHolder : entry.getValue()) {
                accumulatedHashes.add(hashHolder.hash);
            }
            Map<OctetString, String> parentEntityIds = hashValuesHolder.findIdsByHash(entry.getKey(), new ArrayList<>(accumulatedHashes));
            for (HashHolder hashHolder : entry.getValue()) {
                OutputEntity childEntity = hashHolder.childEntity;
                String parentEntityId = parentEntityIds.get(hashHolder.hash);
                if (parentEntityId == null) {
                    parentEntityId = findInChunks(hashHolder.hash, mergeEntities.get(entry.getKey()));
                }
                if (parentEntityId != null) {
                    ReflectionUtil.setFieldValue(childEntity, hashHolder.foreignKeyRule.getFkField(), parentEntityId);
                }
            }
        }

        List<OutputEntity> fkFailedEntities = new ArrayList<>();
        Set<String> fkFailedIds = new HashSet<>();
        for (OutputEntity entity : entitiesList) {
            String entityName = entity.getClass().getSimpleName();
            EntityTablesRule entityTablesRule = staticConfigurationHolder.getEntityTablesRuleByEntityName(entityName);
            TableRule tableRule = staticConfigurationHolder.getTableRule(entityTablesRule.getName());
            for (ForeignKeyRule fkRule : tableRule.getForeignKey()) {
                Object fkFieldValue = ReflectionUtil.getFieldValue(entity, fkRule.getFkField());
                if ((fkFieldValue == null && !Util.isTrue(fkRule.isNullable()))) {
                    fkFailedEntities.add(entity);
                    fkFailedIds.add(entity.getId());
                    LOGGER.debug("FK for entity {} {} failed: {}", entity.getClass().getName(), entity.getId(), entity.allFieldsToString());
                    break;
                }
                if ((fkFailedIds.contains(fkFieldValue))) {
                    LOGGER.debug("FK for entity {} {} parent failed: {}, parent id: {}", entity.getClass().getName(),
                            entity.getId(), entity.allFieldsToString(), fkFieldValue);
                    if (Util.isTrue(fkRule.isNullable())) {
                        ReflectionUtil.setFieldValue(entity, fkRule.getFkField(), null);
                    } else {
                        fkFailedEntities.add(entity);
                        fkFailedIds.add(entity.getId());
                        break;
                    }
                }
            }
        }
        return fkFailedEntities;
    }

    public List<OutputEntity> validateFKsInHashes(List<OutputEntity> entitiesList) throws Exception {
        List<OutputEntity> fkFailedEntities = new ArrayList<>();

        for (OutputEntity entity : entitiesList) {
            String entityName = entity.getClass().getSimpleName();
            String tableName = staticConfigurationHolder.getEntityTablesRuleByEntityName(entityName).getName();
            List<ForeignKeyRule> foreignKeys = staticConfigurationHolder.getTableRule(tableName).getForeignKey();
            for (ForeignKeyRule fkRule : foreignKeys) {
                if (fkRule.getTargetTable().equals("RESULT_STUDY")) {
                    continue;
                }
                String hash = entity.getSha1ForReferFields().get(fkRule.getSource());
                EntityTablesRule targetEntityTablesRule = staticConfigurationHolder.getEntityTablesRule(fkRule.getTargetTable());
                List<EntityTableBaseRule> parentEntitiesNames = targetEntityTablesRule.getEntity();
                String parentEntityName = null;
                List<String> entityNames = configurationUtil.getEntityNames();
                for (EntityTableBaseRule entityTableBaseRule : parentEntitiesNames) {
                    String name = entityTableBaseRule.getName();
                    if (entityNames.contains(name)) {
                        parentEntityName = name;
                        break;
                    }
                }
                if (parentEntityName == null) {
                    parentEntityName = parentEntitiesNames.get(0).getName();
                }
                Class<?> parentEntity = ReflectionUtil.getEntityClass(parentEntityName);
                if (hash != null) {
                    boolean parentPresent = hashValuesHolder.checkEntityAction(parentEntity, new OctetString(hash));
                    if (!parentPresent) {
                        Object fkFieldValue = ReflectionUtil.getFieldValue(entity, fkRule.getFkField());
                        if (fkFieldValue != null) {
                            fkFailedEntities.add(entity);
                            String uniqueHash = entity.getSha1ForUniqueFields();
                            int secondaryHash = entity.getIntHashForSecondaryFields();
                            String id = entity.getId();
                            State state = entity.getState();
                            hashValuesHolder.addHashValuesForTable(entity.getClass(), uniqueHash, secondaryHash, id, state, false);
                            LOGGER.debug("FK for entity {} failed: {}", entity.getId(), entity.allFieldsToString());
                        }
                    }
                }
            }
        }


        return fkFailedEntities;
    }

    private String findInChunks(OctetString hash, Map<String, OutputEntity> entitiesList) {
        String res = null;
        if (hash != null && entitiesList != null) {
            OutputEntity entry = entitiesList.get(hash.toString());
            if (entry != null) {
                res = entry.getId();
            }
        }
        return res;
    }

}
