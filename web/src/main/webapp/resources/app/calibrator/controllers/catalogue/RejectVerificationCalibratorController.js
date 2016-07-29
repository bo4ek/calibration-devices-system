angular
    .module('employeeModule')
    .controller('RejectVerificationCalibratorController', ['$scope', '$log', '$modalInstance', 'rejectVerification', '$rootScope', 'VerificationServiceCalibrator',
        function ($scope, $log, $modalInstance, rejectVerification, verificationServiceCalibrator, $rootScope) {

            $scope.reasons = rejectVerification.data;
            $scope.rejectedReason = {};
            $scope.rejectedReason.name = $scope.reasons[0];


            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.submit = function () {
                $modalInstance.close($scope.rejectedReason);
            }
        }]);