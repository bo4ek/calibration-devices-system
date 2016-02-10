angular
    .module('adminModule')
    .controller(
    'AgreementAddController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$filter',
        'AgreementService',
        'OrganizationService',
        'agreement',
        function ($rootScope, $scope, $translate, $modalInstance, $filter,
                  agreementService, organizationService, agreement) {

            /**
             * Initialize all variables
             */
            $scope.init = function () {

                $scope.addAgreementFormData = {};
                $scope.data = {};
                $scope.data.customerType = "";
                $scope.data.customers = [];
                $scope.data.selectedCustomer = {};
                $scope.data.executorType = "";
                $scope.data.executors = [];
                $scope.data.selectedExecutors = {};

                $scope.deviceTypeData = [
                    {
                        type: 'WATER',
                        label: $filter('translate')('WATER')
                    },
                    {
                        type: 'THERMAL',
                        label: $filter('translate')('THERMAL')
                    }
                ];

                $scope.organizationTypeData = [
                    {
                        type: 'PROVIDER',
                        label: $filter('translate')('PROVIDER')
                    },
                    {
                        type: 'CALIBRATOR',
                        label: $filter('translate')('CALIBRATOR')
                    }
                ];

                if (agreement !== undefined) {
                    $scope.addAgreementFormData.number = agreement.number;
                    $scope.addAgreementFormData.deviceType = {
                        type: agreement.deviceType,
                        label: $filter('translate')(agreement.deviceType)
                    };

                    $scope.addAgreementFormData.customerId = agreement.customerId;
                    $scope.addAgreementFormData.executorId = agreement.executorId;
                    $scope.addAgreementFormData.deviceCount = agreement.deviceCount;
                    $scope.receiveCustomer(agreement.customerType, agreement.deviceType);
                    $scope.data.customerType = {
                        type: agreement.customerType,
                        label: $filter('translate')(agreement.customerType)
                    };
                    $scope.data.selectedCustomer = {
                        id: agreement.customerId,
                        designation: agreement.customerName
                    };
                    $scope.data.selectedExecutors = {
                        id: agreement.executorId,
                        designation: agreement.executorName
                    };
                } else {
                    $scope.addAgreementFormData.number = '';
                    $scope.addAgreementFormData.deviceType = undefined;
                    $scope.addAgreementFormData.customerId = '';
                    $scope.addAgreementFormData.executorId = '';
                    $scope.addAgreementFormData.deviceCount = '';
                }
            };


            $scope.receiveCustomer = function (customerType, deviceType) {
                $scope.data.selectedCustomer = {};
                $scope.data.selectedExecutors = {};
                organizationService.getOrganizationByOrganizationTypeAndDeviceType(customerType, deviceType).then(function (customers) {
                    $scope.data.customers = customers.data;
                });
                $scope.data.executorType = customerType == 'PROVIDER' ? 'CALIBRATOR' : 'STATE_VERIFICATOR';
                organizationService.getOrganizationByOrganizationTypeAndDeviceType($scope.data.executorType, deviceType).then(function (customers) {
                    $scope.data.executors = customers.data;
                })

            };

            $scope.init();

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Resets form
             */
            $scope.resetAddAgreementForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.addAgreementForm.$setPristine();
                $scope.addAgreementForm.$setUntouched();
                $scope.addAgreementFormData = {};
                $scope.data = {};
            };

            /**
             * Closes the modal window
             */
            $rootScope.closeModal = function (close) {
                $scope.resetAddAgreementForm();
                if (close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            };

            /**
             * Validates organization form before saving
             */
            $scope.onAddAgreementFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.addAgreementForm.$valid) {
                    $scope.addAgreementFormData.deviceType = $scope.addAgreementFormData.deviceType.type;
                    $scope.addAgreementFormData.customerId = $scope.data.selectedCustomer.id;
                    $scope.addAgreementFormData.executorId = $scope.data.selectedExecutors.id;
                    saveAgreement();
                }
            };

            /**
             * Saves agreement
             */
            function saveAgreement() {
                console.log($scope.addAgreementFormData);
                if (agreement === undefined) {
                    agreementService.saveAgreement($scope.addAgreementFormData)
                        .then(function (result) {
                            if (result == 201) {
                                $scope.closeModal(true);
                                $scope.resetAddAgreementForm();
                                $rootScope.onTableHandling();
                            }
                        });
                } else {
                    agreementService.editAgreement($scope.addAgreementFormData, agreement.id)
                        .then(function (result) {
                            if (result == 200) {
                                $scope.closeModal(true);
                                $scope.resetAddAgreementForm();
                                $rootScope.onTableHandling();
                            }
                        });
                }
            }


            $scope.CATEGORY_DEVICE_CODE = /^[\u0430-\u044f\u0456\u0457\u0454a-z\d]{13}$/;
            $scope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $scope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
            $scope.PASSWORD_REGEX = /^(?=.{4,20}$).*/;
            $scope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
            $scope.FLAT_REGEX = /^([1-9]{1}[0-9]{0,3}|0)$/;

            if (agreement !== undefined) {


            }
        }
    ]);