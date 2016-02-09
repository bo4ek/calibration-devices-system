angular
    .module('employeeModule')
    .controller('VerificatorSubdivisionEditModalController',
    ['$rootScope', '$scope', '$modal', '$modalInstance', 'VerificatorSubdivisionService', '$log', '$filter', 'toaster', 'subdivision',
        function ($rootScope, $scope, $modal, $modalInstance, verificatorSubdivisionService, $log, $filter, toaster, subdivision) {

            $scope.subdivisionFormData = subdivision;

            /**
             * Edit team. If everything is ok then
             * resets the team form and closes modal
             * window.
             */

            $scope.editSubdivision = function () {
                $scope.subdivisionFormData = {
                    subdivisionName: $scope.subdivisionFormData.subdivisionName,
                    subdivisionLeader: $scope.subdivisionFormData.subdivisionLeader,
                    subdivisionLeaderPhone: $scope.subdivisionFormData.subdivisionLeaderPhone,
                    subdivisionLeaderEmail: $scope.subdivisionFormData.subdivisionLeaderEmail
                };
                verificatorSubdivisionService.editSubdivision($scope.subdivisionFormData, subdivision.subdivisionId)
                    .then(function (data) {
                        if (data == 200) {
                            $modalInstance.close();
                            $rootScope.onTableHandling();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_EDIT_SUBDIVISION'));
                        } else {
                            $modalInstance.close();
                            $rootScope.onTableHandling();
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_EDIT_SUBDIVISION'));
                        }
                    });
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

            $scope.TEAM_USERNAME_REGEX = /[a-z0-9_-]{3,16}/;
            $scope.TEAM_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.TEAM_LEADER_FULL_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;

        }]);

