angular
    .module('adminModule')
    .controller('UsersController', ['$scope', 'UsersService', '$log', 'ngTableParams', '$timeout', '$filter', '$rootScope',
        'toaster', '$translate',
        function ($scope, userService, $log, ngTableParams, $timeout, $filter, $rootScope, toaster, $translate) {


            $scope.clearAll = function () {
                $scope.selectedUserType.name = null;
                $scope.tableParams.filter({});
            };

            $scope.selectedUserType = {
                name: null
            };

            $scope.userTypeData = [
                {id: 'CALIBRATOR_EMPLOYEE', label: null},
                {id: 'CALIBRATOR_ADMIN', label: null},
                {id: 'PROVIDER_EMPLOYEE', label: null},
                {id: 'PROVIDER_ADMIN', label: null},
                {id: 'STATE_VERIFICATOR_EMPLOYEE', label: null},
                {id: 'STATE_VERIFICATOR_ADMIN', label: null}
            ];

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.userTypeData[0].label = 'Робітник вимірювальної лабораторії';
                    $scope.userTypeData[1].label = 'Адмін вимірювальної лабораторії';
                    $scope.userTypeData[2].label = 'Робітник постачальник послуг';
                    $scope.userTypeData[3].label = 'Адмін постачальник послуг';
                    $scope.userTypeData[4].label = 'Робітник уповноваженої повірочної лабораторії';
                    $scope.userTypeData[5].label = 'Адмін уповноваженої повірочної лабораторії';
                } else if (lang === 'eng') {
                    $scope.userTypeData[0].label = 'Employee calibrator';
                    $scope.userTypeData[1].label = 'Admin calibrator';
                    $scope.userTypeData[2].label = 'Employee provider';
                    $scope.userTypeData[3].label = 'Admin provider';
                    $scope.userTypeData[4].label = 'Employee state verificator';
                    $scope.userTypeData[5].label = 'Admin state verificator';
                } else {
                    $log.debug(lang);
                }
            };


            $scope.setTypeDataLanguage();

            $scope.doSearch = function () {
                $scope.tableParams.reload();
            };

            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 5,
                sorting: {
                    lastname: 'asc'     // initial sorting
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
                    var sortCriteria = Object.keys(params.sorting())[0];
                    var sortOrder = params.sorting()[sortCriteria];


                    userService.getPage(params.page(), params.count(), params.filter(), sortCriteria, sortOrder)
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

        }]);