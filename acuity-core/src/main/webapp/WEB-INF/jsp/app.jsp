<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="App">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACUITY</title>
    <link href="js/js-lib/bootstrap-3.4.1/css/bootstrap.min.css" rel="stylesheet" />
    <link href="js/js-lib/ui-grid/ui-grid.min.css" rel="styleSheet"/>
    <link href="css/ng-sortable/ng-sortable.min.css" rel="styleSheet"/>
    <link href="css/app.css" rel="stylesheet" />

    <script type="text/javascript" src="js/js-lib/jquery-1.10.1.js"></script>
    <script type="text/javascript" src="js/js-lib/lodash.min.js"></script>
    <script type="text/javascript" src="js/js-lib/angular-1.3.3/angular.js"></script>
    <script type="text/javascript" src="js/js-lib/angular-1.3.3/angular-route.js"></script>
    <script type="text/javascript" src="js/js-lib/angular-1.3.3/angular-resource.js"></script>

    <script type="text/javascript" src="js/js-lib/ui-bootstrap-0.11.0/ui-bootstrap-tpls-0.11.0.min.js"></script>

    <script type="text/javascript" src="js/js-lib/ui-grid/ui-grid.min.js"></script>


    <script type="text/javascript" src="js/js-lib/ng-sortable/ng-sortable.min.js"></script>
    <script type="text/javascript" src="js/angular-app/app.js"></script>
    <script type="text/javascript" src="js/angular-app/services.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/auditController.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/fileViewController.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/defaultController.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/reportController.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/subjectGroupingsController.js"></script>
    <script type="text/javascript" src="js/angular-app/controllers/uploadSummaryController.js"></script>
</head>
<body>
    <div ng-include src="'views/header/_header.html'"></div>
     <div class="container-fluid"  ng-controller="mainController" ng-view >
     </div>
     <script type="text/ng-template" id="waitDialogTmpl">
         <div class="modal-body" id="dlgWait">
             <img src="css/images/loader.gif">
             <p>Please wait...</p>
         </div>
     </script>
</body>
</html>
