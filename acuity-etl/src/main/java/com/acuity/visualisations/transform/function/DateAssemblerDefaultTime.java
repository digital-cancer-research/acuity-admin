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

package com.acuity.visualisations.transform.function;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * Created by knml167 on 30/11/2015.
 */
public class DateAssemblerDefaultTime extends AbstractFunction<Date> {
    public Date function(Object[] dates) {
        LocalDate date = null;
        LocalTime time = null;
        for (Object item : dates) {
            if (item != null && (item instanceof Temporal)) {
                if ((item instanceof LocalDate)) {
                    date = (LocalDate) item;
                }
                if ((item instanceof LocalDateTime)) {
                    date = LocalDate.from((LocalDateTime) item);
                    time = LocalTime.from((LocalDateTime) item);
                }
                if ((item instanceof LocalTime)) {
                    time = (LocalTime) item;
                }
            }
        }

        if (date == null) {
            return null;
        }
        LocalDateTime result;
        if (time != null) {
            result = date.atTime(time);
        } else {

            result = date.atStartOfDay();
        }
        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
    }
}
