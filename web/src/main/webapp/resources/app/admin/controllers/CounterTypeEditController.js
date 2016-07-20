angular
    .module('adminModule')
    .controller(
    'CounterTypeEditController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$timeout',
        'CounterTypeService',
        'devices',
        function ($rootScope, $scope, $translate, $modalInstance, $timeout,
                  counterTypeService, devices) {

            $scope.names = devices.data;
            /**
             * init all Standard Size of counters type for selector
             */
            $scope.standardSizes = ['DN 10','DN 15','DN 20','DN 25','DN 32',
                'DN 40','DN 50','DN 65','DN 80','DN 100',
                'DN 125','DN 150', 'DN 200', 'DN 250'];

            /**
             * init fields for none visible fileds of form
             * and insert them into select
             */
            var index = arrayObjectIndexOf($scope.names, $rootScope.countersType.name, $rootScope.countersType.typeWater, "designation", "deviceType");
            $scope.deviceCategoryName = $scope.names[index];
            $scope.deviceId = $scope.names[index].id;

            function arrayObjectIndexOf(myArray, name, typeWater, firstProperty, secondPropery) {
                for (var i = 0, len = myArray.length; i < len; i++) {
                    if (myArray[i][firstProperty] === name && myArray[i][secondPropery] == typeWater) {
                        return i;
                    }
                }
                var elem = {
                    id: length,
                    designation: name
                };
                myArray.push(elem);
                return (myArray.length - 1);
            }



            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Closes the modal window for adding new
             * counters type
             */
            $rootScope.closeModal = function (close) {
                if(close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            };

            /**
             * Save non visible field device category id
             * when we choose name of category
             */
            $scope.setDeviceId = function (selectedDevice) {
                $scope.deviceId = selectedDevice.id;
            };

            /**
             * Check validation of form before saving and
             * init saving form
             */
            $scope.onEditCounterTypeFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.editCounterTypeForm.$valid) {
                    var counterTypeForm = {
                        name: $scope.deviceCategoryName.designation,
                        symbol: $rootScope.countersType.symbol,
                        standardSize: $rootScope.countersType.standardSize,
                        manufacturer: $rootScope.countersType.manufacturer,
                        calibrationInterval: $rootScope.countersType.calibrationInterval,
                        yearIntroduction: $rootScope.countersType.yearIntroduction,
                        gost: $rootScope.countersType.gost,
                        deviceId: $scope.deviceId
                    };
                    saveCounterType(counterTypeForm);
                }
            };

            /**
             * Saves new counter type from the form in database.
             * If everything is ok then resets the countersType
             * form and updates table with counter types.
             */
            function saveCounterType(counterTypeForm) {
                counterTypeService.editCounterType(
                    counterTypeForm,
                    $rootScope.countersType.id).then(
                    function (data) {
                        if (data == 200) {
                            $scope.closeModal(true);
                            $rootScope.onTableHandling();
                        }
                    });
            }

            $scope.currentDate = new Date();
            $scope.currentYear = $scope.currentDate.getFullYear();

        }
    ]);