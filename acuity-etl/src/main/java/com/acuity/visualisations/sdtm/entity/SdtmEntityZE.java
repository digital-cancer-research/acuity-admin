package com.acuity.visualisations.sdtm.entity;

import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Created by knml167 on 18/11/2014.
 */
public class SdtmEntityZE extends SdtmEntity {
    private Double visitnum;
    private String zedtc;
    private String zestresc;
    private String zemethod;
    private String zetestcd;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getZedtc() {
        return zedtc;
    }

    public String getZestresc() {
        return zestresc;
    }

    public String getZemethod() {
        return zemethod;
    }

    public String getZetestcd() {
        return zetestcd;
    }

    @Override
    public void read(TableRow row) {
        visitnum = readDouble(row, "VISITNUM");
        zedtc = readString(row, "ZEDTC");
        zestresc = readString(row, "ZESTRESC");
        zemethod = readStringIntern(row, "ZEMETHOD");
        zetestcd = readStringIntern(row, "ZETESTCD");

    }
}
