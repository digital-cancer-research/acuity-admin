package com.acuity.visualisations.batch.holders.configuration;

import java.util.Date;
import java.util.Set;

public interface StudyRuntimeConfigurationHolder {

    String getCurrentStudyConfFolder();

    void setCurrentStudyConfFolder(String currentStudyConfFolder);

    boolean isIgnoreUnparsedFiles();

    void setIgnoreUnparsedFiles(boolean ignoreUnparsedFiles);

    Set<String> getSuppressLogEntities();

    void setSuppressLogEntities(Set<String> suppressLogEntities);

    boolean isEtlExecuted();

    void setEtlExecuted(boolean etlExecuted);

    String getStudiesConfFolder();

    void setStudiesConfFolder(String studiesConfFolder);

    Date getCurrentEtlStartTime();
    void setCurrentEtlStartTime(Date timestamp);
}
