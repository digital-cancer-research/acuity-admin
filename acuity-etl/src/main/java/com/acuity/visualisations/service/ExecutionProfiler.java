package com.acuity.visualisations.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by knml167 on 23/10/2014.
 */
@Service
public class ExecutionProfiler implements IExecutionProfiler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionProfiler.class);

    private Map<Long, Map<String, Operation>> jobs = new ConcurrentHashMap<>();

    @Override
    public void startOperation(Long jobId, String operationName) {
        Operation op = getOper(jobId, operationName);
        op.setLastStarted(System.currentTimeMillis());
    }

    @Override
    public void stopOperation(Long jobId, String operationName) {
        Operation op = getOper(jobId, operationName);
        if (op.getLastStarted() > 0) {
            op.getTotalDuration().addAndGet(System.currentTimeMillis() - op.getLastStarted());
            op.setLastStarted(0);
        }
    }

    private Operation getOper(Long jobId, String operationName) {
        Map<String, Operation> tasks = jobs.computeIfAbsent(jobId, s -> new HashMap<>());
        Operation res = tasks.computeIfAbsent(operationName, s -> {
            LOGGER.debug(operationName);
            return new Operation();
        });
        return res;
    }

    @Override
    public Map<Long, Map<String, Operation>> getStatistics() {
        return jobs;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearUp() {
        jobs.clear();
    }
}
