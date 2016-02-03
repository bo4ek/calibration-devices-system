angular
    .module('employeeModule')
    .controller('VerificatorSubdivisionAddModalController',
    ['$rootScope', '$scope', '$modal', '$modalInstance', 'VerificatorSubdivisionService', '$filter', 'toaster',
        function ($rootScope, $scope, $modal, $modalInstance, verificatorSubdivisionService, $filter, toaster) {

            /**
             * Checks if the id of subdivision is available
             */
            $scope.isIdAvailable = function () {
                verificatorSubdivisionService.isIdAvailable($scope.subdivisionFormData.subdivisionId)
                    .then(function (isValid) {
                        $scope.subdivisionForm.subdivisionId.$setValidity("duplicate", isValid);
                    }
                )
            };

            /**
             * Saves new subdivision in database and shows alert if the saving was successful
             */
            $scope.subdivisionSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.subdivisionForm.$valid) {
                    verificatorSubdivisionService.addSubdivision($scope.subdivisionFormData)
                        .then(function (status) {
                            if (status == 201) {
                                $modalInstance.close();
                                $rootScope.onTableHandling();
                                toaster.pop('success', $filter('translate')('INFORMATION'),
                                    $filter('translate')('SUCCESSFUL_ADD_SUBDIVISION'));
                            } else {
                                $modalInstance.close();
                                $rootScope.onTableHandling();
                                toaster.pop('error', $filter('translate')('INFORMATION'),
                                    $filter('translate')('ERROR_ADD_SUBDIVISION'));
                            }
                        });
                }
            };

            /**
             * Closes the modal window
             */
            $scope.closeModal = function () {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/close-alert.html',
                    controller: 'VerificationCloseAlertController',
                    size: 'md'
                })
            };

            $scope.$on('close-form', function () {
                $modalInstance.dismiss();
            });

            /**
             * Resets subdivision form
             */
            $scope.resetApplicationForm = function () {
                $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/common/views/modals/reset-alert.html',
                    controller: 'VerificationResetAlertController',
                    size: 'md'
                })
            };

            $scope.$on('reset-form', function () {
                $scope.$broadcast('show-errors-reset');
                $scope.subdivisionForm.$setPristine();
                $scope.subdivisionForm.$setUntouched();
                $scope.subdivisionFormData = undefined;
            });

            //TODO fix regex
            $scope.TEAM_USERNAME_REGEX = /[a-z0-9_-]{3,16}/;
            $scope.TEAM_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.TEAM_LEADER_FULL_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;

        }
    ]
);
