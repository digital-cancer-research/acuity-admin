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
