angular
    .module(
    'adminModule',
    ['spring-security-csrf-token-interceptor', 'ui.bootstrap',
        'ui.router', 'ui.bootstrap.showErrors', 'ngTable',
        'pascalprecht.translate', 'ngCookies', 'ui.select', 'ngSanitize', 'localytics.directives', 'daterangepicker', 'checklist-model','ngAnimate', 'toaster',
        'angular-loading-bar'])

    .config(
    [
        '$translateProvider',
        '$stateProvider',
        '$urlRouterProvider',
        'showErrorsConfigProvider',
        'cfpLoadingBarProvider',
        '$provide', '$httpProvider',
        function ($translateProvider, $stateProvider,
                  $urlRouterProvider, showErrorsConfigProvider,cfpLoadingBarProvider, $provide, $httpProvider) {

            $httpProvider.interceptors.push('responseObserver');

            showErrorsConfigProvider.showSuccess(true);
            cfpLoadingBarProvider.includeSpinner = false;
            cfpLoadingBarProvider.latencyThreshold = 500;

            /**
             * i18n configuration.
             */
            $translateProvider.useStaticFilesLoader({
                prefix: 'resources/assets/i18n/welcome-',
                suffix: '.json'
            });
            $translateProvider.useLocalStorage();
            $translateProvider
                .useSanitizeValueStrategy('escaped');
            $translateProvider.preferredLanguage('ukr');
            /**
             * Routing configuration.
             */
            $urlRouterProvider.otherwise('/');
            $stateProvider
                .state(
                'main',
                {
                    url: '/',
                    templateUrl: 'resources/app/admin/views/main-panel.html'
                })
                .state(
                'organizations',
                {
                    url: '/organizations',
                    templateUrl: 'resources/app/admin/views/organizations-panel.html'
                })
                .state(
                'agreements',
                {
                    url: '/agreements',
                    templateUrl: 'resources/app/admin/views/agreement-panel.html'
                })
                .state(
                'users',
                {
                    url: '/users',
                    templateUrl: 'resources/app/admin/views/users-panel.html'
                })
                .state(
                'sys-admins',
                {
                    url: '/sys-admins',
                    templateUrl: 'resources/app/admin/views/sys-admins-panel.html'
                })
                .state(
                'address',
                {
                    url: '/address',
                    templateUrl: 'resources/app/admin/views/address-panel.html'
                })
                .state(
                'device-category',
                {
                    url: '/device-category',
                    templateUrl: 'resources/app/admin/views/devices-panel.html'
                })
                .state(
                'counters-type',
                {
                    url: '/counters-type',
                    templateUrl: 'resources/app/admin/views/counters-type-panel.html'
                })
                .state(
                'settings',
                {
                    url: '/settings',
                    templateUrl: 'resources/app/admin/views/settings-panel.html'
                })
                .state(
                '403',
                {
                    url: '/403',
                    templateUrl: 'resources/app/admin/views/403.html'
                })
                .state(
                '404',
                {
                    url: '/404',
                    templateUrl: 'resources/app/admin/views/404.html'
                })
                .state("profile-info", {
                    url: '/profile-info',
                    templateUrl: 'resources/app/admin/views/profile-info.html',
                    controller: 'ProfileInfoController'
                })
                .state("measuring-equipment-admin", {
                    url: '/calibration-module',
                    templateUrl: 'resources/app/admin/views/measurement-equipments.html',
                    controller: 'MeasuringEquipmentControllerAdmin'
                })
                .state("unsuitability-reasons", {
                    url: '/unsuitability-reasons',
                    templateUrl: 'resources/app/admin/views/unsuitability-reasons.html',
                    controller: 'UnsuitabilityReasonController'
                });
            /*
             Extended ui-select-choices: added watch for ng-translate event called translateChangeEnd
             When translation of page will end, items of select (on the scope) will be changed too.
             Then we refresh the items of select to get them from scope.
             */
            $provide.decorator('uiSelectDirective', function( $delegate, $parse, $injector) {
                var some_directive = $delegate[ 0],
                    preCompile = some_directive.compile;

                some_directive.compile = function compile() {
                    var link = preCompile.apply( this, arguments );

                    return function( scope, element, attrs, controller ) {
                        link.apply( this, arguments );

                        var $select = controller[ 0 ];

                        var rootScope= $injector.get('$rootScope');

                        rootScope.$on('$translateChangeEnd', function(event){
                            scope.setTypeDataLanguage();
                            $select.refreshItems();
                        });

                    };
                };

                return $delegate;
            });
        }]);

angular.module('adminModule').run(function (paginationConfig, $filter) {
    paginationConfig.firstText = $filter('translate')('FIRST_PAGE');
    paginationConfig.previousText = $filter('translate')('PREVIOUS');
    paginationConfig.nextText = $filter('translate')('NEXT');
    paginationConfig.lastText = $filter('translate')('LAST_PAGE');
});

angular.module('adminModule').run(function ($rootScope){
    $rootScope.FIRST_LAST_NAME_REGEX = /^([A-Z][\u0027]{0,1}[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}|[A-Z][\u0027]{0,1}[a-z]{1,20}\u002d[A-Z][\u0027]{0,1}[a-z]{1,20}|[A-Z][\u0027]{0,1}[a-z]{1,20}\u002d[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}\u002d[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}\u002d[A-Z][\u0027]{0,1}[a-z]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}\u002d[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}\u002d\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10}\u002d[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10}\u002d[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
    $rootScope.MIDDLE_NAME_REGEX = /^([A-Z][\u0027]{0,1}[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
    $rootScope.PHONE_REGEX = /^[1-9]\d{8}$/;
});

define(['controllers/TopNavBarController', 'controllers/MainPanelController',

    'controllers/MeasuringEquipmentControllerAdmin',
    'controllers/MeasuringEquipmentAddModalControllerAdmin',
    'controllers/MeasuringEquipmentDisableModalControllerAdmin',

    'controllers/OrganizationPanelController',
    'controllers/OrganizationAddModalController',
    'controllers/OrganizationEditModalController',
    'controllers/OrganizationEditHistoryModalController',
    'controllers/DeviceController',
    'controllers/CategoryDeviceAddModalController',
    'controllers/CategoryDeviceEditModalController',
    'controllers/CounterTypePanelController',
    'controllers/CounterTypeAddController',
    'controllers/CounterTypeEditController',
    'controllers/SettingsController',
    'controllers/SysAdminsController',
    'controllers/SysAdminEditModalController',
    'controllers/SysAdminDeleteModalController',
    'controllers/UsersController',
    'controllers/ProfileInfoController',
    'controllers/EditProfileInfoController',
    'controllers/SysAdminAddModalController',
    'controllers/InternationalizationController',
    'controllers/agreement/AgreementController',
    'controllers/agreement/AgreementAddController',
    'services/OrganizationService', 'services/StatisticService',
    'services/UserService', 'services/AddressService',
    'services/DeviceService', 'services/DevicesService',
    'services/CounterTypeService',
    'services/AgreementService',
    'services/SettingsService',
    'services/UsersService',
    'services/ProfileService',
    'services/RoleService',
    'services/ResponseObserver',
    'services/MeasuringEquipmentServiceAdmin',
    'directives/unique',
    'controllers/CommonController',
    'controllers/UnsuitabilityReasonController',
    'services/UnsuitabilityReasonService',
    'controllers/UnsuitabilityReasonDeleteModalController',
    'controllers/UnsuitabilityReasonAddModalController',
    'controllers/AdminCloseAlertController',
    'controllers/AdminResetAlertController',
    'controllers/AdminCloseAlertDeleteController'
], function () {
});
