package com.acuity.visualisations.unittests.dal.report.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataFieldReportDao;
import com.acuity.visualisations.report.entity.ColumnErrorType;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:../test-classes/applicationContext.xml"})
@Transactional
public class DataFieldReportDaoTests extends ACUITYDaoSupport {

    @Autowired
    private IDataFieldReportDao dao;
    
    @SuppressWarnings("serial")
    @Test
    public final void testInsert() {
        
        // Set up the test data with the Builder pattern
        
        final long jobExecID = (long) getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Long.class);
        
        List<ReportFieldEntity> expected = new ArrayList<ReportFieldEntity>() {{
            add(ReportFieldEntity.builder()
                .jobExecID(jobExecID)
                .dataField("ipdcDate")
                .rawDataColumn("IPDC_DAT")
                .isMapped(true)
                .errorType(ColumnErrorType.NO_ERROR)
                .errorTypeString("-")
                .errorDescription("No errors in upload")
                //here filename is empty,should be replaced with something meaningful
                //when test fixed
                .fileName("")
                .ragStatus(RagStatus.GREEN)
                .build());
            add(ReportFieldEntity.builder()
                .jobExecID(jobExecID)
                .dataField("visitNumber")
                .rawDataColumn("VISIT")
                .isMapped(true)
                .errorType(ColumnErrorType.DATA_WARNING)
                .errorTypeString("Data warning")
                .errorDescription("Up to 50% of source data values could not be parsed")
                //here filename is empty,should be replaced with something meaningful
                //when test fixed
                .fileName("")
                .ragStatus(RagStatus.AMBER)
                .build());
        }};
        
        // Run the test
        
        this.dao.insertReportData(jobExecID, expected);
        
        // Assert
        
        assertEquals(expected.size(), (int) getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM report_data_field WHERE rdf_je_id = ?", new Object[] { jobExecID }, Integer.class));
    }
    
    @Test
    public final void testSelect() {

        // Set up the test data with the Builder pattern
        
        final int jobExecID = getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Integer.class);
        
        ReportFieldEntity expected = ReportFieldEntity.builder()
                                        .jobExecID(jobExecID)
                                        .dataField("ipdcDate")
                                        .rawDataColumn("IPDC_DAT")
                                        .isMapped(true)
                                        .errorType(ColumnErrorType.NO_ERROR)
                                        .errorType(ColumnErrorType.NO_ERROR)
                                        .errorDescription("No errors in upload")
                                        //here filename is empty,should be replaced with something meaningful
                                        //when test fixed
                                        .fileName("")
                                        .ragStatus(RagStatus.GREEN)
                                        .build();
        
        getJdbcTemplate().update("INSERT INTO report_data_field " +
                "(rdf_id, rdf_je_id, rdf_data_source, rdf_data_field, rdf_raw_data_column," +
                    "rdf_is_mapped, rdf_error_type, rdf_error_description, rdf_rag_status) " +
                "VALUES(-1," + jobExecID + ", '" + expected.getFileName() + "', '" + expected.getDataField() + "', '" + expected.getRawDataColumn() + "', '" + 
                (expected.isMapped() ? 1:0) + "', '" + expected.getErrorType().toString() + "', '" + expected.getErrorDescription() + "', '"  + expected.getRagStatus().toString() + "')");

        // Run the test
        
        ReportFieldEntity actual = dao.selectReportData(jobExecID).get(0);
        
        // Assert
        
        assertEquals(expected.getFileName(), actual.getFileName());
        assertEquals(expected.getDataField(), actual.getDataField());
        assertEquals(expected.getRawDataColumn(), actual.getRawDataColumn());
        assertEquals(expected.isMapped(), actual.isMapped());
        assertEquals(expected.getErrorType(), actual.getErrorType());
        assertEquals(expected.getErrorDescription(), actual.getErrorDescription());
        assertEquals(expected.getRagStatus(), actual.getRagStatus());
    }
}
