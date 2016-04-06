angular
    .module('employeeModule')
    .controller(
        'EditAllUserVerificationsInTaskControllerCalibrator',
        ['$scope', '$log', '$modalInstance',  '$rootScope', '$modal', 'ngTableParams',
            'CalibrationTaskServiceCalibrator',
        function ($scope, $log, $modalInstance, $rootScope, $modal,ngTableParams, CalibrationTaskServiceCalibrator) {

               // $scope.selectedData.verificationWithDismantle = true;

                $rootScope.$on('$locationChangeStart', function () {
                    $modalInstance.close();
                });

                $scope.cancel = function () {
                    $modalInstance.close();
                };

                $scope.openEditVerificationModal = function (verificationId) {
                    $rootScope.verifIDforEditing = verificationId;
                    $rootScope.editAll = $scope.selectedData.verificationWithDismantle;
                    var editVerificationModal = $modal.open({
                        animation: true,
                        controller: 'AddingVerificationsControllerCalibrator',
                        templateUrl: 'resources/app/calibrator/views/modals/initiate-verification.html',
                        size: 'lg'
                    });
                    editVerificationModal.result.then(function() {
                        $scope.tableParams.reload();
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
                                sortCriteria, sortOrder, $rootScope.taskId)
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