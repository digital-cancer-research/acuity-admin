package com.acuity.visualisations.model.output.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Source extends TimestampedEntity  {

    private String name;
    private String version;
    private String type;

    public Source(String name, String version, String type) {
        this.name = name;
        this.version = version;
        this.type = type;
    }

}
