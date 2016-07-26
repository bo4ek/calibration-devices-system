
angular
    .module('employeeModule')

    .controller('CapacityEmployeeControllerVerificator', ['$scope', '$log', '$modalInstance', 'capacity',
        function ($scope, $log, $modalInstance, capacity) {

            $scope.verifications = capacity.data.content;

            $scope.close = function () {
                $modalInstance.close();
            };


        }]);




