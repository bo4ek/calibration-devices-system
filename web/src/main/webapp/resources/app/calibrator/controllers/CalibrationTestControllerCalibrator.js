angular
    .module('employeeModule')
    .controller('CalibrationTestControllerCalibrator', ['$rootScope', '$scope', '$modal', '$http', '$log',
        'CalibrationTestServiceCalibrator', '$location', 'Upload', '$timeout','ngTableParams', '$translate','VerificationServiceCalibrator', '$sce', '$filter', 'toaster',
        function ($rootScope, $scope, $modal, $http, $log, calibrationTestServiceCalibrator, $location, Upload, $timeout, ngTableParams, $translate, verificationServiceCalibrator, $sce, $filter, toaster) {

            $scope.resultsCount = 0;

            /**
             *  get data of selected verification for
             *  created manual test
             */
            $scope.IdsOfVerifications = calibrationTestServiceCalibrator.dataOfVerifications().getIdsOfVerifications();

            $scope.testId = $location.search().param;

            $scope.isSavedScanDoc = false;

            $scope.setFirstManufacturerNumber = true;

            $scope.idOfManualTest = 0;

            $scope.isVerificationEdit = $location.search().editVer || false;

            /**
             *  disable use upload single bbi but this functionality can
             *  be necessary in the future
             */
            $scope.disableUseUploadSingleBBI=true;

            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.myDatePicker.pickerDate = {
                startDate: moment(),
                endDate: moment() // current day
            };

            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 50,
                sorting: {
                    date: 'desc'
                }
            }, {
                total: 0,
                counts :[],
                getData: function ($defer, params) {
                            //$defer.resolve($scope.IdsOfVerifications);
                    var arr = [{
                        ids:1,
                        name:"my"
                    },{
                        ids:2,
                        name:"my"
                    }];
                    $defer.resolve(arr);
                    params.total(arr);
                    $scope.dati = arr;
                }
            });

            $scope.selectedData = {
                numberProtocol : null
            };


            /**
             *  create entity for send to backend
             */
            function retranslater(){
                $scope.selectedData.dateOfManualTest = new Date($scope.myDatePicker.pickerDate.startDate);
                $scope.selectedData.dateOfManualTest.setHours($scope.selectedData.timeFrom.getHours());
                $scope.selectedData.dateOfManualTest.setMinutes($scope.selectedData.timeFrom.getMinutes());
                testManualForSend = {
                    serialNumber: $scope.selectedData.manufacturerNumber.serialNumber,
                    numberOfTest: $scope.selectedData.numberProtocolManual,
                    listOfCalibrationTestDataManual: $scope.dataOfManualTests,
                    dateOfTest: $scope.convertDateToLong($scope.selectedData.dateOfManualTest),
                    pathToScanDoc: $scope.pathToScanDoc,
                    moduleId: $scope.selectedData.manufacturerNumber.moduleId
                }
            }


            /**
             *  creat and update manual test
             */
            $scope.createAndUpdateTest = function () {
                $scope.$broadcast('show-errors-check-validity');
                if($scope.clientForm.$valid) {
                    retranslater();
                    if (!$scope.selectedData.numberProtocol) {
                        calibrationTestServiceCalibrator.createTestManual(testManualForSend)
                            .then(function (status) {
                                if (status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if (status == 200) {
                                    toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_ADDED_TEST'));
                                }
                            })
                    } else {
                        calibrationTestServiceCalibrator.editTestManual(testManualForSend, $scope.testId ,$scope.isVerificationEdit)
                            .then(function (status) {
                                if (status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if (status == 200) {
                                    toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_EDITED'));
                                }
                            })
                    }
                }
            };


            /**
             *  review scan doc of manual test
             */
            $scope.review = function () {
                calibrationTestServiceCalibrator.getScanDoc($scope.pathToScanDoc)
                    .then(function (result) {
                        var file = new Blob([result.data], {type: 'application/pdf'});
                        $scope.fileURL = window.URL.createObjectURL(file);
                        $scope.dataScanDoc = $sce.trustAsResourceUrl($scope.fileURL);
                        if (result.status == 201) {
                            $rootScope.onTableHandling();
                        }
                        if (result.status == 200) {
                            $modal.open({
                                animation: true,
                                templateUrl: 'resources/app/calibrator/views/modals/reviewScanDoc.html',
                                controller: 'ReviewScanDocController',
                                size: 'lg',
                                resolve: {
                                    parentScope: function () {
                                        return $scope;
                                    }
                                }
                            })
                        }
                        //URL.revokeObjectURL($scope.fileURL)
                    })
            };


            $scope.testManual = {};
            $scope.symbols = [];
            $scope.moduleTypes = [];
            $scope.manufacturerNumbers = [];
            $scope.dataOfManualTests = [];
            $scope.selectedData.standardSize = null;
            $scope.selectedData.testFirst = [];
            $scope.selectedData.testSecond = [];
            $scope.selectedData.testThird = [];
            $scope.unsuitabilityReasons = [];
            $scope.verification = null;
            $scope.selectedData.numberProtocol=null;
            $scope.isUploadScanDoc = false;
            $scope.block = true;
            $scope.selectedData.timeFrom = new Date();
            $scope.pathToScanDoc = null;
            $scope.IsScanDoc = false;
            $scope.selectedData.numberProtocolManual=null;

            /**
             *  receive data of all calibration modules
             */
            function receiveAllModule() {
                calibrationTestServiceCalibrator.getAllModule()
                    .then(function (result) {
                        $scope.calibrationModelDATA = result.data;
                        $scope.selectedData.condDesignation = null;
                        $scope.selectedData.moduleType = null;
                        $scope.selectedData.manufacturerNumber = null;
                        $log.debug("inside");
                        $scope.receiveAllOriginalCondDesignation($scope.calibrationModelDATA);
                        $scope.receiveAllOriginalModuleType($scope.calibrationModelDATA);
                        $scope.receiveAllManufacturerNumbers($scope.calibrationModelDATA);
                        receiveAllVerificationForManualTest($scope.IdsOfVerifications);
                        $scope.selectedData.standardSize = $scope.dataOfManualTests[0].standardSize;
                    });
            }

            receiveAllModule();

            /**
             *  receive data of manual completed test for edit
             */
            function receiveDataForCompletedTest(map) {
                    calibrationTestServiceCalibrator.getDataForCompletedTest($scope.testId)
                        .then(function (result) {
                            var dataCompletedTest = result.data;
                            var dataOfCounter = map.get($scope.testId);
                            var testManual = {
                                verificationId: $scope.testId,
                                standardSize: dataOfCounter.standardSize,
                                symbol: dataOfCounter.symbol,
                                realiseYear: dataOfCounter.realiseYear,
                                numberCounter: dataOfCounter.numberCounter,
                                statusTestFirst: dataCompletedTest.statusTestFirst,
                                statusTestSecond: dataCompletedTest.statusTestSecond,
                                statusTestThird: dataCompletedTest.statusTestThird,
                                statusCommon: dataCompletedTest.statusCommon,
                                status: ['SUCCESS', 'FAILED', 'RAW'],
                                unsuitabilityReason : dataCompletedTest.unsuitabilityReason
                            };
                            $scope.setDataUseManufacturerNumber(findcalibrationModuleBySerialNumber(dataCompletedTest.calibrationTestManualDTO.serialNumber));
                            $scope.selectedData.numberProtocolManual = dataCompletedTest.calibrationTestManualDTO.numberOfTest;
                            $scope.selectedData.numberProtocol = dataCompletedTest.calibrationTestManualDTO.generateNumber;
                            $scope.selectedData.dateOfManualTest = new Date(dataCompletedTest.calibrationTestManualDTO.dateOfTest);
                            $scope.idOfManualTest = dataCompletedTest.calibrationTestManualDTO.id;
                            $scope.myDatePicker.pickerDate = {
                                startDate: (new Date(dataCompletedTest.calibrationTestManualDTO.dateOfTest)),
                                endDate: (new Date(dataCompletedTest.calibrationTestManualDTO.dateOfTest))
                            };

                            $scope.selectedData.timeFrom = $scope.selectedData.dateOfManualTest;
                            $scope.dataOfManualTests.push(testManual);
                            $scope.selectedData.standardSize = $scope.dataOfManualTests[0].standardSize;
                            $scope.pathToScanDoc = dataCompletedTest.calibrationTestManualDTO.pathToScanDoc;
                            $scope.checkIsScanDoc();
                        });

            }

            function findcalibrationModuleBySerialNumber(snumber) {
                var calibrationModel;
                for (var x = 0; x < $scope.calibrationModelDATA.length; x++) {
                    calibrationModel = $scope.calibrationModelDATA[x];
                    if (calibrationModel.serialNumber == snumber) {
                        break;
                    }
                }
                return calibrationModel;
            }

            /**
             *  receive data of verifications for review
             */
            function receiveAllVerificationForManualTest(map) {
                map.forEach(function (value, key) {
                    if (value.status == 'TEST_COMPLETED' || value.status == 'SENT_TO_VERIFICATOR' || value.status == 'TEST_NOK' || value.status == 'TEST_OK') {
                        receiveDataForCompletedTest(map);
                    } else {
                        $scope.dataOfManualTests.push(creatorTestManual(value, key));
                    }
                }, map);
            }

            /**
             * entity of manual test
             */
            function creatorTestManual(value, key) {
                var testManual = {
                    verificationId: key,
                    standardSize: value.standardSize,
                    symbol: value.symbol,
                    realiseYear: value.realiseYear,
                    numberCounter: value.numberCounter,
                    statusTestFirst: 'SUCCESS',
                    statusTestSecond: 'SUCCESS',
                    statusTestThird: 'SUCCESS',
                    statusCommon: 'SUCCESS',
                    status: ['SUCCESS', 'FAILED', 'RAW'],
                    counterId: value.counterId,
                    unsuitabilityReason : null
                };
                return testManual
            }


            /**
             * receive directory of all  manufacturer numbers
             */
            $scope.receiveAllManufacturerNumbers = function (data) {
                var model = null;
                for (var i = 0; i < data.length; i++) {
                    model = data[i];
                    $scope.manufacturerNumbers.push(model);
                }
            };

            /**
             * receive directory of all  original condDesignation
             */
            $scope.receiveAllOriginalCondDesignation = function (data) {
                var maoOfCondDesignation = new Map();
                var symbol = null;
                for (var i = 0; i < data.length; i++) {
                    symbol = data[i];
                    maoOfCondDesignation.set(symbol.condDesignation, symbol);
                }
                maoOfCondDesignation.forEach(function (value, key) {
                    $scope.symbols.push(value);
                }, maoOfCondDesignation)
            };

            /**
             * receive directory of all  original moduleType
             */
            $scope.receiveAllOriginalModuleType = function (data) {
                var mapOfmoduleType = new Map();
                var type = null;
                for (var i = 0; i < data.length; i++) {
                    type = data[i];
                    mapOfmoduleType.set(type.moduleType, type);
                }
                mapOfmoduleType.forEach(function (value, key) {
                    $scope.moduleTypes.push(value);
                }, mapOfmoduleType)
            };

            /**
             * set data for drop-down use selected condDesignation
             */
            $scope.setDataUseCondDesignation = function (currentClibrationModel) {
                if (currentClibrationModel) {
                    $scope.clearManufacturerNumbers();
                    $scope.setFirstManufacturerNumber = false;
                    $scope.moduleTypes = [];
                    var map = new Map();
                    var model = null;
                    for (var i = 0; i < $scope.calibrationModelDATA.length; i++) {
                        model = $scope.calibrationModelDATA[i];
                        if (model.condDesignation == currentClibrationModel.condDesignation) {
                            map.set(model.moduleType, model);
                            if ($scope.selectedData.moduleType != null && $scope.selectedData.moduleType.moduleType == model.moduleType) {
                                $scope.manufacturerNumbers.push(model);
                            } else if ($scope.selectedData.moduleType == null) {
                                $scope.manufacturerNumbers.push(model);
                            }
                        }
                    }
                    map.forEach(function (value, key) {
                        $scope.moduleTypes.push(value);
                    }, map)
                } else if (currentClibrationModel == undefined && $scope.selectedData.moduleType != null) {
                    $scope.clearAllArrays();
                    $scope.setDataUseModuleType($scope.selectedData.moduleType);
                    $scope.receiveAllOriginalModuleType($scope.calibrationModelDATA);
                } else {
                    $scope.clearAllArrays();
                    $scope.receiveAllOriginalCondDesignation($scope.calibrationModelDATA);
                    $scope.receiveAllOriginalModuleType($scope.calibrationModelDATA);
                    $scope.receiveAllManufacturerNumbers($scope.calibrationModelDATA);
                }
            };

            /**
             * set data for drop-down use selected moduleType
             */
            $scope.setDataUseModuleType = function (currentClibrationModel) {
                if (currentClibrationModel != undefined) {
                    $scope.clearManufacturerNumbers();
                    $scope.setFirstManufacturerNumber = false;
                    $scope.symbols = [];
                    var map = new Map();
                    var model = null;
                    for (var i = 0; i < $scope.calibrationModelDATA.length; i++) {
                        model = $scope.calibrationModelDATA[i];
                        if (model.moduleType == currentClibrationModel.moduleType) {
                            map.set(model.condDesignation, model);
                            if ($scope.selectedData.condDesignation != null && $scope.selectedData.condDesignation.condDesignation == model.condDesignation) {
                                $scope.manufacturerNumbers.push(model);
                            } else if ($scope.selectedData.condDesignation == null) {
                                $scope.manufacturerNumbers.push(model);
                            }
                        }
                    }
                    map.forEach(function (value, key) {
                        $scope.symbols.push(value);
                    }, map)
                } else if (currentClibrationModel == undefined && $scope.selectedData.condDesignation != null) {
                    $scope.clearAllArrays();
                    $scope.setDataUseCondDesignation($scope.selectedData.condDesignation);
                    $scope.receiveAllOriginalCondDesignation($scope.calibrationModelDATA);
                } else {
                    $scope.clearAllArrays();
                    $scope.receiveAllOriginalCondDesignation($scope.calibrationModelDATA);
                    $scope.receiveAllOriginalModuleType($scope.calibrationModelDATA);
                    $scope.receiveAllManufacturerNumbers($scope.calibrationModelDATA);
                }
            };

            /**
             * set data for drop-down use selected manufacturerNumber
             */
            $scope.setDataUseManufacturerNumber = function (currentClibrationModel) {
                if (currentClibrationModel) {
                    $scope.selectedData.manufacturerNumber = currentClibrationModel;
                    $scope.selectedData.condDesignation = currentClibrationModel;
                    $scope.selectedData.moduleType = currentClibrationModel;
                } else if (!currentClibrationModel && $scope.setFirstManufacturerNumber) {
                    $scope.selectedData.condDesignation = null;
                    $scope.selectedData.moduleType = null;
                    $scope.clearAllArrays();
                    $scope.receiveAllOriginalCondDesignation($scope.calibrationModelDATA);
                    $scope.receiveAllOriginalModuleType($scope.calibrationModelDATA);
                    $scope.receiveAllManufacturerNumbers($scope.calibrationModelDATA);
                }
            };

            /**
             * get data of unsuitabilityReasons for drop-down
             */
            function getAllUnsuitabilityReasons() {
                calibrationTestServiceCalibrator.getAllUnsuitabilityReasons()
                    .then(function (reasons) {
                        $scope.unsuitabilityReasons = reasons.data;
                    })
            }

            $scope.clearAllArrays = function () {
                $scope.symbols = [];
                $scope.moduleTypes = [];
                $scope.manufacturerNumbers = [];
            };

            $scope.clearManufacturerNumbers = function () {
                $scope.selectedData.manufacturerNumber = null;
                $scope.manufacturerNumbers = [];
            };


            /**
             * one of tests is changing status then change status common of test
             */
            $scope.changeStatus = function (verification) {
                if (verification.statusTestFirst == 'RAW' || verification.statusTestSecond == 'RAW' || verification.statusTestThird == 'RAW') {
                    verification.statusCommon = 'FAILED';
                    getAllUnsuitabilityReasons();
                } else if (verification.statusTestFirst == 'FAILED' || verification.statusTestSecond == 'FAILED' || verification.statusTestThird == 'FAILED') {
                    verification.statusCommon = 'FAILED';
                    verification.unsuitabilityReason = null;
                    $scope.unsuitabilityReasons = [];
                } else if (verification.statusTestFirst == 'SUCCESS' || verification.statusTestSecond == 'SUCCESS' || verification.statusTestThird == 'SUCCESS') {
                    verification.statusCommon = 'SUCCESS';
                    verification.unsuitabilityReason = null;
                    $scope.unsuitabilityReasons = [];
                }
            };


            /**
             * setup date Datepicker
             */

            $scope.selectedData.dateOfManualTest = new Date();
            $scope.defaultDate = null;

            $scope.initDatePicker = function () {

                if ($scope.defaultDate == null) {
                    //copy of original daterange
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

                $scope.setTypeDataLangDatePicker = function () {

                    $scope.opts = {
                        format: 'DD-MM-YYYY',
                        singleDatePicker: true,
                        showDropdowns: true,
                        eventHandlers: {}
                    };

                };

                $scope.setTypeDataLangDatePicker();
            };

            $scope.showPicker = function () {
                angular.element("#datepickerfieldSingle").trigger("click");
            };

            $scope.initDatePicker();

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                } else {
                    moment.locale('en'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                }
            };

            $scope.setTypeDataLanguage();


            $scope.formats = ['dd-MMMM-yyyy', 'yyyy-MM-dd', 'dd.MM.yyyy', 'shortDate'];
            $scope.format = $scope.formats[0];

            $scope.clearDate = function () {
                $scope.selectedData.dateOfManualTest= null;
            };

            $scope.disabled = function (date, mode) {
                return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
            };

            $scope.toggleMin = function () {
                $scope.min = $scope.minDate ? null : new Date();
            };
            $scope.toggleMin();
            $scope.max = new Date(2100, 5, 22);

            $scope.dateOptions = {
                formatYear: 'yyyy',
                startingDay: 1,
                showWeeks: 'false'
            };

            $scope.convertDateToLong = function(date) {
                return (new Date(date)).getTime();
            };


            /**
             *  upload scan document of manual Test
             */
            $scope.uploadScanDoc = function(){
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/upload-scanDoc.html',
                    controller: 'UploadScanDocController',
                    size: 'lg',
                    resolve: {
                        parentScope: function () {
                            return $scope;
                        },
                        checkIsScanDoc: function () {
                            return $scope.checkIsScanDoc;
                        }
                    }
                });
            };

            /**
             *  delete scan document of manual Test
             */
            function deleteScanDoc(cb) {
                $scope.resultDelete = false;
                $scope.dataScanDoc = null;
                window.URL.revokeObjectURL($scope.fileURL);
                calibrationTestServiceCalibrator.deleteScanDoc($scope.pathToScanDoc, $scope.idOfManualTest)
                    .then(function (data) {
                        if (data.status == 201) {
                            $rootScope.onTableHandling();
                            $scope.resultDelete = false;
                        }
                        if (data.status == 200) {
                            $scope.resultDelete = true;
                            $scope.pathToScanDoc = null;
                            $scope.checkIsScanDoc();
                        }
                        if (cb) {
                            cb();
                        }
                    });
            }

            /**
             *  repeat scan document of manual Test
             */
            $scope.repeatUpload = function () {
                deleteScanDoc(function(){
                    if ($scope.resultDelete) {
                        $scope.uploadScanDoc();
                    }
                });
            };




            /**
             *  for show icon
             */
            $scope.checkIsScanDoc = function () {
                if ($scope.pathToScanDoc) {
                    $scope.IsScanDoc = true;
                } else {
                    $scope.IsScanDoc = false;
                }
                return
            };

            $scope.closeTestManual = function () {
                if (!$scope.selectedData.numberProtocol && $scope.pathToScanDoc) {
                    deleteScanDoc();
                    window.history.back();
                } else {
                    window.history.back();
                }

            };

            /**
             * close modal use time
             */
            function closeTime($modalInstance) {
                $timeout(function () {
                    $modalInstance.close();
                    window.history.back();
                }, 2500);
            }



            $scope.openDetails = function (verifId, verifDate) {

                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/provider/views/modals/archival-verification-details.html',
                    controller: 'ArchivalDetailsModalController',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getArchivalVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
                                    return verification;
                                });
                        }
                    }
                });
            };


            $scope.openAddTest = function () {
                if ($scope.pathToScanDoc != null && $scope.isSavedScanDoc) {
                    //deleteScanDoc();
                }
                calibrationTestServiceCalibrator
                    .getEmptyTest($scope.testId)
                    .then(function (data) {
                        $log.debug("inside");
                        var testId = data.id;
                        var url = $location.path('calibrator/verifications/calibration-test-add/').search({'param': $scope.testId});
                    })
            };


        }]);


