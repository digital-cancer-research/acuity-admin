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

package com.acuity.visualisations.web.service.uploadreports;

import com.acuity.visualisations.report.entity.ReportExceptionEntity;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.web.service.uploadreports.vo.UploadReportSummary;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 16/03/2015.
 */
@Service
public class UploadReportService {

    @Autowired
    private UploadReportRepository uploadReportRepository;

    public List<ReportSummaryEntity> getSummaryData(final long clinicalStudyId) {
        List<ReportSummaryEntity> summaryData = uploadReportRepository.getSummaryData(clinicalStudyId);
        List<ReportSummaryEntity> latestSummaries = summaryData.stream().limit(3).peek(item -> {
            item.setSummary(item.getRagStatus().getSummary());

            if (item.getEndDate() != null) {
                long duration = (item.getEndDate().getTime() - item.getStartDate().getTime()) / 1000;
                item.setDuration(String.format("%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60, duration % 60));
            }
        }).collect(Collectors.toList());
        return latestSummaries;
    }

    public List<ReportExceptionEntity> getExceptionReport(final long clinicalStudyId, int jobExecID) {
        return uploadReportRepository.getExceptionReport(clinicalStudyId, jobExecID);
    }

    public List<ReportTableEntity> getTableReport(final long clinicalStudyId, int jobExecID) {
        return uploadReportRepository.getTableReport(clinicalStudyId, jobExecID);
    }

    public List<ReportFieldEntity> getFieldReport(final long clinicalStudyId, int jobExecID) {
        return uploadReportRepository.getFieldReport(clinicalStudyId, jobExecID);
    }

    public List<ReportValueEntity> getValueReport(final long clinicalStudyId, int jobExecID) {
        return uploadReportRepository.getValueReport(clinicalStudyId, jobExecID);
    }

    public List<UploadReportSummary> getSummaryUploadReport(Date dateFrom, Date dateTo) {
        List<ReportSummaryEntity> summaryData = uploadReportRepository.getSummaryDataForPeriod(dateFrom, dateTo);

        Map<LocalDate, List<ReportSummaryEntity>> entriesByInterval = summaryData.stream()
                .collect(Collectors.groupingBy(e -> e.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

        List<UploadReportSummary> summaries = entriesByInterval.entrySet().stream()
                .map(e -> UploadReportSummary
                        .builder()
                        .date(Date.from(e.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        .filesSize(e.getValue().stream().mapToLong(ReportSummaryEntity::getFilesSize).sum())
                        .filesCount(e.getValue().stream().mapToInt(ReportSummaryEntity::getFilesCount).sum())
                        .build())
                .collect(Collectors.toList());
        return summaries;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    private static class DateInterval {
        /**
         * Start date inclusive
         */
        private LocalDate startDate;
        /**
         * End date inclusive
         */
        private LocalDate endDate;
    }


}
