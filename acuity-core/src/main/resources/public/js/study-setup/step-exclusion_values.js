var ExclusionValuesStep = function (studyWizard) {
    var $exclusionValuesAddButton = $('#exclusionValuesAddButton');
    var $exclusionValuesDeleteButton = $('#exclusionValuesDeleteButton');
    var $exclusionValuesSaveButton = $('#exclusionValuesSaveButton');

    var $exclusionValuesTable = $('#exclusionValuesTable');

    var entities;
    var fieldRulesArray = {};
    var entityRulesArray = {};
    var fieldIdToEntityId = {};
    var entityIdToField = {};

    var originTableData;
    var isDataDirty = false;
    var isDataSelected = false;

    var locked = true;

    var lastRow, lastCol, lastRowId;

    var loadTable = function () {
        ajaxModule.sendAjaxRequestWithoutParam('study-setup/get-excluding-values', {showDialog: true}, function (result) {
            originTableData = result;
            for (var i = 0; i < originTableData.length; i++) {
                originTableData[i].entityRuleId = fieldIdToEntityId[originTableData[i].fieldRuleId];

                if (originTableData[i].id == null) {
                    originTableData[i].id = $.jgrid.randId();
                }
            }
            $exclusionValuesTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: originTableData}).trigger('reloadGrid');
            isDataDirty = false;
            alignButtons();
        });
    };

    var updateEditOptionsAndLoadTable = function () {
        $exclusionValuesTable.setColProp('entityRuleId', {
                    editoptions : {
                        value : entityRulesArray
                    }
                });
        $exclusionValuesTable.setColProp('fieldRuleId', {
                    editoptions : {
                        value : fieldRulesArray
                    }
                });
        loadTable();
    }

    var addHelpText = function() {
    	$('a[href=#colExclMapping]').attr('title', $('#colExclMapping').val());
    	$('a[href=#colExclDataField]').attr('title', $('#colExclDataField').val());
    	$('a[href=#colExclValue]').attr('title', $('#colExclValue').val());
    	$(".help").tipTip();
    };

    var saveTable = function (callback) {
        $exclusionValuesTable.jqGrid('saveCell', lastRow, lastCol);

        var data = $exclusionValuesTable.jqGrid('getGridParam', 'data');
        data = _(data)
            .map(function (item) {
                return {fieldRuleId: item.fieldRuleId, value: $.trim(item.value)};
            })
            .filter(function (item) {
                return item.fieldRuleId && item.fieldRuleId != '0' && item.value;
            })
            .uniq(function (item) {
                return item.fieldRuleId + '|' + item.value;
            }).value();

        ajaxModule.sendAjaxRequestSimpleParams('/study-setup/save-excluding-values', JSON.stringify(data),
            {showDialog: true}, function () {
                updateEditOptionsAndLoadTable();
                if (callback) {
                    callback();
                }
            }, true);
    };

    var loadEntities = function () {
        ajaxModule.sendAjaxRequestWithoutParam('/api/data/get_entities', {showDialog: true}, function (result) {
            entities = result;
            entityRulesArray[0] = 'Please select...';

            for (var i = 0; i < entities.length; i++) {
                var entityRule = entities[i];
                entityRulesArray[entityRule.id] = entityRule.name;
                var fieldArray = {};
                fieldArray[0] = 'Please select...';
                for (var j = 0; j < entityRule.fieldRules.length; j++) {
                    var fieldRule = entityRule.fieldRules[j];
                    fieldRulesArray[fieldRule.id] = fieldRule.name;
                    fieldIdToEntityId[fieldRule.id] = entityRule.id;
                    fieldArray[fieldRule.id] = fieldRule.name;
                }
                entityIdToField[entityRule.id] = fieldArray;
            }
            updateEditOptionsAndLoadTable();
        });
    }

    this.startStep = function () {
        $("#exc-values-std-name").text(studyWizard.workflow.selectedStudy.studyCode + ' Exclusion values')
        locked = true;
        $('#exc-values-block-inputs').removeClass("open");
        $(".enabled").attr('disabled', 'disabled');
        $('#exc-values-block-inputs').off("click");
        $('#exc-values-block-inputs').on("click", function(){
            if($(this).hasClass("open")){
            //disable edit
                $exclusionValuesTable.jqGrid('saveCell', lastRow, lastCol);
                locked = true;
                $(this).removeClass("open");
                $(".enabled").attr('disabled', 'disabled');
                $exclusionValuesTable.jqGrid({
                    cellEdit: true
                });
                saveTable(function() {});
                isDataDirty = false;
                isDataSelected = false;
           }else{
            //enable edit
                locked = false;
                $(this).addClass("open");
                $(".enabled").removeAttr('disabled', 'disabled');
                $exclusionValuesTable.jqGrid({
                    cellEdit: false
                });
             }
            alignButtons();
        });
        alignButtons();
        loadEntities();
    };

    var cleanup = function () {
        $exclusionValuesTable.jqGrid('clearGridData');

        var entities = null;
        var fieldRulesArray = {};
        var entityRulesArray = {};
        var fieldIdToEntityId = {};
        var entityIdToField = {};

        isDataDirty = false;
        isDataSelected = false;
    };

    this.leaveStep = function (toStep) {
        if (isDataDirty) {
            wizardCommonModule.showSaveDialog('Are you sure?', 'You have unsaved exclusion values.<br/>' +
                'Do you wish to save or discard these changes?',
                function (toStep) {
                    saveTable(function() {
                        cleanup();
                        studyWizard.goToStep(toStep);
                    });
                }, function (toStep) {
                    cleanup();
                    studyWizard.goToStep(toStep);
                }, function (toStep) {
                    return false;
                }, toStep);
            return false;
        } else {
            cleanup();
            return true;
        }
    };

    this.canGoToAdmin = function () {
        if (isDataDirty) {
            wizardCommonModule.showSaveDialog('Are you sure?', 'You have unsaved exclusion values.<br/>' +
                'Do you wish to save or discard these changes?',
                function () {
                    saveTable();
                    return true;
                }, function () {
                    return true;
                }, function () {
                    return false;
                });
        } else {
            //cleanup();
            return true;
        }
    };

    var alignButtons = function () {
        if (isDataDirty && !locked) {
            $exclusionValuesSaveButton.removeAttr('disabled');
        } else {
            $exclusionValuesSaveButton.attr('disabled', 1);
        }

        if (isDataSelected && !locked) {
            $exclusionValuesDeleteButton.removeAttr('disabled');
        } else {
            $exclusionValuesDeleteButton.attr('disabled', 1);
        }
    };

    //Buttons actions
    $exclusionValuesAddButton.click(function () {
        var id = $.jgrid.randId();
        $exclusionValuesTable.jqGrid('addRowData', id, {entityRuleId: 0, fieldRuleId: 0});
        isDataDirty = true;
        alignButtons();
    });

    $exclusionValuesDeleteButton.click(function () {
        wizardCommonModule.showYesNoDialog('Delete', 'Delete selected rows?', function () {
            var rows = $exclusionValuesTable.jqGrid('getGridParam', 'selarrrow');
            var dirty = false;
            while (!_.isEmpty(rows) && $exclusionValuesTable.jqGrid('delRowData', rows.pop())) {
                dirty = true;
            }
            isDataDirty = dirty;
            isDataSelected = false;
            alignButtons();
        })
    });

    $exclusionValuesSaveButton.click(function () {
        wizardCommonModule.showYesNoDialog('Save changes',
                'This will replace the current exclusion values in the database for this study.<br/>' +
                'Continue?', function () {
                saveTable();
            });
    });

    var onTableSelectionChanged = function () {
        var rows = $exclusionValuesTable.jqGrid('getGridParam', 'selarrrow');
        isDataSelected = rows.length > 0;
        alignButtons();
    };

    //Table
    $exclusionValuesTable.jqGrid({
            datatype: 'local',
            autoencode: true,
            width: '100%',
            height: '100%',
            cellEdit: true,
            multiselect: true,
            rowNum: 9999,
            cellsubmit: 'clientArray',
            colNames: ['',
                'Mapping <a href="#colExclMapping" class="help">?</a>',
                'Data field  <a href="#colExclDataField" class="help">?</a>',
                'Value  <a href="#colExclValue" class="help">?</a>'],
            colModel: [
                {name: 'id', index: 'id', width: 20, hidden: true},
                {name: 'entityRuleId', index: 'entityRuleId', width: 250, edittype: 'select',
                formatter: 'select'},
                {name: 'fieldRuleId', index: 'fieldRuleId', width: 250, edittype: 'select',
                formatter: 'select', editoptions: {value: {0:'Please select...'}}},
                {name: 'value', index: 'value', width: 250}
            ],
            beforeSelectRow: function(rowid) {
                if (lastRowId && lastRowId != rowid) {
                    var columns = $exclusionValuesTable.jqGrid('getGridParam', 'colModel');
                    if (lastCol && columns[lastCol].name == 'fieldRuleId') {
                        $exclusionValuesTable.jqGrid('saveCell', lastRow, lastCol);
                    }
                }

                var entityRuleId = $exclusionValuesTable.jqGrid('getCell', rowid, 'entityRuleId');
                var isEditable = entityRuleId != 0 && !locked;

                $exclusionValuesTable.setColProp('entityRuleId', {
                    editable: !locked
                });

                $exclusionValuesTable.setColProp('fieldRuleId', {
                    editable: isEditable
                });

                $exclusionValuesTable.setColProp('value', {
                    editable: isEditable
                });

                if (isEditable) {
                    $exclusionValuesTable.setColProp('fieldRuleId', {
                        editoptions: {value: ''}
                    });

                    $exclusionValuesTable.setColProp('fieldRuleId', {
                        editoptions: {value: entityIdToField[entityRuleId]}
                    });
                }

                return true;
            },
            afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                lastRow = iRow;
                lastCol = iCol;
                lastRowId = rowid;
            },
            afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                isDataDirty = true;
                alignButtons();

                if (cellname == 'entityRuleId') {
                    $exclusionValuesTable.jqGrid('setCell', rowid, 'fieldRuleId', 0);
                    $exclusionValuesTable.jqGrid('setCell', rowid, 'value', undefined);
                }
            },
            onSelectRow: onTableSelectionChanged,
            onSelectAll: onTableSelectionChanged
        }
    );
    addHelpText();
};