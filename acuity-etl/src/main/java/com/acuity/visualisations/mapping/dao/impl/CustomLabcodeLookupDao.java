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

import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.mapping.dao.ICustomLabcodeLookupDao;
import com.acuity.visualisations.mapping.entity.CustomLabcodeLookup;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by knml167 on 13/03/14.
 */
@Repository
public class CustomLabcodeLookupDao extends BasicDynamicEntityDao<CustomLabcodeLookup> implements ICustomLabcodeLookupDao {

	private static final String MAP_CUSTOM_LABCODE_LOOKUP_TABLE = "MAP_CUSTOM_LABCODE_LOOKUP";
	private static final String ID_COLUMN = "CLL_ID";

	@Override
	protected void prepareStatementToInsert(PreparedStatement ps, CustomLabcodeLookup entity) throws SQLException {
		int paramIndex = 1;
		ps.setString(paramIndex++, entity.getLabcode());
		ps.setString(paramIndex++, entity.getTestName());
		ps.setString(paramIndex++, entity.getSampleName());
		ps.setLong(paramIndex++, entity.getStudyRuleId());
	}

	@Override
	protected String getInsertStatement() {
		String targetTable = MAP_CUSTOM_LABCODE_LOOKUP_TABLE;
		List<TableField> fieldsToInsert = new ArrayList<TableField>();
		TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
		fieldsToInsert.add(fieldBuilder.setField(ID_COLUMN).setValue("nextval('cll_seq')").build());
		fieldBuilder = new TableFieldBuilder(targetTable);
		fieldsToInsert.add(fieldBuilder.setField("CLL_LABCODE").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_TEST_NAME").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_SAMPLE_NAME").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_MSR_ID").build());
		return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
	}

	@Override
	protected void prepareStatementToUpdate(PreparedStatement ps, CustomLabcodeLookup entity) throws SQLException {
		int paramIndex = 1;
		ps.setString(paramIndex++, entity.getLabcode());
		ps.setString(paramIndex++, entity.getTestName());
		ps.setString(paramIndex++, entity.getSampleName());
		ps.setLong(paramIndex++, entity.getStudyRuleId());
		ps.setLong(paramIndex++, entity.getId());
	}

	@Override
	protected String getUpdateStatement() {
		String targetTable = MAP_CUSTOM_LABCODE_LOOKUP_TABLE;
		List<TableField> fieldsToInsert = new ArrayList<TableField>();
		List<TableField> whereFields = new ArrayList<TableField>();
		TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
		fieldsToInsert.add(fieldBuilder.setField("CLL_LABCODE").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_TEST_NAME").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_SAMPLE_NAME").build());
		fieldsToInsert.add(fieldBuilder.setField("CLL_MSR_ID").build());
		whereFields.add(fieldBuilder.setField(ID_COLUMN).build());
		return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
	}

	@Override
	protected String getIdColumnName() {
		return ID_COLUMN;
	}

	public List<CustomLabcodeLookup> getLabCodesByStudyRule(long studyRuleId) {
		String sql = "select * from MAP_CUSTOM_LABCODE_LOOKUP where CLL_MSR_ID=?";
		return getJdbcTemplate().query(sql, ROW_MAPPER, studyRuleId);
	}

	public void deleteAllLabCodesByStudyRule(long studyRuleId) {
		String sql = "delete from MAP_CUSTOM_LABCODE_LOOKUP where CLL_MSR_ID=?";
		getJdbcTemplate().update(sql, studyRuleId);
	}

	private static final RowMapper<CustomLabcodeLookup> ROW_MAPPER = (rs, rowNum) -> {
        CustomLabcodeLookup out = new CustomLabcodeLookup();
        out.setId(rs.getLong("CLL_ID"));
        out.setLabcode(rs.getString("CLL_LABCODE"));
        out.setTestName(rs.getString("CLL_TEST_NAME"));
        out.setSampleName(rs.getString("CLL_SAMPLE_NAME"));
        out.setStudyRuleId(rs.getLong("CLL_MSR_ID"));
        return out;
    };

	public void insertLabCodes(final List<CustomLabcodeLookup> labcodes) {
		String sql = "insert into MAP_CUSTOM_LABCODE_LOOKUP " + "(CLL_ID, CLL_LABCODE, CLL_TEST_NAME, CLL_SAMPLE_NAME, CLL_MSR_ID) "
				+ "values (nextval('cll_seq'),?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, labcodes.get(i).getLabcode());
				ps.setString(2, labcodes.get(i).getTestName());
				ps.setString(3, labcodes.get(i).getSampleName());
				ps.setLong(4, labcodes.get(i).getStudyRuleId());
			}

			public int getBatchSize() {
				return labcodes.size();
			}
		});
	}
}
