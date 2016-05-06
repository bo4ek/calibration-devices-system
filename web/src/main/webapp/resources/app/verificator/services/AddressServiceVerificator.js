angular
    .module('employeeModule')
    .factory('AddressServiceVerificator', ['$http', '$log', function ($http, $log) {
        return {
            findAllRegions: function () {
                return getData('regions');
            },
            findDistrictsByRegionId: function (id) {
                return getData('districts/' + id);
            },
            findLocalitiesByDistrictId: function (id) {
                return getData('localities/' + id);
            },
            findStreetsByLocalityId: function (id) {
                return getData('streets/' + id);
            },
            findBuildingsByStreetId: function (id) {
                return getData('buildings/' + id);
            }
        };

        function getData(url) {
            return $http.get('application/' + url, {timeout: 10000})
                .success(function (data) {
                    return data;
                })
                .error(function (err) {
                    return err;
                });
        }
    }]);
