package com.acuity.visualisations.model.output;

/**
 * Created by knml167 on 18/12/2014.
 */
public interface PivotableEntity<V> {

    boolean isPivotedField(String fieldName);

    void setPivotedCategoryValue(String sourceField, String category, V value);

}
