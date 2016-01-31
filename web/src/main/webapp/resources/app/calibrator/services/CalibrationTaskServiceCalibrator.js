angular
    .module('employeeModule')
    .factory('CalibrationTaskServiceCalibrator', ['$http', function ($http) {

        return {
            getPage: function (pageNumber, itemsPerPage, search, sortCriteria, sortOrder) {
                return getDataWithParams(pageNumber + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
            },
            getVerificationsByTask: function (pageNumber, itemsPerPage, sortCriteria, sortOrder, taskID) {
                return getData('verifications/' + pageNumber + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder + '/' + taskID);
            },
            removeVerificationFromTask: function (verificationId) {
                return getData('removeVerification/' + verificationId);
            },
            sendTaskToStation: function (taskIDs) {
                return sendData('sendTask', taskIDs);
            },
            changeTaskDate: function (taskID, dateOfTask) {
                return sendData('changeTaskDate/' + taskID, dateOfTask);
            },

            sendVerificationWithQueue: function (newLightVerification) {
                return $http.put('task/saveQueue', newLightVerification).then(function (result) {
                    return result;
                })
            }
        };

        function sendData(url, data) {
            return $http.post('task/' + url, data)
                .success(function (result) {
                    return result;
                }).error(function(err) {
                    return err;
                });
        }

        function getData(url) {
            return $http.get('task/' + url).success(function (result) {
                return result;
            }).error(function (err) {
                return err;
            });
        }

        function getDataWithParams(url, params) {
            return $http.get('task/' + url, {
                params: params
            }).success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }

    }]);
