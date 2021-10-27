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

@NoArgsConstructor
public class AdverseEvent extends TimestampedEntity implements Cloneable {

    private String eventTypeGuid;
    private String patientGuid;
    private String aeText;
    private String comment;

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

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;

    private String serious;
    private String causality;
    private String maxSeverity;
    private String actionTaken;
    private String subject;
    private String part;
    private String pt;
    private String hlt;
    private String soc;
    private String llt;
    private Integer number;

    private transient String startingCtcGrade;
    private transient Object[] ctcGradeChanges;
    private transient Object[] ctcGradeChangeDates;

    private transient Object[] ipDrugs;
    private transient Object[] adDrugs;

    private transient Object[] initialActionTakenForIpDrugs;
    private transient Object[] initialActionTakenForAdDrugs;

    private transient Object[] changedActionTakenForIpDrugs;
    private transient Object[] changedActionTakenForAdDrugs;

    private transient Object[] causalityForIpDrugs;
    private transient Object[] causalityForAdDrugs;

    private String groupGuid;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (AdverseEvent) super.clone();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Object[] getIpDrugs() {
        return ipDrugs;
    }

    public void setIpDrugs(Object[] ipDrugs) {
        this.ipDrugs = ipDrugs;
    }

    public Object[] getAdDrugs() {
        return adDrugs;
    }

    public void setAdDrugs(Object[] adDrugs) {
        this.adDrugs = adDrugs;
    }

    public Object[] getInitialActionTakenForIpDrugs() {
        return initialActionTakenForIpDrugs;
    }

    public void setInitialActionTakenForIpDrugs(Object[] initialActionTakenForIpDrugs) {
        this.initialActionTakenForIpDrugs = initialActionTakenForIpDrugs;
    }

    public Object[] getInitialActionTakenForAdDrugs() {
        return initialActionTakenForAdDrugs;
    }

    public void setInitialActionTakenForAdDrugs(Object[] initialActionTakenForAdDrugs) {
        this.initialActionTakenForAdDrugs = initialActionTakenForAdDrugs;
    }

    public Object[] getChangedActionTakenForIpDrugs() {
        return changedActionTakenForIpDrugs;
    }

    public void setChangedActionTakenForIpDrugs(Object[] changedActionTakenForIpDrugs) {
        this.changedActionTakenForIpDrugs = changedActionTakenForIpDrugs;
    }

    public Object[] getChangedActionTakenForAdDrugs() {
        return changedActionTakenForAdDrugs;
    }

    public void setChangedActionTakenForAdDrugs(Object[] changedActionTakenForAdDrugs) {
        this.changedActionTakenForAdDrugs = changedActionTakenForAdDrugs;
    }

    public Object[] getCausalityForIpDrugs() {
        return causalityForIpDrugs;
    }

    public void setCausalityForIpDrugs(Object[] causalityForIpDrugs) {
        this.causalityForIpDrugs = causalityForIpDrugs;
    }

    public Object[] getCausalityForAdDrugs() {
        return causalityForAdDrugs;
    }

    public void setCausalityForAdDrugs(Object[] causalityForAdDrugs) {
        this.causalityForAdDrugs = causalityForAdDrugs;
    }

    public String getGroupGuid() {
        return groupGuid;
    }

    public void setGroupGuid(String groupGuid) {
        this.groupGuid = groupGuid;
    }

    public String getStartingCtcGrade() {
        return startingCtcGrade;
    }

    public void setStartingCtcGrade(String startingCtcGrade) {
        this.startingCtcGrade = startingCtcGrade;
    }

    public Object[] getCtcGradeChanges() {
        return ctcGradeChanges;
    }

    public void setCtcGradeChanges(Object[] ctcGradeChanges) {
        this.ctcGradeChanges = ctcGradeChanges;
    }

    public Object[] getCtcGradeChangeDates() {
        return ctcGradeChangeDates;
    }

    public void setCtcGradeChangeDates(Object[] ctcGradeChangeDates) {
        this.ctcGradeChangeDates = ctcGradeChangeDates;
    }

    public String getEventTypeGuid() {
        return eventTypeGuid;
    }

    public void setEventTypeGuid(String eventTypeId) {
        this.eventTypeGuid = eventTypeId;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patient) {
        this.patientGuid = patient;
    }

    public String getAeText() {
        return aeText;
    }

    public void setAeText(String aeText) {
        this.aeText = aeText;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getSerious() {
        return serious;
    }

    public void setSerious(String serious) {
        this.serious = serious;
    }

    public String getCausality() {
        return causality;
    }

    public void setCausality(String causality) {
        this.causality = causality;
    }

    public String getMaxSeverity() {
        return maxSeverity;
    }

    public void setMaxSeverity(String maxSeverity) {
        this.maxSeverity = maxSeverity;
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

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getPT() {
        return pt == null ? null : pt.toUpperCase();
    }

    public void setPT(String pt) {
        this.pt = pt == null ? null : pt.toUpperCase();
    }

    public String getLLT() {
        return llt;
    }

    public void setLLT(String llt) {
        this.llt = llt;
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

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("startDate", startDate).append("PT", pt).append("HLT", hlt).append("SOC", soc).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("startDate", startDate).append("PT", pt).append("HLT", hlt).append("SOC", soc)
                .append("LLT", llt).append("endDate", endDate).append("serious", serious)
                .append("causality", causality).append("maxSeverity", maxSeverity).append("aeText", aeText)
                .append("comment", comment).append("number", number).append("outcome", outcome)
                .append("doseLimitingToxicity", doseLimitingToxicity).append("timePoint", timePoint)
                .append("immuneMediated", immuneMediated).append("infusionReaction", infusionReaction)
                .append("requiredTreatment", requiredTreatment).append("causedSubjectWithdrawal", causedSubjectWithdrawal)
                .toString();
    }

}
