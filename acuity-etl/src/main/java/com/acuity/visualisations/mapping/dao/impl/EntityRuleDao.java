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
