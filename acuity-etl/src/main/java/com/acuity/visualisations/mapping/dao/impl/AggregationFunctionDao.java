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
