angular
    .module('employeeModule')
    .controller(
    'VerificationListModalController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$filter',
        'ngTableParams',
        'taskID',
        'CalibrationTaskServiceCalibrator',
        function ($rootScope, $scope, $translate, $modalInstance, $filter, ngTableParams, taskID,
                  CalibrationTaskServiceCalibrator) {

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

            $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 5,
                    sorting: {
                        'clientData.clientAddress.street': 'asc'
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
                                params.total(result.totalItems);
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                });

        }]);