package com.acuity.visualisations.mapping.dao;

import java.util.List;
import java.util.Map;

import com.acuity.visualisations.mapping.entity.EntityRule;
import com.acuity.visualisations.mapping.entity.FieldRule;

public interface ICommonStaticDao {

	List<EntityRule> getStaticData();

	List<FieldRule> getFieldsWithFileId();

	List<Map.Entry<Long, Long>> getEntityDescription();

}
