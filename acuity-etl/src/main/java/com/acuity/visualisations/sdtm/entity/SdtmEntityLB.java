package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Laboratory Test Results
 */
public class SdtmEntityLB extends SdtmEntity {

    private Double visitnum;
    private String lbdtc;
    private String lbtest;
    private Double lbstresn;
    private String lbstresu;

    private Double lbstnrlo;
    private Double lbstnrhi;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getLbdtc() {
        return lbdtc;
    }

    public String getLbtest() {
        return lbtest;
    }

    public Double getLbstresn() {
        return lbstresn;
    }

    public String getLbstresu() {
        return lbstresu;
    }

    public Double getLbstnrlo() {
        return lbstnrlo;
    }

    public Double getLbstnrhi() {
        return lbstnrhi;
    }

    public void read(TableRow row) {

        /**
         * 1. Clinical encounter number.
         * 2. Numeric version of VISIT, used for sorting.
         */
        visitnum = readDouble(row, "VISITNUM"); // Visit number

        /**
         * Date/Time of Specimen Collection
         */
        lbdtc = readString(row, "LBDTC");

        /**
         * Lab Test or Examination Short Name
         */
        lbtest = readStringIntern(row, "LBTEST");

        /**
         * Numeric Result/Finding in Standard Units
         */
        lbstresn = readDouble(row, "LBSTRESN");

        /**
         * Standard Units
         */
        lbstresu = readStringIntern(row, "LBSTRESU");

        /**
         * Reference Range Lower Limit-Std Units
         */
        lbstnrlo = readDouble(row, "LBSTNRLO");

        /**
         * Reference Range Upper Limit-Std Units
         */
        lbstnrhi = readDouble(row, "LBSTNRHI");
    }
}
