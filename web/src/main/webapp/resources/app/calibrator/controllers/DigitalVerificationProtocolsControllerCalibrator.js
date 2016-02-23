angular
    .module('employeeModule')
    .controller('DigitalVerificationProtocolsControllerCalibrator', ['$scope', '$log', '$modal',
        'DigitalVerificationProtocolsServiceCalibrator', 'CalibrationTestServiceCalibrator', 'VerificationServiceCalibrator', '$rootScope', 'ngTableParams','$filter', 'toaster', '$location', '$translate',

        function ($scope, $log, $modal, digitalVerificationProtocolsServiceCalibrator, calibrationTestServiceCalibrator, verificationServiceCalibrator, $rootScope, ngTableParams,
                  $filter, toaster, $location, $translate) {
            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPerPage = 5;
            $scope.pageContent = [];
            $scope.checked = false;

            $scope.$watch('globalSearchParams',function(newParam,oldParam){
                if($scope.hasOwnProperty("tableParams")) {
                    $scope.tableParams.reload();
                }
            },true);
            $scope.clearAll = function () {
                $scope.selectedStatus.name = null;
                $scope.tableParams.filter({});
                $scope.clearDate();
            };

            $scope.clearDate = function () {
                $scope.datePicker.initialDate = $scope.defaultDate;
                $scope.tableParams.filter()['date'] = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.datePicker = {};
            $scope.datePicker.initialDate = null;
            $scope.defaultDate = null;


            $scope.initDatePicker = function (date) {

                $scope.datePicker.initialDate = {
                    startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                    endDate: moment()
                };

                if ($scope.defaultDate == null) {
                    $scope.defaultDate = angular.copy($scope.datePicker.initialDate);
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

            $scope.isInitialDateDefault = function () {
                var initialDate = $scope.datePicker.initialDate;

                if (initialDate == null || $scope.defaultDate == null) {
                    return true;
                }
                if (initialDate.startDate.isSame($scope.defaultDate.startDate, 'day')
                    && initialDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                    return true;
                }
                return false;
            };


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
                if ($scope.isInitialDateDefault())
                    return false;
                else if (!moment(obj.date).isSame($scope.defaultDate.startDate)
                    || !moment(obj.endDate).isSame($scope.defaultDate.endDate)) {
                    return true;
                }
                return false;
            };

            $scope.selectedStatus = {
                name: null
            };

            $scope.statusData = [
                {id: 'IN_PROGRESS', label: null},
                {id: 'TEST_PLACE_DETERMINED', label: null},
                {id: 'SENT_TO_TEST_DEVICE', label: null},
                {id: 'TEST_COMPLETED', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'В роботі';
                    $scope.statusData[1].label = 'Визначено спосіб повірки';
                    $scope.statusData[2].label = 'Відправлено на установку';
                    $scope.statusData[3].label = 'Проведено вимірювання';
                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'In progress';
                    $scope.statusData[1].label = 'Test place determined';
                    $scope.statusData[2].label = 'Sent to test device';
                    $scope.statusData[3].label = 'Test completed';

                }
            };

            $scope.setTypeDataLanguage();

            digitalVerificationProtocolsServiceCalibrator.getNewVerificationEarliestDate().success(function (date) {
                $scope.initDatePicker(date);
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    default: 'default'
                }
            }, {
                total: 0,
                filterDelay: 1500,
                getData: function ($defer, params) {
                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    params.filter().date = $scope.datePicker.initialDate.startDate.format("YYYY-MM-DD");
                    params.filter().endDate = $scope.datePicker.initialDate.endDate.format("YYYY-MM-DD");

                    if ($scope.selectedStatus.name != null) {
                        params.filter().status = $scope.selectedStatus.name.id;
                    }
                    else {
                        params.filter().status = null;
                    }
                    digitalVerificationProtocolsServiceCalibrator.getProtocols(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                        .success(function (result) {
                            $scope.totalItems = result.totalItems;
                            $defer.resolve(result.content);
                            params.total(result.totalItems);
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                }
            })});
            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;
            $scope.idsOfCalibrators = null;
            $scope.dataToManualTest = new Map();


            /**
             * push verification id to array
             */

            $scope.resolveVerificationId = function (id) {
                var index = $scope.idsOfVerifications.indexOf(id);
                if (index === -1) {
                    $scope.idsOfVerifications.push(id);
                    index = $scope.idsOfVerifications.indexOf(id);
                }

                if (!$scope.checkedItems[index]) {
                    $scope.idsOfVerifications.splice(index, 1, id);
                    $scope.checkedItems.splice(index, 1, true);
                } else {
                    $scope.idsOfVerifications.splice(index, 1);
                    $scope.checkedItems.splice(index, 1);
                }
                checkForEmpty();
            };
            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        backdrop : 'static',
                        templateUrl: 'resources/app/calibrator/views/modals/protocols-sending.html',
                        controller: 'DigitalProtocolsSendingModalControllerCalibrator',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return digitalVerificationProtocolsServiceCalibrator.getVerificators()
                                    .success(function (data) {

                                        return data;
                                    }
                                );
                            }
                        }
                    });

                    /**
                     * executes when modal closing
                     */
                    modalInstance.result.then(function (formData) {

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            organizationId: formData.verificator.id
                        };

                        digitalVerificationProtocolsServiceCalibrator.sendProtocols(dataToSend)
                            .success(function () {
                                $log.debug('success sending');
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-verificator');
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('SUCCESS_SENT'));
                            });
                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                    });
                } else {
                    $scope.isClicked = true;
                }
            };

            /**
             * check if idsOfVerifications array is empty
             */
            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };

            $scope.openTest = function (verification) {
                if(!verification.manual) {
                    $location.path('/calibrator/verifications/calibration-test-add/').search({
                        'param': verification.id,
                        'loadProtocol': 1
                    });
                }else{
                    $scope.createManualTest(verification);
                    calibrationTestServiceCalibrator.dataOfVerifications().setIdsOfVerifications($scope.dataToManualTest);
                    $location.path('/calibrator/verifications/calibration-test/').search({
                        param: verification.id
                    });
                }
            };

            $scope.createManualTest = function (verification) {
                var manualTest = {
                    realiseYear: verification.realiseYear,
                    numberCounter: verification.numberOfCounter,
                    status:verification.status
                };
                $scope.dataToManualTest.set(verification.id, manualTest);
            };

            $scope.openDetails = function (verificationId) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verificationId)
                                .success(function (verification) {
                                    return verification;
                                });
                        }
                    }
                });
            };

        }]);
