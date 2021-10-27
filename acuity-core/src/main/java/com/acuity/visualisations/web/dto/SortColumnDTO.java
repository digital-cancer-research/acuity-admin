package com.acuity.visualisations.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SortColumnDTO {
    private Integer columnIndex;
    private Integer sortOrder;
}
