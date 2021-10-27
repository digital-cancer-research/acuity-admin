var StudyGroupingsStep = function (studyWizard) {
    this.studyWizard = studyWizard;

    var studySubjectGroupingsTable = $('#studySubjectGroupingsTable');
    var studySubjectGroupingsSaveButton = $('#studySubjectGroupingsSaveButton');
    var studySubjectGroupingsCancelButton = $('#studySubjectGroupingsCancelButton');

    var scope = this;
    var model, source;

    var tbodyTemplate = _.template($('#studySubjectGroupingsTemplate').text(), {
        noneSelectedText: 'Nothing selected',
        evaluate: /< %([\s\S]+?)%>/g,
        escape: /< %-([\s\S]+?)%>/g,
        interpolate: /< %=([\s\S]+?)%>/g
    });

    studySubjectGroupingsCancelButton.click(function () {
        scope.createStudyGroupingsTables();
    });

    studySubjectGroupingsSaveButton.click(function () {
        request = null;
        if (!_.isEqual(model, source)) {
            request= {
                studyId: model.studyId,
                groupingsType: {
                    DOSE: [model.cohortDoseGrouping],
                    NONE: [model.cohortOtherGrouping],
                    BIOMARKER: model.biomarkerGroupings
                }
            };
        };

        if (request) {
            ajaxModule.sendAjaxRequest('/study-setup/save-study-subject-groupings', JSON.stringify(request), null,
                function (data) {
                    source = scope.studyWizard.workflow.selectedStudy.studySubjectGrouping = _.cloneDeep(model);
                    touchButtonsState();
                }
            );
        }
    });

    var touchButtonsState = function () {
        if (_.isEqual(source, model)) {
            studySubjectGroupingsSaveButton.prop('disabled', true);
            studySubjectGroupingsCancelButton.prop('disabled', true);
        } else {
            studySubjectGroupingsSaveButton.prop('disabled', false);
            studySubjectGroupingsCancelButton.prop('disabled', false);
        }
    };

    scope.createStudyGroupingsTables = function () {
        source = scope.studyWizard.workflow.selectedStudy.studySubjectGrouping;
        model = _.cloneDeep(source);

        $.getJSON('/study-setup/selected-study-subject-groupings', {id: scope.studyWizard.workflow.selectedStudy.id},
            function (data) {
                selectedGroupings = data;
                if (selectedGroupings.groupingsType.DOSE) model.cohortDoseGrouping = selectedGroupings.groupingsType.DOSE[0];
                if (selectedGroupings.groupingsType.NONE) model.cohortOtherGrouping = selectedGroupings.groupingsType.NONE[0];
                model.biomarkerGroupings = selectedGroupings.groupingsType.BIOMARKER;


                var tbodyString = tbodyTemplate({groupings: model});
                studySubjectGroupingsTable.find('tbody').html(tbodyString);
                studySubjectGroupingsTable.find('[data-toggle="popover"]').popover();

                var comboboxes = $('.studySubjectGroupingsCohortDoseCombo, .studySubjectGroupingsCohortOtherCombo, ' +
                    '.studySubjectGroupingsBiomarkerCombo');
                comboboxes.selectpicker({'selectedTextFormat': 'count'});

                $('.studySubjectGroupingsCohortDoseCombo').change(function () {
                    var item = model;
                    var selected = $(this).val();
                    if (selected == 'Nothing selected') {
                        item.cohortDoseGrouping = null;
                    } else {
                        item.cohortDoseGrouping = item.groupingsType.DOSE[selected];
                    }
                    touchButtonsState();
                });

                $('.studySubjectGroupingsCohortOtherCombo').change(function () {
                    var item = model;
                    var selected = $(this).val();
                    if (selected == 'Nothing selected') {
                        item.cohortOtherGrouping = null;
                    } else {
                        item.cohortOtherGrouping = item.groupingsType.NONE[selected];
                    }
                    touchButtonsState();
                });

                $('.studySubjectGroupingsBiomarkerCombo').change(function () {
                    var item = model;
                    var selected = $(this).val();

                    item.biomarkerGroupings = _.map(selected, function (id) {
                        return item.groupingsType.BIOMARKER[id];
                    });

                    touchButtonsState()
                });
            }
        );


    }
};

StudyGroupingsStep.prototype = {
    startStep: function () {
        this.createStudyGroupingsTables();
    },

    endStep: function () {
    }
};
