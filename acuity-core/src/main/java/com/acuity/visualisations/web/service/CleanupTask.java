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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acuity.visualisations.mapping.dao.QuerySearchWorker;

public class CleanupTask implements Runnable {
    private Map<String, QuerySearchWorker<?>> searchWorkers;
    private long cleanupMillis;
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupTask.class);
    private Hook hook;

    public CleanupTask(Map<String, QuerySearchWorker<?>> searchWorkers, long cleanupMillis) {
        this.searchWorkers = searchWorkers;
        this.cleanupMillis = cleanupMillis;
    }

    @Override
    public void run() {
        while (hook.isKeepRunning()) {
            pause(cleanupMillis);
            checkWorkers();
        }
    }

    private void checkWorkers() {
        Set<String> queryIds = searchWorkers.keySet();
        for (Iterator<String> it = queryIds.iterator(); it.hasNext();) {
            String queryId = it.next();
            QuerySearchWorker<?> searchWorker = searchWorkers.get(queryId);
            if (searchWorker == null || searchWorker.isRunning()) {
                continue;
            }
            long completedMillis = searchWorker.getCompletedMillis();
            if (completedMillis != 0 && completedMillis + cleanupMillis < System.currentTimeMillis()) {
                try {
                    it.remove();
                } catch (RuntimeException e) {
                    LOGGER.error("Unexpected exception when removing a querySearchWorker!", e);
                }
            }
        }
    }


    private void pause(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }
}
