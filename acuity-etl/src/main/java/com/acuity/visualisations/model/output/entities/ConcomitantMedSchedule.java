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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConcomitantMedSchedule extends TimestampedEntity {

    private String patientGuid;
    private String medicineGuid;
    private BigDecimal dose;
    private String doseUnit;
    private String frequency;
    private String atcCode;
    private String atcCodeText;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;

    private String treatmentReason; // indication on ui
    private String subject;
    private String part;
    private String drugName;
    private String drugParent;

    private BigDecimal doseTotal;
    private String doseUnitOther;
    private String frequencyOther;
    private String route;
    private String therapyReason;
    private String therapyReasonOther;
    private String prophylaxisSpecOther;
    private Integer aeNumber;

    private String infBodySys;
    private String infBodySysOther;
    private String reasonStop;
    private String reasonStopOther;
    private String activeIngredient1;
    private String activeIngredient2;

    private transient Object[] aeNum;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (ConcomitantMedSchedule) super.clone();
    }
}
