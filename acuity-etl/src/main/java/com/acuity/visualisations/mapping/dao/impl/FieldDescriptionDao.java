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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IFieldDescriptionDao;
import com.acuity.visualisations.mapping.entity.FieldDescription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FieldDescriptionDao extends ACUITYDaoSupport implements IFieldDescriptionDao {

    public List<FieldDescription> selectAll() {
        return getJdbcTemplate().query("SELECT * FROM MAP_FIELD_DESCRIPTION", (rs, rowNum) -> {
            FieldDescription fieldDescription = new FieldDescription();
            fieldDescription.setId(rs.getLong("MFID_ID"));
            fieldDescription.setText(rs.getString("MFID_TEXT"));
            return fieldDescription;
        });
    }
}
