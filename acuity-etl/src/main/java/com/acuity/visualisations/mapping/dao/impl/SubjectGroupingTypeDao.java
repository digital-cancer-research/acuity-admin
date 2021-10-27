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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.ISubjectGroupingTypeDao;
import com.acuity.visualisations.mapping.entity.SubjectGroupingType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author adavliatov.
 * @since 19.07.2016.
 */
@Repository
public class SubjectGroupingTypeDao extends ACUITYDaoSupport implements ISubjectGroupingTypeDao {
    @Override
    public List<SubjectGroupingType> selectAll() {
        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName());
            return ps;
        }, new SubjectGroupingTypeDao.Mapper());
    }

    private String getTableName() {
        return "MAP_SUBJECT_GROUPING_TYPE";
    }

    private static final class Mapper implements RowMapper<SubjectGroupingType> {
        @Override
        public SubjectGroupingType mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubjectGroupingType type = new SubjectGroupingType();
            type.setId(rs.getLong("MSGT_ID"));
            type.setName(rs.getString("MSGT_TYPE"));
            return type;
        }
    }
}
