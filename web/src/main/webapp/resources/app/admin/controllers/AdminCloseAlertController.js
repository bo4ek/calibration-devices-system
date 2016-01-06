angular
    .module('adminModule')
    .controller('AdminCloseAlertController', ['$scope', '$log', '$modalInstance',  '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function() {
                $rootScope.$broadcast('close-modal');
                $modalInstance.close();
            };
        }]);