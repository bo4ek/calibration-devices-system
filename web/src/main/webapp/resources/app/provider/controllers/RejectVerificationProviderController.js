angular
    .module('employeeModule')
    .controller('RejectVerificationProviderController', ['$scope', '$log', '$modalInstance', 'rejectVerification', '$rootScope', 'VerificationServiceProvider',
        function ($scope, $log, $modalInstance, rejectVerification) {

            $scope.reasons = rejectVerification.data;
            $scope.rejectedReason = {};
            $scope.rejectedReason.name = $scope.reasons[0];


            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.submit = function () {
                $scope.$broadcast('show-errors-check-validity');
                $modalInstance.close($scope.rejectedReason);
            }
        }]);
