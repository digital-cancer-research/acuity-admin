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
 * Tumor response, disease response
 * "CDISC SDTM Implementation Guide (Version 3.2)"
 * RS (Disease Response): The RS domain represents the response evaluation(s) determined from the data in TR.
 * Data from other sources (in other SDTM domains) might also be used in an assessment of response.
 */
public class SdtmEntityRS extends SdtmEntity {

    private Double visitnum;
    private String rsstresc;
    private String rslinkid;
    private String rstestcd;

    public Double getVisitnum() {
        return visitnum;
    }

    public String getRsstresc() {
        return rsstresc;
    }

    public String getRslinkid() {
        return rslinkid;
    }

    public String getRstestcd() {
        return rstestcd;
    }

    public void read(TableRow row) {
        seq = readString(row, "RSSEQ");

        /**
         * 1. Clinical encounter number.
         * 2. Numeric version of VISIT, used for sorting.
         */
        visitnum = readDouble(row, "VISITNUM"); // Visit number

        /**
         * - RECIST Response
         * - Investigator Opinion of Patient Status
         * - Response of Non-Target Lesions
         */
        rsstresc = (String) row.getValue("RSSTRESC");

        /**
         * Used to link the response assessment to the appropriate measurement records (in TR)
         * for same tumor that was used to determine the response result.
         */
        rslinkid = (String) row.getValue("RSLINKID");

        /**
         * Response Assessment Short Name
         */
        rstestcd = (String) row.getValue("RSTESTCD");

    }
}
