package com.acuity.visualisations.dal;

import com.acuity.visualisations.model.output.OutputEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class BasicEntityDao<T extends OutputEntity> extends ACUITYDaoSupport implements IBasicEntityDao<T> {

    public void insert(final T entity) {
        PreparedStatementCreator psc = con -> {
            String sql = getInsertStatement();
            PreparedStatement ps = con.prepareStatement(sql, new String[]{getIdColumnName().toLowerCase()});
            prepareStatementToInsert(ps, entity);
            return ps;
        };
        getJdbcTemplate().update(psc);
    }

    public String getErrorMessage() {
        return "Exception while working with database table " + getTableName() + ".";
    }

    public void batchInsert(final List<T> list) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                prepareStatementToInsert(ps, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        };

        getJdbcTemplate().batchUpdate(getInsertStatement(), bpss);
    }

    public void update(final T entity) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement ps = con.prepareStatement(getUpdateStatement());
            prepareStatementToUpdate(ps, entity);
            return ps;
        };
        getJdbcTemplate().update(psc);
    }

    public void batchUpdate(final List<T> list) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                prepareStatementToUpdate(ps, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        };

        getJdbcTemplate().batchUpdate(getUpdateStatement(), bpss);
    }

    protected abstract String getInsertStatement();

    protected abstract void prepareStatementToInsert(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getIdColumnName();

    protected abstract void prepareStatementToUpdate(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getUpdateStatement();

    protected abstract String getTableName();
}
