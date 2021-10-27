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
 * Concomitant data: chemotherapy, radiotherapy, general concomitant medication
 */
public class SdtmEntityCM extends SdtmEntity {

    private String cmtrt;
    private String cmdecod;
    private String cmstdtc;
    private String cmendtc;
    private Double cmdose;
    private String cmdosu;
    private String cmdosfrq;
    private String cmindc;
    private String cmcat;
    private String cmscat;
    private String cmloc;
    private Double visitnum;

    public String getCmdecod() {
        return cmdecod;
    }

    public String getCmstdtc() {
        return cmstdtc;
    }

    public String getCmendtc() {
        return cmendtc;
    }

    public Double getCmdose() {
        return cmdose;
    }

    public String getCmscat() {
        return cmscat;
    }

    public String getCmtrt() {
        return cmtrt;
    }

    public String getCmloc() {
        return cmloc;
    }

    public Double getVisitnum() {
        return visitnum;
    }

    public String getCmdosu() {
        return cmdosu;
    }

    public String getCmdosfrq() {
        return cmdosfrq;
    }

    public String getCmindc() {
        return cmindc;
    }

    public String getCmcat() {
        return cmcat;
    }

    public void read(TableRow row) {
        seq = readString(row, "CMSEQ");

        cmtrt = (String) row.getValue("CMTRT");
        cmdecod = readStringIntern(row, "CMDECOD");
        cmstdtc = (String) row.getValue("CMSTDTC");
        cmendtc = (String) row.getValue("CMENDTC");
        cmdose = readDouble(row, "CMDOSE");
        cmdosu = readStringIntern(row, "CMDOSU");
        cmdosfrq = readStringIntern(row, "CMDOSFRQ");
        cmindc = readString(row, "CMINDC");
        cmcat = readStringIntern(row, "CMCAT");
        cmscat = readStringIntern(row, "CMSCAT");
        cmloc = readStringIntern(row, "CMLOC");
        visitnum = readDouble(row, "VISITNUM");
    }
}
