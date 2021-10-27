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
 * Vital Signs
 * Measurements including but not limited to blood pressure, temperature, respiration, body surface area, BMI, height and weight.
 */
public class SdtmEntityVS extends SdtmEntity {

    private Double visitnum;

    private String vsdtc;
    private String vstestcd;
    private String vstest;
    private String vsstresn;
    private String vsstresu;

    public String getVstest() {
        return vstest;
    }

    public String getVsstresu() {
        return vsstresu;
    }

    public Double getVisitnum() {
        return visitnum;
    }

    public String getVsdtc() {
        return vsdtc;
    }

    public String getVsstresn() {
        return vsstresn;
    }

    public String getVstestcd() {
        return vstestcd;
    }

    public void read(TableRow row) {
        seq = readString(row, "VSSEQ");

        visitnum = readDouble(row, "VISITNUM");

        /**
         * Date/Time of Measurements
         */
        vsdtc = readString(row, "VSDTC");

        /**
         * Numeric Result/Finding in Standard Units
         */
        vsstresn = readString(row, "VSSTRESN");

        vsstresu = readString(row, "VSSTRESU");

        /**
         * Vital Signs Test Short Name
         * PULSE, SYSBP, DIABP, WEIGHT, HEIGHT, SPO2, etc
         */
        vstestcd = readStringIntern(row, "VSTESTCD");

        vstest = readStringIntern(row, "VSTEST");
    }
}
