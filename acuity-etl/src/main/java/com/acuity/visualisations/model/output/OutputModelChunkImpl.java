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

package com.acuity.visualisations.model.output;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OutputModelChunkImpl implements OutputModelChunk {

    private Map<Class<?>, List<OutputEntity>> entities;

    public OutputModelChunkImpl() {
        entities = new LinkedHashMap<Class<?>, List<OutputEntity>>();
    }

    @Override
    public void addEntity(OutputEntity entity) {
        if (!entities.containsKey(entity.getClass())) {
            entities.put(entity.getClass(), new LinkedList<OutputEntity>());
        }
        entities.get(entity.getClass()).add(entity);
    }

    @Override
    public List<OutputEntity> getEntities() {
        List<OutputEntity> result = new LinkedList<OutputEntity>();
        for (Map.Entry<Class<?>, List<OutputEntity>> entry : entities.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    @Override
    public Set<Class<?>> getEntityClass() {
        return entities.keySet();
    }

    @Override
    public void addEntities(List<? extends OutputEntity> entities) {
        for (OutputEntity entity : entities) {
            addEntity(entity);
        }

    }

}
