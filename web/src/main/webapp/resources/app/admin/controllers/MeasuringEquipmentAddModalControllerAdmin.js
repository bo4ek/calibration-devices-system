angular
    .module('adminModule')
    .filter('translateArray', ['$filter', function ($filter) {
        return function (input) {
            var result = $filter('translate')(input);
            var returnArr = [];
            for (var item in result) {
                returnArr.push(result[item]);
            }
            return returnArr.join(", ");
        }
    }])
    .controller(
    'MeasuringEquipmentAddModalControllerAdmin',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$filter',
        'MeasuringEquipmentServiceAdmin',
        'calibrationModule',
        function ($rootScope, $scope, $translate, $modalInstance, $filter, measuringEquipmentServiceAdmin, calibrationModule) {

            $scope.selectedValues = {};
            $scope.organizationCodes = [];
            $scope.addCalibrationModuleFormData = {};
            $scope.addCalibrationModuleFormData.organizationCode = '';
            $scope.addCalibrationModuleFormData.condDesignation = '';
            $scope.addCalibrationModuleFormData.serialNumber = '';
            $scope.addCalibrationModuleFormData.employeeFullName = '';
            $scope.addCalibrationModuleFormData.telephone = '';
            $scope.addCalibrationModuleFormData.workDate = '';
            $scope.addCalibrationModuleFormData.moduleType = '';
            $scope.addCalibrationModuleFormData.email = '';
            $scope.addCalibrationModuleFormData.calibrationType = '';

            $scope.oldSerialNumber = '';
            $scope.headerTranslate = 'ADD_INSTALLATION';
            $scope.applyButtonText = 'ADD';

            $scope.deviceTypeData = [
                {id: 'WATER', label: $filter('translate')('WATER')},
                {id: 'THERMAL', label: $filter('translate')('THERMAL')}
//                    {id: 'ELECTRICAL', label: $filter('translate')('ELECTRICAL')},
//                    {id: 'GASEOUS', label: $filter('translate')('GASEOUS')}
            ];

            $scope.moduleTypeData = [
                {id: 'INSTALLATION_FIX', label: $filter('translate')('INSTALLATION_FIX')},
                {id: 'INSTALLATION_PORT', label: $filter('translate')('INSTALLATION_PORT')}
            ];

            if (calibrationModule) {
                $scope.addCalibrationModuleFormData.deviceType = [];
                for (var i in calibrationModule.deviceType) {
                    $scope.addCalibrationModuleFormData.deviceType[i] = {
                        id: calibrationModule.deviceType[i],
                        label: $filter('translate')(calibrationModule.deviceType[i])
                    }
                }

                measuringEquipmentServiceAdmin.findAllOrganizationCodesAndNames()
                    .success(function (respCodes) {
                        $scope.organizationCodes = getNamesAndCodes(respCodes);
                        var index = getIndexOfSelectedValue($scope.organizationCodes, calibrationModule.organizationCode);
                        $scope.selectedValues.selectedOrganizationCode = $scope.organizationCodes[index];
                });

                $scope.addCalibrationModuleFormData.moduleType = {
                    id: calibrationModule.moduleType,
                    label: $filter('translate')(calibrationModule.moduleType)
                };
                $scope.addCalibrationModuleFormData.moduleId = calibrationModule.moduleId;
                $scope.addCalibrationModuleFormData.condDesignation = calibrationModule.condDesignation;
                $scope.addCalibrationModuleFormData.serialNumber = calibrationModule.serialNumber;
                $scope.oldSerialNumber = calibrationModule.serialNumber;
                $scope.addCalibrationModuleFormData.employeeFullName = calibrationModule.employeeFullName;
                $scope.addCalibrationModuleFormData.telephone = calibrationModule.telephone;
                $scope.addCalibrationModuleFormData.workDate = calibrationModule.workDate;
                $scope.addCalibrationModuleFormData.email = calibrationModule.email;
                $scope.addCalibrationModuleFormData.calibrationType = calibrationModule.calibrationType;
                $scope.addCalibrationModuleFormData.workDate = {
                    endDate: calibrationModule.workDate
                };

                $scope.headerTranslate = 'EDIT_INSTALLATION';
                $scope.applyButtonText = 'EDIT';
            } else {
                measuringEquipmentServiceAdmin.findAllOrganizationCodesAndNames()
                    .success(function (organizationCodes) {
                        $scope.organizationCodes = getNamesAndCodes(organizationCodes);
                        $scope.selectedValues.selectedOrganizationCode = undefined; //for ui-selects
                    }
                );

                $scope.addCalibrationModuleFormData.deviceType = undefined;

                $scope.headerTranslate = 'ADD_INSTALLATION';
                $scope.applyButtonText = 'ADD';
            }

            function getNamesAndCodes(organizationCodes) {
                var array = [];
                for (var i = 0; i < organizationCodes.length; i++) {
                    array.push(organizationCodesAndNames = {
                        code : organizationCodes[i][0],
                        name : organizationCodes[i][1]
                    });
                }
                return array;
            }

            function getIndexOfSelectedValue(myArray, searchTerm) {
                for (var i = 0, len = myArray.length; i < len; i++) {
                    if (myArray[i].code == searchTerm) {
                        return i;
                    }
                }
                var elem = {
                    organizationCode: searchTerm
                };
                myArray.push(elem);
                return (myArray.length - 1);
            }

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Resets form
             */
            $scope.resetCalibrationModuleForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.addCalibrationModuleForm.$setPristine();
                $scope.addCalibrationModuleForm.$setUntouched();
                $scope.addCalibrationModuleFormData = {};
                $scope.selectedValues.selectedOrganizationCode = undefined;
            };

            /**
             * Closes the modal window
             */
            $rootScope.closeModal = function (close) {
                $scope.resetCalibrationModuleForm();
                if (close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            };

            /**
             * Validates calibration module form before saving
             */
            $scope.onAddCalibrationModuleFormSubmit = function (event) {
                if ($scope.addCalibrationModuleFormData.deviceType === undefined) {
                    $scope.addCalibrationModuleForm.deviceType.$error = {"required": true};
                    $scope.addCalibrationModuleForm.deviceType.$valid = false;
                    $scope.addCalibrationModuleForm.deviceType.$invalid = true;
                }
                $scope.$broadcast('show-errors-check-validity');

                event.preventDefault();
                if ($scope.addCalibrationModuleFormData.serialNumber && $scope.addCalibrationModuleFormData.serialNumber !== $scope.oldSerialNumber) {
                    var response = measuringEquipmentServiceAdmin.isSerialNumberDuplicate($scope.addCalibrationModuleFormData.serialNumber);
                    response.then(function (isDuplicate) {
                            $scope.addCalibrationModuleForm.serialNumber.$setValidity("duplicate", !isDuplicate.data);
                            $scope.$broadcast('show-errors-check-validity');
                            $scope.saveIfValid();
                        }
                    )
                } else {
                    $scope.addCalibrationModuleForm.serialNumber.$setValidity("duplicate", true);
                    $scope.saveIfValid();
                }
            };

            /**
             * Save calibration module data and close modal window if all data are valid
             */
            $scope.saveIfValid = function() {
                if ($scope.addCalibrationModuleForm.$valid) {
                    for (var i in $scope.addCalibrationModuleFormData.deviceType) {
                        $scope.addCalibrationModuleFormData.deviceType[i] = $scope.addCalibrationModuleFormData.deviceType[i].id;
                    }
                    $scope.addCalibrationModuleFormData.moduleType = $scope.addCalibrationModuleFormData.moduleType.id;
                    saveCalibrationModule();
                }
            };

            /**
             * Saves calibration module
             */
            function saveCalibrationModule() {
                $scope.addCalibrationModuleFormData.workDate = $scope.addCalibrationModuleFormData.workDate.endDate;
                $scope.addCalibrationModuleFormData.organizationCode = $scope.selectedValues.selectedOrganizationCode.code;
                if (calibrationModule === undefined) {
                    measuringEquipmentServiceAdmin.saveCalibrationModule($scope.addCalibrationModuleFormData)
                        .then(function (result) {
                            if (result == 201) {
                                $scope.closeModal(true);
                                $rootScope.onTableHandling();
                            }
                        });
                } else {
                    measuringEquipmentServiceAdmin.editCalibrationModule($scope.addCalibrationModuleFormData, calibrationModule.moduleId)
                        .then(function (result) {
                            if (result == 200) {
                                $scope.closeModal(true);
                                $scope.resetCalibrationModuleForm();
                                $rootScope.onTableHandling();
                            }
                        });
                }
            }

            /**
             *  Date picker and formatter setup
             *
             */

            $scope.initDatePicker = function () {

                $scope.setTypeDataLangDatePicker = function () {

                    $scope.opts = {
                        format: 'DD-MM-YYYY',
                        singleDatePicker: true,
                        showDropdowns: true,
                        eventHandlers: {}
                    };

                };

                $scope.setTypeDataLangDatePicker();
            };

            $scope.showPicker = function () {
                angular.element("#datepickerfieldSingle").trigger("click");
            };

            $scope.initDatePicker();

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                } else {
                    moment.locale('en'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                }
            };

            $scope.setTypeDataLanguage();

            $scope.clearDate = function () {
                calibrationModule.workDate = null;
            };

            $scope.isSerialNumberDuplicate = function() {
                return measuringEquipmentServiceAdmin.isSerialNumberDuplicate($scope.addCalibrationModuleFormData.serialNumber);
            };

            /**
             * Check is calibration module with the same serial number
             */
            $scope.checkForDuplicates = function () {
                if ($scope.addCalibrationModuleFormData.serialNumber && $scope.addCalibrationModuleFormData.serialNumber !== $scope.oldSerialNumber) {
                    $scope.isSerialNumberDuplicate().then(function (isDuplicate) {
                            $scope.addCalibrationModuleForm.serialNumber.$setValidity("duplicate", !isDuplicate.data);
                        }
                    )
                }
            };

            /**
             * Hide error when user changing value
             */
            $scope.hideDuplicateError = function() {
                if($scope.addCalibrationModuleForm.serialNumber.$error.duplicate) {
                    $scope.addCalibrationModuleForm.serialNumber.$setValidity("duplicate", true);
                    $scope.$broadcast('show-errors-check-validity');
                }
            };

            $scope.CATEGORY_DEVICE_CODE = /^[\u0430-\u044f\u0456\u0457\u0454a-z\d]{13}$/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
        }
    ]);