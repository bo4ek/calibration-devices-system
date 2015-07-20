angular
    .module('welcomeModule')
    .factory('DataSendingService', ['$http', function ($http) {

        return {
            sendApplication: function (data) {
                return sendData('add', data);
            },
            editApplication: function (data) {
            	return sendData('edit', data);
            }
        };

        function sendData(url, data) {
            return $http.post('application/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }
    }]);
