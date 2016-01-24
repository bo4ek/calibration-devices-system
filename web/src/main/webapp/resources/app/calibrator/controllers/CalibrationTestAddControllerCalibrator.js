/**
 * Created by Konyk on 11.08.2015.
 */
angular
    .module('employeeModule')
    .controller('CalibrationTestAddControllerCalibrator', ['$rootScope', '$translate', '$scope', '$modal', '$http', '$log',
        'CalibrationTestServiceCalibrator', '$location', 'Upload', '$timeout', 'toaster', '$filter',
        function ($rootScope, $translate, $scope, $modal, $http, $log, calibrationTestServiceCalibrator, $location, Upload, $timeout, toaster, $filter) {

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
            $scope.servers = [
                {
                    "issuerCNs": ["Акредитований центр сертифікації ключів ІДД ДФС",
                        "Акредитований центр сертифікації ключів ІДД Міндоходів",
                        "Акредитований центр сертифікації ключів ІДД ДПС"],
                    "address": "acskidd.gov.ua",
                    "ocspAccessPointAddress": "acskidd.gov.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "acskidd.gov.ua",
                    "tspAddress": "acskidd.gov.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК органів юстиції України",
                        "АЦСК Держінформ'юсту"],
                    "address": "ca.informjust.ua",
                    "ocspAccessPointAddress": "ca.informjust.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "ca.informjust.ua",
                    "tspAddress": "ca.informjust.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["ЦСК Укрзалізниці"],
                    "address": "csk.uz.gov.ua",
                    "ocspAccessPointAddress": "csk.uz.gov.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "csk.uz.gov.ua",
                    "tspAddress": "csk.uz.gov.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["Регіональний ЦСК Дніпропетровської області"],
                    "address": "ca.dp.gov.ua",
                    "ocspAccessPointAddress": "ca.dp.gov.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "ca.dp.gov.ua",
                    "tspAddress": "ca.dp.gov.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["ЦСК \"MASTERKEY\" ТОВ \"АРТ-МАСТЕР\"",
                        "ТОВ \"Арт-мастер\""],
                    "address": "masterkey.ua",
                    "ocspAccessPointAddress": "masterkey.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "masterkey.ua",
                    "tspAddress": "masterkey.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК ТОВ \"КС\""],
                    "address": "ca.ksystems.com.ua",
                    "ocspAccessPointAddress": "ca.ksystems.com.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "ca.ksystems.com.ua",
                    "tspAddress": "ca.ksystems.com.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК ДП \"УСС\""],
                    "address": "csk.uss.gov.ua",
                    "ocspAccessPointAddress": "csk.uss.gov.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "csk.uss.gov.ua",
                    "tspAddress": "csk.uss.gov.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК Публічного акціонерного товариства \"УкрСиббанк\""],
                    "address": "csk.ukrsibbank.com",
                    "ocspAccessPointAddress": "csk.ukrsibbank.com/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "csk.ukrsibbank.com",
                    "tspAddress": "csk.ukrsibbank.com",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК ПАТ КБ «ПРИВАТБАНК»",
                        "АЦСК «ПРИВАТБАНК»"],
                    "address": "acsk.privatbank.ua",
                    "ocspAccessPointAddress": "acsk.privatbank.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "acsk.privatbank.ua",
                    "tspAddress": "acsk.privatbank.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["Акредитований центр сертифікації ключів Збройних Сил"],
                    "address": "ca.mil.gov.ua",
                    "ocspAccessPointAddress": "ca.mil.gov.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "ca.mil.gov.ua",
                    "tspAddress": "ca.mil.gov.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["АЦСК ТОВ \"Центр сертифікації ключів \"Україна\"",
                        "ТОВ \"Центр сертифікації ключів \"Україна\""],
                    "address": "uakey.com.ua",
                    "ocspAccessPointAddress": "uakey.com.ua",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "uakey.com.ua",
                    "tspAddress": "uakey.com.ua",
                    "tspAddressPort": "80"
                },
                {
                    "issuerCNs": ["ТОВ \"Український сертифікаційний центр\""],
                    "address": "ocsp.ukrcc.com",
                    "ocspAccessPointAddress": "ocsp.ukrcc.com",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "",
                    "tspAddress": "",
                    "tspAddressPort": ""
                },
                {
                    "issuerCNs": ["АЦСК Державної казначейської служби України",
                        "ЦСК Державної казначейської служби України"],
                    "address": "",
                    "ocspAccessPointAddress": "",
                    "ocspAccessPointPort": "",
                    "cmpAddress": "",
                    "tspAddress": "",
                    "tspAddressPort": "",
                    "certsInKey": true
                },
                {
                    "issuerCNs": ["АЦСК 'eSign' ТОВ 'Алтерсайн'"],
                    "address": "ca.altersign.com.ua",
                    "ocspAccessPointAddress": "ca.altersign.com.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "",
                    "tspAddress": "",
                    "tspAddressPort": ""
                },
                {
                    "issuerCNs": ["Тестовий ЦСК АТ \"ІІТ\""],
                    "address": "ca.iit.com.ua",
                    "ocspAccessPointAddress": "ca.iit.com.ua/services/ocsp/",
                    "ocspAccessPointPort": "80",
                    "cmpAddress": "ca.iit.com.ua",
                    "tspAddress": "ca.iit.com.ua",
                    "tspAddressPort": "80"
                }
            ];

            $scope.setPointerEvents = function (element, enable) {
                element.style.pointerEvents = enable ? "auto" : "none";
            };

            $scope.selectPrivateKeyFile = function (event) {
                var enable = (event.target.files.length == 1);
                $scope.setPointerEvents(document.getElementById('PKeyReadButton'), enable);
                document.getElementById('PKeyPassword').disabled = enable ? '' : 'disabled';
                document.getElementById('PKeyFileName').value = enable ? event.target.files[0].name
                    : '';
                document.getElementById('PKeyPassword').value = '';
            };


            $scope.loadServers = function () {
                var out = "<select>";
                for (var i = 0; i < $scope.servers.length; i++) {
                    out += '<option>' + $scope.servers[i].issuerCNs[0] + '</option>';
                }
                out += "</select";
                document.getElementById("CAsServersSelect").innerHTML = out;
            };

            $scope.installPKeyModalLoad = function () {
                $scope.loadServers();
                document.getElementById('PKeyFileInput').addEventListener('change',
                    $scope.selectPrivateKeyFile, false);
                document.getElementById('FileToSign').addEventListener('change',
                    $scope.chooseFileToSign, false);
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
                $scope.TestForm.testDate = moment(date).utcOffset(0).format("DD.MM.YYYY HH:mm");
                $scope.TestForm.testPhoto = "data:image/png;base64," + $scope.TestForm.testPhoto;
                $scope.TestDataFormData = data.listTestData;
                $scope.rotateIndex = data.rotateIndex;
                $scope.selectedReason.selected = data.reasonUnsuitabilityName;
                $scope.isReasonsUnsuitabilityShown();
            };

            /**
             * Get all reasons unsuitability for counter with {counterTypeId} type if
             * at least one of test has result 'RAW'
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
                        if ($scope.TestDataFormData[i].testResult == 'RAW') {
                            return true;
                        }
                    }
                }
                return false;
            };


            $scope.showEditMainPhotoModal = function (id) {
                console.log($scope.TestForm.signed);
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
                        $log.debug("inside");
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
                if ($scope.TestForm.testResult == 'FAILED') {
                    return 'TEST_NOK';
                } else {
                    return 'TEST_OK';
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
                                .then(function (status) {
                                    if (status == 200) {
                                        $scope.TestForm.signed = true;
                                        toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESS_SIGNED'));
                                    }
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
            $scope.consumptionStatusResult = function(consumptionStatus) {
                if(consumptionStatus == 'IN_THE_AREA') {
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
            $scope.commonTestResult = function(testResult) {
                if (testResult == 'SUCCESS') {
                    return true;
                } else {
                    return false;
                }
            }

            $scope.rotateLeft = function () {
                $scope.rotateIndex-= 90;
                if ($scope.rotateIndex == -90) {
                    $scope.rotateIndex = 270;
                }
            };

            $scope.rotateRight = function () {
                $scope.rotateIndex+= 90;
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