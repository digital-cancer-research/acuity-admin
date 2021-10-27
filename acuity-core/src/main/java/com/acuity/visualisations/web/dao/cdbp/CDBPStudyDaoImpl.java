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

package com.acuity.visualisations.web.dao.cdbp;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudyRule.Status;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Profile("cdbp")
@Repository
public class CDBPStudyDaoImpl extends CDBPDaoSupport implements CDBPStudyDao {

    private static final String SEARCH_STUDY_QUERY = "select * from vw_acuity "
            + "where (upper(STUDY) LIKE upper(?) or upper(ACTIVE_SUBSTANCE) LIKE upper(?)) and (dbl_act is null or dbl_act <= getdate())";

    private static final String SELECT_STUDIES_QUERY =
            "select distinct ACTIVITY_CODE from vw_acuity where (dbl_act is null or dbl_act <= getdate())";

    private static final RowMapper<StudyRule> ROW_MAPPER = (rs, rowNum) -> {
        StudyRule std = new StudyRule();
        std.setStudyCode(rs.getString("ACTIVITY_CODE"));
        std.setStudyName(rs.getString("STUDY"));
        std.setClinicalStudyId(rs.getString("ACTIVITY_CODE"));
        std.setClinicalStudyName(rs.getString("STUDY"));
        std.setDrugProgramme(rs.getString("ACTIVE_SUBSTANCE"));
        std.setPhase(rs.getString("STUDY_PHASE"));
        std.setType(rs.getString("STUDY_TYPE"));
        std.setDeliveryModel(rs.getString("DELIVERY_MODEL"));
        std.setFirstSubjectInPlanned(rs.getDate("FSI_PLN"));
        std.setDatabaseLockPlanned(rs.getDate("DBL_PLN"));
        std.setBlinding(true);
        std.setRegulatory(true);
        std.setRandomisation(true);
        std.setStatus(Status.notInAcuity);
        std.setPhaseType(StudyRule.PhaseType.EARLY);
        return std;
    };

    @Override
    public void fillSearchQueryWorker(String query, QuerySearchWorker<StudyRule> worker) {
        query = "%" + query + "%";
        worker.setSqlCDBPQuery(SEARCH_STUDY_QUERY);
        worker.setCdbpTemplate(getJdbcTemplate());
        worker.setRowCDBPMapper(ROW_MAPPER);
        worker.setCdbpQueryParams(new Object[]{query, query});
    }

    @Override
    public List<StudyRule> searchExactStudy(String query) {
        return safeCallForList(() -> getJdbcTemplate().query(SEARCH_STUDY_QUERY, new Object[]{query, query}, ROW_MAPPER),
                Collections::emptyList);
    }

    @Override
    public List<String> getStudyCodes() {
        return safeCallForList(() -> getJdbcTemplate()
                        .queryForList("select distinct ACTIVITY_CODE from vw_acuity where (dbl_act is null or dbl_act <= getdate())", String.class),
                Collections::emptyList);
    }

    private <T> T safeCallForObject(Supplier<T> supplier, Supplier<T> fallback) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            logger.error(t);
            return fallback.get();
        }
    }

    private <T> List<T> safeCallForList(Supplier<List<T>> supplier, Supplier<List<T>> fallback) {
        return safeCallForObject(supplier, fallback);
    }

}
