package com.acuity.visualisations.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TableMetaDTO implements Serializable {
    private static final long serialVersionUID = 1426169207L;

    private long total;
    private TableField[] fields;

    public TableMetaDTO(long total, TableField[] fields) {
        this.total = total;
        this.fields = fields;
    }

    @Getter
    @Setter
    public static class TableField  implements Serializable {
        private static final long serialVersionUID = 1426169525L;

        private String name;

        public TableField(String name) {
            this.name = name;
        }
    }
}
