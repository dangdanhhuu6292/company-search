appcontrollers.controller('SupportProblemController',
	['$window', '$scope', '$rootScope', '$location', 'maxFieldLengths', 'SupportService',
		function($window, $scope, $rootScope, $location, maxFieldLengths, SupportService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			if(typeof $scope.init == 'undefined') {
				$scope.init = true;

				$scope.supporttypes = [
					{id: 'KLT', description: 'Klacht'},
					{id: 'PRB', description: 'Probleem'},
					{id: 'VRG', description: 'Vraag'},
					{id: 'SGT', description: 'Suggestie'}
				];

				$scope.supportredenen = [
					{id: 'PAN', description: 'Overig'},
					{id: 'PBA', description: 'Kan geen melding doen van betalingsachterstand'},
					{id: 'PBD', description: 'Kan geen bedrijf opzoeken'},
					{id: 'PBM', description: 'Kan geen monitor op bedrijf plaatsen'},
					{id: 'PGB', description: 'Kan geen gebruiker toevoegen'}
				];

				$scope.support = {
					supportType : null,
					supportReden: null,
					bericht     : null,
					gebruiker   : {gebruikersId: JSON.parse($window.sessionStorage.user).userId}
				};
			}

			$scope.$watch('support.supportType', function(newType) {
				if(newType == 'KLT' || newType == 'VRG') {
					$scope.support.supportReden = null;
				}
			});

			$scope.gotoSupport = function() {
				$location.path('/support');
				$location.url($location.path());
			};

			$scope.submitSupport = function() {
				$scope.formSubmitted = true;

				delete $scope.supportSavedOk;

				if(!$scope.checkSupportFormInvalid()) {
					$scope.formsupportkpv.$setPristine();
					SupportService.createsupportticket($scope.support, function(response) {
						if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = response.errorCode + ' ' + response.errorMsg;
						} else {
							$scope.supportSavedOk = true;
							$scope.supportReferentie = response.referentieNummer
						}
					});
					$scope.formSubmitted = false;
				}
			};

			$scope.isInvalid = function(targetForm, targetField) {
				//als er een fout is, moet er true teruggegeven worden
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(result_dirty) {
					delete $scope.error;
				}

				if(targetField == 'supportreden') {
					if(($scope.support.supportType == 'PRB' && $scope.support.supportReden == null) ||
						($scope.support.supportType != 'PRB' && $scope.support.supportReden != null)) {
						return true;
					}
				}

				return result_dirty && result_invalid;
			}
			;

			$scope.checkSupportFormInvalid = function() {
				//als er een fout is, moet er true teruggegeven worden
				return $scope.formsupportkpv.$invalid || $scope.isInvalid($scope.formsupportkpv, 'supportreden');
			}
		}
	])
;