var SearchProgrammeStep = function (programmeWizard) {

    this.currentTablePage = 0;
    this.selectedDrugProgramme = 0;
    this.programmeWizard = programmeWizard;

    this.searchResultId = 'searchResultTable';
    this.searchInputId = 'input-step-1';
    this.searchInputEmptyText = 'Enter drug ID';

    var searchBtnId = 'search-btn-step-1';
    var listAllLinkId = 'link-all-projects';
    var addProgrammeBlockId = "add-new-programme-block";
    var addNewProgrammeId = "smartBtnNextAddProgramme";
    var scope = this;
    var selectedDrugProgramme = null;


    /* private methods ------*/
    var init = function () {
        searchFilterModule.updateSearchInput(scope.searchInputId, scope.searchInputEmptyText, searchBtnId);
        createSearchResultTable();
        $("#" + addProgrammeBlockId).hide();

        var studiesGrid = $("#" + scope.searchResultId);
        studiesGrid.setGridWidth($("#rightPane").width() - 50, true);
        $(window).bind('resize', function () {
            studiesGrid.setGridWidth($("#rightPane").width() - 50, true);
        });

        var searchInput = $('#' + scope.searchInputId);

        if (programmeSearchResult != null && programmeSearchResult.length > 0) {
            scope.selectedDrugProgramme = programmeSearchResult[0].drugId;
            fillSearchResultTable(programmeSearchResult);
            searchInput.val(programmeSearchResult[0].drugId);
            studiesGrid.jqGrid('setSelection', programmeSearchResult[0].drugId);
        }

        $('#' + listAllLinkId).on("click", function () {
            searchInput.val("");
            searchFilterModule.updateSearchInputText(scope.searchInputId, scope.searchInputEmptyText);
            scope.searchProjects(false, true);
        });

        $('#' + searchBtnId).on("click", function () {
            scope.searchProjects(false, true);
        });

        $('#' + addNewProgrammeId).on("click", function () {
            programmeWizard.loadProjectWizard(programmeWizard.workflow, programmeWizard.PROJECT_EDIT_STEP_INX, true);
            // programmeWizard.goToStep(programmeWizard.PROJECT_EDIT_STEP_INX);
        });

        addHelpText();
    };

    var goToSummary = function (drugProgramme) {
        scope.selectedDrugProgramme = drugProgramme;
        scope.selectProject(programmeWizard.SUMMARY_STEP_INX, true);
    };

    var linkFormatter = function (cellvalue, options, rowObject) {
        if (cellvalue && cellvalue == true) {
            var rowId = options.rowId;
            return "<a id='link-" + wizardCommonModule.htmlEscape(rowId) + "' style='color: #FFF; cursor: pointer; font-weight: bold;font-style: italic;'>ACUITY Summary</a>";
        }
        return "";
    };

    this.ajaxAsyncSearchProjectsByText = function (text, updateResults) {
        ajaxModule.sendQueryAsyncAjaxRequest("programme-setup/programme-run-search", "programme-setup/programme-query-status", "programme-setup/programme-cancel-query", {showDialog: true}, {query: text}, function () {
            ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-get-search-result", {query: text}, {showDialog: true}, function (result) {
                onSearchCompleted(result, updateResults);
            });
        });
    };

    this.ajaxSearchProjectsByText = function (text, updateResults) {
        ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-search", {query: text}, {showDialog: true}, function (result) {
            onSearchCompleted(result, updateResults);
        });
    };

    var onSearchCompleted = function (result, updateTableResult) {
        if (result.length == 0) {
            $("#" + addProgrammeBlockId).show();
            $("#" + scope.searchResultId).jqGrid('clearGridData');
            programmeWizard.addedProgrammeMode = true;
        } else {
            $("#" + addProgrammeBlockId).hide();
            programmeWizard.addedProgrammeMode = false;
            fillSearchResultTable(result);
            if (updateTableResult) {
                var grid = $("#" + scope.searchResultId);
                grid.setGridParam({page: scope.currentTablePage});
                grid.trigger("reloadGrid");
                grid.jqGrid('setSelection', programmeWizard.workflow.selectedProject.drugId);
            }
        }
        programmeWizard.recalculateSplit(programmeWizard.PROJECT_SEARCH_STEP_INX);
    };

    var fillSearchResultTable = function (result) {
        $("#" + scope.searchResultId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
    };

    var addHelpText = function () {
        $('a[href=#colRadio]').attr('title', $('#colRadio').val());
        $('a[href=#colDrugID]').attr('title', $('#colDrugID').val());
        $('a[href=#colNStudies]').attr('title', $('#colNStudies').val());
        $('a[href=#colNAcuityStudies]').attr('title', $('#colNAcuityStudies').val());
        $('a[href=#colAcuityEnabled]').attr('title', $('#colAcuityEnabled').val());
        $('a[href=#colAction]').attr('title', $('#colAction').val());
        $(".help").tipTip();
    };

    var getColumnIndexByName = function (grid, columnName) {
        var cm = grid.jqGrid('getGridParam', 'colModel'), i, l = cm.length;
        for (i = 0; i < l; i++) {
            if (cm[i].name === columnName) {
                return i; // return the index
            }
        }
        return -1;
    };


    var createSearchResultTable = function () {

        $("#" + scope.searchResultId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['<a href="#colRadio" class="help">?</a>',
                    'Drug ID <a href="#colDrugID" class="help">?</a>',
                    'Number of studies <a href="#colNStudies" class="help">?</a>',
                    'Number of ACUITY enabled studies <a href="#colNAcuityStudies" class="help">?</a>',
                    'ACUITY enabled? <a href="#colAcuityEnabled" class="help">?</a>',
                    'Action <a href="#colAction" class="help">?</a>'
                ],
                colModel: [
                    {
                        name: 'selected', index: 'selected', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<input id='" + rowObject.id + "'type='radio' name='prgStep1' value='" + wizardCommonModule.htmlEscape(rowObject.drugId) + "'></input>";
                        }
                    },
                    {name: 'drugId', index: 'drugId', width: 150, key: true, align: "center"},
                    {name: 'totalStudyCount', index: 'totalStudyCount', width: 150, align: "center"},
                    {
                        name: 'numberOfAcuityEnabledStudies',
                        index: 'numberOfAcuityEnabledStudies',
                        width: 150,
                        align: "center"
                    },
                    {
                        name: 'acuityEnabled', index: 'acuityEnabled', width: 150,
                        formatter: function (cellvalue, options, rowObject) {
                            var result = "No";
                            if (cellvalue && cellvalue != undefined) {
                                if (cellvalue == true) {
                                    result = "Yes";
                                }
                            }
                            return result;
                        },
                        align: "center"
                    },
                    {name: 'acuityEnabled', index: 'acuityEnabled', width: 150, formatter: linkFormatter, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                shrinkToFit: true,
                rowNum: 15,
                pager: '#projectResultPager',
                onSelectRow: function (id) {
                    var radioBtn = $("input[name='prgStep1'][value='" + id + "']");
                    radioBtn.trigger('click');
                    if (programmeWizard.workflow != null && programmeWizard.workflow.selectedProject && programmeWizard.workflow.selectedProject.drugId == id) {
                        editProgrammeWorkflow = null;
                    }
                },
                onPaging: function (pgButton) {
                    $('#smartBtnNext').addClass("disabled");
                },
                gridComplete: function () {
                    $("#" + scope.searchResultId).setGridWidth($("#rightPane").width() - 50, true);
                },
                loadComplete: function (data) {
                    var grid = $("#" + scope.searchResultId);
                    var btnNext = $('#smartBtnNext');
                    $("input[name='prgStep1']").change(function () {
                        grid.setSelection(this.value, true);
                        if ($(this).is(':checked')) {
                            btnNext.removeClass("disabled");
                            scope.selectedDrugProgramme = this.value;
                        } else {
                            btnNext.addClass("disabled");
                        }
                    });

                    if (scope.selectedDrugProgramme) {
                        grid.jqGrid('setSelection', scope.selectedDrugProgramme);
                        btnNext.removeClass("disabled");
                    } else {
                        btnNext.addClass("disabled");
                    }
                    
                    var iCol = getColumnIndexByName($(this), 'acuityEnabled');
                    $(this).find(">tbody>tr.jqgrow>td:nth-child(" + (iCol + 2) + ")").each(function () {
                        $(this).find("a").on("click", function (e) {
                            var drugProgramme = $(e.target).closest("tr.jqgrow").attr("id");
                            goToSummary(drugProgramme);
                        });
                    });
                },
                resizeStop: function (width, index) {
                    programmeWizard.recalculateSplit(programmeWizard.PROJECT_SEARCH_STEP_INX);
                }
            });
    };

    init();
};

/* public methods ------*/
SearchProgrammeStep.prototype = {
    onShowStep: function () {
        if ($("input[name='prgStep1']").is(':checked')) {
            $('#smartBtnNext').removeClass("disabled");
        } else {
            $('#smartBtnNext').addClass("disabled");
        }
        ajaxModule.sendAjaxRequestWithoutParam("programme-setup/programme-total-count", {showDialog: false}, function (result) {
            var linkAllProjects = $("#link-all-projects");
            linkAllProjects.text("List All(" + result + ")");
        });
    },

    selectProject: function (stepIndex, isSummaryLink) {
        var scope = this;
        scope.currentTablePage = $("#" + scope.searchResultId).getGridParam('page');
        if (scope.programmeWizard.workflow && scope.programmeWizard.workflow.selectedProject.drugId === scope.selectedDrugProgramme) {
            var currentStep = $('#' + scope.programmeWizard.wizardId).smartWizard('currentStep');
            if (isSummaryLink && currentStep != scope.programmeWizard.SUMMARY_STEP_INX) {
                scope.programmeWizard.goToStep(stepIndex);
            }
            return true;
        }

        ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-select", {drugProgramme: this.selectedDrugProgramme}, {showDialog: true}, function (result) {
            scope.programmeWizard.loadProjectWizard(result, stepIndex);
        });
        return false;
    },

    searchProjects: function (updateResults, asyncRequest) {
        var scope = this;
        var searchText = $('#' + this.searchInputId).val();
        searchText = searchText.replace(this.searchInputEmptyText, "");
        scope.selectedDrugProgramme = null;
        if (asyncRequest) {
            this.ajaxAsyncSearchProjectsByText(searchText, updateResults);
        } else {
            this.ajaxSearchProjectsByText(searchText, updateResults);
        }
    }
};
