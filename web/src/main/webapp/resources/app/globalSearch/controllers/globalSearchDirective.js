
(function () {
    'use strict';
    angular.module('globalSearchModule')
        .factory('globalSearchService', function ($http) {
            return {
                getAllFilters: function (locationUrl) {
                    return getData(locationUrl);
                },
                saveFilter: function (locationUrl, filter) {
                    return sendData("add/" + locationUrl, filter);
                },
                deleteFilter: function (locationUrl, filterName) {
                    return sendData("delete/" + locationUrl, filterName);
                },
                updateFilter: function (locationUrl, filter) {
                    return sendData("update/" + locationUrl, filter);
                }
            };
            function getData(url) {
                return $http.get('globalSearch/' + url).success(function (result) {
                    return result;
                }).error(function (err) {
                    return err;
                });
            }

            function sendData(url, data) {
                return $http.post('globalSearch/' + url, data)
                    .then(function (result) {
                        return result.status;
                    });
            }
        })
        .directive('cdsGlobalSearch', function ($location, globalSearchService) {
            return {
                restrict: 'E',
                scope: {
                    params: '=',
                    model: '=',
                    placeholder: '@'
                },
                templateUrl: 'resources/app/globalSearch/views/cds-global-search.html',
                link: function ($scope, $element, $attrs, $filter) {
                    var locationUrl = $location.path().replace(/\//g, '-').slice(1);
                    $scope.editSavedFilterName = false;
                    $scope.selected = {};
                    $scope.selectedParams = [];
                    $scope.selectedValues = [];
                    $scope.savedFilters = [];
                    $scope.selectedSavedFilter = {};
                    $scope.mainButton = false;
                    $scope.savedFilterNameInput = '';
                    $scope.newSearchParamAvailable = ($scope.params.length - $scope.selectedParams.length > 0);
                    $scope.clickMainButton = function () {
                        $scope.mainButton = !$scope.mainButton;
                        $scope.reloadSelectedParams();

                        $scope.newSearchParamAvailable = ($scope.params.length - $scope.selectedParams.length > 0);
                        $scope.getAllSavedFilters();
                    };
                    $scope.newSearchParam = function () {
                        $scope.selectedParams.push({});
                        $scope.selectedKey = '';
                        $scope.newSearchParamAvailable = ($scope.params.length - $scope.selectedParams.length > 0);
                        $scope.myDatePicker = {};
                        $scope.myDatePicker.pickerDate = {
                            startDate: moment(),
                            endDate: moment()
                        };
                    };
                    $scope.setParamsToModel = function () {
                        for (var i = 0; i < $scope.selectedParams.length; ++i) {
                            var modelMapIndex = $scope.model.map(function (e) {
                                return e.key
                            }).indexOf($scope.selectedParams[i].params.key);
                            if (modelMapIndex >= 0) {
                                $scope.model[modelMapIndex].value = $scope.selectedParams[i].params.value;

                            } else {
                                $scope.model.push({
                                    key: $scope.selectedParams[i].params.key,
                                    value: $scope.selectedParams[i].params.value,
                                    type: $scope.selectedParams[i].params.type,
                                    name: $scope.selectedParams[i].params.name
                                });
                            }
                        }
                        $scope.clearParams();
                        $scope.clearModel();
                        $scope.clearSelectedParams();
                    };
                    $scope.deleteSearchParam = function (index) {
                        $scope.selectedParams.splice(index, 1);
                        $scope.newSearchParamAvailable = ($scope.params.length - $scope.selectedParams.length > 0);
                        $scope.clearParams();
                    };
                    $scope.clearAllSearchParams = function () {
                        $scope.selectedParams = [];
                        $scope.selected = {};
                        $scope.newSearchParamAvailable = ($scope.params.length - $scope.selectedParams.length > 0);
                        $scope.clearModel();
                    };
                    $scope.clearModel = function () {
                        for (var i = $scope.model.length - 1; i >= 0; i--) {
                            var paramIndex = $scope.params.map(function (e) {
                                return e.key
                            }).indexOf($scope.model[i].key);
                            var selectedParamIndex = $scope.selectedParams.map(function (e) {
                                return e.params.key
                            }).indexOf($scope.model[i].key);
                            if (paramIndex >= 0 && selectedParamIndex < 0) {
                                $scope.model.splice(i, 1)
                            }
                        }
                    };
                    $scope.clearSelectedParams = function () {
                        for (var i = $scope.selectedParams.length - 1; i >= 0; i--) {
                            if (!$scope.selectedParams[i].hasOwnProperty('params')) {
                                $scope.selectedParams.splice(i, 1);
                            }
                        }
                        $scope.selectedParams.forEach(function (param) {
                            var modelIndex = $scope.model.map(function (e) {
                                return e.key
                            }).indexOf(param.params.key);
                            if (modelIndex < 0) {
                                param.params.value = '';
                            }

                        });
                    };
                    $scope.clearParams = function () {
                        $scope.params.forEach(function (param) {
                            var paramIndex = $scope.selectedParams.map(function (e) {
                                if (e.hasOwnProperty('params')) {
                                    return e.params.key
                                } else {
                                    return -1;
                                }
                            }).indexOf(param.key);
                            if (paramIndex < 0) {
                                param.value = '';
                            }
                        });

                    };
                    $scope.reloadSelectedParams = function () {
                        $scope.selectedParams = [];
                        $scope.model.forEach(function (param) {
                            var paramIndex = $scope.params.map(function (e) {
                                return e.key;
                            }).indexOf(param.key);
                            if (paramIndex >= 0) {
                                if ($scope.params[paramIndex].hasOwnProperty('options')) {
                                    $scope.selectedParams.push({
                                        params: {
                                            key: param.key,
                                            name: param.name,
                                            type: param.type,
                                            value: param.value,
                                            options: $scope.params[paramIndex].options
                                        }
                                    });
                                }
                                else {
                                    $scope.selectedParams.push({
                                        params: {
                                            key: param.key,
                                            name: param.name,
                                            type: param.type,
                                            value: param.value
                                        }
                                    });
                                }
                            }
                        });
                        $scope.clearModel();
                        $scope.clearSelectedParams();
                        $scope.clearParams();
                    };
                    $scope.allIsSelected = function (status) {
                        var isSelected = status;
                        $scope.selectedParams.forEach(function (param) {
                            if (!param.hasOwnProperty('params') || !param.params.hasOwnProperty("key")) {
                                isSelected = false;
                            }
                        });
                        if ($scope.selectedParams.length == 0) {
                            isSelected = false;
                        }
                        return isSelected;
                    };
                    $scope.isFiltered = function (filterList) {
                        return function (param) {
                            var i = (filterList.map(function (e) {
                                if (e.hasOwnProperty("params")) {
                                    return e.params.key;
                                }
                            }).indexOf(param.key));
                            return i < 0;
                        }
                    };
                    $scope.formats = ['DD-MM-YYYY', 'YYYY/MM/DD', 'DD.MM.YYYY', 'shortDate'];
                    $scope.opts = {
                        autoUpdateInput: false,
                        format: $scope.formats[2],
                        showDropdowns: true,
                        minDate: '01-01-2013'
                    };
                    $scope.setDateToSelectedParam = function (index) {
                        $scope.selectedParams[index].params.value = [];
                        $scope.selectedParams[index].params.value
                            .push($scope.myDatePicker.pickerDate.startDate.format($scope.formats[2]),
                                $scope.myDatePicker.pickerDate.endDate.format($scope.formats[2]));
                    };
                    $scope.getAllSavedFilters = function () {
                        globalSearchService.getAllFilters(locationUrl)
                            .success(function (result) {
                                $scope.savedFilters = [];
                                for (var i = 0; i < result.length; i++) {
                                    $scope.savedFilters.push({
                                        name: result[i].name,
                                        filter: JSON.parse([result[i].filter])
                                    })
                                }
                            }, function (result) {
                                $log.debug('error fetching data:', result);
                            });
                    };
                    $scope.getAllSavedFilters();
                    $scope.saveFilter = function () {
                        $scope.getAllSavedFilters();
                        $scope.getAllSavedFilters();
                        if ($scope.selectedParams.length > 0) {
                            var filter = JSON.stringify($scope.selectedParams);
                            var newFilter = {
                                name: $scope.savedFilterNameInput,
                                filter: filter
                            };
                        }
                        if ($scope.savedFilters.map(function (e) {
                                return e.name
                            }).indexOf($scope.savedFilterNameInput) < 0) {
                            globalSearchService.saveFilter(locationUrl, newFilter);
                        }
                        else {
                            globalSearchService.updateFilter(locationUrl, newFilter);
                        }
                        $scope.getAllSavedFilters();
                        $scope.selected.savedFilter = {};
                        $scope.selected.savedFilter.filter = angular.copy($scope.selectedParams);
                        $scope.selected.savedFilter.name = $scope.savedFilterNameInput;
                    };
                    $scope.deleteSavedFilter = function () {
                        globalSearchService.deleteFilter(locationUrl, $scope.selected.savedFilter);
                        $scope.getAllSavedFilters();
                        $scope.getAllSavedFilters();
                        $scope.clearAllSearchParams();
                        $scope.reloadSelectedParams();

                    };
                    $scope.$watch('selected', function (newParam, oldParam) {
                        if (newParam.savedFilter === undefined) {
                            $scope.selectedParams = [];
                            $scope.setParamsToModel();
                            $scope.savedFilterNameInput = '';

                        } else if ($scope.selected.hasOwnProperty('savedFilter')) {
                            if ($scope.selected.savedFilter && $scope.selected.savedFilter.hasOwnProperty('filter')) {
                                $scope.selectedParams = [];
                                for (var i = 0; i < $scope.selected.savedFilter.filter.length; i++) {
                                    if ($scope.selected.savedFilter.filter[i].params.type == 'Date') {
                                        $scope.myDatePicker = {};
                                        $scope.myDatePicker.pickerDate = {};
                                        $scope.myDatePicker.pickerDate.startDate = moment($scope.selected.savedFilter.filter[i].params.value[0], "DD_MM-YYYY");
                                        $scope.myDatePicker.pickerDate.endDate = moment($scope.selected.savedFilter.filter[i].params.value[1], "DD_MM-YYYY");
                                    }
                                    $scope.selectedParams.push({
                                        params: $scope.selected.savedFilter.filter[i].params
                                    });
                                }
                                $scope.savedFilterNameInput = $scope.selected.savedFilter.name;
                                $scope.setParamsToModel();
                            }
                        }
                    }, true);
                }
            };
        });
})();