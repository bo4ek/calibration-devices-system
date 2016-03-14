angular
    .module('adminModule')
    .controller(
        'StreetAddModalControllerAdmin',
        [
            '$rootScope',
            '$scope',
            '$translate',
            '$modalInstance',
            '$filter',
            'street',
            'AddressService',
            'toaster',
            function ($rootScope, $scope, $translate, $modalInstance, $filter, street, addressService, toaster) {

                $scope.selectedValues = {};

                /**
                 * Closes modal window on browser's back/forward button click.
                 */
                $rootScope.$on('$locationChangeStart', function () {
                    $modalInstance.close();
                });

                $scope.resetForm = function () {
                    $scope.$broadcast('show-errors-reset');
                    $scope.addStreetForm.$setPristine();
                    $scope.addStreetForm.$setUntouched();
                    $scope.addStreetFormData = {};
                    $scope.selectedValues.selectedRegion = undefined;
                    $scope.selectedValues.selectedDistrict = undefined;
                    $scope.selectedValues.selectedLocality = undefined;
                };

                /**
                 * Closes the modal window
                 */
                $rootScope.closeModal = function (close) {
                    $rootScope.editMode = false;
                    $modalInstance.close();
                };

                /**
                 * Receives all possible districts.
                 * On-select handler in region input form element.
                 */
                $scope.receiveDistricts = function (selectedRegion) {
                    $scope.districts = [];
                    addressService.findDistrictsByRegionId(selectedRegion.id).then(function (districts) {
                        $scope.districts = districts;
                        $scope.selectedValues.selectedDistrict = undefined;
                        $scope.selectedValues.selectedLocality = undefined;
                    });
                };

                $scope.receiveRegions = function () {
                    $scope.regions = [];
                    addressService.findAllRegions().then(function (regions) {
                            $scope.regions = regions;
                            $scope.selectedValues.selectedRegion = undefined; //for ui-selects
                            $scope.selectedValues.selectedDistrict = undefined;
                            $scope.selectedValues.selectedLocality = undefined;
                        }
                    );
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
                                $scope.selectedValues.selectedLocality = undefined;
                            }
                        );
                    }
                };

                if (!street) {
                    $scope.receiveRegions();
                }

                $scope.sendStreetData = function () {
                    $scope.$broadcast('show-errors-check-validity');
                    if ($scope.addStreetForm.$valid) {
                        $scope.addStreetFormData.localityId = $scope.selectedValues.selectedLocality.id;
                        addressService.addNewStreet($scope.addStreetFormData)
                            .then(function (response) {
                                if (response.status == 201) {
                                    toaster.pop('success', $filter('translate')('INFORMATION'),
                                        $filter('translate')('STREET_SUCCESSFULLY_ADDED'));
                                    $scope.closeModal(true);
                                }
                            });
                    }
                };

                $scope.editStreet = function () {
                    $scope.$broadcast('show-errors-check-validity');
                    if ($scope.addStreetForm.$valid) {
                        $scope.addStreetFormData.localityId = $scope.selectedValues.selectedLocality.id;
                        addressService.deleteStreet($rootScope.editingStreet.id).then(function (response) {
                            if (response.status == 200) {
                                addressService.addNewStreet($scope.addStreetFormData)
                                    .then(function (response) {
                                        if (response.status == 201) {
                                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                                $filter('translate')('SUCCESSFUL_EDITED_STREET'));
                                            $scope.closeModal(true);
                                        }
                                    });
                            }
                        });
                    }
                };


                $scope.isStreetIdDuplicate = function () {
                    return addressService.isStreetIdDuplicate($scope.addStreetFormData.streetId);
                };

                $scope.isStreetNameDuplicate = function () {
                    return addressService.isStreetNameDuplicate($scope.addStreetFormData.streetName, $scope.selectedValues.selectedLocality.id);
                };

                $scope.checkStreetIdForDuplicates = function () {
                    if (!$scope.addStreetFormData.streetName || $scope.addStreetFormData.streetId != $rootScope.editingStreet.id) {
                        $scope.isStreetIdDuplicate().then(function (isDuplicate) {
                                $scope.addStreetForm.streetId.$setValidity("duplicate", !isDuplicate.data);
                            }
                        )
                    }
                };

                $scope.checkStreetNameForDuplicates = function () {
                    if ($scope.addStreetFormData.streetName || $scope.addStreetFormData.streetName != $rootScope.editingStreet.streetName) {
                        $scope.isStreetNameDuplicate().then(function (isDuplicate) {
                                $scope.addStreetForm.streetName.$setValidity("duplicate", !isDuplicate.data);
                            }
                        )
                    }
                };

                $scope.hideStreetIdDuplicateError = function () {
                    if ($scope.addStreetForm.streetId.$error.duplicate) {
                        $scope.addStreetForm.streetId.$setValidity("duplicate", true);
                        $scope.$broadcast('show-errors-check-validity');
                    }
                };

                $scope.hideStreetNameDuplicateError = function () {
                    if ($scope.addStreetForm.streetName.$error.duplicate) {
                        $scope.addStreetForm.streetName.$setValidity("duplicate", true);
                        $scope.$broadcast('show-errors-check-validity');
                    }
                };
            }
        ]);