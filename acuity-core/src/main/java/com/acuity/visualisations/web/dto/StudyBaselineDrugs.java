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


import com.acuity.visualisations.mapping.entity.StudyBaselineDrug;

import java.util.List;

public class StudyBaselineDrugs {
    private boolean useCustomDrugsForBaseline;
    private List<StudyBaselineDrug> customBaselineDrugs;

    public boolean isUseCustomDrugsForBaseline() {
        return useCustomDrugsForBaseline;
    }

    public void setUseCustomDrugsForBaseline(boolean useCustomDrugsForBaseline) {
        this.useCustomDrugsForBaseline = useCustomDrugsForBaseline;
    }

    public List<StudyBaselineDrug> getCustomBaselineDrugs() {
        return customBaselineDrugs;
    }

    public void setCustomBaselineDrugs(List<StudyBaselineDrug> customBaselineDrugs) {
        this.customBaselineDrugs = customBaselineDrugs;
    }
}
