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

import com.acuity.visualisations.transform.entity.EntitiesRootRule;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRootRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRule;
import com.acuity.visualisations.transform.table.TableRule;
import com.acuity.visualisations.transform.table.TablesRootRule;

public interface StaticConfigurationHolder {

    EntityDescriptionRule getEntityRule(String entityName);

    void setEntityConf(EntitiesRootRule entitiesRootRule);

    TableRule getTableRule(String tableName);

    void setTableConf(TablesRootRule tablesRootRule);

    EntityTablesRule getEntityTablesRule(String tableName);

    EntityTablesRule getEntityTablesRuleByEntityName(String entityName);

    void setTableToEntityConf(EntityTablesRootRule entityTablesRootRule);

    String getTableByEntity(String entityName);

}
