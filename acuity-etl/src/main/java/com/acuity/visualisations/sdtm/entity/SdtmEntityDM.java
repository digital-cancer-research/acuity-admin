package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Demography
 */
public class SdtmEntityDM extends SdtmEntity {

    private String sex;
    private String race;
    private String dmdtc;
    private String brthdtc;

    public String getSex() {
        return sex;
    }

    public String getRace() {
        return race;
    }

    public String getDmdtc() {
        return dmdtc;
    }

    public String getBrthdtc() {
        return brthdtc;
    }

    public void read(TableRow row) {
        sex = (String) row.getValue("SEX");
        race = (String) row.getValue("RACE");
        dmdtc = (String) row.getValue("DMDTC");
        brthdtc = (String) row.getValue("BRTHDTC");
    }
}
