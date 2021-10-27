package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Visit
 */
public class SdtmEntitySV extends SdtmEntity {

    private String visitnum;
    private String svstdtc;

    public String getVisitnum() {
        return visitnum;
    }

    public String getSvstdtc() {
        return svstdtc;
    }

    public void read(TableRow row) {
        visitnum = readString(row, "VISITNUM");
        svstdtc = (String) row.getValue("SVSTDTC");
    }
}
