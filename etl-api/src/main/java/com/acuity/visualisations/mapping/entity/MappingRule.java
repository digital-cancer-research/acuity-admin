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

public class MappingRule extends MappingEntity implements DynamicEntity {

    private FieldDescription description;

    private String fmtName;
    private String value;
    private List<ColumnRule> columnRules = new ArrayList<ColumnRule>();
    private AggregationFunction aggregationFunction = new AggregationFunction();
    private FileRule fileRule;

    private Long fileRuleId;

    private List<FieldRule> fieldRules = new ArrayList<FieldRule>();
    private Long aggregationFunctionId;
    private String helpText;

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    public FieldDescription getDescription() {
        return description;
    }

    public void setDescription(FieldDescription description) {
        this.description = description;
    }

    public String getFmtName() {
        return fmtName;
    }

    public void setFmtName(String fmtName) {
        this.fmtName = fmtName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ColumnRule> getColumnRules() {
        return columnRules;
    }

    public void setColumnRules(List<ColumnRule> columnRules) {
        this.columnRules = columnRules;
    }

    public AggregationFunction getAggregationFunction() {
        return aggregationFunction;
    }

    public void setAggregationFunction(AggregationFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    public Long getFileRuleId() {
        return fileRuleId;
    }

    public void setFileRuleId(Long fileRuleId) {
        this.fileRuleId = fileRuleId;
    }

    public FileRule getFileRule() {
        return fileRule;
    }

    public void setFileRule(FileRule fileRule) {
        this.fileRule = fileRule;
    }

    public List<FieldRule> getFieldRules() {
        return fieldRules;
    }

    public void setFieldRules(List<FieldRule> fieldRules) {
        this.fieldRules = fieldRules;
    }

    public Long getAggregationFunctionId() {
        return aggregationFunctionId;
    }

    public void setAggregationFunctionId(Long aggregationFunctionId) {
        this.aggregationFunctionId = aggregationFunctionId;
    }

    public boolean validate() {
        if (!fieldRules.isEmpty()) {
            if (!fieldRules.get(0).isMandatory() || (value != null && !value.isEmpty())) {
                return true;
            }
            if (columnRules.size() == 0) {
                return false;
            }
            for (ColumnRule columnRule : columnRules) {
                if (columnRule.getName() == null || columnRule.getName().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public FieldRule getFirstFieldRule() {
        return getFieldRules().get(0);
    }

}
