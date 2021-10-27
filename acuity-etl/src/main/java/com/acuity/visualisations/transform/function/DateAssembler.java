package com.acuity.visualisations.transform.function;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Optional;

public class DateAssembler extends AbstractFunction<Date> {
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

        LocalDateTime result = Optional.ofNullable(time).map(date::atTime).orElse(date.atStartOfDay());

        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
    }
}
