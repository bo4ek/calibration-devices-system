angular
    .module('employeeModule')
    .controller(
        'TaskForStationController',
        [
            '$rootScope',
            '$scope',
            '$modal',
            '$http',
            '$filter',
            '$q',
            'ngTableParams',
            '$translate',
            '$timeout',
            'toaster',
            'CalibrationTaskServiceCalibrator',
            'VerificationServiceCalibrator',
            '$location',
            function ($rootScope, $scope, $modal, $http, $filter, $q,
                      ngTableParams, $translate, $timeout, toaster, CalibrationTaskServiceCalibrator, verificationServiceCalibrator, $location) {

                $scope.pageContent = [];
                $scope.taskIDs = [];
                $scope.allTests = false;
                $scope.path = $location.path();
                $scope.forBrigades = $scope.path == "/calibrator/task-for-brigades";

                /**
                 * Date-range picker
                 *
                 *
                 */
                $scope.clearDate = function () {
                    $scope.defaultDate.startDate = moment();
                    $scope.defaultDate.endDate = moment();
                    $scope.myDatePicker.pickerDate = $scope.defaultDate;
                    $rootScope.onTableHandling();
                };

                $scope.myDatePicker = {};
                $scope.myDatePicker.pickerDate = null;
                $scope.defaultDate = {
                    startDate: moment(),
                    endDate: moment() 
                };

                $scope.initDatePicker = function (workDate) {
                    /**
                     *  Date picker and formatter setup
                     */
                    $scope.myDatePicker.pickerDate = {
                        startDate: $scope.defaultDate.startDate,
                        endDate: $scope.defaultDate.endDate 
                    };

                    $scope.setTypeDataLangDatePicker = function () {
                        var lang = $translate.use();
                        if (lang === 'ukr') {
                            moment.locale('uk');
                        } else {
                            moment.locale('en');
                        }
                        $scope.opts = {
                            format: 'DD-MM-YYYY',
                            showDropdowns: true,
                            locale: {
                                firstDay: 1,
                                fromLabel: $filter('translate')('FROM_LABEL'),
                                toLabel: $filter('translate')('TO_LABEL'),
                                applyLabel: $filter('translate')('APPLY_LABEL'),
                                cancelLabel: $filter('translate')('CANCEL_LABEL'),
                                customRangeLabel: $filter('translate')('CUSTOM_RANGE_LABEL')
                            },
                            ranges: {},
                            eventHandlers: {}
                        };
                        $scope.opts.ranges[$filter('translate')('TODAY')] = [moment(), moment()];
                        $scope.opts.ranges[$filter('translate')('YESTERDAY')] = [moment().subtract(1, 'day'), moment().subtract(1, 'day')];
                        $scope.opts.ranges[$filter('translate')('THIS_WEEK')] = [moment().startOf('week'), moment().endOf('week')];
                        $scope.opts.ranges[$filter('translate')('THIS_MONTH')] = [moment().startOf('month'), moment().endOf('month')];
                        $scope.opts.ranges[$filter('translate')('LAST_MONTH')] = [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')];
                        $scope.opts.ranges[$filter('translate')('ALL_TIME')] = [moment("2015-12-31", "YYYY-MM-DD"), moment()];
                    };

                    $scope.setTypeDataLangDatePicker();
                };

                $scope.initDatePicker();

                $scope.dateOptions = {
                    formatYear: 'yyyy',
                    startingDay: 1,
                    showWeeks: 'false'
                };

                $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
                $scope.format = $scope.formats[3];

                $scope.showPicker = function ($event) {
                    angular.element("#datepickerfield").trigger("click");
                };

                $scope.isDateDefault = function () {
                    return ($scope.myDatePicker.pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') 
                    && $scope.myDatePicker.pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day'));
                };

                /**
                 * Date picker for editing tha date of calibration task
                 *
                 *
                 */
                $scope.calendars = {};

                /**
                 * opens date picker
                 * on the modal
                 *
                 * @param $event
                 */
                $scope.open = function ($event, taskID) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    for (var key in $scope.calendars) {
                        if ($scope.calendars[key]) {
                            $scope.calendars[key] = false;
                        }
                    }
                    $scope.calendars[taskID] = true;
                };

                /**
                 * Disables weekend selection
                 *
                 * @param date
                 * @param mode
                 * @returns {boolean}
                 */
                $scope.disabled = function (date, mode) {
                    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
                };

                $scope.toggleMin = function () {
                    $scope.minDate = $scope.minDate ? null : new Date();
                };

                $scope.toggleMin();
                $scope.maxDate = new Date(2100, 5, 22);

                /**
                 * adds or removes selected taskId to the array
                 *
                 * @param id
                 */
                $scope.resolveTaskID = function (id) {
                    var index = $scope.taskIDs.indexOf(id);
                    if (index > -1) {
                        $scope.taskIDs.splice(index, 1);
                    } else {
                        $scope.taskIDs.push(id);
                    }
                };

                /**
                 * opens task for station modal
                 * if task saved successfully reloads
                 * table data
                 */
                $scope.sendTaskToStation = function () {
                    if ($scope.taskIDs.length === 0) {
                        toaster.pop('error', $filter('translate')('INFORMATION'),
                            $filter('translate')('CHOOSE_CALIBRATION_TASK'));
                    } else {
                        CalibrationTaskServiceCalibrator.sendTaskToStation($scope.taskIDs).then(function (result) {
                            if (result.status == 200) {
                                $scope.taskIDs = [];
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('TASK_SENT'));
                            } else {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('TASK_NOT_SENT'));
                            }
                            $rootScope.onTableHandling();
                        });
                    }
                };

                $scope.sendTaskToTeam = function () {
                    if ($scope.taskIDs.length === 0) {
                        toaster.pop('error', $filter('translate')('INFORMATION'),
                            $filter('translate')('CHOOSE_CALIBRATION_TASK'));
                    } else {
                        CalibrationTaskServiceCalibrator.sendTaskToTeam($scope.taskIDs).then(function (result) {
                            if (result.status == 200) {
                                $scope.taskIDs = [];
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('TASK_SENT'));
                            } else {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('TASK_NOT_SENT'));
                            }
                            $rootScope.onTableHandling();
                        });
                    }
                };

                $scope.moduleTypes = [
                    {id: 'INSTALLATION_FIX', label: $filter('translate')('INSTALLATION_FIX')},
                    {id: 'INSTALLATION_PORT', label: $filter('translate')('INSTALLATION_PORT')}
                ];

                $scope.setTypeDataLanguage = function () {
                    $scope.moduleTypes[0].label = $filter('translate')('INSTALLATION_FIX');
                    $scope.moduleTypes[1].label = $filter('translate')('INSTALLATION_PORT');
                    $scope.setTypeDataLangDatePicker();
                };

                $scope.clearAll = function () {
                    $scope.clearDate();
                    $scope.tableParams.filter({});
                };

                /**
                 * Updates the table.
                 */
                $rootScope.onTableHandling = function () {
                    $scope.tableParams.reload();
                };

                $scope.isFilter = function () {
                    if ($scope.tableParams == null) return false; 
                    var obj = $scope.tableParams.filter();
                    for (var i in obj) {
                        if (i == 'isForStation') {
                        }
                        else if (obj.hasOwnProperty(i) && obj[i]) {
                            return true;
                        }
                    }
                    return false;
                };

                $scope.changeTaskDate = function (taskID, dateOfTask) {
                    CalibrationTaskServiceCalibrator.changeTaskDate(taskID, dateOfTask)
                        .then(function (result) {
                            if (result.status == 200) {
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('DATE_OF_TASK_CHANGED'));
                            } else if (result.status == 403) {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_CHANGE_DATE_OF_TASK'));
                            } else {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_WHILE_CHANGING_TASK_DATE'));
                            }
                            $scope.tableParams.reload();
                        });
                };

                $scope.openVerificationListModal = function (calibrationTaskID, dateOfTask, nameOfStationOrTeam) {
                    $rootScope.dateOfTaskModalView = dateOfTask;
                    $rootScope.nameOfStationOrTeamModalView = nameOfStationOrTeam;
                    var verificationsModal;
                    $rootScope.taskForTeam = $scope.forBrigades;
                    verificationsModal = $modal
                        .open({
                            animation: true,
                            controller: 'VerificationListModalController',
                            templateUrl: '/resources/app/calibrator/views/modals/verification-list-modal.html',
                            windowClass: 'verification-list-modal',
                            resolve: {
                                taskID: function () {
                                    return calibrationTaskID;
                                }
                            }
                        });
                    verificationsModal.result.then(function () {
                        $rootScope.onTableHandling();
                    }, function () {
                        $rootScope.onTableHandling();
                    });
                };

                $scope.refreshTable = function () {
                    $scope.tableParams.reload();
                };

                $scope.tableParams = new ngTableParams({
                        page: 1,
                        count: 50,
                        sorting: {
                            dateOfTask: 'asc'
                        }, filter: {
                            startDateToSearch: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                            endDateToSearch: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD")
                        }
                    },
                    {
                        total: 0,
                        filterDelay: 10000,
                        getData: function ($defer, params) {
                            $scope.taskIDs = [];
                            var sortCriteria = Object.keys(params.sorting())[0];
                            var sortOrder = params.sorting()[sortCriteria];
                            params.filter().isForStation = true;

                            params.filter().startDateToSearch = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                                params.filter().endDateToSearch = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");


                            var request = $scope.forBrigades ? CalibrationTaskServiceCalibrator.getForTeam(params.page(), params.count(), params.filter(), sortCriteria, sortOrder, $scope.allTests)
                                : CalibrationTaskServiceCalibrator.getPage(params.page(), params.count(), params.filter(), sortCriteria, sortOrder, $scope.allTests);

                            request.success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                                $scope.calendars = {};
                                for (var i = 0; i < result.content.length; i++) {
                                    $scope.calendars[result.content[i].taskID] = false;
                                }
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                        }
                    }
                );

                $scope.popNotification = function (title, text) {
                    toaster.pop('success', title, text);
                };

                $scope.uploadArchiveFix = function () {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'resources/app/calibrator/views/modals/upload-archive.html',
                        controller: 'UploadArchiveController',
                        size: 'lg',
                        resolve: {
                            uploadForStation: function () {
                                return true;
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.tableParams.reload();
                    });
                }

                $scope.uploadArchivePortable = function () {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'resources/app/calibrator/views/modals/upload-archive.html',
                        controller: 'UploadArchiveController',
                        size: 'lg',
                        resolve: {
                            uploadForStation: function () {
                                return false;
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.tableParams.reload();
                    });
                }

                $scope.changeModule = function (taskId, dateOfTask) {
                    $rootScope.dateOfTask = dateOfTask;
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'resources/app/calibrator/views/modals/change-stationOrTeam-modal.html',
                        controller: 'ChangeStationOrTeamModalController',
                        size: 'xs',
                        windowClass: 'xx-dialog',
                        resolve: {
                            modules: function () {
                                return verificationServiceCalibrator.receiveAllWorkers()
                                    .success(function (modules) {
                                            return modules;
                                        }
                                    );
                            }
                        }
                    });

                    modalInstance.result.then(function (serialNumber) {
                        var dto = {};
                        dto.moduleSerialNumber = serialNumber;
                        dto.taskId = taskId;
                        dto.dateOfTask = $rootScope.dateOfTask
                        verificationServiceCalibrator.changeWorkers(dto).then(function (data) {
                            switch (data.status) {
                                case 200:
                                {
                                    toaster.pop('success', $filter('translate')('INFORMATION'),
                                        $filter('translate')('SUCCESSFUL_EDITED'));
                                    $scope.tableParams.reload();
                                    break;
                                }
                                case 403:
                                {
                                    toaster.pop('error', $filter('translate')('INFORMATION'),
                                        $filter('translate')('ERROR_WHILE_CREATING_TASK'));
                                    break;
                                }
                                default:
                                {
                                    toaster.pop('error', $filter('translate')('INFORMATION'),
                                        $filter('translate')('SAVE_VERIF_ERROR'));
                                }
                            }
                        });

                    });
                    $scope.tableParams.reload();
                };

            }]);
