angular
    .module('employeeModule')
    .factory('VerificatorSubdivisionService',
    function ($http) {
        return {
            addSubdivision: function (formData) {
                return $http.post('verificator/subdivision/add', formData)
                    .then(function (result) {
                        return result.status;
                    });
            },
            editSubdivision: function (formData, id) {
                return $http.put('verificator/subdivision/edit/' + id, formData)
                    .then(function (result) {
                        return result.status;
                    });
            },
            deleteSubdivision: function (id) {
                return $http.delete('verificator/subdivision/delete/' + id)
                    .then(function (result) {
                        return result.status;
                    });
            },
            getSubdivisionWithId: function (id) {
                return $http.get('verificator/subdivision/get/' + id)
                    .then(function (result) {
                        return result.data;
                    });
            },
            getAllSubdivisions: function() {
                return $http.get('verificator/subdivision/all')
                    .then(function (result){
                        return result.data;
                    })
            },
            isIdAvailable: function (id) {
                return $http.get('verificator/subdivision/available/' + id)
                    .then(function (result) {
                        return result.data;
                    });
            },
            getPage: function (pageNumber, itemsPerPage, search) {
                var url = '/verificator/subdivision/' + pageNumber + '/' + itemsPerPage;
                if (search) {
                    url += '/' + search;
                }
                return $http.get(url, {timeout: 10000})
                    .then(function (result) {
                        return result.data;
                    });
            }
        }
    });