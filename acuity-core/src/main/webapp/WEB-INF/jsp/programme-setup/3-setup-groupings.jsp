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

<script type="text/javascript" src="js/programme-setup/3-setup-groupings.js?1436877341"></script>

<input id="colGroupingName" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colGroupingName") %>'/>
<input id="colTypeOfGroup" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colTypeOfGroup") %>'/>
<input id="colDataSource" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colDataSource") %>'/>
<input id="colAcuityEnabled" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colAcuityEnabled") %>'/>
<input id="colLastEdited" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colLastEdited") %>'/>
<input id="colDate" type="hidden" value='<%= HelpController.getHelpText("4-setup-groupings", "colDate") %>'/>
<input id="colAeGroupName" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colAeGroupName") %>'/>
<input id="colMedDra" type="hidden" value='<%= HelpController.getHelpText("4-setup-groupings", "colMedDra") %>'/>
<input id="colLabGroupingName" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colLabGroupingName") %>'/>
<input id="colLabcodeValue" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colLabcodeValue") %>'/>
<input id="colLabDescription" type="hidden"
       value='<%= HelpController.getHelpText("4-setup-groupings", "colLabDescription") %>'/>

<div class="contentTable">
    <table id="groupingsTable"></table>
    <input type="button" id="groupingsRefresh" class="commonButton disabled" disabled value="Refresh data"/>
    <input type="button" id="groupingsDelete" class="commonButton disabled" disabled style="float:right"
           value="Delete"/>
    <input type="button" id="groupingsSettings" class="commonButton disabled" disabled style="float:right"
           value="Edit Settings"/>
    <input type="button" id="groupingsAdd" class="commonButton" style="float:right" value="Add"/>
</div>
<div class="contentTable" style="margin-top:50px">
    <div id="groupAdverseEventTableWrapper" style="display: none;">
        <table id="groupAdverseEventTable"></table>
        <div id="groupAdvEventPager"></div>
        <input type="button" id="groupTableAddRowBtn" class="commonButton" style="float:left" value="Add"/>
        <input type="button" id="groupTableSaveBtn" class="commonButton" style="float:left" value="Save"/>
        <input type="button" id="groupTableDeleteBtn" class="commonButton" style="float:left" value="Delete"/>
        <input type="button" id="aeTableCancelBtn" class="commonButton" style="float:left" value="Cancel"/>
    </div>
</div>

<div class="contentTable" style="margin-top:50px">
    <div id="groupLabTestTableWrapper" style="display: none;">
        <table id="groupLabTestTable"></table>
        <div id="groupLabTestPager"></div>
        <input type="button" id="groupManuallyAddRowBtn" class="commonButton" style="float:left" value="Add"/>
        <input type="button" id="groupManuallySaveBtn" class="commonButton" style="float:left" value="Save"/>
        <input type="button" id="groupManuallyDeleteBtn" class="commonButton" style="float:left" value="Delete"/>
        <input type="button" id="labTableCancelBtn" class="commonButton" style="float:left" value="Cancel"/>
    </div>
</div>


<div id="addGroupingDlg" style="display:none">
    <form id="add-group-form" action="programme-setup/programme-save-group" method="post" enctype="multipart/form-data">
        <table border="0" class="propertiesTable">
            <tr>
                <td style="font-weight: bold;">Grouping type</td>
                <td><select id="groupType" name="groupType">
                    <option value="ae">Adverse Event</option>
                    <option value="lab">Lab Test</option>
                </select></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("4-setup-groupings", "groupType") %>'>?</a></td>
            </tr>
            <tr>
                <td style="font-weight: bold;">Grouping name</td>
                <td><input type="text" style="padding: 0 0;" id="groupingName" name="name"
                           placeholder="Enter group name" maxlength="100"/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("4-setup-groupings", "groupingName") %>'>?</a>
                    <div id="groupingNameWarn">
                        <div class="groupWarningIcon crossIcon"></div>
                        <div style="color: #891a4f; display: inline-block;float: left;padding-left: 3px;margin-bottom: 2px;">
                            Duplicate name
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td style="font-weight: bold;">Default group value</td>
                <td><input type="text" style="padding: 0 0" value="Default group" id="defaultValue"
                           name="defaultValue"/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("4-setup-groupings", "defaultValue") %>'>?</a></td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">The data source location<a href="#" class="help"
                                                                                           title='<%= HelpController.getHelpText("4-setup-groupings", "dataLocation") %>'>?</a>
                </td>
            </tr>
            <tr>
                <td style="text-align:left; font-style: italic;"><span id="source-info"></span></td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="radio" id="source-manually" name="dataSource" checked
                                                   value="source-manual"/><label for="source-manually"> I will enter the
                    groups manually</label></td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="radio" id="source-file" name="dataSource"
                                                   value="source-local"/><label for="source-file"> I will choose an
                    existing file</label></td>
            </tr>
            <tr>
                <td><input type="text" id="gropingSourceFile" style="margin-left:13px;float: left;"/><label
                        for="fileinput" id="group-browse-btn" class="labelButton">Browse</label><input name="upload"
                                                                                                       type="file"
                                                                                                       id="fileinput"
                                                                                                       style="display:none"/>
                </td>


            <tr>
                <td style="text-align:left"><input type="radio" id="storage-source-manually" name="dataSource"
                                                   value="source-remote"/><label for="storage-source-manually"> I will
                    enter the file path manually</label></td>
            </tr>
            <tr>
                <td><input type="text" id="gropingStorage" style="margin-left:13px;float: left;" name="remoteLocation"/>
                </td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">About the data source</td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="checkbox" id="source-headers" name="headerRow" value="true"
                                                   checked/><label for="source-headers"> Data has header row</label><a
                        href="#" class="help"
                        title='<%= HelpController.getHelpText("4-setup-groupings", "headerRow") %>'>?</a></td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">What ACUITY does with the data</td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="checkbox" id="acuityEnabled" value="true" name="ready"/><label
                        for="acuityEnabled"> This source is ready to be used, so allow ACUITY to use these
                    groups</label><a href="#" class="help"
                                     title='<%= HelpController.getHelpText("4-setup-groupings", "dataReady") %>'>?</a>
                </td>
            </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <div style="float:left;margin-top:5px">
        <input type="button" id="groupingsOkBtn" class="commonButton" style="float:right" value="Ok"/>
        <input type="button" id="groupingsCancelBtn" class="commonButton" style="float:right" value="Cancel"/>
    </div>
</div>

