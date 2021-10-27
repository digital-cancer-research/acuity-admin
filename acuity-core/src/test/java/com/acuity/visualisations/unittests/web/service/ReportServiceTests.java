package com.acuity.visualisations.unittests.web.service;

import com.acuity.visualisations.report.dao.IDataFieldReportDao;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.dao.IDataTableReportDao;
import com.acuity.visualisations.report.dao.IDataValueReportDao;
import com.acuity.visualisations.report.entity.ColumnErrorType;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportEntity;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.report.entity.ValueErrorType;
import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import com.acuity.visualisations.web.entity.ReportType;
import com.acuity.visualisations.web.service.ReportService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link ReportService} class
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTests {

    // Setup the mock instance variables that belong to the ReportService class
    
    /**
     * This mocks the dataSummaryReportDao instance variable in {@link ReportService}
     */
    @Mock
    private IDataSummaryReportDao dataSummaryReportDao;
    
    /**
     * This mocks the datatableReportDao instance variable in {@link ReportService}
     */
    @Mock
    private IDataTableReportDao dataTableReportDao;
    
    /**
     * This mocks the dataFieldReportDao instance variable in {@link ReportService}
     */
    @Mock
    private IDataFieldReportDao dataFieldReportDao;
    
    /**
     * This mocks the dataValueReportDao instance variable in {@link ReportService}
     */
    @Mock
    private IDataValueReportDao dataValueReportDao;
    
    /**
     * This mocks the jobExecutionDao instance variable in {@link ReportService}
     */
    @Mock
    private ExtendedJobExecutionDao jobExecutionDao;
    
    // Inject the mock objects
    
    /**
     * This is the instance of the {@link ReportService} class that we will be running.
     * The @InjectMocks annotation injects the @Mock annotated instance variables into
     * the object.
     */
    @InjectMocks
    private ReportService service;

    /**
     * Job exec IDs that will be used in all tests
     */
    private ArrayList<Integer> jobExecIDs = new ArrayList<Integer>();
    
    /**
     * The study code that will be used in all tests
     */
    private String studyCode;
    
    /**
     * Called before each test. Don't forget that JUnit creates a new instance of ReportServiceTest
     * each time a test is run.
     */
    @Before
    public void setUp() {
        
        // Build the global test data
        this.studyCode ="D4510C00001"; 
        this.jobExecIDs.add(4387);
        this.jobExecIDs.add(4385);
        this.jobExecIDs.add(5012);
        
        // Need to run this before each test.
        // Mock the getJobExecutionsByStudy method
        when(this.jobExecutionDao.getJobExecutionsByStudy(this.studyCode)).thenReturn(this.jobExecIDs);
//        this.service.initReport(this.studyCode);
    }

    @SuppressWarnings("serial")
    @Test
    public final void testGetSummaryData() {
        
        // Set up the test data with the Builder pattern
        List<ReportSummaryEntity> expected = new ArrayList<ReportSummaryEntity>() {{
           add(ReportSummaryEntity.builder().jobExecID(jobExecIDs.get(0)).build());
           add(ReportSummaryEntity.builder().jobExecID(jobExecIDs.get(1)).build());
           add(ReportSummaryEntity.builder().jobExecID(jobExecIDs.get(2)).build());
        }};
        
        // Create the stubs
        when(this.dataSummaryReportDao.selectReportData(this.studyCode)).thenReturn(expected);
        
        // Run the test
        List<ReportSummaryEntity> actual = this.service.getSummaryData(studyCode);
        
        // Assert
        
        // We are only testing that the references end up in the correct place, so .equals() on the object is fine. 
        // Can't do a direct array index comparison because the results are put into a HashSet in the code
        for (int i = 0; i < actual.size(); i++) {
            assertTrue("The summary report data was not collated correctly", actual.contains(expected.get(i)));
        }
    }
    
    @Test
    public final void testNoReportDataIsReturnedForLegacyEtlRuns() {

        // Set up the test data with the Builder pattern
        
        // If the job exec ID is 0 then the ETL run was made before the report functionality was implemented
        List<ReportSummaryEntity> data = new ArrayList<ReportSummaryEntity>();
        
        // Create the stubs
        when(this.dataSummaryReportDao.selectReportData(this.studyCode)).thenReturn(data);
        
        // Run the test
        List<ReportSummaryEntity> actual = this.service.getSummaryData(studyCode);
        
        // Assert
        assertEquals("There should be no report data for legacy ETL runs", 0, actual.size()); 
    }

    @Test
    public final void testGetTableFieldValueReport() {
     
        // Set up the test data with the Builder pattern
        
        int jobExecID = this.jobExecIDs.get(0);
        
        List<ReportTableEntity> expectedTableData = new ArrayList<ReportTableEntity>();
        List<ReportFieldEntity> expectedFieldData = new ArrayList<ReportFieldEntity>();
        List<ReportValueEntity> expectedValueData = new ArrayList<ReportValueEntity>();
        
        // Create the stubs
        when(this.dataTableReportDao.selectReportData(jobExecID)).thenReturn(expectedTableData);
        when(this.dataFieldReportDao.selectReportData(jobExecID)).thenReturn(expectedFieldData);
        when(this.dataValueReportDao.selectReportData(jobExecID)).thenReturn(expectedValueData);
        
        // Run the test
        Map<ReportType, List<? extends ReportEntity>> actual = this.service.getExceptionTableFieldValueReport(jobExecID);
        
        // Assert
        
        // We are only testing that the references end up in the correct place, so .equals() on the object is fine
        assertEquals("The table report data was not collated correctly", expectedTableData, actual.get(ReportType.TABLE));
        assertEquals("The field report data was not collated correctly", expectedFieldData, actual.get(ReportType.VALUE));
        assertEquals("The value report data was not collated correctly", expectedValueData, actual.get(ReportType.FIELD));
    }
    
    @Test
    @SuppressWarnings({ "unchecked", "serial" })
    public final void testFieldErrorTypeIsDeterminedCorrectly() {
        
        // Set up the test data with the Builder pattern
        
        int jobExecID = this.jobExecIDs.get(0);
        
        List<ReportFieldEntity> data = new ArrayList<ReportFieldEntity>() {{
            add(ReportFieldEntity.builder().errorType(ColumnErrorType.NO_ERROR).build());
            add(ReportFieldEntity.builder().errorType(ColumnErrorType.DATA_WARNING).build());
            add(ReportFieldEntity.builder().errorType(ColumnErrorType.DATA_ERROR).build());
            add(ReportFieldEntity.builder().errorType(ColumnErrorType.DATA_SOURCE_ERROR).build());
            add(ReportFieldEntity.builder().errorType(ColumnErrorType.MAPPING_ERROR).build());
        }};
        
        // Create the stubs
        when(this.dataFieldReportDao.selectReportData(jobExecID)).thenReturn(data);
        
        // Run the test
        Map<ReportType, List<? extends ReportEntity>> results = this.service.getExceptionTableFieldValueReport(jobExecID);
     
        List<ReportFieldEntity> actual = (List<ReportFieldEntity>) results.get(ReportType.FIELD);
        
        // Assert
        assertEquals("The error type string was not determined correctly for NO_ERROR", "-", actual.get(0).getErrorTypeString());
        assertEquals("The error type string was not determined correctly for DATA_WARNING", "Data error", actual.get(1).getErrorTypeString());
        assertEquals("The error type string was not determined correctly for DATA_ERROR", "Data error", actual.get(2).getErrorTypeString());
        assertEquals("The error type string was not determined correctly for DATA_SOURCE_ERROR", "Data source error", actual.get(3).getErrorTypeString());
        assertEquals("The error type string was not determined correctly for MAPPING_ERROR", "Mapping error", actual.get(4).getErrorTypeString());
    }
    
    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public final void testFormatMultipleErrorSetsTheCorrectErrorDescription() {
        
        // Set up the test data with the Builder pattern
        
        int jobExecID = this.jobExecIDs.get(0);
        
        List<ReportFieldEntity> data = new ArrayList<ReportFieldEntity>() {{
            add(ReportFieldEntity.builder()
                            .errorType(ColumnErrorType.DATA_WARNING)
                            .isMapped(false)
                            .errorDescription("Up to 50% of source data values could not be parsed")
                            .build());
            add(ReportFieldEntity.builder()
                            .errorType(ColumnErrorType.DATA_ERROR)
                            .isMapped(false)
                            .errorDescription("Over 50% of source data values could not be parsed")
                            .build());
        }};
        
        // Create the stubs
        when(this.dataFieldReportDao.selectReportData(jobExecID)).thenReturn(data);
        
        // Run the test
        Map<ReportType, List<? extends ReportEntity>> results = this.service.getExceptionTableFieldValueReport(jobExecID);
     
        List<ReportFieldEntity> actual = (List<ReportFieldEntity>) results.get(ReportType.FIELD);
        
        // Assert
        assertEquals("The error description was not determined correctly for DATA_WARNING and mapping error", 
                        "Up to 50% of source data values could not be parsed. No mapping has been found for this column", 
                        actual.get(0).getErrorDescription());
        assertEquals("The error description was not determined correctly for DATA_ERROR and mapping error", 
                        "Over 50% of source data values could not be parsed. No mapping has been found for this column", 
                        actual.get(1).getErrorDescription());
        assertEquals("The RAG status was not determined correctly for DATA_WARNING and mapping error", 
                        RagStatus.AMBER, actual.get(0).getRagStatus());
        assertEquals("The RAG status was not determined correctly for DATA_ERROR and mapping error", 
                        RagStatus.RED, actual.get(1).getRagStatus());
    }
    
    @SuppressWarnings({ "serial", "unchecked" })
    @Test
    public final void testValueErrorTypeIsDeterminedCorrectly() {
        
        // Set up the test data with the Builder pattern
        
        int jobExecID = this.jobExecIDs.get(0);
        
        List<ReportValueEntity> data = new ArrayList<ReportValueEntity>() {{
            add(ReportValueEntity.builder().errorType(ValueErrorType.PARSE_ERROR).build());
            add(ReportValueEntity.builder().errorType(ValueErrorType.PARSE_WARNING).build());
        }};
        
        // Create the stubs
        when(this.dataValueReportDao.selectReportData(jobExecID)).thenReturn(data);
     
        // Run the test
        Map<ReportType, List<? extends ReportEntity>> results = this.service.getExceptionTableFieldValueReport(jobExecID);
     
        List<ReportValueEntity> actual = (List<ReportValueEntity>) results.get(ReportType.VALUE);
        
        // Assert
        assertEquals("The error type string was not determined correctly for PARSE_ERROR", "Parse error", actual.get(0).getErrorTypeString());
        assertEquals("The error type string was not determined correctly for PARSE_WARNING", "Parse warning", actual.get(1).getErrorTypeString());
    }
}
