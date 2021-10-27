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

package com.acuity.visualisations.batch.holders;

import com.acuity.visualisations.batch.holders.configuration.ConfigurationDataHolder;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtilHolder;
import com.acuity.visualisations.batch.holders.configuration.SdtmDataHolder;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StudyMappingConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StudyRuntimeConfigurationHolder;
import com.acuity.visualisations.batch.processor.DataAlertReport;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.batch.processor.ExceptionReport;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.util.LoggerDecorator;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class HoldersAware {

    private static final LoggerDecorator LOGGER = new LoggerDecorator(HoldersAware.class);

    @Autowired
    private HolderResolver holderResolver;

    private ConfigurationDataHolder configurationDataHolder;
    private ConfigurationUtilHolder configurationUtilFactory;
    private CntlinDataHolder cntlinDataHolder;
    private SdtmDataHolder sdtmDataHolder;

    private EOFEventHolder eofEventHolder;
    private HashValuesHolder hashValuesHolder;

    private DataAlertReport dataAlertReport;
    private DataCommonReport dataCommonReport;
    private ExceptionReport exceptionReport;

    private Long jobExecutionId;
    private String jobName;

    private String projectName;
    private String studyName;
    private String uniqueKey;

    private JobExecutionInfo jobExecutionInfo;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecutionId = stepExecution.getJobExecutionId();
        projectName = stepExecution.getJobParameters().getString(JobLauncherConsts.PROJECT_KEY);
        studyName = stepExecution.getJobParameters().getString(JobLauncherConsts.STUDY_KEY);
        uniqueKey = stepExecution.getJobParameters().getString(JobLauncherConsts.UNIQUE_KEY);
        jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        jobExecutionInfo = new JobExecutionInfoBuilder().setJobExecutionId(jobExecutionId).setJobName(jobName).setProjectName(projectName)
                .setStudyName(studyName).setUniqueKey(uniqueKey).build();
        initHolders();
    }

    protected abstract void initHolders();

    protected Long getJobExecutionId() {
        return jobExecutionId;
    }

    protected String getJobName() {
        return jobName;
    }

    protected String getProjectName() {
        return projectName;
    }

    protected String getStudyName() {
        return studyName;
    }

    protected String getUniqueKey() {
        return uniqueKey;
    }

    protected void error(String format, Object[] argArray) {
        LOGGER.error(jobExecutionId, jobName, projectName, studyName, format, argArray);
    }

    protected void error(String format, Object arg) {
        LOGGER.error(jobExecutionId, jobName, projectName, studyName, format, arg);
    }

    protected void error(String message) {
        LOGGER.error(jobExecutionId, jobName, projectName, studyName, message);
    }

    protected void error(String message, Throwable t) {
        LOGGER.error(jobExecutionId, jobName, projectName, studyName, message, t);
    }

    protected void debug(String format, Object... argArray) {
        LOGGER.debug(jobExecutionId, jobName, projectName, studyName, format, argArray);
    }

    protected void debug(String format, Object arg) {
        LOGGER.debug(jobExecutionId, jobName, projectName, studyName, format, arg);
    }

    protected void debug(String message) {
        LOGGER.debug(jobExecutionId, jobName, projectName, studyName, message);
    }

    protected void debug(String message, Throwable t) {
        LOGGER.debug(jobExecutionId, jobName, projectName, studyName, message, t);
    }

    protected void info(String format, Object[] argArray) {
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, format, argArray);
    }

    protected void info(String format, Object arg) {
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, format, arg);
    }

    protected void info(String message) {
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, message);
    }

    protected void info(String message, Throwable t) {
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, message, t);
    }

    protected void warn(String format, Object[] argArray) {
        LOGGER.warn(jobExecutionId, jobName, projectName, studyName, format, argArray);
    }

    protected void warn(String format, Object arg) {
        LOGGER.warn(jobExecutionId, jobName, projectName, studyName, format, arg);
    }

    protected void warn(String message) {
        LOGGER.warn(jobExecutionId, jobName, projectName, studyName, message);
    }

    protected void warn(String message, Throwable t) {
        LOGGER.warn(jobExecutionId, jobName, projectName, studyName, message, t);
    }

    protected ConfigurationUtilHolder getConfigurationUtilHolder() {
        if (configurationUtilFactory == null) {
            configurationUtilFactory = holderResolver.getConfigurationUtilHolder(jobExecutionId);
            configurationUtilFactory.setConfigurationDataHolder(getConfigurationDataHolder());
            configurationUtilFactory.setJobExecutionInfo(jobExecutionInfo);
        }
        return configurationUtilFactory;
    }

    protected ConfigurationUtil<?> getConfigurationUtil() {
        return getConfigurationUtilHolder().getConfigurationUtil();
    }

    protected StudyRuntimeConfigurationHolder getStudyRuntimeConfigurationHolder() {
        return getConfigurationDataHolder().getRuntimeConfiguration();
    }

    protected StudyMappingConfigurationHolder getStudyMappingConfigurationHolder() {
        return getConfigurationDataHolder().getMappingConfiguration();
    }

    protected StaticConfigurationHolder getStaticConfigurationHolder() {
        return getConfigurationDataHolder().getStaticConfigurationHolder();
    }

    protected ConfigurationDataHolder getConfigurationDataHolder() {
        if (configurationDataHolder == null) {
            configurationDataHolder = holderResolver.getConfigurationDataHolder(jobExecutionId);
            configurationDataHolder.setJobExecutionInfo(jobExecutionInfo);
        }
        return configurationDataHolder;
    }

    protected CntlinDataHolder getCntlinDataHolder() {
        if (cntlinDataHolder == null) {
            cntlinDataHolder = holderResolver.getCntlinDataHolder(jobExecutionId);
            cntlinDataHolder.setJobExecutionInfo(jobExecutionInfo);
        }
        return cntlinDataHolder;
    }

    protected SdtmDataHolder getSdtmDataHolder() {
        if (sdtmDataHolder == null) {
            sdtmDataHolder = holderResolver.getSdtmSupplementalDataHolder(jobExecutionId);
        }
        return sdtmDataHolder;
    }

    protected EOFEventHolder getEOFEventHolder() {
        if (eofEventHolder == null) {
            eofEventHolder = holderResolver.getEOFEventHolder(jobExecutionId);
            eofEventHolder.setJobExecutionInfo(jobExecutionInfo);
        }
        return eofEventHolder;
    }

    protected HashValuesHolder getHashValuesHolder() {
        if (hashValuesHolder == null) {
            hashValuesHolder = holderResolver.getHashValuesHolder(jobExecutionId);
            hashValuesHolder.setJobExecutionInfo(jobExecutionInfo);
        }
        return hashValuesHolder;
    }

    protected DataAlertReport getDataAlertReport() {
        if (dataAlertReport == null) {
            dataAlertReport = holderResolver.getDataAlertReport(jobExecutionId);
            dataAlertReport.setJobExecutionInfo(jobExecutionInfo);
        }
        return dataAlertReport;
    }

    protected DataCommonReport getDataCommonReport() {
        if (dataCommonReport == null) {
            dataCommonReport = holderResolver.getDataCommonReport(jobExecutionId);
            dataCommonReport.setJobExecutionInfo(jobExecutionInfo);
        }
        return dataCommonReport;
    }

    protected ExceptionReport getExceptionReport() {
        if (exceptionReport == null) {
            exceptionReport = holderResolver.getExceptionReport(jobExecutionId);
            exceptionReport.setJobExecutionInfo(jobExecutionInfo);
        }
        return exceptionReport;
    }

    protected void removeAllHolders() {
        holderResolver.removeConfigurationUtilHolder(jobExecutionId);
        holderResolver.removeCntlinDataHolder(jobExecutionId);
        holderResolver.removeSdtmSupplementalDataHolder(jobExecutionId);
        holderResolver.removeConfigurationDataHolder(jobExecutionId);
        holderResolver.removeEOFEventHolder(jobExecutionId);
        holderResolver.removeHashValuesHolder(jobExecutionId);
        holderResolver.removeDataAlertReport(jobExecutionId);
        holderResolver.removeDataCommonReport(jobExecutionId);
        holderResolver.removeExceptionReport(jobExecutionId);
    }
}
