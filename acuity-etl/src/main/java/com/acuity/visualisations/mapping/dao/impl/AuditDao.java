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
import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.dao.IAuditDao;
import com.acuity.visualisations.mapping.entity.Audit;
import com.acuity.visualisations.mapping.entity.AuditEntity;
import com.acuity.visualisations.util.Pair;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by knml167 on 20/06/2014.
 */
@Repository
public class AuditDao extends ACUITYDaoSupport implements IAuditDao {
    public static final String INSERT_SQL = ""
            + "INSERT INTO MAP_AUDIT ("
            + "MAU_ID, MAU_USER, MAU_ACTION, MAU_ENTITY, MAU_OBJECT_ID, MAU_OBJECT_NAME, MAU_COMMENT) "
            + "VALUES (nextval('mau_seq'), ?, ?, ?, ?, ?, ?)";

    @Override
    public void logAction(String user, AuditAction action, AuditEntity entity,
                          Long objectId, String objectName, String comment) {
        getJdbcTemplate().update(INSERT_SQL, user, action.name(), entity.name(), objectId, objectName, comment);
    }


    @Override
    public List<Audit> getActions(int limit, int offset, Audit.Field sortBy, boolean sortReverse) {
        StringBuilder sb = new StringBuilder("select * from\n")
                .append("(select t.* from\n");

        if (sortBy == null || sortBy == Audit.Field.timestamp) {
            sb
                    .append("  (select * from map_audit order by MAU_TIMESTAMP")
                    .append(sortReverse ? " DESC" : "")
                    .append(") t\n");
        } else {
            sb
                    .append("  (select * from map_audit order by upper(")
                    .append(sortBy.getColumn())
                    .append(sortReverse ? ") DESC" : ")")
                    .append(", MAU_TIMESTAMP DESC) t\n");
        }

        sb.append("  limit ?) as a\n")
                .append("offset ?");
        return getJdbcTemplate().query(sb.toString(), MAPPER, limit + offset, offset);
    }

    @Override
    public List<Audit> searchActions(int offset, int limit, Audit audit) {
        return getJdbcTemplate().query(prepareSearchQuery(offset, limit, audit), MAPPER);
    }

    @Override
    public int getTotalActions(Audit audit) {
        return getJdbcTemplate().query(prepareCountQuery(audit), (rs, rowNum) -> rs.getInt("cnt")).stream().findAny().orElse(0);
    }

    @Override
    public int getTotalActions() {
        return getJdbcTemplate().queryForObject("select count(1) from map_audit", Integer.class);
    }

    private PreparedStatementCreator prepareCountQuery(Audit audit) {
        return con -> {
            final Pair<StringBuilder, List<Object>> conditionsParams = getQueryConditions(audit);
            StringBuilder conditions = conditionsParams.getA();
            List<Object> params = conditionsParams.getB();
            StringBuilder query = new StringBuilder(""
                    + "select count(1) as cnt from map_audit");
            if (conditions.length() > 0) {
                query = query.append(" where ").append(conditions.substring(4));
            }
            return prepareStatement(con, query.toString(), params);
        };
    }

    private PreparedStatementCreator prepareSearchQuery(int offset, int limit, Audit audit) {
        return con -> {
            final Pair<StringBuilder, List<Object>> conditionsParams = getQueryConditions(audit);
            StringBuilder conditions = conditionsParams.getA();
            List<Object> params = conditionsParams.getB();
            params.add(offset);
            params.add(limit);
            StringBuilder query = new StringBuilder("SELECT * FROM map_audit ");
            if (conditions.length() > 0) {
                query = query.append(" where ").append(conditions.substring(4));
            }
            query = query.append(" ORDER by MAU_TIMESTAMP DESC  offset ?  limit ? ");
            return prepareStatement(con, query.toString(), params);
        };
    }


    private PreparedStatement prepareStatement(Connection con, String query, List<Object> params) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        IntStream
                .range(0, params.size())
                .forEach(i -> {
                    try {
                        statement.setObject(i + 1, params.get(i));
                    } catch (SQLException ignored) {
                    }
                });
        return statement;
    }

    private Pair<StringBuilder, List<Object>> getQueryConditions(Audit audit) {
        StringBuilder conditions = new StringBuilder();
        List<Object> params = new ArrayList<>();
        if (audit.getAction() != null) {
            conditions = conditions.append(" AND MAU_ACTION=? ");
            params.add(audit.getAction().name());
        }
        if (audit.getComment() != null) {
            conditions = conditions.append(" AND MAU_COMMENT=? ");
            params.add(audit.getComment());
        }
        if (audit.getResourceId() != null) {
            conditions = conditions.append(" AND MAU_OBJECT_ID=? ");
            params.add(audit.getResourceId());
        }
        if (audit.getResourceName() != null) {
            conditions = conditions.append(" AND MAU_OBJECT_NAME=? ");
            params.add(audit.getResourceName());
        }
        if (audit.getResourceType() != null) {
            conditions = conditions.append(" AND MAU_ENTITY=? ");
            params.add(audit.getResourceType().name());
        }
        if (audit.getUsername() != null) {
            conditions = conditions.append(" AND MAU_USER=? ");
            params.add(audit.getUsername());
        }
        return new Pair<>(conditions, params);
    }

    private final RowMapper<Audit> MAPPER = (rs, rowNum) ->
            Audit.builder()
                    .action(AuditAction.valueOf(rs.getString("MAU_ACTION")))
                    .comment(rs.getString("MAU_COMMENT"))
                    .resourceId(rs.getLong("MAU_OBJECT_ID"))
                    .resourceName(rs.getString("MAU_OBJECT_NAME"))
                    .resourceType(AuditEntity.valueOf(rs.getString("MAU_ENTITY")))
                    .timestamp(rs.getTimestamp("MAU_TIMESTAMP"))
                    .username(rs.getString("MAU_USER"))
                    .build();
}
