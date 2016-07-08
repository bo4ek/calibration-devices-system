angular
    .module('employeeModule')
    .controller('ChangeStationOrTeamModalController', ['$scope', '$log', '$modalInstance', 'modules', '$rootScope', 'VerificationServiceCalibrator',
        function ($scope, $log, $modalInstance, modules, $rootScope, verificationServiceCalibrator) {

            $scope.modules = modules.data;
            $scope.module = {};
            $scope.module = $scope.modules[0];
            $scope.getSerialNumber = null;

            $scope.access = false;
            $scope.showMessege = false;

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.submit = function () {
                $modalInstance.close($scope.getSerialNumber);
            };

            $scope.checkStation = function (serialNumber) {
                var dto = {};
                dto.dateOfTask = $rootScope.dateOfTask;
                dto.moduleSerialNumber = serialNumber;
                verificationServiceCalibrator.checkStationByDateOfTask(dto).then(function (data) {
                    switch (data.status) {
                        case 200:
                        {
                            $scope.showMessege = true;
                            $scope.access = true;
                            $scope.getSerialNumber = serialNumber;
                            break;
                        }
                        case 201:
                        {
                            $scope.showMessege = false;
                            $scope.access = true;
                            $scope.getSerialNumber = serialNumber;
                            break;
                        }
                        default:
                        {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('SAVE_VERIF_ERROR'));
                        }
                    }
                    dto = {};
                });
            };
        }]);