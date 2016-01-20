angular.module('adminModule').factory('DataReceivingService',
		[ '$http', '$log', function($http, $log) {

			return {
				findAllRegions : function() {
					return getData('regions');
				},
				findAllDevices : function() {
					return getData('devices');
				},
				findAllDevicesByType : function() {
					return getData('devices/{typeDevice}');
				},
				findAllDevicesType : function() {
					return getData('devicesName');
				},
				findDistrictsByRegionId : function(id) {
					return getData('districts/' + id);
				},
				findLocalitiesByDistrictId : function(id) {
					return getData('localities/' + id);
				},
				findMailIndexByLocality : function(localityDesignation ,districtId) {
					return getData('localities/' + localityDesignation+'/'+districtId);
				},
				findProvidersByLocality: function (localityId) {
					return getData("providersInLocality/" + localityId)
				},
				findProvidersByLocalityAndDeviceType: function (localityId, deviceType) {
					return getData("providers/" + localityId + "/" + deviceType)
				},
				findStreetsByLocalityId : function(id) {
					return getData('streets/' + id);
				},
				findStreetsTypes : function() {
					return getData('streetsTypes/');
				},
				findBuildingsByStreetId : function(id) {
					return getData('buildings/' + id);
				},
				getVerificationStatusById : function(code) {
					return getData('check/' + code);
				},
				getVerificationById : function(code) {
					return getData('verification/' + code);
				}
			};

			function getData(url) {
				return $http.get('application/' + url).success(function(data) {
					return data;
				}).error(function(err) {
					return err;
				});
			}
		} ]);
