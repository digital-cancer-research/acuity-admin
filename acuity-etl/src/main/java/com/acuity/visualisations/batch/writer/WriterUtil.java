package com.acuity.visualisations.batch.writer;

import com.acuity.visualisations.model.output.OutputEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WriterUtil {
    private WriterUtil() {
    }

    public static Map<Class<?>, Map<String, List<OutputEntity>>> groupByUniqueHash(List<OutputEntity> entitiesList) {
        Map<Class<?>, Map<String, List<OutputEntity>>> entities = new LinkedHashMap<>();
        for (OutputEntity entity : entitiesList) {
            Class<? extends OutputEntity> entityClass = entity.getClass();
            String sha1ForUniqueFields = entity.getSha1ForUniqueFields();
            Map<String, List<OutputEntity>> entitiesByClass = entities.computeIfAbsent(entityClass, c -> new HashMap<>());
            if (!entitiesByClass.containsKey(sha1ForUniqueFields)) {
                entitiesByClass.put(sha1ForUniqueFields, new ArrayList<>());
            }
            entitiesByClass.get(sha1ForUniqueFields).add(entity);
        }
        return entities;
    }
}
