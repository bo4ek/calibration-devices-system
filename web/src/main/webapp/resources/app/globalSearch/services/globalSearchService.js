
(function () {
        'use strict';
        angular.module('globalSearchModule')
            .factory('globalSearchService', function ($http) {
                return {
                    getAllFilters: function (locationUrl) {
                        return getData(locationUrl);
                    },
                    saveFilter: function (locationUrl, filter) {
                        return sendData("add/"+locationUrl,filter);
                    },
                    deleteFilter:function(locationUrl,filterName){
                        return sendData("delete/"+locationUrl,filterName);
                    }
                };
                function getData(url) {
                    return $http.get('globalSearch' + url).success(function (result) {
                        return result;
                    }).error(function (err) {
                        return err;
                    });
                }
                function sendData(url, data) {
                    return $http.post('globalSearch' + url, data)
                        .then(function (result) {
                            return result.status;
                        });
                }
            });
    }
);