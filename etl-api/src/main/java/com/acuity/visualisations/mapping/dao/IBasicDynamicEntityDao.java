package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.DynamicEntity;

public interface IBasicDynamicEntityDao<T extends DynamicEntity> {

	Long insert(T entity);

	void update(T entity);

}
