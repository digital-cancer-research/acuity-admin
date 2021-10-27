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

package com.acuity.visualisations.transform.parser;

import java.text.DateFormat;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.ParserRule;

public class DateParserTest {

    @Test
    public void testDDslashMMslashYYYY() throws InvalidDataFormatException {
        String dateStr = "22/07/2013";

        DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
        Temporal date = dateParser.parse(dateStr);


        Assert.assertEquals(date.getClass(), LocalDateTime.class);
        Assert.assertEquals(22, ((LocalDateTime)date).getDayOfMonth());
        Assert.assertEquals(Month.JULY, ((LocalDateTime)date).getMonth());
        Assert.assertEquals(2013, ((LocalDateTime)date).getYear());
    }

	@Test
	public void testDDMMMYYYY() throws InvalidDataFormatException {
		String dateStr = "01JAN2012";

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal date = dateParser.parse(dateStr);

		Assert.assertEquals(date.getClass(), LocalDateTime.class);
		Assert.assertEquals(1, ((LocalDateTime)date).getDayOfMonth());
		Assert.assertEquals(Month.JANUARY, ((LocalDateTime)date).getMonth());
		Assert.assertEquals(2012, ((LocalDateTime)date).getYear());
	}

	@Test
	public void testDMMMYYYY() throws InvalidDataFormatException {
		String dateStr = "1JAN2012";

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal date = dateParser.parse(dateStr);

		Assert.assertEquals(date.getClass(), LocalDateTime.class);
		Assert.assertEquals(1, ((LocalDateTime)date).getDayOfMonth());
		Assert.assertEquals(Month.JANUARY, ((LocalDateTime)date).getMonth());
		Assert.assertEquals(2012, ((LocalDateTime)date).getYear());
	}

    @Test
    public void test__() throws InvalidDataFormatException {
        String dateStr = "30-Mar-14";

        DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);

		Temporal date = dateParser.parse(dateStr);


		Assert.assertEquals(date.getClass(), LocalDateTime.class);
		Assert.assertEquals(30, ((LocalDateTime)date).getDayOfMonth());
		Assert.assertEquals(Month.MARCH, ((LocalDateTime)date).getMonth());
		Assert.assertEquals(2014, ((LocalDateTime)date).getYear());
    }

	@Test
	public void test1() throws InvalidDataFormatException {
		String dateStr = "21/02/2013 00:00:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 21, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime)parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}
	@Test
	public void test2() throws InvalidDataFormatException {
		String dateStr = "12/12/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.DECEMBER, 12, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

//	@Test
//	public void testIntTime() throws InvalidDataFormatException {
//		String dateStr = "44400";
//
//		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
//		Temporal parsed = dateParser.parse(dateStr);
//		Assert.assertEquals(parsed.getClass(), LocalTime.class);
//
//		Assert.assertEquals(12, ((LocalTime)parsed).getHour());
//		Assert.assertEquals(20, ((LocalTime)parsed).getMinute());
//		Assert.assertEquals(0, ((LocalTime)parsed).getSecond());
//
//	}

	@Test
	public void test3() throws InvalidDataFormatException {
		String dateStr = "9/1/2013 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.JANUARY, 9, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test4() throws InvalidDataFormatException {
		String dateStr = "1/11/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.NOVEMBER, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test5() throws InvalidDataFormatException {
		String dateStr = "11/9/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.SEPTEMBER, 11, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.DAY_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test6() throws InvalidDataFormatException {
		String dateStr = "02/21/2013 00:00:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 21, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.MONTH_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test7() throws InvalidDataFormatException {
		String dateStr = "12/12/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.DECEMBER, 12, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.MONTH_FIRST);

		Temporal parsed = dateParser.parse(dateStr);
		parsed = dateParser.parse(dateStr);
		parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime)parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test8() throws InvalidDataFormatException {
		String dateStr = "1/9/2013 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.JANUARY, 9, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.MONTH_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test9() throws InvalidDataFormatException {
		String dateStr = "11/1/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.NOVEMBER, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.MONTH_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}

	@Test
	public void test10() throws InvalidDataFormatException {
		String dateStr = "9/11/2012 0:00";
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.SEPTEMBER, 11, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date expectedDate = cal.getTime();

		DateParser dateParser = new DateParser(ParserRule.MONTH_FIRST);
		Temporal parsed = dateParser.parse(dateStr);
		Assert.assertEquals(parsed.getClass(), LocalDateTime.class);
		Date parsedDate = Date.from(((LocalDateTime) parsed).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		String expectedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(expectedDate);
		String parsedDateStr = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(parsedDate);
		Assert.assertTrue("Expected: " + expectedDateStr + ", parsed: " + parsedDateStr, parsedDate.compareTo(expectedDate) == 0);
	}
}
