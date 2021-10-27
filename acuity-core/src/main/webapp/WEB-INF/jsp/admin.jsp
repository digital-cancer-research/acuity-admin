<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
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
    <title>ACUITY Jobs</title>
    <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet"/>
    <link href="js/js-lib/bootstrap-3.4.1/css/bootstrap.min.css" rel="stylesheet"/>
    <%@ include file="/WEB-INF/jsp/includes.jsp" %>
    <link type="text/css" href="css/admin.css" rel="stylesheet"/>
    <!--<link type="text/css" href="css/drug-project.css" rel="stylesheet" />-->

    <script src="js/js-lib/bootstrap-3.4.1/js/bootstrap.min.js"></script>
    <script src="js/js-lib/bootbox.min.js"></script>

    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="js/admin.js?v1"></script>
    <script>
        projectsInAcuity = <%= request.getAttribute("projectsInAcuity") %>;
        webappUrl =
        <%= request.getAttribute("acuity.vahub.url") %>
    </script>
    <script>
        $(function () {
            $(".tooltipRef").tooltip();
            $('[data-toggle="tooltip"]').tooltip();
            $('[data-toggle="popover"]').popover();
        });
    </script>
</head>
<body style="overflow-y: scroll">
<%@ include file="/WEB-INF/jsp/admin-header.jsp" %>
<%@ include file="/WEB-INF/jsp/wizard-common.jsp" %>
<div class="container" style="margin-top: 60px;">
    <img src="css/images/acuity-logo.png"
         height="42" class="pull-right">

    <div class="col-md-4 pull-right">
        <div class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span> </span>
            <input id="adminSearch" type="text" class="form-control" placeholder="Search for drug programme...">

            <span class="input-group-addon" data-toggle="popover" data-placement="bottom" data-container="body"
                  data-trigger="hover focus" title="Search"
                  data-content="Search by programme name, study name, study code, study primary location, visualisation name">
                    <span class="glyphicon glyphicon-question-sign"></span> </span>
        </div>
    </div>

    <ul class="nav nav-tabs">
        <li class="active"><a href="#">Summary</a></li>
        <li><a href="/app#/audit">History</a></li>
        <li><a href="/app#/upload-summary">Upload summary</a></li>
    </ul>


    <div class="row" style="margin-top: 20px;">

        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title"> Drug Programmes
                    <a href="#" class="tooltipRef pull-right" data-toggle="tooltip" data-placement="bottom"
                       title='<%= HelpController.getHelpText("admin", "drugProgrammes") %>'>
                        <span class="glyphicon glyphicon-question-sign"></span>
                    </a>
                </h3>
            </div>
            <div class="panel-body">
                <div class="col-md-1">
                    <form id="edit-project-form" method="post" action="admin/edit-project">
                        <input type="hidden" id="projectId" name="projectId" value="STDY4102"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    <div class="btn-group-vertical">
                        <button id="editDrugProgrammeBtn" type="button"
                                class="btn btn-default disabled" disabled
                                onclick="forms['edit-project-form'].submit();">Edit
                        </button>
                        <button id="removeDrugProgrammeBtn" type="button"
                                class="btn btn-default disabled" disabled>Remove
                        </button>
                        <sec:authorize
                                access="@permissionHelper.isGlobalAdmin(authentication)">
                            <button id="addDrugProgrammeBtn" type="button"
                                    class="btn btn-default">Add
                            </button>
                        </sec:authorize>
                    </div>
                </div>
                <div class="col-md-5">
                    <div id="programmes-list" class="list-group"
                         style="max-height: 300px; overflow-y:auto; overflow-x: hidden;">
                    </div>
                </div>
                <div class="col-md-6">
                    <div id="programme-summary-block" style="display:none; word-break: break-all;">
                        <h4 id="programme-name"></h4>
                        <p>ID: <b id="programme-id"></b></p>

                        <p>Total number of datasets: <b id="programme-total-nbr-studies"></b></p>

                        <%--<p>Administrator: <b id="programme-admin"></b></p>--%>

                    </div>
                </div>

            </div>
        </div>
    </div>

    <div class="row">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title"> Clinical Study Data Sets
                    <a href="#" class="tooltipRef pull-right" data-toggle="tooltip" data-placement="bottom"
                       title='<%= HelpController.getHelpText("admin", "clinicalStudyDataSets") %>'>
                        <span class="glyphicon glyphicon-question-sign"></span>
                    </a>
                </h3>
            </div>
            <div class="panel-body">
                <div class="col-md-1">
                    <form id="edit-study-form" method="post" action="admin/edit-study">
                        <input type="text" id="studyId" name="studyId" value="" style="display:none"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    <div class="btn-group-vertical">
                        <button id="editStudyBtn" type="button"
                                class="btn btn-default disabled" disabled
                                onclick="forms['edit-study-form'].submit();">Edit
                        </button>
                        <button id="removeStudyBtn" type="button"
                                class="btn btn-default disabled" disabled>Remove
                        </button>
                        <button id="addStudyBtn" type="button"
                                class="btn btn-default">Add
                        </button>
                    </div>
                </div>
                <div class="col-md-5">
                    <div id="study-list" class="list-group"
                         style="max-height: 300px; overflow-y:auto; overflow-x: hidden;">
                    </div>
                </div>
                <div class="col-md-6">
                    <div id="study-summary-block" style="display:none; word-break: break-all;">
                        <h4 id="study-id" style="display: inline-block"></h4>
                        <a id="reportLink" target="report" href="report" class="reportLink">
                        <span class="label label-default">
                            <span class="glyphicon glyphicon-import"></span>View data load report
                        </span>
                        </a>
                        <br/>

                        <p>Dataset name: <b id="study-name"></b></p>
                        <p>Study name: <b id="clinical-study-name"></b></p>
                        <p>Study id: <b id="clinical-study-id"></b></p>

                        <p>Dataset study commence date: <b id="study-com-date"></b></p>

                        <p>Dataset study end date: <b id="study-end-date"></b></p>

                        <p>Dataset study phase: <b id="study-phase"></b></p>

                        <p>Blinding status: <b id="study-blinded"></b></p>

                        <p>Randomisation status: <b id="study-rand-status"></b></p>

                        <p>Regulatory status: <b id="study-reg-status"></b></p>

                        <p>Files locations: <b id="study-locations"></b></p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</body>
</html>
