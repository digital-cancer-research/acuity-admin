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

import java.time.LocalDateTime;

@Getter
@Setter
public class MedDosDisc extends TimestampedEntity {

    private String patientGuid;
    private Integer ipDisc;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime ipdcDate;

    private String ipdcReas;
    private String ipdcSpec;
    private String drugName;
    private String subject;
    private String part;
    private String subjectDecisionSpec;
    private String subjectDecisionSpecOther;

    public MedDosDisc() {
        initId();
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("drugName", drugName).
                append("ipdcDate", ipdcDate).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("drugName", drugName).
                append("ipdcDate", ipdcDate).
                append("ipdcReas", ipdcReas).
                append("ipdcSpec", ipdcSpec).
                append("ipDisc", ipDisc).
                append("subjectDecisionSpec", subjectDecisionSpec).
                append("subjectDecisionSpecOther", subjectDecisionSpecOther).
                toString();
    }

    /**
     * RCT-4146: Dose data standardisation should happen in one place
     * DISCARD EVENTS WITHOUT TIMINGS
     * Discard all discontinuation events where the dateTime is null.
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return ipdcDate != null;
    }
}
