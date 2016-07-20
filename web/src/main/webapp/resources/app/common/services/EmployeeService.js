angular
    .module('employeeModule')
    .factory('EmployeeService', function ($http) {
        return {
            getAll: function (pageNumber, itemsPerPage) {
                return getData('employee/admin/users/' + pageNumber + "/" + itemsPerPage);
            },
            getPage: function (currentPage, itemsPerPage, searchObj, filterObj) {
                var field;
                var value;
                for (var key in filterObj) {
                    field = key;
                    value = filterObj[field];
                }
                if (value != 'asc') {
                    field = "-" + field;
                }
                return getData('employee/admin/users/' + currentPage + '/' + itemsPerPage + '/' +
                    field, searchObj);
            }
        };

        function getData(url, params) {
            return $http.get(url, {
                params: params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }
    });
