package com.acuity.visualisations.batch.reader;

import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.util.SerializationUtil;
import org.json.JSONException;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SkippedFilesAndHoldersAware extends HoldersAware {

    public static final String UNPARSED_FILES_TAG = "UNPARSED_FILES";
    public static final String COMPLETED_WITH_SKIPS = "COMPLETED WITH SKIPS";

    private DataCommonReport report;
    
    private ConfigurationUtil<?> configurationUtil;
    
    private List<String> skippedFiles = new ArrayList<String>();

    @Override
    protected void initHolders() {
        this.configurationUtil = getConfigurationUtil();
        this.report = getDataCommonReport();
    }
    
    protected void addSkippedItem(String fileName) {
        skippedFiles.add(fileName);
        configurationUtil.addSkippedEntities(configurationUtil.getEntities(fileName));
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (skippedFiles.isEmpty() && configurationUtil.getSkippedEntities().isEmpty()) {
            return stepExecution.getExitStatus();
        } else {
            Map<String, List<String>> messageContent = new HashMap<>();
            messageContent.put(UNPARSED_FILES_TAG, skippedFiles);
            for (String fileName : skippedFiles) {
                
                // This will set the number of subjects to zero and will display a red status
                report.getFileReport(fileName);
                
                // This will set the ACUITY entity name for any failing files.
                List<String> entitiesForFile = this.configurationUtil.getEntities(fileName);
                for (String entityName : entitiesForFile) {
                    report.getFileReport(fileName).addAcuityEntity(entityName);
                }
            }
            try {
                String exitDescription = SerializationUtil.serializeMap(messageContent);
                return new ExitStatus(COMPLETED_WITH_SKIPS, exitDescription);
            } catch (JSONException e) {
                return new ExitStatus(COMPLETED_WITH_SKIPS);
            }
        }
    }
}
