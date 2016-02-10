angular
    .module('adminModule')
    .filter('organizationAddFilter', function () {
        return function (allTypes, currentTypes) {
            var filtered = allTypes;
            for (var i in currentTypes) {
                if (currentTypes[i].id != 'CALIBRATOR') {
                    var filtered = [];
                    filtered.push(allTypes[1]);
                    filtered.push(currentTypes[i]);
                }
            }
            return filtered;
        }
    })
    .controller(
    'OrganizationAddModalController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modal',
        '$modalInstance',
        '$filter',
        'AddressService',
        'OrganizationService',
        'UserService',
        'regions',
        function ($rootScope, $scope, $translate, $modal, $modalInstance, $filter,
                  addressService, organizationService, userService, regions) {

            $scope.blockSearchFunctions = false;
            $scope.typeData = [
                {
                    id: 'PROVIDER',
                    label: null
                },
                {
                    id: 'CALIBRATOR',
                    label: null
                },
                {
                    id: 'STATE_VERIFICATOR',
                    label: null
                }
            ];

            $scope.counterData = [
                {
                    id: 'WATER',
                    label: null
                },
                {
                    id: 'THERMAL',
                    label: null
                }
            ];

            /**
             * controls the number of simultaneously open windows. Is changing by checkbox on modal form
             * (for Accordion)
             * @type {boolean}
             */
            $scope.oneAtATime = true;

            /**
             * to open first block "General Information" when the modal form is loaded
             * (for Accordion)
             * @type {boolean}
             */
            $scope.generalInformation = true;

            $scope.organizationFormData = {};

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
                $scope.organizationFormData.certificateDate = null;
            };

            /**
             * Localization of multiselect for type of organization and device types
             */
            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    moment.locale('uk');
                    $scope.typeData[0].label = 'Надавач послуг';
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
            $scope.setTypeDataLanguage();

            /**
             * Reset organization form
             */
            $scope.resetApplicationForm = function () {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/admin/views/modals/reset-alert.html',
                    controller: 'AdminResetAlertController',
                    size: 'md'
                })
            };

            $scope.$on('reset-form', function (event, args) {
                $scope.resetOrganizationForm();
            });

            $scope.resetOrganizationForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.organizationForm.$setPristine();
                $scope.organizationForm.$setUntouched();
                $scope.organizationFormData = [];
                $scope.organizationFormData.types = undefined;
                $scope.organizationFormData.counters = undefined;
                $scope.organizationFormData.region = undefined;
                $scope.organizationFormData.district = undefined;
                $scope.organizationFormData.locality = undefined;
                $scope.organizationFormData.street = "";
                $scope.organizationFormData.building = "";
                $scope.organizationFormData.flat = null;

                $scope.organizationFormData.codeEDRPOU = undefined;
                $scope.organizationFormData.subordination = undefined;
                $scope.organizationFormData.certificateNumberAuthorization = undefined;
                $scope.organizationFormData.certificateDate = undefined;

                $scope.organizationFormData.regionRegistered = undefined;
                $scope.organizationFormData.districtRegistered = undefined;
                $scope.organizationFormData.localityRegistered = undefined;
                $scope.organizationFormData.streetRegistered = undefined;
                $scope.organizationFormData.buildingRegistered = undefined;
                $scope.organizationFormData.flatRegistered = undefined;

                $scope.selectedServiceAreaLocalities = [];
                $scope.serviceArea.locality = [[]];
                $scope.serviceArea.districts = [];
                $scope.serviceArea.region = undefined;
                $scope.serviceArea = {};
                $scope.organizationFormData.serviceArea = null;

                $log.debug("$scope.resetApplicationForm");
            };

            /**
             * Closes the modal window for adding new
             * organization.
             */
            $rootScope.closeModal = function () {
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


            /**
             * Checks whereas given username is available to use
             * for new user
             *
             */
            $scope.isUsernameAvailable = true;
            $scope.checkIfUsernameIsAvailable = function () {
                var username = $scope.organizationFormData.username;
                userService.isUsernameAvailable(username).then(
                    function (data) {
                        $scope.isUsernameAvailable = data;
                        if (!data) {
                            $scope.organizationForm.username.$valid = data;
                            $scope.organizationForm.username.$invalid = !data;
                        }
                    })
            };


            /**
             * Checks validation of service area
             * if wasn't chose any service area set form not valid
             */
            $scope.isValidAcordion = true;
            function checkValidAcardion() {
                if ($scope.selectedServiceAreaLocalities.length === 0) {
                    $scope.isValidAcordion = false;
                    $scope.organizationForm.serviceAreaRegion.$invalid = true;
                    $scope.organizationForm.serviceAreaRegion.$valid = false;
                    $scope.organizationForm.$valid = false;
                    $scope.organizationForm.$invalid = true;
                } else {
                    $scope.isValidAcordion = true;
                    $scope.organizationForm.serviceAreaRegion.$invalid = false;
                    $scope.organizationForm.serviceAreaRegion.$valid = true;
                }
            }

            $scope.regions = regions;
            $scope.districts = undefined;
            $scope.localities = undefined;
            $scope.streets = "";
            $scope.buildings = null;

            /**
             * Receives all possible districts.
             * On-select handler in region input form element.
             */
            $scope.receiveDistricts = function (selectedRegion) {
                if (!$scope.blockSearchFunctions) {
                    $scope.districts = [];
                    addressService.findDistrictsByRegionId(selectedRegion.id)
                        .then(function (districts) {
                            $scope.districts = districts;
                            $scope.organizationFormData.district = undefined;
                            $scope.organizationFormData.locality = undefined;
                            $scope.organizationFormData.street = "";
                            $scope.organizationForm.region.$valid = true;
                            $scope.organizationForm.region.$invalid = false;
                        });
                }
            };

            /**
             * Receives all possible localities.
             * On-select handler in district input form element.
             */
            $scope.receiveLocalities = function (selectedDistrict) {
                if (!$scope.blockSearchFunctions) {
                    $scope.localities = [];
                    addressService.findLocalitiesByDistrictId(selectedDistrict.id)
                        .then(function (localities) {
                            $scope.localities = localities;
                            $scope.organizationFormData.locality = undefined;
                            $scope.organizationFormData.street = "";

                        });
                }
            };

            /**
             * Receives all possible streets.
             * On-select handler in locality input form element
             */
            $scope.receiveStreets = function (selectedLocality) {
                if (!$scope.blockSearchFunctions) {
                    $scope.streets = [];
                    addressService.findStreetsByLocalityId(selectedLocality.id)
                        .then(function (streets) {
                            $scope.streets = streets;
                            $scope.organizationFormData.street = "";
                            $scope.organizationFormData.building = "";
                            $scope.organizationFormData.flat = "";
                        }
                    );
                }
            };

            $scope.regionsReg = regions;
            $scope.districtsReg = undefined;
            $scope.localitiesReg = undefined;
            $scope.streetsReg = undefined;
            $scope.buildingsReg = undefined;

            /**
             * Receives all possible districts.
             * On-select handler in regionReg input form element for RegisteredAddress.
             */
            $scope.receiveDistrictsRegistered = function (selectedRegionReg) {
                if (!$scope.blockSearchFunctions) {
                    $scope.districtsReg = [];
                    addressService.findDistrictsByRegionId(selectedRegionReg.id)
                        .then(function (districtsReg) {
                            $scope.districtsReg = districtsReg;
                            $scope.organizationFormData.districtRegistered = undefined;
                            $scope.organizationFormData.localityRegistered = undefined;
                            $scope.organizationFormData.streetRegistered = undefined;
                        });
                }
            };

            /**
             * Receives all possible localities.
             * On-select handler in districtReg input form element for RegisteredAddress.
             */
            $scope.receiveLocalitiesRegistered = function (selectedDistrictReg) {
                if (!$scope.blockSearchFunctions) {
                    $scope.localitiesReg = [];
                    addressService.findLocalitiesByDistrictId(selectedDistrictReg.id)
                        .then(function (localitiesReg) {
                            $scope.localitiesReg = localitiesReg;
                            $scope.organizationFormData.localityRegistered = undefined;
                            $scope.organizationFormData.streetRegistered = undefined;

                        });
                }
            };

            /**
             * Receives all possible streets.
             * On-select handler in localityReg input form element for RegisteredAddress
             */
            $scope.receiveStreetsRegistered = function (selectedLocalityReg) {
                if (!$scope.blockSearchFunctions) {
                    $scope.streetsReg = [];
                    addressService.findStreetsByLocalityId(selectedLocalityReg.id)
                        .then(function (streetsReg) {
                            $scope.streetsReg = streetsReg;
                            $scope.organizationFormData.streetRegistered = undefined;
                            $scope.organizationFormData.buildingRegistered = undefined;
                            $scope.organizationFormData.flatRegistered = undefined;
                        }
                    );
                }
            };


            $scope.serviceArea = {};
            $scope.serviceArea.region = undefined;
            $scope.serviceArea.districts = [];
            $scope.serviceArea.locality = [[]];
            $scope.selectedServiceAreaLocalities = [];

            /**
             * Receives all possible Districts for service area
             * On-select handler in serviceArea.region input form element.
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
                if (!$scope.blockSearchFunctions) {
                    if ($scope.serviceArea.locality === undefined) {
                        $scope.serviceArea.locality = [[]];
                    }
                    if ($scope.serviceArea.locality[index] === undefined || $scope.serviceArea.locality[index].length === 0) {
                        addressService.findLocalitiesByDistrictId(selectedDistrict.id)
                            .then(function (localities) {
                                $scope.serviceArea.locality[index] = localities;

                            });
                    }
                }
            };

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

                /**
                 * fill district by localities
                 */
                if ($scope.serviceArea.locality[index] === undefined || $scope.serviceArea.locality[index].length === 0) {
                    addressService.findLocalitiesByDistrictId(district.id)
                        .then(function (localities) {
                            $scope.serviceArea.locality[index] = localities;

                            /**
                             * check all localities
                             */
                            $scope.serviceArea.locality[index].forEach(function (element) {
                                var selectedIndex = $scope.selectedServiceAreaLocalities.indexOf(element.id);
                                if (selectedIndex === -1) {
                                    $scope.selectedServiceAreaLocalities.push(element.id);
                                }
                            });
                        });
                } else if (district.checked) {
                    $scope.serviceArea.locality[index].forEach(function (element) {
                        var selectedIndex = $scope.selectedServiceAreaLocalities.indexOf(element.id);
                        if (selectedIndex === -1) {
                            $scope.selectedServiceAreaLocalities.push(element.id);
                        }
                    });
                }
                else {
                    $scope.serviceArea.locality[index].forEach(function (element) {
                        var selectedIndex = $scope.selectedServiceAreaLocalities.indexOf(element.id);
                        if (selectedIndex > -1) {
                            $scope.selectedServiceAreaLocalities.splice(selectedIndex, 1);
                        }
                    });
                }
            };

            /**
             * Convert address data to string
             */
            function addressFormToOrganizationForm() {
                $scope.organizationFormData.region = $scope.organizationFormData.region.designation;
                $scope.organizationFormData.district = $scope.organizationFormData.district.designation;
                $scope.organizationFormData.locality = $scope.organizationFormData.locality.designation;
                $scope.organizationFormData.street = $scope.organizationFormData.street.designation;
                $scope.organizationFormData.serviceAreas = $scope.selectedServiceAreaLocalities;

                if ($scope.organizationFormData.regionRegistered) {
                    $scope.organizationFormData.regionRegistered = $scope.organizationFormData.regionRegistered.designation;
                }
                if ($scope.organizationFormData.districtRegistered) {
                    $scope.organizationFormData.districtRegistered = $scope.organizationFormData.districtRegistered.designation;
                }
                if ($scope.organizationFormData.localityRegistered) {
                    $scope.organizationFormData.localityRegistered = $scope.organizationFormData.localityRegistered.designation;
                }
                if ($scope.organizationFormData.streetRegistered) {
                    $scope.organizationFormData.streetRegistered = $scope.organizationFormData.streetRegistered.designation;
                }
            }

            function certificateDateToLong() {
                $scope.organizationFormData.certificateDate = (new Date($scope.organizationFormData.certificateDate.endDate)).getTime();
            }

            function objectTypesToStringTypes() {
                for (var i in $scope.organizationFormData.types) {
                    $scope.organizationFormData.types[i] = $scope.organizationFormData.types[i].id;
                }
                for (var i in $scope.organizationFormData.counters) {
                    $scope.organizationFormData.counters[i] = $scope.organizationFormData.counters[i].id;
                }
            }

            /**
             * Validates organization form before saving
             */
            $scope.onOrganizationFormSubmit = function () {
                if ($scope.organizationFormData.counters === undefined) {
                    $scope.organizationForm.counters.$error = {"required": true};
                    $scope.organizationForm.counters.$valid = false;
                    $scope.organizationForm.counters.$invalid = true;
                }
                if ($scope.organizationFormData.types === undefined) {
                    $scope.organizationForm.types.$error = {"required": true};
                    $scope.organizationForm.types.$valid = false;
                    $scope.organizationForm.types.$invalid = true;
                }
                checkValidAcardion();
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.organizationForm.$valid && $scope.isUsernameAvailable) {
                    addressFormToOrganizationForm();
                    certificateDateToLong();
                    objectTypesToStringTypes();
                    saveOrganization();
                }
            };

            /**
             * Saves new organization from the form in database.
             * If everything is ok then resets the organization
             * form and updates table with organizations.
             */
            function saveOrganization() {
                organizationService.saveOrganization($scope.organizationFormData)
                    .then(function (data) {
                        if (data == 201) {
                            $rootScope.onTableHandling();
                        }
                        $modalInstance.close();
                    });
            }

            $scope.ORGANIZATION_NAME_REGEX = /^[0-9A-Za-zА-ЯЄІЇҐ"'а-яєіїґ -]+$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
            $scope.PASSWORD_REGEX = /^(?=.{4,20}$).*/;
            $scope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
            $scope.FLAT_REGEX = /^([1-9]{1}[0-9]{0,3}|0)$/;
        }
    ]);