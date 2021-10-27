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

package com.acuity.visualisations.sdtm;

import com.acuity.visualisations.batch.reader.tablereader.FileTypeAwareTableReader;
import com.acuity.visualisations.batch.reader.tablereader.TableReader;
import com.acuity.visualisations.batch.reader.tablereader.TableRow;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.sdtm.entity.SdtmEntity;
import com.acuity.visualisations.sdtm.entity.SdtmEntityAE;
import com.acuity.visualisations.sdtm.entity.SdtmEntityCE;
import com.acuity.visualisations.sdtm.entity.SdtmEntityCM;
import com.acuity.visualisations.sdtm.entity.SdtmEntityDM;
import com.acuity.visualisations.sdtm.entity.SdtmEntityDS;
import com.acuity.visualisations.sdtm.entity.SdtmEntityDV;
import com.acuity.visualisations.sdtm.entity.SdtmEntityEG;
import com.acuity.visualisations.sdtm.entity.SdtmEntityEX;
import com.acuity.visualisations.sdtm.entity.SdtmEntityFA;
import com.acuity.visualisations.sdtm.entity.SdtmEntityLB;
import com.acuity.visualisations.sdtm.entity.SdtmEntityMH;
import com.acuity.visualisations.sdtm.entity.SdtmEntityRS;
import com.acuity.visualisations.sdtm.entity.SdtmEntitySV;
import com.acuity.visualisations.sdtm.entity.SdtmEntityTR;
import com.acuity.visualisations.sdtm.entity.SdtmEntityTU;
import com.acuity.visualisations.sdtm.entity.SdtmEntityVS;
import com.acuity.visualisations.sdtm.entity.SdtmEntityZE;
import com.acuity.visualisations.sdtm.entity.SdtmKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SdtmDomain {
    AE(true, new String[]{"AD", "IP", "SOCABRV", "MEDDRAV", "AEDESC01", "SAEDTC", "SAEIADTC", "SAEHODTC", "SAEDIDTC", "SAECAUSP", "SAESP"}) {
        public SdtmEntityAE newSdtmEntity() {
            return new SdtmEntityAE();
        }
    },

    CE(false, null) {
        public SdtmEntityCE newSdtmEntity() {
            return new SdtmEntityCE();
        }
    },

    CM(true, new String[]{
            "CXTRTST", "CXBRESP", "NOFRACRY", "VISITDTC",
            "CMGROUP", "ATCCD" // for  GENERAL CONCOMITANT MEDICATION
    }) {
        public SdtmEntityCM newSdtmEntity() {
            return new SdtmEntityCM();
        }
    },

    DM(false, null) {
        public SdtmEntityDM newSdtmEntity() {
            return new SdtmEntityDM();
        }
    },

    DS(true, new String[]{"SD", "DSSPFY"}) {
        public SdtmEntityDS newSdtmEntity() {
            return new SdtmEntityDS();
        }
    },

    DV(false, null) {
        public SdtmEntityDV newSdtmEntity() {
            return new SdtmEntityDV();
        }
    },

    EG(true, new String[]{"EGSPFY"}) {
        public SdtmEntityEG newSdtmEntity() {
            return new SdtmEntityEG();
        }
    },

    EX(true, new String[]{"EXACN", "EXADJDSC"}) {
        public SdtmEntityEX newSdtmEntity() {
            return new SdtmEntityEX();
        }
    },

    FA(false, null) {
        public SdtmEntityFA newSdtmEntity() {
            return new SdtmEntityFA();
        }
    },

    LB(false, null) {
        public SdtmEntityLB newSdtmEntity() {
            return new SdtmEntityLB();
        }
    },

    RS(true, new String[]{"RSSPFY"}) {
        public SdtmEntityRS newSdtmEntity() {
            return new SdtmEntityRS();
        }
    },

    SV(false, null) {
        public SdtmEntitySV newSdtmEntity() {
            return new SdtmEntitySV();
        }
    },

    TR(true, new String[]{"VISITDTC"}) {
        public SdtmEntityTR newSdtmEntity() {
            return new SdtmEntityTR();
        }
    },

    TU(true, new String[]{"VISITDTC"}) {
        public SdtmEntityTU newSdtmEntity() {
            return new SdtmEntityTU();
        }
    },

    VS(true, new String[]{"MODULE"}) {
        public SdtmEntityVS newSdtmEntity() {
            return new SdtmEntityVS();
        }
    },

    ZE(false, null) {
        public SdtmEntityZE newSdtmEntity() {
            return new SdtmEntityZE();
        }
    },

    MH(false, null) {
        public SdtmEntityMH newSdtmEntity() {
            return new SdtmEntityMH();
        }
    };

    private String mainFile;
    private boolean hasSupplementalData;
    private Set<String> supplementalDataFilter;

    SdtmDomain(boolean hasSupplementalData, String[] suppQnamFilters) {
        this.mainFile = name().toLowerCase();

        this.hasSupplementalData = hasSupplementalData;
        if (suppQnamFilters != null) {
            this.supplementalDataFilter = new HashSet<>(Arrays.asList(suppQnamFilters));
        }
    }

    public abstract SdtmEntity newSdtmEntity();

    public boolean hasSupplementalData() {
        return hasSupplementalData;
    }

    public static String getSuppFile(SdtmDomain givenDomain, String fileForGivenDomain) {
        return fileForGivenDomain.replace("/" + givenDomain.mainFile + ".", "/supp" + givenDomain.mainFile + ".");
    }

    public static String getSiblingFile(SdtmDomain givenDomain, String fileForGivenDomain, SdtmDomain targetDomain) {
        return fileForGivenDomain.replace("/" + givenDomain.mainFile + ".", "/" + targetDomain.mainFile + ".");
    }

    public static SdtmDomain resolveByFile(String file) {
        StringJoiner domains = new StringJoiner("|");
        for (SdtmDomain sdtmDomain : SdtmDomain.values()) {
            domains.add(sdtmDomain.mainFile);
        }
        Pattern pattern = Pattern.compile(".+/(" + domains.toString() + ")\\.(csv|sas7bdat)");
        Matcher matcher = pattern.matcher(file);
        if (matcher.find()) {
            return SdtmDomain.valueOf(matcher.group(1).toUpperCase());
        }
        return null;
    }

    public SdtmData readMainFile(String mainFile, DataProvider dataProvider) {
        SdtmData<SdtmEntity> sdtmData = new SdtmData<>(this);

        try (TableReader reader = new FileTypeAwareTableReader(mainFile, dataProvider)) {
            Optional<TableRow> optional = reader.nextRow();
            while (optional.isPresent()) {
                TableRow row = optional.get();
                String studyid = (String) row.getValue("STUDYID");
                String usubjid = (String) row.getValue("USUBJID");

                if (usubjid == null) {
                    continue;
                }

                SdtmKey sdtmKey = new SdtmKey(studyid, usubjid);
                SdtmEntity sdtmEntity = newSdtmEntity();
                sdtmEntity.read(row);

                sdtmData.add(sdtmKey, sdtmEntity);

                optional = reader.nextRow();
            }
        }
        return sdtmData;
    }

    public SdtmSuppData readSuppFile(String suppFile, DataProvider dataProvider) {
        SdtmSuppData sdtmSupplementalData = new SdtmSuppData();
        try (TableReader reader = new FileTypeAwareTableReader(suppFile, dataProvider)) {
            Optional<TableRow> optional = reader.nextRow();
            while (optional.isPresent()) {
                TableRow row = optional.get();
                String usubjid = (String) row.getValue("USUBJID");
                String idvarval = (String) row.getValue("IDVARVAL");
                String qnam = (String) row.getValue("QNAM");
                String qval = (String) row.getValue("QVAL");

                if (!supplementalDataFilter.contains(qnam)) {
                    continue;
                }

                SdtmKey sdtmKey = new SdtmKey(usubjid);
                sdtmSupplementalData.add(sdtmKey, idvarval, qnam, qval);

                optional = reader.nextRow();
            }
        }
        return sdtmSupplementalData;
    }
}
