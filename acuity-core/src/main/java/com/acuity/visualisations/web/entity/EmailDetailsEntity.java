package com.acuity.visualisations.web.entity;

/**
 * Entity class for the UTIL_EMAIL_DETAILS database table
 */
public class EmailDetailsEntity {

    public enum EmailType {
        DRUG_PROJECT_ENABLED,
        STUDY_JOBS_FAILED,
        STUDY_SETUP_VALUES_CHANGED,
        STUDY_EXCLUDING_VALUES_CHANGED,
        FILES_NOT_ACCESSIBLE;
    }

    private EmailType emailType;
    private String toAddresses;
    private String ccAddresses;
    private String fromAddress;
    private String subject;
    private String attachments;
    private String text;

    public String getEmailType() {
        return emailType.toString();
    }

    public void setEmailType(String emailType) {
        this.emailType = EmailType.valueOf(emailType);
    }

    public String getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(String ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
