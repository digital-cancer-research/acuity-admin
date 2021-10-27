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
import com.acuity.visualisations.mapping.dao.IFileTypeDao;
import com.acuity.visualisations.mapping.entity.FileType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FileTypeDao extends ACUITYDaoSupport implements IFileTypeDao {

	private static final class FileTypeMapper implements RowMapper<FileType> {
		@Override
		public FileType mapRow(ResultSet rs, int rowNum) throws SQLException {
			FileType fileType = new FileType();
			fileType.setId(rs.getLong("MFT_ID"));
			fileType.setType(rs.getString("MFT_NAME"));
			fileType.setDelimiter(rs.getString("MFT_DELIMITER"));
			return fileType;
		}
	}

	public List<FileType> selectAll() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName());
            return ps;
        }, new FileTypeMapper());
	}

	private String getTableName() {
		return "MAP_FILE_TYPE";
	}

}
