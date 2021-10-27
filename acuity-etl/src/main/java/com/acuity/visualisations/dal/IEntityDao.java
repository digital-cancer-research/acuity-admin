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

import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IEntityDao<T extends OutputEntity> extends IBasicEntityDao<T> {

    Map<OctetString, RowParameters> findHash(String studyId, Class<?> entityClass);

    Map<OctetString, RowParameters> findHash(String studyId, Map<OctetString, RowParameters> hashes, Class<?> entityClass);

    HashMap<String, String> findIdsByHash(List<String> hashList);

    HashMap<String, String> findIdsByRefHash(List<String> hashList);

    void updateState(List<T> entities);

    void deleteByIds(List<String> ids);

    /**
     * This method is used in data load report table in column 'Subjects-data table'.
     *
     * @param studyName study name
     * @return subjects ids list or empty list in case when table not linked to RESULT_PATIENT table
     * (for instance, RESULT_EVENT_TYPE)
     */
    List<String> getSubjectsIdsByStudyName(String studyName);

}
