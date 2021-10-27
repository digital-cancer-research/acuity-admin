package com.acuity.visualisations.mapping.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author adavliatov.
 * @since 19.07.2016.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SubjectGroupingType extends MappingEntity implements StaticEntity {
    private String name;
}
