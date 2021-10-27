<meta name="_csrf" content="${_csrf.token}"/>
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta name="_csrf_param" content="${_csrf.parameterName}"/>

<!-- styles -->
<link rel="stylesheet/less" type="text/css" href="css/reset.css">
<link type="text/css" href="css/style.css" rel="stylesheet"/>
<link type="text/css" href="css/jquery.multiselect.css" rel="stylesheet"/>
<link type="text/css" href="css/tipTip.css" rel="stylesheet" />
<link type="text/css" href="css/help.css" rel="stylesheet" />

<!-- scripts -->
<script type="text/javascript" src="js/js-lib/jquery-1.10.1.js"></script>
<script type="text/javascript" src="js/js-lib/jquery-migrate.js"></script>
<script type="text/javascript" src="js/js-lib/jquery-ui-1.10.3.custom.min.js"></script>
<script type="text/javascript" src="js/js-lib/i18n/grid.locale-en.js"></script>
<script type="text/javascript" src="js/js-lib/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/js-lib/jquery.ui.datepicker.validation.min.js"></script>
<script type="text/javascript" src="js/js-lib/jquery.multiselect.min.js"></script>
<script type="text/javascript" src="js/js-lib/jquery.tipTip.minified.js"></script>
<script type="text/javascript" src="js/js-lib/jquery.parse.min.js"></script>
<script type="text/javascript" src="js/js-lib/lodash.min.js"></script>
<script type="text/javascript" src="js/help.js"></script>
<script type="text/javascript" src="js/ajax.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/set-tab-id-cookie.js"></script>

<script type="text/javascript">
    $(function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    });
</script>

