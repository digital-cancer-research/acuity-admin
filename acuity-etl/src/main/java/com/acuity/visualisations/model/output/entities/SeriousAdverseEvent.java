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

import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeriousAdverseEvent extends TimestampedEntity {

    private String subject;
    private String part;
    private String patientGuid;

    private String aeText;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime aeBecomeSeriousDate;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime aeFindOutDate;

    private String isResultInDeath;
    private String isLifeThreatening;
    private String isHospitalizationRequired;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime hospitalizationDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime dischargeDate;

    private String isDisability;
    private String isCongenitalAnomaly;
    private String isOtherSeriousEvent;
    private String isCausedByOther;
    private String otherMedication;
    private String isCausedByStudy;
    private String studyProcedure;
    private String aeDescription;
    private Integer aeNum;
    private String primaryCauseOfDeath;
    private String secondaryCauseOfDeath;
    private String additionalDrug;
    private String causedByAdditionalDrug;
    private String additionalDrug1;
    private String causedByAdditionalDrug1;
    private String additionalDrug2;
    private String causedByAdditionalDrug2;
}
