
angular
    .module('employeeModule')
    .controller('PieCalibratorEmployee', ['$scope', '$log', '$filter',
        function ($scope, $log, $filter) {
            $scope.displayGraficPipe = function (data) {
                    $scope.chartConfic = {
                        options: {
                            chart: {
                                type: 'pie'
                            },
                            title: {
                                text: '',
                                x: -20
                            },

                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0"> </td>' +
                                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            }
                        },
                        series:
                            [{
                            data: [[$filter('translate')('NOT_ASSIGNED_VERIFICATIONS'),data.NO_EMPLOYEE],[$filter('translate')('ASSIGNED_VERIFICATIONS'),data.HAS_EMPLOYEE]]
                        }]



                }
            }
        }
    ]);
