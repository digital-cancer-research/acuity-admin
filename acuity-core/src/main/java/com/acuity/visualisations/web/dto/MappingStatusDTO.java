package com.acuity.visualisations.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MappingStatusDTO {
    private long fileRuleId;
    private long fileDescriptionId;
    private String name;
    private boolean ready;
    private boolean enabled;
    private String dataSource;
}
