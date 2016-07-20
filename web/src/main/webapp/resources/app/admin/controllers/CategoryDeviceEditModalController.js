angular
    .module('adminModule')
    .controller(
    'CategoryDeviceEditModalController',
    [
        '$rootScope',
        '$scope',
        '$translate',
        '$modalInstance',
        '$timeout',
        'DevicesService',
        function ($rootScope, $scope, $translate, $modalInstance, $timeout,
                  devicesService) {

            $scope.defaultData = {};
            $scope.defaultData.deviceType = {
                type: $rootScope.countersCategory.deviceType,
                label: null
            };

            $scope.deviceTypeData = [
                /*{
                 type: 'ELECTRICAL',
                 label: null
                 },
                 {
                 type: 'GASEOUS',
                 label: null
                 },*/
                {
                    type: 'WATER',
                    label: null
                },
                {
                    type: 'THERMAL',
                    label: null
                }
            ];

            /**
             * Localization of select for counters kind
             */
            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    // $scope.deviceTypeData[0].label = 'Електричний';
                    //$scope.deviceTypeData[1].label = 'Газовий';
                    $scope.deviceTypeData[0].label = 'Холодна вода';
                    $scope.deviceTypeData[1].label = 'Гаряча вода';
                } else if (lang === 'eng') {
                    //$scope.deviceTypeData[0].label = 'Electrical';
                    // $scope.deviceTypeData[1].label = 'Gaseous';
                    $scope.deviceTypeData[0].label = 'Cold water';
                    $scope.deviceTypeData[1].label = 'Hot water';
                }
            };

            var setCurrentTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    switch ($scope.defaultData.deviceType.type) {
                        case "WATER":
                            $scope.defaultData.deviceType.label = 'Холодна вода';
                            break;
                        case "THERMAL":
                            $scope.defaultData.deviceType.label = 'Гаряча вода';
                            break;
                        default:
                    }
                } else if (lang === 'eng') {
                    switch ($scope.defaultData.deviceType.type) {
                        case "WATER":
                            $scope.defaultData.deviceType.label = 'Cold water';
                            break;
                        case "THERMAL":
                            $scope.defaultData.deviceType.label = 'Hot water';
                            break;
                        default:
                    }
                }
            };

            $scope.setTypeDataLanguage();
            setTimeout(setCurrentTypeDataLanguage(), 2000);

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Closes the modal window for adding new
             * category.
             */
            $rootScope.closeModal = function (close) {
                if(close === true) {
                    $modalInstance.close();
                }
                $modalInstance.dismiss();
            };

            /**
             * Validates category form before saving
             */
            $scope.onEditCategoryFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.editCategoryForm.$valid) {
                    var deviceCategoryForm = {
                        deviceType: $scope.defaultData.deviceType.type,
                        deviceName: $rootScope.countersCategory.deviceName
                    };
                    saveDeviceCategory(deviceCategoryForm);
                }
            };

            /**
             * Saves new category from the form in database.
             * If everything is ok then resets the category
             * form and updates table with devices categories.
             */
            function saveDeviceCategory(deviceCategoryForm) {
                devicesService.editDeviceCategory(
                    deviceCategoryForm,
                    $rootScope.countersCategory.id).then(
                    function (data) {
                        if (data == 200) {
                            $scope.closeModal(true);
                            $rootScope.onTableHandling();
                        }
                    });
            }

            $scope.COUNTER_NAME = /^([A-ZА-ЯЇІЄ']{1}[a-zа-яіїє']{1,20}|[A-ZА-ЯЇІЄ']{1}[a-zа-яіїє']{1,20}([ ]{1}[a-zа-яіїє']{1,20})+)$/;
        }
    ]);