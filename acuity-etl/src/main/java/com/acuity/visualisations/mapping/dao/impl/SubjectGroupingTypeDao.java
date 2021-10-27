package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.ISubjectGroupingTypeDao;
import com.acuity.visualisations.mapping.entity.SubjectGroupingType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author adavliatov.
 * @since 19.07.2016.
 */
@Repository
public class SubjectGroupingTypeDao extends ACUITYDaoSupport implements ISubjectGroupingTypeDao {
    @Override
    public List<SubjectGroupingType> selectAll() {
        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName());
            return ps;
        }, new SubjectGroupingTypeDao.Mapper());
    }

    private String getTableName() {
        return "MAP_SUBJECT_GROUPING_TYPE";
    }

    private static final class Mapper implements RowMapper<SubjectGroupingType> {
        @Override
        public SubjectGroupingType mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubjectGroupingType type = new SubjectGroupingType();
            type.setId(rs.getLong("MSGT_ID"));
            type.setName(rs.getString("MSGT_TYPE"));
            return type;
        }
    }
}
