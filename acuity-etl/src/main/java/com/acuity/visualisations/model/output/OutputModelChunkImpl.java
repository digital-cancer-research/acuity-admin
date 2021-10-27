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
