(function () {
    angular.module('employeeModule', ['spring-security-csrf-token-interceptor',
            'ui.bootstrap', 'ui.bootstrap.datepicker', 'ui.router', 'ui.bootstrap.showErrors', 'ngTable', 'pascalprecht.translate', 'ngCookies', 'localytics.directives',
            'highcharts-ng', 'ngFileUpload', 'ngRoute', 'angular-loading-bar', 'daterangepicker', 'ui.select', 'ngSanitize', 'ngAnimate', 'toaster', 'globalSearchModule', 'focusModule', 'delayModule'])

        .config(['$translateProvider', '$stateProvider', '$urlRouterProvider', 'showErrorsConfigProvider', 'cfpLoadingBarProvider', '$provide',

            function ($translateProvider, $stateProvider, $urlRouterProvider, showErrorsConfigProvider, cfpLoadingBarProvider, $provide) {


                cfpLoadingBarProvider.includeSpinner = false;
                cfpLoadingBarProvider.latencyThreshold = 500;
                showErrorsConfigProvider.showSuccess(true);

                /**
                 *  i18n configuration.
                 */
                $translateProvider.useStaticFilesLoader({
                    prefix: 'resources/assets/i18n/welcome-',
                    suffix: '.json'
                });
                $translateProvider.useLocalStorage();
                $translateProvider.useSanitizeValueStrategy('escaped');
                $translateProvider.preferredLanguage('ukr');
                /**
                 * Routing configuration.
                 */

                $urlRouterProvider.otherwise('/');

                $stateProvider
                    .state('main-panel-provider', {
                        url: '/provider/',
                        templateUrl: 'resources/app/provider/views/main-panel.html',
                        controller: 'MainPanelControllerProvider'
                    })
                    .state("new-verifications-provider", {
                        url: '/provider/verifications/new',
                        templateUrl: 'resources/app/provider/views/new-verifications.html',
                        controller: 'NewVerificationsControllerProvider'
                    })
                    .state("accepted-verifications-provider", {
                        url: '/provider/verifications/accepted',
                        templateUrl: 'resources/app/provider/views/new-verifications.html',
                        controller: 'AcceptedVerificationsControllerProvider'
                    })
                    .state("employee-show-provider", {
                        url: '/provider/employee-show',
                        templateUrl: 'resources/app/provider/views/employee/show-employee.html',
                        controller: 'UsersController'
                    })
                    .state("verifications-archive-provider", {
                        url: '/provider/verifications/archive',
                        templateUrl: 'resources/app/provider/views/archival-verifications.html',
                        controller: 'ArchivalVerificationsControllerProvider'
                    })
                    .state("settings-provider", {
                        url: '/provider/verificator/settings',
                        templateUrl: 'resources/app/provider/views/settings-panel.html'

                    })
                    .state("statistic-show-providerEmployee", {
                        url: '/provider/statistic/employee',
                        templateUrl: 'resources/app/provider/views/employee/calendar-providerEmployee.html',
                        controller: 'CalendarEmployeeProvider'
                    })
                    .state('main-panel-calibrator', {
                        url: '/calibrator/',
                        templateUrl: 'resources/app/calibrator/views/main-panel.html',
                        controller: 'MainPanelControllerCalibrator'
                    })
                    .state("profile-info", {
                        url: '/profile-info',
                        templateUrl: 'resources/app/common/views/profile-info.html',
                        controller: 'ProfileInfoController'
                    })
                    .state("new-verifications-calibrator", {
                        url: '/calibrator/verifications/new',
                        templateUrl: 'resources/app/calibrator/views/new-verifications.html',
                        controller: 'NewVerificationsControllerCalibrator'
                    })
                    .state("calibration-test", {
                        url: '/calibrator/verifications/calibration-test/',
                        templateUrl: 'resources/app/calibrator/views/calibration-test-panel.html',
                        controller: 'CalibrationTestControllerCalibrator'
                    })
                    .state("calibration-test-add", {
                        url: '/calibrator/verifications/calibration-test-add/',
                        templateUrl: 'resources/app/calibrator/views/calibration-test-add-panel.html',
                        controller: 'CalibrationTestAddControllerCalibrator'
                    })
                    .state("verifications-archive-calibrator", {
                        url: '/calibrator/verifications/archive',
                        templateUrl: 'resources/app/calibrator/views/archival-verifications.html',
                        controller: 'ArchivalVerificationsControllerCalibrator'
                    })
                    .state("verifications-reject-calibrator", {
                        url: '/calibrator/verifications/rejected-archive',
                        templateUrl: 'resources/app/calibrator/views/rejected-verifications.html',
                        controller: 'RejectedVerificationsControllerCalibrator'
                    })
                    .state("disassembly-team-calibrator", {
                        url: 'calibrator/disassemblyTeam/',
                        templateUrl: 'resources/app/calibrator/views/disassembly-team.html',
                        controller: 'DisassemblyTeamControllerCalibrator'
                    })
                    .state("verificator-subdivision", {
                        url: 'verificator/verificator-subdivision/',
                        templateUrl: 'resources/app/verificator/views/verificator-subdivision.html',
                        controller: 'VerificatorSubdivisionController'
                    })
                    .state("employee-show-calibrator", {
                        url: '/calibrator/employee-show',
                        templateUrl: 'resources/app/calibrator/views/employee/show-employee.html',
                        controller: 'UsersControllerCalibrator'
                    })
                    .state("planning-task-calibrator", {
                        url: '/calibrator/verifications/task',
                        templateUrl: 'resources/app/calibrator/views/task-for-verifications.html',
                        controller: 'VerificationPlanningTaskController'
                    })
                    .state("task-for-calibration-module", {
                        url: '/calibrator/task-for-calibration-module',
                        templateUrl: 'resources/app/calibrator/views/task-for-calibration-module.html',
                        controller: 'TaskForStationController'
                    })
                    .state("calibrator-task-station", {
                        url: '/calibrator/task/',
                        templateUrl: 'resources/app/calibrator/views/modals/addTaskForStationModal.html',
                        controller: 'TaskForStationModalControllerCalibrator'
                    })
                    .state("calibrator-task-team", {
                        url: '/calibrator/task/',
                        templateUrl: 'resources/app/calibrator/views/modals/addTaskForTeamModal.html',
                        controller: 'TaskForTeamModalControllerCalibrator'
                    })
                    .state("calibrator-counter-status", {
                        url: '/calibrator/task/',
                        templateUrl: 'resources/app/calibrator/views/modals/counterStatusModal.html',
                        controller: 'CounterStatusControllerCalibrator'
                    })
                    .state('main-panel-verificator', {
                        url: '/verificator/',
                        templateUrl: 'resources/app/verificator/views/main-panel.html',
                        controller: 'MainPanelControllerVerificator'
                    })
                    .state("new-verifications-verificator", {
                        url: '/verifications/new',
                        templateUrl: 'resources/app/verificator/views/new-verifications.html',
                        controller: 'NewVerificationsControllerVerificator'
                    })
                    .state("rejected-verifications-verificator", {
                        url: '/verifications/rejected',
                        templateUrl: 'resources/app/verificator/views/new-verifications.html',
                        controller: 'NewVerificationsControllerVerificator'
                    })
                    .state("employee-show-verificator", {
                        url: '/verificator/employee-show',
                        templateUrl: 'resources/app/verificator/views/employee/show-employee.html',
                        controller: 'UsersControllerVerificator'
                    })
                    .state("verifications-archive-verificator", {
                        url: '/verifications/archive',
                        templateUrl: 'resources/app/verificator/views/archival-verifications.html',
                        controller: 'ArchivalVerificationsControllerVerificator'
                    })
                    .state("reports-provider", {
                        url: '/verifications/reports',
                        templateUrl: 'resources/app/provider/views/reports-provider.html',
                        controller: 'DocumentController'
                    })
                    .state("not-standard-verifications-calibrator", {
                        url: 'calibrator/not-standard-verifications',
                        templateUrl: 'resources/app/calibrator/views/not-standard-verifications.html',
                        controller: 'NotStandardVerificationControllerCalibrator'
                    })
                    .state("verifications-for-provider", {
                        url: 'calibrator/verifications-for-provider',
                        templateUrl: 'resources/app/calibrator/views/verifications-for-provider.html',
                        controller: 'NotStandardVerificationControllerCalibrator'
                    })
                    .state("not-standard-verifications-provider", {
                        url: 'provider/not-standard-verifications',
                        templateUrl: 'resources/app/provider/views/not-standard-verifications.html',
                        controller: 'NotStandardVerificationControllerProvider'
                    })
                    .state("rejected-verifications-protocols-calibrator", {
                        url: '/calibrator/protocols/rejected',
                        templateUrl: 'resources/app/calibrator/views/show-verification-protocols.html',
                        controller: 'DigitalVerificationProtocolsControllerCalibrator'
                    })
                    .state("verifications-protocols-calibrator", {
                        url: '/calibrator/protocols',
                        templateUrl: 'resources/app/calibrator/views/show-verification-protocols.html',
                        controller: 'DigitalVerificationProtocolsControllerCalibrator'

                    })
                    .state("rejected-modal-calibrator", {
                        url: '/calibrator/verifications/receiveAllReasons/',
                        templateUrl: 'resources/app/common/views/modals/reject-verification-modal.html',
                        controller: 'RejectVerificationCalibratorController'
                    })
                    .state("rejected-modal-provider", {
                        url: '/calibrator/verifications/receiveAllReasons/',
                        templateUrl: 'resources/app/common/views/modals/reject-verification-modal.html',
                        controller: 'RejectVerificationProviderController'
                    })
                    .state("verifications-reject-provider", {
                        url: '/calibrator/verifications/rejected-archive/',
                        templateUrl: 'resources/app/provider/views/rejected-verifications.html',
                        controller: 'RejectedVerificationsControllerProvider'
                    })
                    .state("reports-calibrator", {
                        url: '/verifications/reports',
                        templateUrl: 'resources/app/calibrator/views/reports-calibrator.html',
                        controller: 'DocumentController'
                    })
                ;

                /*
                 Extended ui-select-choices: added watch for ng-translate event called translateChangeEnd
                 When translation of page will end, items of select (on the scope) will be changed too.
                 Then we refresh the items of select to get them from scope.
                 */
                $provide.decorator('uiSelectDirective', function ($delegate, $parse, $injector) {
                    var some_directive = $delegate[0],
                        preCompile = some_directive.compile;

                    some_directive.compile = function compile() {
                        var link = preCompile.apply(this, arguments);

                        return function (scope, element, attrs, controller) {
                            link.apply(this, arguments);

                            var $select = controller[0];

                            var rootScope = $injector.get('$rootScope');

                            rootScope.$on('$translateChangeEnd', function (event) {
                                scope.setTypeDataLanguage();
                                $select.refreshItems();
                            });

                        };
                    };

                    return $delegate;
                });

            }]);

    angular.module('employeeModule').run(['UserService', '$state', 'paginationConfig', '$filter', function (userService, $state, paginationConfig, $filter) {
        paginationConfig.firstText = $filter('translate')('FIRST_PAGE');
        paginationConfig.previousText = $filter('translate')('PREVIOUS_PAGE');
        paginationConfig.nextText = $filter('translate')('NEXT');
        paginationConfig.lastText = $filter('translate')('LAST');

        /**
         * Initial state
         */
        userService.getLoggedInUserRoles().success(function (response) {
            var roles = response + '';
            var role = roles.split(',');

            for (var i = 0; i < role.length; i++) {
                if (role[i] === 'PROVIDER_ADMIN' || role[i] === 'PROVIDER_EMPLOYEE')
                    $state.transitionTo('new-verifications-provider');
                if (role[i] === 'CALIBRATOR_ADMIN' || role[i] === 'CALIBRATOR_EMPLOYEE')
                    $state.transitionTo('new-verifications-calibrator');
                if (role[i] === 'STATE_VERIFICATOR_ADMIN' || role[i] === 'STATE_VERIFICATOR_EMPLOYEE')
                    $state.transitionTo('new-verifications-verificator');
            }

        })
    }]);


    angular.module('employeeModule').directive('chosen', function () {
            return {
                priority: 1,
                restrict: 'A',
                link: {
                    pre: function (scope, element, attr, ngModel) {
                        var defaultText = attr.placeholder;
                        angular.element(element[0]).attr('data-placeholder', defaultText);
                    }
                }
            }
        })

        .run(function ($rootScope) {
            $rootScope.FIRST_LAST_NAME_REGEX = /^([A-Z][\u0027]?[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][\u0027]?[a-z]{1,20}[\u002d\u0020]?[A-Z][\u0027]?[a-z]{1,20}|[A-Z][\u0027]?[a-z]{1,20}[\u002d\u0020]?[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}[\u002d\u0020]?[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}[\u002d\u0020]?[A-Z][\u0027]?[a-z]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}[\u002d\u0020]?\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
            $rootScope.MIDDLE_NAME_REGEX = /^([A-Z][\u0027]?[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
            $rootScope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $rootScope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $rootScope.FLAT_REGEX = /^([1-9][0-9]{0,3}|0)$/;
            $rootScope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
            $rootScope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
        });

    define([
        'provider/controllers/InternationalizationController',
        'provider/controllers/TopNavBarControllerProvider',
        'provider/controllers/MainPanelControllerProvider',
        'provider/controllers/ArchivalVerificationsControllerProvider',
        'provider/controllers/NewVerificationsControllerProvider',
        'provider/controllers/AcceptedVerificationsControllerProvider',
        'provider/controllers/AddingVerificationsControllerProvider',
        'provider/controllers/DetailsModalControllerProvider',
        'provider/controllers/SendingModalControllerProvider',
        'provider/controllers/AddEmployeeController',
        'provider/controllers/ResetFormAddEmployeeController',
        'provider/controllers/AddressModalControllerProvider',
        'provider/controllers/UsersController',
        'provider/controllers/SettingsControllerProvider',
        'provider/controllers/NotificationsControllerProvider',
        'provider/controllers/AcceptedVerificationsNotificationsControllerProvider',
        'provider/controllers/ProviderEmployeeControllerProvider',
        'provider/controllers/MailSendingModalControllerProvider',
        'provider/services/VerificationServiceProvider',
        'provider/services/AddressServiceProvider',
        'provider/services/SettingsServiceProvider',
        'provider/services/UserService',
        'provider/controllers/CapacityEmployeeControllerProvider',
        'provider/controllers/GraficEmployeeProvider',
        'provider/controllers/GraphicEmployeeProviderMainPanel',
        'provider/controllers/PieProviderEmployee',
        'provider/controllers/CalendarEmployeeProvider',
        'provider/controllers/ArchivalDetailsModalController',
        'provider/controllers/EditEmployeeController',
        'provider/controllers/NotStandardVerificationControllerProvider',
        'provider/services/NotStandardVerificationServiceProvider',
        'provider/controllers/NotStandardNotificationsControllerProvider',
        'provider/controllers/RejectVerificationProviderController',
        'provider/controllers/RejectedVerificationsControllerProvider',

        'calibrator/controllers/TopNavBarControllerCalibrator',
        'calibrator/controllers/MainPanelControllerCalibrator',
        'calibrator/controllers/NewVerificationsControllerCalibrator',
        'calibrator/controllers/DetailsModalControllerCalibrator',
        'calibrator/controllers/DigitalProtocolsSendingModalControllerCalibrator',
        'calibrator/controllers/CalibrationTestEditModalController',
        'calibrator/controllers/CalibrationTestControllerCalibrator',
        'calibrator/controllers/EmployeeControllerCalibrator',
        'calibrator/controllers/AddressModalControllerCalibrator',
        'calibrator/controllers/ArchivalVerificationsControllerCalibrator',
        'calibrator/controllers/NotificationsControllerCalibrator',
        'calibrator/controllers/CalibrationTestAddControllerCalibrator',
        'calibrator/controllers/TaskForStationController',
        'calibrator/controllers/VerificationListModalController',
        'calibrator/controllers/AddingVerificationsControllerCalibrator',
        'calibrator/controllers/NotStandardNotificationsControllerCalibrator',
        'calibrator/controllers/PlaningTaskNotificationsControllerCalibrator',
        'calibrator/controllers/catalogue/DisassemblyTeamAddModalController',
        'calibrator/controllers/catalogue/DisassemblyTeamEditModalController',
        'calibrator/controllers/catalogue/DisassemblyTeamControllerCalibrator',
        'calibrator/controllers/ReviewScanDocController',
        'calibrator/controllers/UploadBbiFileController',
        'calibrator/controllers/UploadScanDocController',
        'calibrator/controllers/UploadArchiveController',
        'calibrator/controllers/UploadPhotoController',
        'calibrator/controllers/UsersControllerCalibrator',
        'calibrator/controllers/CalibratorEmployeeControllerCalibrator',
        'calibrator/controllers/CapacityEmployeeControllerCalibrator',
        'calibrator/controllers/TaskForStationModalControllerCalibrator',
        'calibrator/controllers/TaskForTeamModalControllerCalibrator',
        'calibrator/controllers/TaskSendingModalControllerCalibrator',
        'calibrator/controllers/VerificationPlanningTaskController',
        'calibrator/controllers/CounterStatusControllerCalibrator',
        'calibrator/controllers/GraphicEmployeeCalibratorMainPanel',
        'calibrator/controllers/InstallPrivateKeyController',
        'calibrator/services/VerificationPlanningTaskService',
        'calibrator/services/CalibrationTestServiceCalibrator',
        'calibrator/services/InitializeLibForDigitalSignService',
        'calibrator/services/AddressServiceCalibrator',
        'calibrator/services/UserServiceCalibrator',
        'calibrator/services/VerificationServiceCalibrator',
        'calibrator/controllers/PieCalibratorEmployee',
        'calibrator/controllers/EditPhotoController',
        'calibrator/services/DisassemblyTeamServiceCalibrator',
        'calibrator/services/CalibrationTaskServiceCalibrator',
        'calibrator/services/DataReceivingServiceCalibrator',
        'calibrator/controllers/NotStandardVerificationControllerCalibrator',
        'calibrator/services/NotStandardVerificationCalibratorService',
        'calibrator/controllers/NotStandardVerificationSendingControllerCalibrator',
        'calibrator/controllers/VerificationsForProviderNotificationController',
        'calibrator/controllers/EditAllUserVerificationsInTaskControllerCalibrator',
        'calibrator/controllers/catalogue/RejectVerificationCalibratorController',
        'calibrator/controllers/RejectedVerificationsControllerCalibrator',

        'verificator/controllers/TopNavBarControllerVerificator',
        'verificator/controllers/MainPanelControllerVerificator',
        'verificator/controllers/NewVerificationsControllerVerificator',
        'verificator/controllers/DetailsModalControllerVerificator',
        'verificator/controllers/SendingModalControllerVerificator',
        'verificator/controllers/EmployeeControllerVerificator',
        'verificator/controllers/CapacityEmployeeControllerVerificator',
        'verificator/controllers/AddressModalControllerVerificator',
        'verificator/controllers/NotificationsControllerVerificator',
        'verificator/controllers/TestRejectControllerVerificator',
        'verificator/controllers/CalibrationTestReviewControllerVerificator',
        'verificator/controllers/ArchivalVerificationsControllerVerificator',
        'verificator/controllers/NewVerificationsControllerVerificator',
        'verificator/controllers/GraphicEmployeeVerificatorMainPanel',
        'verificator/controllers/VerificatorEmployeeControllerVerificator',
        'verificator/controllers/PieVerificatorEmployee',
        'verificator/controllers/UsersControllerVerificator',
        'verificator/controllers/VerificatorSubdivisionAddModalController',
        'verificator/controllers/VerificatorSubdivisionEditModalController',
        'verificator/controllers/VerificatorSubdivisionController',

        'verificator/services/AddressServiceVerificator',
        'verificator/services/VerificatorSubdivisionService',
        'verificator/services/UserServiceVerificator',
        'verificator/services/VerificationServiceVerificator',
        'provider/filters/unique',
        'common/controllers/ProfileInfoController',
        'common/controllers/NotificationsRejectedProtocolsController',
        'common/controllers/EditProfileInfoController',
        'common/controllers/CommonController',
        'common/controllers/DocumentController',
        'common/services/DocumentService',
        'common/services/ProfileService',
        'common/services/EmployeeService',
        'calibrator/controllers/DigitalVerificationProtocolsControllerCalibrator',
        'calibrator/services/DigitalVerificationProtocolsServiceCalibrator',
        'common/controllers/VerificationCloseAlertController',
        'common/controllers/VerificationResetAlertController'
    ], function () {
    });
})();
