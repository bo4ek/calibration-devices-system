(function () {
    angular
        .module(
        'welcomeModule',
        [ 'spring-security-csrf-token-interceptor', 'ui.bootstrap',
            'pascalprecht.translate', 'ngCookies', 'ui.router',
            'ui.bootstrap.showErrors', 'ngTable',
            'localytics.directives',
            'frapontillo.bootstrap-switch', 'luegg.directives',
            'irontec.simpleChat', 'ui.select', 'ngSanitize', 'ngAnimate', 'toaster', 'slick'])

        .config(
        [
            '$translateProvider',
            '$stateProvider',
            '$urlRouterProvider',
            'showErrorsConfigProvider',
            '$provide',
            function ($translateProvider, $stateProvider, $urlRouterProvider, showErrorsConfigProvider, $provide) {

                showErrorsConfigProvider.showSuccess(true);

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
                $urlRouterProvider.otherwise('/start');

                $stateProvider
                    .state(
                    'start',
                    {
                        url: '/start',
                        templateUrl: 'resources/app/welcome/views/start.html',
                        controller: 'WelcomePageController'

                    })
                    .state(
                    'about',
                    {
                        url: '/about',
                        templateUrl: 'resources/app/welcome/views/about.html'
                    })
                    .state(
                    'login',
                    {
                        url: '/login',
                        templateUrl: 'resources/app/welcome/views/login.html',
                        controller: 'LoginController'
                    })
                    .state(
                    'application-sending',
                    {
                        url: '/application-sending/{verificationId}',
                        templateUrl: 'resources/app/welcome/views/application-sending.html',
                        controller: 'ApplicationSendingController'
                    })
                    .state(
                    'application-status',
                    {
                        url: '/application-status/{clientCode}',
                        templateUrl: 'resources/app/welcome/views/application-status.html',
                        controller: 'ApplicationStatusController'
                    })
                /*
                 Extended ui-select-choices: added watch for ng-translate event called translateChangeEnd
                 When translation of page will end, items of select (on the scope) will be changed too.
                 Then we refresh the items of select to get them from scope.
                 */
                $provide.decorator('uiSelectDirective', function ($delegate, $parse, $injector) {
                    var some_directive = $delegate[ 0],
                        preCompile = some_directive.compile;

                    some_directive.compile = function compile() {
                        var link = preCompile.apply(this, arguments);

                        return function (scope, element, attrs, controller) {
                            link.apply(this, arguments);

                            var $select = controller[ 0 ];

                            var rootScope = $injector.get('$rootScope');

                            rootScope.$on('$translateChangeEnd', function (event) {
                                $select.refreshItems();
                            });

                        };
                    };

                    return $delegate;
                });
            } ])

        .run(function ($rootScope) {
            $rootScope.FIRST_LAST_NAME_REGEX = /^([A-Z][\u0027]?[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][\u0027]?[a-z]{1,20}[\u002d\u0020]?[A-Z][\u0027]?[a-z]{1,20}|[A-Z][\u0027]?[a-z]{1,20}[\u002d\u0020]?[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}[\u002d\u0020]?[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}|[A-Z][a-z]{1,20}[\u0027]?[a-z]{0,20}[\u002d\u0020]?[A-Z][\u0027]?[a-z]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}[\u002d\u0020]?\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u002d\u0020]?[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]?[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
            $rootScope.MIDDLE_NAME_REGEX = /^([A-Z][\u0027]{0,1}[a-z]{1,20}|[A-Z][a-z]{1,20}[\u0027]{0,1}[a-z]{0,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,20}|[\u0410-\u042f\u0407\u0406\u0404][\u0430-\u044f\u0456\u0457\u0454]{1,10}[\u0027]{0,1}[\u0430-\u044f\u0456\u0457\u0454]{1,10})$/;
            $rootScope.PHONE_REGEX = /^[1-9]\d{8}$/;
            $rootScope.EMAIL_REGEX = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
            $rootScope.FLAT_REGEX = /^([1-9][0-9]{0,3}|0)$/;
            $rootScope.BUILDING_REGEX = /^[1-9]{1}[0-9]{0,3}([A-Za-z]|[\u0410-\u042f\u0407\u0406\u0430-\u044f\u0456\u0457]){0,1}$/;
            $rootScope.USERNAME_REGEX = /^[a-z0-9_-]{3,16}$/;
        });

    define([ 'controllers/LoginController',
            'controllers/ApplicationSendingController',
            'controllers/ApplicationStatusController',
            'controllers/InternationalizationController',
            'controllers/NavigationController',
            'controllers/WelcomePageController',
            'controllers/WelcomeCloseAlertController',
            'controllers/DetailsController', 'controllers/FeedbackController',
            'controllers/ChatController', 'services/DataReceivingService',
            'services/DataSendingService', 'directives/OnStartupMessage',
            'directives/unique', 'filters/HideSelected'],
        function () {
        });
})();
