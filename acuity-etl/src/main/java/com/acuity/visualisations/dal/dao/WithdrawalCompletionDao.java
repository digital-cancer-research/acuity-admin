package com.acuity.visualisations.dal.dao;

import com.acuity.visualisations.dal.EntityDao;
import com.acuity.visualisations.dal.util.JoinDeclaration;
import com.acuity.visualisations.dal.util.JoinDeclarationBuilder;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.WithdrawalCompletion;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class WithdrawalCompletionDao extends EntityDao<WithdrawalCompletion> {
    private static final String RESULT_WITHDRAWAL_COMPLETION = "RESULT_WITHDRAWAL_COMPLETION";

    @Override
    protected String getInsertStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("WC_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("WC_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("WC_WITHDRAWAL_COMPLETION_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_PREMATURELY_WITHDRAWN").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_MAIN_REASON").build());
        fieldsToInsert.add(fieldBuilder.setField("WC_SPECIFICATION").build());

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, WithdrawalCompletion entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getWithdrawalCompletionDate()));
        ps.setString(paramIndex++, entity.getPrematurelyWithdrawn());
        ps.setString(paramIndex++, entity.getMainReason());
        ps.setString(paramIndex, entity.getSpecification());
    }

    @Override
    protected String getIdColumnName() {
        return "WC_ID";
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("WC_DATE_UPDATED").build());

        fieldsToSet.add(fieldBuilder.setField("WC_WITHDRAWAL_COMPLETION_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("WC_PREMATURELY_WITHDRAWN").build());
        fieldsToSet.add(fieldBuilder.setField("WC_MAIN_REASON").build());
        fieldsToSet.add(fieldBuilder.setField("WC_SPECIFICATION").build());

        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("WC_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, WithdrawalCompletion entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getWithdrawalCompletionDate()));
        ps.setString(paramIndex++, entity.getPrematurelyWithdrawn());
        ps.setString(paramIndex++, entity.getMainReason());
        ps.setString(paramIndex++, entity.getSpecification());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    public String getTableName() {
        return RESULT_WITHDRAWAL_COMPLETION;
    }

    @Override
    protected String getTablePrefix() {
        return "WC";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_WITHDRAWAL_COMPLETION).setTargetEntity("RESULT_PATIENT").addColumnToJoin("WC_PAT_ID", "PAT_ID").build());

        List<TableField> fieldValues = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("WC_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_WITHDRAWAL_COMPLETION, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT WC_PAT_ID FROM RESULT_WITHDRAWAL_COMPLETION "
                + "left join RESULT_PATIENT on (WC_PAT_ID = PAT_ID) "
                + "left join RESULT_STUDY on (PAT_STD_ID = STD_ID) "
                + "where STD_NAME = ?", String.class, studyName);
    }
}
