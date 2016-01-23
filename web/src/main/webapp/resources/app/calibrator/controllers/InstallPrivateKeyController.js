/**
 * Created by Lidiya on 23.01.2016.
 */
angular
    .module('employeeModule')
    .controller('InstallPrivateKeyController', ['$scope', '$rootScope', '$route', '$log', '$modalInstance',
        '$timeout', 'CalibrationTestServiceCalibrator', 'parentScope', '$translate', 'DataReceivingServiceCalibrator',
        function ($scope, $rootScope, $route, $log, $modalInstance, $timeout, calibrationTestServiceCalibrator, parentScope, $translate, dataReceivingService) {

            $scope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.cancel = function () {
                $modalInstance.dismiss("cancel");
            };

            $scope.parentScope = parentScope;

            $scope.privateKey = null;
            $scope.privateKeyPassword = null;

            $scope.readPrivateKeyButtonClick = function () {

            };

        }
    ])
;
