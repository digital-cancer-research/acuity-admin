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

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.transform.standard.CDASHDoseFrequency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
public class MedDosingSchedule extends TimestampedEntity implements SmartEntity {
    private String patientGuid;
    private BigDecimal dose;
    private String doseUnit;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;

    private Integer frequency;
    private String freqName;
    private Double freqRank;
    private String frequenceUnit;
    private String comment;
    private String actionTaken;
    private String reasonForActionTaken;
    private String subject;
    private String part;
    private String drug;
    private BigDecimal totalDailyDose;
    private String treatmentCycleDelayed;
    private String reasonTreatmentCycleDelayed;
    private String reasonTreatmentCycleDelayedOther;

    private String medicationCode;
    private String medicationDictionaryText;
    private String atcCode;
    private String atcDictionaryText;
    private String drugPreferredName;
    private String medicationGroupingName;
    private Object[] activeIngredient;
    private String studyDrugCategory;
    private BigDecimal plannedDose;
    private String plannedDoseUnits;
    private Integer plannedNoDaysTreatment;
    private Object[] aeNumCausedActionTaken;
    private String formulation;
    private String route;
    private String reasonForTherapy;
    private Object[] aeNumCausedTreatmentCycleDelayed;

    private String aeNumsCausedActionTaken;
    private String aeNumsCausedCycleDelayed;
    private String activeIngredientOutput;

    public String getActiveIngredientOutput() {
        return processToString(activeIngredient);
    }

    public String getAeNumsCausedActionTaken() {
        return processToString(aeNumCausedActionTaken);
    }

    public String getAeNumsCausedCycleDelayed() {
        return processToString(aeNumCausedTreatmentCycleDelayed);
    }

    private String processToString(Object[] input) {
        if (input != null) {
            return Stream.of(input)
                    .map(String::valueOf)
                    .distinct()
                    .collect(Collectors.joining(","));
        } else {
            return null;
        }
    }

    public String getFrequencyUnit() {
        return frequenceUnit;
    }

    public void setFrequencyUnit(String frequenceUnit) {
        this.frequenceUnit = frequenceUnit;
    }


    @Override
    public void complete() {
        //RCT-4391: if a unit value is an integer number, don't show the value. Otherwise show it
        if (StringUtils.isNumericSpace(doseUnit)) {
            doseUnit = null;
        }

        if (isEmpty(freqName)) {
            CDASHDoseFrequency doseFrequency = CDASHDoseFrequency.getByFrequencyAndUnit(frequency, frequenceUnit);
            if (doseFrequency == null) {
                freqName = isEmpty(frequenceUnit) && frequency == null ? null
                        : (frequency == null ? "?" : frequency) + "-per-" + (isEmpty(frequenceUnit) ? "?" : frequenceUnit);
                freqRank = CDASHDoseFrequency.getRank(frequency, frequenceUnit);
            } else {
                freqName = doseFrequency.getName();
                freqRank = doseFrequency.getRank();
            }
        } else {
            CDASHDoseFrequency doseFrequency = CDASHDoseFrequency.getByName(freqName);
            if (doseFrequency == null) {
                freqRank = 0d;
            } else {
                freqRank = doseFrequency.getRank();

                // than try to support old fields
                if (frequency == null && doseFrequency.getFreq() != null) {
                    frequency = doseFrequency.getFreq();
                }
                if (isEmpty(frequenceUnit) && doseFrequency.getUnit() != null) {
                    frequenceUnit = doseFrequency.getUnit();
                }
            }
        }
    }

    /**
     * RCT-4146: Dose data standardisation should happen in one place
     * DISCARD EVENTS WITHOUT TIMINGS
     * Discard all dose events where the start dateTime is null.
     * This is because dose events with no timing are not of any use in ACUITY
     * <p>
     * DISCARD NONSENSE DOSE RECORDS
     * If the dose start datetime occurs at or after the dose end datetime, discard the record.
     * If the dose per admin is null then it is neither a confirmation of dosing nor a confirmation of inactive dosing, so discard these too.
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return startDate != null && (endDate == null || startDate.isBefore(endDate)) && dose != null;
    }
}
