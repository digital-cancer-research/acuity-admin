package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.StaticEntity;

import java.util.List;

public interface IStaticEntityDao<T extends StaticEntity> {

    List<T> selectAll();
}
