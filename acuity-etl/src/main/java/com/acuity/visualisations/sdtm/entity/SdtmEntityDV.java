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
 * Protocol Deviations
 * <p>
 * The intent of the domain is to capture protocol violations and deviations during the course of the study
 * and will store only those criteria violation by or deviated from by the subject
 * and not a response to each violation or deviation.
 */
public class SdtmEntityDV extends SdtmEntity {

    private String dvterm;
    private String dvcat;
    private String dvdecod;
    private String dvdtc;

    public String getDvterm() {
        return dvterm;
    }

    public String getDvcat() {
        return dvcat;
    }

    public String getDvdecod() {
        return dvdecod;
    }

    public String getDvdtc() {
        return dvdtc;
    }

    public void read(TableRow row) {
        dvterm = readStringIntern(row, "DVTERM");
        dvcat = readStringIntern(row, "DVCAT");
        dvdecod = readStringIntern(row, "DVDECOD");
        dvdtc = readString(row, "DVDTC");
    }
}
