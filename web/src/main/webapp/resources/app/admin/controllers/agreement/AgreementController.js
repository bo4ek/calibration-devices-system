angular
    .module('adminModule')
    .controller(
        'AgreementController',
        [
            '$rootScope',
            '$scope',
            '$modal',
            '$http',
            '$filter',
            'AgreementService',
            'ngTableParams',
            '$translate',
            '$timeout',
            'toaster',
            function ($rootScope, $scope, $modal, $http, $filter, agreementService, ngTableParams, $translate, $timeout, toaster) {
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.itemsPerPage = 5;
                $scope.pageContent = [];

                //for measurement device type
                $scope.selectedDeviceType = {
                    name: null
                };
                /**
                 * Date
                 */
                $scope.clearDate = function () {
                    //daterangepicker doesn't support null dates
                    $scope.myDatePicker.pickerDate = $scope.defaultDate;
                    //setting corresponding filters with 'all time' range
                    $scope.tableParams.filter().startDateToSearch = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                    $scope.tableParams.filter().endDateToSearch = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");
                };

                $scope.myDatePicker = {};
                $scope.myDatePicker.pickerDate = null;
                $scope.defaultDate = null;

                $scope.initDatePicker = function (date) {
                    /**
                     *  Date picker and formatter setup
                     *
                     */
                    /*TODO: i18n*/
                    $scope.myDatePicker.pickerDate = {
                        startDate: (date ? moment(date, "YYYY-MM-DD") : moment()),
                        //earliest day of  all the verifications available in table
                        //we should reformat it here, because backend currently gives date in format "YYYY-MM-DD"
                        endDate: moment() // current day
                    };

                    if ($scope.defaultDate == null) {
                        //copy of original daterange
                        $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                    }
                    moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
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
                // $scope.initDatePicker();

                $scope.showPicker = function ($event) {
                    angular.element("#datepickerfield").trigger("click");
                };

                $scope.isDateDefault = function () {
                    var pickerDate = $scope.myDatePicker.pickerDate;

                    if (pickerDate == null || $scope.defaultDate == null) { //moment when page is just loaded
                        return true;
                    }
                    if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') //compare by day
                        && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                        return true;
                    }
                    return false;
                };


                $scope.deviceTypeData = [
                    {
                        id: 'WATER',
                        label: $filter('translate')('WATER')
                    },
                    {
                        id: 'THERMAL',
                        label: $filter('translate')('THERMAL')
                    }
                ];

                /**
                 * Localization of multiselect for type of organization
                 */
                $scope.setTypeDataLanguage = function () {
                    $scope.deviceTypeData[0].label = $filter('translate')('WATER');
                    $scope.deviceTypeData[1].label = $filter('translate')('THERMAL');
                };

                $scope.clearAll = function () {
                    $scope.selectedDeviceType.name = null;
                    $scope.tableParams.filter({});
                };


                /**
                 * Updates the table.
                 */
                $rootScope.onTableHandling = function () {
                    //if ($scope.tableParams == null) return false; //table not yet initialized
                    $scope.tableParams.reload();
                };

                // $rootScope.onTableHandling();

                $scope.isFilter = function () {
                    if ($scope.tableParams == null) return false; //table not yet initialized
                    var obj = $scope.tableParams.filter();
                    for (var i in obj) {
                        if (obj.hasOwnProperty(i) && obj[i]) {
                            return true;
                        }
                    }
                    return false;
                };

                agreementService.getEarliestAgreementDate().success(function (date) {
                    $scope.initDatePicker(date);
                    $scope.tableParams = new ngTableParams({
                            page: 1,
                            count: 10,
                            sorting: {
                                id: 'desc'
                            }, filter: {
                                startDateToSearch: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                                endDateToSearch: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD"),
                                deviceType: null
                            }
                        },
                        {
                            total: 0,
                            filterDelay: 10000,
                            getData: function ($defer, params) {

                                var sortCriteria = Object.keys(params.sorting())[0];
                                var sortOrder = params.sorting()[sortCriteria];

                                if ($scope.selectedDeviceType.name != null) {
                                    params.filter().deviceType = $scope.selectedDeviceType.name.id;
                                }
                                else {
                                    params.filter().deviceType = null; //case when the filter is cleared with a button on the select
                                }

                                params.filter().startDateToSearch = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                                params.filter().endDateToSearch = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

                                agreementService.getPage(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                                    .success(function (result) {
                                        $scope.resultsCount = result.totalItems;
                                        $defer.resolve(result.content);
                                        params.total(result.totalItems);
                                    }, function (result) {
                                        $log.debug('error fetching data:', result);
                                    });
                            }
                        });
                    //$rootScope.onTableHandling();
                });

                /**
                 * Opens modal window for adding new agreement.
                 */
                $scope.openAddAgreementModal = function () {
                    var addAgreementModal = $modal.open({
                        animation: true,
                        controller: 'AgreementAddController',
                        templateUrl: 'resources/app/admin/views/modals/agreement-add-modal.html',
                        size: 'md',
                        resolve: {
                            agreement: function () {
                                return undefined;
                            }
                        }
                    });

                    /**
                     * executes when modal closing
                     */
                    addAgreementModal.result.then(function () {
                        $scope.popNotification($filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_ADDED_AGREEMENT'));
                    });
                };
                /**
                 * Opens modal window for editing agreement
                 */
                $scope.openEditAgreementModal = function (agreementId) {
                    agreementService.getAgreementById(agreementId).then(
                        function (agreement) {
                            var deviceDTOModal = $modal
                                .open({
                                    animation: true,
                                    controller: 'AgreementAddController',
                                    templateUrl: 'resources/app/admin/views/modals/agreement-add-modal.html',
                                    size: 'md',
                                    resolve: {
                                        agreement: function () {
                                            return agreement.data;
                                        }
                                    }
                                });

                            /**
                             * executes when modal closing
                             */
                            deviceDTOModal.result.then(function () {
                                $scope.popNotification($filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_EDITED_AGREEMENT'));
                            });
                        });

                };

                $scope.disableAgreement = function (id) {
                    agreementService.disableAgreement(id).then(function () {
                        $scope.popNotification($filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_DISABLED_AGREEMENT'));
                    });

                    $timeout(function () {
                        console.log('delete with timeout');
                        $rootScope.onTableHandling();
                    }, 700);
                };

                $scope.popNotification = function (title, text) {
                    toaster.pop('success', title, text);
                };

                $scope.dateOptions = {
                    formatYear: 'yyyy',
                    startingDay: 1,
                    showWeeks: 'false'
                };

                $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
                $scope.format = $scope.formats[2];


            }]);