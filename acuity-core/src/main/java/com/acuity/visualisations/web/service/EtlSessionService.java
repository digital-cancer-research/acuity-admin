package com.acuity.visualisations.web.service;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.util.JsonUtil;
import org.json.JSONObject;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.util.JobLauncherConsts.AML_JOB_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.ETL_GROUP_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.ETL_JOB_NAME;

@Component
@Scope("session")
public class EtlSessionService {

    @Autowired
    private SchedulerSessionStorage storage;

    @Autowired
    private EtlSchedulerService etlSchedulerService;

    @Autowired
    @Qualifier("schedulerFactory")
    private Scheduler scheduler;

    @Autowired
    private UIFunctionsManager functionsManager;

    @LogMeAround
    public JSONObject poll() throws Exception {
        JobExecutionEvent event = storage.getEvent();

        String projectName = event.getExecutionKey().getProjectName();
        String studyName = event.getExecutionKey().getStudyCode();
        String jobExecutionGuid = event.getExecutionKey().getJobExecutionGuid();
        String eventGuid = event.getExecutionId();
        JSONObject jsonObject = new JSONObject();
        String jobName = event.isInAmlRunningState() ? AML_JOB_NAME : ETL_JOB_NAME;
        JobExecution latestJobExecution = functionsManager.getLatestJobExecution(projectName, studyName, jobExecutionGuid, jobName);

        if (event.isInEtlRunningState()) {
            JsonUtil.setJobExecutionAndCurrentRunStatusToJson(jsonObject, latestJobExecution, event.getExecutionState());
            Map<String, Map<String, EtlSchedulerService.JobInfo>> jobInfos = etlSchedulerService.getSchedulerInfo(ETL_GROUP_NAME);
            EtlSchedulerService.JobInfo jobInfo = jobInfos.get(projectName).get(studyName);
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobInfo.getJobKey());
            JsonUtil.setTriggerToJson(jsonObject, triggersOfJob.get(0));
        } else if (event.isInAmlRunningState()) {
            JsonUtil.setAmlStepExecutionAndCurrentRunStatusToJson(jsonObject, latestJobExecution, event.getExecutionState());
        } else {
            if (event.isStudyInDatabase()) {
                boolean inDatabase = functionsManager.isStudyInDatabase(projectName, studyName);
                JsonUtil.setStudyInDatabase(jsonObject, inDatabase);
            } else {
                if (latestJobExecution != null) {
                    JsonUtil.setJobExecutionToJson(jsonObject, latestJobExecution);
                } else {
                    JsonUtil.setEmptyJobExecutionToJson(jsonObject);
                }
                if (event.isFinished() && event.getException() != null) {
                    JsonUtil.setError(jsonObject, event.getException().getMessage());
                }
            }
        }
        jsonObject.put("projectName", projectName);
        jsonObject.put("studyCode", studyName);
        jsonObject.put("eventGuid", eventGuid);
        return jsonObject;
    }

}
