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
