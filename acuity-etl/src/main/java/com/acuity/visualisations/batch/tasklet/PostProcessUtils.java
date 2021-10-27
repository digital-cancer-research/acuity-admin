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

        //todo. Check, that these calculations are unnecessary
//        commonEntityDao.updateEDiaryMedicationUsage(studyGuid);
//        commonEntityDao.updateEDiaryObservations(studyGuid);

        //this one should be the last
        commonEntityDao.updateDatasetLastEventDate(studyGuid);
    }
}
