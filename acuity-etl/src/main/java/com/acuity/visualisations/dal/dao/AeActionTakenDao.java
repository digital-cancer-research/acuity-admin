package com.acuity.visualisations.dal.dao;

import com.acuity.visualisations.dal.EntityDao;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.AeActionTaken;
import java.util.Collections;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class AeActionTakenDao extends EntityDao<AeActionTaken> {
    private static final String AEAT_ID = "AEAT_ID";
    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());

        fieldsToInsert.add(fieldBuilder.setField(AEAT_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("AEAT_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("AEAT_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AEAT_DATE_UPDATED").build());

        fieldsToInsert.add(fieldBuilder.setField("AEAT_ACTION_TAKEN").build());

        fieldsToInsert.add(fieldBuilder.setField("AEAT_AES_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("AEAT_DRUG_ID").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("AEAT_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("AEAT_ACTION_TAKEN").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(AEAT_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, AeActionTaken entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getActionTaken());

        ps.setString(paramIndex++, entity.getAeSeverityGuid());
        ps.setString(paramIndex, entity.getDrugGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, AeActionTaken entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getActionTaken());
        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return AEAT_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_AE_ACTION_TAKEN";
    }

    @Override
    public String getTablePrefix() {
        return "AEAT";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        return "select AEAT_UNQ_SHA1, AEAT_SEC_HASH, AEAT_ID "
                + "from RESULT_AE_ACTION_TAKEN "
                + "inner join RESULT_AE_SEVERITY on AES_ID = AEAT_AES_ID "
                + "inner join RESULT_AE on AE_ID = AES_AE_ID "
                + "inner join RESULT_PATIENT on PAT_ID = AE_PAT_ID "
                + "where PAT_STD_ID = ?";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}
