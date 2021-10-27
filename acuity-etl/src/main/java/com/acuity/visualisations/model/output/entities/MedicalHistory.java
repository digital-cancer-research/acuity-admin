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

import java.time.LocalDateTime;
import java.util.Objects;

public class MedicalHistory extends TimestampedEntity {
    protected String part;
    protected String subject;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime startDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime endDate;
    private String patientGuid;
    private String category;
    private String term;
    private String conditionStatus;
    private String lltName;
    private String ptName;
    private String hltName;
    private String socName;
    private String currentMedication;

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getLltName() {
        return lltName;
    }

    public void setLltName(String lltName) {
        this.lltName = lltName;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public String getHltName() {
        return hltName;
    }

    public void setHltName(String hltName) {
        this.hltName = hltName;
    }

    public String getSocName() {
        return socName;
    }

    public void setSocName(String socName) {
        this.socName = socName;
    }

    public String getCurrentMedication() {
        return currentMedication;
    }

    public void setCurrentMedication(String currentMedication) {
        this.currentMedication = currentMedication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MedicalHistory that = (MedicalHistory) o;
        return Objects.equals(part, that.part)
                && Objects.equals(subject, that.subject)
                && Objects.equals(patientGuid, that.patientGuid)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(term, that.term)
                && Objects.equals(category, that.category)
                && Objects.equals(conditionStatus, that.conditionStatus)
                && Objects.equals(lltName, that.lltName)
                && Objects.equals(ptName, that.ptName)
                && Objects.equals(hltName, that.hltName)
                && Objects.equals(socName, that.socName)
                && Objects.equals(currentMedication, that.currentMedication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), part,
                subject,
                patientGuid,
                startDate,
                endDate,
                term,
                category,
                conditionStatus,
                lltName,
                ptName,
                hltName,
                socName,
                currentMedication);
    }
}
