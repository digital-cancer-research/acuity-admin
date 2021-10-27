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
