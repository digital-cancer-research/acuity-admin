package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.batch.holders.JobExecutionInfoAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("configurationUtilHolder")
@Scope("prototype")
public class ConfigurationUtilHolder extends JobExecutionInfoAware {

    private ConfigurationDataHolder configurationDataHolder;
    private ConfigurationUtil<?> configurationUtil;

    public void setConfigurationDataHolder(ConfigurationDataHolder configurationDataHolder) {
        this.configurationDataHolder = configurationDataHolder;
    }

    public ConfigurationUtil<?> getConfigurationUtil() {
        if (configurationUtil == null) {
            configurationUtil = configurationDataHolder.createConfigurationUtil();
        }
        return configurationUtil;
    }

}
