package com.acuity.visualisations.report.entity;

public class DataAlert extends Report {

    private String studyCode;
    private String dataEntity;
    private String countSourceFile;
    private String countDatabase;

    @Override
    public String getReportFormat() {
        return "Study: {}, Entity: {}, Count in Source File: {}, Count in Database: {}";
    }

    @Override
    public Object[] getReportArgs() {
        return new Object[]{studyCode, dataEntity, countSourceFile, countDatabase};
    }

    public String getStudyCode() {
        return studyCode;
    }

    public void setStudyCode(String sCode) {
        studyCode = sCode;
    }

    public String getDataEntity() {
        return dataEntity;
    }

    public void setDataEntity(String e) {
        dataEntity = e;
    }

    public String getCountSourceFile() {
        return countSourceFile;
    }

    public void setCountSourceFile(String count) {
        countSourceFile = count;
    }

    public String getCountDatabase() {
        return countDatabase;
    }

    public void setCountDatabase(String count) {
        countDatabase = count;
    }

}
