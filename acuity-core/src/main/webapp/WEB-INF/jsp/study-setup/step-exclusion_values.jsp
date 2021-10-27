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

<script type="text/javascript" src="js/study-setup/step-exclusion_values.js"></script>

<input id="colExclMapping" type="hidden" value="<%= HelpController.getHelpText("exclusion-values", "colExclMapping") %>" />
<input id="colExclDataField" type="hidden" value="<%= HelpController.getHelpText("exclusion-values", "colExclDataField") %>" />
<input id="colExclValue" type="hidden" value="<%= HelpController.getHelpText("exclusion-values", "colExclValue") %>" />
		<table border="0">
			<tr>
				<td><h2 id="exc-values-std-name" style="margin: 5px 5px 5px 100px;"></h2></td>
				<td><span id="exc-values-block-inputs" class="blocking"></span></tr>
		</table>
<div class="contentTable">
    <table id="exclusionValuesTable" class="enabled"></table>
    <div id="exclusionValuesButtons">
        <input type="button" id="exclusionValuesAddButton" class="commonButton enabled" style="float:left" value="Add"/>
        <input type="button" id="exclusionValuesDeleteButton" class="commonButton enabled" style="float:left" value="Delete"/>
        <input type="button" id="exclusionValuesSaveButton" class="commonButton enabled" style="float:left" value="Save"/>
    </div>
    <div style="clear: both;"></div>
</div>
