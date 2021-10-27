package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * ECG Test Results
 * Findings related to the collection of ECG data, including position of the subject, method of evaluation,
 * all cycle measurements and all findings from the ECG including an overall interpretation if collected or derived.
 */
public class SdtmEntityEG extends SdtmEntity {

    private Double visitnum;

    private String egdtc;
    private String egstresc;
    private String egtestcd;

    private String egstresn;
    private String egmethod;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getEgdtc() {
        return egdtc;
    }

    public String getEgstresc() {
        return egstresc;
    }

    public String getEgtestcd() {
        return egtestcd;
    }

    public String getEgstresn() {
        return egstresn;
    }

    public String getEgmethod() {
        return egmethod;
    }

    public void read(TableRow row) {
        seq = readString(row, "EGSEQ");

        visitnum = readDouble(row, "VISITNUM");

        /**
         * Date/Time of Measurements
         */
        egdtc = readString(row, "EGDTC");

        /**
         * Character Result/Finding in Std Format
         */
        egstresc = readString(row, "EGSTRESC");

        /**
         * ECG Test or Examination Short Name
         */
        egtestcd = readStringIntern(row, "EGTESTCD");

        egmethod = readStringIntern(row, "EGMETHOD");

        egstresn = readString(row, "EGSTRESN");
    }
}
