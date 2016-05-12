angular
    .module('employeeModule')
    .factory('NotStandardVerificationServiceProvider', ['$http',
        function ($http) {
            return {
                getPage: function (pageNumber, itemsPerPage) {
                    var url = '/provider/not-standard-verifications/' + pageNumber + '/' + itemsPerPage;
                    return getDataWithParams(url);
                },
                sendEmployeeProvider: function (verification) {
                    var url = '/provider/not-standard-verifications/assign/providerEmployee';
                    return $http.put(url, verification)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                },
                assignEmployeeProvider: function (verificationId) {
                    var url = '/provider/not-standard-verifications/assign/providerEmployee/' + verificationId;
                    return $http.put(url)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                },
                rejectVerification : function(verification) {
                    var url = '/provider/not-standard-verifications/new/reject';
                    return $http.put(url, verification)
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