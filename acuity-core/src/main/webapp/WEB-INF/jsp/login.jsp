<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
  ~ Copyright 2021 The University of Manchester
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>ACUITY login page</title>
	<link rel="stylesheet" type="text/css" href="css/smoothness/jquery-ui-1.10.3.custom.css" />
	<link rel="stylesheet/less" type="text/css" href="css/login.less" />
	<script type="text/javascript" src="js/js-lib/less.js"></script>
	<%@ include file="/WEB-INF/jsp/includes.jsp" %>
	<script type="text/javascript" src="js/js-lib/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="js/js-lib/noty/jquery.noty.js"></script>
	<script type="text/javascript" src="js/js-lib/noty/bottomLeft.js"></script>
	<script type="text/javascript" src="js/js-lib/noty/top.js"></script>
	<script type="text/javascript" src="js/js-lib/noty/default.js"></script>
	<script type="text/javascript" src="js/login.js"></script>
</head>
<body>
	<div class="login-form">

		<h1>Login Form</h1>

		<form name='f' action="<c:url value='/loginProcess' />" method="POST" id="loginf" autocomplete="off">

			<input type="text" name="j_username"  id="j_username"/>

			<input type="password" name="j_password"  id="j_password"/>

			<input id="submitBtn" type="button" value="login" class="button"  />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		</form>

	</div>
</body>

</html>
