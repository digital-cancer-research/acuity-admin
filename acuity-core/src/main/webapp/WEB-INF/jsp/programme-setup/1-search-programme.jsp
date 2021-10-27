<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
<script type="text/javascript" src="js/programme-setup/1-search-programme.js"></script>

<input id="colRadio" type="hidden" value='<%= HelpController.getHelpText("1-search-programme", "colRadio") %>'/>
<input id="colDrugID" type="hidden" value='<%= HelpController.getHelpText("1-search-programme", "colDrugID") %>'/>
<input id="colNStudies" type="hidden" value='<%= HelpController.getHelpText("1-search-programme", "colNStudies") %>'/>
<input id="colNAcuityStudies" type="hidden"
       value='<%= HelpController.getHelpText("1-search-programme", "colNAcuityStudies") %>'/>
<input id="colAcuityEnabled" type="hidden"
       value='<%= HelpController.getHelpText("1-search-programme", "colAcuityEnabled") %>'/>
<input id="colAction" type="hidden" value='<%= HelpController.getHelpText("1-search-programme", "colAction") %>'/>
<div class="searchWrapper">
    <jsp:include page="/WEB-INF/jsp/search-filter.jsp">
        <jsp:param name="stepIndex" value="${param.stepIndex}"/>
    </jsp:include>
    <br/><br/><br/>
    <div class="searchBar">
        <a href="javascript:void(0)" id="link-all-projects" class="searchLink"></a>
        <div id="add-new-programme-block">
            <button id="smartBtnNextAddProgramme" class="buttonNext" type="button">Add New</button>
            <label class="label-add-next">Cannot find your programme in the list?</label>
        </div>
    </div>
</div>
<br/><br/><br/>
<div class="contentTable">
    <table id="searchResultTable"></table>
    <div id="projectResultPager"></div>
</div>
