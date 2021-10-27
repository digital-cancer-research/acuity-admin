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

<%--<script type="text/javascript" src="js/study-setup/step-cbioportal-genomic-profile.js"></script>--%>
<div style="text-align: center;">
    <em style="font-size:20px;font-weight:bold;">
        Attention!
    </em>
    <br>
    The subject IDs must be the same in both data sets to be loaded, ACUITY and cBioPortal.
    <br><br>
    Select the genomic profiles that have been loaded into cBioPortal from the list below.
    <br>
    <b><i>NOTE: Ideally the cBioPortal Study name should be the same as the ACUITY study name - this will allow you to easily
    identify the study on the cBioPortal study page when the link below is used to determine the cBioPortal genomic
    profiles for entry into ACUITY.</i></b>
    <br>
    <b><i>This will also help the end user identify the study when linking out from the genomic profile plot in ACUITY into cBioPortal.</i></b>
    <br>
    <br>
    To determine which profiles have been loaded, <a href=<%= request.getAttribute("cBioPortalUrl") %> target="_blank">click here</a> to launch the linked
    cBioPortal instance in a new tab.
    <br>
    Select the study you are loading from the 'Select Studies' window.
    <br>
    The genomic profiles loaded into cBioPortal will be shown against 'Select Genomic Profiles'.
    <br>
    From the list below select the genomic profiles shown to be loaded into cBioPortal.
    <br>
    <div>
        <label style="font-size:20px; font-weight: 600;">Enable cBioPortal</label>
        &nbsp;
        <input type="checkbox" id="cbioPortalEnableStatus" class="double_size" style="margin-top: -10px"/>
    </div>
    <br>
    <div>
        <label>cBioPortal Study ID </label>
        <input type="text" id="cbioPortalStudyCode" disabled="true"/>
        <br>
        <b><i>NOTE: Ideally the cBioPortal Study ID should be the same as the ACUITY study ID</i></b>
    </div>
    <div style="margin-top:20px;margin-bottom:10px">
        <h1 style="font-size:20px;font-weight:600;text-align: center;">
            cBioPortal Genomic Profiles
        </h1>
        <table border="0" class="dataTable largeSecondColumn centered" style="float: none;">
            <tbody>
            <tr>
                <td class="tdfkeyparam">Mutations</td>
                <td><input class="enabled" type="checkbox" id="mutationsProf" value="true" name="mutationsProf"
                           checked="" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">CNA data (linear)</td>
                <td><input class="enabled" type="checkbox" id="cnaLinProf" value="true" name="cnaLinProf" disabled="">
                </td>
            </tr>
            <tr>
                <td class="tdfkeyparam">CNA data (discrete)</td>
                <td><input class="enabled" type="checkbox" id="cnaDiscProf" value="true" name="cnaDiscProf" disabled="">
                </td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Putative CNA from GISTIC</td>
                <td><input class="enabled" type="checkbox" id="cnaGisticProf" value="true" name="cnaGisticProf"
                           disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">mRNA Expression (U133 microarray only)</td>
                <td><input class="enabled" type="checkbox" id="mRNAU133Prof" value="true" name="mRNAU133Prof"
                           disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">mRNA Expression z-Scores (U133 microarray only)</td>
                <td><input class="enabled" type="checkbox" id="mRNAU133zScoresProf" value="true"
                           name="mRNAU133zScoresProf" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">mRNA Expression z-Scores (microarray)</td>
                <td><input class="enabled" type="checkbox" id="mRNAzScoresProf" value="true" name="mRNAzScoresProf"
                           disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">mRNA Expression (RNA Seq V2 RSEM)</td>
                <td><input class="enabled" type="checkbox" id="mRNASeqProf" value="true" name="mRNASeqProf" disabled="">
                </td>
            </tr>
            <tr>
                <td class="tdfkeyparam">mRNA Expression z-Scores (RNA Seq V2 RSEM)</td>
                <td><input class="enabled" type="checkbox" id="mRNASeqzScoresProf" value="true"
                           name="mRNASeqzScoresProf" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Methylation (HM27)</td>
                <td><input class="enabled" type="checkbox" id="methylationH27Prof" value="true"
                           name="methylationH27Prof" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Methylation (HM450)</td>
                <td><input class="enabled" type="checkbox" id="methylationH450Prof" value="true"
                           name="methylationH450Prof" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Protein Expression (RPPA)</td>
                <td><input class="enabled" type="checkbox" id="rppaProf" value="true" name="rppaProf" disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Protein Expression Z-scores (RPPA)</td>
                <td><input class="enabled" type="checkbox" id="rppazScoresProf" value="true" name="rppazScoresProf"
                           disabled=""></td>
            </tr>
            <tr>
                <td class="tdfkeyparam">Protein level Z-scores (mass spectrometry by CPTAC) (RPPA)</td>
                <td><input class="enabled" type="checkbox" id="rppaCptaczScoresProf" value="true"
                           name="rppaCptaczScoresProf" disabled=""></td>
            </tr>
            </tbody>
        </table>

    </div>
</div>
