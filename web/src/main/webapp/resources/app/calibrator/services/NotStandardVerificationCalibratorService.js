angular
    .module('employeeModule')
    .factory('NotStandardVerificationCalibratorService', ['$http',
        function ($http) {
            return {
                getPage: function (status, currentPage, itemsPerPage, sortCriteria, sortOrder) {

                    return getDataWithParams('/calibrator/not-standard-verifications/' + status + '/' +currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder);
                },
                sendVerification: function (verification) {
                    var url = '/calibrator/not-standard-verifications/send';
                    return $http.put(url, verification)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                },
                getProviders: function (url) {
                    var url = '/calibrator/not-standard-verifications/providers';
                    return $http.get(url)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                }
            };
            function getDataWithParams(url, params) {
                return $http.get(url, {
                    params: params
                }).success(function (data) {
                    return data;
                }).error(function (err) {
                    return err;
                });
            }

        }]);