package com.acuity.visualisations.unittests.dal.report.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Contains the unit tests for the DataTableReportDao class. This must be executed in a single transaction
 * so that the results can be rolled back.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:../test-classes/applicationContext.xml"})
@Transactional
public class DataSummaryDaoTests extends ACUITYDaoSupport {

    @Autowired
    private IDataSummaryReportDao dao;
    
    @Test
    public final void testRedRagStatusWhenETLRunFails() {

        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='FAILED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.RED, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberRagStatusWhenETLRunStatusIsUnknown() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='UNKNOWN' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberRagStatusWhenETLRunIsCompletedWithSkips() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED WITH SKIPS' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberRagStatusForZeroSubjectsInSource() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id, rdt_num_subject_source) values(-1, ?, ?)", 
                new Object[] { jobExecID, 0 });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberRagStatusWhenSourceAndAcuitySubjectNumbersAreNotEqual() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id, rdt_num_subject_source, rdt_num_subjects_acuity) values(-1, ?, ?, ?)",
                new Object[] { jobExecID, 11, 10 });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberWhenFieldMappingError() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id, rdf_error_type) values(-1, ?, ?)", 
                new Object[] { jobExecID, "MAPPING_ERROR" });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberWhenDataFieldError() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id, rdf_error_type) values(-1, ?, ?)", 
                new Object[] { jobExecID, "DATA_ERROR" });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testAmberWhenValueParseError() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id, rdv_error_type) values(-1, ?, ?)", 
                new Object[] { jobExecID, "PARSE_ERROR" });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.AMBER, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testGreenWhenNoWarningsOrErrors() {
        
        // Get a job execution ID that does not have report data for it
        final long jobExecID = (long) getJdbcTemplate().queryForObject(
                "SELECT job_execution_id FROM batch_job_execution WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND exit_code='COMPLETED' AND ROWNUM < 2", Long.class);

        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });
        
        // Run the test
        
        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        String ragStatus = (String) getJdbcTemplate().queryForObject("SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = " + jobExecID, String.class);
        
        // Assert
        
        assertEquals(RagStatus.GREEN, RagStatus.valueOf(ragStatus));
    }
    
    @Test
    public final void testSelect() {

        // Set up the test data
        
        Map<String, Object> jobExecData = getJdbcTemplate().queryForMap("SELECT j.job_execution_id, j.exit_code, p.string_val, j.start_time, j.end_time-j.start_time AS duration " +
                                            "FROM batch_job_execution j " +
                                            "LEFT JOIN batch_job_execution_params p ON j.job_execution_id = p.job_execution_id " +
                                            "WHERE job_execution_id NOT IN (SELECT rdt_je_id FROM report_data_table) AND p.key_name = 'etl.study' AND ROWNUM < 2");
        
//        byte[] timeDiffArray = ((oracle.sql.INTERVALDS) jobExecData.get("duration")).toBytes();
        byte[] timeDiffArray = new byte[0];
        String hours = ("00" + (timeDiffArray[4] - 60)).substring(new Integer(timeDiffArray[4] - 60).toString().length());
        String minutes = ("00" + (timeDiffArray[5] - 60)).substring(new Integer(timeDiffArray[5] - 60).toString().length());
        String seconds = ("00" + (timeDiffArray[6] - 60)).substring(new Integer(timeDiffArray[6] - 60).toString().length());
        String duration = String.format("%s:%s:%s", hours, minutes, seconds);
        
        long jobExecID = ((BigDecimal) jobExecData.get("job_execution_id")).longValue();
        String studyCode = (String) jobExecData.get("string_val");
        
        ReportSummaryEntity expected = ReportSummaryEntity.builder()
            .jobExecID(jobExecID)
            .studyID(studyCode)
            .ragStatus(RagStatus.GREEN)
            .startDate((Date)jobExecData.get("start_time"))
            .duration(duration)
            .exitCode((String) jobExecData.get("exit_code"))
            .build();
        
        // Insert blank rows into the report tables (it doesn't matter that the rows only contain the job execution ID)
        getJdbcTemplate().update("INSERT INTO report_data_table (rdt_id, rdt_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_field (rdf_id, rdf_je_id) values(-1, ?)", new Object[] { jobExecID });
        getJdbcTemplate().update("INSERT INTO report_data_value (rdv_id, rdv_je_id) values(-1, ?)", new Object[] { jobExecID });

        this.dao.insertReportDataForCompletedEtlRun(ReportSummaryEntity.builder().jobExecID(jobExecID).build());
        
        // Run the test
        
        List<ReportSummaryEntity> actual = this.dao.selectReportData(studyCode);
        
        assertEquals("The selected job exec ID was incorrect", expected.getJobExecID(),  actual.get(0).getJobExecID());
        assertEquals("The selected study code was incorrect", expected.getStudyID(),  actual.get(0).getStudyID());
        assertEquals("The selected RAG status was incorrect", expected.getRagStatus(),  actual.get(0).getRagStatus());
        assertEquals("The selected date was incorrect", expected.getStartDate(),  actual.get(0).getStartDate());
        assertEquals("The selected duration was incorrect", expected.getDuration(),  actual.get(0).getDuration());
        assertEquals("The selected exit code was incorrect", expected.getExitCode(),  actual.get(0).getExitCode());
    }
}
