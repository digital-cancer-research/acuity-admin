package com.acuity.visualisations.model.output;

import java.util.Set;

public interface FieldDelegatingEntity {

    Set<String> getFieldNames();

    void setField(String name, Object value);

    Object getField(String name);

}
