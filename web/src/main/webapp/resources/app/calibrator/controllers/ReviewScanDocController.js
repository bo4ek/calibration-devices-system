angular
    .module('employeeModule')

    .controller('ReviewScanDocController', ['$scope', '$rootScope', '$route', '$filter', '$log', '$modalInstance',
        '$timeout', 'parentScope',
        function ($scope, $rootScope, $route, $filter, $log, $modalInstance, $timeout, parentScope) {

            $scope.scanDoc = parentScope.dataScanDoc;



        }]);