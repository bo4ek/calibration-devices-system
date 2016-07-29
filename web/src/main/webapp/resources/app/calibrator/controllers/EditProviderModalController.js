angular
    .module('employeeModule')
    .controller('EditProviderModalController', ['$scope', '$modalInstance', '$log', 'response', '$rootScope', 'VerificationServiceCalibrator', 'toaster', '$filter',
        'DataReceivingServiceCalibrator',
        function ($scope, $modalInstance, $log, response, $rootScope, verificationServiceCalibrator, toaster, $filter, dataReceivingService) {

            $scope.resultData = {};

            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            dataReceivingService.findAllDeviceTypes()
                .success(function (deviceTypes) {
                    $scope.deviceTypes = deviceTypes;
                });

            /**
             * select device by deviceType (isn't very usefull. only not to broke another functionality)
             * this method gets all devices and set wrong deviceId, right deviceId chooses on server side
             */
            $scope.selectDevice = function () {
                angular.forEach($scope.devices, function (value) {
                    if (value.deviceType === $scope.selectedData.firstSelectedDeviceType) {
                        $scope.selectedData.firstSelectedDevice = value;
                        $scope.formData.deviceType = value.deviceType;
                    }
                });
            };
            
            $scope.receiveProviders = function (deviceType) {
                $scope.resultData.deviceType = deviceType;
                $scope.selectedData = {};
                dataReceivingService.findProvidersForCalibratorByType(deviceType)
                    .success(function (providers) {
                        $scope.firstDeviceProviders = providers;
                        if ($scope.isProvider > 0) {
                            var index = arrayObjectIndexOf($scope.firstDeviceProviders, $scope.isProvider, "id");
                            $scope.selectedData.firstSelectedProvider = $scope.firstDeviceProviders[index];
                        }
                    });
            };

            $scope.setResultProvider = function (provider) {
                $scope.resultData.provider = provider;
            }

            $scope.close = function () {
                $scope.resultData = {};
                $modalInstance.dismiss();
            };

            $scope.submit = function () {
                if ($scope.selectedData.firstSelectedProvider) {
                    $modalInstance.close($scope.selectedData.firstSelectedProvider);
                }
            };
        }]);
