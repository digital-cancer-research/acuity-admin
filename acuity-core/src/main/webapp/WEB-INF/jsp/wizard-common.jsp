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

<script type="text/javascript" src="js/wizard-common.js"></script>
<div id="waitingDlg" style="display:none;">
 <img src='css/images/ajax-loader.gif' style="float:left;"/>
 <div style="float:left; padding: 2px 0 0 5px;">
    <p><label id="waitingDlgInfo">Please wait...</label></p>
 </div>
</div>

<div id="waitingCancelDlg" style="display:none;">
 <img src='css/images/ajax-loader.gif' style="float:left;"/>
 <div style="float:left; padding: 2px 0 0 5px;">
    <p><label id="waitingCancelDlgInfo">Please wait...</label></p>
 </div>
 <input type="button" id="waitingDlgCancelBtn" class="commonButton" value="Cancel" style="float:right;"/>
 <div style="clear:both;"></div>
</div>

<div id="progressDlg" style="display:none;">
 <img src='css/images/ajax-loader.gif' style="float:left;"/>
 <div style="float:left; padding: 2px 0 0 5px;">
    <p><label id="progressDlgInfo"></label></p>
    <p><label id="progressDlgMsg"></label></p>
 </div>
</div>
<div id="errorDlg" style="display:none;">
<div>The ACUITY system has experienced an error, click <a id="linkToACUITYSupport" class="errorEmailLink">here</a> to report the error</div>
<input type="button" id="errorDlgStackBtn" class="commonButton" value="Show details"/>
 <div id="error-block" style="min-height:30px;height:390px;display:none;margin-top: 30px;overflow:auto;">
    <p><label id="errorDlgMessage"></label></p>
 </div>
 <input type="button" id="errorDlgOkBtn" class="commonButton" value="OK" style="float:right;"/>
</div>
<div id="warningDlg" style="display:none;">
 <div>
    <p><label id="warningDlgMessage"></label></p>
 </div>
 <input type="button" id="warningDlgOkBtn" class="commonButton" value="OK" style="float:right;"/>
</div>

<div id="sessionExpiredDlg" style="display: none;">
  <div>
    <p>Your session has expired.<br/>You will be redirected to the Login page.</p>
  </div>
 <input type="button" id="sessionExDlgOkBtn" class="commonButton" value="OK" style="float:right;"/>
</div>

<div id="accessDeniedDlg" style="display: none;">
  <div>
    <p><label id="accessDeniedMsg">Access denied</label></p>
  </div>
 <input type="button" id="accessDeniedDlgOkBtn" class="commonButton" value="OK" style="float:right;"/>
</div>

<div id="yesNoDlg" style="display: none;">
    <div>
        <p id="yesNoDlgMessage"></p>
    </div>
    <input type="button" id="yesNoDlgNoBtn" class="commonButton" value="No" style="float:right;"/>
    <input type="button" id="yesNoDlgYesBtn" class="commonButton" value="Yes" style="float:right;"/>
    <div style="clear:both;"></div>
</div>

<div id="saveDlg" style="display: none;">
    <div>
        <p id="saveDlgMessage"></p>
    </div>
    <input type="button" id="saveDlgSaveBtn" class="commonButton" value="Save" style="float:right;"/>
    <input type="button" id="saveDlgDiscardBtn" class="commonButton" value="Discard" style="float:right;"/>
    <input type="button" id="saveDlgCancelBtn" class="commonButton" value="Cancel" style="float:right;"/>
    <div style="clear:both;"></div>
</div>

<div id="infoDlg" style="display: none;">
    <div>
        <p><label id="infoDlgMessage"></label></p>
    </div>
    <input type="button" id="infoDlgOkBtn" class="commonButton" value="OK" style="float: right;" />
</div>

