angular
    .module('employeeModule')
    .controller(
        'VerificationListModalController',
        [
            '$rootScope',
            '$scope',
            '$translate',
            '$modalInstance',
            '$modal',
            '$filter',
            'ngTableParams',
            'taskID',
            'toaster',
            'CalibrationTaskServiceCalibrator',
            function ($rootScope, $scope, $translate, $modalInstance, $modal, $filter, ngTableParams, taskID, toaster, CalibrationTaskServiceCalibrator) {

                /**
                 * Closes modal window on browser's back/forward button click.
                 */
                $rootScope.$on('$locationChangeStart', function () {
                    $modalInstance.close();
                });

                /**
                 * Closes the modal window
                 */
                $rootScope.closeModal = function () {
                    $modalInstance.close();
                };

                $scope.openEditVerificationModal = function (verificationId) {
                    $rootScope.verifIDforEditing = verificationId;
                    var editVerificationModal = $modal.open({
                        animation: true,
                        controller: 'AddingVerificationsControllerCalibrator',
                        templateUrl: 'resources/app/calibrator/views/modals/initiate-verification.html',
                        size: 'lg'
                    });
                    editVerificationModal.result.then(function () {
                        $scope.tableParams.reload();
                    });
                };

                $scope.openCheckGroupVerificationModal = function (verificationId) {
                    $rootScope.verifIDforEditing = verificationId;

                    CalibrationTaskServiceCalibrator.hasVerificationGroup(verificationId)
                        .success(function (response) {
                            $scope.hasGroup = response;
                            if (!$scope.hasGroup) {
                                $rootScope.editAll = false;
                                $scope.openEditVerificationModal(verificationId);
                                return;
                            }
                            $rootScope.taskId = taskID;
                            $scope.editAll = true;
                            var editAllVerificationModal = $modal.open({
                                animation: true,
                                controller: 'EditAllUserVerificationsInTaskControllerCalibrator',
                                templateUrl: 'resources/app/calibrator/views/modals/check-group-tasks-verifications-modal.html',
                                size: 'md'
                            });
                            editAllVerificationModal.result.then(function () {
                                $scope.tableParams.reload();
                            });
                        });
                };

                $scope.inputQueue = function (verification, input, allverification) {

                    allverification.forEach(function (item, i, allverification) {
                        item.queue = i;
                    });

                    var current = verification.queue;

                    if (input < current) {
                        allverification.forEach(function (item, i, allverification) {
                            if (item.queue >= input && item.queue < current) {
                                item.queue++;
                            }
                        });
                    } else if (input > current) {
                        allverification.forEach(function (item, i, allverification) {
                            if (item.queue > current && item.queue <= input) {
                                item.queue--;
                            }
                        });
                    }

                    verification.queue = input;
                };

                $scope.VerificationPlanningTaskDTO = [];

                $scope.sendNewQueue = function () {
                    $scope.verifications.forEach(function (item, i) {
                        $scope.VerificationPlanningTaskDTO[i] = {"verificationId": item.verificationId, "queue": item.queue};
                    });

                    CalibrationTaskServiceCalibrator.sendVerificationWithQueue($scope.VerificationPlanningTaskDTO).then(function (result) {
                        if (result.status == 200) {
                            $scope.tableParams.reload();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_EDITED'));
                        } else if (result.status == 403) {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_ACCESS'));
                        } else {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_UPDATE_QUEUE'));
                        }
                    });
                };


                $scope.removeVerificationFromTask = function (verificationId) {
                    CalibrationTaskServiceCalibrator.removeVerificationFromTask(verificationId).then(function (result) {
                        if (result.status == 200) {
                            $scope.tableParams.reload();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('VERIFICATION_SUCCESSFULLY_REMOVED_FROM_TASK'));
                        } else {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_REMOVING_VERIFICATION_FROM_TASK'));
                        }
                    });
                };

                $scope.tableParams = new ngTableParams({
                        page: 1,
                        count: 100,
                        sorting: {
                            'queue': 'asc'
                        }
                    },
                    {
                        total: 0,
                        getData: function ($defer, params) {
                            var sortCriteria = Object.keys(params.sorting())[0];
                            var sortOrder = params.sorting()[sortCriteria];
                            CalibrationTaskServiceCalibrator.getVerificationsByTask(params.page(), params.count(),
                                    sortCriteria, sortOrder, taskID)
                                .success(function (result) {
                                    $scope.resultsCount = result.totalItems;
                                    $defer.resolve(result.content);
                                    $scope.verifications = result.content;
                                    params.total(result.totalItems);
                                }, function (result) {
                                    $log.debug('error fetching data:', result);
                                });
                        }
                    });

            }]);
