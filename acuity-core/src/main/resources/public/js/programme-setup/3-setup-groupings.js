var GroupingsProgrammeStep = function (programmeWizard) {

    var addNewGroupBtnId = 'groupingsAdd';
    var groupingsSettingsBtnId = 'groupingsSettings';
    var groupingsDeleteBtnId = 'groupingsDelete';
    var groupingsRefreshBtnId = 'groupingsRefresh';
    var groupingsDlgOkBtnId = 'groupingsOkBtn';
    var groupingsDlgCancelBtnId = 'groupingsCancelBtn';
    var groupingsBrowseBtnId = 'group-browse-btn';
    var groupLabTestAddRowBtnId = 'groupManuallyAddRowBtn';
    var groupAdverseEventSaveBtnId = 'groupTableSaveBtn';
    var groupLabTestSaveBtnBtnId = 'groupManuallySaveBtn';
    var adverseEventDeleteGroupValueBtnId = 'groupTableDeleteBtn';
    var labDeleteGroupValueBtnId = 'groupManuallyDeleteBtn';
    var cancelAeTableBtnId = 'aeTableCancelBtn';
    var cancelLabTableBtnId = 'labTableCancelBtn';

    var scope = this;

    this.groupingsTableId = 'groupingsTable';
    this.groupAdverseEventTableId = 'groupAdverseEventTable';
    this.groupLabTestTableId = 'groupLabTestTable';
    this.saveAdvValuesBtnId = "groupTableSaveBtn";
    this.deleteAdvValuesBtnId = "groupTableDeleteBtn";
    this.cancelAdvBtnElementId = "aeTableCancelBtn";
    this.saveLabValuesBtnId = "groupManuallySaveBtn";
    this.deleteLabValuesBtnId = "groupManuallyDeleteBtn";
    this.cancelLabBtnElementId = "labTableCancelBtn";
    this.addAdverseEvnBtnId = 'groupTableAddRowBtn';

    this.lastDetailRow = null;
    this.lastDetailCol = null;

    this.GROUPING_COUNTER = 0;
    this.editGroupName = "";
    this.programmeWizard = programmeWizard;

    /**
     private methods  */

    var init = function () {
        // create all step tables
        createGroupingsTable();
        createGroupAdverseEventTable();
        createGroupLabTestTable();

        $("#" + scope.groupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
        $(window).bind('resize', function () {
            $("#" + scope.groupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
        });

        addMainGroupingTableActions();
        addAdverseEventTableActions();
        addLabTestTableActions();

        addHelpText();
    };

    var addMainGroupingTableActions = function () {

        $('#' + addNewGroupBtnId).on("click", function () {
            showAddGroupingDialog('add-group');
        });

        $('#' + groupingsSettingsBtnId).on("click", function () {
            showAddGroupingDialog('edit-settings');
        });

        $('#' + groupingsRefreshBtnId).on("click", function () {
            var mainTable = $('#' + scope.groupingsTableId);
            var gridId = mainTable.jqGrid('getGridParam', 'selrow');
            var dbId = mainTable.jqGrid('getCell', gridId, "id");
            var groupType = mainTable.jqGrid('getCell', gridId, "type");
            ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-refresh-group", {
                'groupId': dbId,
                'groupType': groupType
            }, {showDialog: true}, function (result) {
                if (result.type == "ae") {
                    fillDataValues(result.values, $("#" + scope.groupAdverseEventTableId));
                } else if (result.type == "lab") {
                    fillDataValues(result.values, $("#" + scope.groupLabTestTableId));
                }
            });
        });

        $('#' + groupingsDeleteBtnId).on("click", function () {
            var mainTable = $('#' + scope.groupingsTableId);
            var gridId = mainTable.jqGrid('getGridParam', 'selrow');
            var groupName = mainTable.jqGrid('getCell', gridId, "name");
            var dlgMessage = "Delete grouping " + groupName + "?";
            wizardCommonModule.showYesNoDialog("ACUITY", dlgMessage, function () {
                var mainTable = $('#' + scope.groupingsTableId);
                var gridId = mainTable.jqGrid('getGridParam', 'selrow');
                var dbId = mainTable.jqGrid('getCell', gridId, "id");
                var groupType = mainTable.jqGrid('getCell', gridId, "type");
                ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-delete-group", {
                    'groupId': dbId,
                    'groupType': groupType
                }, {showDialog: true}, function (result) {
                    $("#groupAdverseEventTableWrapper").hide();
                    $("#groupLabTestTableWrapper").hide();

                    mainTable.jqGrid('delRowData', gridId);
                    var delIndex = -1;
                    for (var i = 0; i < programmeWizard.workflow.groupings.length; i++) {
                        var group = programmeWizard.workflow.groupings[i];
                        if (group.id == result.id) {
                            delIndex = i;
                        }
                    }
                    programmeWizard.workflow.groupings.splice(delIndex, 1);
                    scope.GROUPING_COUNTER = scope.GROUPING_COUNTER - 1;
                    programmeWizard.changeStepText(programmeWizard.GROUP_STEP_INX, "(" + scope.GROUPING_COUNTER + ")");
                    $("#" + scope.groupLabTestTableId).jqGrid('clearGridData');
                    $("#" + scope.groupAdverseEventTableId).jqGrid('clearGridData');
                    toggleMainTableControls(false);
                    programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
                });

            });
        });
    };

    this.forceSaveEditingCell = function ($table) {
        if (scope.lastDetailRow !== null && scope.lastDetailCol !== null) {
            $table.jqGrid('saveCell', scope.lastDetailRow, scope.lastDetailCol);
        }
        scope.lastDetailRow = null;
        scope.lastDetailCol = null;
    };

    this.assertUniqueItems = function () {
        var mainTable = $('#groupingsTable');
        var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
        var groupType = mainTable.jqGrid('getCell', selectedGroupId, 'type');

        var items, message, slaveTable;
        if (groupType == 'ae') {
            slaveTable = $('#groupAdverseEventTable');
            this.forceSaveEditingCell(slaveTable);

            items = _.pluck(slaveTable.jqGrid('getGridParam', 'data'), 'pt');

            message = 'One or more preferred terms occurs more than once; each preferred term can only appear once and all groups must be disjoint. ' +
                'Please review group data before resubmitting this information.';
        } else if (groupType == 'lab') {
            slaveTable = $('#groupLabTestTable');
            this.forceSaveEditingCell(slaveTable);

            items = _.pluck(slaveTable.jqGrid('getGridParam', 'data'), 'labCode');

            message = 'One or more lab measurement types occurs more than once; each lab measurement type can only appear once and all groups must be disjoint. ' +
                'Please review group data before resubmitting this information.';
        }

        var difference = _(items).countBy().pairs().reject({1: 1}).pluck(0).value();
        if (difference.length) {
            message += '<br/>List of duplicates: ' + difference.join(', ');
            wizardCommonModule.showWarningDialog(message, 750);
            return false;
        }
        return true;
    };

    var addAdverseEventTableActions = function () {

        $('#' + scope.addAdverseEvnBtnId).on("click", function () {
            var rowData = {};
            rowData.groupName = "";
            rowData.pt = "";
            var advEventTable = $("#" + scope.groupAdverseEventTableId);
            var newId = $.jgrid.randId();
            advEventTable.jqGrid('addRowData', newId, rowData);
            programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);

        });

        $('#' + groupAdverseEventSaveBtnId).on("click", function () {
            /* if (!scope.assertUniqueItems()) {
             return;
             }*/
            var slaveTable = $("#" + scope.groupAdverseEventTableId);
            if (slaveTable.getGridParam("reccount") > 0) {
                slaveTable.jqGrid('saveCell', scope.lastDetailRow, scope.lastDetailCol);
            }
            var recs = slaveTable.jqGrid('getGridParam', 'reccount');
            if (recs == 0)
                return false;
            wizardCommonModule.showYesNoDialog("ACUITY", "Save changes?", function () {
                var mainTable = $("#" + scope.groupingsTableId);
                var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
                scope.saveGroupValues(selectedGroupId, mainTable, slaveTable, false);
                toggleChangesControls(false);
            });
        });

        $('#' + adverseEventDeleteGroupValueBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("ACUITY", "Delete rows?", function () {
                var slaveTable = $("#" + scope.groupAdverseEventTableId);
                var rows = slaveTable.jqGrid('getGridParam', 'selarrrow');

                while (rows.length > 0) {
                    slaveTable.jqGrid('delRowData', rows.pop());
                }
                var mainTable = $("#" + scope.groupingsTableId);
                var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
                scope.deleteGroupValues(selectedGroupId, mainTable, slaveTable);
                $("#" + adverseEventDeleteGroupValueBtnId).attr('disabled', 'disabled').addClass('disabled');
                toggleChangesControls(false);
                programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
            });
        });

        $("#" + cancelAeTableBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("ACUITY", "Discard changes?", function () {
                var mainTable = $("#" + scope.groupingsTableId);
                var id = mainTable.jqGrid('getGridParam', 'selrow');
                mainTable.jqGrid('setSelection', id);
                toggleChangesControls(false);
                programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
            });
        });

    };

    var addLabTestTableActions = function () {

        $('#' + groupLabTestAddRowBtnId).on("click", function () {
            var rowData = {};
            rowData.labGrouping = "";
            rowData.labcode = "";
            rowData.description = "";
            var table = $("#" + scope.groupLabTestTableId);
            var newId = $.jgrid.randId();
            table.jqGrid('addRowData', newId, rowData);
            programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);

        });

        $('#' + groupLabTestSaveBtnBtnId).on("click", function () {
            if (!scope.assertUniqueItems()) {
                return;
            }
            var slaveTable = $("#" + scope.groupLabTestTableId);
            var recs = slaveTable.jqGrid('getGridParam', 'reccount');
            if (recs == 0)
                return false;
            wizardCommonModule.showYesNoDialog("ACUITY", "Save changes?", function () {
                var mainTable = $("#" + scope.groupingsTableId);
                var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
                scope.saveGroupValues(selectedGroupId, mainTable, slaveTable, false);
                toggleLabChangesControls(false);
            });
        });

        $('#' + labDeleteGroupValueBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("ACUITY", "Delete rows?", function () {
                var slaveTable = $("#" + scope.groupLabTestTableId);
                var rows = slaveTable.jqGrid('getGridParam', 'selarrrow');
                while (rows.length > 0) {
                    slaveTable.jqGrid('delRowData', rows.pop());
                }
                var mainTable = $("#" + scope.groupingsTableId);
                var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
                scope.deleteGroupValues(selectedGroupId, mainTable, slaveTable);
                $("#" + labDeleteGroupValueBtnId).attr('disabled', 'disabled').addClass('disabled');
                toggleLabChangesControls(false);
                programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
            });
        });

        $("#" + cancelLabTableBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("ACUITY", "Discard changes?", function () {
                var id = $('#' + scope.groupingsTableId).jqGrid('getGridParam', 'selrow');
                $('#' + scope.groupingsTableId).jqGrid('setSelection', id);
                toggleLabChangesControls(false);
                programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
            });
        });

    };

    var toggleChangesControls = function (enabled) {
        var btns = $("#" + scope.cancelAdvBtnElementId + ", #" + scope.saveAdvValuesBtnId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var toggleLabChangesControls = function (enabled) {
        var btns = $("#" + scope.saveLabValuesBtnId + ", #" + scope.cancelLabBtnElementId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var toggleMainTableControls = function (enabled) {
        var btns = $("#" + groupingsDeleteBtnId + ", #" + groupingsSettingsBtnId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var showAddGroupingDialog = function (mode) {
        $("#addGroupingDlg").data('mode', mode).dialog({
            title: 'Define data source information',
            modal: true,
            resizable: false,
            width: 500,
//    		height : 500,
            open: function () {
                var mode = $("#addGroupingDlg").data('mode');
                addGroupingDialogFill(mode);
            },
            create: function () {
                addGroupingDialogInit();
            }
        });
    };

    var addDataSourceInfo = function () {
        var groupingType = $("#groupType").val();
        if (groupingType == "ae") {
            $("#source-info").text("_The input file (*.csv) should have two columns in the following order: " +
                "adverse event group name, MedDRA preferred term");
        } else if (groupingType == "lab") {
            $("#source-info").text("_The input file (*.csv) should have two columns in the following order: " +
                "lab group name, lab measurement name");
        }
    };

    var clearAddGroupingDialog = function () {
        $("#add-group-form").trigger('reset');
        $("#groupType").removeAttr('disabled');
        $("#groupType").val('ae');
        $("input[name='ready']").attr('checked', true);
        addDataSourceInfo();
    };

    var fillEditGroupingDialog = function () {
        var mainTable = $('#' + scope.groupingsTableId);
        var gridId = mainTable.jqGrid('getGridParam', 'selrow');
        var dbId = mainTable.jqGrid('getCell', gridId, "id");
        var groupType = mainTable.jqGrid('getCell', gridId, "type");
        ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-get-group", {
            'groupId': dbId,
            'groupType': groupType
        }, {showDialog: false}, function (result) {
            $("#groupType").val(result.type);
            $("#groupType").attr('disabled', 'disabled');
            addDataSourceInfo();
            var groupNameDlgElement = $("#groupingName");
            groupNameDlgElement.val(result.name);
            groupNameDlgElement.css('color', 'black');
            scope.editGroupName = result.name;
            $("#defaultValue").val(result.defaultValue);
            $("input[name='headerRow'][value=" + result.headerRow + "]").attr('checked', 'checked');
            if (result.dataSource != null) {
                $("input[name='dataSource'][value='storage-source-manually']").attr('checked', 'checked');
                $("#gropingStorage").removeAttr('disabled');
                $("#gropingStorage").val(result.dataSource);
            } else {
                $("input[name='dataSource'][value='source-manually']").attr('checked', 'checked');
                $("#gropingSourceFile").removeAttr('disabled');
            }
            $("input[name='ready']").attr('checked', result.ready);
        });
    };

    var addSourceUIAction = function () {
        if ($("#source-manually").is(":checked")) {
            $("input[name='headerRow']").attr('disabled', 'disabled');
            $("input[name='headerRow'][value='false']").attr('checked', 'checked');
            $("#group-browse-btn").attr('disabled', 'disabled');
            $("#group-browse-btn").addClass('disabled');
            $("#gropingStorage").attr('disabled', 'disabled');
            $("#gropingSourceFile").attr('disabled', 'disabled');
        } else if ($("#source-file").is(":checked")) {
            $("#gropingStorage").attr('disabled', 'disabled');
            $("#gropingSourceFile").removeAttr('disabled');
        } else if ($("#storage-source-manually").is(":checked")) {
            $("#gropingSourceFile").attr('disabled', 'disabled');
            $("#gropingStorage").removeAttr('disabled');

        }
    };

    var addGroupingDialogFill = function (mode) {
        if (mode == 'add-group') {
            clearAddGroupingDialog();
        } else if (mode == 'edit-settings') {
            fillEditGroupingDialog();
        }
        var groupNameDlgElement = $("#groupingName");
        var groupNameText = groupNameDlgElement.val();
        if ($.trim(groupNameText).length == 0) {
            var currentGroupType = $("#groupType").val();
            if (currentGroupType === "ae") {
                groupNameDlgElement.val("Custom AE grouping " + getGroupNumber("ae"));
            } else if (currentGroupType === "lab") {
                groupNameDlgElement.val("Custom lab grouping " + +getGroupNumber("lab"));
            }
        }
        $("#groupingNameWarn").hide();
        addSourceUIAction();
    };

    var fillDataValues = function (values, table) {
        table.jqGrid('clearGridData').jqGrid('setGridParam', {data: values}).trigger('reloadGrid');
    };

    var validateDataSource = function (mode) {
        var radioBtn = $("#storage-source-manually");
        var inputField = $("#gropingStorage");
        if (radioBtn.is(":checked")) {
            if ($.trim(inputField.val()).length == 0) {
                wizardCommonModule.showWarningDialog("Source remote path is empty");
                return false;
            }
        }
        radioBtn = $("#source-file");
        inputField = $("#gropingSourceFile");
        if (radioBtn.is(":checked")) {
            if ($.trim(inputField.val()).length == 0) {
                wizardCommonModule.showWarningDialog("Source file path is empty");
                return false;
            }

            var regex = /[^\\]*\.(csv)$/i;

            isValid = regex.test(inputField.val());
        }

        var groupNameAlreadyExist = function (name, mode) {
            var mainTable = $("#" + scope.groupingsTableId);
            var ids = mainTable.jqGrid('getDataIDs');
            for (var i = 0; i < ids.length; i++) {
                var rowId = ids[i];
                var groupName = mainTable.jqGrid('getCell', rowId, "name");
                if ((groupName === name && mode != 'edit-settings') ||
                    (mode === 'edit-settings' && groupName === name && name != scope.editGroupName)) {
                    return true;
                }
            }
            return false;
        };

        var groupNameDlgElement = $("#groupingName");
        var groupNameText = groupNameDlgElement.val();
        if (groupNameAlreadyExist(groupNameText, mode)) {
            $("#groupingNameWarn").show();
            return false;
        }

        return true;
    };

    var addGrouping = function () {
        ajaxModule.uploadData($("#add-group-form"), {showDialog: true}, function (result) {
            if ($.trim(result.message).length > 0) {
                if (result.message == "BadFile") {
                    wizardCommonModule.showWarningDialog("The ACUITY system cannot parse this file. Please check the file format.");
                } else if (result.message == "AccessDenied") {
                    wizardCommonModule.showWarningDialog("The ACUITY system does not have access to this file location. As an alternative, you may wish to download the file to a local folder and upload it directly into ACUITY.");
                } else if (result.message == "MultiGroup") {
                    wizardCommonModule.showWarningDialog("One or more value occurs more than once in the groups provided; each value should " +
                        "only appear once and all groups must be disjoint. Please review group data before resubmitting this information.");
                }
                return;
            }
            var data = result.group;
            var rowData = {};
            rowData.id = data.id;
            rowData.name = data.name;
            rowData.type = data.type;
            rowData.dataSource = data.dataSource == null ? "manual" : data.dataSource;
            rowData.ready = data.ready;
            rowData.date = "";
            var groupTable = $("#" + scope.groupingsTableId);
            var rowid = groupTable.jqGrid('getDataIDs').length + 1;
            groupTable.jqGrid('addRowData', rowid, rowData);

            if (data.type == "ae") {
                $("#" + scope.groupAdverseEventTableId).jqGrid('clearGridData');
            } else {
                $("#" + scope.groupLabTestTableId).jqGrid('clearGridData');

            }

            if (data.dataSource != null) {
                $("#groupingsRefresh").removeAttr('disabled', 'disabled');
                $("#groupingsRefresh").removeClass('disabled');
            } else {
                $("#groupingsRefresh").attr('disabled', 'disabled');
                $("#groupingsRefresh").addClass('disabled');

            }
            $("#addGroupingDlg").dialog("close");
            groupTable.setSelection(rowid, true);
            scope.GROUPING_COUNTER = scope.GROUPING_COUNTER + 1;
            programmeWizard.changeStepText(programmeWizard.GROUP_STEP_INX, "(" + scope.GROUPING_COUNTER + ")");
            if (!programmeWizard.workflow.groupings) {
                programmeWizard.workflow.groupings = [];
            }
            programmeWizard.workflow.groupings.push(data);
            programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
        }, 'programme-setup/programme-save-group?_csrf=' + ajaxModule.csrf.token);
    };

    var editGrouping = function () {
        ajaxModule.uploadData($("#add-group-form"), {showDialog: true}, function (result) {
            if ($.trim(result.message).length > 0) {
                if (result.message == "BadFile") {
                    wizardCommonModule.showWarningDialog("The ACUITY system cannot parse this file. Please check the file format.");
                } else if (result.message == "AccessDenied") {
                    wizardCommonModule.showWarningDialog("The ACUITY system does not have access to this file location. As an alternative, you may wish to download the file to a local folder and upload it directly into ACUITY.");
                }
                return;
            }
            var data = result.group;
            var rowData = {};
            rowData.id = data.id;
            rowData.name = data.name;
            rowData.type = data.type;
            rowData.dataSource = data.dataSource == null ? "manual" : data.dataSource;
            rowData.ready = data.ready;
            rowData.date = "";
            var groupTable = $("#" + scope.groupingsTableId);
            var rowid = groupTable.jqGrid('getGridParam', 'selrow');
            groupTable.jqGrid('setRowData', rowid, rowData);
            $("#addGroupingDlg").dialog("close");
            groupTable.setSelection(rowid, true);

        }, 'programme-setup/programme-edit-group?_csrf=' + ajaxModule.csrf.token);
    };

    var getGroupNumber = function (type) {
        var mainTable = $("#" + scope.groupingsTableId);
        var ids = mainTable.jqGrid('getDataIDs');
        var groupDefaultNumber = 1;
        for (var i = 0; i < ids.length; i++) {
            var rowId = ids[i];
            var groupType = mainTable.jqGrid('getCell', rowId, "type");
            if (groupType != type) {
                continue;
            }
            var groupName = mainTable.jqGrid('getCell', rowId, "name");
            if (!(/^Custom/).test(groupName)) {
                continue;
            }
            groupDefaultNumber++;
        }
        return groupDefaultNumber;
    };

    var addGroupingDialogInit = function () {
        $('#' + groupingsDlgOkBtnId).on("click", function () {
            var mode = $("#addGroupingDlg").data('mode');

            if (!validateDataSource(mode)) {
                return;
            }

            if (mode == 'add-group') {
                addGrouping();
            } else if (mode == 'edit-settings') {
                editGrouping();
            }
        });

        $('#' + groupingsBrowseBtnId).on("click", function () {
            if ($(this).hasClass("disabled")) {
                return false;
            }
        });

        $('input[type="file"]').on('change', function (e) {
            var path = $(this).val();
            $("#gropingSourceFile").val(path + "");
        });


        $('#' + groupingsDlgCancelBtnId).on("click", function () {
            $("#addGroupingDlg").dialog("close");
        });

        var groupNameDlgElement = $("#groupingName");
        groupNameDlgElement.focus(function () {
            $("#groupingNameWarn").hide();
        });

        groupNameDlgElement.blur(function () {
            var groupNameText = groupNameDlgElement.val();
            if ($.trim(groupNameText).length == 0) {
                var currentGroupType = $("#groupType").val();
                if (currentGroupType === "ae") {
                    groupNameDlgElement.val("Custom AE grouping " + getGroupNumber("ae"));
                } else if (currentGroupType === "lab") {
                    groupNameDlgElement.val("Custom lab grouping " + +getGroupNumber("lab"));
                }
            }
        });

        $("#groupType").on('change', function () {
            addDataSourceInfo();
            var currentGroupType = $(this).val();
            var groupNameText = groupNameDlgElement.val();
            if (currentGroupType === "ae" || (/^Custom LAB/).test(groupNameText)) {
                groupNameDlgElement.val("Custom AE grouping " + getGroupNumber("ae"));
            } else if (currentGroupType === "lab" || (/^Custom AE/).test(groupNameText)) {
                groupNameDlgElement.val("Custom lab grouping " + +getGroupNumber("lab"));
            }
        });

        $("input[name='dataSource']").on('change', function () {
            if ($("#source-manually").is(":checked")) {
                $("input[name='headerRow']").attr('disabled', 'disabled');
                $("input[name='headerRow'][value='false']").attr('checked', 'checked');
                $("#group-browse-btn").attr('disabled', 'disabled');
                $("#group-browse-btn").addClass('disabled');
                $("#gropingStorage").attr('disabled', 'disabled');
                $("#gropingSourceFile").attr('disabled', 'disabled');
            } else if ($("#source-file").is(":checked")) {
                $("#gropingStorage").attr('disabled', 'disabled');
                $("#gropingSourceFile").removeAttr('disabled');
                $("input[name='headerRow']").removeAttr('disabled', 'disabled');
                $("#group-browse-btn").removeAttr('disabled', 'disabled');
                $("#group-browse-btn").removeClass('disabled');
            } else if ($("#storage-source-manually").is(":checked")) {
                $("#gropingSourceFile").attr('disabled', 'disabled');
                $("#gropingStorage").removeAttr('disabled');
                $("input[name='headerRow']").removeAttr('disabled', 'disabled');
                $("#group-browse-btn").removeAttr('disabled', 'disabled');
                $("#group-browse-btn").removeClass('disabled');
            }
        });
    };

    this.getActualGroupValues = function (id, mainTable, slaveTable) {
        var out = {};
        out.id = mainTable.jqGrid('getCell', id, "id");
        out.groupType = mainTable.jqGrid('getCell', id, "type");
        out.values = [];

        var data = slaveTable.jqGrid('getGridParam', 'data');

        for (var i = 0; i < data.length; i++) {
            var row = data[i];
            if (out.groupType == 'ae') {
                if (row.name && row.pt) {
                    out.values.push({id: row.id, name: row.name, pt: row.pt});
                }
            } else if (out.groupType == 'lab') {
                if (row.name && row.labCode) {
                    out.values.push({id: row.id, name: row.name, labCode: row.labCode, description: row.description});
                }
            }
        }
        return out;
    };

    this.deleteGroupValues = function (id, mainTable, slaveTable) {
        var sendData = scope.getActualGroupValues(id, mainTable, slaveTable);
        ajaxModule.sendAjaxRequest("programme-setup/programme-delete-group-values", JSON.stringify(sendData), {showDialog: true}, function (result) {
            wizardCommonModule.showInfoDialog("ACUITY", "Delete successful");
        });
    };

    var valueTableSelectionChanged = function () {
        var selection = $(this).jqGrid('getGridParam', 'selarrrow');
        var deleteBtn = $(this).parents(".contentTable").find("input[value='Delete']");
        if (selection.length > 0)
            deleteBtn.removeAttr('disabled').removeClass('disabled');
        else {
            deleteBtn.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    var getGroupValues = function (tableWrapperShow, tableWrapperHide, tableToShow, result) {
        tableWrapperHide.hide();
        tableToShow.jqGrid('clearGridData');
        tableWrapperShow.show();
        if (!result) {
            result = [];
        }
        result.push({id: 'jqtmpid_'});
        fillDataValues(result, tableToShow);
        valueTableSelectionChanged.call(tableToShow);
        programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
    };

    var checkTable = function ($grid, termUrl, colName) {
        var values = $grid.getCol(colName, true);

        var valuesToCheck = [];
        for (var i = 0; i < values.length; i++) {
            var value = values[i].value;
            if (value && value.indexOf('<input type="text"') < 0) {
                valuesToCheck.push(value);
            }
        }

        $.getJSON(termUrl, {term: valuesToCheck.join(',')}, function (data) {
            for (var i = 0; i < values.length; i++) {
                var rowid = values[i].id;
                var count = 0;
                for (var j = 0; j < data.length; j++) {
                    if (values[i].value == data[j].term) {
                        count = data[j].count;
                        break;
                    }
                }
                if (count > 0 || !values[i].value) {
                    $grid.setCell(rowid, 'msg', 0);
                } else {
                    $grid.setCell(rowid, 'msg', 1);
                }
            }
            toggleWarnColumn($grid);
        });
    };

    var toggleWarnColumn = function ($grid) {
        var hasWarn = false;
        var warnings = $grid.getCol('msg');
        for (var i = 0; i < warnings.length; i++) {
            if (warnings[i]) {
                hasWarn = true;
                break;
            }
        }

        if (hasWarn) {
            $grid.showCol('msg');
        } else {
            $grid.hideCol('msg');
        }
    };

    var checkRow = function ($grid, termUrl, colName, rowid) {
        var value = $grid.getLocalRow(rowid)[colName];

        if (!value) {
            $grid.setCell(rowid, 'msg', 0);
            toggleWarnColumn($grid);
            return;
        }

        $.getJSON(termUrl, {term: value}, function (data) {
            if (data[0].count > 0) {
                $grid.setCell(rowid, 'msg', 0);
            } else {
                $grid.setCell(rowid, 'msg', 1);
            }
            toggleWarnColumn($grid);
        })
    };

    var selectGroupingsRowHandler = function (id) {
        var mainTable = $("#" + scope.groupingsTableId);
        var groupType = mainTable.jqGrid('getCell', id, "type");
        var groupId = mainTable.jqGrid('getCell', id, "id");
        var dataSource = mainTable.jqGrid('getCell', id, "dataSource");
        var refreshBtnElement = $("#groupingsRefresh");
        if (dataSource != "-" && dataSource != 'manual') {
            refreshBtnElement.removeAttr('disabled', 'disabled');
            refreshBtnElement.removeClass('disabled');
        } else {
            refreshBtnElement.attr('disabled', 'disabled');
            refreshBtnElement.addClass('disabled');
        }
        toggleMainTableControls(true);

        if (groupType == "lab") { // lab test
            $("#groupAdverseEventTableWrapper").show();
            ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-get-group-values",
                {groupId: groupId, groupType: groupType}, {showDialog: false}, function (result) {
                    getGroupValues($("#groupLabTestTableWrapper"), $("#groupAdverseEventTableWrapper"),
                        $("#" + scope.groupLabTestTableId), result);
                });
        } else if (groupType == "ae") {  //adverse event
            $("#groupLabTestTableWrapper").show();
            ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-get-group-values", {
                groupId: groupId,
                groupType: groupType
            }, {showDialog: false}, function (result) {
                getGroupValues($("#groupAdverseEventTableWrapper"), $("#groupLabTestTableWrapper"),
                    $("#" + scope.groupAdverseEventTableId), result);
            });
        }
    };

    var sourceFormatter = function (cellvalue, options, rowObject) {
        return cellvalue == null ? "manual" : wizardCommonModule.htmlEscape(cellvalue);
    };

    var createGroupingsTable = function () {
        $("#" + scope.groupingsTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['', 'Grouping Name <a href="#colGroupingName" class="help">?</a>',
                    'Type of group <a href="#colTypeOfGroup" class="help">?</a>',
                    'Data source <a href="#colDataSource" class="help">?</a>',
                    'ACUITY enabled? <a href="#colAcuityEnabled" class="help">?</a>',
                    'Last edited by <a href="#colLastEdited" class="help">?</a>',
                    'Date <a href="#colDate" class="help">?</a>', ''],
                colModel: [
                    {name: 'id', index: 'id', width: 20, hidden: true, align: "center"},
                    {name: 'name', index: 'name', width: 200, align: "center"},
                    {name: 'type', index: 'type', width: 150, align: "center"},
                    {name: 'dataSource', index: 'dataSource', width: 150, align: "center", formatter: sourceFormatter},
                    {name: 'ready', index: 'ready', formatter: 'checkbox', width: 150, align: "center"},
                    {name: 'lastEditedBy', index: 'lastEditedBy', width: 150, align: "center"},
                    {
                        name: 'date',
                        index: 'date',
                        width: 150,
                        align: "center",
                        formatter: wizardCommonModule.formatDate
                    },
                    {name: 'dataSourceType', index: 'dataSourceType', hidden: true, width: 150, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
                gridview: true,
                onSelectRow: function (id) {
                    selectGroupingsRowHandler(id);
                },
                resizeStop: function (width, index) {
                    programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
                },
            });
    };

    var createGroupAdverseEventTable = function () {
        $("#" + scope.groupAdverseEventTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['', 'AE Grouping Name <a href="#colAeGroupName" class="help">?</a>',
                    'MedDRA PT <a href="#colMedDra" class="help">?</a>', ''],
                colModel: [
                    {name: 'id', index: 'id', hidden: true, width: 200, align: "center"},
                    {name: 'name', index: 'name', editable: true, width: 200, align: "center"},
                    {
                        name: 'pt', index: 'pt', width: 150, editable: true, align: "center",
                        editoptions: {
                            dataInit: function (elem) {
                                $(elem).autocomplete({minLength: 2, source: 'api/dict/pt/search'});
                            }
                        }
                    },
                    {
                        name: 'msg', hidden: true, width: 24, align: "center",
                        formatter: function (cellvalue) {
                            if (cellvalue == 1) {
                                return '<div class="warning-icon" title="Validity warning:\n' +
                                    'This adverse event preferred term has not previously been observed by ACUITY, ' +
                                    'it may have been entered incorrectly."></div>';
                            }
                            return '';
                        }
                    }
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                cellEdit: true,
                pager: "#groupAdvEventPager",
                rowNum: 9999,
                cellsubmit: 'clientArray',
                multiselect: true,
                afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                    toggleChangesControls(true);
                    if (cellname == 'pt') {
                        checkRow($(this), 'api/dict/pt/count', 'pt', rowid);
                    }
                    var ids = $("#" + scope.groupAdverseEventTableId).jqGrid('getDataIDs');
                    var data = $("#" + scope.groupAdverseEventTableId).jqGrid('getRowData', ids[ids.length - 1]);
                    if (data.name != "" || data.pt != "") {
                        $('#' + scope.addAdverseEvnBtnId).click();
                    }
                    $('#' + scope.groupAdverseEventTableId + ' td.dirty-cell').removeClass("dirty-cell edit-cell");
                    $('#' + scope.groupAdverseEventTableId + '  tr.edited').removeClass("edited");
                },
                afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                    scope.lastDetailRow = iRow;
                    scope.lastDetailCol = iCol;
                },
                gridComplete: function () {
                    //var recs = $("#" + scope.groupAdverseEventTableId).jqGrid('getGridParam','reccount');
                    //if (recs == 0) {
                    //    $("#groupAdverseEventTableWrapper").hide();
                    //}else {
                    //    $("#groupAdverseEventTableWrapper").show();
                    checkTable($(this), 'api/dict/pt/count', 'pt');
                    //}
                },
                resizeStop: function (width, index) {
                    programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
                },
                onSelectRow: valueTableSelectionChanged,
                onSelectAll: valueTableSelectionChanged
            });
    };

    var createGroupLabTestTable = function () {
        $("#" + scope.groupLabTestTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['', 'Lab grouping name  <a href="#colLabGroupingName" class="help">?</a>',
                    'Labcode  <a href="#colLabcodeValue" class="help">?</a>',
                    'Description  <a href="#colLabDescription" class="help">?</a>', ''],
                colModel: [
                    {name: 'id', index: 'id', hidden: true, width: 200, align: "center"},
                    {name: 'name', index: 'name', editable: true, width: 200, align: "center"},
                    {
                        name: 'labCode', index: 'labCode', width: 150, editable: true, align: "center",
                        editoptions: {
                            dataInit: function (elem) {
                                $(elem).autocomplete({minLength: 2, source: 'api/dict/lab_code/search'});
                            }
                        }
                    },
                    {name: 'description', index: 'description', editable: true, width: 150, align: "center"},
                    {
                        name: 'msg', hidden: true, width: 24, align: "center",
                        formatter: function (cellvalue) {
                            if (cellvalue == 1) {
                                return '<div class="warning-icon" title="Validity warning:\n' +
                                    'This laboratory measurement term is not known to ACUITY, ' +
                                    'it may have been entered incorrectly."></div>';
                            }
                            return '';
                        }
                    }
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                pager: "#groupLabTestPager",
                rowNum: 9999,
                multiselect: true,
                cellEdit: true,
                cellsubmit: 'clientArray',
                afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                    toggleLabChangesControls(true);
                    if (cellname == 'labCode') {
                        checkRow($(this), 'api/dict/lab_code/count', 'labCode', rowid);
                    }
                    var ids = $("#" + scope.groupLabTestTableId).jqGrid('getDataIDs');
                    var data = $("#" + scope.groupLabTestTableId).jqGrid('getRowData', ids[ids.length - 1]);
                    if (data.name != "" || data.labCode != "" || data.description != "") {
                        $('#' + groupLabTestAddRowBtnId).click();
                    }

                    $('#' + scope.groupLabTestTableId + ' td.dirty-cell').removeClass("dirty-cell edit-cell");
                    $('#' + scope.groupLabTestTableId + '  tr.edited').removeClass("edited");

                },
                afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                    scope.lastDetailRow = iRow;
                    scope.lastDetailCol = iCol;
                },
                gridComplete: function () {
                    //var recs = $("#" + scope.groupLabTestTableId).jqGrid('getGridParam','reccount');
                    //if (recs == 0) {
                    //    $("#groupLabTestTableWrapper").hide();
                    //}else {
                    //    $("#groupLabTestTableWrapper").show();
                    checkTable($(this), 'api/dict/lab_code/count', 'labCode');
                    //}
                },
                resizeStop: function (width, index) {
                    programmeWizard.recalculateSplit(programmeWizard.GROUP_STEP_INX);
                },

                onSelectRow: valueTableSelectionChanged,
                onSelectAll: valueTableSelectionChanged
            });
    };

    var addHelpText = function () {
        $('a[href=#colGroupingName]').attr('title', $('#colGroupingName').val());
        $('a[href=#colTypeOfGroup]').attr('title', $('#colTypeOfGroup').val());
        $('a[href=#colDataSource]').attr('title', $('#colDataSource').val());
        $('a[href=#colAcuityEnabled]').attr('title', $('#colAcuityEnabled').val());
        $('a[href=#colLastEdited]').attr('title', $('#colLastEdited').val());
        $('a[href=#colDate]').attr('title', $('#colDate').val());
        $('a[href=#colAeGroupName]').attr('title', $('#colAeGroupName').val());
        $('a[href=#colMedDra]').attr('title', $('#colMedDra').val());
        $('a[href=#colLabGroupingName]').attr('title', $('#colLabGroupingName').val());
        $('a[href=#colLabcodeValue]').attr('title', $('#colLabcodeValue').val());
        $('a[href=#colLabDescription]').attr('title', $('#colLabDescription').val());
        $(".help").tipTip();
    };

    init();
};

GroupingsProgrammeStep.prototype = {
    onShowStep: function () {
        var scope = this;
        $("#" + scope.groupAdverseEventTableId).jqGrid('clearGridData');
        $("#" + scope.groupLabTestTableId).jqGrid('clearGridData');
        $("#" + scope.groupingsTableId).jqGrid('resetSelection');
        var refreshBtnElement = $("#groupingsRefresh");
        var deleteBtnElement = $("#groupingsDelete");
        var settingsBtnElement = $("#groupingsSettings");

        refreshBtnElement.attr('disabled', 'disabled');
        refreshBtnElement.addClass('disabled');
        deleteBtnElement.attr('disabled', 'disabled');
        deleteBtnElement.addClass('disabled');
        settingsBtnElement.attr('disabled', 'disabled');
        settingsBtnElement.addClass('disabled');

        $("#" + scope.saveAdvValuesBtnId).attr('disabled', 'disabled').addClass('disabled');
        $("#" + scope.deleteAdvValuesBtnId).attr('disabled', 'disabled').addClass('disabled');
        $("#" + scope.cancelAdvBtnElementId).attr('disabled', 'disabled').addClass('disabled');

        $("#" + scope.saveLabValuesBtnId).attr('disabled', 'disabled').addClass('disabled');
        $("#" + scope.deleteLabValuesBtnId).attr('disabled', 'disabled').addClass('disabled');
        $("#" + scope.cancelLabBtnElementId).attr('disabled', 'disabled').addClass('disabled');
        scope.programmeWizard.recalculateSplit(scope.programmeWizard.GROUP_STEP_INX);
        $("#" + scope.groupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
    },

    endStep: function () {
        var scope = this;
        $("#" + scope.groupAdverseEventTableId).jqGrid('clearGridData');
        $("#" + scope.groupLabTestTableId).jqGrid('clearGridData');
    },

    canGoToAdmin: function () {
        var scope = this;
        var slaveTable = $("#" + scope.groupAdverseEventTableId);
        var isSlaveEditable = wizardCommonModule.isSlaveTableEditable(slaveTable);
        if (isSlaveEditable) {
            if (!scope.assertUniqueItems()) {
                return;
            }
            wizardCommonModule.showSaveDialog("ACUITY", "Save changes?",
                function () {
                    var mainTable = $("#" + scope.groupingsTableId);
                    var selectedGroupId = mainTable.jqGrid('getGridParam', 'selrow');
                    scope.saveGroupValues(selectedGroupId, mainTable, slaveTable, true);
                },
                function () {
                    window.location = "admin";
                });
            return false;
        }
        return true;
    },

    fillGroupingsTable: function (result) {
        var scope = this;
        var groupTable = $("#" + scope.groupingsTableId);
        groupTable.jqGrid('clearGridData').trigger('reloadGrid');
        for (var i = 0; i < result.length; i++) {
            groupTable.jqGrid('addRowData', i + 1, result[i]);
        }
        scope.GROUPING_COUNTER = scope.GROUPING_COUNTER + result.length;
    },

    clearGroupingsTable: function () {
        var scope = this;
        $("#" + scope.groupingsTableId).jqGrid('clearGridData').trigger('reloadGrid');
        scope.GROUPING_COUNTER = 0;
    },

    saveGroupValues: function (id, mainTable, slaveTable, toAdmin) {
        var scope = this;
        var sendData = scope.getActualGroupValues(id, mainTable, slaveTable);
        ajaxModule.sendAjaxRequest("programme-setup/programme-save-group-values", JSON.stringify(sendData), {showDialog: true}, function (result) {
            $('#' + slaveTable.id + ' td.edit-cell').removeClass("edit-cell");
            wizardCommonModule.showInfoDialog("Save changes", "Save successful");
            if (toAdmin) {
                window.location = "admin";
            }
        });
    }
};

