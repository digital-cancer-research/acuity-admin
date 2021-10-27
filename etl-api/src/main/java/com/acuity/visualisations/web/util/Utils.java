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

package com.acuity.visualisations.web.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public final class Utils {


    public static final SimpleDateFormat DD_MMM_YY_FORMAT = new SimpleDateFormat("dd-MMM-yy");
    public static final SimpleDateFormat DD_MMM_YYYY_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    public static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{1,2}-[a-zA-Z]{3}-[0-9]{2,4}");

    private Utils() {
    }

    public static String getDateString(Date date) {
        return DD_MMM_YYYY_FORMAT.format(date);
    }


    public static Date parse(final String date) throws Exception {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        if (DATE_PATTERN.matcher(date.trim()).matches()) {
            return DD_MMM_YY_FORMAT.parse(date);
        }
        throw new ParseException("Unparseable date: [" + date + "]", 0);
    }

}
