angular
    .module('welcomeModule')
    .controller('ChatController', ['$scope', '$modalInstance', '$log', 'response',
        function ($scope, $modalInstance, $log, response) {

            $scope.verificationData = response.data;

            $scope.close = function () {
                $modalInstance.close();
            };
        }]);
