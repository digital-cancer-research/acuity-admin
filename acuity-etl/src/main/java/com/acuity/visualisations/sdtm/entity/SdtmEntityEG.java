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
 * ECG Test Results
 * Findings related to the collection of ECG data, including position of the subject, method of evaluation,
 * all cycle measurements and all findings from the ECG including an overall interpretation if collected or derived.
 */
public class SdtmEntityEG extends SdtmEntity {

    private Double visitnum;

    private String egdtc;
    private String egstresc;
    private String egtestcd;

    private String egstresn;
    private String egmethod;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getEgdtc() {
        return egdtc;
    }

    public String getEgstresc() {
        return egstresc;
    }

    public String getEgtestcd() {
        return egtestcd;
    }

    public String getEgstresn() {
        return egstresn;
    }

    public String getEgmethod() {
        return egmethod;
    }

    public void read(TableRow row) {
        seq = readString(row, "EGSEQ");

        visitnum = readDouble(row, "VISITNUM");

        /**
         * Date/Time of Measurements
         */
        egdtc = readString(row, "EGDTC");

        /**
         * Character Result/Finding in Std Format
         */
        egstresc = readString(row, "EGSTRESC");

        /**
         * ECG Test or Examination Short Name
         */
        egtestcd = readStringIntern(row, "EGTESTCD");

        egmethod = readStringIntern(row, "EGMETHOD");

        egstresn = readString(row, "EGSTRESN");
    }
}
