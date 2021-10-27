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
