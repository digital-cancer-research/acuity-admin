package com.acuity.visualisations.web.dao.cdbp;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.StudyRule;

import java.util.List;

public interface CDBPStudyDao {
    void fillSearchQueryWorker(String query, QuerySearchWorker<StudyRule> worker);

    List<StudyRule> searchExactStudy(String query);

    List<String> getStudyCodes();
}
