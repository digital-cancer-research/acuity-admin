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
