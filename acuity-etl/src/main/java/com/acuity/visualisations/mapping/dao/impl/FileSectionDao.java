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
import com.acuity.visualisations.mapping.dao.IFileSectionDao;
import com.acuity.visualisations.mapping.entity.FileSection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileSectionDao extends ACUITYDaoSupport implements IFileSectionDao {

	public List<FileSection> selectAll() {
		return getJdbcTemplate().query("select * from MAP_FILE_SECTION order by MFS_ORDER", (rs, rowNum) -> {
            FileSection fieldRule = new FileSection();
            fieldRule.setId(rs.getLong("MFS_ID"));
            fieldRule.setName(rs.getString("MFS_NAME"));
            return fieldRule;
        });
	}
}
