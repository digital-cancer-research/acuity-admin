package com.acuity.visualisations.model.output;

import java.util.List;
import java.util.Set;

public interface OutputModelChunk {

    void addEntities(List<? extends OutputEntity> entities);

    void addEntity(OutputEntity entity);

    List<OutputEntity> getEntities();

    Set<Class<?>> getEntityClass();
}
