/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
