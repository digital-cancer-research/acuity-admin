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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Exacerbation extends TimestampedEntity implements SmartEntity {
    private static final Map<String, String> SEVERITY_MAP = new HashMap<>();
    private static final String SEVERE_CONST = "(C) Severe";

    /**
     * RCT-4066, this map generated directly from the "SeverityRules.xlsx"
     */
    static {
        SEVERITY_MAP.put("000000", "(A) Mild");
        SEVERITY_MAP.put("001000", "(B) Moderate");
        SEVERITY_MAP.put("000100", "(B) Moderate");
        SEVERITY_MAP.put("111111", SEVERE_CONST);
        SEVERITY_MAP.put("111110", SEVERE_CONST);
        SEVERITY_MAP.put("111101", SEVERE_CONST);
        SEVERITY_MAP.put("111100", SEVERE_CONST);
        SEVERITY_MAP.put("111011", SEVERE_CONST);
        SEVERITY_MAP.put("111010", SEVERE_CONST);
        SEVERITY_MAP.put("111001", SEVERE_CONST);
        SEVERITY_MAP.put("111000", SEVERE_CONST);
        SEVERITY_MAP.put("110111", SEVERE_CONST);
        SEVERITY_MAP.put("110110", SEVERE_CONST);
        SEVERITY_MAP.put("110101", SEVERE_CONST);
        SEVERITY_MAP.put("110100", SEVERE_CONST);
        SEVERITY_MAP.put("110011", SEVERE_CONST);
        SEVERITY_MAP.put("110010", SEVERE_CONST);
        SEVERITY_MAP.put("110001", SEVERE_CONST);
        SEVERITY_MAP.put("110000", SEVERE_CONST);
        SEVERITY_MAP.put("101111", SEVERE_CONST);
        SEVERITY_MAP.put("101110", SEVERE_CONST);
        SEVERITY_MAP.put("101101", SEVERE_CONST);
        SEVERITY_MAP.put("101100", SEVERE_CONST);
        SEVERITY_MAP.put("101011", SEVERE_CONST);
        SEVERITY_MAP.put("101010", SEVERE_CONST);
        SEVERITY_MAP.put("101001", SEVERE_CONST);
        SEVERITY_MAP.put("101000", SEVERE_CONST);
        SEVERITY_MAP.put("100111", SEVERE_CONST);
        SEVERITY_MAP.put("100110", SEVERE_CONST);
        SEVERITY_MAP.put("100101", SEVERE_CONST);
        SEVERITY_MAP.put("100100", SEVERE_CONST);
        SEVERITY_MAP.put("100011", SEVERE_CONST);
        SEVERITY_MAP.put("100010", SEVERE_CONST);
        SEVERITY_MAP.put("100001", SEVERE_CONST);
        SEVERITY_MAP.put("100000", SEVERE_CONST);
        SEVERITY_MAP.put("011111", SEVERE_CONST);
        SEVERITY_MAP.put("011110", SEVERE_CONST);
        SEVERITY_MAP.put("011101", SEVERE_CONST);
        SEVERITY_MAP.put("011100", SEVERE_CONST);
        SEVERITY_MAP.put("011011", SEVERE_CONST);
        SEVERITY_MAP.put("011010", SEVERE_CONST);
        SEVERITY_MAP.put("011001", SEVERE_CONST);
        SEVERITY_MAP.put("011000", SEVERE_CONST);
        SEVERITY_MAP.put("010111", SEVERE_CONST);
        SEVERITY_MAP.put("010110", SEVERE_CONST);
        SEVERITY_MAP.put("010101", SEVERE_CONST);
        SEVERITY_MAP.put("010100", SEVERE_CONST);
        SEVERITY_MAP.put("010011", SEVERE_CONST);
        SEVERITY_MAP.put("010010", SEVERE_CONST);
        SEVERITY_MAP.put("010001", SEVERE_CONST);
        SEVERITY_MAP.put("010000", SEVERE_CONST);
        SEVERITY_MAP.put("001111", SEVERE_CONST);
        SEVERITY_MAP.put("001110", SEVERE_CONST);
        SEVERITY_MAP.put("001101", SEVERE_CONST);
        SEVERITY_MAP.put("001100", SEVERE_CONST);
        SEVERITY_MAP.put("001011", SEVERE_CONST);
        SEVERITY_MAP.put("001010", SEVERE_CONST);
        SEVERITY_MAP.put("001001", SEVERE_CONST);
        SEVERITY_MAP.put("000111", SEVERE_CONST);
        SEVERITY_MAP.put("000110", SEVERE_CONST);
        SEVERITY_MAP.put("000101", SEVERE_CONST);
        SEVERITY_MAP.put("000011", SEVERE_CONST);
        SEVERITY_MAP.put("000010", SEVERE_CONST);
        SEVERITY_MAP.put("000001", SEVERE_CONST);
    }

    protected String part;
    protected String subject;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime exacStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime exacEndDate;
    protected String ltpca;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime ltpcaStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime ltpcaEndDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    protected LocalDateTime ediaryAlertDate;
    protected String depotGcs;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime depotGcsStartDate;
    protected String syscortTrt;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime syscortTrtStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime syscortTrtEndDate;
    protected String antibioticsTrt;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime antibioticsTrtStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime antibioticsTrtEndDate;
    protected String emerTrt;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    protected LocalDateTime emerTrtDate;
    protected String hospit;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime hospitStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime hospitEndDate;
    protected String icsTrt;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    protected LocalDateTime icsStartDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    protected LocalDateTime icsEndDate;
    protected String severity;
    private String patientGuid;

    @Override
    public void complete() {
        if (severity == null) {
            String severityKey = ("yes".equalsIgnoreCase(depotGcs) ? "1" : "0")
                    + ("yes".equalsIgnoreCase(syscortTrt) ? "1" : "0")
                    + ("yes".equalsIgnoreCase(icsTrt) ? "1" : "0")
                    + ("yes".equalsIgnoreCase(antibioticsTrt) ? "1" : "0")
                    + ("yes".equalsIgnoreCase(hospit) ? "1" : "0")
                    + ("yes".equalsIgnoreCase(emerTrt) ? "1" : "0");
            severity = SEVERITY_MAP.get(severityKey);
        }
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public LocalDateTime getExacStartDate() {
        return exacStartDate;
    }

    public void setExacStartDate(LocalDateTime exacStartDate) {
        this.exacStartDate = exacStartDate;
    }

    public LocalDateTime getExacEndDate() {
        return exacEndDate;
    }

    public void setExacEndDate(LocalDateTime exacEndDate) {
        this.exacEndDate = exacEndDate;
    }

    public String getLtpca() {
        return ltpca;
    }

    public void setLtpca(String ltpca) {
        this.ltpca = ltpca;
    }

    public LocalDateTime getLtpcaStartDate() {
        return ltpcaStartDate;
    }

    public void setLtpcaStartDate(LocalDateTime ltpcaStartDate) {
        this.ltpcaStartDate = ltpcaStartDate;
    }

    public LocalDateTime getLtpcaEndDate() {
        return ltpcaEndDate;
    }

    public void setLtpcaEndDate(LocalDateTime ltpcaEndDate) {
        this.ltpcaEndDate = ltpcaEndDate;
    }

    public LocalDateTime getEdiaryAlertDate() {
        return ediaryAlertDate;
    }

    public void setEdiaryAlertDate(LocalDateTime ediaryAlertDate) {
        this.ediaryAlertDate = ediaryAlertDate;
    }

    public String getDepotGcs() {
        return depotGcs;
    }

    public void setDepotGcs(String depotGcs) {
        this.depotGcs = depotGcs;
    }

    public LocalDateTime getDepotGcsStartDate() {
        return depotGcsStartDate;
    }

    public void setDepotGcsStartDate(LocalDateTime depotGcsStartDate) {
        this.depotGcsStartDate = depotGcsStartDate;
    }

    public String getSyscortTrt() {
        return syscortTrt;
    }

    public void setSyscortTrt(String syscortTrt) {
        this.syscortTrt = syscortTrt;
    }

    public LocalDateTime getSyscortTrtStartDate() {
        return syscortTrtStartDate;
    }

    public void setSyscortTrtStartDate(LocalDateTime syscortTrtStartDate) {
        this.syscortTrtStartDate = syscortTrtStartDate;
    }

    public LocalDateTime getSyscortTrtEndDate() {
        return syscortTrtEndDate;
    }

    public void setSyscortTrtEndDate(LocalDateTime syscortTrtEndDate) {
        this.syscortTrtEndDate = syscortTrtEndDate;
    }

    public String getAntibioticsTrt() {
        return antibioticsTrt;
    }

    public void setAntibioticsTrt(String antibioticsTrt) {
        this.antibioticsTrt = antibioticsTrt;
    }

    public LocalDateTime getAntibioticsTrtStartDate() {
        return antibioticsTrtStartDate;
    }

    public void setAntibioticsTrtStartDate(LocalDateTime antibioticsTrtStartDate) {
        this.antibioticsTrtStartDate = antibioticsTrtStartDate;
    }

    public LocalDateTime getAntibioticsTrtEndDate() {
        return antibioticsTrtEndDate;
    }

    public void setAntibioticsTrtEndDate(LocalDateTime antibioticsTrtEndDate) {
        this.antibioticsTrtEndDate = antibioticsTrtEndDate;
    }

    public String getEmerTrt() {
        return emerTrt;
    }

    public void setEmerTrt(String emerTrt) {
        this.emerTrt = emerTrt;
    }

    public LocalDateTime getEmerTrtDate() {
        return emerTrtDate;
    }

    public void setEmerTrtDate(LocalDateTime emerTrtDate) {
        this.emerTrtDate = emerTrtDate;
    }

    public String getHospit() {
        return hospit;
    }

    public void setHospit(String hospit) {
        this.hospit = hospit;
    }

    public LocalDateTime getHospitStartDate() {
        return hospitStartDate;
    }

    public void setHospitStartDate(LocalDateTime hospitStartDate) {
        this.hospitStartDate = hospitStartDate;
    }

    public LocalDateTime getHospitEndDate() {
        return hospitEndDate;
    }

    public void setHospitEndDate(LocalDateTime hospitEndDate) {
        this.hospitEndDate = hospitEndDate;
    }

    public String getIcsTrt() {
        return icsTrt;
    }

    public void setIcsTrt(String icsTrt) {
        this.icsTrt = icsTrt;
    }

    public LocalDateTime getIcsStartDate() {
        return icsStartDate;
    }

    public void setIcsStartDate(LocalDateTime icsStartDate) {
        this.icsStartDate = icsStartDate;
    }

    public LocalDateTime getIcsEndDate() {
        return icsEndDate;
    }

    public void setIcsEndDate(LocalDateTime icsEndDate) {
        this.icsEndDate = icsEndDate;
    }
}
