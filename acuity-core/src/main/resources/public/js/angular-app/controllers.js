'use strict';

/* Controllers */

angular.module('App.controllers', [])
.controller('mainController', ['$window', '$http', '$scope', '$modal','userInfoFactory', '$timeout',
    function($window, $http, $scope, $modal, userInfoFactory, $timeout) {

        // This is model for global manipulation of data in all controllers.
        $scope.MODEL = {
            dialogWait: null,
            userInfo:null,
            optionsWaitDlg: {
                templateUrl: 'waitDialogTmpl',
                backdrop: 'static',
                keyboard: false,
                windowClass: 'dlg-wait',
                size: 'sm',
                controller: function ($scope, $modalInstance) {},
                resolve: {   }
            }
        };

        var waitTimeout;

        $scope.showWait = function () {
            if (!waitTimeout && !$scope.MODEL.dialogWait) {
                waitTimeout = $timeout(function () {
                    waitTimeout = null;
                    $scope.MODEL.dialogWait = $modal.open($scope.MODEL.optionsWaitDlg);

                }, 500);
            }
        };

        $scope.showError = function(){
        };

        $scope.hideWait = function () {
            if (waitTimeout) {
                $timeout.cancel(waitTimeout);
                waitTimeout = null
            }
            if ($scope.MODEL.dialogWait) {
                $scope.MODEL.dialogWait.dismiss();
                $scope.MODEL.dialogWait = null;
            }
        };

        $scope.logout = function(){
            $http.post('/j_spring_security_logout')
            .success(function(){
                $window.location.href = "/login";
            });
        };

        var init = function(){
            userInfoFactory.getUserInfo(function(data){
                $scope.MODEL.userInfo = data.displayName;
            });
        };

        init();
    }
]);
