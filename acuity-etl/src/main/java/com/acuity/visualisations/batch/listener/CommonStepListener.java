package com.acuity.visualisations.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.util.LoggerDecorator;

@Component("stepListener")
public class CommonStepListener implements StepExecutionListener {

    private static final LoggerDecorator LOGGER = new LoggerDecorator(CommonStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Long jobExecutionId = stepExecution.getJobExecution().getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String studyName = jobParameters.getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobParameters.getString(JobLauncherConsts.PROJECT_KEY);
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, "Step started, Step Name: {}",
                new Object[]{stepExecution.getStepName()});
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Long jobExecutionId = stepExecution.getJobExecution().getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String studyName = jobParameters.getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobParameters.getString(JobLauncherConsts.PROJECT_KEY);
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, "Step finished, Step Name: {}, Step Status: {}", new Object[]{
                stepExecution.getStepName(), stepExecution.getStatus()});
        return stepExecution.getExitStatus();
    }

}
