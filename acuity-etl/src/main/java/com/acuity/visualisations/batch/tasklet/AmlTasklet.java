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
