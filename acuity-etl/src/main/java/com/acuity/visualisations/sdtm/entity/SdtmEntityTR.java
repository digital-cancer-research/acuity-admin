/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
