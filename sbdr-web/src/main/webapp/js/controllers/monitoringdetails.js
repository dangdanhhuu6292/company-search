appcontrollers.controller('MonitoringDetailsController',
	['$window', '$modal', '$scope', '$rootScope', '$routeParams', '$location', 'maxFieldLengths',
		'$filter', '$q', 'monitoringDetailData', 'GebruikersServicePath', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, $routeParams, $location, maxFieldLengths,
				  $filter, $q, monitoringDetailData, GebruikersServicePath, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			var allCompanies = [];
			var companies = [];
			var companyidentifier = $routeParams.eigenBedrijfId;
			var monitoringId = $routeParams.monitoringId;
			var searchurl = $routeParams.searchurl;

			$scope.monitorData = monitoringDetailData.monitoring;
			$scope.hasSearchResults = false;

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			var RemoveNotificationController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.reason = reason;
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function () {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result);
				};
			}];

			$scope.documentRequested = {
				action: 'monitorDetails',
				bedrijfId: companyidentifier,
				monitorId: monitoringId
			};

			$scope.back = function () {
				url = gotoDurl(searchurl);
				if (url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			$scope.bedragVermelding = function (bedrag) {
				if (bedrag == undefined || bedrag == null)
					return 'afgeschermd';
				else
					return $filter('currency')(bedrag);
			};

			$scope.bedrijfdoorgegevens = function (bedrijfId, bedrijfsnaam, adres, sbiOmschrijving) {
				if (bedrijfId != undefined && bedrijfId != null) {
					var bedrijfsgegevens = '';
					if (bedrijfsnaam != undefined && bedrijfsnaam != null)
						bedrijfsgegevens = bedrijfsgegevens + bedrijfsnaam;
					if (adres != undefined && adres != null)
						bedrijfsgegevens = bedrijfsgegevens + ', ' + adres;

					return bedrijfsgegevens;
				} else
					return sbiOmschrijving;
			};

			$scope.fetchResult = function (success) {
				if (monitoringDetailData.meldingen != undefined && monitoringDetailData.meldingen != null) {
					$scope.allCompanies = monitoringDetailData.meldingen;
					$scope.totalItems = $scope.allCompanies.length;
					success();
				} else {
					$scope.allCompanies = [];
					$scope.totalItems = 0;
				}
			};

			$scope.hasVermeldingen = function () {
				if ($scope.companies != undefined && $scope.companies != null) {
					return $scope.companies.length > 0;
				} else
					return false;
			};

			$scope.notificationChange = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$monitoringdetails$' + companyidentifier + '$' + monitoringId + escapeSearchUrl(searchurl) + '/' + meldingId);
			};

			$scope.notificationReadOnly = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$monitoringdetails$' + companyidentifier + '$' + monitoringId + escapeSearchUrl(searchurl) + '/' + meldingId + '/true');
			};

			$scope.notificationRemove = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				var modalInstance = $modal.open({
					templateUrl: 'removenotificationmon.html',
					controller: RemoveNotificationController,
					size: 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function (result) {
					if (result.remove)
						removeNotification(meldingId, bedrijfId, result.reason);
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			getCompaniesOnPage = function (currentPage, itemsOnPage, totalItems, allcompanieslist) {
				var begin = (currentPage - 1) * itemsOnPage;
				var end = Math.min(begin + itemsOnPage, totalItems);

				return allcompanieslist.slice(begin, end);
			};
			
			$scope.pageChanged = function () {
				if ($scope.companies) {
					console.log('Page changed to: ' + $scope.currentPage);
					$scope.searchcompany.pageNumber = $scope.currentPage;

					pageparturl = '$' + $scope.currentPage;
					searchurl = searchparturl + pageparturl;

					$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);
				}
			};

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.showRemoveNotification = function (userId, status) {
//	    	 if (userId == JSON.parse($window.sessionStorage.user).userId)
//	    		 return true;
//	    	 else if (status == 'ACT')
				return true;
			};

			$scope.getAfdeling = function(afdeling) {
				// afdeling name fill with default if unknown
				if(typeof afdeling == 'undefined' || afdeling == null || afdeling == '') {
					return 'Vermeldingen';
				} else {
					return afdeling;
				}
			};

			$scope.vermeldingenPagingOn = function () {
				if ($scope.hasVermeldingen()) {
					return $scope.totalItems - $scope.itemsPage > 1;
				} else
					return false;
			};

			performSearchCompanies = function () {
				$scope.hasSearchResults = true;

				$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);

			};

			removeNotification = function (meldingId, bedrijfId, reason) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				CompanyService.removeNotificationCompany({
					meldingId: meldingId,
					bedrijfId: bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				}, function (result) {
					if (typeof result.errorCode != 'undefined')
						$scope.error = result.errorMsg;
					else {
						$scope.notificationRemoved = true;
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.fetchResult(performSearchCompanies);

			if ($scope.monitorData != undefined && $scope.monitorData.gebruikerAangemaaktId != undefined) {

				$scope.acties = [];

				//Add actions concerning the addition/edits/removals of the monitoring
				var gebruikerLaatsteMutatie = null;
				var gebruikerVerwijderd = null;
				var datumLaatsteMutatie = null;
				var datumVerwijderd = null;
				if ($scope.monitorData.gebruikerVerwijderdId != undefined && $scope.monitorData.gebruikerVerwijderdId != null)
					gebruikerVerwijderd = $scope.monitorData.gebruikerVerwijderdId;
				if ($scope.monitorData.gewijzigd != undefined && $scope.monitorData.gewijzigd != null)
					datumLaatsteMutatie = $scope.monitorData.gewijzigd;
				if ($scope.monitorData.gebruikerLaatsteMutatieId != undefined && $scope.monitorData.gebruikerLaatsteMutatieId != null)
					gebruikerLaatsteMutatie = $scope.monitorData.gebruikerLaatsteMutatieId;
				if ($scope.monitorData.verwijderd != undefined && $scope.monitorData.verwijderd != null)
					datumVerwijderd = $scope.verwijderd;
				$scope.gebruikerIds = [
					$scope.monitorData.gebruikerAangemaaktId.toString(),
					gebruikerLaatsteMutatie != null ? gebruikerLaatsteMutatie.toString() : 'null',
					gebruikerVerwijderd != null ? gebruikerVerwijderd.toString() : 'null'];

				var gebruikerIdsJson = {gebruikerIds: $scope.gebruikerIds};

				GebruikersServicePath.gebruikerdata(gebruikerIdsJson, function (result) {
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.gebruikers = result;

						$scope.acties.push({
							actie: 'Monitoring gestart',
							datum: monitoringDetailData.toegevoegd,
							gebruiker: $scope.gebruikers[0].fullName,
							functie: $scope.gebruikers[0].functie,
							afdeling: $scope.gebruikers[0].afdeling,
							email: $scope.gebruikers[0].emailAdres
						});

						//i = 1;
						//
						//if (datumVerwijderd != null) {
						//	if (gebruikerVerwijderd != null) {
						//		$scope.acties.push({
						//			actie: 'Verwijderd op',
						//			datum: monitoringDetailData.verwijderd,
						//			gebruiker: $scope.gebruikers[i].fullName,
						//			functie: $scope.gebruikers[i].functie,
						//			afdeling: $scope.gebruikers[i].afdeling,
						//			email: $scope.gebruikers[i].emailAdres
						//		});
						//		i++;
						//	} else {
						//		$scope.acties.push({
						//			actie: 'Verwijderd op',
						//			datum: monitoringDetailData.verwijderd,
						//			gebruiker: '-',
						//			functie: '-',
						//			afdeling: '-',
						//			email: '-'
						//		});
						//	}
						//}
						//
						//if (datumLaatsteMutatie != null) {
						//	if (gebruikerLaatsteMutatie != null) {
						//		$scope.acties.push({
						//			actie: 'Laatst bijgewerkt op',
						//			datum: monitoringDetailData.gewijzigd,
						//			gebruiker: $scope.gebruikers[i].fullName,
						//			functie: $scope.gebruikers[i].functie,
						//			afdeling: $scope.gebruikers[i].afdeling,
						//			email: $scope.gebruikers[i].emailAdres
						//		});
						//		i++;
						//	} else {
						//		$scope.acties.push({
						//			actie: 'Laatst bijgewerkt op',
						//			datum: monitoringDetailData.gewijzigd,
						//			gebruiker: '-',
						//			functie: '-',
						//			afdeling: '-',
						//			email: '-'
						//		});
						//	}
						//}
					}

					//Add actions of notifications about the company in monitoring
					if ($scope.companies != undefined) {
						GebruikersServicePath.sbdrgebruiker(function(result){
							if (typeof result.errorCode != 'undefined') {
								$scope.error = result.errorCode + " " + result.errorMsg;
							} else {
								$scope.companies.forEach(function (company) {
									if (company.toegevoegd != undefined) {
										$scope.acties.push({
											actie: 'Betalingsachterstand ' + company.referentieIntern + ' opgenomen',
											datum: company.datumGeaccordeerd,
											gebruiker: result.fullName.trim(),
											functie: result.functie,
											afdeling: result.afdeling,
											email: result.emailAdres
										})
									}									

									if (company.datumVerwijderd != undefined) {
										$scope.acties.push({
											actie: 'Betalingsachterstand ' + company.referentieIntern + ' verwijderd',
											datum: company.datumVerwijderd,
											gebruiker: result.fullName.trim(),
											functie: result.functie,
											afdeling: result.afdeling,
											email: result.emailAdres
										})
									}
								});
								
								//Sort the actions list on date, newest first
								$scope.acties.sort(function (x, y) {
									var dateTimeRegex = /(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2})/;
									var xDateArray = dateTimeRegex.exec(x.datum);
									var yDateArray = dateTimeRegex.exec(y.datum);
									var xDate = new Date((+xDateArray[3]), (+xDateArray[2]) - 1, (+xDateArray[1]), (+xDateArray[4]), (+xDateArray[5]));
									var yDate = new Date((+yDateArray[3]), (+yDateArray[2]) - 1, (+yDateArray[1]), (+yDateArray[4]), (+yDateArray[5]));
									if (xDate.getTime() > yDate.getTime())
										return 1;
									else if (xDate.getTime() === yDate.getTime())
										return 0;
									else
										return -1;
								});
							
							}
						});
					}

				});
			}
		}]);