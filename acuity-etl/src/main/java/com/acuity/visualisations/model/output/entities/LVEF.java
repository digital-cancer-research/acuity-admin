package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class LVEF extends TimestampedEntity {

    private String testGuid;
    private Integer lvef;
    private String method;
    private String methodOther;
    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    public LVEF() {
        initId();
    }

    public String getTestGuid() {
        return testGuid;
    }

    public void setTestGuid(String testGuid) {
        this.testGuid = testGuid;
    }

    public Integer getLvef() {
        return lvef;
    }

    public void setLvef(Integer lvef) {
        this.lvef = lvef;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodOther() {
        return methodOther;
    }

    public void setMethodOther(String methodOther) {
        this.methodOther = methodOther;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("date", date).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("date", date).
                append("lvef", lvef).
                append("method", method).
                append("methodOther", methodOther).
                toString();
    }

}
