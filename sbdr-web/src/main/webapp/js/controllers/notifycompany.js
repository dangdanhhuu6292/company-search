appcontrollers.controller('NotifyController',
	['$window', '$modal', '$scope', '$rootScope', '$routeParams', '$http', 'maxFieldLengths',
		'$location', '$filter', '$q', 'ngTableParams', 'ownCompanyData',
		'companyData', 'CompanyService', 'GebruikersServicePath', 'NotificationsToSend',
		function($window, $modal, $scope, $rootScope, $routeParams, $http, maxFieldLengths,
				 $location, $filter, $q, ngTableParams, ownCompanyData,
				 companyData, CompanyService, GebruikersServicePath, NotificationsToSend) {
			console.log("notifyController");
			
			//$scope.time = new Date(1970, 0, 1, 10, 30, 40);
			//  $scope.selectedTimeAsNumber = 10 * 36e5 + 30 * 6e4 + 40 * 1e3;
			//  $scope.selectedTimeAsString = '10:00';
			//  $scope.sharedDate = new Date(new Date().setMinutes(0, 0));			

			$scope.maxFieldLengths = maxFieldLengths;
			
			//tmhDynamicLocale.set('nl-nl');

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}
			orgsearchurl = searchurl;

			$scope.sliderValue = 8;

			$scope.htmlTooltip = 'Wanneer u deze optie geselecteerd houdt zullen uw bedrijfsgegevens worden weergeven binnen het register.';
			//\nUw melding zal dan openbaar zijn.\nWenst u liever anoniem een melding te verrichten?\nZorg er dan voor dat het u het vinkje weghaalt.\nDe anonimiteit strekt zich uit tot derden die niet bij uw vordering betrokken zijn.\nDe partij waarvan u melding maakt zal uiteraard wel op de hoogte gesteld worden van uw melding.';

			$scope.dosomething = function() {
				console.log("hello World");
			};

			// whole numbers only $scope.NUMBER_REGEXP = /^[0-9]+$/;
			$scope.NUMBERINTERNAL_REGEXP = /^(\d*\.\d{1,2}|\d+)$/; // number '.' sep
			$scope.dateFormat = 'ddMMyyyy';
			$scope.timeFormat = 'HHmm'

			$scope.minDateInvoice = new Date(2010, 1, 1);
			$scope.maxDateInvoice = new Date();

			var bedragHelpSpan = $("#bedraghelp");
			var datumfactuurHelpSpan = $("#datumfactuurhelp");
			var referenceHelpSpan = $("#referentiehelp");

			var companyidentifier = $routeParams.bedrijfId;
			var notificationidentifier = $routeParams.meldingId || undefined;
			var readonly = $routeParams.readonly || undefined;

			console.log('companyidentifier: ' + companyidentifier + ' readonly: ' + readonly);

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(targetForm[targetField].$dirty) {
					delete $scope.error;
					delete $scope.meldingVerwerkt;
					delete $scope.meldingSavedOk;
					delete $scope.contactMomentSavedOk;
					delete $scope.notitieSavedOk;
					delete $scope.meldingRemovedOk;
					// Save old hash (might be no hash), set location, scroll to it, and set old hash back(most common result: no hash in url)
					// $anchorScroll() works best when an id is provided
					//var old = $location.hash();
					//$location.hash(targetForm[targetField].$id);
					//$anchorScroll();
					//$location.hash(old);
				}

				if((targetField == 'akkoordBedrijf' || targetField == 'geenAkkoordBedrijf') && result_dirty) {
					result_invalid = !($scope.controle.bedrijfsgegevensok || $scope.controle.bedrijfsgegevensNietJuist);
				}

				//500
				//check
				//if ((targetField == 'bedrag' || targetField == 'vorderingHoogGenoeg') && result_dirty) {
				//	result_invalid = $scope.bedragAanwezig($scope.melding.bedrag) == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false);
				//	if (result_invalid) {
				//		if (targetField == 'bedrag')
				//			bedragHelpSpan.text('Indien er geen bedrag ingevuld wordt, hoger dan 500 Euro, dient bevestigd te worden dat het vorderingbedrag van de vermelding hoger is dan 500 Euro.');
				//	}
				//
				//}

				return result_dirty && result_invalid;

			};

			$scope.owncompany = ownCompanyData;
			//{
			//	name: "De Voorkant B.V.",
			//		address
			//:
			//	"Astrounaut 22d",
			//		zipcode
			//:
			//	"3824 MJ",
			//		city
			//:
			//	"Amersfoort",
			//		telephone
			//:
			//	"033-4557485",
			//		kvknumber
			//:
			//	"38024739"
			//}

			//$scope.company = CompanyCrudService.get({id: companyidentifier}, function() {
			//console.log("companycase name=" + companycase.name);

			//});
			$scope.company = companyData;
			//{
			//	name: "Wanbetaler B.V.",
			//		address
			//:
			//	"Croeselaan 32 UTRECHT",
			//		kvkNumber
			//:
			//	"00021345"
			//}

			if(typeof $scope.melding === 'undefined' && typeof notificationidentifier !== 'undefined') {
				console.log('edit existing melding');
				$scope.editExistingMelding = true;
				$scope.meldingDetails = true;
			}

			$scope.hideConfirm = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.hidePlaatsVermelding = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.isUnchanged = function(melding) {
				return angular.equals(melding, $scope.mastermelding);
			};
			
			$scope.isUnchangedAdminNote = function() {
				return (!$scope.gebruikerAllowed() || 
							(typeof $scope.masterMeldingNotitiesAdmin === 'undefined' && typeof $scope.meldingNotitiesAdmin === 'undefined') || 
							(typeof $scope.masterMeldingNotitiesAdmin !== 'undefined' && angular.equals($scope.masterMeldingNotitiesAdmin, $scope.meldingNotitiesAdmin)));
			};

			$scope.showAanpassenVermelding = function() {
				return !!($scope.editExistingMelding == true && typeof $scope.meldingVerwerkt === 'undefined');
			};

			$scope.hideAnnuleren = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.showTerugDetail = function() {
				return !!$scope.editExistingMelding;
			};

			/// START Melding

			$scope.meldingIdSelected = null;
			$scope.setSelected = function(meldingId) {
				$scope.meldingIdSelected = meldingId;
			};

			$scope.buttonUserDisabled = function() {
				return false;
			};

			$scope.checkMeldingFormInvalid = function(targetform) {
				// TODO Fields!!
				return targetform['voornaam'].$invalid ||
					targetform['naam'].$invalid ||
					targetform['emailadres'].$invalid ||
					targetform['functie'].$invalid ||
					$scope.emailCheckInvalid(targetform, 'emailadres', 'emailadrescontrole');
			};

			cleanMeldingForm = function() {
				delete $rootScope.error;
				delete $scope.error;
				delete $scope.meldingSavedOk;
				delete $scope.meldingRemovedOk;

				// set form fields not dirty
				if($scope.formmeldinggegevens) {
					$scope.formmeldinggegevens.$setPristine();
				}

				if($scope.formbedrijfsgegevens) {
					$scope.formbedrijfsgegevens.$setPristine();
				}
				
				if($scope.formmeldingnotes) {
					$scope.formmeldingnotes.$setPristine();
				}
				
				if($scope.formmeldingnotesadmin) {
					$scope.formmeldingnotesadmin.$setPristine();
				}
			};

			$scope.newMelding = function() {
				cleanMeldingForm();

				delete $scope.meldingIdSelected;
				$scope.meldingDetails = true;

				// TODO Fields!!
				$scope.controle = {
					bedrijfsgegevensok       : $scope.controle.bedrijfsgegevensok, //false,
					vorderingbedragok        : false,
					akkoord                  : true,
					bedrijfsgegevensNietJuist: $scope.controle.bedrijfsgegevensNietJuist // false
				};
				$scope.melding = {
					meldingId                : $scope.newMeldingId,
					gebruikerAangemaaktId    : JSON.parse($window.sessionStorage.user).userId,
					bedrijfIdGerapporteerd   : $scope.owncompany.bedrijfId,
					bedrijfId                : $scope.company.bedrijfId,
					bedrijfsgegevensNietJuist: false,
					doorBedrijfWeergeven     : true,
					bedragWeergeven          : true,
					debiteurSommatieOntvangen: false,
					debiteurBetwistNiet      : false
				};
				$scope.newMeldingId++;

				$scope.mastermelding = angular.copy($scope.melding);

				$scope.mandatoryChecks();
			};

			$scope.changeMelding = function(meldingId) {
				cleanMeldingForm();
				$scope.meldingIdSelected = meldingId;
				$scope.meldingDetails = true;

				melding = $.grep($scope.meldingen, function(e) {
					return e.meldingId == meldingId;
				});

				$scope.mastermelding = angular.copy($scope.melding);

				// TODO Fields!!!
				$scope.controle = {
					bedrijfsgegevensok       : $scope.controle.bedrijfsgegevensok, //true,
					vorderingbedragok        : false, // 500 check true
					akkoord                  : true,
					bedrijfsgegevensNietJuist: $scope.controle.bedrijfsgegevensNietJuist // false
				};

				// for editing validation purpose
				var convertedDatumFactuur = null;
				if(melding[0].datumFactuur) {
					convertedDatumFactuur = melding[0].datumFactuur ? melding[0].datumFactuur.replace(/-/g, '') : null;
				}

				$scope.melding = {
					meldingId                : melding[0].meldingId,
					gebruikerAangemaaktId    : melding[0].gebruikerAangemaaktId,
					gebruikerLaatsteMutatieId: JSON.parse($window.sessionStorage.user).userId,
					bedrijfIdGerapporteerd   : melding[0].bedrijfIdGerapporteerd,
					bedrijfId                : melding[0].bedrijfId,
					bedrijfsgegevensNietJuist: melding[0].bedrijfsgegevensNietJuist,
					doorBedrijfWeergeven     : melding[0].doorBedrijfWeergeven,
					bedragWeergeven          : melding[0].bedragWeergeven,
					referentie               : melding[0].referentie,
					datumFactuur             : convertedDatumFactuur,
					bedrag                   : melding[0].bedrag,
					debiteurSommatieOntvangen: melding[0].debiteurSommatieOntvangen,
					debiteurBetwistNiet      : melding[0].debiteurBetwistNiet
				};

				$scope.mandatoryChecks();

				//$scope.filterResult();
			};

			$scope.deleteMelding = function(meldingId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller : RemoveNotificationController,
					size       : 'lg'
				});

				modalInstance.result.then(function(result) {
					if(result.remove) {
						cleanMeldingForm();
						$scope.meldingIdSelected = meldingId;

						// do delete
						removeNotification(meldingId, companyData.bedrijfId);
					}
				}, function() {
					console.log('Modal dismissed at: ' + new Date());
				});

			};

			$scope.getAfdeling = function(afdeling) {
				// afdeling name fill with default if unknown
				if(typeof afdeling == 'undefined' || afdeling == null || afdeling == '') {
					return 'Vermeldingen';
				} else {
					return afdeling;
				}
			};

			removeNotification = function(meldingId, bedrijfId) {
				$scope.removeMeldingId = meldingId;

				//CompanyService.removeNotificationCompany({
				//	meldingId: meldingId,
				//	bedrijfId: bedrijfId,
				//	gebruikerId: JSON.parse($window.sessionStorage.user).userId
				//}, function (result) {
				//	if (typeof result.errorCode !== "undefined")
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	else {
				//		$scope.meldingIdSelected = null;
				//		delete $scope.meldingDetails;
				//
				//		$scope.deleteMeldingCallback();
				//	}
				//});

				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;

				$scope.deleteMeldingCallback();
			};

			$scope.cancelMeldingDetails = function() {
				cleanMeldingForm();
				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;
				delete $scope.melding;
				delete $scope.duplicateFactuurnummer;

				// for verzendlijst
				$scope.controle.vermeldingenJuist = false;
			};

			$scope.saveMeldingCallback = function() {
				$scope.meldingSavedOk = true;

				$scope.filterResult();
				//refresh
				//$scope.currentPage = 1;
				//$scope.pageChanged();
				//
				//$scope.meldingIdSelected = null;
				//delete $scope.meldingDetails;
				//delete $scope.melding;
				//
				//save + new
				$scope.newMelding();
			};

			$scope.updateMeldingCallback = function() {
				$scope.meldingVerwerkt = true;
				
				// obsolete
				//$scope.meldingSavedOk = true;

				// set form fields not dirty
				$scope.formmeldingnotes.$setPristine();
				
				$scope.formmeldinggegevens.$setPristine();

				$scope.formbedrijfsgegevens.$setPristine();
			};

			$scope.deleteMeldingCallback = function() {
				$scope.meldingRemovedOk = true;

				$scope.filterResult();
				// refresh
				//$scope.currentPage = 1;
				//$scope.pageChanged();

				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;
				delete $scope.melding;
			};

			validateNotificationList = function() {
				var result = true;

				if($scope.melding && typeof $scope.melding.referentie !== 'undefined' && $scope.melding.referentie !== null) {

					for(var i in $scope.meldingen) {
						if($scope.meldingen[i].meldingId != $scope.melding.meldingId && $scope.meldingen[i].referentie.toLowerCase().replace(/[^a-zA-Z 0-9]+/g, '') == $scope.melding.referentie.toLowerCase().replace(/[^a-zA-Z 0-9]+/g, '')) {
							result = false;
							break; //Stop this loop, we found it!
						}
					}
				}

				return result;
			};

			$scope.saveMeldingPassTwo = function(callback) {
				//delete $scope.error;
				//delete $scope.duplicateFactuurnummer;
				cleanMeldingForm();

				if(validateNotificationList()) {
					$scope.meldingToValidate = {
						meldingId                : (!isNaN(parseFloat($scope.melding.meldingId)) && isFinite($scope.melding.meldingId) ? null : $scope.melding.meldingId),
						gebruikerAangemaaktId    : $scope.melding.gebruikerAangemaaktId,
						gebruikerLaatsteMutatieId: JSON.parse($window.sessionStorage.user).userId,
						bedrijfIdGerapporteerd   : $scope.melding.bedrijfIdGerapporteerd,
						bedrijfId                : $scope.melding.bedrijfId,
						bedrijfsgegevensNietJuist: $scope.melding.bedrijfsgegevensNietJuist,
						doorBedrijfWeergeven     : $scope.melding.doorBedrijfWeergeven,
						bedragWeergeven          : $scope.melding.bedragWeergeven,
						referentie               : $scope.melding.referentie,
						datumFactuur             : convertDate($scope.melding.datumFactuur),
						bedrag                   : $scope.melding.bedrag,
						debiteurSommatieOntvangen: $scope.melding.debiteurSommatieOntvangen,
						debiteurBetwistNiet      : $scope.melding.debiteurBetwistNiet
					};

					CompanyService.validateNotification($scope.meldingToValidate, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							if(response.errorCode == 118) // Duplicate referenceNumber
							{
								$scope.duplicateFactuurnummer = true;
							}
							else {
								$scope.error = response.errorCode + " " + response.errorMsg;
							}
						}
						else {
							$scope.preregister($scope.saveMeldingCallback); // create or update!

							callback();
							//$scope.saveMeldingCallback();
						}
					});
				}
				else {
					$scope.duplicateFactuurnummer = true;
				}
			};

			// form button
			$scope.updateMelding = function() {
				$scope.formSubmitted = true;

				if(!($scope.formmeldinggegevens.$invalid || $scope.formbedrijfsgegevens.$invalid || ($scope.isUnchanged($scope.melding) && $scope.isUnchangedAdminNote()) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;
					//delete $scope.error;
					if (!$scope.isUnchanged($scope.melding)) {
						cleanMeldingForm();
						$scope.register($scope.updateMeldingCallback); // create or update!
					}
					
					// admin note
					if ($scope.gebruikerAllowed && !$scope.isUnchangedAdminNote()) {
						var notitieAdmin = {
								meldingId : $scope.melding.meldingId,
								notitie : $scope.meldingNotitiesAdmin
						}
						
						CompanyService.addAdminNotitieNotification(notitieAdmin, function(response) {
							if(typeof response.errorCode !== 'undefined') {
								$scope.error = response.errorCode + " " + response.errorMsg;
							} else {	
								$scope.notitieSavedOk = true;
								
								$scope.formmeldingnotesadmin.$setPristine();
							}
						});
					}
				}
			};
			
			//Form update note
			$scope.updateNote = function() {
				$scope.formSubmitted = true;
				
				if(!(($scope.isUnchanged($scope.melding) && $scope.isUnchangedAdminNote()) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;
					delete $scope.error;
					if (!$scope.isUnchanged($scope.melding)) {
						cleanMeldingForm();
						$scope.register($scope.updateMeldingCallback); // create or update!
					}
					
					// admin note
					if ($scope.gebruikerAllowed && !$scope.isUnchangedAdminNote()) {
						var notitieAdmin = {
								meldingId : $scope.melding.meldingId,
								notitie : $scope.meldingNotitiesAdmin
						}
						
						CompanyService.addAdminNotitieNotification(notitieAdmin, function(response) {
							if(typeof response.errorCode !== 'undefined') {
								$scope.error = response.errorCode + " " + response.errorMsg;
							} else {	
								$scope.notitieSavedOk = true;
								
								$scope.formmeldingnotesadmin.$setPristine();
							}
						});
					}
				}
			};
			
			

			// form button
			$scope.saveMelding = function() {
				$scope.formSubmitted = true;
				if(!($scope.formmeldinggegevens.$invalid || ($scope.formbedrijfsgegevens.$invalid || (!$scope.controle.bedrijfsgegevensok && !$scope.controle.bedrijfsgegevensNietJuist)) || $scope.isUnchanged($scope.melding) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;

					$scope.saveMeldingPassTwo(function() {
					});
				}
			};

			// form button
			$scope.saveMeldingAndSend = function() {
				//$scope.saveMeldingPassTwo($scope.sendMeldingen);
				$scope.formSubmitted = true;
				if(!($scope.formmeldinggegevens.$invalid || ($scope.formbedrijfsgegevens.$invalid || (!$scope.controle.bedrijfsgegevensok && !$scope.controle.bedrijfsgegevensNietJuist)) || $scope.isUnchanged($scope.melding) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;

					$scope.saveMeldingPassTwo($scope.cancelMeldingDetails);
				}
			};

			// form button
			$scope.sendMeldingen = function() {
				$scope.formSubmitted = true;

				if($scope.controle.vermeldingenJuist) {
					//searchurl = '$notifycompany';

					// set bedrijfsgegevensjuist
					for(var i in $scope.meldingen) {
						$scope.meldingen[i].bedrijfsgegevensNietJuist = $scope.controle.bedrijfsgegevensNietJuist;
					}

					NotificationsToSend.setNotifications($scope.meldingen);

					$location.path('/notificationssend/' + $scope.owncompany.bedrijfId + '/' + $scope.company.bedrijfId + '/' + searchurl);
					$location.url($location.path());
				}
			};

			$scope.$on("$destroy", function() {
				// empty notification cache if not sending them
				if($location.path().lastIndexOf('/notificationssend', 0) !== 0) {
					NotificationsToSend.setNotifications([]);
				}
			});

			// Data table functions
			$scope.filterResult = function() {

				return $scope.fetchResult(function() {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.currentPage = 1;
				});
			};

			$scope.pageChanged = function() {
				if($scope.meldingDetails == true) {
					$scope.cancelMeldingDetails();
				}
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();

				console.log('Page changed to: ' + $scope.currentPage);
				$scope.fetchResult(function() {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function(success) {

				// no server call needed

				// add new melding to array
				if($scope.meldingSavedOk == true) {
					if(typeof $scope.meldingIdSelected != 'undefined' && $scope.meldingIdSelected != null) {
						for(var i in $scope.meldingen) {
							if($scope.meldingen[i].meldingId == $scope.meldingIdSelected) {
								$scope.meldingen[i] = $scope.melding;
								break; //Stop this loop, we found it!
							}
						}
					}
					else {
						if(typeof $scope.meldingen != 'undefined') {
							if(Object.prototype.toString.call($scope.meldingen) === '[object Array]') {
								$scope.meldingen.push($scope.melding);
							} else {
								$scope.meldingen = [$scope.melding];
							}
						}
						else {
							$scope.meldingen = [$scope.melding];
						}

						if($scope.totalItems) {
							$scope.totalItems++;
						} else {
							$scope.totalItems = 1;
						}

						// if new items added, goto companiesnotified dashboard afterwards
						//searchurl = '$dashboard$companiesnotifiedtab';
					}
				}
				// remove melding from array
				else if($scope.meldingRemovedOk == true) {
					$scope.meldingen = $scope.meldingen
						.filter(function(el) {
							return el.meldingId !== $scope.removeMeldingId;
						});
					$scope.totalItems--;

					// if no new items added anymore, goto back to original
					if($scope.totalItems == 0) {
						searchurl = orgsearchurl;
						//delete $scope.totalItems;
					}
				}
			};
			
			$scope.contactMomentPageChanged = function(page) {
				$scope.contactMomentCurrentPage = page;
				$scope.contactMomentFilterCriteria.pageNumber = $scope.contactMomentCurrentPage;
				$scope.fetchContactMomenten(function() {
					delete $rootScope.contactMomentRange;
				});
			};
			
			$scope.fetchContactMomenten = function(success) {
				$rootScope.range = 'items=' + (($scope.contactMomentFilterCriteria.pageNumber - 1) * $scope.contactMomentItemsPage + 1) + '-' + (($scope.contactMomentFilterCriteria.pageNumber - 1) * $scope.contactMomentItemsPage + $scope.contactMomentItemsPage);
				CompanyService.getContactMomentsOfNotification($scope.contactMomentFilterCriteria, function(data, headers) {
					if(data != undefined && data.length > 0) {
						$scope.contactMomenten = data;

						$scope.contactMomentTotalItems = paging_totalItems(headers('Content-Range'));
					} else {
						$scope.contactMomenten = [];
						$scope.contactMomentTotalItems = 0;
					}
					success();
				}, function(error) {
					$scope.contactMomenten = [];
					$scope.contactMomentTotalItems = 0;

					$scope.error = error;
				})
			};

			/// END Melding

			$scope.datePattern = /^[0-9]{2}-[0-9]{2}-[0-9]{4}$/i;

			$scope.pickDateFactuur = function(dateval) {

				$scope.melding.datumFactuur = $filter('date')(dateval, $scope.dateFormat);
			};
			
			$scope.dateChange = function(dateval) {
			    var selectedDate = dateval;
			    var min = new Date(selectedDate.getTime());
			    min.setHours(2);
			    min.setMinutes(0);
			    $scope.min = min;

			    var max = new Date(selectedDate.getTime());
			    max.setHours(4);
			    max.setMinutes(0);
			    $scope.max = max;
			}			

		    $scope.initTimePicker = function(selectedDate) {
		         var min = new Date(selectedDate.getTime());
		         min.setHours(0);
		         min.setMinutes(0);
		         $scope.min = min;

		         var max = new Date(selectedDate.getTime());
		         max.setHours(11);
		         max.setMinutes(59);
		         $scope.max = max;
		    };	    
		    
			$scope.pickDateContact = function(dateval) {
				$scope.contactMoment.datumContact = $filter('date')(dateval, $scope.dateFormat);				
			};
			
			$scope.pickTimeContact = function(timeval) {
				$scope.contactMoment.timeContact = $filter('date')(timeval, $scope.timeFormat);
			}
			
			$scope.pickDateContactTerug = function(dateval) {

				$scope.contactMoment.datumContactTerug = $filter('date')(dateval, $scope.dateFormat);
			};		
			
			
			convertDate = function(date) {
				if(date && date.length == 8) {
					return date.substring(0, 2) + '-' + date.substring(2, 4) + '-' + date.substring(4, 8);
				}
				else if(date) {
					return date;
				} else {
					return null;
				}
			};
			
			convertDatetime = function(date, time) {
				var datetime = convertDate(date);
				
				if (datetime && time && time.length == 4) {
					datetime +=  ' ' + time.substring(0,2) + ':' + time.substring(2,4) + ':00';
				}
				
				if (datetime)
					return datetime;
				else
					return null;
			}	
			
			$scope.toggleContactwijze = function(contactwijze) {
				if (contactwijze != 'Anders') {
					$scope.contactMoment.contactGegevens = null;
				}
			}
			
			$scope.contactMomentComplete = function() {
				var result = false;
				if (typeof $scope.contactMoment != 'undefined') {
					if ($scope.contactMoment.datumContact != null &&
							$scope.contactMoment.timeContact != null &&
							$scope.contactMoment.contactwijze != null &&
							$scope.contactMoment.beantwoord != null &&
							$scope.contactMoment.notitie != null)
						return true;				
				}
				
				return result;
			}
			
			$scope.removeContactMoment = function(contactMomentId) {
				CompanyService.removeContactMomentNotification({contactMomentId : contactMomentId, bedrijfId : $scope.melding.bedrijfIdGerapporteerd}, function(response) {
					if (typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorCode + " " + response.errorMsg;
					} else {
						$scope.meldingVerwerkt = true;
						$scope.fetchContactMomenten(null);
					}
				});
			}

			$scope.saveContactMoment = function() {
				if ($scope.contactMomentComplete())
				{
					$scope.contactMomentTransfer = {
							datumContact : convertDatetime($scope.contactMoment.datumContact, $scope.contactMoment.timeContact),
							datumContactTerug : convertDate($scope.contactMoment.dateContactTerug),
							contactWijze : $scope.contactMoment.contactwijze,
							contactGegevens : $scope.contactMoment.contactGegevens,
							beantwoord : $scope.contactMoment.beantwoord,
							notitie : $scope.contactMoment.notitie,
							notitieIntern : $scope.contactMoment.notitieIntern,
							alert : $scope.contactMoment.alert,
							meldingId : $scope.melding.meldingId,
							bedrijfIdGerapporteerd : $scope.melding.bedrijfIdGerapporteerd,
							notitieType : 'CMO'
					};
					
					CompanyService.addContactMomentNotification($scope.contactMomentTransfer, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							$scope.error = response.errorCode + " " + response.errorMsg;
						} else {
							$scope.contactMoment = response.contactMoment;
							$scope.contactMoment = {
									datumContact : $filter('date')(dateNow, $scope.dateFormat),
									timeContact : $filter('date')(dateNow, $scope.timeFormat),
							};							
							$scope.formcontactmoment.$setPristine();
							
							$scope.contactMomentSavedOk = true;
							
							$scope.fetchContactMomenten(function() {
								delete $rootScope.contactMomentRange;
							});							
						}
					});
				}
			}
			
			$scope.preregister = function(callback) {
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				console.log('savemelding function');
				//if (typeof $scope.datumFactuur !== 'undefined' )
				//{
				//$scope.melding.datumFactuur = $scope.datumFactuur;
				//$scope.melding.datumFactuur = $filter('date')($scope.melding.datumFactuur,'dd-MM-yyyy');
				//}
				$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

				callback();
			};

			$scope.register = function(callback) {
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				console.log('register function');
				//if (typeof $scope.datumFactuur !== 'undefined' )
				//{
				//$scope.melding.datumFactuur = $scope.datumFactuur;
				//$scope.melding.datumFactuur = $filter('date')($scope.melding.datumFactuur,'dd-MM-yyyy');
				//}
				$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

				CompanyService.notifyCompany($scope.melding, function(response) {
					if(typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorCode + " " + response.errorMsg;
					} else {
						//response.doorBedrijfWeergeven = response.doorBedrijfWeergeven == 'true' ? true : false;
						//response.bedragWeergeven = response.bedragWeergeven == 'true' ? true : false;
						//response.bedrijfsgegevensNietJuist = response.bedrijfsgegevensNietJuist  == 'true' ? true : false;

						$scope.controle.bedrijfsgegevensNietJuist = response.bedrijfsgegevensNietJuist;
						$scope.bedrijfgegevensJuistOnLoad = !response.bedrijfsgegevensNietJuist;

						if(response.bedrag) {
							response.bedrag = Number(response.bedrag);
						}

						// for form validation/editing
						if(response.datumFactuur) {
							response.datumFactuur = response.datumFactuur.replace(/-/g, '');
						}

						response.debiteurSommatieOntvangen = true;
						response.debiteurBetwistNiet = true;

						$scope.melding = response;
						$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

						callback();
					}
				});
			};

			$scope.open = function($event, indexCtrl) {
				$event.preventDefault();
				$event.stopPropagation();

				if (indexCtrl == 1)
					$scope.opened_1 = true;
				else if (indexCtrl == 2)
					$scope.opened_2 = true;
					
			};

			$scope.dateOptions = {
				formatYear : 'yyyy',
				startingDay: 1
			};

			$scope.clearDateInvoice = function() {
				$scope.melding.datumFactuur = null;
			};

			$scope.back = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			addMandatoryText = function(type, text) {
				var item = {type: type, text: text};

				if(typeof $scope.mandatoryText != 'undefined') {
					if(Object.prototype.toString.call($scope.mandatoryText) === '[object Array]') {
						$scope.mandatoryText.push(item);
					} else {
						$scope.mandatoryText = [item];
					}
				}
				else {
					$scope.mandatoryText = [item];
				}
			};

			$scope.maakbedragop = function(bedrag) {
				if(bedrag) {
					return '&euro; ' + bedrag;
				} else {
					return '-'
				}
			};

			$scope.mandatoryChecks = function() {
				$scope.mandatoryText = [];

//				if($scope.controle.bedrijfsgegevensok == false) {
//					addMandatoryText('warning', 'Er moet aangegeven worden dat de bedrijfsgegevens van de vermelding juist zijn.');
//				}

				var debiteurText = null;
				if(typeof $scope.melding.debiteurSommatieOntvangen == 'undefined' || $scope.melding.debiteurSommatieOntvangen == false) {
					debiteurText = 'U dient aan te geven dat de debiteur reeds op de hoogte is van de vordering.';
				}
				if(typeof $scope.melding.debiteurBetwistNiet == 'undefined' || $scope.melding.debiteurBetwistNiet == false) {
					if(debiteurText == null) {
						addMandatoryText('warning', 'U dient aan te geven dat de debiteur de vordering niet betwist.');
					} else {
						addMandatoryText('warning', 'U dient aan te geven dat de debiteur reeds op de hoogte is van de vordering en dat deze niet betwist wordt.');
					}
				} else if(debiteurText != null) {
					addMandatoryText('warning', debiteurText);
				}

				// 500 check if ($scope.bedragAanwezig($scope.melding.bedrag) == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false) )
				//	addMandatoryText('warning', 'Indien er geen bedrag ingevuld wordt, hoger dan 500 Euro, dient bevestigd te worden dat het factuurbedrag van de vermelding hoger is dan 500 Euro.');

				if($scope.controle.akkoord == false) {
					addMandatoryText('warning', 'U moet akkoord gaan met onze algemene voorwaarden.');
				}

				if($scope.mandatoryText.length == 0 && !($scope.isBedragInvalid($scope.melding.bedrag) || $scope.isDatumFactuurInvalid($scope.melding.datumFactuur)) && !($scope.formmeldinggegevens.$invalid || $scope.formbedrijfsgegevens.$invalid || $scope.buttonMeldingDisabled())) {
					if(!$scope.wijziging) {
						addMandatoryText('success', ' U kunt kiezen om deze vermelding direct te verzenden of hier nog een vermelding aan toe te voegen.');
					}
				}
			};

			$scope.buttonMeldingDisabled = function() {

				if(typeof $scope.melding != 'undefined') {
					if($scope.isBedragInvalid($scope.melding.bedrag) || $scope.isDatumFactuurInvalid($scope.melding.datumFactuur)) {
						return true;
					} else if($scope.bedragAanwezig == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false)) {
						return true;
					}
				}

				//if (!controle.akkoord)
				//	return true;

				return false;
			};

			$scope.bedragAanwezig = function(bedrag) {
				return !!(typeof bedrag != 'undefined' && bedrag != null && bedrag != "");
			};

			$scope.isReferentieInvalid = function(referentie) {
				var result = false;
				var referencetext = 'Indien u geen factuurnummer hanteert geeft u een dossiernummer of ander kenmerk op.';

				if($scope.duplicateFactuurnummer) {
					referencetext = 'Het ingevoerde factuurnummer is al gebruikt voor een bestaande of ingevoerde vermelding. Voer een ander factuurnummer in.';
					result = true;
				}

				referenceHelpSpan.text(referencetext);

				return result;
			};

			$scope.isBedragInvalid = function(bedrag) {
				if($scope.bedragAanwezig(bedrag)) {
					$scope.controle.vorderingbedragok = false;
				}

				//var bedragtext = "Voer een geldig bedrag (afgerond op hele Euro's) in";
				var bedragtext = "Voer het exacte bedrag in.";
				var result = false;

				if(!$scope.wijziging && typeof bedrag != 'undefined' && bedrag != null && bedrag != "" && $scope.NUMBERINTERNAL_REGEXP.test(bedrag)) {
					if(bedrag < 100) {
						bedragtext = "Het openstaande bedrag moet minimaal 100,- Euro bedragen.";
						result = true; // invalid
					}
					else {
						result = false;
					} // valid
				} else {
					result = false;
				} // valid

				if($scope.wijziging && typeof bedrag != 'undefined') {
					if(typeof $scope.mastermelding.bedrag !== 'undefined' && $scope.mastermelding.bedrag != null) {
						if(typeof bedrag != 'undefined' && bedrag != null) {
							if($scope.mastermelding.bedrag < bedrag) {
								bedragtext = "Het bedrag mag alleen worden verlaagd.";
								result = true; // invalid
							}
						} else {
							bedrag = $scope.mastermelding.bedrag; // this may not happen
						}
					} else {
						bedragtext = "Bedrag kan niet worden gewijzigd"; // this may not happen
						result = true; // invalid
					}
				}

				if(bedrag > 100000000) {
					bedragtext = "Het ingevoerde bedrag mag maximaal 100.000.000,- zijn.";
					result = true; // invalid					
				}

				bedragHelpSpan.text(bedragtext);

				return result;
			};

			$scope.isDatumFactuurInvalid = function(datumFactuur) {
				var datumFactuurtext = 'Vul de vervaldatum van de factuur in (dd-mm-jjjj).';
				var result = false;

				if(typeof datumFactuur != 'undefined' || datumFactuur != null) {
					var datumFactuurdate = new Date();
					var datumVandaag = new Date();

					if(typeof datumFactuur == 'string') {
						if(datumFactuur.length == 8) {
							datumFactuurdate = new Date(datumFactuur.substring(4, 8), datumFactuur.substring(2, 4) - 1, datumFactuur.substring(0, 2));
						} else if(datumFactuur.length == 10) {
							datumFactuurdate = new Date(datumFactuur.substring(6, 10), datumFactuur.substring(3, 5) - 1, datumFactuur.substring(0, 2));
						} else {
							result = true;
						} // invalid
					}
					else {
						datumFactuurdate = datumFactuur;
					}

					if(!result) { // if valid
						if (datumVandaag !== null && datumFactuurdate !== null) {
							if(dates.compare(datumVandaag, datumFactuurdate) < 0) {
								datumFactuurtext = 'Factuurdatum mag niet in de toekomst liggen.';
								result = true; // invalid
							}
							else {
	
								var timeDiff = Math.abs(datumVandaag.getTime() - datumFactuurdate.getTime());
								var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
	
								if(diffDays < 30) {
									datumFactuurtext = 'Vervaldatum van factuur moet minimaal 30 dagen zijn.';
									result = true; // invalid
								}
								else {
									result = false;
								} // valid
							}
						} 
					}
				}

				datumfactuurHelpSpan.text(datumFactuurtext);
				return result;
			};

			$scope.gebruikerAllowed = function() {
				if (typeof $window.sessionStorage.user !== 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;
	
					return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd') 
				} else
					return false;
			};

			$scope.meldingVerwijderd = function() {
				if(typeof $scope.melding !== 'undefined' && $scope.melding != null) {
					return !!($scope.melding.meldingstatusCode == 'DEL' || $scope.melding.meldingstatusCode == 'AFW');
				} else {
					return false;
				}
			};

			$scope.redenVerwijderen = function() {
				if(typeof $scope.melding !== 'undefined' && $scope.melding != null) {
					if(typeof $scope.melding.redenVerwijderenOmschrijving !== 'undefined' && $scope.melding.redenVerwijderenOmschrijving != null) {
						if($scope.melding.redenVerwijderenCode == 'AND') {
							return 'Onbekend';
						} else {
							return $scope.melding.redenVerwijderenOmschrijving;
						}
					}
					else {
						return 'Onbekend';
					}
				} else {
					return 'Onbekend';
				}

			};

			$scope.changeAdresCheck = function(field) {
				if($scope.controle.bedrijfsgegevensok == true &&
					$scope.controle.bedrijfsgegevensNietJuist == true) {
					if(field == 'selBedrijfAdresOk') {
						$scope.controle.bedrijfsgegevensNietJuist = false;
					} else {
						$scope.controle.bedrijfsgegevensok = false;
					}
				}

				$scope.mandatoryChecks();
			};

			if(typeof $scope.init === "undefined") {
				$scope.readonly = !!(typeof readonly !== 'undefined' && readonly == 'true' || readonly == true);

				$scope.wijziging = !!(typeof notificationidentifier !== 'undefined' && $scope.readonly == false);

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20; // 2
				$scope.maxSize = 5;
				$scope.newMeldingId = 0;

				$scope.formSubmitted = false;

				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				delete $scope.meldingen;

				if(typeof notificationidentifier === 'undefined') {
					$scope.controle = {
						bedrijfsgegevensok       : false,
						vorderingbedragok        : false,
						akkoord                  : true,
						bedrijfsgegevensNietJuist: false
					};

					$scope.meldingen = NotificationsToSend.getNotifications();
					if(typeof $scope.meldingen !== 'undefined' && $scope.meldingen.length > 0) {
						// for verzendlijst
						$scope.controle.vermeldingenJuist = false;
					}

					$scope.totalItems = $scope.meldingen.length;

					if($scope.totalItems == 0) {
						$scope.newMelding();
					}
					else {
						$scope.melding = {
							gebruikerAangemaaktId    : JSON.parse($window.sessionStorage.user).userId,
							bedrijfIdGerapporteerd   : $scope.owncompany.bedrijfId,
							bedrijfId                : $scope.company.bedrijfId,
							doorBedrijfWeergeven     : true,
							bedragWeergeven          : true,
							bedrijfsgegevensNietJuist: false,
							debiteurSommatieOntvangen: false,
							debiteurBetwistNiet      : false
						};
					}
				}
				else {
					$scope.controle = {
						bedrijfsgegevensok: true,
						vorderingbedragok : false,
						akkoord           : true
					};

					$scope.controle.bedrijfsgegevensok = true;

					var notificationData = CompanyService.companyNotificationData({
						meldingId: notificationidentifier,
						bedrijfId: companyidentifier
					});

					var resultsNotificationPromise = $q.all({
						notificationdata: notificationData.$promise
					}).then(function(data) {
						if(typeof data.notificationdata.errorCode !== "undefined") {
							$scope.error = data.errorCode + " " + data.errorMsg;
						} else {

							//data.notificationdata.doorBedrijfWeergeven = data.notificationdata.doorBedrijfWeergeven == 'true' ? true : false;
							//data.notificationdata.bedragWeergeven = data.notificationdata.bedragWeergeven == 'true' ? true : false;
							//data.notificationdata.bedrijfsgegevensNietJuist = data.notificationdata.bedrijfsgegevensNietJuist  == 'true' ? true : false;

							$scope.controle.bedrijfsgegevensNietJuist = data.notificationdata.bedrijfsgegevensNietJuist;
							if(data.notificationdata.bedrijfsgegevensNietJuist) {
								bedrijfgegevensJuistOnLoad = false;
							} else {
								$scope.bedrijfgegevensJuistOnLoad = true;
							}

							if(data.notificationdata.bedrag) {
								data.notificationdata.bedrag = Number(data.notificationdata.bedrag);
							}

							if((typeof $scope.readonly == 'undefined' || $scope.readonly == false) && !$scope.editExistingMelding) {
								// for form validation/editing
								if(data.notificationdata.datumFactuur) {
									data.notificationdata.datumFactuur = data.notificationdata.datumFactuur.replace(/-/g, '');
								}
							}

							// 500 check if (typeof data.notificationdata.bedrag == 'undefined' || data.notificationdata.bedrag == null || (data.notificationdata.bedrag && data.notificationdata.bedrag >= 500))
							//	$scope.controle.vorderingbedragok = true;

							data.notificationdata.debiteurSommatieOntvangen = true;
							data.notificationdata.debiteurBetwistNiet = true;

							$scope.melding = data.notificationdata;

							$scope.mastermelding = angular.copy($scope.melding);

							$scope.mandatoryChecks();

							if(typeof $scope.melding !== 'undefined' && typeof $scope.melding.gebruikerAangemaaktId !== 'undefined') {
								var gebruikerLaatsteMutatie = 'null';
								var gebruikerVerwijderd = 'null';
								var gebruikerGeaccordeerd = 'null';
								var datumLaatsteMutatie = 'null';
								var datumAfgewezen = 'null';
								var datumVerwijderd = 'null';
								var datumGeaccordeerd = 'null';
								if(typeof $scope.melding.gebruikerLaatsteMutatieId !== 'undefined') {
									gebruikerLaatsteMutatie = $scope.melding.gebruikerLaatsteMutatieId;
								}
								if(typeof $scope.melding.gebruikerVerwijderdId !== 'undefined') {
									gebruikerVerwijderd = $scope.melding.gebruikerVerwijderdId;
								}
								if(typeof $scope.melding.gebruikerGeaccordeerdId !== 'undefined') {
									gebruikerGeaccordeerd = $scope.melding.gebruikerGeaccordeerdId;
								}
								if(typeof $scope.melding.datumLaatsteMutatie !== 'undefined') {
									datumLaatsteMutatie = $scope.melding.datumLaatsteMutatie;
								}
								if(typeof $scope.melding.datumGeaccordeerd !== 'undefined') {
									datumGeaccordeerd = $scope.melding.datumGeaccordeerd;
								}
								if(typeof $scope.melding.datumVerwijderd !== 'undefined' && $scope.melding.meldingstatusCode == 'AFW') {
									datumAfgewezen = $scope.melding.datumVerwijderd;
								}
								if(typeof $scope.melding.datumVerwijderd !== 'undefined' && $scope.melding.meldingstatusCode == 'DEL') {
									datumVerwijderd = $scope.melding.datumVerwijderd;
								}
								$scope.gebruikerIds = [$scope.melding.gebruikerAangemaaktId.toString(), gebruikerGeaccordeerd.toString(), gebruikerVerwijderd.toString(), gebruikerLaatsteMutatie.toString()];
								var gebruikerIdsJson = {gebruikerIds: $scope.gebruikerIds};
								var gebruikerData = GebruikersServicePath.gebruikerdata(gebruikerIdsJson);

								var resultsGebruikerPromise = $q.all({
									gebruikerdata: gebruikerData.$promise
								}).then(function(data) {
									if(typeof data.gebruikerdata.errorCode !== "undefined") {
										$scope.error = data.errorCode + " " + data.errorMsg;
									} else {
										$scope.gebruiker = data.gebruikerdata;

										$scope.acties = [{
											actie    : 'Ingediend',
											datum    : $scope.melding.datumAangemaakt,
											gebruiker: $scope.gebruiker[0].fullName,
											functie  : $scope.gebruiker[0].functie,
											afdeling : $scope.gebruiker[0].afdeling,
											email    : $scope.gebruiker[0].emailAdres
										}];
										i = 1;
										if(datumGeaccordeerd != 'null') {
											if(gebruikerGeaccordeerd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Geaccordeerd',
														datum    : $scope.melding.datumGeaccordeerd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Geaccordeerd',
													datum    : $scope.melding.datumGeaccordeerd,
													gebruiker: '-',
													functie  : '-',
													afdeling : '-',
													email    : '-'
												});
											}
										}
										if(datumVerwijderd != 'null') {
											if(gebruikerVerwijderd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Verwijderd',
														datum    : $scope.melding.datumVerwijderd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Verwijderd',
													datum    : $scope.melding.datumVerwijderd,
													gebruiker: 'CRZB',
													email    : '-'
												});
											}
										}
										if(datumAfgewezen != 'null') {
											if(gebruikerVerwijderd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Afgewezen',
														datum    : $scope.melding.datumVerwijderd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Afgewezen',
													datum    : $scope.melding.datumVerwijderd,
													gebruiker: 'CRZB',
													email    : '-'
												});
											}
										}
										//if (datumLaatsteMutatie != 'null') {
										//	if (gebruikerLaatsteMutatie != 'null') {
										//		if($scope.gebruiker[i]!=null) {
										//			$scope.acties.push({
										//				actie: 'Laatst gewijzigd',
										//				datum: $scope.melding.datumLaatsteMutatie,
										//				gebruiker: $scope.gebruiker[i].fullName,
										//				functie: $scope.gebruiker[i].functie,
										//				afdeling: $scope.gebruiker[i].afdeling,
										//				email: $scope.gebruiker[i].emailAdres
										//			});
										//			i++;
										//		}
										//	} else
										//		$scope.acties.push({
										//			actie: 'Laatst gewijzigd',
										//			datum: $scope.melding.datumLaatsteMutatie,
										//			gebruiker: 'crzb',
										//			functie: '-',
										//			afdeling: '-',
										//			email: '-'
										//		});
										//}
									}
								}).catch(function(error) {
									console.log('Error fetching notification data');
									$scope.error = 'Er is een fout opgetreden.';
								});
							}
							
							$scope.contactwijzen = [
								{id: 'Telefoon', description: 'Telefoon' + (($scope.melding.telefoonNummerDebiteur !== 'undefined' && $scope.melding.telefoonNummerDebiteur != null) ? ' | ' + $scope.melding.telefoonNummerDebiteur : '')},
								{id: 'Email', description: 'Email' + (($scope.melding.emailAdresDebiteur !== 'undefined' && $scope.melding.emailAdresDebiteur != null) ? ' | ' + $scope.melding.emailAdresDebiteur : '')},
								{id: 'Brief', description: 'Brief | ' + $scope.melding.adres},
								{id: 'Aangetekend', description: 'Aangetekend | ' + $scope.melding.adres},
								{id: 'Persoonlijk', description: 'Persoonlijk'},
								{id: 'Anders', description: 'Anders'}
							];
							
							$scope.contactMomentFilterCriteria = {
									userId     : JSON.parse($window.sessionStorage.user).userId,
									meldingId  : $scope.melding.meldingId,
									pageNumber : 1,
									sortDir    : '', // asc, desc
									sortedBy   : '',
									filterValue: '' // text to filter on
								};
							
							$scope.contactMomentCurrentPage = 1;
							$scope.contactMomentItemsPage = 10;
							$scope.contactMomentMaxSize = 5;
							$scope.contactMomentTotalItems = 0;

							if ($scope.gebruikerAllowed()) {
								CompanyService.getAdminNoteOfNotification({meldingId : $scope.melding.meldingId}, function(response) {
									if (typeof response !== 'undefined') {
										if (typeof response.errorCode !== 'undefined') {
											$scope.error = response.errorMsg;
										} else {
											if (response != null) {
												$scope.meldingNotitiesAdmin = response.notitie;
												$scope.masterMeldingNotitiesAdmin = $scope.meldingNotitiesAdmin;
											}
										}
									}
								});
							}
							
							$scope.fetchContactMomenten(function(){});								
						}
					}).catch(function(error) {
						console.log('Error fetching notification data');
						$scope.error = 'Er is een fout opgetreden.';
					});
									

					//$scope.melding = CompanyService.companyNotificationData({
					//	meldingId: notificationidentifier,
					//	bedrijfId: companyidentifier
					//}, function (data) {
					//	if (typeof data.errorCode !== "undefined") {
					//		$scope.error = data.errorCode + " " + data.errorMsg;
					//	} else {
					//
					//		data.doorBedrijfWeergeven = data.doorBedrijfWeergeven == 'true' ? true : false;
					//		data.bedragWeergeven = data.bedragWeergeven == 'true' ? true : false;
					//
					//		data.debiteurSommatieOntvangen = false,
					//			data.debiteurBetwistNiet = false
					//
					//		return data;
					//	}
					//});

				}

				$scope.formSubmitted = false;
				
				$scope.beantwoordTypes = [
					{id: 'Ja', description: 'Ja'},
					{id: 'Nee', description: 'Nee'},
					{id: 'Terugbellen', description: 'Terugbellen'},
					{id: 'BuitenGebruik', description: 'Buiten gebruik'},
					{id: 'Nvt', description: 'n.v.t.'},
					{id: 'Bezwaar', description: 'Bezwaar'},
					{id: 'Anders', description: 'Anders'}
				];
				
				$scope.EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
				$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;				
				
				var dateNow = new Date();
				
				$scope.initTimePicker(dateNow);
				
				$scope.contactMoment = {
						datumContact : $filter('date')(dateNow, $scope.dateFormat),
						timeContact : $filter('date')(dateNow, $scope.timeFormat),
						//contactwijze : "",
						//contactGegevens : "",
						//beantwoord : "",
						//datumContactTerug : $filter('date')(dateNow, $scope.dateFormat),
						//notitie : "",
						//notitieIntern : ""
				};
				$scope.timeContact = dateNow;
				
				$scope.meldingVerwerkt = false;
				
				$scope.init = true;
			}

			// fetch initial data for 1st time
			$scope.filterResult();

			$scope.createDocumentRequested = function (meldingId) {
				return {action: 'letter_notification', bedrijfId: 0, meldingId: meldingId, referentie: ''};
			};
			
			$scope.annulerenall = function() {

				if($scope.totalItems > 0) {
					var modalInstance = $modal.open({
						templateUrl: 'removeallnotifications.html',
						controller : RemoveAllNotificationsController,
						size       : 'lg'
						//resolve: { removeNotification: removeNotification() }
					});

					modalInstance.result.then(function(result) {
						if(result.remove) {
							$scope.back();
						}
					}, function() {
						console.log('Modal dismissed at: ' + new Date());
					});

				}
				else {
					$scope.back();
				}
			};

			var RemoveAllNotificationsController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeAllNotificationsOk = function(reason) {
					$scope.remove = true;
					$scope.closeRemoveAllNotificationsModal();
				};

				$scope.closeRemoveAllNotificationsModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			$scope.annuleren = function(terug) {

				if(!$scope.meldingVerwerkt && !$scope.meldingSavedOk && 
						($scope.formmeldinggegevens.$dirty || $scope.formmeldingnotes.$dirty || $scope.formmeldingnotesadmin.$dirty)) {
					var modalInstance = $modal.open({
						templateUrl: 'removechanges.html',
						controller : RemoveChangesController,
						size       : 'lg'
						//resolve: { removeNotification: removeNotification() }
					});

					modalInstance.result.then(function(result) {
						if(result.remove) {
							if(terug) {
								$scope.back();
							} else {
								$scope.cancelMeldingDetails();
							}
						}
					}, function() {
						console.log('Modal dismissed at: ' + new Date());
					});
				} else {
					if(terug) {
						$scope.back();
					} else {
						$scope.cancelMeldingDetails();
					}
				}

			};

			var RemoveChangesController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeChangesOk = function(reason) {
					$scope.remove = true;
					$scope.closeRemoveChangesModal();
				};

				$scope.closeRemoveChangesModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			var RemoveNotificationController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeNotificationOk = function(reason) {
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];
		}]);