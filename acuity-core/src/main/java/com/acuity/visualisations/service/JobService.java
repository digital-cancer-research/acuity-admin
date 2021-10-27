package com.acuity.visualisations.service;

import org.quartz.Scheduler;

public interface JobService {

    void refreshJobList(Scheduler scheduler);

    void refreshJobList();

    void runJobNow(String studyCode);
}
