package com.acuity.visualisations.mapping.entity;

/**
 * Created by knml167 on 06/05/2016.
 */
public class ChartTimeInterval {
    private Integer id;
    private String name;
    private Boolean checked;

    public ChartTimeInterval(Integer id, String name, Boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }

    public ChartTimeInterval() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChartTimeInterval that = (ChartTimeInterval) o;

        return (id != null ? id.equals(that.id) : that.id == null)
                && !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
