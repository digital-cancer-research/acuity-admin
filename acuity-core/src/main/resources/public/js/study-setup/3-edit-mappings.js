var MappingStudyStep = function (studyWizard) {
    var studyDlgOkBtnId = "studyOkBtn";
    var studyDlgCancelBtnId = "studyCancelBtn";
    var studyDlgId = "addStudyDlg";
    var studyImportMappingDlgId = "studyImportMappingDlg";
    var studyBrowseBtnId = "study-browse-btn";
    var studyAddBtnId = "mainStudyAdd";
    var studyExportBtnId = "mainStudyExport";
    var studyImportBtnId = "mainStudyImport";
    var studyEditBtnId = "mainStudyEditSettingsBtn";
    var studyDetailsSaveBtnId = "studyDetailsTableSaveBtn";
    var studyDetailsImportBtnId = "studyDetailsImportBtn";
    var studyDetailsExportBtnId = "studyDetailsExportBtn";
    var deleteMFileRuleBtnId = "mainStudyDeleteBtn";
    var revertMappingsBtnId = "studyDetailsCancelBtn";
    var mainStudyMoveUpBtnId = "mainStudyMoveUpBtn";
    var mainStudyMoveDownBtnId = "mainStudyMoveDownBtn";
    var studyDetailsTableAddMappingRuleBtnId = "studyDetailsTableAddMappingRuleBtn";
    var studyDetailsTableDeleteMappingRuleBtnId = "studyDetailsTableDeleteMappingRuleBtn";
    var csvDelimeter = ',';
    var csvHeader = ['Data field', 'Source column', 'Decoding', 'Default value', 'Aggregation function'];

    this.mappingHierarchy = null;
    this.aggregationFunctions = null;
    this.defaultTypes = null;
    this.lastStudyDetailsRow = 0;
    this.lastStudyDetailsCol = 0;
    this.lastSourceDataRow = 0;
    this.lastSourceDataCol = 0;
    this.mappingsLoaded = false;
    this.studyDetailsTableId = "studyDetailsTable";
    this.studyMainTableId = "mainStudyTable";
    this.selectedFileRuleId = 0;
    this.selectedMappingRuleId = 0;
    this.fileSections = null;
    this.completeMappings = null;
    this.studyWizard = studyWizard;
    this.counter = 0;

    var scope = this;

    var init = function () {
        createMainStudyTable();
        createDetailsStudyTable();

        $("#" + scope.studyMainTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
        $("#" + scope.studyDetailsTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
        $(window).bind('resize', function () {
            $("#" + scope.studyMainTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
            $("#" + scope.studyDetailsTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
        });
        $('#' + studyEditBtnId).on("click", function () {
            showMappingDialog('edit-mapping');
        });

        $('#' + studyAddBtnId).on("click", function () {
            showMappingDialog('add-mapping');
        });
        $('#' + studyImportBtnId).on("click", function () {
            $("#" + studyImportMappingDlgId).dialog({
                title: 'Import file mappings',
                modal: true,
                resizable: false,
                width: 'auto',
                //height : 280,
                open: function () {
                    $('#' + studyImportMappingDlgId).trigger('reset');
                },
                create: function () {
                    importMappingsDialogInit();
                }

            });
        });
        var importMappingsDialogInit = function () {
            $('#study-import-mapping-form input').trigger('reset');
            /*
             $('#studyMappingInput').change(function() {
             $('#studyGropingSourceFile').val($(this).val());
             });
             */
            $('#importMappingsCancelBtn').on("click", function () {
                $("#" + studyImportMappingDlgId).dialog("close");
            });
            $('#importMappingsOkBtn').on("click", function () {
                if ($('#study-import-mapping-form input').val() == '') {
                    wizardCommonModule.showWarningDialog('Please select file');
                }
                else {
                    wizardCommonModule.showWaitingDialog();
                    var formData = new FormData($('#study-import-mapping-form')[0]);

                    $.ajax({
                        url: 'import-study-mapping?_csrf=' + ajaxModule.csrf.token,
                        type: 'POST',
                        complete: function () {
                            wizardCommonModule.closeWaitingDialog();
                        },
                        success: function (result) {
                            $("#" + studyImportMappingDlgId).dialog("close");
                            scope.completeMappings = result;
                            scope.updateCheckList(result);

                            loadFileRules();
                        },
                        error: function (jqXHR) {
                            // wizardCommonModule.closeWaitingDialog();
                            if (jqXHR.status == 401 || jqXHR.status == 419) { // Service unavailable - means session expired
                                wizardCommonModule.showSessionExpiredDialog();
                            } else if (jqXHR.status == 403) { // Access denied
                                wizardCommonModule.showErrorDialog('Access denied', jqXHR.responseText);
                            } else {
                                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);

                            }
                        },
                        data: formData,
                        cache: false,
                        contentType: false,
                        processData: false
                    });
                }
            });
        }
        $('#' + studyExportBtnId).on("click", function () {
            window.location = "export-study-mapping";
        });

        $('#' + studyDetailsSaveBtnId).on("click", function () {
            scope.checkStudyCompleteAndSaveValues(null, false);
        });

        $('#' + studyDetailsImportBtnId).on("change", function (e) {
            importStudyMappings(e);
        });

        $('#' + studyDetailsExportBtnId).on("click", function () {
            exportStudyMappings();
        });

        $('#' + deleteMFileRuleBtnId).on("click", function () {
            onDeleteMappingCallback();
        });

        $("#" + revertMappingsBtnId).on("click", function () {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Discard mapping changes?", scope.revertValueChanges);
        });

        $("#studyAcuityEnabled").on('click', function () {
            toggleChangesControls(true);
        });

        $("#" + studyDlgId).keypress(function (event) {
            if (event.keyCode == 13) {
                event.preventDefault();
                $('#' + studyDlgOkBtnId).trigger('click');
            }
        });

        $('#mainStudyAutoMapButton').on('click', function () {
            autoMapping();
        });

        initUpDownButtons();

        addHelpText();

        $("#" + studyDetailsTableAddMappingRuleBtnId).on('click', function () {
            var slaveTable = $("#" + scope.studyDetailsTableId);
            slaveTable.jqGrid('addRowData', --scope.counter, {
                id: scope.counter,
                dataField: '[Please enter source data column name]',
                mandatory: false,
                agrFunctions: 1,
                dynamic: true
            });
        });

        $("#" + studyDetailsTableDeleteMappingRuleBtnId).on('click', function () {
            var slaveTable = $("#" + scope.studyDetailsTableId);
            var rows = slaveTable.getGridParam('data');
            var totalDynamic = 0;
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if (row.dynamic) totalDynamic++;
            }
            if (totalDynamic > 1) {
                slaveTable.jqGrid('delRowData', scope.selectedMappingRuleId);
                slaveTable.trigger('reloadGrid');
                scope.selectedMappingRuleId = 0;
            } else {
                wizardCommonModule.showWarningDialog("At least one mapping rule must be added");
            }

        });

    };

    var loadMappingStatuses = function () {
        ajaxModule.sendAjaxRequestWithoutParam('study-setup-get-mapping-statuses', {showDialog: true}, function (result) {
            scope.completeMappings = result;
            scope.updateCheckList(scope.completeMappings);
        });
    };

    var autoMapping = function () {
        if (!studyWizard.workflow.selectedStudy.primarySource) {
            wizardCommonModule.showWarningDialog('Primary Source Folder location not specified');
            return;
        }

        var todoFileDescriptions = [];

        if (scope.fileSections && scope.completeMappings) {
            var allFileDescriptions = _(scope.fileSections).pluck('fileDescriptions').flatten().sortBy('processOrder').value();
            for (var i = 0; i < allFileDescriptions.length; i++) {
                var mapped = false;
                for (var j = 0; j < scope.completeMappings.length; j++) {
                    if (allFileDescriptions[i].id == scope.completeMappings[j].fileDescriptionId) {
                        mapped = true;
                        break;
                    }
                }
                if (!mapped) {
                    todoFileDescriptions.push(allFileDescriptions[i]);
                }
            }
        }

        if (todoFileDescriptions.length < 1) {
            wizardCommonModule.showWarningDialog('Nothing more to map');
            return;
        }

        //sas file decoding information rule should be first
        var cntlinFiles = _.remove(todoFileDescriptions, {id: 18});

        if (cntlinFiles.length > 0) {
            todoFileDescriptions = _(cntlinFiles).concat(todoFileDescriptions).value();
        }

        wizardCommonModule.showYesNoDialog('Auto-map function',
            'This function attempts to predict the mappings for your study by looking at previous studies in ACUITY.' +
            ' This method is more likely to work if your study is similar to an accepted standard.' +
            ' You will be required to confirm that all mappings for a file are correct before the source files can be used in ACUITY.',
            function () {
                var total = todoFileDescriptions.length;
                var i = 0;

                var sendMapping = function () {
                    if (todoFileDescriptions.length == 0) {
                        //auto-mapping complete
                        loadFileRules();
                        loadMappingStatuses();
                        return;
                    }

                    i++;
                    var fileDescription = todoFileDescriptions.shift();
                    var mapping = {
                        typeId: fileDescription.id,
                        dataSourceLocationType: 'filePrediction',
                        skipFileRuleCreationIfFilePredictionFailed: true,
                        mappingsPrediction: true,
                        studyAcuityEnabled: false
                    };

                    var msg = 'Mapping ' + i + '/' + total + ':<br/>' + fileDescription.displayName;


                    wizardCommonModule.showProgressDialog(msg);
                    ajaxModule.sendAjaxRequest("study-setup-save-file-rule", JSON.stringify(mapping), null,
                        function () {
                            wizardCommonModule.closeProgressDialog();
                            sendMapping();
                        }, function () {
                            wizardCommonModule.closeProgressDialog();
                            sendMapping();
                        });
                };

                sendMapping();

            }, null, 450);
    };

    var loadFileRules = function () {
        ajaxModule.sendAjaxRequestWithoutParam('study-setup-get-file-rules', {showDialog: true}, function (result) {
            $("#" + scope.studyMainTableId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
            scope.resizeMappingStep();
            scope.initDecodingValues();
        });
    };

    /** Main mapping table def**/
    var createMainStudyTable = function () {
        $("#" + scope.studyMainTableId).jqGrid({
            datatype: "local",
            autoencode: true,
            width: "100%",
            height: "100%",
            colNames: ['', 'Type of information <a href="#colTypeOfInfo" class="help">?</a>',
                'Data source <a href="#colDataSource" class="help">?</a>',
                'ACUITY enabled? <a href="#colAcuityEnabled" class="help">?</a>',
                'Last edited by <a href="#colLastEdited" class="help">?</a>', 'File standard',
                '', ''],
            colModel: [{name: 'id', index: 'id', hidden: true, align: 'center', key: true},
                {name: 'typeId', index: 'typeId', width: 200, align: 'center', formatter: typeFormatter},
                {
                    name: 'dataSourceLocation',
                    index: 'dataSourceLocation',
                    width: 400,
                    align: 'center',
                    formatter: sourceFormatter
                },
                {name: 'studyAcuityEnabled', index: 'acuityEnable', formatter: 'checkbox', width: 100, align: "center"},
                {name: 'lastEditedBy', index: 'lastEditedBy', width: 100, align: "center"},
                {name: 'fileStandard', index: 'fileStandard', width: 100},
                {name: 'dataSourceLocationType', index: 'studyStorage', hidden: true},
                {name: 'dataSourceLocation', index: 'dataSourceLocation', hidden: true}],
            recordpos: 'left',
            viewrecords: true,
            gridview: true,
            shrinkToFit: true,
            rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
            beforeSelectRow: function (id) {
                var slaveTable = $("#" + scope.studyDetailsTableId);
                var isSlaveInEditMode = wizardCommonModule.isSlaveTableEditable(slaveTable);
                if (isSlaveInEditMode) {
                    wizardCommonModule.showSaveDialog("Are You Sure?", "You have unsaved data-mapping edits." +
                        "Do you wish to save or discard these changes?",
                        function (id) {
                            scope.checkStudyCompleteAndSaveValues(id, false);
                        },
                        function (id) {
                            //scope.revertValueChanges();
                            $("#" + scope.studyMainTableId).jqGrid('setSelection', id);
                        },
                        function (id) {
                            // do nothing
                        }, id
                    );
                    return false;
                }
                return true;
            },
            onSelectRow: function (id) {
                selectMappingRowHandler(id);
            },
            resizeStop: function (width, index) {
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_MAPPING_STEP_INX);
            }
        });
    };

    var typeFormatter = function (cellvalue, options, rowObject) {
        return getFileDescriptionById(cellvalue);
    };

    var sourceFormatter = function (cellvalue, options, rowObject) {
        return cellvalue == null ? "manual" : "<a href='/app#/fileView?fileUrl=" + encodeURI(cellvalue) + "' target='_blank'>" + cellvalue + "</a>";
    };

    this.sourceColumnNames = [];
    this.sourceColumnMetadata = {};
    var getColumnNamesAjax;
    var getColumnMetadataAjax;
    var selectedRowId = 1;

    /**
     * Load columns info for the given file
     * @param file
     * @param callback
     */
    var loadMetaData = function (file, callback) {

        scope.sourceColumnMetadata = [];
        scope.sourceColumnNames = [];

        if (getColumnNamesAjax) {
            getColumnNamesAjax.abort();
            getColumnNamesAjax = null;
        }
        if (getColumnMetadataAjax) {
            getColumnMetadataAjax.abort();
            getColumnMetadataAjax = null;
        }

        if (_.isEmpty(_.trim(file))) {
            return;
        }

        if (wizardCommonModule.validateSambaSasFilePath(file)) {
            getColumnMetadataAjax = $.getJSON('api/source/get_column_metadata', {path: file}, function (data) {
                getColumnMetadataAjax = null;
                if (data) {
                    scope.sourceColumnMetadata = data;
                    scope.sourceColumnNames = _.map(_.keys(data), removeCommas);
                }
                if (callback) {
                    callback();
                }
            });
        } else {
            getColumnNamesAjax = $.getJSON('api/source/get_column_names', {path: file}, function (data) {
                getColumnNamesAjax = null;
                if (data) {
                    scope.sourceColumnNames = _.map(data, removeCommas)
                }
                if (callback) {
                    callback();
                }
            });
        }
    };

    var removeCommas = function (token) {
        return token.split(',').join(' ');
    };

    var selectMappingRowHandler = function (id) {
        var $mainTable = $("#" + scope.studyMainTableId);
        var mappingId = $mainTable.jqGrid('getCell', id, 'id');
        selectedRowId = id;
        scope.selectedFileRuleId = id;

        var rowData = $mainTable.jqGrid('getRowData', id)
        var path = rowData['dataSourceLocation'];
        var isWide = rowData['typeId'].toUpperCase().indexOf('WIDE') > -1;

        loadMetaData(path, function () {
            $("#" + scope.studyDetailsTableId).trigger('reloadGrid');
        });

        $("#studyDetailsTableWrapper").hide();
        if ($mainTable.jqGrid('getCell', selectedRowId, "studyAcuityEnabled") == 'Yes')
            $("#studyAcuityEnabled").attr('checked', true);
        else {
            $("#studyAcuityEnabled").attr('checked', false);
        }

        var deleteMFileRuleBtnElement = $("#" + deleteMFileRuleBtnId);
        var studyEditBtnElement = $("#" + studyEditBtnId);

        deleteMFileRuleBtnElement.removeAttr('disabled').removeClass('disabled');
        studyEditBtnElement.removeAttr('disabled').removeClass('disabled');

        ajaxModule.sendAjaxRequestSimpleParams("study-setup-get-mapping-rules", {
            fileRuleId: mappingId
        }, {
            showDialog: false
        }, function (result) {
            var slaveTable = $("#" + scope.studyDetailsTableId);
            slaveTable.jqGrid('clearGridData');
            $("#studyDetailsTableWrapper").show();
            if (result && result.length > 0) {
                fillDataValues(result, slaveTable);
            }
            console.log(result);
            if (isWide) {
                $("#studyDetailsTableMappingButtons").show();
                var mapped = false;
                for (var i = 0; i < result.length; i++) {
                    if (result[i].dynamic) {
                        mapped = true;
                        break;
                    }
                }
                if (!mapped) {
                    slaveTable.jqGrid('addRowData', --scope.counter, {
                        id: scope.counter,
                        dataField: '[Please enter source data column name]',
                        mandatory: false,
                        agrFunctions: 1,
                        dynamic: true
                    });
                }
            } else {
                $("#studyDetailsTableMappingButtons").hide();
            }
            toggleChangesControls(false);
            scope.resizeMappingStep();
        });
    };

    var fillDataValues = function (values, table) {
        table.jqGrid('clearGridData').jqGrid('setGridParam', {
            data: values
        }).trigger('reloadGrid');
        $(".help").tipTip();
        $("[role='gridcell']").attr("title", "");
    };
    /** Main mapping table def end**/

    // We need to know field name in order to select type of the editor: input[text] or textarea
    var customEditorField = null;

    function isArrayLikeField(fieldName) {
        switch (fieldName) {
            case 'AE severity grade changes':
            case 'AE severity change dates (mm/dd/yyyy)':
            case 'Action taken due to severity grade changes for investigational drugs':
            case 'Action taken due to severity grade changes for additional drugs':
            case 'Maximum severity grade':
            case 'Investigational drug names':
            case 'Additional drug names':
            case 'Initial action taken for investigational drugs':
            case 'Initial action taken for additional drugs':
            case 'Causality for investigational drugs':
            case 'Causality for additional drugs':
            case 'AE Description':
                return true;
        }
        return false;
    }

    function customEditorElement(value) {
        var element;

        if (isArrayLikeField(customEditorField)) {
            element = $('<textarea>').css({resize: 'vertical', width: '100%', padding: '5px'}).prop({
                rows: 3,
                spellcheck: false
            });
        } else {
            element = $('<input>').css({width: '100%', padding: '5px'}).prop({type: 'text'});
        }

        element.val(value);

        element.autocomplete({
            source: function (request, response) {
                var words = request.term.split(", ");
                if (words.length > 0) {
                    var term = words[words.length - 1];
                    var matcher = new RegExp($.ui.autocomplete.escapeRegex(term), "i");
                    response($.grep(scope.sourceColumnNames, function (item) {
                        return matcher.test(item);
                    }));
                } else {
                    response(null);
                }
            },
            focus: function (event, ui) {
                if (event.keyCode) {
                    this.value = sourceDataGetConcatenatedValue(this.value, ui.item.value);
                    return false;
                }
                return true;
            },
            select: function (event, ui) {
                this.value = sourceDataGetConcatenatedValue(this.value, ui.item.value);
                return false;
            },
            position: {collision: 'flip flip'}
        });

        setTimeout(function () {
            element.focus();
        }, 0);

        return element;
    }

    function customEditorValue(elem, operation, value) {
        if (operation === 'get') {
            return $(elem).val();
        } else if (operation === 'set') {
            $('input', elem).val(value);
        }
    }

    /** Slave mapping table def**/
    var createDetailsStudyTable = function () {
        $("#" + scope.studyDetailsTableId).jqGrid({
            datatype: "local",
            autoencode: true,
            width: "100%",
            height: "100%",
            colNames: ['', 'Data field <a href="#colDataField" class="help">?</a>',
                'Source data column <a href="#colSourceData" class="help">?</a>',
                'Decoding information <a href="#colDecodingInfo" class="help">?</a>',
                'Default value <a href="#colDefaultValue" class="help">?</a>',
                'Aggregation functions <a href="#colAggregation" class="help">?</a>',
                'Mandatory <a href="#colMandatory" class="help">?</a>',
                '',
                ''],
            colModel: [
                {name: 'id', index: 'id', hidden: true, width: 200, align: "center"},
                {name: 'dataField', index: 'dataField', editable: false, width: 200, align: "center"},
                {
                    name: 'sourceData', index: 'sourceData', width: 200, editable: true, align: "center",
                    formatter: sourceDataFormatter, unformat: simpleUnFormat, edittype: 'custom',
                    editoptions: {custom_element: customEditorElement, custom_value: customEditorValue}
                },
                {
                    name: 'decodingInfo', index: 'decodingInfo', width: 94, editable: false, align: "center",
                    formatter: decodingFormatter, unformat: simpleUnFormat,
                    editoptions: {
                        dataInit: function (elem) {
                            $(elem).autocomplete({
                                source: scope.decodingValues,
                                position: {collision: 'flip flip'}
                            });
                        }
                    }
                },
                {name: 'defaultValue', index: 'defaultValue', width: 120, editable: true, align: "center"},
                {
                    name: 'agrFunctions',
                    index: 'agrFunctions',
                    width: 145,
                    edittype: 'select',
                    formatter: 'select',
                    editoptions: {value: "1:One;2:Two"},
                    editable: true,
                    align: "center"
                },
                {name: 'mandatory', index: 'mandatory', width: 120, editable: false, align: "center"},
                {
                    name: 'helpText',
                    index: 'helpText',
                    width: 40,
                    editable: false,
                    align: "center",
                    formatter: helpFormatter
                },
                {name: 'dynamic', index: 'dynamic', editable: true, hidden: true}
            ],
            recordpos: 'left',
            viewrecords: true,
            gridview: true,
            cellEdit: true,
            rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
            cellsubmit: 'clientArray',
            afterEditCell: function (rowid, cellname, value, iRow, iCol) {
                scope.lastStudyDetailsRow = iRow;
                scope.lastStudyDetailsCol = iCol;
                toggleChangesControls(true);
                //copy-paste from http://stackoverflow.com/questions/4628949/excel-like-enter-and-tab-key-navigation-in-cell-editing
                // Get a handle to the actual input field
                var inputControl = jQuery('#' + (iRow) + '_' + cellname);

                // Listen for enter (and shift-enter)
                inputControl.bind("keydown", function (e) {

                    if (e.keyCode === 13) {  // Enter key:
                        var increment = e.shiftKey ? -1 : 1;

                        // Do not go out of bounds
                        var lastRowInd = jQuery("#" + scope.studyDetailsTableId).jqGrid("getGridParam", "reccount")
                        if (iRow + increment == 0 || iRow + increment == lastRowInd + 1)
                            return;   // we could re-edit current cell or wrap
                        else
                            jQuery("#" + scope.studyDetailsTableId).jqGrid("editCell", iRow + increment, iCol, true);
                    }

                }); // End keydown binding
            },
            afterSaveCell: function (rowid, cellname, value, iRow, iCol) {
                $('#' + scope.studyDetailsTableId + ' td.dirty-cell').removeClass("dirty-cell edit-cell");
                $('#' + scope.studyDetailsTableId + '  tr.edited').removeClass("edited");

                var dynamic = $("#" + scope.studyDetailsTableId).jqGrid('getCell', rowid, 'dynamic');

                if (cellname == 'sourceData' && parseBoolean(dynamic)) {
                    if (value.length > 0) {
                        $("#" + scope.studyDetailsTableId).jqGrid('setCell', rowid, 'dataField', value);
                    } else {
                        $("#" + scope.studyDetailsTableId).jqGrid('setCell', rowid, 'dataField', '[Please enter source data column name]');
                    }
                }

                if (cellname == 'sourceData' && value && scope.sourceColumnMetadata) {
                    $("#" + scope.studyDetailsTableId).jqGrid('setCell', rowid, 'decodingInfo', decoding);
                    var colMeta = scope.sourceColumnMetadata[value.toUpperCase()];
                    if (colMeta) {
                        var decoding = colMeta.format;
                        if (decoding.substring(0, 1) == '$') {
                            decoding = decoding.substring(1);
                        }
                        if (scope.decodingValues.indexOf(decoding) >= 0) {
                            $("#" + scope.studyDetailsTableId).jqGrid('setCell', rowid, 'decodingInfo', decoding);
                            $("#" + scope.studyDetailsTableId).jqGrid('getLocalRow', rowid).decodingInfo = decoding;
                        }
                    }
                }
            },
            onSelectCell: function (rowid, cellname, value, iRow, iCol) {
                if (cellname == "decodingInfo") {
                    $('#' + scope.studyDetailsTableId + ' td.edit-cell').removeClass("edit-cell");
                    wizardCommonModule.showWarningDialog("In order to use this option you must map a value-decoding file into the ACUITY system!");
                }
            },
            beforeSelectRow: function (rowid) {
                scope.selectedMappingRuleId = rowid;
                var rowData = $(this).jqGrid('getRowData', rowid);
                console.log(rowData);
                if (rowData.mandatory === "true") {
                    $("#" + studyDetailsTableDeleteMappingRuleBtnId).prop('disabled', true);
                } else {
                    $("#" + studyDetailsTableDeleteMappingRuleBtnId).prop('disabled', false);
                }
                return true;
            },
            beforeEditCell: function (rowid) {
                customEditorField = $(this).jqGrid('getCell', rowid, 'dataField');
                return true;
            },
            gridComplete: function () {
                var table = $("#" + scope.studyDetailsTableId);
                table.setGridWidth($("#leftInnerPane").width() - 50, true);
                var recs = table.jqGrid('getGridParam', 'reccount');
                if (recs == 0) {
                    $("#studyDetailsTableWrapper").hide();
                } else {
                    $("#studyDetailsTableWrapper").show();
                }
            },
            resizeStop: function (width, index) {
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_MAPPING_STEP_INX);
            }
        });
    };

    var parseBoolean = function (b) {
        return !(/^(false|0)$/i).test(b) && !!b;
    }

    var sourceDataFormatter = function (cellValue, options, rowObject) {
        if (!cellValue) {
            return "";
        }

        if (scope.sourceColumnNames && scope.sourceColumnNames.length > 0) {
            var values = cellValue.split(", ");
            var result = [];
            for (var i = 0; i < values.length; i++) {
                var value = values[i];
                var item = scope.sourceColumnNames.indexOf(value) == -1 ?
                "<span class='curly-underline'>" + value + "</span>" : value;
                result.push(item);
            }
            result = result.join(", ");

            if (values.length > 1) {
                // OMG what the fucking shit the Jqgrid is
                // http://www.trirand.com/jqgridwiki/doku.php?id=wiki:custom_formatter
                // all its API is an inconsistent and confusing heap of crap
                // rowObject - sometimes it is Object, but sometimes is XML
                var selectorForCellInCurrentRow = 'tr[id=' + options.rowId + '] td[aria-describedby=studyDetailsTable_dataField]';
                var fieldType = rowObject.dataField ? rowObject.dataField :
                    $(rowObject).find(selectorForCellInCurrentRow).prop('title');

                if (isArrayLikeField(fieldType)) {
                    if (!_.isEqual(values, _.uniq(_.sortBy(values)))) {
                        result = '<span style="background-color: lightpink" title="Perhaps these items are out of order?">' + result + '</span>';
                    }
                }
            }

            return result;
        }
        else {
            return cellValue;
        }
    };

    var simpleUnFormat = function (cellValue, options, cell) {
        return cellValue;
    }

    var decodingFormatter = function (cellValue, options, rowObject) {
        if (!cellValue) {
            return "";
        }

        if (scope.decodingValues.length > 0 &&
            scope.decodingValues.indexOf(cellValue) == -1) {
            return "<span class='curly-underline'>" + cellValue + "</span>";
        }
        else {
            return cellValue;
        }
    }

    var sourceDataGetConcatenatedValue = function (currentValue, newItemValue) {
        var previousTerms = "";
        var delimiter = ", ";
        var lastDelimiterIndex = currentValue.lastIndexOf(delimiter);
        if (lastDelimiterIndex >= 0) {
            previousTerms = currentValue.substring(0, lastDelimiterIndex + delimiter.length);
        }
        return previousTerms + newItemValue;
    }

    var helpFormatter = function (cellValue, options, rowObject) {
        if (cellValue) {
            return "<a class='help' title='" + cellValue + "'>?</a>";
        } else {
            return "";
        }

    };

    var findAggrFunctionByNameAndHelperStr = function (nameAndHelperStr) {
        for (var i = 0; i < scope.aggregationFunctions.length; i++) {
            if (getAggrFuncNameAndHelperStr(scope.aggregationFunctions[i].name, scope.aggregationFunctions[i].helper) == nameAndHelperStr)
                return scope.aggregationFunctions[i];
        }
        ;
        return null;
    };

    var getAggrFuncNameAndHelperStr = function (name, helper) {
        return name + (helper ? ':' + helper : '');
    };

    var findAggrFunctionById = function (id) {
        for (var i = 0; i < scope.aggregationFunctions.length; i++) {
            if (scope.aggregationFunctions[i].id == id)
                return scope.aggregationFunctions[i];
        }
        ;
        return null;
    };

    var importStudyMappings = function (e) {
        // Check for the various File API support.
        if (window.File && window.FileReader && window.FileList && window.Blob) {
            // All the File APIs are supported. Doing job.
            if ($('#' + studyDetailsImportBtnId)[0].files.length > 0) {
                var reader = new FileReader();
                $(reader).on('load', function () {
                    var csvData = reader.result;
                    var result = $.parse(csvData, {
                        delimiter: csvDelimeter,
                        header: true,
                        dynamicTyping: true
                    });

                    if (result.errors.length == 0 && Array.prototype.slice.call(result.results.fields).indexOf('Data field') >= 0) {

                        wizardCommonModule.showYesNoDialog("This will overwrite current mapping. Are you sure?", "Perform import?", function () {
                            var resMap = {};
                            for (var i = 0; i < result.results.rows.length; i++) {
                                if (result.results.rows[i]['Data field'])
                                    resMap[result.results.rows[i]['Data field']] = result.results.rows[i];
                            }
                            var readCount = result.results.rows.length;
                            var importedCount = 0;
                            var mapRules = $("#" + scope.studyDetailsTableId).getGridParam('data');
                            for (var i = 0; i < mapRules.length; i++) {
                                var item = resMap[mapRules[i].dataField];
                                if (item) {
                                    importedCount++;
                                    mapRules[i].sourceData = item['Source column'];
                                    mapRules[i].decodingInfo = item['Decoding'] != "" ? item['Decoding'] : null;
                                    mapRules[i].defaultValue = item['Default value'] != "" ? item['Default value'] : null;
                                    var aggrFunc = findAggrFunctionByNameAndHelperStr(item['Aggregation function']);
                                    mapRules[i].agrFunctions = aggrFunc ? aggrFunc.id : 1;
                                }
                                else {
                                    mapRules[i].sourceData = null;
                                    mapRules[i].decodingInfo = null;
                                    mapRules[i].defaultValue = null;
                                    mapRules[i].agrFunctions = 1;

                                }

                            }
                            ;
                            if (importedCount == 0) {
                                wizardCommonModule.showWarningDialog('No mapping data for this file rule was found in CSV file');
                            }
                            else {
                                fillDataValues(mapRules, $("#" + scope.studyDetailsTableId));
                                toggleChangesControls(true);
                                wizardCommonModule.showInfoDialog('Done', 'Imported ' + importedCount + ' of ' + readCount + ' column rules from CSV file');
                            }
                        });
                    }
                    else
                        wizardCommonModule.showErrorDialog('Error', 'Error parsing CSV file');
                });
                reader.readAsText($('#' + studyDetailsImportBtnId)[0].files[0]);
            }
        } else {
            wizardCommonModule.showWarningDialog('The File APIs are not fully supported in this browser.');
        }
        $('#' + studyDetailsImportBtnId).val('');
    };

    var exportStudyMappings = function (rowId, toAdmin) {
        var slaveTable = $("#" + scope.studyDetailsTableId);
        var recs = slaveTable.jqGrid('getGridParam', 'reccount');
        if (recs == 0) {
            return false;
        }
        ;
        var mapRules = slaveTable.getGridParam('data');
        var data = [];
        for (var i = 0; i < mapRules.length; i++) {
            var item = {};
            item['Data field'] = mapRules[i].dataField;
            item['Source column'] = mapRules[i].sourceData;
            item['Decoding'] = mapRules[i].decodingInfo;
            item['Default value'] = mapRules[i].defaultValue;
            var aggrFunc = findAggrFunctionById(mapRules[i].agrFunctions);
            if (aggrFunc)
                item['Aggregation function'] = getAggrFuncNameAndHelperStr(aggrFunc.name, aggrFunc.helper);
            for (var f in item) {
                if (item[f] == null) item[f] = "";
            }
            data.push(item);
        }
        ;
        var csvContent = "data:text/csv;charset=utf-8,";
        csvContent += csvHeader.join(csvDelimeter);
        data.forEach(function (infoArray, index) {
            var dataString = '';
            for (var i = 0; i < csvHeader.length; i++) {
                var item = infoArray[csvHeader[i]];
                if (item && (item.indexOf(',') >= 0 || item.indexOf('"') >= 0))
                    item = '"' + item.replace('"', '""') + '"';
                dataString += item + ((i < csvHeader.length - 1) ? csvDelimeter : '');
            }
            csvContent += index < data.length ? '\n' + dataString : dataString;

        });
        var encodedUri = encodeURI(csvContent);
        var link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        var fileName = scope.studyWizard.workflow.selectedStudy.studyCode +
            '_' + $("#" + scope.studyMainTableId).jqGrid('getCell', scope.selectedFileRuleId, "typeId") + '.csv';

        link.setAttribute("download", fileName);
        link.click();
    };
    /** Slave mapping table def end**/


    var onDeleteMappingCallback = function () {
        var mainTable = $('#' + scope.studyMainTableId);
        var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
        if (selRowId == null) {
            wizardCommonModule.showInfoDialog('Message', 'There is no row selected.');
            return;
        }
        var selType = mainTable.jqGrid('getCell', selRowId, 'typeId');
        var message = 'Are you sure you wish to delete the mapping for ' + selType + '?';
        wizardCommonModule.showYesNoDialog('Delete data source information', message, function () {
                var callBack = function () {
                    ajaxModule.sendAjaxRequestSimpleParams("study-setup-delete-file-rule", {id: mainTable.jqGrid("getCell", selRowId, "id")},
                        {showDialog: true}, function (result) {
                            scope.completeMappings = result;
                            scope.updateCheckList(result);
                            var mainTable = $('#' + scope.studyMainTableId);
                            var selType = mainTable.jqGrid('getCell', selRowId, 'typeId');
                            var isPatientInfo = scope.isPatientInfo(selType);
                            var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
                                name: selType,
                                fileRuleId: selRowId
                            });

                            if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo && !hasEnoughMandatoryMappings) {
                                scope.studyWizard.workflow.selectedStudy.studyEnabled = false;
                            }
                            $('#' + scope.studyMainTableId).jqGrid('delRowData', selRowId);
                            $('#' + scope.studyDetailsTableId).jqGrid('clearGridData');
                            scope.disableButtons();
                            scope.resizeMappingStep();

                            // SAS value decoding information
                            if (selType == getFileDescriptionById(18)) {
                                scope.initDecodingValues();
                            }
                        });
                };

                var mainTable = $('#' + scope.studyMainTableId);
                var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
                var selType = mainTable.jqGrid('getCell', selRowId, 'typeId');

                var isPatientInfo = scope.isPatientInfo(selType);

                var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
                    name: selType,
                    fileRuleId: selRowId
                });

                if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo && !hasEnoughMandatoryMappings) {
                    var message = "These changes to ACUITY setup for clinical study " + scope.studyWizard.workflow.selectedStudy.studyCode +
                        " will result in deletion of visualisation module " +
                        scope.studyWizard.toStringInstances(scope.studyWizard.workflow.instances) +
                        " because the required study data will not be available. Do you wish to continue? ";
                    wizardCommonModule.showYesNoDialog("ACUITY", message, callBack, null, 450);
                } else {
                    callBack();
                }
            },
            function () {

            });
    };

    var initUpDownButtons = function () {
        $("#" + mainStudyMoveUpBtnId + ", #" + mainStudyMoveDownBtnId).hide();//RCT-2276
        /*$("#" + mainStudyMoveUpBtnId + ", #" + mainStudyMoveDownBtnId).on("click", function(){
         var table = $("#" + scope.studyMainTableId);
         var selectedRow = $("tr.ui-state-highlight.jqgrow",table);
         if(this.id == mainStudyMoveUpBtnId){
         if(selectedRow.index() == 1)
         return;
         selectedRow.insertBefore(selectedRow.prev());
         }else {
         if(selectedRow.index() == table.getGridParam("reccount"))
         return;
         selectedRow.insertAfter(selectedRow.next());
         }
         });*/
    };

    var addHelpText = function () {
        $('a[href=#colTypeOfInfo]').attr('title', $('#colTypeOfInfo').val());
        $('a[href=#colDataSource]').attr('title', $('#colDataSource').val());
        $('a[href=#colAcuityEnabled]').attr('title', $('#colAcuityEnabled').val());
        $('a[href=#colLastEdited]').attr('title', $('#colLastEdited').val());
        $('a[href=#colDate]').attr('title', $('#colDate').val());
        $('a[href=#colDataField]').attr('title', $('#colDataField').val());
        $('a[href=#colSourceData]').attr('title', $('#colSourceData').val());
        $('a[href=#colDecodingInfo]').attr('title', $('#colDecodingInfo').val());
        $('a[href=#colDefaultValue]').attr('title', $('#colDefaultValue').val());
        $('a[href=#colAggregation]').attr('title', $('#colAggregation').val());
        $('a[href=#colMandatory]').attr('title', $('#colMandatory').val());
        $(".help").tipTip();
    };

    this.decodingValues = [];
    var getDecodingValuesAjax;

    this.initDecodingValues = function () {
        scope.decodingValues = [];
        if (getDecodingValuesAjax) {
            getDecodingValuesAjax.abort();
            getDecodingValuesAjax = null;
        }

        getDecodingValuesAjax = $.getJSON('api/source/get_decoding_values', function (data) {
            getDecodingValuesAjax = null;
            scope.decodingValues = data;
            if ($("#studyDetailsTableWrapper").is(":visible")) {
                var values = $("#" + scope.studyDetailsTableId).jqGrid('getGridParam', 'data');
                fillDataValues(values, $("#" + scope.studyDetailsTableId));
            }
        });
    }


    /** Add/Edit mapping Dialog**/
    var showMappingDialog = function (mode) {
        $("#" + studyDlgId).data('mode', mode).dialog({
            title: 'Define data source information',
            modal: true,
            resizable: false,
            width: 'auto',
            //height : 280,
            open: function () {
                addMappingDialogFill();
            },
            create: function () {
                addStudyDialogInit();
            }

        });
    };

    var addMappingDialogFill = function () {
        var mode = $("#" + studyDlgId).data('mode');
        if (mode == 'add-mapping') {
            resetStudyDialog();
            if (studyWizard.workflow.selectedStudy.primarySource) {
                $("#study-file-prediction").removeAttr('disabled');
            }
            if ($("#study-no-file").is(":checked")) {
                $("#mappingsPrediction").attr("disabled", 'disabled');
            }
            else {
                $("#mappingsPrediction").removeAttr("disabled");
            }
        } else if (mode == 'edit-mapping') {
            // fill dialog values
            resetStudyDialog();
            fillEditStudyDialog();
        }

        if ($("#study-no-file").is(":checked") || $("#study-file-prediction").is(":checked")) {
            $("#" + studyBrowseBtnId).attr('disabled', 'disabled').addClass('disabled');
            $("#studyStorageText").attr('disabled', 'disabled');
        } else if ($("#study-source-remote").is(":checked")) {
            $("#studyStorageText").removeAttr('disabled');
        }
    };

    var resetStudyDialog = function () {
        $("#studyDataType").multiselect('uncheckAll');
        $("#studyDataType").multiselect('enable');
        $("#studyDataType").multiselect('refresh');
        $("#study-no-file").attr('checked', true);
//    	$("#mappingsPrediction").attr('checked', true);
        $("#isSdtm").attr('checked', false);

        $("#mappingsPrediction").attr("disabled", 'disabled');
        $("#study-file-prediction").attr('disabled', 'disabled');

        $("#studyStorageText").val('');
//    	$("#studyAcuityEnabled").attr('checked', true);
//    	$("#studyUpdateAcuityEnabled").attr('checked', true);
        $("#fileRuleId").val("");
    };

    var fillEditStudyDialog = function () {
        var mainTable = $("#" + scope.studyMainTableId);
        if (scope.selectedFileRuleId == null)
            return;
        var dlgData = mainTable.getRowData(scope.selectedFileRuleId);

        $("#studyDataType").multiselect("widget").find("li span").each(function () {
            var tmp = $(this).text();
            if (dlgData.typeId == tmp)
                this.click();
        });

        $("#studyDataType").multiselect('disable');
        $("#studyDataType").multiselect('refresh');

        if (dlgData.dataSourceLocation != "") {
            $("input[name='dataSourceLocationType'][value='studySourceRemote']").attr('checked', 'checked');
            $("#studyStorageText").removeAttr('disabled');
            $("#studyStorageText").val($.trim(dlgData.dataSourceLocation));
        } else {
            $("input[name='dataSourceLocationType'][value='studyNoFile']").attr('checked', 'checked');
            $("#studyStorageText").attr('disabled', 'disabled');
        }

        if (dlgData.studyAcuityEnabled == "No") {
            $("#studyAcuityEnabled").attr('checked', false);
        }

        $("#isSdtm").attr('checked', dlgData.fileStandard == 'SDTM');

        $("#studyStorageText").val(dlgData.dataSourceLocation);
        $("#fileRuleId").val(dlgData.id);
    };

    var addStudyDialogInit = function () {
        $('#' + studyDlgCancelBtnId).on("click", function () {
            $("#" + studyDlgId).dialog("close");
        });
        $('#' + studyDlgOkBtnId).on("click", function () {
            var mode = $("#" + studyDlgId).data('mode');
            if ($("#studyDataType").multiselect('getChecked').length == 0) {
                wizardCommonModule.showWarningDialog("Data type must be selected");
                return;
            }

            function runMapping(mode) {
                if (mode == 'add-mapping') {
                    addMapping();
                } else if (mode == 'edit-mapping') {
                    editMapping();
                }
            }

            if ($("#study-source-remote").is(":checked")) {
                var file = $.trim($("#studyStorageText").val());

                if (!file) {
                    wizardCommonModule.showWarningDialog("Source remote path is empty");
                    return;
                }
                $("#studyStorageText").val(file);

                if (!wizardCommonModule.endsWithCsvSas(file)) {
                        wizardCommonModule.showWarningDialog("This file type is currently not supported.<br/>" +
                            "ACUITY can currently parse .csv or .sas7bdat files only.<br/>");
                }

                $.getJSON('api/source/check_file_exists', {path: file}, function (data) {
                    if (data.obj) {
                        runMapping(mode);
                    } else {
                        wizardCommonModule.showYesNoDialog("File not found",
                            "ACUITY could not find a file at the path provided or the path is out of storage scope. Do you wish to continue?",
                            function () {
                                runMapping(mode);
                            }, null, 450);
                    }
                });
            } else {
                runMapping(mode);
            }
        });

        $("input[name='dataSourceLocationType']").on('change', function () {
            if ($("#study-no-file").is(":checked") || $("#study-file-prediction").is(":checked")) {
                $("#" + studyBrowseBtnId).attr('disabled', 'disabled').addClass('disabled');
                $("#studyStorageText").attr('disabled', 'disabled');
            } else {
                $("#" + studyBrowseBtnId).removeAttr('disabled', 'disabled').removeClass('disabled');
                $("#studyStorageText").removeAttr('disabled');
                if (scope.studyWizard.editStudyStep.study.primarySource) {
                    $("#studyStorageText").val(scope.studyWizard.editStudyStep.study.primarySource);
                } else {
                    var locations = $("#" + scope.studyMainTableId).getCol('dataSourceLocation');
                    for (var i = locations.length - 1; locations && i >= 0; i--) {
                        var location = $(locations[i]).text() ? $(locations[i]).text() : locations[i];
                        if (location) {
                            $("#studyStorageText").val(location.substring(0, location.lastIndexOf('\\') + 1));
                            break;
                        }
                    }
                }
                $("#studyStorageText").focus();
            }
            var mode = $("#" + studyDlgId).data('mode');

            if ($("#study-no-file").is(":checked") || mode == 'edit-mapping') {
                $("#mappingsPrediction").attr("disabled", 'disabled');
            } else {
                $("#mappingsPrediction").removeAttr('disabled');
            }
        });

        fillDlgFileSections();
    };

    var addMapping = function () {
        var formData = $("#fileRuleForm").serializeObject();
        if (formData.isSdtm) {
            formData.fileStandard = 'SDTM';
        }

        ajaxModule.sendAjaxRequest("study-setup-save-file-rule", JSON.stringify(formData), {showDialog: true}, function (result) {
            var mainTable = $("#" + scope.studyMainTableId);
            mainTable.jqGrid('addRowData', result.id, result);
            mainTable.setSelection(result.id, true);
            $("#" + studyDlgId).dialog("close");

            // SAS value decoding information
            if (result.typeId == 18) {
                scope.initDecodingValues();
            }
            loadMappingStatuses();
        });
    };

    var editMapping = function () {
        var rowId = scope.selectedFileRuleId;
        var callBack = function () {
            var rowId = scope.selectedFileRuleId;
            var formData = $("#fileRuleForm").serializeObject();
            if (formData.isSdtm) {
                formData.fileStandard = 'SDTM';
            }
            ajaxModule.sendAjaxRequest("study-setup-save-file-rule", JSON.stringify(formData), {showDialog: true}, function (result) {
                var mainTable = $("#" + scope.studyMainTableId);
                mainTable.jqGrid('setRowData', rowId, result);
                var selType = mainTable.jqGrid('getCell', rowId, 'typeId');
                var isPatientInfo = scope.isPatientInfo(selType);

                var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
                    name: selType,
                    fileRuleId: rowId
                });
                if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo) {
                    scope.studyWizard.workflow.selectedStudy.studyEnabled = false;
                }
                $("#" + studyDlgId).dialog("close");

                // SAS value decoding information
                if (result.typeId == 18) {
                    scope.initDecodingValues();
                }
                selectMappingRowHandler(rowId);
            });
        };
        if ($("#studyAcuityEnabled").is(":checked")) {
            callBack();
            return;
        }
        var mainTable = $("#" + scope.studyMainTableId);
        var selType = mainTable.jqGrid('getCell', rowId, 'typeId');
        var isPatientInfo = scope.isPatientInfo(selType);

        var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
            name: selType,
            fileRuleId: rowId
        });
        if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo && !hasEnoughMandatoryMappings) {
            var message = "These changes to ACUITY setup for clinical study " + scope.studyWizard.workflow.selectedStudy.studyCode +
                " will result in deletion of visualisation module " + scope.studyWizard.toStringInstances(scope.studyWizard.workflow.instances) +
                " because the required study data will not be available. Do you wish to continue? ";
            wizardCommonModule.showYesNoDialog("ACUITY", message, callBack, null, 450);
        } else {
            callBack();
        }
    };

    var fillDlgFileSections = function () {
        if (!scope.fileSections) {
            return;
        }
        for (var i = 0; i < scope.fileSections.length; i++) {
            var group = scope.fileSections[i];
            var opgroup = $('<optgroup label="' + group.name + '">');
            for (var j = 0; j < group.fileDescriptions.length; j++) {
                var entity = group.fileDescriptions[j];
                var option = $(' <option value="' + entity.id + '">' + entity.displayName + '</option>');
                opgroup.append(option);
            }
            $("#studyDataType").append(opgroup);
        }
        $("#studyDataType").multiselect({
            header: false,
            height: 500,
            selectedList: 1,
            multiple: false,
            noneSelectedText: "Select an Option",
            create: function () {
                $("#studyDataType").multiselect('uncheckAll');
                $("#studyDataType").multiselect('refresh');

            },
            beforeoptgrouptoggle: function () {
                return false;
            }
        });
    };
    /** Add/Edit mapping Dialog end**/



    var getFileDescriptionById = function (id) {
        var result = null;
        $.each(scope.fileSections, function (index, value) {
            $.each(value.fileDescriptions, function (i, val) {
                if (val.id == id) {
                    result = val.displayName;
                    return false;
                }
            });
            return !result;
        });
        return result;
    };

    var checkMappings = {
        getMappedColumns: function (mappings, fieldName) {
            var values = _.find(mappings.mapRules, {dataField: fieldName});
            if (values && values.sourceData) {
                return _.words(values.sourceData, /[^, ]+/g);
            } else if (values && values.defaultValue) {
                return _.words(values.defaultValue, /[^, ]+/g);
            }
            return null;
        },

        checkGradeChangesHasSameLength: function (mappings) {
            var gradeChanges = checkMappings.getMappedColumns(mappings, 'AE severity grade changes');
            var gradeChangeDates = checkMappings.getMappedColumns(mappings, 'AE severity change dates (mm/dd/yyyy)');

            if ((gradeChanges || gradeChangeDates) && !(gradeChanges && gradeChanges && gradeChanges.length == gradeChangeDates.length)) {
                throw 'The same number of CTC grade changes and CTC grade change dates must be mapped in order to upload multiple CTC grade changes';
            }
        },

        checkThat_IpDrug_ActionTaken_Causality_haveTheSameLength: function (mappings) {
            var drugs = checkMappings.getMappedColumns(mappings, 'Investigational drug names');
            var initialATs = checkMappings.getMappedColumns(mappings, 'Initial action taken for investigational drugs');
            var causalities = checkMappings.getMappedColumns(mappings, 'Causality for investigational drugs');
            var changedATs = checkMappings.getMappedColumns(mappings, 'Action taken due to severity grade changes for investigational drugs');
            var gradeChanges = checkMappings.getMappedColumns(mappings, 'AE severity grade changes');

            if (initialATs && !(drugs && drugs.length == initialATs.length)) {
                throw 'Investigational drug names and Initial action taken are not aligned';
            }

            if (causalities && !(drugs && drugs.length == causalities.length)) {
                throw 'Investigational drug names and Causality are not aligned';
            }

            if (changedATs && !(drugs && gradeChanges && (changedATs.length == drugs.length * gradeChanges.length))) {
                throw 'Investigational drug names and action taken are not aligned';
            }
        },

        checkThat_AdDrug_ActionTaken_Causality_haveTheSameLength: function (mappings) {
            var drugs = checkMappings.getMappedColumns(mappings, 'Additional drug names');
            var initialATs = checkMappings.getMappedColumns(mappings, 'Initial action taken for additional drugs');
            var causalities = checkMappings.getMappedColumns(mappings, 'Causality for additional drugs');
            var changedATs = checkMappings.getMappedColumns(mappings, 'Action taken due to severity grade changes for additional drugs');
            var gradeChanges = checkMappings.getMappedColumns(mappings, 'AE severity grade changes');

            if (initialATs && !(drugs && drugs.length == initialATs.length)) {
                throw 'Additional drug names and Initial action taken are not aligned';
            }

            if (causalities && !(drugs && drugs.length == causalities.length)) {
                throw 'Additional drug names and Causality are not aligned';
            }

            if (changedATs && !(drugs && gradeChanges && (changedATs.length == drugs.length * gradeChanges.length))) {
                throw 'Additional drug names and action taken are not aligned';
            }

        },

        checkForEmpty: function (mappings) {
            var rules = mappings.mapRules;

            for (var i = 0; i < rules.length; i++) {
                var rule = rules[i];
                if (rule.dataField.indexOf("[") > -1 || !rule.dataField) {
                    throw 'Not all mapping rules are valid';
                }
            }
        },

        checkMandatory: function (mappings) {
            var rules = mappings.mapRules;

            for (var i = 0; i < rules.length; i++) {
                var rule = rules[i];
                if (rule.mandatory && !(rule.sourceData || rule.defaultValue)) {
                    throw 'Not all mandatory columns are aligned';
                }
            }
        },

        checkAll: function (mappings) {
            checkMappings.checkForEmpty(mappings);
            checkMappings.checkMandatory(mappings);
            checkMappings.checkGradeChangesHasSameLength(mappings);
            checkMappings.checkThat_IpDrug_ActionTaken_Causality_haveTheSameLength(mappings);
            checkMappings.checkThat_AdDrug_ActionTaken_Causality_haveTheSameLength(mappings);
        }
    };

    var toggleChangesControls = function (enabled) {
        var btns = $("#" + revertMappingsBtnId + ", #" + studyDetailsSaveBtnId);
        if (enabled) {
            btns.removeAttr('disabled', 'disabled').removeClass('disabled');
        } else {
            btns.attr('disabled', 'disabled').addClass('disabled');
        }
    };

    this.resizeMappingStep = function () {
        scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_MAPPING_STEP_INX);
        scope.studyWizard.innerSplit.position(scope.studyWizard.innerSplit.width() * scope.studyWizard.innerSplit.lastPercentPos);
        $("#" + scope.studyMainTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
        $("#" + scope.studyDetailsTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
    };

    this.disableButtons = function () {
        /*
         var mainStudyMoveUpBtnElement = $("#" + mainStudyMoveUpBtnId);
         var mainStudyMoveDownBtnElement = $("#" + mainStudyMoveDownBtnId);
         */
        var deleteMFileRuleBtnElement = $("#" + deleteMFileRuleBtnId);
        var studyEditBtnElement = $("#" + studyEditBtnId);
        var revertMappingsBtnElement = $("#" + revertMappingsBtnId);
        /*
         mainStudyMoveUpBtnElement.attr('disabled', 'disabled').addClass('disabled');
         mainStudyMoveDownBtnElement.attr('disabled', 'disabled').addClass('disabled');
         */
        deleteMFileRuleBtnElement.attr('disabled', 'disabled').addClass('disabled');
        studyEditBtnElement.attr('disabled', 'disabled').addClass('disabled');
        revertMappingsBtnElement.attr('disabled', 'disabled').addClass('disabled');
    };

    this.updateCheckList = function (data) {
        $(".check-list .label").css("color", "#888");
        $(".statusIcon").removeClass('warningStatus successStatus').addClass('errorStatus');

        var decodingInfoMapped = false;
        var counter = 0;
        for (var i = 0; i < data.length; i++) {
            var item = $("li:has(span:contains(" + data[i].name + "))");
            if (data[i].name == "SAS value decoding information") {
                decodingInfoMapped = true;
            }
            var status = item.find(".statusIcon");
            if (data[i].ready && data[i].enabled) {
                status.addClass('successStatus');
                item.find(".label").css("color", 'rgb(0, 128, 0)');
                counter++;
            } else {
                if (data[i].ready) {
                    status.addClass("warningStatus");
                    counter++;
                }
            }
        }
        var slaveTable = $("#" + scope.studyDetailsTableId);
        if (decodingInfoMapped) {
            slaveTable.setColProp('decodingInfo', {editable: true});
        } else {
            slaveTable.setColProp('decodingInfo', {editable: false});
        }

        studyWizard.changeStepText(studyWizard.STUDY_MAPPING_STEP_INX, "Create mappings to the source data(" + counter + ")");
    };

    this.checkIfEnoughMandatoryMappings = function (mappings, mappingToExclude) {
        return _(mappings).filter(function (mapping) {
                return mapping.enabled && mapping.name == mappingToExclude.name && mapping.fileRuleId != mappingToExclude.fileRuleId;
            }).size() > 0;
    };

    this.isPatientInfo = function (displayName) {
        for (var i = 0; i < fileSections.length; i++) {
            if (fileSections[i].name != "Mandatory patient information") {
                continue;
            }
            var descriptions = fileSections[i].fileDescriptions;
            for (var k = 0; k < descriptions.length; k++) {
                if (descriptions[k].displayName == displayName) {
                    return true;
                }
            }
        }
        return false;
    };

    this.revertValueChanges = function () {
        var mainTable = $('#' + scope.studyMainTableId);
        var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
        mainTable.jqGrid('setSelection', selRowId);
        scope.resizeMappingStep();
    };

    this.saveValueChanges = function (nextRowId, toAdmin) {
        var slaveTable = $("#" + scope.studyDetailsTableId);
        var recs = slaveTable.jqGrid('getGridParam', 'reccount');
        if (recs == 0) {
            return false;
        }
        var sendData = {};
        sendData.studyAcuityEnabled = $("#studyAcuityEnabled").is(':checked');
        sendData.fileRuleId = $("#" + scope.studyMainTableId).jqGrid('getCell', scope.selectedFileRuleId, "id");
        sendData.mapRules = slaveTable.getGridParam('data');

        for (var i = 0; i < sendData.mapRules.length; i++) {
            if (!sendData.mapRules[i].sourceData) {
                sendData.mapRules[i].sourceData = '';
            }
        }

        try {
            checkMappings.checkAll(sendData);
        } catch (error) {
            wizardCommonModule.showWarningDialog(error, 500);
            return;
        }

        console.log(sendData);

        slaveTable.jqGrid('saveCell', scope.lastStudyDetailsRow, scope.lastStudyDetailsCol);
        ajaxModule.sendAjaxRequest("study-setup-save-mapping-rules", JSON.stringify(sendData), {
            showDialog: true
        }, function (result) {

            if ($("#studyAcuityEnabled").is(':checked')) {
                $("#mainStudyTable").jqGrid('setRowData', selectedRowId, {studyAcuityEnabled: "Yes"});
            }
            else {
                $("#mainStudyTable").jqGrid('setRowData', selectedRowId, {studyAcuityEnabled: "No"});
            }
            $('#' + scope.studyDetailsTableId + ' td.edit-cell').removeClass("edit-cell");
            scope.completeMappings = result;
            scope.updateCheckList(result);
            var mainTable = $("#" + scope.studyMainTableId);
            var dataType = mainTable.jqGrid('getCell', scope.selectedFileRuleId, "typeId");
            var isPatientInfo = scope.isPatientInfo(dataType);
            var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
                name: dataType,
                fileRuleId: scope.selectedFileRuleId
            });
            if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo && !hasEnoughMandatoryMappings) {
                scope.studyWizard.workflow.selectedStudy.studyEnabled = false;
            }
            wizardCommonModule.showInfoDialog("Save successful", "The mapping parameters for <strong>" + dataType + "</strong> have been saved");
            toggleChangesControls(false);
            if (nextRowId) {
                mainTable.jqGrid('setSelection', nextRowId);
            }
            toggleChangesControls(false);
            if (toAdmin) {
                window.location = "admin";
            }
        });
    };

    this.showCheckListSplitter = function () {
        $('#leftInnerPane').removeClass("left_panel");
        $('#rightInnerPane').removeClass("right_panel");
        $('#rightInnerPane').addClass("right_inner_panel");
        $('#rightInnerPane').show();
    };

    this.setFileSections = function (fileSections) {
        if (scope.fileSections != null) {
            return;
        }
        scope.fileSections = fileSections;

        var checkListDataDiv = $('#checkListContent');
        for (var i = 0; i < scope.fileSections.length; i++) {
            var group = scope.fileSections[i];
            var checkListUl = $('<ul class="check-list"></ul>');
            var spanGroup = $('<span class="sum-header">' + group.name + '</span>');
            checkListUl.append(spanGroup);
            for (var j = 0; j < group.fileDescriptions.length; j++) {
                var entity = group.fileDescriptions[j];
                checkListUl.append('<li><span id="status-' + entity.id + '" class="statusIcon"></span>' +
                    '<span id="span-name-' + entity.id + '" class="label" style="padding-left:5px;">' + entity.displayName + '</span></li>');
            }
            checkListDataDiv.append(checkListUl);
        }
    };

    this.setAggregationFunctions = function (aggregationFunctions) {
        scope.aggregationFunctions = aggregationFunctions;
        if (!scope.aggregationFunctions)
            return;
        var data = {};
        for (var i = 0; i < scope.aggregationFunctions.length; i++) {
            var element = scope.aggregationFunctions[i];
            data[element.id] = element.description;
        }
        $("#" + scope.studyDetailsTableId).setColProp('agrFunctions', {
            editoptions: {
                value: data
            }
        });
    };

    this.setDefaultTypes = function (defaultTypes) {
        scope.defaultTypes = defaultTypes;
    };

    this.resizeTable = function () {
        $("#" + scope.studyMainTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
    };

    init();
};

/* public methods ------*/
MappingStudyStep.prototype = {

    startStep: function () {
        var scope = this;
        var rightPaneWidth = '' + ($('#rightPane').width() - 300);
        scope.studyWizard.innerSplit = $('#rightPane').split({
            orientation: 'vertical', limit: 320, position: rightPaneWidth, onDrag: function () {
                scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_MAPPING_STEP_INX);
                $("#" + scope.studyMainTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
                $("#" + scope.studyDetailsTableId).setGridWidth($("#leftInnerPane").width() - 50, true);
            }
        });

        scope.showCheckListSplitter();
        //static data
        scope.setFileSections(fileSections);

        if (scope.completeMappings) {
            scope.updateCheckList(scope.completeMappings);
            //scope.completeMappings = null;
        }
        scope.setAggregationFunctions(aggregationFunctions);
        scope.setDefaultTypes(defaultTypes);
        scope.resizeTable();
        //grids dialogs
        $("#" + scope.studyDetailsTableId).jqGrid('clearGridData');
        $("#" + scope.studyMainTableId).jqGrid('resetSelection');
        scope.disableButtons();

        if (scope.studyWizard.editStudyStep.study.primarySource) {
            $('#mainStudyAutoMapButton').removeAttr('disabled');
        } else {
            $('#mainStudyAutoMapButton').attr('disabled', 'disabled');
        }

        ajaxModule.sendAjaxRequestWithoutParam('study-setup-get-file-rules', {showDialog: true}, function (result) {
            $("#" + scope.studyMainTableId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
            scope.resizeMappingStep();
            scope.initDecodingValues();
        });
    },

    leaveStep: function (toStep) {
        var scope = this;
        var slaveTable = $("#" + scope.studyDetailsTableId);
        var isSlaveEditable = wizardCommonModule.isSlaveTableEditable(slaveTable);
        if (isSlaveEditable) {
            wizardCommonModule.showSaveDialog("Are You Sure?", "You have unsaved data-mapping edits." +
                "Do you wish to save or discard these changes?", function (toStep) {
                scope.checkStudyCompleteAndSaveValues(null, false);
            }, function (toStep) {
                scope.revertValueChanges();
            }, function (toStep) {
                // do nothing
            }, toStep);

            return false;
        }
        $("#" + scope.studyDetailsTableId).jqGrid('clearGridData');
        $("#" + scope.studyMainTableId).jqGrid('clearGridData');
        return true;
    },

    canGoToAdmin: function () {
        var scope = this;
        var slaveTable = $("#" + scope.studyDetailsTableId);
        var isSlaveEditable = wizardCommonModule.isSlaveTableEditable(slaveTable);
        if (isSlaveEditable) {
            wizardCommonModule.showSaveDialog("ACUITY", "Save changes?",
                function () {
                    scope.checkStudyCompleteAndSaveValues(null, true);
                },
                function () {
                    window.location = "admin";
                });
            return false;
        }
        return true;
    },

    checkStudyCompleteAndSaveValues: function (rowId, toAdmin) {
        var scope = this;
        var slaveTable = $("#" + scope.studyDetailsTableId);
        slaveTable.jqGrid('saveCell', scope.lastStudyDetailsRow, scope.lastStudyDetailsCol);
        var ids = slaveTable.jqGrid('getDataIDs');

        var existWrongSourceData = false;
        var existWrongDecodingInfo = false;
        for (var i = 0; i < ids.length; i++) {
            var rowId = ids[i];

            var sourceData = slaveTable.jqGrid('getCell', rowId, 'sourceData');
            if (sourceData && scope.sourceColumnNames.length > 0) {
                var values = sourceData.split(", ");
                for (var j = 0; j < values.length; j++) {
                    var value = values[j];
                    if (scope.sourceColumnNames.indexOf(value) == -1) {
                        existWrongSourceData = true;
                        break;
                    }
                }
                if (existWrongSourceData) {
                    break;
                }
            }

            var decodingInfo = slaveTable.jqGrid('getCell', rowId, 'decodingInfo');
            if (decodingInfo && scope.decodingValues.length > 0 && scope.decodingValues.indexOf(decodingInfo) == -1) {
                existWrongDecodingInfo = true;
                break;
            }
        }
        if (existWrongSourceData || existWrongDecodingInfo) {
            var message = existWrongSourceData
                ? "Some of the column headers you have entered are not found in the source data file." +
            " Do you wish to continue?"
                : "Some of the decoding values you have entered are not found in the SAS value" +
            " decoding information source file. Do you want to continue?";
            wizardCommonModule.showYesNoDialog("ACUITY", message, function (rowId) {
                scope.saveValueChanges(rowId, toAdmin);
            }, scope.revertValueChanges, 450, rowId);
            return;
        }

        var mainTable = $('#' + scope.studyMainTableId);
        var selRowId = mainTable.jqGrid('getGridParam', 'selrow');
        var selType = mainTable.jqGrid('getCell', selRowId, 'typeId');
        var isPatientInfo = scope.isPatientInfo(selType);

        var hasEnoughMandatoryMappings = scope.checkIfEnoughMandatoryMappings(scope.completeMappings, {
            name: selType,
            fileRuleId: rowId
        });
        if (scope.studyWizard.workflow.selectedStudy.studyEnabled && isPatientInfo && !hasEnoughMandatoryMappings) {

            var notWillBeMapped = false;
            for (var i = 0; i < ids.length; i++) {
                var rowId = ids[i];
                var mandatory = slaveTable.jqGrid('getCell', rowId, 'mandatory');
                if (mandatory != 'true') {
                    continue;
                }
                var sourceData = slaveTable.jqGrid('getCell', rowId, 'sourceData');
                var defaultValue = slaveTable.jqGrid('getCell', rowId, 'defaultValue');
                if ($.trim(sourceData).length == 0 && $.trim(defaultValue).length == 0) {
                    notWillBeMapped = true;
                    break;
                }
            }
            if (notWillBeMapped) {
                var message = "These changes to ACUITY setup for clinical study " + scope.studyWizard.workflow.selectedStudy.studyCode +
                    " will result in deletion of visualisation module " + mainClinicalModule.toStringInstances(scope.studyWizard.workflow.instances) +
                    " because the required study data will not be available. Do you wish to continue? ";
                wizardCommonModule.showYesNoDialog("ACUITY", message, function (rowId) {
                    scope.saveValueChanges(rowId, toAdmin);
                }, scope.revertValueChanges, 450, rowId);
            } else {
                scope.saveValueChanges(rowId, toAdmin);
            }
        } else {
            scope.saveValueChanges(rowId, toAdmin);
        }
    }
};
