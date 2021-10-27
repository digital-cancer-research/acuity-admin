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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EG extends TimestampedEntity {

    private String testGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    private String testName;
    private BigDecimal testResult;
    private String resultUnit;

    private String evaluation;
    private String abnormality;
    private String significant;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime dateOfLastDose;
    private String lastDoseAmount;
    private String method;
    private String atrialFibrillation;
    private String sinusRhythm;
    private String reasonNoSinusRhythm;
    private String heartRhythm;
    private String heartRhythmOther;
    private String extraSystoles;
    private String specifyExtraSystoles;
    private String typeOfConduction;
    private String conduction;
    private String reasonAbnormalConduction;
    private String sttChanges;
    private String stSegment;
    private String tWave;

    private String protocolScheduleTimePoint;

    public EG(String subject, String part, LocalDateTime date, String testName, BigDecimal testResult, String resultUnit,
              String evaluation, String abnormality, String significant) {
        this.subject = subject;
        this.part = part;
        this.date = date;
        this.testName = testName;
        this.testResult = testResult;
        this.resultUnit = resultUnit;
        this.evaluation = evaluation;
        this.abnormality = abnormality;
        this.significant = significant;
    }
}
