package com.acuity.visualisations.mapping.entity;

public class AEGroupValueRule extends GroupValueBase {

    private String pt;

    public void setPt(String pt) {
        this.pt = pt;
    }

    @Override
    public void setValues(String[] row) {
        setPt(row[1]);
    }

    @Override
    public String getUniqueField() {
        return pt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AEGroupValueRule)) {
            return false;
        }

        AEGroupValueRule that = (AEGroupValueRule) o;

        return name.equals(that.name) && pt.equals(that.pt);
    }

    @Override
    public int hashCode() {
        int result = pt.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
