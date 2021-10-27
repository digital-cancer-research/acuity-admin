package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.batch.holders.JobExecutionInfoAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("configurationHolder")
@Scope("prototype")
public class ConfigurationDataHolder extends JobExecutionInfoAware {

    private StudyMappingConfigurationHolder mappingConfiguration;
    private StudyRuntimeConfigurationHolder runtimeConfiguration;
    private StaticConfigurationHolder staticConfigurationHolder;

    public void setMappingConfiguration(StudyMappingConfigurationHolder mappingConfiguration) {
        this.mappingConfiguration = mappingConfiguration;
    }

    public void setRuntimeConfiguration(StudyRuntimeConfigurationHolder runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
    }

    public void setStaticConfigurationHolder(StaticConfigurationHolder staticConfigurationHolder) {
        this.staticConfigurationHolder = staticConfigurationHolder;
    }

    public StudyMappingConfigurationHolder getMappingConfiguration() {
        return mappingConfiguration;
    }

    public StudyRuntimeConfigurationHolder getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    public StaticConfigurationHolder getStaticConfigurationHolder() {
        return staticConfigurationHolder;
    }

    ConfigurationUtil<?> createConfigurationUtil() {
        ConfigurationUtil<?> configurationUtil = mappingConfiguration.createConfigurationUtil();
        configurationUtil.setStaticConfigurationHolder(staticConfigurationHolder);
        return configurationUtil;
    }

}
