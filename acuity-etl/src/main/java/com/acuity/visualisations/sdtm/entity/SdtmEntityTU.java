package com.acuity.visualisations.sdtm.entity;


import com.acuity.visualisations.batch.reader.tablereader.TableRow;

/**
 * Tumor identicaton
 * "CDISC SDTM Implementation Guide (Version 3.2)" / "Section 6.3 - TU TR and RS Domains.pdf"
 * <p>
 * The TU domain represents data that uniquely identifies tumors (i.e. malignant tumors and other sites of disease,
 * e.g. lymph nodes). The tumors are identified by an investigator and/or independent assessor and classified according to the disease assessment criteria.
 * In RECIST terms this equates to the identification of Target, Non-Target or New tumors.
 * A record in the TU domain contains the following information: a unique tumor ID value; anatomical location of the tumor;
 * method used to identify the tumor; role of the individual identifying the tumor; and timing information.
 */
public class SdtmEntityTU extends SdtmEntity {

    private Double visitnum;
    private String tudtc;
    private String tuloc;
    private String tulinkid;
    private String tutestcd;
    private String tustresc;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getTulinkid() {
        return tulinkid;
    }

    public String getTudtc() {
        return tudtc;
    }

    public String getTuloc() {
        return tuloc;
    }

    public String getTutestcd() {
        return tutestcd;
    }

    public String getTustresc() {
        return tustresc;
    }

    public void read(TableRow row) {
        seq = readString(row, "TUSEQ");

        /**
         * 1. Clinical encounter number.
         * 2. Numeric version of VISIT, used for sorting.
         */
        visitnum = readDouble(row, "VISITNUM"); // Visit number

        /**
         * Identifier used to link identified tumors to the assessment results (in TR domain) over the course of the study.
         */
        tulinkid = (String) row.getValue("TULINKID"); // Target Lesion Number

        /**
         * TUDTC variable represents the date of the scan/image/physical exam
         * not the date that the image was read to identify tumors.
         * TUDTC variable does not represent the VISIT date.
         *
         * - New Lesion Date of Scan/Clinical Examination
         * - Target Lesion Date of Scan/Clinical Examination
         * - Non-Target Lesion Date of Scan/Clinical Examination
         */
        tudtc = (String) row.getValue("TUDTC");

        /**
         * Location of the Tumor
         * - New Lesion Site
         * - Site of Target Lesion
         * - Site of Non-Target Lesion
         */
        tuloc = (String) row.getValue("TULOC");

        /**
         * Short name of the TEST in TUTEST. TUTESTCD cannot be longer
         * than 8 characters nor can start with a number. TUTESTCD contain
         * characters other than letters, numbers, or underscores. Example:
         * TUMIDENT (Tumor Identification).
         */
        tutestcd = (String) row.getValue("TUTESTCD");


        /**
         * Tumor Identification Result
         */
        tustresc = (String) row.getValue("TUSTRESC");
    }
}
