<%@page import="com.acuity.visualisations.web.controller.HelpController"%>
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
