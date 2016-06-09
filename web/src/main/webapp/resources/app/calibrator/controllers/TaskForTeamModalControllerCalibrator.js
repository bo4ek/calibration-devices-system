angular
    .module('employeeModule')
    .controller('TaskForTeamModalControllerCalibrator', ['$rootScope', '$scope', '$modal', '$modalInstance', 'VerificationPlanningTaskService', '$log',
        function ($rootScope, $scope, $modal, $modalInstance, verificationPlanningTaskService, $log) {

            $scope.calibrationTask = {};
            $scope.incorrectValue = false;


            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * Closes edit modal window.
             */
            $scope.closeModal = function () {
                console.log("close modal window");
                $modalInstance.close();
            };


            /**
             *  Date picker and formatter setup
             *
             */
            $scope.firstCalendar = {};
            $scope.firstCalendar.isOpen = false;

            /**
             * open first date picker
             * on the modal
             *
             * @param $event
             */
            $scope.open1 = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.firstCalendar.isOpen = true;
            };

            /**
             * set date pickers options
             * @type {{formatYear: string, startingDay: number, showWeeks: string}}
             */
            $scope.dateOptions = {
                formatYear: 'yyyy',
                startingDay: 1,
                showWeeks: 'false'

            };

            /**
             * set format of date picker date
             */
            $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
            $scope.format = $scope.formats[2];


            $scope.toggleMin = function () {
                $scope.minDate = $scope.minDate ? null : new Date();
            };

            $scope.toggleMin();
            $scope.maxDate = new Date(2100, 5, 22);


            $scope.clearDate1 = function () {
                $log.debug($scope.calibrationTask.taskDate);
                $scope.calibrationTask.taskDate = null;
            };

            /**
             * reset task form
             */
            $scope.resetTaskForm = function () {
                $scope.$broadcast('show-errors-reset');
                $scope.calibrationTask = {};
                $scope.incorrectValue = false;
                $scope.calibrationTask.pickerDate = null;
                $scope.installationNumberValidation = null;
                $scope.showSendingMessage = false;
                $scope.teams = {};
            };


            /**
             * create team array from response data
             * @type {Array}
             */
            $scope.teams = [];
            function createTeamArray(data) {
                for (var i = 0; i < data.length; i++) {
                    $scope.teams[i] = {
                        teamName: data[i].teamName,
                        teamNumber: data[i].teamNumber
                    }
                }
            }

            /**
             * make asynchronous request to the server
             * and receive the teams info
             */
            $scope.receiveTeams = function () {
                console.log($scope.calibrationTask.taskDate + " " + $scope.calibrationTask.applicationFiled);
                var taskDate = $scope.calibrationTask.taskDate;
                var applicationFiled = $scope.calibrationTask.applicationFiled;
                verificationPlanningTaskService.getTeams(taskDate, applicationFiled)
                    .then(function (result) {
                        createTeamArray(result.data);
                    });
            }

            $scope.showSendingMessage = false;

            /**
             * send the task for team data
             * to the server to be saved in the database
             * if response status 200 opens success modal,
             * else open error modal
             */
            $scope.save = function () {
                if ($rootScope.emptyStatus == true) {
                    $scope.showSendingMessage = true;
                } else {
                    var calibrationTask = {
                        "dateOfTask": $scope.calibrationTask.taskDate,
                        "moduleNumber": $scope.calibrationTask.mountingCrew.teamNumber,
                        "verificationsId": $rootScope.verifIds
                    };
                    console.log(calibrationTask);
                    verificationPlanningTaskService.saveTaskForTeam(calibrationTask).
                        then(function (data) {
                            if (data.status == 200) {
                                $scope.closeModal();
                                $rootScope.verifIds = [];
                                $modal.open({
                                    animation: true,
                                    templateUrl: 'resources/app/calibrator/views/modals/task-add-success.html',
                                    controllerAs: 'successController',
                                    size: 'md'
                                });
                            } else if (data.status == 409) {
                                $scope.incorrectValue = true;
                                console.log($scope.incorrectValue);
                                $scope.closeModal();
                                $modal.open({
                                    animation: true,
                                    templateUrl: 'resources/app/calibrator/views/modals/task-adding-error.html',
                                    size: 'md'
                                });
                            }
                        });
                }
            }
        }]);

