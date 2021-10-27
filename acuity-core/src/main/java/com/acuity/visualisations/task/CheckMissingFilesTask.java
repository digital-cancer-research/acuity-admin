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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import com.acuity.visualisations.web.service.AcuityEmailNotificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by knml167 on 23/11/2015.
 */
public class CheckMissingFilesTask {
    protected static final Log LOGGER = LogFactory.getLog(CheckMissingFilesTask.class);

    @Autowired
    private ExtendedJobExecutionDao customJobExecutionDao;

    @Autowired
    private AcuityEmailNotificationService acuityEmailNotificationService;

    @Autowired
    private DataProvider provider;

    @Scheduled(cron = "0 0 6 * * ?")
    public void run() {
        LOGGER.info("Checking missing source files");
        Map<String, List<String>> studyAllFiles = customJobExecutionDao.getScheduledStudiesFilenames();
        Map<String, List<String>> studyMissingFiles = new HashMap<>();
        studyAllFiles.forEach((key, allFiles) -> {
            List<String> missingFiles = allFiles.stream().filter(Objects::nonNull).filter(fn -> {
                try {
                    Data data = provider.get(fn);
                    return !data.exists();
                } catch (Throwable ignored) {
                    return true;
                }
            }).collect(Collectors.toList());
            if (missingFiles.size() > 0) {
                studyMissingFiles.put(key, missingFiles);
            }
        });
        if (!studyMissingFiles.isEmpty()) {
            acuityEmailNotificationService.sendMissingFilesNotificationEmail(studyMissingFiles);
        }
    }
}
