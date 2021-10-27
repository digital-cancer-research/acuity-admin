var ProjectGroupingsStep = function(studyWizard){

   	this.studyWizard = studyWizard;
   	this.selectedAe = null;
   	this.aeGroupingsTableId = 'AEGroupingsTable';
    this.labGroupingsTableId = 'labGroupingsTable';
   	var scope = this;

   	var init = function(){
        createAEGroupingsTable();
        createLabGroupingsTable();
   	};

   	var createAEGroupingsTable = function() {
    	$("#" + scope.aeGroupingsTableId).jqGrid(
    			{
    		        datatype: "local",
    	            autoencode: true,
    		        width: "25%",
    		        height: "100%",
    		        colNames:['','Select', 'Grouping Name'],
    		        colModel: [
    		            {name:'id', index:'id', width:70, align:"center", key:true,hidden:true},
    		          	{name:'select', index:'select', width:70, align:"center",
    			            	formatter: function (cellvalue, options, rowObject) {
    			          	      return '<input type="checkbox" name="selectedAEGroup" value="' +
    			          	       wizardCommonModule.htmlEscape(rowObject.id) + '"/>';
    			          	  }},
    		            {name:'name',index:'name', width:150,   align:"center"},
    		        ],
    		        recordpos: 'left',
    		       	viewrecords: true,
    		       	gridview: true,
    		       	rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
    		       	resizeStop: function(width, index) {
                        scope.studyWizard.recalculateSplit(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX);
                    },
    		       	onSelectRow: selectGroupHandler,
    			    loadComplete: function(){
    			    	$("input[name='selectedAEGroup']").change(function () {
    			    		jQuery("#" + scope.aeGroupingsTableId).setSelection(this.value, true);
    			    	});
    			    }
                });
    };

    var selectGroupHandler = function(id){
    	$("input[type='checkbox']",this).removeAttr("checked");
    	if($(this).data('selected') && $(this).data('selected') == id){
    		$(this).data('selected',null);
    		$(this).jqGrid('resetSelection');
    		scope.selectedAe = null;
    		return false;
    	}
    	$("#" + id + " input[type='checkbox']",this).attr("checked",true);
    	$(this).data('selected',id);
    };

    var createLabGroupingsTable = function() {
    	$("#" + scope.labGroupingsTableId).jqGrid(
    			{
    				datatype: "local",
    	            autoencode: true,
    				width: "25%",
    				height: "100%",
    				colNames:['','Select', 'Grouping Name'],
    				colModel: [
    		            {name:'id', index:'id', width:70, align:"center", key:true,hidden:true},
    		          	{name:'select', index:'select', width:70, align:"center",
    			            	formatter: function (cellvalue, options, rowObject) {
    			          	      return '<input type="checkbox" name="selectedLabGroup" value="' +
    			          	      wizardCommonModule.htmlEscape(rowObject.id) + '"/>';
    			          	  }},
    		            {name:'name',index:'name', width:150,   align:"center"},
    		        ],
    		        recordpos: 'left',
    		       	viewrecords: true,
    		       	gridview: true,
    		       	rowNum: wizardCommonModule.GRID_MAX_ROW_NUM,
                    resizeStop: function(width, index) {
                        scope.studyWizard.recalculateSplit(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX);
                    },
    		       	onSelectRow: selectGroupHandler,
    			    loadComplete: function(){
    			    	$("input[name='selectedLabGroup']").change(function () {
    			    		jQuery("#"+ scope.labGroupingsTableId).setSelection(this.value, true);
    			    	});
    			    }
    			});
    };


    this.showProjectGroupingsData = function() {
    	ajaxModule.sendAjaxRequestSimpleParams("study-get-project-groupings", {}, {showDialog: false}, function(result){
    	    var aeTable = $("#" + scope.aeGroupingsTableId);
    	    var labTable = $("#" + scope.labGroupingsTableId);
    		aeTable.jqGrid('clearGridData').trigger('reloadGrid');
    		labTable.jqGrid('clearGridData').trigger('reloadGrid');
    		aeTable.data('selected', null);
            aeTable.jqGrid('resetSelection');
            labTable.data('selected', null);
            labTable.jqGrid('resetSelection');
    		if(result.ae.length == 0 && result.lab.length == 0){
    			$("#pt-grp-msg").show();
    			$("#pt-grp-msg span").text(scope.studyWizard.searchProgrammeStudyStep.selectedProject.drugId);
    			$("#pt-grp-msg a").unbind("click");
    			$("#pt-grp-msg a").on("click", function(){
                   $("#projectId").val(scope.studyWizard.searchProgrammeStudyStep.selectedProject.id);
                   $("#submitEditProgrammeActionId").trigger("click");
    			});
    			return;
    		}
    		$("#pt-grp-msg").hide();
    		aeTable.jqGrid('setGridParam', {data: result.ae}).trigger('reloadGrid');
    		labTable.jqGrid('setGridParam', {data:  result.lab}).trigger('reloadGrid');
    		if(scope.studyWizard.workflow.selectedStudy.projectGroupRules){
              var projectGroupings  =  scope.studyWizard.workflow.selectedStudy.projectGroupRules;
              for(var i = 0; i < projectGroupings.length; i++){
                 aeTable.setSelection(projectGroupings[i].id + "");
                 labTable.setSelection(projectGroupings[i].id + "");
               }
    		}
             scope.studyWizard.recalculateSplit(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX);
        }) ;
    };

   	init();

};

/**
 public methods */
ProjectGroupingsStep.prototype = {
    startStep : function(){
        var scope = this;
        scope.showProjectGroupingsData();
   	      scope.studyWizard.recalculateSplit(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX);
    },

    leaveStep : function(){
        var scope = this;
        var aeTable = $("#" + scope.aeGroupingsTableId);
        var labTable = $("#" + scope.labGroupingsTableId);
   	      var selectedAe = aeTable.jqGrid ('getGridParam', 'selrow');
   	      var selectedLab = labTable.jqGrid ('getGridParam', 'selrow');
   	      scope.studyWizard.selectedProjectGroups = [];
   	      var ids =[];
   	      if(selectedAe){
   	          ids.push(aeTable.jqGrid('getCell' , selectedAe, 'id'));
   	          scope.studyWizard.selectedProjectGroups.push($.extend(aeTable.jqGrid('getRowData' , selectedAe),{type:'AE'}));
   	      }
   	      if(selectedLab){
   	          ids.push(labTable.jqGrid('getCell' , selectedLab, 'id'));
   	          scope.studyWizard.selectedProjectGroups.push($.extend(labTable.jqGrid('getRowData' , selectedLab),{type:'LAB'}));
   	      }
/*
   	      if(ids.length == 0){
   	          scope.studyWizard.changeStepText(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX,
   	                  "Select custom project groupings(" + ids.length + ")");
   	          return;
   	      }
*/
   	      ajaxModule.sendAjaxRequestSimpleParams("study-select-project-groupings",{'ids': ids},{showDialog: true},function(result){
   	          scope.studyWizard.workflow.selectedStudy.projectGroupRules = result;
   	          scope.studyWizard.changeStepText(scope.studyWizard.PROJECT_GROUPINGS_STEP_INX,
   	                  "Select custom project groupings("+ ids.length +")");
   	      });
   	      return true;
    },

    clearStepTables : function(){
        var scope = this;
        $("#" + scope.aeGroupingsTableId).jqGrid('clearGridData').trigger('reloadGrid');
        $("#" + scope.labGroupingsTableId).jqGrid('clearGridData').trigger('reloadGrid');
    }
};