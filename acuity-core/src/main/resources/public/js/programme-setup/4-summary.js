var SummaryProgrammeStep = function (programmeWizard) {
    var projectSumGroupTableId = 'projectSumGroupTable';
    var scope = this;

    var init = function () {
        projectSumGroupTable();
        $('#summaryLinkToGroup').on("click", function () {
            programmeWizard.goToStep(programmeWizard.GROUP_STEP_INX);
        });
    };

    this.fillSummaryStep = function (result) {
        $("#sumProgrammeDrugId").text(result.selectedProject.drugId);
        $("#sumProgrammeDrugName").text(result.selectedProject.drugProgrammeName);
        $("#sumProgrammeAcuityEnabled").text(result.selectedProject.acuityEnabled ? "Yes" : "No");
        $("#sumProgrammeNumStudies").text(result.selectedProject.totalStudyCount);
        $("#sumProgrammeNumEnabledStudies").text(result.selectedProject.numberOfAcuityEnabledStudies);
        //$("#sumProgrammeAdmin").text(result.selectedProject.admin);
        $("#sumCreateDashboard").text(result.selectedProject.createDashboard ? "Yes" : "No");
        if (result.groupings && result.groupings.length > 0) {
            $("#" + projectSumGroupTableId).jqGrid('clearGridData').jqGrid('setGridParam', {data: result.groupings}).trigger('reloadGrid');
        } else {
            $("#" + projectSumGroupTableId).jqGrid('clearGridData');
        }
        programmeWizard.recalculateSplit(programmeWizard.SUMMARY_STEP_INX);
    };

    var projectSumGroupTable = function () {
        $("#" + projectSumGroupTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['Grouping Name', 'Type of group'],
                colModel: [
                    {name: 'name', index: 'name', width: 200, align: "center"},
                    {name: 'type', index: 'type', width: 150, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                gridview: true,
                pager: "#programmeSummaryGroupPager",
                rowNum: 10,
                resizeStop: function (width, index) {
                    programmeWizard.recalculateSplit(programmeWizard.SUMMARY_STEP_INX);
                }
            });
    };
    init();
};

SummaryProgrammeStep.prototype = {
    reloadProjectSummaryData: function () {
        var scope = this;
        ajaxModule.sendAjaxRequestSimpleParams("programme-setup/programme-get-summary", {}, {showDialog: false}, function (result) {
            scope.fillSummaryStep(result);
        });
    }
};

