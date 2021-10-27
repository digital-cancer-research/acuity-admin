package com.acuity.visualisations.sdtm.entity;

public class SdtmKey {
    private final String studyId;
    private final String subjectId;

    public SdtmKey(String subjectId) {
        this.subjectId = subjectId;
        this.studyId = null;
    }

    public SdtmKey(String studyId, String subjectId) {
        this.studyId = studyId;
        this.subjectId = subjectId;
    }

    public String getStudyId() {
        return studyId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SdtmKey)) {
            return false;
        }

        SdtmKey sdtmKey = (SdtmKey) o;

        return subjectId.equals(sdtmKey.subjectId);

    }

    @Override
    public int hashCode() {
        return subjectId.hashCode();
    }
}
