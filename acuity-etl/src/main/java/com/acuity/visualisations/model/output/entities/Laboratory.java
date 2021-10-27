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

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString(callSuper = true)
public class Laboratory extends TimestampedEntity implements SmartEntity {

    public static final String SOURCE_PATIENT = "Patient";

    private String testGuid;
    private String laboratoryGroupGuid;
    private String labCode;
    private BigDecimal laboratoryValue;
    private String laboratoryUnit;
    private BigDecimal refLow;
    private BigDecimal refHigh;
    private String subject;
    private String part;
    private String comment;
    private String valueDipstick;
    private String protocolScheduleTimepoint;

    private String sourceType; // Sponsor or Patient

    // from patient's device
    private String sourceGiud;
    private String sourceDeviceName;
    private String sourceDeviceVersion;
    private String sourceDeviceType;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    @Override
    public void complete() {
        if (sourceType != null) {
            setSourceType(StringUtils.capitalize(sourceType.toLowerCase()));
        }
    }
}
