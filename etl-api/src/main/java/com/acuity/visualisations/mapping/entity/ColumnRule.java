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

public class ColumnRule extends MappingEntity {

    private String name;

    private MappingRule mappingRule;

    private Long mappingRuleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MappingRule getMappingRule() {
        return mappingRule;
    }

    public void setMappingRule(MappingRule mappingRule) {
        this.mappingRule = mappingRule;
    }

    public Long getMappingRuleId() {
        return mappingRuleId;
    }

    public void setMappingRuleId(Long mappingRuleId) {
        this.mappingRuleId = mappingRuleId;
    }
}
