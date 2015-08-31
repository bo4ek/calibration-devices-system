angular
	.module('adminModule')
	.controller(
	'OrganizationEditModalController',
	[
		'$rootScope',
		'$scope',
		'$modalInstance',
		'AddressService',
		'OrganizationService','$log',
		function($rootScope, $scope, $modalInstance,
				 addressService, organizationService,$log) {


			function arrayObjectIndexOf(myArray, searchTerm, property) {
				for(var i = 0, len = myArray.length; i < len; i++) {
					if (myArray[i][property] === searchTerm) return i;
				}
				var elem = {
					id: length,
					designation: searchTerm
				}
				myArray.push(elem);
				return (myArray.length-1);
			}

			$scope.regions = null;
			$scope.districts = [];
			$scope.localities = [];
			$scope.streets = [];
			$scope.buildings = [];

			/**
			 * Finds all regions
			 */
			function initFormData() {
				if (!$scope.regions) {
					addressService.findAllRegions().then(
						function(respRegions) {
							$log.debug($rootScope.organization);
							$scope.regions = respRegions;
						/*	var index = arrayObjectIndexOf($scope.regions,  $rootScope.organization.address.region.designation, "designation");
							$rootScope.organization.address.region = $scope.regions[index];
							*//*$scope.onRegionSelected($scope.regions[index].id);*/
						});
				}
			}
			/*var index = arrayObjectIndexOf($scope.regions,  $scope.user.address.region, "designation");
			$scope.employeeFormData.region = $scope.regions[index];
			$scope.onRegionSelected($scope.regions[index].id);*/
			initFormData();

			/**
			 * Finds districts in a given region.
			 *
			 * @param regionId
			 *            to identify region
			 */
			$scope.onRegionSelected = function(regionId) {
				addressService
					.findDistrictsByRegionId(regionId)
					.then(function(data) {
						$scope.districts = data;
					});
			};

			/**
			 * Finds localities in a given district.
			 *
			 * @param districtId
			 *            to identify district
			 */
			$scope.onDistrictSelected = function(districtId) {
				addressService.findLocalitiesByDistrictId(
					districtId).then(function(data) {
						$scope.localities = data;
					});
			};

			/**
			 * Finds streets in a given locality.
			 *
			 * @param localityId
			 *            to identify locality
			 */
			$scope.onLocalitySelected = function(localityId) {
				addressService.findStreetsByLocalityId(
					localityId).then(function(data) {
						$scope.streets = data;
					});
			};

			/**
			 * Finds buildings in a given street.
			 *
			 * @param streetId
			 *            to identify street
			 */
			$scope.onStreetSelected = function(streetId) {
				addressService
					.findBuildingsByStreetId(streetId)
					.then(function(data) {
						$scope.buildings = data;
					});
			};

			/**
			 * Resets organization form
			 */
			/*$scope.resetOrganizationForm = function() {
				$scope.$broadcast('show-errors-reset');
				$rootScope.organization = null;
			};*/


			/**
			 * Convert address data to string
			 */
			function addressFormToOrganizationForm() {
				if (typeof $rootScope.organization.address.region == 'object') {
					$rootScope.organization.address.region = $rootScope.organization.address.region.designation;
				}
				if (typeof $rootScope.organization.address.district == 'object') {
					$rootScope.organization.address.district = $rootScope.organization.address.district.designation;
				}
				if (typeof $rootScope.organization.address.locality == 'object') {
					$rootScope.organization.address.locality = $rootScope.organization.address.locality.designation;
				}
				if (typeof $rootScope.organization.address.street == 'object') {
					$rootScope.organization.address.street = $rootScope.organization.address.street.designation;
				}
				if (typeof $rootScope.organization.address.building == 'object') {
					$rootScope.organization.address.building = $rootScope.organization.address.building.designation;
				}
			}

			/**
			 * Edit organization. If everything is ok then
			 * resets the organization form and closes modal
			 * window.
			 */
			$scope.editOrganization = function() {
				addressFormToOrganizationForm();
				var organizationForm = {
					name : $rootScope.organization.name,
					phone : $rootScope.organization.phone,
					email : $rootScope.organization.email,
					employeesCapacity : $rootScope.organization.employeesCapacity,
					region : $rootScope.organization.address.region,
					locality : $rootScope.organization.address.locality,
					district : $rootScope.organization.address.district,
					street : $rootScope.organization.address.street,
					building : $rootScope.organization.address.building,
					flat : $rootScope.organization.address.flat
				}
				organizationService.editOrganization(
					organizationForm,
					$rootScope.organizationId).then(
					function(data) {
						if (data == 200) {
							$scope.closeModal();
							$scope.resetOrganizationForm();
							$rootScope.onTableHandling();
						}
					});
			}

			/**
			 * Closes edit modal window.
			 */
			$scope.closeModal = function() {
				$modalInstance.close();
			};

			$scope.PHONE_REGEX = /^0[1-9]\d{8}$/;

		}

	]);