package com.acuity.visualisations.dal.util;

public class JoinDeclarationBuilder {

    private JoinDeclaration declaration;

    public JoinDeclarationBuilder() {
        declaration = new JoinDeclaration();
    }

    public JoinDeclarationBuilder setTargetEntity(String targetEntity) {
        declaration.setTargetEntity(targetEntity);
        return this;
    }

    public JoinDeclarationBuilder setSourceEntity(String sourceEntity) {
        declaration.setSourceEntity(sourceEntity);
        return this;
    }

    public JoinDeclarationBuilder addColumnToJoin(String sourceColumn, String targetColumn) {
        declaration.putColumnsToJoin(sourceColumn, targetColumn);
        return this;
    }

    public JoinDeclaration build() {
        JoinDeclaration resultDeclaration = new JoinDeclaration(declaration);
        declaration = new JoinDeclaration();
        return resultDeclaration;
    }
}
