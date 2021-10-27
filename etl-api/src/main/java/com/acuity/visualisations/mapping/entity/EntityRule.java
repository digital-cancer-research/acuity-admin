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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRule extends MappingEntity implements StaticEntity {

    private String name;
    private Map<FileDescription, FileDescription> descriptions = new HashMap<FileDescription, FileDescription>();
    private boolean storeInCache;
    private String globalFunction;
    private List<FieldRule> fieldRules = new ArrayList<FieldRule>();

    public EntityRule() {
    }

    public EntityRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<FileDescription, FileDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<FileDescription, FileDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public boolean isStoreInCache() {
        return storeInCache;
    }

    public void setStoreInCache(boolean storeInCache) {
        this.storeInCache = storeInCache;
    }

    public String getGlobalFunction() {
        return globalFunction;
    }

    public void setGlobalFunction(String globalFunction) {
        this.globalFunction = globalFunction;
    }

    public List<FieldRule> getFieldRules() {
        return fieldRules;
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
        EntityRule other = (EntityRule) obj;
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

}
