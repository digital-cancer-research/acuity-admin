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

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.service.IEmailService;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import com.acuity.visualisations.web.dao.EmailDetailsDao;
import com.acuity.visualisations.web.entity.EmailDetailsEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for sending emails.
 */
@Service
public class AcuityEmailNotificationService implements IEmailService {

    @Value("${send-debug-info-to}")
    private String debugEmailTo;

    @Autowired
    private Environment environment;
    /**
     * The mail sender object that is responsible for sending the email
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * The DAO for getting email parameters
     */
    @Autowired
    private EmailDetailsDao emailDetailsDao;

    @Autowired
    private AdminService adminService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendProjectCreationEmail(final String drugId, final String requestor, final String dataOwner) {

        final Optional<EmailDetailsEntity> emailDetailsOpt = this.emailDetailsDao.getAcuityProjectEnabledEmailDetails();
        if (!emailDetailsOpt.isPresent()) {
            return;
        }
        EmailDetailsEntity emailDetails = emailDetailsOpt.get();

        String text = String.format(emailDetails.getText(), requestor, drugId, drugId, drugId, drugId, dataOwner,
                "5", "please see updates in analytics application", drugId, drugId, drugId, drugId);
        ClassPathResource attachment = (emailDetails.getAttachments() == null)
                ? null : new ClassPathResource(emailDetails.getAttachments());

        sendMessage(emailDetails, text, 0, attachment);
    }

    @Override
    public void sendFailedStudiesEmail(final List<String> failedStudies, final List<String> notRunStudies) {
        final Optional<EmailDetailsEntity> emailDetailsOpt = this.emailDetailsDao.getStudyJobsFailedEmailDetails();
        if (!emailDetailsOpt.isPresent()) {
            return;
        }
        EmailDetailsEntity emailDetails = emailDetailsOpt.get();

        String text = String.format(emailDetails.getText(),
                failedStudies.size() == 0 ? "<not found>" : StringUtils.join(failedStudies, ", "),
                notRunStudies.size() == 0 ? "<not found>" : StringUtils.join(notRunStudies, ", "));

        sendMessage(emailDetails, text);
    }

    @Override
    public void sendStudyExclusionValuesChangedEmail(final StudyRule study, final String oldValues, final String newValues) {
        final Optional<EmailDetailsEntity> emailDetailsOpt = this.emailDetailsDao.getStudyExcludingValuesChangedDetails();
        if (!emailDetailsOpt.isPresent()) {
            return;
        }
        EmailDetailsEntity emailDetails = emailDetailsOpt.get();

        String text = emailDetails.getText()
                .replace("%%DRUG_ID%%", adminService.getProjectRule(study.getProjectId()).getDrugId())
                .replace("%%STUDY_ID%%", study.getStudyCode())
                .replace("%%TIMESTAMP%%", new Date().toString())
                .replace("%%USER%%", UserInfoHolder.getUserInfo().getDisplayName())
                .replace("%%OLD_VALUES%%", oldValues)
                .replace("%%NEW_VALUES%%", newValues);

        sendMessage(emailDetails, text, 2, null);
    }

    @Override
    public void sendStudyConfigChangedEmail(final StudyRule oldStudy, final StudyRule newStudy) {
        final Optional<EmailDetailsEntity> emailDetailsOpt = this.emailDetailsDao.getStudyConfigChangedDetails();
        if (!emailDetailsOpt.isPresent()) {
            return;
        }
        EmailDetailsEntity emailDetails = emailDetailsOpt.get();

        String text = emailDetails.getText()
                .replace("%%DRUG_ID%%", adminService.getProjectRule(newStudy.getProjectId()).getDrugId())
                .replace("%%STUDY_ID%%", newStudy.getStudyCode())
                .replace("%%TIMESTAMP%%", new Date().toString())
                .replace("%%USER%%", UserInfoHolder.getUserInfo().getDisplayName())
                .replace("%%OLD_BLND%%", Boolean.toString(oldStudy.isBlinding()))
                .replace("%%OLD_RND%%", Boolean.toString(oldStudy.isRandomisation()))
                .replace("%%OLD_REG%%", Boolean.toString(oldStudy.isRegulatory()))
                .replace("%%NEW_BLND%%", Boolean.toString(newStudy.isBlinding()))
                .replace("%%NEW_RND%%", Boolean.toString(newStudy.isRandomisation()))
                .replace("%%NEW_REG%%", Boolean.toString(newStudy.isRegulatory()));

        sendMessage(emailDetails, text);
    }

    public void sendMissingFilesNotificationEmail(Map<String, List<String>> studyMissingFiles) {

        final Optional<EmailDetailsEntity> emailDetailsOpt = this.emailDetailsDao.getMissingSourceFilesDetails();
        if (!emailDetailsOpt.isPresent()) {
            return;
        }
        StringBuilder list = new StringBuilder();
        list.append("<ul>");
        studyMissingFiles.forEach((key, value) -> {
            list.append("<b>").append(key).append("</b>").append("<ul>");
            value.forEach(file -> {
                list.append("<li>").append(file).append("</li>");
            });
            list.append("</ul>");
        });
        list.append("</ul>");

        EmailDetailsEntity emailDetails = emailDetailsOpt.get();
        String text = String.format(emailDetails.getText(), list.toString());
        sendMessage(emailDetails, text);
    }

    private void sendMessage(EmailDetailsEntity emailDetails, String text) {
        sendMessage(emailDetails, text, 0, null);
    }

    private void sendMessage(EmailDetailsEntity emailDetails, String text, int priority, ClassPathResource attachment) {
        this.mailSender.send(getMessage(emailDetails, text, priority, attachment));
    }

    private MimeMessagePreparator getMessage(EmailDetailsEntity emailDetails, String text,
                                             int priority, ClassPathResource attachment) {
        return mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(emailDetails.getFromAddress());
            if (environment.acceptsProfiles("prod")) {
                message.setTo(emailDetails.getToAddresses().split(";"));

                if (emailDetails.getCcAddresses() != null) {
                    message.setCc(emailDetails.getCcAddresses().split(";"));
                }
            } else {
                //if not prod, send mail to debug recipients
                message.setTo(debugEmailTo.split(";"));
            }
            message.setSubject(emailDetails.getSubject() + "/" + Arrays.toString(environment.getActiveProfiles()));
            message.setText(text, true);
            if (priority > 0) {
                message.setPriority(priority);
            }
            if (attachment != null) {
                message.addAttachment(attachment.getFilename(), attachment);
            }
        };
    }
}

