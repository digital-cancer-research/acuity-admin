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

public class FieldDescription extends MappingEntity implements StaticEntity {

    private String text;

    public FieldDescription() {
    }

    public FieldDescription(String text, Long id) {
        this.text = text;
        setId(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
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
        FieldDescription other = (FieldDescription) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else {
            if (!text.equals(other.text)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "FieldDescription [text=" + text + "]";
    }

}
