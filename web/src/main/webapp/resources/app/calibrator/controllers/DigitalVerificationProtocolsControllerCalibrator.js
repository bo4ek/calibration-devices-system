angular
    .module('employeeModule')
    .controller('DigitalVerificationProtocolsControllerCalibrator', ['$scope', '$log', '$modal',
        'DigitalVerificationProtocolsServiceCalibrator', 'CalibrationTestServiceCalibrator', 'VerificationServiceCalibrator', '$rootScope', 'ngTableParams', '$filter', 'toaster', '$location', '$translate',

        function ($scope, $log, $modal, digitalVerificationProtocolsServiceCalibrator, calibrationTestServiceCalibrator, verificationServiceCalibrator, $rootScope, ngTableParams,
                  $filter, toaster, $location, $translate) {
            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPerPage = 5;
            $scope.pageContent = [];
            $scope.checked = false;
            $scope.path = $location.path();
            $scope.rejected = $scope.path == "/calibrator/protocols/rejected";

            $scope.$watch('globalSearchParams', function (newParam, oldParam) {
                if ($scope.hasOwnProperty("tableParams")) {
                    $scope.tableParams.reload();
                }
            }, true);
            $scope.clearAll = function () {
                $scope.selectedStatus.name = null;
                $scope.tableParams.filter({});
                $scope.clearInitialDate();
            };

            $scope.clearInitialDate = function () {
                $scope.datePicker.initialDate = $scope.defaultInitialDate;
                $scope.tableParams.filter()['date'] = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.datePicker = {};
            $scope.datePicker.initialDate = null;
            $scope.defaultInitialDate = null;


            $scope.initDatePicker = function (date) {

                $scope.datePicker.initialDate = {
                    startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                    endDate: moment()
                };

                if ($scope.defaultInitialDate == null) {
                    $scope.defaultInitialDate = angular.copy($scope.datePicker.initialDate);
                }
                moment.locale('uk');
                $scope.opts = {
                    format: 'DD-MM-YYYY',
                    showDropdowns: true,
                    locale: {
                        firstDay: 1,
                        fromLabel: $filter('translate')('FROM'),
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
                $scope.opts.ranges[$filter('translate')('ALL_TIME')] = [$scope.defaultInitialDate.startDate, $scope.defaultInitialDate.endDate];
            };

            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };

            $scope.isInitialDateDefault = function () {
                var initialDate = $scope.datePicker.initialDate;

                if (initialDate == null || $scope.defaultInitialDate == null) {
                    return true;
                }
                if (initialDate.startDate.isSame($scope.defaultInitialDate.startDate, 'day')
                    && initialDate.endDate.isSame($scope.defaultInitialDate.endDate, 'day')) {
                    return true;
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
                if ($scope.isInitialDateDefault())
                    return false;
                else if (!moment(obj.date).isSame($scope.defaultInitialDate.startDate)
                    || !moment(obj.endDate).isSame($scope.defaultInitialDate.endDate)) {
                    return true;
                }
                return false;
            };

            $scope.selectedStatus = {
                name: null
            };

            $scope.statusData = [
                {id: 'IN_PROGRESS', label: $filter('translate')('IN_PROGRESS')},
                {id: 'TEST_PLACE_DETERMINED', label: $filter('translate')('TEST_PLACE_DETERMINED')},
                {id: 'SENT_TO_TEST_DEVICE', label: $filter('translate')('SENT_TO_TEST_DEVICE')},
                {id: 'TEST_COMPLETED', label: $filter('translate')('TEST_COMPLETED')}
            ];

            $scope.cancelTest = function (verification) {
                var idVerification = verification.id;
                if (!verification.isManual) {
                    digitalVerificationProtocolsServiceCalibrator.cancelProtocol(idVerification)
                        .then(function (response) {
                            switch (response.status) {
                                case 200:
                                {
                                    $scope.tableParams.reload();
                                    break;
                                }

                            }
                            }
                        );
                }
            };


            var dateRequest = $scope.rejected ? digitalVerificationProtocolsServiceCalibrator.getRejectedVerificationEarliestDate()
                : digitalVerificationProtocolsServiceCalibrator.getNewVerificationEarliestDate();
            dateRequest.success(function (date) {
                $scope.initDatePicker(date);
                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 10,
                    sorting: {
                        default: 'default'
                    }, filter: {
                        date: $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD"),
                        status: null
                    }
                }, {
                    total: 0,
                    filterDelay: 1500,
                    getData: function ($defer, params) {
                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        params.filter().date = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");

                        if ($scope.selectedStatus.name != null) {
                            params.filter().status = $scope.selectedStatus.name.id;
                        }
                        else {
                            params.filter().status = null;
                        }
                        var request = $scope.rejected ? digitalVerificationProtocolsServiceCalibrator.getRejectedProtocols(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                            : digitalVerificationProtocolsServiceCalibrator.getProtocols(params.page(), params.count(), params.filter(), sortCriteria, sortOrder);
                        request.success(function (result) {
                            $scope.totalItems = result.totalItems;
                            $defer.resolve(result.content);
                            params.total(result.totalItems);
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                    }
                })
            });
            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;
            $scope.idsOfCalibrators = null;
            $scope.dataToManualTest = new Map();


            /**
             * push verification id to array
             */
            $scope.resolveVerificationId = function (id) {
                var index = $scope.idsOfVerifications.indexOf(id);
                if (index === -1) {
                    $scope.idsOfVerifications.push(id);
                    index = $scope.idsOfVerifications.indexOf(id);
                }

                if (!$scope.checkedItems[index]) {
                    $scope.idsOfVerifications.splice(index, 1, id);
                    $scope.checkedItems.splice(index, 1, true);
                } else {
                    $scope.idsOfVerifications.splice(index, 1);
                    $scope.checkedItems.splice(index, 1);
                }
                checkForEmpty();
            };
            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        backdrop: 'static',
                        templateUrl: 'resources/app/calibrator/views/modals/protocols-sending.html',
                        controller: 'DigitalProtocolsSendingModalControllerCalibrator',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return digitalVerificationProtocolsServiceCalibrator.getVerificators()
                                    .success(function (data) {

                                            return data;
                                        }
                                    );
                            }
                        }
                    });

                    /**
                     * executes when modal closing
                     */
                    modalInstance.result.then(function (formData) {

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            organizationId: formData.verificator.id
                        };

                        digitalVerificationProtocolsServiceCalibrator.sendProtocols(dataToSend)
                            .success(function () {
                                $log.debug('success sending');
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-verificator');
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('SUCCESS_SENT'));
                            });
                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                    });
                } else {
                    $scope.isClicked = true;
                }
            };

            /**
             * check if idsOfVerifications array is empty
             */
            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };

            $scope.openTest = function (verification) {
                if (!verification.manual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                } else {
                    $scope.createManualTest(verification);
                    calibrationTestServiceCalibrator.dataOfVerifications().setIdsOfVerifications($scope.dataToManualTest);
                    $location.path('/calibrator/verifications/calibration-test/').search({
                        param: verification.id
                    });
                }
            };

            $scope.createManualTest = function (verification) {
                var manualTest = {
                    realiseYear: verification.realiseYear,
                    numberCounter: verification.numberOfCounter,
                    status: verification.status
                };
                $scope.dataToManualTest.set(verification.id, manualTest);
            };

            $scope.openDetails = function (verificationId) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verificationId)
                                .success(function (verification) {
                                    return verification;
                                });
                        }
                    }
                });
            };

        }]);
