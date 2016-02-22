angular.module('employeeModule')
    .controller('DisassemblyTeamEditModalControllerCalibrator',
    ['$rootScope', '$scope', '$modalInstance', 'DisassemblyTeamServiceCalibrator', '$log', '$filter', 'toaster', 'team', '$translate',
        function ($rootScope, $scope, $modalInstance, DisassemblyTeamServiceCalibrator, $log, $filter, toaster, team, $translate) {

            $scope.team = team;

            $scope.team.effectiveTo = {
                endDate: ($scope.team.effectiveTo)
            };

            for (var i = 0; i < team.specialization.length; i++) {
                $scope.team.specialization[i] = {
                    type: $scope.team.specialization[i],
                    label: $filter('translate')(team.specialization[i])
                };
            }

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });

            /**
             * init ui-select
             */
            $scope.deviceTypeData = [
                {
                    type: 'WATER',
                    label: $filter('translate')('WATER')
                },
                {
                    type: 'THERMAL',
                    label: $filter('translate')('THERMAL')
                }
            ];

            /**
             *  Date picker and formatter setup
             *
             */

            $scope.initDatePicker = function () {

                $scope.setTypeDataLangDatePicker = function () {

                    $scope.opts = {
                        format: 'DD-MM-YYYY',
                        singleDatePicker: true,
                        showDropdowns: true,
                        minDate: new Date(),
                        eventHandlers: {}
                    };

                };

                $scope.setTypeDataLangDatePicker();
            };

            $scope.showPicker = function () {
                angular.element("#datepickerfieldSingle").trigger("click");
            };

            $scope.initDatePicker();

            $scope.setTypeDataLanguage = function () {
                var lang = $translate.use();
                if (lang === 'ukr') {
                    moment.locale('uk'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                } else {
                    moment.locale('en'); //setting locale for momentjs library (to get monday as first day of the week in ranges)
                }
            };

            $scope.setTypeDataLanguage();

            // Disable weekend selection
            $scope.disabled = function (date, mode) {
                return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
            };

            $scope.toggleMin = function () {
                $scope.minDate = $scope.minDate ? null : new Date();
            };

            $scope.toggleMin();
            $scope.maxDate = new Date(2100, 5, 22);


            $scope.clearDate1 = function () {
                $log.debug($scope.team.effectiveTo);
                $scope.team.effectiveTo = null;
            };

            $scope.clearDate2 = function () {
                $log.debug($scope.team.dateOfVerif);
                $scope.team.dateOfVerif = null;
            };

            $scope.clearDate3 = function () {
                $log.debug($scope.team.noWaterToDate);
                $scope.team.noWaterToDate = null;
            };

            /**
             * Resets Team form
             */
            $scope.resetTeamForm = function () {
                $scope.$broadcast('show-errors-reset');
                if ($scope.teamForm) {
                    $scope.teamForm.$setPristine();
                    $scope.teamForm.$setUntouched();
                }
                $scope.teamNumber = null;
                $scope.team = null;
            };

            /**
             * Edit team. If everything is ok then
             * resets the team form and closes modal
             * window.
             */


            $scope.editDisassemblyTeam = function () {
                var specializations = [];
                for (var i = 0; i < $scope.team.specialization.length; i++) {
                    specializations[i] = $scope.team.specialization[i].type;
                }
                $scope.teamFormData = {
                    teamNumber: $scope.team.teamNumber,
                    teamName: $scope.team.teamName,
                    effectiveTo: $scope.team.effectiveTo.endDate,
                    specialization: specializations,
                    leaderFullName: $scope.team.leaderFullName,
                    leaderPhone: $scope.team.leaderPhone,
                    leaderEmail: $scope.team.leaderEmail
                };
                DisassemblyTeamServiceCalibrator.editDisassemblyTeam(
                    $scope.teamFormData,
                    $rootScope.teamId).then(
                    function (data) {
                        if (data == 200) {
                            $scope.closeModal();
                            $scope.resetTeamForm();
                            $rootScope.onTableHandling();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_EDIT_TEAM'));
                        }
                    });
            };

            /**
             * Closes edit modal window.
             */
            $scope.closeModal = function () {
                $modalInstance.close();
            };

            $scope.TEAM_USERNAME_REGEX = /[a-z0-9_-]{3,16}/;
            $scope.TEAM_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.TEAM_LEADER_FULL_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;

        }]);

