package com.acuity.visualisations.dal.util;

public class TableFieldBuilder {

    private final String sourceTable;
    private String field;
    private String value;

    public TableFieldBuilder(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public TableFieldBuilder setField(String field) {
        this.field = field;
        return this;
    }

    public TableFieldBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public TableField build() {
        return new TableField(sourceTable, field, value);
    }

}
