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
