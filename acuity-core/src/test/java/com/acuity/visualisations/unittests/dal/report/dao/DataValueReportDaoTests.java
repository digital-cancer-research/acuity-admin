package com.acuity.visualisations.unittests.dal.report.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataValueReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.report.entity.ValueErrorType;
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
public class DataValueReportDaoTests extends ACUITYDaoSupport {

    @Autowired
    private IDataValueReportDao dao;
    
    @SuppressWarnings("serial")
    @Test
    public final void testInsert() {
        
        // Set up the test data with the Builder pattern
        
        final long jobExecID = (long) getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Long.class);
        
        List<ReportValueEntity> expected = new ArrayList<ReportValueEntity>() {{
            add(ReportValueEntity.builder()
                .jobExecID(jobExecID)
                .dataField("refLow")
                .rawDataColumn("REFLOW")
                .rawDataValue("NaN")
                .errorType(ValueErrorType.PARSE_ERROR)
                .errorDescription("Error parsing value of expected type \"Real\": BigDecimal parser: Unable to parse following value: NaN")
                //here filename is empty,should be replaced with something meaningful
                //when test fixed
                .fileName("")
                .ragStatus(RagStatus.RED)
                .build());
            add(ReportValueEntity.builder()
                .jobExecID(jobExecID)
                .dataField("refLow")
                .rawDataColumn("REFLOW")
                .rawDataValue("NaN")
                .errorType(ValueErrorType.PARSE_WARNING)
                .errorDescription("Warning parsing value of expected type \"Real\": Converted value to real")
                //here filename is empty,should be replaced with something meaningful
                //when test fixed
                .fileName("")
                .ragStatus(RagStatus.AMBER)
                .build());
        }};
        
        // Run the test
        
        this.dao.insertReportData(jobExecID, expected);
        
        // Assert
        
        assertEquals(expected.size(), (int) getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM report_data_value WHERE rdv_je_id = ?", new Object[] { jobExecID }, Integer.class));
    }
    
    @Test
    public final void testSelect() {

        // Set up the test data with the Builder pattern
        
        final int jobExecID = getJdbcTemplate().queryForObject("SELECT job_execution_id FROM batch_job_execution WHERE ROWNUM < 2", Integer.class);
        
        ReportValueEntity expected = ReportValueEntity.builder()
                                        .jobExecID(jobExecID)
                                        .dataField("refLow")
                                        .rawDataColumn("REFLOW")
                                        .rawDataValue("NaN")
                                        .errorType(ValueErrorType.PARSE_ERROR)
                                        .errorDescription("Error parsing value of expected type \"Real\": BigDecimal parser: Unable to parse following value: NaN")
                                        //here filename is empty,should be replaced with something meaningful
                                        //when test fixed
                                        .fileName("")
                                        .ragStatus(RagStatus.RED)
                                        .build();
        
        getJdbcTemplate().update("INSERT INTO report_data_value " +
                "(rdv_id, rdv_je_id, rdv_data_source, rdv_data_field, rdv_raw_data_column, "
            + "rdv_raw_data_value, rdv_error_type, rdv_error_description, rdv_rag_status) " +
                "VALUES(-1," + jobExecID + ", '" + expected.getFileName() + "', '" + expected.getDataField() + "', '" + expected.getRawDataColumn() + "', '" + 
                expected.getRawDataValue() + "', '" + expected.getErrorType().toString() + "', '" + expected.getErrorDescription() + "', '"  + expected.getRagStatus().toString() + "')");

        // Run the test
        
        ReportValueEntity actual = dao.selectReportData(jobExecID).get(0);
        
        // Assert
        
        assertEquals(expected.getFileName(), actual.getFileName());
        assertEquals(expected.getDataField(), actual.getDataField());
        assertEquals(expected.getRawDataColumn(), actual.getRawDataColumn());
        assertEquals(expected.getRawDataValue(), actual.getRawDataValue());
        assertEquals(expected.getErrorType(), actual.getErrorType());
        assertEquals(expected.getErrorDescription(), actual.getErrorDescription());
        assertEquals(expected.getRagStatus(), actual.getRagStatus());
    }

}
