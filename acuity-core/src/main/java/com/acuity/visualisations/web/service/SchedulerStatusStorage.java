package com.acuity.visualisations.web.service;

import com.acuity.visualisations.web.entity.JobExecutionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SchedulerStatusStorage {

    private ConcurrentMap<String, JobExecutionEvent.ExecutionState> studyStatusesMap = new ConcurrentHashMap<>();

    public ConcurrentMap<String, JobExecutionEvent.ExecutionState> getStudyStatusesMap() {
        return studyStatusesMap;
    }

}
