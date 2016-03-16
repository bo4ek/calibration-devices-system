angular
    .module('employeeModule')
    .controller('NotificationsControllerProvider', ['$scope', '$log', 'VerificationServiceProvider', '$interval', '$state', '$rootScope', '$timeout',
        function ($scope, $log, verificationServiceProvider, $interval, $state,  $rootScope, $timeout) {
    	
	    	var promiseInterval;
	    	var promiseTimeOut;
	    	$scope.countOfUnreadVerifications = 0;
	    	
	    	$scope.initializeCounter = function () {
	    		verificationServiceProvider.getCountOfNewVerifications().success(function (count) {
		       		$scope.countOfUnreadVerifications = count;
					});
	    	};
	    	
	    	$scope.initializeCounter();
	    	
	    	$scope.reloadVerifications = function() {
	    		$rootScope.$broadcast('refresh-table');
	    	};
			
            $scope.startPolling = function(){
					$scope.stopPolling();
					if(angular.isDefined(promiseInterval)) return;
					promiseInterval = $interval(function () {
                        $scope.initializeCounter()
					}, 10000);
			};

	    	$scope.stopPolling = function() {
	    		$interval.cancel(promiseInterval);
	    	};
	    	
	    	$scope.startPolling();
	    	
			$rootScope.$on('verification-sent-to-calibrator', function(){
                $scope.initializeCounter();
			});	   	
		
			$rootScope.$on('verification-was-read', function(){
                $scope.initializeCounter();
			});
		
			$scope.$on('$destroy', function () {
					$scope.stopPolling();
			});

	}]);