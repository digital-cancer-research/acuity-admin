package com.acuity.visualisations.web.dto;


import com.acuity.visualisations.mapping.entity.GroupRuleBase;

public class GroupRuleDTO {
    private GroupRuleBase group;
    private String message;

    public GroupRuleDTO(GroupRuleBase group, String message) {
        this.group = group;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupRuleBase getGroup() {
        return group;
    }

    public void setGroup(GroupRuleBase group) {
        this.group = group;
    }


}
