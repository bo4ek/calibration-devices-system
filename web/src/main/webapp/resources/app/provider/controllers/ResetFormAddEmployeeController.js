angular
    .module('employeeModule')
    .controller('ResetFormAddEmployeeController', ['$scope', '$log', '$modalInstance',  '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function() {
                $rootScope.$broadcast('submitReset');
                $modalInstance.close();
            };
        }]);
