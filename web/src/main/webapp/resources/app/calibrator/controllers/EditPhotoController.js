angular
    .module('employeeModule')
    .controller('EditPhotoController', ['$scope', '$rootScope', '$route', '$log', '$modalInstance',
        '$timeout', 'photoId', 'CalibrationTestServiceCalibrator', 'parentScope', '$translate', 'DataReceivingServiceCalibrator',
        function ($scope, $rootScope, $route, $log, $modalInstance, $timeout, photoId, calibrationTestServiceCalibrator,
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

            $scope.photoId = photoId;
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

            $scope.isChanged = false;

            $scope.changed = function () {
                $scope.isChanged = true;
            };

            $scope.updateValues = function (index) {
                var test = parentScope.TestDataFormData[index];
                if (test.endValue == 0 || test.initialValue > test.endValue || test.initialValue == 0) {
                    test.testResult = 'NOT_PROCESSED';
                    parentScope.TestForm.testResult = 'FAILED';
                    test.calculationError = 0;
                    test.volumeInDevice = 0;
                } else if (test.initialValue == test.endValue) {
                    test.testResult = 'FAILED';
                    parentScope.TestForm.testResult = 'FAILED';
                    test.calculationError = 0;
                    test.volumeInDevice = 0;
                } else if (test.acceptableError >= Math.abs($scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard))) {
                    test.testResult = 'SUCCESS';
                    parentScope.TestForm.testResult = 'SUCCESS';
                    test.calculationError = $scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard);
                    test.volumeInDevice = parseFloat((test.endValue - test.initialValue).toFixed(2));
                } else {
                    test.testResult = 'FAILED';
                    parentScope.TestForm.testResult = 'FAILED';
                    test.calculationError = $scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard);
                    test.volumeInDevice = parseFloat((test.endValue - test.initialValue).toFixed(2));
                }
                parentScope.TestDataFormData[index] = test;
            };

            $scope.calcError = function (initialValue, endValue, volumeOfStandard) {
                return parseFloat(((endValue - initialValue - volumeOfStandard) / (volumeOfStandard * 100)).toFixed(1));
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
             * set  typeWater and counterType
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
                $scope.newValues.counterValue = $scope.photoType == 'begin'
                    ? parentScope.TestDataFormData[$scope.photoIndex].initialValue
                    : parentScope.TestDataFormData[$scope.photoIndex].endValue;
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
                    }
                    $modalInstance.close("saved");
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

