package com.acuity.visualisations.dal.util;

public class TableField {

    private String sourceTable;
    private String field;
    private String value;

    public TableField(String sourceTable, String field) {
        this.sourceTable = sourceTable;
        this.field = field;
    }

    public TableField(String sourceTable, String field, String value) {
        this.sourceTable = sourceTable;
        this.field = field;
        this.value = value;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
