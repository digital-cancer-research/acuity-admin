package com.acuity.visualisations.mapping;

public class StudyType {
    private Boolean blinded;
    private Boolean randomised;
    private Boolean regulatory;

    public StudyType(Boolean blinded, Boolean randomised, Boolean regulatory) {
        this.blinded = blinded;
        this.randomised = randomised;
        this.regulatory = regulatory;
    }
    public boolean hasNotNull() {
        return blinded != null || randomised != null || regulatory != null;
    }

    public Boolean getBlinded() {
        return blinded;
    }

    public void setBlinded(Boolean blinded) {
        this.blinded = blinded;
    }

    public Boolean getRandomised() {
        return randomised;
    }

    public void setRandomised(Boolean randomised) {
        this.randomised = randomised;
    }

    public Boolean getRegulatory() {
        return regulatory;
    }

    public void setRegulatory(Boolean regulatory) {
        this.regulatory = regulatory;
    }
}
