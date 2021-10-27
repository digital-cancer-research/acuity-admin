package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.dao.IStudyBaselineDrugDao;
import com.acuity.visualisations.mapping.entity.StudyBaselineDrug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyBaselineDrugService {

    @Autowired
    private IStudyBaselineDrugDao studyBaselineDrugDao;

    public List<StudyBaselineDrug> loadStudyBaselineDrugs(long studyRuleId) {
        return studyBaselineDrugDao.selectStudyBaselineDrugs(studyRuleId);
    }

    public void saveStudyBaselineDrugs(long studyRuleId, List<StudyBaselineDrug> studyBaselineDrugs) {
        studyBaselineDrugDao.deleteStudyBaselineDrugs(studyRuleId);
        studyBaselineDrugDao.insertStudyBaselineDrugs(studyRuleId, studyBaselineDrugs);
    }
}
