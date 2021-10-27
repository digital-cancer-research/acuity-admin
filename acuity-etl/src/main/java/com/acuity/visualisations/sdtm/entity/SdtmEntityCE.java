package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Date of death
 */
public class SdtmEntityCE extends SdtmEntity {

    private String cestdtc;
    private String cecat;

    public String getCestdtc() {
        return cestdtc;
    }

    public String getCecat() {
        return cecat;
    }

    public void read(TableRow row) {
        cestdtc = (String) row.getValue("CESTDTC");
        cecat = (String) row.getValue("CECAT");
    }
}
