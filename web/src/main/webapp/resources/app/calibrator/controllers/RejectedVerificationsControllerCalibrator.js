angular
    .module('employeeModule')
    .controller('RejectedVerificationsControllerCalibrator', ['$scope', '$modal', '$log',
        'VerificationServiceCalibrator', 'CalibrationTestServiceCalibrator', 'ngTableParams', '$location', '$filter', '$rootScope', '$timeout', '$translate',

        function ($scope, $modal, $log, verificationServiceCalibrator, calibrationTestServiceCalibrator, ngTableParams, $location, $filter, $rootScope,
                  $timeout, $translate) {

            $scope.resultsCount = 0;


            $scope.clearAll = function () {
                $scope.tableParams.filter({});
                $scope.clearDate(); 
            };


            $scope.clearDate = function () {
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                $scope.tableParams.filter()['starDate'] = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                $scope.tableParams.filter()['endDate'] = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

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
                        starDate: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD")
                    }
                }, {
                    debugMode : true,
                    total: 0,
                    filterDelay: 1500,
                    getData: function ($defer, params) {

                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        params.filter().starDate = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

                        verificationServiceCalibrator.getRejectedVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder).success(function (result) {
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


        }]);

