angular.module('App.controllers')
.controller('defaultController', [ '$scope', '$window',
    function($scope, $window) {
        //TODO: replace by admin main page
        $window.location.href = "/";
    }
]);
