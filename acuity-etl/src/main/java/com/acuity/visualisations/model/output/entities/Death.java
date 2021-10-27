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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Death extends TimestampedEntity {

    private String patientGuid;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime date;

    private String subject;
    private String part;
    private String cause;
    private String designationOfCause;
    private String autopsyPerformed;
    private String relatedToInvestigationDisease;
    private String narrativeCause;
    private String preferredTerm;
    private String llt;
    private String hlt;
    private String soc;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("date", date)
                .append("cause", cause)
                .append("designationOfCause", designationOfCause)
                .append("autopsyPerformed", autopsyPerformed)
                .append("relatedToInvestigationDisease", relatedToInvestigationDisease)
                .append("narrativeCause", narrativeCause)
                .append("preferredTerm", preferredTerm)
                .append("llt", llt)
                .append("hlt", hlt)
                .append("soc", soc)
                .toString();
    }

    /**
     * RCT-4146: Dose data standardisation should happen in one place
     * DISCARD EVENTS WITHOUT TIMINGS
     * Discard all death events where the dateTime is null.
     *
     * @return whether record is valid (date is not empty)
     */
    @Override
    public boolean isValid() {
        return date != null;
    }
}
