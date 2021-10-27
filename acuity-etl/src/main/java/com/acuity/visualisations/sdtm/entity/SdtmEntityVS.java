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
