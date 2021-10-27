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

package com.acuity.visualisations.web.entity;

public class MappedColumnInfo {
    private long projectId;
    private long fileRuleId;
    private String columnName;
    private String fileRuleFilePath;

    public MappedColumnInfo() {
    }

    public MappedColumnInfo(long projectId, long fileRuleId, String columnName, String fileRuleFilePath) {
        this.projectId = projectId;
        this.fileRuleId = fileRuleId;
        this.columnName = columnName;
        this.fileRuleFilePath = fileRuleFilePath;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getFileRuleId() {
        return fileRuleId;
    }

    public void setFileRuleId(long fileRuleId) {
        this.fileRuleId = fileRuleId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFileRuleFilePath() {
        return fileRuleFilePath;
    }

    public void setFileRuleFilePath(String fileRuleFilePath) {
        this.fileRuleFilePath = fileRuleFilePath;
    }
}
