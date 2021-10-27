var ProgrammeWizard = function () {
    /**
     wizard steps numbers
     **/
    this.PROJECT_SEARCH_STEP_INX = 0;
    this.PROJECT_EDIT_STEP_INX = 1;
    this.GROUP_STEP_INX = 2;
    this.SUMMARY_STEP_INX = 3;

    var scope = this;
    this.wizardId = "wizard";
    /**
     wizard steps headers
     **/
    this.PROJECT_SEARCH_STEP_INX_HEADER = "Select the drug programme to enable with ACUITY";
    this.PROJECT_SEARCH_STEP_INX_SUB_HEADER = "Here you should search for " +
        "the drug programme you wish to enable with ACUITY using a drug identifier.";
    this.PROJECT_EDIT_STEP_HEADER = " Programme Parameters";
    this.PROJECT_EDIT_STEP_SUB_HEADER = "Using the padlock icon to allow editing," +
        " please provide contact names for the Medical Science Director" +
        " who will be the primary administrator for this drug project " +
        "and the name of the data owner (who controls data access requests).";
    this.PROJECT_GROUPINGS_STEP_HEADER = " Custom Groups";
    this.PROJECT_GROUPINGS_STEP_SUB_HEADER = "Here you can enter and edit 'Custom Groupings' " +
        "that can be applied to ACUITY visualisations. Custom Groupings are used to group sets of adverse events " +
        "(by their preferred term) or types of lab measurement (by a standard name) " +
        "that may be of particular interest in this drug programme. You may enter the groups manually, " +
        "or upload a file containing this information. ";
    this.SUMMARY_STEP_HEADER = " programme - ACUITY summary";
    this.SUMMARY_STEP_SUB_HEADER = 'Here you can review the information provided about the drug programme. ' +
        'Edits can be made to this information by clicking "Back" to the desired setup page,' +
        'or using the "view/edit" links provided below. When you are done, click "Finish".';

    this.searchProgrammeStep = new SearchProgrammeStep(this);
    this.editProgrammeStep = new EditProgrammeStep(this);
    this.groupingsProgrammeStep = new GroupingsProgrammeStep(this);
    this.summaryProgrammeStep = new SummaryProgrammeStep(this);
    this.project = null;
    this.workflow = null;
    this.addedProgrammeMode = false;
    this.programmeSplitter = null;

    /**
     private methods  */

    var init = function () {
        var wizard = $('#' + scope.wizardId);

        wizardCommonModule.showWaitingDialog();

        try {
            wizard.smartWizard({
                transitionEffect: 'none',
                navContainer: "#leftPane",
                stepsContainer: ".stepContainer",
                keyNavigation: false,
                backButtonSupport: false,
                useURLhash: false,
                showStepURLhash: false,
                theme: 'acuity',
                toolbarSettings: {
                    toolbarPosition: 'top',
                    nextButtonTemplate: "<button id='smartBtnNext' class='sw-btn sw-btn-next' type='button'></button>",
                    previousButtonTemplate: "<button id='smartBtnPrevious' class='sw-btn sw-btn-prev' type='button'></button>",
                    toolbarExtraButtons: [
                        $("<button id='smartBtnFinish' class='sw-btn sw-btn-finish' type='button'>Finish</button>")
                            .on('click', onFinishCallback),
                        $("<a id='smartBtnAdminPage' class='sw-btn buttonAdmin' type='button'>Admin home</a>")
                            .attr('href','#'),
                    ],
                    toolbarButtonPosition: 'inline',
                },
                lang: {  // Language variables
                    next: 'Next   ►',
                    previous: '◄   Back'
                },
            })
            .on('showStep', onShowStepCallback)
            .on('leaveStep', onLeaveStepCallback);
        } finally {
            wizard.removeClass("wizard-loading");
            wizardCommonModule.closeWaitingDialog();
        }

        // call the event handler manually, since the event handler is bound after 
        // SmartWizard constructor and first "showStep" call is performed in constructor
        onShowStepCallback(null, null, scope.PROJECT_SEARCH_STEP_INX);

        $('#' + scope.wizardId + " .sw-toolbar").append("<div class='wizard-header'></div>");
        $('#' + scope.wizardId + " .sw-toolbar").append("<div class='wizard-step-info'></div>");

        wizardCommonModule.setHeader(scope.PROJECT_SEARCH_STEP_INX_HEADER);
        wizardCommonModule.setStepSubHeading(scope.PROJECT_SEARCH_STEP_INX_SUB_HEADER);
        scope.programmeSplitter = wizard.split({
            orientation: 'vertical', limit: 300, position: '300', onDrag: function () {
                resizeSearchAndGroupingsStepsTable();
                var currentStep = $('#' + scope.wizardId).smartWizard('currentStep');
                wizardCommonModule.resizeWizard(currentStep, scope.wizardId);
            }
        });

        $('#smartBtnAdminPage').on("click", function () {
            window.location = "admin";
        });

        onAdminHeaderLinkClick();

        if (editProgrammeWorkflow != null) {
            $('#' + scope.searchProgrammeStep.searchInputId).val(editProgrammeWorkflow.selectedProject.drugId);
            scope.searchProgrammeStep.selectedDrugProgramme = editProgrammeWorkflow.selectedProject.drugId;
            scope.loadProjectWizard(editProgrammeWorkflow, scope.SUMMARY_STEP_INX);
        }
        //     fix for IE
        if ($.browser.msie) {
            $('.searchLink').css('float', 'none');
        }
        $(window).on("resize", function () {
            var currentStep = wizard.smartWizard('currentStep');
            wizardCommonModule.resizeWizard(currentStep, scope.wizardId);
        });
        resizeSearchAndGroupingsStepsTable();
    };

    var resizeSearchAndGroupingsStepsTable = function () {
        $("#" + scope.searchProgrammeStep.searchResultId).setGridWidth($("#rightPane").width() - 50, true);
        $("#" + scope.groupingsProgrammeStep.groupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
    };

    var onAdminHeaderLinkClick = function () {
        $('#adminMainPage').on("click", function () {
            var currentStep = $('#' + scope.wizardId).smartWizard('currentStep');
            if (currentStep === scope.PROJECT_EDIT_STEP_INX) {
                if (scope.editProgrammeStep.onBackStep(null, true)) {
                    goToPageByUrl("admin");
                }
                return false;
            } else if (currentStep === scope.GROUP_STEP_INX) {
                if (!scope.groupingsProgrammeStep.canGoToAdmin()) {
                    return false;
                }
            }
            goToPageByUrl("admin");
        });
    };

    var goToPageByUrl = function (url) {
        window.location = url;
    };

    var onLeaveStepCallback = function (e, selTab, fromStep, toStep) {
        if (fromStep == scope.PROJECT_SEARCH_STEP_INX && editProgrammeWorkflow == null && !scope.addedProgrammeMode) {
            return scope.searchProgrammeStep.selectProject(toStep);
        } else if (fromStep == scope.PROJECT_EDIT_STEP_INX && toStep != scope.PROJECT_SEARCH_STEP_INX) {
            if ($("#" + scope.editProgrammeStep.blockingId).hasClass("open")) {
                $("#" + scope.editProgrammeStep.blockingId).removeClass("open");
                $(".enabled").attr('disabled', 'disabled');
            }
            var validate = scope.editProgrammeStep.validateProject();
            if (!validate.status) {
                $("#" + scope.editProgrammeStep.blockingId).addClass("open");
                $(".enabled").removeAttr('disabled', 'disabled');
                return false;
            }
            return scope.editProgrammeStep.onNextStep(toStep);
        } else if (toStep === scope.PROJECT_SEARCH_STEP_INX && fromStep != scope.PROJECT_EDIT_STEP_INX) {
            scope.searchProgrammeStep.searchProjects(true, true);
        } else if (fromStep === scope.PROJECT_EDIT_STEP_INX && toStep === scope.PROJECT_SEARCH_STEP_INX) {
            return scope.editProgrammeStep.onBackStep(toStep);
        } else if (fromStep === scope.GROUP_STEP_INX) {
            scope.groupingsProgrammeStep.endStep();
        }
        return true;
    };

    var onShowStepCallback = function (e, selTab, currentStep) {
        scope.recalculateSplit();
        if (currentStep != scope.GROUP_STEP_INX && currentStep != scope.PROJECT_EDIT_STEP_INX) {
            $('#smartBtnNext').addClass("disabled");
        }
        if (currentStep === scope.PROJECT_SEARCH_STEP_INX) {
            wizardCommonModule.showFirstStepButtons();
            wizardCommonModule.setHeader(scope.PROJECT_SEARCH_STEP_INX_HEADER);
            wizardCommonModule.setStepSubHeading(scope.PROJECT_SEARCH_STEP_INX_SUB_HEADER);
            scope.searchProgrammeStep.onShowStep();
        } else if (currentStep === scope.PROJECT_EDIT_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.searchProgrammeStep.selectedDrugProgramme + scope.PROJECT_EDIT_STEP_HEADER);
            wizardCommonModule.setStepSubHeading(scope.PROJECT_EDIT_STEP_SUB_HEADER);
            scope.editProgrammeStep.startStep();
        } else if (currentStep === scope.GROUP_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.project.drugProgrammeName + scope.PROJECT_GROUPINGS_STEP_HEADER);
            wizardCommonModule.setStepSubHeading(scope.PROJECT_GROUPINGS_STEP_SUB_HEADER);
            scope.groupingsProgrammeStep.onShowStep();
        } else if (currentStep == scope.SUMMARY_STEP_INX) {
            wizardCommonModule.showFinishStepButtons();
            wizardCommonModule.setHeader(scope.project.drugProgrammeName + scope.SUMMARY_STEP_HEADER);
            wizardCommonModule.setStepSubHeading(scope.SUMMARY_STEP_SUB_HEADER);
            scope.summaryProgrammeStep.reloadProjectSummaryData();
        }
    };

    var onFinishCallback = function () {
        ajaxModule.sendAjaxRequestWithoutParam("programme-setup/programme-email", {showDialog: true}, function () {
            window.location = "admin"
        });
    };

    init();

};


/**
 public methods */

ProgrammeWizard.prototype = {

    changeStepText: function (stepIndex, text) {
        // increment stepIndex, because it is 0-based
        $("#text-step-" + (stepIndex + 1)).html(wizardCommonModule.htmlScriptEscape(text));
    },

    goToStep: function (stepIndex) {
        $('#' + this.wizardId).smartWizard('goToStep', stepIndex);
    },

    recalculateSplit: function (stepIndex) {
        if (typeof stepIndex === 'number') {
            wizardCommonModule.resizeWizard(stepIndex, this.wizardId);
        }
        if (this.programmeSplitter) {
            this.programmeSplitter.position(this.programmeSplitter.width() * this.programmeSplitter.lastPercentPos);
        }
    },

    setCurrentProject: function (project) {
        this.project = project;
    },

    eraseProjectWizard: function (stepIndex) {
        var wizard = $('#' + this.wizardId);
        if (typeof stepIndex === 'number') {
            wizard.smartWizard('stepState', stepIndex, 'enable');
            this.goToStep(stepIndex);
        }
        if (this.PROJECT_EDIT_STEP_INX > stepIndex) {
            wizard.smartWizard('stepState', this.PROJECT_EDIT_STEP_INX, 'initial');
        }
        wizard.smartWizard('stepState', this.GROUP_STEP_INX, 'initial');
        wizard.smartWizard('stepState', this.SUMMARY_STEP_INX, 'initial');
    },

    loadProjectWizard: function (workflow, stepIndex, isNew) {
        this.workflow = workflow || {selectedProject: {}};
        var wizard = $('#' + this.wizardId);

        this.setCurrentProject(this.workflow.selectedProject);
        var searchText = $('#' + this.searchProgrammeStep.searchInputId).val();
        this.editProgrammeStep.setCurrentProject(this.workflow.selectedProject, searchText);
        var drugId = this.workflow.selectedProject.drugId || searchText;
        var projectSearchstepIndexText = isNew || !drugId ? this.PROJECT_SEARCH_STEP_INX_HEADER : drugId + " setup selected";
        this.changeStepText(this.PROJECT_SEARCH_STEP_INX, projectSearchstepIndexText);
        this.groupingsProgrammeStep.GROUPING_COUNTER = 0;
        var groupstepIndexText = isNew ? this.PROJECT_GROUPINGS_STEP_HEADER : "(" + this.groupingsProgrammeStep.GROUPING_COUNTER + ")";
        this.changeStepText(this.GROUP_STEP_INX, groupstepIndexText);

        this.groupingsProgrammeStep.clearGroupingsTable();
        if (isNew) {
            // means new project (not in ACUITY)
            this.eraseProjectWizard(stepIndex);
            return;
        }

        wizard.smartWizard('stepState', this.PROJECT_EDIT_STEP_INX, 'enable');
        wizard.smartWizard('stepState', this.GROUP_STEP_INX, 'enable');
        if (this.workflow.groupings.length > 0) {
            this.groupingsProgrammeStep.fillGroupingsTable(this.workflow.groupings);
            this.changeStepText(this.GROUP_STEP_INX, "(" + this.groupingsProgrammeStep.GROUPING_COUNTER + ")");
        }

        wizard.smartWizard('stepState', this.SUMMARY_STEP_INX, 'enable');

        if (typeof stepIndex === 'number') {
            wizard.smartWizard('stepState', stepIndex, 'enable');
            this.goToStep(stepIndex);
        }
    }
};

jQuery(function ($) {
    new ProgrammeWizard();
});
