angular.module('App.controllers')
    .controller('fileViewController', function ($scope, fileViewFactory, $routeParams) {
        var fileUrl = $routeParams.fileUrl;

        var paginationOptions = {
            pageNumber: 1,
            pageSize: 25
        };

        $scope.gridOptions = {
            enableSorting: true,
            paginationPageSizes: [25, 50, 75],
            paginationPageSize: 25,
            useExternalPagination: true,
            useExternalSorting: true,
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                $scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                    if (sortColumns) {
                        paginationOptions.sortColumns = _.map(sortColumns, function (item) {
                            return {
                                columnIndex: _.indexOf($scope.gridOptions.columnNames, item.field),
                                sortOrder: item.sort.direction == "asc" ? 1 : -1
                            }
                        });
                    } else {
                        paginationOptions.sortColumns = null;
                    }
                    listData();
                });
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    paginationOptions.pageNumber = newPage;
                    paginationOptions.pageSize = pageSize;
                    listData();
                });
            },
            data: [],
            columnDefs: [],
            paginationCurrentPage: 1
        };

        function listData() {
            var firstRow = (paginationOptions.pageNumber - 1) * paginationOptions.pageSize;
            $scope.gridOptions.data = [];

            $scope.showWait();
            fileViewFactory.listData(fileUrl, firstRow, paginationOptions.pageSize, paginationOptions.sortColumns).
                success(function (data) {
                    $scope.hideWait();
                    $scope.gridOptions.data = _.map(data.data, function (row) {
                        return _.zipObject($scope.gridOptions.columnNames, row);
                    });
                    $scope.gridApi.core.refresh();
                }).error(function () {
                    $scope.hideWait();
                    $scope.showError();
                });
        }

        function loadData() {
            $scope.showWait();
            fileViewFactory.loadFile(fileUrl).
                success(function (data) {
                    $scope.hideWait();
                    $scope.gridOptions.totalItems = data.total;
                    $scope.gridOptions.columnNames = _.pluck(data.fields, 'name');
                    $scope.gridOptions.columnDefs = _.map(data.fields, function (item) {
                        return {
                            name: item.name,
                            displayName: item.name,
                            width: item.name.length * 15 + 10
                        };
                    });
                    listData();
                }).error(function () {
                    $scope.hideWait();
                    $scope.showError();
                });
        }

        if (fileUrl) {
            loadData();
        }
    });
