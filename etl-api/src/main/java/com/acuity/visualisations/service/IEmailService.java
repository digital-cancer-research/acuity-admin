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
