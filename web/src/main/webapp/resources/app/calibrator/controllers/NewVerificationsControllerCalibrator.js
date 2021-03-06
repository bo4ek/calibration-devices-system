angular
    .module('employeeModule')
    .controller('NewVerificationsControllerCalibrator', ['$scope', '$log',
        '$modal', 'VerificationServiceCalibrator',
        '$rootScope', 'ngTableParams', '$timeout', '$filter', '$window', '$location', '$translate', 'toaster',
        'CalibrationTestServiceCalibrator', 'DigitalVerificationProtocolsServiceCalibrator', 'ProfileService',
        function ($scope, $log, $modal, verificationServiceCalibrator, $rootScope, ngTableParams,
                  $timeout, $filter, $window, $location, $translate, toaster, calibrationTestServiceCalibrator, digitalVerificationProtocolsServiceCalibrator, profileService) {

            $scope.resultsCount = 0;

            $scope.searchParameters = [
                {
                    name: 'TASK_STATUS',
                    key: 'taskStatus',
                    type: 'Enumerated',
                    options: ['SENT',
                        'ACCEPTED',
                        'REJECTED',
                        'IN_PROGRESS',
                        'PLANNING_TASK',
                        'TASK_PLANED',
                        'TEST_PLACE_DETERMINED',
                        'SENT_TO_DISMANTLING_TEAM',
                        'SENT_TO_TEST_DEVICE',
                        'TEST_COMPLETED',
                        'SENT_TO_VERIFICATOR',
                        'TEST_OK',
                        'TEST_NOK']
                },
                {
                    name: 'READ_STATUS',
                    key: 'readStatus',
                    type: 'Enumerated',
                    options: ['READ', 'UNREAD']
                },
                {
                    name: 'REJECTED_MESSAGE',
                    key: 'rejectedMessage',
                    type: 'String'
                },
                {
                    name: 'COMMENT',
                    key: 'comment',
                    type: 'String'
                },
                {
                    name: 'PROVIDER_NAME',
                    key: 'providerEmployee',
                    type: 'User'
                },
                {
                    name: "INITIAL_DATE",
                    key: "initialDate",
                    type: "Date"
                },
                {
                    name: "CLIENT_FULL_NAME",
                    key: "clientData",
                    type: "clientData"
                }
            ];
            $scope.globalSearchParams = [];
            $scope.showGlobalSearch = false;


            $scope.isCalibratorEmployee = function () {
                verificationServiceCalibrator.getIfEmployeeCalibrator().success(function (data) {
                    $scope.isEmployee = data;
                });

            };

            $scope.isCalibratorEmployee();

            $scope.$watch('globalSearchParams', function (newParam, oldParam) {
                if ($scope.hasOwnProperty("tableParams")) {
                    $scope.tableParams.reload();
                }
            }, true);
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

            $scope.statusData = [
                {id: 'IN_PROGRESS', label: null},
                {id: 'TEST_PLACE_DETERMINED', label: null},
                {id: 'SENT_TO_TEST_DEVICE', label: null},
                {id: 'TEST_COMPLETED', label: null},
                {id: 'SENT_TO_DISMANTLING_TEAM', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'В роботі';
                    $scope.statusData[1].label = 'Визначено спосіб повірки';
                    $scope.statusData[2].label = 'Відправлено на установку';
                    $scope.statusData[3].label = 'Проведено вимірювання';
                    $scope.statusData[4].label = 'Відправлено на демонтажну бригаду';
                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'In progress';
                    $scope.statusData[1].label = 'Test place determined';
                    $scope.statusData[2].label = 'Sent to test device';
                    $scope.statusData[3].label = 'Test completed';
                    $scope.statusData[4].label = 'Sent to dismantling team';
                }
            };

            $scope.setTypeDataLanguage();

            $scope.statusDismantled = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.selectedDismantled = {
                name: null
            };

            $scope.setTypeDataL = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusDismantled[0].label = 'Так';
                    $scope.statusDismantled[1].label = 'Ні';
                } else if (lang === 'eng') {
                    $scope.statusDismantled[0].label = 'True';
                    $scope.statusDismantled[1].label = 'False';
                }
            };

            $scope.setTypeDataL();

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
                moment.locale('uk'); 
                $scope.opts = {
                    format: 'DD-MM-YYYY',
                    showDropdowns: true,
                    locale: {
                        firstDay: 1,
                        fromLabel: 'Від',
                        toLabel: 'До',
                        applyLabel: "Прийняти",
                        cancelLabel: "Зачинити",
                        customRangeLabel: "Обрати самостійно"
                    },
                    ranges: {
                        'Сьогодні': [moment(), moment()],
                        'Вчора': [moment().subtract(1, 'day'), moment().subtract(1, 'day')],
                        'Цього тижня': [moment().startOf('week'), moment().endOf('week')],
                        'Цього місяця': [moment().startOf('month'), moment().endOf('month')],
                        'Попереднього місяця': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                        'За увесь час': [$scope.defaultDate.startDate, $scope.defaultDate.endDate]
                    },
                    eventHandlers: {}
                };
            };

            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };

            $scope.isDateDefault = function () {
                var pickerDate = $scope.myDatePicker.pickerDate;

                if (pickerDate == null || $scope.defaultDate == null) { 
                    return true;
                }
                return !!(pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day')
                && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day'));

            };

            verificationServiceCalibrator.getNewVerificationEarliestDate().success(function (date) {
                $scope.initDatePicker(date);
                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 50,
                    sorting: {
                        date: 'desc'
                    }, filter: {
                        date: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD"),
                        status: null,
                        dismantled: null
                    }
                }, {
                    total: 0,
                    getData: function ($defer, params) {
                        $scope.idsOfVerifications = [];
                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        if ($scope.selectedStatus.name != null) {
                            params.filter().status = $scope.selectedStatus.name.id;
                        }
                        else {
                            params.filter().status = null; 
                        }
                        if ($scope.selectedDismantled.name != null) {
                            params.filter().dismantled = $scope.selectedDismantled.name.id;
                        }
                        else {
                            params.filter().dismantled = null;
                        }
                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");
                        var searchParams = {};
                        searchParams.globalSearchParams = $scope.globalSearchParams;
                        searchParams.newVerificationsFilterSearch = params.filter();
                        verificationServiceCalibrator.getNewVerifications(params.page(), params.count(), searchParams, sortCriteria, sortOrder)
                            .success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $scope.allVerifications = result.content;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                })
            });

            $scope.$on('calibrator-save-verification', function (event, args) {
                $scope.tableParams.reload();
            });

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

            $scope.markAsRead = function (id) {
                var dataToSend = {
                    verificationId: id,
                    readStatus: 'READ'
                };
                verificationServiceCalibrator.markVerificationAsRead(dataToSend).success(function () {
                    $rootScope.$broadcast('verification-was-read');
                    $scope.tableParams.reload();
                });
            };

            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verifId)
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

            $scope.openTask = function () {
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
                                return 'INSTALLATION_FIX';
                            }
                        }
                    });
                    $scope.$modalInstance.result.then(function () {
                        $scope.tableParams.reload();
                    });
                }
            };


            /**
             * check whether standardSize of counters is identic
             */
            $scope.checkStandardSize = function (map) {
                var setOfStandardSize = new Set();
                map.forEach(function (value, key) {
                    setOfStandardSize.add(value.standardSize);
                }, map);
                return setOfStandardSize.size <= 1;
            };

            /**
             * check is a manual completed test for pass
             */
            $scope.checkSingleManualCompletedTest = function (verification) {
                if ($scope.dataToManualTest.size == 0 && verification.status == 'TEST_COMPLETED' && verification.isManual) {
                    $scope.createManualTest(verification);
                }
            };

            /**
             * redirect to manual test
             */
            $scope.openTests = function (verification) {
                if (!$scope.dataToManualTest.has(verification.id)) {
                    $scope.createDataForManualTest(verification);
                }
                if ($scope.checkStandardSize($scope.dataToManualTest)) {
                    $scope.checkSingleManualCompletedTest(verification);
                    calibrationTestServiceCalibrator.dataOfVerifications().setIdsOfVerifications($scope.dataToManualTest);
                    var url = $location.path('/calibrator/verifications/calibration-test/').search({param: verification.id});
                } else {
                    modalStandartSize();
                }
            };

            /**
             * open modal if standard size of counters are different
             */
            function modalStandartSize() {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/incorrectStandartSize.html',
                    controller: function ($modalInstance) {
                        this.ok = function () {
                            $modalInstance.close();
                        };
                        closeTime();
                        function closeTime() {
                            $timeout(function () {
                                $modalInstance.close();
                            }, 5000);
                        }
                    },
                    controllerAs: 'successController',
                    size: 'md'
                });
            }

            $scope.openAddTest = function (verification) {
                if (!verification.isManual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                } else {
                    $scope.openTests(verification);
                }
            };

            $scope.idsOfVerifications = [];
            $scope.allIsEmpty = true;
            $scope.dataToManualTest = new Map();


            /**
             * create data of tests for manual protocol
             */
            $scope.createDataForManualTest = function (verification) {
                if (verification.status != 'TEST_COMPLETED') {
                    if ($scope.dataToManualTest.has(verification.id)) {
                        $scope.dataToManualTest.delete(verification.id);
                    } else {
                        $scope.createManualTest(verification);
                    }
                }
            };

            $scope.createManualTest = function (verification) {
                var manualTest = {
                    standardSize: verification.standardSize,
                    symbol: verification.symbol,
                    realiseYear: verification.realiseYear,
                    numberCounter: verification.numberCounter,
                    counterId: verification.counterId,
                    status: verification.status,
                    measurementDeviceType: verification.measurementDeviceType
                };
                $scope.dataToManualTest.set(verification.id, manualTest);
            };

            $scope.resolveVerificationId = function (verification) {
                $scope.createDataForManualTest(verification);
                var index = $scope.idsOfVerifications.indexOf(verification.id);
                if (index > -1) {
                    $scope.idsOfVerifications.splice(index, 1);
                } else {
                    $scope.idsOfVerifications.push(verification.id);
                }
                checkForEmpty();
            };

            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'resources/app/calibrator/views/modals/verification-sending.html',
                        controller: 'SendingModalControllerCalibrator',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return verificationServiceCalibrator.getVerificators()
                                    .success(function (verificators) {
                                        return verificators;
                                    });
                            }
                        }
                    });

                    modalInstance.result.then(function (verificator) {

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            organizationId: verificator.id
                        };
                        verificationServiceCalibrator
                            .sendVerificationsToCalibrator(dataToSend)
                            .success(function () {
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-verificator');
                            });
                        $scope.idsOfVerifications = [];
                    });
                } else {
                    $scope.isClicked = true;
                }
            };

            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };

            $scope.uploadBbiFile = function (idVerification) {

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/upload-bbiFile.html',
                    controller: 'UploadBbiFileController',
                    size: 'lg',
                    resolve: {
                        verification: function () {
                            return idVerification;

                        }
                    }
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

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
                } else if (verification.status == 'TEST_COMPLETED') {
                    calibrationTestServiceCalibrator.deleteTestManual(verification.id)
                        .then(function (status) {
                            if (status == 201) {
                                $rootScope.onTableHandling();
                            }
                            if (status == 200) {
                                $scope.tableParams.reload();
                            }
                        })
                }
            };


            /**
             *  Date picker and formatter setup
             *
             */
            $scope.openState = {};
            $scope.openState.isOpen = false;

            $scope.open = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.openState.isOpen = true;
            };


            $scope.dateOptions = {
                formatYear: 'yyyy',
                startingDay: 1,
                showWeeks: 'false'
            };

            $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
            $scope.format = $scope.formats[2];

            $scope.initiateVerification = function () {
                $rootScope.verifIDforTempl = $scope.idsOfVerifications[0];
                var modalInstance = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'resources/app/calibrator/views/modals/initiate-verification.html',
                    controller: 'AddingVerificationsControllerCalibrator',
                    size: 'lg'
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            $scope.removeCalibratorEmployee = function (verifId) {
                var dataToSend = {
                    idVerification: verifId
                };
                verificationServiceCalibrator.cleanCalibratorEmployeeField(dataToSend)
                    .success(function () {
                        $scope.tableParams.reload();
                    });
            };

            $scope.addCalibratorEmployee = function (verificationId) {
                profileService.havePermissionToAssignPerson()
                    .then(function (response) {
                        if (response.data) {
                            var modalInstance = $modal.open({
                                animation: true,
                                templateUrl: 'resources/app/calibrator/views/employee/assigning-calibratorEmployee.html',
                                controller: 'CalibratorEmployeeControllerCalibrator',
                                size: 'md',
                                windowClass: 'xx-dialog',
                                resolve: {
                                    calibratorEmploy: function () {
                                        return verificationServiceCalibrator.getCalibrators()
                                            .success(function (calibrators) {
                                                    return calibrators;
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
                                    employeeCalibrator: formData.provider
                                };
                                verificationServiceCalibrator
                                    .sendEmployeeCalibrator(dataToSend)
                                    .success(function () {
                                        $scope.tableParams.reload();
                                    });
                            });
                        } else {

                            verificationServiceCalibrator
                                .assignEmployeeCalibrator(verificationId)
                                .then(function () {
                                    $scope.tableParams.reload();
                                });
                        }
                    });
            };

            $scope.assignEmployeeCalibratorForAll = function () {
                profileService.havePermissionToAssignPerson()
                    .then(function (response) {
                        if (response.data) {
                            var modalInstance = $modal.open({
                                animation: true,
                                templateUrl: 'resources/app/calibrator/views/employee/assigning-calibratorEmployee.html',
                                controller: 'CalibratorEmployeeControllerCalibrator',
                                size: 'md',
                                windowClass: 'xx-dialog',
                                resolve: {
                                    calibratorEmploy: function () {
                                        return verificationServiceCalibrator.getCalibrators()
                                            .success(function (calibrators) {
                                                    return calibrators;
                                                }
                                            );
                                    }
                                }
                            });
                            /**
                             * executes when modal closing
                             */
                            modalInstance.result.then(function (formData) {
                                var verificationIds = [];
                                angular.forEach($scope.allVerifications, function (verification) {
                                    if (verification.calibratorEmployee === null) {
                                        verificationIds.push(verification.id);
                                    }
                                });
                                var dataToSend = {
                                    idsOfVerifications: verificationIds,
                                    employeeCalibrator: formData.provider
                                };

                                verificationServiceCalibrator
                                    .sendEmployeeCalibrator(dataToSend)
                                    .success(function () {
                                        $scope.tableParams.reload();
                                    });
                            });
                        } else {
                            var verificationIds = [];
                            angular.forEach($scope.allVerifications, function (verification) {
                                if (verification.calibratorEmployee === null) {
                                    verificationIds.push(verification.id);
                                }
                            });
                            var dataToSend = {
                                idsOfVerifications: verificationIds
                            };

                            verificationServiceCalibrator
                                .sendEmployeeCalibrator(dataToSend)
                                .success(function () {
                                    $scope.tableParams.reload();
                                });
                        }
                    });
            };

            $scope.cancelPersonForAll = function () {
                var idsOfVerifications = [];
                angular.forEach($scope.allVerifications, function (verification) {
                    if (verification.calibratorEmployee != null) {
                        idsOfVerifications.push(verification.id);
                    }
                });
                var dataToSend = {
                    idsOfVerifications: idsOfVerifications
                };
                verificationServiceCalibrator.cleanCalibratorEmployeeFieldForAll(dataToSend)
                    .success(function () {
                        $scope.tableParams.reload();
                    });
            }

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