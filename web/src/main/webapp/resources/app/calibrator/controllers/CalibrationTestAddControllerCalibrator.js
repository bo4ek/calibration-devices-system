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
            $scope.euSign = new EUSignCP();
            $scope.utils = new Utils($scope.euSign);
            $scope.privateKeyCerts = null;
            $scope.CAsServers = null;
            $scope.CAServer = {
                "issuerCNs": null,
                "address": null,
                "ocspAccessPointAddress": null,
                "ocspAccessPointPort": null,
                "cmpAddress": null,
                "tspAddress": null,
                "tspAddressPort": null
            };
            $scope.offline = false;
            $scope.useCMP = false;
            $scope.loadPKCertsFromFile = false;
            $scope.PrivateKeyNameSessionStorageName = "PrivateKeyName";
            $scope.PrivateKeySessionStorageName = "PrivateKey";
            $scope.PrivateKeyPasswordSessionStorageName = "PrivateKeyPassword";
            $scope.CertsLocalStorageName = "Certificates";
            $scope.CRLsLocalStorageName = "CRLs";
            $scope.CACertificatesSessionStorageName = "CACertificates";
            $scope.CAServerIndexSessionStorageName = "CAServerIndex";
            $scope.PrivateKeyCertificatesChainSessionStorageName = "PrivateKeyCertificatesChain";
            $scope.PrivateKeyCertificatesSessionStorageName = "PrivateKeyCertificates";
            $scope.URL_XML_HTTP_PROXY_SERVICE = "http://localhost:8080/Data/java/ProxyHandler.php";
            $scope.URL_GET_CERTIFICATES = "http://localhost:8080/Data/CACertificates.p7b?version=1.0.4";
            $scope.URL_CAS = "http://localhost:8080/Data/CAs.json?version=1.0.4";
            $scope.initialize = function () {
                var _onSuccess = function () {
                    try {
                        $scope.euSign.Initialize();
                        $scope.euSign.SetJavaStringCompliant(true);
                        $scope.euSign.SetCharset("UTF-16LE");

                        if ($scope.euSign.DoesNeedSetSettings()) {
                            $scope.setDefaultSettings();

                            if ($scope.utils.IsStorageSupported()) {
                                $scope.loadCertsAndCRLsFromLocalStorage();
                            } else {
                                alert("Локальне сховище не підтримується");
                            }
                        }
                        $scope.loadCertsFromServer();
                        $scope.setCASettings(0);

                        if ($scope.utils.IsSessionStorageSupported()) {
                            var _readPrivateKeyAsStoredFile = function () {
                                $scope.readPrivateKeyAsStoredFile();
                            };
                            setTimeout(_readPrivateKeyAsStoredFile, 10);
                        }
                    } catch (e) {
                        alert(e);
                    }
                };

                var _onError = function () {
                    alert('Виникла помилка ' +
                        'при завантаженні криптографічної бібліотеки');
                };

                $scope.loadCAsSettings(_onSuccess, _onError);
            };
            $scope.setDefaultSettings = function () {
                try {
                    $scope.euSign.SetXMLHTTPProxyService($scope.URL_XML_HTTP_PROXY_SERVICE);

                    var settings = $scope.euSign.CreateFileStoreSettings();
                    settings.SetPath("/certificates");
                    settings.SetSaveLoadedCerts(true);
                    $scope.euSign.SetFileStoreSettings(settings);

                    settings = $scope.euSign.CreateProxySettings();
                    $scope.euSign.SetProxySettings(settings);

                    settings = $scope.euSign.CreateTSPSettings();
                    $scope.euSign.SetTSPSettings(settings);

                    settings = $scope.euSign.CreateOCSPSettings();
                    $scope.euSign.SetOCSPSettings(settings);

                    settings = $scope.euSign.CreateCMPSettings();
                    $scope.euSign.SetCMPSettings(settings);

                    settings = $scope.euSign.CreateLDAPSettings();
                    $scope.euSign.SetLDAPSettings(settings);

                    settings = $scope.euSign.CreateOCSPAccessInfoModeSettings();
                    settings.SetEnabled(true);
                    $scope.euSign.SetOCSPAccessInfoModeSettings(settings);

                    var CAs = $scope.CAsServers;
                    settings = $scope.euSign.CreateOCSPAccessInfoSettings();
                    for (var i = 0; i < CAs.length; i++) {
                        settings.SetAddress(CAs[i].ocspAccessPointAddress);
                        settings.SetPort(CAs[i].ocspAccessPointPort);

                        for (var j = 0; j < CAs[i].issuerCNs.length; j++) {
                            settings.SetIssuerCN(CAs[i].issuerCNs[j]);
                            $scope.euSign.SetOCSPAccessInfoSettings(settings);
                        }
                    }
                } catch (e) {
                    alert("Виникла помилка при встановленні налашувань: " + e);
                }

            };
            $scope.loadFilesFromLocalStorage = function (localStorageFolder, loadFunc) {

                if (!$scope.utils.IsStorageSupported())
                    $scope.euSign.RaiseError(EU_ERROR_NOT_SUPPORTED);

                if ($scope.utils.IsFolderExists(localStorageFolder)) {
                    var files = $scope.utils.GetFiles(localStorageFolder);
                    for (var i = 0; i < files.length; i++) {
                        var file = $scope.utils.ReadFile(
                            localStorageFolder, files[i]);
                        loadFunc(files[i], file);
                    }
                    return files;
                }
                else {
                    $scope.utils.CreateFolder(localStorageFolder);
                    return null;
                }
            };

            $scope.setItemsToList = function (listId, items) {
                var output = [];
                for (var i = 0, item; item = items[i]; i++) {
                    output.push('<li><strong>', item, '</strong></li>');
                }

                document.getElementById(listId).innerHTML =
                    '<ul>' + output.join('') + '</ul>';

            };

            $scope.loadCertsAndCRLsFromLocalStorage = function () {

                try {
                    var files = $scope.loadFilesFromLocalStorage(
                        $scope.CertsLocalStorageName,
                        function (fileName, fileData) {
                            if (fileName.indexOf(".cer") >= 0)
                                $scope.euSign.SaveCertificate(fileData);
                            else if (fileName.indexOf(".p7b") >= 0)
                                $scope.euSign.SaveCertificates(fileData);
                        });
                    if (files != null && files.length > 0)
                        $scope.setItemsToList('SelectedCertsList', files);

                } catch (e) {
                    alert("Виникла помилка при завантаженні сертифікатів " +
                        "з локального сховища");

                }

                try {
                    var files = $scope.loadFilesFromLocalStorage(
                        $scope.CRLsLocalStorageName,
                        function (fileName, fileData) {
                            if (fileName.indexOf(".crl") >= 0) {
                                try {
                                    $scope.euSign.SaveCRL(true, fileData);
                                } catch (e) {
                                    $scope.euSign.SaveCRL(false, fileData);
                                }
                            }
                        });
                    if (files != null && files.length > 0)
                        $scope.setItemsToList('SelectedCRLsList', files);

                } catch (e) {
                    alert("Виникла помилка при завантаженні СВС з локального сховища");
                }

            };

            $scope.loadCertsFromServer = function () {
                var certificates = $scope.utils.GetSessionStorageItem(
                    $scope.CACertificatesSessionStorageName, true, false);
                if (certificates != null) {
                    try {
                        $scope.euSign.SaveCertificates(certificates);
                        return;
                    } catch (e) {
                        alert("Виникла помилка при імпорті " +
                            "завантажених з сервера сертифікатів " +
                            "до файлового сховища");
                    }
                }

                var _onSuccess = function (certificates) {
                    try {
                        $scope.euSign.SaveCertificates(certificates);
                        $scope.utils.SetSessionStorageItem(
                            $scope.CACertificatesSessionStorageName,
                            certificates, false);
                    } catch (e) {
                        alert("Виникла помилка при імпорті " +
                            "завантажених з сервера сертифікатів " +
                            "до файлового сховища");
                    }
                };

                var _onFail = function (errorCode) {
                    console.log("Виникла помилка при завантаженні сертифікатів з сервера. " +
                        "(HTTP статус " + errorCode + ")");
                };

                $scope.utils.GetDataFromServerAsync($scope.URL_GET_CERTIFICATES, _onSuccess, _onFail, true);

            };

            $scope.setCASettings = function (caIndex) {

                try {
                    var caServer = (caIndex < $scope.CAsServers.length) ?
                        $scope.CAsServers[caIndex] : null;
                    var offline = ((caServer == null) ||
                        (caServer.address == "")) ?
                        true : false;
                    var useCMP = (!offline && (caServer.cmpAddress != ""));
                    var loadPKCertsFromFile = (caServer == null) ||
                        (!useCMP && !caServer.certsInKey);

                    $scope.CAServer = caServer;
                    $scope.offline = offline;
                    $scope.useCMP = useCMP;
                    $scope.loadPKCertsFromFile = loadPKCertsFromFile;


                    var settings;
                    settings = $scope.euSign.CreateTSPSettings();
                    if (!offline) {
                        settings.SetGetStamps(true);
                        if (caServer.tspAddress != "") {
                            settings.SetAddress(caServer.tspAddress);
                            settings.SetPort(caServer.tspAddressPort);
                        } else {
                            settings.SetAddress('acskidd.gov.ua');
                            settings.SetPort('80');
                        }
                    }
                    $scope.euSign.SetTSPSettings(settings);

                    settings = $scope.euSign.CreateOCSPSettings();
                    if (!offline) {
                        settings.SetUseOCSP(true);
                        settings.SetBeforeStore(true);
                        settings.SetAddress(caServer.ocspAccessPointAddress);
                        settings.SetPort(caServer.ocspAccessPointPort);
                    }
                    $scope.euSign.SetOCSPSettings(settings);

                    settings = $scope.euSign.CreateCMPSettings();
                    settings.SetUseCMP(useCMP);
                    if (useCMP) {
                        settings.SetAddress(caServer.cmpAddress);
                        settings.SetPort("80");
                    }
                    $scope.euSign.SetCMPSettings(settings);

                    settings = $scope.euSign.CreateLDAPSettings();
                    $scope.euSign.SetLDAPSettings(settings);
                } catch (e) {
                    alert("Виникла помилка при встановленні налашувань: " + e);
                }

            };

            $scope.readPrivateKeyAsStoredFile = function () {

                var keyName = $scope.utils.GetSessionStorageItem(
                    $scope.PrivateKeyNameSessionStorageName, false, false);
                var key = $scope.utils.GetSessionStorageItem(
                    $scope.PrivateKeySessionStorageName, true, false);
                var password = $scope.utils.GetSessionStorageItem(
                    $scope.PrivateKeyPasswordSessionStorageName, false, true);
                if (keyName == null || key == null || password == null)
                    return;

                $scope.loadCAServer();


                $scope.setPointerEvents(document.getElementById('PKeyReadButton'), true);
                document.getElementById('PKeyFileName').value = keyName;
                document.getElementById('PKeyPassword').value = password;
                var _readPK = function () {
                    $scope.readPrivateKey(keyName, key, password, null, true);
                };
                setTimeout(_readPK, 10);

                return;

            };
            $scope.loadCAServer = function () {

                var index = $scope.utils.GetSessionStorageItem(
                    $scope.CAServerIndexSessionStorageName, false, false);
                if (index != null) {
                    document.getElementById("CAsServersSelect").selectedIndex =
                        parseInt(index);
                    $scope.setCASettings(parseInt(index));
                }
            };
            $scope.loadCAsSettings = function (onSuccess, onError) {

                var _onSuccess = function (casResponse) {
                    try {
                        var servers = JSON.parse(casResponse.replace(/\\'/g, "'"));
                        var select = document.getElementById("CAsServersSelect");
                        for (var i = 0; i < servers.length; i++) {
                            var option = document.createElement("option");
                            option.text = servers[i].issuerCNs[0];
                            select.add(option);
                        }

                        var option = document.createElement("option");
                        option.text = "інший";
                        select.add(option);

                        select.onchange = function () {
                            $scope.setCASettings(select.selectedIndex);
                        };

                        $scope.CAsServers = servers;

                        onSuccess();
                    } catch (e) {
                        onError();
                    }
                }
                $scope.euSign.LoadDataFromServer($scope.URL_CAS, _onSuccess, onError, false);
            };


            $scope.showOwnerInfo = function () {

                try {
                    var ownerInfo = $scope.euSign.GetPrivateKeyOwnerInfo();

                    console.log("Власник: " + ownerInfo.GetSubjCN() + "\n" +
                        "ЦСК: " + ownerInfo.GetIssuerCN() + "\n" +
                        "Серійний номер: " + ownerInfo.GetSerial());
                } catch (e) {
                    alert(e);
                }
            };

            $scope.removeStoredPrivateKey = function () {

                $scope.utils.RemoveSessionStorageItem(
                    $scope.PrivateKeyNameSessionStorageName);
                $scope.utils.RemoveSessionStorageItem(
                    $scope.PrivateKeySessionStorageName);
                $scope.utils.RemoveSessionStorageItem(
                    $scope.PrivateKeyPasswordSessionStorageName);
                $scope.utils.RemoveSessionStorageItem(
                    $scope.PrivateKeyCertificatesChainSessionStorageName);
                $scope.utils.RemoveSessionStorageItem(
                    $scope.PrivateKeyCertificatesSessionStorageName);

                $scope.removeCAServer();
            };

            $scope.removeCAServer = function () {

                $scope.utils.RemoveSessionStorageItem(
                    $scope.CAServerIndexSessionStorageName);
            };

            $scope.storePrivateKey = function (keyName, key, password, certificates) {

                if (!$scope.utils.SetSessionStorageItem(
                    $scope.PrivateKeyNameSessionStorageName, keyName, false) || !$scope.utils.SetSessionStorageItem(
                    $scope.PrivateKeySessionStorageName, key, false) || !$scope.utils.SetSessionStorageItem(
                    $scope.PrivateKeyPasswordSessionStorageName, password, true) || !$scope.storeCAServer()) {
                    return false;
                }

                if (Array.isArray(certificates)) {
                    if (!$scope.utils.SetSessionStorageItems(
                        $scope.PrivateKeyCertificatesSessionStorageName,
                        certificates, false)) {
                        return false;
                    }
                } else {
                    if (!$scope.utils.SetSessionStorageItem(
                        $scope.PrivateKeyCertificatesChainSessionStorageName,
                        certificates, false)) {
                        return false;
                    }
                }

                return true;
            };

            $scope.storeCAServer = function () {
                var index = document.getElementById("CAsServersSelect").selectedIndex;
                return $scope.utils.SetSessionStorageItem(
                    $scope.CAServerIndexSessionStorageName, index.toString(), false);
            };

            $scope.privateKeyReaded = function (isReaded) {
                var enabled = '';
                var disabled = 'disabled';

                if (!isReaded) {
                    enabled = 'disabled';
                    disabled = '';
                }

                document.getElementById('CAsServersSelect').disabled = disabled;

                document.getElementById('PKeyFileName').disabled = disabled;
                document.getElementById('PKeyReadButton').title =
                    isReaded ? 'Readed' : 'Read';
                document.getElementById('PKeyReadButton').innerHTML =
                    isReaded ? 'Зчитано' : 'Зчитати';


                document.getElementById('PKeyPassword').disabled = disabled;
                if (!isReaded) {
                    document.getElementById('PKeyPassword').value = '';
                    document.getElementById('PKeyPassword').disabled = 'disabled';
                    document.getElementById('PKeyFileName').value = '';
                    document.getElementById('PKeyFileInput').value = null;
                    $scope.setPointerEvents(document.getElementById('PKeyReadButton'), false);
                }
            };

            $scope.getPrivateKeyCertificates = function (key, password, fromCache, onSuccess, onError) {

                var certificates;

                if ($scope.CAServer != null &&
                    $scope.CAServer.certsInKey) {
                    onSuccess([]);
                    return;
                }

                if (fromCache) {
                    if ($scope.useCMP) {
                        certificates = $scope.utils.GetSessionStorageItem(
                            $scope.PrivateKeyCertificatesChainSessionStorageName, true, false);
                    } else if ($scope.loadPKCertsFromFile) {
                        certificates = $scope.utils.GetSessionStorageItems(
                            $scope.PrivateKeyCertificatesSessionStorageName, true, false)
                    }

                    onSuccess(certificates);
                } else if ($scope.useCMP) {
                    $scope.getPrivateKeyCertificatesByCMP(
                        key, password, onSuccess, onError);
                } else if ($scope.loadPKCertsFromFile) {
                    var _onSuccess = function (files) {
                        var certificates = [];
                        for (var i = 0; i < files.length; i++) {
                            certificates.push(files[i].data);
                        }

                        onSuccess(certificates);
                    };

                    $scope.euSign.ReadFiles(
                        $scope.privateKeyCerts,
                        _onSuccess, onError);
                }
            };

            $scope.getPrivateKeyCertificatesByCMP = function (key, password, onSuccess, onError) {

                try {
                    var cmpAddress = $scope.getCAServer().cmpAddress + ":80";
                    var keyInfo = $scope.euSign.GetKeyInfoBinary(key, password);
                    onSuccess($scope.euSign.GetCertificatesByKeyInfo(keyInfo, [cmpAddress]));
                } catch (e) {
                    onError(e);
                }
            };

            $scope.getCAServer = function () {

                var index = document.getElementById("CAsServersSelect").selectedIndex;

                if (index < $scope.CAsServers.length)
                    return $scope.CAsServers[index];

                return null;
            };

            $scope.installPKeyModalLoad = function () {
                $scope.initialize();
                document.getElementById('PKeyFileInput').addEventListener('change',
                    $scope.selectPrivateKeyFile, false);
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
                                .then(function (file) {
//                                    if (status == 200) {
//                                        $scope.TestForm.signed = true;
//                                        toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESS_SIGNED'));
//                                    }
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