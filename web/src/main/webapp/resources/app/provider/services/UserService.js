angular
    .module('employeeModule')
    .factory('UserService', function ($http) {
        return {
            isUsernameAvailable: function (username) {
                return getData('employee/admin/users/available/' + username);
            },
            saveUser: function (userData) {
                return saveData('employee/admin/users/add', userData);
            },
            isAdmin: function () {
                return getData('employee/admin/users/verificator');  
            },
            getLoggedInUserRoles: function () {
                return getData('loginuser/roles');					
            },
            getPage: function (currentPage, itemsPerPage, searchObj, filterObj) {
                var field;
                var value;
                for (var key in filterObj) {
                    field = key;
                    value = filterObj[field];
                }
                value=='asc'?field=field:field="-"+field;
                return getData('employee/admin/users/' + currentPage + '/' + itemsPerPage + '/' +
                    field, searchObj);
            },
            getCapacityOfWork: function (username) {
                return getData('employee/admin/users/capacityOfEmployee' + '/' + username);
            },
            getGraficData: function (dataToSearch) {
                return getData('provider/admin/users/graphicCapacity', dataToSearch);
            },
            getGraficDataMainPanel: function(dataToSearch) {
                return getData('provider/admin/users/graphicmainpanel', dataToSearch);
            },
            getPieDataMainPanel: function(){
                return getData('provider/admin/users/piemainpanel');
            },
            getUser: function(username){
                return getData('employee/admin/users/getUser/' + username);
            },
            updateUser: function (userData) {
                return saveData('employee/admin/users/update', userData);
            },
            getOrganizationEmployeeCapacity: function () {
                return getData('employee/admin/users/organizationCapacity');
            },

            loggedInUser: function () {
                return getLoginUser();
            }
        };

        function getLoginUser() {
            return $http.get('loginuser')
                .then(function (result) {
                    return result.data;
                });
        }

        function getData(url, params) {
            return $http.get(url, {
                params: params,
                timeout: 10000
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }


        function saveData(url, data) {
            return $http.post(url, data)
                .success(function (response) {
                    return response;
                })
                .error(function (err) {
                    alert(err);
                    alert("in error");
                    return err;
                });
        }


    });
