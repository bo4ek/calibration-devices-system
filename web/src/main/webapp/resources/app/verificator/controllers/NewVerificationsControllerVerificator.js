angular
    .module('employeeModule')
    .controller('NewVerificationsControllerVerificator', ['$scope', '$log', '$modal', '$location', 'CalibrationTestServiceCalibrator', 'VerificationServiceVerificator',
        '$rootScope', 'ngTableParams', '$filter', '$timeout', '$translate',
        function ($scope, $log, $modal, $location, calibrationTestServiceCalibrator, verificationServiceVerificator, $rootScope, ngTableParams, $filter, $timeout,
                  $translate) {

            $scope.resultsCount = 0;
            $scope.pageNumber = 1;
            $scope.itemsPerPage = 50;
            $scope.sortCriteria = 'default';
            $scope.sortOrder = 'default';
            $scope.path = $location.path();

            $scope.dataToManualTest = new Map();
            $scope.rejectedPage = $scope.path == "/verifications/rejected";

            /**
             * this function return true if is StateVerificatorEmployee
             */
            $scope.isStateVerificatorEmployee = function () {
                verificationServiceVerificator.getIfEmployeeStateVerificator().success(function (data) {
                    $scope.isEmployee = data;
                });

            };

            /**
             * create data of tests for manual protocol
             */
            $scope.createManualTest = function (verification) {
                var manualTest = {
                    realiseYear: verification.realiseYear,
                    numberCounter: verification.numberOfCounter,
                    status: verification.status
                };
                $scope.dataToManualTest.set(verification.id, manualTest);
            };


            $scope.openAddTest = function (verification, index) {
                if (!verification.manual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'index': (($scope.pageNumber - 1) * $scope.itemsPerPage) + index,
                        'sortCriteria': $scope.sortCriteria,
                        'sortOrder': $scope.sortOrder,
                        'loadProtocol': 1,
                        'ver': 1
                    });
                } else {
                    $scope.createManualTest(verification);
                    calibrationTestServiceCalibrator.dataOfVerifications().setIdsOfVerifications($scope.dataToManualTest);
                    $location.path('/calibrator/verifications/calibration-test/').search({
                        'param': verification.id,
                        'editVer': 1,
                        'loadProtocol': 1,
                        'ver': 1
                    });
                }
            };

            $scope.isStateVerificatorEmployee();


            $scope.clearAll = function () {
                $scope.tableParams.filter({});
                $scope.clearInitialDate();
                $scope.clearSentToVerificatorDate();
            };

            $scope.clearInitialDate = function () {
                $scope.datePicker.initialDate = $scope.defaultInitialDate;
                $scope.tableParams.filter()['date'] = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");
            };

            $scope.clearSentToVerificatorDate = function () {
                $scope.datePicker.sentToVerificatorDate = $scope.defaultSentToVerificatorDate;
                $scope.tableParams.filter()['sentToVerificatorDateFrom'] = $scope.datePicker.sentToVerificatorDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['sentToVerificatorDateTo'] = $scope.datePicker.sentToVerificatorDate.endDate.format("YYYY-MM-DD");
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            /**
             *  Date picker and formatter setup
             *
             */

            $scope.datePicker = {};
            $scope.datePicker.initialDate = null;
            $scope.datePicker.sentToVerificatorDate = null;
            $scope.defaultInitialDate = null;
            $scope.defaultSentToVerificatorDate = null;


            $scope.initDatePicker = function (date, sentToVerificatorDate) {
                $scope.datePicker.initialDate = {
                    startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                    endDate: moment()
                };

                $scope.datePicker.sentToVerificatorDate = {
                    startDate: (sentToVerificatorDate ? moment(sentToVerificatorDate, "YYYY-MM-DD") : moment()),
                    endDate: moment()
                };
                if ($scope.defaultInitialDate == null) {
                    $scope.defaultInitialDate = angular.copy($scope.datePicker.initialDate);
                }

                if ($scope.defaultSentToVerificatorDate == null) {
                    $scope.defaultSentToVerificatorDate = angular.copy($scope.datePicker.sentToVerificatorDate);
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

            $scope.showPickerMy = function ($event) {
                angular.element("#sent_to_verificator_datepicker").trigger("click");
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

            $scope.isSentVerificatorDateDefault = function () {
                var sentToVerificatorDate = $scope.datePicker.sentToVerificatorDate;

                if (sentToVerificatorDate == null || $scope.defaultSentToVerificatorDate == null) {
                    return true;
                }
                if (sentToVerificatorDate.startDate.isSame($scope.defaultSentToVerificatorDate.startDate, 'day')
                    && sentToVerificatorDate.endDate.isSame($scope.defaultSentToVerificatorDate.endDate, 'day')) {
                    return true;
                }
                return false;
            };

            $scope.checkFilters = function () {
                if ($scope.tableParams == null) return false;
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        if (i == 'date' || i == 'endDate' || i == 'sentToVerificatorDateFrom' || i == 'sentToVerificatorDateTo')
                            continue;
                        return true;
                    }
                }
                return false;
            };

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
                    if (verification.providerEmployee) {
                        $scope.resolveVerificationId(verification.id);
                    }
                });
            };

            $scope.allSelected = function (value) {
                if (value !== undefined) {
                    return setAllSelected(value);
                } else {
                    return getAllSelected();
                }
            };

            verificationServiceVerificator.getEarliestDateOfCreatingProtocol().success(function (date) {
                verificationServiceVerificator.getEarliestDateOfSentToVerificator().success(function (sentToVerificatorDate) {
                    $scope.initDatePicker(date, sentToVerificatorDate);
                    $scope.tableParams = new ngTableParams({
                        page: $scope.pageNumber,
                        count: $scope.itemsPerPage,
                        sorting: {
                            default: 'default'
                        }, filter: {
                            date: $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD"),
                            endDate: $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD"),
                            sentToVerificatorDateFrom: $scope.datePicker.sentToVerificatorDate.startDate.format("YYYY-MM-DD"),
                            sentToVerificatorDateTo: $scope.datePicker.sentToVerificatorDate.endDate.format("YYYY-MM-DD")
                        }
                    }, {
                        total: 0,
                        getData: function ($defer, params) {

                            var sortCriteria = Object.keys(params.sorting())[0];
                            var sortOrder = params.sorting()[sortCriteria];

                            params.filter().date = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                            params.filter().endDate = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");

                            params.filter().sentToVerificatorDateFrom = $scope.datePicker.sentToVerificatorDate.startDate.format("YYYY-MM-DD");
                            params.filter().sentToVerificatorDateTo = $scope.datePicker.sentToVerificatorDate.endDate.format("YYYY-MM-DD");

                            var request = $scope.path == "/verifications/rejected" ? verificationServiceVerificator.geRejectedVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                                : verificationServiceVerificator.getNewVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)

                            request.success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                                $scope.allVerifications = result.content;
                                $scope.pageNumber = params.page();
                                $scope.itemsPerPage = params.count();
                                $scope.sortCriteria = sortCriteria;
                                $scope.sortOrder = sortOrder;

                            }, function (result) {
                            });
                        }
                    })
                })
            });

            $scope.markAsRead = function (id) {
                var dataToSend = {
                    verificationId: id,
                    readStatus: 'READ'
                };

                verificationServiceVerificator.markVerificationAsRead(dataToSend).success(function () {
                    $rootScope.$broadcast('verification-was-read');
                    $scope.tableParams.reload();
                });
            };

            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/verificator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerVerificator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceVerificator.getNewVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
                                    if (verifReadStatus == 'UNREAD') {
                                        $scope.markAsRead(verifId);
                                    }
                                    return verification;
                                });
                        }
                    }
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            $scope.testReview = function (verifId) {
                $modal.open({

                    animation: true,
                    templateUrl: 'resources/app/verificator/views/modals/testReview.html',
                    controller: 'CalibrationTestReviewControllerVerificator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceVerificator.getCalibraionTestDetails(verifId)
                                .success(function (calibrationTest) {
                                    return calibrationTest;
                                })
                                .error(function () {
                                });
                        }
                    }
                });
            };

            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;

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

            $scope.openRejectTest = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/verificator/views/modals/mailComment.html',
                    controller: 'TestRejectControllerVerificator',
                    size: 'md'

                });
                if ($scope.idsOfVerifications.length === 0) {
                    $scope.idsOfVerifications[0] = verificationId;
                }
                /**
                 * executes when modal closing
                 */
                modalInstance.result.then(function (formData) {
                    var dataToSend = {
                        idsOfVerifications: $scope.idsOfVerifications,
                        message: formData.message
                    };

                    verificationServiceVerificator
                        .rejectTestToCalibrator(dataToSend)
                        .then(function (status) {
                            $scope.tableParams.reload();
                            $rootScope.$broadcast('verification-sent-to-calibrator');
                            if (status.status == 201) {
                                $rootScope.onTableHandling();
                            }
                            if (status.status == 200) {
                                $modal.open({
                                    animation: true,
                                    templateUrl: 'resources/app/verificator/views/modals/rejecting-success.html',
                                    controller: function ($modalInstance) {
                                        this.ok = function () {
                                            $modalInstance.close();
                                        }
                                    },
                                    controllerAs: 'successController',
                                    size: 'md'
                                });
                            }
                        });

                    $scope.idsOfVerifications = [];
                    $scope.checkedItems = [];

                });
            };

            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    {
                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications
                        };
                        verificationServiceVerificator
                            .sendVerificationsToProvider(dataToSend)
                            .then(function (status) {
                                if (status.status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if (status.status == 200) {
                                    $modal.open({
                                        animation: true,
                                        templateUrl: 'resources/app/verificator/views/modals/sending-success.html',
                                        controller: function ($modalInstance) {
                                            this.ok = function () {
                                                $modalInstance.close();
                                            }
                                        },
                                        controllerAs: 'successController',
                                        size: 'md'
                                    });
                                }
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-provider');
                            });

                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                    }
                    ;
                } else {
                    $scope.isClicked = true;
                }
            };


            //For NOT_OK!!!
            $scope.openSendingModalNotOK = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'resources/app/verificator/views/modals/verification-sending.html',
                        controller: 'SendingModalControllerVerificator',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return verificationServiceVerificator.getProviders()
                                    .success(function (providers) {
                                        return providers;
                                    });
                            }
                        }
                    });


                    //executes when modal closing
                    modalInstance.result.then(function (formData) {

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            organizationId: formData.provider.id
                        };
                        verificationServiceVerificator
                            .sendVerificationNotOkStatus(dataToSend)
                            .sendEmployeeVerificator(dataToSend)
                            .success(function () {
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-provider');
                            });

                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                    });
                } else {
                    $scope.isClicked = true;
                }
            };

            /**
             * removing employee from chosen verification
             * @param verificationId
             */
            $scope.removeVerificatorEmployee = function (verificationId) {
                var dataToSend = {
                    idVerification: verificationId
                };
                verificationServiceVerificator.cleanVerificatorEmployeeField(dataToSend)
                    .success(function () {
                        $scope.tableParams.reload();
                    });
            };

            /**
             * assigning new employee to verification
             * @param verificationId
             */
            $scope.addVerificatorEmployee = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/verificator/views/employee/assigning-verificatorEmployee.html',
                    controller: 'VerificatorEmployeeControllerVerificator',
                    size: 'md',
                    windowClass: 'xx-dialog',
                    resolve: {
                        verificatorEmployee: function () {
                            return verificationServiceVerificator.getVerificators()
                                .success(function (verificators) {
                                        return verificators;
                                    }
                                );
                        }
                    }
                });
                if ($scope.idsOfVerifications.length === 0) {
                    $scope.idsOfVerifications[0] = verificationId;
                }
                /**
                 * executes when modal closing
                 */
                modalInstance.result.then(function (formData) {
                    var dataToSend = {
                        idsOfVerifications: $scope.idsOfVerifications,
                        employeeVerificator: formData.provider
                    };
                    verificationServiceVerificator
                        .sendEmployeeVerificator(dataToSend)
                        .success(function (data) {
                            alert($filter('translate')('COUNT_OF_SUCCESS_ASSIGN_VERIFICATOR_EMPLOYEE') + "  " + data);
                            $scope.tableParams.reload();
                        });
                });
            };


            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };

            $scope.initiateVerification = function () {

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/provider/views/modals/initiate-verification.html',
                    controller: 'AddingVerificationsControllerProvider',
                    size: 'lg'
                });
            };

        }]);
