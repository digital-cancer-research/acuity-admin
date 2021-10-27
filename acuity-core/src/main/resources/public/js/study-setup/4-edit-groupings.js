var GroupingsStudyStep = function (studyWizard) {
    var addBtnId = "studyGroupingsAdd";
    var groupingsDlgOkBtnId = "studyGroupingsOkBtn";
    var groupingsBrowseBtnId = "studyGroupBrowseBtn";
    var groupingsDlgCancelBtnId = "studyGroupingsCancelBtn";
    var studySubjectAddRowBtnId = "studySubjectAddRowBtn";
    var deleteGroupValuesBtnId = "studyDeleteGroupValues";
    var studyGroupingsDlg = "studyAddGroupingDlg";

    this.lastSubjectRow = 0;
    this.lastSubjectCol = 0;
    this.groupValues = [];
    this.editGroupName = "";
    this.studyGroupingsTableId = 'studyGroupingsTable';
    this.studySubjectTableId = 'studySubjectTable';
    this.refreshBtnId = "studyGroupingsRefresh";
    this.deleteBtnId = "studyGroupingsDelete";
    this.editBtnId = "studyGroupingsSettings";
    this.studySubjectCancelBtnId = "studySubjectCancelBtn";
    this.studySubjectSaveBtnBtnId = "studySubjectSaveBtn";
    this.studyWizard = studyWizard;

    var scope = this;

    var init = function () {

        createStudyGroupingsTable();
        createGroupSubjectTable();

        $("#" + scope.studyGroupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
        $(window).bind('resize', function () {
            $("#" + scope.studyGroupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
        });
        // main table actions
        $('#' + addBtnId).on("click", function () {
            showGroupingDialog('add-group');
        });

        $('#' + scope.editBtnId).on("click", function () {
            showGroupingDialog('edit-group');
        });

        $('#' + scope.deleteBtnId).on("click", function () {
            deleteGroup();
        });

        $('#' + scope.refreshBtnId).on("click", function () {
            refreshGroup();
        });

        // slaveTable actions
        $('#' + studySubjectAddRowBtnId).on("click", function () {
            var rowData = {};
            var newId = $.jgrid.randId();
            $("#" + scope.studySubjectTableId).jqGrid('addRowData', newId, rowData);
            scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
        });

        $('#' + scope.studySubjectSaveBtnBtnId).on("click", function () {
            var slaveTable = $("#" + scope.studySubjectTableId);
            var recs = slaveTable.jqGrid('getGridParam', 'reccount');
            if (recs == 0)
                return false;
            wizardCommonModule.showYesNoDialog("Save changes", "Save changes?", function () {
                scope.saveGroupValues(false);
            });
        });

        $("#" + deleteGroupValuesBtnId).on("click", function () {
            deleteGroupValues();
        });

        $("#" + scope.studySubjectCancelBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Discard subject changes?", function () {
                var mainTable = $('#' + scope.studyGroupingsTableId);
                var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
                mainTable.jqGrid('setSelection', selRowId);
                toggleSlaveChangesControls(false);
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
            });
        });

        addHelpText();
    };

    /** Main groupings table def**/
    var createStudyGroupingsTable = function () {
        $("#" + scope.studyGroupingsTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['', 'Grouping Name <a href="#colGroupingName" class="help">?</a>',
                    'Data source <a href="#colDataSource" class="help">?</a>',
                    'ACUITY enabled? <a href="#colAcuityEnabled" class="help">?</a>',
                    'Last edited by  <a href="#colLastEdited" class="help">?</a>',
                    'Date <a href="#colDate" class="help">?</a>', '', ''],
                colModel: [
                    {name: 'id', index: 'id', width: 20, hidden: true, align: "center"},
                    {name: 'name', index: 'name', width: 200, align: "center"},
                    {name: 'dataSource', index: 'dataSource', width: 150, align: "center", formatter: sourceFormatter},
                    {name: 'ready', index: 'ready', formatter: 'checkbox', width: 150, align: "center"},
                    {name: 'lastEditedBy', index: 'lastEditedBy', width: 150, align: "center"},
                    {name: 'date', index: 'date', width: 150, align: "center"},
                    {name: 'headerRow', index: 'headerRow', hidden: true},
                    {name: 'defaultValue', index: 'headerRow', hidden: true}
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
                resizeStop: function (width, index) {
                    scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
                },
                onSelectRow: function (id) {
                    selectGroupingsRowHandler(id);
                }
            }
        );
    };

    var sourceFormatter = function (cellvalue, options, rowObject) {
        return cellvalue == null ? "manual" : cellvalue;
    };

    var selectGroupingsRowHandler = function (rowId) {
        $("#studySubjectTableWrapper").show();

        var refreshBtnElement = $("#" + scope.refreshBtnId);
        var groupId = $("#" + scope.studyGroupingsTableId).jqGrid('getCell', rowId, "id");
        var dataSource = $("#" + scope.studyGroupingsTableId).jqGrid('getCell', rowId, "dataSource");
        if (dataSource != "-" && dataSource != 'manual') {
            refreshBtnElement.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            refreshBtnElement.attr('disabled', 'disabled').addClass('disabled');
        }

        toggleMainTableControls(true);

        ajaxModule.sendAjaxRequestSimpleParams("study-setup-get-group-values", {groupId: groupId}, {showDialog: true}, function (result) {
            if (!result) {
                result = [];
            }
            result.push({id: $.jgrid.randId()});


            $("#" + scope.studySubjectTableId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
            scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
        });
    };

    var toggleMainTableControls = function (enabled) {
        var btns = $("#" + scope.deleteBtnId + ", #" + scope.editBtnId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var deleteGroup = function () {
        var mainTable = $("#" + scope.studyGroupingsTableId);
        var recs = mainTable.jqGrid('getGridParam', 'reccount');
        if (recs == 0) {
            return false;
        }
        var gridId = mainTable.jqGrid('getGridParam', 'selrow');
        var groupName = mainTable.jqGrid('getCell', gridId, "name");
        var dlgMessage = "Delete grouping " + groupName + "?";
        wizardCommonModule.showYesNoDialog("Delete", dlgMessage, function () {
            var mainTable = $('#' + scope.studyGroupingsTableId);
            var gridId = mainTable.jqGrid('getGridParam', 'selrow');
            var dbId = mainTable.jqGrid('getCell', gridId, "id");
            ajaxModule.sendAjaxRequestSimpleParams("study-delete-group", {'groupId': dbId}, {showDialog: true}, function (result) {
                $('#' + scope.studyGroupingsTableId).jqGrid('delRowData', gridId);
                $("#" + scope.studySubjectTableId).jqGrid('clearGridData');
                $("#studySubjectTableWrapper").hide();
                var delIndex = -1;
                for (var i = 0; i < scope.studyWizard.workflow.groupings.length; i++) {
                    var group = scope.studyWizard.workflow.groupings[i];
                    if (group.id == result.id) {
                        delIndex = i;
                    }
                }
                scope.studyWizard.workflow.groupings.splice(delIndex, 1);
                toggleMainTableControls(false);
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
            });
        });
    };

    var refreshGroup = function () {
        var mainTable = $('#' + scope.studyGroupingsTableId);
        var gridId = mainTable.jqGrid('getGridParam', 'selrow');
        var dbId = mainTable.jqGrid('getCell', gridId, "id");
        ajaxModule.sendAjaxRequestSimpleParams("study-refresh-group", {'groupId': dbId}, {showDialog: true}, function (result) {
            $("#" + scope.studySubjectTableId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result.values}).trigger('reloadGrid');
            toggleSlaveChangesControls(true);
        });
    };
    /** Main groupings table end**/


    /** Slave groupings table def**/
    var createGroupSubjectTable = function () {
        $("#" + scope.studySubjectTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['', 'Subject ID <a href="#colSubjectID" class="help">?</a>',
                    'Group name <a href="#colGroupName" class="help">?</a>'],
                colModel: [
                    {name: 'id', index: 'id', hidden: true, width: 200, align: "center"},
                    {name: 'subjectId', index: 'subjectId', editable: true, width: 200, align: "center"},
                    {name: 'name', index: 'name', width: 150, editable: true, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                cellEdit: true,
                pager: "#studySubjectPager",
                rowNum: 10,
                multiselect: true,
                cellsubmit: 'clientArray',
                afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                    var tableElement = $(this);
                    var ids = tableElement.jqGrid('getDataIDs');
                    var data = tableElement.jqGrid('getRowData', ids[ids.length - 1]);
                    if (data.subjectId != "" || data.name != "")
                        $('#' + studySubjectAddRowBtnId).click();
                },
                afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                    scope.lastSubjectRow = iRow;
                    scope.lastSubjectCol = iCol;
                    toggleSlaveChangesControls(true);
                },
                //gridComplete: function() {
                //  var recs = $(this).jqGrid('getGridParam','reccount');
                //   if (recs == 0) {
                //       $("#studySubjectTableWrapper").hide();
                //   }else {
                //       $("#studySubjectTableWrapper").show();
                //        var studyDeleteGroupValuesBtnElement = $("#" + deleteGroupValuesBtnId);
                //        studyDeleteGroupValuesBtnElement.attr('disabled', 'disabled').addClass('disabled');
                //   }
                //},
                resizeStop: function (width, index) {
                    scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
                },
                onSelectRow: valueTableSelectionChanged,
                onSelectAll: valueTableSelectionChanged
            });
    };

    var toggleSlaveChangesControls = function (enabled) {
        var btns = $("#" + scope.studySubjectCancelBtnId + ", #" + scope.studySubjectSaveBtnBtnId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var valueTableSelectionChanged = function () {
        var selection = $(this).jqGrid('getGridParam', 'selarrrow');
        var deleteBtn = $(this).parents(".contentTable").find("input[value='Delete']");
        if (selection.length > 0) {
            deleteBtn.removeAttr('disabled').removeClass('disabled');
        } else {
            deleteBtn.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var getActualGroupSubjectValues = function (id, mainTable, slaveTable, deletionPrepare) {
        var sendData = {};
        sendData.groupId = mainTable.jqGrid('getCell', id, "id");
        sendData.values = [];

        var data = slaveTable.jqGrid('getGridParam', 'data');

        if (deletionPrepare) {
            forDeletion = slaveTable.jqGrid('getGridParam', 'selarrrow');
            _.remove(data, function (row) {
                return _.includes(forDeletion, row.id.toString());
            });
        }

        for (var i = 0; i < data.length; i++) {
            var row = data[i];
            if (row.subjectId && row.name) {
                sendData.values.push({subjectId: row.subjectId, name: row.name});
            }
        }
        return sendData;
    };

    var deleteGroupValues = function () {
        wizardCommonModule.showYesNoDialog("Delete", "Delete rows?", function () {
            var slaveTable = $("#" + scope.studySubjectTableId);
            var mainTable = $("#" + scope.studyGroupingsTableId);
            var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
            var sendData = getActualGroupSubjectValues(selRowId, mainTable, slaveTable, true);

            if (_.uniq(sendData.values, function (row) {
                    return row.subjectId + "#" + row.name;
                }).length === _.uniq(sendData.values, 'subjectId').length) {
                var rows = slaveTable.jqGrid('getGridParam', 'selarrrow');
                while (rows.length > 0) {
                    slaveTable.jqGrid('delRowData', rows.pop());
                }

                ajaxModule.sendAjaxRequest("study-setup-save-group-values", JSON.stringify(sendData), {showDialog: true}, function (result) {
                    wizardCommonModule.showInfoDialog("Save changes", "Save successful");
                    toggleSlaveChangesControls(false);
                });
                $("#" + deleteGroupValuesBtnId).attr('disabled', 'disabled').addClass('disabled');
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
            } else {
                wizardCommonModule.showWarningDialog("One or more subjects occurs more than once in the groups provided; each subject should " +
                    "only appear once and all groups must be disjoint. Please review group data before resubmitting this information.");
            }
        });
    };

    this.saveGroupValues = function (toAdmin) {
        var mainTable = $("#" + scope.studyGroupingsTableId);
        var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
        var slaveTable = $("#" + scope.studySubjectTableId);
        slaveTable.jqGrid('saveCell', scope.lastSubjectRow, scope.lastSubjectCol);
        var sendData = getActualGroupSubjectValues(selRowId, mainTable, slaveTable, false);
        if (sendData.values.length === _.uniq(sendData.values, 'subjectId').length) {
            ajaxModule.sendAjaxRequest("study-setup-save-group-values", JSON.stringify(sendData), {showDialog: true}, function (result) {
                toggleSlaveChangesControls(false);
                wizardCommonModule.showInfoDialog("Save changes", "Save successful");
                if (toAdmin) {
                    window.location = "admin";
                }
            });
        } else {
            wizardCommonModule.showWarningDialog("One or more subjects occurs more than once in the groups provided; each subject should " +
                "only appear once and all groups must be disjoint. Please review group data before resubmitting this information.");
        }
    };
    /** Slave groupings table end**/


    /** Add/Edit groupings Dialog**/
    var showGroupingDialog = function (mode) {
        $("#" + studyGroupingsDlg).data('mode', mode).dialog({
            title: 'Define data source information',
            modal: true,
            resizable: false,
            width: 500,
            //height: 430,
            open: function () {
                addGroupingDialogFill();
            },
            create: function () {
                addGroupingDialogInit();
            }
        });
    };

    var addGroupingDialogFill = function () {
        var groupBrowseBtn = $('#' + groupingsBrowseBtnId);
        $("#studyGroupingNameWarn").hide();

        resetGroupingDialog();

        if ($("#source-manually").is(":checked")) {
            $("input[name='studyHeaderRow']").attr('disabled', 'disabled');
            groupBrowseBtn.attr('disabled', 'disabled');
            groupBrowseBtn.addClass('disabled');
        }
        var mode = $("#" + studyGroupingsDlg).data('mode');
        var groupNameDlgElement = $("#studyGroupingName");
        if (mode == 'edit-group') {
            fillEditGroupingDialog();
        } else {
            groupNameDlgElement.val("Custom grouping " + getGroupNumber());
        }
    };

    var resetGroupingDialog = function () {
        $("#studyGroupingName").val('');
        $("#studyDrugName").val('Default group');
        $("#source-manually").attr('checked', true);
        $("#studyGropingSourceFile").val('');
        $("#gropingStorage").val('');
        $("#source-headers").attr('checked', true);
        $("#acuityEnabled").attr('checked', true);
        $("#studyGroupingId").val('');
    };

    var fillEditGroupingDialog = function () {
        var mainTable = $("#" + scope.studyGroupingsTableId);
        var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
        var data = mainTable.getRowData(selRowId);

        $("#studyGroupingId").val(data.id);
        $("#studyGroupingName").val(data.name);
        scope.editGroupName = data.name;
        $("#studyDrugName").val(data.defaultValue);
        if (data.dataSource != "manual") {
            $("#storage-source-manually").click();
            $("#gropingStorage").val(data.dataSource);
        } else {
            $("#source-manually").click();
        }

        $("input[name='studyHeaderRow'][value=" + data.headerRow + "]").attr('checked', 'checked');
        if (data.ready == "Yes") {
            $("#acuityEnabled").attr('checked', 'true');
        }
        $("#studyGroupingId").val(data.id);
    };

    var editGrouping = function () {
        ajaxModule.uploadData($("#study-add-group-form"), {showDialog: true}, function (result) {
            if ($.trim(result.message).length > 0) {
                if (result.message == "BadFile") {
                    wizardCommonModule.showWarningDialog("The ACUITY system cannot parse this file. Please check the file format.");
                } else if (result.message == "AccessDenied") {
                    wizardCommonModule.showWarningDialog("The ACUITY system does not have access to this file location. " +
                        "As an alternative, you may wish to download the file to a local folder and upload it directly into ACUITY.");
                } else if (result.message == "MultiGroup") {
                    wizardCommonModule.showWarningDialog("One or more subjects occurs more than once in the groups provided; each subject should " +
                        "only appear once and all groups must be disjoint. Please review group data before resubmitting this information.");
                }
                return;
            }
            var data = result.group;
            var groupTable = $("#" + scope.studyGroupingsTableId);
            var rowid = $("#" + scope.studyGroupingsTableId).jqGrid('getGridParam', 'selrow');
            groupTable.jqGrid('setRowData', rowid, data);
            groupTable.setSelection(rowid, true);
            $("#" + scope.refreshBtnId).attr('disabled', 'disabled').addClass('disabled');
            $("#" + scope.studySubjectTableId).jqGrid('clearGridData');
            $("#" + studyGroupingsDlg).dialog("close");
        }, 'study-setup-edit-group?_csrf=' + ajaxModule.csrf.token);
    };

    var addGrouping = function () {
        ajaxModule.uploadData($("#study-add-group-form"), {showDialog: true}, function (result) {
            if ($.trim(result.message).length > 0) {
                if (result.message == "BadFile") {
                    wizardCommonModule.showWarningDialog("The ACUITY system cannot parse this file. Please check the file format.");
                } else if (result.message == "AccessDenied") {
                    wizardCommonModule.showWarningDialog("The ACUITY system does not have access to this file location. As an alternative, you may wish to download the file to a local folder and upload it directly into ACUITY.");
                } else if (result.message == "MultiGroup") {
                    wizardCommonModule.showWarningDialog("One or more subjects occurs more than once in the groups provided; each subject should " +
                        "only appear once and all groups must be disjoint. Please review group data before resubmitting this information.");
                }
                return;
            }
            var data = result.group;
            var groupTable = $("#" + scope.studyGroupingsTableId);
            var rowid = $.jgrid.randId();
            groupTable.jqGrid('addRowData', rowid, data);
            groupTable.setSelection(rowid, true);
            $("#" + scope.refreshBtnId).attr('disabled', 'disabled').addClass('disabled');
            $("#" + scope.studySubjectTableId).jqGrid('clearGridData');
            $("#" + studyGroupingsDlg).dialog("close");
            scope.studyWizard.workflow.groupings.push(data);
            scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
        }, 'study-setup-save-group?_csrf=' + ajaxModule.csrf.token);
    };

    var addGroupingDialogInit = function () {
        var groupBrowseBtn = $('#' + groupingsBrowseBtnId);
        groupBrowseBtn.on("click", function () {
            if ($(this).hasClass("disabled")) {
                return false;
            }
        });
        $('input[type="file"]').on('change', function (e) {
            var path = $(this).val();
            path = path.replace('C:\\fakepath\\', '');
            $("#studyGropingSourceFile").val(path);
        });
        $('#' + groupingsDlgCancelBtnId).on("click", function () {
            $("#" + studyGroupingsDlg).dialog("close");
        });
        $("input[name='studyDataSource']").on('change', function () {
            if ($("#source-manually").is(":checked")) {
                $("input[name='studyHeaderRow']").attr('disabled', 'disabled');
                groupBrowseBtn.attr('disabled', 'disabled').addClass('disabled');
            } else {
                $("input[name='studyHeaderRow']").removeAttr('disabled', 'disabled');
                groupBrowseBtn.removeAttr('disabled', 'disabled').removeClass('disabled');
            }
        });

        var groupNameDlgElement = $("#studyGroupingName");
        groupNameDlgElement.focus(function () {
            $("#studyGroupingNameWarn").hide();
        });
        groupNameDlgElement.blur(function () {
            var groupNameText = groupNameDlgElement.val();
            if ($.trim(groupNameText).length == 0) {
                groupNameDlgElement.val("Custom grouping " + getGroupNumber());
            }
        });

        $('#' + groupingsDlgOkBtnId).on("click", function () {
            var mode = $("#" + studyGroupingsDlg).data('mode');
            if (!validateDataSource(mode)) {
                return;
            }
            if (mode == 'edit-group') {
                editGrouping();
            } else if (mode == 'add-group') {
                addGrouping();
            }
        });
    };

    var getGroupNumber = function () {
        var mainTable = $("#" + scope.studyGroupingsTableId);
        var ids = mainTable.jqGrid('getDataIDs');
        var groupDefaultNumber = 1;
        for (var i = 0; i < ids.length; i++) {
            var rowId = ids[i];
            var groupName = mainTable.jqGrid('getCell', rowId, "name");
            if (!(/^Custom/).test(groupName)) {
                continue;
            }
            groupDefaultNumber++;
        }
        return groupDefaultNumber;
    };

    var validateDataSource = function (mode) {
        if ($.trim($('#studyGroupingName').val()).length == 0) {
            wizardCommonModule.showWarningDialog("Grouping name is empty");
            return false;
        }
        var radioBtn = $("#storage-source-manually");
        var inputField = $("#gropingStorage");
        if (radioBtn.is(":checked")) {
            if ($.trim(inputField.val()).length == 0) {
                wizardCommonModule.showWarningDialog("Source remote path is empty");
                return false;
            }
            inputField.val(inputField.val());
        }
        radioBtn = $("#source-file");
        inputField = $("#studyGropingSourceFile");
        if (radioBtn.is(":checked")) {
            if ($.trim(inputField.val()).length == 0) {
                wizardCommonModule.showWarningDialog("Source file path is empty");
                return false;
            }
            var regex = /[^\\]*\.(csv)$/i;
            isValid = regex.test(inputField.val());
        }
        var groupNameAlreadyExist = function (name, mode) {

            if (_.includes(scope.subjectGroupingNamesFromResultTable, name)) {
                return true;
            }

            var mainTable = $("#" + scope.studyGroupingsTableId);
            var ids = mainTable.jqGrid('getDataIDs');
            for (var i = 0; i < ids.length; i++) {
                var rowId = ids[i];
                var groupName = mainTable.jqGrid('getCell', rowId, "name");
                if ((groupName === name && mode != 'edit-group') ||
                    (mode === 'edit-group' && groupName === name && name != scope.editGroupName)) {
                    return true;
                }
            }
            return false;
        };

        var groupNameDlgElement = $("#studyGroupingName");
        var groupNameText = groupNameDlgElement.val();
        if (groupNameAlreadyExist(groupNameText, mode)) {
            $("#studyGroupingNameWarn").show();
            return false;
        }
        return true;
    };
    /** Add/Edit groupings Dialog end**/

    var addHelpText = function () {
        $('a[href=#colGroupingName]').attr('title', $('#colGroupingName').val());
        $('a[href=#colDataSource]').attr('title', $('#colDataSource').val());
        $('a[href=#colAcuityEnabled]').attr('title', $('#colAcuityEnabled').val());
        $('a[href=#colLastEdited]').attr('title', $('#colLastEdited').val());
        $('a[href=#colDate]').attr('title', $('#colDate').val());
        $('a[href=#colSubjectID]').attr('title', $('#colSubjectID').val());
        $('a[href=#colGroupName]').attr('title', $('#colGroupName').val());
        $(".help").tipTip();
    };

    init();

};


/* public methods ------*/
GroupingsStudyStep.prototype = {
    startStep: function () {
        var scope = this;

        ajaxModule.sendAjaxRequestWithoutParam('study-setup-get-subject-grouping-names-from-result-table', null, function (result) {
            scope.subjectGroupingNamesFromResultTable = result;
        });

        var mainTable = $("#" + scope.studyGroupingsTableId);
        var slaveTable = $("#" + scope.studySubjectTableId);
        slaveTable.jqGrid('resetSelection');
        slaveTable.jqGrid('clearGridData');
        var refreshBtnElement = $("#" + scope.refreshBtnId);
        var deleteBtnElement = $("#" + scope.deleteBtnId);
        var settingsBtnElement = $("#" + scope.editBtnId);
        var studySubjectCancelBtnElement = $("#" + scope.studySubjectCancelBtnId);
        var studySubjectSaveBtnElement = $("#" + scope.studySubjectSaveBtnBtnId);
        studySubjectCancelBtnElement.attr('disabled', 'disabled').addClass('disabled');
        refreshBtnElement.attr('disabled', 'disabled').addClass('disabled');
        deleteBtnElement.attr('disabled', 'disabled').addClass('disabled');
        settingsBtnElement.attr('disabled', 'disabled').addClass('disabled');
        studySubjectSaveBtnElement.attr('disabled', 'disabled').addClass('disabled');

        if (scope.studyWizard.workflow.groupings.length > 0) {
            mainTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: scope.studyWizard.workflow.groupings}).trigger('reloadGrid');
        } else {
            mainTable.jqGrid('clearGridData');
        }
        scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_GROUPING_STEP_INX);
        mainTable.setGridWidth($("#rightPane").width() - 50, true);
    },

    leaveStep: function () {
        $("#" + this.studySubjectTableId).jqGrid('clearGridData');
        return true;
    },

    canGoToAdmin: function () {
        var scope = this;
        var slaveTable = $("#" + scope.studySubjectTableId);
        var isSlaveEditable = wizardCommonModule.isSlaveTableEditable(slaveTable);
        if (isSlaveEditable) {
            wizardCommonModule.showSaveDialog("Save changes", "Save changes?",
                function () {
                    scope.saveGroupValues(true);
                },
                function () {
                    window.location = "admin";
                });
            return false;
        }
        return true;
    }
};
