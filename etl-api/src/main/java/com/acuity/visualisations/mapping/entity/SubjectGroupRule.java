package com.acuity.visualisations.mapping.entity;

public class SubjectGroupRule extends GroupRuleBase {

    @Override
    public ProjectGroupType getType() {
        return ProjectGroupType.subject;
    }

    @Override
    public GroupValueBase getValueBaseInstance() {
        return new SubjectGroupValueRule();
    }
}
