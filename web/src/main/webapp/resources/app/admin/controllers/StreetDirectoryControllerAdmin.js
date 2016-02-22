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

            $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 5
                },
                {
                    total: 0,
                    filterDelay: 1000,
                    getData: function ($defer, params) {
                        var sortCriteria = Object.keys(params.sorting())[0];
                        var sortOrder = params.sorting()[sortCriteria];
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
                $scope.tableParams.filter({});
            };


        }]);

