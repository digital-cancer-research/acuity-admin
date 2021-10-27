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
