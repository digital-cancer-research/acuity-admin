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

package com.acuity.visualisations.batch.tasklet;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.dal.CommonEntityDao;
import com.acuity.visualisations.dal.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostProcessUtils {

    @Autowired
    private CommonEntityDao commonEntityDao;

    @Autowired
    private EntityManager entityManager;

    @LogMeAround("Tasklet")
    public void cleanOldData(Class<?> entityClass, List<String> ids) {
        entityManager.deleteByIds(entityClass, ids);
    }

    @LogMeAround("Tasklet")
    public void calculateTables(String studyGuid, String studyName) {
        long studyRuleId = commonEntityDao.getStudyRuleId(studyGuid);

        commonEntityDao.setLastETLRunDate(studyGuid);

        commonEntityDao.dosesPostProcessing(studyGuid);

        commonEntityDao.calculateWithdrawal(studyGuid);

        commonEntityDao.calculatePatientCountry(studyGuid, studyRuleId);

        commonEntityDao.calculatePatientRandDate(studyGuid);

        commonEntityDao.calculatePatientFirstDoseDate(studyGuid, studyRuleId);

        commonEntityDao.calculateSubjectStudyStatus(studyGuid);

        commonEntityDao.resolveGroupingNameConflicts(studyGuid, studyRuleId);

        commonEntityDao.calculateCustomExacerbationSeverity(studyGuid);

        commonEntityDao.aesPostProcessing(studyGuid);
        commonEntityDao.tumourPostProcessing(studyGuid);

//        commonEntityDao.updateEDiaryMedicationUsage(studyGuid);
//        commonEntityDao.updateEDiaryObservations(studyGuid);

        //this one should be the last
        commonEntityDao.updateDatasetLastEventDate(studyGuid);
    }
}
