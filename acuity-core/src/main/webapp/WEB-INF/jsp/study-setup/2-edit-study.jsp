<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<script type="text/javascript" src="js/study-setup/2-edit-study.js"></script>
<div class="searchWrapper" style="margin: 0px 0px 0px 250px;">
    <div class="headerStudy" style="margin-top: 0px;">
        <table border="0">
            <tr>
                <td><h2 id="selected-study-code"
                        style="margin: 5px 5px 5px 100px;">SELECTED DATASET</h2></td>
                <td><span id="study-edit-block-inputs" class="blocking"></span></td>
            </tr>
        </table>
    </div>
    <form id="form-edit-study">
        <table border="0" class="dataTable">
            <tr>
                <td class="tdf">Drug ID</td>
                <td><input type="text" id="studyDrugId" name="studyDrugId" disabled/>
                    <select type="text" class="enabled"
                            id="studyDrugIdSelect"
                            name="studyDrugIdSelect" maxlength="1">
                    </select></td>
                <td><a href="#" class="help" title='<%= HelpController.getHelpText("2-edit-study", "drugID") %>'>?</a>
                </td>
            </tr>
            <tr>
                <td class="tdf">Dataset ID</td>
                <td><input type="text"
                           id="studyCode" name="studyCode" disabled required/></td>
                <td><a href="#" class="help" title='<%= HelpController.getHelpText("2-edit-study", "studyID") %>'>?</a>
                </td>
            </tr>
            <tr>
                <td class="tdf">Dataset name</td>
                <td><input type="text" class="enabled" required
                           id="studyName" name="studyName"
                           disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "studyName") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Clinical study code</td>
                <td><input type="text" class="enabled" required
                           id="clinicalStudyId" name="clinicalStudyId"
                           disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "clinicalStudyId") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Clinical study name</td>
                <td><input type="text" class="enabled" required
                           id="clinicalStudyName" name="clinicalStudyName"
                           disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "clinicalStudyName") %>'>?</a></td>
            </tr>
            <tr style="display: none;">
                <td class="tdf">Study phase</td>
                <td><input type="text" class="enabled"
                           id="studyPhase"
                           name="studyPhase" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "studyPhase") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Study phase</td>
                <td>
                    <select class="enabled" required id="studyPhaseType" name="studyPhaseType" disabled>
                        <option value="EARLY">Early (Phases I or II)</option>
                        <option value="LATE">Late (Phase III)</option>
                    </select>
                </td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "studyPhase") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Dataset study type</td>
                <td><input type="text" class="enabled" required
                           id="studyType"
                           name="studyType" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "studyType") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Dataset study delivery model</td>
                <td><input type="text" class="enabled" required
                           id="studyDeliveryModel"
                           name="studyDeliveryModel" disabled/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "studyDeliveryModel") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Planned date for first subject in</td>
                <td><input type="text" class="enabled" required
                           id="studyFsiPln"
                           name="studyFsiPln" disabled value='04/04/2013'/></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "subjectPlannedDate") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Planned date of database lock</td>
                <td><input type="text" class="enabled" required
                           id="studyDblPln"
                           name="studyDblPln" disabled value='04/04/2013'/></td>
                <td><a href="#" class="help" title='<%= HelpController.getHelpText("2-edit-study", "dbPlannedDate") %>'>?</a>
                </td>
            </tr>
            <tr>
                <td class="tdf">Primary Source Folder</td>
                <td><input type="text" class="enabled"
                           id="studyPrimarySource"
                           name="studyPrimarySource" disabled/></td>
                <td><a href="#" class="help"
                       title="<%= HelpController.getHelpText("2-edit-study", "studyPrimarySource") %>">?</a></td>
            </tr>
        </table>
        <div style="width:500px;margin-top:20px;margin-bottom:10px">
            <h1 style="font-size:20px;font-weight:600;display:block;margin-left:auto;margin-right:auto;width:100px;margin-bottom:4px;">
                Attention!</h1>
            The following parameters define the blinding, randomisation and regulatory aspects of the study. Wrong
            assignment of any one of these
            parameters could cause the ACUITY system to produce inappropriate analyses that could jeopardise the
            integrity
            of the study. It is the
            responsibility of you, the ACUITY user to ensure that these values are properly assigned. If you are in any
            doubt, please leave these
            values at their defaults and contact ACUITY Support for
            guidance.
        </div>
        <table border="0" class="dataTable">
            <tr>
                <td class="tdf" style="width:220px">Blinding</td>
                <td style="width:270px"><select type="text" class="enabled" required
                                                id="studyBlinding"
                                                name="studyBlinding" disabled>
                    <option value=true>Blinded</option>
                    <option value=false>Not blinded</option>
                </select></td>
                <td><a href="#" class="help" title='<%= HelpController.getHelpText("2-edit-study", "blinded") %>'>?</a>
                </td>
            </tr>
            <tr>
                <td class="tdf">Randomisation</td>
                <td><select type="text" class="enabled" required
                            id="studyRandomisation"
                            name="studyRandomisation" disabled>
                    <option value="true">Randomised</option>
                    <option value="false">Not randomised</option>
                </select></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "randomised") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Regulatory</td>
                <td><select type="text" class="enabled" required
                            id="studyRegulatory"
                            name="studyRegulatory" disabled>
                    <option value=true>Regulated</option>
                    <option value=false>Not regulated</option>
                </select></td>
                <td><a href="#" class="help"
                       title='<%= HelpController.getHelpText("2-edit-study", "regulatory") %>'>?</a></td>
            </tr>
            <tr>
                <td class="tdf">Scheduled</td>
                <td><input class="enabled" type="checkbox" id="studyScheduled" value="true" checked
                           name="studyScheduled" disabled></td>
            </tr>
            <tr>
                <td class="tdf">Automatically assign country based on AZ standard E-codes</td>
                <td><input class="enabled" type="checkbox" id="autoAssignedCountry" value="true" checked
                           name="autoAssignedCountry" disabled></td>
            </tr>
            <tr>
                <td class="tdf">Limit X-Axis options to Visit Number</td>
                <td><input class="enabled" type="checkbox" id="xAxisLimitedToVisit" value="true" checked
                           name="v" disabled></td>
            </tr>
        </table>

        <c:if test='${amlEnabledGlobally}'>
            <div style="width:500px;margin-top:20px;margin-bottom:10px">
                <h1 style="font-size:20px;font-weight:600;text-align: center;">
                    Machine Insights Settings
                    <a href="#" class="help" title='<%= HelpController.getHelpText("2-edit-study", "amlEnabledForStudy") %>'>?</a>
                </h1>
                <table border="0" class="dataTable">
                    <tr>
                        <td class="tdfkeyparam">Enable QT Interval algorithm</td>
                        <td><input class="enabled" type="checkbox" id="amlEnabled" value="true" name="amlEnabled"
                                   disabled></td>
                    </tr>
                </table>
            </div>

        </c:if>
    </form>
</div>
