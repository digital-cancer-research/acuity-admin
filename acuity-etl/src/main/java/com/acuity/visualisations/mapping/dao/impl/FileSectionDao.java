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
