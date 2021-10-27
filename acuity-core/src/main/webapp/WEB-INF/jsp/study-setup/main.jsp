<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
    <script type="text/javascript" src="js/study-setup/step-cbioportal-genomic-profile.js"></script>


    <script src="js/js-lib/bootstrap-3.4.1/js/bootstrap.min.js"></script>

    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="js/study-setup/main.js"></script>
    <script type="text/javascript">
        defaultTypes = [];
        aggregationFunctions = <%= request.getAttribute("aggregationFunctions") %>;
        fileSections = <%= request.getAttribute("fileSections") %>;
        phaseTypes = <%= request.getAttribute("phaseTypes") %>;
        editStudyWorkflow = <%= request.getAttribute("editStudyWorkflow") %>;
        studyRulesSearchForEdit = <%= request.getAttribute("studyRulesSearch") %>;
        webappUrl = <%= request.getAttribute("acuity.vahub.url") %>;
        cBioPortalUrl = <%= request.getAttribute("cBioPortalUrl") %>;
        amlEnabledGlobally = <%= request.getAttribute("amlEnabledGlobally") %>;
    </script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/admin-header.jsp" %>
<%@ include file="/WEB-INF/jsp/wizard-common.jsp" %>
<!-- Tabs -->
<div id="clinical-wizard" style="margin-top: 50px;" class="wizard-loading">
    <div id="leftPane" class="pane">
        <ul>
            <li>
                <h2>
                    <span>
                       Clinical Study Setup
                    </span>
                </h2>
            </li>
            <li>
                <a href="#step-1" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small id="study-text-step-1">Select the clinical study to configure for ACUITY</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-2" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Review and edit the key dataset parameters</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-3" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small id="study-text-step-3">Create mappings to the source data</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-4" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Determine how baseline values are calculated</small>
                    </span>
                </a>
            </li>

            <li>
                <a href="#step-5" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Setup alternative labcode decoding information</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-6" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Setup exclusion values</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-7" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Setup alternative subject groupings</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-8" target="_blank" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Annotate subject groupings</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-9" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Select custom project groupings</small>
                    </span>
                </a>
            </li>
            <li>
                <a href="#step-10" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Select subject groupings</small>
                    </span>
                </a>
            </li>
            <%
                String cBioPortalUrl = (String) request.getAttribute("cBioPortalUrl");
            %>
            <c:if test='${not empty cBioPortalUrl}'>
                <li>
                    <a href="#step-11" class="step-link">
                        <span class="sideBarIcon"></span>
                        <span class="stepDesc">
                            <small>cBioPortal Genomic Profile</small>
                        </span>
                    </a>
                </li>
            </c:if>
            <li>
                <a href="#step-12" class="step-link">
                    <span class="sideBarIcon"></span>
                    <span class="stepDesc">
                        <small>Review the dataset setup</small>
                    </span>
                </a>
            </li>
        </ul>
    </div>
    <div id="rightPane" class="pane">
        <div id="leftInnerPane" class="pane">
            <div class="stepContainer">
                <div id="step-1" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/1-search-study.jsp">
                        <jsp:param name="stepIndex" value="study-step-1"/>
                    </jsp:include>
                </div>
                <div id="step-2" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/2-edit-study.jsp">
                        <jsp:param name="stepIndex" value="study-step-2"/>
                    </jsp:include>
                </div>
                <div id="step-3"  class="" style="min-height: 1000px;">
                    <jsp:include page="/WEB-INF/jsp/study-setup/3-edit-mappings.jsp">
                        <jsp:param name="stepIndex" value="study-step-3"/>
                    </jsp:include>
                </div>

                <div id="step-4" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/step-baseline-drugs.jsp">
                        <jsp:param name="stepIndex" value="study-step-4"/>
                    </jsp:include>
                </div>

                <div id="step-5" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/step-alt_lab_codes.jsp">
                        <jsp:param name="stepIndex" value="study-step-5"/>
                    </jsp:include>
                </div>
                <div id="step-6" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/step-exclusion_values.jsp">
                        <jsp:param name="stepIndex" value="study-step-6"/>
                    </jsp:include>
                </div>
                <div id="step-7" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/4-edit-groupings.jsp">
                        <jsp:param name="stepIndex" value="study-step-7"/>
                    </jsp:include>
                </div>
                <div id="step-8" class="">
                    <div class="text-center">
                        <a class="btn btn-primary" id="refToWebappGroupings" href="#" role="button" target="_blank">
                            Edit
                        </a>
                    </div>
                </div>
                <div id="step-9" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/project-groupings.jsp">
                        <jsp:param name="stepIndex" value="study-step-9"/>
                    </jsp:include>
                </div>
                <div id="step-10" class="" style="min-height: 500px">
                    <jsp:include page="/WEB-INF/jsp/study-setup/study-subject-groupings.jsp">
                        <jsp:param name="stepIndex" value="study-step-10"/>
                    </jsp:include>
                </div>
                <c:if test='${not empty cBioPortalUrl}'>
                    <div id="step-11" class="">
                        <jsp:include page="/WEB-INF/jsp/study-setup/step-cBioPortal-genomic-profile.jsp">
                            <jsp:param name="stepIndex" value="study-step-11"/>
                        </jsp:include>
                    </div>
                </c:if>
                <div id="step-12" class="">
                    <jsp:include page="/WEB-INF/jsp/study-setup/5-summary.jsp">
                        <jsp:param name="stepIndex" value="study-step-12"/>
                    </jsp:include>
                </div>
            </div>
        </div>
    </div>
    <div id="rightInnerPane" class="rightMappingSplitter" style="overflow-y:scroll;">
        <h2>
                    <span>
                       Data Checklist <a href="#" class="help"
                                         title='<%= HelpController.getHelpText("study-setup-main", "dataChecklist") %>'>?</a>
                    </span>
        </h2>
        <div id="checkListContent" class="gridItems">
        </div>
    </div>
</div>
</div>
</body>
</html>
