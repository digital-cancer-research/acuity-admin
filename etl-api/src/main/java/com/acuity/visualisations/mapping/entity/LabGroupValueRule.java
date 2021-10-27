package com.acuity.visualisations.mapping.entity;

public class LabGroupValueRule extends GroupValueBase {

    protected String labCode;
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    @Override
    public void setValues(String[] row) {
        setLabCode(row[1]);
        setDescription(row[2]);
    }

    @Override
    public String getUniqueField() {
        return labCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabGroupValueRule)) {
            return false;
        }

        LabGroupValueRule that = (LabGroupValueRule) o;

        return name.equals(that.name) && labCode.equals(that.labCode);
    }

    @Override
    public int hashCode() {
        int result = labCode.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
