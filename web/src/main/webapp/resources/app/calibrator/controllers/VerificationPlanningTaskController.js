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
                $scope.clearDate(); // sets 'all time' timerange
            };

            $scope.clearDate = function () {
                //daterangepicker doesn't support null dates
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                //setting corresponding filters with 'all time' range
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

            $scope.selectedSealPresence = {
                name: $scope.statusSealPresence
            };

            $scope.statusServiceability = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.selectedServiceability = {
                name: $scope.statusServiceability
            };

            $scope.setTypeDataLanguage = function () {
                $scope.statusSealPresence[0].label = $filter('translate')('true');
                $scope.statusSealPresence[1].label = $filter('translate')('false');
                $scope.statusServiceability[0].label = $filter('translate')('true');
                $scope.statusServiceability[1].label = $filter('translate')('false');
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
                    //earliest day of  all the verifications available in table
                    //we should reformat it here, because backend currently gives date in format "YYYY-MM-DD"
                    endDate: moment() // current day
                };

                if ($scope.defaultDate == null) {
                    //copy of original daterange
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

                $scope.setTypeDataLangDatePicker = function () {
                    var lang = $translate.use();
                    if (lang === 'ukr') {
                        moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                    } else {
                        moment.locale('en'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
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

                $scope.setTypeDataLanguage = function () {
                    $scope.statusSealPresence[0].label = $filter('translate')('true');
                    $scope.statusSealPresence[1].label = $filter('translate')('false');
                    $scope.statusServiceability[0].label = $filter('translate')('true');
                    $scope.statusServiceability[1].label = $filter('translate')('false');
                    $scope.setTypeDataLangDatePicker();
                };

                $scope.setTypeDataLangDatePicker();
            };


            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };


            $scope.isDateDefault = function () {
                var pickerDate = $scope.myDatePicker.pickerDate;

                if (pickerDate == null || $scope.defaultDate == null) { //moment when page is just loaded
                    return true;
                }
                if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') //compare by day
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
                //console.log("isFilter");
                if ($scope.tableParams == null) return false; //table not yet initialized
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
                if ($scope.tableParams == null) return false; //table not yet initialized
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        if (i == 'date' || i == 'endDate')
                            continue; //check for these filters is in another function
                        return true;
                    }
                }
                return false;
            };

            $scope.checkDateFilters = function () {
                if ($scope.tableParams == null) return false; //table not yet initialized
                var obj = $scope.tableParams.filter();
                if ($scope.isDateDefault())
                    return false;
                else if (!moment(obj.date).isSame($scope.defaultDate.startDate)
                    || !moment(obj.endDate).isSame($scope.defaultDate.endDate)) {
                    //filters are string,
                    // so we are temporarily convertin them to momentjs objects
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
                    count: 10
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
                        else {
                            params.filter().status = null; //case when the filter is cleared with a button on the select
                        }

                        if ($scope.selectedSealPresence.name != null) {
                            params.filter().sealPresence = $scope.selectedSealPresence.name.id;
                        }
                        else {
                            params.filter().sealPresence = null;
                        }

                        if ($scope.selectedServiceability.name != null) {
                            params.filter().serviceability = $scope.selectedServiceability.name.id;
                        }
                        else {
                            params.filter().serviceability = null;
                        }


                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("x");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("x");

                        verificationPlanningTaskService.getVerificationsByCalibratorEmployeeAndTaskStatus(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                            .success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                            });
                    }

                })
            });

            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];

            /**
             * adds selected verificationId to the array
             * or delete it if it when it is not selected
             * but it it is still in the array
             *
             * @param id
             */
            $scope.resolveVerificationId = function (id) {
                var index = $scope.idsOfVerifications.indexOf(id);
                if (index > -1) {
                    $scope.idsOfVerifications.splice(index, 1);
                } else {
                    $scope.idsOfVerifications.push(id);
                }
            };

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
                    });
                }
            };

            /**
             * opens task for team modal
             * if task saved successfully reloads
             * table data
             */
            $scope.openTaskForTeam = function () {
                $rootScope.verifIds = [];
                for (var i = 0; i < $scope.idsOfVerifications.length; i++) {
                    $rootScope.verifIds[i] = $scope.idsOfVerifications[i];
                }
                // $rootScope.emptyStatus = $scope.allIsEmpty;
                $scope.$modalInstance = $modal.open({
                    animation: true,
                    controller: 'TaskForTeamModalControllerCalibrator',
                    templateUrl: 'resources/app/calibrator/views/modals/addTaskForTeamModal.html'
                });
                $scope.$modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                    $scope.checkedItems = [];
                    $scope.idsOfVerifications = [];
                    // $scope.allIsEmpty = true;
                });
            };

            /**
             * opens counter info modal
             * if task saved successfully reloads
             * table data
             */
            $rootScope.verificationId = null;
            $scope.openCounterInfoModal = function (id) {
                $rootScope.verificationId = id;
                $log.debug($rootScope.verificationId);
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

        }]);


