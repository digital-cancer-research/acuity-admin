package com.acuity.visualisations.dal.util;

import java.util.HashMap;
import java.util.Map;

public class JoinDeclaration {

    private String sourceEntity;
    private String targetEntity;
    private Map<String, String> columnsToJoin = new HashMap<String, String>();

    public JoinDeclaration() {
    }

    public JoinDeclaration(JoinDeclaration declaration) {
        this.sourceEntity = declaration.sourceEntity;
        this.targetEntity = declaration.targetEntity;
        for (Map.Entry<String, String> entry : declaration.columnsToJoin.entrySet()) {
            columnsToJoin.put(entry.getKey(), entry.getValue());
        }
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void putColumnsToJoin(String sourceColumn, String targetColumn) {
        columnsToJoin.put(sourceColumn, targetColumn);
    }

    public Map<String, String> getColumnsToJoin() {
        return columnsToJoin;
    }

    public String getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(String sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

}
