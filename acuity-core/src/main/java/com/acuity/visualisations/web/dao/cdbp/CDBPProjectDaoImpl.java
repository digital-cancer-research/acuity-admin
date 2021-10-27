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
import com.acuity.visualisations.mapping.entity.ProjectRule;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Profile("cdbp")
@Repository
public class CDBPProjectDaoImpl extends CDBPDaoSupport implements CDBPProjectDao {

    private static final String SEARCH_PROJECTS_QUERY = "select COUNT(*) as STUDIES_TOTAL, MIN(ACTIVE_SUBSTANCE) as ACTIVE_SUBSTANCE "
            + "from vw_acuity "
            + "where (dbl_act is null or dbl_act <= getdate()) "
            + "and (upper(ACTIVE_SUBSTANCE) LIKE upper(?) OR upper(PROJECT_CODE) LIKE upper(?) OR upper(ACTIVITY_CODE) LIKE upper(?)) "
            + "group by ACTIVE_SUBSTANCE;";


    private static final String SELECT_PROJECT_TOTAL_STUDY_COUNT = "select COUNT(*) as STUDIES_TOTAL, MIN(ACTIVE_SUBSTANCE) as ACTIVE_SUBSTANCE "
            + "from vw_acuity "
            + "where (dbl_act is null or dbl_act <= getdate()) "
            + "and (upper(ACTIVE_SUBSTANCE) = upper(?)) "
            + "group by ACTIVE_SUBSTANCE;";

    private static final String SELECT_DRUG_PROJECTS_QUERY =
            "select distinct ACTIVE_SUBSTANCE from vw_acuity where (dbl_act is null or dbl_act <= getdate())";

    @Override
    public List<ProjectRule> searchProjects(String query) {
        return getJdbcTemplate().query(SEARCH_PROJECTS_QUERY, new Object[]{query, query, query}, ROW_MAPPER);
    }

    @Override
    public Integer getTotalStudyCountByDrugId(String drugId) {
        List<ProjectRule> resultQuery = getJdbcTemplate().query(SELECT_PROJECT_TOTAL_STUDY_COUNT, new Object[]{drugId}, ROW_MAPPER);
        if (!resultQuery.isEmpty()) {
            return resultQuery.get(0).getTotalStudyCount();
        }
        return 0;
    }

    @Override
    public List<String> getCDBPDrugIds() {
        try {
            List<String> resultQuery = getJdbcTemplate().query(SELECT_DRUG_PROJECTS_QUERY, (resultSet, i) -> resultSet.getString(1));
            return resultQuery;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static final RowMapper<ProjectRule> ROW_MAPPER = (rs, rowNum) -> {
        ProjectRule prj = new ProjectRule();
        prj.setTotalStudyCount(rs.getInt("STUDIES_TOTAL"));
        prj.setDrugId(rs.getString("ACTIVE_SUBSTANCE"));
        return prj;
    };

    @Override
    public void fillSearchQueryWorker(String query, QuerySearchWorker<ProjectRule> queryWorker) {
        queryWorker.setCdbpTemplate(getJdbcTemplate());
        queryWorker.setRowCDBPMapper(ROW_MAPPER);
        queryWorker.setSqlCDBPQuery(SEARCH_PROJECTS_QUERY);
        queryWorker.setCdbpQueryParams(new Object[]{query, query, query});
    }
}
