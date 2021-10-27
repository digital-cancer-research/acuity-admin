package com.acuity.visualisations.mapping.entity;

public class SubjectGroupValueRule extends GroupValueBase {
    private String subjectId;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public void setValues(String[] row) {
        setSubjectId(row[1]);
    }

    @Override
    public String getUniqueField() {
        return subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubjectGroupValueRule)) {
            return false;
        }

        SubjectGroupValueRule that = (SubjectGroupValueRule) o;

        return name.equals(that.name) && subjectId.equals(that.subjectId);
    }

    @Override
    public int hashCode() {
        int result = subjectId.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
