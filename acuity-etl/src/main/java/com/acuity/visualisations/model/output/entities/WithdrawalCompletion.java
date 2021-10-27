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
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class WithdrawalCompletion extends TimestampedEntity {
    private String patientGuid;
    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime withdrawalCompletionDate;

    private String prematurelyWithdrawn;
    private String mainReason;
    private String specification;

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public LocalDateTime getWithdrawalCompletionDate() {
        return withdrawalCompletionDate;
    }

    public void setWithdrawalCompletionDate(LocalDateTime withdrawalCompletionDate) {
        this.withdrawalCompletionDate = withdrawalCompletionDate;
    }

    public String getPrematurelyWithdrawn() {
        return prematurelyWithdrawn;
    }

    public void setPrematurelyWithdrawn(String prematurelyWithdrawn) {
        this.prematurelyWithdrawn = prematurelyWithdrawn;
    }

    public String getMainReason() {
        return mainReason;
    }

    public void setMainReason(String mainReason) {
        this.mainReason = mainReason;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("prematurelyWithdrawn", prematurelyWithdrawn)
                .append("mainReason", mainReason)
                .append("specification", specification)
                .append("withdrawalCompletionDate", withdrawalCompletionDate)
                .toString();
    }

    /**
     * RCT-4146: Dose data standardisation should happen in one place
     * DISCARD EVENTS WITHOUT TIMINGS
     * Discard all withdrawal/completion events where the dateTime is null.
     * @return
     */
    @Override
    public boolean isValid() {
        return withdrawalCompletionDate != null;
    }
}
