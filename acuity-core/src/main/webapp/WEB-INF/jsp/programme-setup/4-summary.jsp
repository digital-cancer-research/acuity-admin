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

<script type="text/javascript" src="js/programme-setup/4-summary.js"></script>
<table border="0" class="summaryTable">
    <tr>
        <td>
            <div style="margin-bottom:5px;text-align: center;">
                <label class="sum-header">Programme Summary</label>
            </div>
            <table border="0" class="summaryInnerTable">
                <tr>
                    <td class="tdf">Drug Programme ID :</td>
                    <td id="sumProgrammeDrugId"></td>
                </tr>
                <tr>
                    <td class="tdf">Drug Programme Name :</td>
                    <td id="sumProgrammeDrugName"></td>
                </tr>
                <tr>
                    <td class="tdf">ACUITY enabled? :</td>
                    <td id="sumProgrammeAcuityEnabled"></td>
                </tr>
                <tr>
                    <td class="tdf">Total number of studies listed in the Impact database :</td>
                    <td id="sumProgrammeNumStudies"></td>
                </tr>
                <tr>
                    <td class="tdf">Number of ACUITY enabled studies :</td>
                    <td id="sumProgrammeNumEnabledStudies"></td>
                </tr>
                <tr style="display:none;">
                    <td class="tdf">Create dashboard :</td>
                    <td id="sumCreateDashboard"></td>
                </tr>
            </table>
        </td>
        <td style="vertical-align:top;">
            <div style="float:right" class="contentTable">
                <div style="margin-bottom:5px;">
                    <label class="sum-header">Custom data groupings</label>
                    <a href="javascript:void(0)" class="editLink" id="summaryLinkToGroup">view/edit</a>
                </div>
                <table id="projectSumGroupTable"></table>
                <div id="programmeSummaryGroupPager"></div>
            </div>
        </td>
    </tr>
</table>




