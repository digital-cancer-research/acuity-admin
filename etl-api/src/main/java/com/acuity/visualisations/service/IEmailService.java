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

package com.acuity.visualisations.service;

import com.acuity.visualisations.mapping.entity.StudyRule;

import java.util.List;

/**
 * Interface for the sending of emails
 */
public interface IEmailService {

    /**
     * When a new ACUITY-enabled drug programme is created in the ACUITY project setup UI,
     * an email is sent to a ACUITY system administrator. This method sends the email.
     *
     * @param drugId    The name of the project e.g. AZ4547
     * @param requestor The PRID of the user making the request
     * @param dataOwner The name of the project's data owner
     */
    void sendProjectCreationEmail(String drugId, String requestor, String dataOwner);
    void sendFailedStudiesEmail(List<String> failedStudies, List<String> notRunStudies);
    void sendStudyConfigChangedEmail(StudyRule oldStudy, StudyRule newStudy);
    void sendStudyExclusionValuesChangedEmail(StudyRule study, String oldValues, String newValues);

}
