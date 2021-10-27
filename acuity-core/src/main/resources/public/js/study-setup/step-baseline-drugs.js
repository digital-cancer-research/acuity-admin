var BaselineDrugsStep = function (studyWizard) {

    var $customBaselineDrugsTable = $('#customBaselineDrugsTable');
    var $customBaselineDrugsPane = $('#customBaselineDrugsPane');
    var $baselinesSaveButton = $('#baselinesSaveButton');
    var $baselinesAddButton = $('#baselinesAddButton');

    var lastRow, lastCol;

    var useCustomDrugsForBaseline = false;
    var customBaselineDrugs = [];
    var availableStudyDrugs = [];

    /**
     * Load StudyDrugs setting from the server
     */
    function loadStudyBaselineDrugs() {
        console.log('Loading StudyBaselineDrugs');

        ajaxModule.sendAjaxRequestWithoutParam('/study-setup/load-study-baseline-drugs',
            {showDialog: true}, function (result) {
                useCustomDrugsForBaseline = result.useCustomDrugsForBaseline;
                customBaselineDrugs = result.customBaselineDrugs;

                availableStudyDrugs = _.pluck(customBaselineDrugs, 'drugName');

                if (useCustomDrugsForBaseline) {
                    $('input:radio[name=baselineDrugsMode][value=custom]').prop('checked', true);
                    $customBaselineDrugsPane.show();
                } else {
                    $('input:radio[name=baselineDrugsMode][value=any]').prop('checked', true);
                    $customBaselineDrugsPane.hide();
                }
                setSaveState(false);
                updateTableData();
            });
    }

    function saveStudyBaselineDrugs() {
        console.log('Saving StudyBaselineDrugs');
        console.log(customBaselineDrugs);
        _.forEach(customBaselineDrugs, function (item) {
            item.id = null;
        });
        ajaxModule.sendAjaxRequestSimpleParams('/study-setup/save-study-baseline-drugs',
            JSON.stringify({
                customBaselineDrugs: customBaselineDrugs,
                useCustomDrugsForBaseline: useCustomDrugsForBaseline
            }),
            {showDialog: true}, function (result) {
                console.log('Saved StudyBaselineDrugs');
                setSaveState(false);
            }, true);
    }

    function validateAndSaveStudyBaselineDrugs() {
        commitTable();
        if (hasUnsavedData()) {
            if (isValidState()) {
                saveStudyBaselineDrugs();
            } else {
                wizardCommonModule.showWarningDialog('You can\'t leave the table blank when the option 2 is selected.', 350);
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the first option is selected,
     * or the second option is selected and the table is not empty
     * @returns {boolean}
     */
    function isValidState() {
        if (useCustomDrugsForBaseline) {
            return customBaselineDrugs.length > 0;
        } else {
            return true;
        }
    }

    function setSaveState(needToSave) {
        if (needToSave) {
            $baselinesSaveButton.removeAttr('disabled');
        } else {
            $baselinesSaveButton.attr('disabled', 1);
        }
    }

    function commitTable() {
        $customBaselineDrugsTable.jqGrid('saveCell', lastRow, lastCol);
        customBaselineDrugs = getRefinedTableData();
        updateTableData();
    }

    function updateTableData() {
        _.forEach(customBaselineDrugs, function (item, i) {
            item.id = i;
        });
        $customBaselineDrugsTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: customBaselineDrugs}).trigger('reloadGrid');
    }

    function hasUnsavedData() {
        return !$baselinesSaveButton.attr('disabled');
    }

    /**
     *  Dump table, remove duplicates, keep only last drug option per drug, then return data
     */
    function getRefinedTableData() {
        var data = $customBaselineDrugsTable.jqGrid('getGridParam', 'data');

        data = _.filter(data, 'drugName');
        data = data.reverse();
        data = _.uniq(data, 'drugName');

        _.each(data, function (item) {
            item.include = item.include == true || item.include == 'Yes';
        });
        data = data.reverse();
        return data;
    }

    this.startStep = function () {
        loadStudyBaselineDrugs();
    };

    this.leaveStep = function () {
        return validateAndSaveStudyBaselineDrugs();
    };

    $('input:radio[name=baselineDrugsMode]').change(
        function () {
            setSaveState(true);

            if ($(this).val() == 'custom') {
                useCustomDrugsForBaseline = true;
                $customBaselineDrugsPane.show();

            } else {
                useCustomDrugsForBaseline = false;
                $customBaselineDrugsPane.hide();
            }
        }
    );

    $baselinesAddButton.click(function () {
        setSaveState(true);
        var data = $customBaselineDrugsTable.jqGrid('getGridParam', 'data');
        data.push({drugName: null, include: false});
        $customBaselineDrugsTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
    });

    $baselinesSaveButton.click(function () {
        validateAndSaveStudyBaselineDrugs();
    });

    $customBaselineDrugsTable.jqGrid({
            datatype: 'local',
            width: '100%',
            height: '100%',
            cellEdit: true,
            rowNum: 9999,

            cellsubmit: 'clientArray',
            colNames: ['Drug identifier', 'Include'],

            colModel: [
                {
                    name: 'drugName', index: 'drugName',
                    width: 200, editable: true, editoptions: {
                    dataInit: function (elem) {
                        $(elem).autocomplete({minLength: 0, source: availableStudyDrugs});
                    }
                }
                }, {
                    name: 'include', index: 'include', width: 100, align: 'center',
                    editable: true, edittype: 'checkbox',
                    formatter: 'checkbox', formatoptions: {disabled: false}
                }
            ],

            beforeSelectRow: function (rowid, e) {
                // workaround for checkbox formatter/editor from the
                // http://stackoverflow.com/questions/26490263/jqgrid-checkbox-not-saving
                var $self = $(this),
                    iCol = $.jgrid.getCellIndex($(e.target).closest("td")[0]),
                    cm = $self.jqGrid("getGridParam", "colModel"),
                    localData = $self.jqGrid("getLocalRow", rowid);
                if (cm[iCol].name === "include") {
                    localData.include = $(e.target).is(":checked");
                }
                setSaveState(true);
                return true; // allow selection
                //this buggy jqgrid makes me cry
            },

            afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                lastRow = iRow;
                lastCol = iCol;

                setSaveState(true);
            },

            afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                setSaveState(true);
            }
        }
    );
};