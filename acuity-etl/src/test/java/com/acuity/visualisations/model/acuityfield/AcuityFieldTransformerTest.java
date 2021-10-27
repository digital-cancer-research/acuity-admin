package com.acuity.visualisations.model.acuityfield;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;

public class AcuityFieldTransformerTest {

    @Test
    public void testShouldNotModifyDateWithTime() {
        LocalDateTime ldt = LocalDateTime.of(2012, 12, 25, 10, 15, 45);
        assertThat(AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.INITIATING_EVENT_00_00_01)).isSameAs(ldt);
        assertThat(AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)).isSameAs(ldt);
        assertThat(AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)).isSameAs(ldt);
    }

    @Test
    public void testShouldModifyDateWithoutTime() {
        LocalDateTime ldt = LocalDateTime.of(2012, 12, 25, 0, 0);
        LocalDateTime out;

        out = AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.INITIATING_EVENT_00_00_01);
        assertThat(out.get(ChronoField.SECOND_OF_DAY)).isEqualTo(1);

        out = AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00);
        assertThat(out.get(ChronoField.SECOND_OF_DAY)).isEqualTo(12 * 60 * 60);

        out = AcuityFieldTransformer.transform(ldt, AcuityFieldTransformation.TERMINATING_EVENT_23_59_59);
        assertThat(out.get(ChronoField.SECOND_OF_DAY)).isEqualTo(24 * 60 * 60 - 1);
    }
}
