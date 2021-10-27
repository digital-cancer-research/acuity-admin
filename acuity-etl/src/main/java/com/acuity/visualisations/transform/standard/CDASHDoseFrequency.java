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

package com.acuity.visualisations.transform.standard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CDASHDoseFrequency {
    private final String name;
    private final Integer freq;
    private final String unit;
    private final Double rank;

    public String getName() {
        return name;
    }

    public Double getRank() {
        return rank;
    }

    public Integer getFreq() {
        return freq;
    }

    public String getUnit() {
        return unit;
    }

    private CDASHDoseFrequency(String name, Integer freq, String unit, Double rank) {
        this.name = name;
        this.freq = freq;
        this.unit = unit;
        this.rank = rank;
    }

    private static final String HOUR = "hour";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    private static final List<CDASHDoseFrequency> DOSE_FREQUENCIES = Arrays.asList(
            new CDASHDoseFrequency("QH", 1, HOUR, 24.0),
            new CDASHDoseFrequency("Q2H", 12, DAY, 12.0),
            new CDASHDoseFrequency("Q4H", 6, DAY, 6.0),
            new CDASHDoseFrequency("QID", 4, DAY, 4.0),
            new CDASHDoseFrequency("TID", 3, DAY, 3.0),
            new CDASHDoseFrequency("BID", 2, DAY, 2.0),
            new CDASHDoseFrequency("QD", 1, DAY, 1.0),
            new CDASHDoseFrequency("4 Times per Week", 4, WEEK, 0.5714),
            new CDASHDoseFrequency("QOD", 178, YEAR, 0.5),
            new CDASHDoseFrequency("3 Times per Week", 3, WEEK, 0.4285),
            new CDASHDoseFrequency("2 Times per Week", 2, WEEK, 0.2857),
            new CDASHDoseFrequency("Every Week", 1, WEEK, 0.1428),
            new CDASHDoseFrequency("Every 2 Weeks", 26, YEAR, 0.0714),
            new CDASHDoseFrequency("Every 3 Weeks", null, null, 0.0476),
            new CDASHDoseFrequency("Every 4 Weeks", 13, YEAR, 0.0357),
            new CDASHDoseFrequency("QM", 1, MONTH, 0.0333),
            new CDASHDoseFrequency("Once", null, null, 0.0027),
            new CDASHDoseFrequency("PRN", null, null, 0.000_000_000_2),
            new CDASHDoseFrequency("Other", null, null, 0.000_000_000_1),
            new CDASHDoseFrequency("Unknown", null, null, 0.0)
    );

    private static final Map<String, CDASHDoseFrequency> DOSE_FREQUENCY_BY_NAME = new HashMap<>();

    static {
        for (CDASHDoseFrequency doseFrequency : DOSE_FREQUENCIES) {
            DOSE_FREQUENCY_BY_NAME.put(doseFrequency.getName(), doseFrequency);
        }
    }

    public static Double getRank(Integer frequency, String frequencyUnitString) {
        if (frequency != null && frequencyUnitString != null) {
            frequencyUnitString = frequencyUnitString.toLowerCase();
            switch (frequencyUnitString) {
                case HOUR:
                    return frequency * 24d;
                case DAY:
                    return frequency * 1d;
                case WEEK:
                    return frequency / 7d;
                case MONTH:
                    return frequency / 30d;
                case YEAR:
                    return frequency / 360d;
                default:
                    return 0d;
            }
        }
        return 0d;
    }

    public static CDASHDoseFrequency getByName(String dosingFrequencyName) {
        return DOSE_FREQUENCY_BY_NAME.get(dosingFrequencyName);
    }

    public static String getCdashNameBySdtmName(String dosingFrequencyName) {
        if (dosingFrequencyName == null) {
            return dosingFrequencyName;
        }

        switch (dosingFrequencyName) {
            case "QD":
            case "QH":
            case "QM":
            case "BID":
            case "TID":
            case "QID":
                return dosingFrequencyName;
            case "QS":
                return  "Every Week";
            case "BIS":
                return "2 Times per Week";
            case "TIS":
                return  "3 Times per Week";
            case "QIS":
                return  "4 Times per Week";
            default:
                return  "Other";
        }
    }

    public static CDASHDoseFrequency getByFrequencyAndUnit(Integer frequency, String frequencyUnitString) {
        if (frequency != null && frequencyUnitString != null) {
            frequencyUnitString = frequencyUnitString.toLowerCase();

            for (CDASHDoseFrequency doseFrequency : DOSE_FREQUENCIES) {
                if (frequency.equals(doseFrequency.getFreq()) && frequencyUnitString.equals(doseFrequency.getUnit())) {
                    return doseFrequency;
                }
            }
        }
        return null;
    }
}
