angular
    .module('adminModule')
    .controller('AdminResetAlertController', ['$scope', '$log', '$modalInstance',  '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function() {
                $rootScope.$broadcast('reset-form');
                $modalInstance.close();
            };
        }]);