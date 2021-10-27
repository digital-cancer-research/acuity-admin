<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ACUITY Jobs</title>

    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet" media="screen"/>
    <link type="text/css" href="css/ui.jqgrid.css" rel="stylesheet" media="print"/>
    <link type="text/css" href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet" media="screen"/>
    <%@ include file="/WEB-INF/jsp/includes.jsp" %>
    <%@ include file="/WEB-INF/jsp/wizard-common.jsp" %>

    <script type="text/javascript" src="js/js-lib/i18n/grid.locale-en.js"></script>

    <script type="text/javascript" src="js/js-lib/jquery.jqGrid.min-4.5.4.js"></script>

    <script type="text/javascript" src="js/js-lib/throttle-debounce-1.1.js"></script>
    <script type="text/javascript" src="js/js-lib/noty/jquery.noty.js"></script>
    <script type="text/javascript" src="js/js-lib/noty/bottomLeft.js"></script>
    <script type="text/javascript" src="js/js-lib/noty/top.js"></script>
    <script type="text/javascript" src="js/js-lib/noty/default.js"></script>
    <script type="text/javascript" src="js/wizard-common.js"></script>
    <script type="text/javascript" src="js/scheduler.js"></script>
    <script type="text/javascript">
        $(function () {
            schedulerModule.setAmlEnabledGlobally(${amlEnabledGlobally})
            schedulerModule.fillData('${data}');
        });
    </script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<div id="schedulingTableResult">
    <table id="schedulingTable"></table>
    <div id="gridpager"></div>
</div>
</body>
</html>
