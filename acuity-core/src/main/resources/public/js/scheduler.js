var schedulerModule = {
    schedulingTableId: 'schedulingTable',
    currentPage: 1
}
var amlEnabledGlobally = false;
schedulerModule.setAmlEnabledGlobally = function(enable) {
    amlEnabledGlobally = enable;
}

schedulerModule.triggerJob = function (studyCode, projectName, cl) {
    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
        previousRunStatus: 'Loading...',
        previousRun: 'Loading...',
        previousRunTime: 'Loading...'
    });
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/trigger', {studyCode: studyCode, projectName: projectName},
        {
            showDialog: false
        }, function (result) {
            if (result != null) {
                if (result.error != null) {
                    noty({text: result.error, layout: 'bottomLeft', type: 'error'});
                }
                if (result.previousRunStatus != null && result.previousRun != null && result.previousRunTime != null) {
                    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
                        previousRunStatus: result.previousRunStatus,
                        previousRun: result.previousRun,
                        previousRunTime: result.previousRunTime
                    });
                }
                if (result.previousAmlRun != null) {
                    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
                        previousAmlRun: result.previousAmlRun,
                    });
                }
                if (result.previousAmlRunStatus != null) {
                    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
                        previousAmlRunStatus: result.previousAmlRunStatus,
                    });
                }
                if (result.previousAmlRunTime != null) {
                    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
                        previousAmlRunTime: result.previousAmlRunTime,
                    });
                }
            }
        });
};

schedulerModule.unscheduleJob = function (studyCode, projectName, cl) {
    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {nextRun: 'Loading...'});
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/unschedule', {studyCode: studyCode, projectName: projectName},
        {
            showDialog: false
        }, function (result) {
            $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {nextRun: result.nextRun});
        });
};

schedulerModule.reset = function (studyCode, cl) {
    $("#reset-" + cl).prop("disabled", true);
    $("#clean-" + cl).prop("disabled", true);
    $("#but-" + cl).prop("disabled", true);
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/reset-etl-status', {
            studyCode: studyCode
        },
        {
            showDialog: false
        }, function (result) {
            if (result && result.previousRunStatus) {
                $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {previousRunStatus: result.previousRunStatus});
            } else {
                noty({
                    text: 'The ETL status could not be reset, because the current status is not \'STARTED\'',
                    layout: 'bottomLeft', type: 'warning', timeout: 3000
                });
            }
            $("#reset-" + cl).prop("disabled", false);
            $("#clean-" + cl).prop("disabled", false);
            $("#but-" + cl).prop("disabled", false);
        });
};

schedulerModule.reset = function (studyCode, cl) {
    $("#reset-" + cl).prop("disabled", true);
    $("#clean-" + cl).prop("disabled", true);
    $("#but-" + cl).prop("disabled", true);
    $.post('scheduler/reset-etl-status', {studyCode: studyCode}, function (result) {
        var obj = JSON && JSON.parse(result) || $.parseJSON(result);
        if (obj && obj.previousRunStatus) {
            $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {previousRunStatus: obj.previousRunStatus});
        } else {
            noty({
                text: 'The ETL status could not be reset, because the current status is not \'STARTED\'',
                layout: 'bottomLeft', type: 'warning', timeout: 3000
            });
        }
        $("#reset-" + cl).prop("disabled", false);
        $("#clean-" + cl).prop("disabled", false);
        $("#but-" + cl).prop("disabled", false);
    });
};

schedulerModule.clean = function (studyCode, projectName, cl) {
    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {inDatabase: 'Clean running'});
    $("#clean-" + cl).prop("disabled", true);
    $("#reset-" + cl).prop("disabled", true);
    $("#but-" + cl).prop("disabled", true);
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/clean', {studyCode: studyCode, projectName: projectName},
        {
            showDialog: false
        }, function (result) {
            $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {inDatabase: result.inDatabase});
            $("#clean-" + cl).prop("disabled", false);
            $("#reset-" + cl).prop("disabled", false);
            $("#but-" + cl).prop("disabled", false);
        });
};

schedulerModule.runAml = function (studyCode, projectName, cl, amlEnabled) {
    $("#runAml-" + cl).prop("disabled", true);
    $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {
        previousAmlRun: 'Loading...',
        previousAmlRunStatus: 'Loading...',
        previousAmlRunTime: 'Loading...'
    });
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/run-aml', {studyCode: studyCode, projectName: projectName, amlEnabled: amlEnabled},
        {
            showDialog: false
        }, function (result) {
            noty({
                text: 'Azure Machine Learning algorithm has started',
                layout: 'bottomLeft', type: 'notification', timeout: 3000
            });
            $("#runAml-" + cl).prop("disabled", false);
        });
};

schedulerModule.scheduleClean = function (studyCode, projectName, cl) {
    ajaxModule.sendAjaxRequestSimpleParams('scheduler/scheduleClean/' + projectName + '/' + studyCode, {
            studyCode: studyCode,
            projectName: projectName
        },
        {
            showDialog: false
        }, function (result) {
            $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', cl, {scheduledClean: result.scheduledClean});
        });
};

schedulerModule.fillData = function (response) {
    if (!response || !response.length)
        return;
    var result = eval('(' + response + ')');
    schedulerModule.createSchedulingTable();
    schedulerModule.fillSchedulingTable(schedulerModule.schedulingTableId, result);
};

schedulerModule.fillSchedulingTable = function (tableId, result) {
    var data = result.tasks;
    for (var j = 0; j < data.length; j++) {
        $("#" + tableId).jqGrid('addRowData', j, data[j]);
    }
    $("#" + tableId).trigger("reloadGrid");
};

schedulerModule.resizeSchedulingTable = function () {
    $("#" + schedulerModule.schedulingTableId).setGridWidth($("#schedulingTableResult").width(), false);
};

schedulerModule.createSchedulingTable = function () {
    $("#" + schedulerModule.schedulingTableId).jqGrid(
        {
            datatype: 'local',
            autoencode: true,
            width: '100%',
            height: '100%',
            rowNum: 20,
            rowList: [10, 20, 30, 40, 50],
            pager: '#gridpager',
            colNames: ['Project Name', 'Study Code', 'In Database', 'Next Run', 'Previous Run',
                'Previous Run Status', 'Previous Run Time', 'Start Now', 'Unschedule', 'Clean Database Now',
                'Schedule Database Clean', 'Reset ETL Status', 'Run AML', 'AML Enabled', 'Previous AML Run',
                'Previous AML Run Status', 'Previous AML Run Duration'],
            colModel: [{name: 'projectName', index: 'projectName', width: 90, sortable: true},
                {name: 'studyCode', index: 'studyCode', width: 150, sortable: true},
                {name: 'inDatabase', index: 'inDatabase', width: 70, sortable: true},
                {name: 'nextRun', index: 'nextRun', width: 170, sortable: true},
                {name: 'previousRun', index: 'previousRun', width: 170, sortable: true},
                {name: 'previousRunStatus', index: 'previousRunStatus', width: 170, sortable: true},
                {name: 'previousRunTime', index: 'previousRunTime', width: 110, sortable: true},
                {
                    name: 'action', index: 'action', width: 70, sortable: false,
                    formatter: schedulerModule.buttonFormatter, align: 'center'
                },
                {
                    name: 'unshedule', index: 'unschedule', width: 85, sortable: false,
                    formatter: schedulerModule.unscheduleFormatter, align: 'center'
                },
                {
                    name: 'clean', index: 'clean', width: 110, sortable: false,
                    formatter: schedulerModule.cleanFormatter, align: 'center'
                },
                {
                    name: 'scheduledClean', index: 'scheduledClean', width: 150, sortable: false,
                    formatter: schedulerModule.scheduleCleanFormatter, align: 'center'
                },
                {
                    name: 'reset', index: 'reset', width: 100, sortable: false,
                    formatter: schedulerModule.resetFormatter, align: 'center'
                },
                {
                    name: 'runAml', index: 'runAml', width: 70, sortable: false,
                    formatter: schedulerModule.runAmlFormatter, align: 'center', hidden: !amlEnabledGlobally
                },
                {
                    name: 'amlEnabled', index: 'amlEnabled', width: 1, sortable: false, hidden: true // permanently hidden, needed to send with request
                },
                {name: 'previousAmlRun', index: 'previousRunAml', width: 170, sortable: true, hidden: !amlEnabledGlobally},
                {name: 'previousAmlRunStatus', index: 'previousRunAmlStatus', width: 120, sortable: true, hidden: !amlEnabledGlobally},
                {name: 'previousAmlRunTime', index: 'previousAmlRunTime', width: 140, sortable: true, hidden: !amlEnabledGlobally}],
            gridview: true,
            viewrecords: true,
            recordpos: 'left',
            sortable: false
        });

    schedulerModule.resizeSchedulingTable();

    $(window).bind('resize', $.debounce(100, function () {
        schedulerModule.resizeSchedulingTable();
    }));

    function fetchData() {
        $.ajax({
            type: "GET", url: 'scheduler/poll', async: true, cache: false, dataType: 'json',
            success: function (result) {
                if (result) {
                    if (result.error != null)
                        noty({text: result.error, layout: 'bottomLeft', type: 'error'});
                    var rowIdList = jQuery("#" + schedulerModule.schedulingTableId).getDataIDs();
                    var eventRowId;
                    for (i = 0; i < rowIdList.length; i++) {
                        rowData = jQuery("#" + schedulerModule.schedulingTableId).getRowData(rowIdList[i]);
                        if (rowData.projectName == result.projectName && rowData.studyCode == result.studyCode)
                            eventRowId = i;
                    }
                    if (result.previousRunStatus != null && result.previousRun != null && result.previousRunTime != null) {
                        $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {
                            previousRunStatus: result.previousRunStatus,
                            previousRun: result.previousRun,
                            previousRunTime: result.previousRunTime
                        });
                    }
                    if (result.nextRun != null) {
                        var data = $("#" + schedulerModule.schedulingTableId).jqGrid('getRowData', rowIdList[eventRowId]);
                        if (data.nextRun !== '-') {
                            $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {nextRun: result.nextRun});
                        }
                    }
                    if (result.inDatabase != null) {
                        $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {inDatabase: result.inDatabase});
                    }
                    if (result.previousAmlRun != null) {
                        $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {
                            previousAmlRun: result.previousAmlRun,
                        });
                    }
                    if (result.previousAmlRunStatus != null) {
                        $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {
                            previousAmlRunStatus: result.previousAmlRunStatus,
                        });
                    }
                    if (result.previousAmlRunTime != null) {
                        $("#" + schedulerModule.schedulingTableId).jqGrid('setRowData', rowIdList[eventRowId], {
                            previousAmlRunTime: result.previousAmlRunTime,
                        });
                    }
                }
                $.post('scheduler/eventAknowledge', {eventId: result.eventGuid}, function (result) {
                    fetchData();
                });
            },
            error: function (x, t, m) {
                if (t === "timeout") {
                    fetchData();
                }
            }
        });
    }

    $("#" + schedulerModule.schedulingTableId).ready(function () {
        fetchData();
    });

};

schedulerModule.buttonFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    var projectName = rowObject['projectName'];

    if (studyCode != undefined && projectName != undefined) {
        var cl = options.rowId;
        button = "<button id='but-" + cl + "' onclick=\"schedulerModule.triggerJob('" + studyCode + "', '" + projectName + "', '" + cl + "');\">Run now</button>";
        return button;
    } else {
        return "";
    }
};

schedulerModule.unscheduleFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    var projectName = rowObject['projectName'];

    if (studyCode != undefined && projectName != undefined) {
        var cl = options.rowId;
        button = "<button id='unshedule-" + cl + "' onclick=\"schedulerModule.unscheduleJob('" + studyCode + "', '" + projectName + "', '" + cl + "');\">Unschedule</button>";
        return button;
    } else {
        return "";
    }
};

schedulerModule.resetFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    if (studyCode !== undefined) {
        var cl = options.rowId;
        button = "<button id='reset-" + cl + "' onclick=\"confirm('The ETL status for the study will be set to FAILED')" +
            " ? schedulerModule.reset('" + studyCode + "', '" + cl + "')" +
            " : false;\">Reset</button>";
        return button;
    } else {
        return "";
    }
};

schedulerModule.runAmlFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    var projectName = rowObject['projectName'];
    var amlEnabled = rowObject['amlEnabled'];
    var disabled = amlEnabled ? '' : 'disabled';

    if (studyCode !== undefined) {
        var cl = options.rowId;
        button = "<button id='runAml-" + cl + "' onclick=\"schedulerModule.runAml('" + studyCode + "', '" + projectName + "', '" + cl + "', '" + amlEnabled + "');\" " + disabled + ">Run AML</button>";
        return button;
    } else {
        return "";
    }
};

schedulerModule.cleanFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    var projectName = rowObject['projectName'];

    if (studyCode != undefined && projectName != undefined) {
        var cl = options.rowId;
        button = "<button id='clean-" + cl + "' onclick=\"confirm('The study will be cleaned in database')" +
            " ? schedulerModule.clean('" + studyCode + "', '" + projectName + "', '" + cl + "')" +
            " : false;\">Clean</button>";
        return button;
    } else {
        return "";
    }
};

schedulerModule.scheduleCleanFormatter = function (cellvalue, options, rowObject) {
    var studyCode = rowObject['studyCode'];
    var projectName = rowObject['projectName'];
    if (studyCode !== undefined && projectName !== undefined) {
        var checked = cellvalue ? "' checked='checked' " : "' ";
        return "<input type='checkbox' id='scheduledClean-" + options.rowId + checked + "onclick=\"schedulerModule.scheduleClean('" + studyCode + "','" + projectName + "', '" + options.rowId + "');\"/>";
    }
}

$(function () {
    $("#logout").button();
});