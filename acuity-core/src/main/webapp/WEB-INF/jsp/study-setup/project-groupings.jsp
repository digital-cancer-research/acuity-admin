<%@page import="com.acuity.visualisations.web.controller.HelpController"%>
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

<script type="text/javascript"
	src="js/study-setup/project-groupings.js"></script>

<div class="contentTable" style="display: inline-block;">
	<div style="margin-bottom: 5px;">
		<label class="sum-header">AE Groupings</label> <a href="#" class="help" title='<%= HelpController.getHelpText("2-project-groupings", "aeGroupings") %>'>?</a>
	</div>
	<table id="AEGroupingsTable"></table>
</div>

<div class="contentTable"
	style="display: inline-block; margin-left: 20px;">
	<div style="margin-bottom: 5px;">
		<label class="sum-header">Lab Groupings</label> <a href="#" class="help" title='<%= HelpController.getHelpText("2-project-groupings", "labGroupings") %>'>?</a>
	</div>
	<table id="labGroupingsTable"></table>
</div>

<p id="pt-grp-msg" style="margin-top:20px; display:none;">
Note: Adverse event and lab groupings can be created in the drug programme level setup pages for <span></span>, click <a id="programmeSetupLink" class="hereLink" target="_blank">here</a> to navigate to those pages.
<form id="edit-programme-form" method="post" action="instance-edit-programme-setup">
    <input type="text" id="projectId" name="projectId" value="" style="display:none"/>
    <input  id="submitEditProgrammeActionId" type="submit" name="submitEditProgrammeActionId" style="display:none"/>
     <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
</p>
