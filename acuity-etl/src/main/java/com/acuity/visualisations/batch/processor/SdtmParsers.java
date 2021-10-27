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

package com.acuity.visualisations.batch.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SdtmParsers {
    private SdtmParsers() {
    }

    public static final String YES = "Yes";
    public static final String NO = "No";

    private static final Pattern SUBJECT_FROM_USUBJID = Pattern.compile("^\\w+[^\\w]+(.)+?");
    private static final Pattern CENTRE_FROM_USUBJID = Pattern.compile("^\\w+[^\\w]+[a-zA-Z]*(\\d\\d\\d\\d).+?");

    public static Integer parseCentre(String val) {
        if (val != null) {
            Matcher matcher = CENTRE_FROM_USUBJID.matcher(val);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return null;
    }


    public static String parseSubject(String val) {
        if (val != null) {
            return SUBJECT_FROM_USUBJID.matcher(val).replaceFirst("$1");
        }
        return null;
    }

    /**
     * Decode dose frequency unit information from SDTM:DOSEFRQ according to this table:
     * <p>
     * DOSING FREQUENCY
     * SDTM     Freq    Unit
     * term
     * QD       1       Day
     * QH		1       Hour
     * QM 		1	    Month
     * QS		1	    Week
     * BID 		2   	Day
     * BIM		2	    Month
     * BIS		2	    Week
     * TID		3	    Day
     * TIS		3	    Week
     * QID		4	    Day
     * QIS		4	    Week
     *
     * @param val
     * @return
     */
    public static Integer parseDoseFrequency(String val) {
        if (val != null) {
            switch (val) {
                case "QD":
                case "QH":
                case "QM":
                case "QS":
                    return 1;

                case "BID":
                case "BIM":
                case "BIS":
                    return 2;

                case "TID":
                case "TIS":
                    return 3;

                case "QID":
                case "QIS":
                    return 4;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Decode dose frequency information from SDTM:DOSEFRQ according to this table:
     * <p>
     * DOSING FREQUENCY
     * SDTM     Freq    Unit
     * term
     * QD       1       Day
     * QH		1       Hour
     * QM 		1	    Month
     * QS		1	    Week
     * BID 		2   	Day
     * BIM		2	    Month
     * BIS		2	    Week
     * TID		3	    Day
     * TIS		3	    Week
     * QID		4	    Day
     * QIS		4	    Week
     *
     * @param val
     * @return
     */
    public static String parseDoseFrequencyUnit(String val) {
        if (val != null) {
            switch (val) {
                case "QD":
                case "BID":
                case "TID":
                case "QID":
                    return "Day";

                case "QH":
                    return "Hour";

                case "QS":
                case "BIS":
                case "TIS":
                case "QIS":
                    return "Week";

                case "QM":
                case "BIM":
                    return "Month";
                default:
                    return null;
            }
        }
        return null;
    }

    public static String parseYesNo(String val) {
        if (val != null) {
            switch (val) {
                case "0":
                case "N":
                case "NO":
                    return NO;
                case "1":
                case "Y":
                case "YES":
                    return YES;
                default:
                    return null;
            }
        }
        return null;
    }
}
