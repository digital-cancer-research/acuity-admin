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
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.service.IExecutionProfiler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BDService {

    private enum CommandType {
        INSERT, UPDATE
    }

    private class BDCommand {

        private CommandType type;
        private OutputEntity entity;
    }

    private String[] dataFileNames;
    private Comparator<Class<?>> entityComparator;

    private EntityManager entityManager;

    private Map<Class<?>, Map<String, List<BDCommand>>> commands = new HashMap<Class<?>, Map<String, List<BDCommand>>>();
    private IExecutionProfiler executionProfiler;
    private Long jobId;

    public BDService(Long jobId, List<String> parentEntities, String[] dataFileNames, Comparator<Class<?>> entityComparator,
                     HashValuesHolder hashValuesHolder, EntityManager entityManager, IExecutionProfiler executionProfiler) {
        this.dataFileNames = dataFileNames;
        this.entityComparator = entityComparator;
        this.entityManager = entityManager;
        this.executionProfiler = executionProfiler;
        this.jobId = jobId;
    }

    public void addEntityToInsert(OutputEntity entity) {
        addEntity(entity, CommandType.INSERT);
    }

    public void addEntitiesToInsert(List<OutputEntity> entities) {
        for (OutputEntity entity : entities) {
            addEntityToInsert(entity);
        }
    }

    public void addEntityToUpdate(OutputEntity entity) {
        addEntity(entity, CommandType.UPDATE);
    }

    public void addEntitiesToUpdate(List<OutputEntity> entities) {
        for (OutputEntity entity : entities) {
            addEntityToUpdate(entity);
        }
    }

    public void persist() {
        Map<Class<?>, Set<OutputEntity>> normalizedToInsert = new TreeMap<>(entityComparator);
        Map<Class<?>, Set<OutputEntity>> normalizedToUpdate = new HashMap<>();
        for (Map.Entry<Class<?>, Map<String, List<BDCommand>>> entry : commands.entrySet()) {
            Set<OutputEntity> entitiesToInsert = new HashSet<>();
            Set<OutputEntity> entitiesToUpdate = new HashSet<>();
            Class<?> curEntityClass = entry.getKey();
            Map<String, List<BDCommand>> commandsByEntity = entry.getValue();
            if (commandsByEntity.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, List<BDCommand>> entry2 : commandsByEntity.entrySet()) {
                List<BDCommand> commandsByEntityInstance = entry2.getValue();
                BDCommand command = mergeCommands(commandsByEntityInstance);
                switch (command.type) {
                    case INSERT:
                        entitiesToInsert.add(command.entity);
                        break;
                    case UPDATE:
                        entitiesToUpdate.add(command.entity);
                        break;
                    default:
                        break;
                }
            }

            normalizedToInsert.put(curEntityClass, entitiesToInsert);
            normalizedToUpdate.put(curEntityClass, entitiesToUpdate);
            commandsByEntity.clear();
        }

        for (Map.Entry<Class<?>, Set<OutputEntity>> entry : normalizedToInsert.entrySet()) {
            Set<OutputEntity> normalizedToInsertByEntity = entry.getValue();
            Class<?> entityClass = entry.getKey();
            if (!normalizedToInsertByEntity.isEmpty()) {
                executionProfiler.startOperation(jobId, "batchInsert");
                entityManager.batchInsert(entityClass, new ArrayList<>(normalizedToInsertByEntity));
                executionProfiler.stopOperation(jobId, "batchInsert");
            }
        }

        for (Map.Entry<Class<?>, Set<OutputEntity>> entry : normalizedToUpdate.entrySet()) {
            Set<OutputEntity> normalizedToUpdateByEntity = entry.getValue();
            Class<?> entityClass = entry.getKey();
            if (!normalizedToUpdateByEntity.isEmpty()) {
                executionProfiler.startOperation(jobId, "batchUpdate");
                entityManager.batchUpdate(entityClass, new ArrayList<>(normalizedToUpdateByEntity));
                executionProfiler.stopOperation(jobId, "batchUpdate");
            }
        }
    }

    private BDCommand mergeCommands(List<BDCommand> commands) {
        BDCommand resultCommand = new BDCommand();
        BDCommand insertCommand = null;
        List<BDCommand> updateCommands = new ArrayList<>();
        for (BDCommand command : commands) {
            switch (command.type) {
                case INSERT:
                    insertCommand = command;
                    break;
                case UPDATE:
                    updateCommands.add(command);
                    break;
                default:
                    break;
            }
        }
        List<OutputEntity> entities = entitiesFromCommands(updateCommands);
        if (insertCommand != null) {
            resultCommand.type = CommandType.INSERT;
            if (!entities.isEmpty()) {
                OutputEntity applyUpdatePolicySingle = MaxRowUpdatePolicy.applyUpdatePolicySingle(entities, dataFileNames);
                applyUpdatePolicySingle.setId(insertCommand.entity.getId());
                resultCommand.entity = applyUpdatePolicySingle;
            } else {
                resultCommand.entity = insertCommand.entity;
            }
            resultCommand.entity.setState(insertCommand.entity.getState());
        } else {
            OutputEntity applyUpdatePolicySingle = MaxRowUpdatePolicy.applyUpdatePolicySingle(entities, dataFileNames);
            resultCommand = findCommand(updateCommands, applyUpdatePolicySingle);
        }
        return resultCommand;
    }

    private List<OutputEntity> entitiesFromCommands(List<BDCommand> commands) {
        List<OutputEntity> entities = new ArrayList<OutputEntity>();
        for (BDCommand command : commands) {
            entities.add(command.entity);
        }
        return entities;
    }

    private BDCommand findCommand(List<BDCommand> commands, OutputEntity entity) {
        for (BDCommand command : commands) {
            if (command.entity == entity) {
                return command;
            }
        }
        return null;
    }

    private void addEntity(OutputEntity entity, CommandType type) {
        Class<? extends OutputEntity> entityClass = entity.getClass();
        if (!commands.containsKey(entityClass)) {
            commands.put(entityClass, new HashMap<>());
        }
        Map<String, List<BDCommand>> commandsByEntity = commands.get(entityClass);
        BDCommand command = new BDCommand();
        command.type = type;
        command.entity = entity;
        String sha1ForUniqueFields = entity.getSha1ForUniqueFields();
        if (!commandsByEntity.containsKey(sha1ForUniqueFields)) {
            commandsByEntity.put(sha1ForUniqueFields, new ArrayList<>());
        }
        commandsByEntity.get(sha1ForUniqueFields).add(command);
    }
}
