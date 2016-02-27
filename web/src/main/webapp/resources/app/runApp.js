require.config({
    paths: {
        angular: '../assets/bower_components/angular/angular.min',
        csrfInterceptor: '../assets/bower_components/spring-security-csrf-token-interceptor/dist/spring-security-csrf-token-interceptor.min',
        angularBootstrap: '../assets/bower_components/angular-bootstrap/ui-bootstrap-tpls.min',
        angularUIRouter: '../assets/bower_components/angular-ui-router/release/angular-ui-router.min',
        showErrors: '../assets/bower_components/angular-bootstrap-show-errors/src/showErrors.min',
        ngTable: '../assets/bower_components/ng-table/ng-table',
        angularTranslate: '../assets/bower_components/angular-translate/angular-translate.min',
        angularTranslateLoaderStaticFiles: '../assets/bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.min',
        angularCookie: '../assets/bower_components/angular-cookies/angular-cookies.min',
        angularTranslateStorageLocal: '../assets/bower_components/angular-translate-storage-local/angular-translate-storage-local.min',
        angularTranslateStorageCookie: '../assets/bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.min',
        employeeModule: 'employeeModule',
        highchartsAngular:  '../assets/bower_components/highcharts-ng/dist/highcharts-ng.min',
        highcharts:  '../assets/bower_components/highcharts-release/highcharts.src',
        chosen : '../assets/angular-chosen-localytics/chosen',
        standalone:  '../assets/bower_components/highcharts-release/adapters/standalone-framework.src',
        upload : '../assets/bower_components/ng-file-upload/ng-file-upload-all.min',
        ngRoute : '../assets/bower_components/angular-route/angular-route.min',
        angularLoadingBar: '../assets/bower_components/angular-loading-bar/build/loading-bar',
        jquery: '../assets/bower_components/jquery/dist/jquery.js',
        moment: "../assets/bower_components/moment/min/moment-with-locales.min",
        bootstrapDateRangePicker: "../assets/bower_components/bootstrap-daterangepicker/daterangepicker",
        angularDateRangePicker: "../assets/bower_components/angular-daterangepicker/js/angular-daterangepicker.min",
        angularUISelect: "../assets/bower_components/ui-select/dist/select.min",
        ngSanitize: "../assets/bower_components/angular-sanitize/angular-sanitize.min",
        angularAnimate : "../assets/bower_components/angular-animate/angular-animate.min",
        angularJsToaster : "../assets/bower_components/angularjs-toaster/toaster.min",
        semanticUI: "../assets/bower_components/semantic/dist/semantic.min",
        globalSearchModule:'globalSearch/globalSearchModule',
        focusModule: 'common/directives/focusModule',
        angularLocaleUk: '../assets/bower_components/angular-i18n/angular-locale_uk'
    },
    shim: {

        angular: {
            exports: "angular"
        },

        angularAnimate:{
            deps:['angular']
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
            deps: ['angular', 'angularTranslate', 'angularTranslateStorageCookie']
        },
        angularTranslateLoaderStaticFiles: {
            deps: ['angular', 'angularTranslate']
        },
        angularUIRouter: {
            deps: ['angular']
        },
        showErrors: {
            deps: ['angular']
        },
        ngTable: {
        	exports:"ngTable",
        	 deps: [ 'angular' ]
        },
        highcharts:{
            deps: [ 'angular','standalone' ]
        },
        highchartsAngular:{
            deps: [ 'angular','highcharts']

        },
        chosen : {
            exports : "chosen",
            deps : [ 'angular' ]
        },
        upload : {
            deps : [ 'angular' ],
            exports : "upload"
        },
        ngRoute : {
            deps : [ 'angular' ],
            exports : "ngRoute"
        },
        angularLoadingBar : {
            deps : [ 'angular' ],
            exports : "angularLoadingBar"
        },

        angularDateRangePicker: {
            deps: ['angular', 'moment', 'bootstrapDateRangePicker'],
            exports: 'angularDateRangePicker'
        },

        angularUISelect: {
            deps:['angular'],
            exports: 'angularUISelect'
        },

        ngSanitize:{
            deps:['angular']
        },
        angularJsToaster: {
            deps:['angular', 'angularAnimate'],
            exports: 'angularJsToaster'
        },
        angularLocaleUk: {
            deps:['angular'],
            exports: 'angularLocaleUk'
        },
        semanticUI:{
            deps:['angular']
        },
        globalSearchModule:{
            deps:['angular']
        },
        focusModule:{
            deps:['angular']
        },
        employeeModule: {
            deps: ['angular', 'csrfInterceptor', 'angularBootstrap', 'angularTranslate', 'angularCookie', 'angularTranslateStorageCookie',
                   'angularTranslateStorageLocal', 'angularTranslateLoaderStaticFiles', 'angularUIRouter', 'angularLocaleUk',
                'showErrors', 'ngTable', 'highchartsAngular', 'chosen', 'upload', 'ngRoute', 'angularLoadingBar',
                'moment', 'bootstrapDateRangePicker', 'angularDateRangePicker', 'angularUISelect', 'ngSanitize',
                'angularAnimate', 'angularJsToaster', 'semanticUI','globalSearchModule', 'focusModule']
        }
    }
});

require(['employeeModule'], function () {
    angular.bootstrap(document.getElementById('employeeModule'), ['employeeModule']);
});
