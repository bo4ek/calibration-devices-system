angular
    .module('adminModule')
    .controller('ProfileInfoController', ['$rootScope','$scope', '$http','UsersService', '$modal', function ($rootScope, $scope, $http, UsersService, $modal) {
        $scope.logout = function () {
            $http({
                method: 'POST',
                url: 'logout'
            }).then(function () {
                window.location.replace(".");
            });
        };

        $scope.user=null;
        UsersService.loggedInUser().
            then(function (data) {
                $scope.user = data;
            });


        $scope.openEditProfileModal = function() {
            $rootScope.checkboxModel = false;

            if ($scope.user.secondPhone != null) {
                $rootScope.checkboxModel = true;
            };

            var addEmployeeModal = $modal
                .open({
                    animation : true,
                    controller : 'EditProfileInfoController',
                    size: 'lg',
                    templateUrl : 'resources/app/admin/views/modals/profile-info-edit-modal.html',
                    resolve: {
                        user: function (){
                            return $scope.user;
                        }
                    }
                });
        };
    }]);