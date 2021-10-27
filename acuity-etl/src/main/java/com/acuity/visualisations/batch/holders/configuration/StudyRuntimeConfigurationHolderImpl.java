package com.acuity.visualisations.batch.holders.configuration;

import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
public class StudyRuntimeConfigurationHolderImpl implements StudyRuntimeConfigurationHolder {

    private String studiesConfFolder;
    private String currentStudyConfFolder;
    private boolean ignoreUnparsedFiles;
    private Set<String> suppressLogEntities;
    private Date currentEtlStartTime;
    private boolean etlExecuted;

    @Override
    public boolean isEtlExecuted() {
        return etlExecuted;
    }

    @Override
    public void setEtlExecuted(boolean etlExecuted) {
        this.etlExecuted = etlExecuted;
    }

    public String getStudiesConfFolder() {
        return studiesConfFolder;
    }

    public void setStudiesConfFolder(String studiesConfFolder) {
        this.studiesConfFolder = studiesConfFolder;
    }

    public String getCurrentStudyConfFolder() {
        return currentStudyConfFolder;
    }

    public void setCurrentStudyConfFolder(String currentStudyConfFolder) {
        this.currentStudyConfFolder = currentStudyConfFolder;
    }

    public boolean isIgnoreUnparsedFiles() {
        return ignoreUnparsedFiles;
    }

    public void setIgnoreUnparsedFiles(boolean ignoreUnparsedFiles) {
        this.ignoreUnparsedFiles = ignoreUnparsedFiles;
    }

    public Set<String> getSuppressLogEntities() {
        return suppressLogEntities;
    }

    public void setSuppressLogEntities(Set<String> suppressLogEntities) {
        this.suppressLogEntities = suppressLogEntities;
    }

    public Date getCurrentEtlStartTime() {
        return currentEtlStartTime;
    }

    @Override
    public void setCurrentEtlStartTime(Date timestamp) {
        currentEtlStartTime = timestamp;
    }
}
