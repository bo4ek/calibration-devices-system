angular
    .module('adminModule')
    .controller(
    'UserAddModalController',
    [
        '$rootScope',
        '$scope',
        '$log',
        '$translate',
        '$modalInstance',
        '$filter',
        'AddressService',
        'OrganizationService',
        'UserService',
        function($rootScope, $scope, $log, $translate, $modalInstance, $filter,
                 addressService, organizationService,
                 userService) {
            //$rootScope, $scope, $modalInstance, $log, $state, $http, userService, addressServiceProvider
            var employeeData = {};

            $scope.regions = null;
            $scope.districts = [];
            $scope.localities = [];
            $scope.streets = [];
            $scope.buildings = [];


            function initFormData() {
                if (!$scope.regions) {
                    addressService.findAllRegions().then(
                        function(data) {
                            $scope.regions = data;
                        });
                }
            }

            initFormData();

            /**
             * Closes modal window on browser's back/forward button click.
             */

            $rootScope.$on('$locationChangeStart', function() {
                $modalInstance.close();
            });
            /**

            $scope.regions = null;
            $scope.districts = [];
            $scope.localities = [];
            $scope.streets = [];
            $scope.buildings = [];

            /**
             * Resets employee form
             */


            $scope.resetEmployeeForm = function () {
                $scope.$broadcast('show-errors-reset');
                if ($scope.employeeForm) {
                    //$scope.employeeForm.$setValidity(true);
                    $scope.employeeForm.$setPristine();
                    $scope.employeeForm.$setUntouched();
                    // $scope.employeeForm.lastName.dataset = {};
                    //$scope.employeeForm.$rollbackViewValue();
                    //$scope.employeeFormData.lastName="";
                }
                $scope.usernameValidation = null;
                $scope.employeeFormData = null;
            };

            $scope.resetEmployeeForm();

            /**
             * Calls resetOrganizationForm after the view loaded
             */

            //$scope.resetEmployeeForm();

            /**
             * Validates
             */

            $scope.checkField = function (caseForValidation) {
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
                            isUsernameAvailable(username);
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
            $scope.isUsernameAvailable = true;

            $scope.checkIfUsernameIsAvailable = function() {
                var username = $scope.employeeFormData.username;
                userService.isUsernameAvailable(username).then(
                    function(data) {
                        $scope.isUsernameAvailable = data;
                        validator('existLogin', data);
                     //   if (!isUsernameAvailable) {
                       //     angular.element(document.getElementById('notAv')).addClass('usernameNotAvailable');
                        //}
                    })
            }
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
                    case 'loginValid':
                        $scope.usernameValidation = {
                            isValid: isValid,
                            css: isValid? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'К-сть символів не повинна бути меншою за 3\n і більшою за 16 '
                        }
                        break;
                    case 'existLogin':
                        $scope.usernameValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'Такий логін вже існує'
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
                    addressService.findAllRegions().then(
                        function(data) {
                            $scope.regions = data;
                        });
                }
            }

            initFormData();

            /**
             * Finds districts in a given region.
             * @param regionId
             *            to identify region
             */
            $scope.onRegionSelected = function(regionId) {
                addressService
                    .findDistrictsByRegionId(regionId)
                    .then(function(data) {
                        $scope.districts = data;
                    });
            };

            /**
             * Finds localities in a given district.
             *
             * @param districtId
             *            to identify district
             */
            $scope.onDistrictSelected = function(districtId) {
                addressService.findLocalitiesByDistrictId(
                    districtId).then(function(data) {
                        $scope.localities = data;
                    });
            };

            /**
             * Finds streets in a given locality.
             *
             * @param localityId
             *            to identify locality
             */
            $scope.onLocalitySelected = function(localityId) {
                addressService.findStreetsByLocalityId(
                    localityId).then(function(data) {
                        $scope.streets = data;
                    });
            };

            /**
             * Finds buildings in a given street.
             *
             * @param streetId
             *            to identify street
             */
            $scope.onStreetSelected = function(streetId) {
                addressService
                    .findBuildingsByStreetId(streetId)
                    .then(function(data) {
                        $scope.buildings = data;
                    });
            };

            /**
             * Convert address data to string
             * Refactor data
             */
            function retranslater() {
                $scope.employeeFormData.address = {
                    region : $scope.employeeFormData.region.designation,
                    district : $scope.employeeFormData.district.designation,
                    locality : $scope.employeeFormData.locality.designation,
                    street : $scope.employeeFormData.street.designation,
                    building :$scope.employeeFormData.building,
                    flat : $scope.employeeFormData.flat
                },
                employeeData = {
                    firstName: $scope.employeeFormData.firstName,
                    lastName: $scope.employeeFormData.lastName,
                    middleName: $scope.employeeFormData.middleName,
                    phone: $scope.employeeFormData.phone,
                    email: $scope.employeeFormData.email,
                    username: $scope.employeeFormData.username,
                    password: $scope.employeeFormData.password,
                    userRoles: ['SYS_ADMIN'],
                    address : $scope.employeeFormData.address
                }

//                    employeeData.address = {
//                        region: $scope.employeeFormData.region.designation,
//                        district: $scope.employeeFormData.district.designation,
//                        locality: $scope.employeeFormData.locality.designation,
//                        street: $scope.employeeFormData.street,
//                        building: $scope.employeeFormData.building,
//                        flat: $scope.employeeFormData.flat,
//                    }


            }

            bValidation = function () {
                if (( $scope.firstNameValidation === undefined) || ($scope.lastNameValidation === undefined)
                    || ($scope.middleNameValidation === undefined) || ($scope.emailValidation === undefined)
                    || ($scope.passwordValidation === undefined) || ($scope.usernameValidation === undefined)
                //|| ($scope.employeeFormData.region === undefined) || ($scope.employeeFormData.district === undefined)
                //|| ($scope.employeeFormData.locality === undefined)
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
                            $scope.closeModal();
                            $scope.resetEmployeeForm();
                        } else {
                            alert('Error');
                        }
                    });
            };

            /**
             * Receives all regex for input fields
             *
             *
             */

            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
            $scope.PNOHE_REGEX_MY = /^[1-9]\d{8}$/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
            $scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
            $scope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
            $scope.FLAT_REGEX=/^([1-9]{1}[0-9]{0,3}|0)$/;


            /* Closes the modal window
             */
            $rootScope.closeModal = function () {
                $modalInstance.close();
            };

            //   $log.info(employeeFormData);

        } ]);
