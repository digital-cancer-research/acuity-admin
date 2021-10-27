package com.acuity.visualisations.mapping.entity;


public class AEGroupRule extends GroupRuleBase {

    @Override
    public ProjectGroupType getType() {
        return ProjectGroupType.ae;
    }

    @Override
    public GroupValueBase getValueBaseInstance() {
        return new AEGroupValueRule();
    }

}
