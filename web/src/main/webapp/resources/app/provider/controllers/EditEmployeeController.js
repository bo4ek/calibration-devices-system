angular
    .module('employeeModule')
    .controller('EditEmployeeController', ['$rootScope', '$scope', '$modalInstance', '$log', '$modal',
        '$timeout', '$state', '$http', 'UserService', 'VerificatorSubdivisionService','toaster', '$filter', 'user',
        function ($rootScope, $scope, $modalInstance, $log, $modal, $timeout, $state, $http, userService, verificatorSubdivisionService, toaster, $filter, user) {

            $scope.user = user;
            if(user.secondPhone) {
                $scope.checkboxModel = true;
            }
            $scope.showRestore = user.isAvailable != true;
            $scope.selectedEmployee = [];
            $scope.selectedValues = {};
            $scope.selectedValues.subdivision = user.subdivision;

            var organizationTypeProvider = false;
            var organizationTypeCalibrator = false;
            var organizationTypeVerificator = false;
            var employeeData = {};

            for (var i = 0; i < user.userRoles.length; i++) {
                if (user.userRoles[i] === 'PROVIDER_EMPLOYEE') {
                    $scope.selectedEmployee.push('PROVIDER_EMPLOYEE');
                    organizationTypeProvider = true;
                }
                if (user.userRoles[i] === 'CALIBRATOR_EMPLOYEE') {
                    $scope.selectedEmployee.push('CALIBRATOR_EMPLOYEE');
                    organizationTypeCalibrator = true;
                }
                if (user.userRoles[i] === 'STATE_VERIFICATOR_EMPLOYEE') {
                    $scope.selectedEmployee.push('STATE_VERIFICATOR_EMPLOYEE');
                    organizationTypeVerificator = true;
                    $scope.showListOfSubdivisions = true;
                }
            }

            userService.isAdmin()
                .success(function (response) {
                    var thereIsAdmin = 0;
                    var roles = response + '';
                    var role = roles.split(',');
                    for (var i = 0; i < role.length; i++) {
                        if (role[i] === 'PROVIDER_ADMIN' || role[i] === 'CALIBRATOR_ADMIN' || role[i] === 'STATE_VERIFICATOR_ADMIN')
                            thereIsAdmin++;
                    }
                    if (thereIsAdmin === 0) {
                        $scope.accessLable = true;
                    } else {
                        $scope.verificator = true;
                    }
                    if (thereIsAdmin === 1) {
                        if (role[0] === 'PROVIDER_ADMIN')
                            organizationTypeProvider = true;
                        if (role[0] === 'CALIBRATOR_ADMIN')
                            organizationTypeCalibrator = true;
                        if (role[0] === 'STATE_VERIFICATOR_ADMIN')
                            organizationTypeVerificator = true;
                    }
                    if (thereIsAdmin > 1) {
                        $scope.showListOfOrganization = true;
                        for (var i = 0; i < role.length; i++) {
                            if ((role[0] === 'PROVIDER_ADMIN' && role[1] === 'CALIBRATOR_ADMIN') ||
                                (role[0] === 'CALIBRATOR_ADMIN' && role[1] === 'PROVIDER_ADMIN'))
                                $scope.showListOfOrganizationChosenOne = true;
                            if ((role[0] === 'STATE_VERIFICATOR_ADMIN' && role[1] === 'CALIBRATOR_ADMIN') ||
                                (role[0] === 'CALIBRATOR_ADMIN' && role[1] === 'STATE_VERIFICATOR_ADMIN'))
                                $scope.showListOfOrganizationChosenTwo = true;
                        }
                    }
                });

            /**
             * Choose role of employee
             * @param selectedEmployee
             */
            $scope.choose = function (selectedEmployee) {
                organizationTypeProvider = false;
                organizationTypeCalibrator = false;
                organizationTypeVerificator = false;
                var employee = selectedEmployee + '';
                var resultEmployee = employee.split(',');
                for (var i = 0; i < resultEmployee.length; i++) {
                    if (resultEmployee[i] === 'PROVIDER_EMPLOYEE') {
                        organizationTypeProvider = true;
                    }
                    if (resultEmployee[i] === 'CALIBRATOR_EMPLOYEE') {
                        organizationTypeCalibrator = true;
                    }
                    if (resultEmployee[i] === 'STATE_VERIFICATOR_EMPLOYEE') {
                        organizationTypeVerificator = true
                    }
                }
            };

            /**
             * Finds all subdivisions
             */
            verificatorSubdivisionService.getAllSubdivisions()
                .then(function (subdivisions) {
                    $scope.subdivisions = subdivisions;
                });

            $scope.selectedEmployee = [];

            /**
             * Change password
             */
            $scope.changePassword = function () {
                $scope.user.password = 'generate';
                $scope.generationMessage = true;
            };

            /**
             * Refactor data
             */
            function retranslater() {
                employeeData = {
                    subdivision: $scope.selectedValues.subdivision,
                    firstName: $scope.user.firstName,
                    lastName: $scope.user.lastName,
                    middleName: $scope.user.middleName,
                    phone: $scope.user.phone,
                    secondPhone: $scope.user.secondPhone,
                    email: $scope.user.email,
                    username: $scope.user.username,
                    password: $scope.user.password,
                    userRoles: [],
                    isAvailable: true
                };

                if (organizationTypeProvider === true) {
                    employeeData.userRoles.push('PROVIDER_EMPLOYEE');
                }
                if (organizationTypeCalibrator === true) {
                    employeeData.userRoles.push('CALIBRATOR_EMPLOYEE');
                }
                if (organizationTypeVerificator === true) {
                    employeeData.userRoles.push('STATE_VERIFICATOR_EMPLOYEE');
                }
            }

            /*
             Fire employee
             */
            $scope.fireEmployee = function (action) {
                if (action === 'fire') {
                    updateEmployee();
                    $scope.showRestore = true;
                    employeeData.isAvailable = false;
                } else {
                    $scope.showRestore = false;
            }
            };

            $scope.onEmployeeFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.checkboxModel == false) {
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
                userService.updateUser(employeeData)
                    .then(function (data) {
                        if (data.status == 201) {
                            $rootScope.$broadcast('new-employee-added');
                            $scope.closeModal();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_EDIT_EMPLOYEE'));
                        } else {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_EDIT_EMPLOYEE'));
                        }
                    });
            }

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /* Closes the modal window
             */
            $rootScope.closeModal = function () {
                $modalInstance.close();
            };

            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;

        }]);