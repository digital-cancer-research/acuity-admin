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
import com.acuity.visualisations.model.aml.Conmed;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository("amlConmedDao")
public class ConmedDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<Conmed> getByStudyId(String studyId) {
        String query = "SELECT DISTINCT cms_id, cms_pat_id, cms_start_date,  cms_end_date "
                + "FROM result_conmed_schedule con "
                + "INNER JOIN result_patient ON cms_pat_id = pat_id "
                + "INNER JOIN result_study ON pat_std_id = std_id "
                + "WHERE std_name = ?";
        return Optional.ofNullable(getJdbcTemplate()
                .query(query,
                        (rs, i) -> Conmed.builder()
                                .id(rs.getString("cms_id"))
                                .studyId(studyId)
                                .subject(rs.getString("cms_pat_id"))
                                .startDate(rs.getDate("cms_start_date"))
                                .endDate(rs.getDate("cms_end_date"))
                                .build(),
                        studyId))
                .orElse(emptyList());
    }

}
