angular
    .module('adminModule')
    .controller(
    'CounterTypeAddController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        'devices',
        '$modalInstance',
        'CounterTypeService',
        function ($rootScope, $scope, $translate, devices, $modalInstance,
                  counterTypeService) {

            $scope.addCounterTypeFormData = {};
            $scope.currentTime = {};
            $scope.names = devices.data;
            /**
             * init all Standard Size of counter type for selector
             */
            $scope.standardSizes = ['DN 10','DN 15','DN 20','DN 25','DN 32',
                                    'DN 40','DN 50','DN 65','DN 80','DN 100',
                                    'DN 125','DN 150', 'DN 200', 'DN 250'];
            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Resets addCounterTypeForm form
             */
            $scope.resetAddCounterTypeForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.addCounterTypeForm.$setPristine();
                $scope.addCounterTypeForm.$setUntouched();
                $scope.addCounterTypeFormData = {};
            };

            /**
             * Set device category id
             * On-select handler in name input form element.
             */
            $scope.setDeviceId = function (selectedDevice) {
                $scope.addCounterTypeForm.name = selectedDevice.designation;
                $scope.addCounterTypeForm.deviceId = selectedDevice.id;
            };

            /**
             * Closes the modal window for adding new
             * counter type.
             */
            $rootScope.closeModal = function (close) {
                $scope.resetAddCounterTypeForm();
                if(close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            };

            /**
             * Check validation of form before saving and
             * init non-visible fields
             */
            $scope.onAddCounterTypeFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.addCounterTypeForm.$valid) {
                    $scope.addCounterTypeFormData.name = $scope.addCounterTypeForm.name;
                    $scope.addCounterTypeFormData.deviceId = $scope.addCounterTypeForm.deviceId;
                    saveCounterType();
                }
            };

            /**
             * Saves new counter type from the form in database.
             * If everything is ok then resets the countersType
             * form and updates table with counter types.
             */
            function saveCounterType() {

                counterTypeService.saveCounterType($scope.addCounterTypeFormData)
                    .then(function (data) {
                        if (data == 201) {
                            $scope.closeModal(true);
                            $scope.resetAddCounterTypeForm();
                            $rootScope.onTableHandling();
                        }
                    });
            }

            $scope.currentDate = new Date();
            $scope.currentYear = $scope.currentDate.getFullYear();

        }
    ]);