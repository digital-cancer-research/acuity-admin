<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="header">
	<div class="login-info">
		Logged in as:
		<span class="login-name"><sec:authentication property="name" /></span>

	</div>
</div>
<hr/>