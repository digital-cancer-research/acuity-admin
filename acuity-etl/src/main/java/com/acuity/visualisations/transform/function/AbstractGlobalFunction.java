package com.acuity.visualisations.transform.function;

import com.acuity.visualisations.batch.processor.EntityCache;
import com.acuity.visualisations.model.output.OutputEntity;

import java.util.List;

public abstract class AbstractGlobalFunction<T extends OutputEntity> {

    public abstract List<T> function(EntityCache cache);

    protected abstract void cleanCache(EntityCache cache);
}
