angular
    .module('employeeModule')
    .controller('NewVerificationsControllerProvider', ['$scope', '$log', '$modal', 'VerificationServiceProvider', '$rootScope',
        'ngTableParams', '$filter', '$timeout', '$translate', 'ProfileService', '$window', '$location', 'toaster',
        function ($scope, $log, $modal, verificationServiceProvider, $rootScope, ngTableParams, $filter,
                  $timeout, $translate, profileService, $window, $location,  toaster ) {

            $scope.resultsCount = 0;
            $scope.isSentVerifications = true;

            /**
             * this function return true if is StateVerificatorEmployee
             */
            $scope.isVerificatorEmployee = function () {
                verificationServiceProvider.getIfEmployeeProvider().success(function (data) {
                    $scope.isEmployee = data;
                });

            };

            $scope.isVerificatorEmployee();

            $scope.clearAll = function () {
                $scope.selectedStatus = null;
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

            verificationServiceProvider.getNewVerificationEarliestDate().success(function (date) {
                $scope.initDatePicker(date);
                $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 10,
                    sorting: {
                        date: 'desc'
                    }, filter: {
                        date: $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD"),
                        endDate: $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD"),
                        status: 'SENT'
                    }
                }, {
                    total: 0,
                    filterDelay: 1000,
                    getData: function ($defer, params) {

                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];

                        params.filter().status = 'SENT';

                        params.filter().date = $scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD");
                        params.filter().endDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");

                        verificationServiceProvider.getNewVerifications(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
                            .success(function (result) {

                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                                $scope.allVerifications = result.content;
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                });
            });

            $scope.$on('provider-save-verification', function (event, args) {
                $scope.tableParams.reload();
            });


            $scope.markAsRead = function (id) {
                var dataToSend = {
                    verificationId: id,
                    readStatus: 'READ'
                };

                verificationServiceProvider.markVerificationAsRead(dataToSend).success(function () {
                    $rootScope.$broadcast('verification-was-read');
                    $scope.tableParams.reload();
                });
            };


            /**
             * open modal
             */
            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {

                var modalInstance = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'resources/app/provider/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerProvider',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceProvider.getNewVerificationDetails(verifId)
                                .success(function (verification) {
                                    $rootScope.verificationID = verifId;
                                    verification.id = $rootScope.verificationID;
                                    verification.initialDate = verifDate;
                                    if (verifReadStatus == 'UNREAD') {
                                        $scope.markAsRead(verifId);
                                    }
                                    return verification;
                                });
                        }
                    }
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            $scope.removeProviderEmployee = function (verifId) {
                var dataToSend = {
                    idVerification: verifId
                };
                $log.info(dataToSend);
                verificationServiceProvider.cleanProviderEmployeeField(dataToSend)
                    .success(function () {
                        $scope.tableParams.reload();
                    });
            };

            $scope.addProviderEmployee = function (verificationId) {
                profileService.havePermissionToAssignPerson()
                    .then(function (response) {
                        if (response.data) {
                            var modalInstance = $modal.open({
                                animation: true,
                                templateUrl: 'resources/app/provider/views/modals/adding-providerEmployee.html',
                                controller: 'ProviderEmployeeControllerProvider',
                                size: 'md',
                                windowClass: 'xx-dialog',
                                resolve: {
                                    providerEmploy: function () {
                                        return verificationServiceProvider.getProviders()
                                            .success(function (providers) {
                                                    return providers;
                                                }
                                            );
                                    }
                                }
                            });
                            /**
                             * executes when modal closing
                             */
                            modalInstance.result.then(function (formData) {
                                var idVerification = 0;
                                var dataToSend = {
                                    idVerification: verificationId,
                                    employeeProvider: formData.provider
                                };
                                $log.info(dataToSend);
                                verificationServiceProvider
                                    .sendEmployeeProvider(dataToSend)
                                    .success(function () {
                                        $scope.tableParams.reload();
                                    });
                            });
                        } else {
                            verificationServiceProvider
                                .assignEmployeeProvider(verificationId)
                                .then(function () {
                                    $scope.tableParams.reload();
                                });
                        }
                    })
            };

            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;
            $scope.idsOfCalibrators = null;

            $scope.checkProviderEmployee = function () {
                var result = false;
                angular.forEach($scope.allVerifications, function (verification) {
                    if (verification.providerEmployee) {
                        result = true;
                    }
                });
                return result;
            };
            var getAllSelected = function () {
                if (!$scope.allVerifications) {
                    return false;
                }

                var checkedItems = $scope.allVerifications.filter(function (verification) {
                    return verification.selected;
                });

                return checkedItems.length === $scope.allVerifications.length;
            };

            var setAllSelected = function (value) {
                angular.forEach($scope.allVerifications, function (verification) {
                    verification.selected = value;
                    if (verification.providerEmployee) {
                        $scope.resolveVerificationId(verification.id);
                    }
                });
            };

            $scope.allSelected = function (value) {
                if (value !== undefined) {
                    return setAllSelected(value);
                } else {
                    return getAllSelected();
                }
            };

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

            $scope.resolveVerification = function (verification) {
                if ($scope.selectedSentVerification && $scope.selectedSentVerification !== verification) {
                    $scope.selectedSentVerification.selected = false;
                }
                $scope.selectedSentVerification = verification;
                $scope.idsOfVerifications[0] = verification.id;

            };

            /**
             * check if idsOfVerifications array is empty
             */
            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };
            
            $scope.initiateVerification = function () {

                $rootScope.verifIDforTempl = $scope.idsOfVerifications[0];
                var modalInstance = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'resources/app/provider/views/modals/initiate-verification.html',
                    controller: 'AddingVerificationsControllerProvider',
                    size: 'lg'
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            $scope.openRejectVerificationModal = function (verificationId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/reject-verification-modal.html',
                    controller: 'RejectVerificationProviderController',
                    size: 'md',
                    windowClass: 'xx-dialog',
                    resolve: {
                        rejectVerification: function () {
                            return verificationServiceProvider.receiveAllReasons()
                                .success(function (reasons) {
                                        return reasons;
                                    }
                                );
                        }
                    }
                });

                modalInstance.result.then(function (reason) {
                    verificationServiceProvider.rejectVerificationByIdAndReason(verificationId, reason.name.id).then(function (data) {
                        switch (data.status) {
                            case 200:
                            {
                                $scope.tableParams.reload();
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('REJECTED'));
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
            }

        }]);

