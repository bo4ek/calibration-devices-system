angular
    .module('employeeModule')
    .controller('ArchivalVerificationsControllerVerificator', ['$scope', '$modal', '$log', 'VerificationServiceVerificator', 'ngTableParams', '$filter', '$rootScope', '$timeout', '$translate',

        function ($scope, $modal, $log, verificationServiceVerificator, ngTableParams, $filter, $rootScope, $timeout, $translate) {

            $scope.resultsCount = 0;

            $scope.clearAll = function () {
                $scope.selectedStatus.name = null;
                $scope.tableParams.filter({});
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            /**
             *  Date picker and formatter setup
             *
             */

            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function () {

                $scope.myDatePicker.pickerDate = {

                };

                if ($scope.defaultDate == null) {
                    //copy of original daterange
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

                $scope.setTypeDataLangDatePicker = function () {
                    var lang = $translate.use();
                    if (lang === 'ukr') {
                        moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                    } else {
                        moment.locale('en'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                    }
                    $scope.opts = {
                        format: 'DD-MM-YYYY',
                        singleDatePicker: true,
                        showDropdowns: true,
                        eventHandlers: {}
                    };
                };

                $scope.setTypeDataLangDatePicker();
            };

            $scope.showPicker = function ($event) {
                angular.element("#datepickerfield").trigger("click");
            };

            $scope.clearDate = function () {
                //daterangepicker doesn't support null dates
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
                $scope.tableParams.filter({});
            };

            $scope.initDatePicker();

            $scope.selectedStatus = {
                name: null
            };

            $scope.statusData = [
                { id: 'TEST_OK', label: null },
                { id: 'TEST_NOK', label: null }
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'Перевірено придатний';
                    $scope.statusData[1].label = 'Перевірено непридатний';

                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'Tested OK';
                    $scope.statusData[1].label = 'Tested NOK';

                }
                $scope.setTypeDataLangDatePicker();
            };

            $scope.setTypeDataLanguage();

            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    date: 'desc'
                }
            }, {
                total: 0,
                filterDelay: 1500,
                getData: function ($defer, params) {

                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    if ($scope.myDatePicker.pickerDate.startDate != null) {
                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                    }

                    if ($scope.selectedStatus.name != null) {
                        params.filter().status = $scope.selectedStatus.name.id;
                    }

                    verificationServiceVerificator.getArchiveVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder).success(function (result) {
                        $scope.resultsCount = result.totalItems;
                        $defer.resolve(result.content);
                        params.total(result.totalItems);
                    }, function (result) {
                        $log.debug('error fetching data:', result);
                    });
                }
            });

            $scope.checkFilters = function () {
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
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
                            return verificationServiceVerificator.getArchivalVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
                                    return verification;
                                });
                        }
                    }
                });
            };

        }]);
