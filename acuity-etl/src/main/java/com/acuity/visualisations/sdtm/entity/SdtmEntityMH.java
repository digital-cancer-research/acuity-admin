package com.acuity.visualisations.sdtm.entity;

import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Created by knml167 on 19/11/2014.
 */
public class SdtmEntityMH extends SdtmEntity {
    private String mhterm;
    private String fastresc;
    private String mhscat;
    private String mhstdtc;

    public String getMhterm() {
        return mhterm;
    }

    public String getFastresc() {
        return fastresc;
    }

    public String getMhscat() {
        return mhscat;
    }

    public String getMhstdtc() {
        return mhstdtc;
    }

    @Override
    public void read(TableRow row) {
        seq = readString(row, "MHSEQ");
        mhterm = readString(row, "MHTERM");
        fastresc = readString(row, "FASTRESC");
        mhscat = readString(row, "MHSCAT");
        mhstdtc = readString(row, "MHSTDTC");
    }
}
