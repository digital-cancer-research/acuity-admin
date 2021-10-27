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

/**
 * Created by knml167 on 21/07/2014.
 */

public class SpecimenCollection extends TimestampedEntity {
    private String patientGuid;
    private String subject;
    private String part;
    private String spcIdentifier;
    private BigDecimal visitNumber;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime spcDate;

    private String spcCategory;
    private String protocolSchedule;
    private Integer protocolScheduleDay;
    private BigDecimal protocolScheduleHour;
    private Integer protocolScheduleMinute;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime drugAdmDate;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("patientGuid", patientGuid).append("spcDate", spcDate).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("patientGuid", patientGuid).append("visitNumber", visitNumber)
                .append("visitDate", visitDate).append("spcDate", spcDate).append("spcCategory", spcCategory)
                .append("protocolSchedule", protocolSchedule).append("protocolScheduleDay", protocolScheduleDay)
                .append("protocolScheduleHour", protocolScheduleHour).append("protocolScheduleMinute", protocolScheduleMinute)
                .append("drugAdmDate", drugAdmDate).toString();
    }

    public String getSpcIdentifier() {
        return spcIdentifier;
    }

    public void setSpcIdentifier(String spcIdentifier) {
        this.spcIdentifier = spcIdentifier;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public BigDecimal getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(BigDecimal visitNumber) {
        this.visitNumber = visitNumber;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public LocalDateTime getSpcDate() {
        return spcDate;
    }

    public void setSpcDate(LocalDateTime spcDate) {
        this.spcDate = spcDate;
    }

    public String getSpcCategory() {
        return spcCategory;
    }

    public void setSpcCategory(String spcCategory) {
        this.spcCategory = spcCategory;
    }

    public String getProtocolSchedule() {
        return protocolSchedule;
    }

    public void setProtocolSchedule(String protocolSchedule) {
        this.protocolSchedule = protocolSchedule;
    }

    public Integer getProtocolScheduleDay() {
        return protocolScheduleDay;
    }

    public void setProtocolScheduleDay(Integer protocolScheduleDay) {
        this.protocolScheduleDay = protocolScheduleDay;
    }

    public BigDecimal getProtocolScheduleHour() {
        return protocolScheduleHour;
    }

    public void setProtocolScheduleHour(BigDecimal protocolScheduleHour) {
        this.protocolScheduleHour = protocolScheduleHour;
    }

    public Integer getProtocolScheduleMinute() {
        return protocolScheduleMinute;
    }

    public void setProtocolScheduleMinute(Integer protocolScheduleMinute) {
        this.protocolScheduleMinute = protocolScheduleMinute;
    }

    public LocalDateTime getDrugAdmDate() {
        return drugAdmDate;
    }

    public void setDrugAdmDate(LocalDateTime drugAdmDate) {
        this.drugAdmDate = drugAdmDate;
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
}
