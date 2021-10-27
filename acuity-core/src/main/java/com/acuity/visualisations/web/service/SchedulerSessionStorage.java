/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.service.listener.AllEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Scope("session")
public class SchedulerSessionStorage implements InitializingBean, DisposableBean, Observer {

    private static final int EVENT_QUEUE_MAX_SIZE = 400;

    private final BlockingQueue<JobExecutionEvent> events = new ArrayBlockingQueue<>(EVENT_QUEUE_MAX_SIZE, true);

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<AllEventListener> eventListeners;

    @Override
    public void afterPropertiesSet() {
        log.debug("SchedulerSessionStorage bean initialization");
        eventListeners.forEach(listener -> listener.addObserver(this));
    }

    @Override
    public void update(Observable o, Object arg) {
        JobExecutionEvent event = (JobExecutionEvent) arg;

        try {
            final String code = event.getExecutionKey().getStudyCode();
            final String project = event.getExecutionKey().getProjectName();
            log.debug("Events queue size: {}. At processing study {}.{}. Current status: {}",
                    events.size(), project, code, event.getExecutionState());
            events.put(event);
        } catch (InterruptedException e) {
            log.error("Failed to put event {} to event queue. It won't be handled.", event);
        }
    }

    public JobExecutionEvent getEvent() throws Exception {
        return events.take();
    }

    @Override
    public void destroy() {
        log.debug("SchedulerSessionStorage bean destroyed");
        eventListeners.forEach(listener -> listener.deleteObserver(this));
    }
}
