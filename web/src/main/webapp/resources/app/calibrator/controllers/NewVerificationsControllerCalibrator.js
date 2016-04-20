angular
    .module('employeeModule')
    .controller('NewVerificationsControllerCalibrator', ['$scope', '$log',
        '$modal', 'VerificationServiceCalibrator',
        '$rootScope', 'ngTableParams', '$timeout', '$filter', '$window', '$location', '$translate', 'toaster',
        'CalibrationTestServiceCalibrator', 'ProfileService',
        function ($scope, $log, $modal, verificationServiceCalibrator, $rootScope, ngTableParams,
                  $timeout, $filter, $window, $location, $translate, toaster, calibrationTestServiceCalibrator, profileService ) {

            $scope.resultsCount = 0;

            $scope.searchParameters = [
                {
                    name: 'TASK_STATUS',
                    key:'taskStatus',
                    type: 'Enumerated',
                    options:['SENT',
                        'ACCEPTED',
                        'REJECTED',
                        'IN_PROGRESS',
                        'PLANNING_TASK',
                        'TASK_PLANED',
                        'TEST_PLACE_DETERMINED',
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
                    options:['READ','UNREAD']
                },
                {
                    name: 'REJECTED_MESSAGE',
                    key:'rejectedMessage',
                    type:'String'
                },
                {
                    name:'COMMENT',
                    key:'comment',
                    type:'String'
                },
                {
                    name:'PROVIDER_NAME',
                    key:'providerEmployee',
                    type:'User'
                },
                {
                    name:"INITIAL_DATE",
                    key:"initialDate",
                    type:"Date"
                },
                {
                    name:"CLIENT_FULL_NAME",
                    key:"clientData",
                    type:"clientData"
                }
            ];
            $scope.globalSearchParams=[];
            $scope.showGlobalSearch=false;


            $scope.isCalibratorEmployee = function () {
                verificationServiceCalibrator.getIfEmployeeCalibrator().success(function(data){
                    $scope.isEmployee =  data;
                });

            };

            $scope.isCalibratorEmployee();

            $scope.$watch('globalSearchParams',function(newParam,oldParam){
                if($scope.hasOwnProperty("tableParams")) {
                    $scope.tableParams.reload();
                }
            },true);
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

            $scope.statusData = [
                {id: 'IN_PROGRESS', label: null},
                {id: 'TEST_PLACE_DETERMINED', label: null},
                {id: 'SENT_TO_TEST_DEVICE', label: null},
                {id: 'TEST_COMPLETED', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'В роботі';
                    $scope.statusData[1].label = 'Визначено спосіб повірки';
                    $scope.statusData[2].label = 'Відправлено на установку';
                    $scope.statusData[3].label = 'Проведено вимірювання';
                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'In progress';
                    $scope.statusData[1].label = 'Test place determined';
                    $scope.statusData[2].label = 'Sent to test device';
                    $scope.statusData[3].label = 'Test completed';

                }
            };

            $scope.setTypeDataLanguage();

            $scope.statusDismantled = [
                {id: 'True', label: null},
                {id: 'False', label: null}
            ];

            $scope.selectedDismantled  = {
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
                    //earliest day of  all the verifications available in table
                    //we should reformat it here, because backend currently gives date in format "YYYY-MM-DD"
                    endDate: moment() // current day
                };

                if ($scope.defaultDate == null) {
                    //copy of original daterange
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }
                moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
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

                if (pickerDate == null || $scope.defaultDate == null) { //moment when page is just loaded
                    return true;
                }
                return !!(pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') //compare by day
                && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day'));

            };

            verificationServiceCalibrator.getNewVerificationEarliestDate().success(function (date) {
                //first we will try to receive date period
                // to populate ng-table filter
                // I did this to reduce reloading and flickering of the table
                $scope.initDatePicker(date);
                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 50,
                    sorting: {
                        date: 'desc'
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
                        else {
                            params.filter().status = null; //case when the filter is cleared with a button on the select
                        }
                        if ($scope.selectedDismantled.name != null) {
                            params.filter().dismantled = $scope.selectedDismantled.name.id;
                        }
                        else {
                            params.filter().dismantled = null;
                        }
                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");
                        //var globalSearchParamsString=JSON.stringify($scope.globalSearchParams)+"";
                        var searchParams={};
                        searchParams.globalSearchParams=$scope.globalSearchParams;
                        searchParams.newVerificationsFilterSearch=params.filter();
                        verificationServiceCalibrator.getNewVerifications(params.page(), params.count() ,searchParams, sortCriteria, sortOrder)
                            .success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                })});

            $scope.$on('calibrator-save-verification', function(event, args) {
                $scope.tableParams.reload();
            });

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

            $scope.openTask = function() {
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
                            moduleType: function() {
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
            $scope.checkStandardSize = function (map){
                var setOfStandardSize =  new Set();
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
                $log.debug("inside");
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
                if(!verification.isManual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                }else{
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
                    status:verification.status,
                    measurementDeviceType : verification.measurementDeviceType
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
                                //todo need to find verificators by agreements(договорах)
                                return verificationServiceCalibrator.getVerificators()
                                    .success(function (verificators) {
                                        $log.debug(verificators);
                                        return verificators;
                                    });
                            }
                        }
                    });

                    //executes when modal closing
                    modalInstance.result.then(function (verificator) {

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            organizationId: verificator.id
                        };

                        $log.debug(dataToSend);
                        $log.debug(verificator);

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
            if(!verification.isManual) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/cancel-bbiFile.html',
                    controller: 'CancelBbiProtocolCalibrator',
                    size: 'md',
                    resolve: {
                        verificationId: function () {
                            return verificationServiceCalibrator.cancelUploadFile(idVerification)
                                .success(function (bbiName) {
                                    return bbiName;
                                }
                            );
                        }
                    }
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
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
                $log.info(dataToSend);
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
                                $log.info(dataToSend);
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

            $scope.uploadArchive = function() {
                console.log("Entered upload archive function");
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

        }]);