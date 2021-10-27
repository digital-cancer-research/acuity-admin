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

public class AEGroupValueRule extends GroupValueBase {

    private String pt;

    public void setPt(String pt) {
        this.pt = pt;
    }

    @Override
    public void setValues(String[] row) {
        setPt(row[1]);
    }

    @Override
    public String getUniqueField() {
        return pt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AEGroupValueRule)) {
            return false;
        }

        AEGroupValueRule that = (AEGroupValueRule) o;

        return name.equals(that.name) && pt.equals(that.pt);
    }

    @Override
    public int hashCode() {
        int result = pt.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
