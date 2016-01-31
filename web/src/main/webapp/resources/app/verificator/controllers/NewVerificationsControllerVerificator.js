angular
    .module('employeeModule')
    .controller('NewVerificationsControllerVerificator', ['$scope', '$log', '$modal', '$location', 'CalibrationTestServiceCalibrator', 'VerificationServiceVerificator',
        '$rootScope', 'ngTableParams', '$filter', '$timeout', '$translate',
        function ($scope, $log, $modal, $location, calibrationTestServiceCalibrator,  verificationServiceVerificator, $rootScope, ngTableParams, $filter, $timeout,
                  $translate ) {

            $scope.resultsCount = 0;

            $scope.dataToManualTest = new Map();

            /**
             * this function return true if is StateVerificatorEmployee
             */
            $scope.isStateVerificatorEmployee = function () {
                verificationServiceVerificator.getIfEmployeeStateVerificator().success(function(data){
                    $scope.isEmployee =  data;
                });

            };

            /**
             * create data of tests for manual protocol
             */
            $scope.createManualTest = function (verification) {
                var manualTest = {
                    standardSize: verification.standardSize,
                    symbol: verification.symbol,
                    realiseYear: verification.realiseYear,
                    numberCounter: verification.numberCounter,
                    counterId: verification.counterId,
                    status:verification.status
                };
                $scope.dataToManualTest.set(verification.id, manualTest);
            };


            $scope.openAddTest = function (verification) {
                if (!verification.isManual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
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
                $scope.selectedStatus.name = null;
                $scope.tableParams.filter({});
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
            };

            $scope.clearStatus = function (status) {
                if (!status) {
                    $scope.clearAll();
                }
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            /**
             *  Date picker and formatter setup
             *
             */

            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function () {

                $scope.myDatePicker.pickerDate = {

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
                        singleDatePicker: true,
                        showDropdowns: true,
                        eventHandlers: {}
                    };
                };

                $scope.setTypeDataLangDatePicker();
            };

            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };

            $scope.clearDate = function () {
                //daterangepicker doesn't support null dates
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                $scope.tableParams.filter({});
            };

            $scope.initDatePicker();

            $scope.selectedStatus = {
                name: null
            }

            $scope.statusData = [
                {id: 'SENT_TO_VERIFICATOR', label: null},
                {id: 'TEST_OK', label: null},
                {id: 'TEST_NOK', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {

                    $scope.statusData[0].label = 'Предявлено повірнику';
                    $scope.statusData[1].label = 'Перевірено придатний';
                    $scope.statusData[2].label = 'Перевірено непридатний';

                } else if (lang === 'eng') {

                    $scope.statusData[0].label = 'Sent to verificator';
                    $scope.statusData[1].label = 'Tested OK';
                    $scope.statusData[2].label = 'Tested NOK';
                }
                $scope.setTypeDataLangDatePicker();
            };

            $scope.setTypeDataLanguage();


            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    date: 'desc'
                }
            }, {
                total: 0,
                filterDelay: 1500,
                getData: function ($defer, params) {

                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    if ($scope.myDatePicker.pickerDate.startDate != null) {
                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                    }

                    if ($scope.selectedStatus.name != null) {
                        params.filter().status = $scope.selectedStatus.name.id;
                    }

                    if (params.filter().client_full_name != null && params.filter().client_full_name.split(" ").length > 1) {
                        var allNames = params.filter().client_full_name.split(" ");
                        var modifiedFilter = params.filter();
                        if (allNames.length == 1) {
                            modifiedFilter.lastName = allNames[0].trim();
                        } else if (allNames.length == 2) {
                            modifiedFilter.lastName = allNames[0].trim();
                            modifiedFilter.firstName = allNames[1].trim();
                        } else if (allNames.length == 3) {
                            modifiedFilter.lastName = allNames[0].trim();
                            modifiedFilter.firstName = allNames[1].trim();
                            modifiedFilter.middleName = allNames[2].trim();
                        }
                    } else {
                        params.filter().lastName = null;
                        params.filter().middleName = null;
                        params.filter().firstName = null;
                    }
                    if (params.filter().address != null && params.filter().address.length > 0) {
                        var allPoints = params.filter().address.split(",");
                        var modified = params.filter();
                        if (allPoints.length == 1) {
                            modified.district = allPoints[0].trim();
                        } else if (allPoints.length == 2) {
                            modified.district = allPoints[0].trim();
                            modified.street = allPoints[1].trim();
                        } else if (allPoints.length == 3) {
                            modified.district = allPoints[0].trim();
                            modified.street = allPoints[1].trim();
                            modified.building = allPoints[2].trim();
                        } else if (allPoints.length == 4) {
                            modified.district = allPoints[0].trim();
                            modified.street = allPoints[1].trim();
                            modified.building = allPoints[2].trim();
                            modified.flat = allPoints[3].trim();
                        }
                    } else {
                        params.filter().district = null;
                        params.filter().street = null;
                        params.filter().building = null;
                        params.filter().flat = null;
                    }

                    verificationServiceVerificator.getNewVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                        .success(function (result) {
                            $scope.resultsCount = result.totalItems;
                            $defer.resolve(result.content);
                            params.total(result.totalItems);
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                }
            });

            $scope.checkFilters = function () {
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
                }
                return false;
            };


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
                $modal.open({
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
            };

            $scope.testReview = function (verifId) {
                $log.debug(verifId);
                $modal.open({

                    animation: true,
                    templateUrl: 'resources/app/verificator/views/modals/testReview.html',
                    controller: 'CalibrationTestReviewControllerVerificator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceVerificator.getCalibraionTestDetails(verifId)
                                .success(function (calibrationTest) {
                                    //calibrationTest.id = verifId;
                                    $log.debug('CalibrationTest');
                                    $log.debug(calibrationTest);
                                    return calibrationTest;
                                })
                                .error(function () {
                                    console.log('ERROR');
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
                                $log.debug('success sending');
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-calibrator');
                                if (status.status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if(status.status==200){
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

                        $log.info(dataToSend);
                        verificationServiceVerificator
                            .sendVerificationsToProvider(dataToSend)
                            .then(function (status) {
                                if (status.status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if(status.status==200){
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
                    };
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

                        $log.info(dataToSend);
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
                $log.info(dataToSend);
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
                /**
                 * executes when modal closing
                 */
                modalInstance.result.then(function (formData) {
                    idVerification = 0;
                    var dataToSend = {
                        idVerification: verificationId,
                        employeeVerificator: formData.provider
                    };
                    $log.info(dataToSend);
                    verificationServiceVerificator
                        .sendEmployeeVerificator(dataToSend)
                        .success(function () {
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
