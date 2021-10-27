package com.acuity.visualisations.model.input;

import java.util.Map;
import java.util.Set;

public interface InputModelChunk {

    void setColumnsToSkip(Set<String> columnsToSkip);

    Set<String> getColumnsToSkip();

    String getProjectName();

    void setProjectName(String projectName);

    String getStudyName();

    void setStudyName(String studyName);

    String getSourceName();

    void setSourceName(String sourceName);

    Map<Integer, String[]> getRecords();

    void addRecord(int rowNumber, String[] record);

}
