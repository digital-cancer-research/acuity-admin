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
import com.acuity.visualisations.batch.processor.DataAlertReport;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.batch.processor.ExceptionReport;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.service.IExecutionProfiler;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Spring batch tasklet the published the data reports
 */
@Component("publishReportsTasklet")
@Scope("step")
public class PublishReportsTasklet extends HoldersAware implements Tasklet {

    /**
     * The service object that will be reporting the exceptions
     */
    private ExceptionReport exceptionReport;

    @Autowired
    private IDataSummaryReportDao summaryReportDao;


    @Autowired
    private IExecutionProfiler executionProfiler;

    /**
     * Report on any data alerts
     */
    private DataAlertReport dataAlertReport;

    /**
     * Report on common data report (table, field, value)
     */
    private DataCommonReport dataCommonReport;

    @Override
    protected void initHolders() {
        dataAlertReport = getDataAlertReport();
        dataCommonReport = getDataCommonReport();
        exceptionReport = getExceptionReport();
    }

    @Override
    @LogMeAround("Tasklet")
    public RepeatStatus execute(StepContribution sc, ChunkContext chunkContext) throws Exception {
        executionProfiler.startOperation(getJobExecutionId(), "publishReports");
        dataAlertReport.publishReport(getStudyName(), getJobExecutionId());
        dataCommonReport.publishReport(chunkContext);
        Collection<StepExecution> stepExecutions = chunkContext.getStepContext().getStepExecution().getJobExecution().getStepExecutions();
        reportExceptions(chunkContext.getStepContext().getStepExecution().getJobExecutionId(), stepExecutions);
        executionProfiler.stopOperation(getJobExecutionId(), "publishReports");
        return null;
    }


    /**
     * This method reports any exceptions encountered during the batch run. It
     * is deliberately not included in the {@link DataCommonReport} class
     * because we want to capture exceptions that may occur there too.
     *
     * @param jobExecID      The job execution ID
     * @param stepExecutions The steps that have been executed in the batch run
     */
    @LogMeAround("Tasklet")
    private void reportExceptions(long jobExecID, Collection<StepExecution> stepExecutions) {
        for (StepExecution execution : stepExecutions) {
            // Collect all of the exceptions
            for (Throwable exception : execution.getFailureExceptions()) {
                exceptionReport.addException(execution.getStepName(), exception);
            }
        }

        boolean published = exceptionReport.publishReport();

        // This condition will be satisfied if an exception has occurred
        // prior to the readProcessWrite
        // step in the ETL
        if (published && !this.summaryReportDao.isReportDataPresent(jobExecID)) {
            this.summaryReportDao.insertReportDataForUnsuccessfulEtlRun(ReportSummaryEntity.builder()
                    .jobExecID(jobExecID)
                    .ragStatus(RagStatus.RED).build());
        }
    }

}
