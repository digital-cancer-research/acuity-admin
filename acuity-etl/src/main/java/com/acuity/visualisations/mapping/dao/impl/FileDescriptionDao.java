package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IFileDescriptionDao;
import com.acuity.visualisations.mapping.entity.FileDescription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileDescriptionDao extends ACUITYDaoSupport implements IFileDescriptionDao {

    public List<FileDescription> selectAll() {
        return getJdbcTemplate().query("SELECT * FROM MAP_FILE_DESCRIPTION ORDER BY MFD_PROCESS_ORDER", (rs, rowNum) -> {
            FileDescription fileDescription = new FileDescription();
            fileDescription.setId(rs.getLong("MFD_ID"));
            fileDescription.setDescription(rs.getString("MFD_NAME"));
            fileDescription.setDisplayName(rs.getString("MFD_DISPLAY_NAME"));
            fileDescription.setSectionId(rs.getLong("MFD_SECTION_ID"));
            fileDescription.setProcessOrder(rs.getDouble("MFD_PROCESS_ORDER"));
            return fileDescription;
        });
    }
}
