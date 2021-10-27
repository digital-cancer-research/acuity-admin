package com.acuity.visualisations.batch;

import java.io.InputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

/**
 * Created by knml167 on 02/05/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:../test-classes/applicationContext-h2test.xml" })
public class H2InitExample {
	@Autowired
	private DataSource dataSource;

	@Test
	public void uploadXML() throws Exception {
		Connection jdbcCon = dataSource.getConnection();
		IDatabaseConnection con = new DatabaseConnection(jdbcCon);
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("acuity_result_STDY4321.xml");
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(resourceAsStream));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);
		con.close();
	}
}
