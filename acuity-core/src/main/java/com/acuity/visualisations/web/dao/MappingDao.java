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

package com.acuity.visualisations.web.dao;

import com.acuity.visualisations.web.entity.MappedColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class MappingDao {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    protected JdbcTemplate jdbc;

    @PostConstruct
    public final void init() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.setFetchSize(100);
    }

    public List<MappedColumnInfo> getMappedColumnInfosByFileDescriptionId(long fileDescriptionId) {
        String sql = "select\n"
                + "msr_prj_id, mfr_id, mcr_name, mfr_name\n"
                + "from map_column_rule\n"
                + "inner join map_mapping_rule on mcr_mmr_id=mmr_id\n"
                + "inner join map_file_rule on mmr_mfr_id=mfr_id\n"
                + "inner join map_study_rule on mfr_msr_id=msr_id\n"
                + "inner join map_description_file on mfr_id=mdf_mfr_id\n"
                + "inner join map_file_description on mfd_id=mdf_mfd_id\n"
                + "where\n"
                + "mfd_id=?\n"
                + "and mfr_name is not null and msr_status='mapped'";
        return jdbc.query(sql, MAPPED_COLUMN_INFO_ROW_MAPPER, fileDescriptionId);
    }

    private static final RowMapper<MappedColumnInfo> MAPPED_COLUMN_INFO_ROW_MAPPER = (rs, rowNum) -> {
        MappedColumnInfo out = new MappedColumnInfo();
        out.setColumnName(rs.getString("mcr_name"));
        out.setFileRuleFilePath(rs.getString("mfr_name"));
        out.setFileRuleId(rs.getLong("mfr_id"));
        out.setProjectId(rs.getLong("msr_prj_id"));
        return out;
    };
}
