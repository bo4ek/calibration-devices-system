angular
    .module('employeeModule')
    .controller('SubmitCancelControllerCalibrator', ['$scope', '$log', '$modalInstance', '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function () {
                $modalInstance.close();
            };
        }]);