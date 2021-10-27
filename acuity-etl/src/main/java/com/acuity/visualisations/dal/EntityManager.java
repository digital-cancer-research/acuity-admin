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

package com.acuity.visualisations.dal;

import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.dal.dao.AeActionTakenDao;
import com.acuity.visualisations.dal.dao.AeCausalityDao;
import com.acuity.visualisations.dal.dao.AeDao;
import com.acuity.visualisations.dal.dao.AeNumActionTakenDao;
import com.acuity.visualisations.dal.dao.AeNumCycleDelayedDao;
import com.acuity.visualisations.dal.dao.AeSeverityDao;
import com.acuity.visualisations.dal.dao.AlcoholSubUseDao;
import com.acuity.visualisations.dal.dao.BiomarkerDao;
import com.acuity.visualisations.dal.dao.CIEventDao;
import com.acuity.visualisations.dal.dao.CVOTDao;
import com.acuity.visualisations.dal.dao.CerebrovascularDao;
import com.acuity.visualisations.dal.dao.ChemotherapyDao;
import com.acuity.visualisations.dal.dao.ConmedProcedureDao;
import com.acuity.visualisations.dal.dao.ConmedScheduleDao;
import com.acuity.visualisations.dal.dao.ConsentDao;
import com.acuity.visualisations.dal.dao.CountryDao;
import com.acuity.visualisations.dal.dao.CtDnaDao;
import com.acuity.visualisations.dal.dao.DECGDao;
import com.acuity.visualisations.dal.dao.DeathDao;
import com.acuity.visualisations.dal.dao.DiseaseExtentDao;
import com.acuity.visualisations.dal.dao.DrugDao;
import com.acuity.visualisations.dal.dao.EDiaryDao;
import com.acuity.visualisations.dal.dao.EGDao;
import com.acuity.visualisations.dal.dao.EventTypeDao;
import com.acuity.visualisations.dal.dao.ExaSeverityMapDao;
import com.acuity.visualisations.dal.dao.ExacerbationDao;
import com.acuity.visualisations.dal.dao.FMGeneDAO;
import com.acuity.visualisations.dal.dao.LVEFDao;
import com.acuity.visualisations.dal.dao.LaboratoryDao;
import com.acuity.visualisations.dal.dao.LaboratoryGroupDao;
import com.acuity.visualisations.dal.dao.LiverDIDao;
import com.acuity.visualisations.dal.dao.LiverRiskFactorsDao;
import com.acuity.visualisations.dal.dao.LiverSSDao;
import com.acuity.visualisations.dal.dao.LungFunctionDao;
import com.acuity.visualisations.dal.dao.MedDosDiscDao;
import com.acuity.visualisations.dal.dao.MedDosingScheduleDao;
import com.acuity.visualisations.dal.dao.MedicalHistoryDao;
import com.acuity.visualisations.dal.dao.MedicineDao;
import com.acuity.visualisations.dal.dao.NicotineSubUseDao;
import com.acuity.visualisations.dal.dao.NotCumulativeEntityDao;
import com.acuity.visualisations.dal.dao.OverdoseReportDao;
import com.acuity.visualisations.dal.dao.PathologyDao;
import com.acuity.visualisations.dal.dao.PatientDao;
import com.acuity.visualisations.dal.dao.PatientDataDao;
import com.acuity.visualisations.dal.dao.PatientGroupDao;
import com.acuity.visualisations.dal.dao.PerformanceStatusDao;
import com.acuity.visualisations.dal.dao.PkConcentrationDao;
import com.acuity.visualisations.dal.dao.PregnancyTestDao;
import com.acuity.visualisations.dal.dao.PrimaryTumourLocationDao;
import com.acuity.visualisations.dal.dao.ProjectDao;
import com.acuity.visualisations.dal.dao.RadiotherapyDao;
import com.acuity.visualisations.dal.dao.RandomisationDao;
import com.acuity.visualisations.dal.dao.RecistAssessmentDao;
import com.acuity.visualisations.dal.dao.RecistNonTargetLesionDao;
import com.acuity.visualisations.dal.dao.RecistTargetLesionDao;
import com.acuity.visualisations.dal.dao.SeriousAdverseEventDao;
import com.acuity.visualisations.dal.dao.SourceDao;
import com.acuity.visualisations.dal.dao.SpecimenCollectionDao;
import com.acuity.visualisations.dal.dao.StackedPkResultsDao;
import com.acuity.visualisations.dal.dao.StudyDao;
import com.acuity.visualisations.dal.dao.SubjectCharacteristicDao;
import com.acuity.visualisations.dal.dao.SurgicalHistoryDao;
import com.acuity.visualisations.dal.dao.SurvivalStatusDao;
import com.acuity.visualisations.dal.dao.TestDao;
import com.acuity.visualisations.dal.dao.VisitDao;
import com.acuity.visualisations.dal.dao.VitalDao;
import com.acuity.visualisations.dal.dao.WithdrawalCompletionDao;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.model.output.entities.AE;
import com.acuity.visualisations.model.output.entities.AeActionTaken;
import com.acuity.visualisations.model.output.entities.AeCausality;
import com.acuity.visualisations.model.output.entities.AeNumActionTaken;
import com.acuity.visualisations.model.output.entities.AeNumCycleDelayed;
import com.acuity.visualisations.model.output.entities.AeSeverity;
import com.acuity.visualisations.model.output.entities.AlcoholSubUse;
import com.acuity.visualisations.model.output.entities.Biomarker;
import com.acuity.visualisations.model.output.entities.CIEvent;
import com.acuity.visualisations.model.output.entities.CVOT;
import com.acuity.visualisations.model.output.entities.Cerebrovascular;
import com.acuity.visualisations.model.output.entities.Chemotherapy;
import com.acuity.visualisations.model.output.entities.ConcomitantMedSchedule;
import com.acuity.visualisations.model.output.entities.ConmedProcedure;
import com.acuity.visualisations.model.output.entities.Consent;
import com.acuity.visualisations.model.output.entities.Country;
import com.acuity.visualisations.model.output.entities.CtDna;
import com.acuity.visualisations.model.output.entities.DECG;
import com.acuity.visualisations.model.output.entities.Death;
import com.acuity.visualisations.model.output.entities.DiseaseExtent;
import com.acuity.visualisations.model.output.entities.Drug;
import com.acuity.visualisations.model.output.entities.ECG;
import com.acuity.visualisations.model.output.entities.EDiary;
import com.acuity.visualisations.model.output.entities.EG;
import com.acuity.visualisations.model.output.entities.EventType;
import com.acuity.visualisations.model.output.entities.ExaSeverityMap;
import com.acuity.visualisations.model.output.entities.Exacerbation;
import com.acuity.visualisations.model.output.entities.FMGene;
import com.acuity.visualisations.model.output.entities.LVEF;
import com.acuity.visualisations.model.output.entities.Laboratory;
import com.acuity.visualisations.model.output.entities.LaboratoryGroup;
import com.acuity.visualisations.model.output.entities.LiverDI;
import com.acuity.visualisations.model.output.entities.LiverRiskFactors;
import com.acuity.visualisations.model.output.entities.LiverSS;
import com.acuity.visualisations.model.output.entities.LungFunction;
import com.acuity.visualisations.model.output.entities.MedDosDisc;
import com.acuity.visualisations.model.output.entities.MedDosingSchedule;
import com.acuity.visualisations.model.output.entities.MedicalHistory;
import com.acuity.visualisations.model.output.entities.Medicine;
import com.acuity.visualisations.model.output.entities.NicotineSubUse;
import com.acuity.visualisations.model.output.entities.OverdoseReport;
import com.acuity.visualisations.model.output.entities.Pathology;
import com.acuity.visualisations.model.output.entities.Patient;
import com.acuity.visualisations.model.output.entities.PatientData;
import com.acuity.visualisations.model.output.entities.PatientGroup;
import com.acuity.visualisations.model.output.entities.PerformanceStatus;
import com.acuity.visualisations.model.output.entities.PkConcentration;
import com.acuity.visualisations.model.output.entities.PregnancyTest;
import com.acuity.visualisations.model.output.entities.PrimaryTumourLocation;
import com.acuity.visualisations.model.output.entities.Project;
import com.acuity.visualisations.model.output.entities.Radiotherapy;
import com.acuity.visualisations.model.output.entities.Randomisation;
import com.acuity.visualisations.model.output.entities.RecistAssessment;
import com.acuity.visualisations.model.output.entities.RecistNonTargetLesion;
import com.acuity.visualisations.model.output.entities.RecistTargetLesion;
import com.acuity.visualisations.model.output.entities.SeriousAdverseEvent;
import com.acuity.visualisations.model.output.entities.Source;
import com.acuity.visualisations.model.output.entities.SpecimenCollection;
import com.acuity.visualisations.model.output.entities.StackedPkResults;
import com.acuity.visualisations.model.output.entities.Study;
import com.acuity.visualisations.model.output.entities.SubjectCharacteristic;
import com.acuity.visualisations.model.output.entities.SurgicalHistory;
import com.acuity.visualisations.model.output.entities.SurvivalStatus;
import com.acuity.visualisations.model.output.entities.Test;
import com.acuity.visualisations.model.output.entities.Visit;
import com.acuity.visualisations.model.output.entities.Vital;
import com.acuity.visualisations.model.output.entities.VitalThin;
import com.acuity.visualisations.model.output.entities.WithdrawalCompletion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityManager {

    private static final int MAX_IN_CLAUSE_SIZE = 1000;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private VisitDao visitDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private RandomisationDao randomisationDao;

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private MedicineDao medicineDao;

    @Autowired
    private ConmedScheduleDao conmedScheduleDao;

    @Autowired
    private MedDosDiscDao medDosDiscDao;

    @Autowired
    private TestDao testDao;

    @Autowired
    private VitalDao vitalDao;

    @Autowired
    private LaboratoryDao laboratoryDao;

    @Autowired
    private SourceDao sourceDao;

    @Autowired
    private PatientDataDao patientDataDao;

    @Autowired
    private EGDao egDao;

    @Autowired
    private DECGDao decgDao;

    @Autowired
    private LVEFDao lvefDao;

    @Autowired
    private LaboratoryGroupDao laboratoryGroupDao;

    @Autowired
    private MedDosingScheduleDao medDosingScheduleDao;

    @Autowired
    private SeriousAdverseEventDao seriousAdverseEventDao;

    @Autowired
    private PatientGroupDao patientGroupDao;

    @Autowired
    private RecistAssessmentDao recistAssessmentDao;

    @Autowired
    private PerformanceStatusDao performanceStatusDao;

    @Autowired
    private RecistTargetLesionDao recistTargetLesionDao;

    @Autowired
    private RecistNonTargetLesionDao recistNonTargetLesionDao;

    @Autowired
    private PrimaryTumourLocationDao tumourLocationDao;

    @Autowired
    private DeathDao deathDao;

    @Autowired
    private RadiotherapyDao radiotherapyDao;

    @Autowired
    private ChemotherapyDao chemotherapyDao;

    @Autowired
    private MedicalHistoryDao medicalHistoryDao;

    @Autowired
    private SpecimenCollectionDao specimenCollectionDao;

    @Autowired
    private PkConcentrationDao pkConcentrationDao;

    @Autowired
    private StackedPkResultsDao stackedPkResultsDao;

    @Autowired
    private WithdrawalCompletionDao withdrawalCompletionDao;

    @Autowired
    private SubjectCharacteristicDao subjectCharacteristicDao;

    @Autowired
    private DrugDao drugDao;

    @Autowired
    private ExacerbationDao exacerbationDao;

    @Autowired
    private ExaSeverityMapDao exaSeverityMapDao;

    @Autowired
    private LungFunctionDao lungFunctionDao;

    @Autowired
    private AeDao aeDao;

    @Autowired
    private AeSeverityDao aeSeverityDao;

    @Autowired
    private AeCausalityDao aeCausalityDao;

    @Autowired
    private AeActionTakenDao aeActionTakenDao;

    @Autowired
    private EDiaryDao eDiaryDao;

    @Autowired
    private FMGeneDAO fmGeneDAO;

    @Autowired
    private PathologyDao pathologyDao;

    @Autowired
    private AlcoholSubUseDao alcoholSubUseDao;

    @Autowired
    private PregnancyTestDao pregnancyTestDao;

    @Autowired
    private NicotineSubUseDao nicotineSubUseDao;

    @Autowired
    private ConmedProcedureDao conmedProcedureDao;

    @Autowired
    private SurgicalHistoryDao surgicalHistoryDao;

    @Autowired
    private DiseaseExtentDao diseaseExtentDao;

    @Autowired
    private LiverDIDao liverDIDao;

    @Autowired
    private ConsentDao consentDao;

    @Autowired
    private OverdoseReportDao overdoseReportDao;

    @Autowired
    private LiverRiskFactorsDao liverRiskFactorsDao;

    @Autowired
    private LiverSSDao liverSSDao;

    @Autowired
    private SurvivalStatusDao survivalStatusDao;

    @Autowired
    private AeNumActionTakenDao aeNumActionTakenDao;

    @Autowired
    private AeNumCycleDelayedDao aeNumCycleDelayedDao;

    @Autowired
    private CIEventDao ciEventDao;

    @Autowired
    private CVOTDao cvotDao;

    @Autowired
    private CerebrovascularDao cerebrovascularDao;

    @Autowired
    private BiomarkerDao biomarkerDao;

    @Autowired
    private CtDnaDao ctDnaDao;

    private final Map<Class<? extends OutputEntity>, IEntityDao<?>> entityDaoMap = new HashMap<Class<? extends OutputEntity>, IEntityDao<?>>();

    @PostConstruct
    public void init() {
        entityDaoMap.put(Patient.class, patientDao);
        entityDaoMap.put(Visit.class, visitDao);
        entityDaoMap.put(Country.class, countryDao);
        entityDaoMap.put(Randomisation.class, randomisationDao);
        entityDaoMap.put(EventType.class, eventTypeDao);
        entityDaoMap.put(Medicine.class, medicineDao);
        entityDaoMap.put(ConcomitantMedSchedule.class, conmedScheduleDao);
        entityDaoMap.put(MedDosDisc.class, medDosDiscDao);
        entityDaoMap.put(Test.class, testDao);
        entityDaoMap.put(VitalThin.class, vitalDao);
        entityDaoMap.put(Vital.class, vitalDao);
        entityDaoMap.put(Laboratory.class, laboratoryDao);
        entityDaoMap.put(Source.class, sourceDao);
        entityDaoMap.put(PatientData.class, patientDataDao);
        entityDaoMap.put(EG.class, egDao);
        entityDaoMap.put(ECG.class, egDao);
        entityDaoMap.put(DECG.class, decgDao);
        entityDaoMap.put(LVEF.class, lvefDao);
        entityDaoMap.put(LaboratoryGroup.class, laboratoryGroupDao); // child of NotCumulativeEntityDao
        entityDaoMap.put(MedDosingSchedule.class, medDosingScheduleDao);
        entityDaoMap.put(SeriousAdverseEvent.class, seriousAdverseEventDao);
        entityDaoMap.put(PatientGroup.class, patientGroupDao);
        entityDaoMap.put(RecistAssessment.class, recistAssessmentDao);
        entityDaoMap.put(PrimaryTumourLocation.class, tumourLocationDao);
        entityDaoMap.put(Death.class, deathDao);
        entityDaoMap.put(Radiotherapy.class, radiotherapyDao);
        entityDaoMap.put(Chemotherapy.class, chemotherapyDao);
        entityDaoMap.put(RecistTargetLesion.class, recistTargetLesionDao);
        entityDaoMap.put(RecistNonTargetLesion.class, recistNonTargetLesionDao);
        entityDaoMap.put(SpecimenCollection.class, specimenCollectionDao);
        entityDaoMap.put(PkConcentration.class, pkConcentrationDao);
        entityDaoMap.put(StackedPkResults.class, stackedPkResultsDao);
        entityDaoMap.put(WithdrawalCompletion.class, withdrawalCompletionDao);
        entityDaoMap.put(SubjectCharacteristic.class, subjectCharacteristicDao);
        entityDaoMap.put(MedicalHistory.class, medicalHistoryDao);

        entityDaoMap.put(Drug.class, drugDao);
        entityDaoMap.put(Exacerbation.class, exacerbationDao);
        entityDaoMap.put(LungFunction.class, lungFunctionDao);
        entityDaoMap.put(ExaSeverityMap.class, exaSeverityMapDao);
        entityDaoMap.put(AE.class, aeDao);
        entityDaoMap.put(AeSeverity.class, aeSeverityDao);
        entityDaoMap.put(AeCausality.class, aeCausalityDao);
        entityDaoMap.put(AeActionTaken.class, aeActionTakenDao);
        entityDaoMap.put(EDiary.class, eDiaryDao);
        entityDaoMap.put(FMGene.class, fmGeneDAO);
        entityDaoMap.put(Pathology.class, pathologyDao);
        entityDaoMap.put(AlcoholSubUse.class, alcoholSubUseDao);
        entityDaoMap.put(PregnancyTest.class, pregnancyTestDao);
        entityDaoMap.put(NicotineSubUse.class, nicotineSubUseDao);
        entityDaoMap.put(ConmedProcedure.class, conmedProcedureDao);
        entityDaoMap.put(SurgicalHistory.class, surgicalHistoryDao);
        entityDaoMap.put(DiseaseExtent.class, diseaseExtentDao);
        entityDaoMap.put(LiverDI.class, liverDIDao);
        entityDaoMap.put(Consent.class, consentDao);
        entityDaoMap.put(PerformanceStatus.class, performanceStatusDao);
        entityDaoMap.put(OverdoseReport.class, overdoseReportDao);
        entityDaoMap.put(LiverRiskFactors.class, liverRiskFactorsDao);
        entityDaoMap.put(LiverSS.class, liverSSDao);
        entityDaoMap.put(SurvivalStatus.class, survivalStatusDao);
        entityDaoMap.put(AeNumActionTaken.class, aeNumActionTakenDao);
        entityDaoMap.put(AeNumCycleDelayed.class, aeNumCycleDelayedDao);
        entityDaoMap.put(CVOT.class, cvotDao);
        entityDaoMap.put(CIEvent.class, ciEventDao);
        entityDaoMap.put(Cerebrovascular.class, cerebrovascularDao);
        entityDaoMap.put(Biomarker.class, biomarkerDao);
        entityDaoMap.put(CtDna.class, ctDnaDao);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private IEntityDao<? extends OutputEntity> getConcreteDao(Class<?> entityClass) {
        IEntityDao<? extends OutputEntity> dao = entityDaoMap.get(entityClass);
        if (dao instanceof NotCumulativeEntityDao) {
            return dao;
        } else {
            return new EntityDaoDecorator(dao);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void singleRowInsert(OutputEntity entity) {
        IEntityDao currentDao = getConcreteDao(entity.getClass());
        currentDao.insert(entity);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<OctetString, RowParameters> findHash(Class<?> entityClass, String studyGuid) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        return currentDao.findHash(studyGuid, entityClass);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<OctetString, RowParameters> findHash(Class<?> entityClass, String studyGuid, Map<OctetString, RowParameters> hashes) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        return currentDao.findHash(studyGuid, hashes, entityClass);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void batchUpdate(Class<?> entityClass, List<OutputEntity> entities) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        for (OutputEntity entity : entities) {
            entity.update();
        }
        currentDao.batchUpdate(entities);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void batchInsert(Class<?> entityClass, List<OutputEntity> entities) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        currentDao.batchInsert(entities);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String, String> findIdsByHash(Class<?> entityClass, List<String> hashes) {
        if (hashes.isEmpty()) {
            return Collections.emptyMap();
        }
        IEntityDao currentDao = getConcreteDao(entityClass);
        int from = 0;
        int to = MAX_IN_CLAUSE_SIZE > hashes.size() ? hashes.size() : MAX_IN_CLAUSE_SIZE;
        Map<String, String> partAnswer = new HashMap<String, String>();
        while (true) {
            List<String> partHashes = hashes.subList(from, to);
            partAnswer.putAll(currentDao.findIdsByHash(partHashes));
            if (to == hashes.size()) {
                break;
            }
            from = to + 1;
            to = from + MAX_IN_CLAUSE_SIZE < hashes.size() ? from + MAX_IN_CLAUSE_SIZE : hashes.size();
        }
        return partAnswer;
    }

    @Transactional
    public String getProjectGuid(String projectName) {

        String guid = projectDao.getProjectGuid(projectName);
        if (guid == null || guid.isEmpty()) {
            Project project = new Project();
            project.setProjectName(projectName);
            guid = project.getId();
            try {
                projectDao.insert(project);
            } catch (DuplicateKeyException e) {
                return projectDao.getProjectGuid(projectName);
            }
        }
        return guid;
    }

    public List<String> getSubjectsIdsByStudyName(Class<?> entityClass, String studyCode) {
        return getConcreteDao(entityClass).getSubjectsIdsByStudyName(studyCode);
    }

    @Transactional
    public String getProjectGuidOnly(String projectName) {
        return projectDao.getProjectGuid(projectName);
    }

    @Transactional
    public String getStudyGuid(String studyName, String projectGuid, String studyDisplay) {
        String guid = studyDao.getStudyGuid(studyName, projectGuid);
        if (guid == null || guid.isEmpty()) {
            Study study = new Study();
            study.setStudyName(studyName);
            study.setStudyDisplay(studyDisplay);
            study.setProjectGuid(projectGuid);
            guid = study.getId();
            studyDao.insert(study);
        }
        return guid;
    }

    public Date getStudyLastUploadDate(String studyName, String projectGuid) {
        return studyDao.getStudyLastUploadDate(studyName, projectGuid);
    }

    public String getStudyLastUploadState(String studyName, Long currentJobId) {
        return studyDao.getStudyLastUploadState(studyName, currentJobId);
    }

    public Date getStudyMappingModifiedDate(String studyName, String projectGuid) {
        return studyDao.getStudyMappingModifiedDate(studyName, projectGuid);
    }

    @Transactional
    public String getStudyGuidOnly(String studyName, String projectGuid, String studyDisplay) {
        return studyDao.getStudyGuid(studyName, projectGuid);
    }

    @SuppressWarnings("rawtypes")
    public void updateFK(Class<?> entityClass, String projectName, String studyCode, Date jobStartDate) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        if (!NotCumulativeEntityDao.class.isAssignableFrom(currentDao.getClass())) {
            throw new UnsupportedOperationException("Not implemented");
        }
        NotCumulativeEntityDao notCumulativeEntityDao = (NotCumulativeEntityDao) currentDao;
        notCumulativeEntityDao.updateFK(projectName, studyCode, jobStartDate);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void updateState(Class<?> entityClass, List<OutputEntity> entities) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        currentDao.updateState(entities);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void deleteByIds(Class<?> entityClass, List<String> ids) {
        IEntityDao currentDao = getConcreteDao(entityClass);
        currentDao.deleteByIds(ids);
    }

    @Transactional
    public boolean isStudyInDatabase(String projectName, String studyCode) {
        Map<String, List<String>> studies = new HashMap<String, List<String>>();
        List<String> studiesList = new ArrayList<String>();
        studiesList.add(studyCode);
        studies.put(projectName, studiesList);
        Map<String, Map<String, Integer>> studyCount = studyDao.getStudyCount(studies);
        return studyCount.containsKey(projectName) && studyCount.get(projectName).containsKey(studyCode)
                && studyCount.get(projectName).get(studyCode) > 0;
    }

    public boolean studyExists(String studyName) {
        return studyDao.studyExists(studyName);
    }
}
