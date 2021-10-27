<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
<script type="text/javascript" src="js/programme-setup/2-edit-programme.js?1437123853"></script>
<div class="searchWrapper">
    <%@ include file="/WEB-INF/jsp/search-filter.jsp" %>
    <br/><br/><br/>

    <div class="headerStudy">
        <table border="0">
            <tr>
                <td><h2 id="study-project-name"></h2></td>
                <td><span id="edit-block-inputs" class="blocking"></span></td>
            </tr>
        </table>
    </div>

    <form id="form-edit-programme">
        <table border="0" class="dataTable">
            <tr>
                <td class="tdf">Drug Programme ID</td>
                <td><input type="text" id="drugProgrammeId" name="drugProgrammeId" disabled required/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "drugProgrammeID") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Drug Programme Name</td>
                <td><input type="text" class="enabled" required
                           id="drugProgrammeName" name="drugProgrammeName"
                           disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "drugProgrammeName") %>'>?</a></td>
            </tr>
            <tr class="hiddenOnAddProgramme">
                <td class="tdf">ACUITY enabled?</td>
                <td><input type="text" id="programmeEnabled" name="programmeEnabled" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "programmeEnabled") %>'>?</a></td>
            </tr>
            <tr class="hiddenOnAddProgramme">
                <td class="tdf">Total number of studies listed in the Impact database</td>
                <td><input type="text" id="studyNumberStudies" name="studyNumberStudies" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "studyNumberStudies") %>'>?</a></td>
            </tr>
            <tr class="hiddenOnAddProgramme">
                <td class="tdf">Number of ACUITY enabled studies</td>
                <td><input type="text" id="studyNumberEnabledStudies" name="studyNumberEnabledStudies" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "studyNumberEnabledStudies") %>'>?</a>
                </td>
            </tr>
            <tr style="display:none;">
                <td class="tdf">Create dashboard</td>
                <td><input class="enabled" type="checkbox" id="createDashboard" value="true" checked
                           name="createDashboard" disabled class="enabled"></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-programme", "createDashboard") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">AE severity type</td>
                <td>
                    <select class="enabled" id="aeSeverityType" name="aeSeverityType" maxlength="1" required disabled>
                        <option value="CTC_GRADES">CTC grades</option>
                        <option value="AE_INTENSITY">AE Intensity</option>
                    </select>
                </td>
            </tr>
        </table>
        <!--input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/-->
    </form>
</div>
