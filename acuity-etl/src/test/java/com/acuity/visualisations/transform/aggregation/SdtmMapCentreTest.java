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

package com.acuity.visualisations.transform.aggregation;

import com.acuity.visualisations.batch.processor.SdtmParsers;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class SdtmMapCentreTest {

    @Test
    public void nullNotMapped() {
        Integer result = SdtmParsers.parseCentre(null);
        assertThat(result).isNull();
    }

    @Test
    public void emptyStringNotMapped() {
        Integer result = SdtmParsers.parseCentre("");
        assertThat(result).isNull();
    }

    @Test
    public void invalidStringNotMapped() {
        Integer result = SdtmParsers.parseCentre("S");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("/E");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E1");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E12");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E123");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E1234");
        assertThat(result).isNull();
    }

    @Test
    public void validStringCorrectlyMapped() {
        Integer result = SdtmParsers.parseCentre("STUDY001/ID1001002");
        assertThat(result).isEqualTo(1001);

        result = SdtmParsers.parseCentre("STD001/SUBJECT12345");
        assertThat(result).isEqualTo(1234);

        result = SdtmParsers.parseCentre("STUDY001-3000-0001");
        assertThat(result).isEqualTo(3000);
    }
}
