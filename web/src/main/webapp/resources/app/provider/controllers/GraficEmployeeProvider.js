
angular
    .module('employeeModule')
    .controller('GraficEmployeeProvider', ['$translate', '$scope', '$log', '$filter',
        function ($translate, $scope, $log, $filter) {
            $scope.displayGrafic = function (graficData) {

                $scope.listMonth = [];
                $scope.getXdata = function (monthNumber, year) {
                    var moment = require('moment');
                    var monthNames = [];
                    var count = 0;
                    if ($translate.use()=='ukr') {
                        moment.locale('uk')
                    }
                    if ($translate.use()=='eng') {
                        moment.locale('en')
                    }

                    while (count < 12) monthNames.push(moment().month(count++).format("MMMM"));
                    return monthNames[monthNumber] + ' ' + year;
                };

                if(!graficData.length==0) {
                    for (var i = 0; i < graficData[0].monthList.length; i++) {
                        $scope.listMonth.push($scope.getXdata(graficData[0].monthList[i].month, graficData[0].monthList[i].year));
                    }
                }else{
                    $scope.listMonth.push($filter('translate')('NO_INFO_IN_PERIOD'));
                }
                
                if (graficData.length > 0) {
                    $scope.chartConfig = {
                        options: {
                            chart: {
                                type: 'column'
                            },
                            title: {
                                text: $filter('translate')('EMPLOYEE_PRODUCTIVITY'),
                                x: -20 
                            },
                            subtitle: {
                                text: $filter('translate')('COUNT_OF_VERIFICATION_CHART_TITLE'),
                                x: -20
                            },

                            xAxis: {
                                categories: $scope.listMonth,
                                crosshair: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: $filter('translate')('COUNT_OF_VERIFICATIONS')
                                }
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                '<td style="padding:0"><b>{point.y:.1f}'+' '+ $filter('translate')('VERIFICATIONS_PROCESSED') + '</b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                        },
                        series: graficData

                    }
                } else {
                    $scope.chartConfig = {
                        title: {
                            text: $filter('translate')('NO_INFO_IN_PERIOD'),
                            x: -20 
                        }
                    }
                }
            }
        }
    ]);