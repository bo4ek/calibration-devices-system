angular
    .module('adminModule')
    .controller('SysAdminEditModalController', ['$rootScope', '$scope', '$filter', '$modal', '$modalInstance', '$log', '$timeout', 'UsersService',
        'AddressService',
        'toaster','regions',
        function ( $rootScope, $scope, $filter, $modal, $modalInstance, $log, $timeout, userService, addressService, toaster, regions) {
            $scope.regions = regions;
            $scope.districts = [];
            $scope.localities = [];
            $scope.streets = [];
            $scope.buildings = [];

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

            if ($rootScope.sysAdmin) {
                $scope.blockSearchFunctions = true;
                addressService.findAllRegions().then(function (respRegions) {
                    $scope.regions = respRegions;
                    var index = arrayObjectIndexOf($scope.regions, $rootScope.sysAdmin.region, "designation");
                    $scope.selectedValues.selectedRegion = $scope.regions[index];

                    addressService.findDistrictsByRegionId($scope.selectedValues.selectedRegion.id)
                        .then(function (districts) {
                            $scope.districts = districts;

                            var index = arrayObjectIndexOf($scope.districts, $rootScope.sysAdmin.district, "designation");
                            $scope.selectedValues.selectedDistrict = $scope.districts[index];

                            addressService.findLocalitiesByDistrictId($scope.selectedValues.selectedDistrict.id)
                                .then(function (localities) {
                                    $scope.localities = localities;
                                    var index = arrayObjectIndexOf($scope.localities, $rootScope.sysAdmin.locality, "designation");
                                    $scope.selectedValues.selectedLocality = $scope.localities[index];

                                    addressService.findStreetsByLocalityId($scope.selectedValues.selectedLocality.id)
                                        .then(function (streets) {
                                            $scope.streets = streets;
                                            var index = arrayObjectIndexOf($scope.streets, $rootScope.sysAdmin.street, "designation");
                                            $scope.selectedValues.selectedStreet = $scope.streets[index];
                                            $scope.blockSearchFunctions = false;

                                            addressService.findBuildingsByStreetId($scope.selectedValues.selectedStreet.id)
                                                .then(function (buildings) {
                                                    $scope.buildings = buildings;
                                                    var index = arrayObjectIndexOf($scope.buildings, $rootScope.sysAdmin.building, "designation");
                                                    $scope.selectedValues.selectedBuilding = $scope.buildings[index].designation;

                                                });
                                        });
                                });
                        });
                });

                /**
                 * Finds all regions
                 */
                function initFormData() {
                    if (!$scope.regions) {
                        addressService.findAllRegions().then(
                            function (data) {
                                $scope.regions = data;
                            });
                    }
                }

                initFormData();

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
                 * Resets organization form
                 */
                $scope.resetSysAdminForm = function () {
                    $scope.$broadcast('show-errors-reset');
                    $rootScope.sysAdmin = null;
                };


                /**
                 * Change password
                 */
                $scope.changePassword = function () {
                    //$scope.preventDefault();
                    $scope.password = 'generate';
                    $scope.generationMessage = true;
                };


                function addressFormToSysAdminForm() {
                    if (typeof $rootScope.sysAdmin.region == 'object') {
                        $rootScope.sysAdmin.region = $rootScope.sysAdmin.region.designation;
                    }
                    if (typeof $rootScope.sysAdmin.district == 'object') {
                        $rootScope.sysAdmin.district = $rootScope.sysAdmin.district.designation;
                    }
                    if (typeof $rootScope.sysAdmin.locality == 'object') {
                        $rootScope.sysAdmin.locality = $rootScope.sysAdmin.locality.designation;
                    }
                    if (typeof $rootScope.sysAdmin.street == 'object') {
                        $rootScope.sysAdmin.street = $rootScope.sysAdmin.street.designation;
                    }
                    if (typeof $rootScope.sysAdmin.building == 'object') {
                        $rootScope.sysAdmin.building = $rootScope.sysAdmin.building.designation;
                    }
                }

                $scope.closeModal = function () {
                    $rootScope.onTableHandling();
                    $modalInstance.dismiss();
                };


                $scope.editSysAdmin = function () {
                    $scope.$broadcast('show-errors-check-validity');
                    addressFormToSysAdminForm();
                    var address = {
                        region: $scope.selectedValues.selectedRegion.designation,
                        locality: $scope.selectedValues.selectedLocality.designation,
                        district: $scope.selectedValues.selectedDistrict.designation,
                        street: $scope.selectedValues.selectedStreet.designation || $scope.selectedValues.selectedStreet,
                        building: $rootScope.sysAdmin.building,
                        flat: $rootScope.sysAdmin.flat
                    }
                    var sysAdminForm = {
                        name: $rootScope.sysAdmin.name,
                        email: $rootScope.sysAdmin.email,
                        phone: $rootScope.sysAdmin.phone,
                        address : address,
                        firstName: $rootScope.sysAdmin.firstName,
                        lastName: $rootScope.sysAdmin.lastName,
                        middleName: $rootScope.sysAdmin.middleName,
                        password: $scope.password
                    };

                    saveSysAdmin(sysAdminForm);
                    $modalInstance.close();
                    $rootScope.onTableHandling();
                };


                function saveSysAdmin(sysAdminForm) {
                    userService.editSysAdmin(
                        sysAdminForm,
                        $rootScope.sysAdmin.username).then(
                        function (data) {
                            if (data == 200) {
                                $scope.closeModal();
                                $scope.resetSysAdminForm();
                                console.log(data);
                                $rootScope.onTableHandling();
                            }
                            else (console.log(data));
                        })
                }

                $scope.popNotification = function (title, text) {
                    toaster.pop('success', title, text);
                };
            }
        }]);

