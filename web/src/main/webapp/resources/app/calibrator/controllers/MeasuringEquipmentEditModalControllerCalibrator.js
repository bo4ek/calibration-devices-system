angular.module('employeeModule')
		.controller('MeasuringEquipmentEditModalControllerCalibrator',
				   ['$rootScope', '$scope', '$modalInstance', 'MeasuringEquipmentServiceCalibrator',
						function($rootScope, $scope, $modalInstance, MeasuringEquipmentServiceCalibrator) {

							
//					   $scope.name = null;
//						$scope.equipmentType = null;
//						$scope.manufacturer = null;
//						$scope.verificationInterval = null;

						/**
						 * Resets Equipment form
						 */
						$scope.resetEquipmentForm = function() {
							$scope.$broadcast('show-errors-reset');
							$rootScope.equipment = null;
						};

						/**
						 * Calls resetEquipmentForm after the view loaded
						 */
						$scope.resetEquipmentForm();

							/**
							 * Edit equipment. If everything is ok then
							 * resets the equipment form and closes modal
							 * window.
							 */
							$scope.editEquipment = function() {
								var equipmentForm = {
									name : $rootScope.equipment.name,
									equipmentType : $rootScope.equipment.equipmentType,
									manufacturer : $rootScope.equipment.manufacturer,
									verificationInterval : $rootScope.equipment.verificationInterval
									
								}
								MeasuringEquipmentServiceCalibrator.editEquipment(
										equipmentForm,
										$rootScope.equipmentId).then(
										function(data) {
											if (data == 200) {
												$scope.closeModal();
												$scope.resetEquipmentForm();
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

						} ]);
