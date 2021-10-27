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
