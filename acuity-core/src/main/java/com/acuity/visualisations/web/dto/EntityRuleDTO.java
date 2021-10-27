package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.FieldRule;

import java.util.ArrayList;
import java.util.List;

public class EntityRuleDTO {
    private Long id;
    private String name;
    private List<FieldRuleDTO> fieldRules;

    public EntityRuleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldRuleDTO> getFieldRules() {
        return fieldRules;
    }

    public void setFieldRules(List<FieldRule> fieldRules) {
        this.fieldRules = new ArrayList<FieldRuleDTO>(fieldRules.size());
        for (FieldRule fieldRule : fieldRules) {
            this.fieldRules.add(new FieldRuleDTO(fieldRule.getId(), fieldRule.getDescription().getText()));
        }
    }
}
