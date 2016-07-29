angular
    .module('employeeModule')
    .controller('ArchivalVerificationsControllerCalibrator', ['$scope', '$modal', '$log',
        'VerificationServiceCalibrator', 'CalibrationTestServiceCalibrator', 'ngTableParams', '$location', '$filter', '$rootScope', '$timeout', '$translate', 'toaster',

        function ($scope, $modal, $log, verificationServiceCalibrator, calibrationTestServiceCalibrator, ngTableParams, $location, $filter, $rootScope,
                  $timeout, $translate, toaster) {

            $scope.resultsCount = 0;

            $scope.clearAll = function () {
                $scope.selectedStatus.name = null;
                $scope.selectedDeviceType.name = null;
                $scope.selectedProtocolStatus.name = null;
                $scope.tableParams.filter({});
                $scope.clearDate(); 
            };


            $scope.clearDate = function () {
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                $scope.tableParams.filter()['date'] = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.selectedStatus = {
                name: null
            };
            $scope.selectedDeviceType = {
                name: null
            };
            $scope.selectedProtocolStatus = {
                name: null
            };


            $scope.statusData = [
                {id: 'TEST_OK', label: null},
                {id: 'TEST_NOK', label: null},
                {id: 'SENT_TO_VERIFICATOR', label: null}
            ];

            $scope.deviceTypeData = [
                {id: 'WATER', label: null},
                {id: 'THERMAL', label: null}
            ];


            $scope.protocolStatusData = [
                {id: 'SUCCESS', label: null},
                {id: 'FAILED', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'Перевірено придатний';
                    $scope.statusData[1].label = 'Перевірено непридатний';
                    $scope.statusData[2].label = 'Пред\'явлено повірнику';

                    $scope.deviceTypeData[0].label = 'Холодна вода';
                    $scope.deviceTypeData[1].label = 'Гаряча вода';

                    $scope.protocolStatusData[0].label = 'Придатний';
                    $scope.protocolStatusData[1].label = 'Не придатний';

                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'Tested OK';
                    $scope.statusData[1].label = 'Tested NOK';
                    $scope.statusData[2].label = 'Sent to verificator';

                    $scope.deviceTypeData[0].label = 'Cold water';
                    $scope.deviceTypeData[1].label = 'Hot water';

                    $scope.protocolStatusData[0].label = 'SUCCESS';
                    $scope.protocolStatusData[1].label = 'FAILED';

                } else {
                    $log.debug(lang);
                }
            };


            $scope.setTypeDataLanguage();
            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function (date) {
                /**
                 *  Date picker and formatter setup
                 *
                 */

                $scope.myDatePicker.pickerDate = {
                    startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                    endDate: moment() 
                };

                if ($scope.defaultDate == null) {
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }
                moment.locale('uk'); 
                $scope.opts = {
                    format: 'DD-MM-YYYY',
                    showDropdowns: true,
                    locale: {
                        firstDay: 1,
                        fromLabel: 'Від',
                        toLabel: 'До',
                        applyLabel: "Прийняти",
                        cancelLabel: "Зачинити",
                        customRangeLabel: "Обрати самостійно"
                    },
                    ranges: {
                        'Сьогодні': [moment(), moment()],
                        'Вчора': [moment().subtract(1, 'day'), moment().subtract(1, 'day')],
                        'Цього тижня': [moment().startOf('week'), moment().endOf('week')],
                        'Цього місяця': [moment().startOf('month'), moment().endOf('month')],
                        'Попереднього місяця': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                        'За увесь час': [$scope.defaultDate.startDate, $scope.defaultDate.endDate]
                    },
                    eventHandlers: {}
                };
            };


            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };

            $scope.isDateDefault = function () {
                var pickerDate = $scope.myDatePicker.pickerDate;

                if (pickerDate == null || $scope.defaultDate == null) { 
                    return true;
                }
                if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') 
                    && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                    return true;
                }
                return false;
            };

            verificationServiceCalibrator.getArchivalVerificationEarliestDate().success(function (date) {
                $scope.initDatePicker(date);
                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 10,
                    sorting: {
                        date: 'desc'
                    }, filter: {
                        status: null,
                        measurement_device_type: null,
                        protocol_status: null,
                        date: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD")
                    }
                }, {
                    total: 0,
                    filterDelay: 1500,
                    getData: function ($defer, params) {

                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        if ($scope.selectedStatus.name != null) {
                            params.filter().status = $scope.selectedStatus.name.id;
                        }
                        else {
                            params.filter().status = null;
                        }

                        if ($scope.selectedDeviceType.name != null) {
                            params.filter().measurement_device_type = $scope.selectedDeviceType.name.id;
                        }
                        else {
                            params.filter().measurement_device_type = null;
                        }

                        if ($scope.selectedProtocolStatus.name != null) {
                            params.filter().protocol_status = $scope.selectedProtocolStatus.name.id;
                        } else {
                            params.filter().protocol_status = null;
                        }

                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

                        verificationServiceCalibrator.getArchiveVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder).success(function (result) {
                            $scope.resultsCount = result.totalItems;
                            $defer.resolve(result.content);
                            params.total(result.totalItems);
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                    }
                })
            });

            $scope.checkFilters = function () {
                if ($scope.tableParams == null) return false;
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        if (i == 'date' || i == 'endDate')
                            continue; 
                        return true;
                    }
                }
                return false;
            };

            $scope.checkDateFilters = function () {
                if ($scope.tableParams == null) return false;
                var obj = $scope.tableParams.filter();
                if ($scope.isDateDefault())
                    return false;
                else if (!moment(obj.date).isSame($scope.defaultDate.startDate)
                    || !moment(obj.endDate).isSame($scope.defaultDate.endDate)) {
                    return true;
                }
                return false;
            };

            $scope.openDetails = function (verifId, verifDate) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/provider/views/modals/archival-verification-details.html',
                    controller: 'ArchivalDetailsModalController',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getArchivalVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
                                    return verification;
                                });
                        }
                    }
                });

            };

            $scope.editStamp = function (verifId) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/edit-stamp-modal.html',
                    controller: 'EditStampModalController',
                    size: 'xs',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getArchivalVerificationStamp(verifId)
                                .success(function (verification) {
                                    return verification;
                                });
                        }
                    }
                });
            };

            $scope.editProvider = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/edit-provider-modal.html',
                    controller: 'EditProviderModalController',
                    size: 'xs',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getAllProviders()
                                .success(function (providers) {
                                    return providers;
                                });
                        }
                    }
                });

                modalInstance.result.then(function (provider) {
                    verificationServiceCalibrator.changeProviderInArchive(verificationId, provider.id).then(function (data) {
                        switch (data.status) {
                            case 200:
                            {
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('SUCCESSFUL_EDITED'));
                                $scope.tableParams.reload();
                                break;
                            }
                            case 403:
                            {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_ACCESS'));
                                break;
                            }
                            default:
                            {
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('SAVE_VERIF_ERROR'));
                            }
                        }
                    });
                });
            };

            /**
             *  Date picker and formatter setup
             *
             */
            $scope.openState = {};
            $scope.openState.isOpen = false;

            $scope.open = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.openState.isOpen = true;
            };


            $scope.dateOptions = {
                formatYear: 'yyyy',
                startingDay: 1,
                showWeeks: 'false'
            };

            $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
            $scope.format = $scope.formats[2];


            $scope.openAddTest = function (verification) {
                if (!verification.manual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                } else {
                    $scope.createManualTest(verification);
                    calibrationTestServiceCalibrator.dataOfVerifications().setIdsOfVerifications($scope.dataToManualTest);
                    $location.path('/calibrator/verifications/calibration-test/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                }
            };

        }]);
