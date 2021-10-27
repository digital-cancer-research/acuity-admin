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
