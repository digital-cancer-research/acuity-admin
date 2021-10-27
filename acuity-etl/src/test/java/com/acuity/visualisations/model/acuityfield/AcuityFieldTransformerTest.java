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
