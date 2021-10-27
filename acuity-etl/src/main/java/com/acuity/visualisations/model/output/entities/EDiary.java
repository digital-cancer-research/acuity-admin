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

public class EDiary extends TimestampedEntity {

    private String patientGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime assessmentDate;

    private String drugIntakeTime;
    private String deviceType;

    private String morningAssessmentTime;
    private BigDecimal pefMorning;

    private String eveningAssessmentTime;
    private BigDecimal pefEvening;

    private Integer asthmaSymptomScoreNight;
    private Integer asthmaSymptomScoreDay;

    private String wokeDueToAsthma;

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
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

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getDrugIntakeTime() {
        return drugIntakeTime;
    }

    public void setDrugIntakeTime(String drugIntakeTime) {
        this.drugIntakeTime = drugIntakeTime;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMorningAssessmentTime() {
        return morningAssessmentTime;
    }

    public void setMorningAssessmentTime(String morningAssessmentTime) {
        this.morningAssessmentTime = morningAssessmentTime;
    }

    public BigDecimal getPefMorning() {
        return pefMorning;
    }

    public void setPefMorning(BigDecimal pefMorning) {
        this.pefMorning = pefMorning;
    }

    public String getEveningAssessmentTime() {
        return eveningAssessmentTime;
    }

    public void setEveningAssessmentTime(String eveningAssessmentTime) {
        this.eveningAssessmentTime = eveningAssessmentTime;
    }

    public BigDecimal getPefEvening() {
        return pefEvening;
    }

    public void setPefEvening(BigDecimal pefEvening) {
        this.pefEvening = pefEvening;
    }

    public Integer getAsthmaSymptomScoreNight() {
        return asthmaSymptomScoreNight;
    }

    public void setAsthmaSymptomScoreNight(Integer asthmaSymptomScoreNight) {
        this.asthmaSymptomScoreNight = asthmaSymptomScoreNight;
    }

    public Integer getAsthmaSymptomScoreDay() {
        return asthmaSymptomScoreDay;
    }

    public void setAsthmaSymptomScoreDay(Integer asthmaSymptomScoreDay) {
        this.asthmaSymptomScoreDay = asthmaSymptomScoreDay;
    }

    public String getWokeDueToAsthma() {
        return wokeDueToAsthma;
    }

    public void setWokeDueToAsthma(String wokeDueToAsthma) {
        this.wokeDueToAsthma = wokeDueToAsthma;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("patientGuid", patientGuid).append("assessmentDate", assessmentDate)
                .append("drugIntakeTime", drugIntakeTime).append("deviceType", deviceType)
                .append("morningAssessmentTime", morningAssessmentTime)
                .append("eveningAssessmentTime", eveningAssessmentTime).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid).append("subject", subject)
                .append("part", part).append("assessmentDate", assessmentDate)
                .append("drugIntakeTime", drugIntakeTime).append("deviceType", deviceType)
                .append("morningAssessmentTime", morningAssessmentTime).append("pefMorning", pefMorning)
                .append("eveningAssessmentTime", eveningAssessmentTime).append("pefEvening", pefEvening)
                .append("asthmaSymptomScoreNight", asthmaSymptomScoreNight)
                .append("asthmaSymptomScoreDay", asthmaSymptomScoreDay)
                .append("wokeDueToAsthma", wokeDueToAsthma)
                .toString();
    }

}
