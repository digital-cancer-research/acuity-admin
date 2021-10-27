'use strict';
/**

 */
angular.module('App.controllers')
    .controller('uploadSummaryController', ['$scope', '$log', 'uploadSummaryService',
        function ($scope, $log, UploadSummaryService) {
            $scope.model = UploadSummaryService.model;
            $scope.toggleFrom = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.fromOpened = !$scope.fromOpened;
            };

            $scope.toggleTo = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.toOpened = !$scope.toOpened;
            };

            $scope.update = function () {
                UploadSummaryService.getUploadSummary($scope.dateFrom, $scope.dateTo);
            };
        }
    ]);
