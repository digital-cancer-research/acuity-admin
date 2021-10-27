package com.acuity.visualisations.web.service.listener;

import org.quartz.Trigger;

public interface ConditionalListener {
    boolean isTriggerApplicable(Trigger trigger);
}
