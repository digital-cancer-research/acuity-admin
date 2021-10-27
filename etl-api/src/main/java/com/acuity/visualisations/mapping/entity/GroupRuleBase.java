package com.acuity.visualisations.mapping.entity;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupRuleBase extends MappingEntity implements DynamicEntity {

    public enum ProjectGroupType {
        ae, lab, subject
    }

    private String name;
    private String time;
    private Boolean ready;
    private String defaultValue;
    private boolean refreshable;
    private String dataSource;
    private Long parentId;
    private boolean headerRow;
    private List<GroupValueBase> values = new ArrayList<GroupValueBase>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRefreshable() {
        return refreshable;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long projectId) {
        this.parentId = projectId;
    }

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public List<GroupValueBase> getValues() {
        return values;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void clearGroupValues() {
        values = new ArrayList<GroupValueBase>();
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public abstract ProjectGroupType getType();

    public abstract GroupValueBase getValueBaseInstance();
}
