/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
