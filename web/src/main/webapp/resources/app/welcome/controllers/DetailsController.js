angular
    .module('welcomeModule')
    .controller('DetailsController', ['$scope', '$modalInstance', '$log', 'response',
        function ($scope, $modalInstance, $log, response) {

            $scope.verificationData = response.data;

            $scope.close = function () {
                $modalInstance.close();
            };
        }]);
