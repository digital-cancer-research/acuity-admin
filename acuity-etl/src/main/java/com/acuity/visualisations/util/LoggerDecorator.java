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

package com.acuity.visualisations.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerDecorator {

    private Logger logger;

    public LoggerDecorator(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void error(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object[] argArray) {
        logger.error(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, argArray));
    }

    public void error(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object arg) {
        logger.error(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{arg}));
    }

    public void error(Long jobExecutionId, String jobName, String projectName, String studyName, String message) {
        logger.error(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}));
    }

    public void error(Long jobExecutionId, String jobName, String projectName, String studyName, String message, Throwable t) {
        logger.error(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}), t);
    }

    public void debug(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object[] argArray) {
        logger.debug(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, argArray));
    }

    public void debug(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object arg) {
        logger.debug(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{arg}));
    }

    public void debug(Long jobExecutionId, String jobName, String projectName, String studyName, String message) {
        logger.debug(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}));
    }

    public void debug(Long jobExecutionId, String jobName, String projectName, String studyName, String message, Throwable t) {
        logger.debug(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}), t);
    }

    public void info(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object[] argArray) {
        logger.info(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, argArray));
    }

    public void info(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object arg) {
        logger.info(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{arg}));
    }

    public void info(Long jobExecutionId, String jobName, String projectName, String studyName, String message) {
        logger.info(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}));
    }

    public void info(Long jobExecutionId, String jobName, String projectName, String studyName, String message, Throwable t) {
        logger.info(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}), t);
    }

    public void warn(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object[] argArray) {
        logger.warn(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, argArray));
    }

    public void warn(Long jobExecutionId, String jobName, String projectName, String studyName, String format, Object arg) {
        logger.warn(getNewFormat(format), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{arg}));
    }

    public void warn(Long jobExecutionId, String jobName, String projectName, String studyName, String message) {
        logger.warn(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}));
    }

    public void warn(Long jobExecutionId, String jobName, String projectName, String studyName, String message, Throwable t) {
        logger.warn(getNewFormat(message), getNewArgArray(jobExecutionId, jobName, projectName, studyName, new Object[]{}), t);
    }

    private static Object[] getNewArgArray(Long jobExecutionId, String jobName, String projectName, String studyName, Object[] argArray) {
        Object[] newArgArray = ArrayUtils.addAll(new Object[]{jobExecutionId, jobName, projectName, studyName}, argArray);
        return newArgArray;
    }

    private static String getNewFormat(String format) {
        String newFormat = "Job Execution Id: {}, Job Name: {}, Project: {}, Study: {}. " + format;
        return newFormat;
    }

}
