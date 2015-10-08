angular
    .module('employeeModule')
    .controller('EditProfileInfoController', ['$rootScope', '$scope', '$modalInstance', '$log', '$modal', '$timeout', '$state', '$http', 'UserService', 'user',

        function ($rootScope, $scope, $modalInstance, $log, $modal, $timeout, $state, $http, userService, user) {
            var organizationTypeProvider = false;
            var organizationTypeCalibrator = false;
            var organizationTypeVerificator = false;
            var employeeData = {};

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.employeeFormData = {};
            $scope.user = user;

            /**
             * Change password
             */
            $scope.changePassword = function () {
                $scope.user.password = 'generate';
                $scope.generationMessage = true;
            }

            /**
             * Refactor data
             */
            function retranslater() {
                employeeData = {
                    firstName: $scope.user.firstName,
                    lastName: $scope.user.lastName,
                    middleName: $scope.user.middleName,
                    phone: $scope.user.phone,
                    secondPhone: $scope.user.secondPhone,
                    email: $scope.user.email,
                    username: $scope.user.username,
                    password: $scope.user.password,

                    userRoles: [],
                    isAvaliable: true
                };
            }

                $scope.onEmployeeFormSubmit = function () {

                $scope.$broadcast('show-errors-check-validity');
                if ($scope.checkboxModel == false){
                	$scope.user.secondPhone = null;
                }
                retranslater();
                updateEmployee();
                $scope.incorrectValue = true;


            };

            /**
             * Update new employee in database.
             */
            function updateEmployee() {
                userService.updateUser(
                    employeeData).then(
                    function (data) {
                        if (data.status == 201) {
                            $rootScope.$broadcast('new-employee-added');
                            $scope.closeModal();
                            $modal.open({
                                animation: true,
                                templateUrl: '/resources/app/provider/views/modals/success-editing.html',
                                controller: function ($modalInstance) {
                                    this.ok = function () {
                                        $modalInstance.close();
                                    }
                                },
                                controllerAs: 'successController',
                                size: 'md'
                            });
                        } else {
                            alert('Error');
                        }
                    });
            };

            $scope.closeWindow = function () {
                $modalInstance.close();
            };

            /* Closes the modal window
             */
            $rootScope.closeModal = function () {
                $modalInstance.close();
            };
            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
            $scope.PNOHE_REGEX_MY = /^[1-9]\d{8}$/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;


        }]);
