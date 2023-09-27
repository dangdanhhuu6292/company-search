appcontrollers.controller('SupportDetailController',
	['$window', '$routeParams', '$scope', '$rootScope', '$location', 'supportTicketsDetails',
		'SupportService', 'CompanyService', 'backToAlerts',
		function($window, $routeParams, $scope, $rootScope, $location, supportTicketDetails,
				 SupportService, CompanyService, backToAlerts) {

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}
		
			if(typeof $scope.init == 'undefined') {

				$scope.supportredenen = [
					{id: 'BBV', description: 'Betwist de vordering'},
					{id: 'BFO', description: 'Factuur is ongegrond'},
					{id: 'BOO', description: 'Organisatie is onbekend'},
					{id: 'BRG', description: 'Regeling is getroffen'},
					{id: 'BAN', description: 'Overig'}
				];

				if(supportTicketDetails.supportTicketChain[0].errorCode != null) {
					$scope.showDetailsNotAllowed = true;
					$scope.topError = supportTicketDetails.supportTicketChain[0].errorCode + ' ' + supportTicketDetails.supportTicketChain[0].errorMsg;
				} else {
					$scope.showDetailsNotAllowed = false;
					$scope.supportchain = supportTicketDetails.supportTicketChain;
				}

				if(!$scope.showDetailsNotAllowed) {
					$scope.reaction = {
						betwistBezwaar : null,
						supportReden   : null,
						bericht        : null,
						gebruiker      : {gebruikersId: JSON.parse($window.sessionStorage.user).userId},
						supportParentId: $scope.supportchain[$scope.supportchain.length - 1].supportId
					};

					$scope.attachments = [];

					$scope.supportTicket = $scope.supportchain[0];

					$scope.thisUser = JSON.parse($window.sessionStorage.user);

					if($scope.supportTicket.melding) {
						$scope.meldingVanBedrijf = CompanyService.companyData({bedrijfId: $scope.supportTicket.melding.bedrijfIdGerapporteerd});
					}

					$scope.supportSavedOk = false;

					$scope.obj = {};
				}

				$scope.init = true;
			}

			$scope.$on('attachmentControllerEvent', function(event, data) {
				$scope.attachmenterror = true;
				$scope.error = data;
			});

			$scope.downloadAttachment = function(sBId) {
				return {action: 'support_attachment', supportBestandId: sBId};
			};

			$scope.checkObjectionReactionFormInvalid = function(thisForm) {
				return thisForm.$invalid || $scope.isInvalid(thisForm, 'rbobjection');
			};

			//$scope.fetchAttachments = function(supportTicket){
			//    return SupportService.findsupportticketbestandenbysupportid({supportId: supportTicket.supportId});
			//};

			$scope.getStaticSuccessMsg = function() {
				if($scope.supportTicket.supportStatus == 'ARC') {
					if($scope.supportTicket.supportType == 'BZW') {
						if(typeof $scope.supportTicket.melding != 'undefined') {
							if($scope.supportTicket.melding.meldingstatusCode == 'ACT') {
								return 'Bezwaar is afgewezen, melding is opgenomen';
							} else if($scope.supportTicket.melding.meldingstatusCode == 'AFW' || $scope.supportTicket.melding.meldingstatusCode == 'DEL') {
								return 'Bezwaar is geaccepteerd of kwestie is gearchiveerd, vermelding is verwijderd';
							} else if($scope.supportTicket.melding.meldingstatusCode == 'INB'){
								return 'Bezwaar is afgewezen. De vermelding blijft opgenomen in het register.';
							}
						}
					} else {
						return 'Support ticket is reeds afgehandeld';
					}
				} else if($scope.supportTicket.supportStatus == 'GST'&&!$scope.hasAdminRole($scope.thisUser.roles)){
					return 'Het bezwaar is in behandeling bij de afdeling bezwaar. U wordt op de hoogte gehouden van het verloop en indien nodig gevraagd uw kant van het verhaal te belichten.';
				}
			};

			$scope.gotoSupport = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
				
//				if(backToAlerts == 'true') {
//					$location.path('/dashboard');
//					$location.url($location.path());
//				} else {
//					$location.path('/support');
//					$location.url($location.path());
//				}
			};

			$scope.handleObjectionSbdrReaction = function(discardObjection) {
				if(!this.formobjectionsbdrreaction.$invalid) {
					this.formobjectionsbdrreaction.$setPristine();

					//Set the correct supportType
					$scope.reaction.supportType = 'BZW';

					if(discardObjection) {
						SupportService.sbdrdiscardobjection($scope.reaction, function(response) {
							if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
								$scope.error = response.errorCode + ' ' + response.errorMsg;
							} else {
								$scope.supportSavedOk = true;
								$scope.successMsg = 'Bezwaar is afgewezen, melding wordt opgenomen';

								SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
									$scope.supportchain = newChain;

									$scope.supportTicket = $scope.supportchain[0];
								});
							}
						});
					} else {
						SupportService.sbdracceptobjection($scope.reaction, function(response) {
							if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
								$scope.error = response.errorCode + ' ' + response.errorMsg;
							} else {
								$scope.supportSavedOk = true;
								$scope.successMsg = 'Bezwaar is geaccepteerd, melding wordt verwijderd';

								SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
									$scope.supportchain = newChain;

									$scope.supportTicket = $scope.supportchain[0];
								});
							}
						});
					}
				}
			};

			$scope.hasAdminRole = function(roles) {
				return hasRole(roles, 'SBDR') || hasRole(roles, 'SBDRHOOFD') || hasRole(roles, 'admin_sbdr_hoofd') || hasRole(roles, 'admin_sbdr');
			};

			$scope.isInvalid = function(targetForm, targetField) {
				if(targetForm[targetField] != undefined) {
					var result_invalid = targetForm[targetField].$invalid;
					var result_dirty = targetForm[targetField].$dirty;

					if(result_dirty  && (typeof $scope.attachmenterror == 'undefined' || !$scope.attachmenterror)) {
						delete $scope.error;
						delete $scope.successMsg;
					}

					if(targetField == 'rbobjection') {
						result_invalid = targetForm['rbobjection'].$modelValue != 'true' && targetForm['rbobjection'].$modelValue != 'false';
					}

					return result_dirty && result_invalid;
				}
				else {
					return true;
				}
			};

			$scope.pickUpSupportTicket = function() {
				var supportInfo = {
					refNo : $scope.supportTicket.referentieNummer,
					userId: $scope.thisUser.userId
				};

				SupportService.pickupsupportticket(supportInfo, function(response) {
					if(typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					} else {
						$scope.supportTicketPickUpOK = true;

						SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
							$scope.supportchain = newChain;

							$scope.supportTicket = $scope.supportchain[0];
						});
					}
				});
			};

			$scope.showStaticSuccessMsg = function() {
				if($scope.supportTicket.supportStatus == 'ARC') {
					if($scope.supportTicket.supportType == 'BZW') {
						if(typeof $scope.supportTicket.melding != 'undefined') {
							if($scope.supportTicket.melding.meldingstatusCode == 'ACT' || $scope.supportTicket.melding.meldingstatusCode == 'INB') {
								return true;
							} else if($scope.supportTicket.melding.meldingstatusCode == 'AFW' || $scope.supportTicket.melding.meldingstatusCode == 'DEL') {
								return true;
							}
						}
					} else {
						return true;
					}
				} else if($scope.supportTicket.supportStatus == 'GST'&&!$scope.hasAdminRole($scope.thisUser.roles)){
					return true;
				}

				return false;
			};

			$scope.submitObjectionReaction = function() {
				if(!$scope.checkObjectionReactionFormInvalid(this.formobjectionreaction)) {
					this.formobjectionreaction.$setPristine();

					//BOTH sbdrdiscardobjection AND sbdracceptobjection can be called,
					//The boolean that is used in the webservice won't be used when a user discards an objection
					SupportService.createsupportticketreaction($scope.reaction, function(newSupport) {
						if(typeof newSupport.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = newSupport.errorCode + ' ' + newSupport.errorMsg;
						} else {
							var fileResults = [];
							var continueLoop = true;
							async.each($scope.attachments, function(attachment, callback) {
								if(continueLoop) {
									var formData = createFormData(attachment, newSupport.supportId, newSupport.referentieNummer);

									SupportService.createsupportticketbestand(formData, function(response) {
										if(typeof response.errorCode != 'undefined') {
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
									$scope.successMsg = 'Reactie op ticket met referentie ' + newSupport.referentieNummer + ' wordt behandeld';

									SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
										$scope.supportchain = newChain;

										$scope.supportTicket = $scope.supportchain[0];
									});
								}
							});
						}
					});
				}
			};

			$scope.submitSupportSbdrReaction = function() {
				delete $scope.successMsg;

				if(!this.formsupportsbdrreaction.$invalid) {
					this.formsupportsbdrreaction.$setPristine();
					SupportService.createsupportticket($scope.reaction, function(response) {
						if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = response.errorCode + ' ' + response.errorMsg;
						} else {
							$scope.supportSavedOk = true;
							$scope.successMsg = 'Reactie is met succes geplaatst';

							SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
								$scope.supportchain = newChain;

								$scope.supportTicket = $scope.supportchain[0];
							});
						}
					})
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
		}]);

appcontrollers.controller('SupporAttachmentController', ['$scope', function($scope) {
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
				if(newFile != undefined && typeof $scope.attachments !== 'undefined' && $scope.attachments.length <= 2) {
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
				$scope.$emit('attachmentControllerEvent', 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv');
				//$scope.error = 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv';
			}
		}
	});

	$scope.removeAttachment = function(index) {
		$scope.attachments.splice(index, 1);
	}
}]);