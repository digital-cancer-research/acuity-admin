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

package com.acuity.visualisations.mapping.entity;

import java.math.BigDecimal;

public class FieldRule extends MappingEntity implements StaticEntity {

    private String name;
    private FieldDescription description;
    private String type;
    private String entityRuleId;
    private Long fileDescriptionId;
    private boolean mandatory;
    private BigDecimal order;
    private int entityProcessOrder;
    private boolean dynamic;

    public FieldRule() {
    }

    public FieldRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldDescription getDescription() {
        return description;
    }

    public void setDescription(FieldDescription description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FieldRule other = (FieldRule) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else {
            if (!name.equals(other.name)) {
                return false;
            }
        }
        return true;
    }

    public String getEntityRuleId() {
        return entityRuleId;
    }

    public void setEntityRuleId(String entityRuleId) {
        this.entityRuleId = entityRuleId;
    }

    public Long getFileDescriptionId() {
        return fileDescriptionId;
    }

    public void setFileDescriptionId(Long fileDescriptionId) {
        this.fileDescriptionId = fileDescriptionId;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public BigDecimal getOrder() {
        return order;
    }

    public void setOrder(BigDecimal order) {
        this.order = order;
    }

    public int getEntityProcessOrder() {
        return entityProcessOrder;
    }

    public void setEntityProcessOrder(int entityProcessOrder) {
        this.entityProcessOrder = entityProcessOrder;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
