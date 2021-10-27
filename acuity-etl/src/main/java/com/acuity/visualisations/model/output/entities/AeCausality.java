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

public class AeCausality extends TimestampedEntity {

    private String aeGuid;
    private String drugGuid;

    private String causality;

    private transient AE ae;
    private transient Drug drug;

    public AeCausality() {

    }

    public AeCausality(AE ae, Drug drug, String causality) {
        this.ae = ae;
        this.drug = drug;
        this.causality = causality;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeUniqueFields", ae.uniqueFieldsToString())
                .append("drugUniqueFields", drug.uniqueFieldsToString())
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeAllFields", ae.allFieldsToString())
                .append("drugAllFields", drug.allFieldsToString())
                .append("causality", causality)
                .toString();
    }

    public String getAeUniqueFields() {
        return ae.uniqueFieldsToString();
    }

    public String getDrugUniqueFields() {
        return drug.uniqueFieldsToString();
    }

    public String getAeGuid() {
        return aeGuid;
    }

    public void setAeGuid(String aeGuid) {
        this.aeGuid = aeGuid;
    }

    public String getDrugGuid() {
        return drugGuid;
    }

    public void setDrugGuid(String drugGuid) {
        this.drugGuid = drugGuid;
    }

    public String getCausality() {
        return causality;
    }

    public void setCausality(String causality) {
        this.causality = causality;
    }

    public AE getAe() {
        return ae;
    }

    public void setAe(AE ae) {
        this.ae = ae;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    // These getters below required by EntityDescription.xml
    public String getAePT() {
        return ae.getPT();
    }

    public String getAeHLT() {
        return ae.getHLT();
    }

    public String getAeSOC() {
        return ae.getSOC();
    }

    public String getAeSubject() {
        return ae.getSubject();
    }

    public LocalDateTime getAeStartDate() {
        return ae.getStartDate();
    }

    public String getDrugName() {
        return drug.getDrugName();
    }
}
