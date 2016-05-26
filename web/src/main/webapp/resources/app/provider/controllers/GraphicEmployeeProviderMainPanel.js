
angular
    .module('employeeModule')
    .controller('GraphicEmployeeProviderMainPanel', ['$scope', '$log',
        function ($scope, $log) {
            $scope.displayGrafic = function (graficData) {

                $scope.listMonth = [];
                $scope.getXdata = function (monthNumber, year) {
                    var monthNames = ['Січень', 'Лютий', 'Березень', 'Квітень', 'Травень', 'Червень',
                        'Липень', 'Серпень', 'Вересень', 'Жовтень', 'Листопад', 'Грудень'];
                    return monthNames[monthNumber] + ' ' + year;
                };
                if(!graficData.length==0) {
                    for (var i = 0; i < graficData[0].monthList.length; i++) {
                        $scope.listMonth.push($scope.getXdata(graficData[0].monthList[i].month, graficData[0].monthList[i].year));
                    }
                }else{
                    $scope.listMonth.push('За даний період немає інформації про працівників');
                }

                $log.debug(graficData)
                if (graficData.length > 0) {
                    $scope.chartConfig = {
                        options: {
                            chart: {
                                type: 'column'
                            },
                            title: {
                                text: 'Кількість заявок, що надійшли до організації',
                                x: -20 //center
                            },
                            xAxis: {
                                categories: $scope.listMonth,
                                crosshair: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Кількість заявок '
                                }
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0"> </td>' +
                                '<td style="padding:0"><b>{point.y:.1f} надійшло заявок</b></td></tr>',
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
                            text: 'За даний період немає інформації про працівників',
                            x: -20 //center
                        }
                    }
                }
            }
        }
    ]);


