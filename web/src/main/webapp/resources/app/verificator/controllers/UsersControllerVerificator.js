
angular
    .module('employeeModule')
    .controller('UsersControllerVerificator', ['$scope', 'UserServiceVerificator', 'UserService', '$modal',
        '$log', 'ngTableParams', '$timeout', '$filter', '$rootScope',
        function ($scope, UserServiceVerificator, userService, $modal, $log, ngTableParams,
                  $timeout, $filter, $rootScope) {
            $scope.totalEmployee = 0;
            $scope.cantAddEmployee;

            $scope.tableParams = new ngTableParams({
                    page: 1,
                    count: 5,
                    sorting: {
                        lastName: 'asc'
                    }
                },
                {
                    total: 0,
                    getData: function ($defer, params) {
                        UserServiceVerificator.getPage(params.page(), params.count(), params.filter(), params.sorting())
                            .success(function (result) {
                                $scope.totalEmployee = result.totalItems;
                                $defer.resolve(result.content);
                                params.total(result.totalItems);
                                $scope.cantAddNewEmployee();
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    }
                });

            $scope.isFilter = function () {
                var obj = $scope.tableParams.filter();
                for (var i in obj) {
                    if (obj.hasOwnProperty(i) && obj[i]) {
                        return true;
                    }
                }
                return false;
            };

            $scope.showCapacity = function (username) {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/verificator/views/employee/capacity-verificatorEmployee.html',
                    controller: 'CapacityEmployeeControllerVerificator',
                    size: 'lg',
                    resolve: {

                        capacity: function () {
                            return UserServiceVerificator.getCapacityOfWork(username)
                                .success(function (verifications) {
                                    return verifications;
                                });
                        }
                    }
                });
            };

            $scope.onTableHandling = function () {
                UserServiceVerificator.isAdmin()
                    .success(function (response) {
                        var roles = response + '';
                        var role = roles.split(',');
                        var thereIsAdmin = 0;
                        for (var i = 0; i < role.length; i++) {
                            if (role[i] === 'PROVIDER_ADMIN') {
                                thereIsAdmin++;
                            }
                            if (role[i] === 'CALIBRATOR_ADMIN') {
                                thereIsAdmin++;
                            }
                            if (role[i] === 'STATE_VERIFICATOR_ADMIN') {
                                thereIsAdmin++;
                            }
                        }
                        if (thereIsAdmin > 0) {
                            $scope.verificator = true;
                        } else {
                            $scope.accessLable = true;
                        }
                    });
            };

            /**
             * this method use code of views and Controllers from provider
             */
            $scope.openAddEmployeeModal = function () {
                var addEmployeeModal = $modal
                    .open({
                        animation: true,
                        size: 'lg',
                        controller: 'AddEmployeeController',
                        templateUrl: 'resources/app/provider/views/employee/employee-add-modal.html'
                    });
            };

            $scope.onTableHandling();

            /**
             * open edit modal and use code from Provider
             * (views and Controllers that give opportunity to
             * change data in Verificator employee)
             * @param username
             */
            $scope.openEditEmployeeModal = function (username) {
                userService.getUser(username)
                    .then(function (data) {
                        $modal.open({
                            animation: true,
                            size: 'lg',
                            controller: 'EditEmployeeController',
                            templateUrl: 'resources/app/provider/views/employee/employee-edit-modal.html',
                            resolve: {
                                user: function () {
                                    return data.data;
                                }
                            }
                        });
                    });
            };

            /**
             * check if we can to add new employee
             */
            $scope.cantAddNewEmployee = function () {
                userService.getOrganizationEmployeeCapacity()
                    .success(function (data) {
                            $scope.organizationEmployeesCapacity = data;
                            $scope.cantAddEmployee = $scope.totalEmployee >= $scope.organizationEmployeesCapacity;
                        }
                    );
            };

            /**
             * update table with employees after edit or add new employee
             */
            $scope.$on('new-employee-added', function () {
                $scope.tableParams.reload();
            });

        }]);