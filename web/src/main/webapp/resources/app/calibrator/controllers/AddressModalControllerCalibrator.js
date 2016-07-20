angular
    .module('employeeModule')
    .controller('AddressModalControllerCalibrator', ['$scope', '$log', '$modalInstance', 'address', 'AddressServiceCalibrator',
        function ($scope, $log, $modalInstance, address, addressServiceCalibrator) {
            $log.info(address);

            $scope.addressData = {};

            $scope.regions = [];
            addressServiceCalibrator.findAllRegions()
                .success(function (regions) {
                    $scope.regions = regions;
                });
            /**
             * Receives all possible districts.
             * On-select handler in region input form element.
             */
            $scope.receiveDistricts = function (selectedRegion) {
                $scope.districts = [];
                addressServiceCalibrator.findDistrictsByRegionId(selectedRegion.id)
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
            $scope.receiveLocalitiesAndCalibrators = function (selectedDistrict) {
                $scope.localities = [];
                addressServiceCalibrator.findLocalitiesByDistrictId(selectedDistrict.id)
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
                addressServiceCalibrator.findStreetsByLocalityId(selectedLocality.id)
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
                addressServiceCalibrator.findBuildingsByStreetId(selectedStreet.id)
                    .success(function (buildings) {
                        $scope.buildings = buildings;
                    });
            };

            if (address) {
                $scope.addressData = address;
                $scope.receiveDistricts(address.selectedRegion);
                $scope.receiveLocalitiesAndCalibrators(address.selectedDistrict);
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
