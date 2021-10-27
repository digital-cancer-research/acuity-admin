package com.acuity.visualisations.batch.tasklet;

import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.processor.DataAlertReport;
import com.acuity.visualisations.batch.util.DataInsertTracker;
import com.acuity.visualisations.dal.dao.MedicineDao;
import com.acuity.visualisations.model.output.entities.Patient;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.fest.assertions.Fail;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("dataIntegrityValidationTasklet")
@Scope("step")
public class DataIntegrityValidationTasklet extends HoldersAware implements Tasklet {

    @Autowired
    private MedicineDao patientDao;

    DataAlertReport dataAlertReport = null;

    private static final String PATIENT_ENTITY = "Patient";

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        System.out.println("--------------------------------------------------------");
        final HashMap<String, String> returnedData = new HashMap<String, String>();

        Map<String, Object> jobMap = chunkContext.getStepContext().getJobParameters();
        String studyCode = (String) jobMap.get("etl.study");
        String projectName = (String) jobMap.get("etl.project");

        // Get the DataInsertTracker singleton
        DataInsertTracker dataInsertTracker = DataInsertTracker.getInstance();
        int etlCount = dataInsertTracker.getEntityCount(projectName, studyCode, Patient.class);

        JdbcTemplate jdbcTemplate = patientDao.getJdbcTemplate();

        // This could be improved - avoid building up a SQL string like this
        String sql = "SELECT p.PAT_UNQ_SHA1, p.PAT_ID ";
        sql += "FROM patient p, study s, project pr ";
        sql += "WHERE p.pat_std_id = s.std_id ";
        sql += "AND s.std_prj_id = pr.prj_id ";
        sql += "AND s.std_name = '" + studyCode + "' ";
        sql += "AND pr.prj_name = '" + projectName + "' ";

        jdbcTemplate.query(sql, (ResultSet rs) -> {
                    while (rs.next()) {
                        String hash = rs.getString("PAT_UNQ_SHA1");
                        String id = rs.getString("PAT_ID");
                        returnedData.put(hash, id);
                    }
                    return returnedData;
                }
        );

        int dbCount = returnedData.size();

        if( dbCount != etlCount ) {
            // Insert a record into the dataAlertReport
            dataAlertReport.addToReport(studyCode, PATIENT_ENTITY, new Integer(etlCount).toString(), new Integer(dbCount).toString());

            Fail.fail( "Database count for Patients does not match the ETL value" );
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    protected void initHolders() {
        dataAlertReport = getDataAlertReport();
    }

}
