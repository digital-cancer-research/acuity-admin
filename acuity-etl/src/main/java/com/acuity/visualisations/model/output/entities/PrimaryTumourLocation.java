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

public class PrimaryTumourLocation extends TimestampedEntity {
    private String subject;
    private String part;
    private String primaryTumLocation;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime originalDiagnosisDate;

    private String primaryTumLocationComment;
    private String patientGuid;

    public PrimaryTumourLocation() {
        initId();
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

    public String getPrimaryTumLocation() {
        return primaryTumLocation;
    }

    public void setPrimaryTumLocation(String primaryTumLocation) {
        this.primaryTumLocation = primaryTumLocation;
    }

    public LocalDateTime getOriginalDiagnosisDate() {
        return originalDiagnosisDate;
    }

    public void setOriginalDiagnosisDate(LocalDateTime originalDiagnosisDate) {
        this.originalDiagnosisDate = originalDiagnosisDate;
    }

    public String getPrimaryTumLocationComment() {
        return primaryTumLocationComment;
    }

    public void setPrimaryTumComment(String primaryTumLocationComment) {
        this.primaryTumLocationComment = primaryTumLocationComment;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("primaryTumLocation", primaryTumLocation).append("originalDiagnosisDate", primaryTumLocation)
                .append("primaryTumLocationComment", primaryTumLocationComment).toString();
    }
}
