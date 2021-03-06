angular
    .module('employeeModule')
    .controller('VerificationPlanningTaskController', ['$scope', '$log',
        '$modal', 'VerificationPlanningTaskService', 'VerificationServiceCalibrator',
        '$rootScope', 'ngTableParams', '$timeout', '$filter', '$window', '$location', '$translate', 'toaster',
        function ($scope, $log, $modal, verificationPlanningTaskService, verificationServiceCalibrator, $rootScope, ngTableParams,
                  $timeout, $filter, $window, $location, $translate, toaster) {

            $scope.resultsCount = 0;
            $scope.verifications = [];

            $scope.clearAll = function () {
                $scope.selectedStatus.name = null;
                $scope.tableParams.filter({});
                $scope.clearDate();
            };

            $scope.clearDate = function () {
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                $scope.tableParams.filter()['date'] = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.selectedStatus = {
                name: null
            };

            $scope.statusSealPresence = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.selectedSealPresence = {};

            $scope.selectedServiceability = {};

            $scope.selectedVerificationWithDismantle = {};

            $scope.statusServiceability = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.statusVerificationWithDismantle = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                $scope.statusSealPresence[0].label = $filter('translate')('true');
                $scope.statusSealPresence[1].label = $filter('translate')('false');
                $scope.statusServiceability[0].label = $filter('translate')('true');
                $scope.statusServiceability[1].label = $filter('translate')('false');
                $scope.statusVerificationWithDismantle[0].label = $filter('translate')('true');
                $scope.statusVerificationWithDismantle[1].label = $filter('translate')('false');
            };

            $scope.setTypeDataLanguage();

            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function (date) {
                /**
                 *  Date picker and formatter setup
                 *
                 */

                /*TODO: i18n*/
                $scope.myDatePicker.pickerDate = {
                    startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                    endDate: moment() 
                };

                if ($scope.defaultDate == null) {
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

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
                    $scope.opts.ranges[$filter('translate')('ALL_TIME')] = [$scope.defaultDate.startDate, $scope.defaultDate.endDate];
                };

                $scope.setTypeDataLangDatePicker();
            };


            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };


            $scope.isDateDefault = function () {
                var pickerDate = $scope.myDatePicker.pickerDate;

                if (pickerDate == null || $scope.defaultDate == null) { 
                    return true;
                }
                if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day')
                    && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                    return true;
                }
                return false;
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
                    if (i == 'isActive' || (i == "startDateToSearch" || i == "endDateToSearch")) {
                        continue;
                    } else if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
                }
                return false;
            };

            $scope.checkFilters = function () {
                if ($scope.tableParams == null) return false; 
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        if (i == 'date' || i == 'endDate')
                            continue; 
                        return true;
                    }
                }
                return false;
            };

            $scope.checkDateFilters = function () {
                if ($scope.tableParams == null) return false; 
                var obj = $scope.tableParams.filter();
                if ($scope.isDateDefault())
                    return false;
                else if (!moment(obj.date).isSame($scope.defaultDate.startDate)
                    || !moment(obj.endDate).isSame($scope.defaultDate.endDate)) {
                    return true;
                }
                return false;
            };


            verificationPlanningTaskService.getEarliestPlanningTaskDate().success(function (date) {
                /**
                 * fills the planning task table
                 */
                $scope.initDatePicker(date);

                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 50,
                    sorting: {
                        date: 'desc'
                    }, filter: {
                        status: null,
                        noWaterToDate: null,
                        sealPresence: null,
                        serviceability: null,
                        verificationWithDismantle: null,
                        date: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD")
                    }
                }, {
                    total: 0,
                    filterDelay: 1500,
                    getData: function ($defer, params) {
                        $scope.idsOfVerifications = [];

                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        if ($scope.selectedStatus.name != null) {
                            params.filter().status = $scope.selectedStatus.name.id;
                        }

                        if ($scope.selectedSealPresence.name != null) {
                            params.filter().sealPresence = $scope.selectedSealPresence.name.id;
                        }

                        if ($scope.selectedServiceability.name != null) {
                            params.filter().serviceability = $scope.selectedServiceability.name.id;
                        }

                        if ($scope.selectedVerificationWithDismantle.name != null) {
                            params.filter().verificationWithDismantle = $scope.selectedVerificationWithDismantle.name.id;
                        }

                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

                        verificationPlanningTaskService.getVerificationsByCalibratorEmployeeAndTaskStatus(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                            .success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                $scope.allVerifications = result.content;
                                params.total(result.totalItems);
                                $scope.idsOfVerifications = [];
                            });
                    }

                })
            });

            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;

            /**
             * opens task for station modal
             * if task saved successfully reloads
             * table data
             */
            $scope.openTaskForStation = function () {
                if ($scope.idsOfVerifications.length === 0) {
                    toaster.pop('error', $filter('translate')('INFORMATION'),
                        $filter('translate')('NO_VERIFICATIONS_CHECKED'));
                } else {
                    $scope.$modalInstance = $modal.open({
                        animation: true,
                        controller: 'TaskForStationModalControllerCalibrator',
                        templateUrl: 'resources/app/calibrator/views/modals/addTaskForStationModal.html',
                        resolve: {
                            verificationIDs: function () {
                                return $scope.idsOfVerifications;
                            },
                            moduleType: function () {
                                return 'INSTALLATION_PORT';
                            }
                        }
                    });
                    $scope.$modalInstance.result.then(function () {
                        $scope.tableParams.reload();
                        $scope.idsOfVerifications = [];
                        $rootScope.$broadcast('verification-sent-to-station');
                    });
                }
                $scope.allIsEmpty = true;
            };

            /**
             * opens task for team modal
             * if task saved successfully reloads
             * table data
             */
            $scope.openTaskForTeam = function () {
                if ($scope.idsOfVerifications.length === 0) {
                    toaster.pop('error', $filter('translate')('INFORMATION'),
                        $filter('translate')('NO_VERIFICATIONS_CHECKED'));
                } else {
                    $rootScope.verifIds = [];
                    for (var i = 0; i < $scope.idsOfVerifications.length; i++) {
                        $rootScope.verifIds[i] = $scope.idsOfVerifications[i];
                    }
                    $scope.$modalInstance = $modal.open({
                        animation: true,
                        controller: 'TaskForTeamModalControllerCalibrator',
                        templateUrl: 'resources/app/calibrator/views/modals/addTaskForTeamModal.html'
                    });
                    $scope.$modalInstance.result.then(function () {
                        $scope.tableParams.reload();
                        $rootScope.$broadcast('verification-sent-to-team');
                        $scope.idsOfVerifications = [];
                    });
                }
                $scope.allIsEmpty = true;
            };

            /**
             * opens counter info modal
             * if task saved successfully reloads
             * table data
             */
            $rootScope.verificationId = null;
            $scope.openCounterInfoModal = function (id) {
                $rootScope.verificationId = id;
                $scope.$modalInstance = $modal.open({
                    animation: true,
                    controller: 'CounterStatusControllerCalibrator',
                    templateUrl: 'resources/app/calibrator/views/modals/counterStatusModal.html'
                });
                $scope.$modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            $scope.openDetails = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verificationId)
                                .then(function (verification) {
                                    return verification;
                                });
                        }
                    }
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();

                });
            };

            $scope.checkProviderEmployee = function () {
                var result = false;
                angular.forEach($scope.allVerifications, function (verification) {
                    if (verification.providerEmployee) {
                        result = true;
                    }
                });
                return result;
            };

            $scope.cancelPersonForAll = function () {
                var idsOfVerifications = [];
                angular.forEach($scope.allVerifications, function (verification) {
                    idsOfVerifications.push(verification.verificationId);
                });
                var dataToSend = {
                    idsOfVerifications: idsOfVerifications
                };
                verificationServiceCalibrator.cleanCalibratorEmployeeFieldForAll(dataToSend)
                    .success(function () {
                        $scope.tableParams.reload();
                    });
            }

            var getAllSelected = function () {
                if (!$scope.allVerifications) {
                    return false;
                }
                var checkedItems = $scope.allVerifications.filter(function (verification) {
                    return verification.selected;
                });

                return checkedItems.length === $scope.allVerifications.length;
            };

            var setAllSelected = function (value) {
                angular.forEach($scope.allVerifications, function (verification) {
                    verification.selected = value;
                    $scope.resolveVerificationId(verification.verificationId);
                });

            };

            $scope.allSelected = function (value) {
                if (value !== undefined) {
                    return setAllSelected(value);
                } else {
                    return getAllSelected();
                }
            };

            /**
             * push verification id to array
             */

            $scope.resolveVerificationId = function (id) {
                $scope.idsOfVerifications.push(id);
                checkForEmpty();
            };

            $scope.resolveVerification = function (verification) {
                if ($scope.selectedSentVerification && $scope.selectedSentVerification !== verification) {
                    $scope.selectedSentVerification.selected = false;
                }
                $scope.selectedSentVerification = verification;
                $scope.idsOfVerifications[0] = verification.id;

            };

            /**
             * check if idsOfVerifications array is empty
             */
            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };

            $scope.openRejectVerificationModal = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/reject-verification-modal.html',
                    controller: 'RejectVerificationCalibratorController',
                    size: 'md',
                    windowClass: 'xx-dialog',
                    resolve: {
                        rejectVerification: function () {
                            return verificationServiceCalibrator.receiveAllReasons()
                                .success(function (reasons) {
                                        return reasons;
                                    }
                                );
                        }
                    }
                });

                modalInstance.result.then(function (reason) {
                    verificationServiceCalibrator.rejectVerificationByIdAndReason(verificationId, reason.name.id).then(function (data) {
                        switch (data.status) {
                            case 200:
                            {
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('REJECTED'));
                                $scope.tableParams.reload();
                                break;
                            }
                            case 403:
                            {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_ACCESS'));
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
            }

        }]);


