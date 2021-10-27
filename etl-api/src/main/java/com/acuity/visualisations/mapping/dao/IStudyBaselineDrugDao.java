package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.StudyBaselineDrug;

import java.util.List;

public interface IStudyBaselineDrugDao {

    void deleteStudyBaselineDrugs(long studyRuleId);

    List<StudyBaselineDrug> selectStudyBaselineDrugs(long studyRuleId);

    void insertStudyBaselineDrugs(long studyRuleId, List<StudyBaselineDrug> studyBaselineDrugs);
}
