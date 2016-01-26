angular
    .module('adminModule')
    .filter('organizationFilter', function () {
        // filters available options in the Organization Type multiselect,
        // depends on what options are selected
        return function (allTypes, currentTypes) {
            var filtered = allTypes; //all types in select

            if (currentTypes != undefined && currentTypes.length == 0) {
                return filtered; //nothing in the input, all the options available
            }

            if (currentTypes.length == 2)
                return []; //no options available, everything set already

            if (currentTypes[0].type == 'CALIBRATOR') {
                filtered = [];
                filtered.push(allTypes[0]); //provider
                filtered.push(allTypes[2]); //verificator
                return filtered;
            }

            if (currentTypes[0].type == 'PROVIDER' || currentTypes[0].type == 'STATE_VERIFICATOR') {
                filtered = [];
                filtered.push(allTypes[1]); //calibrator
                return filtered;
            }
            return filtered;
        }
    })
    .controller
(
    'OrganizationEditModalController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modal',
        '$modalInstance',
        '$filter',
        '$timeout',
        'AddressService',
        'UserService',
        'DevicesService',
        'OrganizationService', '$log', 'regions',
        function ($rootScope, $scope, $translate, $modal, $modalInstance, $filter, $timeout,
                  addressService,
                  userService, devicesService, organizationService, $log, regions) {

            $scope.defaultData = {};

            $scope.typeData = [
                {
                    type: 'PROVIDER',
                    label: $filter('translate')('PROVIDER')
                },
                {
                    type: 'CALIBRATOR',
                    label: $filter('translate')('CALIBRATOR')
                },
                {
                    type: 'STATE_VERIFICATOR',
                    label: $filter('translate')('STATE_VERIFICATOR')
                }
            ];

            $scope.counterData = [
                {
                    type: 'WATER',
                    label: $filter('translate')('WATER')
                },
                {
                    type: 'THERMAL',
                    label: $filter('translate')('THERMAL')
                }
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    moment.locale('uk');
                    $scope.typeData[0].label = 'Постачальник послуг';
                    $scope.typeData[1].label = 'Вимірювальна лабораторія';
                    $scope.typeData[2].label = 'Уповноважена повірочна лабораторія';
                    $scope.counterData[0].label = 'Холодна вода';
                    $scope.counterData[1].label = 'Гаряча вода';
                } else if (lang === 'eng') {
                    moment.locale('en');
                    $scope.typeData[0].label = 'Service provider';
                    $scope.typeData[1].label = 'Measuring laboratory';
                    $scope.typeData[2].label = 'Authorized calibration laboratory';
                    $scope.counterData[0].label = 'Cold water';
                    $scope.counterData[1].label = 'Hot water';
                }
            };

            var setCurrentTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    for (var i = 0; i < $scope.defaultData.organizationTypes.length; i++) {
                        switch ($scope.defaultData.organizationTypes[i].type) {
                            case "PROVIDER":
                                console.log($scope.defaultData.organizationTypes[i]);
                                $scope.defaultData.organizationTypes[i].label = 'Постачальник послуг';
                                break;
                            case "CALIBRATOR":
                                $scope.defaultData.organizationTypes[i].label = 'Вимірювальна лабораторія';
                                break;
                            case "STATE_VERIFICATOR":
                                $scope.defaultData.organizationTypes[i].label = 'Уповноважена повірочна лабораторія';
                                break;
                            default:
                                console.log($scope.defaultData.organizationTypes[i].type + " not organization type");
                        }
                    }

                    for (var i = 0; i < $scope.defaultData.deviceType.length; i++) {
                        switch ($scope.defaultData.deviceType[i].type) {
                            case "WATER":
                                console.log($scope.defaultData.deviceType[i]);
                                $scope.defaultData.deviceType[i].label = 'Холодна вода';
                                break;
                            case "THERMAL":
                                console.log($scope.defaultData.deviceType[i]);
                                $scope.defaultData.deviceType[i].label = 'Гаряча вода';
                                break;
                            default:
                                console.log($scope.defaultData.deviceType[i].type + " not device type");
                        }
                    }

                } else if (lang === 'eng') {
                    for (var i = 0; i < $scope.defaultData.organizationTypes.length; i++) {
                        switch ($scope.defaultData.organizationTypes[i].type) {
                            case 'PROVIDER':
                                $scope.defaultData.organizationTypes[i].label = 'Service provider';
                                break;
                            case 'CALIBRATOR':
                                $scope.defaultData.organizationTypes[i].label = 'Measuring laboratory';
                                break;
                            case 'STATE_VERIFICATOR':
                                $scope.defaultData.organizationTypes[i].label = 'Authorized calibration laboratory';
                                break;
                            default:
                                console.error($scope.defaultData.organizationTypes[i] + " not organization type");
                        }
                    }

                    for (var i = 0; i < $scope.defaultData.deviceType.length; i++) {
                        switch ($scope.defaultData.deviceType[i].type) {
                            case "WATER":
                                $scope.defaultData.deviceType[i].label = 'Cold water';
                                break;
                            case "THERMAL":
                                $scope.defaultData.deviceType[i].label = 'Hot water';
                                break;
                            default:
                                console.log($scope.defaultData.deviceType[i].type + " not device type");
                        }
                    }
                }
            };

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function (close) {
                $modalInstance.close();
                if (close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            });

            $scope.defaultData.organizationTypes = [];
            for (var i = 0; i < $rootScope.organization.types.length; i++) {
                $scope.defaultData.organizationTypes[i] = {
                    type: $rootScope.organization.types[i],
                    label: $filter('translate')($rootScope.organization.types[i])
                }
            }

            $scope.defaultData.deviceType = [];
            for (var i = 0; i < $rootScope.organization.counters.length; i++) {
                $scope.defaultData.deviceType[i] = {
                    type: $rootScope.organization.counters[i],
                    label: $filter('translate')($rootScope.organization.counters[i])
                }
            }

            $scope.setTypeDataLanguage();
            // setTimeout(setCurrentTypeDataLanguage(), 3000);

            console.log($scope.defaultData.organizationTypes);
            console.log($scope.defaultData.deviceType);

            $scope.regions = regions;
            $scope.districts = [];
            $scope.localities = [];
            $scope.streets = [];
            $scope.buildings = [];

            $scope.regionsReg = regions;
            $scope.districtsReg = [];
            $scope.localitiesReg = [];
            $scope.streetsReg = [];
            $scope.buildingsReg = [];


            $scope.initDatePicker = function () {

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

            $scope.clearDate = function () {
                $rootScope.organization.certificateDate = null;
            };

            $scope.organization.certificateDate = {
                endDate: ($scope.organization.certificateDate)
            };


            $scope.isUsernameAvailable = true;

            organizationService.getOrganizationAdmin($rootScope.organization.id).then(
                function (data) {
                    $scope.adminsFirstName = data.firstName;
                    $scope.adminsLastName = data.lastName;
                    $scope.adminsMiddleName = data.middleName;
                    $scope.adminsUserName = data.username;
                    $scope.oldUsername = data.username;
                    console.log(data);
                    console.log(data.firstName);
                }
            );


            $scope.checkIfUsernameIsAvailable = function () {
                var username = $scope.adminsUserName;
                userService.isUsernameAvailable(username).then(
                    function (data) {

                        if ($scope.USERNAME_REGEX.test(username) && ($scope.oldUsername != username) && (username != "")) {
                            $scope.isUsernameAvailable = data;
                        } else {
                            $scope.isUsernameAvailable = true;
                        }
                    })
            };


            $scope.checkField = function (caseForValidation) {
                switch (caseForValidation) {
                    case ('organizationName') :
                        var organizationName = $rootScope.organization.name;
                        if (organizationName == null) {
                        } else if ($scope.ORGANIZATION_NAME_REGEX.test(organizationName)) {
                            validator('organizationName', false);
                        } else {
                            validator('organizationName', true);
                        }
                        break;
                    case ('firstName') :
                        var firstName = $rootScope.organization.firstName;
                        if (firstName == null) {
                        } else if ($scope.FIRST_LAST_NAME_REGEX.test(firstName)) {
                            validator('firstName', false);
                        } else {
                            validator('firstName', true);
                        }
                        break;
                    case ('lastName') :
                        var lastName = $rootScope.organization.lastName;
                        if (lastName == null) {

                        } else if ($scope.FIRST_LAST_NAME_REGEX.test(lastName)) {

                            validator('lastName', false);
                        } else {
                            validator('lastName', true);
                        }
                        break;
                    case ('middleName') :
                        var middleName = $rootScope.organization.middleName;
                        if (middleName == null) {
                        } else if ($scope.MIDDLE_NAME_REGEX.test(middleName)) {
                            validator('middleName', false);
                        } else {
                            validator('middleName', true);
                        }
                        break;
                    case ('phone') :
                        var phone = $rootScope.organization.phone;
                        if (phone == null) {
                        } else if ($scope.PHONE_REGEX.test(phone)) {
                            validator('phone', false);
                        } else {
                            validator('phone', true);
                        }
                        break;
                    case ('email') :
                        var email = $rootScope.organization.email;
                        if (email == null) {
                        } else if ($scope.EMAIL_REGEX.test(email)) {
                            validator('email', false);
                        } else {
                            validator('email', true);
                        }
                        break;
                    case ('login') :
                        var username = $scope.adminsUserName;
                        if (username == null) {
                        } else if ($scope.USERNAME_REGEX.test(username)) {
                            $scope.checkIfUsernameIsAvailable();
                        } else {
                            validator('loginValid', false);
                        }
                        break;
                }
            };


            function validator(caseForValidation, isValid) {

                switch (caseForValidation) {
                    case 'organizationName':
                        $scope.organizationNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'firstName':
                        $scope.firstNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'lastName':
                        $scope.lastNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'middleName':
                        $scope.middleNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'phone':
                        $scope.phoneNumberValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'email':
                        $scope.emailValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        };
                        break;
                    case 'loginValid':
                        $scope.usernameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'К-сть символів не повинна бути меншою за 3\n і більшою за 16 '
                        };
                        break;
                }
            }

            function arrayObjectIndexOf(myArray, searchTerm, property) {
                for (var i = 0, len = myArray.length; i < len; i++) {
                    if (myArray[i][property] === searchTerm) {
                        return i;
                    }
                }
                var elem = {
                    id: length,
                    designation: searchTerm
                };
                myArray.push(elem);
                return (myArray.length - 1);
            }

            $scope.selectedValues = {};

            if ($rootScope.organization) {
                $scope.blockSearchFunctions = true;

                var index = arrayObjectIndexOf($scope.regions, $rootScope.organization.region, "designation");
                $scope.selectedValues.selectedRegion = $scope.regions[index];

                addressService.findDistrictsByRegionId($scope.selectedValues.selectedRegion.id)
                    .then(function (districts) {
                        $scope.districts = districts;
                        var index = arrayObjectIndexOf($scope.districts, $rootScope.organization.district, "designation");
                        $scope.selectedValues.selectedDistrict = $scope.districts[index];

                        addressService.findLocalitiesByDistrictId($scope.selectedValues.selectedDistrict.id)
                            .then(function (localities) {
                                $scope.localities = localities;
                                var index = arrayObjectIndexOf($scope.localities, $rootScope.organization.locality, "designation");
                                $scope.selectedValues.selectedLocality = $scope.localities[index];

                                addressService.findStreetsByLocalityId($scope.selectedValues.selectedLocality.id)
                                    .then(function (streets) {
                                        $scope.streets = streets;
                                        var index = arrayObjectIndexOf($scope.streets, $rootScope.organization.street, "designation");
                                        $scope.selectedValues.selectedStreet = $scope.streets[index];
                                        $scope.blockSearchFunctions = false;

                                        addressService.findBuildingsByStreetId($scope.selectedValues.selectedStreet.id)
                                            .then(function (buildings) {
                                                $scope.buildings = buildings;
                                                var index = arrayObjectIndexOf($scope.buildings, $rootScope.organization.building, "designation");
                                                $scope.selectedValues.selectedBuilding = $scope.buildings[index].designation;
                                            });
                                    });
                            });
                    });

                var index = arrayObjectIndexOf($scope.regionsReg, $rootScope.organization.regionRegistered, "designation");
                $scope.selectedValues.regionRegistered = $scope.regionsReg[index];

                addressService.findDistrictsByRegionId($scope.selectedValues.regionRegistered.id)
                    .then(function (districts) {
                        $scope.districtsReg = districts;
                        var index = arrayObjectIndexOf($scope.districtsReg, $rootScope.organization.districtRegistered, "designation");
                        $scope.selectedValues.districtRegistered = $scope.districtsReg[index];

                        addressService.findLocalitiesByDistrictId($scope.selectedValues.districtRegistered.id)
                            .then(function (localities) {
                                $scope.localitiesReg = localities;
                                var index = arrayObjectIndexOf($scope.localitiesReg, $rootScope.organization.localityRegistered, "designation");
                                $scope.selectedValues.localityRegistered = $scope.localitiesReg[index];

                                addressService.findStreetsByLocalityId($scope.selectedValues.localityRegistered.id)
                                    .then(function (streets) {
                                        $scope.streetsReg = streets;
                                        var index = arrayObjectIndexOf($scope.streetsReg, $rootScope.organization.streetRegistered, "designation");
                                        $scope.selectedValues.streetRegistered = $scope.streetsReg[index];
                                        $scope.blockSearchFunctions = false;

                                        addressService.findBuildingsByStreetId($scope.selectedValues.streetRegistered.id)
                                            .then(function (buildings) {
                                                $scope.buildingsReg = buildings;
                                                var index = arrayObjectIndexOf($scope.buildingsReg, $rootScope.organization.buildingRegistered, "designation");
                                                $scope.selectedValues.buildingRegistered = $scope.buildingsReg[index];
                                            });
                                    });
                            });
                    });

                organizationService.getServiceAreaLocalities($rootScope.organization.id)
                    .then(function (localities) {
                        organizationService.getServiceAreaRegion(localities.data[0].districtId)
                            .then(function (region) {
                                $scope.serviceArea.region = region.data;
                                $scope.serviceArea.districts = [];
                                addressService.findDistrictsByRegionId($scope.serviceArea.region.id)
                                    .then(function (districts) {
                                        $scope.serviceArea.districts = districts;
                                        $scope.selectedServiseAreaLocalities = localities.data.map(function (element) {
                                            return element.id;
                                        });
                                        var selectedDistricts = $scope.convertLocalityToDistrictList(localities);
                                        angular.forEach($scope.serviceArea.districts, function (element, index) {
                                            var i = selectedDistricts.indexOf(element.id);
                                            if (i !== -1) {
                                                $scope.selectRegionsFromDistrict(element, index);
                                            }
                                        });
                                    });
                            });
                    });
            }

            $scope.convertLocalityToDistrictList = function (list) {
                var resList = [];
                angular.forEach(list.data, function (element) {
                    var selectedIndex = resList.indexOf(element.districtId);
                    if (selectedIndex === -1) {
                        resList.push(element.districtId);
                    }
                });
                return resList;
            };

            /**
             * Receives all possible districts.
             * On-select handler in region input form element.
             */
            $scope.receiveDistricts = function (selectedRegion) {
                $scope.districts = [];
                addressService.findDistrictsByRegionId(selectedRegion.id)
                    .then(function (districts) {
                        $scope.districts = districts;
                        $scope.selectedValues.selectedLocality = undefined;
                        $scope.selectedValues.selectedDistrict = undefined;
                        $scope.selectedValues.selectedStreet = "";
                        $scope.organization.building = "";
                        $scope.organization.flat = "";
                    });
            };

            /**
             * Receives all possible localities.
             * On-select handler in district input form element.
             */
            $scope.receiveLocalities = function (selectedDistrict) {
                $scope.localities = [];
                addressService.findLocalitiesByDistrictId(selectedDistrict.id)
                    .then(function (localities) {
                        console.log(localities);
                        $scope.localities = localities;
                        $scope.selectedValues.selectedLocality = undefined;
                        $scope.selectedValues.selectedStreet = "";
                        $scope.organization.building = "";
                        $scope.organization.flat = "";
                    });
            };

            $scope.receiveStreets = function (selectedLocality) {
                $scope.streets = [];
                addressService.findStreetsByLocalityId(selectedLocality.id)
                    .then(function (streets) {
                        $scope.streets = streets;
                        $scope.selectedValues.selectedStreet = "";
                        $scope.organization.building = "";
                        $scope.organization.flat = "";
                        $log.debug("$scope.streets");
                        $log.debug($scope.streets);

                    }
                );
            };

            /**
             * receive information for registered address
             */
            $scope.receiveDistrictsRegistered = function (selectedRegion) {
                $scope.districts = [];
                addressService.findDistrictsByRegionId(selectedRegion.id)
                    .then(function (districts) {
                        $scope.districtsReg = districts;
                        $scope.selectedValues.localityRegistered = undefined;
                        $scope.selectedValues.districtRegistered = undefined;
                        $scope.selectedValues.streetRegistered = "";
                        $scope.selectedValues.buildingRegistered = "";
                        $rootScope.organization.flatRegistered = "";
                    });
            };

            $scope.receiveLocalitiesRegistered = function (selectedDistrict) {
                $scope.localitiesReg = [];
                addressService.findLocalitiesByDistrictId(selectedDistrict.id)
                    .then(function (localities) {
                        console.log(localities);
                        $scope.localitiesReg = localities;
                        $scope.selectedValues.localityRegistered = undefined;
                        $scope.selectedValues.streetRegistered = "";
                        $scope.selectedValues.buildingRegistered = "";
                        $rootScope.organization.flatRegistered = "";
                    });
            };

            $scope.receiveStreetsRegistered = function (selectedLocality) {
                $scope.streetsReg = [];
                addressService.findStreetsByLocalityId(selectedLocality.id)
                    .then(function (streets) {
                        $scope.streetsReg = streets;
                        $scope.selectedValues.streetRegistered = "";
                        $scope.selectedValues.buildingRegistered = "";
                        $rootScope.organization.flatRegistered = "";
                        $log.debug("$scope.streetsReg");
                        $log.debug($scope.streetsReg);

                    }
                );
            };

            /**
             * Finds districts in a given region.
             *
             * @param regionId
             *            to identify region
             */
            $scope.onRegionSelected = function (regionId) {
                addressService
                    .findDistrictsByRegionId(regionId)
                    .then(function (data) {
                        $scope.districts = data;
                    });
            };

            /**
             * Finds localities in a given district.
             *
             * @param districtId
             *            to identify district
             */
            $scope.onDistrictSelected = function (districtId) {
                addressService.findLocalitiesByDistrictId(
                    districtId).then(function (data) {
                        $scope.localities = data;
                    });
            };

            /**
             * Finds streets in a given locality.
             *
             * @param localityId
             *            to identify locality
             */
            $scope.onLocalitySelected = function (localityId) {
                addressService.findStreetsByLocalityId(
                    localityId).then(function (data) {
                        $scope.streets = data;
                    });
            };

            /**
             * Finds buildings in a given street.
             *
             * @param streetId
             *            to identify street
             */
            $scope.onStreetSelected = function (streetId) {
                addressService
                    .findBuildingsByStreetId(streetId)
                    .then(function (data) {
                        $scope.buildings = data;
                    });
            };

            /**
             * Change password
             */
            $scope.changePassword = function () {
                $scope.password = 'generate';
                $scope.generationMessage = true;
            };

            function addressFormToOrganizationForm() {
                if (typeof $rootScope.organization.region == 'object') {
                    $rootScope.organization.region = $rootScope.organization.region.designation;
                }
                if (typeof $rootScope.organization.district == 'object') {
                    $rootScope.organization.district = $rootScope.organization.district.designation;
                }
                if (typeof $rootScope.organization.locality == 'object') {
                    $rootScope.organization.locality = $rootScope.organization.locality.designation;
                }
                if (typeof $rootScope.organization.street == 'object') {
                    $rootScope.organization.street = $rootScope.organization.street.designation;
                }
                if (typeof $rootScope.organization.building == 'object') {
                    $rootScope.organization.building = $rootScope.organization.building.designation;
                }
            }

            function objectTypesToStringTypes() {
                for (var i = 0; i < $scope.defaultData.organizationTypes.length; i++) {
                    $scope.defaultData.organizationTypes[i] = $scope.defaultData.organizationTypes[i].type;
                }

                for (var j in $scope.defaultData.deviceType) {
                    $scope.defaultData.deviceType[j] = $scope.defaultData.deviceType[j].type;
                }
            }

            console.log($rootScope.organization.types);
            console.log($rootScope.organization.counters);

            /**
             * Finds districts in a given region.
             * @param regionId
             *            to identify region
             */
            $scope.onRegionSelected = function (regionId) {
                addressService
                    .findDistrictsByRegionId(regionId)
                    .then(function (data) {
                        $scope.districts = data;
                    });
            };

            /**
             * Finds localities in a given district.
             *
             * @param districtId
             *            to identify district
             */
            $scope.onDistrictSelected = function (districtId) {
                addressService.findLocalitiesByDistrictId(
                    districtId).then(function (data) {
                        $scope.localities = data;
                    });
            };

            /**
             * Finds streets in a given locality.
             *
             * @param localityId
             *            to identify locality
             */
            $scope.onLocalitySelected = function (localityId) {
                addressService.findStreetsByLocalityId(
                    localityId).then(function (data) {
                        $scope.streets = data;
                    });
            };

            /**
             * Finds buildings in a given street.
             *
             * @param streetId
             *            to identify street
             */
            $scope.onStreetSelected = function (streetId) {
                addressService
                    .findBuildingsByStreetId(streetId)
                    .then(function (data) {
                        $scope.buildings = data;
                    });
            };

            /**
             * Receives all possible buildings.
             * On-select handler in street input form element.
             */
            $scope.receiveBuildings = function (selectedStreet) {
                $scope.buildings = [];
                addressService.findBuildingsByStreetId(selectedStreet.id)
                    .success(function (buildings) {
                        $scope.buildings = buildings;
                    }
                );
            };

            /**
             * Convert date to long to sent it to backend
             * @param date
             * @returns {number}
             */
            $scope.convertDateToLong = function (date) {
                return (new Date(date)).getTime();
            };

            $scope.editOrganization = function () {
                $scope.$broadcast('show-errors-check-validity');
                addressFormToOrganizationForm();
                objectTypesToStringTypes();
                var organizationForm = {
                    name: $rootScope.organization.name,
                    email: $rootScope.organization.email,
                    phone: $rootScope.organization.phone,
                    types: $scope.defaultData.organizationTypes,
                    counters: $scope.defaultData.deviceType,
                    employeesCapacity: $rootScope.organization.employeesCapacity,
                    maxProcessTime: $rootScope.organization.maxProcessTime,

                    codeEDRPOU: $rootScope.organization.codeEDRPOU,
                    subordination: $rootScope.organization.subordination,
                    certificateNumberAuthorization: $rootScope.organization.certificateNumberAuthorization,
                    certificateDate: ($scope.convertDateToLong($rootScope.organization.certificateDate) !== 0) ?
                        $scope.convertDateToLong($rootScope.organization.certificateDate.endDate) : null,

                    region: $scope.selectedValues.selectedRegion.designation,
                    locality: $scope.selectedValues.selectedLocality.designation,
                    district: $scope.selectedValues.selectedDistrict.designation,
                    street: $scope.selectedValues.selectedStreet.designation || $scope.selectedValues.selectedStreet,
                    building: $rootScope.organization.building,
                    flat: $rootScope.organization.flat,

                    regionRegistered: $scope.selectedValues.regionRegistered.designation,
                    localityRegistered: $scope.selectedValues.districtRegistered.designation,
                    districtRegistered: $scope.selectedValues.localityRegistered.designation,
                    streetRegistered: $scope.selectedValues.streetRegistered.designation || $scope.selectedValues.streetRegistered,
                    buildingRegistered: $scope.selectedValues.buildingRegistered.designation || $scope.selectedValues.buildingRegistered,
                    flatRegistered: $rootScope.organization.flatRegistered,

                    username: $scope.adminsUserName,
                    firstName: $scope.adminsFirstName,
                    lastName: $scope.adminsLastName,
                    middleName: $scope.adminsMiddleName,
                    password: $scope.password,
                    serviceAreas: $scope.selectedServiseAreaLocalities
                };
                saveOrganization(organizationForm);
            };

            function saveOrganization(organizationForm) {
                organizationService.editOrganization(
                    organizationForm,
                    $rootScope.organizationId).then(
                    function (data) {
                        if (data == 200) {
                            console.log(data);
                            $rootScope.onTableHandling();
                        }
                        $modalInstance.close();
                    });
            }

            /**
             * Closes edit modal window.
             */

            $scope.closeModal = function () {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/admin/views/modals/close-alert.html',
                    controller: 'AdminCloseAlertController',
                    size: 'md'
                })
            };

            $scope.$on('close-modal', function (event, args) {
                $modalInstance.dismiss();
            });

            $scope.ORGANIZATION_NAME_REGEX = /^(?=.{5,50}$).*/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404'][a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d[A-Z\u0410-\u042f\u0407\u0406\u0404'][a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404'][a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404'][a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
            $scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
            $scope.PASSWORD_REGEX = /^(?=.{4,20}$).*/;
            $scope.BUILDING_REGEX = /^[1-9][0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457])?$/;
            $scope.FLAT_REGEX = /^([1-9][0-9]{0,3}|0)$/;

            /**
             * Receives all possible Districts for service area
             * On-select handler in region input form element.
             */
            $scope.receiveDistrictsForServiceArea = function (selectedRegion) {
                if (!$scope.blockSearchFunctions) {
                    $scope.serviceArea.districts = [];
                    addressService.findDistrictsByRegionId(selectedRegion.id)
                        .then(function (districts) {
                            $scope.serviceArea.districts = districts;
                            $scope.serviceArea.locality = undefined;
                        });
                }
            };

            /**
             * Receives all Localities from district
             * @param selectedDistrict
             * @param index
             */
            $scope.selectRegionsFromDistrict = function (selectedDistrict, index) {
                // if (!$scope.blockSearchFunctions) {
                if ($scope.serviceArea.locality === undefined) {
                    $scope.serviceArea.locality = [[]];
                }
                if ($scope.serviceArea.locality[index] === undefined || $scope.serviceArea.locality[index].length === 0) {
                    addressService.findLocalitiesByDistrictId(selectedDistrict.id)
                        .then(function (localities) {
                            $scope.serviceArea.locality[index] = localities;

                        });
                }
                //}
            };

            $scope.serviceArea = {};
            $scope.serviceArea.region = [];
            $scope.serviceArea.districts = [];
            $scope.serviceArea.locality = [[]];
            $scope.selectedServiseAreaLocalities = [];

            /**
             * Select all localities in district for service area
             * @param district
             * @param index
             * @param $event
             */
            $scope.selectAllLocalities = function (district, index, $event) {
                /**
                 * need to stop click propagation
                 */
                $event.stopPropagation();

                if ($scope.serviceArea.locality === undefined) {
                    $scope.serviceArea.locality = [[]];
                }
                if ($scope.serviceArea.locality[index] === undefined || $scope.serviceArea.locality[index].length === 0) {
                    addressService.findLocalitiesByDistrictId(district.id)
                        .then(function (localities) {
                            $scope.serviceArea.locality[index] = localities;

                            $scope.serviceArea.locality[index].forEach(function (element) {
                                var selectedIndex = $scope.selectedServiseAreaLocalities.indexOf(element.id);
                                if (selectedIndex === -1) {
                                    $scope.selectedServiseAreaLocalities.push(element.id);
                                }
                            });
                        });
                } else if (district.checked) {
                    $scope.serviceArea.locality[index].forEach(function (element) {
                        var selectedIndex = $scope.selectedServiseAreaLocalities.indexOf(element.id);
                        if (selectedIndex === -1) {
                            $scope.selectedServiseAreaLocalities.push(element.id);
                        }
                    });
                }
                else {
                    $scope.serviceArea.locality[index].forEach(function (element) {
                        var selectedIndex = $scope.selectedServiseAreaLocalities.indexOf(element.id);
                        if (selectedIndex > -1) {
                            $scope.selectedServiseAreaLocalities.splice(selectedIndex, 1);
                        }
                    });
                }
            };
        }
    ]);