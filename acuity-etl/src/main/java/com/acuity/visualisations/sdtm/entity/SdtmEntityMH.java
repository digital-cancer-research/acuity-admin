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
 * Created by knml167 on 19/11/2014.
 */
public class SdtmEntityMH extends SdtmEntity {
    private String mhterm;
    private String fastresc;
    private String mhscat;
    private String mhstdtc;

    public String getMhterm() {
        return mhterm;
    }

    public String getFastresc() {
        return fastresc;
    }

    public String getMhscat() {
        return mhscat;
    }

    public String getMhstdtc() {
        return mhstdtc;
    }

    @Override
    public void read(TableRow row) {
        seq = readString(row, "MHSEQ");
        mhterm = readString(row, "MHTERM");
        fastresc = readString(row, "FASTRESC");
        mhscat = readString(row, "MHSCAT");
        mhstdtc = readString(row, "MHSTDTC");
    }
}
