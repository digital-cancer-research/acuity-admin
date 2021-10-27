/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
