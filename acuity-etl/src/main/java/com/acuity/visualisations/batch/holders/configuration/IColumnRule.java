package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.util.List;

public interface IColumnRule {
    String getName();

    Mapper getMapper();

    String getField();

    String getFmtname();

    String getFmtdefault();

    String getType();

    ParserRule getHelper();

    String getDefault();

    Boolean isPart();

    String getAggrFunction();

    boolean getSubjectField();

    boolean isRadio();

    List<String> getExcludingValues();

    String getDescription();

    void setDescription(String description);
}
