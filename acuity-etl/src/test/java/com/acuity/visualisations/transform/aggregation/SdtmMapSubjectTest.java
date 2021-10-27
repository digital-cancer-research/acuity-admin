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


public class SdtmMapSubjectTest {

    @Test
    public void nullNotMapped() {
        String result = SdtmParsers.parseSubject(null);
        assertThat(result).isNull();
    }

    @Test
    public void emptyStringNotMapped() {
        String result = SdtmParsers.parseSubject("");
        assertThat(result).isEqualTo("");
    }

    @Test
    public void invalidStringNotMapped() {
        String result = SdtmParsers.parseSubject("S");
        assertThat(result).isEqualTo("S");

        result = SdtmParsers.parseSubject("S/");
        assertThat(result).isEqualTo("S/");

        result = SdtmParsers.parseSubject("/E");
        assertThat(result).isEqualTo("/E");
    }

    @Test
    public void extractSubjectFromValidUsubjid() {
        String result = SdtmParsers.parseSubject("STUDY001/SUBJECT001");
        assertThat(result).isEqualTo("SUBJECT001");

        result = SdtmParsers.parseSubject("S/E");
        assertThat(result).isEqualTo("E");

        result = SdtmParsers.parseSubject("STUDY002-000-0001");
        assertThat(result).isEqualTo("000-0001");
    }
}
