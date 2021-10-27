<script type="text/javascript" src="js/study-setup/study-subject-groupings.js"></script>
<script src="js/js-lib/bootstrap-select-v1.6.3/js/bootstrap-select.min.js"></script>
<link href="js/js-lib/bootstrap-select-v1.6.3/css/bootstrap-select.min.css" rel="stylesheet"/>

<script type="text/plain" id="studySubjectGroupingsTemplate">
<tr>
    <td>
        < % if(_.isEmpty(groupings.groupingsType.DOSE)) { %>
            <button type="button" class="btn btn-default" style="width:100%; text-align: left;"
                data-toggle="popover" title="Subject Groupings" data-placement="bottom" data-trigger="focus"
                data-content="Please visit the clinical study setup to create subject groupings that can be employed in visualisation modules">
                None available
            </button>
        < % } else { %>
            <select class="studySubjectGroupingsCohortDoseCombo" data-width="100%" data-row="< %- 0 %>">
                <option>Nothing selected</option>
                < % _.forEach(groupings.groupingsType.DOSE, function(cohortDoseGrouping, j) { %>
                    <option value="< %- j %>" < %= _.isEqual(groupings.cohortDoseGrouping, cohortDoseGrouping) ? 'selected' : '' %>>
                    < %- cohortDoseGrouping.name %></option>
                < % }); %>
            </select>
        < % } %>
    </td>
    <td>
        < % if(_.isEmpty(groupings.groupingsType.NONE)) { %>
            <button type="button" class="btn btn-default" style="width:100%; text-align: left;"
                data-toggle="popover" title="Subject Groupings" data-placement="bottom" data-trigger="focus"
                data-content="Please visit the clinical study setup to create subject groupings that can be employed in visualisation modules">
                None available
            </button>
        < % } else { %>
            <select class="studySubjectGroupingsCohortOtherCombo" data-width="100%" data-row="< %- 0 %>">
                <option>Nothing selected</option>
                < % _.forEach(groupings.groupingsType.NONE, function(cohortOtherGrouping, j) { %>
                    <option value="< %- j %>"< %= _.isEqual(groupings.cohortOtherGrouping, cohortOtherGrouping) ? 'selected' : '' %>>
                    < %- cohortOtherGrouping.name %></option>
                < % }); %>
            </select>
        < % } %>
    </td>
    <td id="biomarker-td">
        < % if(_.isEmpty(groupings.groupingsType.BIOMARKER)) { %>
            <button type="button" class="btn btn-default" style="width:100%; text-align: left;"
                data-toggle="popover" title="Subject Groupings" data-placement="bottom" data-trigger="focus"
                data-content="Please visit the clinical study setup to create subject groupings that can be employed in visualisation modules">
                None available
            </button>
        < % } else { %>
            <select class="studySubjectGroupingsBiomarkerCombo" data-width="100%" data-row="< %- 0 %>" multiple>
                < % _.forEach(groupings.groupingsType.BIOMARKER, function(biomarkerGrouping, j) { %>
                    <option value="< %- j %>"< %= _.some(groupings.biomarkerGroupings, function(grouping) { return biomarkerGrouping.name == grouping.name }) ? 'selected' : '' %>>
                    < %- biomarkerGrouping.name %></option>
                < % }); %>
            </select>
        < % } %>
    </td>
</tr>
</script>

<div style="width: 1000px;">
    <div class="panel panel-default">
        <div class="panel-body">
            <table class="table table-striped table-bordered" id="studySubjectGroupingsTable">
                <thead>
                <tr>
                    <th style="width: 400px;">Cohort - Dose</th>
                    <th style="width: 400px;">Cohort - Other</th>
                    <th style="width: 600px;" id="biomarker-th">Biomarkers</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <th scope="row">study</th>
                    <td>
                        <select class="studySubjectGroupingsCohortDoseCombo" data-width="100%">
                            <option>Nothing selected</option>
                        </select>
                    </td>
                    <td>
                        <select class="studySubjectGroupingsCohortOtherCombo" data-width="100%">
                            <option>Nothing selected</option>
                        </select>
                    </td>
                    <td id="biomarker-td">
                        <select class="studySubjectGroupingsBiomarkerCombo" data-width="100%" multiple>
                        </select>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="text-center">
                <button class="btn btn-primary" disabled id="studySubjectGroupingsSaveButton">Save</button>
                <button class="btn btn-primary" disabled id="studySubjectGroupingsCancelButton">Cancel</button>
            </div>
        </div>
    </div>

</div>
