package com.acuity.visualisations.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

public class DataAccessExceptionRetryListener extends RetryListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessExceptionRetryListener.class);

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        LOGGER.warn("Trying to launch job again, retry number: {}. Retry was caused by {}.", context.getRetryCount() + 1,
                throwable.getClass().getCanonicalName());
    }
}
