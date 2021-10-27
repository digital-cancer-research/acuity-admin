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

package com.acuity.visualisations.data.util;

import org.apache.commons.lang.builder.ToStringStyle;
import org.quartz.JobKey;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class Util {

    public static final String DATE_FORMAT = "EEE MMM d HH:mm:ss z yyyy";
    public static final String GMT_TZ = "GMT";

    private Util() {
    }

    @SuppressWarnings("serial")
    private static final class AcuityToStringStyle extends ToStringStyle {
        private AcuityToStringStyle() {
            setUseShortClassName(true);
        }

        @Override
        public void appendStart(StringBuffer buffer, Object object) {
            if (object != null) {
                appendClassName(buffer, object);
                appendContentStart(buffer);
            }
        }
    }

    private static AcuityToStringStyle acuityToStringStyle = new AcuityToStringStyle();

    private static final Map<String, String> TYPE_PARSER = new HashMap<String, String>();

    static {
        TYPE_PARSER.put("Integer", "IntegerParser");
        TYPE_PARSER.put("Date", "DateParser");
        TYPE_PARSER.put("Real", "BigDecimalParser");
        TYPE_PARSER.put("Labvalue", "LabvalueParser");
        TYPE_PARSER.put("String", "DefaultParser");
        TYPE_PARSER.put("Double", "DoubleParser");
    }

    public static boolean isTrue(Boolean val) {
        if (val != null) {
            return val;
        }
        return false;
    }

    public static boolean isEmpty(String val) {
        return val == null || val.isEmpty();
    }

    public static boolean isEmpty(Object val) {
        return val == null || (val instanceof String && ((String) val).isEmpty());
    }

    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            sb.append((char) Integer.parseInt(str, 16));
        }
        return sb.toString();
    }

    public static String subtractDates(Date minuend, Date subtrahend) {
        long minuedMillis = minuend.getTime();
        long subtrahendMillis = subtrahend.getTime();
        long difference = minuedMillis - subtrahendMillis;
        long diffSeconds = difference / 1000 % 60;
        long diffMinutes = difference / (60 * 1000) % 60;
        long diffHours = difference / (60 * 60 * 1000);
        return String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
    }

    public static Date currentDate() {
        TimeZone target = TimeZone.getTimeZone(GMT_TZ);
        return getDateFromCalendar(target);
    }

    private static Date getDateFromCalendar(TimeZone target) {
        Calendar first = Calendar.getInstance(target);

        Calendar output = Calendar.getInstance();
        output.set(Calendar.YEAR, first.get(Calendar.YEAR));
        output.set(Calendar.MONTH, first.get(Calendar.MONTH));
        output.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH));
        output.set(Calendar.HOUR_OF_DAY, first.get(Calendar.HOUR_OF_DAY));
        output.set(Calendar.MINUTE, first.get(Calendar.MINUTE));
        output.set(Calendar.SECOND, first.get(Calendar.SECOND));
        output.set(Calendar.MILLISECOND, first.get(Calendar.MILLISECOND));

        Date time = output.getTime();
        return time;
    }

    public static String getDate(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat(Util.DATE_FORMAT, Locale.ENGLISH);
        Date result = df.parse(date);
        df.setTimeZone(TimeZone.getTimeZone(GMT_TZ));
        return df.format(result);
    }

    public static java.sql.Date getSQLDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp getSQLTimestamp(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return Timestamp.valueOf(date);
    }

    public static java.sql.Timestamp getSQLTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

    public static String getParserName(String type) {
        if (isEmpty(type)) {
            return TYPE_PARSER.get("String");
        }
        return TYPE_PARSER.get(type);
    }

    public static ToStringStyle getToStringStyle() {
        return acuityToStringStyle;
    }

    public static String jobKeyToString(JobKey jobKey) {
        String jobName = jobKey.getName();
        String group = jobKey.getGroup();
        return jobName + "|" + group;
    }

    public static JobKey jobKeyFromString(String jobKeyString) {
        int delimiterIndex = jobKeyString.lastIndexOf("|");
        String jobName = jobKeyString.substring(0, delimiterIndex);
        String group = jobKeyString.substring(delimiterIndex + 1);
        return new JobKey(jobName, group);
    }
}
