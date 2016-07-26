angular
    .module('employeeModule')
    .controller('EmployeeControllerCalibrator', ['$scope', '$log', '$modal', '$state', 'UserServiceCalibrator',

        function ($scope, $log, $modal, $state, userServiceCalibrator) {
    	
		userServiceCalibrator.isAdmin()
			.success(function (response) {
				if (response === 'CALIBRATOR_ADMIN'){
					$scope.verificator = true;
				}else{
					$scope.accessLable = true;	
				}
			});

            $scope.employeeData = {};
            $scope.form = {};

            $scope.openAddressModal = function () {
                var addressModal = $modal.open({
                    animation: true,
                    controller: 'AddressModalController',
                    templateUrl: 'resources/app/calibrator/views/modals/address.html',
                    size: 'lg',
                    resolve: {
                        address: function () {
                            return $scope.address;
                        }
                    }
                });

                addressModal.result.then(function (address) {
                    $scope.address = address;
                    $scope.addressMessage = null;

                    if (address) {
                        $scope.addressMessage =
                            address.selectedRegion.designation + " область, " +
                            address.selectedDistrict.designation + " район, " +
                            address.selectedLocality.designation + ", " +
                            address.selectedStreet.designation + " " +
                            (address.selectedBuilding.designation || address.selectedBuilding) + " " +
                            (address.selectedFlat || "");
                    }
                });
            };

            $scope.checkUsername = function (username) {

                userServiceCalibrator
                    .isUsernameAvailable(username)
                    .success(function (result) {
                        $scope.form.employee.username.$setValidity("isAvailable", result);
                    })
            };

            $scope.checkPasswords = function () {
                var first = $scope.employeeData.password;
                var second = $scope.form.rePassword;
                if (first && second) {
                    var isMatch = first === second;
                    $scope.form.employee.password.$setValidity("isMatch", isMatch);
                    $scope.form.employee.rePassword.$setValidity("isMatch", isMatch);
                }
            };

            $scope.resetForm = function () {
                $state.go($state.current, {}, {reload: true});
            };

            $scope.addEmployee = function () {
                $scope.$broadcast('show-errors-check-validity');

                if ($scope.form.employee.$valid) {

                    var employeeData = $scope.employeeData;
                    var address = $scope.address;

                    employeeData.address = {
                        region: address.selectedRegion.designation,
                        district: address.selectedDistrict.designation,
                        locality: address.selectedLocality.designation,
                        street: address.selectedStreet.designation,
                        building: address.selectedBuilding.designation || address.selectedBuilding,
                        flat: address.selectedFlat
                    };

                    userServiceCalibrator.saveUser(employeeData)
                        .success(function (response) {
                            $modal.open({
                                animation: true,
                                templateUrl: 'resources/app/calibrator/views/modals/employee-adding-success.html',
                                controller: function ($modalInstance) {
                                    this.ok = function () {
                                        $modalInstance.close();
                                    }
                                },
                                controllerAs: 'successController',
                                size: 'md'
                            });

                            $scope.resetForm();
                        });
                }
            };
        }]);
