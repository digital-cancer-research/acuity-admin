package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IBasicDynamicEntityDao;
import com.acuity.visualisations.mapping.entity.DynamicEntity;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BasicDynamicEntityDao<T extends DynamicEntity> extends ACUITYDaoSupport implements IBasicDynamicEntityDao<T> {

    public Long insert(final T entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = con -> {
            String sql = getInsertStatement();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepareStatementToInsert(ps, entity);
            return ps;
        };
        getJdbcTemplate().update(psc, keyHolder);
        Long id = ((BigDecimal) keyHolder.getKeys().get(getIdColumnName())).longValue();
        entity.setId(id);
        return id;
    }

    public void update(final T entity) {
        PreparedStatementCreator psc = con -> {
            String sql = getUpdateStatement();
            PreparedStatement ps = con.prepareStatement(sql);
            prepareStatementToUpdate(ps, entity);
            return ps;
        };
        getJdbcTemplate().update(psc);
    }

    protected abstract void prepareStatementToInsert(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getInsertStatement();

    protected abstract void prepareStatementToUpdate(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getUpdateStatement();

    protected abstract String getIdColumnName();

}
