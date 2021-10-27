'use strict';

/* Services */

angular.module('App.services', ['ngResource'])
    .factory('userInfoFactory', ['$http', function ($http) {
        return {
            getUserInfo: function (cb) {
                return $http.post('/admin/user-info')
                    .success(function (data) {
                        if (cb) {
                            cb(angular.fromJson(data))
                        }

                    })
            }
        }
    }])
    .factory('auditFactory', ['$http', function ($http) {
        return {
            getHistory: function (pageNum, pageSize, sortBy, sortReverse, cb) {
                return $http.post('/api/audit/history', {
                    pageNum: pageNum, pageSize: pageSize,
                    sortBy: sortBy, sortReverse: sortReverse
                })
                    .success(function (data) {
//                        var offset = new Date().getTimezoneOffset();
//                        _.forEach(data.items, function (item) {
//                            item.timestamp -= offset*60 * 1000;
//                        });

                        if (cb) {
                            cb(data)
                        }
                    })
            }
        }
    }])
    .factory('fileViewFactory', ['$http', function ($http) {
        return {
            loadFile: function (fileUrl) {
                return $http.post('/fileView/load', fileUrl);
            },
            listData: function (fileUrl, offset, limit, sortColumns) {
                return $http.post('/fileView/data', {
                    fileUrl: fileUrl,
                    offset: offset,
                    limit: limit,
                    sortColumns: sortColumns
                });
            }
        }
    }])
    .factory('reportService', ['$http', '$log', '$filter', function ($http, $log, $filter) {

        var reportTableSortOptions = [
            {},
            {sortField: 'acuityEntities', reverse: false},
            {sortField: 'dataField', reverse: false},
            {sortField: 'dataField', reverse: false}
        ];
        var model = {
            studyId: null,
            selectedUpload: null,
            dateMask: 'dd-MMM-yyyy',
            timeMask: 'HH:mm',
            reportType: 0,

            reportButtons: [
                'Exception report',
                'Source data table report',
                'Source data field report',
                'Source data value report'
            ],
            reportTypes: ['exception', 'table', 'field', 'value']
        };
        /**
         * Split data array to paged array
         * @param {Array} inList  - input array
         * @param {number} pageSize - count items on the page
         * @return {Array} - paged array
         * //TODO replace with lodash _.chunk
         */
        var groupToPages = function (inList, pageSize) {
            var resultList = [];

            for (var i = 0; i < inList.length; i++) {
                if (i % pageSize === 0) {
                    resultList[Math.floor(i / pageSize)] = [inList[i]];
                } else {
                    resultList[Math.floor(i / pageSize)].push(inList[i]);
                }
            }

            return resultList;
        };
        return {
            model: model,
            groupToPages: groupToPages,
            /**
             * Provides information about loaded reports for the current study
             * @returns {*} - $http promise
             */
            getStudyUploads: function () {
                model.uploadTable.loading = true;
                return $http.get('/uploadreport/' + model.studyId + '/summary')
                    .success(function (data) {
                        model.studyUploads = data;
                        model.selectedUpload = null;
                        model.sortedStudyUploads = groupToPages(data, model.uploadTable.pagingOptions.pageSize);
                        model.studyCode = data[0].studyCode;
                        model.uploadTable.loading = false;
//                        return data;
                    }).error(function (data, status) {
                        console.error('Error getting uploads information for clinical study', status);
                    });
            },

            /**
             * Provides information about exception reports for the current study
             * @returns {*} - $http promise
             */
            getReportOfType: function () {
                model.reportTable.loading = true;
                return $http.get('/uploadreport/' + model.studyId + '/' + model.reportTypes[model.reportType] + '/' + model.selectedUpload.jobExecID)
                    .success(function (data) {
                        model.reportsData[model.reportType] = data;
                        if (model.reportTable.sortOptions[model.reportType].sortField) {
                            data = $filter('orderBy')(model.reportsData[model.reportType], model.reportTable.sortOptions[model.reportType].sortField, model.reportTable.sortOptions[model.reportType].reverse);
                        }
                        model.sortedReports[model.reportType] = groupToPages(data, model.reportTable.pageSize);
                        model.reportTable.pagingOptions[model.reportType].currentPage = 1;
                        model.reportTable.loading = false;
                    }).error(function (data, status) {
                        model.reportTable.loading = false;
                        console.error('Error getting ' + model.reportTypes[model.reportType] + ' reports for clinical study', status);
                    });
            },
            resetDataOptions: function () {
                model.reportsData = [];
                model.sortedReports = [];
                model.reportTable.sortOptions = _.cloneDeep(reportTableSortOptions);
                _.each(model.reportTable.pagingOptions, function (option) {
                    option.currentPage = 1;
                });
            }
        };
    }])
    .factory('uploadSummaryService', ['$http', function ($http) {
        var model = {
            dateMask: 'dd-MMM-yyyy',
            uploadSummary: [],
            totalSummary: {filesCount: 0, filesSize: 0},
            averageSummary: {filesCount: 0, filesSize: 0}
        };
        var months = ["Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"];

        function formatDate (date) {
            var d = new Date(date);
            var day = d.getDate();
            var month = months[d.getMonth()];
            var year = d.getFullYear() % 100;
            return day + '-' + month + '-' + year;
        }

        function sumBy (array, property) {
            return array.map(function (d) {
                return d[property]
            }).reduce(function (previousValue, currentValue) {
                return previousValue + currentValue;
            })
        }

        function round (value) {
            return Math.round(value * 100) / 100;
        }

        return {
            model: model,
            /**
             * Provides summary information about upload reports for all     studies
             * @returns {*} - $http promise
             */
            getUploadSummary: function (dateFrom, dateTo) {
                model.loading = true;
                return $http.post('/uploadreport/upload-summary',
                    {
                        dateFrom: dateFrom ? formatDate(dateFrom) : undefined,
                        dateTo: dateTo ? formatDate(dateTo) : undefined
                    })
                    .success(function (data) {
                        model.uploadSummary = data.sort(function (a, b) {
                            return a.date - b.date;
                        });
                        var nonEmptyUploadDates = data.filter(function (d) {
                            return !!d.filesSize;
                        }).length;
                        var totalFilesCount = data && data.length > 0 && nonEmptyUploadDates > 0
                            ? sumBy(data, 'filesCount')
                            : 0;
                        var totalFilesSize = data && data.length > 0 && nonEmptyUploadDates > 0
                            ? sumBy(data, 'filesSize')
                            : 0;

                        model.totalSummary = {
                            filesCount: totalFilesCount,
                            filesSize: totalFilesSize
                        };

                        // Please note that average summary is calculated taking into account
                        // only those days when any file was uploaded and upload was non-empty
                        model.averageSummary = {
                            filesCount: totalFilesCount ? round(totalFilesCount / nonEmptyUploadDates) : 0,
                            filesSize: totalFilesSize ? round(totalFilesSize / nonEmptyUploadDates) : 0
                        };
                        model.loading = false;
                    }).error(function (data, status) {
                        model.loading = false;
                        console.error('Error getting upload summary information for clinical studies', status);
                    });
            }
        };
    }])
    .factory('groupResource', ['$resource', function ($resource) {
        return $resource('/studygroupingssetup/:studyId/subjectgroups/:id', null,
            {
                update: {
                    method: 'POST',
                    isArray: true
                }
            });
    }])
    .factory('subjectGroupingsService', ['$http', '$log', '$resource', 'groupResource', '$filter', function ($http, $log, $resource, groupResource, $filter) {

        var frequencies = [
            {term: 'QH - Once per hour', freq: '24', unit: 'Day'},
            {term: 'Q2H - Once every two hours', freq: '12', unit: 'Day'},
            {term: 'Q4H - Once every four hours', freq: '6', unit: 'Day'},
            {term: 'QID - Four times per day', freq: '4', unit: 'Day'},
            {term: 'TID - Three times per day', freq: '3', unit: 'Day'},
            {term: 'BID - Twice per day', freq: '2', unit: 'Day'},
            {term: 'QD - Once per day', freq: '1', unit: 'Day'},
            {term: '4 Times per Week', freq: '4', unit: 'Week'},
            {term: 'QOD - Every other day', freq: '1', unit: '48 hours'},
            {term: '3 Times per Week', freq: '3', unit: 'Week'},
            {term: '2 Times per Week', freq: '2', unit: 'Week'},
            {term: 'Every Week', freq: '1', unit: 'Week'},
            {term: 'Every 2 Week', freq: '1', unit: 'Fortnight'},
            {term: 'Every 3 Weeks', freq: '1', unit: '3-week'},
            {term: 'Every 4 Weeks', freq: '1', unit: '4-Week'},
            {term: 'QM', freq: '1', unit: 'Month'},
            {term: 'Once', freq: '1', unit: '-'},
            {term: 'Unknown', freq: '-', unit: '-'},
            {term: 'PRN', freq: '-', unit: '-'},
            {term: 'Other', freq: '-', unit: '?'}
        ];

        var model = {
            studyId: null,
            studyCode: null,
            annotateGroups: false,
            selectedGrouping: "",
            openedTab: "dose",
            groupings: [],
            groupingTypes: [],
            savedCopy: null,
            formulationNames: [
                'Capsule',
                'Gel',
                'Injection',
                'Ointment',
                'Patch',
                'Solution',
                'Spray',
                'Suppository',
                'Suspension',
                'Tablet',
                'Other'
            ],
            administrationRoutes: [
                'Auricular',
                'Buccal',
                'Dental',
                'Epidural',
                'Intracaudal',
                'Intradermal',
                'Intramuscular',
                'Intraspinal',
                'Intrathecal',
                'Intrathoracic',
                'Intravenous',
                'Nasal',
                'Ophthalmic',
                'Oral',
                'Rectal',
                'Respiratory',
                'Subcutaneous',
                'Sublingual',
                'Topical',
                'Transdermal',
                'Vaginal',
                'Other'
            ],
            periodNames: [
                'Day',
                'Week',
                'Year',
                'Cycle'
            ],

            frequencyUnits: _.uniq(_.pluck(frequencies, 'unit')),
            frequencyTerms: _.uniq(_.pluck(frequencies, 'term')),

            frequencies: frequencies
        };

        var deleteGroup = function (groupNumber) {
            model.groupings[model.selectedGrouping].groups.splice(groupNumber, 1);
            model.savedCopy = _.cloneDeep(model.groupings);
        };

        var processDataFromServer = function (data) {
            _.each(data, function (savedGroup) {
                var localGroup = _.find(model.groupings[savedGroup.groupingName].groups, {groupName: savedGroup.groupName});
                if (localGroup) {
                    _.assign(localGroup, _.omit(savedGroup, 'dosings'), {unsaved: false});
                    _.each(localGroup.dosings, function (localDosing) {
                        var savedDosing = _.find(savedGroup.dosings, {drug: localDosing.drug});
                        _.assign(localDosing, savedDosing);
                    });
                    fixDosings(localGroup);
                }
            });
        };

        var fixDosings = function (group) {
            _.each(group.dosings, function (dosing) {
                _.each(dosing.schedule, function (schedule) {
                    schedule.duration = schedule.duration || 'Continuous';
                    schedule.frequency = schedule.frequency === 0 ? '-' : schedule.frequency;
                });
                dosing.totalDuration = dosing.totalDurationCycles ? 'planned' : 'indeterminate';
                checkRepeatMarked(dosing);
            });
        };

        var checkRepeatMarked = function (dosing) {
            var option = !_.reduce(dosing.schedule, function (result, schedule) {
                return result || schedule.repeat;
            }, false);
            dosing.disableCycleOption = option;
            if (option && dosing.totalDurationType == 'Cycle') {
                dosing.totalDurationType = 'None';
            }
        };

        var getUnsavedGroups = function () {
            return _.flatten(_.map(model.groupings, function (grouping) {
                return _.filter(grouping.groups, function (group) {
                    return group.unsaved;
                });
            }));
        };

        var sortList = function () {
            if (_.isEmpty(model.groupings[model.selectedGrouping].groups) || _.isUndefined(model.groupings[model.selectedGrouping].groups[0].index)) {
                model.groupings[model.selectedGrouping].groups = $filter('orderBy')(model.groupings[model.selectedGrouping].groups, 'groupPreferedName||groupName');
            } else {
                model.groupings[model.selectedGrouping].groups = $filter('orderBy')(model.groupings[model.selectedGrouping].groups, 'index');
            }
            _.forEach(model.groupings[model.selectedGrouping].groups, function (group, index) {
                group.index = index;
            });
        };

        return {
            model: model,
            sortList: sortList,
            getUnsavedGroups: getUnsavedGroups,
            checkRepeatMarked: checkRepeatMarked,
            getGroupings: function () {
                model.loading = true;
                groupResource.get({studyId: model.studyId}, function (data) {

                    model.groupings = {};
                    _.each(data.groupings, function (grouping) {
                        model.groupings[grouping.name] = grouping;
                    });

                    model.studyCode = data.studyCode;
                    model.studyName = data.studyName;
                    _.each(model.groupings, function (grouping) {
                        _.each(grouping.groups, function (group) {
                            fixDosings(group);
                        });
                    });
                    model.savedCopy = _.cloneDeep(model.groupings);
                    model.loading = false;
                }, function (data, status) {
                    console.error('Error getting groups for clinical study', data.status);
                    model.loading = false;
                });
            },

            listGroupingTypes: function () {
                // var params = _.pick(grouping, 'id', 'name', 'type');
                return $http.get('/studygroupingssetup/subject-grouping-types')
                    .success(function (data) {
                        model.groupingTypes = data;
                        console.log(data);
                    }).error(function (data, status) {
                        console.error('Error listing grouping types', status);
                    });
            },

            saveGroups: function () {
                var groupsList = _.cloneDeep(_.map(getUnsavedGroups(), function (group) {
                    return _.pick(group, 'subjectsCount', 'groupPreferedName', 'groupName', 'id', 'groupingName', 'dosings', 'index');
                }));
                _.each(groupsList, function (group) {
                    _.each(group.dosings, function (dosing, dosingIndex) {
                        group.dosings[dosingIndex].totalDurationCycles = dosing.totalDuration == "indeterminate" ? 0 : dosing.totalDurationCycles;
                        group.dosings[dosingIndex].totalDurationType = dosing.totalDurationType == "-" ? null : dosing.totalDurationType;
                        group.dosings[dosingIndex] = _.omit(dosing, 'editing', 'totalDuration', 'durationType', 'disableCycleOption');
                        _.each(group.dosings[dosingIndex].schedule, function (schedule) {
                            schedule.duration = schedule.duration == 'Continuous' ? 0 : schedule.duration;
                            schedule.frequency = schedule.frequency > 0 && schedule.frequency < 100 ? schedule.frequency : 0;
                        });
                    });
                });
                if (!_.isEmpty(groupsList)) {
                    model.loading = true;
                    groupResource.update({studyId: model.studyId}, groupsList, function (data) {
                        processDataFromServer(data);
                        model.savedCopy = _.cloneDeep(model.groupings);
                        sortList();
                        model.loading = false;
                    }, function (data, status) {
                        console.error('Error saving groups for clinical study', data.status);
                        model.loading = false;
                    });
                }
            },
            removeGroup: function (group) {
                var groupNumber = _.indexOf(model.groupings[model.selectedGrouping].groups, group);
                if (group.id) {
                    model.loading = true;
                    groupResource.delete({studyId: model.studyId, id: group.id}, function (data) {
                        model.loading = false;
                        deleteGroup(groupNumber);
                        sortList();
                    }, function (data, status) {
                        console.error('Error deleting group for clinical study', data.status);
                        model.loading = false;
                    });
                } else {
                    deleteGroup(groupNumber);
                }
            },
            /**
             * Provides Lab results for the LB-pop-1 chart
             *
             * @param {number} grouping - subject grouping
             * @returns {*} - $http promise
             */
            saveGrouping: function (grouping) {
                var params = _.pick(grouping, 'id', 'name', 'type');
                return $http.post('/studygroupingssetup/' + model.studyId + '/subjectgroups/save-subject-grouping', params)
                    .success(function (data) {
                        return data;
                    }).error(function (data, status) {
                        console.error('Error saving annotation tick', status);
                    });
            },

        };

    }]);
