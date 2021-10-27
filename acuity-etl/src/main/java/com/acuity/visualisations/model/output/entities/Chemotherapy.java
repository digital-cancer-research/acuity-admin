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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

@Data
public class Chemotherapy extends TimestampedEntity {
    private String patientGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private BigDecimal visit;
    private String preferredNameOfMed;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime chemoStartDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime chemoEndDate;

    private Integer numberOfCycles;
    private String chemoClass;
    private String treatmentStatus;
    private String bestResponse;
    private String reasonForFailure;
    private String chemoTimeStatus;
    private String concomitantTherapy;
    private Integer numberOfPriorRegiments;
    private String cancerTherapyAgent;
    private String therapyReason;
    private String route;
    private String treatmentContinues;


    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("patientGuid", patientGuid).append("chemoStartDate", chemoStartDate)
                .append("preferredNameOfMed", preferredNameOfMed).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("patientGuid", patientGuid).append("visitDate", visitDate).append("visit", visit)
                .append("preferredNameOfMed", preferredNameOfMed).append("chemoStartDate", chemoStartDate)
                .append("chemoEndDate", chemoEndDate).append("numberOfCycles", numberOfCycles)
                .append("chemoClass", chemoClass).append("treatmentStatus", treatmentStatus)
                .append("bestResponse", bestResponse).append("reasonForFailure", reasonForFailure)
                .append("chemoTimeStatus", bestResponse).append("concomitantTherapy", reasonForFailure)
                .append("numberOfPriorRegiments", bestResponse).append("cancerTherapyAgent", reasonForFailure)
                .append("therapyReason", bestResponse).append("route", reasonForFailure)
                .append("treatmentContinues", bestResponse)
                .toString();
    }
}
