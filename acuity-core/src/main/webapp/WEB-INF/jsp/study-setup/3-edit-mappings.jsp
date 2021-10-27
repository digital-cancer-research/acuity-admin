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

<script type="text/javascript" src="js/study-setup/3-edit-mappings.js?1455809630"></script>

<input id="colTypeOfInfo" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colTypeOfInfo") %>'/>
<input id="colDataSource" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colDataSource") %>'/>
<input id="colAcuityEnabled" type="hidden"
       value='<%= HelpController.getHelpText("3-edit-mappings", "colAcuityEnabled") %>'/>
<input id="colLastEdited" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colLastEdited") %>'/>
<input id="colDate" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colDate") %>'/>
<input id="colDataField" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colDataField") %>'/>
<input id="colSourceData" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colSourceData") %>'/>
<input id="colDecodingInfo" type="hidden"
       value='<%= HelpController.getHelpText("3-edit-mappings", "colDecodingInfo") %>'/>
<input id="colDefaultValue" type="hidden"
       value='<%= HelpController.getHelpText("3-edit-mappings", "colDefaultValue") %>'/>
<input id="colAggregation" type="hidden"
       value='<%= HelpController.getHelpText("3-edit-mappings", "colAggregation") %>'/>
<input id="colMandatory" type="hidden" value='<%= HelpController.getHelpText("3-edit-mappings", "colMandatory") %>'/>

<input id="Study identifier" type="hidden"
       value='<%= HelpController.getHelpText("3-edit-mappings", "Study identifier") %>'/>

<div class="contentTable">
    <table id="mainStudyTable"></table>
    <input type="button" id="mainStudyMoveUpBtn" class="commonButton" value="Move up"/>
    <input type="button" id="mainStudyMoveDownBtn" class="commonButton" value="Move down"/>
    <input type="button" id="mainStudyDeleteBtn" class="commonButton" style="float:right" value="Delete"/>
    <input type="button" id="mainStudyEditSettingsBtn" class="commonButton" style="float:right" value="Edit Settings"/>
    <input type="button" id="mainStudyAutoMapButton" class="commonButton" style="float:right" value="Auto-map"/>
    <input type="button" id="mainStudyAdd" class="commonButton" style="float:right" value="Add"/>
    <input type="button" id="mainStudyExport" class="commonButton" style="float:right" value="Export mappings"/>
    <input type="button" id="mainStudyImport" class="commonButton" style="float:right" value="Import mappings"/>
</div>
<div class="contentTable" style="margin-top:50px">
    <div id="studyDetailsTableWrapper">
        <table id="studyDetailsTable"></table>
        <div id="studyDetailsTableMappingButtons">
            <input type="button" id="studyDetailsTableAddMappingRuleBtn" class="commonButton" style="float:left;"
                   value="Add mapping rule"/>
            <input type="button" id="studyDetailsTableDeleteMappingRuleBtn" class="commonButton" style="float:left;"
                   value="Delete mapping rule"/>
        </div>
        <div style="clear:both; padding: 5px;">
            <input type="checkbox" id="studyAcuityEnabled" value="true" checked name="studyAcuityEnabled"/>
            <label for="studyAcuityEnabled">I confirm that these mappings are correct, and that ACUITY can use this
                data</label>
        </div>

        <input type="button" id="studyDetailsTableSaveBtn" class="commonButton" style="float:left;" value="Save"/>
        <input type="button" id="studyDetailsCancelBtn" class="commonButton" style="float:left;" value="Cancel"/>
        <input type="button" id="studyDetailsExportBtn" class="commonButton" style="float:left; margin-left: 30px;"
               value="Export"/>
        <div class="commonButton"
             style="text-align: center; overflow: hidden; width: 66px; height: 25px; padding-top: 4px;">
            Import
            <input type="file" accept=".csv" id="studyDetailsImportBtn" size="1"
                   style="margin-top: -25px; margin-left:-410px; -moz-opacity: 0; filter: alpha(opacity=0); opacity: 0; font-size: 150px; height: 100px;">
        </div>
    </div>
</div>

<div id="addStudyDlg" style="display:none">
    <form id="fileRuleForm">

        <input type="hidden" id="fileRuleId" name="id"/>
        <br/>

        <div class="form-group">
            <label class="control-label">
                Data type
                <select id="studyDataType" name="typeId" multiple="multiple" style="width: 400px;"></select>
            </label>
            <a href="#" class="help" title='<%= HelpController.getHelpText("3-edit-mappings", "dataType") %>'>?</a>
        </div>

        <h5>
            The data source location
            <a href="#" class="help" title='<%= HelpController.getHelpText("3-edit-mappings", "dataLocation") %>'>?</a>
        </h5>

        <div class="radio">
            <label>
                <input type="radio" id="study-no-file" name="dataSourceLocationType" checked
                       value="studyNoFile"/>
                The file does not yet exist, or I do not know the path
            </label>
        </div>
        <div class="radio">
            <label>
                <input type="radio" id="study-file-prediction" name="dataSourceLocationType"
                       value="filePrediction"/>
                Predict which source file to use based on past mappings
            </label>
        </div>
        <div class="radio">
            <label>
                <input type="radio" id="study-source-remote" name="dataSourceLocationType"
                       value="studySourceRemote"/>
                I will enter the file path manually
            </label>
        </div>

        <div>
            <input type="text" id="studyStorageText" name="dataSourceLocation" class="form-control"
                   placeholder="File location">
        </div>

        <br/>
        <h5>
            What ACUITY does with the data
            <a href="#" class="help" title='<%= HelpController.getHelpText("3-edit-mappings", "whatWithData") %>'>?</a>
        </h5>

        <div class="checkbox">
            <label>
                <input type="checkbox" id="mappingsPrediction" value="true" checked name="mappingsPrediction"/>
                Predict column mapping details based on past mappings
            </label>
        </div>

        <div class="checkbox">
            <label>
                <input type="checkbox" id="isSdtm" value="true" checked name="isSdtm"/>
                SDTM file
            </label>
        </div>

        <div style="float:right;margin-top:5px">
            <input type="button" id="studyCancelBtn" class="commonButton" style="float:right" value="Cancel"/>
            <input type="button" id="studyOkBtn" class="commonButton" style="float:right" value="Ok"/>
        </div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>

<div id="studyImportMappingDlg" style="display:none">
    <form id="study-import-mapping-form" action="import-study-mapping" method="post" enctype="multipart/form-data">
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left; font-weight: bold;">Please select mapping data file</td>
            </tr>
            <tr>
                <td style="text-align:left"><input name="upload" type="file" accept=".csv" id="studyMappingInput"/></td>
            </tr>
        </table>
        <table border="0" class="propertiesTable">
            <tr>
                <td style="text-align:left"><input type="checkbox" id="replaceExisting" value="true"
                                                   name="replaceExisting"/><label for="replaceExisting">Replace existing
                    mappings?</label></td>
            </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <div style="float:right;margin-top:5px">
        <input type="button" id="importMappingsCancelBtn" class="commonButton" style="float:right" value="Cancel"/>
        <input type="button" id="importMappingsOkBtn" class="commonButton" style="float:right" value="Ok"/>
    </div>
</div>
