package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.output.PivotableEntity;
import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by knml167 on 30/07/2015.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunction extends TimestampedEntity implements PivotableEntity<BigDecimal>, SmartEntity {
    private static final Map<String, String> PIVOTED_FIELDS = new HashMap<>();

    static {
        PIVOTED_FIELDS.put("fev1l", "FEV1 (L)");
        PIVOTED_FIELDS.put("fev1perc", "FEV1 (%)");
        PIVOTED_FIELDS.put("fvcl", "FVC (L)");
    }

    protected String part;
    protected String subject;

    private String patientGuid;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;
    private BigDecimal visit;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime assessDate;
    private String protocolSchedule;

    private String sourceField;
    protected String testName;
    protected BigDecimal testResult;
    private BigDecimal diffusingCapacity;
    private BigDecimal totalLungCapacity;
    private BigDecimal inspiredOxFraction;


    @Override
    public boolean isPivotedField(String fieldName) {
        return PIVOTED_FIELDS.containsKey(fieldName);
    }

    @Override
    public void setPivotedCategoryValue(String sourceField, String category, BigDecimal value) {
        this.testName = category;
        this.testResult = value;
        this.sourceField = sourceField;
    }

    @Override
    public void complete() {
        if (sourceField != null) {
            String descr = PIVOTED_FIELDS.get(sourceField.toLowerCase());
            if (descr != null) {
                testName = descr;
            }
        }
    }
}
