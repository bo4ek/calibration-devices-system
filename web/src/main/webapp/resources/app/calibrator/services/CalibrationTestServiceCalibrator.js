angular
    .module('employeeModule')
    .factory('CalibrationTestServiceCalibrator', function ($http) {
        var idsOfVerifications = undefined;
        return {
            dataOfVerifications: function(){

                var setIdsOfVerifications = function(dataofID){
                    idsOfVerifications = dataofID;
                };

                var getIdsOfVerifications = function(){
                    return idsOfVerifications;
                };
                return {
                    setIdsOfVerifications : setIdsOfVerifications,
                    getIdsOfVerifications : getIdsOfVerifications
                };
            },

            getCalibrationTests: function (testId) {
                var url = 'calibrator/calibrationTestData/' + testId;
                return $http.get(url)
                    .then(function (result) {
                        return result.data;
                    });
            },

            getCounterTypeId: function (verificationId) {
                var url = 'calibrator/calibrationTests/getCounterTypeId/' + verificationId;
                return $http.get(url)
                    .then(function (result) {
                        return result;
                    });
            },

            getPage: function (currentPage, itemsPerPage, search, sortCriteria, sortOrder, id) {
                return getDataWithParams('calibrator/verifications/calibration-test/' + currentPage + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
            },


            editTestProtocol: function (data,testId) {
                return $http.post("calibrator/calibrationTests/editTest/" + testId, data)
                    .then(function (result) {
                        return result.status;
                    });
            },

            deleteCalibrationTest: function (calibrationTestId) {
                var url = 'calibrator/calibrationTests/delete/' + calibrationTestId;
                return $http.post(url)
                    .then(function (result) {
                        return result.status;
                    });
            },
            getEmptyTest: function (verificationId) {
                var url = 'calibrator/calibrationTests/createEmptyTest/' + verificationId;
                return $http.get(url)
                    .then(function (result) {
                        return result.data;
                    });
            },
            getReasonsUnsuitability: function (counterTypeId) {
                var url = 'reasons_unsuitability/' + counterTypeId;
                return $http.get(url).success(function (data) {
                    return data;
                }).error(function (err) {
                    return err;
                });
            },
            getTestProtocol: function (verificationId) {
                var url = 'calibrator/calibrationTests/getTest/' + verificationId;
                return $http.get(url)
                    .then(function (result) {
                        return result.data;
                    });
            },
            editCalibrationTest: function (formData, testId) {
                var url = 'calibrator/calibrationTests/edit/' + testId;
                return $http.post(url, formData)
                    .then(function (result) {
                        return result.status;
                    });
            },

            signTestProtocol: function (testId) {
                return $http.get("calibrator/calibrationTests/signTest/" + testId, {responseType: 'arraybuffer'})
                    .then(function (response) {
                        var file = new Blob([response], {type: 'application/pdf'});
                        console.log(file);
                        return file;
                    });
            },

            signEDSTestProtocol: function (file, testId) {
                var formData=new FormData();
                formData.append("file",file);
                var url = 'calibrator/calibrationTests/signEDSTest/' + testId;
                return $http.post(url, formData, {
                    transformRequest: function(data, headersGetterFunction) {
                        return data;
                    },
                    headers: { 'Content-Type': undefined }
                }).then(function (result) {
                        return result.status;
                    });
            },

            parseBbiFile: function (fileName) {
                var dotIndex = fileName.lastIndexOf('.');
                var extension = fileName.substring(dotIndex + 1);
                fileName = fileName.substring(0, dotIndex);
                var url = 'calibrator/calibrationTestData/parseBbi/' + fileName + '/' + extension;
                return $http.get(url).then(function (result) {
                    return result;
                })
            },

            getAllModule: function () {
                var url = 'calibrator/calibrationTests/getCalibrationModules';
                return $http.get(url).then(function (result) {
                    return result;
                })
            },
            getDataForCompletedTest: function (testId) {
                var url = ('calibrator/calibrationTests/getTestManual/' + testId)
                return $http.get(url).then(function (result) {
                    return result;
                })
            },
            createTestManual: function (data) {
                var url = 'calibrator/calibrationTests/createTestManual';
                return $http.post(url, data).then(function (result) {
                    return result.status;
                })
            },
            editTestManual: function (dataTest, testId , verificationEdit) {
                return $http.put('calibrator/calibrationTests/editTestManual/' + testId + '/' + verificationEdit, dataTest)
                    .then(function (result) {
                        return result.status;
                    })
            },
            deleteTestManual: function (verificationId) {
                return $http.delete('calibrator/calibrationTests/deleteTestManual/' + verificationId)
                    .then(function (result) {
                        return result.status;
                    })
            },

            getScanDoc: function (pathToScanDoc) {
                return $http.get('calibrator/calibrationTests/getScanDoc/' + pathToScanDoc, {responseType: 'arraybuffer'})
                    .then(function (result) {
                        return result;
                    })
            },
            deleteScanDoc: function (pathToScanDoc,id) {
                return $http.delete('calibrator/calibrationTests/deleteScanDoc/' + pathToScanDoc + '/' + id)
                    .then(function (result) {
                        return result;
                    })
            },
            getCountersTypes: function () {
                return $http.get('calibrator/calibrationTests/getCountersTypes')
                    .then(function (result) {
                        return result;
                    })
            },

            getAllCounterTypesByStandardSizeAndDeviceTypeAndSymbol: function (standardSize, deviceType, symbol) {
                return $http.get('calibrator/calibrationTests/getCountersTypes/' + standardSize + '/' + deviceType + '/' + symbol)
                    .then(function (result) {
                        return result;
                    })
            },

            getCountersTypesByCounterType: function (counterId) {
                return $http.get('calibrator/calibrationTests/getCountersTypesByCounterId/' + counterId)
                    .then(function (result) {
                        return result;
                    })
            },

            getAllUnsuitabilityReasons: function (counterId) {
                var url = 'getAllUnsuitabilityReasons/' + counterId;
                return $http.get(url).success(function (data) {
                    return data;
                }).error(function (err) {
                    return err;
                });
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

        function getDataWithParams(url, params) {
            return $http.get(url, {
                params: params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }

        function sendData(url, data) {
            return $http.post('calibrator/' + url, data)
                .success(function (responseData) {
                    return responseData;
                })
                .error(function (err) {
                    return err;
                });
        }
    });
