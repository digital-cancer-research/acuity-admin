package com.acuity.visualisations.web.dao.cdbp;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Profile("!cdbp")
@Repository
public class FallbackCDBPProjectDao implements CDBPProjectDao {
    @Override
    public List<ProjectRule> searchProjects(String query) {
        return new ArrayList<>();
    }

    @Override
    public Integer getTotalStudyCountByDrugId(String drugId) {
        return 0;
    }

    @Override
    public List<String> getCDBPDrugIds() {
        return new ArrayList<>();
    }

    @Override
    public void fillSearchQueryWorker(String query, QuerySearchWorker<ProjectRule> queryWorker) {
        // implementation ignore
    }
}
