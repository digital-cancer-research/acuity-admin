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
 * Drug doses
 */
public class SdtmEntityEX extends SdtmEntity {

    private String extrt;
    private Double exdose;
    private String exdosu;
    private String exstdtc;
    private String exendtc;
    private String exdosfrq;
    private String exadj;

    public String getExtrt() {
        return extrt;
    }

    public Double getExdose() {
        return exdose;
    }

    public String getExdosu() {
        return exdosu;
    }

    public String getExstdtc() {
        return exstdtc;
    }

    public String getExendtc() {
        return exendtc;
    }

    public String getExdosfrq() {
        return exdosfrq;
    }

    public String getExadj() {
        return exadj;
    }

    public void read(TableRow row) {
        seq = readString(row, "EXSEQ");

        extrt = (String) row.getValue("EXTRT");
        exdose = readDouble(row, "EXDOSE");
        exdosu = (String) row.getValue("EXDOSU");
        exstdtc = (String) row.getValue("EXSTDTC");
        exendtc = (String) row.getValue("EXENDTC");
        exdosfrq = (String) row.getValue("EXDOSFRQ");
        exadj = (String) row.getValue("EXADJ");
    }
}
