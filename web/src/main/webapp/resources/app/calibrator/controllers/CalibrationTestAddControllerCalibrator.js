/**
 * Created by Konyk on 11.08.2015.
 */
angular
    .module('employeeModule')
    .controller('CalibrationTestAddControllerCalibrator', ['$rootScope', '$translate', '$scope', '$modal', '$http', '$log',
        'CalibrationTestServiceCalibrator', '$location', 'Upload', '$timeout', 'toaster', '$filter', 'InitializeLibForDigitalSign',
        function ($rootScope, $translate, $scope, $modal, $http, $log, calibrationTestServiceCalibrator, $location, Upload, $timeout, toaster, $filter, initializeLibForDigitalSign) {

            $scope.testId = $location.search().param;
            $scope.hasProtocol = $location.search().loadProtocol || false;
            $scope.isVerification = $location.search().ver || false;
            $scope.showReasons = false;
            $scope.rotateIndex = 0;

            $scope.reasonsUnsuitability = [];
            $scope.fileLoaded = false;

            $scope.TestDataFormData = [
                {},
                {},
                {},
                {},
                {},
                {}
            ];

            var setPointerEvents = function (element, enable) {
                element.style.pointerEvents = enable ? "auto" : "none";
            };

            $scope.selectPrivateKeyFile = function (event) {
                var enable = (event.target.files.length == 1);
                setPointerEvents(document.getElementById('PKeyReadButton'), enable);
                document.getElementById('PKeyPassword').disabled = enable ? '' : 'disabled';
                document.getElementById('PKeyFileName').value = enable ? event.target.files[0].name
                    : '';
                document.getElementById('PKeyPassword').value = '';
            };

            $scope.installPKeyModalLoad = function () {
                $http.get('resources/httpproxy.properties').then(function(response){
                    initializeLibForDigitalSign.initialize(response);
                    document.getElementById('PKeyFileInput').addEventListener('change',
                        $scope.selectPrivateKeyFile, false);
                })

            };


            $scope.showInstallPrivateKeyModal = function () {
                var modalInstance = $modal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'resources/app/calibrator/views/modals/insert-private-key-modal.html',
                    controller: 'InstallPrivateKeyController',
                    size: 'lg',
                    resolve: {
                        parentScope: function () {
                            return $scope;
                        }
                    }
                });
                modalInstance.opened.then($timeout(function () {
                    $scope.installPKeyModalLoad();
                }, 1000));


            };


            /**
             * Resets Test form
             */
            $scope.resetTestForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.TestForm = null;
                $scope.TestDataFormData = [
                    {},
                    {},
                    {},
                    {},
                    {},
                    {}
                ];
            };

            $scope.uploadBbiFile = function (testId) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/upload-bbiFile.html',
                    controller: 'UploadBbiFileController',
                    size: 'lg',
                    resolve: {
                        calibrationTest: function () {
                            return testId;
                        },
                        parseBbiFile: function () {
                            return $scope.parseBbiFile;
                        }
                    }
                });
            };


            $scope.parseBbiFile = function (data) {
                $scope.fileLoaded = true;
                $scope.TestForm = data;
                var date = $scope.TestForm.testDate;
                $scope.TestForm.testDate = moment(date).utcOffset(0).format("DD.MM.YY");
                $scope.TestForm.testTime = moment(date).utcOffset(0).format("HH:mm");
                $scope.TestForm.testPN = $scope.getPNFromBBIName(data.fileName);
                $scope.TestForm.testPhoto = "data:image/png;base64," + $scope.TestForm.testPhoto;
                $scope.TestDataFormData = data.listTestData;
                $scope.rotateIndex = data.rotateIndex;
                $scope.settingNumber = data.serialNumber + "  " + data.condDesignation;
                $scope.selectedReason.selected = data.reasonUnsuitabilityName;
                $scope.isReasonsUnsuitabilityShown();
            };

            $scope.getPNFromBBIName = function (fileName) {
                return fileName.substring(6, fileName.length - ".bbi".length);
            }

            /**
             * Get all reasons unsuitability for counter with {counterTypeId} type if
             * at least one of test has result 'NOT_PROCESSED'
             */
            $scope.isReasonsUnsuitabilityShown = function () {
                if ($scope.isTestRaw()) {
                    calibrationTestServiceCalibrator.getReasonsUnsuitability($scope.TestForm.counterTypeId).success(function (reasons) {
                        $scope.reasonsUnsuitability = reasons;
                        $scope.showReasons = true;
                    });
                } else {
                    $scope.showReasons = false;
                }
            };

            $scope.isTestRaw = function () {
                if ($scope.hasProtocol && $scope.isVerification && $scope.TestDataFormData) {
                    for (var i = 0; i < $scope.TestDataFormData.length; i++) {
                        if ($scope.TestDataFormData[i].testResult == 'NOT_PROCESSED') {
                            return true;
                        }
                    }
                }
                return false;
            };


            $scope.showEditMainPhotoModal = function (id) {
                if (!$scope.TestForm.signed) {
                    var modalInstance = $modal.open({
                        animation: true,
                        windowClass: 'preview-protocol-image',
                        templateUrl: 'resources/app/calibrator/views/modals/edit-main-photo-modal.html',
                        controller: 'EditPhotoController',
                        size: 'md',
                        resolve: {
                            photoId: function () {
                                return id;
                            },
                            parentScope: function () {
                                return $scope;
                            }
                        }
                    });
                }
            };

            $scope.showEditPhotoModal = function (id) {
                if (!$scope.TestForm.signed) {
                    var modalInstance = $modal.open({
                        animation: true,
                        windowClass: 'preview-protocol-image',
                        templateUrl: 'resources/app/calibrator/views/modals/edit-photo-modal.html',
                        controller: 'EditPhotoController',
                        size: 'md',
                        resolve: {
                            photoId: function () {
                                return id;
                            },
                            parentScope: function () {
                                return $scope;
                            }
                        }
                    });
                }
            };

            $scope.setMainPhoto = function (data) {
                $scope.TestForm.testPhoto = data;
            };

            function getCalibrationTests() {
                calibrationTestServiceCalibrator
                    .getCalibrationTests()
                    .then(function (data) {
                        $scope.calibrationTests = data.calibrationTests;
                    })
            }

            function getProtocolTest(verificationID) {
                calibrationTestServiceCalibrator
                    .getTestProtocol(verificationID)
                    .then(function (data) {
                        $scope.parseBbiFile(data);
                    });
            }

            $scope.selectedReason = {};

            //TODO check situation when there not Protocol
            if ($scope.hasProtocol) {
                getProtocolTest($scope.testId);
            } else {
                getCalibrationTests();
            }

            function retranslater() {
                protocol = {
                    fileName: $scope.TestForm.fileName,
                    accumulatedVolume: $scope.TestForm.accumulatedVolume,
                    counterNumber: $scope.TestForm.counterNumber,
                    temperature: $scope.TestForm.temperature,
                    installmentNumber: $scope.TestForm.installmentNumber,
                    latitude: $scope.TestForm.latitude,
                    longitude: $scope.TestForm.longitude,
                    testResult: $scope.TestForm.testResult,
                    status: $scope.getStatus(),
                    listTestData: $scope.TestForm.listTestData,
                    counterProductionYear: $scope.TestForm.counterProductionYear,
                    counterTypeId: $scope.TestForm.counterTypeId,
                    reasonUnsuitabilityId: $scope.getReasonId(),
                    volumeInDevice: $scope.volumeInDevice,
                    rotateIndex: $scope.rotateIndex
                }
            }

            /**
             * Get id of reason unsuitability by it name
             * @returns {id} of reason unsuitability
             */
            $scope.getReasonId = function () {
                if (!$scope.showReasons || !$scope.selectedReason) {
                    return null;
                } else {
                    for (var i = 0; i < $scope.reasonsUnsuitability.length; i++) {
                        if ($scope.reasonsUnsuitability[i].name == $scope.selectedReason.selected) {
                            return $scope.reasonsUnsuitability[i].id;
                        }
                    }
                }
            };

            /**
             * Get status of verification
             * @returns {'TEST_NOT'} if test was failed, {'TEST_OT'} if test was success
             */
            $scope.getStatus = function () {
                if($scope.isVerification) {
                    if ($scope.TestForm.testResult == 'FAILED') {
                        return 'TEST_NOK';
                    } else {
                        return 'TEST_OK';
                    }
                } else {
                    return 'TEST_COMPLETED';
                }
            };

            $scope.closeForm = function () {
                window.history.back();
            };

            $scope.signCalibrationTest = function () {
                retranslater();
                calibrationTestServiceCalibrator
                    .editTestProtocol(protocol, $scope.testId)
                    .then(function (status) {
                        if (status == 200) {
                            calibrationTestServiceCalibrator
                                .signTestProtocol($scope.testId)
                                .then(function (file) {
                                    $scope.fileToSign = file;
                                    var modalInstance = $modal.open({
                                        animation: true,
                                        backdrop: 'static',
                                        templateUrl: 'resources/app/calibrator/views/modals/insert-private-key-modal.html',
                                        controller: 'InstallPrivateKeyController',
                                        size: 'lg',
                                        resolve: {
                                            parentScope: function () {
                                                return $scope;
                                            },
                                            aFile: function () {
                                                return $scope.fileToSign;
                                            }
                                        }
                                    });
                                    modalInstance.opened.then($timeout(function () {
                                        $scope.installPKeyModalLoad();
                                    }, 1000));
                                    modalInstance.result.then(function (signedFile) {
                                            $scope.signedFile = signedFile;
                                            calibrationTestServiceCalibrator.signEDSTestProtocol($scope.signedFile, $scope.testId)
                                                .then(function () {
                                                    $scope.TestForm.signed = true;
                                                    toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESS_SIGNED'));
                                                });
                                        }
                                    );
                                })
                        }
                    })
            };

            $scope.updateCalibrationTest = function () {
                retranslater();
                calibrationTestServiceCalibrator
                    .editTestProtocol(protocol, $scope.testId)
                    .then(function (status) {
                        if (status == 201) {
                            $rootScope.onTableHandling();
                        }
                        if (status == 200) {
                            toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESSFUL_EDITED'));
                        }
                    });
            };

            /**
             * Return is positive or negative consumption status result
             * @param consumptionStatus
             * @returns {boolean} true - if test result is positive, false - in other case
             */
            $scope.consumptionStatusResult = function (consumptionStatus) {
                if (consumptionStatus == 'IN_THE_AREA') {
                    return true;
                } else {
                    return false;
                }
            };

            /**
             * Return is positive or negative common test result
             * @param testResult
             * @returns {boolean} true - if test result is positive, false - in other case
             */
            $scope.commonTestResult = function (testResult) {
                if (testResult == 'SUCCESS') {
                    return true;
                } else {
                    return false;
                }
            }

            $scope.rotateLeft = function () {
                $scope.rotateIndex -= 90;
                if ($scope.rotateIndex == -90) {
                    $scope.rotateIndex = 270;
                }
            };

            $scope.rotateRight = function () {
                $scope.rotateIndex += 90;
                if ($scope.rotateIndex == 360) {
                    $scope.rotateIndex = 0;
                }
            };

            $scope.rotate180 = function () {
                $scope.rotateIndex += 180;
                if ($scope.rotateIndex >= 360) {
                    $scope.rotateIndex -= 360;
                }
            };
        }]);