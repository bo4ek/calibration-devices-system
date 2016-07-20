angular
    .module('adminModule')
    .controller(
    'SysAdminDeleteModalController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$modal',
        '$timeout',
        'UsersService',
        function ($rootScope, $scope, $translate, $modalInstance, $modal, $timeout, userService) {

            $scope.submitDelete = function () {
                userService.deleteSysAdmin($rootScope.username)
                    .then(function(data) {
                        if(data == 200){
                            $timeout(function() {
                                  $modalInstance.close();
                                  $rootScope.onTableHandling();
                      }, 700);
                }
            });
            };

            $rootScope.cancel = function () {
                $modalInstance.dismiss();
            };
        }]);
