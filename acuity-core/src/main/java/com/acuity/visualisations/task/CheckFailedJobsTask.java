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

package com.acuity.visualisations.task;

import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import com.acuity.visualisations.web.service.AcuityEmailNotificationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Created by knml167 on 17/06/2014.
 */
public class CheckFailedJobsTask {
    protected static final Log LOGGER = LogFactory.getLog(CheckFailedJobsTask.class);

    @Autowired
    private ExtendedJobExecutionDao customJobExecutionDao;

    @Autowired
    private AcuityEmailNotificationService acuityEmailNotificationService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void run() {
        LOGGER.info("Checking failed jobs");
        List<String> failed = customJobExecutionDao.getFailedStudies();
        List<String> notRun = customJobExecutionDao.getNotRunStudies();
        if (failed.size() > 0 || notRun.size() > 0) {
            LOGGER.warn(String.format("Failed studies: %s, not run studies: %s, not cleaned studies: %s",
                    StringUtils.join(failed, ", "), StringUtils.join(notRun, ", ")));
            acuityEmailNotificationService.sendFailedStudiesEmail(failed, notRun);
        } else {
            LOGGER.info("No failed jobs found");
        }
    }
}
