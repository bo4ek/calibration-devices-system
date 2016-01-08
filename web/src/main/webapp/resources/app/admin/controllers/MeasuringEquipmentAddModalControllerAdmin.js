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
        function ($rootScope, $scope, $translate, $modalInstance, $filter,
                  measuringEquipmentServiceAdmin, calibrationModule) {

            /**
             * Initialize all variables
             */
            $scope.init = function () {

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

                $scope.headerTranslate = 'ADD_INSTALLATION';
                $scope.applyButtonText = 'ADD';

                $scope.deviceTypeData = [
                    {id: 'WATER', label: $filter('translate')('WATER')},
                    {id: 'THERMAL', label: $filter('translate')('THERMAL')}
                ];

                $scope.moduleTypeData = [
                    {id: 'INSTALLATION_FIX', label: $filter('translate')('INSTALLATION_FIX')},
                    {id: 'INSTALLATION_PORT', label: $filter('translate')('INSTALLATION_PORT')}
                ];

                if (calibrationModule !== undefined) {
                    $scope.addCalibrationModuleFormData.deviceType = new Array();
                    for (var i in calibrationModule.deviceType) {
                        $scope.addCalibrationModuleFormData.deviceType[i] = {
                            id: calibrationModule.deviceType[i],
                            label: $filter('translate')(calibrationModule.deviceType[i])
                        }
                    }

                    $scope.addCalibrationModuleFormData.moduleType = {
                        id: calibrationModule.moduleType,
                        label: $filter('translate')(calibrationModule.moduleType)
                    };
                    $scope.addCalibrationModuleFormData.moduleId = calibrationModule.moduleId;
                    $scope.addCalibrationModuleFormData.organizationCode = calibrationModule.organizationCode;
                    $scope.addCalibrationModuleFormData.condDesignation = calibrationModule.condDesignation;
                    $scope.addCalibrationModuleFormData.serialNumber = calibrationModule.serialNumber;
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
                    $scope.addCalibrationModuleFormData.deviceType = undefined;
                    $scope.addCalibrationModuleFormData.organizationCode = '';
                    $scope.addCalibrationModuleFormData.condDesignation = '';
                    $scope.addCalibrationModuleFormData.serialNumber = '';
                    $scope.addCalibrationModuleFormData.employeeFullName = '';
                    $scope.addCalibrationModuleFormData.telephone = '';
                    $scope.addCalibrationModuleFormData.workDate = '';
                    $scope.addCalibrationModuleFormData.moduleType = undefined;
                    $scope.addCalibrationModuleFormData.email = '';
                    $scope.addCalibrationModuleFormData.calibrationType = '';

                    $scope.headerTranslate = 'ADD_INSTALLATION';
                    $scope.applyButtonText = 'ADD';
                }
            };

            $scope.init();

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
            $scope.onAddCalibrationModuleFormSubmit = function () {
                if ($scope.addCalibrationModuleFormData.deviceType === undefined) {
                    $scope.addCalibrationModuleForm.deviceType.$error = {"required": true};
                    $scope.addCalibrationModuleForm.deviceType.$valid = false;
                    $scope.addCalibrationModuleForm.deviceType.$invalid = true;
                }

                $scope.$broadcast('show-errors-check-validity');
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
                            console.log("else");
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


            $scope.CATEGORY_DEVICE_CODE = /^[\u0430-\u044f\u0456\u0457\u0454a-z\d]{13}$/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
            $scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
        }
    ]);