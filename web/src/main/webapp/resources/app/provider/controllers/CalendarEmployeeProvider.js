
angular
    .module('employeeModule')
    .controller('CalendarEmployeeProvider', ['$rootScope', '$scope', '$controller', 'UserService', '$modal', '$log', 'ngTableParams', '$timeout', '$filter', '$translate',
        function ($rootScope, $scope, $controller, userService, $modal, $log, ngTableParams, $timeout, $filter, $translate) {

            /**
             *  Date picker and formatter setup
             *
             */
            $scope.toMaxDate = new Date();

            $scope.myDatePicker = {};
            $scope.myDatePicker.pickerDate = null;
            $scope.defaultDate = null;

            $scope.initDatePicker = function (date) {

                $scope.myDatePicker.pickerDate = {
                    startDate: moment().startOf('year'),
                    endDate: moment().endOf('year')
                };

                if ($scope.defaultDate == null) {
                    $scope.defaultDate = angular.copy($scope.myDatePicker.pickerDate);
                }

                $scope.setTypeDataLangDatePicker = function () {
                    var lang = $translate.use();
                    if (lang === 'ukr') {
                        moment.locale('uk'); 
                    } else {
                        moment.locale('en'); 
                    }
                    $scope.opts = {
                        format: 'DD-MM-YYYY',
                        showDropdowns: true,
                        locale: {
                            firstDay: 1,
                            fromLabel: $filter('translate')('FROM_LABEL'),
                            toLabel: $filter('translate')('TO_LABEL'),
                            applyLabel: $filter('translate')('APPLY_LABEL'),
                            cancelLabel: $filter('translate')('CANCEL_LABEL'),
                            customRangeLabel: $filter('translate')('CUSTOM_RANGE_LABEL')
                        },
                        ranges: {},
                        eventHandlers: {}
                    };
                };

                $scope.setTypeDataLanguage = function () {
                    $scope.setTypeDataLangDatePicker();
                };

                $scope.setTypeDataLanguage();
            };

            $scope.showPicker = function ($event) {
                angular.element("#datepickerfieldcalendar").trigger("click");
            };

            $scope.isDateDefault = function () {
                var pickerDate = $scope.myDatePicker.pickerDate;

                if (pickerDate == null || $scope.defaultDate == null) {
                    return true;
                }
                if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') 
                    && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                    return true;
                }
                return false;
            };

            $scope.clearDate = function () {
                $scope.myDatePicker.pickerDate = $scope.defaultDate;
            };

            $scope.initDatePicker();

            $rootScope.$on('$translateChangeEnd', function(event){
                $scope.setTypeDataLanguage();
            });

            var me = $scope;
            $controller('GraficEmployeeProvider', {
                $scope: $scope
            });

            var date1 = new Date(new Date().getFullYear(), 0, 1);
            var date2 = new Date();
            $scope.dataToSearch = {
                fromDate: date1,
                toDate: date2
            };


            $scope.cancel = function () {
                $modal.dismiss();
            };

            $scope.showGrafic = function () {
                var dataToSearch = {
                		
                    fromDate: $scope.changeDateToSend($scope.myDatePicker.pickerDate.startDate.format("YYYY-MM-DD")),
                    toDate: $scope.changeDateToSend($scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD"))
                };
                userService.getGraficData(dataToSearch)
                    .success(function (data) {
                        return me.displayGrafic(data);
                    });
            };

            $scope.changeDateToSend = function (value) {
            	if ($scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD") != null) {
            		$scope.fromMaxDate = $scope.myDatePicker.pickerDate.endDate.format("YYYY-MM-DD");
            	} else {
            		$scope.fromMaxDate = new Date();
            	}
            	
            	$scope.toMinDate = $scope.dataToSearch.fromDate;
            	
                if (angular.isUndefined(value)) {
                    return null;

                } else {

                    return $filter('date')(value, 'dd-MM-yyyy');
                }
            };
            $scope.showGrafic();


        }
    ]
)