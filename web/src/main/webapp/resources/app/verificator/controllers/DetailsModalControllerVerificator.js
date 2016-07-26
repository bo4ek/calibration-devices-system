angular
    .module('employeeModule')
    .controller('DetailsModalControllerVerificator', ['$scope', '$modalInstance', '$log', 'response',
        function ($scope, $modalInstance, $log, response) {

            $scope.verificationData = response.data;

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.rejectVerification = function () {
                $rootScope.$broadcast("verification_rejected", { verifID: $rootScope.verificationID });
                $modalInstance.close();
            };
        }]);