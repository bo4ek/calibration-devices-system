/**
 * Created by vova on 23.09.15.
 */
angular
    .module('adminModule')
    .controller(
    'OrganizationEditHistoryModalController',
    [
        '$rootScope',
        '$scope',
        '$modal',
        'OrganizationService',
        function($rootScope, $scope, $modal, organizationService) {

            $scope.noChanges = true;

            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            if ($rootScope.organization[1]){
                $scope.noChanges = false;
            }

            $rootScope.closeModal = function () {
                $modalInstance.dismiss();
            };
        }
    ]);