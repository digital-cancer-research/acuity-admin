package com.acuity.visualisations.report.dao;

import java.util.List;

import com.acuity.visualisations.report.entity.Report;

public interface IBasicReportDao<T extends Report> {

	void batchInsert(List<T> list);

	void delete(List<Long> jobExecutionIds);

}
