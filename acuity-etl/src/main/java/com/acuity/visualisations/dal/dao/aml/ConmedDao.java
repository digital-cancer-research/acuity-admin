package com.acuity.visualisations.dal.dao.aml;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.model.aml.Conmed;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository("amlConmedDao")
public class ConmedDao extends ACUITYDaoSupport {

    @Transactional(readOnly = true)
    public List<Conmed> getByStudyId(String studyId) {
        String query = "SELECT DISTINCT cms_id, cms_pat_id, cms_start_date,  cms_end_date "
                + "FROM result_conmed_schedule con "
                + "INNER JOIN result_patient ON cms_pat_id = pat_id "
                + "INNER JOIN result_study ON pat_std_id = std_id "
                + "WHERE std_name = ?";
        return Optional.ofNullable(getJdbcTemplate()
                .query(query,
                        (rs, i) -> Conmed.builder()
                                .id(rs.getString("cms_id"))
                                .studyId(studyId)
                                .subject(rs.getString("cms_pat_id"))
                                .startDate(rs.getDate("cms_start_date"))
                                .endDate(rs.getDate("cms_end_date"))
                                .build(),
                        studyId))
                .orElse(emptyList());
    }

}
