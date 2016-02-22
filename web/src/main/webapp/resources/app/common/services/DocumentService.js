angular
    .module('employeeModule')
    .factory('DocumentService', ['$http', '$window', function ($http, $window) {
        return {
            getDocument: function (documentType, verificationId, fileFormat) {
                var url = "doc/" + documentType + "/" + verificationId + "/" + fileFormat;
                return $http.get(url, {responseType: 'arraybuffer'})
                    .then(function (response) {
                        return response;

                    });
            },
            addSignToDocument: function (documentType, verificationId, fileFormat, file, signature) {

                var url = "doc/" + documentType + "/" + verificationId + "/" + fileFormat + "/" + "signed";
                var formData = new FormData();
                formData.append('file', file);
                formData.append('signature', signature);
                console.log("form data " + formData);
                return $http.post(url, formData, {
                    transformRequest: angular.identity,
                    headers: { 'Content-Type': undefined},
                    responseType: 'arraybuffer'})
                    .then(function (response) {
                        var file = new Blob([response.data], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});
                        console.log(file);
                        return file;
                    });
            },
            isSignedCertificate: function (verificationId) {
                var url = "certificate/isSigned/" + verificationId;
                return $http.get(url)
                    .success(function (result) {
                        return result;
                    });
            },
            isParsedCertificate: function (verificationId){
                var url = "certificate/isParsed/" + verificationId;
                return $http.get(url)
                    .success(function (result) {
                        return result;
                    });
            }
        };
    }]);
