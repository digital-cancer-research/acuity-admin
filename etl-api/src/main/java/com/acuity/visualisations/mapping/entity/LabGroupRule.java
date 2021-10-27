package com.acuity.visualisations.mapping.entity;


public class LabGroupRule extends GroupRuleBase {

    @Override
    public ProjectGroupType getType() {
        return ProjectGroupType.lab;
    }

    @Override
    public GroupValueBase getValueBaseInstance() {
        return new LabGroupValueRule();
    }
}
