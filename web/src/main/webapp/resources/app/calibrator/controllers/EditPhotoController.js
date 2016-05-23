angular
    .module('employeeModule')
    .controller('EditPhotoController', ['$scope', '$rootScope', '$route', '$log', '$modalInstance',
        '$timeout', 'photoId', 'testNumber', 'CalibrationTestServiceCalibrator', 'parentScope', '$translate', 'DataReceivingServiceCalibrator',
        function ($scope, $rootScope, $route, $log, $modalInstance, $timeout, photoId, testNumber, calibrationTestServiceCalibrator,
                  parentScope, $translate, dataReceivingService) {

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $scope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.cancel = function () {
                $modalInstance.close("cancel");
            };

            $scope.ACCURACY_OF_CALCULATION = 2;

            $scope.photoId = photoId;
            $scope.testNumber = testNumber;
            $scope.parentScope = parentScope;

            $scope.newValues = {};
            $scope.newValues.counterNumber = null;
            $scope.newValues.counterYear = null;
            $scope.newValues.accumulatedVolume = null;
            $scope.newValues.counterValue = null;
            $scope.newValues.counterManufacturer = null;
            $scope.newValues.counterSymbol = null;
            $scope.standardSizes = [];
            $scope.symbols = [];
            $scope.manufacturers = [];
            $scope.photoType = null;
            $scope.photoIndex = null;
            $scope.imageSize = {};
            $scope.imageSize.small = true;

            $scope.isChanged = false;

            $scope.nextPhotoOnSpaceBarPress = function ($event) {
                $scope.saveTestPhoto();
                var SPACE_BAR_CODE = 32;
                if ($event.keyCode === SPACE_BAR_CODE || $event.charCode === SPACE_BAR_CODE) {
                    var idSplit = $scope.photoId.split("Photo");
                    var photoType = idSplit[0];
                    var photoIndex = parseInt(idSplit[1]);
                    if (photoType === 'begin') {
                        photoType = 'end';
                    } else {
                        if (photoType == 'end') {
                            photoType = 'begin';
                            photoIndex = photoIndex + 1;
                            if (photoIndex == 3) {
                                photoIndex = 0;
                            }
                            $scope.testNumber = 'Test' + (photoIndex + 1);
                        }
                    }
                    $scope.photoType = photoType;
                    $scope.photoIndex = photoIndex;
                    $scope.photoId = photoType + 'Photo' + photoIndex;
                    $scope.photo = document.getElementById($scope.photoId).src;
                    $scope.initialize();
                }
            };

            $scope.changed = function () {
                $scope.isChanged = true;
            };

            $scope.isTestSuccess = function (tests) {
                for (var i = 0; i < tests.length; i++) {
                    if (tests[i].testResult != 'SUCCESS') {
                        return false;
                    }
                }
                return true;
            };

            $scope.zoom = function () {
                $(".counter-test-value").focus();
                if ($scope.imageSize.small) {
                    if (photoId == "testMainPhoto") {
                        $(".modal-dialog").css({"width": "100%", "max-width": "1366px"});
                    } else {
                        $(".modal-dialog").css({"width": "1000px"});
                    }
                    $scope.imageSize.small = false;
                } else {
                    $(".modal-dialog").css({"width": "900px"});
                    $scope.imageSize.small = true;
                }
            };

            function equals(a, b, accurancy) {
                return Math.abs(a - b) < accurancy;
            }

            $scope.updateValues = function (index) {
                var test = parentScope.TestDataFormData[index];
                if (test.endValue == 0 || test.initialValue > test.endValue || test.initialValue == 0) {
                    test.testResult = 'NOT_PROCESSED';
                } else if (test.initialValue == test.endValue) {
                    test.testResult = 'FAILED';
                } else {
                    var calcError = Math.abs($scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard));
                    if (equals(test.acceptableError, calcError, Math.pow(0.1, $scope.ACCURACY_OF_CALCULATION)) || test.acceptableError >= calcError) {
                        test.testResult = 'SUCCESS';
                    } else {
                        test.testResult = 'FAILED';
                    }
                }

                parentScope.TestForm.testResult = $scope.isTestSuccess(parentScope.TestDataFormData) ? 'SUCCESS' : 'FAILED';

                test.calculationError = (parseFloat($scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard))).toFixed($scope.ACCURACY_OF_CALCULATION);
                test.volumeInDevice = parseFloat((test.endValue - test.initialValue).toFixed(2));
                parentScope.TestDataFormData[index] = test;
            };

            $scope.calcError = function (initialValue, endValue, volumeOfStandard) {
                var value = parseFloat(((endValue - initialValue - volumeOfStandard) * 100 / (volumeOfStandard))).toFixed(5);
                return value;
            };

            $scope.selectedTypeWater = {
                type: null
            };

            $scope.statusTypeWater = [
                {type: 'WATER'},
                {type: 'THERMAL'}
            ];

            $scope.setStatusTypeWater = function (typeWater) {
                $scope.selectedTypeWater.type = typeWater;
            };

            /**
             * find counterType by id in list of countersTypes
             */
            function findCounterTypeById(id, list) {
                var counterType = null;
                for (var i = 0; i < list.length; i++) {
                    counterType = list[i];
                    if (counterType.id == id) {
                        break;
                    }
                }
                return counterType;
            }

            /**
             * get all counterType and set current manufacturer name
             */
            function getCurrentCounterManufacturer() {
                calibrationTestServiceCalibrator.getCountersTypes()
                    .then(function (result) {
                        $scope.countersTypesAll = result.data;
                        $scope.newValues.counterManufacturer = findCounterTypeById(parentScope.TestForm.counterTypeId, $scope.countersTypesAll);
                    })
            }

            /**
             * get all standardSizes from counter_type table and set current from bbi
             */
            function getAllStandardSizes() {
                dataReceivingService.findAllStandardSize()
                    .success(function (standardSize) {
                        $scope.standardSizes = standardSize;
                        $scope.newValues.counterStandardSize = parentScope.TestForm.standardSize;
                    })
            }

            /**
             * get all symbols from counter_type by standardSize and deviceType
             * @param standardSize - from counter_type table
             * @param deviceType - from counter_type table
             */
            $scope.getAllSymbolsByStandardSizeAndDeviceType = function (standardSize, deviceType) {
                dataReceivingService.findAllSymbolsByStandardSizeAndDeviceType(standardSize, deviceType)
                    .success(function (symbols) {
                        $scope.symbols = symbols;
                        $scope.eraseCurrentSymbolAndManufacturer();
                    });
            };

            /**
             * get all manufacturer from counter_type by standardSize,deviceType and symbol
             * @param standardSize
             * @param deviceType
             * @param symbol
             */
            $scope.getAllManufacturerByStandardSizeAndDeviceTypeAndSymbol = function (standardSize, deviceType, symbol) {
                calibrationTestServiceCalibrator.getAllCounterTypesByStandardSizeAndDeviceTypeAndSymbol(standardSize, deviceType, symbol)
                    .then(function (result) {
                        $scope.manufacturers = result.data;
                        $scope.newValues.counterManufacturer = undefined;
                    })
            };

            /**
             * set  typeWater
             */
            $scope.changeTypeWater = function (typeWater) {
                $scope.setStatusTypeWater(typeWater.type);
            };

            /**
             * erase current counterType and manufacturer
             */
            $scope.eraseCurrentSymbolAndManufacturer = function () {
                $scope.newValues.counterSymbol = undefined;
                $scope.newValues.counterManufacturer = undefined;
            };

            $scope.initialize = function () {
                $scope.newValues.counterValue = $scope.photoType == 'begin'
                    ? parentScope.TestDataFormData[$scope.photoIndex].initialValue
                    : parentScope.TestDataFormData[$scope.photoIndex].endValue;
            };

            if (photoId == "testMainPhoto") {
                $scope.newValues.counterNumber = parentScope.TestForm.counterNumber;
                $scope.newValues.counterYear = parentScope.TestForm.counterProductionYear;
                $scope.newValues.accumulatedVolume = parentScope.TestForm.accumulatedVolume;
                $scope.newValues.counterSymbol = parentScope.TestForm.symbol;
                $scope.setStatusTypeWater(parentScope.TestForm.typeWater);
                getCurrentCounterManufacturer();
                getAllStandardSizes();
            } else {
                var idSplit = photoId.split("Photo");
                $scope.photoType = idSplit[0];
                $scope.photoIndex = parseInt(idSplit[1]);
                $scope.initialize();
            }
            $scope.rotateIndex = parentScope.rotateIndex;

            $scope.photo = document.getElementById(photoId).src;

            $scope.saveOnExit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.clientForm.$valid) {
                    if (photoId == "testMainPhoto") {
                        parentScope.TestForm.counterNumber = $scope.newValues.counterNumber;
                        parentScope.TestForm.accumulatedVolume = $scope.newValues.accumulatedVolume;
                        parentScope.TestForm.counterProductionYear = $scope.newValues.counterYear;
                        parentScope.TestForm.typeWater = $scope.newValues.counterManufacturer.typeWater;
                        parentScope.TestForm.standardSize = $scope.newValues.counterManufacturer.standardSize;
                        parentScope.TestForm.symbol = $scope.newValues.counterManufacturer.symbol;
                        if (parentScope.TestForm.counterTypeId != $scope.newValues.counterManufacturer.id) {
                            parentScope.TestForm.counterTypeId = $scope.newValues.counterManufacturer.id;
                            parentScope.selectedReason.selected = undefined;
                            parentScope.isReasonsUnsuitabilityShown();
                        }
                    } else {
                        $scope.saveTestPhoto();
                    }
                    $modalInstance.close("saved");
                }
            };

            $scope.saveTestPhoto = function () {
                if ($scope.photoType == 'begin') {
                    parentScope.TestDataFormData[$scope.photoIndex].initialValue = $scope.newValues.counterValue;
                    $scope.updateValues($scope.photoIndex);
                } else {
                    parentScope.TestDataFormData[$scope.photoIndex].endValue = $scope.newValues.counterValue;
                    $scope.updateValues($scope.photoIndex);
                }
                $scope.isChanged = false;
                if (parentScope.showReasons) {
                    parentScope.showReasons = parentScope.isTestRaw();
                } else {
                    parentScope.selectedReason.selected = undefined;
                    parentScope.isReasonsUnsuitabilityShown();
                }
            };

            $scope.checkAll = function (caseForValidation) {
                switch (caseForValidation) {
                    case ('accumulatedVolume'):
                        var accumulatedVolume = $scope.newValues.accumulatedVolume;
                        var matches = /^[0-9]*$/.test(accumulatedVolume);
                        if (matches) {
                            if (/^\d{1,10}$/.test(accumulatedVolume)) {
                                validator('accumulatedVolume', false);
                            } else {
                                validator('accumulatedVolume', true);
                            }
                        } else {
                            validator('accumulatedVolume', true);
                        }

                        break;
                    case 'counterValue':
                        var counterValue = $scope.newValues.counterValue;
                        if (/^[0-9]*$/.test(counterValue)) {
                            validator(false);
                        } else {
                            validator(true);
                        }
                        break;
                }
            };

            function validator(caseForValidation, isValid) {
                switch (caseForValidation) {
                    case ('accumulatedVolume'):
                        $scope.accumulatedVolume = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case ('counterValue'):
                        $scope.timeValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                }
            }
        }
    ]);

