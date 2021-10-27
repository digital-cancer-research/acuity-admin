package com.acuity.visualisations.dal;

import com.acuity.visualisations.model.output.OutputEntity;

import java.util.List;

public interface IBasicEntityDao<T extends OutputEntity> {

    void insert(T entity);

    String getErrorMessage();

    void batchInsert(List<T> list);

    void update(T entity);

    void batchUpdate(List<T> list);

}
