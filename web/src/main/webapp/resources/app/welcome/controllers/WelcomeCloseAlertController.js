angular
    .module('welcomeModule')
    .controller('WelcomeCloseAlertController', ['$scope', '$log', '$modalInstance',  '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function() {
                $rootScope.$broadcast('close-form');
                $modalInstance.close();
            };
        }]);