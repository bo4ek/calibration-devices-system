angular
    .module('employeeModule')
    .controller('AddEmployeeController', ['$rootScope', '$scope', '$modalInstance','$modal',
        '$timeout', '$log', '$state', '$http', 'UserService', 'AddressServiceProvider', 'toaster', '$filter',

        function ($rootScope, $scope, $modalInstance,$modal, $timeout, $log, $state, $http,
                  userService, addressServiceProvider, toaster, $filter) {
            var organizationTypeProvider = false;
            var organizationTypeCalibrator = false;
            var organizationTypeVerificator = false;
            var employeeData = {};

            /**
             * Closes modal window on browser's back/forward button click.
             */

            $rootScope.$on('$locationChangeStart', function() {
                $modalInstance.close();
            });

            userService.isAdmin()
                .success(function (response) {
                    var includeCheckBox = false;
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
                                $scope.showListOfOrganizationChouenTwo = true;
                        }
                    }
                });

            /**
             * Choose role of employee
             * @param selectedEmployee
             */
            $scope.choose = function (selectedEmployee) {
                var employee = selectedEmployee + '';
                var resaultEmployee = employee.split(',');
                for (var i = 0; i < resaultEmployee.length; i++) {
                    if (resaultEmployee[i] === 'provider') {
                        organizationTypeProvider = true;
                    }
                    if (resaultEmployee[i] === 'calibrator') {
                        organizationTypeCalibrator = true;
                    }
                    if (resaultEmployee[i] === 'verificatot') {
                        organizationTypeVerificator = true
                    }
                }
            }

            $scope.regions = null;
            $scope.districts = [];
            $scope.localities = [];
            $scope.streets = [];
            $scope.buildings = [];

            /**
             * Resets employee form
             */

            $rootScope.resetEmployeeForm = function() {
                $modal.open({
                    animation : true,
                    templateUrl: 'resources/app/common/views/modals/reset-alert.html',
                    controller : 'ResetFormAddEmployeeController'
                })
            };

            $scope.$on('submitReset', function(event, args) {
                $scope.submitResetEmployeeForm();
            });


            $scope.submitResetEmployeeForm = function () {
                $scope.$broadcast('show-errors-reset');
                if ($scope.employeeForm) {
                    $scope.employeeForm.$setPristine();
                    $scope.employeeForm.$setUntouched();
                }
                $scope.usernameValidation = null;
                $scope.employeeFormData = null;
            };

            $scope.submitResetEmployeeForm();

            /**
             * Calls resetOrganizationForm after the view loaded
             */

            /**
             * Validates
             */

            $scope.checkFirstName = function (caseForValidation) {
                switch (caseForValidation) {
                    case ('firstName') :
                        var firstName = $scope.employeeFormData.firstName;
                        if (firstName == null) {
                        } else if ($scope.FIRST_LAST_NAME_REGEX.test(firstName)) {
                            validator('firstName', false);
                        } else {
                            validator('firstName', true);
                        }
                        break;
                    case ('lastName') :
                        var lastName = $scope.employeeFormData.lastName;
                        if (lastName == null) {

                        } else if ($scope.FIRST_LAST_NAME_REGEX.test(lastName)) {

                            validator('lastName', false);
                        } else {
                            validator('lastName', true);
                        }
                        break;
                    case ('middleName') :
                        var middleName = $scope.employeeFormData.middleName;
                        if (middleName == null) {
                        } else if ($scope.MIDDLE_NAME_REGEX.test(middleName)) {
                            validator('middleName', false);
                        } else {
                            validator('middleName', true);
                        }
                        break;
                    case ('phone') :
                        var phone = $scope.employeeFormData.phone;
                        if (phone == null) {
                        } else if ($scope.PHONE_REGEX.test(phone)) {
                            validator('phone', false);
                        } else {
                            validator('phone', true);
                        }
                        break;
                    case ('email') :
                        var email = $scope.employeeFormData.email;
                        if (email == null) {
                        } else if ($scope.EMAIL_REGEX.test(email)) {
                            validator('email', false);
                        } else {
                            validator('email', true);
                        }
                        break;
                    case ('login') :
                        var username = $scope.employeeFormData.username;
                        if (username == null) {
                        } else if ($scope.USERNAME_REGEX.test(username)) {
                            isUsernameAvailable(username)
                        } else {
                            validator('loginValid', false);
                        }
                        break;
                }
            }

            /**
             * Checks whereas given username is available to use
             * for new user
             * @param username
             */
            function isUsernameAvailable(username) {
                userService.isUsernameAvailable(username).then(
                    function (data) {
                        validator('existLogin', data.data);
                    })
            }


            function validator(caseForValidation, isValid) {

                switch (caseForValidation) {
                    case 'firstName':
                        $scope.firstNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        }
                        break;
                    case 'lastName':
                        $scope.lastNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        }
                        break;
                    case 'middleName':
                        $scope.middleNameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        }
                        break;
                    case 'phone':
                        $scope.phoneNumberValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        }
                        break;
                    case 'email':
                        $scope.emailValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-error' : 'has-success'
                        }
                        break;
                    case 'existLogin':
                        $scope.usernameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'Такий логін вже існує'
                        }
                        break;
                    case 'loginValid':
                        $scope.usernameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'К-сть символів не повинна бути меншою за 3\n і більшою за 16 '
                        }
                        break;
                }
            }

            /**
             * Check passwords for equivalent
             */

            $scope.checkPasswords = function () {
                var first = $scope.employeeFormData.password;
                var second = $scope.employeeFormData.rePassword;
                $log.info(first);
                $log.info(second);
                var isValid = false;
                if (first != second) {
                    isValid = true;
                }
                $scope.passwordValidation = {
                    isValid: isValid,
                    css: isValid ? 'has-error' : 'has-success'
                }
            };

            /**
             * Finds all regions
             */
            function initFormData() {
                if (!$scope.regions) {
                    addressServiceProvider.findAllRegions().then(
                        function (data) {
                            $scope.regions = data.data;
                        });
                }
            }


            /**
             * Finds districts in a given region.
             * @param regionId
             *            to identify region
             */
            $scope.onRegionSelected = function (regionId) {
                addressServiceProvider
                    .findDistrictsByRegionId(regionId)
                    .then(function (data) {
                        $scope.districts = data.data;
                    });
            };

            /**
             * Finds localities in a given district.
             * @param districtId
             *            to identify district
             */
            $scope.onDistrictSelected = function (districtId) {
                addressServiceProvider.findLocalitiesByDistrictId(
                    districtId).then(function (data) {
                        $scope.localities = data.data;
                    });
            };

            /**
             * There are no DB records for this methods.
             * Finds streets in a given locality.
             * @param localityId
             *            to identify locality
             */
            $scope.onLocalitySelected = function (localityId) {
                addressServiceProvider.findStreetsByLocalityId(
                    localityId).then(function (data) {
                        $scope.streets = data.data;
                    });
            };

            /**
             * Finds buildings in a given street.
             *
             * @param streetId
             *            to identify street
             */

            /**
             * Refactor data
             */
            function retranslater() {
                employeeData = {
                    firstName: $scope.employeeFormData.firstName,
                    lastName: $scope.employeeFormData.lastName,
                    middleName: $scope.employeeFormData.middleName,
                    isAvaliable : true,
                    phone: $scope.employeeFormData.phone,
                    secondPhone: $scope.employeeFormData.secondPhone,
                    email: $scope.employeeFormData.email,
                    username: $scope.employeeFormData.username,
                    password: $scope.employeeFormData.password,
                    userRoles: [],
                }


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

            bValidation = function () {
                if (( $scope.firstNameValidation === undefined) || ($scope.lastNameValidation === undefined)
                    || ($scope.middleNameValidation === undefined) || ($scope.emailValidation === undefined)
                    || ($scope.passwordValidation === undefined) || ($scope.usernameValidation === undefined)
                ) {
                    $scope.incorrectValue = true;
                    return false;
                } else {
                    return true;
                }
            }

            $scope.onEmployeeFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if (bValidation()) {
                    if (!$scope.firstNameValidation.isValid && !$scope.lastNameValidation.isValid
                        && !$scope.middleNameValidation.isValid && !$scope.emailValidation.isValid) {
                        retranslater();
                        saveEmployee();
                    } else {
                        $scope.incorrectValue = true;
                    }
                }
            };

            /**
             * Update new employee in database.
             */
            function saveEmployee() {
                userService.saveUser(
                    employeeData).then(
                    function (data) {
                        if (data.status == 201) {
                            $rootScope.$broadcast('new-employee-added');
                            $rootScope.$broadcast('close-form');
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_ADD_EMPLOYEE'));
                        } else {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_ADD_EMPLOYEE'));
                        }
                    });
            }

            /**
             * Receives all regex for input fields
             *
             *
             */

            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
            $scope.PNOHE_REGEX_MY = /^[1-9]\d{8}$/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~$%^&*_=+}{\'?]+(\.[-a-z0-9~$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;


            /* Closes the modal window
             */
            $scope.closeModal = function () {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/close-alert.html',
                    controller: 'VerificationCloseAlertController',
                    size: 'md'
                })
            };

            $scope.$on('close-form', function(event, args) {
                $modalInstance.close();
            });

            //   $log.info(employeeFormData);
        }]);
