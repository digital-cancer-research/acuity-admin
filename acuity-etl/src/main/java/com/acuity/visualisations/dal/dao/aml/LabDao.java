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

package com.acuity.visualisations.dal.dao.aml;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.model.aml.Lab;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository("amlLabDao")
public class LabDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<Lab> getByStudyIdWithPotassiumMagnesiumFromSerumInPredefinedUnits(String studyId) {
        String query = "SELECT distinct "
                + "LAB_ID, PAT_ID, TST_VISIT, "
                + "COALESCE(lcd_definition, cll_test_name, lcl_test_name, lab_code, 'EMPTY') as LAB_LABCODE, "
                + "LAB_VALUE, LAB_UNIT, TST_DATE "
                + "FROM result_laboratory lab "
                + "INNER JOIN result_test "
                + "ON tst_id=lab_tst_id "
                + "INNER JOIN result_patient pat "
                + "ON pat_id=tst_pat_id "
                + "INNER JOIN result_study std "
                + "ON std_id = pat_std_id "
                + "INNER JOIN map_study_rule "
                + "ON (std_name = msr_study_code) "
                + "LEFT JOIN map_custom_labcode_lookup "
                + "ON (upper(lab_code)       = upper(cll_labcode) "
                + "AND cll_msr_id            = msr_id "
                + "AND msr_use_alt_lab_codes = 1) "
                + "LEFT JOIN util_labcode_lookup "
                + "ON (upper(lab_code) = upper(lcl_labcode)) "
                + "LEFT JOIN util_labcode_synonym "
                + "ON (upper(COALESCE(cll_test_name, lcl_test_name, lab_code)) = upper(lcs_synonym)) "
                + "LEFT JOIN util_labcode_dictionary "
                + "ON (lcs_lcd_id = lcd_id) "
                + "left join UTIL_LABCTCG_LOOKUP lkp on lab_code = lkp.LABCGL_CODE "
                + "    WHERE "
                + "    ( LOWER( COALESCE(lcd_definition, cll_test_name, lcl_test_name, lab_code, 'EMPTY') ) = 'potassium' "
                + "    OR LOWER( COALESCE(lcd_definition, cll_test_name, lcl_test_name, lab_code, 'EMPTY') ) = 'magnesium' ) "
                + "    AND LAB_UNIT IN ('mmol/L', 'mEq/L', 'mg/dL', 'g/dL', 'g/L', 'mg/L') "// allowed units
                + "    AND lab_code IN ('02115', '02113') "// Serum as sample only
                + "    AND std_name = ?";
        return Optional.ofNullable(getJdbcTemplate()
                .query(query,
                        (rs, i) -> Lab.builder()
                                .id(rs.getString("LAB_ID"))
                                .studyId(studyId)
                                .subject(rs.getString("PAT_ID"))
                                .visitNumber(rs.getDouble("TST_VISIT"))
                                .labCode(rs.getString("LAB_LABCODE"))
                                .value(rs.getDouble("LAB_VALUE"))
                                .unit(rs.getString("LAB_UNIT"))
                                .testDate(rs.getDate("TST_DATE"))
                                .build(),
                        studyId))
                .orElse(emptyList());
    }
}
