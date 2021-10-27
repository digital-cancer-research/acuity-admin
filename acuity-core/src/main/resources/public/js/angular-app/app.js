'use strict';

var app = angular.module('App', [
    'App.controllers',
    'App.services',
    'ngRoute',
    'as.sortable',
    'ui.bootstrap',
    'ui.grid',
    'ui.grid.resizeColumns',
    'ui.grid.pagination'
]);
app.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(false);

    $routeProvider.when('/audit/', {
        templateUrl: 'views/audit/index.html',
        controller: 'auditController'
    });
    $routeProvider.when('/upload-summary/', {
        templateUrl: 'views/uploadSummary/index.html',
        controller: 'uploadSummaryController'
    });

    $routeProvider.when('/fileView/', {
        templateUrl: 'views/fileView/index.html',
        controller: 'fileViewController'
    });

    $routeProvider.when('/report/:studyId', {
        templateUrl: 'views/report/report.html',
        controller: 'reportController'
    });

    $routeProvider.when('/', {
        templateUrl: 'views/main.html',
        controller: 'defaultController'
    });

    $routeProvider.when('/groupings/:studyId', {
        templateUrl: 'views/subjectGroupings/subjectGroupings.html',
        controller: 'subjectGroupingsController',
    });

    $routeProvider.otherwise({ redirectTo: '/' });

}]);