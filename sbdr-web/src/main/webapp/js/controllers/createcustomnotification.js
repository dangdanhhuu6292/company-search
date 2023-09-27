appcontrollers.controller('CreateCustomNotificationController', ['$window', '$scope', '$rootScope', '$routeParams', '$location', '$filter', 'maxFieldLengths', 'CompanyService', 'NotificationsToSend', function($window, $scope, $rootScope, $routeParams, $location, $filter, maxFieldLengths, CompanyService, NotificationsToSend) {
	console.log("notificationsSendController");

	$scope.maxFieldLengths = maxFieldLengths;
	
	var searchurl = $routeParams.searchurl;
	var companyidentifier = $routeParams.bedrijfId;
	var owncompanyidentifier = $routeParams.eigenBedrijfId;

	//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
	$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

	$scope.isInvalid = function(targetForm, targetField) {
		var result_invalid = targetForm[targetField].$invalid;
		var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

		//if(targetForm[targetField].$dirty) {
		//	delete $scope.error;
		//}

		if(targetField == 'incorrectGegeven' || targetField == 'faillissementVraag') {

			var anyReasonsAreDirty = targetForm['incorrectGegeven'].$dirty || targetForm['faillissementVraag'].$dirty || $scope.formSubmitted;

			if(anyReasonsAreDirty && $scope.melding.incorrectGegeven == false && $scope.melding.faillissementVraag == false) {
				return true;
			}
		}

		return result_dirty && result_invalid;
	};

	$scope.send = function() {

		console.log('save function');
		$scope.formSubmitted = true;

		if(!($scope.formcreatecustomnotification.$invalid || $scope.buttonDisabled())) {
			delete $scope.formSubmitted;

			delete $scope.error;
			delete $scope.meldingSavedOk;
			delete $scope.vermeldingResultaat;

			var customMelding = {
				bedrijfId             : companyidentifier,
				bedrijfIdGerapporteerd: owncompanyidentifier,
				incorrectGegeven      : $scope.melding.incorrectGegeven,
				meldingDetails        : $scope.melding.meldingDetails,
				faillissementVraag    : $scope.melding.faillissementVraag,
				voornaam              : $scope.melding.naam,
				achternaam            : $scope.melding.achternaam,
				telefoonnummer        : $scope.melding.telefoonnummer,
				afdeling              : $scope.melding.afdeling,
				wachtwoord            : $scope.melding.wachtwoord,
				meldingen             : NotificationsToSend.getNotifications()
			};
			CompanyService.createCustomMelding(customMelding, function(response) {
				if(typeof response.errorCode !== 'undefined') {
					$scope.error = response.errorMsg;
				}
				else {
					$scope.meldingSavedOk = true;
				}
			});
		}
	};

	$scope.buttonDisabled = function() {
		return !!((!$scope.melding.incorrectGegeven && !$scope.melding.faillissementVraag) ||
		($scope.melding.meldingDetails == null || $scope.melding.meldingDetails == ''));
	};

	$scope.back = function() {
		url = gotoDurl(searchurl);
		if(url != null) {
			$location.path(url);
			$location.url($location.path());
		}
	};

	if(typeof $scope.init === "undefined") {
		$scope.melding = {
			incorrectGegeven     : false,
			faillissementVraag   : false,
			meldingDetails       : '',
			voornaam             : JSON.parse($window.sessionStorage.user).firstName,
			achternaam           : JSON.parse($window.sessionStorage.user).lastName,
			telefoonnummer       : JSON.parse($window.sessionStorage.user).gebruikerTelefoonNummer,
			afdeling             : null
		};
		$scope.formSubmitted = false;

		$scope.init = true;
	}
}]);