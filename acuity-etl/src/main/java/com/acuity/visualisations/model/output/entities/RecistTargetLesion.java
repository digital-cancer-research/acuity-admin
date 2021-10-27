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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RecistTargetLesion extends TimestampedEntity {
    private String patientGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime lesionDate;

    private String lesionSite;
    private String lesionNumber;
    private String lesionPresent;
    private BigDecimal lesionDiameter;
    private String investigatorsResponse;
    private String lesionNoLongerMeasurable;
    private String methodOfAssessment;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private BigDecimal visitNumber;

    public RecistTargetLesion() {
        super();
    }

    @Override
    public boolean isValid() {
        return subject != null && lesionDate != null;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("lesionDate", lesionDate)
                .append("lesionNumber", lesionNumber)
                .append("lesionNoLongerMeasurable", lesionNoLongerMeasurable)
                .append("methodOfAssessment", methodOfAssessment)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("lesionDate", lesionDate)
                .append("lesionSite", lesionSite)
                .append("lesionNumber", lesionNumber)
                .append("lesionPresent", lesionPresent)
                .append("lesionDiameter", lesionDiameter)
                .append("visitDate", visitDate)
                .append("visitNumber", visitNumber)
                .append("investigatorsResponse", investigatorsResponse)
                .append("lesionNoLongerMeasurable", lesionNoLongerMeasurable)
                .append("methodOfAssessment", methodOfAssessment)
                .toString();
    }
}
