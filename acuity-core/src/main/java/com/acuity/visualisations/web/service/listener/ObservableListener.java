package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent;

public interface ObservableListener {

    void processEvent(JobExecutionEvent event);
}
