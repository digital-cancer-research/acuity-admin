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

package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.MappingRule;

import java.math.BigDecimal;
import java.util.List;

public class MapRuleDTO {
    private Long id;
    private Long agrFunctions;
    private String dataField;
    private String decodingInfo;
    private String defaultValue;
    private String sourceData;
    private boolean mandatory;
    private String helpText;
    private BigDecimal order;
    private boolean dynamic;

    public MapRuleDTO() {
    }

    public MapRuleDTO(MappingRule mappingRule) {
        id = mappingRule.getId();
        agrFunctions = mappingRule.getAggregationFunction().getId();
        List<FieldRule> fieldRules = mappingRule.getFieldRules();
        if (!fieldRules.isEmpty()) {
            dataField = fieldRules.get(0).getDescription().getText();
        }
        decodingInfo = mappingRule.getFmtName();
        defaultValue = mappingRule.getValue();
        StringBuilder builder = new StringBuilder();
        List<ColumnRule> columnRules = mappingRule.getColumnRules();
        for (int i = 0; i < columnRules.size(); i++) {
            ColumnRule columnRule = columnRules.get(i);
            builder.append(columnRule.getName());
            builder.append(", ");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }
        sourceData = builder.toString();
        mandatory = fieldRules.get(0).isMandatory();
        helpText = mappingRule.getHelpText();
        order = fieldRules.get(0).getOrder();
        dynamic = fieldRules.get(0).isDynamic();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgrFunctions() {
        return agrFunctions;
    }

    public void setAgrFunctions(Long agrFunctions) {
        this.agrFunctions = agrFunctions;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public String getDecodingInfo() {
        return decodingInfo;
    }

    public void setDecodingInfo(String decodingInfo) {
        this.decodingInfo = decodingInfo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public BigDecimal getOrder() {
        return order;
    }

    public void setOrder(BigDecimal order) {
        this.order = order;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
