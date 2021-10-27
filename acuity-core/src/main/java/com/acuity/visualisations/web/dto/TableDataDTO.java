package com.acuity.visualisations.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableDataDTO {

    private int total;
    private List<Object[]> data;

    public TableDataDTO(int total, List<Object[]> data) {
        this.total = total;
        this.data = data;
    }
}
