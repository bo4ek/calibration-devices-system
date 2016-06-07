angular
    .module('employeeModule')
    .controller('NotStandardVerificationControllerProvider', [
        '$rootScope',
        '$scope',
        '$log',
        '$modal',
        'NotStandardVerificationServiceProvider',
        'VerificationServiceProvider',
        'ngTableParams',
        '$filter',
        'toaster', 'ProfileService',
        function ($rootScope, $scope, $log, $modal, notStandardVerificationService, verificationServiceProvider,
                  ngTableParams, $filter, toaster, profileService) {
            $scope.totalItems = 0;
            $scope.pageContent = [];
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    id: 'desc'
                }
            }, {
                total: 0,
                getData: function ($defer, params) {

                    notStandardVerificationService.getPage(params.page(), params.count())
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

            $scope.addProviderEmployee = function (verificationId) {
                profileService.havePermissionToAssignPerson()
                    .then(function (response) {
                        if (response.data) {
                            var modalInstance = $modal.open({
                                animation: true,
                                backdrop: 'static',
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

                                if ($scope.idsOfVerifications[0] == undefined) {
                                    $scope.idsOfVerifications[0] = verificationId;
                                }
                                var dataToSend = {
                                    idsOfVerifications: $scope.idsOfVerifications,
                                    employeeProvider: formData.provider
                                };
                                $scope.idsOfVerifications = []
                                notStandardVerificationService
                                    .sendEmployeeProvider(dataToSend)
                                    .success(function () {
                                        $log.info($scope.idsOfVerifications);
                                        $scope.tableParams.reload();
                                        $rootScope.$broadcast('verification-sent-to-calibrator');
                                        toaster.pop('success', $filter('translate')('INFORMATION'),
                                            $filter('translate')('SUCCESS_ASSIGN_AND_SENT'));
                                    });
                            });
                        } else {
                            notStandardVerificationService
                                .assignEmployeeProvider(verificationId)
                                .then(function () {
                                    $scope.tableParams.reload();
                                });
                        }
                    })
            };
            /**
             * Modal window used to explain the reason of verification rejection
             */
            $scope.openMailModal = function (ID) {
                var modalInstance = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'resources/app/provider/views/modals/mailComment.html',
                    controller: 'MailSendingModalControllerProvider',
                    size: 'md',
                });

                /**
                 * executes when modal closing
                 */
                modalInstance.result.then(function (formData) {
                    var dataToSend = {
                        verificationId: ID,
                        message: formData.message
                    };
                    notStandardVerificationService.rejectVerification(dataToSend).success(function () {
                        $scope.tableParams.reload();
                        $rootScope.$broadcast('verification-sent-to-calibrator');
                        toaster.pop('success', $filter('translate')('INFORMATION'),
                            $filter('translate')('SUCCESS_REJECTED_AND_SENT'));
                    });
                });
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
                    $scope.resolveVerificationId(verification.id);
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


        }]);


