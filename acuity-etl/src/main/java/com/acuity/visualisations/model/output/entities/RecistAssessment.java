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
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecistAssessment extends TimestampedEntity {

    private String patientGuid;

    private String subject;
    private String part;

    private String newLesionsSinceBaseline;
    private String newLesionSite;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime newLesionDate;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private BigDecimal visit;

    private String overallRecistResponse;
    private String invAgreeWithRecistResponse;
    private String invOpinion;
    private String reasonAssessmentsDiffer;

    private Integer assessmentFrequency;

    public Integer getAssessmentFrequency() {
        return assessmentFrequency;
    }

    public void setAssessmentFrequency(Integer assessmentFrequency) {
        this.assessmentFrequency = assessmentFrequency;
    }

    public RecistAssessment() {
        initId();
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getNewLesionsSinceBaseline() {
        return newLesionsSinceBaseline;
    }

    public void setNewLesionsSinceBaseline(String newLesionsSinceBaseline) {
        this.newLesionsSinceBaseline = newLesionsSinceBaseline;
    }

    public String getNewLesionSite() {
        return newLesionSite;
    }

    public void setNewLesionSite(String newLesionSite) {
        this.newLesionSite = newLesionSite;
    }

    public LocalDateTime getNewLesionDate() {
        return newLesionDate;
    }

    public void setNewLesionDate(LocalDateTime newLesionDate) {
        this.newLesionDate = newLesionDate;
    }

    public String getOverallRecistResponse() {
        return overallRecistResponse;
    }

    public void setOverallRecistResponse(String overallRecistResponse) {
        this.overallRecistResponse = overallRecistResponse;
    }

    public String getInvAgreeWithRecistResponse() {
        return invAgreeWithRecistResponse;
    }

    public void setInvAgreeWithRecistResponse(String invAgreeWithRecistResponse) {
        this.invAgreeWithRecistResponse = invAgreeWithRecistResponse;
    }

    public String getInvOpinion() {
        return invOpinion;
    }

    public void setInvOpinion(String invOpinion) {
        this.invOpinion = invOpinion;
    }

    public String getReasonAssessmentsDiffer() {
        return reasonAssessmentsDiffer;
    }

    public void setReasonAssessmentsDiffer(String reasonAssessmentsDiffer) {
        this.reasonAssessmentsDiffer = reasonAssessmentsDiffer;
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

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("visitDate", visitDate).append("visit", visit).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("newLesionDate", newLesionDate).append("newLesionSite", newLesionSite)
                .append("newLesionsSinceBaseline", newLesionsSinceBaseline).append("overallRecistResponse", overallRecistResponse)
                .append("invAgreeWithRecistResponse", invAgreeWithRecistResponse).append("invOpinion", invOpinion)
                .append("reasonAssessmentsDiffer", reasonAssessmentsDiffer)
                .append("assessmentFrequency", assessmentFrequency)
                .append("visit", visit).append("visitDate", visitDate).toString();
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public BigDecimal getVisit() {
        return visit;
    }

    public void setVisit(BigDecimal visit) {
        this.visit = visit;
    }

}
