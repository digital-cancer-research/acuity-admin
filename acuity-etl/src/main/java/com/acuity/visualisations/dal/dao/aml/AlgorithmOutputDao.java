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
import com.acuity.visualisations.model.aml.AlgorithmOutcome;
import com.acuity.visualisations.model.aml.EventType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.acuity.visualisations.data.util.Util.currentDate;
import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;
import static java.util.Collections.emptyList;

@Repository
public class AlgorithmOutputDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<AlgorithmOutcome> getByEventIds(Set<String> eventIds) {
        String query = "SELECT * FROM RESULT_ALGORITHM_OUTCOMES WHERE AO_EVENT_ID IN (:eventIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("eventIds", eventIds);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(getJdbcTemplate());
        return Optional.ofNullable(template
                .query(query, parameters,
                        (rs, i) -> AlgorithmOutcome.builder()
                                .id(rs.getString("AO_ID"))
                                .eventId(rs.getString("AO_EVENT_ID"))
                                .eventType(EventType.valueOf(rs.getString("AO_EVENT_TYPE")))
                                .sourceId(rs.getString("AO_SRC_ID"))
                                .result(rs.getString("AO_ID"))
                                .createdDate(rs.getDate("AO_DATE_CREATED"))
                                .updatedDate(rs.getDate("AO_DATE_UPDATED"))
                                .build()
                ))
                .orElse(emptyList());
    }

    @Transactional
    public int[] save(List<AlgorithmOutcome> outputs) {
        String query = "INSERT "
                + "INTO RESULT_ALGORITHM_OUTCOMES "
                + "(AO_ID, AO_EVENT_ID, AO_EVENT_TYPE, AO_SRC_ID, AO_RESULT, AO_DATE_CREATED, AO_DATE_UPDATED) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                AlgorithmOutcome output = outputs.get(i);
                preparedStatement.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
                preparedStatement.setString(2, output.getEventId());
                preparedStatement.setString(3, output.getEventType().name());
                preparedStatement.setString(4, output.getSourceId());
                preparedStatement.setString(5, output.getResult());
                Timestamp currentDate = getSQLTimestamp(currentDate());
                preparedStatement.setTimestamp(6, currentDate);
                preparedStatement.setTimestamp(7, currentDate);
            }

            @Override
            public int getBatchSize() {
                return outputs.size();
            }
        });
    }

}
