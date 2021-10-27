var EditProgrammeStep = function (programmeWizard) {
    this.project = null;
    this.programmeWizard = programmeWizard;
    var searchInputId = 'input-step-2';
    var searchBtnId = 'search-btn-step-2';
    var blockingId = 'edit-block-inputs';
    this.canNextFlag = false;
    this.canBackFlag = false;
    var scope = this;

    /**
     private methods  */

    var init = function () {
        searchFilterModule.updateSearchInput(searchInputId,
            programmeWizard.searchProgrammeStep.searchInputEmptyText,
            searchBtnId);
        $('#' + searchBtnId).on("click", function () {
            var searchText = $('#' + searchInputId).val();
            $('#' + programmeWizard.searchProgrammeStep.searchInputId).val(searchText);

            programmeWizard.goToStep(programmeWizard.PROJECT_SEARCH_STEP_INX);
            programmeWizard.searchProgrammeStep.searchProjects(false, true);
        });

        $('#' + blockingId).on("click", function () {

            if ($(this).hasClass("open")) {
                if ($.trim($('#drugProgrammeName').val()).length == 0 ||
                    $.trim($('#drugProgrammeName').val()) != $.trim(scope.project.drugProgrammeName)) {
                    wizardCommonModule.showYesNoDialog('Attention!', 'You are about to change the Drug Programme Name. This change will be visible in VaHub, VA Security and the Admin UI.',
                        function () {
                            $('#' + blockingId).removeClass("open");
                            $(".enabled").attr('disabled', 'disabled');
                            scope.updateProject(scope.programmeWizard.PROJECT_EDIT_STEP_INX);
                        }, null, 450);
                } else {
                    $(this).removeClass("open");
                    $(".enabled").attr('disabled', 'disabled');
                }
            } else {
                $(this).addClass("open");
                $(".enabled").removeAttr('disabled', 'disabled');
            }
        });

        initValidator();
    };

    var initValidator = function () {
        $("#drugProgrammeId").keypress(function()  {
            return utilsModule.handleRegexValidation(event, $(this));
        });

        $("#drugProgrammeId").bind('paste', function()  {
            return utilsModule.handleRegexValidation(event, $(this));
        });

        jQuery.validator.setDefaults({
            debug: true,
            success: "valid",
            ignore: ".ignore"
        });

        var editForm = $("#form-edit-programme");
        editForm.validate({
            errorPlacement: function (error, element) {
                error.appendTo(element.parent("td").next("td"));
            },
            success: function (label, element) {
            },
            messages: {
                //chiefProgramme : "<< Required field",
                drugProgrammeId: "<< Required field",
                drugProgrammeName: "<< Required field",
                programmeEnabled: "<< Required field",
                studyNumberStudies: "<< Required field",
                studyNumberEnabledStudies: "<< Required field"
            }
        });
    };

    this.clearEnabledAllData = function (text) {
        $("#study-project-name").text(text);
        $("#drugProgrammeId").val("");
        $("#drugProgrammeName").val("");
        $("#programmeEnabled").val("");
        $("#studyNumberStudies").val("");
        $("#studyNumberEnabledStudies").val("");
        //$("#chiefProgramme").val("");
        $("#studyDeliveryModel").val("");
        $("#createDashboard").prop("checked", true);

        $(".enabled").each(function () {
            if ($(this).is(":disabled")) {
                $(this).removeAttr("disabled");
            }
        });
        $('#' + blockingId).addClass("open");
        $('#drugProgrammeId').removeAttr("disabled");
        $("#drugProgrammeName").removeAttr("disabled");
        $('#programmeEnabled').removeAttr("disabled");
        $('#studyNumberStudies').removeAttr("disabled");
        $('#studyNumberEnabledStudies').removeAttr("disabled");
    };

    this.updateProject = function (toStep) {
        scope.fillProgrammeParams();
        var sendData = scope.project;
        ajaxModule.sendAjaxRequest("programme-setup/programme-edit", JSON.stringify(sendData), {showDialog: false}, function (result) {
            if (toStep == scope.programmeWizard.PROJECT_SEARCH_STEP_INX) {
                programmeWizard.searchProgrammeStep.searchProjects(true);
            }
        });
    };

    this.closePadLock = function () {
        if ($("#" + blockingId).hasClass("open")) {
            $("#" + blockingId).removeClass("open");
            $(".enabled").attr('disabled', 'disabled');
        }
        $(".enabled").each(function () {
            var id = this.id;
            $("label[for='" + id + "']").hide();
        });
    };

    this.fillProgrammeParams = function () {
        if (!scope.project) {
            scope.project = {};
        }
        if (programmeWizard.addedProgrammeMode) {
            scope.project.drugId = $("#drugProgrammeId").val();
            scope.project.id = null;
            scope.project.acuityEnabled = $("#programmeEnabled").val() === "Yes" ? true : false;
            scope.project.totalStudyCount = $("#studyNumberStudies").val();
            scope.project.numberOfAcuityEnabledStudies = $("#studyNumberEnabledStudies").val();
        }

        //scope.project.admin = $("#chiefProgramme").val();
        scope.project.drugProgrammeName = $("#drugProgrammeName").val();
        scope.project.createDashboard = $("#createDashboard").prop("checked");
        scope.project.aeSeverityType = $("#aeSeverityType").val();
    };

    this.confirmDrugProgrammeNameChange = function (toStep) {
        if ($.trim($('#drugProgrammeName').val()).length == 0 ||
            $.trim($('#drugProgrammeName').val()) != $.trim(scope.project.drugProgrammeName)) {
            wizardCommonModule.showYesNoDialog('Attention!', 'You are about to change the Drug Programme Name. This change will be visible in VaHub, VA Security and the Admin UI.',
                function () {
                    scope.closePadLock();
                    scope.updateProject(toStep);
                    scope.programmeWizard.loadProjectWizard(scope.programmeWizard.workflow, toStep);
                }, function () {
                    if ($("#" + blockingId).hasClass("open")) {
                        $(".enabled").removeAttr('disabled', 'disabled');
                    }
                }, 450);
            return false;
        }
        scope.updateProject(toStep);
        return true;
    };

    init();

};

/* public methods ------*/
EditProgrammeStep.prototype = {

    startStep: function () {
        if (this.programmeWizard.addedProgrammeMode) {
            wizardCommonModule.setHeader("New Programme Parameters");
            this.clearEnabledAllData("New Programme Parameters");
            $('.hiddenOnAddProgramme').hide();
            $("#drugProgrammeId").val($('#' + this.programmeWizard.searchProgrammeStep.searchInputId).val());
            $('#smartBtnNext').removeClass("disabled");
        } else {
            if (this.programmeWizard.workflow) {
                wizardCommonModule.setHeader(this.programmeWizard.workflow.selectedProject.drugProgrammeName
                    + this.programmeWizard.PROJECT_EDIT_STEP_HEADER);
            }
            $('.hiddenOnAddProgramme').show();
        }
        this.programmeWizard.recalculateSplit(this.programmeWizard.PROJECT_EDIT_STEP_INX);
    },

    onNextStep: function (toStep) {
        var scope = this;
        if (scope.canNextFlag) {
            scope.updateProject(toStep);
            scope.canNextFlag = false;
            return true;
        }
        if (this.programmeWizard.addedProgrammeMode) {
            scope.fillProgrammeParams();
            ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-setup-exist", {
                'drugId': scope.project.drugId
            }, {
                showDialog: true
            }, function (result) {
                var programmeAlreadyExist = result.obj;
                var callBack = function (toStep) {
                    var sendData = scope.project;
                    ajaxModule.sendAjaxRequest("programme-setup/programme-setup-add-programme", JSON.stringify(sendData),
                        {showDialog: true}, function (result) {
                            scope.canNextFlag = true;
                            scope.programmeWizard.addedProgrammeMode = false;
                            $('#' + scope.programmeWizard.searchProgrammeStep.searchInputId).val(scope.project.drugId);

                            $('#drugProgrammeId').attr('disabled', 'disabled');
                            $('#drugProgrammeName').attr('disabled', 'disabled');
                            $('#programmeEnabled').attr('disabled', 'disabled');
                            $('#studyNumberStudies').attr('disabled', 'disabled');
                            $('#studyNumberEnabledStudies').attr('disabled', 'disabled');
                            $('.hiddenOnAddProgramme').show();
                            scope.programmeWizard.loadProjectWizard(result, toStep, true);
                        });
                };

                if (programmeAlreadyExist) {
                    var message = "A drug programme " + scope.project.drugId + " already exists, do you wish " +
                        "to continue to edit the setup of that drug programme?";
                    wizardCommonModule.showYesNoDialog("ACUITY", message, function () {
                        ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-setup-revert-programme",
                            {'drugId': scope.project.drugId}, {showDialog: true}, function (result) {
                                scope.canNextFlag = true;
                                scope.programmeWizard.addedProgrammeMode = false;
                                $('#' + scope.programmeWizard.searchProgrammeStep.searchInputId)
                                    .val(scope.project.drugId);
                                $('#drugProgrammeId').attr('disabled', 'disabled');
                                $('#drugProgrammeName').attr('disabled', 'disabled');
                                $('#programmeEnabled').attr('disabled', 'disabled');
                                $('#studyNumberStudies').attr('disabled', 'disabled');
                                $('#studyNumberEnabledStudies').attr('disabled', 'disabled');
                                $('.hiddenOnAddProgramme').show();
                                scope.programmeWizard.loadProjectWizard(result, toStep);

                            });
                    }, null, 450);
                } else {
                    callBack(toStep);

                }
            });
            return false;
        } else {
            return scope.confirmDrugProgrammeNameChange(toStep);
        }
        return true;
    },

    onBackStep: function (toStep, toAdmin) {
        var scope = this;
        if (scope.canBackFlag) {
            scope.programmeWizard.searchProgrammeStep.searchProjects(true);
            scope.canBackFlag = false;
            return true;
        }

        var isEditable = false;
        $(".enabled").each(function () {
            if (scope.programmeWizard.addedProgrammeMode) {
                return false;
            }
            var id = this.id;
            if ($.trim($(this).val()).length == 0) {
                isEditable = true;
                return false;
            }
            //if(id == "chiefProgramme" && $.trim($(this).val())!= $.trim(scope.project.admin)){
            //   isEditable = true;
            //   return false;
            //}

            if (id == "createDashboard" && $(this).prop("checked") != scope.project.createDashboard) {
                isEditable = true;
                return false;
            }
            if (id == "drugProgrammeName" && $.trim($(this).val()) != $.trim(scope.project.drugProgrammeName)) {
                isEditable = true;
                return false;
            }
        });
        if (isEditable) {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Discard programme parameters changes?", function () {
                scope.canBackFlag = true;
                var searchText = $('#' + scope.programmeWizard.searchProgrammeStep.searchInputId).val();
                scope.setCurrentProject(scope.programmeWizard.workflow.selectedProject, searchText);
                scope.closePadLock();
                setTimeout(function () {
                    if (typeof toStep === 'number') {
                        scope.programmeWizard.loadProjectWizard(scope.programmeWizard.workflow, toStep, true);
                    } else if (toAdmin) {
                        window.location = "admin";
                    }
                }, 300);

            });
            return false;
        } else if (scope.programmeWizard.addedProgrammeMode) {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Drug programme will not be added", function () {
                scope.canBackFlag = true;
                scope.closePadLock();
                if (typeof toStep === 'number') {
                    scope.programmeWizard.loadProjectWizard(scope.programmeWizard.workflow, toStep, true);
                } else if (toAdmin) {
                    window.location = "admin";
                }
            });
            return false;
        } else {
            scope.closePadLock();
            if (!toAdmin) {
                scope.programmeWizard.searchProgrammeStep.searchProjects(true);
            }
        }
        return true;

    },

    setCurrentProject: function (project, searchText) {
        this.project = project;
        $("#study-project-name").text(this.project.drugId);
        $("#drugProgrammeId").val(this.project.drugId);
        $('#drugProgrammeName').val(this.project.drugProgrammeName);
        $("#programmeEnabled").val(this.project.acuityEnabled ? "Yes" : "No");
        $("#studyNumberStudies").val(this.project.totalStudyCount);
        $("#studyNumberEnabledStudies").val(this.project.numberOfAcuityEnabledStudies);
        //$("#chiefProgramme").val(this.project.admin);
        $("#aeSeverityType").val(this.project.aeSeverityType || 'CTC_GRADES');
        $("#createDashboard").prop("checked", !!this.project.createDashboard);
        $('#' + this.searchInputId).val(searchText);
    },

    validateProject: function () {
        var editForm = $("#form-edit-programme");

        $(".enabled").each(function () {
            if ($(this).is(":disabled")) {
                $(this).removeAttr("disabled");
            }
        });
        var isValid = editForm.valid();
        $(".enabled").each(function () {
            $(this).attr('disabled', 'disabled');
        });
        return {status: isValid};
    }
};

