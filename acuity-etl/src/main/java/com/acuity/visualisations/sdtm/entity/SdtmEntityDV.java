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
