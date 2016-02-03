angular
    .module('employeeModule')
    .controller('NotStandardVerificationControllerCalibrator', [
        '$rootScope',
        '$scope',
        '$log',
        '$modal',
        'NotStandardVerificationCalibratorService',
        'VerificationServiceCalibrator',
        'ngTableParams',
        '$filter',
        'toaster',

        function ($rootScope, $scope, $log, $modal, verificationService, verificationServiceCalibrator, ngTableParams, $filter, toaster) {
            $scope.totalItems = 0;
            $scope.pageContent = [];
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    providerFromBBI: 'desc'
                }
            }, {
                total: 0,
                getData: function ($defer, params) {

                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    verificationService.getPage(params.page(), params.count(), sortCriteria, sortOrder)
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

            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
                                    if (verifReadStatus == 'UNREAD') {
                                        $scope.markAsRead(verifId);
                                    }
                                    return verification;
                                });
                        }
                    }
                });
            };

            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        backdrop: 'static',
                        templateUrl: 'resources/app/calibrator/views/modals/verification-sending-to-provider.html',
                        controller: 'NotStandardVerificationSendingControllerCalibrator',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return verificationService.getProviders()
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
                            organizationId: formData.provider.id
                        };

                        verificationService.sendVerification(dataToSend)
                            .success(function () {
                                $log.debug('success sending');
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('verification-sent-to-provider');
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
        }]);


