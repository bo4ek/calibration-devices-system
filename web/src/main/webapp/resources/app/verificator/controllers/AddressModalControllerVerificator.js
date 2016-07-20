angular
    .module('employeeModule')
    .controller('AddressModalControllerVerificator', ['$scope', '$log', '$modalInstance', 'address', 'AddressServiceVerificator',
        function ($scope, $log, $modalInstance, address, AddressServiceVerificator) {
            $log.info(address);

            $scope.addressData = {};

            $scope.regions = [];
            AddressServiceVerificator.findAllRegions()
                .success(function (regions) {
                    $scope.regions = regions;
                });
            /**
             * Receives all possible districts.
             * On-select handler in region input form element.
             */
            $scope.receiveDistricts = function (selectedRegion) {
                $scope.districts = [];
                AddressServiceVerificator.findDistrictsByRegionId(selectedRegion.id)
                    .success(function (districts) {
                        $scope.districts = districts;
                    });
                if (!address) {
                    $scope.addressData.selectedDistrict = "";
                    $scope.addressData.selectedLocality = "";
                    $scope.addressData.selectedStreet = "";
                }
            };
            /**
             * Receives all possible localities.
             * On-select handler in district input form element.
             */
            $scope.receiveLocalitiesAndVerificators = function (selectedDistrict) {
                $scope.localities = [];
                AddressServiceVerificator.findLocalitiesByDistrictId(selectedDistrict.id)
                    .success(function (localities) {
                        $scope.localities = localities;
                    });
                if (!address) {
                    $scope.addressData.selectedLocality = "";
                    $scope.addressData.selectedStreet = "";
                }
            };
            /**
             * Receives all possible streets.
             * On-select handler in locality input form element
             */
            $scope.receiveStreets = function (selectedLocality) {
                $scope.streets = [];
                AddressServiceVerificator.findStreetsByLocalityId(selectedLocality.id)
                    .success(function (streets) {
                        $scope.streets = streets;
                    });
                if (!address) {
                    $scope.addressData.selectedStreet = "";
                }
            };
            /**
             * Receives all possible buildings.
             * On-select handler in street input form element.
             */
            $scope.receiveBuildings = function (selectedStreet) {
                $scope.buildings = [];
                AddressServiceVerificator.findBuildingsByStreetId(selectedStreet.id)
                    .success(function (buildings) {
                        $scope.buildings = buildings;
                    });
            };

            if (address) {
                $scope.addressData = address;
                $scope.receiveDistricts(address.selectedRegion);
                $scope.receiveLocalitiesAndVerificators(address.selectedDistrict);
                $scope.receiveStreets(address.selectedLocality);
                $scope.receiveBuildings(address.selectedStreet);
                address = null;
            }

            $scope.cancel = function () {
                $modalInstance.close();
            };
            $scope.submitAddress = function () {
                $scope.$broadcast('show-errors-check-validity');

                if ($scope.addressForm.$valid) {
                    $modalInstance.close($scope.addressData);
                }
            }
        }]);
