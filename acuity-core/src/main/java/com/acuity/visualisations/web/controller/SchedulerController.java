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

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.service.ETLJobService;
import com.acuity.visualisations.web.service.EtlSchedulerService;
import com.acuity.visualisations.web.service.EtlSessionService;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@PreAuthorize("@permissionHelper.canAccessSchedulerPage()")
@Controller
@Scope(value = "request")
@RequestMapping("/scheduler")
public class SchedulerController {
    private static final String STUDY_CODE = "studyCode";
    private static final String PROJECT_NAME = "projectName";

    @Value("${azureml.enable:false}")
    private boolean amlEnabledGlobally;

    @Autowired
    private EtlSchedulerService schedulerService;
    @Autowired
    private EtlSessionService etlSessionService;
    @Autowired
    private ETLJobService etlJobService;

    @RequestMapping(value = "/isScheduled/{drugProgramme}/{studyCode}")
    @ResponseBody
    public boolean isStudyScheduled(@PathVariable("studyCode") String studyCode, @PathVariable("drugProgramme") String drugProgramme)
            throws SchedulerException {
        return schedulerService.isStudyScheduled(studyCode, drugProgramme);
    }

    @RequestMapping(value = "/poll", method = RequestMethod.GET)
    @ResponseBody
    public String poll(final HttpServletRequest request) throws Exception {
        JSONObject jsonObject = etlSessionService.poll();
        return jsonObject.toString();
    }

    @RequestMapping
    public ModelAndView listJobs(final HttpServletRequest request) throws Exception {
        etlJobService.refreshJobList();
        final ModelAndView modelAndView = new ModelAndView("scheduler");
        final JSONObject resultJSON = schedulerService.getJobs();
        String resultData = StringEscapeUtils.escapeJavaScript(resultJSON.toString());
        modelAndView.addObject("data", resultData);
        modelAndView.addObject("amlEnabledGlobally", amlEnabledGlobally);
        return modelAndView;
    }

    @RequestMapping(value = "/trigger", method = RequestMethod.POST)
    @ResponseBody
    public String triggerJob(final HttpServletRequest request) throws Exception {
        String studyCode = ServletRequestUtils.getRequiredStringParameter(request, STUDY_CODE);
        String projectName = ServletRequestUtils.getRequiredStringParameter(request, PROJECT_NAME);
        JSONObject jsonObject = schedulerService.triggerJob(projectName, studyCode);
        return jsonObject.toString();
    }

    @RequestMapping(value = "/eventAknowledge", method = RequestMethod.POST)
    @ResponseBody
    public String eventAknowledge(final HttpServletRequest request) {
        return "";
    }

    @RequestMapping(value = "/unschedule", method = RequestMethod.POST)
    @ResponseBody
    public String unsheduleJob(final HttpServletRequest request) throws Exception {
        String studyCode = ServletRequestUtils.getRequiredStringParameter(request, STUDY_CODE);
        String projectName = ServletRequestUtils.getRequiredStringParameter(request, PROJECT_NAME);
        JSONObject jsonObject = schedulerService.unsheduleJob(projectName, studyCode);
        return jsonObject.toString();
    }

    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    @ResponseBody
    public String clean(final HttpServletRequest request) throws Exception {
        String studyCode = ServletRequestUtils.getRequiredStringParameter(request, STUDY_CODE);
        String projectName = ServletRequestUtils.getRequiredStringParameter(request, PROJECT_NAME);
        JSONObject jsonObject = schedulerService.clean(projectName, studyCode);
        return jsonObject.toString();
    }

    @RequestMapping(value = "/scheduleClean/{projectName}/{studyCode}", method = RequestMethod.POST)
    @ResponseBody
    public String scheduleClean(@PathVariable("projectName") String projectName,
                         @PathVariable("studyCode") String studyCode) throws Exception {
        JSONObject jsonObject = schedulerService.triggerScheduledClean(projectName, studyCode);
        etlJobService.refreshJobList();
        return jsonObject.toString();
    }

    @RequestMapping(value = "/reset-etl-status", method = RequestMethod.POST)
    @ResponseBody
    public String etlStatusReset(final HttpServletRequest request) throws Exception {
        String studyCode = ServletRequestUtils.getRequiredStringParameter(request, STUDY_CODE);
        JSONObject jsonObject = schedulerService.resetStudyEtlStatus(studyCode);
        return jsonObject.toString();
    }

    @RequestMapping(value = "/run-aml", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public String runAml(final HttpServletRequest request) throws Exception {
        String studyCode = ServletRequestUtils.getRequiredStringParameter(request, STUDY_CODE);
        String projectName = ServletRequestUtils.getRequiredStringParameter(request, PROJECT_NAME);
        Boolean amlEnabledForStudy = Boolean.valueOf(ServletRequestUtils.getRequiredStringParameter(request, "amlEnabled"));
        JSONObject jsonObject = amlEnabledGlobally && amlEnabledForStudy ? schedulerService.triggerAmlJob(projectName, studyCode) : new JSONObject();
        return jsonObject.toString();
    }
}
