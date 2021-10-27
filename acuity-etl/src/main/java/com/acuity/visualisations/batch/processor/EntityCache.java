package com.acuity.visualisations.batch.processor;

import com.acuity.visualisations.model.output.OutputEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityCache {

    private Map<Class<?>, List<OutputEntity>> cache = new HashMap<Class<?>, List<OutputEntity>>();

    public void addEntity(OutputEntity entity) {
        Class<?> entityClass = entity.getClass();
        if (!cache.containsKey(entityClass)) {
            cache.put(entityClass, new ArrayList<OutputEntity>());
        }
        cache.get(entityClass).add(entity);
    }

    public void addEntities(List<OutputEntity> entities) {
        for (OutputEntity entity : entities) {
            addEntity(entity);
        }
    }

    public void removeEntities(Class<?> entityClass) {
        cache.remove(entityClass);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getEntities(Class<T> entityClass) {
        if (!cache.containsKey(entityClass)) {
            return new ArrayList<T>();
        }
        return (List<T>) cache.get(entityClass);
    }
}
