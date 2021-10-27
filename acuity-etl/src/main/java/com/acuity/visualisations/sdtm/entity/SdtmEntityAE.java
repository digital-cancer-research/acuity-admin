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

public class SdtmEntityAE extends SdtmEntity {

    private String aeterm;
    private String aestdtc;
    private String aeendtc;
    private String aespid;

    private String aeser;
    private String aerel;
    private String aetoxgr;
    private String aedecod;
    private String aellt;
    private String aehlt;
    private String aeacn;
    private String aesdth;
    private String aeslife;
    private String aeshosp;
    private String aesdisab;
    private String aescong;
    private String aesmie;
    private String aesoc;

    public String getAeterm() {
        return aeterm;
    }

    public String getAestdtc() {
        return aestdtc;
    }

    public String getAeendtc() {
        return aeendtc;
    }

    public String getAespid() {
        return aespid;
    }

    public String getAeser() {
        return aeser;
    }

    public String getAerel() {
        return aerel;
    }

    public String getAetoxgr() {
        return aetoxgr;
    }

    public String getAedecod() {
        return aedecod;
    }

    public String getAehlt() {
        return aehlt;
    }

    public String getAeacn() {
        return aeacn;
    }

    public String getAellt() {
        return aellt;
    }

    public String getAesdth() {
        return aesdth;
    }

    public String getAeslife() {
        return aeslife;
    }

    public String getAeshosp() {
        return aeshosp;
    }

    public String getAesdisab() {
        return aesdisab;
    }

    public String getAescong() {
        return aescong;
    }

    public String getAesmie() {
        return aesmie;
    }

    public String getAesoc() {
        return aesoc;
    }

    public void read(TableRow row) {
        seq = readString(row, "AESEQ");

        aeterm = (String) row.getValue("AETERM"); //AE Term
        aestdtc = (String) row.getValue("AESTDTC"); //AE Start Date
        aeendtc = (String) row.getValue("AEENDTC"); //AE End Date
        aespid = (String) row.getValue("AESPID");
        aeser = (String) row.getValue("AESER"); //Serious AE (yes/no)
        aerel = (String) row.getValue("AEREL"); //Reasonable Possibility AE Caused by Investigational Product (yes/no)
        aetoxgr = (String) row.getValue("AETOXGR"); //Maximum AE CTC grade
        aedecod = (String) row.getValue("AEDECOD"); //MEDDRA Preferred term
        aehlt = (String) row.getValue("AEHLT"); //MEDDRA Higher-Level Term
        aellt = (String) row.getValue("AELLT"); //MEDDRA Low-Level Term
        aesoc = (String) row.getValue("AESOC"); //MEDDRA System Organ Class
        aeacn = (String) row.getValue("AEACN");

        aesdth = (String) row.getValue("AESDTH"); //Results in Death (yes/no)
        aeslife = (String) row.getValue("AESLIFE"); //Life Threatening (yes/no)
        aeshosp = (String) row.getValue("AESHOSP"); //Requires or Prolongs Hospitalization
        aesdisab = (String) row.getValue("AESDISAB"); //Persist. or Sign. Disability/Incapacity
        aescong = (String) row.getValue("AESCONG"); //Congenital Anomaly or Birth Defect
        aesmie = (String) row.getValue("AESMIE"); //Other Serious/Important Medically Event
    }

    public void setAeterm(String aeterm) {
        this.aeterm = aeterm;
    }

    public void setAestdtc(String aestdtc) {
        this.aestdtc = aestdtc;
    }

    public void setAeendtc(String aeendtc) {
        this.aeendtc = aeendtc;
    }

    public void setAespid(String aespid) {
        this.aespid = aespid;
    }

    public void setAeser(String aeser) {
        this.aeser = aeser;
    }

    public void setAerel(String aerel) {
        this.aerel = aerel;
    }

    public void setAetoxgr(String aetoxgr) {
        this.aetoxgr = aetoxgr;
    }

    public void setAedecod(String aedecod) {
        this.aedecod = aedecod;
    }

    public void setAellt(String aellt) {
        this.aellt = aellt;
    }

    public void setAehlt(String aehlt) {
        this.aehlt = aehlt;
    }

    public void setAeacn(String aeacn) {
        this.aeacn = aeacn;
    }

    public void setAesdth(String aesdth) {
        this.aesdth = aesdth;
    }

    public void setAeslife(String aeslife) {
        this.aeslife = aeslife;
    }

    public void setAeshosp(String aeshosp) {
        this.aeshosp = aeshosp;
    }

    public void setAesdisab(String aesdisab) {
        this.aesdisab = aesdisab;
    }

    public void setAescong(String aescong) {
        this.aescong = aescong;
    }

    public void setAesmie(String aesmie) {
        this.aesmie = aesmie;
    }

    public void setAesoc(String aesoc) {
        this.aesoc = aesoc;
    }
}
