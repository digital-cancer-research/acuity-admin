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

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class DictinaryDao {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    protected JdbcTemplate jdbc;
    protected NamedParameterJdbcTemplate npjdbc;

    @PostConstruct
    public final void init() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.setFetchSize(300);
        npjdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    public enum Table {
        result_event_type,
        result_laboratory,
        util_labcode_lookup
    }

    public enum Field {
        evt_pt,
        lab_code,
        lcl_test_name,
        lcl_sample_name
    }

    public List<String> search(Table table, Field field, String term) {
        String sql = "select distinct " + field + " from " + table;
        if (term == null) {
            sql += " where " + field + " is not null order by 1";
            return jdbc.queryForList(sql, String.class);
        } else {
            sql += " where upper(" + field + ") LIKE '%'||upper(?)||'%' order by 1";
            return jdbc.queryForList(sql, String.class, term);
        }
    }

    public List<TermCount> count(Table table, Field field, List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = "select " + field + ", count(1) from " + table + " where " + field + " in (:in) group by " + field;

        MapSqlParameterSource params = new MapSqlParameterSource("in", terms);
        List<TermCount> result = npjdbc.query(sql, params, ROW_MAPPER);

        List<DictinaryDao.TermCount> out = new ArrayList<>();
        t:
        for (String term : terms) {
            for (DictinaryDao.TermCount termCount : result) {
                if (term.equals(termCount.term)) {
                    out.add(termCount);
                    continue t;
                }
            }
            out.add(new DictinaryDao.TermCount(term, 0));
        }
        return out;
    }

    @Getter
    @Setter
    public static class TermCount {

        private String term;
        private int count;

        public TermCount(String term, int count) {
            this.term = term;
            this.count = count;
        }
    }

    private static final RowMapper<TermCount> ROW_MAPPER = (rs, rowNum) -> new TermCount(rs.getString(1), rs.getInt(2));

}
