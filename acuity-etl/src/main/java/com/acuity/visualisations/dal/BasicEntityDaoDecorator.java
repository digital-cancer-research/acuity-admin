package com.acuity.visualisations.dal;

import com.acuity.visualisations.exception.EntityDaoException;
import com.acuity.visualisations.model.output.OutputEntity;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class BasicEntityDaoDecorator<T extends OutputEntity> implements IBasicEntityDao<T> {

    private IBasicEntityDao<T> basicEntityDao;

    public BasicEntityDaoDecorator(IBasicEntityDao<T> basicEntityDao) {
        this.basicEntityDao = basicEntityDao;
    }

    public void insert(T entity) {
        try {
            basicEntityDao.insert(entity);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    public String getErrorMessage() {
        return basicEntityDao.getErrorMessage();
    }

    public void batchInsert(List<T> list) {
        try {
            basicEntityDao.batchInsert(list);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    public void update(T entity) {
        try {
            basicEntityDao.update(entity);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    public void batchUpdate(List<T> list) {
        try {
            basicEntityDao.batchUpdate(list);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }
}
