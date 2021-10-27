package com.acuity.visualisations.dal.dao.aml;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.model.aml.Ecg;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository("amlEcgDao")
public class EcgDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<Ecg> getByStudyIdWithQtMeasurement(String studyId) {
        String query = "SELECT EG_ID, PAT_ID, TST_DATE,  EG_TEST_RESULT, TST_VISIT "
                + "FROM result_eg "
                + "LEFT JOIN result_test ON tst_id = eg_tst_id "
                + "INNER JOIN result_patient ON pat_id = tst_pat_id "
                + "INNER JOIN result_study ON std_id = pat_std_id "
                + "WHERE std_name = ? AND eg_test_name='Summary (Mean) QT Duration' AND TST_DATE IS NOT NULL";
        return Optional.ofNullable(getJdbcTemplate()
                .query(query,
                        (rs, i) -> Ecg.builder()
                                .id(rs.getString("EG_ID"))
                                .studyId(studyId)
                                .subject(rs.getString("PAT_ID"))
                                .qtInterval(rs.getDouble("EG_TEST_RESULT"))
                                .visitNumber(rs.getDouble("TST_VISIT"))
                                .date(rs.getDate("TST_DATE"))
                                .build(),
                        studyId))
                .orElse(emptyList());
    }

}
