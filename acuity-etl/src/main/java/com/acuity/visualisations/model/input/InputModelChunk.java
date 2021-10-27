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
