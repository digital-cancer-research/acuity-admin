package com.acuity.visualisations.mapping.entity;


public abstract class MappingEntity implements DynamicEntity {

    private Long id;

    public MappingEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
