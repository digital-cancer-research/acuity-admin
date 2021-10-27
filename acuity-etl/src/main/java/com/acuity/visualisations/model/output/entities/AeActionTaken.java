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
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class AeActionTaken extends TimestampedEntity {

    private String aeSeverityGuid;
    private String drugGuid;

    private transient AeSeverity aeSeverity;
    private transient Drug drug;

    private String actionTaken;

    public AeActionTaken() {

    }

    public AeActionTaken(AeSeverity aeSeverity, Drug drug, String actionTaken) {
        this.aeSeverity = aeSeverity;
        this.drug = drug;
        this.actionTaken = actionTaken;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeSeverityUniqueFields", aeSeverity.uniqueFieldsToString())
                .append("drugUniqueFields", drug.uniqueFieldsToString())
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeSeverityAllFields", aeSeverity.allFieldsToString())
                .append("drugAllFields", drug.allFieldsToString())
                .append("actionTaken", actionTaken)
                .toString();
    }

    public String getAeSeverityUniqueFields() {
        return aeSeverity.uniqueFieldsToString();
    }

    public String getDrugUniqueFields() {
        return drug.uniqueFieldsToString();
    }

    public String getAeSeverityGuid() {
        return aeSeverityGuid;
    }

    public void setAeSeverityGuid(String aeSeverityGuid) {
        this.aeSeverityGuid = aeSeverityGuid;
    }

    public String getDrugGuid() {
        return drugGuid;
    }

    public void setDrugGuid(String drugGuid) {
        this.drugGuid = drugGuid;
    }

    public AeSeverity getAeSeverity() {
        return aeSeverity;
    }

    public void setAeSeverity(AeSeverity aeSeverity) {
        this.aeSeverity = aeSeverity;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }


    // These getters below required by EntityDescription.xml
    public LocalDateTime getAeSeverityStartDate() {
        return aeSeverity.getStartDate();
    }

    public String getAeUniqueFields() {
        return aeSeverity.getAeUniqueFields();
    }

    public String getDrugName() {
        return drug.getDrugName();
    }
}
