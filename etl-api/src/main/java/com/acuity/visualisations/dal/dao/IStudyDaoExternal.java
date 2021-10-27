package com.acuity.visualisations.dal.dao;

import java.util.List;
import java.util.Map;

public interface IStudyDaoExternal {

    void delete(String study);

    String getStudyGuid(String studyName, String projectGuid);

    Map<String, Map<String, Integer>> getStudyCount(Map<String, List<String>> studies);

    boolean studyExists(String studyCode);

    Map<String, Map<String, Boolean>> getStudyScheduledCleanFlag(Map<String, List<String>> studies);

    Map<String, Map<String, Boolean>> getStudyAmlEnabledFlag();

    void setScheduleCleanFlag(String study);

    void setScheduleCleanFlag(String study, Boolean flag);

    int resetStudyEtlStatus(String studyCode);
}
