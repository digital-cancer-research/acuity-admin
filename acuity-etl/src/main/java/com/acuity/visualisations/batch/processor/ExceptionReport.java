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

package com.acuity.visualisations.batch.processor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.acuity.visualisations.batch.holders.JobExecutionInfoAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acuity.visualisations.report.dao.IExceptionReportDao;
import com.acuity.visualisations.report.entity.ReportExceptionEntity;
import com.acuity.visualisations.report.entity.RagStatus;

/**
 * Service method for collecting and publishing exceptions thrown during an ETL run
 */
@Component("exceptionReport")
@Scope("prototype")
public class ExceptionReport  extends JobExecutionInfoAware {

    /**
     * The DAO for exception reporting
     */
    @Autowired
    private IExceptionReportDao exceptionReportDao;
    
    /**
     * A collection of exceptions thrown during an ETL run
     */
    private List<ReportExceptionEntity> exceptionReport;
    
    /**
     * Adds an exception to the report
     *   
     * @param etlStep    The ETL step that threw the exception
     * @param exception  The exception that was thrown
     */
    public void addException(String etlStep, Throwable exception) {
        if (this.exceptionReport == null) {
            this.exceptionReport = new ArrayList<ReportExceptionEntity>();
        }
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        this.exceptionReport.add(new ReportExceptionEntity(etlStep, exception.getClass().getName(),
                exception.getMessage(), stackTrace, RagStatus.RED));
    }
    
    /**
     * Publishes the exception report to the database
     */
    @Transactional
    public boolean publishReport() {
        if (this.exceptionReport != null && this.exceptionReport.size() > 0) {
            this.exceptionReportDao.insertReportData(getJobExecutionId(), this.exceptionReport);
            this.exceptionReport.clear();
            return true;
        }
        return false;
    }
}
