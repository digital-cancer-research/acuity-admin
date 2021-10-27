package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.util.ETLStudyRule;

import java.util.List;

public interface IStudyRuleDao extends IBasicDynamicEntityDao<StudyRule> {

	StudyRule getByCode(String code);

	List<StudyRule> getByProjectId(Long id);

	List<StudyRule> getByDrugNameAndStudyCode(String drugName, String studyCode);

	void unbind(Long projectId);

	void fillSearchQueryWorker(String query, QuerySearchWorker<StudyRule> worker);

	StudyRule getStudyByCode(String code);

	void removeStudy(Long studyId);

	StudyRule getStudyById(long studyId);

	List<StudyRule> getStudies(List<Long> ids);

	List<StudyRule> getAllStudyRules();

	List<ETLStudyRule> getEtlConfigList();

    List<String> getStudyCodes();

    List<String> getStudyCodesByDrug(String drug);
}
