package com.acuity.visualisations.web.dao.cdbp;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.ProjectRule;

import java.util.List;

public interface CDBPProjectDao {
    List<ProjectRule> searchProjects(String query);

    Integer getTotalStudyCountByDrugId(String drugId);

    List<String> getCDBPDrugIds();

    void fillSearchQueryWorker(String query, QuerySearchWorker<ProjectRule> queryWorker);
}
