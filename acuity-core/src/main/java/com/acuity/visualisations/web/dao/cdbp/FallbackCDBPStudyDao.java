package com.acuity.visualisations.web.dao.cdbp;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.StudyRule;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Profile("!cdbp")
@Repository
public class FallbackCDBPStudyDao implements CDBPStudyDao {

    @Override
    public void fillSearchQueryWorker(String query, QuerySearchWorker<StudyRule> worker) {
        // implementation ignore
    }

    @Override
    public List<StudyRule> searchExactStudy(String query) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getStudyCodes() {
        return new ArrayList<>();
    }
}
