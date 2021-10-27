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
