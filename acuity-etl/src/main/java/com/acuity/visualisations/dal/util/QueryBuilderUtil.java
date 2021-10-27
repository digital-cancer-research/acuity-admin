package com.acuity.visualisations.dal.util;

import com.acuity.visualisations.data.util.Util;

import java.util.List;
import java.util.Map;

public final class QueryBuilderUtil {

    private QueryBuilderUtil() {
    }

    public static String buildSelectHashesQuery(String tableName, List<String> hashesColumns, List<JoinDeclaration> joinChain,
                                                List<TableField> fieldValues) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (String hashColumnName : hashesColumns) {
            sb.append(hashColumnName);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(" from ");
        sb.append(tableName);
        if (!fieldValues.isEmpty()) {
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(" where ");
            for (JoinDeclaration declaration : joinChain) {
                sb.append(", ");
                sb.append(declaration.getTargetEntity());
                sb.append(" ");
                for (Map.Entry<String, String> joinColumns : declaration.getColumnsToJoin().entrySet()) {
                    whereClause.append(joinColumns.getKey());
                    whereClause.append("=");
                    whereClause.append(joinColumns.getValue());
                    whereClause.append(" and ");
                }
            }
            for (TableField fieldValue : fieldValues) {
                whereClause.append(fieldValue.getField());
                whereClause.append("= ?");
                whereClause.append(" and ");
            }
            whereClause.setLength(whereClause.length() - 5);
            sb.append(whereClause);
        }
        return sb.toString();
    }

    public static String buildUpdateQuery(String entityTable, List<TableField> fieldsToSet, List<TableField> whereFields) {
        StringBuilder predicate = new StringBuilder();
        predicate.append("update ");
        predicate.append(entityTable);
        StringBuilder setClause = new StringBuilder();
        setClause.append(" set ");
        for (TableField field : fieldsToSet) {
            setClause.append(field.getField());
            setClause.append("=?");
            setClause.append(", ");
        }
        setClause.setLength(setClause.length() - 2);
        StringBuilder whereClause = new StringBuilder();
        if (!whereFields.isEmpty()) {
            whereClause.append(" where ");
            for (TableField field : whereFields) {
                whereClause.append(field.getField());
                whereClause.append("=?");
                whereClause.append(" and ");
            }
            whereClause.setLength(whereClause.length() - 5);
        }
        return predicate.append(setClause).append(whereClause).toString();
    }

    public static String buildInsertQuery(String entityTable, List<TableField> fieldsToInsert) {
        StringBuilder predicate = new StringBuilder();
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValues = new StringBuilder();

        predicate.append("insert into ");
        predicate.append(entityTable);
        if (!fieldsToInsert.isEmpty()) {
            columnNames.append(" (");
            columnValues.append("values (");
            for (TableField field : fieldsToInsert) {
                columnNames.append(field.getField());
                columnNames.append(", ");
                if (Util.isEmpty(field.getValue())) {
                    columnValues.append("?, ");
                } else {
                    columnValues.append(field.getValue() + ", ");
                }
            }
            columnNames.setLength(columnNames.length() - 2);
            columnNames.append(") ");
            columnValues.setLength(columnValues.length() - 2);
            columnValues.append(")");
        }
        return predicate.append(columnNames).append(columnValues).toString();
    }

}
