package com.acuity.visualisations.mapping.entity;

public abstract class GroupValueBase extends MappingEntity implements DynamicEntity {

    protected String name;
    protected Long groupId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void setValues(String[] row);

    public abstract String getUniqueField();

    public String getNameAndUniqueField() {
        return name + "#" + getUniqueField();
    }
}
