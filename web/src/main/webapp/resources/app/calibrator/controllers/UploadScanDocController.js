
angular
    .module('employeeModule')

    .controller('UploadScanDocController', ['$scope', '$rootScope', '$route', '$filter', '$log', '$modalInstance',
         'Upload', '$timeout','parentScope', 'checkIsScanDoc',
        function ($scope, $rootScope, $route, $filter, $log, $modalInstance, Upload, $timeout , parentScope, checkIsScanDoc) {

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


            $scope.upload = function (files) {
                if (files && files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        Upload.upload({
                            url: 'calibrator/calibrationTests/uploadScanDoc/' + parentScope.idOfManualTest,
                            file: file
                        }).progress(function (evt) {
                            $scope.uploaded = true;
                            $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        }).success(function (data, status, headers, config) {
                                $timeout(function () {
                                    if (status === 200) {
                                        parentScope.pathToScanDoc = data;
                                        checkIsScanDoc();
                                        $scope.messageError = null;
                                        $scope.messageSuccess = $filter('translate')('UPLOAD_SUCCESS') + config.file.name;
                                        $timeout(function () {
                                            $modalInstance.close("cancel");
                                        }, 2000);
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