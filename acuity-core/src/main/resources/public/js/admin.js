var Admin = function () {
    var addProgrammeBtnId = "addDrugProgrammeBtn";
    var editProgrammeBtnId = "editDrugProgrammeBtn";
    var removeProgrammeBtnId = "removeDrugProgrammeBtn";
    var programmeSumNameId = "programme-name";
    var programmeSumHeaderId = "programme-id";
    var programmeSumStudiesId = "programme-total-nbr-studies";
    //var programmeSumAdminId = "programme-admin";

    var addStudyBtnId = "addStudyBtn";
    var studySumHeaderId = "study-id";
    var studySumNameId = "study-name";
    var studySumCSId = "clinical-study-id";
    var studySumCSName = "clinical-study-name";
    var studySumCommenceDateId = "study-com-date";
    var studySumEndDateId = "study-end-date";
    var studySumPhaseId = "study-phase";
    var studySumBlindingId = "study-blinded";
    var studySumRandId = "study-rand-status";
    var studySumRegulatoryId = "study-reg-status";

    var removeStudyBtnId = "removeStudyBtn";
    var editStudyBtnId = "editStudyBtn";
    this.selectedProjectData = {selectedProjectId: 0, selectedProjectDrugId: ""};
    this.selectedStudyData = {selectedStudyId: 0, selectedStudyName: ""};

    var scope = this;

    this.canEditProject = false;
    this.canDeleteProject = false;

    this.canEditStudy = false;
    this.canDeleteStudy = false;
    this.canAddStudy = false;

    var lastProjectId = sessionStorage.getItem('currentDrug');

    var init = function () {

        toggleProgrammeControls(false);
        toggleStudyControls(false);

        $('#' + addProgrammeBtnId).on("click", function () {
            window.location = "programme-setup";
        });
        $('#' + addStudyBtnId).on("click", function () {
            window.location = "study-setup";
        });

        $('#' + removeProgrammeBtnId).on("click", function () {
            onRemoveProgrammeCallback();
        });

        $('#' + removeStudyBtnId).on("click", function () {
            onRemoveStudyCallback();
        });

        initProgrammeList();
    };

    var renderProjects = function (projects, selectedProjectId) {
        $('#study-list').hide();

        $('#programme-summary-block').hide();
        $('#study-summary-block').hide();

        var $target = $('#programmes-list');
        $target.empty();
        _.forEach(projects, function (project) {
            var element = $('<a href="#" class="project-li list-group-item">' + project.drugProgrammeName + '</a>')
                .attr('id', project.id)
                .click(function () {
                    onProgrammeClickCallBack(this);
                });
            if (selectedProjectId === project.id) {
                element.addClass('active');
                $('#study-list').show();

                $('#programme-summary-block').show();
                $('#study-summary-block').show();
            }
            element.appendTo($target);
        });
    };

    $('#adminSearch').keyup(function () {
            var searchString = $(this).val().toLowerCase();
            var projects = _.filter(projectsInAcuity, function (project) {
                return project.drugId.toLocaleLowerCase().indexOf(searchString) !== -1 ||

                    _.some(project.studyRules, function (studyRule) {
                        return studyRule.studyName.toLocaleLowerCase().indexOf(searchString) !== -1 ||
                            studyRule.studyCode.toLocaleLowerCase().indexOf(searchString) !== -1 ||
                            (studyRule.primarySource && studyRule.primarySource.toLocaleLowerCase().indexOf(searchString) !== -1);
                    });
            });
            renderProjects(projects, lastProjectId);
        }
    );

    var initProgrammeList = function () {
        renderProjects(projectsInAcuity, lastProjectId);
        if (lastProjectId) {
            onProgrammeClickCallBack($('.project-li#' + lastProjectId)[0]);
        }
    };

    var onProgrammeClickCallBack = function (programmeLi) {
        if (!programmeLi) {
            return false;
        }
        lastProjectId = programmeLi.id;
        sessionStorage.setItem('currentDrug', lastProjectId)
        $(".project-li").removeClass("active");
        scope.selectedProjectData.selectedProjectId = programmeLi.id;
        scope.selectedProjectData.selectedProjectDrugId = $(programmeLi).html();
        $("#projectId").val(wizardCommonModule.htmlEscape(scope.selectedProjectData.selectedProjectId));
        $(programmeLi).addClass("active");
        $('#study-summary-block').hide();

        ajaxModule.sendAjaxRequestSimpleParams("admin/programme-info", {
                'projectId': scope.selectedProjectData.selectedProjectId
            }, {showDialog: true},
            function (result) {
                updateProgrammeSummary(result);
                toggleProgrammeControls(true);
            });

        var studyRules = _.find(projectsInAcuity, {id: scope.selectedProjectData.selectedProjectId | 0}).studyRules;
        updateStudyList(studyRules);
    };

    var updateStudyList = function (array) {
        $('#study-list').show();
        $('#study-summary-block').show();

        scope.selectedProjectData.studies = array;

        $("#study-list").children('a').remove();
        for (var i = 0; i < array.length; i++) {
            var studyLi = $('<a href="#" class="study-li list-group-item" id="' + array[i].id + '">'
                + array[i].studyCode + '</a>');
            $("#study-list").append(studyLi);
        }
        toggleStudyControls(false);
        $('#study-summary-block').hide();

        lastId = sessionStorage.getItem("currentStudy-" + scope.selectedProjectData.selectedProjectId);

        if (lastId) {
            studyItem = $(".study-li#" + lastId, $("#study-list"));
            if (studyItem[0]) {
                onStudyClickCallback.apply(studyItem[0]);
            }
        }

        $(".study-li").on("click", onStudyClickCallback);
    };

    var onStudyClickCallback = function () {
        if (this.id.length == 0) {
            return false;
        }
        // Part of VaHub
        $('#reportLink').attr('href', '/app#/report/' + this.id);
        $(".study-li").removeClass("active");

        scope.selectedStudyData.selectedStudyCode = this.id;
        scope.selectedStudyData.selectedStudyName = $(this).html();
        $(this).addClass("active");
        $("#studyId").val(wizardCommonModule.htmlEscape(scope.selectedStudyData.selectedStudyCode));

        ajaxModule.sendAjaxRequestSimpleParams("admin/study-info", {
            'studyId': scope.selectedStudyData.selectedStudyCode
        }, {showDialog: true}, function (result) {
            updateStudySummary(result);
            toggleStudyControls(true);
            sessionStorage.setItem("currentStudy-" + scope.selectedProjectData.selectedProjectId, scope.selectedStudyData.selectedStudyCode);
        });
    }

    var updateProgrammeSummary = function (projectInfo) {
        var selectedProgramme = projectInfo.projectRule;
        $('#' + programmeSumNameId).text(selectedProgramme.drugProgrammeName);
        $('#' + programmeSumHeaderId).text(selectedProgramme.drugId);
        $('#' + programmeSumStudiesId).text(selectedProgramme.totalStudyCount);
        //$('#' + programmeSumAdminId).text(selectedProgramme.admin);
        // $('#' + programmeSumOwnerId).text(selectedProgramme.dataOwnerName == null ? 'Unknown' : selectedProgramme.dataOwnerName);

        scope.canAddStudy = projectInfo.canAddStudy;
        scope.canDeleteProject = projectInfo.canDeleteProject;
        scope.canEditProject = projectInfo.canEditProject;

        $('#programme-summary-block').show();
    };

    var updateStudySummary = function (selectedStudy) {
        $('#' + studySumHeaderId).text(selectedStudy.studyCode);
        $('#' + studySumNameId).text(selectedStudy.studyName);
        $('#' + studySumCSName).text(selectedStudy.clinicalStudyName);
        $('#' + studySumCSId).text(selectedStudy.clinicalStudyId);
        $('#' + studySumCommenceDateId).text(selectedStudy.firstSubjectInPlanned);
        $('#' + studySumEndDateId).text(selectedStudy.databaseLockPlanned);
        $('#' + studySumPhaseId).text(selectedStudy.phase);

        $('#' + studySumBlindingId).text(selectedStudy.blinding ? 'Blinded' : 'Not blinded');
        $('#' + studySumRandId).text(selectedStudy.randomisation ? 'Randomised' : 'Not randomised');
        $('#' + studySumRegulatoryId).text(selectedStudy.regulatory ? 'Regulated' : 'Not regulated');
        updateStudyLocations(selectedStudy);

        scope.canEditStudy = selectedStudy.canEditStudy;
        scope.canDeleteStudy = selectedStudy.canDeleteStudy;

        $('#study-summary-block').show();
    };

    var updateStudyLocations = function (study) {
        var locations = _(study.fileRules)
            .map('dataSourceLocation')
            .filter(file => !!file)
            .map(function (file) {
                file.replace(/\\/g, '/');
                return file && file.substring(0, file.lastIndexOf('/'))
            })
            .compact()
            .uniq()
            .value();
        $('#study-locations').text(locations.join(', '));
    };

    var onRemoveProgrammeCallback = function () {
        if (scope.selectedProjectData.selectedProjectId == 0) {
            return false;
        }

        var message = "Are you sure you want to remove the Drug Programme </br>" +
            scope.selectedProjectData.selectedProjectDrugId + "?</br>" +
            "-Removing a drug programme will cause deletion of all associated study data mappings and visualisation modules ";

        wizardCommonModule.showYesNoDialog("ACUITY-Drug Programme removal", message, function () {
            ajaxModule.sendAjaxRequestSimpleParams("admin/remove-project", {
                'projectId': scope.selectedProjectData.selectedProjectId
            }, {
                showDialog: true
            }, function (result) {
                clearProgrammeAfterDelete();
            });
        }, null, 450);
    };

    var onRemoveStudyCallback = function () {
        if (scope.selectedStudyData.selectedStudyCode == 0) {
            return false;
        }
        var message = "Are you sure you want to remove the study data mapping </br>" +
            scope.selectedStudyData.selectedStudyName + " ?</br>" +
            "-Removing a study data mapping will delete all associated visualisation modules ";

        wizardCommonModule.showYesNoDialog("ACUITY - Study data mapping removal", message, function () {
            ajaxModule.sendAjaxRequestSimpleParams("admin/remove-study", {
                'studyId': scope.selectedStudyData.selectedStudyCode
            }, {
                showDialog: true
            }, function (result) {
                clearStudyAfterDelete();
            });
        }, null, 400);
    };

    var clearProgrammeAfterDelete = function () {
        $("a#" + scope.selectedProjectData.selectedProjectId + ".project-li.active").remove();
        $("#study-list").children('a').remove();
        scope.selectedProjectData = {selectedProjectId: 0, selectedProjectDrugId: ""};
        $('#programme-summary-block').hide();
        $('#study-summary-block').hide();
        $("#projectId").val("");
    };

    var clearStudyAfterDelete = function () {
        $("a#" + scope.selectedStudyData.selectedStudyCode + ".study-li.active").remove();
        _.remove(scope.selectedProjectData.studies, {id: scope.selectedStudyData.selectedStudyCode | 0});
        scope.selectedStudyData = {selectedStudyId: 0, selectedStudyName: ""};
        $('#study-summary-block').hide();
        $("#studyId").val("");
    };

    function toggleEnabled ($item, enabled) {
        if (enabled) {
            $item.removeAttr('disabled', 'disabled');
            $item.removeClass('disabled');
        } else {
            $item.attr('disabled', 'disabled');
            $item.addClass('disabled');
        }
    }

    var toggleProgrammeControls = function (enabled) {
        toggleEnabled($("#" + editProgrammeBtnId), enabled && scope.canEditProject);
        toggleEnabled($("#" + removeProgrammeBtnId), enabled && scope.canDeleteProject);
    };

    var toggleStudyControls = function (enabled) {
        toggleEnabled($("#" + editStudyBtnId), enabled && scope.canEditStudy);
        toggleEnabled($("#" + removeStudyBtnId), enabled && scope.canDeleteStudy);
    };

    init();
};

jQuery(function ($) {
    new Admin();
});
