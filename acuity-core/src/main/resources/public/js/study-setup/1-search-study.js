var SearchStudyStep = function (studyWizard) {

    var searchBtnId = 'search-btn-study-step-1';
    var listAllLinkId = 'study-link-all-projects';
    var addStudyBlockId = 'add-new-study-block';
    var smartBtnNextAddStudyId = 'smartBtnNextAddStudy';

    var currentTablePage = 0;

    var scope = this;

    this.studyWizard = studyWizard;
    this.selectedStudyCode = '';
    this.searchResultId = 'studySearchResultTable';
    this.searchInputId = 'input-study-step-1';
    this.searchInputEmptyText = 'Search for drug programme or dataset';

    var init = function () {
        searchFilterModule.updateSearchInput(scope.searchInputId, scope.searchInputEmptyText, searchBtnId);

        createSearchResultTable();

        $("#" + addStudyBlockId).hide();
        $("#" + scope.searchResultId).setGridWidth($("#leftInnerPane").width() - 50, true);
        $(window).bind('resize', function () {
            $("#" + scope.searchResultId).setGridWidth($("#leftInnerPane").width() - 50, true);
        });
        if (studyRulesSearchForEdit != null) {
            fillSearchResult(studyRulesSearchForEdit);
        }

        $('#' + listAllLinkId).on("click", function () {
            $('#' + scope.searchInputId).val("");
            searchFilterModule.updateSearchInputText(scope.searchInputId, scope.searchInputEmptyText);
            scope.searchStudies(false, true);
        });

        $('#' + searchBtnId).on("click", function () {
            scope.searchStudies(false, true);
        });

        addActionOnAddNewStudyBtn();
        addHelpText();
    };

    var addActionOnAddNewStudyBtn = function () {
        $('#' + smartBtnNextAddStudyId).on("click", function () {
            ajaxModule.sendAjaxRequestWithoutParam("study-setup-get-projects", {showDialog: true}, function (result) {
                if (result.length == 0) {
                    wizardCommonModule.showWarningDialog("There are no completed drug programmes!");
                } else {
                    scope.studyWizard.workflow = null;
                    scope.studyWizard.addedStudyMode = true;
                    scope.studyWizard.editStudyStep.study = null;

                    scope.studyWizard.editStudyStep.fillCompletedProjectList(result);
                    scope.studyWizard.loadStudyWizard(scope.studyWizard.workflow, scope.studyWizard.STUDY_EDIT_STEP_INX, false);
                }
            });
        });
    };

    var createSearchResultTable = function () {

        $("#" + scope.searchResultId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colModel: [
                    {
                        label: '', name: 'selected', index: 'selected', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<input type='radio' name='studySearchStep1' value='" + rowObject.studyCode + "'></input>";
                        }
                    },
                    {
                        label: '',
                        name: 'drugProgramme',
                        index: 'drugProgramme',
                        width: 150,
                        hidden: true,
                        align: "center"
                    },
                    {
                        label: 'Dataset identifier <a href="#colStudyID" class="help">?</a>',
                        name: 'studyCode',
                        index: 'studyCode',
                        width: 150,
                        align: "center",
                        key: true
                    },
                    {
                        label: 'Dataset name <a href="#colStudyName" class="help">?</a>',
                        name: 'studyName',
                        index: 'studyName',
                        width: 150,
                        align: "center"
                    },
                    {label: 'Study ID', name: 'clinicalStudyId', index: 'clinicalStudyId', width: 150, align: "center"},
                    {
                        label: 'Study name',
                        name: 'clinicalStudyName',
                        index: 'clinicalStudyName',
                        width: 150,
                        align: "center"
                    },
                    {
                        label: 'Phase <a href="#colPhase" class="help">?</a>',
                        name: 'phase',
                        index: 'phase',
                        width: 150,
                        align: "center"
                    },
                    {
                        label: 'Dataset setup status <a href="#colStudySetupStatus" class="help">?</a>',
                        name: 'status',
                        index: 'status',
                        width: 150,
                        align: "center",
                        formatter: statusFormatter,
                        unformat: unFormatStatus
                    }
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                shrinkToFit: true,
                rowNum: 15,
                pager: '#studyResultPager',
                onSelectRow: function (studyCode) {
                    var radioBtn = $("input[name='studySearchStep1'][value='" + studyCode + "']");
                    radioBtn.trigger('click');
                    if (studyWizard.workflow && studyWizard.workflow.selectedStudy && studyWizard.workflow.selectedStudy.studyCode != studyCode) {
                        editStudyWorkflow = null;
                    }
                },
                onPaging: function (pgButton) {
                    $('#smartBtnNext').addClass("disabled");
                },
                loadComplete: function () {
                    var grid = $("#" + scope.searchResultId);
                    $("input[name='studySearchStep1']").change(function () {
                        grid.setSelection(this.value, true);
                        if ($(this).is(':checked')) {
                            $('#smartBtnNext').removeClass("disabled");
                            scope.selectedStudyCode = this.value;
                        } else {
                            $('#smartBtnNext').addClass("disabled");
                        }
                    });
                    if (scope.selectedStudyCode) {
                        $('#smartBtnNext').removeClass("disabled");
                        grid.jqGrid('setSelection', scope.selectedStudyCode);
                    } else {
                        $('#smartBtnNext').addClass("disabled");
                    }
                },
                resizeStop: function (width, index) {
                    studyWizard.recalculateSplit(studyWizard.STUDY_SEARCH_STEP_INX);
                }
            });
    };

    var statusFormatter = function (val) {
        switch (val) {
            case 'incomplete':
                return '<span id="incomplete"><span class="progressSearchIcon">' +
                    '</span><span style="padding-left:5px;">Mapping in progress</span></span>';
                break;
            case 'readyToMap':
                return '<span id="readyToMap"><span class="readySearchIcon">' +
                    '</span><span style="padding-left:5px;">Ready to map</span></span>';
                break;
            case 'mapped':
                return '<span id="mapped" ><span class="inAcuitySearchIcon">' +
                    '</span><span style="padding-left:5px;">Previously mapped</span></span>';
                break;
            case 'notInAcuity':
                return '<span id="notInAcuity"><span class="crossSearchIcon">' +
                    '</span><span style="padding-left:5px;">Not added to ACUITY</span></span>';
                break;
        }
    };

    var unFormatStatus = function (cellvalue, options, cell) {
        var spanStatus = $('span', cell)[0].id;
        if (spanStatus && spanStatus != undefined) {
            return spanStatus;
        }
        return "";
    };

    var fillSearchResult = function (result) {
        $("#" + scope.searchResultId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
    };

    var addHelpText = function () {
        $('a[href=#colStudyID]').attr('title', $('#colStudyID').val());
        $('a[href=#colStudyName]').attr('title', $('#colStudyName').val());
        $('a[href=#colPhase]').attr('title', $('#colPhase').val());
        $('a[href=#colStudySetupStatus]').attr('title', $('#colStudySetupStatus').val());
        $(".help").tipTip();
    };

    var onCompleteSearch = function (result, updateTableResult) {
        var grid = $("#" + scope.searchResultId);
        if (result.length == 0) {
            $("#" + addStudyBlockId).show();
            studyWizard.addedStudyMode = true;
            grid.jqGrid('clearGridData');
        } else {
            grid.jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
            $("#" + addStudyBlockId).hide();
            studyWizard.addedStudyMode = false;
            if (updateTableResult) {
                grid.setGridParam({page: scope.currentTablePage});
                grid.trigger("reloadGrid");
                var currentPage = 1;
                while (!_.find(grid.jqGrid('getDataIDs'), function (studyCode) {
                    return studyCode == studyWizard.workflow.selectedStudy.studyCode
                })) {
                    currentPage = currentPage + 1;
                    grid.setGridParam({page: currentPage});
                    grid.trigger("reloadGrid");
                }
                grid.jqGrid('setSelection', studyWizard.workflow.selectedStudy.studyCode, true);
            }
        }
        studyWizard.recalculateSplit(studyWizard.STUDY_SEARCH_STEP_INX);
    };

    this.ajaxAsyncSearchStudiesByText = function (text, updateResults) {
        ajaxModule.sendQueryAsyncAjaxRequest("study-run-search", "study-query-status", "study-cancel-query", {showDialog: true}, {query: text}, function () {
            ajaxModule.sendAjaxRequestSimpleParams("study-get-search-result", {query: text}, {showDialog: true}, function (result) {
                onCompleteSearch(result, updateResults);
            });
        });
    };

    this.ajaxSearchStudiesByText = function (text, updateResults) {
        ajaxModule.sendAjaxRequestSimpleParams("study-setup-search-studies", {query: text}, {showDialog: true}, function (result) {
            onCompleteSearch(result, updateResults);
        });
    };

    this.showNotInACUITYStudyDlg = function (message) {
        var studyNotInACUITYDlg = $("#studyNotInACUITYDlg");
        studyNotInACUITYDlg.dialog({
            modal: true,
            width: 450,
            minHeight: 50,
            resizable: true,
            open: function () {
                $("#studyNotInACUITYDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));
            },
            create: function () {
                $("#studyNotInACUITYDlgOkBtn").on("click", function () {
                    $("#studyNotInACUITYDlg").dialog("close");
                });
                studyNotInACUITYDlg.data("uiDialog")._title = function (title) {
                    title.html(this.options.title);
                };
                studyNotInACUITYDlg.dialog('option', 'title', '<span class="ui-icon ui-icon-alert"></span> Not In ACUITY');
            }
        });
    };

    this.goToProgrammeWizard = function (drugProgramme) {
        $("#drugId").val(drugProgramme);
        $("#submitEditProgrammeActionId").trigger("click");
    };

    init();
};

/**
 public methods */

SearchStudyStep.prototype = {
    startStep: function () {
        var scope = this;
        var searchResult = $("#" + scope.searchResultId);
        searchResult.setGridWidth($("#rightPane").width() - 50, true);
        searchResult.setGridWidth($("#leftInnerPane").width() - 50, true);
        ajaxModule.sendAjaxRequestWithoutParam("study-setup-total-count", {showDialog: false}, function (result) {
            $("#study-link-all-projects").text("List All(" + result + ")");
        });
    },

    searchStudies: function (updateResults, asyncRequest) {
        var scope = this;
        var searchText = $('#' + scope.searchInputId).val();
        searchText = searchText.replace(scope.searchInputEmptyText, "");
        scope.selectedStudyCode = null;
        if (asyncRequest) {
            scope.ajaxAsyncSearchStudiesByText(searchText, updateResults);
        } else {
            scope.ajaxSearchStudiesByText(searchText, updateResults);
        }
    },

    selectStudy: function () {
        var scope = this;
        var selectStudyCode = scope.selectedStudyCode;
        var searchTable = $("#" + scope.searchResultId);
        this.currentTablePage = searchTable.getGridParam('page');
        if (scope.studyWizard.workflow &&
            scope.studyWizard.workflow.selectedStudy &&
            scope.studyWizard.workflow.selectedStudy.studyCode == selectStudyCode)
            return true;
        var selectedStudyCode = searchTable.jqGrid('getCell', selectStudyCode, "studyCode");
        var enabledStudyStatus = searchTable.jqGrid('getCell', selectStudyCode, "status");
        if (enabledStudyStatus == 'notInAcuity' || $.trim(enabledStudyStatus).length == 0) {
            var drugProgramme = searchTable.jqGrid('getCell', selectStudyCode, "drugProgramme");
            var link = "<a id='link-" + wizardCommonModule.htmlEscape(drugProgramme) + "' style='color: #000; cursor: pointer;font-weight: bold;font-style: italic;'>Drug programme setup for " + drugProgramme + "</a>";
            $(document).on('click', '#link-' + wizardCommonModule.htmlEscape(drugProgramme) + '', function () {
                scope.goToProgrammeWizard(wizardCommonModule.htmlEscape(drugProgramme));
            });
            scope.showNotInACUITYStudyDlg("Cannot continue: The selected clinical study has not been enabled during drug programme setup." +
                "Please visit the " + link + " where the programme can be completed");
            return false;
        }
        ajaxModule.sendAjaxRequestSimpleParams("study-setup-select-study", {studyCode: selectedStudyCode}, {showDialog: true}, function (result) {
            scope.studyWizard.loadStudyWizard(result, scope.studyWizard.STUDY_SUMMARY_STEP_INX);
        });
        return false;
    }
};
