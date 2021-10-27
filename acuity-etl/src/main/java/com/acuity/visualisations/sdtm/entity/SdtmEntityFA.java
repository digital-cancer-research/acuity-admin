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
 * Findings About Events or Interventions
 */
public class SdtmEntityFA extends SdtmEntity {
    private static final String FASTRESC = "FASTRESC";

    private String fatest;

    private String faspid;
    private String fadtc;
    private String fastresc;
    private String fady;
    private String fatestcd;
    private String faorres;

    public void setFatest(String fatest) {
        this.fatest = fatest;
    }

    public void setFaspid(String faspid) {
        this.faspid = faspid;
    }

    public void setFadtc(String fadtc) {
        this.fadtc = fadtc;
    }

    public void setFastresc(String fastresc) {
        this.fastresc = fastresc;
    }

    public void setFady(String fady) {
        this.fady = fady;
    }

    public void setFatestcd(String fatestcd) {
        this.fatestcd = fatestcd;
    }

    public void setFaorres(String faorres) {
        this.faorres = faorres;
    }

    public String getFatest() {
        return fatest;
    }

    public String getFaspid() {
        return faspid;
    }

    public String getFadtc() {
        return fadtc;
    }

    public String getFastresc() {
        return fastresc;
    }

    public String getFady() {
        return fady;
    }

    public String getFatestcd() {
        return fatestcd;
    }

    public String getFaorres() {
        return faorres;
    }

    public void read(TableRow row) {
        fatest = (String) row.getValue("FATEST");
        faspid = (String) row.getValue("FASPID");
        fadtc = (String) row.getValue("FADTC");
        fastresc = (String) row.getValue(FASTRESC);
        fastresc = (String) row.getValue(FASTRESC);
        fastresc = (String) row.getValue(FASTRESC);
        fatestcd = (String) row.getValue("FATESTCD");
        faorres = (String) row.getValue("FAORRES");
    }

}
