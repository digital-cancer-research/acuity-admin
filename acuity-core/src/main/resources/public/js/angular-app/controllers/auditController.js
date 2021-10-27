angular.module('App.controllers')
    .controller('auditController',
    function ($scope, $routeParams, $location, auditFactory, $filter, $timeout) {
        $scope.audit = {
            items: [],
            total: 0
        };

        $scope.sortBy = 'timestamp';
        $scope.sortReverse = true;
        $scope.currentPage = 1;
        $scope.itemsPerPage = 10;


        var getHistory = function () {
            $scope.showWait();
            auditFactory.getHistory($scope.currentPage, $scope.itemsPerPage, $scope.sortBy, $scope.sortReverse, function (data) {
                $scope.audit = data;
                $scope.hideWait();
            }, $scope.showError);
        };

        $scope.sort = function (field) {
            $scope.currentPage = 1;
            if (field == $scope.sortBy) {
                $scope.sortReverse = !$scope.sortReverse;
            } else {
                $scope.sortBy = field;
                $scope.sortReverse = false;
            }
            getHistory();
        };

        $scope.pageChanged = function () {
            getHistory();
        };

        getHistory();
    }
);
