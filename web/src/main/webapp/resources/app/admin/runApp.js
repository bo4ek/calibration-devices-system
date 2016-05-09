require
    .config({
        paths: {
            angular: '../../assets/bower_components/angular/angular.min',
            csrfInterceptor: '../../assets/bower_components/spring-security-csrf-token-interceptor/dist/spring-security-csrf-token-interceptor.min',
            angularBootstrap: '../../assets/bower_components/angular-bootstrap/ui-bootstrap-tpls.min',
            angularUIRouter: '../../assets/bower_components/angular-ui-router/release/angular-ui-router.min',
            showErrors: '../../assets/bower_components/angular-bootstrap-show-errors/src/showErrors.min',
            ngTable: '../../assets/bower_components/ng-table/ng-table',
            angularTranslate: '../../assets/bower_components/angular-translate/angular-translate.min',
            angularTranslateLoaderStaticFiles: '../../assets/bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.min',
            angularCookie: '../../assets/bower_components/angular-cookies/angular-cookies.min',
            angularTranslateStorageLocal: '../../assets/bower_components/angular-translate-storage-local/angular-translate-storage-local.min',
            angularTranslateStorageCookie: '../../assets/bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.min',
            chosen: '../../assets/bower_components/angular-chosen-localytics/chosen',
            angularLoadingBars: '../../assets/bower_components/angular-loading-bar/build/loading-bar',
            angularUISelect: "../../assets/bower_components/ui-select/dist/select.min",
            ngSanitize: "../../assets/bower_components/angular-sanitize/angular-sanitize.min",
            semanticUI: "../../assets/bower_components/semantic/dist/semantic.min",
            angularAnimate: "../../assets/bower_components/angular-animate/angular-animate.min",
            angularJsToaster: "../../assets/bower_components/angularjs-toaster/toaster.min",
            adminModule: 'adminModule',
            moment: "../../assets/bower_components/moment/min/moment-with-locales.min",
            checklistModel: '../../assets/bower_components/checklist-model/checklist-model',
            bootstrapDateRangePicker: "../../assets/bower_components/bootstrap-daterangepicker/daterangepicker",
            angularDateRangePicker: "../../assets/bower_components/angular-daterangepicker/js/angular-daterangepicker.min",
            delayModule: 'directives/delayModule'
        },
        shim: {
            angular: {
                exports: "angular"
            },
            csrfInterceptor: {
                deps: ['angular']
            },
            angularBootstrap: {
                deps: ['angular']
            },
            angularTranslate: {
                deps: ['angular']
            },
            angularCookie: {
                deps: ['angular']
            },
            angularTranslateStorageCookie: {
                deps: ['angular', 'angularTranslate', 'angularCookie']
            },
            angularTranslateStorageLocal: {
                deps: ['angular', 'angularTranslate',
                    'angularTranslateStorageCookie']
            },
            angularTranslateLoaderStaticFiles: {
                deps: ['angular', 'angularTranslate']
            },
            angularUIRouter: {
                deps: ['angular']
            },
            showErrors: {
                deps: ['angularBootstrap']
            },

            ngTable: {
                exports: "ngTable",
                deps: ['angular']
            },

            chosen: {
                exports: "chosen",
                deps: ['angular']
            },
            angularLoadingBars: {
                deps: ['angular'],
                exports: "angularLoadingBars"
            },

            ngSanitize: {
                deps: ['angular']
            },
            angularUISelect: {
                deps: ['angular'],
                exports: 'angularUISelect'
            },
            semanticUI: {
                deps: ['angular']
            },
            angularDateRangePicker: {
                deps: ['angular', 'moment', 'bootstrapDateRangePicker'],
                exports: 'angularDateRangePicker'
            },
            checklistModel: {
                deps: ['angular']
            },
            angularAnimate: {
                deps: ['angular']
            },
            angularJsToaster: {
                deps: ['angular', 'angularAnimate'],
                exports: 'angularJsToaster'
            },
            delayModule:{
                deps:['angular']
            },

            adminModule: {

                deps: ['angular', 'csrfInterceptor', 'angularBootstrap',
                    'angularTranslate', 'angularCookie',
                    'angularTranslateStorageCookie',
                    'angularTranslateStorageLocal',
                    'angularTranslateLoaderStaticFiles',
                    'angularUIRouter', 'showErrors', 'ngTable',
                    'chosen', 'angularLoadingBars', 'moment', 'bootstrapDateRangePicker', 'angularDateRangePicker',
                    'angularUISelect', 'ngSanitize', 'checklistModel', 'angularAnimate', 'angularJsToaster', 'semanticUI', 'delayModule']
            }
        }
    });

require(['adminModule'], function () {
    angular
        .bootstrap(document.getElementById('adminModule'),
        ['adminModule']);
});
