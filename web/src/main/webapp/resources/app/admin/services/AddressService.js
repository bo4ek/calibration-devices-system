angular
    .module('adminModule')
    .factory('AddressService', function ($http) {

        function findAll(type) {
            return $http.get('application/' + type)
                .then(function (result) {
                    return result.data;
                });
        }

        function sendData(url, data) {
            return $http.post('admin/streets/' + url, data)
                .then(function (responseData) {
                    return responseData;
                }, function (err) {
                    return err;
                });
        }

        function getData(url) {
            return $http.get('admin/streets/' + url).then(function (result) {
                return result;
            });
        }

        return {
            findAllRegions: function () {
                return findAll('regions');
            },
            getAllStreets: function (pageNumber, itemsPerPage, search, sortCriteria, sortOrder) {
                return $http.get('admin/streets/' + pageNumber + '/' + itemsPerPage + '/' + sortCriteria + '/' + sortOrder, {
                    params: search
                })
                    .then(function (response) {
                        return response.data;
                    });
            },
            addNewStreet: function (data) {
                return sendData('add', data);
            },
            findDistrictsByRegionId: function (id) {
                return findAll('districts/' + id);
            },
            findLocalitiesByDistrictId: function (id) {
                return findAll('localities/' + id);
            },
            isStreetIdDuplicate: function(streetId){
                return getData("isDuplicateId/" + streetId + "/");
            },
            isStreetNameDuplicate: function(streetName){
                return getData("isDuplicateName/" + streetName + "/");
            },
            findStreetsByLocalityId: function (id) {
                return findAll('streets/' + id);
            },
            findBuildingsByStreetId: function (id) {
                return findAll('buildings/' + id);
            }
        }
    });