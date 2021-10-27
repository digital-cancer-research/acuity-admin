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

package com.acuity.visualisations.dal.dao;

import java.util.List;
import java.util.Map;

public interface IStudyDaoExternal {

    void delete(String study);

    String getStudyGuid(String studyName, String projectGuid);

    Map<String, Map<String, Integer>> getStudyCount(Map<String, List<String>> studies);

    boolean studyExists(String studyCode);

    Map<String, Map<String, Boolean>> getStudyScheduledCleanFlag(Map<String, List<String>> studies);

    Map<String, Map<String, Boolean>> getStudyAmlEnabledFlag();

    void setScheduleCleanFlag(String study);

    void setScheduleCleanFlag(String study, Boolean flag);

    int resetStudyEtlStatus(String studyCode);
}
