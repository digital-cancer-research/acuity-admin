'use strict';
/**

 */
angular.module('App.controllers')
    .controller('subjectGroupingsController',
        ['$scope', 'subjectGroupingsService', '$routeParams', '$modal', '$log', '$window',
            function ($scope, SubjectGroupingsService, $routeParams, $modal, $log, $window) {

            $scope.addGroup = function () {
                var lastGroupId = $scope.model.groupings[$scope.model.selectedGrouping].groups.length + 1;
                $scope.model.groupings[$scope.model.selectedGrouping].groups.push({
                    id: null,
                    groupingName: $scope.model.selectedGrouping,
                    unsaved: true,
                    groupName: "Group " + lastGroupId,
                    groupPreferedName: "",
                    subjectsCount: 0,
                    disableClear: true,
                    index: $scope.model.groupings[$scope.model.selectedGrouping].groups.length
                });
                SubjectGroupingsService.sortList();
            };

            $scope.sortList = function () {
                var currentType = $scope.model.groupings[$scope.model.selectedGrouping].type;

                if (currentType) {
                    $scope.model.selectedGroupingType = currentType.id;
                } else {
                    $scope.model.selectedGroupingType = 0;
                }

                SubjectGroupingsService.sortList();
            };

            $scope.changeDrugName = function (group) {
                $scope.markDirty(group);
            };

            $scope.addDrug = function (group) {
                if ($scope.groupHasDuplicatedOrEmptyDrugName(group)) {
                    return;
                }

                $scope.markDirty(group);
                var newDosing = {
                    id: '',
                    drug: "",
                    formulation: "",
                    administrationRoute: "",
                    totalDurationCycles: 0,
                    totalDurationType: "None",
                    totalDuration: 'indeterminate',
                    disableCycleOption: true,
                    schedule: [],
                    editing: true
                };
                if (!group.dosings) {
                    group.dosings = [];
                }
                group.dosings.push(newDosing);
                group.selectedDosingIndex = group.dosings.length - 1;
                //group.notUnigueDrugName = true;
            };

            $scope.addSchedule = function (group) {
                $scope.markDirty(group);
                var scheduleDosing = group.dosings[group.selectedDosingIndex].schedule.length === 0 ? true : !_.last(group.dosings[group.selectedDosingIndex].schedule).dosing;
                group.dosings[group.selectedDosingIndex].schedule.push({
                    dosing: scheduleDosing,
                    duration: scheduleDosing ? "Continuous" : 1,
                    durationUnit: "None",
                    dose: null,
                    doseUnit: "",
                    frequency: '-',
                    frequencyUnit: '-',
                    frequencyTerm: 'Unknown',
                    repeat: false
                });
            };

            $scope.saveAnnotationTick = function () {
                if (!_.isUndefined($scope.model.selectedGroupingType)) {
                    $scope.model.groupings[$scope.model.selectedGrouping].type = $scope.model.groupingTypes.find(function(type) {
                        return type.id === $scope.model.selectedGroupingType;
                    });

                    SubjectGroupingsService.saveGrouping($scope.model.groupings[$scope.model.selectedGrouping]);
                } else {

                }
            };

            $scope.markDirty = function (group) {
                group.unsaved = true;
            };

            $scope.frequencyIncrement = function (schedule, group) {
                if (schedule.frequency < 99) {
                    schedule.frequency++;
                } else if (schedule.frequency > 99) {
                    schedule.frequency = 99;
                } else {
                    schedule.frequency = 1;
                }
                $scope.frequencyChanged(schedule, group);
            };

            $scope.frequencyDecrement = function (schedule, group) {
                if (schedule.frequency > 1) {
                    schedule.frequency--;
                } else {
                    schedule.frequency = '-';
                }
                $scope.frequencyChanged(schedule, group);
            };

            $scope.frequencyChanged = function (schedule, group) {
                if (schedule.frequency === '' || schedule.frequency === '-' || (schedule.frequency > 0 && schedule.frequency < 100)) {
                    //it is ok
                } else {
                    schedule.frequency = '-';
                }

                if (schedule.frequency || schedule.frequencyUnit) {

                    var frequency = _.find($scope.model.frequencies, {
                        freq: '' + schedule.frequency,
                        unit: schedule.frequencyUnit
                    });
                    if (frequency) {
                        schedule.frequencyTerm = frequency.term;
                    } else {
                        schedule.frequencyTerm = 'Other';
                    }
                } else {
                    schedule.frequencyTerm = 'Unknown';
                }
                group.unsaved = true;
            };

            $scope.frequencyTermChanged = function (schedule, group) {
                var frequency = _.find($scope.model.frequencies, {
                    term: schedule.frequencyTerm
                });
                schedule.frequency = frequency.freq;
                schedule.frequencyUnit = frequency.unit;
                group.unsaved = true;
            };

            $scope.selectDosing = function (group, index) {
                if (group.selectedDosingIndex == index) {
                    group.dosings[index].editing = true;
                } else {
                    group.selectedDosingIndex = index;
                }
            };

            $scope.changeTotalDuration = function (group) {
                if (group.dosings[group.selectedDosingIndex].totalDuration == "planned") {
                    group.dosings[group.selectedDosingIndex].totalDurationCycles = group.dosings[group.selectedDosingIndex].totalDurationCycles || 1;
                }
                $scope.markDirty(group);
            };

            $scope.incrementSpinner = function (object, counter, group) {
                if (object[counter] == "Continuous") {
                    object[counter] = 1;
                } else {
                    if ((counter == 'duration' || counter == 'totalDurationCycles') && object[counter] >= 999) {
                        object[counter] = 999;
                    } else {
                        if (counter == 'frequency' && object[counter] >= 99) {
                            object[counter] = 99;
                        } else {
                            object[counter]++;
                        }
                    }
                }
                $scope.markDirty(group);
            };

            $scope.decrementSpinner = function (object, counter, group) {
                if (object[counter] < 2 || object[counter] == "Continuous") {
                    if (counter == 'duration') {
                        object[counter] = "Continuous";
                    }
                } else {
                    object[counter]--;
                }
                $scope.markDirty(group);
            };

            $scope.changeScheduleDosing = function (group, schedule) {
                if (!schedule.dosing) {
                    schedule.dose = null;
                    schedule.doseUnit = "";
                    schedule.frequency = null;
                    schedule.frequencyUnit = "None";
                }
                $scope.markDirty(group);
            };

            $scope.repeatCheckboxChange = function (group) {
                SubjectGroupingsService.checkRepeatMarked(group.dosings[group.selectedDosingIndex]);
                $scope.markDirty(group);
            };

            $scope.saveChanges = function () {
                var error = inputHasErrors();
                if (error) {
                    console.log(error);
                } else {
                    SubjectGroupingsService.saveGroups();
                }
            };

            $scope.dragControlListeners = {
                orderChanged: function (event) {
                    _.forEach($scope.model.groupings[$scope.model.selectedGrouping].groups, function (group, index) {
                        $scope.markDirty(group);
                        group.index = index;
                    });
                }
            };

            $scope.clearChanges = function (group) {
                group.groupPreferedName = "";
                group.unsaved = true;
                SubjectGroupingsService.sortList();
            };

            $scope.cancelChanges = function () {
                $scope.model.groupings[$scope.model.selectedGrouping].groups = _.cloneDeep($scope.model.savedCopy[$scope.model.selectedGrouping].groups);
                SubjectGroupingsService.sortList();
            };

            $scope.editDosingName = function (dosing) {
                dosing.editing = true;
            };

            $scope.doneEditingDosing = function (group, dosing) {
                dosing.editing = false;
                $scope.checkUnigueDrugName(group);
            };

            var inputHasErrors = function () {
                var groups = $scope.model.groupings[$scope.model.selectedGrouping].groups;
                var groupWithProblem;

                groupWithProblem = _.find(groups, $scope.groupHasDuplicatedOrEmptyDrugName);
                if (groupWithProblem) {
                    return 'Group "' + groupWithProblem.groupName + '" has empty or duplicated drug names';
                }

                groupWithProblem = _.find(groups, $scope.groupHasSimilarName);
                if (groupWithProblem) {
                    return 'Some groups have duplicated names';
                }

                groupWithProblem = _.find(groups, $scope.groupHasSimilarPN);
                if (groupWithProblem) {
                    return 'Some groups have duplicated names';
                }
            };

            $scope.groupHasSimilarPN = function (currentGroup) {
                var groups = $scope.model.groupings[$scope.model.selectedGrouping].groups;
                if (currentGroup.groupPreferedName) {
                    return _.filter(groups, function (group) {
                            return currentGroup.groupPreferedName == group.groupPreferedName || currentGroup.groupPreferedName == group.groupName;
                        }).length > 1;
                }
                return false;
            };

            $scope.groupHasSimilarName = function (currentGroup) {
                var groups = $scope.model.groupings[$scope.model.selectedGrouping].groups;
                return _.filter(groups, {groupName: currentGroup.groupName}).length > 1;
            };

            $scope.groupHasDuplicatedOrEmptyDrugName = function (group) {
                return group.dosings && _(group.dosings).pluck('drug').compact().uniq().value().length != group.dosings.length;
            };

            $scope.checkUnigueDrugName = function (group) {
                group.notUnigueDrugName = $scope.groupHasDuplicatedOrEmptyDrugName(group);
            };

            $scope.openGroup = function (group) {
                if (!group.opened) {
                    _.forEach($scope.model.groupings[$scope.model.selectedGrouping].groups, function (group) {
                        group.opened = false;
                    });
                }
                group.opened = !group.opened;
            };

            $scope.removeGroup = function (group) {
                if (!group.subjectsCount) {
                    var callback = function () {
                        SubjectGroupingsService.removeGroup(group);
                    };
                    openModal(callback, 'Are you sure you want to remove this group annotation?');
                }
            };

            $scope.removeDrug = function (group, index) {
                var callback = function () {
                    group.unsaved = true;
                    if (group.selectedDosingIndex == index) {
                        group.selectedDosingIndex = null;
                    } else {
                        if (index < group.selectedDosingIndex) {
                            group.selectedDosingIndex--;
                        }
                    }
                    group.dosings.splice(index, 1);
                    $scope.checkUnigueDrugName(group);
                };
                openModal(callback, 'Are you sure you want to remove this drug?');
            };

            $scope.removeSchedule = function (group, index) {
                if (!inputHasErrors()) {
                    var callback = function () {
                        $scope.markDirty(group);
                        group.dosings[group.selectedDosingIndex].schedule.splice(index, 1);
                        SubjectGroupingsService.saveGroups();
                    };
                    openModal(callback, 'Are you sure you want to remove this schedule?');
                }
            };

            var openModal = function (callback, question, buttonName) {
                $modal.open({
                    templateUrl: 'removeModalContent',
                    backdrop: true,
                    windowClass: 'app-modal-window',
                    controller: function ($scope, $modalInstance) {
                        $scope.question = question;
                        $scope.buttonName = buttonName ? buttonName : 'Remove';
                        $scope.remove = function () {
                            callback();
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                    }
                });
            };

            var init = function () {
                $scope.model = SubjectGroupingsService.model;
                $scope.model.studyId = $routeParams.studyId;
                SubjectGroupingsService.getGroupings();
                SubjectGroupingsService.listGroupingTypes();
            };

            init();
        }]
    );
