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

package com.acuity.visualisations.batch.sdtm;


import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.model.output.entities.AdverseEvent;
import com.acuity.visualisations.sdtm.SdtmEntityMapper;
import com.acuity.visualisations.sdtm.SdtmSuppData;
import com.acuity.visualisations.sdtm.entity.SdtmEntityAE;
import com.acuity.visualisations.sdtm.entity.SdtmEntityFA;
import com.acuity.visualisations.sdtm.entity.SdtmKey;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SdtmEntityMapperTest {

    private SdtmEntityFA newGradeChangeFaEntity(String FASPID, String FATESTCD, String FAORRES, String FADTC) {
        SdtmEntityFA fa = new SdtmEntityFA();
        fa.setFaspid(FASPID);
        fa.setFatestcd(FATESTCD);
        fa.setFaorres(FAORRES);
        fa.setFastresc(FAORRES);
        fa.setFadtc(FADTC);
        return fa;
    }

    /**
     * Based on Natalie's "AERAW mapping example.xlsx" and "AZ Oncology AE (AERAW) mapping v2.xls"
     */
    @Test
    public void shouldExtractAtionsTakenForAdverseEvent() throws InvalidDataFormatException {
        SdtmEntityMapper mapper = new SdtmEntityMapper();

        //record from ae.file
        SdtmEntityAE aeSdtmEntity = new SdtmEntityAE();

        aeSdtmEntity.setSeq("101");
        aeSdtmEntity.setAespid("AE No 1");
        aeSdtmEntity.setAeterm("Migraine");
        aeSdtmEntity.setAetoxgr("3");
        aeSdtmEntity.setAestdtc("10/10/2010");
        aeSdtmEntity.setAeendtc("10/14/2010");

        //key for this record
        SdtmKey sdtmKey = new SdtmKey("xx", "xx/E001001");

        //Drugs from fa.file
        SdtmSuppData sdtmSuppData = new SdtmSuppData();
        sdtmSuppData.add(sdtmKey, "101", "IP", "XXX");
        sdtmSuppData.add(sdtmKey, "101", "AD", "YYY");

        //Grade changes from the fa.file
        Map<SdtmKey, List<SdtmEntityFA>> sdtmFaData = new HashMap<>();

        SdtmEntityFA ipAction0 = newGradeChangeFaEntity("AE No 1", "IPACTION", "Dose Not Changed", "10/10/2010");
        SdtmEntityFA ipAction1 = newGradeChangeFaEntity("AE No 1", "IPACTION", "Dose Reduced", "10/11/2010");
        SdtmEntityFA ipAction2 = newGradeChangeFaEntity("AE No 1", "IPACTION", "Dose Interrupted", "10/13/2010");

        SdtmEntityFA adAction0 = newGradeChangeFaEntity("AE No 1", "ADACTION", "Dose Not Changed", "10/10/2010");
        SdtmEntityFA adAction1 = newGradeChangeFaEntity("AE No 1", "ADACTION", "Dose Not Changed", "10/11/2010");
        SdtmEntityFA adAction2 = newGradeChangeFaEntity("AE No 1", "ADACTION", "Dose Reduced", "10/13/2010");

        SdtmEntityFA gradeChange0 = newGradeChangeFaEntity("AE No 1", "CTCGRADE", "1", "10/10/2010");
        SdtmEntityFA gradeChange1 = newGradeChangeFaEntity("AE No 1", "CTCGRADE", "2", "10/11/2010");
        SdtmEntityFA gradeChange2 = newGradeChangeFaEntity("AE No 1", "CTCGRADE", "3", "10/13/2010");

        sdtmFaData.put(sdtmKey, Arrays.asList(
                ipAction0, ipAction1, ipAction2,
                adAction0, adAction1, adAction2,
                gradeChange0, gradeChange1, gradeChange2));

        AdverseEvent result = mapper.mapAdverseEvent(aeSdtmEntity, sdtmKey, sdtmFaData, sdtmSuppData);

        assertThat(result).isNotNull();

        assertThat(result.getAeText()).isEqualTo("Migraine");

        assertThat(result.getIpDrugs()).containsOnly("XXX");
        assertThat(result.getAdDrugs()).containsOnly("YYY");

        assertThat(result.getStartingCtcGrade()).isEqualTo("1");
        assertThat(result.getCtcGradeChanges()).containsExactly("2", "3");

        assertThat(result.getInitialActionTakenForIpDrugs()).containsOnly("Dose Not Changed");
        assertThat(result.getInitialActionTakenForAdDrugs()).containsOnly("Dose Not Changed");

        assertThat(result.getChangedActionTakenForIpDrugs()).containsExactly("Dose Reduced", "Dose Interrupted");
        assertThat(result.getChangedActionTakenForAdDrugs()).containsExactly("Dose Not Changed", "Dose Reduced");

    }
}
