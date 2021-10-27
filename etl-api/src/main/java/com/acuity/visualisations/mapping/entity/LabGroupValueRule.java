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

public class LabGroupValueRule extends GroupValueBase {

    protected String labCode;
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    @Override
    public void setValues(String[] row) {
        setLabCode(row[1]);
        setDescription(row[2]);
    }

    @Override
    public String getUniqueField() {
        return labCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabGroupValueRule)) {
            return false;
        }

        LabGroupValueRule that = (LabGroupValueRule) o;

        return name.equals(that.name) && labCode.equals(that.labCode);
    }

    @Override
    public int hashCode() {
        int result = labCode.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
