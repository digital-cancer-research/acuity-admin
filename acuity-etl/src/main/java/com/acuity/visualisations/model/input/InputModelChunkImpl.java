package com.acuity.visualisations.model.input;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class InputModelChunkImpl implements InputModelChunk {

    private Map<Integer, String[]> records = new HashMap<Integer, String[]>();
    private String projectName;
    private String studyName;
    private String sourceName;
    private Set<String> columnsToSkip;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Map<Integer, String[]> getRecords() {
        return records;
    }

    public void addRecord(int rowNumber, String[] record) {
        records.put(rowNumber, record);
    }

    public Set<String> getColumnsToSkip() {
        return columnsToSkip;
    }

    public void setColumnsToSkip(Set<String> columnsToSkip) {
        this.columnsToSkip = columnsToSkip;
    }

}
