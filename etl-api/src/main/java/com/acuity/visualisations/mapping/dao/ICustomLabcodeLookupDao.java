package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.CustomLabcodeLookup;

public interface ICustomLabcodeLookupDao extends IBasicDynamicEntityDao<CustomLabcodeLookup> {

	List<CustomLabcodeLookup> getLabCodesByStudyRule(long studyRuleId);

	void deleteAllLabCodesByStudyRule(long studyRuleId);

	void insertLabCodes(List<CustomLabcodeLookup> labcodes);

}
