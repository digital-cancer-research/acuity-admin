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

import com.acuity.visualisations.data.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;


@Getter
@Setter
public class PkConcentration extends TimestampedEntity {
    private String patientGuid;
    private String subject;
    private String part;
    private String spcIdentifier;
    private String analyte;
    private Double analyteConcentration;
    private String analyteConcentrationUnit;
    private BigDecimal lowerLimit;
    private String treatmentCycle;
    private String treatment;
    private String treatmentSchedule;
    private String comment;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("spcIdentifier", spcIdentifier)
                .append("analyte", analyte)
                .append("treatment", treatment)
                .append("treatmentCycle", treatmentCycle)
                .append("treatmentSchedule", treatmentSchedule)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("spcIdentifier", spcIdentifier)
                .append("analyte", analyte)
                .append("treatment", treatment)
                .append("treatmentCycle", treatmentCycle)
                .append("treatmentSchedule", treatmentSchedule)

                .append("analyteConcentration", analyteConcentration)
                .append("analyteConcentrationUnit", analyteConcentrationUnit)
                .append("lowerLimit", lowerLimit)
                .append("comment", comment)
                .toString();
    }

}
