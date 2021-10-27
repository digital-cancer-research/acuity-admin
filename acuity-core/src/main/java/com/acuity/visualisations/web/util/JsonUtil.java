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

package com.acuity.visualisations.web.util;

import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.Trigger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import static com.acuity.visualisations.data.util.Util.subtractDates;

public final class JsonUtil {

    private static final String NONE = "-";

    private static final String PREVIOUS_RUN = "previousRun";
    private static final String PREVIOUS_RUN_STATUS = "previousRunStatus";
    private static final String PREVIOUS_RUN_TIME = "previousRunTime";
    private static final String NEXT_RUN = "nextRun";

    private static final String PREVIOUS_AML_RUN = "previousAmlRun";
    private static final String PREVIOUS_AML_RUN_STATUS = "previousAmlRunStatus";
    private static final String PREVIOUS_AML_RUN_TIME = "previousAmlRunTime";

    private static JsonFactory jf = new JsonFactory();
    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Util.DATE_FORMAT);

    private JsonUtil() {
    }

    public static void setStudyInDatabase(JSONObject jsonObject, Object inDatabase) throws JSONException {
        if (inDatabase != null) {
            jsonObject.put("inDatabase", inDatabase.toString());
        } else {
            jsonObject.put("inDatabase", "false");
        }
    }

    public static void setEmptyJobExecutionToJson(JSONObject jsonObject) throws JSONException {
        jsonObject.put(PREVIOUS_RUN, NONE);
        jsonObject.put(PREVIOUS_RUN_STATUS, NONE);
        jsonObject.put(PREVIOUS_RUN_TIME, NONE);
    }

    public static void setJobExecutionAndCurrentRunStatusToJson(JSONObject jsonObject, JobExecution jobExecution,
                                                                ExecutionState executionState) throws JSONException {
        setPreviousRunDateToJson(jobExecution, jsonObject);
        setPreviousRunTimeToJson(jobExecution, jsonObject);
        jsonObject.put(PREVIOUS_RUN_STATUS, executionState.toString());
    }

    public static void setFailedJobExecutionToJson(JSONObject jsonObject) throws JSONException {
        jsonObject.put(PREVIOUS_RUN_STATUS, "FAILED");
    }

    private static void setPreviousRunDateToJson(JobExecution jobExecution, JSONObject jsonObject) throws JSONException {
        if (jobExecution == null || jobExecution.getEndTime() == null) {
            jsonObject.put(PREVIOUS_RUN, simpleDateFormat.format(Util.currentDate()));
            jsonObject.put(PREVIOUS_RUN_STATUS, NONE);
        } else {
            jsonObject.put(PREVIOUS_RUN, simpleDateFormat.format(jobExecution.getEndTime()));
        }
    }

    private static void setPreviousRunTimeToJson(JobExecution jobExecution, JSONObject jsonObject) throws JSONException {
        jsonObject.put(PREVIOUS_RUN_TIME,
                (jobExecution == null || jobExecution.getEndTime() == null)
                        ? NONE : subtractDates(jobExecution.getEndTime(), jobExecution.getStartTime()));
    }

    public static void setJobExecutionToJson(JSONObject jsonObject, JobExecution latestJobExecution) throws JSONException {
        setPreviousRunDateToJson(latestJobExecution, jsonObject);
        setPreviousRunTimeToJson(latestJobExecution, jsonObject);

        if (latestJobExecution != null && latestJobExecution.getStatus() != null) {
            if (latestJobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
                jsonObject.put(PREVIOUS_RUN_STATUS, latestJobExecution.getExitStatus().getExitCode());
            } else {
                jsonObject.put(PREVIOUS_RUN_STATUS, latestJobExecution.getStatus());
            }
        } else {
            jsonObject.put(PREVIOUS_RUN_STATUS, NONE);
        }
    }

    public static void setSchedulerJobExecutionToJson(JSONObject jsonObject, ExecutionState executionState)
            throws JSONException {
        jsonObject.put(PREVIOUS_RUN, NONE);
        jsonObject.put(PREVIOUS_RUN_STATUS, executionState);
        jsonObject.put(PREVIOUS_RUN_TIME, NONE);
    }

    public static void setTriggerToJson(JSONObject jsonObject, Trigger trigger) throws JSONException {
        SimpleDateFormat dt = new SimpleDateFormat(Util.DATE_FORMAT);
        if (trigger != null && trigger.getNextFireTime() != null) {
            jsonObject.put(NEXT_RUN, dt.format(trigger.getNextFireTime()));
        } else {
            jsonObject.put(NEXT_RUN, NONE);
        }
    }

    public static void setScheduledCleanFlag(JSONObject jsonObject, boolean scheduledCleanFlag) throws JSONException {
        jsonObject.put("scheduledClean", scheduledCleanFlag);
    }

    public static void setError(JSONObject jsonObject, String errorMessage) throws JSONException {
        jsonObject.put("error", errorMessage);
    }

    public static void setAmlJobDataToJson(JSONObject jsonObject, JobExecution jobExecution, Boolean amlEnabled) throws JSONException {
        if (!amlEnabled || jobExecution == null || jobExecution.getEndTime() == null) {
            jsonObject.put(PREVIOUS_AML_RUN, NONE);
            jsonObject.put(PREVIOUS_AML_RUN_STATUS, NONE);
            jsonObject.put(PREVIOUS_AML_RUN_TIME, NONE);
        } else {
            jsonObject.put(PREVIOUS_AML_RUN, simpleDateFormat.format(jobExecution.getEndTime()));
            jsonObject.put(PREVIOUS_AML_RUN_STATUS, jobExecution.getStatus());
            jsonObject.put(PREVIOUS_AML_RUN_TIME, subtractDates(jobExecution.getEndTime(), jobExecution.getStartTime()));

        }
    }

    public static void setAmlStepExecutionAndCurrentRunStatusToJson(JSONObject jsonObject, JobExecution jobExecution,
                                                                    ExecutionState executionState) throws JSONException {
        if (jobExecution == null || jobExecution.getEndTime() == null) {
            jsonObject.put(PREVIOUS_AML_RUN, simpleDateFormat.format(Util.currentDate()));
        } else {
            jsonObject.put(PREVIOUS_AML_RUN, simpleDateFormat.format(jobExecution.getEndTime()));
            jsonObject.put(PREVIOUS_AML_RUN_TIME,
                    ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())
                            ? subtractDates(jobExecution.getEndTime(), jobExecution.getStartTime()) : NONE);
        }
        jsonObject.put(PREVIOUS_AML_RUN_STATUS, executionState);
    }

    public static String toJson(Object pojo) throws IOException {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createJsonGenerator(sw);
        mapper.writeValue(jg, pojo);
        return StringUtils.replaceEach(sw.toString(), new String[]{"<", ">"}, new String[]{"&lt;", "&gt;"});  //prevent script injection
    }
}
