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

package com.acuity.visualisations.sdtm.entity;

import com.acuity.visualisations.batch.reader.tablereader.TableRow;


public abstract class SdtmEntity {
    protected String seq;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public abstract void read(TableRow row);

    public String readString(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return value.toString();
    }

    /**
     * Read string and save memory
     *
     * @param row
     * @param column
     * @return
     */
    public String readStringIntern(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim().intern();
        }
        return value.toString().intern();
    }

    public Double readDouble(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof String) {
            try {
                return Double.valueOf(((String) value).trim());
            } catch (Exception e) {
                return null;
            }
        }
        return Double.valueOf(value.toString());
    }
}
