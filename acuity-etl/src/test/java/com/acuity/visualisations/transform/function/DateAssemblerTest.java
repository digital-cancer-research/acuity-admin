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

import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.util.Date;

public class DateAssemblerTest {

    @Test
    public void test1() throws Exception {
        DateAssembler dateAssembler = new DateAssembler();
        LocalDate date = LocalDate.of(2010, Month.DECEMBER, 12);
        LocalTime time = LocalTime.of(10, 30);

        Date res = dateAssembler.function(new Object[]{date,time});
        Assert.assertNotNull(res);
        LocalDateTime dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{time,date});
        Assert.assertNotNull(res);

        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{date,null});
        Assert.assertNotNull(res);

        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 0);
        Assert.assertEquals(dt.getMinute(), 0);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{null, date});
        Assert.assertNotNull(res);

        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 0);
        Assert.assertEquals(dt.getMinute(), 0);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{null,time});
        Assert.assertNull(res);

        res = dateAssembler.function(new Object[]{time, null});
        Assert.assertNull(res);
    }

    @Test
    public void test2() throws Exception {
        DateAssembler dateAssembler = new DateAssembler();
        LocalDateTime date = LocalDateTime.of(2010, Month.DECEMBER, 12, 10, 30);

        Date res = dateAssembler.function(new Object[]{date});
        Assert.assertNotNull(res);
        LocalDateTime dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{null,date});
        Assert.assertNotNull(res);
        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);
    }

    @Test
    public void test3() throws Exception {
        DateAssembler dateAssembler = new DateAssembler();
        LocalDateTime date = LocalDateTime.of(1970, Month.JANUARY, 1, 10, 30);

        Date res = dateAssembler.function(new Object[]{date});
        Assert.assertNotNull(res);
        LocalDateTime dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 1970);
        Assert.assertEquals(dt.getMonth(), Month.JANUARY);
        Assert.assertEquals(dt.getDayOfMonth(), 1);

        res = dateAssembler.function(new Object[]{null,date});
        Assert.assertNotNull(res);
        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 1970);
        Assert.assertEquals(dt.getMonth(), Month.JANUARY);
        Assert.assertEquals(dt.getDayOfMonth(), 1);
    }

    @Test
    public void test4() throws Exception {
        DateAssembler dateAssembler = new DateAssembler();
        LocalDate date = LocalDate.of(1970, Month.JANUARY, 1);
        LocalTime time = LocalTime.of(10, 30);

        Date res = dateAssembler.function(new Object[]{date, time});
        Assert.assertNotNull(res);
        LocalDateTime dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 1970);
        Assert.assertEquals(dt.getMonth(), Month.JANUARY);
        Assert.assertEquals(dt.getDayOfMonth(), 1);

        res = dateAssembler.function(new Object[]{time,date});
        Assert.assertNotNull(res);
        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 10);
        Assert.assertEquals(dt.getMinute(), 30);
        Assert.assertEquals(dt.getYear(), 1970);
        Assert.assertEquals(dt.getMonth(), Month.JANUARY);
        Assert.assertEquals(dt.getDayOfMonth(), 1);

        res = dateAssembler.function(new Object[]{time});
        Assert.assertNull(res);
    }

    @Test
    public void test5() throws Exception {
        DateAssembler dateAssembler = new DateAssembler();
        LocalDate date = LocalDate.of(2010, Month.DECEMBER, 12);

        Date res = dateAssembler.function(new Object[]{date, null});
        Assert.assertNotNull(res);
        LocalDateTime dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 0);
        Assert.assertEquals(dt.getMinute(), 0);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{null,date});
        Assert.assertNotNull(res);
        dt = LocalDateTime.ofInstant(res.toInstant(), ZoneId.systemDefault());
        Assert.assertEquals(dt.getHour(), 0);
        Assert.assertEquals(dt.getMinute(), 0);
        Assert.assertEquals(dt.getYear(), 2010);
        Assert.assertEquals(dt.getMonth(), Month.DECEMBER);
        Assert.assertEquals(dt.getDayOfMonth(), 12);

        res = dateAssembler.function(new Object[]{null});
        Assert.assertNull(res);
    }
}
