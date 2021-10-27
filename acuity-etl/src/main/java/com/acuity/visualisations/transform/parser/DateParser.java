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

package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.acuity.visualisations.data.util.Util.isEmpty;

public class DateParser extends AbstractParser<Temporal> {

    private static final Set<String> SEPARATORS = new HashSet<>();

    static {
        SEPARATORS.add("/");
        SEPARATORS.add("-");
        SEPARATORS.add(" ");
    }

    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final String DEFAULT_DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss.S";

    private enum DatePattern {
        // month first
        YYYY_MM_DD, MM_DD_YYYY, M_DD_YYYY, MM_D_YYYY, M_D_YYYY, YYYY_MM, MM_DD_YYYY_HH_mm_ss, M_DD_YYYY_HH_mm_ss,
        MM_D_YYYY_HH_mm_ss, M_D_YYYY_HH_mm_ss, MM_DD_YYYY_H_mm, M_DD_YYYY_H_mm, MM_D_YYYY_H_mm, M_D_YYYY_H_mm,
        MON_DD_YYYY, MON_D_YYYY, MON_DD_YY, MON_D_YY, YYYY_MON_DD, YYYY_MON_D, YY_MON_DD, YY_MON_D,
        YYYY_MM_DDTHH_MM, YYYY_MM_DDTHH_MM_SS,
        // day first
        YYYY_DD_MM, DD_MM_YYYY, D_MM_YYYY, DD_M_YYYY, D_M_YYYY, YYYY_DD, DD_MM_YYYY_HH_mm_ss, D_MM_YYYY_HH_mm_ss,
        DD_M_YYYY_HH_mm_ss, D_M_YYYY_HH_mm_ss, DD_MM_YYYY_H_mm, D_MM_YYYY_H_mm, DD_M_YYYY_H_mm, D_M_YYYY_H_mm,
        DD_MON_YYYY, DDMONYYYY, D_MON_YYYY, DMONYYYY, DD_MON_YY, D_MON_YY, YYYY_DD_MON, YYYY_D_MON, YY_DD_MON, YY_D_MON,
        //
        YYYY;

        private static List<DatePattern> dayFirstPatterns = new ArrayList<>();

        static {
            dayFirstPatterns.add(YYYY_DD_MM);
            dayFirstPatterns.add(DD_MM_YYYY);
            dayFirstPatterns.add(D_MM_YYYY);
            dayFirstPatterns.add(DD_M_YYYY);
            dayFirstPatterns.add(D_M_YYYY);
            dayFirstPatterns.add(YYYY_DD);
            dayFirstPatterns.add(DD_MM_YYYY_HH_mm_ss);
            dayFirstPatterns.add(D_MM_YYYY_HH_mm_ss);
            dayFirstPatterns.add(DD_M_YYYY_HH_mm_ss);
            dayFirstPatterns.add(D_M_YYYY_HH_mm_ss);
            dayFirstPatterns.add(DD_MM_YYYY_H_mm);
            dayFirstPatterns.add(D_MM_YYYY_H_mm);
            dayFirstPatterns.add(DD_M_YYYY_H_mm);
            dayFirstPatterns.add(D_M_YYYY_H_mm);
            dayFirstPatterns.add(MON_DD_YYYY);
            dayFirstPatterns.add(MON_D_YYYY);
            dayFirstPatterns.add(MON_DD_YY);
            dayFirstPatterns.add(MON_D_YY);
            dayFirstPatterns.add(DD_MON_YYYY);
            dayFirstPatterns.add(DDMONYYYY);
            dayFirstPatterns.add(D_MON_YYYY);
            dayFirstPatterns.add(DMONYYYY);
            dayFirstPatterns.add(DD_MON_YY);
            dayFirstPatterns.add(D_MON_YY);
            dayFirstPatterns.add(YYYY_MON_DD);
            dayFirstPatterns.add(YYYY_MON_D);
            dayFirstPatterns.add(YY_MON_D);
            dayFirstPatterns.add(YYYY_DD_MON);
            dayFirstPatterns.add(YYYY_D_MON);
            dayFirstPatterns.add(YY_DD_MON);
            dayFirstPatterns.add(YY_D_MON);
            dayFirstPatterns.add(YYYY);
        }

        private static List<DatePattern> monthFirstPatterns = new ArrayList<>();

        static {
            monthFirstPatterns.add(MM_DD_YYYY);
            monthFirstPatterns.add(YYYY_MM_DD);
            monthFirstPatterns.add(M_DD_YYYY);
            monthFirstPatterns.add(MM_D_YYYY);
            monthFirstPatterns.add(M_D_YYYY);
            monthFirstPatterns.add(YYYY_MM);
            monthFirstPatterns.add(MM_DD_YYYY_HH_mm_ss);
            monthFirstPatterns.add(M_DD_YYYY_HH_mm_ss);
            monthFirstPatterns.add(MM_D_YYYY_HH_mm_ss);
            monthFirstPatterns.add(M_D_YYYY_HH_mm_ss);
            monthFirstPatterns.add(MM_DD_YYYY_H_mm);
            monthFirstPatterns.add(M_DD_YYYY_H_mm);
            monthFirstPatterns.add(MM_D_YYYY_H_mm);
            monthFirstPatterns.add(M_D_YYYY_H_mm);
            monthFirstPatterns.add(MON_DD_YYYY);
            monthFirstPatterns.add(MON_D_YYYY);
            monthFirstPatterns.add(MON_DD_YY);
            monthFirstPatterns.add(MON_D_YY);
            monthFirstPatterns.add(DD_MON_YYYY);
            monthFirstPatterns.add(DDMONYYYY);
            monthFirstPatterns.add(D_MON_YYYY);
            monthFirstPatterns.add(DMONYYYY);
            monthFirstPatterns.add(D_MON_YY);
            monthFirstPatterns.add(YYYY_MON_DD);
            monthFirstPatterns.add(YYYY_MON_D);
            monthFirstPatterns.add(YY_MON_DD);
            monthFirstPatterns.add(YY_MON_D);
            monthFirstPatterns.add(YYYY_DD_MON);
            monthFirstPatterns.add(YYYY_D_MON);
            monthFirstPatterns.add(YY_DD_MON);
            monthFirstPatterns.add(YY_D_MON);
            monthFirstPatterns.add(YYYY_MM_DDTHH_MM);
            monthFirstPatterns.add(YYYY_MM_DDTHH_MM_SS);
            monthFirstPatterns.add(YYYY);
        }

        static List<DatePattern> getDatePatterns(ParserRule parserRule) {
            switch (parserRule) {
                case DAY_FIRST:
                    return dayFirstPatterns;
                case MONTH_FIRST:
                    return monthFirstPatterns;
                default:
                    return Collections.emptyList();
            }
        }

        static String getPattern(DatePattern name) {
            switch (name) {
                case YYYY_MM_DD:
                case YYYY_DD_MM:
                    return "\\d{4}{sep}\\d{2}{sep}\\d{2}";
                case DD_MM_YYYY:
                case MM_DD_YYYY:
                    return "\\d{2}{sep}\\d{2}{sep}\\d{4}";
                case D_MM_YYYY:
                case M_DD_YYYY:
                    return "\\d{1}{sep}\\d{2}{sep}\\d{4}";
                case DD_M_YYYY:
                case MM_D_YYYY:
                    return "\\d{2}{sep}\\d{1}{sep}\\d{4}";
                case D_M_YYYY:
                case M_D_YYYY:
                    return "\\d{1}{sep}\\d{1}{sep}\\d{4}";
                case YYYY_DD:
                case YYYY_MM:
                    return "\\d{4}{sep}\\d{2}";
                case DD_MM_YYYY_HH_mm_ss:
                case MM_DD_YYYY_HH_mm_ss:
                    return "\\d{2}{sep}\\d{2}{sep}\\d{4} \\d{2}:\\d{2}:\\d{2}";
                case YYYY_MM_DDTHH_MM:
                    return "\\d{4}{sep}\\d{2}{sep}\\d{2}T\\d{2}:\\d{2}";
                case YYYY_MM_DDTHH_MM_SS:
                    return "\\d{4}{sep}\\d{2}{sep}\\d{2}T\\d{2}:\\d{2}:\\d{2}";
                case D_MM_YYYY_HH_mm_ss:
                case M_DD_YYYY_HH_mm_ss:
                    return "\\d{1}{sep}\\d{2}{sep}\\d{4} \\d{2}:\\d{2}:\\d{2}";
                case DD_M_YYYY_HH_mm_ss:
                case MM_D_YYYY_HH_mm_ss:
                    return "\\d{2}{sep}\\d{1}{sep}\\d{4} \\d{2}:\\d{2}:\\d{2}";
                case D_M_YYYY_HH_mm_ss:
                case M_D_YYYY_HH_mm_ss:
                    return "\\d{1}{sep}\\d{1}{sep}\\d{4} \\d{2}:\\d{2}:\\d{2}";
                case DD_MM_YYYY_H_mm:
                case MM_DD_YYYY_H_mm:
                    return "\\d{2}{sep}\\d{2}{sep}\\d{4} \\d{1}:\\d{2}";
                case D_MM_YYYY_H_mm:
                case M_DD_YYYY_H_mm:
                    return "\\d{1}{sep}\\d{2}{sep}\\d{4} \\d{1}:\\d{2}";
                case DD_M_YYYY_H_mm:
                case MM_D_YYYY_H_mm:
                    return "\\d{2}{sep}\\d{1}{sep}\\d{4} \\d{1}:\\d{2}";
                case D_M_YYYY_H_mm:
                case M_D_YYYY_H_mm:
                    return "\\d{1}{sep}\\d{1}{sep}\\d{4} \\d{1}:\\d{2}";
                case DD_MON_YYYY:
                    return "\\d{2}{sep}[a-zA-Z]{3}{sep}\\d{4}";
                case DDMONYYYY:
                    return "\\d{2}[a-zA-Z]{3}\\d{4}";
                case MON_DD_YYYY:
                    return "[a-zA-Z]{3}{sep}\\d{2}{sep}\\d{4}";
                case MON_D_YYYY:
                    return "[a-zA-Z]{3}{sep}\\d{1}{sep}\\d{4}";
                case D_MON_YYYY:
                    return "\\d{1}{sep}[a-zA-Z]{3}{sep}\\d{4}";
                case DMONYYYY:
                    return "\\d{1}[a-zA-Z]{3}\\d{4}";
                case DD_MON_YY:
                    return "\\d{2}{sep}[a-zA-Z]{3}{sep}\\d{2}";
                case MON_DD_YY:
                    return "[a-zA-Z]{3}{sep}\\d{2}{sep}\\d{2}";
                case MON_D_YY:
                    return "[a-zA-Z]{3}{sep}\\d{1}{sep}\\d{2}";
                case D_MON_YY:
                    return "\\d{1}{sep}[a-zA-Z]{3}{sep}\\d{2}";
                case YYYY_MON_DD:
                    return "\\d{4}{sep}[a-zA-Z]{3}{sep}\\d{2}";
                case YYYY_MON_D:
                    return "\\d{4}{sep}[a-zA-Z]{3}{sep}\\d{1}";
                case YY_MON_DD:
                    return "\\d{2}{sep}[a-zA-Z]{3}{sep}\\d{2}";
                case YY_MON_D:
                    return "\\d{2}{sep}[a-zA-Z]{3}{sep}\\d{1}";
                case YYYY_DD_MON:
                    return "\\d{4}{sep}\\d{2}{sep}[a-zA-Z]{3}";
                case YYYY_D_MON:
                    return "\\d{4}{sep}\\d{1}{sep}[a-zA-Z]{3}";
                case YY_DD_MON:
                    return "\\d{2}{sep}\\d{2}{sep}[a-zA-Z]{3}";
                case YY_D_MON:
                    return "\\d{2}{sep}\\d{1}{sep}[a-zA-Z]{3}";
                case YYYY:
                    return "\\d{4}";
                default:
                    return null;
            }
        }

        static String getFormat(DatePattern name) {
            switch (name) {
                case YYYY_MM_DD:
                    return "yyyy{sep}MM{sep}dd";
                case YYYY_DD_MM:
                    return "yyyy{sep}dd{sep}MM";
                case DD_MM_YYYY:
                case DD_MM_YYYY_H_mm:
                case DD_MM_YYYY_HH_mm_ss:
                    return "dd{sep}MM{sep}yyyy";
                case MM_DD_YYYY:
                case MM_DD_YYYY_H_mm:
                case MM_DD_YYYY_HH_mm_ss:
                    return "MM{sep}dd{sep}yyyy";
                case YYYY_MM_DDTHH_MM:
                    return "yyyy{sep}MM{sep}dd\'T\'HH:mm";
                case YYYY_MM_DDTHH_MM_SS:
                    return "yyyy{sep}MM{sep}dd\'T\'HH:mm:ss";
                case D_MM_YYYY:
                case D_MM_YYYY_H_mm:
                case D_MM_YYYY_HH_mm_ss:
                    return "d{sep}MM{sep}yyyy";
                case M_DD_YYYY:
                case M_DD_YYYY_H_mm:
                case M_DD_YYYY_HH_mm_ss:
                    return "M{sep}dd{sep}yyyy";
                case DD_M_YYYY:
                case DD_M_YYYY_H_mm:
                case DD_M_YYYY_HH_mm_ss:
                    return "dd{sep}M{sep}yyyy";
                case MM_D_YYYY:
                case MM_D_YYYY_H_mm:
                case MM_D_YYYY_HH_mm_ss:
                    return "MM{sep}d{sep}yyyy";
                case D_M_YYYY:
                case D_M_YYYY_H_mm:
                case D_M_YYYY_HH_mm_ss:
                    return "d{sep}M{sep}yyyy";
                case M_D_YYYY:
                case M_D_YYYY_H_mm:
                case M_D_YYYY_HH_mm_ss:
                    return "M{sep}d{sep}yyyy";
                case YYYY_DD:
                    return "yyyy{sep}dd";
                case YYYY_MM:
                    return "yyyy{sep}MM";
                case DD_MON_YYYY:
                    return "dd{sep}MMM{sep}yyyy";
                case DDMONYYYY:
                    return "ddMMMyyyy";
                case MON_DD_YYYY:
                    return "MMM{sep}dd{sep}yyyy";
                case D_MON_YYYY:
                    return "d{sep}MMM{sep}yyyy";
                case DMONYYYY:
                    return "dMMMyyyy";
                case MON_D_YYYY:
                    return "MMM{sep}d{sep}yyyy";
                case DD_MON_YY:
                    return "dd{sep}MMM{sep}yy";
                case MON_DD_YY:
                    return "MMM{sep}dd{sep}yy";
                case D_MON_YY:
                    return "d{sep}MMM{sep}yy";
                case MON_D_YY:
                    return "MMM{sep}d{sep}yy";

/*
                YYYY_MON_DD);
                YYYY_MON_D);
                YY_MON_DD);
                YY_MON_D);
                YYYY_DD_MON);
                YYYY_D_MON);
                YY_DD_MON);
                YY_D_MON);
*/

                case YYYY_MON_DD:
                    return "yyyy{sep}MMM{sep}dd";
                case YYYY_MON_D:
                    return "yyyy{sep}MMM{sep}dd";
                case YY_MON_DD:
                    return "yy{sep}MMM{sep}dd";
                case YY_MON_D:
                    return "yy{sep}MMM{sep}d";
                case YYYY_DD_MON:
                    return "yyyy{sep}dd{sep}MMM";
                case YYYY_D_MON:
                    return "yyyy{sep}d{sep}MMM";
                case YY_DD_MON:
                    return "yy{sep}d{sep}MMM";
                case YY_D_MON:
                    return "yy{sep}d{sep}MMM";

                case YYYY:
                    return "yyyy";
                default:
                    return null;
            }
        }
    }

    static final String INT_PATTERN = "^(\\d+)$";
    static final String TIME_PATTERN = "^(([0-9])|([0-1][0-9])|([2][0-3])):(([0-9])|([0-5][0-9]))$";
    static final String TIME_PATTERN_SECONDS = "^(([0-9])|([0-1][0-9])|([2][0-3])):(([0-9])|([0-5][0-9])):(([0-9])|([0-5][0-9]))$";

    static final Map<String, SimpleDateFormat> SDF_CACHE = new ConcurrentHashMap<>();


    private String lastMatchedPattern = null;
    private String lastMatchedFormat = null;

    public DateParser() {
        super();
    }

    public DateParser(Mapper mapper) {
        super(mapper);
    }

    public DateParser(ParserRule parserRule) {
        super(parserRule);
    }

    public DateParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    protected Temporal convert(String input) throws InvalidDataFormatException {
        if (isEmpty(input)) {
            return null;
        }
        String inputTrimmed = input.trim();
        if (isEmpty(inputTrimmed)) {
            return null;
        }

        if (lastMatchedFormat != null) {
            if (lastMatchedPattern != null) {
                if (inputTrimmed.matches(lastMatchedPattern)) {
                    return LocalDateTime.ofInstant(parseDate(inputTrimmed, lastMatchedFormat).toInstant(), ZoneId.systemDefault());
                }
            } else {
                try {
                    return LocalDateTime.ofInstant(parseDate(inputTrimmed, lastMatchedFormat).toInstant(), ZoneId.systemDefault());
                } catch (InvalidDataFormatException e) {
                    //do nothing, try something else
                }
            }
        }

        //trying to parse default date format, produced by Parso for numeric date fields
        //http://docs.oracle.com/javase/6/docs/api/java/util/Date.html#toString()

        try {
            LocalDateTime date = LocalDateTime.ofInstant(parseDate(inputTrimmed, DEFAULT_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            lastMatchedPattern = null;
            lastMatchedFormat = null;
            return date;
        } catch (InvalidDataFormatException e) {
            //do nothing, try something else
        }
        //and one else
        try {

            LocalDateTime date = LocalDateTime.ofInstant(parseDate(inputTrimmed, DEFAULT_DATE_FORMAT_2).toInstant(), ZoneId.systemDefault());
            lastMatchedPattern = null;
            lastMatchedFormat = null;
            return date;
        } catch (InvalidDataFormatException e) {
            //do nothing, try something else
        }

        if (inputTrimmed.matches(TIME_PATTERN) || inputTrimmed.matches(TIME_PATTERN_SECONDS)) {
            try {
                return LocalDateTime.ofInstant(parseDate(inputTrimmed, "HH:mm").toInstant(), ZoneId.systemDefault()).toLocalTime();
            } catch (InvalidDataFormatException e) {
                throw new InvalidDataFormatException("Unable to parse following value: " + inputTrimmed, e);
            }

        } else {
            for (DatePattern name : DatePattern.getDatePatterns(getParserRule())) {
                String pattern = DatePattern.getPattern(name);
                String format = DatePattern.getFormat(name);
                for (String separator : SEPARATORS) {
                    String sepPattern = pattern.replace("{sep}", separator);
                    if (inputTrimmed.matches(sepPattern)) {
                        String sepFormat = format.replace("{sep}", separator);
                        lastMatchedFormat = sepFormat;
                        lastMatchedPattern = sepPattern;
                        return LocalDateTime.ofInstant(parseDate(inputTrimmed, sepFormat).toInstant(), ZoneId.systemDefault());
                    }
                }
            }
        }

        if (inputTrimmed.matches(INT_PATTERN)) {
            int sec = Integer.parseInt(inputTrimmed);
            if (sec == 0) {
                return null;
            }
            return LocalDateTime.ofEpochSecond(sec, 0, ZoneOffset.UTC);
        }

        throw new InvalidDataFormatException("Unable to parse following value: " + inputTrimmed);
    }

    private static synchronized Date parseDate(String input, String pattern) throws InvalidDataFormatException {
        SimpleDateFormat dateFormat = SDF_CACHE.computeIfAbsent(pattern, SimpleDateFormat::new);
        try {
            return dateFormat.parse(input);
        } catch (ParseException e) {
            throw new InvalidDataFormatException("Unable to parse following value: " + input, e);
        }
    }
}
