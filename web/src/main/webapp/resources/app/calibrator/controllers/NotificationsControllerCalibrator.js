angular
    .module('employeeModule')
    .controller('NotificationsControllerCalibrator', ['$scope', '$log', 'VerificationServiceCalibrator', '$interval', '$state', '$rootScope', '$timeout',
        function ($scope, $log, verificationServiceCalibrator, $interval, $state, $rootScope, $timeout) {

            var promiseInterval;
            var promiseTimeOut;
            $scope.countOfUnreadVerifications = 0;

            $scope.initializeCounter = function () {
                verificationServiceCalibrator.getCountOfNewVerifications().success(function (count) {
                    $scope.countOfUnreadVerifications = count;
                });
            };

            $scope.initializeCounter();

            $scope.reloadVerifications = function () {
                promiseTimeOut = $timeout(function () {
                    $state.reload();
                }, 300);
            };
            $scope.startPolling = function () {
                $scope.stopPolling();
                if (angular.isDefined(promiseInterval)) return;
                promiseInterval = $interval(function () {
                    verificationServiceCalibrator.getCountOfNewVerifications().success(function (count) {
                        $scope.countOfUnreadVerifications = count;
                    })
                }, 10000);
            };

            $scope.stopPolling = function () {
                $interval.cancel(promiseInterval);
            };

            $scope.startPolling();


            $rootScope.$on('verification-sent-to-verifikator', function () {
                verificationServiceCalibrator.getCountOfNewVerifications().success(function (count) {
                    $scope.countOfUnreadVerifications = count;
                });
            });

            $rootScope.$on('verification-was-read', function () {
                verificationServiceCalibrator.getCountOfNewVerifications().success(function (count) {
                    $scope.countOfUnreadVerifications = count;
                });
            });

            $rootScope.$on('test-is-created', function (event, args) {
                verificationServiceCalibrator.getCountOfNewVerifications().success(function (count) {
                    $scope.countOfUnreadVerifications = count;
                });
            });

            $scope.$on('$destroy', function () {
                $scope.stopPolling();
            });


        }]);