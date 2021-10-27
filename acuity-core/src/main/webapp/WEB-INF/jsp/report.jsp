<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.acuity.visualisations.web.controller.HelpController"%>

<%--
  ~ Copyright 2021 The University of Manchester
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>ACUITY Data Report</title>
    <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet" />
    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet" media="screen" />
    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet" media="print" />

    <%@ include file="includes.jsp"%>
    <%@ include file="wizard-common.jsp" %>

    <link href="css/jquery.splitter.css" rel="stylesheet" />
    <link href="css/report.css" rel="stylesheet" />
    <script type="text/javascript" src="js/js-lib/jquery.jqGrid.min-4.5.4.js"></script>
    <script type="text/javascript" src="js/js-lib/jquery.form.js"></script>
    <script type="text/javascript" src="js/report.js"></script>
    <script type="text/javascript" src="js/wizard-common.js"></script>
    <script>
          studyCode = "<%= request.getParameter("studyCode") %>";
    </script>
</head>
<body>

    <input id="colClinicalStudy" type="hidden" value='<%= HelpController.getHelpText("report", "colClinicalStudy") %>' />
    <input id="colDateOfUpload" type="hidden" value='<%= HelpController.getHelpText("report", "colDateOfUpload") %>' />
    <input id="colTimeOfUpload" type="hidden" value='<%= HelpController.getHelpText("report", "colTimeOfUpload") %>' />
    <input id="colDurationOfUpload" type="hidden" value='<%= HelpController.getHelpText("report", "colDurationOfUpload") %>' />
    <input id="colSuccessOfUpload" type="hidden" value='<%= HelpController.getHelpText("report", "colSuccessOfUpload") %>' />
    <input id="colUploadSummary" type="hidden" value='<%= HelpController.getHelpText("report", "colUploadSummary") %>' />
    <input id="colAcuityDataSet" type="hidden" value='<%= HelpController.getHelpText("report", "colAcuityDataSet") %>' />
    <input id="colRawDataSource" type="hidden" value='<%= HelpController.getHelpText("report", "colRawDataSource") %>' />
    <input id="colSubjectsSource" type="hidden" value='<%= HelpController.getHelpText("report", "colSubjectsSource") %>' />
    <input id="colSubjectsAcuity" type="hidden" value='<%= HelpController.getHelpText("report", "colSubjectsAcuity") %>' />
    <input id="colCurrentTotalValidEvents" type="hidden" value='<%= HelpController.getHelpText("report", "colCurrentTotalValidEvents") %>' />
    <input id="colOverwrittenRecords" type="hidden" value='<%= HelpController.getHelpText("report", "colOverwrittenRecords") %>' />
    <input id="colErrorCount" type="hidden" value='<%= HelpController.getHelpText("report", "colErrorCount") %>' />
    <input id="colErrorStatus" type="hidden" value='<%= HelpController.getHelpText("report", "colErrorStatus") %>' />
    <input id="colAcuityDataField" type="hidden" value='<%= HelpController.getHelpText("report", "colAcuityDataField") %>' />
    <input id="colRawDataColumn" type="hidden" value='<%= HelpController.getHelpText("report", "colRawDataColumn") %>' />
    <input id="colErrorType" type="hidden" value='<%= HelpController.getHelpText("report", "colErrorType") %>' />
    <input id="colErrorDetail" type="hidden" value='<%= HelpController.getHelpText("report", "colErrorDetail") %>' />
    <input id="colRawDataValue" type="hidden" value='<%= HelpController.getHelpText("report", "colRawDataValue") %>' />
    <input id="colEtlStep" type="hidden" value='<%= HelpController.getHelpText("report", "colEtlStep") %>' />
    <input id="colExceptionType" type="hidden" value='<%= HelpController.getHelpText("report", "colExceptionType") %>' />
    <input id="colExceptionMessage" type="hidden" value='<%= HelpController.getHelpText("report", "colExceptionMessage") %>' />

	<div id="report">
		<div id="study">
		    ACUITY clinical study data upload report
		</div>
		<div id="summary">
		   <div id="summaryTitle">
		       Summary
		   </div>
		   <table id="dataSummaryReport"></table>
		   <div id="dataSummaryPager"></div>
		</div>
		<div id="tabs">
		  <ul id="tabButtons">
            <li class="tabList"><a href="#tabs-1" class="tabButtonText">Exception report</a></li>
		    <li class="tabList"><a href="#tabs-2" class="tabButtonText">Source data table report</a></li>
		    <li class="tabList"><a href="#tabs-3" class="tabButtonText">Source data field report</a></li>
		    <li class="tabList"><a href="#tabs-4" class="tabButtonText">Source data value report</a></li>
		  </ul>
          <div id="tabs-1" class="tab">
            <table id="exceptionReport"></table>
            <div id="exceptionPager"></div>
          </div>
		  <div id="tabs-2" class="tab">
		    <table id="dataTableReport"></table>
		    <div id="dataTablePager"></div>
		  </div>
		  <div id="tabs-3" class="tab">
		    <table id="dataFieldReport"></table>
		    <div id="dataFieldPager"></div>
		  </div>
		  <div id="tabs-4" class="tab">
			<table id="dataValueReport"></table>
		    <div id="dataValuePager"></div>
		  </div>
		</div>
	</div>
</body>
</html>
