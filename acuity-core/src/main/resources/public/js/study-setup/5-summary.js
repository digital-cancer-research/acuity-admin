var SummaryStudyStep = function (studyWizard) {
    var studyLinkToMappings = 'studyLinkToMappings';
    var studyLinkToGroupings = 'studyLinkToGroupings';

    this.sumHistoryTableId = 'sumHistoryTable';
    this.dataMappingsTableId = 'dataMappingsStudyTable';
    this.subjectTableId = 'subjectGroupingsTable';
    this.studyWizard = studyWizard;

    var scope = this;

    var init = function () {
        createDataMappingsTable();
        createSubjectTable();
        $(window).bind('resize', function () {
            var mappingTable = $("#" + scope.dataMappingsTableId);
            var subjectTable = $("#" + scope.subjectTableId);
            scope.resizeSummaryTables(mappingTable, subjectTable);
        });
        addSummaryLinksActions();
    };

    var createDataMappingsTable = function () {
        $("#" + scope.dataMappingsTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['Type of', 'Data source'],
                colModel: [
                    {name: 'name', index: 'name', width: 200, align: "center"},
                    {name: 'dataSource', index: 'dataSource', width: 150, align: "center", formatter: sourceFormatter}
                ],
                recordpos: 'left',
                viewrecords: true,
                rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
                gridview: true,
                shrinkToFit: true,
                resizeStop: function (width, index) {
                    scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_SUMMARY_STEP_INX);
                }
            });
    };

    var createSubjectTable = function () {
        $("#" + scope.subjectTableId).jqGrid(
            {
                datatype: "local",
                autoencode: true,
                width: "100%",
                height: "100%",
                colNames: ['Subject grouping name'],
                colModel: [
                    {name: 'name', index: 'name', width: 250, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
                gridview: true,
                shrinkToFit: true,
                resizeStop: function (width, index) {
                    scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_SUMMARY_STEP_INX);
                }
            });
    };

    var sourceFormatter = function (cellvalue, options, rowObject) {
        return cellvalue == null ? "manual" : cellvalue;
    };

    var addSummaryLinksActions = function () {
        $('#' + studyLinkToMappings).on("click", function () {
            scope.studyWizard.goToStep(scope.studyWizard.STUDY_MAPPING_STEP_INX);
        });

        $('#' + studyLinkToGroupings).on("click", function () {
            scope.studyWizard.goToStep(scope.studyWizard.STUDY_GROUPING_STEP_INX);
        });
    };

    this.resizeSummaryTables = function (mappingTable, subjectTable) {
        var width = $("#summaryStudyTable").width();
        mappingTable.setGridWidth(width - 3, true);
        subjectTable.setGridWidth(width - 3, true);
    };

    init();
};

/* public methods ------*/
SummaryStudyStep.prototype = {
    showStudySummaryData: function () {
        var scope = this;
        ajaxModule.sendAjaxRequestSimpleParams("study-setup-summary", {}, {showDialog: false}, function (result) {
            $("#stdName").text(result.selectedStudy.studyName);
            $("#stdId").text(result.selectedStudy.studyCode);
            $("#csName").text(result.selectedStudy.clinicalStudyName);
            $("#csId").text(result.selectedStudy.clinicalStudyId);
            $("#endDate").text(result.selectedStudy.databaseLockPlanned);
            $("#phase").text(result.selectedStudy.phase);
            $("#blinding").text(result.selectedStudy.blinding ? 'Blinded' : 'Not blinded');
            $("#randomisation").text(result.selectedStudy.randomisation ? 'Randomised' : 'Not randomised');
            $("#regularity").text(result.selectedStudy.regulatory ? 'Regulated' : 'Not regulated');
            $("#disclaimerWarn").html(wizardCommonModule.htmlScriptEscape(result.disclaimerWarning));
            if (result.selectedStudy.studyValid) {
                $("#confirm").attr('checked', 'checked');
            } else {
                $("#not-confirm").attr('checked', 'checked');
            }

            var mappingTable = $("#" + scope.dataMappingsTableId);
            var subjectTable = $("#" + scope.subjectTableId);


            mappingTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: result.completeMappings}).trigger('reloadGrid');
            subjectTable.jqGrid('clearGridData').jqGrid('setGridParam', {data: result.groupings}).trigger('reloadGrid');
            scope.studyWizard.recalculateSplit(scope.studyWizard.STUDY_SUMMARY_STEP_INX);
            scope.resizeSummaryTablesByRightPane(mappingTable, subjectTable);
        });
    },

    resizeSummaryTablesByRightPane: function (mappingTable, subjectTable) {
        var scope = this;
        if (!mappingTable) {
            mappingTable = $("#" + scope.dataMappingsTableId);
        }
        if (!subjectTable) {
            subjectTable = $("#" + scope.subjectTableId);
        }
        var rightPaneElement = $("#rightPane");
        mappingTable.setGridWidth(rightPaneElement.width() - 50, true);
        subjectTable.setGridWidth(rightPaneElement.width() - 50, true);
    },

    createHistoryTable: function () {
        var scope = this;
        var timeStamp = function (ts) {
            var now = new Date(ts);
            var date = [now.getMonth() + 1, now.getDate(), now.getFullYear()];
            var time = [now.getHours(), now.getMinutes(), now.getSeconds()];
            var suffix = ( time[0] < 12 ) ? "AM" : "PM";
            time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;
            time[0] = time[0] || 12;
            for (var i = 1; i < 3; i++) {
                if (time[i] < 10) {
                    time[i] = "0" + time[i];
                }
            }
            return date.join("/") + " " + time.join(":") + " " + suffix;
        };

        $.ajax({
            dataType: "json",
            url: 'study-setup/history',
            method: 'POST',
            contentType: "application/json",
            data: JSON.stringify({"pageNum": 1, "pageSize": 10})
        }).done(function (result) {
            var mydata = _.map(result.items, function (el) {
                return {comment: el.comment, username: el.username, timestamp: timeStamp(el.timestamp)}
            });
            $("#" + scope.sumHistoryTableId).jqGrid({
                datatype: "local",
                autoencode: true,
                data: mydata,
                width: "100%",
                height: "100%",
                colModel: [
                    {label: 'Status of dataset setup', name: 'comment', index: 'comment', width: 350, align: "center"},
                    {label: 'User', name: 'username', index: 'username', width: 250, align: "center"},
                    {label: 'Date', name: 'timestamp', index: 'timestamp', width: 150, align: "center"}
                ],
                recordpos: 'left',
                viewrecords: true,
                rowNum: 20,
                gridview: true
            });
        });
    }

};
