<%@page import="com.acuity.visualisations.web.controller.HelpController" %>

<script type="text/javascript" src="js/study-setup/1-search-study.js"></script>

<input id="colStudyID" type="hidden" value='<%= HelpController.getHelpText("1-search-study", "colStudyID") %>'/>
<input id="colStudyName" type="hidden" value='<%= HelpController.getHelpText("1-search-study", "colStudyName") %>'/>
<input id="colPhase" type="hidden" value='<%= HelpController.getHelpText("1-search-study", "colPhase") %>'/>
<input id="colStudySetupStatus" type="hidden"
       value='<%= HelpController.getHelpText("1-search-study", "colStudySetupStatus") %>'/>

<div class="searchWrapper">
    <jsp:include page="/WEB-INF/jsp/search-filter.jsp">
        <jsp:param name="stepIndex" value="${param.stepIndex}"/>
    </jsp:include>
    <br/><br/><br/>
    <div class="searchBar">
        <a href="javascript:void(0)" id="study-link-all-projects" class="searchLink"></a>
        <div id="add-new-study-block">
            <a id="smartBtnNextAddStudy" href="#" class="buttonNext">Add New</a>
            <label class="label-add-next">Can not find your study in the list?</label>
        </div>
    </div>
</div>
<br/><br/><br/>
<div class="contentTable">
    <table id="studySearchResultTable"></table>
    <div id="studyResultPager"></div>
</div>


<div id="studyNotInACUITYDlg" style="display:none;">
    <div>
        <p><label id="studyNotInACUITYDlgMessage"></label></p>
    </div>
    <input type="button" id="studyNotInACUITYDlgOkBtn" class="commonButton" value="OK" style="float:right;"/>
    <form id="edit-programme-form" method="post" action="study-edit-programme-setup">
        <input type="text" id="drugId" name="drugId" value="" style="display:none"/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input id="submitEditProgrammeActionId" type="submit" name="submitEditProgrammeActionId" style="display:none"/>
    </form>
</div>
