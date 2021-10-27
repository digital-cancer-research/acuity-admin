package com.acuity.visualisations.unittests.testsuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.acuity.visualisations.unittests.dal.report.dao.DataFieldReportDaoTests;
import com.acuity.visualisations.unittests.dal.report.dao.DataSummaryDaoTests;
import com.acuity.visualisations.unittests.dal.report.dao.DataTableReportDaoTests;
import com.acuity.visualisations.unittests.dal.report.dao.DataValueReportDaoTests;
import com.acuity.visualisations.unittests.web.service.ReportServiceTests;

/**
 * Run this test suite so that all Report tests are run
 */
@RunWith(Suite.class)
@SuiteClasses({
    ReportServiceTests.class,
    DataSummaryDaoTests.class,
    DataTableReportDaoTests.class,
    DataFieldReportDaoTests.class,
    DataValueReportDaoTests.class
})
public class ReportTestSuite {

}
