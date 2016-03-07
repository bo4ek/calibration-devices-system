angular
    .module('employeeModule')
    .factory('DigitalVerificationProtocolsServiceCalibrator', ['$http',
        function ($http) {
            return {
                getProtocols: function (currentPage, itemsPerPage, search, sortCriteria, sortOrder) {
                    return getDataWithParams('calibrator/protocols/' + currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
                },
                sendProtocols: function (protocol) {
                    var url = '/calibrator/protocols/send';
                    return $http.put(url, protocol)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                },
                getVerificators: function (url) {
                    var url = '/calibrator/protocols/verificators';
                    return $http.get(url)
                        .success(function (data) {
                            return data;
                        }).error(function (err) {
                            return err;
                        });
                },

                getNewVerificationEarliestDate: function () {
                    var url = '/calibrator/protocols/earliestDate/creatingProtocol';
                    return $http.get(url)
                        .success(function (data) {
                            return data;
                        })
                        .error(function (err) {
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