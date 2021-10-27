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
 * Dose discontinuation
 */
public class SdtmEntityDS extends SdtmEntity {

    private String dsterm;
    private String dsstdtc;
    private String dsdecod;
    private String dscat;
    private String dsscat;

    public String getDsterm() {
        return dsterm;
    }

    public String getDsstdtc() {
        return dsstdtc;
    }

    public String getDsdecod() {
        return dsdecod;
    }

    public String getDscat() {
        return dscat;
    }

    public String getDsscat() {
        return dsscat;
    }

    public void read(TableRow row) {
        seq = readString(row, "DSSEQ");

        dsterm = readString(row, "DSTERM");
        dsstdtc = readString(row, "DSSTDTC");
        dsdecod = readString(row, "DSDECOD");
        dscat = readString(row, "DSCAT");
        dsscat = readString(row, "DSSCAT");
    }
}
