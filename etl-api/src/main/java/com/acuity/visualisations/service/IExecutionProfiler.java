package com.acuity.visualisations.service;

import java.util.Map;

public interface IExecutionProfiler {

	void startOperation(Long jobId, String operationName);

	void stopOperation(Long jobId, String operationName);

	Map<Long, Map<String, Operation>> getStatistics();

}
