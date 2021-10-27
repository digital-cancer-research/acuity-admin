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
import com.acuity.visualisations.model.aml.Ecg;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository("amlEcgDao")
public class EcgDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<Ecg> getByStudyIdWithQtMeasurement(String studyId) {
        String query = "SELECT EG_ID, PAT_ID, TST_DATE,  EG_TEST_RESULT, TST_VISIT "
                + "FROM result_eg "
                + "LEFT JOIN result_test ON tst_id = eg_tst_id "
                + "INNER JOIN result_patient ON pat_id = tst_pat_id "
                + "INNER JOIN result_study ON std_id = pat_std_id "
                + "WHERE std_name = ? AND eg_test_name='Summary (Mean) QT Duration' AND TST_DATE IS NOT NULL";
        return Optional.ofNullable(getJdbcTemplate()
                .query(query,
                        (rs, i) -> Ecg.builder()
                                .id(rs.getString("EG_ID"))
                                .studyId(studyId)
                                .subject(rs.getString("PAT_ID"))
                                .qtInterval(rs.getDouble("EG_TEST_RESULT"))
                                .visitNumber(rs.getDouble("TST_VISIT"))
                                .date(rs.getDate("TST_DATE"))
                                .build(),
                        studyId))
                .orElse(emptyList());
    }

}
