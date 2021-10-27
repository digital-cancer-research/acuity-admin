package com.acuity.visualisations.report.dao;

import java.util.List;

import com.acuity.visualisations.report.entity.DataAlert;
import com.acuity.visualisations.report.entity.Report;

public interface IDataAlertDao extends IBasicReportDao<DataAlert> {

	List<? extends Report> selectReportData(long jobExecutionId, long lastPrimaryKey);

}
