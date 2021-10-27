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
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CIEvent extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    private String eventTerm;
    private Integer aeNumber;
    private String ischemicSymptoms;
    private String cieSymptomsDuration;
    private String symptCausedUnscheduledHosp;
    private String symptCausedStentThromb;
    private String prevEcgBeforeAvailableEvent;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime prevEcgDate;
    private String availableEcgAtEventTime;
    private String noEcgAtEventTime;
    private String localCardiacBiomarkersDrawn;
    private String coronaryAngiographyPerformed;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime angiographyDate;
    private String finalDiagnosis;
    private String otherDiagnosis;
    private String description1;
    private String description2;
    private String description3;
    private String description4;
    private String description5;
}
