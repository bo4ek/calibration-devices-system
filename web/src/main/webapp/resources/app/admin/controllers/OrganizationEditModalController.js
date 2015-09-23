angular
	.module('adminModule')
	.controller(
	'OrganizationEditModalController',
	[
		'$rootScope',
		'$scope',
		'$translate',
		'$modalInstance',
		'$filter',
		'$timeout',
		'AddressService',
		'UserService',
		'DevicesService',
		'OrganizationService','$log', 'regions',
		function ($rootScope, $scope, $translate, $modalInstance, $filter, $timeout,
				 addressService,
				  userService, devicesService, organizationService, $log, regions) {

			$scope.typeData = [
				{
					type: 'PROVIDER',
					label: null
				},
				{
					type: 'CALIBRATOR',
					label: null
				},
				{
					type: 'STATE_VERIFICATOR',
					label: null
				}
			];



			$rootScope.organization.region.selected = "Львівська";

			$scope.setTypeDataLanguage = function () {
				var lang = $translate.use();
				if (lang === 'ukr') {
					$scope.typeData[0].label = 'Постачальник послуг';
					$scope.typeData[1].label = 'Вимірювальна лабораторія';
					$scope.typeData[2].label = 'Уповноважена повірочна лабораторія';
				} else if (lang === 'eng') {
					$scope.typeData[0].label = 'Service provider';
					$scope.typeData[1].label = 'Measuring laboratory';
					$scope.typeData[2].label = 'Authorized calibration laboratory';
				} else {
					console.error(lang);
				}
			};


			/*
			 */
		 	 var setCurrentTypeDataLanguage = function () {
				var lang = $translate.use();
				if (lang === 'ukr') {
					for (var i = 0; i < $scope.organizationTypes.length; i++) {
						switch ($scope.organizationTypes[i].type) {
							case 'PROVIDER':
								console.log($scope.organizationTypes[i].type);
								$scope.organizationTypes[i].label = 'Постачальник послуг';
								break;
							case 'CALIBRATOR':
								$scope.organizationTypes[i].label = 'Вимірювальна лабораторія';
								break;
							case 'STATE_VERIFICATOR':
								$scope.organizationTypes[i].label = 'Уповноважена повірочна лабораторія';
								break;
							default: console.log($scope.organizationTypes[i].type + " not organization type");
						}
					}
				} else if (lang === 'eng') {
					for (var i = 0; i < $scope.organizationTypes; i++) {
						switch ($scope.organizationTypes[i].type) {
							case 'PROVIDER':
								$scope.organizationTypes[i].label = 'Service provider';
								break;
							case 'CALIBRATOR':
								$scope.organizationTypes[i].label = 'Measuring laboratory';
								break;
							case 'STATE_VERIFICATOR':
								$scope.organizationTypes[i].label = 'Authorized calibration laboratory';
								break;
							default: console.error($scope.organizationTypes[i].type + " not organization type");
						}
					}
				} else {
					console.error(lang);
				}
			};

			/*
			* convert organizationType object
			* */
			
			 /**
             * Closes modal window on browser's back/forward button click.
             */
			$rootScope.$on('$locationChangeStart', function() {
			    $modalInstance.close();
			});

			$scope.organizationTypes = [];
			//for (var i = 0; i < $rootScope.organization.organizationTypes.length; i++) {
			//	$scope.organizationTypes[i] = {
			//		type : $rootScope.organization.organizationTypes[i].type,
			//		label : null
			//	}
			//}
			
			$scope.setTypeDataLanguage();
			$timeout(setCurrentTypeDataLanguage(), 3);


			console.log($scope.organizationTypes);

			$scope.regions = regions;
			$scope.districts = [];
			$scope.localities = [];
			$scope.streets = [];
			$scope.buildings = [];


			$scope.isUsernameAvailable = true;

				organizationService.getOrganizationAdmin($rootScope.organization.id).then(
					function (data) {
						$scope.adminsFirstName = data.firstName;
						$scope.adminsLastName = data.lastName;
						$scope.adminsMiddleName = data.middleName;
						$scope.adminsUserName = data.username;
						$scope.oldUsername = data.username;
						console.log(data);
						console.log(data.firstName);
					}
				);


			$scope.checkIfUsernameIsAvailable = function() {
				var username = $scope.adminsUserName;
				userService.isUsernameAvailable(username).then(
					function(data) {

						if ($scope.USERNAME_REGEX.test(username) && ($scope.oldUsername != username) && (username != "")){
							$scope.isUsernameAvailable = data;
						}else {
							$scope.isUsernameAvailable = true;
						}
					})
			}


			$scope.checkField = function (caseForValidation) {
				switch (caseForValidation) {
					case ('organizationName') :
						var organizationName = $rootScope.organization.name;
						if (organizationName == null) {
						} else if ($scope.ORGANIZATION_NAME_REGEX.test(organizationName)) {
							validator('organizationName', false);
						} else {
							validator('organizationName', true);
						}
						break;
					case ('firstName') :
						var firstName = $rootScope.organization.firstName;
						if (firstName == null) {
						} else if ($scope.FIRST_LAST_NAME_REGEX.test(firstName)) {
							validator('firstName', false);
						} else {
							validator('firstName', true);
						}
						break;
					case ('lastName') :
						var lastName = $rootScope.organization.lastName;
						if (lastName == null) {

						} else if ($scope.FIRST_LAST_NAME_REGEX.test(lastName)) {

							validator('lastName', false);
						} else {
							validator('lastName', true);
						}
						break;
					case ('middleName') :
						var middleName = $rootScope.organization.middleName;
						if (middleName == null) {
						} else if ($scope.MIDDLE_NAME_REGEX.test(middleName)) {
							validator('middleName', false);
						} else {
							validator('middleName', true);
						}
						break;
					case ('phone') :
						var phone = $rootScope.organization.phone;
						if (phone == null) {
						} else if ($scope.PHONE_REGEX.test(phone)) {
							validator('phone', false);
						} else {
							validator('phone', true);
						}
						break;
					case ('email') :
						var email = $rootScope.organization.email;
						if (email == null) {
						} else if ($scope.EMAIL_REGEX.test(email)) {
							validator('email', false);
						} else {
							validator('email', true);
						}
						break;
					case ('login') :
						var username = $scope.adminsUserName ;
						if (username == null) {
						} else if ($scope.USERNAME_REGEX.test(username)) {
							$scope.checkIfUsernameIsAvailable();
						} else {
							validator('loginValid', false);
						}
						break;
				}
			}


			function validator(caseForValidation, isValid) {

				switch (caseForValidation) {
					case 'organizationName':
						$scope.organizationNameValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'firstName':
						$scope.firstNameValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'lastName':
						$scope.lastNameValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'middleName':
						$scope.middleNameValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'phone':
						$scope.phoneNumberValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'email':
						$scope.emailValidation = {
							isValid: isValid,
							css: isValid ? 'has-error' : 'has-success'
						}
						break;
					case 'loginValid':
						$scope.usernameValidation = {
							isValid: isValid,
							css: isValid? 'has-success' : 'has-error',
							message: isValid ? undefined : 'К-сть символів не повинна бути меншою за 3\n і більшою за 16 '
						}
						break;
				}
			}


			function arrayObjectIndexOf(myArray, searchTerm, property) {
				for (var i = 0, len = myArray.length; i < len; i++) {
					if (myArray[i][property] === searchTerm) {
						return i;
					}
				}
				var elem = {
					id: length,
					designation: searchTerm
				};
				myArray.push(elem);
				return (myArray.length - 1);
			}

			$scope.selectedValues ={};

			if ($rootScope.organization) {
				$scope.blockSearchFunctions = true;
				addressService.findAllRegions().then(function (respRegions) {
					$scope.regions = respRegions;
					var index = arrayObjectIndexOf($scope.regions, $rootScope.organization.region, "designation");
					$scope.selectedValues.selectedRegion = $scope.regions[index];

					addressService.findDistrictsByRegionId($scope.selectedValues.selectedRegion.id)
						.then(function (districts) {
							$scope.districts = districts;
							var index = arrayObjectIndexOf($scope.districts, $rootScope.organization.district, "designation");
							$scope.selectedValues.selectedDistrict = $scope.districts[index];

							addressService.findLocalitiesByDistrictId($scope.selectedValues.selectedDistrict.id)
								.then(function (localities) {
									$scope.localities = localities;
									var index = arrayObjectIndexOf($scope.localities, $rootScope.organization.locality, "designation");
									$scope.selectedValues.selectedLocality = $scope.localities[index];

									addressService.findStreetsByLocalityId($scope.selectedValues.selectedLocality.id)
										.then(function (streets) {
											$scope.streets = streets;
											var index = arrayObjectIndexOf($scope.streets, $rootScope.organization.street, "designation");
											$scope.selectedValues.selectedStreet = $scope.streets[index];
										});

									addressService.findBuildingsByStreetId($scope.selectedValues.selectedStreet.id)
										.then(function (buildings) {
											$scope.buildings = buildings;
											var index = arrayObjectIndexOf($scope.buildings, $rootScope.organization.building, "designation");
											$scope.selectedValues.selectedBuilding = $scope.buildings[index].designation;
								});
						});
				});
			});
			}
			/**
			 * Finds all regions
			 */
			function initFormData() {
				if (!$scope.regions) {
					addressService.findAllRegions().then(
						function(respRegions) {
							$log.debug($rootScope.organization);
							$scope.regions = respRegions;
						/*	var index = arrayObjectIndexOf($scope.regions,  $rootScope.organization.region.designation, "designation");
							$rootScope.organization.region = $scope.regions[index];
							*//*$scope.onRegionSelected($scope.regions[index].id);*/
						});
				}
			}
			/*var index = arrayObjectIndexOf($scope.regions,  $scope.user.address.region, "designation");
			$rootScope.organization.region = $scope.regions[index];
			$scope.onRegionSelected($scope.regions[index].id);*/
			initFormData();

			/**
			 * Receives all possible districts.
			 * On-select handler in region input form element.
			 */
			$scope.receiveDistricts = function (selectedRegion) {
					$scope.districts = [];
					addressService.findDistrictsByRegionId(selectedRegion.id)
						.then(function (districts) {
							$scope.districts = districts;
							$scope.selectedValues.selectedLocality = undefined;
							$scope.selectedValues.selectedDistrict = undefined;
							$rootScope.organization.street = "";
						});
			};

			/**
			 * Receives all possible localities.
			 * On-select handler in district input form element.
			 */
			$scope.receiveLocalities = function (selectedDistrict) {
					$scope.localities = [];
					addressService.findLocalitiesByDistrictId(selectedDistrict.id)
						.then(function (localities) {
							console.log(localities);
							$scope.localities = localities;
							$scope.selectedValues.selectedStreet = "";

						});
			};

			$scope.receiveStreets = function (selectedLocality) {
					$scope.streets = [];
					addressService.findStreetsByLocalityId(selectedLocality.id)
						.then(function (streets) {
							$scope.streets = streets;
							$scope.selectedValues.selectedStreet  = "";
							$scope.organization.address.building = "";
							$scope.organization.address.flat = "";
							$log.debug("$scope.streets");
							$log.debug($scope.streets);

						}
					);
			};

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
			$scope.resetOrganizationForm = function() {
				$scope.$broadcast('show-errors-reset');
				$rootScope.organization = null;
			};


			/**
			 * Change password
			 */
			$scope.changePassword = function () {
				//$scope.preventDefault();
				$scope.password = 'generate';
				$scope.generationMessage = true;
			}

			function addressFormToOrganizationForm() {
				if (typeof $rootScope.organization.region == 'object') {
					$rootScope.organization.region = $rootScope.organization.region.designation;
				}
				if (typeof $rootScope.organization.district == 'object') {
					$rootScope.organization.district = $rootScope.organization.district.designation;
				}
				if (typeof $rootScope.organization.locality == 'object') {
					$rootScope.organization.locality = $rootScope.organization.locality.designation;
				}
				if (typeof $rootScope.organization.street == 'object') {
					$rootScope.organization.street = $rootScope.organization.street.designation;
				}
				if (typeof $rootScope.organization.building == 'object') {
					$rootScope.organization.building = $rootScope.organization.building.designation;
				}
			}

			//function objectTypesToStringTypes() {
			//	for (var i in $rootScope.organization.organizationTypes) {
			//		$scope.organizationTypes[i] = $scope.organizationTypes[i].type;
			//		console.log($scope.organizationTypes[i]);
			//	}
			//}
			//console.log($rootScope.organization.organizationTypes);

			/**
			 * Edit organization. If everything is ok then
			 * resets the organization form and closes modal
			 * window.
			 */

			/**
			 * Finds all regions
			 */
			function initFormData() {
				if (!$scope.regions) {
					addressService.findAllRegions().then(
						function(data) {
							$scope.regions = data;
						});
				}
			}

			initFormData();

			/**
			 * Finds districts in a given region.
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
			 * Receives all possible buildings.
			 * On-select handler in street input form element.
			 */
			$scope.receiveBuildings = function (selectedStreet) {
					$scope.buildings = [];
					dataReceivingService.findBuildingsByStreetId(selectedStreet.id)
						.success(function (buildings) {
							$scope.buildings = buildings;
						}
					);
			};


			$scope.editOrganization = function() {
				addressFormToOrganizationForm();
			//	objectTypesToStringTypes();
				var organizationForm = {
					name : $rootScope.organization.name,
					email : $rootScope.organization.email,
					phone : $rootScope.organization.phone,
					types: $scope.organizationTypes,
					employeesCapacity : $rootScope.organization.employeesCapacity,
					maxProcessTime: $rootScope.organization.maxProcessTime,
					region : $scope.selectedValues.selectedRegion.designation,
					locality : $scope.selectedValues.selectedLocality.designation,
					district : $scope.selectedValues.selectedDistrict.designation,
					street : $scope.selectedValues.selectedStreet.designation || $scope.selectedValues.selectedStreet,
					building : $rootScope.organization.building,
					flat : $rootScope.organization.flat,
					username : $scope.adminsUserName,
					firstName : $scope.adminsFirstName,
					lastName : $scope.adminsLastName,
					middleName : $scope.adminsMiddleName,
					password: $scope.password
				};
				console.log(organizationForm);
				console.log($scope.organizationTypes);

				organizationService.editOrganization(
					organizationForm,
					$rootScope.organizationId).then(
					function(data) {
						if (data == 200) {
							$scope.closeModal();
							$scope.resetOrganizationForm();
							console.log(data);
							$rootScope.onTableHandling();
						}
						else (console.log(data));
					});
			};
			/**
			 * Closes edit modal window.
			 */
			$scope.closeModal = function() {
				$rootScope.onTableHandling();
				$modalInstance.close();
			};

			$scope.ORGANIZATION_NAME_REGEX = /^(?=.{5,50}$).*/;
			$scope.PHONE_REGEX = /^[1-9]\d{8}$/;
			$scope.EMAIL_REGEX = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
			$scope.FIRST_LAST_NAME_REGEX = /^([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})$/;
			$scope.MIDDLE_NAME_REGEX = /^[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}$/;
			$scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
			$scope.PASSWORD_REGEX = /^(?=.{4,20}$).*/;
			$scope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
			$scope.FLAT_REGEX=/^([1-9]{1}[0-9]{0,3}|0)$/;

			}

	]);