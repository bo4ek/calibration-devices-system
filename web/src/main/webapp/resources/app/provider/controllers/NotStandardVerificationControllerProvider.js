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
                        }, function (result) {
                            $log.debug('error fetching data:', result);
                        });
                }
            });
            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;
            $scope.idsOfCalibrators = null;

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
                                var idVerification = 0;
                                var dataToSend = {
                                    idVerification: verificationId,
                                    employeeProvider: formData.provider
                                };
                                $log.info(dataToSend);
                                notStandardVerificationService
                                    .sendEmployeeProvider(dataToSend)
                                    .success(function () {
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
                $log.debug('ID');
                $log.debug(ID);
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

        }]);


