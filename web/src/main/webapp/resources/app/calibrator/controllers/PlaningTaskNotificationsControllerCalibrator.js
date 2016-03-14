angular
    .module('employeeModule')
    .controller('PlaningTaskNotificationsControllerCalibrator', ['$scope', '$log', 'VerificationServiceCalibrator', '$interval', '$state', '$rootScope', '$timeout',
        function ($scope, $log, verificationServiceCalibrator, $interval, $state,  $rootScope, $timeout) {

            var promiseInterval;
            var promiseTimeOut;
            $scope.countOfTasks = 0;

            $scope.initializeCounter = function () {
                verificationServiceCalibrator.getCountOfPlanedTasks().success(function (count) {
                    $scope.countOfTasks = count;
                });
            };

            $scope.initializeCounter();

            $scope.reloadTasks = function() {
                promiseTimeOut = $timeout(function() {
                    $state.reload();
                }, 300);
            };

            $scope.startPolling = function(){
                $scope.stopPolling();
                if(angular.isDefined(promiseInterval)) return;
                promiseInterval = $interval(function () {
                    $scope.initializeCounter()
                }, 10000);
            };

            $scope.stopPolling = function() {
                $interval.cancel(promiseInterval);
            };

            $scope.startPolling();


            $rootScope.$on('verification-sent-to-station', function(){
                $scope.initializeCounter();
            });

            $rootScope.$on('verification-sent-to-team', function(){
                $scope.initializeCounter();
            });

            $scope.$on('$destroy', function () {
                $scope.stopPolling();
            });


        }]);