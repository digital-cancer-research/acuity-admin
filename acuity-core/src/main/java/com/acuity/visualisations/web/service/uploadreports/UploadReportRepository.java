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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface UploadReportRepository {

    @Select("SELECT "
        + "r.rds_je_id as jobExecID, "
        + "r.rds_study_code as studyID, "
        + "r.rds_rag_status as ragStatus, "
        + "r.rds_files_size as filesSize, "
        + "r.rds_files_count as filesCount, "
        + "j.exit_code as exitCode, "
        + "j.start_time as startDate, "
        + "j.end_time as endDate "
        + "FROM report_data_summary r "
        + "LEFT JOIN batch_job_execution j ON r.rds_je_id = j.job_execution_id "
        + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = r.rds_study_code) "
        + "WHERE MSR_ID = #{clinicalStudyId} "
        + "ORDER BY start_time DESC")
    List<ReportSummaryEntity> getSummaryData(@Param("clinicalStudyId") long clinicalStudyId);

    @Select("SELECT "
            + "r.rds_je_id as jobExecID, "
            + "r.rds_study_code as studyID, "
            + "r.rds_rag_status as ragStatus, "
            + "r.rds_files_size as filesSize, "
            + "r.rds_files_count as filesCount, "
            + "j.exit_code as exitCode, "
            + "j.start_time as startDate, "
            + "j.end_time as endDate "
            + "FROM report_data_summary r "
            + "LEFT JOIN batch_job_execution j ON r.rds_je_id = j.job_execution_id "
            + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = r.rds_study_code) "
            + "WHERE date_trunc('day', start_time) >= date_trunc('day', #{dateFrom}::timestamp) "
            + "AND date_trunc('day', end_time) <= date_trunc('day', #{dateTo}::timestamp) "
            + "ORDER BY start_time DESC")
    List<ReportSummaryEntity> getSummaryDataForPeriod(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Select("SELECT "
        + "rex_je_id as jobExecID, "
        + "rex_etl_step as etlStep, "
        + "rex_exception_type as exceptionClass, "
        //RCT-1946: we don't show stack trace to users
        //"rex_stack_trace as stackTrace, " +
        + "rex_rag_status as ragStatus, "
        + "rex_message as message "
        + "FROM report_exceptions "
        + "INNER JOIN report_data_summary on (rds_je_id = rex_je_id) "
        + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = rds_study_code) "
        + "WHERE rex_je_id = #{jobExecID} and MSR_ID = #{clinicalStudyId}")
    List<ReportExceptionEntity> getExceptionReport(@Param("clinicalStudyId") long clinicalStudyId, @Param("jobExecID") int jobExecID);

    @Select("SELECT "
        + "rdt_je_id as jobExecID, "
        + "rdt_data_source as fileName, "
        + "rdt_rag_status as ragStatus, "
        + "rdt_acuity_entities as acuityEntities, "
        + "rdt_num_subject_source as numSubjectsSource, "
        + "rdt_num_subjects_acuity as numSubjectsAcuity, "
        + "rdt_num_events_uploaded as numEventRowsUploaded "
        + "FROM report_data_table d "
        + "INNER JOIN report_data_summary on (rds_je_id = rdt_je_id) "
        + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = rds_study_code) "
        + "WHERE rdt_je_id = #{jobExecID} and MSR_ID = #{clinicalStudyId}")
    List<ReportTableEntity> getTableReport(@Param("clinicalStudyId") long clinicalStudyId, @Param("jobExecID") int jobExecID);

    @Select("SELECT "
        + "rdf_je_id as jobExecID, "
        + "rdf_data_source as fileName, "
        + "rdf_is_mapped as isMapped, "
        + "rdf_raw_data_column as rawDataColumn, "
        + "rdf_data_field as dataField, "
        + "rdf_error_type as errorType, "
        + "rdf_error_description as errorDescription, "
        + "rdf_rag_status as ragStatus "
        + "FROM report_data_field "
        + "INNER JOIN report_data_summary on (rds_je_id = rdf_je_id) "
        + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = rds_study_code) "
        + "WHERE rdf_je_id = #{jobExecID} and MSR_ID = #{clinicalStudyId}")
    List<ReportFieldEntity> getFieldReport(@Param("clinicalStudyId") long clinicalStudyId, @Param("jobExecID") int jobExecID);

    @Select("SELECT "
        + "rdv_je_id as jobExecID, "
        + "rdv_data_source as fileName, "
        + "rdv_data_field as dataField, "
        + "rdv_raw_data_column as rawDataColumn, "
        + "rdv_raw_data_value as rawDataValue, "
        + "rdv_error_type as errorType, "
        + "rdv_error_description as errorDescription, "
        + "coalesce(rdv_error_count,0) as errorCount, "
        + "rdv_rag_status as ragStatus "
        + "FROM report_data_value "
        + "INNER JOIN report_data_summary on (rds_je_id = rdv_je_id) "
        + "LEFT JOIN MAP_STUDY_RULE ON (MSR_STUDY_CODE = rds_study_code) "
        + "WHERE rdv_je_id = #{jobExecID} and MSR_ID = #{clinicalStudyId}")
    List<ReportValueEntity> getValueReport(@Param("clinicalStudyId") long clinicalStudyId, @Param("jobExecID") int jobExecID);

    @Delete("DELETE "
            + "FROM report_exceptions "
            + "WHERE EXISTS "
            + "(SELECT 1 FROM report_data_summary "
            + "WHERE (rds_je_id = rex_je_id) AND report_data_summary.rds_study_code = #{clinicalStudyCode})")
    void removeStudyExceptionsReport(@Param("clinicalStudyCode") String clinicalStudyCode);

    @Delete("DELETE "
            + "FROM report_data_table "
            + "WHERE EXISTS "
            + "(SELECT 1 FROM report_data_summary "
            + "WHERE (rds_je_id = rdt_je_id) AND report_data_summary.rds_study_code = #{clinicalStudyCode})")
    void removeStudyTableReport(@Param("clinicalStudyCode") String clinicalStudyCode);

    @Delete("DELETE "
            + "FROM report_data_field "
            + "WHERE EXISTS "
            + "(SELECT 1 FROM report_data_summary "
            + "WHERE (rds_je_id = rdf_je_id) AND report_data_summary.rds_study_code = #{clinicalStudyCode})")
    void removeStudyFieldReport(@Param("clinicalStudyCode") String clinicalStudyCode);

    @Delete("DELETE "
            + "FROM report_data_value "
            + "WHERE EXISTS "
            + "(SELECT 1 FROM report_data_summary "
            + "WHERE (rds_je_id = rdv_je_id) AND report_data_summary.rds_study_code = #{clinicalStudyCode})")
    void removeStudyValueReport(@Param("clinicalStudyCode") String clinicalStudyCode);

    @Delete("DELETE "
            + "FROM report_data_summary "
            + "WHERE rds_study_code = #{clinicalStudyCode}")
    void removeStudyRelatedSummaryReport(@Param("clinicalStudyCode") String clinicalStudyCode);
}
