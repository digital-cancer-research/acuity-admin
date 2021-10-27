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

package com.acuity.visualisations.web.dto;


import java.util.Map;

public final class DisclaimerWarningsHolder {
    private Map<String, String> studyDisclaimerMap;
    private static DisclaimerWarningsHolder instance;

    private DisclaimerWarningsHolder() {

    }

    public static DisclaimerWarningsHolder instance() {
        if (instance == null) {
            instance = new DisclaimerWarningsHolder();
        }
        return instance;
    }

    public Map<String, String> getStudyDisclaimerMap() {
        return studyDisclaimerMap;
    }

    public void setStudyDisclaimerMap(Map<String, String> studyDisclaimerMap) {
        this.studyDisclaimerMap = studyDisclaimerMap;
    }

}
