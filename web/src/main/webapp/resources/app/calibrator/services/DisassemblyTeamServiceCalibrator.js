angular
    .module('employeeModule')
    .factory('DisassemblyTeamServiceCalibrator',
    function ($http) {
        return {
            getPage: function (pageNumber, itemsPerPage, search) {
                var url = 'calibrator/disassemblyTeam/' + pageNumber + '/' + itemsPerPage;
                if (search != null && search != undefined && search != "") {
                    url += '/' + search;
                }
                return $http.get(url)
                    .then(function (result) {
                        return result.data;
                    });
            },
            isDisassemblyTeamNameAvailable: function (Tname) {
                var url = 'calibrator/disassemblyTeam/available/' + Tname;
                return $http.get(url)
                    .then(function (result) {
                        return result.data;
                    });
            },
            saveDisassemblyTeam: function (formData) {
                return $http.post("calibrator/disassemblyTeam/add", formData)
                    .then(function (result) {
                        return result.status;
                    });
            },
            getDisassemblyTeamWithId: function (id) {
                var url = 'calibrator/disassemblyTeam/getDisassemblyTeam/' + id;
                return $http.get(url).then(function (result) {
                    return result.data;
                });
            },
            editDisassemblyTeam: function (formData, id) {
                var url = 'calibrator/disassemblyTeam/edit/' + id;
                return $http.post(url, formData)
                    .then(function (result) {
                        return result.status;
                    });
            },
            deleteDisassemblyTeam: function (disassemblyTeamId) {
                var url = 'calibrator/disassemblyTeam/delete/' + disassemblyTeamId;
                return $http.post(url)
                    .then(function (result) {
                        return result.status;
                    });
            }
        }
    });