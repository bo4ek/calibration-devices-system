angular
    .module('adminModule')
    .controller('ReportsController', ['$rootScope', '$scope', 'OrganizationService', function ($rootScope, $scope, organizationService) {

        $scope.myDatePicker = {};
        $scope.myDatePicker.pickerDate = null;
        $scope.defaultDate = null;
        $scope.module = null;

        $scope.selectedValues = {};
        $scope.calibrators = [];

        $scope.receiveCalibrators = function () {
            organizationService.getAllCalibrators()
                .success(function (calibrators) {
                        $scope.calibrators = calibrators;
                        $scope.selectedValues.calibrator = undefined;
                        debugger;
                    }
                );
        };

        $scope.showPicker = function ($event) {
            angular.element("#datepickerfield").trigger("click");
        };


        $scope.isDateDefault = function () {
            var pickerDate = $scope.myDatePicker.pickerDate;

            if (pickerDate == null || $scope.defaultDate == null) { //moment when page is just loaded
                return true;
            }
            if (pickerDate.startDate.isSame($scope.defaultDate.startDate, 'day') //compare by day
                && pickerDate.endDate.isSame($scope.defaultDate.endDate, 'day')) {
                return true;
            }
            return false;
        };

        $scope.downloadResultReport = function (documentType) {
            $scope.startDate = $scope.myDatePicker.pickerDate.startDate.format("DD.MM.YYYY");
            $scope.endDate = $scope.myDatePicker.pickerDate.endDate.format("DD.MM.YYYY");
            var url = "doc/report/" + $scope.selectedValues.calibrator.id + "/" + documentType + "/" + $scope.startDate + "/" + $scope.endDate + "/xls";
            $scope.selectedValues.calibrator = undefined;
            $scope.myDatePicker.pickerDate = null;
            location.href = url;
        };

        $scope.initDatePicker = function () {

            moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
            $scope.opts = {
                format: 'DD-MM-YYYY',
                showDropdowns: true,
                locale: {
                    firstDay: 1,
                    fromLabel: 'Від',
                    toLabel: 'До',
                    applyLabel: "Прийняти",
                    cancelLabel: "Зачинити",
                    customRangeLabel: "Обрати самостійно",
                },
                ranges: {
                    'Сьогодні': [moment(), moment()],
                    'Вчора': [moment().subtract(1, 'day'), moment().subtract(1, 'day')],
                    'Цього тижня': [moment().startOf('week'), moment().endOf('week')],
                    'Цього місяця': [moment().startOf('month'), moment().endOf('month')],
                    'Попереднього місяця': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                },
                eventHandlers: {}
            };
        };

    }]);
