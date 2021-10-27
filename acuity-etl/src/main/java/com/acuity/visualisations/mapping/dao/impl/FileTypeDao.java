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
