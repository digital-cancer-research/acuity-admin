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

package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

/**
 * Adverse Event
 */
@NoArgsConstructor
public class AE extends TimestampedEntity {
    private static final String COMMENT_CONST = "comment";
    private String eventTypeGuid; //FK to EventType
    private String patientGuid; //FK to Patient

    private String aeText;
    private String comment;
    private String serious;
    private Integer number;

    private String outcome;
    private String doseLimitingToxicity;
    private String timePoint;
    private String immuneMediated;
    private String infusionReaction;
    private String requiredTreatment;
    private String causedSubjectWithdrawal;
    private String suspectedEndpoint;
    private String suspectedEndpointCategory;
    private String aeOfSpecialInterest;

    //We need these fields only for foreign keys to EventType and Patient tables
    private transient String subject;
    private transient String part;
    private transient String pt;
    private transient String hlt;
    private transient String soc;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private transient LocalDateTime startDate;

    public String getSuspectedEndpoint() {
        return suspectedEndpoint;
    }

    public void setSuspectedEndpoint(String suspectedEndpoint) {
        this.suspectedEndpoint = suspectedEndpoint;
    }

    public String getSuspectedEndpointCategory() {
        return suspectedEndpointCategory;
    }

    public void setSuspectedEndpointCategory(String suspectedEndpointCategory) {
        this.suspectedEndpointCategory = suspectedEndpointCategory;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject).append("startDate", startDate).append("PT", pt).append("HLT", hlt).append("SOC", soc)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject).append("startDate", startDate).append("PT", pt)
                .append("serious", serious).append("number", number)
                .append("aeText", aeText)
                .append(COMMENT_CONST, outcome)
                .append(COMMENT_CONST, doseLimitingToxicity)
                .append(COMMENT_CONST, timePoint)
                .append(COMMENT_CONST, immuneMediated)
                .append(COMMENT_CONST, infusionReaction)
                .append(COMMENT_CONST, requiredTreatment)
                .append(COMMENT_CONST, causedSubjectWithdrawal)
                .append(COMMENT_CONST, comment)
                .toString();
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getPT() {
        return pt;
    }

    public void setPT(String pt) {
        this.pt = pt;
    }

    public String getEventTypeGuid() {
        return eventTypeGuid;
    }

    public void setEventTypeGuid(String eventTypeGuid) {
        this.eventTypeGuid = eventTypeGuid;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getAeText() {
        return aeText;
    }

    public void setAeText(String aeText) {
        this.aeText = aeText;
    }

    public String getSerious() {
        return serious;
    }

    public void setSerious(String serious) {
        this.serious = serious;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getHLT() {
        return hlt == null ? null : hlt.toUpperCase();
    }

    public void setHLT(String hlt) {
        this.hlt = hlt == null ? null : hlt.toUpperCase();
    }

    public String getSOC() {
        return soc == null ? null : soc.toUpperCase();
    }

    public void setSOC(String soc) {
        this.soc = soc == null ? null : soc.toUpperCase();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getDoseLimitingToxicity() {
        return doseLimitingToxicity;
    }

    public void setDoseLimitingToxicity(String doseLimitingToxicity) {
        this.doseLimitingToxicity = doseLimitingToxicity;
    }

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    public String getImmuneMediated() {
        return immuneMediated;
    }

    public void setImmuneMediated(String immuneMediated) {
        this.immuneMediated = immuneMediated;
    }

    public String getInfusionReaction() {
        return infusionReaction;
    }

    public void setInfusionReaction(String infusionReaction) {
        this.infusionReaction = infusionReaction;
    }

    public String getRequiredTreatment() {
        return requiredTreatment;
    }

    public void setRequiredTreatment(String requiredTreatment) {
        this.requiredTreatment = requiredTreatment;
    }

    public String getCausedSubjectWithdrawal() {
        return causedSubjectWithdrawal;
    }

    public void setCausedSubjectWithdrawal(String causedSubjectWithdrawal) {
        this.causedSubjectWithdrawal = causedSubjectWithdrawal;
    }

    public String getAeOfSpecialInterest() {
        return aeOfSpecialInterest;
    }

    public void setAeOfSpecialInterest(String aeOfSpecialInterest) {
        this.aeOfSpecialInterest = aeOfSpecialInterest;
    }
}
