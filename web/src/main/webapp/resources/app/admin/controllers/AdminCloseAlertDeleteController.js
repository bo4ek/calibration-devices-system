angular
    .module('adminModule')
    .controller('AdminCloseAlertDeleteController', ['$scope', '$log', '$modalInstance',  '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.submit = function() {
                $rootScope.$broadcast('delete');
                $modalInstance.close();
            };
        }]);