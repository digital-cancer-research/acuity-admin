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

/**
 * Created by knml167 on 18/11/2014.
 */
public class SdtmEntityZE extends SdtmEntity {
    private Double visitnum;
    private String zedtc;
    private String zestresc;
    private String zemethod;
    private String zetestcd;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getZedtc() {
        return zedtc;
    }

    public String getZestresc() {
        return zestresc;
    }

    public String getZemethod() {
        return zemethod;
    }

    public String getZetestcd() {
        return zetestcd;
    }

    @Override
    public void read(TableRow row) {
        visitnum = readDouble(row, "VISITNUM");
        zedtc = readString(row, "ZEDTC");
        zestresc = readString(row, "ZESTRESC");
        zemethod = readStringIntern(row, "ZEMETHOD");
        zetestcd = readStringIntern(row, "ZETESTCD");

    }
}
