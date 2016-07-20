angular
    .module('employeeModule')
    .controller('EditStampModalController', ['$scope', '$modalInstance', '$log', 'response', '$rootScope', 'VerificationServiceCalibrator', 'toaster', '$filter',
        function ($scope, $modalInstance, $log, response, $rootScope, verificationServiceCalibrator, toaster, $filter) {

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.verificationData = response.data;
            $scope.calibrationTestData = response;

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.submit = function () {
                verificationServiceCalibrator.sendEditedStamp($scope.verificationData).then(function (result) {
                    if (result.status == 200) {
                        toaster.pop('success', $filter('translate')('INFORMATION'),
                            $filter('translate')('SUCCESSFUL_EDITED'));
                    } else if (result.status == 403) {
                        toaster.pop('error', $filter('translate')('INFORMATION'),
                            $filter('translate')('ERROR_ACCESS'));
                    } else {
                        toaster.pop('error', $filter('translate')('INFORMATION'),
                            $filter('translate')('SAVE_VERIF_ERROR'));
                    }
                });
                $modalInstance.close();
            }
        }]);
