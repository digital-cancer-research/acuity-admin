package com.acuity.visualisations.batch.processor;

import com.acuity.visualisations.batch.holders.JobExecutionInfoAware;
import com.acuity.visualisations.report.dao.IDataAlertDao;
import com.acuity.visualisations.report.entity.DataAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("dataAlertReport")
@Scope("prototype")
public class DataAlertReport extends JobExecutionInfoAware {

    @Autowired
    private IDataAlertDao dataAlertDao;

    private class DataAlertInfo {
        private String countSourceFile;
        private String countDatabase;
    }

    private Map<String, Map<String, DataAlertInfo>> report = new HashMap<String, Map<String, DataAlertInfo>>();

    public void addToReport(String studyId, String entity, String countSourceFile, String countDatabase) {

        if (!report.containsKey(studyId)) {
            report.put(studyId, new HashMap<>());
        }

        Map<String, DataAlertInfo> studyMap = report.get(studyId);
        if (!studyMap.containsKey(entity)) {
            DataAlertInfo info = new DataAlertInfo();
            info.countSourceFile = countSourceFile;
            info.countDatabase = countDatabase;
            studyMap.put(entity, info);
        }
    }


    public void publishReport(String studyId, Long jobExecutionId) {

        List<DataAlert> reports = new ArrayList<DataAlert>();

        for (String studyKey : report.keySet()) {
            Map<String, DataAlertInfo> fileMap = report.get(studyKey);

            for (String entityId : fileMap.keySet()) {

                DataAlertInfo daInfo = fileMap.get(entityId);
                DataAlert dataAlert = new DataAlert();
                dataAlert.setJobExecutionId(jobExecutionId);
                dataAlert.setStudyCode(studyId);
                dataAlert.setDataEntity(entityId);
                dataAlert.setCountSourceFile(daInfo.countSourceFile);
                dataAlert.setCountDatabase(daInfo.countDatabase);

                reports.add(dataAlert);
            }
        }
        dataAlertDao.batchInsert(reports);
    }
}
