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

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString(callSuper = true)
public class PatientData extends TimestampedEntity implements SmartEntity {
    private String subject;
    private String part;
    private String measurementName;
    private BigDecimal value;
    private String unit;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime measurementDate;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime reportDate;
    private String comment;
    private String sourceType;
    private String patientGuid;

    // from patient's device
    private String sourceGiud;
    private String sourceDeviceName;
    private String sourceDeviceVersion;
    private String sourceDeviceType;


    @Override
    public void complete() {
        if (sourceType != null) {
            setSourceType(StringUtils.capitalize(sourceType.toLowerCase()));
        }
    }
}
