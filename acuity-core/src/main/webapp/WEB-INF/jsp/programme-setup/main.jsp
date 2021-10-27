<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ACUITY Admin</title>
    <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet"/>
    <link href="js/js-lib/bootstrap-3.4.1/css/bootstrap.min.css" rel="stylesheet"/>
    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet"
          media="screen"/>
    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet"
          media="print"/>


    <%@ include file="/WEB-INF/jsp/includes.jsp" %>

    <link href="css/smart_wizard_vertical.css" rel="stylesheet" type="text/css">
    <link href="css/smart_wizard.min.css" rel="stylesheet" type="text/css">
    <link href="css/jquery.splitter.css" rel="stylesheet"/>
    <link href="css/drug-project.css" rel="stylesheet"/>

    <script type="text/javascript" src="js/js-lib/jquery.smartWizard-4.4.1.js"></script>
    <script type="text/javascript" src="js/js-lib/jquery.jqGrid.min-4.5.4.js"></script>
    <script type="text/javascript" src="js/js-lib/jquery.form.js"></script>

    <script src="js/js-lib/bootstrap-3.4.1/js/bootstrap.min.js"></script>

    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="js/programme-setup/main.js"></script>
    <script>
        editProgrammeWorkflow = <%= request.getAttribute("editProgrammeWorkflow") %>;
        programmeSearchResult = <%= request.getAttribute("programmeSearchResult") %>;
    </script>

</head>
<body>
<%@ include file="/WEB-INF/jsp/admin-header.jsp" %>
<%@ include file="/WEB-INF/jsp/wizard-common.jsp" %>
<!-- Tabs -->
<div id="wizard" style="margin-top: 50px;" class="wizard-loading">
    <div id="leftPane" class="pane">
        <ul>
            <li>
                <h2>
                    <span>
                        Drug Programme Setup
                    </span>
                </h2>
            </li>
            <li>
                <a href="#step-1" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small id="text-step-1">Select the drug programme to enable with ACUITY</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-2" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Review and edit the key drug programme parameters</small>
                        <small id="text-step-2"></small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-3" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Setup the custom data-groupings that can be applied to the study data</small>
                        <small id="text-step-3"></small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-4" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Review project summary</small>
                    </span>
                </a>
            </li>
        </ul>
    </div>
    <div id="rightPane" class="pane">
        <div class="stepContainer">
            <div id="step-1" class="">
                <jsp:include page="/WEB-INF/jsp/programme-setup/1-search-programme.jsp">
                    <jsp:param name="stepIndex" value="step-1"/>
                </jsp:include>
            </div>
            <div id="step-2" class="">
                <jsp:include page="/WEB-INF/jsp/programme-setup/2-edit-programme.jsp">
                    <jsp:param name="stepIndex" value="step-2"/>
                </jsp:include>
            </div>
            <div id="step-3" class="">
                <jsp:include page="/WEB-INF/jsp/programme-setup/3-setup-groupings.jsp">
                    <jsp:param name="stepIndex" value="step-3"/>
                </jsp:include>
            </div>
            <div id="step-4" class="">
                <jsp:include page="/WEB-INF/jsp/programme-setup/4-summary.jsp">
                    <jsp:param name="stepIndex" value="step-4"/>
                </jsp:include>
            </div>
        </div>
    </div>
</div>

</body>
</html>
