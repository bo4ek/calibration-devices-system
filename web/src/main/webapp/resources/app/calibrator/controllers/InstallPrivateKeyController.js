/**
 * Created by Lidiya on 23.01.2016.
 */
angular
    .module('employeeModule')
    .controller('InstallPrivateKeyController', ['$scope', '$window', '$rootScope', '$route', '$log', '$modalInstance',
        '$timeout', 'parentScope', '$translate', 'aFile',
        function ($scope, $window, $rootScope, $route, $log, $modalInstance, $timeout, parentScope, $translate, aFile) {

            $scope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            $scope.ok = function () {
                $modalInstance.close($scope.signedFileBlob);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };



            $scope.parentScope = parentScope;
            $scope.fileToSign = aFile;
            $scope.signedFile = false;
            $scope.disableOk = true;
            $scope.disableRead = false;

            $scope.fileNameChanged = function (element) {
                $scope.$apply(function ($scope) {
                    $scope.files = element.files;
                });
            };
            $scope.privateKeyPassword = null;
            $scope.EU_ERROR_CERT_NOT_FOUND = 0x0033;

            $scope.readPrivateKeyButtonClick = function () {

                var onError = function (message) {
                    setStatus('');
                    alert(message);
                };

                var onSuccess = function (keyName, key) {
                    $scope.readPrivateKey(keyName, new Uint8Array(key),
                        $scope.privateKeyPassword, null, false);
                    $scope.signFile();
                };
                if (document.getElementById('PKeyReadButton').title == 'Read') {
                    setStatus('reading key');

                    if ($scope.privateKeyPassword == null) {
                        onError('No password!');
                        return;
                    }
                    var onFileRead = function (readedFile) {
                        onSuccess(readedFile.file.name, readedFile.data);
                    };
                    parentScope.euSign.ReadFile($scope.files[0], onFileRead, onError);
                }

            };
            $scope.readPrivateKey = function (keyName, key, password, certificates, fromCache) {
                var _onError = function (e) {
                    setStatus('');

                    if (fromCache) {
                        parentScope.removeStoredPrivateKey();
                        parentScope.privateKeyReaded(false);
                    } else {
                        alert(e);
                    }
                };

                if (certificates == null) {
                    var _onGetCertificates = function (certs) {
                        if (certs == null) {
                            _onError(parentScope.euSign.MakeError(EU_ERROR_CERT_NOT_FOUND));
                            return;
                        }

                        $scope.readPrivateKey(keyName, key, password, certs, fromCache);
                    };

                    parentScope.getPrivateKeyCertificates(
                        key, password, fromCache, _onGetCertificates, _onError);
                    return;
                }

                try {
                    if (Array.isArray(certificates)) {
                        for (var i = 0; i < certificates.length; i++) {
                            parentScope.euSign.SaveCertificate(certificates[i]);
                        }
                    } else {
                        parentScope.euSign.SaveCertificates(certificates);
                    }

                    parentScope.euSign.ReadPrivateKeyBinary(key, password);

                    if (!fromCache && parentScope.utils.IsSessionStorageSupported()) {
                        if (!parentScope.storePrivateKey(
                            keyName, key, password, certificates)) {
                            parentScope.removeStoredPrivateKey();
                        }
                    }

                    parentScope.privateKeyReaded(true);

                    if (!fromCache)
                        parentScope.showOwnerInfo();
                } catch (e) {
                    _onError(e);
                }

            };

            $scope.signFile = function () {
                if ($scope.fileToSign.size > Module.MAX_DATA_SIZE) {
                    alert("Розмір файлу для піпису занадто великий");
                    return;
                }

                var fileReader = new FileReader();

                fileReader.onloadend = (function () {
                    return function (evt) {
                        if (evt.target.readyState != FileReader.DONE)
                            return;
                        var isAddCert = false;
                        var data = new Uint8Array(evt.target.result);

                        try {
                            $scope.signedFileArray = parentScope.euSign.SignDataInternal(isAddCert, data, false);
                            $scope.signedFileBlob = new Blob([$scope.signedFileArray], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
                            $scope.signedFileBlob.lastModifiedDate;
                            $scope.signedFileBlob.name = parentScope.testId + ".p7s";
                            $scope.signedFile = true;
                            $scope.info = "Файл успішно підписано";
                            $scope.disableOk = false;
                            $scope.disableRead = true;
                            setStatus('');
                        } catch (e) {
                            alert(e);
                        }
                    };
                })($scope.fileToSign.name);

                setStatus('signing file');
                fileReader.readAsArrayBuffer($scope.fileToSign);

            };

            function setStatus(message) {
                if (message != '')
                    message = '(' + message + '...)';
                document.getElementById('status').innerHTML = message;
            }
        }
    ])
;
