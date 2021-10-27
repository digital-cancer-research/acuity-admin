package com.acuity.visualisations.unittests.dal.report.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataTableReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Contains the unit tests for the DataTableReportDao class. This must be executed in a single transaction
 * so that the results can be rolled back.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:../test-classes/applicationContext.xml"})
@Transactional
public class DataTableReportDaoTests extends ACUITYDaoSupport {

    @Autowired
    private IDataTableReportDao dao;
    
    @SuppressWarnings("serial")
    @Test
    public final void testInsert() {

        // Set up the test data with the Builder pattern
        
        final long jobExecID = getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Long.class);
        
        List<ReportTableEntity> expected = new ArrayList<ReportTableEntity>() {{
            add(ReportTableEntity.builder()
                    .jobExecID(jobExecID)
                    //here filename is empty,should be replaced with something meaningful
                    //when test fixed
                    .fileName("")
                    .numEventRowsUploaded(10)
                    .numOverwrittenRecords(0)
                    .numSubjectsAcuity(30)
                    .numSubjectsSource(40)
                    .ragStatus(RagStatus.AMBER)
                    .acuityEntities("Test, ECG, UNIT TEST")
                    .studyCode("D4510C00001")
                    .build());
            add(ReportTableEntity.builder()
                    .jobExecID(jobExecID)
                    //here filename is empty,should be replaced with something meaningful
                    //when test fixed
                    .fileName("")
                    .numEventRowsUploaded(10)
                    .numOverwrittenRecords(0)
                    .numSubjectsAcuity(30)
                    .numSubjectsSource(40)
                    .ragStatus(RagStatus.AMBER)
                    .acuityEntities("Test, ECG, UNIT TEST")
                    .studyCode("D4510C00001")
                    .build());
        }};
        
        // Run the test
        
        this.dao.insertReportData(jobExecID, expected);
        
        // Assert
        
        assertEquals(expected.size(), (int) getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM report_data_table WHERE rdt_acuity_entities LIKE '%UNIT TEST%'", Integer.class));
    }
    
    @Test
    public final void testSelect() {

        // Set up the test data with the Builder pattern
        
        final int jobExecID = (int) getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Integer.class);
        
        ReportTableEntity expected = ReportTableEntity.builder()
                                            .jobExecID(jobExecID)
                                            //here filename is empty,should be replaced with something meaningful
                                            //when test fixed
                                            .fileName("")
                                            .numEventRowsUploaded(10)
                                            .numOverwrittenRecords(0)
                                            .numSubjectsAcuity(30)
                                            .numSubjectsSource(40)
                                            .ragStatus(RagStatus.AMBER)
                                            .acuityEntities("Test, ECG, UNIT TEST")
                                            .studyCode("D4510C00001")
                                            .build();
        
        getJdbcTemplate().update("INSERT INTO report_data_table " +
                "(rdt_id, rdt_je_id, rdt_data_source, rdt_acuity_entities, rdt_num_subject_source, rdt_num_subjects_acuity, rdt_num_events_uploaded, rdt_rag_status) " +
                "VALUES(-1," + jobExecID + ", '" + expected.getFileName() + "', '" + expected.getAcuityEntities() + "', " + expected.getNumSubjectsSource() + ", " + expected.getNumSubjectsAcuity() + ", "
                + expected.getNumEventRowsUploaded() + ", '" + expected.getRagStatus().toString() + "')");

        // Run the test
        
        ReportTableEntity actual = dao.selectReportData(jobExecID).get(0);
        
        // Assert
        
        assertEquals(expected.getFileName(), actual.getFileName());
        assertEquals(expected.getAcuityEntities(), actual.getAcuityEntities());
        assertEquals(expected.getNumSubjectsSource(), actual.getNumSubjectsSource());
        assertEquals(expected.getNumSubjectsAcuity(), actual.getNumSubjectsAcuity());
        assertEquals(expected.getNumEventRowsUploaded(), actual.getNumEventRowsUploaded());
        assertEquals(expected.getRagStatus(), actual.getRagStatus());
    }
}
