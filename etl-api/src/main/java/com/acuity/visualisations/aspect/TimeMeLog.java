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

package com.acuity.visualisations.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Util for time logging
 */
@Slf4j
public abstract class TimeMeLog {
    private static final int MAX_TIME_BEFORE_LOG_ARGS = 2000;
    private static final int MAX_COLLECTION_SIZE_TO_LOG = 100;
    private static final String LOG_MESSAGE_FORMAT = "%s execution time: %dms (Time range: %s)";
    private static final String LOG_SLOW_MESSAGE_FORMAT = "%s slow execution time: %dms (Time range: %s), args %s";

    protected void logExecutionTime(String classAndMethod, StopWatch stopWatch, List<Object> queryArgs) {
        stopWatch.stop();
        long executionTime = stopWatch.getTotalTimeMillis();
        String range = findTime(executionTime);
        if (stopWatch.getTotalTimeMillis() < MAX_TIME_BEFORE_LOG_ARGS) {
            String logMessage = String.format(LOG_MESSAGE_FORMAT, classAndMethod, executionTime, range);
            log.debug(logMessage);
        } else {
            List<?> truncatedQueryArgs = truncateLargeCollections(queryArgs);
            String logSlowMessage = String.format(LOG_SLOW_MESSAGE_FORMAT, classAndMethod,
                    executionTime, range, truncatedQueryArgs);
            log.warn(logSlowMessage);
        }
    }

    protected String findTime(long totalTime) {
        return String.format("%d secs", totalTime / 1000L);
    }

    private List<?> truncateLargeCollections(List<Object> queryArgs) {
        return queryArgs.stream()
                        .map(arg -> {
                            if (arg instanceof Collection) {
                                return ((Collection<?>) arg).stream()
                                                            .limit(MAX_COLLECTION_SIZE_TO_LOG)
                                                            .collect(toList());
                            } else {
                                return arg;
                            }
                        })
                        .collect(toList());
    }
}
