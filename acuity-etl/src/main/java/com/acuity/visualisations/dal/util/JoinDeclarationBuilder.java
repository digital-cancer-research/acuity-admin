/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
