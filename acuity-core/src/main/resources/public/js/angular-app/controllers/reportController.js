'use strict';
/**

 */
angular.module('App.controllers')
    .controller('reportController', ['$scope', '$log', 'reportService', '$filter', '$routeParams', function ($scope, $log, ReportService, $filter, $routeParams) {
        var $ctrl = this;

        this.loadDetailedReport = loadDetailedReport;

        $scope.model = ReportService.model;
        $scope.model.uploadTable = {
            pagingOptions: {
                pageSize: 5,
                currentPage: 1
            },
            loading: false,
            sortOptions: {
                sortField: null,
                reverse: false
            }
        };
        $scope.model.reportTable = {
            loading: false,
            sortOptions: [
                {},
                {sortField: 'acuityEntities', reverse: false},
                {sortField: 'dataField', reverse: false},
                {sortField: 'dataField', reverse: false}
            ],
            pagingOptions: [
                {},
                {},
                {},
                {}
            ],
            headerOptions: [
                {
                    titles: ['ETL Step', 'Exception type', 'Details', 'Error status'],
                    tooltips: ['The step in the ETL process that threw the exception.', 'The Java exception class that threw the exception.', 'The detailed message of the exception.', 'The red, amber or green status of this upload. A red status shows that no records have been read from this source file. The most likely cause of this is an incorrect file path. An amber status shows that the total number of unique subjects in the database for this file differs from the total number of unique subjects in the source file. A green status means that the file was read without these errors.']
                },
                {
                    titles: ['ACUITY data table', 'Subjects-source', 'Subjects-data table', 'Current total valid events', 'Error status', 'Raw data source'],
                    tooltips: ['The ACUITY data set collection that the report log message relates to', 'The number of unique subjects in the source file', 'The number of unique subjects in the data table', 'The total number of events, e.g. adverse events, lab measurements, dose events etc., in the ACUITY system after the upload', 'The red, amber or green status of this upload. A red status shows that no records have been read from this source file. The most likely cause of this is an incorrect file path. An amber status shows that the total number of unique subjects in the database for this file differs from the total number of unique subjects in the source file. A green status means that the file was read without these errors.', 'The original file that the data was in prior to upload into ACUITY']
                },
                {
                    titles: ['ACUITY data field', 'Raw data column', 'Error type', 'Error detail', 'Error status', 'Raw data source'],
                    tooltips: ["The ACUITY data field that the log entry relates to", "The column in the raw file that the log entry relates to", "The type of error, e.g. a mapping error or a data error, that lead to the Red, Amber or Green status", "A longer description of the Error type", "The red, amber or green status of this upload. A red status shows that no records have been read from this source file. The most likely cause of this is an incorrect file path. An amber status shows that the total number of unique subjects in the database for this file differs from the total number of unique subjects in the source file. A green status means that the file was read without these errors.", "The original file that the data was in prior to upload into ACUITY"]
                },
                {
                    titles: ['ACUITY data field', 'Raw data column', 'Raw data value', 'Error count', 'Error status', 'Error type', 'Error detail', 'Raw data source'],
                    tooltips: ['The ACUITY data field that the log entry relates to', 'The column in the raw file that the log entry relates to', 'A value of the field that caused the error', 'The number of found errors', "The red, amber or green status of this upload. A red status shows that no records have been read from this source file. The most likely cause of this is an incorrect file path. An amber status shows that the total number of unique subjects in the database for this file differs from the total number of unique subjects in the source file. A green status means that the file was read without these errors.", 'The type of error, e.g. a mapping error or a data error, that lead to the Red, Amber or Green status', 'A longer description of the Error type', 'The original file that the data was in prior to upload into ACUITY']
                }
            ],
            rowOptions: [
                ['etlStep', 'exceptionClass', 'message', 'ragStatus'],
                ['acuityEntities', 'numSubjectsSource', 'numSubjectsAcuity', 'numEventRowsUploaded', 'ragStatus', 'fileName'],
                ['dataField', 'rawDataColumn', 'errorType', 'errorDescription', 'ragStatus', 'fileName'],
                ['dataField', 'rawDataColumn', 'rawDataValue', 'errorCount', 'ragStatus', 'errorType', 'errorDescription', 'fileName']
            ],
            pageSize: 15
        };
        $scope.model.studyUploads = [];
        $scope.model.sortedReports = [];
        $scope.model.reportsData = [];

        $scope.opened = true;

        $scope.togglePanel = function () {
            $scope.opened = !$scope.opened;
        };

        $scope.selectUpload = function (upload) {
            $scope.model.selectedUpload = upload;
            $scope.model.reportType = $scope.model.reportType | 0;
            ReportService.resetDataOptions();
            ReportService.getReportOfType();
        };

        $scope.applyUploadsPagination = function () {
            $scope.model.sortedStudyUploads = $filter('orderBy')($scope.model.studyUploads, $scope.model.uploadTable.sortOptions.sortField, $scope.model.uploadTable.sortOptions.reverse);
            $scope.model.sortedStudyUploads = ReportService.groupToPages($scope.model.sortedStudyUploads, $scope.model.uploadTable.pagingOptions.pageSize);
            $scope.model.uploadTable.pagingOptions.currentPage = 1;
        };

        $scope.applyReportPagination = function () {
            var sortedData = $filter('orderBy')($scope.model.reportsData[$scope.model.reportType], $scope.model.reportTable.sortOptions[$scope.model.reportType].sortField, $scope.model.reportTable.sortOptions[$scope.model.reportType].reverse);
            $scope.model.sortedReports[$scope.model.reportType] = ReportService.groupToPages(sortedData, $scope.model.reportTable.pageSize);
            $scope.model.reportTable.pagingOptions[$scope.model.reportType].currentPage = 1;
        };

        $scope.sort = function (sortField, isUploadTable) {
            var sortOptions = $scope.model.reportTable.sortOptions[$scope.model.reportType];
            if (isUploadTable) {
                sortOptions = $scope.model.uploadTable.sortOptions;
            }
            if (sortOptions.sortField == sortField) {
                sortOptions.reverse = !sortOptions.reverse;
            } else {
                sortOptions.sortField = sortField;
                sortOptions.reverse = false;
            }
            if (isUploadTable) {
                $scope.applyUploadsPagination();
            } else {
                $scope.applyReportPagination();
            }
        };

        $scope.loadReport = function (index) {
            $scope.model.reportType = index;
            if ($scope.model.selectedUpload && !$scope.model.reportsData[index]) {
                ReportService.getReportOfType();
            }
        };

        function loadDetailedReport() {
            $scope.model.studyId = $routeParams.studyId;
            ReportService.getStudyUploads();

        };

        loadDetailedReport();
    }
    ]);
