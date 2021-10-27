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

package com.acuity.visualisations.report.dao.impl;

import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.report.dao.IDataAlertDao;
import com.acuity.visualisations.report.entity.DataAlert;
import com.acuity.visualisations.report.entity.Report;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("dataAlertDao")
public class DataAlertDao extends BasicReportDao<DataAlert> implements IDataAlertDao {

    private static final String TABLE_NAME = "REPORT_DATA_ALERT";
    private static final String RDA_STD_CODE = "RDA_STD_CODE";

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, DataAlert entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getStudyCode());
        ps.setString(paramIndex++, entity.getDataEntity());
        ps.setString(paramIndex++, entity.getCountSourceFile());
        ps.setString(paramIndex++, entity.getCountDatabase());
        ps.setLong(paramIndex, entity.getJobExecutionId());
    }

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(TABLE_NAME);
        fieldsToInsert.add(fieldBuilder.setField("RDA_ID").setValue("nextval('rda_seq')").build());
        fieldBuilder = new TableFieldBuilder(TABLE_NAME);
        fieldsToInsert.add(fieldBuilder.setField(RDA_STD_CODE).build());
        fieldsToInsert.add(fieldBuilder.setField("RDA_DATA_ENTITY").build());
        fieldsToInsert.add(fieldBuilder.setField("RDA_COUNT_SOURCE_FILE").build());
        fieldsToInsert.add(fieldBuilder.setField("RDA_COUNT_DATABASE").build());
        fieldsToInsert.add(fieldBuilder.setField("RDA_JE_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(TABLE_NAME, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getExecutionIdColumn() {
        return "RDA_JE_ID";
    }

    @Override
    protected String getStudyIdColumn() {
        return RDA_STD_CODE;
    }
    
    @Override
    protected String getSelectFields() {
        return "rda_id, rda_count_database, rda_count_source_file, rda_data_entity, rda_std_code";
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "rda_id";
    }

    @Override
    public List<? extends Report> selectReportData(long jobExecutionId, long lastPrimaryKey) {
        String selectSql =  getSelectStatementForJobExecutionId();

        List<DataAlert> reportRows = new ArrayList<DataAlert>();
        
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(selectSql, jobExecutionId, lastPrimaryKey);

        for (Map<String, Object> row : rows) {
            DataAlert report = new DataAlert();
            report.setPrimaryKey(((BigDecimal) row.get("RDA_ID")).longValue());
            BigDecimal dbCount = (BigDecimal) row.get("RDA_COUNT_DATABASE");
            report.setCountDatabase(dbCount.toString());
            BigDecimal fileCount = (BigDecimal) row.get("RDA_COUNT_SOURCE_FILE");
            report.setCountSourceFile(fileCount.toString());
            report.setDataEntity((String) row.get("RDA_DATA_ENTITY"));
            report.setJobExecutionId(jobExecutionId);
            report.setStudyCode((String) row.get(RDA_STD_CODE));

            reportRows.add(report);
        }

        return reportRows;
    }

}
