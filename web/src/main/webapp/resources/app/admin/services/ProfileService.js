angular
    .module('adminModule')
    .factory('ProfileService', function ($http) {
        return {
            getUser: function(){
                return getData('employee/profile/get');
            },
            updateUser: function (userData) {
                return saveData('employee/profile/update', userData);
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
