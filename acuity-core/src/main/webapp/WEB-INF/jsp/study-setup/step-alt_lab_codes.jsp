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

<script type="text/javascript" src="js/study-setup/step-alt_lab_codes.js?20140325_2"></script>

<div>
    Please select a decoding standard:
    <select id="altLabCodesSelect">
        <option value="azraw" selected>Use AZ RAW values</option>
        <option value="custom">Use Custom values</option>
    </select>
</div>

<br/>

<div id="altLabCodesTableWrap" class="contentTable">
    <table id="altLabCodesTable"></table>
    <div id="altLabCodesPager"></div>
    <div id="altLabCodesButtons">
        <input type="button" id="altLabCodesAddButton" class="commonButton" style="float:left" value="Add new decoding value"/>
        <input type="button" id="altLabCodesSaveButton" class="commonButton" style="float:left" value="Save changes"/>
        <input type="button" id="altLabCodesDeleteButton" class="commonButton" style="float:left" value="Delete"/>
        <input type="button" id="altLabCodesCancelButton" class="commonButton" style="float:left" value="Discard changes"/>
    </div>
    <div style="clear: both;"></div>
</div>
