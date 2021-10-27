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
import com.acuity.visualisations.mapping.dao.IAggregationFunctionDao;
import com.acuity.visualisations.mapping.entity.AggregationFunction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AggregationFunctionDao extends ACUITYDaoSupport implements IAggregationFunctionDao {

	private static final class AggregationFunctionMapper implements RowMapper<AggregationFunction> {
		@Override
		public AggregationFunction mapRow(ResultSet rs, int rowNum) throws SQLException {
			AggregationFunction aggregationFunction = new AggregationFunction();
			aggregationFunction.setId(rs.getLong("MAF_ID"));
			aggregationFunction.setName(rs.getString("MAF_NAME"));
			aggregationFunction.setDescription(rs.getString("MAF_DESCRIPTION"));
			aggregationFunction.setHelper(rs.getString("MAF_HELPER"));
			aggregationFunction.setResultType(rs.getString("MAF_RESULT_TYPE"));
			return aggregationFunction;
		}
	}

	public List<AggregationFunction> selectAll() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName());
            return ps;
        }, new AggregationFunctionMapper());
	}

	private String getTableName() {
		return "MAP_AGGR_FUN";
	}
}
