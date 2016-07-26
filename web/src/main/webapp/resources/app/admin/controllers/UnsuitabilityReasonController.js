angular
    .module('adminModule')
    .controller(
        'UnsuitabilityReasonController',
        [
            '$rootScope',
            '$scope',
            '$log',
            '$modal',
            'UnsuitabilityReasonService',
            'ngTableParams',
            '$timeout',
            '$filter',
            'toaster',
            function ($rootScope, $scope, $log, $modal, unsuitabilityReasonService, ngTableParams, $timeout, $filter, toaster) {
                /**
                 * init of page params
                 */
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
                        unsuitabilityReasonService.getPage(params.page(), params.count())
                            .success(function (result) {
                                $scope.resultsCount = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                });
                /**
                 * Updates the table with unsuitability reasons params.
                 */
                $rootScope.onTableHandling = function () {
                    $scope.tableParams.reload();
                };
                /**
                 * initializing table params
                 */
                $rootScope.onTableHandling();


                /**
                 * Opens modal window for adding new unsuitability reason.
                 */
                $scope.openAddUnsuitabilityReasonModal = function () {
                    var modalInstance = $modal.open({
                        animation: true,
                        backdrop: 'static',
                        templateUrl: 'resources/app/admin/views/modals/unsuitability-reason-add-modal.html',
                        controller: 'UnsuitabilityReasonAddModalController',
                        size: 'md',
                        resolve: {
                            devices: function () {
                                return unsuitabilityReasonService.getDevices().success(function (data) {
                                    return data;
                                })
                            }
                        }
                    });
                    /**
                     * executes when modal closing
                     */
                    modalInstance.result.then(function (formData) {
                        var dataToAdd = {
                            name: formData.name,
                            deviceId: formData.deviceName.id
                        };
                        unsuitabilityReasonService.saveUnsuitabilityReason(dataToAdd)
                            .success(function () {
                                $scope.tableParams.reload();
                                $rootScope.$broadcast('save-new-reason');
                                toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_ADDED_NEW_REASON'));
                            });

                    });
                };

                /**
                 * Opens modal window for disabling/removing calibration module
                 */
                $scope.openDeleteUnsuitabilityReasonModal = function (id) {
                    var deleteModal = $modal
                        .open({
                            animation: true,
                            backdrop: 'static',
                            controller: 'UnsuitabilityReasonDeleteModalController',
                            templateUrl: '/resources/app/admin/views/modals/reason-delete.html',
                            size: 'md',
                            windowClass: 'center-modal',
                            resolve: {
                                'id': function () {
                                    return id;
                                }
                            }
                        });

                    /**
                     * executes when modal closing
                     */
                    deleteModal.result.then(function () {
                        if (status == 409) {
                            toaster.pop('info', $filter('translate')('INFORMATION'), $filter('translate')('ERROR_DELETED_REASON'));
                        } else {
                            toaster.pop('info', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_DELETED_REASON'));
                        }
                    });

                };

                /**
                 * Remove unsuitability reason by id
                 * @param id
                 */

            }]);