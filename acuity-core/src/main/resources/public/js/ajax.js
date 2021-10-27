ajaxModule = {};

ajaxModule.sendAjaxRequestWithoutParam = function (byUrl, dialogInfo, successCallback) {
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingDialog();
    }
    jQuery.ajax({
        url: byUrl,
        type: 'POST',
        dataType: 'json',
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        success: function (data, textStatus, jqXHR) {
            if (dialogInfo && dialogInfo.showDialog)
                wizardCommonModule.closeWaitingDialog();
            if (successCallback)
                successCallback(data, jqXHR.statusText);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (dialogInfo && dialogInfo.showDialog)
                wizardCommonModule.closeWaitingDialog();
            if (jqXHR.status == 401 || jqXHR.status == 419) { // Service unavailable - means session expired
                wizardCommonModule.showSessionExpiredDialog();
            } else {
                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
            }
        }
    });
};

ajaxModule.sendQueryAsyncAjaxRequest = function (startUrl, pollUrl, cancelUrl, dialogInfo, params, successCallback) {
    var allow = true;
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingCancelDialog(cancelUrl);
    }
    var request = $.ajax({
        url: startUrl,
        type: "post",
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        data: params,
        dataType: "json"
    });

    var updateCallBack = function (pollUrl, reply) {
        if (updateRequest) {
            updateRequest.abort();
        }
        var updateRequest = $.ajax({
            url: pollUrl,
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            data: reply,
            type: "post"
        });
        updateRequest.done(function (hasBeenCanceled) {
            wizardCommonModule.closeWaitingCancelDialog();
            if (!hasBeenCanceled) {
                if (successCallback) {
                    successCallback();
                }
            }
        });

        updateRequest.always(function () {
            allow = true;
        });

        updateRequest.fail(function (jqXHR, textStatus, errorThrown) {
            wizardCommonModule.closeWaitingCancelDialog();
            if (jqXHR.status != 404) {
                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
            }
        });
    };

    request.done(function (reply) {
        if (allow === true) {
            allow = false;
            updateCallBack(pollUrl, reply);
        }
    });

    request.fail(function (jqXHR, textStatus, errorThrown) {
        wizardCommonModule.closeWaitingCancelDialog();
        if (jqXHR.status != 404) {
            wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
        }

    });
};

ajaxModule.sendAjaxRequestSimpleParams = function (byUrl, params, dialogInfo, successCallback, sendJson) {
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingDialog();
    }
    jQuery.ajax({
        url: byUrl,
        type: 'POST',
        data: params,
        dataType: 'json',
        contentType: sendJson ? 'application/json' : undefined,
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        success: function (data, textStatus, jqXHR) {
            wizardCommonModule.closeWaitingDialog();
            successCallback(data, jqXHR.statusText);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            wizardCommonModule.closeWaitingDialog();
            if (jqXHR.status == 401 || jqXHR.status == 419) { // Service unavailable - means session expired
                wizardCommonModule.showSessionExpiredDialog();
            }
            else if (jqXHR.status == 403) { // Access denied
                wizardCommonModule.showAccessDeniedDialog(jqXHR.responseText);
            } else {
                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
            }
        }
    });
};

ajaxModule.csrf =
    {
        token: $('meta[name=_csrf]').attr('content'),
        param: $('meta[name=_csrf_param]').attr('content')
    };

ajaxModule.uploadData = function (form, dialogInfo, successCallback, byUrl) {
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingDialog();
    }
    var options = {
        url: byUrl,
        dataType: 'json',
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        success: function (data) {
            wizardCommonModule.closeWaitingDialog();
//         if($.browser.msie){
//            var index1 = data.indexOf("{");
//            var index2 = data.lastIndexOf("}") + 1;
//            data = data.substring(index1, index2);
//         }
//         data = $.parseJSON(data);
            successCallback(data);
        },
        error: function (jqXHR) {
            wizardCommonModule.closeWaitingDialog();
            if (jqXHR.status == 401 || jqXHR.status == 419) { // Service unavailable - means session expired
                wizardCommonModule.showSessionExpiredDialog();
            } else if (jqXHR.status == 403) { // Access denied
                wizardCommonModule.showErrorDialog('Access denied', jqXHR.responseText);
            } else {
                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);

            }
        }
    };

    form.ajaxSubmit(options);
};


ajaxModule.uploadFormData = function (byUrl, formData, dialogInfo, successCallback) {
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingDialog();
    }

    $.ajax({
        url: byUrl,
        data: formData,
        dataType: 'text',
        processData: false,
        contentType: false,
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        type: 'POST',
        success: function (response) {
            wizardCommonModule.closeWaitingDialog();
            successCallback(response);
        }
    });
};


ajaxModule.sendAjaxRequest = function (byUrl, params, dialogInfo, successCallback, errorCallback) {
    if (dialogInfo && dialogInfo.showDialog) {
        wizardCommonModule.showWaitingDialog();
    }

    jQuery.ajax({
        url: byUrl,
        type: 'POST',
        dataType: 'json',
        data: params,
        contentType: 'application/json',
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        error: function (jqXHR, textStatus, errorThrown) {
            wizardCommonModule.closeWaitingDialog();
            if (errorCallback) {
                errorCallback();
            } else {
                if (jqXHR.status == 403) { // Access denied
                    wizardCommonModule.showErrorDialog('Access denied', jqXHR.responseText);
                } else if (jqXHR.status == 401 || jqXHR.status == 419) { // Service unavailable - means session expired
                    wizardCommonModule.showSessionExpiredDialog();
                } else {
                    wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
                }
            }
        },
        success: function (response) {
            wizardCommonModule.closeWaitingDialog();
            if (successCallback) {
                successCallback(response);
            }
        }
    });
};

