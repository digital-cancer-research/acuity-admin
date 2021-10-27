var loginModule = {};

loginModule.showError = function(param) {
	var inputs = document.getElementsByTagName("input"), len = inputs.length, i, exception;
	for (i = 0; i < len; i++) {
		if (inputs[i].name == "exceptionStorage") {
			exception = inputs[i].value;
			break;
		}
	}
	noty({
		text : "Your login attempt was not successful, try again or contact the Support team.<br /> ",
		layout : 'bottomLeft',
		type : 'error'
	});
};

$(function() {
	var param = location.search;
	if (param && param.length && param.indexOf("login_error") !== -1) {
		loginModule.showError(param);
	}
	
	$("input").keypress(function(event) {
	    if (event.which == 13) {
	        event.preventDefault();
	        $(this).parent("form").submit();
	    }
	});
	
	$("#submitBtn").on("click", function(){
		$('#loginf').submit();
	});
});