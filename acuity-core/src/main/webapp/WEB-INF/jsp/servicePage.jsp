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

<html>
<head>
    <title>Creating instances</title>
    <%@ include file="/WEB-INF/jsp/includes.jsp" %>
    <%@ include file="/WEB-INF/jsp/wizard-common.jsp" %>
    <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet"/>
    <link type="text/css" href="css/drug-project.css" rel="stylesheet"/>
    <script>
        function checkDxpStatus() {
            ajaxModule.sendAjaxRequestWithoutParam("check-create-dxp-status", null, function (response) {
                $('#create-all-dxps #status-info').html('');
                for (var i = 0; i < response.items.length; i++) {
                    var itemEl = document.createElement('div');
                    itemName = document.createElement('span');
                    $(itemName).text("Item: " + response.items[i].a);
                    itemStatus = document.createElement('span');
                    $(itemStatus).text(response.items[i].b == null ? "In progress" : (response.items[i].b ? "Success" : "Failed"));
                    $(itemStatus).css("color", response.items[i].b == null ? "rgb(126, 122, 29)" : (response.items[i].b ? "rgb(27, 124, 23)" : "rgb(201, 32, 32)"));
                    $(itemStatus).css("margin-left", "10px");

                    itemEl.appendChild(itemName);
                    itemEl.appendChild(itemStatus);
                    $('#create-all-dxps #status-info').append(itemEl);

                    if (!response.inProgress) {

                    }
                }
                ;
                $('#create-all-dxps #start-link').toggle(!response.inProgress);
                $('#create-all-dxps #in-progress').toggle(!!response.inProgress);
                $('#create-all-dxps #loading').toggle(!!response.inProgress);
                if (response.inProgress) {
                    setTimeout(checkDxpStatus, 3000);
                }
                ;
            });
        };

        function checkFolderStatus() {
            ajaxModule.sendAjaxRequestWithoutParam("check-create-folders-status", null, function (response) {
                $('#create-all-folders #status-info').html('');
                for (var i = 0; i < response.items.length; i++) {
                    var itemEl = document.createElement('div');
                    itemName = document.createElement('span');
                    $(itemName).text("Item: " + response.items[i].a);
                    itemStatus = document.createElement('span');
                    $(itemStatus).text(response.items[i].b == null ? "In progress" : (response.items[i].b ? "Success" : "Failed"));
                    $(itemStatus).css("color", response.items[i].b == null ? "rgb(126, 122, 29)" : (response.items[i].b ? "rgb(27, 124, 23)" : "rgb(201, 32, 32)"));
                    $(itemStatus).css("margin-left", "10px");

                    itemEl.appendChild(itemName);
                    itemEl.appendChild(itemStatus);
                    $('#create-all-folders #status-info').append(itemEl);

                    if (!response.inProgress) {

                    }
                }
                ;
                $('#create-all-folders #start-link').toggle(!response.inProgress);
                $('#create-all-folders #in-progress').toggle(!!response.inProgress);
                $('#create-all-folders #loading').toggle(!!response.inProgress);
                if (response.inProgress) {
                    setTimeout(checkFolderStatus, 3000);
                }
                ;
            });
        };
        $(checkDxpStatus);
        $(checkFolderStatus);
    </script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<div id='create-all-dxps'>
        <span style='display: none;' id='start-link'>Instance creation is not running.<br>Start instance creation for:
            <ul>
                <li><a href='create-all-dxps'>All modules</a></li>
                <li><a href='create-all-dxps?modules=tolerability'>Tolerability</a></li>
                <li><a href='create-all-dxps?modules=oncology'>Oncology</a></li>
                <li><a href='create-all-dxps?modules=aesummaries'>AESummaries</a></li>
                <li><a href='create-all-dxps?modules=exposure'>Exposure</a></li>
                <li><a href='create-all-dxps?modules=respiratory'>Respiratory</a></li>
            </ul>
            <br>
        </span>
    <span style='display: none;' id='in-progress'>Instance creation is in progress<br></span>
    <div id='status-info'></div>
    <div style='display: none;' id='loading'><img src="css/images/ajax-loader.gif"/></div>
</div>
<div id='create-all-folders'>
        <span style='display: none;' id='start-link'>Folders creation is not running.<br>
            <a href='create-all-folders'>Start folders creation</a>
            <br>
        </span>
    <span style='display: none;' id='in-progress'>Folders creation is in progress<br></span>
    <div id='status-info'></div>
    <div style='display: none;' id='loading'><img src="css/images/ajax-loader.gif"/></div>
</div>

</body>
</html>
