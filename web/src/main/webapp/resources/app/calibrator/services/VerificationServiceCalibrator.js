angular
    .module('employeeModule')
    .factory('VerificationServiceCalibrator', ['$http', '$log', function ($http, $log) {

        return {
            getArchivalVerificationDetails: function (verificationId) {
                return getData('verifications/archive/' + verificationId);
            },
            getAllProviders: function () {
                return getData('verifications/archive/getAllProviders');
            },
            getArchivalVerificationStamp: function (verificationId) {
                return getData('verifications/archive/stamp/' + verificationId);
            },
            getNewVerifications: function (currentPage, itemsPerPage, params, sortCriteria, sortOrder) {
                return sendDataToUrl('calibrator/verifications/new/' + currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, params);
            },
            getNewVerificationsForMainPanel: function (currentPage, itemsPerPage, search) {
                return getDataWithParams('calibrator/verifications/new/mainpanel/' + currentPage + '/' + itemsPerPage, search);
            },
            getArchiveVerifications: function (currentPage, itemsPerPage, search, sortCriteria, sortOrder) {
                return getDataWithParams('calibrator/verifications/archive/' + currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
            },
            getRejectedVerifications: function (currentPage, itemsPerPage, search, sortCriteria, sortOrder) {
                return getDataWithParams('calibrator/verifications/rejected/' + currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
            },
            getNewVerificationDetails: function (verificationId) {
                return getData('verifications/new/' + verificationId);
            },
            getCalibrators: function (url) {
                return getEmployeeData('new/calibratorEmployees');
            },
            getVerificators: function (url) {
                return getData('verifications/new/verificators');
            },
            sendVerificationsToCalibrator: function (data) {
                return updateData('new/update', data);
            },
            sendInitiatedVerification: function (form) {
                return sendData("send", form);
            },
            sendEditedVerification: function (verificationId, form) {
                return updateData("edit/" + verificationId, form);
            },
            sendEditedStamp: function (data) {
                return updateData("editStamp", data);
            },
            getCalibratorsCorrespondingProvider: function () {
                return getData("applications/calibrators");
            },
            getLocalitiesCorrespondingProvider: function () {
                return getData("applications/localities");
            },
            getStreetsCorrespondingLocality: function (selectedLocality) {
                return getData("applications/streets/" + selectedLocality.id);
            },
            getBuildingsCorrespondingStreet: function (selectedBuilding) {
                return getData("applications/buildings/" + selectedBuilding.id);
            },
            getCountOfNewVerifications: function () {
                return getData('verifications/new/count/calibrator');
            },
            markVerificationAsRead: function (data) {
                return updateData('new/read', data);
            },
            cancelUploadFile: function (idVerification) {
                return getData('verifications/find/uploadFile?idVerification=' + idVerification);
            },
            sendEmployeeCalibrator: function (data) {
                return updateData('assign/calibratorEmployee', data);
            },
            assignEmployeeCalibrator: function (verificationId) {
                return updateData('assign/calibratorEmployee/' + verificationId);
            },
            cleanCalibratorEmployeeField: function (data) {
                return employeeUpdateData('remove/calibratorEmployee', data);
            },
            cleanCalibratorEmployeeFieldForAll: function (data) {
                return updateData('remove/calibratorEmployeeForAll', data);
            },
            getNewVerificationEarliestDate: function () {
                return getData('verifications/new/earliest_date/calibrator');
            },
            getArchivalVerificationEarliestDate: function () {
                return getData('verifications/archive/earliest_date/calibrator');
            },
            getIfEmployeeCalibrator: function () {
                return getData('verifications/calibrator/role');
            },
            checkIfAdditionalInfoExists: function (verifId) {
                return checkInfo('calibrator/verifications/checkInfo/' + verifId);
            },
            getVerificationById: function (code) {
                return getData('applications/verification/' + code);
            },
            saveAdditionalInfo: function (data) {
                return updateData('saveInfo', data);
            },
            editCounterInfo: function (data) {
                return updateData('editCounterInfo', data);
            },
            editClientInfo: function (data) {
                return updateData('editClientInfo', data);
            },
            getCountOfNewNotStandardVerifications: function () {
                return getData('not-standard-verifications/new/count');
            },
            getCountOfPlanedTasks: function () {
                return getData('not-standard-verifications/planed/count');
            },
            getCountOfNewVerificationsForProvider: function () {
                return getData('not-standard-verifications/new/count/verificationsForProvider');
            },
            rejectVerificationByIdAndReason: function (verificationId, reasonId) {
                return updateData('rejectVerification/' + verificationId + '/' + reasonId);
            },
            changeProviderInArchive: function (verificationId, providerId) {
                return updateData('changeProviderInArchive/' + verificationId + '/' + providerId);
            },
            receiveAllReasons: function () {
                return getData('verifications/receiveAllReasons/')
            },
            receiveAllWorkers: function () {
                return getData('verifications/receiveAllWorkers/')
            },
            changeWorkers: function (data) {
                return updateData('changeWorker/', data)
            },
            checkStationByDateOfTask: function (data) {
                return updateData('checkStationByDateOfTask/', data)
            }
        };

        function getData(url) {
            return $http.get('calibrator/' + url)
                .success(function (data) {
                    return data;
                })
                .error(function (err) {
                    return err;
                });
        }

        function updateData(url, data) {
            return $http.put('calibrator/verifications/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }

        function getDataWithParams(url, params) {
            return $http.get(url, {
                params: params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }

        function employeeUpdateData(url, data) {
            return $http.put('calibrator/admin/users/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }

        function getEmployeeData(url) {
            return $http.get('calibrator/admin/users/' + url)
                .success(function (data) {
                    return data;
                })
                .error(function (err) {
                    return err;
                });
        }

        function sendData(url, data) {
            return $http.post('calibrator/applications/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }

        function sendDataToUrl(url, data) {
            return $http.post(url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }

        function sendDataProtocol(url, data) {
            return $http.put('calibrator/verifications/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }

        function saveInfo(url, data) {
            return $http.post(url, data)
                .success(function (response) {
                    return response;
                })
                .error(function (err) {
                    return err;
                });
        }

        function checkInfo(url) {
            return $http.get(url)
                .success(function (response) {
                    return response;
                })
                .error(function (err) {
                    return err;
                });
        }

        function findInfo(url) {
            return $http.get(url)
                .success(function (response) {
                    return response;
                })
                .error(function (err) {
                    return err;
                });
        }
    }]);
