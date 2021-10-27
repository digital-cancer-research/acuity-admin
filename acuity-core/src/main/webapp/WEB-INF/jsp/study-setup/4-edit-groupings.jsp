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

<script type="text/javascript" src="js/study-setup/4-edit-groupings.js"></script>

<input id="colGroupingName" type="hidden"
       value='<%= HelpController.getHelpText("4-edit-groupings", "colGroupingName") %>'/>
<input id="colDataSource" type="hidden" value='<%= HelpController.getHelpText("4-edit-groupings", "colDataSource") %>'/>
<input id="colAcuityEnabled" type="hidden"
       value='<%= HelpController.getHelpText("4-edit-groupings", "colAcuityEnabled") %>'/>
<input id="colLastEdited" type="hidden" value='<%= HelpController.getHelpText("4-edit-groupings", "colLastEdited") %>'/>
<input id="colDate" type="hidden" value='<%= HelpController.getHelpText("4-edit-groupings", "colDate") %>'/>
<input id="colSubjectID" type="hidden" value='<%= HelpController.getHelpText("4-edit-groupings", "colSubjectID") %>'/>
<input id="colGroupName" type="hidden" value='<%= HelpController.getHelpText("4-edit-groupings", "colGroupName") %>'/>

<div class="contentTable">
    <table id="studyGroupingsTable"></table>
    <input type="button" id="studyGroupingsRefresh" class="commonButton" value="Refresh data"/>
    <input type="button" id="studyGroupingsDelete" class="commonButton" style="float:right" value="Delete"/>
    <input type="button" id="studyGroupingsSettings" class="commonButton" style="float:right" value="Edit Settings"/>
    <input type="button" id="studyGroupingsAdd" class="commonButton" style="float:right" value="Add"/>
</div>
<div class="contentTable" style="margin-top:50px">
    <div id="studySubjectTableWrapper" style="display: none;">
        <table id="studySubjectTable"></table>
        <div id="studySubjectPager"></div>
        <input type="button" id="studySubjectAddRowBtn" class="commonButton" style="float:left" value="Add"/>
        <input type="button" id="studySubjectSaveBtn" class="commonButton" style="float:left" value="Save"/>
        <input type="button" id="studyDeleteGroupValues" class="commonButton" style="float:left" value="Delete"/>
        <input type="button" id="studySubjectCancelBtn" class="commonButton" style="float:left" value="Cancel"/>
    </div>
</div>


<div id="studyAddGroupingDlg" style="display:none">
    <form id="study-add-group-form" action="study-setup-save-group" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" id="studyGroupingId"></input>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="font-weight: bold;">Grouping name</td>
                <td><input type="text" style="padding: 0 0" id="studyGroupingName" name="name" maxlength="100"/></td>
                <td>
                    <div id="studyGroupingNameWarn">
                        <div class="groupWarningIcon crossIcon"></div>
                        <div style="color: #891a4f; display: inline-block;float: left;padding-left: 3px;margin-bottom: 2px;">
                            Duplicate name
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td style="font-weight: bold;">Default group value</td>
                <td><input type="text" style="padding: 0 0" value="Default group" id="studyDrugName"
                           name="defaultValue"/></td>
                <td></td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">The data source location</td>
            </tr>
            <tr>
                <td style="text-align:left; font-style: italic;"><span id="source-info">_The input file (*.csv) should have two columns in the following order: subject group name, subject identifier</span>
                </td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="radio" id="source-manually" name="studyDataSource" checked
                                                   value="source-manual"/><label for="source-manually"> I will enter the
                    groups manually</label></td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="radio" id="source-file" name="studyDataSource"
                                                   value="source-local"/><label for="source-file"> I will choose an
                    existing file</label></td>
            </tr>
            <tr>
                <td><input type="text" id="studyGropingSourceFile" style="margin-left:13px; float:left"/><label
                        for="studyFileinput" id="studyGroupBrowseBtn" class="labelButton">Browse</label><input
                        name="upload" type="file" accept=".csv" id="studyFileinput" style="display:none"/></td>


            <tr>
                <td style="text-align:left"><input type="radio" id="storage-source-manually" name="studyDataSource"
                                                   value="source-remote"/><label for="storage-source-manually"> I will
                    enter the file path manually</label></td>
            </tr>
            <tr>
                <td><input type="text" id="gropingStorage" style="margin-left:13px;float:left" name="remoteLocation"/>
                </td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">About the data source</td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="checkbox" id="source-headers" name="studyHeaderRow"
                                                   value="true" checked/><label for="source-headers"> Data has header
                    row</label></td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">What ACUITY does with the data</td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="checkbox" id="acuityEnabled" value="true" name="ready"/><label
                        for="acuityEnabled"> This source is ready to be used, so allow ACUITY to create views from the
                    data</label></td>
            </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <div style="float:right;margin-top:5px">
        <input type="button" id="studyGroupingsCancelBtn" class="commonButton" style="float:right" value="Cancel"/>
        <input type="button" id="studyGroupingsOkBtn" class="commonButton" style="float:right" value="Ok"/>
    </div>
</div>

