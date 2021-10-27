package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.mapping.dao.IStudyBaselineDrugDao;
import com.acuity.visualisations.mapping.entity.StudyBaselineDrug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class StudyBaselineDrugDao implements IStudyBaselineDrugDao {
    private static final RowMapper<StudyBaselineDrug> ROW_MAPPER = (rs, rowNum) -> {
        StudyBaselineDrug out = new StudyBaselineDrug();
        out.setId(rs.getLong("MSBD_ID"));
        out.setDrugName(rs.getString("MSBD_DRUG_NAME"));
        out.setInclude(rs.getBoolean("MSBD_INCLUDE"));
        return out;
    };
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void deleteStudyBaselineDrugs(long studyRuleId) {
        jdbcTemplate.update("DELETE FROM MAP_STUDY_BASELINE_DRUG WHERE MSBD_MSR_ID=?", studyRuleId);
    }

    public List<StudyBaselineDrug> selectStudyBaselineDrugs(long studyRuleId) {
        return jdbcTemplate.query("SELECT * FROM MAP_STUDY_BASELINE_DRUG WHERE MSBD_MSR_ID=?", ROW_MAPPER, studyRuleId);
    }

    public void insertStudyBaselineDrugs(long studyRuleId, List<StudyBaselineDrug> studyBaselineDrugs) {
        List<Object[]> batchParams = studyBaselineDrugs.stream().map(item ->
                new Object[]{studyRuleId, item.getDrugName(), item.isInclude()}
        ).collect(toList());

        jdbcTemplate.batchUpdate("INSERT INTO MAP_STUDY_BASELINE_DRUG "
                + "(MSBD_ID, MSBD_MSR_ID, MSBD_DRUG_NAME, MSBD_INCLUDE) "
                + "VALUES (nextval('MAP_SEQ'), ?, ?, ?)", batchParams);
    }
}
