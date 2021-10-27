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

package com.acuity.visualisations.model.output.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MedDosingScheduleTest {

    @Test
    public void testComplete_LegacyBothNulls() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(null);
        mds.setFrequencyUnit(null);
        mds.complete();

        assertThat(mds.getFrequency()).isNull();
        assertThat(mds.getFrequencyUnit()).isNull();
        assertThat(mds.getFreqName()).isNull();
        assertThat(mds.getFreqRank()).isZero();
    }

    @Test
    public void testComplete_LegacyNullFrequency() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(null);
        mds.setFrequencyUnit("day");
        mds.complete();

        assertThat(mds.getFrequency()).isNull();
        assertThat(mds.getFrequencyUnit()).isEqualTo("day");
        assertThat(mds.getFreqName()).isEqualTo("?-per-day");
        assertThat(mds.getFreqRank()).isZero();
    }

    @Test
    public void testComplete_LegacyNullFrequencyUnit() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(1);
        mds.setFrequencyUnit(null);
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(1);
        assertThat(mds.getFrequencyUnit()).isNull();
        assertThat(mds.getFreqName()).isEqualTo("1-per-?");
        assertThat(mds.getFreqRank()).isZero();
    }

    @Test
    public void testComplete_Legacy() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(5);
        mds.setFrequencyUnit("week");
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(5);
        assertThat(mds.getFrequencyUnit()).isEqualTo("week");
        assertThat(mds.getFreqName()).isEqualTo("5-per-week");
        assertThat(mds.getFreqRank()).isEqualTo(5/7.);
    }

    @Test
    public void testComplete_LegacyCompatibleWithCDASH_QD() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(1);
        mds.setFrequencyUnit("day");
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(1);
        assertThat(mds.getFrequencyUnit()).isEqualTo("day");
        assertThat(mds.getFreqName()).isEqualTo("QD");
        assertThat(mds.getFreqRank()).isEqualTo(1);
    }

    @Test
    public void testComplete_LegacyCompatibleWithCDASH_BID() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(2);
        mds.setFrequencyUnit("day");
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(2);
        assertThat(mds.getFrequencyUnit()).isEqualTo("day");
        assertThat(mds.getFreqName()).isEqualTo("BID");
        assertThat(mds.getFreqRank()).isEqualTo(2);
    }

    @Test
    public void testComplete_CDASH_QOD() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFrequency(null);
        mds.setFrequencyUnit(null);
        mds.setFreqName("QOD");
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(178);
        assertThat(mds.getFrequencyUnit()).isEqualTo("year");
        assertThat(mds.getFreqName()).isEqualTo("QOD");
        assertThat(mds.getFreqRank()).isEqualTo(0.5);
    }

    @Test
    public void testComplete_CDASH_Every_3_Weeks() {
        MedDosingSchedule mds = new MedDosingSchedule();
        mds.setFreqName("Every 3 Weeks");
        mds.complete();

        assertThat(mds.getFrequency()).isEqualTo(null);
        assertThat(mds.getFrequencyUnit()).isEqualTo(null);
        assertThat(mds.getFreqName()).isEqualTo("Every 3 Weeks");
        assertThat(mds.getFreqRank()).isEqualTo(0.0476);
    }
}
