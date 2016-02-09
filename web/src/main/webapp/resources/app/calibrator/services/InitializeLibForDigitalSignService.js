/**
 * Created by Lidiya on 09.02.2016.
 */
angular
    .module('employeeModule')
    .factory('InitializeLibForDigitalSign', function () {

        var euSign = new EUSignCP();
        var utils = new Utils(euSign);
        var URL_XML_HTTP_PROXY_SERVICE = "http://localhost:8080/Data/java/ProxyHandler.php";
        var CAsServers = null;
        var CAServer = {
            "issuerCNs": null,
            "address": null,
            "ocspAccessPointAddress": null,
            "ocspAccessPointPort": null,
            "cmpAddress": null,
            "tspAddress": null,
            "tspAddressPort": null
        };
        var CertsLocalStorageName = "Certificates";
        var CRLsLocalStorageName = "CRLs";
        var offline = false;
        var useCMP = false;
        var loadPKCertsFromFile = false;
        var PrivateKeyNameSessionStorageName = "PrivateKeyName";

        var CACertificatesSessionStorageName = "CACertificates";
        var PrivateKeySessionStorageName = "PrivateKey";
        var PrivateKeyPasswordSessionStorageName = "PrivateKeyPassword";
        var CAServerIndexSessionStorageName = "CAServerIndex";
        var PrivateKeyCertificatesChainSessionStorageName = "PrivateKeyCertificatesChain";
        var PrivateKeyCertificatesSessionStorageName = "PrivateKeyCertificates";
        var URL_GET_CERTIFICATES = "http://localhost:8080/Data/CACertificates.p7b?version=1.0.4";
        var URL_CAS = "http://localhost:8080/Data/CAs.json?version=1.0.4";
        var EU_ERROR_CERT_NOT_FOUND = 0x0033;
        var privateKeyCerts = null;

        function setDefaultSettings() {
            try {
                euSign.SetXMLHTTPProxyService(URL_XML_HTTP_PROXY_SERVICE);

                var settings = euSign.CreateFileStoreSettings();
                settings.SetPath("/certificates");
                settings.SetSaveLoadedCerts(true);
                euSign.SetFileStoreSettings(settings);

                settings = euSign.CreateProxySettings();
                euSign.SetProxySettings(settings);

                settings = euSign.CreateTSPSettings();
                euSign.SetTSPSettings(settings);

                settings = euSign.CreateOCSPSettings();
                euSign.SetOCSPSettings(settings);

                settings = euSign.CreateCMPSettings();
                euSign.SetCMPSettings(settings);

                settings = euSign.CreateLDAPSettings();
                euSign.SetLDAPSettings(settings);

                settings = euSign.CreateOCSPAccessInfoModeSettings();
                settings.SetEnabled(true);
                euSign.SetOCSPAccessInfoModeSettings(settings);

                var CAs = CAsServers;
                settings = euSign.CreateOCSPAccessInfoSettings();
                for (var i = 0; i < CAs.length; i++) {
                    settings.SetAddress(CAs[i].ocspAccessPointAddress);
                    settings.SetPort(CAs[i].ocspAccessPointPort);

                    for (var j = 0; j < CAs[i].issuerCNs.length; j++) {
                        settings.SetIssuerCN(CAs[i].issuerCNs[j]);
                        euSign.SetOCSPAccessInfoSettings(settings);
                    }
                }
            } catch (e) {
                alert("Виникла помилка при встановленні налашувань: " + e);
            }

        }

        function loadCertsFromServer() {
            var certificates = utils.GetSessionStorageItem(
                CACertificatesSessionStorageName, true, false);
            if (certificates != null) {
                try {
                    euSign.SaveCertificates(certificates);
                    return;
                } catch (e) {
                    alert("Виникла помилка при імпорті " +
                        "завантажених з сервера сертифікатів " +
                        "до файлового сховища");
                }
            }

            var _onSuccess = function (certificates) {
                try {
                    euSign.SaveCertificates(certificates);
                    utils.SetSessionStorageItem(
                        CACertificatesSessionStorageName,
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

            utils.GetDataFromServerAsync(URL_GET_CERTIFICATES, _onSuccess, _onFail, true);

        }

        function setCASettings(caIndex) {

            try {
                var caServer = (caIndex < CAsServers.length) ?
                    CAsServers[caIndex] : null;
                var offlineloc = ((caServer == null) ||
                    (caServer.address == "")) ?
                    true : false;
                var useCMPloc = (!offlineloc && (caServer.cmpAddress != ""));
                var loadPKCertsFromFileLoc = (caServer == null) ||
                    (!useCMPloc && !caServer.certsInKey);

                CAServer = caServer;
                offline = offlineloc;
                useCMP = useCMPloc;
                loadPKCertsFromFile = loadPKCertsFromFileLoc;


                var settings;
                settings = euSign.CreateTSPSettings();
                if (!offlineloc) {
                    settings.SetGetStamps(true);
                    if (caServer.tspAddress != "") {
                        settings.SetAddress(caServer.tspAddress);
                        settings.SetPort(caServer.tspAddressPort);
                    } else {
                        settings.SetAddress('acskidd.gov.ua');
                        settings.SetPort('80');
                    }
                }
                euSign.SetTSPSettings(settings);

                settings = euSign.CreateOCSPSettings();
                if (!offlineloc) {
                    settings.SetUseOCSP(true);
                    settings.SetBeforeStore(true);
                    settings.SetAddress(caServer.ocspAccessPointAddress);
                    settings.SetPort(caServer.ocspAccessPointPort);
                }
                euSign.SetOCSPSettings(settings);

                settings = euSign.CreateCMPSettings();
                settings.SetUseCMP(useCMPloc);
                if (useCMPloc) {
                    settings.SetAddress(caServer.cmpAddress);
                    settings.SetPort("80");
                }
                euSign.SetCMPSettings(settings);

                settings = euSign.CreateLDAPSettings();
                euSign.SetLDAPSettings(settings);
            } catch (e) {
                alert("Виникла помилка при встановленні налашувань: " + e);
            }

        }

        function loadCAsSettings(onSuccess, onError) {

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
                        setCASettings(select.selectedIndex);
                    };

                    CAsServers = servers;

                    onSuccess();
                } catch (e) {
                    onError();
                }
            };
            euSign.LoadDataFromServer(URL_CAS, _onSuccess, onError, false);
        }

        function loadCAServer() {

            var index = utils.GetSessionStorageItem(
                CAServerIndexSessionStorageName, false, false);
            if (index != null) {
                document.getElementById("CAsServersSelect").selectedIndex =
                    parseInt(index);
                setCASettings(parseInt(index));
            }
        }

        function setPointerEvents(element, enable) {
            element.style.pointerEvents = enable ? "auto" : "none";
        }

        function setStatus(message) {
            if (message != '')
                message = '(' + message + '...)';
            document.getElementById('status').innerHTML = message;
        }

        function removeCAServer() {

            utils.RemoveSessionStorageItem(
                CAServerIndexSessionStorageName);
        }

        function removeStoredPrivateKey() {

            utils.RemoveSessionStorageItem(
                PrivateKeyNameSessionStorageName);
            utils.RemoveSessionStorageItem(
                PrivateKeySessionStorageName);
            utils.RemoveSessionStorageItem(
                PrivateKeyPasswordSessionStorageName);
            utils.RemoveSessionStorageItem(
                PrivateKeyCertificatesChainSessionStorageName);
            utils.RemoveSessionStorageItem(
                PrivateKeyCertificatesSessionStorageName);

            removeCAServer();
        }

        function privateKeyReaded(isReaded) {
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
                setPointerEvents(document.getElementById('PKeyReadButton'), false);
            }
        }

        function getCAServer() {

            var index = document.getElementById("CAsServersSelect").selectedIndex;

            if (index < CAsServers.length)
                return CAsServers[index];

            return null;
        }

        function getPrivateKeyCertificatesByCMP(key, password, onSuccess, onError) {

            try {
                var cmpAddress = getCAServer().cmpAddress + ":80";
                var keyInfo = euSign.GetKeyInfoBinary(key, password);
                onSuccess(euSign.GetCertificatesByKeyInfo(keyInfo, [cmpAddress]));
            } catch (e) {
                onError(e);
            }
        }

        function getPrivateKeyCertificates(key, password, fromCache, onSuccess, onError) {

            var certificates;

            if (CAServer != null &&
                CAServer.certsInKey) {
                onSuccess([]);
                return;
            }

            if (fromCache) {
                if (useCMP) {
                    certificates = utils.GetSessionStorageItem(
                        PrivateKeyCertificatesChainSessionStorageName, true, false);
                } else if (loadPKCertsFromFile) {
                    certificates = utils.GetSessionStorageItems(
                        PrivateKeyCertificatesSessionStorageName, true, false)
                }

                onSuccess(certificates);
            } else if (useCMP) {
                getPrivateKeyCertificatesByCMP(
                    key, password, onSuccess, onError);
            } else if (loadPKCertsFromFile) {
                var _onSuccess = function (files) {
                    var certificates = [];
                    for (var i = 0; i < files.length; i++) {
                        certificates.push(files[i].data);
                    }

                    onSuccess(certificates);
                };

                euSign.ReadFiles(
                    privateKeyCerts,
                    _onSuccess, onError);
            }
        }

        function storeCAServer() {
            var index = document.getElementById("CAsServersSelect").selectedIndex;
            return utils.SetSessionStorageItem(
                CAServerIndexSessionStorageName, index.toString(), false);
        }

        function storePrivateKey(keyName, key, password, certificates) {

            if (!utils.SetSessionStorageItem(
                PrivateKeyNameSessionStorageName, keyName, false) || !utils.SetSessionStorageItem(
                PrivateKeySessionStorageName, key, false) || !utils.SetSessionStorageItem(
                PrivateKeyPasswordSessionStorageName, password, true) || !storeCAServer()) {
                return false;
            }

            if (Array.isArray(certificates)) {
                if (!utils.SetSessionStorageItems(
                    PrivateKeyCertificatesSessionStorageName,
                    certificates, false)) {
                    return false;
                }
            } else {
                if (!utils.SetSessionStorageItem(
                    PrivateKeyCertificatesChainSessionStorageName,
                    certificates, false)) {
                    return false;
                }
            }

            return true;
        }

        function showOwnerInfo() {

            try {
                var ownerInfo = euSign.GetPrivateKeyOwnerInfo();

                console.log("Власник: " + ownerInfo.GetSubjCN() + "\n" +
                    "ЦСК: " + ownerInfo.GetIssuerCN() + "\n" +
                    "Серійний номер: " + ownerInfo.GetSerial());
            } catch (e) {
                alert(e);
            }
        }

        function readPrivateKey(keyName, key, password, certificates, fromCache) {
            var _onError = function (e) {
                setStatus('');

                if (fromCache) {
                    removeStoredPrivateKey();
                    privateKeyReaded(false);
                } else {
                    alert(e);
                }
            };

            if (certificates == null) {
                var _onGetCertificates = function (certs) {
                    if (certs == null) {
                        _onError(euSign.MakeError(EU_ERROR_CERT_NOT_FOUND));
                        return;
                    }

                    readPrivateKey(keyName, key, password, certs, fromCache);
                };

                getPrivateKeyCertificates(
                    key, password, fromCache, _onGetCertificates, _onError);
                return;
            }

            try {
                if (Array.isArray(certificates)) {
                    for (var i = 0; i < certificates.length; i++) {
                        euSign.SaveCertificate(certificates[i]);
                    }
                } else {
                    euSign.SaveCertificates(certificates);
                }

                euSign.ReadPrivateKeyBinary(key, password);

                if (!fromCache && utils.IsSessionStorageSupported()) {
                    if (!storePrivateKey(
                        keyName, key, password, certificates)) {
                        removeStoredPrivateKey();
                    }
                }

                privateKeyReaded(true);

                if (!fromCache)
                    showOwnerInfo();
            } catch (e) {
                _onError(e);
            }

        }

        function readPrivateKeyAsStoredFile() {

            var keyName = utils.GetSessionStorageItem(
                PrivateKeyNameSessionStorageName, false, false);
            var key = utils.GetSessionStorageItem(
                PrivateKeySessionStorageName, true, false);
            var password = utils.GetSessionStorageItem(
                PrivateKeyPasswordSessionStorageName, false, true);
            if (keyName == null || key == null || password == null)
                return;

            loadCAServer();


            setPointerEvents(document.getElementById('PKeyReadButton'), true);
            document.getElementById('PKeyFileName').value = keyName;
            document.getElementById('PKeyPassword').value = password;
            var _readPK = function () {
                readPrivateKey(keyName, key, password, null, true);
            };
            setTimeout(_readPK, 10);

            return;

        }

        return {
            getEuSign: function(){
                return euSign;
            },
            initialize: function () {
            var _onSuccess = function () {
                try {
                    euSign.Initialize();
                    euSign.SetJavaStringCompliant(true);
                    euSign.SetCharset("UTF-16LE");

                    if (euSign.DoesNeedSetSettings()) {
                        setDefaultSettings();
                    }
                    loadCertsFromServer();
                    setCASettings(0);

                    if (utils.IsSessionStorageSupported()) {
                        var _readPrivateKeyAsStoredFile = function () {
                            readPrivateKeyAsStoredFile();
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
            loadCAsSettings(_onSuccess, _onError);
        },
            getReadPrivateKey: function(keyName, key, password, certificates, fromCache){
                return readPrivateKey(keyName, key, password, certificates, fromCache);
            }


        };





    });