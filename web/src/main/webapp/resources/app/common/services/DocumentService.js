angular
    .module('employeeModule')
    .factory('DocumentService', ['$http', '$window', function ($http, $window) {
        return {
            getDocument: function (documentType, verificationId, fileFormat) {
                var url = "doc/" + documentType + "/" + verificationId + "/" + fileFormat;
                return $http.get(url, {
                        responseType: 'arraybuffer',
                        timeout: 10000
                    })
                    .then(function (response) {
                        return response;

                    });
            },
            addSignToDocument: function (documentType, verificationId, signature) {
                var url = "doc/" + documentType + "/" + verificationId + "/" + "signed";
                var formData = new FormData();
                formData.append('signature', signature);
                return $http.post(url, formData
                    , {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined}
                    }
                    )
                    .then(function (response) {
                        return response;
                    });
            },
            isSignedCertificate: function (verificationId) {
                var url = "certificate/isSigned/" + verificationId;
                return $http.get(url)
                    .success(function (result) {
                        return result;
                    });
            },
            isParsedCertificate: function (verificationId) {
                var url = "certificate/isParsed/" + verificationId;
                return $http.get(url)
                    .success(function (result) {
                        return result;
                    });
            }
        };
    }]);
