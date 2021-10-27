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
