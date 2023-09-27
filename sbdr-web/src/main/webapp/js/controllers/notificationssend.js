appcontrollers.controller('NotificationsSendController',
	['$window', '$scope', '$rootScope', '$routeParams', 'maxFieldLengths',
		'$location', '$filter', 'CompanyService', 'NotificationsToSend',
		function($window, $scope, $rootScope, $routeParams, maxFieldLengths,
				 $location, $filter, CompanyService, NotificationsToSend) {
			console.log("notificationsSendController");

			$scope.maxFieldLengths = maxFieldLengths;
			
			var searchurl = $routeParams.searchurl;
			var companyidentifier = $routeParams.bedrijfId;
			var owncompanyidentifier = $routeParams.eigenBedrijfId;

			//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
			$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || wasFormSubmitted;

				//if(targetForm[targetField].$dirty) {
				//	delete $scope.error;
				//}

				return result_invalid && result_dirty;
			};

			// Controls the 'send' button's active state based on the validity of the form
			$scope.buttonDisabled = function (targetForm, targetField) {
				// if form is invalid set submit to false, so that send can continue processing on resend
				if (targetForm.$invalid && $scope.formSubmitted) {
					wasFormSubmitted = $scope.formSubmitted;
					$scope.formSubmitted = false;
				}
				
				return targetForm.$invalid && wasFormSubmitted;
			};
			
			$scope.send = function() {
				// if already in submit, return
				if ($scope.formSubmitted == true)
					return;
				
				$scope.formSubmitted = true;

				if(!($scope.formnotificationssend.$invalid)) {					

					delete $scope.error;
					delete $scope.meldingenSavedOk;
					delete $scope.vermeldingResultaat;
					console.log('save function');

					if (typeof NotificationsToSend.getNotifications() !== 'undefined') {
						for(var m in NotificationsToSend.getNotifications()) {
							NotificationsToSend.getNotifications()[m].meldingId = !isNaN(parseFloat(NotificationsToSend.getNotifications()[m].meldingId)) && isFinite(NotificationsToSend.getNotifications()[m].meldingId) ? null : NotificationsToSend.getNotifications()[m].meldingId;
							if (typeof NotificationsToSend.getTelefoonNummerDebiteur() !== 'undefined' && NotificationsToSend.getTelefoonNummerDebiteur() != null)
								NotificationsToSend.getNotifications()[m].telefoonNummerDebiteur = NotificationsToSend.getTelefoonNummerDebiteur();
							if (typeof NotificationsToSend.getEmailAdresDebiteur() !== 'undefined' && NotificationsToSend.getEmailAdresDebiteur() != null)
							NotificationsToSend.getNotifications()[m].emailAdresDebiteur = NotificationsToSend.getEmailAdresDebiteur();
						}
					}
					
					var batch = {
						voornaam      : $scope.akkoord.naam,
						achternaam    : $scope.akkoord.achternaam,
						telefoonnummer: $scope.akkoord.telefoonnummer,
						afdeling      : JSON.parse($window.sessionStorage.user).afdeling,
						wachtwoord    : $scope.akkoord.wachtwoord,
						bedrijfIdDoor : owncompanyidentifier,
						bedrijfIdOver : companyidentifier,
						meldingen     : NotificationsToSend.getNotifications()
					};
					

					CompanyService.notifyCompanyBatch(batch, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							$scope.error = response.errorMsg;
							delete $scope.formSubmitted;
						} else {
							NotificationsToSend.setNotifications([]);
							$scope.vermeldingResultaat = response.result; //'Uw vermelding is ontvangen en wordt door ons behandeld.'; //response;
							$scope.meldingenSavedOk = true;
						}
					});
				} //else
				//	delete $scope.formSubmitted;
				
			};

			$scope.back = function() {
				$location.path('/notifycompany/' + owncompanyidentifier + '/' + companyidentifier + '/' + searchurl);
				$location.url($location.path());
			};

			$scope.sendok = function() {
				url = gotoDurl('$dashboard$companiesnotifiedtab');
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			if(typeof $scope.init === "undefined") {
				var user = JSON.parse($window.sessionStorage.user);
				var telefoonNr = null;
				if(typeof user.gebruikerTelefoonNummer != 'undefined') {
					telefoonNr = user.gebruikerTelefoonNummer;
				} else {
					telefoonNr = user.bedrijfTelefoonNummer;
				}
				
				$scope.aantalVermeldingen = NotificationsToSend.getNotifications().length;
				delete $scope.vermeldingenTotaalbedrag;
				
				CompanyService.getTariefOfProduct({ProductCode: 'VER'}, function(response) {
					if(typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorMsg;
					} else {
						vermeldingTarief = response.tarief;
						$scope.vermeldingenTotaalbedrag = $filter('currency')($scope.aantalVermeldingen * vermeldingTarief);
					}					
				});

				$scope.akkoord = {
					voornaam      : user.firstName,
					achternaam    : user.lastName,
					telefoonnummer: telefoonNr,
					afdeling      : user.afdeling
				};

				var wasFormSubmitted = false;
				$scope.formSubmitted = false;
				
				$scope.init = true;
			}
		}]);