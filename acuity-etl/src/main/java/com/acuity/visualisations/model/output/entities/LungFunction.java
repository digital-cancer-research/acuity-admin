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
