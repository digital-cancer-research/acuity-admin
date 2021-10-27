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

package com.acuity.visualisations.dal.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QueryBuilderUtilUpdateTest {

	private String entityTable;
	private List<TableField> fieldsToSet;
	private List<TableField> whereFields;
	private String expectedQuery;

	public QueryBuilderUtilUpdateTest(String entityTable, List<TableField> fieldsToSet, List<TableField> whereFields, String expectedQuery) {
		this.entityTable = entityTable;
		this.fieldsToSet = fieldsToSet;
		this.whereFields = whereFields;
		this.expectedQuery = expectedQuery;
	}

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> testParameters = new ArrayList<Object[]>();
		testParameters.add(getParametersForFirstTest());
		testParameters.add(getParametersForSecondTest());
		testParameters.add(getParametersForThirdTest());
		testParameters.add(getParametersForFourthTest());
		return testParameters;
	}

	private static Object[] getParametersForFourthTest() {
		Object[] parameters = new Object[4];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToSet = new ArrayList<TableField>();
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_CENTRE"));
		parameters[1] = fieldsToSet;
		List<TableField> whereFields = new ArrayList<TableField>();
		whereFields.add(new TableField("RESULT_PATIENT", "PAT_SUBJECT"));
		whereFields.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
		parameters[2] = whereFields;
		parameters[3] = "update RESULT_PATIENT set PAT_AGE=?, PAT_CENTRE=? where PAT_SUBJECT=? and PAT_STD_ID=?";
		return parameters;
	}

	private static Object[] getParametersForThirdTest() {
		Object[] parameters = new Object[4];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToSet = new ArrayList<TableField>();
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_CENTRE"));
		parameters[1] = fieldsToSet;
		List<TableField> whereFields = new ArrayList<TableField>();
		whereFields.add(new TableField("RESULT_PATIENT", "PAT_ID"));
		parameters[2] = whereFields;
		parameters[3] = "update RESULT_PATIENT set PAT_AGE=?, PAT_CENTRE=? where PAT_ID=?";
		return parameters;
	}

	private static Object[] getParametersForSecondTest() {
		Object[] parameters = new Object[4];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToSet = new ArrayList<TableField>();
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		parameters[1] = fieldsToSet;
		List<TableField> whereFields = new ArrayList<TableField>();
		whereFields.add(new TableField("RESULT_PATIENT", "PAT_ID"));
		parameters[2] = whereFields;
		parameters[3] = "update RESULT_PATIENT set PAT_AGE=? where PAT_ID=?";
		return parameters;
	}

	private static Object[] getParametersForFirstTest() {
		Object[] parameters = new Object[4];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToSet = new ArrayList<TableField>();
		fieldsToSet.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		parameters[1] = fieldsToSet;
		parameters[2] = new ArrayList<TableField>();
		parameters[3] = "update RESULT_PATIENT set PAT_AGE=?";
		return parameters;
	}

	@Test
	public void test() {
		String query = QueryBuilderUtil.buildUpdateQuery(entityTable, fieldsToSet, whereFields);
		assertThat(expectedQuery).isEqualTo(query);
	}
}
