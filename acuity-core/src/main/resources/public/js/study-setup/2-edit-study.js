var EditStudyStep = function (studyWizard) {

    this.studyWizard = studyWizard;
    this.canNextFlag = false;
    this.canBackFlag = false;
    this.study = null;
    this.blockingId = 'study-edit-block-inputs';

    var scope = this;

    var checkPathExistsAjax = null;
    this.isPrimarySourceValid = true;

    this.checkPrimarySourceValid = function () {
        if (checkPathExistsAjax) {
            checkPathExistsAjax.abort();
            checkPathExistsAjax = null;
        }
        var path = $("#studyPrimarySource").val();

        var fixedPath = $.trim(path).replace(/(\/|\\)+$/, '');
        if (path != fixedPath) {
            $("#studyPrimarySource").val(fixedPath);
            path = fixedPath;
        }
        if (path && path != "") {
            $.ajaxSetup({
                async: false
            });
            checkPathExistsAjax = $.getJSON('api/source/check_directory_exists', {path: path}, function (data) {
                checkPathExistsAjax = null;
                $("#studyPrimarySource").toggleClass("curly-underline", !data.obj);
                scope.isPrimarySourceValid = data.obj;
            });
            $.ajaxSetup({
                async: true
            });
        }
        else {
            $("#studyPrimarySource").removeClass("curly-underline");
            scope.isPrimarySourceValid = true;
        }
    };

    var init = function () {
        $(".dataTable").css("float", "none");
        $('#' + scope.blockingId).on("click", function () {
            if ($(this).hasClass("open")) {
                $(this).removeClass("open");
                $(".enabled").attr('disabled', 'disabled');
            } else {
                $(this).addClass("open");
                $(".enabled").removeAttr('disabled', 'disabled');
            }

        });

        if (!!editStudyWorkflow) {
            $.ajaxSetup({
                async: false
            });
            $.getJSON(`/scheduler/isScheduled/${editStudyWorkflow.selectedStudy.clinicalStudyId}/${editStudyWorkflow.selectedStudy.drugProgramme}`, function (data) {
                editStudyWorkflow.selectedStudy.scheduled = data
            });
            $.ajaxSetup({
                async: true
            });
        }
        addDataPicker("studyFsiPln");
        addDataPicker("studyDblPln");
        $("#studyPrimarySource").blur(function () {
            scope.checkPrimarySourceValid();
        });
        addValidator();
    };

    var addValidator = function () {
        $("#studyCode").keypress(function () {
            return utilsModule.handleRegexValidation(event, $(this));
        });

        $("#studyCode").bind('paste', function () {
            return utilsModule.handleRegexValidation(event, $(this));
        });

        jQuery.validator.setDefaults({
            debug: true,
            success: "valid",
            ignore: ".ignore"
        });

        var editForm = $("#form-edit-study");
        editForm.validate({
            rules: {
                studyEndDate: {
                    dpDate: true
                }
            },
            errorPlacement: function (error, element) {
                error.appendTo(element.parent("td").next("td"));
            },
            success: function (label, element) {
            },
            messages: {
                studyEndDate: "<< Required field",
                studyPhaseType: "<< Required field",
                studyName: "<< Required field",
                clinicalStudyId: "<< Required field",
                clinicalStudyName: "<< Required field",
                studyType: "<< Required field",
                studyDeliveryModel: "<< Required field",
                studyFsiPln: "<< Required field",
                studyDblPln: "<< Required field",
                studyDrugId: "<< Required field",
                studyCode: "<< Required field"
            }
        });
    };

    var addDataPicker = function (inputId) {
        $("#" + inputId).datepicker({
            changeMonth: true,
            changeYear: true,
            dateFormat: "dd-M-yy"
        });
    };

    this.clearEnabledAllData = function (text) {
        $("#selected-study-code").text(text);
        $("#studyDrugIdSelect").val("");
        $("#studyDrugId").val("");
        $("#studyCode").val("");
        $("#studyName").val("");
        $("#clinicalStudyName").val("");
        $("#clinicalStudyId").val("");
        $("#studyPhase").val("");
        $("#studyPhaseType option").removeAttr('selected');
        $("#studyPhaseType option:first").attr('selected', 'selected');
        $("#studyType").val("");
        $("#studyDeliveryModel").val("");
        $("#studyPrimarySource").val("");
        $("#studyFsiPln").val("");
        $("#studyDblPln").val("");
        $(".enabled").each(function () {
            if ($(this).is(":disabled")) {
                $(this).removeAttr("disabled");
            }
        });
        $('#' + scope.blockingId).addClass("open");
        $('#studyCode').removeAttr("disabled");
        $('#studyRegulatory option').attr('selected', 'false');
        $('#studyRegulatory option[value="true"]').attr('selected', 'selected');
        /*
         $('#studyConfigType option').attr('selected', 'false');
         $('#studyConfigType option[value="database"]').attr('selected', 'selected');
         */
        $("#studyScheduled").prop("checked", true);
        $("#autoAssignedCountry").prop("checked", false);
        $("#xAxisLimitedToVisit").prop("checked", false);
        $("#amlEnabled").prop("checked", false);

        $('#studyRandomisation option').attr('selected', 'false');
        $('#studyRandomisation option[value="true"]').attr('selected', 'selected');
        $('#studyBlinding option').attr('selected', 'false');
        $('#studyBlinding option[value="true"]').attr('selected', 'selected');
    };

    this.fillStudyParams = function () {
        if (!scope.study) {
            scope.study = {};
        }
        if (scope.studyWizard.addedStudyMode) {
            scope.study.projectId = $('#studyDrugIdSelect').val();
            scope.study.id = null;
            scope.study.studyCompleted = false;
            scope.study.studyValid = false;
            scope.study.studyEnabled = false;
            scope.study.status = "readyToMap";
        }
        var searchText = $('#' + this.searchInputId).val();
        scope.study.studyName = $("#studyName").val();
        scope.study.studyCode = $("#studyCode").val();
        scope.study.clinicalStudyId = $("#clinicalStudyId").val();
        scope.study.clinicalStudyName = $("#clinicalStudyName").val();
        scope.study.phaseType = $("#studyPhaseType option:selected").val();
        scope.study.phase = $("#studyPhaseType option:selected").text();
        scope.study.blinding = $("#studyBlinding").val() === "true";
        scope.study.randomisation = $("#studyRandomisation").val() === "true";
        scope.study.regulatory = $("#studyRegulatory").val() === "true";
        /*
         scope.study.configType = $("#studyConfigType").val();
         */
        scope.study.scheduled = $("#studyScheduled").prop("checked");
        scope.study.autoAssignedCountry = $("#autoAssignedCountry").prop("checked");
        scope.study.xAxisLimitedToVisit = $("#xAxisLimitedToVisit").prop("checked");
        scope.study.type = $("#studyType").val();
        scope.study.deliveryModel = $("#studyDeliveryModel").val();
        scope.study.primarySource = $("#studyPrimarySource").val();
        scope.study.firstSubjectInPlanned = $("#studyFsiPln").val();
        scope.study.databaseLockPlanned = $("#studyDblPln").val();
        scope.study.amlEnabled = $("#amlEnabled").prop("checked");
    };


    this.updateStudy = function (toStep) {
        scope.fillStudyParams();
        var sendData = _.cloneDeep(scope.study);
        sendData.projectGroupRules = undefined;
        sendData.labGroups = undefined;
        sendData.aeGroups = undefined;
        sendData.studySubjectGrouping = undefined;
        ajaxModule.sendAjaxRequest("study-setup-edit-study", JSON.stringify(sendData), {showDialog: false}, function (result) {
            if (toStep == studyWizard.STUDY_SEARCH_STEP_INX) {
                scope.studyWizard.searchStudyStep.searchStudies(true);
            }
        });
    };

    this.closePadLock = function () {
        if ($("#" + scope.blockingId).hasClass("open")) {
            $("#" + scope.blockingId).removeClass("open");
            $(".enabled").attr('disabled', 'disabled');
        }
        $(".enabled").each(function () {
            var id = this.id;
            $("label[for='" + id + "']").hide();
        });

        $("label[for='studyCode']").hide();
        $("#studyCode").attr('disabled', 'disabled');
    };

    init();
};

/* public methods ------*/
EditStudyStep.prototype = {
    startStep: function () {
        var scope = this;
        if (scope.studyWizard.addedStudyMode && !(scope.study && scope.study.id)) {
            wizardCommonModule.setHeader("New Dataset Parameters");
            $('#studyDrugId').hide();
            $('#studyDrugIdSelect').show();
            scope.clearEnabledAllData("New Dataset Parameters");
            // scope.wizard.smartWizard('disableStep', this.STUDY_EDIT_STEP_INX);
        } else {
            scope.studyWizard.addedStudyMode = false;
            wizardCommonModule.setHeader(scope.studyWizard.workflow.selectedStudy.studyCode + " Dataset Parameters");
            $('#studyDrugId').show();
            $('#studyDrugIdSelect').hide();
        }
        this.studyWizard.recalculateSplit(this.studyWizard.STUDY_EDIT_STEP_INX);
    },

    onNextStep: function (toStep) {
        var scope = this;
        if (scope.isPrimarySourceValid) {
            return callback();
        }
        else {
            wizardCommonModule.showYesNoDialog("ACUITY", "The Primary Source Folder entered does not appear to be a valid location. Continuing with the setup process may cause incorrect file mappings. Are you sure?", function () {
                if (callback()) {
                    scope.isPrimarySourceValid = true;
                    scope.studyWizard.goToStep(toStep);
                }
            });
        }

        function callback() {
            if (scope.canNextFlag) {
                scope.updateStudy(toStep);
                scope.canNextFlag = false;
                return true;
            }
            if (scope.studyWizard.addedStudyMode) {
                scope.fillStudyParams();
                ajaxModule.sendAjaxRequestSimpleParams("study-setup-exist", {
                    'studyCode': scope.study.studyCode
                }, {
                    showDialog: true
                }, function (result) {
                    var studyAlreadyExist = result.obj;
                    var callBack = function (toStep) {
                        var sendData = scope.study;
                        ajaxModule.sendAjaxRequest("study-setup-add-study", JSON.stringify(sendData), {showDialog: true}, function (result) {
                            scope.canNextFlag = true;
                            scope.studyWizard.addedStudyMode = false;
                            $('#' + scope.studyWizard.searchStudyStep.searchInputId).val(scope.study.studyCode);
                            var drugValue = $('#studyDrugIdSelect').find(":selected").text();
                            $('#studyDrugIdSelect').hide();
                            $('#studyDrugId').show();
                            $('#studyDrugId').val(drugValue);
                            $('#studyCode').attr('disabled', 'disabled');
                            scope.studyWizard.loadStudyWizard(result, toStep);
                        });
                    };
                    if (studyAlreadyExist) {
                        var message = "A study with identifier " + scope.study.studyCode + " already exists, do you wish to continue to edit the setup of that study?";
                        wizardCommonModule.showYesNoDialog("ACUITY", message, function () {
                            ajaxModule.sendAjaxRequestSimpleParams("study-setup-revert-study", {'studyCode': scope.study.studyCode}, {showDialog: true}, function (result) {
                                if (result.selectedStudy.status == 'notInAcuity') {
                                    wizardCommonModule.showWarningDialog("This study is not ACUITY enabled!");
                                } else {
                                    scope.canNextFlag = true;
                                    scope.studyWizard.addedStudyMode = false;
                                    $('#' + scope.studyWizard.searchStudyStep.searchInputId).val(scope.study.studyCode);
                                    var drugValue = $('#studyDrugIdSelect').find(":selected").text();
                                    $('#studyDrugIdSelect').hide();
                                    $('#studyDrugId').show();
                                    $('#studyDrugId').val(drugValue);
                                    $('#studyCode').attr('disabled', 'disabled');
                                    scope.studyWizard.loadStudyWizard(result, toStep);
                                }
                            });
                        }, null, 450);
                    } else {
                        callBack(toStep);
                    }
                });
                return false;
            } else {
                scope.updateStudy(toStep);
            }
            return true;
        }
    },

    onBackStep: function (toStep, toAdmin) {
        var scope = this;
        if (scope.canBackFlag) {
            scope.studyWizard.searchStudyStep.searchStudies(true);
            scope.canBackFlag = false;
            return true;
        }
        var isEditable = false;
        $(".enabled").each(function () {
            if (scope.studyWizard.addedStudyMode) {
                return false;
            }
            var id = this.id;
            if ($.trim($(this).val()).length == 0 && id != "studyDrugIdSelect") {
                isEditable = true;
                return false;
            }
            if (id == "studyName" && $.trim($(this).val()) != $.trim(scope.study.studyName)) {
                isEditable = true;
                return false;
            }
            if (id == "clinicalStudyName" && $.trim($(this).val()) != $.trim(scope.study.clinicalStudyName)) {
                isEditable = true;
                return false;
            }
            if (id == "clinicalStudyId" && $.trim($(this).val()) != $.trim(scope.study.clinicalStudyId)) {
                isEditable = true;
                return false;
            }
            if (id == "studyPhase" && $.trim($(this).val()) != $.trim(scope.study.phase)) {
                isEditable = true;
                return false;
            }
            if (id == "studyType" && $.trim($(this).val()) != $.trim(scope.study.type)) {
                isEditable = true;
                return false;
            }
            if (id == "studyDeliveryModel" && $.trim($(this).val()) != $.trim(scope.study.deliveryModel)) {
                isEditable = true;
                return false;
            }

            if (id == "studyPrimarySource" && $.trim($(this).val()) != $.trim(scope.study.primarySource)) {
                isEditable = true;
                return false;
            }
            if (id == "studyFsiPln" && $.trim($(this).val()) != $.trim(scope.study.firstSubjectInPlanned)) {
                isEditable = true;
                return false;
            }
            if (id == "studyDblPln" && $.trim($(this).val()) != $.trim(scope.study.databaseLockPlanned)) {
                isEditable = true;
                return false;
            }
            if (id == "studyBlinding" && $.trim($(this).val()) != scope.study.blinding) {
                isEditable = true;
                return false;
            }
            if (id == "studyRandomisation" && $.trim($(this).val()) != scope.study.randomisation) {
                isEditable = true;
                return false;
            }
            if (id == "studyRegulatory" && $.trim($(this).val()) != scope.study.regulatory) {
                isEditable = true;
                return false;
            }
            if (id == "studyScheduled" && $(this).prop("checked") != scope.study.scheduled) {
                isEditable = true;
                return false;
            }
            if (id == "autoAssignedCountry" && $(this).prop("checked") != scope.study.autoAssignedCountry) {
                isEditable = true;
                return false;
            }
            if (id == "xAxisLimitedToVisit" && $(this).prop("checked") != scope.study.xAxisLimitedToVisit) {
                isEditable = true;
                return false;
            }
            if (id == "amlEnabled" && $(this).prop("checked") != scope.study.amlEnabled) {
                isEditable = true;
                return false;
            }
        });
        if (isEditable) {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Discard dataset parameters changes?", function () {
                scope.canBackFlag = true;
                var searchText = $('#' + scope.studyWizard.searchStudyStep.searchInputId).val();
                scope.setCurrentStudy(scope.studyWizard.workflow, searchText);
                scope.closePadLock();
                setTimeout(function () {
                    if (typeof toStep === 'number') {
                        scope.studyWizard.loadStudyWizard(scope.studyWizard.workflow, toStep, isEditable);
                    } else if (toAdmin) {
                        window.location = "admin";
                    }
                }, 300);
            });
            return false;
        } else if (scope.studyWizard.addedStudyMode) {
            wizardCommonModule.showYesNoDialog("Are You Sure?", "Dataset will not be added", function () {
                scope.canBackFlag = true;
                scope.closePadLock();
                if (typeof toStep === 'number') {
                    scope.studyWizard.loadStudyWizard(scope.studyWizard.workflow, toStep, isEditable);
                } else if (toAdmin) {
                    window.location = "admin";
                }
            });
            return false;
        } else {
            scope.closePadLock();
            if (!toAdmin) {
                scope.studyWizard.searchStudyStep.searchStudies(true);
            }
        }
        return true;
    },

    setCurrentStudy: function (studyWorkflow, searchText) {
        this.study = studyWorkflow.selectedStudy;
        var scope = this;
        $("#selected-study-code").text(scope.study.studyCode);
        $("#studyDrugId").val(studyWorkflow.parentProject.drugId);
        $("#studyDrugId").attr("title", studyWorkflow.parentProject.drugId);
        $("#studyCode").val(scope.study.studyCode);
        $("#studyCode").attr("title", scope.study.studyCode);
        $("#studyName").val(scope.study.studyName);
        $("#studyName").attr("title", scope.study.studyName);
        $("#clinicalStudyId").val(scope.study.clinicalStudyId);
        $("#clinicalStudyId").attr("title", scope.study.clinicalStudyId);
        $("#clinicalStudyName").val(scope.study.clinicalStudyName);
        $("#clinicalStudyName").attr("title", scope.study.clinicalStudyName);
        $("#studyPhase").val(scope.study.phase);
        $("#studyPhase").attr("title", scope.study.phase);
        $("#studyPhaseType option").filter(function () {
            return $(this).attr('value') == scope.study.phaseType;
        }).prop("selected", true);
        $("#studyPhaseType").attr("title", scope.study.phaseType);
        $("#studyBlinding").val(scope.study.blinding ? "true" : "false");
        $("#studyRandomisation").val(scope.study.randomisation ? "true" : "false");
        $("#studyRegulatory").val(scope.study.regulatory ? "true" : "false");
        /*
         $("#studyConfigType").val(scope.study.configType);
         */
        $("#studyScheduled").prop("checked", !!studyWorkflow.selectedStudy.scheduled);
        $("#studyScheduled").prop("checked", !!scope.study.scheduled);
        $("#autoAssignedCountry").prop("checked", !!scope.study.autoAssignedCountry);
        $("#xAxisLimitedToVisit").prop("checked", !!scope.study.xAxisLimitedToVisit);
        $("#amlEnabled").prop("checked", !!scope.study.amlEnabled);

        $("#studyType").val(scope.study.type);
        $("#studyType").attr("title", scope.study.type);

        $("#studyDeliveryModel").val(scope.study.deliveryModel);
        $("#studyDeliveryModel").attr("title", scope.study.deliveryModel);
        $("#studyPrimarySource").val(scope.study.primarySource);
        $("#studyPrimarySource").attr("title", scope.study.primarySource);
        $("#studyFsiPln").val(scope.study.firstSubjectInPlanned);
        $("#studyDblPln").val(scope.study.databaseLockPlanned);
        $('#' + this.searchInputId).val(searchText);
        scope.checkPrimarySourceValid();
    },

    validateStudy: function () {
        var editForm = $("#form-edit-study");

        $(".enabled").each(function () {
            if ($(this).is(":disabled")) {
                $(this).removeAttr("disabled");
            }
        });
        var isValid = editForm.valid();
        $(".enabled").each(function () {
            $(this).attr('disabled', 'disabled');
        });
        var firstSubjectInPlannedString = $("#studyFsiPln").val();
        var databaseLockPlannedString = $("#studyDblPln").val();
        var regex = /^([0-9]{1,2}-[a-zA-Z]{3}-[0-9]{2,4})$/;
        var isValidSubjectInPlanned = regex.test(firstSubjectInPlannedString);
        var isValidDatabaseLockPlanned = regex.test(databaseLockPlannedString);

        if (!isValidSubjectInPlanned) {
            return {status: false, message: 'Planned date for first subject is invalid'};
        }
        if (!isValidDatabaseLockPlanned) {
            return {status: false, message: 'Planned date of database lock is invalid'};
        }

        var firstSubjectInPlanned = new Date(firstSubjectInPlannedString);
        var databaseLockPlanned = new Date(databaseLockPlannedString);
        var nowStr = $.datepicker.formatDate("dd-M-yy", new Date());
        var nowDate = $.datepicker.parseDate("dd-M-yy", nowStr);

        if (nowDate > databaseLockPlanned) {
            return {status: false, message: 'Planned date of database lock cannot be in the past'};
        }
        if (firstSubjectInPlanned > databaseLockPlanned) {
            return {
                status: false,
                message: 'Planned date for first subject in cannot be after planned date of database lock'
            };
        }

        return {status: isValid, message: ''};
    },

    fillCompletedProjectList: function (result) {
        for (var i = 0; i < result.length; i++) {
            var entity = result[i];
            var option = $(' <option value="' + entity.id + '">' + entity.drugId + '</option>');
            $("#studyDrugIdSelect").append(option);
        }
    }
};
