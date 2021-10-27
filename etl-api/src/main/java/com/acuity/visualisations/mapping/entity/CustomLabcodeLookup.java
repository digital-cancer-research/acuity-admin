package com.acuity.visualisations.mapping.entity;

/**
 * Created by knml167 on 13/03/14.
 */
public class CustomLabcodeLookup extends MappingEntity {
    private String labcode;
    private Long studyRuleId;
    private String testName;
    private String sampleName;

    public String getLabcode() {
        return labcode;
    }

    public void setLabcode(String labcode) {
        this.labcode = labcode;
    }

    public Long getStudyRuleId() {
        return studyRuleId;
    }

    public void setStudyRuleId(Long studyRuleId) {
        this.studyRuleId = studyRuleId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }
}
