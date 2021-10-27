package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.dao.IRelationDao;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.service.IStudyMappingsServicePartial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class StudyRelationService {

    @Autowired
    private StudyMappingsService studyMappingService;

    @Autowired
    private IStudyMappingsServicePartial studyMappingsServicePartial;

    @Autowired
    private IRelationDao relationDao;

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveProjectGroupingsRelation(StudyRule studyRule) {
        relationDao.deleteStudyAeGroups(studyRule);
        relationDao.deleteStudyLabGroups(studyRule);
        relationDao.insertStudyAeGroups(studyRule);
        relationDao.insertStudyLabGroups(studyRule);

    }

}
