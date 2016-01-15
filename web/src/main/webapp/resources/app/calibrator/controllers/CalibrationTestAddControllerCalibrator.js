/**
 * Created by Konyk on 11.08.2015.
 */
angular
    .module('employeeModule')
    .controller('CalibrationTestAddControllerCalibrator', ['$rootScope', '$translate', '$scope', '$modal', '$http', '$log',
        'CalibrationTestServiceCalibrator', '$location', 'Upload', '$timeout', 'toaster', '$filter',
        function ($rootScope, $translate, $scope, $modal, $http, $log, calibrationTestServiceCalibrator, $location, Upload,  $timeout, toaster, $filter) {

            $scope.testId = $location.search().param;
            $scope.hasProtocol = $location.search().loadProtocol || false;
            $scope.isVerification = $location.search().ver || false;

            $scope.fileLoaded = false;

            $scope.TestDataFormData = [{}, {}, {}, {}, {}, {}];


            /**
             * Resets Test form
             */
            $scope.resetTestForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.TestForm = null;
                $scope.TestDataFormData = [{}, {}, {}, {}, {}, {}];
            };

            $scope.uploadBbiFile = function(testId) {
                var modalInstance =  $modal.open({
                    animation: true,
                    templateUrl: 'resources/app/calibrator/views/modals/upload-bbiFile.html',
                    controller: 'UploadBbiFileController',
                    size: 'lg',
                    resolve: {
                        calibrationTest: function () {
                            return testId;
                        },
                        parseBbiFile: function() {
                            return $scope.parseBbiFile;
                        }
                    }
                });
            };



            $scope.parseBbiFile = function (data) {
                $scope.fileLoaded = true;
                //data.counterNumber = Number(data.counterNumber );
                $scope.TestForm = data;
                for (var i = 0; i < $scope.statusData.length; i++) {
                    if ($scope.statusData[i].id === data.status) {
                        $scope.setStatus($scope.statusData[i]);
                    }
                }
                var date = $scope.TestForm.testDate;
                $scope.TestForm.testDate = moment(date).utcOffset(0).format("DD.MM.YYYY HH:mm");
                $scope.TestForm.testPhoto = "data:image/png;base64," + $scope.TestForm.testPhoto;
                $scope.TestDataFormData = data.listTestData;
            };

            $scope.showEditMainPhotoModal = function (id) {
                console.log($scope.TestForm.signed);
                if(!$scope.TestForm.signed) {
                    var modalInstance = $modal.open({
                        animation: true,
                        windowClass: 'preview-protocol-image',
                        templateUrl: 'resources/app/calibrator/views/modals/edit-main-photo-modal.html',
                        controller: 'EditPhotoController',
                        size: 'md',
                        resolve: {
                            photoId: function () {
                                return id;
                            },
                            parentScope: function () {
                                return $scope;
                            }
                        }
                    });
                }
            };

            $scope.setMainPhoto = function (data) {
                $scope.TestForm.testPhoto = data;
            };

            function getCalibrationTests() {
                calibrationTestServiceCalibrator
                    .getCalibrationTests()
                    .then(function (data) {
                        $scope.calibrationTests = data.calibrationTests;
                    })
            }

            function getProtocolTest(verificationID) {
                calibrationTestServiceCalibrator
                    .getTestProtocol(verificationID)
                    .then(function (data){
                        $scope.parseBbiFile(data);
                        $log.debug("inside");
                    } );
            }

            $scope.selectedStatus = {
                label: null
            }
            $scope.statusData = [
                {id: 'TEST_OK', label: null},
                {id: 'TEST_NOK', label: null}
            ];

            $scope.setStatus = function (status) {
                $scope.selectedStatus = {
                    label: status.label,
                    id: status.id
                };
            };

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    $scope.statusData[0].label = 'придатний';
                    $scope.statusData[1].label = 'не придатний';
                } else if (lang === 'eng') {
                    $scope.statusData[0].label = 'Tested OK';
                    $scope.statusData[1].label = 'Tested NOK';
                }
            };


            $scope.setTypeDataLanguage();

            //TODO check situation when there not Protocol
            if ( $scope.hasProtocol){
                getProtocolTest($scope.testId);
            }else{
                getCalibrationTests();
            }

            function retranslater() {
                protocol = {
                    fileName: $scope.TestForm.fileName,
                    accumulatedVolume: $scope.TestForm.accumulatedVolume,
                    counterNumber: $scope.TestForm.counterNumber,
                    temperature: $scope.TestForm.temperature,
                    installmentNumber: $scope.TestForm.installmentNumber,
                    latitude: $scope.TestForm.latitude,
                    longitude: $scope.TestForm.longitude,
                    testResult: $scope.TestForm.testResult,
                    status: $scope.getStatus($scope.selectedStatus),
                    listTestData: $scope.TestForm.listTestData,
                    counterProductionYear: $scope.TestForm.counterProductionYear,
                    counterTypeId: $scope.TestForm.counterTypeId
                }
            };

            $scope.getStatus = function (statusNow){
                var statusSend;
                if(statusNow.id == undefined){
                    statusSend = $scope.TestForm.status;
                }else{
                    statusSend = $scope.selectedStatus.id;
                }
                return statusSend;
            };




            $scope.closeForm = function(){
                window.history.back();
            }

            $scope.signCalibrationTest = function () {
                retranslater();
                calibrationTestServiceCalibrator
                    .editTestProtocol(protocol, $scope.testId)
                    .then(function (status) {
                        if (status == 200) {
                            calibrationTestServiceCalibrator
                                .signTestProtocol($scope.testId)
                                .then(function (status) {
                                    if (status == 200) {
                                        $scope.TestForm.signed = true;
                                        toaster.pop('success', $filter('translate')('INFORMATION'), $filter('translate')('SUCCESS_SIGNED'));
                                    }
                                })
                        }
                    })
            }

            /**
             * update test from the form in database.
             */
                /*$scope.generalForms={testForm:$scope.TestForm, smallForm: $scope.TestDataFormData};
                  $log.debug($scope.generalForms);
                .updateCalibrationTest($scope.TestForm, $scope.testId)*/

            $scope.updateCalibrationTest = function () {
                        retranslater();
                        calibrationTestServiceCalibrator
                            .editTestProtocol(protocol,$scope.testId)
                            .then(function (status) {
                                if (status == 201) {
                                    $rootScope.onTableHandling();
                                }
                                if(status==200){
                                    $modal.open({
                                        animation: true,
                                        templateUrl: 'resources/app/calibrator/views/modals/calibration-test-edited-success.html',
                                        controller: function ($modalInstance) {
                                            this.ok = function () {
                                                $modalInstance.close();
                                                if($scope.hasProtocol){
                                                    window.history.back();
                                                }else{
                                                    window.history.go(-2);
                                                }
                                            }
                                        },
                                        controllerAs: 'successController',
                                        size: 'md'
                                    });

                                }
                            });
            }

        }]);