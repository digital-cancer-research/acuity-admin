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


public class FMGene extends TimestampedEntity {

    private String patientGuid;

    private String subject;
    private String part;

    private String sampleId;

    private String disease;

    private Integer medianExonCoverage;

    private String knownVariants;
    private String likelyVariants;

    private String highLevelAmplifications;
    private String lowLevelAmplifications;

    private String deletions;
    private String rearrangements;

    private String comments;

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

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getMedianExonCoverage() {
        return medianExonCoverage;
    }

    public void setMedianExonCoverage(Integer medianExonCoverage) {
        this.medianExonCoverage = medianExonCoverage;
    }

    public String getKnownVariants() {
        return knownVariants;
    }

    public void setKnownVariants(String knownVariants) {
        this.knownVariants = knownVariants;
    }

    public String getLikelyVariants() {
        return likelyVariants;
    }

    public void setLikelyVariants(String likelyVariants) {
        this.likelyVariants = likelyVariants;
    }

    public String getHighLevelAmplifications() {
        return highLevelAmplifications;
    }

    public void setHighLevelAmplifications(String highLevelAmplifications) {
        this.highLevelAmplifications = highLevelAmplifications;
    }

    public String getLowLevelAmplifications() {
        return lowLevelAmplifications;
    }

    public void setLowLevelAmplifications(String lowLevelAmplifications) {
        this.lowLevelAmplifications = lowLevelAmplifications;
    }

    public String getDeletions() {
        return deletions;
    }

    public void setDeletions(String deletions) {
        this.deletions = deletions;
    }

    public String getRearrangements() {
        return rearrangements;
    }

    public void setRearrangements(String rearrangements) {
        this.rearrangements = rearrangements;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
