var StudyWizard = function () {

    var STUDY_SEARCH_STEP_SUB_HEADER = 'Here you should search for the clinical you wish to ' +
        'enable with ACUITY using either a drug or study/dataset identifier, click to choose from the list ' +
        'and click "Next" to continue with setup.';
    var STUDY_EDIT_STEP_SUB_HEADER = 'Using the padlock icon to allow editing, ' +
        'please complete the requested dataset details. The dates entered for dataset planned start ' +
        'and database lock may be putative. Most importantly, the blinding, randomisation ' +
        'and regulatory status of the dataset must either be entered correctly, or left at their default values.';
    var STUDY_MAPPING_STEP_SUB_HEADER = 'Here you should map source clinical data into ' +
        'the ACUITY system -  only mapped data will be available in ACUITY visualisations. ' +
        'The data checklist on the right shows what data has been successfully mapped: ' +
        'You should aim to map at least all the mandatory fields for types of mandatory patient information.';
    var STUDY_GROUPING_STEP_SUB_HEADER = "Here you can enter and edit 'Alternative Subject Groupings' " +
        "that can be applied to ACUITY visualisations. Alternative Subject Groupings are used to group sets of subjects " +
        "(by their unique subject identifiers) that may be of particular interest in this dataset. " +
        "You may enter the groups manually, or upload a file containing this information.";
    var STUDY_GROUPING_ANNOTATE_STEP_SUB_HEADER = "Here, subject groupings that have been uploaded can be assigned " +
        "to a specific type of cohort (e.g. dose cohort) and can be annotated with additional information, " +
        "such as the details of dosing regimen.";
    var STUDY_SUMMARY_STEP_SUB_HEADER = 'Here you can review the information provided about the dataset. ' +
        'Edits can be made to this information by clicking "Back" to the desired setup page, ' +
        'or using the "view/edit" links provided below. It is important to ensure all information is accurate. ' +
        'Primary drug project administrators should verify the information is correct using the disclaimer. ' +
        'When you are done, click "Finish".';

    var STUDY_ALT_LAB_CODES_STEP_SUB_HEADER = 'Here you should map labcode decoding information into the ACUITY system. ' +
        'Labcodes found in the mapped lab file in the previous step are shown, along with their corresponding AZ RAW values. ' +
        'If any labcodes are not recognised, the option to use AZ RAW values will be unabvailable. ' +
        'If any labcodes are left unmapped, they will not be translated in the ACUITY views.';
    var STUDY_EXCLUSION_VALUES_STEP_SUB_HEADER = 'Here you should specify specific values, if any, which need to be ' +
        'excluded from the visualisations to preserve data integrity. Some AEs or lab tests, for example, can ' +
        'reveal if a certain patient is receiving a study drug or a placebo. If this information should be hidden ' +
        'from study teams, please use the options below to exclude those values from ACUITY visualisations.';

    var STUDY_BASELINE_DRUGS_STEP_SUB_HEADER = 'Here you can edit the way that baseline values are calculated in ACUITY. ' +
        'By default, ACUITY will look for the date of first dose of any drug and use that date to calculate ' +
        'which results are the baseline values. However, in some cases baselines should be calculated on the basis ' +
        'of a subset of drugs, such as a set that excludes run-in compounds.';
    var STUDY_GROUPINGS_STEP_SUB_HEADER = "Here you can select the subject groupings and lab groupings " +
        "that will be used in the visualisations to help you analyse your data. Only groupings " +
        "that were previously created in the clinical study setup are available but you can revisit this setup " +
        "to create new groupings.";

    var PROJECT_GROUPINGS_STEP_HEADER = " Custom Groupings";
    var PROJECT_GROUPINGS_STEP_SUB_HEADER = "Here you can select the custom adverse event " +
        "and lab groupings that will be used in the visualisations to help you analyse your data. " +
        "Only groupings that were previously created in the drug programme setup are available " +
        "but you can revisit this setup to create new groupings. ";

    var CBIOPORTAL_GENOMIC_PROFILE_STEP_HEADER = "cBioPortal Genomic Profile";
    var CBIOPORTAL_GENOMIC_PROFILE_STEP_SUB_HEADER = "This page is only applicable if including genomic data" +
        " (for the Genomic Profile visualisation) and the link out to cBioPortal is required. " +
        "This information is required by cBioPortal to ensure the correct genomic profiles are displayed in both systems.";
    var scope = this;

    var stepsOrderIndex = 0;
    this.STUDY_SEARCH_STEP_INX = stepsOrderIndex++;
    this.STUDY_EDIT_STEP_INX = stepsOrderIndex++;
    this.STUDY_MAPPING_STEP_INX = stepsOrderIndex++;
    this.STUDY_BASELINE_DRUGS_STEP_INX = stepsOrderIndex++;
    this.STUDY_ALT_LAB_CODES_INX = stepsOrderIndex++;
    this.STUDY_EXCLUSION_VALUES_INX = stepsOrderIndex++;
    this.STUDY_GROUPING_STEP_INX = stepsOrderIndex++;
    this.STUDY_ANNOTATE_SUBJECT_GROUPINGS_STEP_INX = stepsOrderIndex++;
    this.PROJECT_GROUPINGS_STEP_INX = stepsOrderIndex++;
    this.STUDY_GROUPINGS_STEP_INX = stepsOrderIndex++;
    if (cBioPortalUrl) {
        this.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX = stepsOrderIndex++;
    } else {
        this.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX = null;
    }
    this.STUDY_SUMMARY_STEP_INX = stepsOrderIndex;

    this.workflow = null;
    this.innerSplit = null;
    this.addedStudyMode = false;
    this.studyLeftSplitter = null;
    this.wizardId = "clinical-wizard";
    this.wizard = undefined;

    this.searchStudyStep = new SearchStudyStep(this);
    this.editStudyStep = new EditStudyStep(this);
    this.mappingStudyStep = new MappingStudyStep(this);
    this.groupingsStudyStep = new GroupingsStudyStep(this);
    this.summaryStudyStep = new SummaryStudyStep(this);
    this.baselineDrugsStep = new BaselineDrugsStep(this);
    this.altLabCodesStudyStep = new AltLabCodesStep(this);
    this.exclusionValuesStudyStep = new ExclusionValuesStep(this);
    this.selectStudySubjectGroupingsStep = new StudyGroupingsStep(this);
    if (cBioPortalUrl) {
        this.cbioPortalGenomicProfileStep = new CBioPortalGenomicProfileStep(this);
    }
    this.selectProjectGroupingsStep = new ProjectGroupingsStep(this);

    var init = function () {
        wizardCommonModule.showWaitingDialog();

        try {
            scope.wizard = $('#' + scope.wizardId).smartWizard({
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
            $('#' + scope.wizardId).removeClass("wizard-loading");
            wizardCommonModule.closeWaitingDialog();
        }

        // call the event handler manually, since the event handler is bound after 
        // SmartWizard constructor and first "showStep" call is performed in constructor
        onShowStepCallback(null, null, scope.STUDY_SEARCH_STEP_INX);

        $('#' + scope.wizardId + " .sw-toolbar").append("<div class='wizard-header'>Clinical study dataset search</div>");
        $('#' + scope.wizardId + " .sw-toolbar").append("<div class='wizard-step-info'></div>");
        wizardCommonModule.setStepSubHeading(STUDY_SEARCH_STEP_SUB_HEADER);

        scope.studyLeftSplitter = $('#' + scope.wizardId).split({
            orientation: 'vertical', limit: 300, position: '300',
            onDrag: function () {
                resizeStepsTable();
                var currentStep = scope.wizard.smartWizard('currentStep');
                wizardCommonModule.resizeWizard(currentStep, scope.wizardId);
            }
        });

        $('#smartBtnAdminPage').on("click", function () {
            window.location = "admin";
        });

        onAdminHeaderLinkClick();

        // load existing study workflow
        if (editStudyWorkflow != null) {
            $('#' + scope.searchStudyStep.searchInputId).val(editStudyWorkflow.selectedStudy.studyCode);
            scope.loadStudyWizard(editStudyWorkflow, scope.STUDY_SUMMARY_STEP_INX, true);
        }

        //fix for IE
        if ($.browser.msie) {
            $('.searchLink').css('float', 'none');
        }

        $(window).on("resize", function () {
            var currentStep = scope.wizard.smartWizard('currentStep');
            wizardCommonModule.resizeWizard(currentStep, scope.wizardId);
        });

        resizeStepsTable();
    };

    var resizeStepsTable = function () {
        $("#" + scope.groupingsStudyStep.studyGroupingsTableId).setGridWidth($("#rightPane").width() - 50, true);
        $("#" + scope.searchStudyStep.searchResultId).setGridWidth($("#leftInnerPane").width() - 50, true);
        scope.summaryStudyStep.resizeSummaryTablesByRightPane();
    };

    var onAdminHeaderLinkClick = function () {
        $('#adminMainPage').on("click", function () {
            var currentStep = scope.wizard.smartWizard('currentStep');
            if (currentStep === scope.STUDY_EDIT_STEP_INX) {
                if (scope.editStudyStep.onBackStep(null, true)) {
                    window.location = "admin";
                }
                return false;
            } else if (currentStep === scope.STUDY_MAPPING_STEP_INX) {
                if (!scope.mappingStudyStep.canGoToAdmin()) {
                    return false;
                }
            } else if (currentStep === scope.STUDY_GROUPING_STEP_INX) {
                if (!scope.groupingsStudyStep.canGoToAdmin()) {
                    return false;
                }
            }
            else if (currentStep === scope.STUDY_EXCLUSION_VALUES_INX) {
                if (!scope.exclusionValuesStudyStep.canGoToAdmin()) {
                    return false;
                }
            }
            window.location = "admin";
        });
    };

    var onShowStepCallback = function (e, selTab, currentStep) {
        scope.recalculateSplit();
        resizeStepsTable();
        if (currentStep == scope.STUDY_SEARCH_STEP_INX) {
            wizardCommonModule.showFirstStepButtons();
            wizardCommonModule.setHeader('Clinical study dataset search');
            scope.searchStudyStep.startStep();
        } else if (currentStep == scope.STUDY_EDIT_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            scope.editStudyStep.startStep();
            wizardCommonModule.setStepSubHeading(STUDY_EDIT_STEP_SUB_HEADER);
        } else if (currentStep == scope.STUDY_MAPPING_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Data Mappings");
            wizardCommonModule.setStepSubHeading(STUDY_MAPPING_STEP_SUB_HEADER);
            scope.mappingStudyStep.startStep();
        } else if (currentStep == scope.STUDY_ALT_LAB_CODES_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Labcode decoding information");
            wizardCommonModule.setStepSubHeading(STUDY_ALT_LAB_CODES_STEP_SUB_HEADER);
            scope.altLabCodesStudyStep.startStep();
        } else if (currentStep == scope.STUDY_BASELINE_DRUGS_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Determine how baseline values are calculated");
            wizardCommonModule.setStepSubHeading(STUDY_BASELINE_DRUGS_STEP_SUB_HEADER);
            scope.baselineDrugsStep.startStep();
        } else if (currentStep == scope.STUDY_EXCLUSION_VALUES_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Exclusion values");
            wizardCommonModule.setStepSubHeading(STUDY_EXCLUSION_VALUES_STEP_SUB_HEADER);
            scope.exclusionValuesStudyStep.startStep();
        } else if (currentStep == scope.STUDY_GROUPING_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Alternative Subject Groupings");
            wizardCommonModule.setStepSubHeading(STUDY_GROUPING_STEP_SUB_HEADER);
            scope.groupingsStudyStep.startStep();
        } else if (currentStep == scope.STUDY_ANNOTATE_SUBJECT_GROUPINGS_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Annotate Subject Groupings");
            wizardCommonModule.setStepSubHeading(STUDY_GROUPING_ANNOTATE_STEP_SUB_HEADER);
            scope.groupingsStudyStep.startStep();
        } else if (currentStep == scope.PROJECT_GROUPINGS_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " " + PROJECT_GROUPINGS_STEP_HEADER);
            wizardCommonModule.setStepSubHeading(PROJECT_GROUPINGS_STEP_SUB_HEADER);
            scope.selectProjectGroupingsStep.startStep();
        } else if (currentStep == scope.STUDY_GROUPINGS_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Select Subject Groupings");
            wizardCommonModule.setStepSubHeading(STUDY_GROUPINGS_STEP_SUB_HEADER);
            scope.selectStudySubjectGroupingsStep.startStep();
        } else if (currentStep == scope.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX) {
            wizardCommonModule.showCommonStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " " + CBIOPORTAL_GENOMIC_PROFILE_STEP_HEADER);
            wizardCommonModule.setStepSubHeading(CBIOPORTAL_GENOMIC_PROFILE_STEP_SUB_HEADER);
            scope.cbioPortalGenomicProfileStep.startStep();
        } else if (currentStep == scope.STUDY_SUMMARY_STEP_INX) {
            wizardCommonModule.showFinishStepButtons();
            wizardCommonModule.setHeader(scope.workflow.selectedStudy.studyCode + " Dataset Summary");
            wizardCommonModule.setStepSubHeading(STUDY_SUMMARY_STEP_SUB_HEADER);
            scope.summaryStudyStep.createHistoryTable();
            scope.summaryStudyStep.showStudySummaryData();
        }

        if (currentStep != scope.STUDY_EDIT_STEP_INX && $("#" + scope.editStudyStep.blockingId).hasClass("open")) {
            $("#" + scope.editStudyStep.blockingId).removeClass("open");
            $(".enabled").attr('disabled', 'disabled');
        }
        if (currentStep != scope.STUDY_EDIT_STEP_INX &&
            currentStep != scope.STUDY_MAPPING_STEP_INX &&
            currentStep != scope.STUDY_GROUPING_STEP_INX &&
            currentStep != scope.STUDY_ALT_LAB_CODES_INX &&
            currentStep != scope.STUDY_BASELINE_DRUGS_STEP_INX &&
            currentStep != scope.STUDY_ANNOTATE_SUBJECT_GROUPINGS_STEP_INX &&
            currentStep != scope.STUDY_EXCLUSION_VALUES_INX &&
            currentStep != scope.PROJECT_GROUPINGS_STEP_INX &&
            currentStep != scope.STUDY_GROUPINGS_STEP_INX &&
            currentStep != scope.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX
        ) {
            $('#smartBtnNext').addClass("disabled");
        }
        if (currentStep != scope.STUDY_MAPPING_STEP_INX) {
            $('#rightInnerPane').removeClass("right_inner_panel");
            $('#rightInnerPane').hide();
        }
    };

    var onLeaveStepCallback = function (e, selTab, fromStep, toStep) {
        if (fromStep == scope.STUDY_EDIT_STEP_INX &&
            toStep != scope.STUDY_SEARCH_STEP_INX) {
            if ($("#" + scope.editStudyStep.blockingId).hasClass("open")) {
                $("#" + scope.editStudyStep.blockingId).removeClass("open");
                $(".enabled").attr('disabled', 'disabled');
            }
            var validate = scope.editStudyStep.validateStudy();
            if (!validate.status) {
                $("#" + scope.editStudyStep.blockingId).addClass("open");
                $(".enabled").removeAttr('disabled', 'disabled');
                if ($.trim(validate.message).length > 0) {
                    wizardCommonModule.showWarningDialog(validate.message);
                }
                return false;
            }
            return scope.editStudyStep.onNextStep(toStep);
        } else if (fromStep == scope.STUDY_EDIT_STEP_INX &&
            toStep == scope.STUDY_SEARCH_STEP_INX) {
            return scope.editStudyStep.onBackStep(toStep);
        }

        if (fromStep == scope.STUDY_SEARCH_STEP_INX && editStudyWorkflow == null
            && !scope.addedStudyMode) {
            return scope.searchStudyStep.selectStudy();
        }

        if (toStep == scope.STUDY_SEARCH_STEP_INX && !scope.addedStudyMode) {
            scope.searchStudyStep.searchStudies(true, true);
        }

        if (fromStep == scope.STUDY_GROUPING_STEP_INX) {
            return scope.groupingsStudyStep.leaveStep(toStep);
        }

        if (fromStep == scope.STUDY_ALT_LAB_CODES_INX) {
            return scope.altLabCodesStudyStep.leaveStep(toStep);
        }

        if (fromStep == scope.STUDY_BASELINE_DRUGS_STEP_INX) {
            return scope.baselineDrugsStep.leaveStep(toStep);
        }

        if (fromStep == scope.STUDY_EXCLUSION_VALUES_INX) {
            return scope.exclusionValuesStudyStep.leaveStep(toStep);
        }

        if (fromStep == scope.PROJECT_GROUPINGS_STEP_INX) {
            return scope.selectProjectGroupingsStep.leaveStep(toStep);
        }

        if (fromStep == scope.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX) {
            return scope.cbioPortalGenomicProfileStep.leaveStep(toStep);
        }

        if (fromStep == scope.STUDY_MAPPING_STEP_INX && scope.innerSplit) {
            if (!scope.mappingStudyStep.leaveStep(toStep)) {
                return false;
            }
            destroyMappingLeftSplitter();
        }

        return true;
    };

    var destroyMappingLeftSplitter = function () {
        $('#leftInnerPane').removeAttr('style');
        $('#rightInnerPane').removeAttr('style');
        scope.innerSplit.destroy();
        $('#rightInnerPane').removeClass("right_inner_panel");
        $('#rightInnerPane').hide();
        scope.recalculateSplit();
    };

    var onFinishCallback = function () {
        var confirm = $("#confirm").is(':checked');
        var callBack = function () {
            var callback2 = function () {
                var confirm = $("#confirm").is(':checked');
                ajaxModule.sendAjaxRequestSimpleParams("study-setup-finish", {
                    'confirm': !!confirm,
                    'upload': false
                }, {
                    showDialog: true
                }, function (result) {

                    function getFinishCallback(isUpload) {
                        return function () {
                            ajaxModule.sendAjaxRequestSimpleParams("study-setup-finish", {
                                'confirm': !!confirm,
                                'upload': !!isUpload
                            }, {
                                showDialog: true
                            }, function (result) {
                                window.location = "admin";
                            });
                        };
                    }

                    if (result.studyEnabled) {
                        wizardCommonModule.showYesNoDialog("ACUITY", "Do you wish to upload your dataset data into the system now?",
                            getFinishCallback(true),
                            function (result) {
                                window.location = "admin";
                            });
                    }
                    else {
                        window.location = "admin";
                    }
                });
            };
            callback2();
        };
        if (!confirm && scope.workflow.selectedStudy.studyEnabled) {
            var message = "These changes to ACUITY setup for dataset " + scope.workflow.selectedStudy.studyCode +
                " will result in deletion of visualisation module " + scope.toStringInstances(scope.workflow.instances) +
                " because the required dataset data will not be available. Do you wish to continue? ";
            wizardCommonModule.showYesNoDialog("ACUITY", message, callBack, null, 450);
        } else {
            callBack();
        }
    };

    this.geMappingCounter = function (data) {
        var counter = 0;
        for (var i = 0; i < data.length; i++) {
            if (data[i].ready) {
                counter++;
            }
        }
        return counter;
    };

    this.toStringInstances = function (array) {
        var arrayString = "";
        for (var i = 0; i < array.length; i++) {
            if (i == (array.length - 1)) {
                arrayString += array[i].name;
                continue;
            }
            arrayString += array[i].name + ", ";
        }
        return arrayString;
    };

    init();
};

/**
 public methods */

StudyWizard.prototype = {

    recalculateSplit: function (stepIndex) {
        var scope = this;
        if (typeof stepIndex === 'number') {
            wizardCommonModule.resizeWizard(stepIndex, scope.wizardId);
        }
        if (scope.studyLeftSplitter) {
            scope.studyLeftSplitter.position(scope.studyLeftSplitter.width() * scope.studyLeftSplitter.lastPercentPos);
        }
    },

    loadStudyWizard: function (workflow, goToStepIdx, editStudyMode) {
        this.workflow = workflow;
        var scope = this;
        var searchText = $('#' + scope.searchStudyStep.searchInputId).val();
        var study;
        if (this.workflow) {
            study = this.workflow.selectedStudy;
        }
        study = study || {studyCode: searchText, studyName: searchText};
        editStudyMode = editStudyMode || study.id;
        var isNew = !editStudyMode;

        if (this.workflow) {
            scope.editStudyStep.setCurrentStudy(this.workflow, searchText);
            this.mappingStudyStep.completeMappings = this.workflow.completeMappings;
            if (study.id) {
                $('#refToWebappGroupings').attr('href', '/app#/groupings/' + study.id);
            }
        }

        var studySearchstepIndexText = isNew || !study.id ? "Select the dataset to configure for ACUITY" : 'Dataset ' + study.studyName + " configuration selected";
        this.changeStepText(this.STUDY_SEARCH_STEP_INX, studySearchstepIndexText);

        var studyMappingstepIndexText = 'Create mappings to the source data' + (isNew ? "" : "(" + this.geMappingCounter(this.workflow.completeMappings) + ")");
        this.changeStepText(this.STUDY_MAPPING_STEP_INX, studyMappingstepIndexText);


        if (isNew && (!this.workflow || study.status == 'readyToMap')) {
            if (this.STUDY_EDIT_STEP_INX > goToStepIdx) {
                this.wizard.smartWizard('stepState', this.STUDY_EDIT_STEP_INX, 'initial');
            }
            this.wizard.smartWizard('stepState', this.STUDY_MAPPING_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_GROUPING_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_BASELINE_DRUGS_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_ALT_LAB_CODES_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_EXCLUSION_VALUES_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_ANNOTATE_SUBJECT_GROUPINGS_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_SUMMARY_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.PROJECT_GROUPINGS_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.STUDY_GROUPINGS_STEP_INX, 'initial');
            this.wizard.smartWizard('stepState', this.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX, 'initial');
            if (typeof goToStepIdx === 'number') {
                this.wizard.smartWizard('stepState', goToStepIdx, 'enable');
                this.goToStep(goToStepIdx);
            }
            return;
        }

        this.wizard.smartWizard('stepState', this.STUDY_EDIT_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_MAPPING_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_GROUPING_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_BASELINE_DRUGS_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_ALT_LAB_CODES_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_ANNOTATE_SUBJECT_GROUPINGS_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_EXCLUSION_VALUES_INX, 'enable');
        this.wizard.smartWizard('stepState', this.PROJECT_GROUPINGS_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.STUDY_GROUPINGS_STEP_INX, 'enable');
        this.wizard.smartWizard('stepState', this.CBIOPORTAL_GENOMIC_PROFILE_STEP_INX, 'enable');

        goToStepIdx = goToStepIdx || this.STUDY_SUMMARY_STEP_INX;
        if (typeof goToStepIdx === 'number') {
            this.wizard.smartWizard('stepState', goToStepIdx, 'enable');
            this.goToStep(goToStepIdx);
        }
    },

    changeStepText: function (stepIndex, text) {
        // increment stepIndex, because it is 0-based
        $("#study-text-step-" + (stepIndex + 1)).html(wizardCommonModule.htmlScriptEscape(text));
    },

    goToStep: function (index) {
        this.wizard.smartWizard('goToStep', index);
    }
};

jQuery(function ($) {
    new StudyWizard();
});
