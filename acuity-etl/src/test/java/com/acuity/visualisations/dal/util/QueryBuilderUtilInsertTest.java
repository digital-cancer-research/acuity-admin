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
public class QueryBuilderUtilInsertTest {

	private String entityTable;
	private List<TableField> fieldsToInsert;
	private String expectedQuery;

	public QueryBuilderUtilInsertTest(String entityTable, List<TableField> fieldsToInsert, String expectedQuery) {
		this.entityTable = entityTable;
		this.fieldsToInsert = fieldsToInsert;
		this.expectedQuery = expectedQuery;
	}

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> testParameters = new ArrayList<Object[]>();
		testParameters.add(getParametersForFirstTest());
		testParameters.add(getParametersForSecondTest());
		testParameters.add(getParametersForThirdTest());
		return testParameters;
	}

	private static Object[] getParametersForThirdTest() {
		Object[] parameters = new Object[3];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToInsert = new ArrayList<TableField>();
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_ID"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_DATE_CREATED"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_DATE_UPDATED"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_CENTRE"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_RACE"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_RAND_DATE"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_SEX"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_SUBJECT"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
		parameters[1] = fieldsToInsert;
		parameters[2] = "insert into RESULT_PATIENT " + "(PAT_ID, PAT_DATE_CREATED, PAT_DATE_UPDATED, PAT_AGE, "
				+ "PAT_CENTRE, PAT_RACE, PAT_RAND_DATE, PAT_SEX, "
				+ "PAT_SUBJECT, PAT_STD_ID) " + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		return parameters;
	}

	private static Object[] getParametersForSecondTest() {
		Object[] parameters = new Object[3];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToInsert = new ArrayList<TableField>();
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_SUBJECT"));
		parameters[1] = fieldsToInsert;
		parameters[2] = "insert into RESULT_PATIENT (PAT_AGE, PAT_SUBJECT) values (?, ?)";
		return parameters;
	}

	private static Object[] getParametersForFirstTest() {
		Object[] parameters = new Object[3];
		parameters[0] = "RESULT_PATIENT";
		List<TableField> fieldsToInsert = new ArrayList<TableField>();
		fieldsToInsert.add(new TableField("RESULT_PATIENT", "PAT_AGE"));
		parameters[1] = fieldsToInsert;
		parameters[2] = "insert into RESULT_PATIENT (PAT_AGE) values (?)";
		return parameters;
	}

	@Test
	public void test() {
		String query = QueryBuilderUtil.buildInsertQuery(entityTable, fieldsToInsert);
		assertThat(expectedQuery).isEqualTo(query);
	}
}
