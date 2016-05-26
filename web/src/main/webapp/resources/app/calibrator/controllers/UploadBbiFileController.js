
angular
    .module('employeeModule')

    .controller('UploadBbiFileController', ['$scope', '$rootScope', '$route', '$filter', '$log', '$modalInstance',
        'calibrationTest', 'Upload', '$timeout', 'parseBbiFile',
        function ($scope, $rootScope, $route, $filter, $log, $modalInstance, verification, Upload, $timeout, parseBbiFile) {

	    	/**
	         * Closes modal window on browser's back/forward button click.
	         */ 
	    	$scope.$on('$locationChangeStart', function() {
			    $modalInstance.close();
			});
    	
            $scope.cancel = function () {
                $modalInstance.close("cancel");
            };

            $scope.$watch('files', function () {
                $scope.upload($scope.files);
            });

            $scope.uploaded = false;

            $scope.progressPercentage = 0;

            $scope.fileName;

            $scope.upload = function (files) {
                if (files && files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        Upload.upload({
                            url: 'calibrator/verifications/new/upload?verificationId=' + verification,
                            file: file
                        }).progress(function (evt) {
                            $scope.uploaded = true;
                            $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        }).success(function (data, status, headers, config) {
                                $timeout(function () {
                                    if (status === 200) {
                                        $scope.messageError = null;
                                        $scope.fileName = config.file.name;
                                        $scope.messageSuccess = $filter('translate')('UPLOAD_SUCCESS') + config.file.name;
                                        parseBbiFile(data);
                                        $modalInstance.close("cancel");
                                    } else {
                                        $scope.messageError = $filter('translate')('UPLOAD_FAIL') + config.file.name;
                                        $scope.progressPercentage = parseInt(0);
                                        $scope.uploaded = false;
                                    }
                                });
                            }
                        )
                            .error(function () {
                                $scope.messageError = $filter('translate')('UPLOAD_FAIL') + config.file.name;
                                $scope.progressPercentage = parseInt(0);
                            })
                    }
                }
            };

        }]);

