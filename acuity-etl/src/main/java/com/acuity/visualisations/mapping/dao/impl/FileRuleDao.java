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
import com.acuity.visualisations.mapping.dao.IFileRuleDao;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileStandard;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileRuleDao extends BasicDynamicEntityDao<FileRule> implements IFileRuleDao {
    private static final String DELETE_QUERY = "delete from MAP_FILE_RULE where MFR_ID = ?";
    private static final String MFR_ID = "MFR_ID";
    private static final String MFR_NAME = "MFR_NAME";
    private static final String MFR_ENABLED = "MFR_ENABLED";
    private static final String MFR_FILE_STANDARD = "MFR_FILE_STANDARD";

    private final RowMapper<FileRule> ROW_MAPPER = (rs, rowNum) -> {
        FileRule fileRule = new FileRule();
        fileRule.setId(rs.getLong(MFR_ID));
        fileRule.setName(rs.getString(MFR_NAME));
        fileRule.setFileTypeId(rs.getLong("MFR_MFT_ID"));
        fileRule.setAcuityEnabled(rs.getInt(MFR_ENABLED) == 1);
        if (rs.getString(MFR_FILE_STANDARD) != null) {
            fileRule.setFileStandard(FileStandard.valueOf(rs.getString("mfr_file_standard")));
        }

        return fileRule;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, FileRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getName());
        ps.setLong(paramIndex++, entity.getStudyRule().getId());
        ps.setLong(paramIndex++, entity.getFileType().getId());
        ps.setObject(paramIndex++, entity.isAcuityEnabled() ? 1 : 0);
        ps.setString(paramIndex, entity.getFileStandard() == null ? null : entity.getFileStandard().name());
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_FILE_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(MFR_ID).setValue("nextval('mfr_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(MFR_NAME).build());
        fieldsToInsert.add(fieldBuilder.setField("MFR_MSR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MFR_MFT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField(MFR_ENABLED).build());
        fieldsToInsert.add(fieldBuilder.setField(MFR_FILE_STANDARD).build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getIdColumnName() {
        return MFR_ID;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, FileRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getName());
        ps.setObject(paramIndex++, entity.isAcuityEnabled() ? 1 : 0);
        ps.setString(paramIndex++, entity.getFileStandard() == null ? null : entity.getFileStandard().name());
        ps.setLong(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = "MAP_FILE_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        List<TableField> whereFields = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(MFR_NAME).build());
        fieldsToInsert.add(fieldBuilder.setField(MFR_ENABLED).build());
        fieldsToInsert.add(fieldBuilder.setField(MFR_FILE_STANDARD).build());
        whereFields.add(fieldBuilder.setField(MFR_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
    }

    public List<FileRule> getFileRulesByStudy(long studyId) {
        final String sql = "select * from MAP_FILE_RULE where MFR_MSR_ID = ?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyId);
    }

    @Override
    public FileRule getFileRule(long fileRuleId) {
        final String sql = "select * from MAP_FILE_RULE where MFR_ID=?";
        return getJdbcTemplate().queryForObject(sql, ROW_MAPPER, fileRuleId);
    }

    @Override
    public List<String> getFileRuleNamesByFileDescriptionId(long fileDescriptionId) {
        String sql = "select distinct MFR_NAME from MAP_FILE_RULE "
                + "inner join MAP_DESCRIPTION_FILE on MFR_ID=MDF_MFR_ID "
                + "where MFR_NAME is not null and MDF_MFD_ID=?";
        return getJdbcTemplate().queryForList(sql, String.class, fileDescriptionId);
    }

    public void delete(FileRule fileRule) {
        getJdbcTemplate().update(DELETE_QUERY, new Object[]{fileRule.getId()});
    }

}
