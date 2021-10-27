package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.ProjectRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBConfigurationUtil extends ConfigurationUtilCommonImpl<DBStudyMappingConfigurationHolder> {
    private DBStudyMappingConfigurationHolder mappingConfigurationHolder;
    private Set<String> skippedEntities = new HashSet<>();

    public void setMappingConfigurationHolder(DBStudyMappingConfigurationHolder mappingConfigurationHolder) {
        this.mappingConfigurationHolder = mappingConfigurationHolder;
    }

    @Override
    public List<DataFileRule> getCntlinFileRules() {
        return mappingConfigurationHolder.getCntlinFileRules();
    }

    @Override
    public void addSkippedEntities(List<String> entityNames) {
        skippedEntities.addAll(entityNames);
    }

    @Override
    public List<String> getSkippedEntities() {
        return new ArrayList<>(skippedEntities);
    }


    @Override
    protected ProjectRule getProjectRule() {
        return mappingConfigurationHolder.getProjectRule();
    }

}
