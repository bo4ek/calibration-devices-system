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
        '$location',

        function ($rootScope, $scope, $log, $modal, verificationService, verificationServiceCalibrator, ngTableParams, $filter, toaster, $location) {
            $scope.totalItems = 0;
            $scope.pageContent = [];
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    client_last_name: 'desc'
                }
            }, {
                total: 0,
                getData: function ($defer, params) {
                    var status;
                    if ($location.absUrl().indexOf("verifications-for-provider") != -1) {
                        status = "CREATED_FOR_PROVIDER";
                    } else {
                        status = "CREATED_BY_CALIBRATOR";
                    }

                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    verificationService.getPage(status, params.page(), params.count(), sortCriteria, sortOrder)
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

            $scope.openDetails = function (verificationId, verificationReadStatus) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalControllerCalibrator',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationServiceCalibrator.getNewVerificationDetails(verificationId)
                                .success(function (verification) {
                                    if (verificationReadStatus == 'UNREAD') {
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