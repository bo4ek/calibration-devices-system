angular
    .module('adminModule')
    .controller(
    'CounterTypePanelController',
    [
        '$rootScope',
        '$scope',
        '$modal',
        '$http',
        'CounterTypeService',
        'ngTableParams',
        '$timeout',
        '$filter',
        'toaster',
        function ($rootScope, $scope, $modal, $http, counterTypeService, ngTableParams, $timeout, $filter, toaster) {
            /**
             * init of page parametres
             */
            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPerPage = 5;
            $scope.pageContent = [];

            /**
             * Clear filtering fields
             */
            $scope.clearAll = function () {
                $scope.tableParams.filter({});
            };

            /**
             * Sorting and filtering of table
             * @type {ngTableParams|*}
             */
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 10,
                sorting: {
                    id: 'desc'
                }
            }, {
                total: 0,
                filterDelay: 10000,
                getData: function ($defer, params) {

                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];

                    counterTypeService.getPage(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
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
             * Updates the table with counter type params.
             */
            $rootScope.onTableHandling = function () {
                $scope.tableParams.reload();
            };
            /**
             * initializing table params
             */
            $rootScope.onTableHandling();

            /**
             * Function for ng-show. When filtering fields are not empty show button for
             * clear this fields
             * @returns {boolean}
             */
            $scope.isFilter = function () {
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
                }
                return false;
            };
            /**
             * Opens modal window for adding new counter type.
             */
            $scope.openAddCounterTypeModal = function() {
                var addCounterTypeCounter = $modal.open({
                    animation : true,
                    controller : 'CounterTypeAddController',
                    templateUrl : 'resources/app/admin/views/modals/counter-type-add-modal.html',
                    size: 'lg',
                    resolve: {
                        devices: function () {
                            console.log(counterTypeService.findAllDevices());
                            return counterTypeService.findAllDevices();
                        }
                    }
                });
                /**
                 * executes when modal closing
                 */
                addCounterTypeCounter.result.then(function () {
                    toaster.pop('success',$filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_ADDED_COUNTER_TYPE'));
                });
            };

            /**
             * Opens modal window for editing countner type.
             */
            $scope.openEditCounterTypeModal = function(
                counterTypeId) {
                $rootScope.counterTypeId = counterTypeId;
                counterTypeService.getCounterTypeById(
                    $rootScope.counterTypeId).then(
                    function(data) {
                        $rootScope.countersType = data;
                        console.log($rootScope.countersType);

                        var counterTypeDTOModal = $modal
                            .open({
                                animation : true,
                                controller : 'CounterTypeEditController',
                                templateUrl : 'resources/app/admin/views/modals/counter-type-edit-modal.html',
                                size: 'lg',
                                resolve: {
                                    devices: function () {
                                        console.log(counterTypeService.findAllDevices());
                                        return counterTypeService.findAllDevices();
                                    }
                                }
                            });

                        counterTypeDTOModal.result.then(function () {
                            toaster.pop('info', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_EDITED_COUNTER_TYPE'));
                        });
                    });

            };


           $rootScope.closeDelete = function(id) {
               $rootScope.counterTypeId = id;
               $modal.open({
                   animation : true,
                   templateUrl: 'resources/app/admin/views/modals/close-alert-delete.html',
                   controller : 'AdminCloseAlertDeleteController'

               })
           };

            $scope.$on('delete', function(event, args) {
                $scope.deleteCounterType($rootScope.counterTypeId);
            });

            /**
             * deleting of counter type
             * @param id
             */
            $scope.deleteCounterType = function (id) {

                console.log(id);
                counterTypeService.deleteCounterType(id).then(function (status) {
                    if (status == 409){
                        toaster.pop('error', $filter('translate')('INFORMATION'), $filter('translate')('ERROR_DELETED_COUNTER_TYPE'));
                    } else {
                        toaster.pop('info', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_DELETED_COUNTER_TYPE'));
                    }
                });
                $timeout(function() {
                    console.log('delete with timeout');
                    $rootScope.onTableHandling();
                }, 700);
            };


        }]);