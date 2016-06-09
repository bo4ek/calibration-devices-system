angular
    .module('employeeModule')

    .controller('UploadArchiveController', ['$scope', '$rootScope', '$route', '$filter', '$log', '$modalInstance',
        'Upload', '$timeout', 'uploadForStation',
        function ($scope, $rootScope, $route, $filter, $log, $modalInstance, Upload, $timeout, uploadForStation) {

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $scope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.cancel = function () {
                $modalInstance.close("cancel");

            };

            $scope.$watch('files', function () {
                $scope.upload($scope.files);
            });
            $scope.$watch('file', function () {
                if ($scope.file != null) {
                    $scope.files = [$scope.file];
                }
            });

            $scope.uploaded = false;

            $scope.progressPercentage = 0;


            $scope.upload = function (files) {
                if (files && files.length) {
                    var pathUrl;
                    if (uploadForStation) {
                        pathUrl = 'calibrator/verifications/new/upload-archive-for-station';
                    } else {
                        pathUrl = 'calibrator/verifications/new/upload-archive';
                    }
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        Upload.upload({
                            url: pathUrl,
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
                                        $scope.uploadedBBIOutcomes = data;
                                        var isSuccessful = function (obj) {
                                            return obj.success ? true : false;
                                        };
                                        var isNotSuccessful = function (obj) {
                                            return !obj.success;
                                        };
                                        $scope.unsuccessfulBBIs = data.filter(isNotSuccessful).length;
                                        $scope.successfulBBIs = data.filter(isSuccessful).length;
                                        $scope.totalBBIs = $scope.uploadedBBIOutcomes.length;
                                    } else {
                                        $scope.messageError = $filter('translate')('UPLOAD_FAIL') + config.file.name;
                                        $scope.progressPercentage = parseInt(0);
                                        $scope.uploaded = false;
                                    }


                                    var textFile = null,
                                        makeTextFile = function (text) {
                                            var result = "\r\n" + $filter('translate')('FILE_NAME') + ":" + config.file.name;
                                            result = result.concat("\r\n" + $filter('translate')('TOTAL') + ":" + $scope.totalBBIs);
                                            result = result.concat("\r\n" + $filter('translate')('DOWNLOAD_SUCCESS') + ":" + $scope.successfulBBIs);
                                            result = result.concat("\r\n" + $filter('translate')('DOWNLOAD_FAIL') + ":" + $scope.unsuccessfulBBIs + "\r\n");

                                            angular.forEach($scope.uploadedBBIOutcomes, function (obj) {
                                                result = result.concat("\r\n" + $filter('translate')('FILE_NAME') + ":" + obj.bbiFileName);
                                                if (obj.success == false) {
                                                    result = result.concat("\r\n" + $filter('translate')('STATUS') + ":" + $filter('translate')('UPLOAD_FAIL'));
                                                }
                                                if (obj.success == true) {
                                                    result = result.concat("\r\n" + $filter('translate')('STATUS') + ":" + $filter('translate')('UPLOAD_SUCCESS'));
                                                }
                                                if (obj.reasonOfRejection != null) {
                                                    result = result.concat("\r\n" + $filter('translate')('REASON') + " : " + $filter('translate')(obj.reasonOfRejection));
                                                }
                                                result = result.concat("\r\n");
                                            });

                                            var data = new Blob([result], {type: 'text/plain'});
                                            if (textFile !== null) {
                                                window.URL.revokeObjectURL(textFile);
                                            }
                                            textFile = window.URL.createObjectURL(data);
                                            return textFile;
                                        };

                                    var create = document.getElementById('create');
                                    create.addEventListener('click', function () {
                                        var link = document.getElementById('downloadlink');
                                        link.href = makeTextFile(data);
                                        link.style.display = 'block';
                                    }, false);
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

