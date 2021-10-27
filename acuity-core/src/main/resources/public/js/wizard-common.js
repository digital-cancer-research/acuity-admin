var wizardCommonModule = {
    GRID_MAX_ROW_NUM: 200,
    waitingAjaxCounter: 0
};

wizardCommonModule.showWaitingDialog = function(message) {
    if (wizardCommonModule.waitingAjaxCounter++ < 1) {
        $("#waitingDlg").dialog({
            modal: true,
            resizable: false,
            minHeight: 50,
            closeOnEscape: false,
            dialogClass: 'no-close'
        });
    }
};

wizardCommonModule.showWaitingCancelDialog = function(cancelUrl) {
   $("#waitingCancelDlg").data("cancelUrl", cancelUrl).dialog({
      modal: true,
      resizable: false,
      minHeight: 50,
      dialogClass:'no-close',
      closeOnEscape: false,
      create: function(){
         $("#waitingDlgCancelBtn").on('click', function(){
              var url = $("#waitingCancelDlg").data("cancelUrl");
              ajaxModule.sendAjaxRequestWithoutParam(url ,{showDialog:false},function(result){
                  wizardCommonModule.closeWaitingCancelDialog();
              });

         });
      }
   });
};

wizardCommonModule.showProgressDialog = function(message) {
   $("#progressDlg").dialog({
      modal: true,
      resizable: false,
      width:"auto",
      minHeight: 50,
      closeOnEscape: false,
      maxWidth: 800,
      fluid: true, //added new option (fix dialog bug (width:auto))
      dialogClass:'no-close',
      open : function(){
       $("#progressDlgInfo").html(wizardCommonModule.htmlScriptEscape(message));
        wizardCommonModule.fluidDialog();
      }
   });
};

wizardCommonModule.showErrorDialog = function(statusText, message, closeCallback) {
   $("#errorDlg").dialog({
      modal: true,
      width: 600,
      minHeight: 50,
      resizable: true,
      open : function(){
       $("#error-block").hide();
       $("#errorDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));
      },
      close: function () {
          if (closeCallback) closeCallback();
      },
      create: function(){
        $("#errorDlgOkBtn").on("click", function(){
            $("#errorDlg").dialog("close");
        });
        $("#errorDlgStackBtn").on("click", function(){
           $("#error-block").show();
        });
         $("#linkToACUITYSupport").on("click", function(){
              var body = "";
              var errorText = $("#errorDlgMessage").html();
              if($.trim(errorText).length > 0){
                if($.browser.msie){
                    body = errorText.substring(0, 320);
                }else{
                    body = errorText.substring(0, 1650);
                }
              }

              var link = "mailto:mail@com" // TODO Change to a desired support email
              +"&subject=" + escape("ACUITY Error")
              +"&body=" + escape("Dear ACUITY Support Team, \n user " + encodeURIComponent($("#userPridStorage").html()) + " experiend the following error:\n ") + encodeURIComponent(body);
              window.location.href = link;
         });
          $("#errorDlg").data( "uiDialog" )._title = function(title) {
                     title.html( this.options.title );
          };
          $("#errorDlg").dialog('option', 'title', '<span class="ui-icon ui-icon-alert"></span>' + statusText);
      }
   });
};

wizardCommonModule.showWarningDialog = function(message, dlgWidth, closeCallback) {
   $("#warningDlg").dialog({
      modal: true,
       width: dlgWidth || 750,
      minHeight: 50,
      resizable: true,
      open : function(){
       $("#warningDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));
      },
      close: function () {
          if (closeCallback) closeCallback();
      },
      create: function(){
        $("#warningDlgOkBtn").on("click", function(){
            $("#warningDlg").dialog("close");
        });
        $("#warningDlg").data( "uiDialog" )._title = function(title) {
            title.html( this.options.title );
        };
        $("#warningDlg").dialog('option', 'title', '<span class="ui-icon ui-icon-alert"></span> Warning');
      }
   });
};

wizardCommonModule.showInfoDialog = function(titleText, message, maxHeight, width, closeCallback) {
   $("#infoDlg").dialog({
      title: titleText,
      modal: true,
      width: width == null ? 250 : width,
      minHeight: 50,
      maxHeight: maxHeight == null ? false : maxHeight,
      resizable: false,
      open : function(){
       $("#infoDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));
      },
      create: function(){
        $("#infoDlgOkBtn").on("click", function(){
            $("#infoDlg").dialog("close");
        });
      },
      close: function() {
        if (closeCallback) {
            closeCallback();
        }
      }
   });
};

wizardCommonModule.showYesNoDialog = function(title, message, yesCallback, noCallback, dlgWidth, params) {
    $("#yesNoDlg").data('yesCallBack', yesCallback).data('noCallback', noCallback).data('params', params).dialog({
        title: title,
        modal: true,
        width: dlgWidth ? dlgWidth : 250,
        minHeight: 50,
        resizable: false,
        draggable: false,
        open: function() {
         $("#yesNoDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));

        },
        create: function(){
         $("#yesNoDlgYesBtn").on("click", function() {
            var dlg = $("#yesNoDlg");
            dlg.dialog("close");
            var yesCallback = dlg.data('yesCallBack');
            var params = dlg.data('params');
            yesCallback(params);
          });

         $("#yesNoDlgNoBtn").on("click", function() {
             var dlg = $("#yesNoDlg");
             dlg.dialog("close");
             var noCallback = dlg.data('noCallback');
            if(noCallback)
                noCallback();
         });

        }
    });
};

wizardCommonModule.showSaveDialog = function(title, message, saveCallBack, discardCallback, noCallback, data) {
    $("#saveDlg").data('saveCallBack', saveCallBack).data('discardCallback', discardCallback)
    .data('noCallback', noCallback).data('data', data).dialog({
        title: title,
        modal: true,
        width: 250,
        minHeight: 50,
        resizable: false,
        open: function() {
         $("#saveDlgMessage").html(wizardCommonModule.htmlScriptEscape(message));

        },
        create: function(){
         $("#saveDlgSaveBtn").on("click", function() {
            var dlg = $("#saveDlg");
            dlg.dialog("close");
            var saveCallBack = dlg.data('saveCallBack');
            var data = dlg.data('data');
            saveCallBack(data);
          });

          $("#saveDlgDiscardBtn").on("click", function() {
            var dlg = $("#saveDlg");
            dlg.dialog("close");
            var discardCallback = dlg.data('discardCallback');
            var data = dlg.data('data');
            discardCallback(data);
          });

         $("#saveDlgCancelBtn").on("click", function() {
             var dlg = $("#saveDlg");
             dlg.dialog("close");
             var noCallback = dlg.data('noCallback');
             var data = dlg.data('data');
             if(noCallback)
                noCallback(data);
         });

        }
    });
};

wizardCommonModule.closeWaitingDialog = function() {
    if (wizardCommonModule.waitingAjaxCounter-- < 2) {
        if($("#waitingDlg").hasClass('ui-dialog-content')  &&  $("#waitingDlg").dialog("isOpen")){
            $("#waitingDlg").dialog("close");
        }
    }
};

wizardCommonModule.closeWaitingCancelDialog = function(message) {
    if($("#waitingCancelDlg").hasClass('ui-dialog-content')  &&  $("#waitingCancelDlg").dialog("isOpen")){
         $("#waitingCancelDlg").dialog("close");
    }
};

wizardCommonModule.closeProgressDialog = function() {
    if( $("#progressDlg").hasClass('ui-dialog-content') && $("#progressDlg").dialog("isOpen")){
         $("#progressDlg").dialog("close");
    }
};

wizardCommonModule.showSessionExpiredDialog = function(){
    $("#sessionExpiredDlg").dialog({
        modal: true,
        resizable: false,
        closeOnEscape: false,
        dialogClass: 'no-close',
        create: function() {
            $("#sessionExDlgOkBtn").on("click", function() {
                $("#sessionExpiredDlg").dialog("close");
                window.location = "/";
            });
           $("#sessionExpiredDlg").data( "uiDialog" )._title = function(title) {
                title.html( this.options.title );
           };
           $("#sessionExpiredDlg").dialog('option', 'title', '<span class="ui-icon ui-icon-alert"></span> Session Expired');
        }
    });
};

wizardCommonModule.showAccessDeniedDialog = function(message){
    $("#accessDeniedDlg").dialog({
        modal: true,
        resizable: false,
        closeOnEscape: false,
        dialogClass: 'no-close',
        open : function(){
         $("#accessDeniedMsg").html(wizardCommonModule.htmlScriptEscape(message));
        },
        create: function() {
            $("#accessDeniedDlgOkBtn").on("click", function() {
                $("#accessDeniedDlg").dialog("close");
            });
           $("#accessDeniedDlg").data( "uiDialog" )._title = function(title) {
                title.html( this.options.title );
           };
           $("#accessDeniedDlg").dialog('option', 'title', '<span class="ui-icon ui-icon-alert"></span> Access denied');
        }
    });
};

wizardCommonModule.setHeader = function(text){
	$(".wizard-header").text(text);
};

wizardCommonModule.setStepSubHeading = function(text){
	$(".wizard-step-info").text(text);
};

wizardCommonModule.showFirstStepButtons = function(){
	$('#smartBtnNext').show();
	$('#smartBtnPrevious').hide();
    $('#smartBtnFinish').hide();
    $('#smartBtnAdminPage').show();
};

wizardCommonModule.showCommonStepButtons = function(){
	$('#smartBtnNext').show();
	$('#smartBtnPrevious').show();
    $('#smartBtnFinish').hide();
    $('#smartBtnAdminPage').hide();
};

wizardCommonModule.showFinishStepButtons = function(){
	$('#smartBtnNext').hide();
	$('#smartBtnPrevious').show();
    $('#smartBtnFinish').show();
    $('#smartBtnAdminPage').hide();
};

wizardCommonModule.blindingFormatter = function(cellvalue, options, rowObject){
	return cellvalue ? 'Blinded' : 'Not blinded';
};

wizardCommonModule.regulatoryFormatter = function(cellvalue, options, rowObject){
	return cellvalue ? 'Regulated' : 'Not regulated';
};

wizardCommonModule.randFormatter = function(cellvalue, options, rowObject){
	return cellvalue? 'Randomised' : 'Not randomised';
};


wizardCommonModule.resizeWizard = function(stepIndex, wizardId) {
    var marginContent = parseInt($('.stepContainer').css('margin-top')) 
        + parseInt($('.stepContainer').css('margin-bottom'));

    var marginActionBar = parseInt($('.sw-toolbar').css('margin-bottom')) 
        + parseInt($('.sw-toolbar').css('margin-top'));

    var headerHeight = $('.header').height();

    var value =  marginContent +  marginActionBar + headerHeight 
        + $("div#step-" + (stepIndex + 1) + ".step-content").height() 
        + $(".sw-toolbar").height();

    $("#" + wizardId).height(value);
};

wizardCommonModule.formatDate = function(val){
	if(val == null || val == "")
		return "";
	if(!$.datepicker)
		return val;
	return $.datepicker.formatDate("dd-M-yy",new Date(val));
};

$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

wizardCommonModule.isSlaveTableEditable = function(slaveTable) {
   var ids = slaveTable.jqGrid('getDataIDs');
   for(var i=0; i<ids.length; i++){
       var rowid = ids[i];
       var ind = slaveTable.jqGrid("getInd",rowid,true);
       var result = false;
       $('td[role="gridcell"]', ind).each(function(i){
           if($(this).hasClass('edit-cell')){
              result = true;
              return false;
           }
       });
       if(result){
          return true;
       }
   }
   return false;
};

wizardCommonModule.fluidDialog = function() {
    var $visible = $(".ui-dialog:visible");
    // each open dialog
    $visible.each(function () {
        var $this = $(this);
        var dialog = $this.find(".ui-dialog-content").data("uiDialog");
        if (dialog.options.maxWidth && dialog.options.width) {
            $this.css("max-width", dialog.options.maxWidth);
            dialog.option("position", dialog.options.position);
        }

        if (dialog.options.fluid) {
            $(window).on("resize.responsive", function () {
                var wWidth = $(window).width();
                if (wWidth < dialog.options.maxWidth + 50) {
                    $this.css("width", "90%");

                }
              dialog.option("position", dialog.options.position);
            });
        }

    });
};

wizardCommonModule.onResizeStop  = function(tableId, tableWidth){
    var model = $("#" + tableId).jqGrid('getGridParam','colModel');
    var sumColumnWidth = 0;
    for (var i = 0; i < model.length; i++) {
         sumColumnWidth += model[i].width;
    }
    if(sumColumnWidth <= tableWidth){
       wizardCommonModule.resizeTable(tableId, sumColumnWidth);
    }else if(sumColumnWidth > tableWidth){
        wizardCommonModule.resizeTable(tableId, tableWidth);
    }
};

wizardCommonModule.resizeTable  = function(tableId, width){
     $("#" + tableId).setGridWidth(width, true);
};

wizardCommonModule.getURLParameter = function(name) {
    return decodeURI(
        (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
};

wizardCommonModule.htmlEncode = function (value){
  return $('<div/>').text(value).html();
};

wizardCommonModule.htmlDecode = function (value){
  return $('<div/>').html(value).text();
};

wizardCommonModule.htmlEscape = function (str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
};

wizardCommonModule.htmlScriptEscape = function (str) {
    return String(str)
        .replace(/<script>/g, '&lt;script&gt;')
        .replace(/<\/script>/g, "&lt;\/script&gt;");
};

wizardCommonModule.htmlUnescape = function htmlUnescape(value){
    return String(value)
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&');
};

wizardCommonModule.validateSambaCsvSasFilePath = function (filepath) {
    /*
     \\servername\sharename\file.csv - GOOD
     \\servername\sharename\somepath\file.csv - GOOD
     \\servername\sharename\somepath\file.sas7bdat - GOOD
     \\file.csv - BAD (absent server name)
     \\servername\file.csv - BAD (absent share name)
     \\servername\sharename\file.tsv - BAD (unknown extension)
     */
    return /^\\\\[\w\.-]+\\[\w\.$-]+(\\[^\\<>:\*"\?\|/]+)+\.(csv|sas7bdat)$/i.test(filepath);
};

wizardCommonModule.validateSambaSasFilePath = function (filepath) {
    return /^\\\\[\w\.-]+\\[\w\.$-]+(\\[^\\<>:\*"\?\|/]+)+\.(sas7bdat)$/i.test(filepath);
};

wizardCommonModule.endsWithCsvSas = function (filepath) {
    filepath=filepath.toLowerCase();
    var csv = ".csv";
    var sas = '.sas7bdat';

    return filepath.indexOf(csv, filepath.length - csv.length) !== -1 ||
    filepath.indexOf(sas, filepath.length - sas.length) !== -1;
}

$.widget('custom.catcomplete', $.ui.autocomplete, {
    _renderMenu: function (ul, items) {
        var that = this, currentCategory = '';
        $.each(items, function (index, item) {
            if (item.category != currentCategory) {
                ul.append('<li style="font-weight: bold;">' + item.category + ':</li>');
                currentCategory = item.category;
            }
            that._renderItemData(ul, item);
        });
    }
});

function createCookie(name, value, days) {
    var expires;

    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    } else {
        expires = "";
    }
    document.cookie = encodeURIComponent(name) + "=" + encodeURIComponent(value) + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = encodeURIComponent(name) + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return decodeURIComponent(c.substring(nameEQ.length, c.length));
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name, "", -1);
}
