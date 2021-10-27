var AltLabCodesStep = function (studyWizard) {
    var scope = this;

    var $altLabCodesSelect = $('#altLabCodesSelect');
    var $altLabCodesAddButton = $('#altLabCodesAddButton');
    var $altLabCodesDeleteButton = $('#altLabCodesDeleteButton');
    var $altLabCodesSaveButton = $('#altLabCodesSaveButton');
    var $altLabCodesCancelButton = $('#altLabCodesCancelButton');

    var $altLabCodesTable = $('#altLabCodesTable');

    var customTestNames, customSampleNames;

    var azrowTestNames, azrowSampleNames;

    var testNames, sampleNames;

    var originTableData;
    var isDataDirty = false;
    var isDataSelected = false;

    var lastRow, lastCol;

    var loadTable = function () {
        ajaxModule.sendAjaxRequestWithoutParam('study-setup/get-alt-lab-codes', {showDialog: true}, function (result) {
            customTestNames = _(result).pluck('testName').uniq().value();
            customSampleNames = _(result).pluck('sampleName').uniq().value();
            if (azrowTestNames) {
                testNames = mergeAutocompleteArrays(customTestNames, azrowTestNames);
            }
            if (azrowSampleNames) {
                sampleNames = mergeAutocompleteArrays(customSampleNames, azrowSampleNames);
            }
            originTableData = result;
            $altLabCodesTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: originTableData}).trigger('reloadGrid');
            isDataDirty = false;
            alignButtons();
        });
    };

    var discardTable = function () {
        loadTable();
        isDataDirty = false;
        alignButtons();
    };

    var saveTable = function () {
        var data = $altLabCodesTable.jqGrid('getGridParam', 'data');
        data = _(data)
            .map(function (item) {
                return {labcode: $.trim(item.labcode), testName: $.trim(item.testName), sampleName: $.trim(item.sampleName) };
            })
            .filter(function (item) {
                return item.labcode && item.testName && item.sampleName;
            })
            .uniq(function (item) {
                return item.labcode + '|' + item.testName + '|' + item.sampleName;
            }).value();

        ajaxModule.sendAjaxRequestSimpleParams('/study-setup/save-alt-lab-codes', JSON.stringify(data),
            {showDialog: true}, function () {
                loadTable();
            }, true);
    };

    var loadDicts = function () {
        ajaxModule.sendAjaxRequestWithoutParam('/api/dict/lab_code/test_name/search', null, function (result) {
            azrowTestNames = result;
            if (customTestNames) {
                testNames = mergeAutocompleteArrays(customTestNames, azrowTestNames);
            }
        });

        ajaxModule.sendAjaxRequestWithoutParam('/api/dict/lab_code/sample_name/search', null, function (result) {
            azrowSampleNames = result;
            if (customSampleNames) {
                sampleNames = mergeAutocompleteArrays(customSampleNames, azrowSampleNames);
            }
        });
    };

    this.startStep = function () {
        if (studyWizard.workflow.selectedStudy.studyUseAltLabCodes) {
            $altLabCodesSelect.val('custom');
        } else {
            $altLabCodesSelect.val('azraw');
        }

        setUseAltCodesState();
        loadTable();
        loadDicts();
    };

    var mergeAutocompleteArrays = function (customArray, azrawArray) {
        return _(customArray)
            .difference(azrawArray)
            .sort()
            .map(function (item) {
                return {label: item, value: item, category: 'Custom'};
            })
            .concat(
                _.map(azrawArray, function (item) {
                    return {label: item, value: item, category: 'AZ RAW'};
                })
            ).value();
    };

    var cleanup = function () {
        $altLabCodesTable.jqGrid('clearGridData');

        isDataDirty = false;
        isDataSelected = false;

        customTestNames = null;
        customSampleNames = null;

        azrowTestNames = null;
        azrowSampleNames = null;

        testNames = null;
        sampleNames = null;
    };

    this.leaveStep = function () {
        if (isDataDirty) {

            return true;
        } else {
            cleanup();
            return true;
        }
    };

    var setUseAltCodesState = function () {
        if (studyWizard.workflow.selectedStudy.studyUseAltLabCodes) {
            $('#altLabCodesTableWrap').show();
//            $altLabCodesTable.jqGrid('setGridParam', {cellEdit: true});
        } else {
            $('#altLabCodesTableWrap').hide();
//            $altLabCodesTable.jqGrid('setGridParam', {cellEdit: false});
        }
        alignButtons();
    };

    var alignButtons = function () {
        if (studyWizard.workflow.selectedStudy.studyUseAltLabCodes) {
            $altLabCodesAddButton.removeAttr('disabled');

            if (isDataDirty) {
                $altLabCodesSaveButton.removeAttr('disabled');
                $altLabCodesCancelButton.removeAttr('disabled');
            } else {
                $altLabCodesSaveButton.attr('disabled', 1);
                $altLabCodesCancelButton.attr('disabled', 1);
            }

            if (isDataSelected) {
                $altLabCodesDeleteButton.removeAttr('disabled');
            } else {
                $altLabCodesDeleteButton.attr('disabled', 1);
            }
        } else {
            $altLabCodesAddButton.attr('disabled', 1);
            $altLabCodesSaveButton.attr('disabled', 1);
            $altLabCodesCancelButton.attr('disabled', 1);
            $altLabCodesDeleteButton.attr('disabled', 1);
        }
    };

    $altLabCodesSelect.change(function () {
        var value = $altLabCodesSelect.val();
        studyWizard.workflow.selectedStudy.studyUseAltLabCodes = value != 'azraw';

        setUseAltCodesState();

        ajaxModule.sendAjaxRequestSimpleParams('/study-setup/set-use-alt-lab-codes',
            {use: studyWizard.workflow.selectedStudy.studyUseAltLabCodes},
            {showDialog: true}, function () {
            }
        );
    });


    //Buttons actions
    $altLabCodesCancelButton.click(function () {
        wizardCommonModule.showYesNoDialog('Discard changes',
            'This will remove all changes made in this session.<br/>Continue?',
            function () {
                discardTable();
            });
    });

    $altLabCodesSaveButton.click(function () {
        if ($altLabCodesTable.getGridParam("reccount") > 0) {
            $altLabCodesTable.jqGrid('saveCell', lastRow, lastCol);
        }

        wizardCommonModule.showYesNoDialog('Save changes',
            'This will replace the current decoding information in the database for this study.<br/>' +
                'If a labcode is missing or incorrect, then this may seriously affect the displayed information in ACUITY.<br/>' +
                'Continue?', function () {
                saveTable();
            });
    });

    $altLabCodesAddButton.click(function () {
        var id = $.jgrid.randId();
        $altLabCodesTable.jqGrid('addRowData', id, {});
        isDataDirty = true;
        alignButtons();
    });

    $altLabCodesDeleteButton.click(function () {
        wizardCommonModule.showYesNoDialog('Delete', 'Delete selected rows?', function () {
            var rows = $altLabCodesTable.jqGrid('getGridParam', 'selarrrow');
            var dirty = false;
            while (!_.isEmpty(rows) && $altLabCodesTable.jqGrid('delRowData', rows.pop())) {
                dirty = true
            }
            isDataDirty = dirty;
            isDataSelected = false;
            alignButtons();
        })
    });

    var onTableSelectionChanged = function () {
        var rows = $altLabCodesTable.jqGrid('getGridParam', 'selarrrow');
        isDataSelected = rows.length > 0;
        alignButtons();
    };

    var labcodeUniqueCheck = function(value, colname) {
        if (!value) return [true, ""];
        var cur_id = $altLabCodesTable.jqGrid('getGridParam', 'selrow');
        var ids = $altLabCodesTable.jqGrid('getDataIDs');
        for (var id in ids) {
            if (cur_id != ids[id]) {
                var val = $altLabCodesTable.jqGrid('getCell', ids[id], 'labcode');
                if (val && val.toUpperCase() == value.toUpperCase()) {
                    return [false,"Such labcode already exists"];
                }
            }
        }
        return [true, ""];
    };

    //Table
    $altLabCodesTable.jqGrid({
            datatype: 'local',
            autoencode: true,
            width: '100%',
            height: '100%',
            cellEdit: true,
            multiselect: true,
            rowNum: 9999,
//            recordpos: 'left',
//            viewrecords: true,
//            gridview: true,
//            pager: 'altLabCodesPager',

            cellsubmit: 'clientArray',
            colNames: ['', 'Labcode', 'Test name', 'Sample name'],

            colModel: [
                {name: 'id', index: 'id', width: 20, hidden: true},
                {name: 'labcode', index: 'labcode', width: 200, editable: true,
                    editrules: {custom: true, custom_func: labcodeUniqueCheck}},
                {name: 'testName', index: 'testName', width: 300, editable: true,
                    editoptions: {
                        dataInit: function (elem) {
                            $(elem).catcomplete({minLength: 0, source: testNames});
                        }
                    }
                },
                {name: 'sampleName', index: 'sampleName', width: 200, editable: true,
                    editoptions: {
                        dataInit: function (elem) {
                            $(elem).catcomplete({minLength: 0, source: sampleNames});
                        }
                    }
                }
            ],
            afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                lastRow = iRow;
                lastCol = iCol;
            },
            afterSaveCell: function (rowid, cellname, value, iRow, iCol) {

                isDataDirty = true;
                alignButtons();

                value = $.trim(value);
                if (cellname == 'testName') {
                    if (_.indexOf(customTestNames, value, true) < 0 && _.indexOf(azrowTestNames, value, true) < 0) {
                        customTestNames.push(value);
                        testNames = mergeAutocompleteArrays(customTestNames, azrowTestNames);
                    }
                } else if (cellname == 'sampleName') {
                    if (_.indexOf(customSampleNames, value, true) < 0 && _.indexOf(azrowSampleNames, value, true) < 0) {
                        customSampleNames.push(value);
                        sampleNames = mergeAutocompleteArrays(customSampleNames, azrowSampleNames);
                    }
                }
            },
            onSelectRow: onTableSelectionChanged,
            onSelectAll: onTableSelectionChanged
        }
    );
};
