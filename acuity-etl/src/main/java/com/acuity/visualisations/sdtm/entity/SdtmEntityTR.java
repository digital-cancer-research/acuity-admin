package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Tumor results
 * "CDISC SDTM Implementation Guide (Version 3.2)" / "Section 6.3 - TU TR and RS Domains.pdf"
 * <p>
 * The TR domain represents quantitative measurements and/or qualitative assessments of the tumors
 * i.e. malignant tumors and other sites of disease, e.g. lymph nodes) identified in the TU domain.
 * These measurements are usually taken at baseline and then at each subsequent assessment to support response evaluations.
 * A record in the TR domain contains the following information: a unique tumor ID value; test and result;
 * method used; role of the individual assessing the tumor; and timing information.
 */
public class SdtmEntityTR extends SdtmEntity {

    private String trstresc;
    private String trlinkid;
    private String trtestcd;
    private String trgrpid;
    private Double visitnum;

    private String trdtc;
    private String trloc;

    public String getTrstresc() {
        return trstresc;
    }

    public String getTrlinkid() {
        return trlinkid;
    }

    public String getTrtestcd() {
        return trtestcd;
    }

    public String getTrgrpid() {
        return trgrpid;
    }

    public Double getVisitnum() {
        return visitnum;
    }

    public String getTrdtc() {
        return trdtc;
    }

    public String getTrloc() {
        return trloc;
    }

    public void read(TableRow row) {
        seq = readString(row, "TRSEQ");

        /**
         * Character Result/Finding in Std Format
         *
         * - Longest Diameter of Target Lesion
         * - Any Target Lesion Present (yes/no)
         * - Any Non Target Lesion Present (yes/no)
         */
        trstresc = (String) row.getValue("TRSTRESC");

        /**
         * Identifier used to link the assessment result records to the individual tumor identification record in TU domain.
         */
        trlinkid = (String) row.getValue("TRLINKID");

        /**
         * Tumor Assessment Short Name
         */
        trtestcd = (String) row.getValue("TRTESTCD");

        trgrpid = (String) row.getValue("TRGRPID");

        visitnum = readDouble(row, "VISITNUM");

        trdtc = (String) row.getValue("TRDTC");

        trloc = (String) row.getValue("TRLOC");
    }
}
