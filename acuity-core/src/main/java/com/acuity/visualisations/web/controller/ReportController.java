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

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.report.entity.ReportExceptionEntity;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.web.service.uploadreports.UploadReportService;
import com.acuity.visualisations.web.service.uploadreports.vo.UploadReportSummary;
import com.acuity.visualisations.web.util.JSONDateDeserializer;
import com.acuity.visualisations.web.util.JSONDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * This is the controller class for data relating to the ETL report
 */
@RestController
@RequestMapping("/uploadreport")
public class ReportController {
    @Autowired
    private UploadReportService uploadReportService;

    @RequestMapping(value = "/{clinicalStudyId}/summary", method = RequestMethod.GET)
    @ResponseBody
    public List<ReportSummaryEntity> getSummaryData(@PathVariable("clinicalStudyId") final long clinicalStudyId) {
        return uploadReportService.getSummaryData(clinicalStudyId);
    }

    @RequestMapping(value = "/{clinicalStudyId}/exception/{jobExecID}", method = RequestMethod.GET)
    @ResponseBody
    public List<ReportExceptionEntity> getExceptionReport(@PathVariable("clinicalStudyId") final long clinicalStudyId,
                                                          @PathVariable("jobExecID") int jobExecID) {
        return uploadReportService.getExceptionReport(clinicalStudyId, jobExecID);
    }

    @RequestMapping(value = "/{clinicalStudyId}/table/{jobExecID}", method = RequestMethod.GET)
    @ResponseBody
    public List<ReportTableEntity> getTableReport(@PathVariable("clinicalStudyId") final long clinicalStudyId,
                                                  @PathVariable("jobExecID") int jobExecID) {
        return uploadReportService.getTableReport(clinicalStudyId, jobExecID);
    }

    @RequestMapping(value = "/{clinicalStudyId}/field/{jobExecID}", method = RequestMethod.GET)
    @ResponseBody
    public List<ReportFieldEntity> getFieldReport(@PathVariable("clinicalStudyId") final long clinicalStudyId,
                                                  @PathVariable("jobExecID") int jobExecID) {
        return uploadReportService.getFieldReport(clinicalStudyId, jobExecID);
    }

    @RequestMapping(value = "/{clinicalStudyId}/value/{jobExecID}", method = RequestMethod.GET)
    @ResponseBody
    public List<ReportValueEntity> getValueReport(@PathVariable("clinicalStudyId") final long clinicalStudyId,
                                                  @PathVariable("jobExecID") int jobExecID) {
        return uploadReportService.getValueReport(clinicalStudyId, jobExecID);
    }

    @RequestMapping(value = "/upload-summary", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public List<UploadReportSummary> getSummaryUploadReport(@RequestBody SummaryRequest requestBody) {
        return uploadReportService.getSummaryUploadReport(requestBody.getDateFrom(), requestBody.getDateTo());
    }

    @Data
    private static class SummaryRequest {
        @JsonSerialize(using = JSONDateSerializer.class)
        @JsonDeserialize(using = JSONDateDeserializer.class)
        private Date dateFrom;

        @JsonSerialize(using = JSONDateSerializer.class)
        @JsonDeserialize(using = JSONDateDeserializer.class)
        private Date dateTo;
    }
}
