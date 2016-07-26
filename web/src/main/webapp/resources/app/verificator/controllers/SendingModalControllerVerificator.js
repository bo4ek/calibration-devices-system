angular
    .module('employeeModule')
    .controller('SendingModalControllerVerificator', ['$scope', '$log', '$modalInstance', 'response','$rootScope',
        function ($scope, $log, $modalInstance, response, $rootScope) {

	    	/**
	         * Closes modal window on browser's back/forward button click.
	         */ 
	    	$rootScope.$on('$locationChangeStart', function() {
			    $modalInstance.close();
			});
    	
            $scope.providers = response.data;
            $scope.formData={};
            $scope.cancel = function () {
                $modalInstance.dismiss();
            };
            $scope.submit = function () {
                $scope.$broadcast('show-errors-check-validity');

                if ($scope.providerSelectionForm.$valid) {
                    $modalInstance.close($scope.formData);
                }
            }
        }]);