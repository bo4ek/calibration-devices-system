angular
    .module('employeeModule')
    .factory('DataReceivingServiceCalibrator', ['$http', '$log',function($http, $log){
    return {
        findAllRegions: function () {
            return getCalibratorData('region');
        },
        findDistrictsByRegionId: function (id) {
            return getCalibratorData('districts/' + id);
        },
        findLocalitiesByDistrictId: function (id) {
            return getCalibratorData('localities/' + id);
        },
        findStreetsTypes: function() {
          return getData('streetsTypes');
        },
        findStreetsByLocalityId: function (id) {
            return getData('streets/' + id);
        },
        findBuildingsByStreetId: function (id) {
            return getData('buildings/' + id);
        },
        findAllDevices: function() {
            return getCalibratorData('devices');
        },
        findAllDeviceTypes: function() {
            return getCalibratorData('deviceTypes');
        },
        findAllSymbols: function(deviceId) {
            return getCalibratorData('symbols/' + deviceId);
        },

        findAllSymbolsByStandardSizeAndDeviceType: function(standardSize, deviceId) {
            return getCalibratorData('symbols/' + standardSize + "/" + deviceId);
        },

        findAllStandardSize : function() {
            return getCalibratorData('standardSizes/');
        },

        findStandardSizesBySymbol: function(symbol, deviceId) {
            return getCalibratorData('standardSizes/' + symbol + "/" + deviceId);
        },
        findMailIndexByLocality: function(localityDesignation, districtId) {
            return getData('localities/' + localityDesignation + '/' + districtId);
        },
        findProvidersForCalibratorByType : function(type) {
            return getData("providers/" + type);
        },
        checkOrganizationType : function() {
            return getCalibratorData('organizationType');
        }
    };

        function getData(url) {
            return $http.get('application/' + url)
                .success(function (data) {
                    return data;
                })
                .error(function (err) {
                    return err;
                });
        }

        function getCalibratorData(url) {
            return $http.get('calibrator/applications/' + url)
                .success(function (data) {
                    return data;
                })
                .error(function (err) {
                    return err;
                });
        }
}]);
