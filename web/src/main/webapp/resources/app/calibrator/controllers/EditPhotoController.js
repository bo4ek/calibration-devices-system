angular
    .module('employeeModule')

    .controller('EditPhotoController', ['$scope', '$rootScope', '$route', '$log', '$modalInstance',
        '$timeout', 'photoId', 'CalibrationTestServiceCalibrator', 'parentScope', '$translate', 'DataReceivingServiceCalibrator',
        function ($scope, $rootScope, $route, $log, $modalInstance, $timeout, photoId, calibrationTestServiceCalibrator, parentScope, $translate, dataReceivingService) {

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
            $scope.newValues.counterStandard = null;
            $scope.newValues.counterManufacturer = null;
            $scope.newValues.counterType = null;
            $scope.countersTypes=[];
            $scope.photoType = null;
            $scope.photoIndex = null;

            $scope.isChanged = false;

            $scope.changed = function () {
                $scope.isChanged = true;
            };

            $scope.updateValues = function (index) {
                var test = parentScope.TestDataFormData[index];
                if (test.endValue == 0 || test.initialValue > test.endValue || test.initialValue == 0) {
                    test.testResult = 'RAW';
                    parentScope.TestForm.testResult = 'RAW';
                    test.calculationError = null;
                } else if (test.initialValue == test.endValue) {
                    test.testResult = 'FAILED';
                    parentScope.TestForm.testResult = 'FAILED';
                    test.calculationError = null;
                } else if (test.acceptableError >= Math.abs($scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard))) {
                    test.testResult = 'SUCCESS';
                    parentScope.TestForm.testResult = 'SUCCESS';
                    test.calculationError = $scope.calcError(test.initialValue, test.endValue, test.volumeOfStandard);
                } else {
                    test.testResult = 'FAILED';
                    parentScope.TestForm.testResult = 'FAILED';
                    test.calculationError = null;
                }
                parentScope.TestDataFormData[index] = test;

            };


            $scope.calcError = function (initialValue, endValue, volumeOfStandard) {
                return parseFloat(((endValue - initialValue - volumeOfStandard) / (volumeOfStandard ) * 100).toFixed(1));
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
             * receive all counters types for selection
             */
            function getAllCountersTypes() {
                calibrationTestServiceCalibrator.getCountersTypes()
                    .then(function (result) {
                        $scope.countersTypes = result.data;
                        var currentCounterType = findCounterTypeById(parentScope.TestForm.counterTypeId, $scope.countersTypes);
                        $scope.newValues.counterStandard = currentCounterType;

                    })
            }

            getAllCountersTypes();


            $scope.receiveAllSymbols = function(standardSize, deviceType) {
                $scope.symbols = [];
                dataReceivingService.findAllSymbolsByStandardSizeAndDeviceType(standardSize, deviceType)
                    .success(function(symbols) {
                        $scope.symbols = symbols;
                        $scope.newValues.counterType = undefined;
                        $scope.newValues.counterManufacturer = undefined;
                    });
            };

            $scope.receiveAllManufacturers = function(symbol) {
                $scope.manufactures = [];
                dataReceivingService.findAllManufactures(symbol)
                    .success(function(manufactures) {
                        $scope.manufactures = manufactures;
                        $scope.newValues.counterManufacturer = undefined;
                    });
            };


            /**
             * change typeWater when changed counterType
             */
            $scope.changeType = function (counterType) {
                if (counterType == undefined) {
                    resetCountersTypes();
                } else {
                    $scope.setStatusTypeWater(counterType.typeWater)
                }
            };

            /**
             * set  typeWater and counterType
             */
            $scope.changeTypeWater = function (typeWater) {
                $scope.setStatusTypeWater(typeWater.type);
                $scope.eraseCounterSymbol();
                $scope.eraseCounterManufacturer();
            };

            $scope.eraseCounterSymbol = function () {
                $scope.newValues.counterType = null;
            };

            $scope.eraseCounterManufacturer = function () {
                $scope.newValues.counterManufacturer = null;
            };

            /**
             * reset countersTypes to get all countersTypes
             */
            function resetCountersTypes() {
                $scope.countersTypes = $scope.countersTypesAllData;
            }


                if (photoId == "testMainPhoto") {
                    $scope.newValues.counterNumber = parentScope.TestForm.counterNumber;
                    $scope.newValues.counterYear = parentScope.TestForm.counterProductionYear;
                    $scope.newValues.accumulatedVolume = parentScope.TestForm.accumulatedVolume;
                    $scope.setStatusTypeWater(parentScope.TestForm.typeWater);
                } else {
                    var idSplit = photoId.split("Photo");
                    $scope.photoType = idSplit[0];
                    $scope.photoIndex = parseInt(idSplit[1]);
                    $scope.newValues.counterValue = $scope.photoType == 'begin'
                        ? parentScope.TestDataFormData[$scope.photoIndex].initialValue
                        : parentScope.TestDataFormData[$scope.photoIndex].endValue;
                }

                $scope.photo = document.getElementById(photoId).src;

                $scope.rotateIndex;

                switch (document.getElementById(photoId).className) {
                    case "rotated90" :
                    {
                        $scope.rotateIndex = 1;
                        break;
                    }
                    case "rotated180" :
                    {
                        $scope.rotateIndex = 2;
                        break;
                    }
                    case "rotated270" :
                    {
                        $scope.rotateIndex = 3;
                        break;
                    }
                    case "rotated0" :
                    {
                        $scope.rotateIndex = 4;
                        break;
                    }
                }

                $scope.rotateLeft = function () {
                    $scope.rotateIndex--;
                    if ($scope.rotateIndex == 0) {
                        $scope.rotateIndex = 4;
                    }
                };

                $scope.rotateRight = function () {
                    $scope.rotateIndex++;
                    if ($scope.rotateIndex == 5) {
                        $scope.rotateIndex = 1;
                    }
                };

                $scope.rotate180 = function () {
                    $scope.rotateIndex += 2;
                    if ($scope.rotateIndex > 4) {
                        $scope.rotateIndex -= 4;
                    }
                };

                $scope.saveOnExit = function () {
                    $scope.$broadcast('show-errors-check-validity');
                    if($scope.clientForm.$valid) {
                        if (photoId == "testMainPhoto") {
                            parentScope.TestForm.counterNumber = $scope.newValues.counterNumber;
                            parentScope.TestForm.accumulatedVolume = $scope.newValues.accumulatedVolume;
                            parentScope.TestForm.counterProductionYear = $scope.newValues.counterYear;
                            parentScope.TestForm.typeWater = $scope.newValues.counterType.typeWater;
                            parentScope.TestForm.standardSize = $scope.newValues.counterStandard.standardSize;
                            parentScope.TestForm.symbol = $scope.newValues.counterType.symbol;
                            parentScope.TestForm.counterTypeId = $scope.newValues.counterType.id;
                        } else {
                            if ($scope.photoType == 'begin') {
                                parentScope.TestDataFormData[$scope.photoIndex].initialValue = $scope.newValues.counterValue;
                                $scope.updateValues($scope.photoIndex);
                            } else {
                                parentScope.TestDataFormData[$scope.photoIndex].endValue = $scope.newValues.counterValue;
                                $scope.updateValues($scope.photoIndex);
                            }
                            $scope.isChanged = false;
                        }

                        switch ($scope.rotateIndex) {
                            case 1:
                            {
                                document.getElementById(photoId).className = "rotated90";
                                break;
                            }
                            case 2:
                            {
                                document.getElementById(photoId).className = "rotated180";
                                break;
                            }
                            case 3:
                            {
                                document.getElementById(photoId).className = "rotated270";
                                break;
                            }
                            case 4:
                            {
                                document.getElementById(photoId).className = "rotated0";
                                break;
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
                        if(matches){
                            if (/^\d{1,10}$/.test(accumulatedVolume)) {
                                validator('accumulatedVolume', false);
                            } else {
                                validator('accumulatedVolume', true);
                            }
                        }else{
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
            ])
            ;

