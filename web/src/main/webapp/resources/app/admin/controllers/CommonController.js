angular
    .module('adminModule')
    .controller('CommonController', ['$rootScope', '$scope', 'RoleService',
        function ($rootScope, $scope, roleService) {
                roleService.isSuperAdmin().then(function (result) {
                    $rootScope.isSuperAdmin = result.data;
                });
    }]);
