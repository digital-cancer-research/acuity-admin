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

package com.acuity.visualisations.dal.dao;

import com.acuity.visualisations.dal.EntityDao;
import com.acuity.visualisations.model.output.OutputEntity;
import org.springframework.jdbc.core.CallableStatementCreator;

import java.sql.CallableStatement;
import java.util.Date;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

public abstract class NotCumulativeEntityDao<T extends OutputEntity> extends EntityDao<T> {
    public void updateFK(final String projectName, final String studyCode, final Date jobStartDate) {
        getJdbcTemplate().execute((CallableStatementCreator) con -> {
            CallableStatement cs = con.prepareCall("{call " + getUpdateFKProcedureName() + "(?, ?, ?, ?)}");
            cs.setTimestamp(1, getSQLTimestamp(jobStartDate));
            cs.setString(2, getDefaultFK());
            cs.setString(3, projectName);
            cs.setString(4, studyCode);
            return cs;
        }, cs -> {
            cs.execute();
            return null;
        }
        );
    }

    protected abstract String getDefaultFK();

    protected abstract String getUpdateFKProcedureName();

    protected abstract String getStudyIdColumnName();

}
