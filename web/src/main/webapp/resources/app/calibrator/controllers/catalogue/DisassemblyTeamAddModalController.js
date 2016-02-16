angular.module('employeeModule')
    .controller('DisassemblyTeamAddModalControllerCalibrator',
    ['$rootScope', '$scope', '$modalInstance', 'DisassemblyTeamServiceCalibrator', '$modal', '$filter', 'toaster', '$translate',
        function ($rootScope, $scope, $modalInstance, DisassemblyTeamServiceCalibrator, $modal, $filter, toaster, $translate) {

            /**
             * Closes modal window on browser's back/forward button click.
             */
            $rootScope.$on('$locationChangeStart', function () {
                $modalInstance.close();
            });


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
                $log.debug($scope.teamFormData.effectiveTo);
                $scope.teamFormData.effectiveTo = null;
            };

            $scope.clearDate2 = function () {
                $log.debug($scope.teamFormData.dateOfVerif);
                $scope.teamFormData.dateOfVerif = null;
            };

            $scope.clearDate3 = function () {
                $log.debug($scope.teamFormData.noWaterToDate);
                $scope.teamFormData.noWaterToDate = null;
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
                $scope.teamFormData = null;
            };

            $scope.resetTeamForm();

            function isDisassemblyTeamAvailable(teamUsername) {
                DisassemblyTeamServiceCalibrator.isDisassemblyTeamNameAvailable(teamUsername)
                    .then(function (data) {
                        validator('availableTeamLogin', data);
                        //validator('availableTeamLogin', true);
                    })
            }

            $scope.checkAll = function (caseForValidation) {
                switch (caseForValidation) {
                    case ('teamNumber'):
                        var teamNumber = $scope.teamFormData.teamNumber;
                        if (teamNumber == null) {
                        } else if ($scope.TEAM_USERNAME_REGEX.test(teamNumber)) {
                            isDisassemblyTeamAvailable(teamNumber);
                        } else {
                            validator('loginTeamValid', false);
                        }
                        break;
                }

            };

            function validator(caseForValidation, isValid) {
                switch (caseForValidation) {
                    case 'availableTeamLogin' :
                        $scope.teamNumberValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'Такий логін вже існує'
                        };
                        break;
                    case 'loginTeamValid' :
                        $scope.teamNumberValidation = {
                            isValid: isValid,
                            css: isValid ? 'has-success' : 'has-error',
                            message: isValid ? undefined : 'К-сть символів не повинна бути меншою за 3\n і більшою за 16 '
                        };
                        break;
                }
            }

            bValidation = function () {
                if ($scope.teamNumberValidation === undefined || $scope.teamNumberValidation.isValid === false) {
                    $scope.incorrectValue = true;
                    return false;
                } else {
                    return true;
                }
            };

            /**
             * Validates team form before saving
             */
            $scope.onTeamFormSubmit = function () {
                $scope.$broadcast('show-errors-check-validity');
                if (bValidation()) {
                    saveDisassemblyTeam();
                } else {
                    $scope.incorrectValue = true;
                }
            };


            /**
             * Saves new team from the form in database.
             * If everything is ok then resets the team
             * form and updates table with teams.
             */
            function saveDisassemblyTeam() {
                for (var i = 0; i < $scope.teamFormData.specialization.length; i++) {
                    $scope.teamFormData.specialization[i] = $scope.teamFormData.specialization[i].type;
                }
                $scope.teamFormData.effectiveTo = $scope.teamFormData.effectiveTo.endDate;
                DisassemblyTeamServiceCalibrator.saveDisassemblyTeam(
                    $scope.teamFormData).then(
                    function (data) {
                        if (data == 201) {
                            $scope.closeModal();
                            $scope.resetTeamForm();
                            $rootScope.onTableHandling();
                            toaster.pop('success', $filter('translate')('INFORMATION'),
                                $filter('translate')('SUCCESSFUL_ADD_TEAM'));
                        } else if (data == 409) {
                            $scope.closeModal();
                            $scope.resetTeamForm();
                            $rootScope.onTableHandling();
                            toaster.pop('error', $filter('translate')('INFORMATION'),
                                $filter('translate')('ERROR_ADD_TEAM'));
                        }
                    });
            }


            /**
             * Closes the modal window for adding new
             * team.
             */
            $rootScope.closeModal = function () {
                $modalInstance.close();
            };


            $scope.TEAM_USERNAME_REGEX = /[a-z0-9_-]{3,16}/;
            $scope.TEAM_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;
            $scope.TEAM_LEADER_FULL_NAME_REGEX = /([A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}\u002d{1}[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20}|[A-Z\u0410-\u042f\u0407\u0406\u0404']{1}[a-z\u0430-\u044f\u0456\u0457\u0454']{1,20})/;

        }]);
