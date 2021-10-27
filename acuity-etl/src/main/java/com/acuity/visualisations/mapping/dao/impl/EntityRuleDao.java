package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IEntityRuleDao;
import com.acuity.visualisations.mapping.entity.EntityRule;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EntityRuleDao extends ACUITYDaoSupport implements IEntityRuleDao {

	private static final class EntityRuleMapper implements RowMapper<EntityRule> {
		@Override
		public EntityRule mapRow(ResultSet rs, int rowNum) throws SQLException {
			EntityRule entityRule = new EntityRule();
			entityRule.setId(rs.getLong("MEN_ID"));
			entityRule.setName(rs.getString("MEN_NAME"));
			entityRule.setStoreInCache(rs.getBoolean("MEN_STORE_IN_CACHE"));
			entityRule.setGlobalFunction(rs.getString("MEN_GLOBAL_FUNCTION"));
			return entityRule;
		}
	}

	public List<EntityRule> selectAll() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName());
            return ps;
        }, new EntityRuleMapper());
	}

	private String getTableName() {
		return "MAP_ENTITY";
	}
}
