package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Drug doses
 */
public class SdtmEntityEX extends SdtmEntity {

    private String extrt;
    private Double exdose;
    private String exdosu;
    private String exstdtc;
    private String exendtc;
    private String exdosfrq;
    private String exadj;

    public String getExtrt() {
        return extrt;
    }

    public Double getExdose() {
        return exdose;
    }

    public String getExdosu() {
        return exdosu;
    }

    public String getExstdtc() {
        return exstdtc;
    }

    public String getExendtc() {
        return exendtc;
    }

    public String getExdosfrq() {
        return exdosfrq;
    }

    public String getExadj() {
        return exadj;
    }

    public void read(TableRow row) {
        seq = readString(row, "EXSEQ");

        extrt = (String) row.getValue("EXTRT");
        exdose = readDouble(row, "EXDOSE");
        exdosu = (String) row.getValue("EXDOSU");
        exstdtc = (String) row.getValue("EXSTDTC");
        exendtc = (String) row.getValue("EXENDTC");
        exdosfrq = (String) row.getValue("EXDOSFRQ");
        exadj = (String) row.getValue("EXADJ");
    }
}
