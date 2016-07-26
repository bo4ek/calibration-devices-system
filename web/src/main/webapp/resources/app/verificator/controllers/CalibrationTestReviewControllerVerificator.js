angular
    .module('employeeModule')
    .controller('CalibrationTestReviewControllerVerificator', ['$scope', '$modalInstance', '$log', 'response',
        function ($scope, $modalInstance, $log, response) {
	    	/**
		     * Closes modal window on browser's back/forward button click.
		     */ 
			$scope.$on('$locationChangeStart', function() {
			    $modalInstance.close();
			});
		
            $scope.calibrationTest = response.data;

            $scope.close = function () {
                $modalInstance.close();
            };
        }]);