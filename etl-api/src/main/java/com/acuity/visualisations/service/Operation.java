package com.acuity.visualisations.service;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by knml167 on 23/10/2014.
 */
@Getter
@Setter
public class Operation {

    private long lastStarted = 0;

    private AtomicLong totalDuration = new AtomicLong(0);
}

