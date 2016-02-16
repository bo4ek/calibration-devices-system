angular.module('employeeModule').controller('AddingVerificationsControllerProvider', ['$scope', '$modal', '$state', '$http', '$log',
    'AddressServiceProvider', 'VerificationServiceProvider', '$stateParams',
    '$rootScope', '$location', '$window', '$modalInstance', '$filter', '$translate', 'toaster',

    function ($scope, $modal, $state, $http, $log, addressServiceProvider, verificationServiceProvider, $stateParams,
              $rootScope, $location, $window, $modalInstance, $filter, $translate, toaster) {
        $scope.isShownForm = true;
        $scope.isCalibrator = -1;
        $scope.calibratorDefined = false;

        /**
         * Receives all regex for input fields
         */
        $scope.FLAT_REGEX = /^([1-9][0-9]{0,3}|0)$/;
        $scope.BUILDING_REGEX = /^[1-9][0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457])?$/;
        $scope.PHONE_REGEX_SECOND = /^[1-9]\d{8}$/;

        $scope.checkboxModel = false;

        /**
         * to open first block "General Information" when the modal form is loaded
         * (for Accordion)
         * @type {boolean}
         */
        $scope.generalInformation = true;

        $scope.regions = [];
        $scope.devices = [];
        $scope.localities = [];
        $scope.providers = [];
        $scope.calibrators = [];
        $scope.streetsTypes = [];
        $scope.symbols = [];
        $scope.standardSizes = [];

        $scope.selectedData = {};
        $scope.selectedData.selectedStreetType = "";
        $scope.selectedData.dismantled = false;
        $scope.selectedData.sealPresence = true;

        $scope.applicationCodes = [];
        $scope.codes = [];
        $scope.selectedData.selectedCount = '1';
        $scope.deviceCountOptions = [1, 2, 3, 4];

        $scope.addInfo = {};
        $scope.addInfo.serviceability = true;

        $scope.formData = {};
        $scope.formData.comment = "";

        $scope.options = {
            hstep: [0.5, 1.0, 1.5, 2.0, 2.5, 3.0]
        };

        $scope.moments = [];


        /**
         * For timepicker
         */
        $scope.updateTimepicker = function() {
            $scope.addInfo.timeFrom = new Date();
            $scope.addInfo.timeFrom.setHours( 8 );
            $scope.addInfo.timeFrom.setMinutes( 0 );

            $scope.updateTimeTo();
        };

        $scope.updateTimeTo = function() {
            $scope.moments = [];
            var time = undefined;
            var plusTime;
            angular.forEach($scope.options.hstep, function(value) {
                time = moment((new Date($scope.addInfo.timeFrom)).getTime());
                plusTime = 60 * value;
                $scope.moments.push(time.add(plusTime, 'minutes').format("HH:mm"));
            });
            $scope.addInfo.timeTo = $scope.moments[3];
        };

        $scope.updateTimepicker();

        /**
         * Closes modal window on browser's back/forward button click.
         */
        $rootScope.$on('$locationChangeStart', function () {
            $modalInstance.close();
        });

        addressServiceProvider.checkOrganizationType().success(function (response) {
            $scope.isCalibrator = response;
        });

        function arrayObjectIndexOf(myArray, searchTerm, property) {
            for (var i = 0, len = myArray.length; i < len; i++) {
                if (myArray[i][property] === searchTerm) return i;
            }
            return 0;
        }

        function arrayIndexOf(myArray, searchTerm) {
            for (var i = 0, len = myArray.length; i < len; i++) {
                if (myArray[i] === searchTerm) return i;
            }
            return 0;
        }

        function arrayObjectIndexOfMoments(myArray, searchTerm) {
            for (var i = 0, len = myArray.length; i < len; i++) {
                if (myArray[i] === searchTerm.format("HH:mm")) return i;
            }
            return 0;
        }

        $scope.fillTimeToForEdit = function() {
            $scope.updateTimeTo();
            var index = arrayObjectIndexOfMoments($scope.moments, moment($scope.verification.data.timeTo, "HH:mm"));
            $scope.addInfo.timeTo = $scope.moments[index];
        };

        /**
         * Receives all possible regions.
         */
        $scope.receiveRegions = function () {
            addressServiceProvider.findAllRegions()
                .success(function (regions) {
                    $scope.regions = regions;
                    $scope.selectedData.region = "";
                    $scope.selectedData.district = "";
                    $scope.selectedData.locality = "";
                    $scope.selectedData.selectedStreet = "";
                });
        };

        $scope.receiveRegions();

        /**
         * Receives all possible devices.
         */
        addressServiceProvider.findAllDevices()
            .success(function (devices) {
                $scope.devices = devices;
                $log.debug('device');
                $log.debug(devices);
                $scope.selectedData.selectedDevice = undefined;  //$scope.devices[0];
                $log.debug($scope.selectedData.selectedCount);
            });

        /**
         * Receives deviceTypes for this organization.
         */
        addressServiceProvider.findAllDeviceTypes()
            .success(function (deviceTypes) {
                $scope.deviceTypes = deviceTypes;
                $log.debug('deviceTypes');
                $log.debug(deviceTypes);
                $scope.selectedData.selectedDeviceType = undefined;  //$scope.devices[0];
                $log.debug($scope.selectedData.selectedCount);
            });

        /**
         * select device by deviceType (isn't very usefull. only not to broke another functionality)
         */
        $scope.selectDevice = function() {

            angular.forEach($scope.devices, function(value){
                if(value.deviceType ===  $scope.selectedData.selectedDeviceType){
                    $scope.selectedData.selectedDevice = value;
                }
            });

        };

        /**
         * Receives list of all symbols from table counter_type
         */
        $scope.receiveAllSymbols = function(deviceType) {
            $scope.symbols = [];
            addressServiceProvider.findAllSymbols(deviceType)
                .success(function(symbols) {
                   $scope.symbols = symbols;
                   $scope.selectedData.counterSymbol = undefined;
                   $scope.selectedData.counterStandardSize = undefined;
                });
        };

        /**
         * Receive list of standardSizes from table counter_type by symbol
         */
        $scope.recieveStandardSizesBySymbol = function (symbol, deviceType) {
            $scope.standardSizes = [];
            addressServiceProvider.findStandardSizesBySymbol(symbol, deviceType)
                .success(function(standardSizes) {
                   $scope.standardSizes = standardSizes;
                   $scope.selectedData.counterStandardSize = undefined;
                });
        };

        /**
         * Receives all possible districts.
         * On-select handler in region input form element.
         */
        $scope.receiveDistricts = function (selectedRegion) {
            $scope.districts = [];
            addressServiceProvider.findDistrictsByRegionId(selectedRegion.id)
                .success(function (districts) {
                    $scope.districts = districts;
                    $scope.selectedData.district = "";
                    $scope.selectedData.locality = "";
                    $scope.selectedData.selectedStreet = "";
                });
        };
        /**
         * Receives all possible localities.
         * On-select handler in district input form element.
         */
        $scope.receiveLocalitiesAndProviders = function (selectedDistrict) {
            addressServiceProvider.findLocalitiesByDistrictId(selectedDistrict.id)
                .success(function (localities) {
                    $scope.localities = localities;
                    $scope.selectedData.locality = "";
                    $scope.selectedData.selectedStreet = "";
                });
        };

        $scope.receiveCalibrators = function (deviceType) {
            addressServiceProvider.findCalibratorsForProviderByType(deviceType)
                .success(function (calibrators) {
                    $scope.calibrators = calibrators;
                    $scope.selectedData.selectedCalibrator = "";
                    if ($scope.isCalibrator > 0) {
                        var index = arrayObjectIndexOf($scope.calibrators, $scope.isCalibrator, "id");
                        $scope.selectedData.selectedCalibrator = $scope.calibrators[index];
                    }
                });
        };

        addressServiceProvider.findStreetsTypes().success(function (streetsTypes) {
            $scope.streetsTypes = streetsTypes;
            $scope.selectedData.selectedStreetType = "";
            $log.debug("$scope.streetsTypes");
            $log.debug($scope.streetsTypes);
        });

        /**
         * Receives all possible streets.
         * On-select handler in locality input form element
         */
        $scope.receiveStreets = function (selectedLocality, selectedDistrict) {
            if (!$scope.blockSearchFunctions) {
                $scope.streets = [];
                addressServiceProvider.findStreetsByLocalityId(selectedLocality.id)
                    .success(function (streets) {
                        $scope.streets = streets;
                        $scope.selectedData.selectedStreet = "";
                    });
                $scope.indexes = [];
                addressServiceProvider.findMailIndexByLocality(selectedLocality.designation, selectedDistrict.id)
                    .success(function (indexes) {
                        $scope.indexes = indexes;
                        $scope.selectedData.index = indexes[0];
                    });
            }
        };
        /**
         * Receives all possible buildings.
         * On-select handler in street input form element.
         */
        $scope.receiveBuildings = function (selectedStreet) {
            $scope.buildings = [];
            addressServiceProvider.findBuildingsByStreetId(selectedStreet.id)
                .success(function (buildings) {
                    $scope.buildings = buildings;
                });
        };

        $scope.selectedData.calibratorRequired = false;
        /**
         * Sends data to the server where Verification entity will be created.
         * On-click handler in send button.
         */
        $scope.isMailValid = true;
        $scope.sendApplicationData = function () {
            $scope.selectedData.calibratorRequired = true;
            $scope.$broadcast('show-errors-check-validity');
            if ($scope.clientForm.$valid) { // && $scope.selectedData.selectedCalibrator

                $scope.fillFormData();
                $scope.formData.calibratorId = $scope.selectedData.selectedCalibrator.id;

                for (var i = 0; i < $scope.selectedData.selectedCount; i++) {
                    verificationServiceProvider.sendInitiatedVerification($scope.formData)
                        .success(function (applicationCode) {
                            if(applicationCode) {
                                if (!$scope.applicationCodes) {
                                    $scope.applicationCodes = [];
                                }
                                $scope.applicationCodes.push(applicationCode);
                                $scope.isShownForm = false;
                            } else {
                                toaster.pop('error', $filter('translate')('SAVE_VERIF_ERROR'));
                            }
                        });
                }
                verificationServiceProvider.checkMailIsExist($scope.formData)
                    .success(function (isMailValid) {
                        $scope.isMailValid = isMailValid;
                    });

                //hide form because application status is shown

            }
        };



        /**
         * create and save in database the verification from filled fields in form when user clicks "Save"
         */
        $scope.save = function() {
            $scope.selectedData.calibratorRequired = false;
            $scope.$broadcast('show-errors-check-validity');
            if($scope.clientForm.$valid) {

                $scope.fillFormData();

                verificationServiceProvider.saveVerification($scope.formData)
                    .success(function (applicationCode) {
                        if(applicationCode) {
                            if (!$scope.applicationCodes) {
                                $scope.applicationCodes = [];
                            }
                            $scope.applicationCodes.push(applicationCode);

                            $rootScope.$broadcast('provider-save-verification');
                            $scope.isShownForm = false;
                        } else {
                            toaster.pop('error', $filter('translate')('SAVE_VERIF_ERROR'));
                        }
                    });
            }
        };


        /**
         * Assing all data into FormData for sending to server. "Save" or "Send" button
         */
        $scope.fillFormData = function() {

            $scope.selectDevice();

            //LOCATION
            $scope.formData.region = $scope.selectedData.region.designation;
            $scope.formData.district = $scope.selectedData.district.designation;
            $scope.formData.locality = $scope.selectedData.locality.designation;
            $scope.formData.street = $scope.selectedData.selectedStreet.designation ||  $scope.selectedData.selectedStreet;
            $scope.formData.building =  $scope.selectedData.selectedBuilding.designation || $scope.selectedData.selectedBuilding;
            $scope.formData.deviceId = $scope.selectedData.selectedDevice.id;

            // COUNTER
            $scope.formData.dismantled = $scope.selectedData.dismantled;
            $scope.formData.dateOfDismantled = ($scope.convertDateToLong($scope.selectedData.dateOfDismantled) !== 0) ?
                $scope.convertDateToLong($scope.selectedData.dateOfDismantled.startDate) : null;
            $scope.formData.dateOfMounted = ($scope.convertDateToLong($scope.selectedData.dateOfMounted) !== 0) ?
                $scope.convertDateToLong($scope.selectedData.dateOfMounted.startDate) : null;
            $scope.formData.numberCounter = $scope.selectedData.numberCounter;
            $scope.formData.sealPresence = $scope.selectedData.sealPresence;
            if($scope.selectedData.counterSymbol) {
                $scope.formData.symbol = $scope.selectedData.counterSymbol;
            }
            if($scope.selectedData.counterStandardSize) {
                $scope.formData.standardSize = $scope.selectedData.counterStandardSize;
            }
            $scope.formData.releaseYear = $scope.selectedData.releaseYear;

            // ADDITION INFO
            $scope.formData.entrance = $scope.addInfo.entrance;
            $scope.formData.doorCode = $scope.addInfo.doorCode;
            $scope.formData.floor = $scope.addInfo.floor;
            $scope.formData.dateOfVerif = ($scope.convertDateToLong($scope.addInfo.dateOfVerif) !== 0) ?
                $scope.convertDateToLong($scope.addInfo.dateOfVerif.startDate) : null;
            $scope.formData.timeFrom = moment($scope.convertDateToLong($scope.addInfo.timeFrom)).format("HH:mm");
            $scope.formData.timeTo = $scope.addInfo.timeTo;

            $scope.formData.serviceability = $scope.addInfo.serviceability;

            $scope.formData.noWaterToDate = ($scope.convertDateToLong($scope.addInfo.noWaterToDate) !== 0) ?
                $scope.convertDateToLong($scope.addInfo.noWaterToDate.startDate) : null;
            $scope.formData.notes = $scope.addInfo.notes;
        };

        /**
         * Convert date to long to sent it to backend
         * @param date
         * @returns {number}
         */
        $scope.convertDateToLong = function(date) {
            return (new Date(date)).getTime();
        };

        $scope.closeAlert = function () {
            if($scope.isShownForm) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/close-alert.html',
                    controller: 'VerificationCloseAlertController',
                    size: 'md'
                })
            }else {
                $modalInstance.close();
            }

        };

        $scope.$on('close-form', function(event, args) {
            $modalInstance.close();
        });

        /**
         * Resets form
         */
        $scope.resetApplicationForm = function () {
            $modal.open({
                animation: true,
                templateUrl: 'resources/app/common/views/modals/reset-alert.html',
                controller: 'VerificationResetAlertController',
                size: 'md'
            })

        };

        $scope.$on('reset-form', function(event, args){
            $scope.reset();
        });

        $scope.reset = function() {
            $scope.$broadcast('show-errors-reset');

            $scope.clientForm.$setPristine();
            $scope.clientForm.$setUntouched();

            $scope.formData = [];

            $scope.selectedData = [];
            $scope.addInfo = [];

            $scope.selectedData.region = undefined;
            $scope.selectedData.district = undefined;
            $scope.selectedData.locality = undefined;
            $scope.selectedData.selectedStreetType = undefined;
            $scope.selectedData.index = undefined;

            $scope.addInfo.serviceability = true;
            $scope.selectedData.dismantled = false;
            $scope.selectedData.sealPresence = true;
            $scope.selectedData.selectedCount = '1';
            $scope.selectedData.calibratorRequired = false;

            $scope.updateTimepicker();

            $log.debug("$scope.resetApplicationForm");
        };

        /**
         * Fill application sending page from verification when some verification is checked (checknox on previous page) and user clicks "Create by pattern"
         * @param ID - Id of verification to fill from
         */
        $scope.createNew = function () {
            if ($rootScope.verifIDforTempl) {
                verificationServiceProvider.getVerificationById($rootScope.verifIDforTempl).then(function (verification) {

                    $scope.verification = verification;
                    $scope.formData = {};
                    $scope.formData.lastName = $scope.verification.data.lastName;
                    $scope.formData.firstName = $scope.verification.data.firstName;
                    $scope.formData.middleName = $scope.verification.data.middleName;
                    $scope.formData.email = $scope.verification.data.email;
                    $scope.formData.phone = $scope.verification.data.phone;
                    $scope.formData.flat = $scope.verification.data.flat;
                    $scope.formData.comment = $scope.verification.data.comment;

                    $scope.selectedData.dismantled = ($scope.verification.data.dismantled !== null)
                        ? $scope.verification.data.dismantled : true;
                    $scope.selectedData.sealPresence = ($scope.verification.data.sealPresence !== null)
                        ? $scope.verification.data.sealPresence : true;
                    $scope.selectedData.dateOfDismantled = $scope.verification.data.dateOfDismantled;
                    $scope.selectedData.dateOfMounted = $scope.verification.data.dateOfMounted;
                    $scope.selectedData.numberCounter = $scope.verification.data.numberCounter;
                    $scope.selectedData.releaseYear = $scope.verification.data.releaseYear;

                    $scope.addInfo.entrance = $scope.verification.data.entrance;
                    $scope.addInfo.doorCode = $scope.verification.data.doorCode;
                    $scope.addInfo.floor = $scope.verification.data.floor;
                    $scope.addInfo.dateOfVerif = $scope.verification.data.dateOfVerif;
                    if($scope.verification.data.timeFrom && $scope.verification.data.timeTo) {
                        $scope.addInfo.timeFrom = moment($scope.verification.data.timeFrom, "HH:mm");
                        $scope.fillTimeToForEdit()
                    } else {
                        $scope.updateTimepicker();
                    }
                    $scope.addInfo.serviceability = ($scope.verification.data.serviceability !== null)
                        ? $scope.verification.data.serviceability : true;
                    $scope.addInfo.noWaterToDate = $scope.verification.data.noWaterToDate;
                    $scope.addInfo.notes = $scope.verification.data.notes;

                    $scope.selectedData.selectedBuilding = $scope.verification.data.building;

                    addressServiceProvider.findAllRegions().then(function (respRegions) {
                        $scope.regions = respRegions.data;
                        var index = arrayObjectIndexOf($scope.regions, $scope.verification.data.region, "designation");
                        $scope.selectedData.region = $scope.regions[index];

                        addressServiceProvider.findDistrictsByRegionId($scope.selectedData.region.id)
                            .then(function (districts) {
                                $scope.districts = districts.data;
                                var index = arrayObjectIndexOf($scope.districts, $scope.verification.data.district, "designation");
                                $scope.selectedData.district = $scope.districts[index];

                                addressServiceProvider.findLocalitiesByDistrictId($scope.selectedData.district.id)
                                    .then(function (localities) {
                                        $scope.localities = localities.data;
                                        var index = arrayObjectIndexOf($scope.localities, $scope.verification.data.locality, "designation");
                                        $scope.selectedData.locality = $scope.localities[index];

                                        addressServiceProvider.findStreetsByLocalityId($scope.selectedData.locality.id)
                                            .then(function(streets) {
                                                $scope.streets = streets.data;
                                                var index = arrayObjectIndexOf($scope.streets, $scope.verification.data.street, "designation");
                                                $scope.selectedData.selectedStreet = $scope.streets[index];

                                            });

                                        addressServiceProvider.findMailIndexByLocality($scope.selectedData.locality.designation,
                                            $scope.selectedData.district.id)
                                            .success(function (indexes) {
                                                $scope.indexes = indexes;
                                                $scope.selectedData.index = $scope.indexes[0];
                                                $scope.blockSearchFunctions = false;
                                            });
                                    });
                            });
                         });


                    if($scope.verification.data.deviceName) {
                        addressServiceProvider.findAllDevices().then(function (devices) {
                            $scope.devices = devices.data;
                            var index = arrayObjectIndexOf($scope.devices, $scope.verification.data.deviceName, "designation");
                            $scope.selectedData.selectedDevice = $scope.devices[index];
                        });

                        addressServiceProvider.findAllDeviceTypes().then(function(deviceTypes) {
                            $scope.deviceTypes = deviceTypes.data;
                            var index = arrayIndexOf($scope.deviceTypes, $scope.verification.data.deviceType);
                            $scope.selectedData.selectedDeviceType = $scope.deviceTypes[index];

                            if ($scope.verification.data.symbol) {

                                addressServiceProvider.findAllSymbols($scope.verification.data.deviceType).then(function (respSymbols) {
                                    $scope.symbols = respSymbols.data;
                                    var index = arrayIndexOf($scope.symbols, $scope.verification.data.symbol);
                                    $scope.selectedData.counterSymbol = $scope.symbols[index];

                                    addressServiceProvider.findStandardSizesBySymbol($scope.selectedData.counterSymbol, $scope.verification.data.deviceType)
                                        .then(function (standardSizes) {
                                            $scope.standardSizes = standardSizes.data;
                                            var index = arrayIndexOf($scope.standardSizes, $scope.verification.data.standardSize);
                                            $scope.selectedData.counterStandardSize = $scope.standardSizes[index];
                                        });
                                });

                            }
                            $scope.receiveCalibrators($scope.verification.data.deviceType);
                        });
                    }

                });
            } else {


                toaster.pop('warning', $filter('translate')('CREATE_BY_PATTERN_WARNING'));
            }
        };

        /**
         *  Date picker and formatter setup
         *
         */


        $scope.selectedData.dateOfDismantled = null;
        $scope.selectedData.dateOfMounted = null;
        $scope.addInfo.dateOfVerif = null;
        $scope.addInfo.noWaterToDate = null;
        $scope.defaultDate = null;

        $scope.initDatePicker = function () {

            if ($scope.defaultDate == null) {
                //copy of original daterange
                $scope.defaultDate = angular.copy($scope.selectedData.dateOfDismantled);
                $scope.defaultDate = angular.copy($scope.selectedData.dateOfMounted);
                $scope.defaultDate = angular.copy($scope.addInfo.dateOfVerif);
                $scope.defaultDate = angular.copy($scope.addInfo.noWaterToDate);
            }

            $scope.setTypeDataLangDatePicker = function () {

                $scope.opts = {
                    format: 'DD-MM-YYYY',
                    singleDatePicker: true,
                    showDropdowns: true,
                    eventHandlers: {}
                };

                $scope.optsMin = {
                    format: 'DD-MM-YYYY',
                    singleDatePicker: true,
                    showDropdowns: true,
                    minDate: new Date(),
                    eventHandlers: {}
                };

                $scope.optsMax = {
                    format: 'DD-MM-YYYY',
                    singleDatePicker: true,
                    showDropdowns: true,
                    maxDate: new Date(),
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


        $scope.clear = function () {
            $scope.addInfo.pickerDate = null;
        };

        // Disable weekend selection
        $scope.disabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
        };

        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };

        $scope.toggleMin();
        $scope.maxDate = new Date(2100, 5, 22);
        $scope.minDateDismantled = new Date(2015, 1, 1);
        $scope.maxDateDismantled = new Date();


        $scope.clearDate1 = function () {
            $scope.addInfo.dateOfVerif = null;
        };

        $scope.clearDate2 = function () {
            $scope.addInfo.noWaterToDate = null;
        };

        $scope.clearDateOfDismantled = function() {
            $scope.selectedData.dateOfDismantled = null;
        };

        $scope.clearDateOfMounted = function() {
            $scope.selectedData.dateOfMounted = null;
        };

        /**
         * additonal info form validation
         *
         * @param caseForValidation
         */
        $scope.checkAll = function (caseForValidation) {
            switch (caseForValidation) {
                case ('installationNumber'):
                    var installationNumber = $scope.calibrationTask.installationNumber;
                    if (/^[0-9]{5,20}$/.test(installationNumber)) {
                        validator('installationNumber', false);
                    } else {
                        validator('installationNumber', true);
                    }
                    break;
                case ('entrance'):
                    var entrance = $scope.addInfo.entrance;
                    if (/^[0-9]{1,2}$/.test(entrance)) {
                        validator('entrance', false);
                    } else {
                        validator('entrance', true);
                    }
                    break;
                case ('doorCode'):
                    var doorCode = $scope.addInfo.doorCode;
                    if (/^[0-9]{1,4}$/.test(doorCode)) {
                        validator('doorCode', false);
                    } else {
                        validator('doorCode', true);
                    }
                    break;
                case ('floor'):
                    var floor = $scope.addInfo.floor;
                    if (floor == null) {

                    } else if (/^\d{1,2}$/.test(floor)) {
                        validator('floor', false);
                    } else {
                        validator('floor', true);
                    }
                    break;
                case ('time'):
                    var time = $scope.addInfo.time;
                    if (/^[0-1]{1}[0-9]{1}(\:)[0-9]{2}(\-)[0-2]{1}[0-9]{1}(\:)[0-9]{2}$/.test(time)) {
                        validator('time', false);
                    } else {
                        validator('time', true);
                    }
                    break;
            }

        };

        function validator(caseForValidation, isValid) {
            switch (caseForValidation) {
                case ('entrance'):
                    $scope.entranceValidation = {
                        isValid: isValid,
                        css: isValid ? 'has-error' : 'has-success'
                    };
                    break;
                case ('doorCode'):
                    $scope.doorCodeValidation = {
                        isValid: isValid,
                        css: isValid ? 'has-error' : 'has-success'
                    };
                    break;
                case ('floor'):
                    $scope.floorValidation = {
                        isValid: isValid,
                        css: isValid ? 'has-error' : 'has-success'
                    };
                    break;
                case ('time'):
                    $scope.timeValidation = {
                        isValid: isValid,
                        css: isValid ? 'has-error' : 'has-success'
                    };
                    break;

            }
        }

    }
]);
