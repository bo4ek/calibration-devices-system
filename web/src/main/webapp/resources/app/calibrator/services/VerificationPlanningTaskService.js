angular
    .module('employeeModule')
    .factory('VerificationPlanningTaskService', ['$http', function ($http) {

        return {
           saveTask: function (task) {
                return sendData('save', task);
            },
           saveTaskForTeam: function (task) {
                return sendData('team/save', task);
           },
           getVerificationsByCalibratorEmployeeAndTaskStatus: function (pageNumber, itemsPerPage, search, sortCriteria, sortOrder) {
                return getData('task/findAll/' + pageNumber + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, search);
           },
           getModules: function (place, pickerDate, applicationField) {
                return getData('findAllModules/' + place + '/' + pickerDate + '/' + applicationField);
           },
           getTeams: function (pickerDate, applicationField) {
                return getData('findAllTeams/'  + pickerDate + '/' + applicationField);
           },
           getSymbolsAndStandartSizes: function (verificationId) {
                return getData('task/findSymbolsAndSizes/' + verificationId);
           },
            getEarliestPlanningTaskDate: function() {
                return getData('task/earliest_date');
            }
        };

        function sendData (url, data) {
            return $http.post('task/' + url, data)
                .success(function (result) {
                    return result;
                }).error(function (err) {
                    console.log(err);
                    return err;
                });
        }

        function getData (url) {
            return $http.get('task/' + url)
            .success(function (data) {
                return data;
            }).error(function (err) {
                return err;
            });
        }

    }]);
