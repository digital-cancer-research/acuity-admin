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
import com.acuity.visualisations.batch.holders.HashValuesHolder;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.dal.dao.IStudyDaoExternal;
import com.acuity.visualisations.model.output.SplitEntity;
import com.acuity.visualisations.service.IExecutionProfiler;
import com.acuity.visualisations.service.VahubCacheService;
import com.acuity.visualisations.transform.rule.StudyRule;
import com.acuity.visualisations.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component("postProcessTasklet")
@Scope("step")
public class PostProcessTasklet extends HoldersAware implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostProcessTasklet.class);
    private static final String ERROR_MESSAGE = "An error happened on preprocess resources takslet";

    private ConfigurationUtil<?> configurationUtil;

    @Autowired
    private PostProcessUtils postProcessUtils;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private IExecutionProfiler executionProfiler;
    @Autowired
    private VahubCacheService vahubCacheService;
    @Autowired
    private IStudyDaoExternal studyDaoExternal;

    private HashValuesHolder hashValuesHolder;
    private boolean isSuccess = false;
    @Value("${azureml.enable:false}")
    private boolean amlEnabledGlobally;

    @Override
    protected void initHolders() {
        configurationUtil = getConfigurationUtil();
        hashValuesHolder = getHashValuesHolder();
    }

    @Override
    @LogMeAround("Tasklet")
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        executionProfiler.startOperation(getJobExecutionId(), "postProcess");

        Collection<StepExecution> stepExecutions = chunkContext.getStepContext().getStepExecution().getJobExecution().getStepExecutions();
        boolean success = true;
        for (StepExecution execution : stepExecutions) {
            if (execution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                success = false;
                switch (execution.getStepName()) {
                    case "readConfiguration":
                        onReadConfigurationError();
                        break;
                    case "preprocessResources":
                        onPreprocessResourcesError();
                        break;
                    case "precleanStudyData":
                        onPrecleanStudyDataError();
                        break;
                    case "readHashValue":
                        onReadHashValueError();
                        break;
                    case "readProcessWrite":
                        onReadProcessWriteError();
                        break;
                    case "publishReports":
                        onPublishReportsError();
                        break;
                    default:
                        break;
                }
            }
        }


        if (success && getStudyRuntimeConfigurationHolder().isEtlExecuted()) {
            isSuccess = true;
        }
        executionProfiler.stopOperation(getJobExecutionId(), "postProcess");
        return RepeatStatus.FINISHED;
    }

    @AfterStep
    @LogMeAround("Tasklet")
    public ExitStatus afterStep() {
        if (!getStudyRuntimeConfigurationHolder().isEtlExecuted()) {
            return ExitStatus.COMPLETED;
        }
        if (isSuccess) {
            try {
                onSuccess();
                return ExitStatus.COMPLETED;
            } catch (DataAccessException e) {
                LOGGER.error(e.getMessage(), e);
                return ExitStatus.FAILED;
            }
        } else {
            return ExitStatus.FAILED;
        }

    }

    private void onReadConfigurationError() {
        LOGGER.error("An error happened on read configuration takslet");
    }

    private void onPreprocessResourcesError() {
        LOGGER.error(ERROR_MESSAGE);
    }

    private void onPrecleanStudyDataError() {
        LOGGER.error(ERROR_MESSAGE);

    }

    private void onReadHashValueError() {
        LOGGER.error("An error happened on preclean study data takslet");
    }

    @LogMeAround("Tasklet")
    private void onReadProcessWriteError() {
        LOGGER.error("An error happened on read process write takslet");
    }

    @LogMeAround("Tasklet")
    private void onPublishReportsError() {
        LOGGER.error(ERROR_MESSAGE);
    }

    @LogMeAround("Tasklet")
    private void onSuccess() {
        String projectName = getProjectName();
        String studyName = getStudyName();

        try {
            List<String> notCumulativeEntities = configurationUtil.getNotCumulativeEntities();
            Date etlStartTime = getStudyRuntimeConfigurationHolder().getCurrentEtlStartTime();

            executionProfiler.startOperation(getJobExecutionId(), "cleanOldData");
            configurationUtil.getEntityNames().stream().filter(e -> !configurationUtil.getSkippedEntities().contains(e))
                    .forEach(entityName -> {
                        executionProfiler.startOperation(getJobExecutionId(), "cleanOldData-" + entityName);
                        Class<?> entityClass;
                        try {
                            entityClass = ReflectionUtil.getEntityClass(entityName);
                        } catch (ClassNotFoundException e) {
                            return;
                        }
                        if (!(SplitEntity.class.isAssignableFrom(entityClass))) {
                            executionProfiler.startOperation(getJobExecutionId(), "cleanOldData-getNoActionIds");
                            List<String> noActionIds = getHashValuesHolder().getNoActionIds(entityClass);
                            executionProfiler.stopOperation(getJobExecutionId(), "cleanOldData-getNoActionIds");
                            debug(String.format("Deleting %d old items for %s", noActionIds.size(), entityName));
                            executionProfiler.startOperation(getJobExecutionId(), "cleanOldData-write");
                            postProcessUtils.cleanOldData(entityClass, noActionIds);
                            executionProfiler.stopOperation(getJobExecutionId(), "cleanOldData-write");
                            executionProfiler.startOperation(getJobExecutionId(), "cleanOldData-removeNoActionHashItems");
                            getHashValuesHolder().removeNoActionItems(entityClass);
                            executionProfiler.stopOperation(getJobExecutionId(), "cleanOldData-removeNoActionHashItems");
                            executionProfiler.stopOperation(getJobExecutionId(), "cleanOldData-" + entityName);
                        }
                    });
            executionProfiler.stopOperation(getJobExecutionId(), "cleanOldData");

            executionProfiler.startOperation(getJobExecutionId(), "calculateTables");
            postProcessUtils.calculateTables(hashValuesHolder.getStudyGuid(), studyName);
            executionProfiler.stopOperation(getJobExecutionId(), "calculateTables");

            LOGGER.info("Updating foreign keys for study {}", studyName);
            for (String entityName : notCumulativeEntities) {
                Class<?> entityClass = ReflectionUtil.getEntityClass(entityName);
                entityManager.updateFK(entityClass, projectName, studyName, etlStartTime);
            }
            callRefreshVahubCache();
        } catch (ClassNotFoundException e) {
            error("Can process entities as not cumulative.", e);
        }
    }

    @LogMeAround("Tasklet")
    private void callRefreshVahubCache() {
        if (!isAmlEnabled()) {
            Long datasetId = ((StudyRule) configurationUtil.getStudy()).getId();
            vahubCacheService.reloadAcuityDatasetCaches(datasetId);
        }
    }

    private boolean isAmlEnabled() {
        return studyDaoExternal.getStudyAmlEnabledFlag()
                .get(getProjectName())
                .get(getStudyName())
                && amlEnabledGlobally;
    }

}
