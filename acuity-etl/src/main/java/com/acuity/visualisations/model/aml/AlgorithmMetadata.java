package com.acuity.visualisations.model.aml;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlgorithmMetadata {

    private String name;
    private String version;

}
