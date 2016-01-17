angular
    .module('employeeModule')
    .factory('DocumentService', function ($http) {
        return {
            getDocument: function (documentType, verificationId, fileFormat) {
                var url = "doc/" + documentType + "/" + verificationId + "/" + fileFormat;
                return $http.get(url, {responseType:'arraybuffer'})
                    .success(function (response) {
                        var file = new Blob([response], {type: 'application/pdf'});
                        var fileURL = URL.createObjectURL(file);
                        console.log(file);
                        console.log(fileURL);
                        return file;
                    });
            },
            isSignedCertificate: function(verificationId) {
                var url = "certificate/isSigned/" + verificationId;
                return $http.get(url)
                    .success(function (result) {
                        return result;
                    });
            }
        };
    });
