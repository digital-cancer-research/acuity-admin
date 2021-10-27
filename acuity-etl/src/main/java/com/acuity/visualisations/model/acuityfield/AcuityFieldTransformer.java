package com.acuity.visualisations.model.acuityfield;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

public final class AcuityFieldTransformer {
    private AcuityFieldTransformer() {
    }

    public static Object transform(Object object, AcuityFieldTransformation transformation, boolean isCompound) {
        if (object == null) {
            return null;
        }
        if (object instanceof Date) {
            object = LocalDateTime.ofInstant(((Date) object).toInstant(), ZoneId.systemDefault());
        }
        if (object instanceof LocalDateTime && !isCompound) {
            return transform((LocalDateTime) object, transformation);
        }

        return object;
    }

    public static LocalDateTime transform(final LocalDateTime date, final AcuityFieldTransformation transformation) {
        if (date == null) {
            return null;
        }

        int hours = date.get(ChronoField.HOUR_OF_DAY);
        int minutes = date.get(ChronoField.MINUTE_OF_HOUR);
        int seconds = date.get(ChronoField.SECOND_OF_MINUTE);

        if (hours == 0 && minutes == 0 && seconds == 0) {
            switch (transformation) {
                case INITIATING_EVENT_00_00_01:
                    return date.plusSeconds(1);
                case MEASUREMENT_EVENT_12_00_00:
                    return date.plusHours(12);
                case TERMINATING_EVENT_23_59_59:
                    return date.plusSeconds(86399);
                default:
                    return date;
            }
        }
        return date;
    }
}
