
angular
    .module('employeeModule')

    .controller('CapacityEmployeeControllerProvider', ['$scope', '$log', '$modalInstance', 'capacity',
        function ($scope, $log, $modalInstance, capacity) {

            $scope.verifications = capacity.data.content;

            $scope.close = function () {
                $modalInstance.close(); 
            };


        }]);



