angular
    .module('adminModule')
    .controller(
    'StreetDirectoryControllerAdmin',
    [
        '$rootScope',
        '$scope',
        '$modal',
        '$http',
        '$filter',
        'ngTableParams',
        '$translate',
        '$timeout',
        'toaster',
        'AddressService',
        function ($rootScope, $scope, $modal, $http, $filter, ngTableParams, $translate, $timeout, toaster, AddressService) {

            $scope.filterData = {};
            $scope.filterData.selectedRegion = 'Київ'; //default value for filter
            $scope.filterData.selectedCity = 'Київ'; //default value for filter

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 10
                },
                {
                    total: 0,
                    filterDelay: 5000,
                    getData: function ($defer, params) {
                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];
                        if ($scope.filterData.selectedRegion != null) {
                            params.filter().region = $scope.filterData.selectedRegion;
                        }
                        if ($scope.filterData.selectedCity != null) {
                            params.filter().city = $scope.filterData.selectedCity;
                        }
                        AddressService.getAllStreets(params.page(), params.count(), params.filter(), sortCriteria, sortOrder).then(function (response) {
                            $scope.resultsCount = response.totalItems;
                            $defer.resolve(response.content);
                            params.total($scope.resultsCount);
                        });
                    }
                });

            $scope.isFilter = function () {
                if (!$scope.tableParams) {
                    return false;
                }
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
                }
                return false;
            };

            $scope.clearAll = function () {
                $scope.filterData.selectedRegion = null;
                $scope.filterData.selectedCity = null;
                $scope.tableParams.filter({});
            };

            /**
             * Opens modal window for adding new calibration module.
             */
            $scope.openAddStreetModal = function () {
                var addStreetModal = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    controller: 'StreetAddModalControllerAdmin',
                    templateUrl: '/resources/app/admin/views/modals/street-add-modal.html',
                    size: 'md',
                    resolve: {
                        street: undefined
                    }
                });
                /**
                 * executes when modal closing
                 */
                addStreetModal.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

        }]);

