angular
    .module('adminModule')
    .factory('UsersService', function ($http) {
        return {
            getPage: function (currentPage, itemsPerPage, searchObj, sortCriteria, sortOrder) {
                return getData('admin/users/' + currentPage + '/' + itemsPerPage + '/'
                   + sortCriteria +  '/' + sortOrder, searchObj);
            },
            getSysAdminsPage : function (currentPage, itemsPerPage, searchObj, sortCriteria, sortOrder) {

                return getData('admin/sysadmins/get_sys_admins');
            },
            getSysAdminByUsername: function (username) {
                var url = 'admin/sysadmins/get_sys_admin/' + username;
                return $http.get(url).then(function (result) {
                    return result;
                });
            },

            editSysAdmin: function (formData, username) {
                var url = 'admin/sysadmins/edit/' + username;
                return $http.post(url, formData)
                    .then(function (result) {
                        return result.status;
                    });
            },

            deleteSysAdmin: function (username) {
                var url = 'admin/sysadmins/delete/' + username;
                return $http.delete(url)
                    .then(function (result) {
                        return result.status;
                    });
            },
            loggedInUser: function () {
                return getLoginUser();
            }
        }

        function getLoginUser() {
            return $http.get('loginuser')
                .then(function (result) {
                    return result.data;
                });
        }

        function getData(url, params) {
            return $http.get(url, {
                params: params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }

        function getDataWithParam(url, params) {
            return $http.get(url, {
                params : params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }


    });