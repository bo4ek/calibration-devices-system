angular
    .module('welcomeModule')
    .controller('FeedbackController', ['$scope', '$modalInstance',
        function ($scope, $modalInstance) {
            $scope.formData = {};
            $scope.complete = false;

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.sendingStarted = false;

            $scope.submit = function () {

                $scope.$broadcast('show-errors-check-validity');
                if ($scope.mailSendingForm.$valid) {
                    $scope.sendingStarted = false;
                    $modalInstance.close($scope.formData, $scope.sendingStarted);

                }
            };

            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;

        }]);
