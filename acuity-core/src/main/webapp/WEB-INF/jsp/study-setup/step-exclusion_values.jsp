<%@page import="com.acuity.visualisations.web.controller.HelpController"%>
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
