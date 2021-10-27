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
import com.acuity.visualisations.aspect.TimeMe;
import com.acuity.visualisations.batch.holders.CntlinDataHolder;
import com.acuity.visualisations.batch.holders.HashValuesHolder;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.reader.tablereader.FileTypeAwareTableReader;
import com.acuity.visualisations.batch.reader.tablereader.TableReader;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.dal.dao.IStudyDaoExternal;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.NamedRule;
import com.acuity.visualisations.transform.rule.StudyRule;
import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("etlPreprocessTasklet")
@Scope("step")
public class PreprocessResourcesTasklet extends HoldersAware implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreprocessResourcesTasklet.class);

    public static final String PROCEED = "PROCEED";
    public static final String SKIP = "SKIP";
    public static final String COMPLETED = "COMPLETED";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private IStudyDaoExternal studyDao;

    @Autowired
    private ExtendedJobExecutionDao jobExecutionDao;

    private ConfigurationUtil<?> configurationUtil;

    private CntlinDataHolder cntlinDataHolder;

    private HashValuesHolder hashValuesHolder;

    private String exitStatus;

    @Autowired
    private DataProvider dataProvider;

    private boolean scheduledCleanFlag;

    @Override
    protected void initHolders() {
        configurationUtil = getConfigurationUtil();
        cntlinDataHolder = getCntlinDataHolder();
        hashValuesHolder = getHashValuesHolder();
    }

    @TimeMe
    @Override
    @LogMeAround("Tasklet")
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String projectName = getProjectName();
        String studyName = getStudyName();
        if (scheduledCleanFlag) {
            cleanStudy(studyName);
        }
        String projectGuid = getProjectGuid(projectName);
        String studyDisplay = configurationUtil.getStudy().getDisplayName();
        String studyGuid = getStudyGuid(studyName, studyDisplay, projectGuid);
        final boolean etlNeedsExec = isEtlExecutionRequired(studyName, projectGuid);

        hashValuesHolder.setStudyGuid(studyGuid);
        hashValuesHolder.setStudyName(studyName);
        hashValuesHolder.initializeMapDB();

        //check if study should be cleaned before processing. If yes, start cleaning study data
        getStudyRuntimeConfigurationHolder().setEtlExecuted(etlNeedsExec);

        exitStatus = etlNeedsExec ? PROCEED : SKIP;

        if (!etlNeedsExec) {
            LOGGER.info("Skipping data upload, files don't seem to have changes since previous successful run.");
        }

        readCntlin();

        return RepeatStatus.FINISHED;
    }

    /**
     * Check if ETL needs refresh - analyzing modification dates of files and study mappings comparing to last etl date
     */
    @LogMeAround("Tasklet")
    private boolean isEtlExecutionRequired(String studyName, String projectGuid) {
        final List<String> allFiles = ((StudyRule) configurationUtil.getStudy()).getFile()
                .stream()
                .map(NamedRule::getName)
                .collect(Collectors.toList());
        Date lastChangeDate = getLastFileChangeDate(allFiles);
        final Date studyLastUploadDate = getStudyLastUploadDate(studyName, projectGuid);
        final String studyLastUploadState = getStudyLastUploadState(studyName, getJobExecutionId());
        final Date studyMappingUpdateDate = getStudyMappingModifiedDate(studyName, projectGuid);
        LOGGER.info("Latest state is {}, last successful upload at {}, "
                        + "latest source file change at {}, latest mapping update at {}.",
                studyLastUploadState, studyLastUploadDate, lastChangeDate, studyMappingUpdateDate);

        boolean lastRunNotSuccessful = !COMPLETED.equalsIgnoreCase(studyLastUploadState);
        boolean isStudyNeverUploaded = studyLastUploadDate == null;

        final boolean etlNeedsExec = lastRunNotSuccessful
                || isStudyNeverUploaded
                || hasFilesChangesSinceLastUpload(lastChangeDate, studyLastUploadDate)
                || hasMappingsChangesSinceLastUpload(studyMappingUpdateDate, studyLastUploadDate);
        return etlNeedsExec;
    }

    private boolean hasFilesChangesSinceLastUpload(Date lastChangeDate, Date studyLastUploadDate) {
        return lastChangeDate.getTime() > studyLastUploadDate.getTime();
    }

    private boolean hasMappingsChangesSinceLastUpload(Date studyMappingUpdateDate, Date studyLastUploadDate) {
        return studyMappingUpdateDate == null || studyMappingUpdateDate.getTime() > studyLastUploadDate.getTime();
    }

    private Date getLastFileChangeDate(List<String> allFiles) {
        long timestamp = allFiles.stream()
                .mapToLong(fn -> dataProvider.get(fn).timestamp())
                .max()
                .orElse(0);

        return new Date(timestamp);
    }

    @AfterStep
    public ExitStatus afterStep() {
        return new ExitStatus(exitStatus);
    }

    @LogMeAround("Tasklet")
    protected String getStudyGuid(String studyName, String studyDisplay, String projectGuid) {
        return entityManager.getStudyGuid(studyName, projectGuid, studyDisplay);
    }

    @LogMeAround("Tasklet")
    protected Date getStudyLastUploadDate(String studyName, String projectGuid) {
        return entityManager.getStudyLastUploadDate(studyName, projectGuid);
    }

    @LogMeAround("Tasklet")
    protected String getStudyLastUploadState(String studyName, Long currentJobId) {
        return entityManager.getStudyLastUploadState(studyName, currentJobId);
    }

    @LogMeAround("Tasklet")
    protected Date getStudyMappingModifiedDate(String studyName, String projectGuid) {
        return entityManager.getStudyMappingModifiedDate(studyName, projectGuid);
    }

    @LogMeAround("Tasklet")
    protected String getProjectGuid(String projectName) {
        return entityManager.getProjectGuid(projectName);
    }

    /**
     * Read content of cntlin file (or multiple files) into the Map
     *
     * @throws Exception
     */
    @LogMeAround("Tasklet")
    private void readCntlin() {
        List<DataFileRule> cntlinFileRules = configurationUtil.getCntlinFileRules();
        List<String> cntlinFileNames = new ArrayList<>();

        if (!CollectionUtils.isEmpty(cntlinFileRules)) {
            for (DataFileRule cntlinFileRule : cntlinFileRules) {
                if (!StringUtils.isEmpty(cntlinFileRule.getName())) {
                    cntlinFileNames.add(cntlinFileRule.getName());
                }
            }
        }

        if (cntlinFileNames.isEmpty()) {
            error("Can't find cntlin files for " + getStudyName());
            return;
        }

        for (String cntlinFileName : cntlinFileNames) {

            try (TableReader reader = new FileTypeAwareTableReader(cntlinFileName, dataProvider)) {
                String[] columns = new String[0];
                try {
                    columns = cntlinFileRules.get(0).getEntity().get(0).getColumn()
                            .stream().map(col -> col.getName()).toArray(String[]::new);
                } catch (Exception ex) {
                    LOGGER.error("Error reading mapped names from file {}/{}!", cntlinFileName, ex.getMessage());
                }
                Optional<String[]> optional = reader.nextRow(columns);
                while (optional.isPresent()) {
                    String[] row = optional.get();
                    String fmtName = row[0];
                    Map<String, String> fmtMap = cntlinDataHolder.getFmtMap(fmtName);

                    if (fmtMap == null) {
                        fmtMap = new HashMap<>();
                        cntlinDataHolder.setFmtMap(fmtName, fmtMap);
                    }
                    fmtMap.put(row[1], row[2]);

                    optional = reader.nextRow(columns);
                }
            }
        }
    }

    @LogMeAround("Tasklet")
    private void cleanStudy(String studyName) {
        try {
            studyDao.delete(studyName);
            jobExecutionDao.insertScheduledCleanEtlStatus(COMPLETED, getJobExecutionId());
        } catch (Exception e) {
            jobExecutionDao.insertScheduledCleanEtlStatus("FAILED", getJobExecutionId());
        } finally {
            studyDao.setScheduleCleanFlag(studyName, false);
        }
    }

    @LogMeAround("Tasklet")
    public void setScheduledCleanFlag(String scheduledCleanFlag) {
        this.scheduledCleanFlag = Boolean.valueOf(scheduledCleanFlag);
    }
}
