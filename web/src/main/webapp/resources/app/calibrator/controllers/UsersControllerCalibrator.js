/**
 * Created by MAX on 30.07.2015.
 */
angular
    .module('employeeModule')
    .controller('UsersControllerCalibrator', ['$scope', 'UserServiceCalibrator', 'UserService', '$modal', '$log',
        'ngTableParams', '$timeout', '$filter', '$rootScope', '$translate',
        function ($scope, UserServiceCalibrator, userService, $modal, $log, ngTableParams, $timeout, $filter, $rootScope,
                  $translate) {
            $scope.totalEmployee = 0;


            $scope.clearAll = function () {
                $scope.selectedUserType.name = null;
                $scope.tableParams.filter({});
            };

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.selectedUserType = {
                name: null
            };

            //types
            $scope.userTypeData = [
                {id: 'CALIBRATOR_EMPLOYEE', label: null},
                {id: 'CALIBRATOR_ADMIN', label: null},
            ];


            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.userTypeData[0].label = 'Робітник';
                    $scope.userTypeData[1].label = 'Адмін';

                } else if (lang === 'eng') {
                    $scope.userTypeData[0].label = 'Employee';
                    $scope.userTypeData[1].label = 'Admin';
                } else {
                    $log.debug(lang);
                }
            };


            $scope.setTypeDataLanguage();

            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 5,
                sorting: {
                    lastName: 'asc'     // initial sorting
                }, filter: {
                    role: null
                }
            }, {
                total: 0,

                getData: function ($defer, params) {
                    if ($scope.selectedUserType.name != null) {
                        params.filter().role = $scope.selectedUserType.name.id;
                    }
                    else {
                        params.filter().role = null;//case when the filter is cleared with a button on the select
                    }
                    UserServiceCalibrator.getPage(params.page(), params.count(), params.filter(), params.sorting())
                        .success(function (result) {
                            $scope.totalEmployee = result.totalItems;
                            $defer.resolve(result.content);
                            params.total(result.totalItems);

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
                    templateUrl: 'resources/app/calibrator/views/employee/capacity-calibratorEmployee.html',
                    controller: 'CapacityEmployeeControllerCalibrator',
                    size: 'lg',
                    resolve: {

                        capacity: function () {
                            return UserServiceCalibrator.getCapacityOfWork(username)
                                .success(function (verifications) {
                                    return verifications;
                                });
                        }
                    }
                });
            };


            $scope.onTableHandling = function () {

                UserServiceCalibrator.isAdmin()
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


            $scope.openAddEmployeeModal = function () {
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

            /**
             * update table with employees after edit or add new employee
             */
            $scope.$on('new-employee-added', function () {
                $scope.tableParams.reload();
            });


        }]);

