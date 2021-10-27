var Report = function(){

    var init = function(){
        $('#tabs').tabs();
        createSummaryReportTable();
        createExceptionReportTable();
        createTableReportTable();
        createFieldReportTable();
        createValueReportTable();

        var studyCode = location.search.split('?')[1].split('=')[1];
        ajaxGetSummaryReportData(studyCode);

        $(document).on('click', '#dataSummaryReport tr', onSummaryTableClick);
        addHelpText();
    };

    var createSummaryReportTable = function () {

        $("#dataSummaryReport").jqGrid({
            datatype: "local",
            autoencode: true,
            height: '280',
            colNames:['Clinical Study <a href="#colClinicalStudy" class="help">?</a>',
                      'Date of Upload <a href="#colDateOfUpload" class="help">?</a>',
                      'Time of Upload <a href="#colTimeOfUpload" class="help">?</a>',
                      'Duration of Upload <a href="#colDurationOfUpload" class="help">?</a>',
                      'Success of Upload <a href="#colSuccessOfUpload" class="help">?</a>',
                      'Upload Summary <a href="#colUploadSummary" class="help">?</a>',
                      'Job Execution ID'],
            colModel: [
                {name:'studyID',index:'studyID', width: colWidth(false, 6, 12), align:"left"},
                {name:'date',index:'date', width: colWidth(false, 6, 12), align:"left"},
                {name:'time',index:'time', width: colWidth(false, 6, 12),  align:"left"},
                {name:'duration', index:'duration', width: colWidth(false, 6, 12), align:"left"},
                {name:'ragStatus', index:'ragStatus', width: colWidth(false, 6, 12),  align:"left"},
                {name:'summary', index:'summary', width: colWidth(false, 6, 40),  align:"left"},
                {name:'jobExecID', index:'jobExecID', hidden: false}
            ],
            recordpos: 'left',
            viewrecords: true,
            gridview: true,
            rowNum:9,
            pager:'#dataSummaryPager',
            loadComplete: applyRag,
            multiSort: true,
        });
    };
    
    var createExceptionReportTable = function () {

        $("#exceptionReport").jqGrid({
            datatype: "local",
            autoencode: true,
            height: '396',
            colNames:['ETL step <a href="#colEtlStep" class="help">?</a>',
                      'Exception type <a href="#colExceptionType" class="help">?</a>',
                      'Details <a href="#colExceptionMessage" class="help">?</a>',
                      'Error status <a href="#colErrorStatus" class="help">?</a>'],
            colModel: [
                {name: 'etlStep', index: 'etlStep', width: colWidth(false, 4, 30), align: 'left'},
                {name: 'exceptionClass', index: 'exceptionClass', width: colWidth(false, 4, 30), align: 'left'},
                {name: 'message', index: 'message', width: colWidth(false, 4, 30),  align: 'left'},
                {name: 'ragStatus', index: 'ragStatus', width: colWidth(false, 4, 9.9), align: 'left'}
            ],
            recordpos: 'left',
            viewrecords: true,
            gridview: true,
            rowNum: 12,
            pager: '#exceptionPager',
            loadComplete: applyRag,
            multiSort: true,
            loadComplete: applyExceptionDetailsFormatting
        });
    };

    var createTableReportTable = function () {

    	$("#dataTableReport").jqGrid({
            datatype: "local",
            autoencode: true,
            height: '396',
            colNames:['ACUITY data set <a href="#colAcuityDataSet" class="help">?</a>',
                      'Raw data source <a href="#colRawDataSource" class="help">?</a>',
                      'Subjects - source <a href="#colSubjectsSource" class="help">?</a>',
                      'Subjects - data table <a href="#colSubjectsAcuity" class="help">?</a>',
                      'Current total valid events <a href="#colCurrentTotalValidEvents" class="help">?</a>',
                      'Error Status <a href="#colErrorStatus" class="help">?</a>'],
            colModel: [
                {name:'acuityEntities',index:'acuityEntities', width: colWidth(true, 6, 15), align:"left"},
                {name:'fileName',index:'fileName', width: colWidth(true, 6, 40), align:"left"},
                {name:'numSubjectsSource',index:'numSubjectsSource', width: colWidth(true, 6, 11.25),  align:"left",
                sorttype: 'int'},
                {name:'numSubjectsAcuity', index:'numSubjectsAcuity', width: colWidth(true, 6, 11.25), align:"left",
                sorttype: 'int'},
                {name:'numEventRowsUploaded', index:'numEventRowsUploaded', width: colWidth(true, 6, 11.25),
                align:"left", sorttype: 'int'},
                {name:'ragStatus', index:'ragStatus', width: colWidth(true, 6, 11.25), align:'left'}
            ],
            recordpos: 'left',
           	viewrecords: true,
           	gridview: true,
           	rowNum:12,
           	pager:'#dataTablePager',
            loadComplete: applyRag
    	});
    };

    var createFieldReportTable = function () {

    	$("#dataFieldReport").jqGrid({
            datatype: "local",
            autoencode: true,
            autowidth: true,
            height: '396',
            colNames:['ACUITY data field <a href="#colAcuityDataField" class="help">?</a>',
                      'Raw data source <a href="#colRawDataSource" class="help">?</a>',
                      'Raw data column <a href="#colRawDataColumn" class="help">?</a>',
                      'Error status <a href="#colErrorStatus" class="help">?</a>',
                      'Error type <a href="#colErrorType" class="help">?</a>',
                      'Error detail <a href="#colErrorDetail" class="help">?</a>'],
            colModel: [
                {name:'dataField',index:'dataField', width:colWidth(true, 6, 16), align:"left"},
                {name:'fileName',index:'fileName', width:colWidth(true, 6, 40), align:"left"},
                {name:'rawDataColumn',index:'rawDataColumn', width:colWidth(true, 6, 7), align:"left"},
                {name:'ragStatus', index:'ragStatus', width:colWidth(true, 6, 10), align:"left"},
                {name:'errorTypeString', index:'errorType', width:colWidth(true, 6, 7),  align:"left"},
                {name:'errorDescription', index:'errorDescription', width:colWidth(true, 6, 20),  align:"left"}
            ],
            ignoreCase: true,
            recordpos: 'left',
           	viewrecords: true,
           	gridview: true,
           	rowNum:12,
           	pager:'#dataFieldPager',
            loadComplete: applyRag
    	});
    };

    var ajaxGetSummaryReportData = function(studyCode) {

    	ajaxModule.sendAjaxRequestSimpleParams('get-summary-report', {studyCode: studyCode},
            {showDialog: true}, function(result) {
    		$('#dataSummaryReport').jqGrid('clearGridData').jqGrid('setGridParam', {data: result}).trigger('reloadGrid');
    	});
    };

    var createValueReportTable = function () {

    	$('#dataValueReport').jqGrid({
            datatype: 'local',
            autoencode: true,
            height: '396',
            colNames:['ACUITY data field <a href="#colAcuityDataField" class="help">?</a>',
                      'Raw data source <a href="#colRawDataSource" class="help">?</a>',
                      'Raw data column <a href="#colRawDataColumn" class="help">?</a>',
                      'Raw data value <a href="#colRawDataValue" class="help">?</a>',
                      'Error count <a href="#colErrorCount" class="help">?</a>',
                      'Error status <a href="#colErrorStatus" class="help">?</a>',
                      'Error type <a href="#colErrorType" class="help">?</a>',
                      'Error detail <a href="#colErrorDetail" class="help">?</a>'],
            colModel: [
                {name:'dataField',index:'dataField', width: colWidth(true, 7, 12.5), align: 'left'},
                {name:'fileName',index:'fileName', width: colWidth(true, 7, 35), align: 'left'},
                {name:'rawDataColumn',index:'rawDataColumn', width: colWidth(true, 7, 7.5), align: 'left'},
                {name:'rawDataValue',index:'rawDataValue', width: colWidth(true, 7, 7.5),  align: 'left'},
                {name:'errorCount', index:'errorCount', width: colWidth(true, 7, 5), align: 'left'},
                {name:'ragStatus', index:'ragStatus', width: colWidth(true, 7, 5), align: 'left'},
                {name:'errorTypeString', index:'errorType', width: colWidth(true, 7, 5),  align: 'left'},
                {name:'errorDescription', index:'errorDescription', width: colWidth(true, 7, 27.5),  align: 'left'}
            ],
            recordpos: 'left',
           	viewrecords: true,
           	gridview: true,
           	rowNum:12,
           	pager:'#dataValuePager',
            loadComplete: applyRag
    	});
    };

    var onSummaryTableClick = function() {

    	var jobExecID = $(this).children('td[aria-describedby="dataSummaryReport_jobExecID"]').html();
    	ajaxGetTableFieldValueData(jobExecID);
    };

    var ajaxGetTableFieldValueData = function(jobExecID) {

    	ajaxModule.sendAjaxRequestSimpleParams('get-exception-table-field-value-reports', {jobExecID: jobExecID},
    	    {showDialog: true}, function(result) {
    	        $('#exceptionReport').jqGrid('clearGridData').jqGrid('setGridParam', {data: result.EXCEPTION}).trigger('reloadGrid');
    	        $('#dataTableReport').jqGrid('clearGridData').jqGrid('setGridParam', {data: result.TABLE}).trigger('reloadGrid');
        		$('#dataFieldReport').jqGrid('clearGridData').jqGrid('setGridParam', {data: result.FIELD}).trigger('reloadGrid');
        		$('#dataValueReport').jqGrid('clearGridData').jqGrid('setGridParam', {data: result.VALUE}).trigger('reloadGrid');
    	});
    };

    var addHelpText = function() {
    	$('a[href=#colClinicalStudy]').attr('title', $('#colClinicalStudy').val());
    	$('a[href=#colDateOfUpload]').attr('title', $('#colDateOfUpload').val());
    	$('a[href=#colTimeOfUpload]').attr('title', $('#colTimeOfUpload').val());
    	$('a[href=#colDurationOfUpload]').attr('title', $('#colDurationOfUpload').val());
    	$('a[href=#colSuccessOfUpload]').attr('title', $('#colSuccessOfUpload').val());
    	$('a[href=#colUploadSummary]').attr('title', $('#colUploadSummary').val());
    	$('a[href=#colAcuityDataSet]').attr('title', $('#colAcuityDataSet').val());
    	$('a[href=#colRawDataSource]').attr('title', $('#colRawDataSource').val());
    	$('a[href=#colSubjectsSource]').attr('title', $('#colSubjectsSource').val());
    	$('a[href=#colSubjectsAcuity]').attr('title', $('#colSubjectsAcuity').val());
    	$('a[href=#colCurrentTotalValidEvents]').attr('title', $('#colCurrentTotalValidEvents').val());
    	$('a[href=#colOverwrittenRecords]').attr('title', $('#colOverwrittenRecords').val());
    	$('a[href=#colErrorCount]').attr('title', $('#colErrorCount').val());
    	$('a[href=#colErrorStatus]').attr('title', $('#colErrorStatus').val());
    	$('a[href=#colAcuityDataField]').attr('title', $('#colAcuityDataField').val());
    	$('a[href=#colRawDataColumn]').attr('title', $('#colRawDataColumn').val());
    	$('a[href=#colErrorType]').attr('title', $('#colErrorType').val());
    	$('a[href=#colErrorDetail]').attr('title', $('#colErrorDetail').val());
    	$('a[href=#colRawDataValue]').attr('title', $('#colRawDataValue').val());
    	$('a[href=#colEtlStep]').attr('title', $('#colEtlStep').val());
    	$('a[href=#colExceptionType]').attr('title', $('#colExceptionType').val());
    	$('a[href=#colExceptionMessage]').attr('title', $('#colExceptionMessage').val());

    	$(".help").tipTip();
    };

    var colWidth = function(tabs, nCols, percentageWidth) {
    	if (tabs) {
    		parentWidth = $('#gview_dataSummaryReport').parent().parent().width() - (5.1 * nCols);
    	} else {
    		parentWidth = $('#report').width() - (4.66 * nCols);
    	}
    	return parentWidth * (percentageWidth/100);
    };

    var applyExceptionDetailsFormatting = function() {
        $('#exceptionReport tr:gt(0) td:nth-child(1)').each(function() {
            if ($(this).text() == 'readConfiguration') {
                $(this).text('Configuration read');
            } else if ($(this).text() == 'preprocessResources') {
                $(this).text('Pre-process');
            } else if ($(this).text() == 'readHashValue') {
                $(this).text('Read hash value');
            } else if ($(this).text() == 'readProcessWrite') {
                $(this).text('Read, process and write');
            } else if ($(this).text() == 'publishReports') {
                $(this).text('Report publishing');
            }
        });
        $('#exceptionReport tr:gt(0) td:nth-child(3)').each(function() { 
            $(this).html('<input type="hidden" value="' + $(this).text().replace('/at/g', 'at<br/>') 
                    + '" /><a href="#" class="excMessage">Click here to view details</a>');
        });
        $('.excMessage').on('click', function() {
            wizardCommonModule.showInfoDialog('Exception details', $(this).parents('td').children('input').attr('value'), 500, 800);
        });
    };
    
    var applyRag = function() {
        $('td').each(function() { 
            if ($(this).text() == "RED") {
                $(this).html('<img src="css/images/bullet_ball_red.png"/>');
            } else if ($(this).text() == "AMBER") {
    	        $(this).html('<img src="css/images/bullet_ball_yellow.png"/>');
    	    } else if ($(this).text() == "GREEN") {
    	        $(this).html('<img src="css/images/bullet_ball_green.png"/>');
    	    } else if ($(this).text() == "UNMAPPED") {
    	        $(this).html('Not mapped');
    	    }
    	});
    };

    init();
};

jQuery(function ($) {
    new Report();
});
