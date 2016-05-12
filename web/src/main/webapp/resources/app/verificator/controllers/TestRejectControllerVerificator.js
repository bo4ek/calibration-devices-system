/**
 * Created by Konyk on 10.08.2015.
 */
angular
    .module('employeeModule')
    .controller('TestRejectControllerVerificator', ['$scope', '$log', '$modalInstance', '$rootScope',
        function ($scope, $log, $modalInstance, $rootScope) {

	    	/**
	         * Closes modal window on browser's back/forward button click.
	         */ 
	    	$rootScope.$on('$locationChangeStart', function() {
			    $modalInstance.close();
			});
    	
            $scope.formData={};

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };
            $scope.submit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.mailSendingForm.$valid){
                    $modalInstance.close($scope.formData);
                }
            }
        }]);
