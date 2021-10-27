package com.acuity.visualisations.batch.tasklet;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.dal.dao.StudyDao;
import com.acuity.visualisations.service.QtIntervalService;
import com.acuity.visualisations.service.VahubCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Spring batch tasklet that sends data to Azure Machine Learning Web Service (calculating Qt interval)
 */
@Slf4j
@Component("amlTasklet")
@Scope("step")
public class AmlTasklet extends HoldersAware implements Tasklet {

    private boolean isSuccess = false;

    private QtIntervalService qtIntervalService;
    private VahubCacheService vahubCacheService;
    private StudyDao studyDao;

    @Autowired
    public AmlTasklet(QtIntervalService qtIntervalService, VahubCacheService vahubCacheService, StudyDao studyDao) {
        this.qtIntervalService = qtIntervalService;
        this.vahubCacheService = vahubCacheService;
        this.studyDao = studyDao;
    }

    @Override
    protected void initHolders() {
        // default implementation ignored
    }

    @Override
    @LogMeAround("Tasklet")
    public RepeatStatus execute(StepContribution sc, ChunkContext chunkContext) {
        String projectName = getProjectName();
        String studyName = getStudyName();
        try {
            qtIntervalService.run(projectName, studyName);
            isSuccess = true;
        } catch (Exception ex) {
            log.debug("Aml job failed, exception message:" + ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }

    @AfterStep
    @LogMeAround("Tasklet")
    public ExitStatus afterStep() {
        reloadCaches();
        return isSuccess ? ExitStatus.COMPLETED : ExitStatus.FAILED;
    }

    private void reloadCaches() {
        String studyName = getStudyName();
        log.debug("Reloading VAHub caches for dataset {}", getStudyName());
        Long datasetId = studyDao.getDatasetIdByStudyCode(studyName);
        vahubCacheService.reloadAcuityDatasetCaches(datasetId);
    }

}
