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

import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.model.output.PivotableEntity;
import com.acuity.visualisations.model.output.SmartEntity;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class Vital extends VitalThin implements PivotableEntity<BigDecimal>, SmartEntity {

    private String sourceField;

    private static final Map<String, VitalField> PIVOTED_FIELDS = new HashMap<>();

    private static class VitalField {
        private final String fieldName;
        private final String testName;
        private final String resultUnit;
        private final String physicalPosition;
        private final String anatomicalLocation;

        VitalField(String fieldName, String testName, String resultUnit, String physicalPosition, String anatomicalLocation) {
            this.fieldName = fieldName;
            this.testName = testName;
            this.resultUnit = resultUnit;
            this.physicalPosition = physicalPosition;
            this.anatomicalLocation = anatomicalLocation;
        }
    }

    static {
        PIVOTED_FIELDS.put("pulsesup", new VitalField("Supine Pulse (beats/min)", "Pulse Rate", "Beats/Min", "Supine", null));
        PIVOTED_FIELDS.put("sbpsup", new VitalField("Supine SBP (mmHg)", "Systolic Blood Pressure", "mmHg", "Supine", null));
        PIVOTED_FIELDS.put("dbpsup", new VitalField("Supine DBP (mmHg)", "Diastolic Blood Pressure", "mmHg", "Supine", null));
        PIVOTED_FIELDS.put("pulsesta", new VitalField("Standing Pulse (beats/min)", "Pulse Rate", "Beats/Min", "Standing", null));
        PIVOTED_FIELDS.put("sbpsta", new VitalField("Standing SBP (mmHg)", "Systolic Blood Pressure", "mmHg", "Standing", null));
        PIVOTED_FIELDS.put("dbpsta", new VitalField("Standing DBP (mmHg)", "Diastolic Blood Pressure", "mmHg", "Standing", null));
        PIVOTED_FIELDS.put("pulsesit", new VitalField("Sitting Pulse (beats/min)", "Pulse Rate", "Beats/Min", "Sitting", null));
        PIVOTED_FIELDS.put("sbpsit", new VitalField("Sitting SBP (mmHg)", "Systolic Blood Pressure", "mmHg", "Sitting", null));
        PIVOTED_FIELDS.put("dbpsit", new VitalField("Sitting DBP (mmHg)", "Diastolic Blood Pressure", "mmHg", "Sitting", null));
        PIVOTED_FIELDS.put("pulse", new VitalField("Pulse (beats/min)", "Pulse Rate", "Beats/Min", null, null));
        PIVOTED_FIELDS.put("sbp", new VitalField("SBP (mmHg)", "Systolic Blood Pressure", "mmHg", null, null));
        PIVOTED_FIELDS.put("dbp", new VitalField("DBP (mmHg)", "Diastolic Blood Pressure", "mmHg", null, null));
        PIVOTED_FIELDS.put("map", new VitalField("Mean Arterial Pressure (mmHg)", "Mean Arterial Pressure", "mmHg", null, null));
        PIVOTED_FIELDS.put("bi", new VitalField("Inter-beat Interval (ms)", "Inter-beat Interval", "msec", null, null));
        PIVOTED_FIELDS.put("hratesup", new VitalField("Supine Heart Rate (beats/min)", "Heart Rate", "Beats/Min", "Supine", null));
        PIVOTED_FIELDS.put("hratesta", new VitalField("Standing Heart Rate (beats/min)", "Heart Rate", "Beats/Min", "Standing", null));
        PIVOTED_FIELDS.put("hratesit", new VitalField("Sitting Heart Rate (beats/min)", "Heart Rate", "Beats/Min", "Sitting", null));
        PIVOTED_FIELDS.put("hrate", new VitalField("Heart Rate (beats/min)", "Heart Rate", "Beats/Min", null, null));
        PIVOTED_FIELDS.put("rratesup", new VitalField("Supine Respiration Rate (breaths/min)", "Respiratory Rate", "Breaths/Min", "Supine", null));
        PIVOTED_FIELDS.put("rratesta", new VitalField("Standing Respiration Rate (breaths/min)", "Respiratory Rate", "Breaths/Min", "Standing", null));
        PIVOTED_FIELDS.put("rratesit", new VitalField("Sitting Respiration Rate (breaths/min)", "Respiratory Rate", "Breaths/Min", "Sitting", null));
        PIVOTED_FIELDS.put("rrate", new VitalField("Respiration Rate (breaths/min)", "Respiratory Rate", "Breaths/Min", null, null));
        PIVOTED_FIELDS.put("ctempora", new VitalField("Body Temperature, Oral (C)", "Temperature", "C", null, "Oral Cavity"));
        PIVOTED_FIELDS.put("ctemprec", new VitalField("Body Temperature, Rectal (C)", "Temperature", "C", null, "Rectum"));
        PIVOTED_FIELDS.put("ctemptym", new VitalField("Body Temperature, Tympanic (C)", "Temperature", "C", null, "Tympanic Membrane"));
        PIVOTED_FIELDS.put("ctempaxi", new VitalField("Body Temperature, Axillary (C)", "Temperature", "C", null, "Axilla"));
        PIVOTED_FIELDS.put("ctempfor", new VitalField("Body Temperature, Forehead (C)", "Temperature", "C", null, "Forehead"));
        PIVOTED_FIELDS.put("ctemp", new VitalField("Body Temperature (C)", "Temperature", "C", null, null));
        PIVOTED_FIELDS.put("ftempora", new VitalField("Body Temperature, Oral (F)", "Temperature", "F", null, "Oral Cavity"));
        PIVOTED_FIELDS.put("ftemprec", new VitalField("Body Temperature, Rectal (F)", "Temperature", "F", null, "Rectum"));
        PIVOTED_FIELDS.put("ftemptym", new VitalField("Body Temperature, Tympanic (F)", "Temperature", "F", null, "Tympanic Membrane"));
        PIVOTED_FIELDS.put("ftempaxi", new VitalField("Body Temperature, Axillary (F)", "Temperature", "F", null, "Axilla"));
        PIVOTED_FIELDS.put("ftempfor", new VitalField("Body Temperature, Forehead (F)", "Temperature", "F", null, "Forehead"));
        PIVOTED_FIELDS.put("ftemp", new VitalField("Body Temperature (F)", "Temperature", "F", null, null));
        PIVOTED_FIELDS.put("ctemprm", new VitalField("Room Temperature (C)", "Room Temperature", "C", null, null));
        PIVOTED_FIELDS.put("ftemprm", new VitalField("Room Temperature (F)", "Room Temperature", "F", null, null));
        PIVOTED_FIELDS.put("spo2", new VitalField("Peripheral Oxygen Saturation (%)", "Oxygen Saturation", "%", null, "Peripheral"));
        PIVOTED_FIELDS.put("sao2", new VitalField("Arterial Oxygen Saturation (%)", "Oxygen Saturation", "%", null, "Aorta"));
        PIVOTED_FIELDS.put("weight_g", new VitalField("Weight (g)", "Weight", "g", null, null));
        PIVOTED_FIELDS.put("weight_p", new VitalField("Weight (lb)", "Weight", "LB", null, null));
        PIVOTED_FIELDS.put("height", new VitalField("Height (cm)", "Height", "cm", null, null));
        PIVOTED_FIELDS.put("weight", new VitalField("Weight (kg)", "Weight", "kg", null, null));
    }


    @Override
    public boolean isPivotedField(String field) {
        return PIVOTED_FIELDS.containsKey(field);
    }

    @Override
    public void setPivotedCategoryValue(String sourceField, String category, BigDecimal value) {
        this.testName = category;
        this.testResult = value;
        this.sourceField = sourceField;
    }

    @Override
    public String getTestGuid() {
        return testGuid;
    }

    @Override
    public void setTestGuid(String testGuid) {
        this.testGuid = testGuid;
    }

    @Override
    public void complete() {

        if (sourceField != null) {
            VitalField descr = PIVOTED_FIELDS.get(sourceField.toLowerCase());
            if (descr != null) {
                testName = descr.testName;
                resultUnit = descr.resultUnit;
                physicalPosition = descr.physicalPosition;
                anatomicalLocation = descr.anatomicalLocation;
            }
        }
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject)
                .append("part", part).append("testName", testName).append("date", date)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject)
                .append("part", part).append("date", date)
                .append("lpdat", lpdat).append("lpdos", lpdos)
                .append("testName", testName).append("testResult", testResult).append("unit", resultUnit)
                .toString();
    }

}
