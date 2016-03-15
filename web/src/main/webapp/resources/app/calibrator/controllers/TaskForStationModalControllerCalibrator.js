angular
    .module('employeeModule')
    .controller(
    'TaskForStationModalControllerCalibrator',
    [
        '$rootScope',
        '$scope',
        '$modal',
        '$modalInstance',
        'VerificationPlanningTaskService',
        '$log',
        '$filter',
        'verificationIDs',
        'moduleType',
        'toaster',
        '$translate',
        function ($rootScope, $scope, $modal, $modalInstance, verificationPlanningTaskService, $log, $filter,
                verificationIDs, moduleType, toaster, $translate) {

            $scope.calibrationTask = {};
            $scope.moduleSerialNumbers = [];
            $scope.noModulesAvailable = false;
            $scope.calibrationTask.moduleType = moduleType;

            /**
             * Device types (application field) for the select dropdown
             */
            $scope.deviceTypeData = [
                {id: 'WATER', label: $filter('translate')('WATER')},
                {id: 'THERMAL', label: $filter('translate')('THERMAL')}
//                {id: 'ELECTRICAL', label: $filter('translate')('ELECTRICAL')},
//                {id: 'GASEOUS', label: $filter('translate')('GASEOUS')}
            ];

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function() {
                $scope.closeModal();
            });

            /**
             * Closes edit modal window.
             */
            $scope.closeModal = function (close) {
                if (close === true) {
                    $modalInstance.close();
                } else {
                    $modalInstance.dismiss();
                }
            };

            /**
             * resets task form
             */
            $scope.resetTaskForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.noModulesAvailable = false;
                $scope.formTask.$submitted = false;
                $scope.calibrationTask.dateOfTask = null;
                $scope.calibrationTask.applicationField = null;
                $scope.calibrationTask.installationNumber = null;
                $scope.moduleSerialNumbers = [];
            };

            /**
             *  Date picker and formatter setup
             *
             */
            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function () {

                if ($scope.defaultDate == null) {
                    //copy of original daterange
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

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

            /**
             * sets format of date picker date
             */
            $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
            $scope.format = $scope.formats[2];

            /**
             * Disables weekend selection
             *
             * @param date
             * @param mode
             * @returns {boolean}
             */
            $scope.disabled = function(date, mode) {
                return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
            };

            $scope.toggleMin = function() {
                $scope.minDate = $scope.minDate ? null : new Date();
            };

            $scope.toggleMin();
            $scope.maxDate = new Date(2100, 5, 22);

            $scope.clearDate = function () {
                $log.debug($scope.calibrationTask.dateOfTask);
                $scope.noModulesAvailable = false;
                $scope.calibrationTask.dateOfTask = null;
                $scope.moduleSerialNumbers = [];
            };

            $scope.$watch('myDatePicker.pickerDate', function() {
                $scope.receiveModuleNumbers();
            });

            /**
             * makes asynchronous request to the server
             * and receives the calibration modules info
             */
            $scope.receiveModuleNumbers = function() {
                if ($scope.myDatePicker.pickerDate!=null) {
                    $scope.calibrationTask.dateOfTask = new Date($scope.myDatePicker.pickerDate.startDate);
                }
                if ($scope.calibrationTask.dateOfTask && $scope.calibrationTask.applicationField && $scope.myDatePicker.pickerDate!=null) {
                    var dateOfTask = $scope.calibrationTask.dateOfTask;
                    var deviceType = $scope.calibrationTask.applicationField;
                    var moduleType = $scope.calibrationTask.moduleType;
                    verificationPlanningTaskService.getModules(moduleType, dateOfTask, deviceType)
                        .then(function (result) {
                            $log.debug(result);
                            $scope.moduleSerialNumbers = result.data;
                            $scope.noModulesAvailable = $scope.moduleSerialNumbers.length === 0;
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                } else {
                    $scope.noModulesAvailable = false;
                    $scope.moduleSerialNumbers = [];
                }
            };

            /**
             * sends the task for calibration module data
             * to the server to be saved in the database
             * if response status 200 opens success modal,
             * else opens error modal
             */
            $scope.save = function () {
                if ($scope.formTask.$valid) {
                    var calibrationTask = {
                        "dateOfTask": $scope.calibrationTask.dateOfTask,
                        "moduleSerialNumber": $scope.calibrationTask.installationNumber,
                        "verificationsId": verificationIDs
                    };
                    verificationPlanningTaskService.saveTask(calibrationTask).then(function(data) {
                        switch(data.status) {
                            case 200: {
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('VERIFICATIONS_ADDED_TO_TASK'));
                                break;
                            }
                            case 201: {
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('TASK_FOR_STATION_CREATED'));
                                break;
                            }
                            default: {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_WHILE_CREATING_TASK'));
                            }
                        }
                        $scope.closeModal(true);
                    });
                }
            }
        }]);
