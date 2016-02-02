angular
    .module('employeeModule')
    .controller('VerificatorSubdivisionController', ['$rootScope', '$scope', '$modal', 'VerificatorSubdivisionService',
        '$timeout', '$filter', 'toaster',
        function ($rootScope, $scope, $modal, verificatorSubdivisionService, $timeout, $filter, toaster) {
            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPerPage = 5;
            $scope.pageContent = [];
            $scope.searchData = null;

            /**
             * Updates the table with subdivisions
             */
            $rootScope.onTableHandling = function () {
                verificatorSubdivisionService.getPage($scope.currentPage, $scope.itemsPerPage, $scope.searchData)
                    .then(function (data) {
                        $scope.pageContent = data.content;
                        $scope.totalItems = data.totalItems;
                    })
            };
            $rootScope.onTableHandling();

            /**
             * Opens modal window for adding new subdivision
             */
            $scope.openAddSubdivisionModal = function () {
                $modal.open({
                        animation: true,
                        controller: 'VerificatorSubdivisionAddModalController',
                        templateUrl: 'resources/app/verificator/views/modals/verificator-subdivision-add-modal.html'
                    })
            };

            /**
             * Opens modal window for editing subdivision
             */
            $scope.openEditSubdivisionModal = function (subdivisionId) {
                verificatorSubdivisionService.getSubdivisionWithId(subdivisionId)
                    .then(function (data) {
                        $modal.open({
                                animation: true,
                                controller: 'VerificatorSubdivisionEditModalController',
                                templateUrl: 'resources/app/verificator/views/modals/verificator-subdivision-edit-modal.html',
                                resolve: {
                                    subdivision: function () {
                                        return data;
                                    }
                                }
                            })
                    });
            };

            /**
             * Delete verificator's subdivision
             */
            $scope.deleteSubdivision = function (id) {
                verificatorSubdivisionService.deleteSubdivision(id)
                    .then(function (status) {
                        if (status == 200) {
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_DELETE_SUBDIVISION'));
                        } else {
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_DELETE_SUBDIVISION'));
                        }
                    });
                $timeout(function () {
                    $rootScope.onTableHandling();
                }, 700);
            }
        }
    ]);