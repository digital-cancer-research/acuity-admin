package com.acuity.visualisations.mapping.entity;

/**
 * Keep Study-Drug relation for baselines calculation
 */
public class StudyBaselineDrug {
    private Long id;

    private String drugName;

    /**
     * If true then include this drug in the calculation of baseline values.
     */
    private boolean include;

    public StudyBaselineDrug() {
    }

    public StudyBaselineDrug(String drugName, boolean include) {
        this.drugName = drugName;
        this.include = include;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }
}
