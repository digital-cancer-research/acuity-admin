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
