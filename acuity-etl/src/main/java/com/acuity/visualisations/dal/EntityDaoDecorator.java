package com.acuity.visualisations.dal;

import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.exception.EntityDaoException;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDaoDecorator<T extends OutputEntity> extends BasicEntityDaoDecorator<T> implements IEntityDao<T> {

    private IEntityDao<T> entityDao;

    public EntityDaoDecorator(IEntityDao<T> entityDao) {
        super(entityDao);
        this.entityDao = entityDao;
    }

    @Override
    public Map<OctetString, RowParameters> findHash(String studyId, Class<?> entityClass) {
        try {
            return entityDao.findHash(studyId, entityClass);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    @Override
    public Map<OctetString, RowParameters> findHash(String studyId, Map<OctetString, RowParameters> hashes, Class<?> entityClass) {
        try {
            return entityDao.findHash(studyId, hashes, entityClass);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    @Override
    public HashMap<String, String> findIdsByHash(List<String> hashList) {
        try {
            return entityDao.findIdsByHash(hashList);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    @Override
    public HashMap<String, String> findIdsByRefHash(List<String> hashList) {
        try {
            return entityDao.findIdsByRefHash(hashList);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }
    }

    @Override
    public void updateState(List<T> entities) {
        try {
            entityDao.updateState(entities);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }

    }

    @Override
    public void deleteByIds(List<String> ids) {
        try {
            entityDao.deleteByIds(ids);
        } catch (DataAccessException e) {
            throw new EntityDaoException(getErrorMessage(), e);
        }

    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return entityDao.getSubjectsIdsByStudyName(studyName);
    }

}
