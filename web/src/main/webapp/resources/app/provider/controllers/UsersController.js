angular
    .module('employeeModule')
    .controller('UsersController', ['$scope', 'UserService', 'EmployeeService', '$modal', '$log', 'ngTableParams',
        function ($scope, userService, employeeService, $modal, $log, ngTableParams) {
            $scope.totalEmployee = 0;

            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 5,
                sorting: {
                    lastName: 'asc'     
                }
            }, {
                total: 0,
                getData: function ($defer, params) {
                    userService.getPage(params.page(), params.count(), params.filter(), params.sorting())
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
                    templateUrl: 'resources/app/provider/views/employee/capacity-providerEmployee.html',
                    controller: 'CapacityEmployeeControllerProvider',
                    size: 'lg',
                    resolve: {
                        capacity: function () {
                            return userService.getCapacityOfWork(username)
                                .success(function (verifications) {
                                    return verifications;
                                });
                        }
                    }
                });
            };

            $scope.onTableHandling = function () {
                userService.isAdmin()
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


            $scope.openAddUserModal = function () {
                var addEmployeeModal = $modal
                    .open({
                        animation: true,
                        controller: 'AddEmployeeController',
                        size: 'lg',
                        templateUrl: 'resources/app/provider/views/employee/employee-add-modal.html',
                    });
            };

            $scope.onTableHandling();

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

            $scope.cantAddNewEmployee = function () {
                userService.getOrganizationEmployeeCapacity().success(
                    function (data) {
                        $scope.organizationEmployeesCapacity = data;
                        $scope.cantAddEmployee = $scope.totalEmployee >= $scope.organizationEmployeesCapacity;
                    });
            };

            $scope.$on('new-employee-added', function () {
                $scope.tableParams.reload();
            });
        }
    ]);