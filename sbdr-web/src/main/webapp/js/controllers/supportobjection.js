appcontrollers.controller('SupportObjectionController',
	['$window', '$scope', '$rootScope', '$location', '$anchorScroll', 'maxFieldLengths',
		'$routeParams', '$q', 'SupportService', 'CompanyService',
		function($window, $scope, $rootScope, $location, $anchorScroll, maxFieldLengths,
				 $routeParams, $q, SupportService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			if($scope.init == undefined) {
				$scope.init = true;

				$scope.supportredenen = [
					{id: 'BBV', description: 'Betwist de vordering'},
					{id: 'BFO', description: 'Factuur is ongegrond'},
					{id: 'BOO', description: 'Organisatie is onbekend'},
					{id: 'BRG', description: 'Regeling is getroffen'},
					{id: 'BVV', description: 'Vordering is voldaan'},
					{id: 'BAN', description: 'Overig'}
				];

				$scope.support = {
					supportType : 'BZW',
					supportReden: null,
					bericht     : null,
					melding     : {meldingId: null},
					gebruiker   : {gebruikersId: JSON.parse($window.sessionStorage.user).userId}
				};

				$scope.attachments = [];

				$scope.companymeldingen = [];

				$scope.selectedCompany = {};
				$scope.selectedNotification = {};

				$scope.meldingBedrijfNaam = '-';
				$scope.meldingReferentieIntern = '-';

				$scope.controle = {
					akkoord: false
				};
			}

			$scope.$watch('createFileAttachment', function(newFile, oldFile) {
				delete $scope.attachmenterror;
				if(newFile != undefined) {
					if(newFile.type == 'application/gzip'
						|| newFile.type == 'application/msword'
						|| newFile.type == 'application/pdf'
						|| newFile.type == 'application/vnd.ms-excel'
						|| newFile.type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
						|| newFile.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
						|| newFile.type == 'application/zip'
						|| newFile.type == 'text/csv'
						|| newFile.type == 'text/plain'
						|| newFile.type == 'text/rtf') {
						if(newFile != undefined && $scope.attachments != undefined && $scope.attachments.length <= 2) {
							var fileExists = false;

							$scope.attachments.some(function(elem, index, array) {
								if(elem.name == newFile.name) {
									fileExists = true;
								}
							});

							if(!fileExists) {
								$scope.attachments.push(newFile);
							}
						}
					} else {
						$scope.attachmenterror = true;
						$scope.error = 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv';
					}
				}
			});

			$scope.checkSupportFormInvalid = function() {
				//als er een fout is, moet er true teruggegeven worden
				return $scope.formsupportobjection.$invalid || $scope.controle.akkoord == false;
			};

			$scope.fetchMeldingen = function() {
				$scope.args = {gebruikerId: JSON.parse($window.sessionStorage.user).userId};
				CompanyService.getNotificationsOfCompany($scope.args, function(response) {
					if(response.errorCode != undefined) {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					} else {
						$scope.companymeldingen = response;
					}
				});
			};

			$scope.gotoSupport = function() {
				$location.path('/support');
				$location.url($location.path());
			};

			$scope.isInvalid = function(targetForm, targetField) {
				//als er een fout is, moet er true teruggegeven worden
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(result_dirty && (typeof $scope.attachmenterror == 'undefined' || !$scope.attachmenterror)) {
					delete $scope.error;
				}

				return result_dirty && result_invalid;
			};

			$scope.removeAttachment = function(index) {
				$scope.attachments.splice(index, 1);
			};

			$scope.submitSupport = function() {
				$scope.formSubmitted = true;

				delete $scope.supportSavedOk;

				if(!$scope.checkSupportFormInvalid()) {
					$scope.formsupportobjection.$setPristine();

					SupportService.createsupportticket($scope.support, function(newSupport) {
						if(newSupport.errorCode != undefined) {
							$scope.error = newSupport.errorCode + ' ' + newSupport.errorMsg;
						} else {
							var fileResults = [];
							var continueLoop = true;

							async.each($scope.attachments, function(attachment, callback) {
								if(continueLoop) {
									var formData = createFormData(attachment, newSupport.supportId, newSupport.referentieNummer);

									SupportService.createsupportticketbestand(formData, function(response) {
										if(response.errorCode != undefined) {
											continueLoop = false;
											callback(response.errorCode + ' ' + response.errorMsg);
										} else {
											fileResults.push(response);
											callback();
										}
									})
								}
							}, function(result) {
								if(result) {
									$scope.error = result;
								} else {
									$scope.supportSavedOk = true;
									$scope.supportReferentie = newSupport.referentieNummer;

									if(fileResults.length == 1) {
										$scope.supportBijlagenMsg = ' met 1 bijlage';
									} else if(fileResults.length > 1) {
										$scope.supportBijlagenMsg = ' met ' + fileResults.length + ' bijlagen';
									}
								}
							});
						}
					});

					$scope.formSubmitted = false;
				}
			};

			createFormData = function(newFile, supportId, supportRef) {
				var fd = new FormData();
				fd.append('file', newFile);
				fd.append('BestandsNaam', newFile.name);
				fd.append('SupportId', supportId);
				fd.append('SupportReferentie', supportRef);

				return fd;
			};

			meldingChanged = function(index) {
				if (index > 0) {
					CompanyService.companyData({bedrijfId: $scope.companymeldingen[index-1].bedrijfIdGerapporteerd}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							$scope.error = result.errorCode + " " + result.errorMsg;
						} else {
							$scope.selectedCompany = result;

							CompanyService.companyNotificationData({
								meldingId: $scope.companymeldingen[index].meldingId,
								bedrijfId: $scope.companymeldingen[index].bedrijfId
							}, function(notificationResult) {
								if(typeof result.errorCode != 'undefined') {
									$scope.error = notificationResult.errorCode + " " + notificationResult.errorMsg;
								} else {
									$scope.selectedNotification = notificationResult;
								}
							});
						}
					});
				} else {
					$scope.selectedCompany = {};
					$scope.selectedNotification = {};
				}
			};

			$scope.fetchMeldingen();
		}]);