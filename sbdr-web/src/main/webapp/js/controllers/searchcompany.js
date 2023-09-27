appcontrollers.controller('SearchCompanyController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', '$filter', 'ngTableParams', '$modal', 'maxFieldLengths',
		'cfpLoadingBar', 'companyData', 'clientip', 'CompanyService', 'NewAccountService',
		function($window, $routeParams, $scope, $rootScope,
				$location, $filter, ngTableParams, $modal, maxFieldLengths,
				 cfpLoadingBar, companyData, clientip, CompanyService, NewAccountService) {
		    
			// redirect if mobile
			//if ($rootScope.isMobileResolution()) {
			//    window.location.hef = "/informatie.php";
			//}		
			
			$scope.hasSearchResults = false;
			$scope.maxFieldLengths = maxFieldLengths;
			
			var allCompanies = [];

			var infoPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closePopup = function() {
					$modalInstance.close();
				}
			}];

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function(){
					$modalInstance.close();
				}
			}];

			var reportPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doReport = false;

				CompanyService.getTariefOfProduct({ProductCode: 'RAP'}, function(result) {
					if(result.errorCode == undefined) {
						$scope.reportCost = result.tarief;
					}
				});

				$scope.createReport = function() {
					$scope.doReport = true;
					$scope.closeReportPopup();
				};

				$scope.closeReportPopup = function() {
					var result = {
						doReport : $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			var searchurl = '';

			var userBedrijfId = undefined;
			if($window.sessionStorage.user) {
				userBedrijfId = JSON.parse($window.sessionStorage.user).bedrijfId || undefined;
				$rootScope.content_id = 'content';
			} else {
				userBedrijfId = undefined;
				$rootScope.content_id = 'nscontent';
			}

			$scope.complete = function() {
				cfpLoadingBar.complete();
			};

			$scope.showInfoPopup = function() {
				var infoModal = $modal.open({
					templateUrl: 'searchInfoPopup.html',
					controller : infoPopupController,
					size       : 'lg'
				});
			};

			$scope.createaccount = function(kvknumber, hoofdvestiging, subdossier) {
				resetFields();
				console.log("create account function");

				if(typeof subdossier != 'undefined') {
					kvknumber += subdossier;
				}
				
				$location.path('/createaccount/' + kvknumber + '/' + hoofdvestiging + '/' + searchurl);
			};

			$scope.fetchResult = function(success) {
				resetFields();
				//$httpProvider.defaults.headers.put['Range'] = $scope.range;
				$rootScope.range = "items=" + (($scope.searchcompany.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.searchcompany.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

				if($window.sessionStorage.user !== undefined) {
					CompanyService.search($scope.searchcompany, function(data, headers) {
						$scope.totalItems = paging_totalItems(headers("Content-Range"));
						console.log("Companies searched + found: " + $scope.totalItems);
						console.log("headers: " + headers('Content-Range'));
						// The request results are managed client side so store all results in overall object
						$scope.allCompanies = data;
						success();
					}, function() {
						console.log("Error search companies");
						$scope.companies = [];
						$scope.totalItems = 0;
					});
				}
				else {
					//$httpProvider.defaults.headers.put['ipAddress'] = clientip;
					//$httpProvider.defaults.headers.put['X-Auth-ApiKey'] = $scope.apikey;								
					NewAccountService.search($scope.searchcompany, function(data, headers) {
						console.log("SearchCompanies searched + found: " + data.totalItems);
						console.log("headers: " + headers('Content-Range'));
						// The request results are managed client side so store all results in overall object
						$scope.totalItems = paging_totalItems(headers("Content-Range"));

						$scope.allCompanies = data;
						success();
					}, function() {
						console.log("Error search companies");

						$scope.companies = [];
						$scope.totalItems = 0;
					});
				}
			};

			$scope.isSearchFieldsOk = function(searchcompany) {
				return searchcompany.searchValue != "";
			};

			$scope.monitoringcompany = function(bedrijfId, bedrijfsNaam, adres, kvkNummer, subDossier, hoofd, bekendBijSbdr, monitoringBijBedrijf) {
				resetFields();
				$scope.monitorBedrijfSelected = bedrijfsNaam;

				searchkvk = kvkNummer;
				if(typeof subDossier != 'undefined') {
					searchkvk += subDossier;
				}

				if(typeof bedrijfId != 'undefined') {
					$location.path('/monitoringquestion/' + bedrijfId + '/' + searchkvk + '/' + hoofd + '/' + encodeURIComponent($rootScope.escapeParam(bedrijfsNaam)) + '/' + encodeURIComponent($rootScope.escapeParam(adres)) + '/' + searchurl + '/' + bekendBijSbdr + '/' + monitoringBijBedrijf);
				} else {
					$location.path('/monitoringquestion/' + 'undefined' + '/' + searchkvk + '/' + hoofd + '/' + encodeURIComponent($rootScope.escapeParam(bedrijfsNaam)) + '/' + encodeURIComponent($rootScope.escapeParam(adres)) + '/' + searchurl + '/' + bekendBijSbdr + '/' + monitoringBijBedrijf);
				}
				$location.url($location.path());
			};
			
			$scope.isOwnCompany = function(kvknumber) {
				if (typeof companyData != 'undefined' && companyData!=null) {
					return companyData.kvkNummer == kvknumber;
				} else
					return false;
			};

			$scope.opmerkingen = function(meldingBijBedrijf, monitoringBijBedrijf, rapportCreatedToday) {
				var result = '';
				var nr = 1;
				if(meldingBijBedrijf) {
					result = 'Er is al eerder een melding gedaan door uw bedrijf.';
					nr = nr + 1;

				}
				if(monitoringBijBedrijf) {
					if(nr > 1) {
						result += '\n' + nr + ') ';
					} 
					result += 'Reeds in monitoring geplaatst';
					nr = nr + 1;
				}
				if(rapportCreatedToday) {
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1 ){
						result = nr + ') ';
					}
					result += 'Er is vandaag al een rapport opgevraagd door uw bedrijf.';
					nr = nr + 1;
				}
				if($scope.isProspect()==true){
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1) {
						result = nr + ') ';
					}
					result += 'Uw accountregistratie is nog in behandeling. U kunt nog geen gebruik maken van rapport of monitoring. Dit wordt mogelijk na verificatie, u ontvangt hierover schriftelijk bericht.';
					nr = nr + 1;
				}
				if (!$scope.isAdresOk()) {
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1) {
						result = nr + ') ';
					}
					result += 'Uw accountregistratie is nog in behandeling. U kunt nog geen gebruik maken van rapport of monitoring. Dit wordt mogelijk na verificatie, u ontvangt hierover schriftelijk bericht.';
					nr = nr + 1;					
				}
				if (nr > 2)
					result = '1) ' + result; 

				return result;
			};

			$scope.pageChanged = function() {
				if($scope.companies) {
					console.log('Page changed to: ' + $scope.currentPage);
					$scope.searchcompany.pageNumber = $scope.currentPage;
					// all items are managed client side, so no new fetch server side needed here

					pageparturl = '$' + $scope.currentPage;
					searchurl = searchparturl + pageparturl;

					$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);
				}
			};

			$scope.registercompany = function(bedrijfId, kvkNummer, subDossier, hoofd) {
				console.log("save function");
				resetFields();

				searchkvk = kvkNummer;
				if(typeof subDossier != 'undefined') {
					searchkvk += subDossier;
				}

				//if (typeof bedrijfId === 'undefined' || bedrijfId == null) {
				CompanyService.getOrCreateCompanyData({kvknumber: searchkvk, hoofd: hoofd}, function(result) {
					if(typeof result.errorCode !== 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					}
					else {
						if(typeof bedrijfId === 'undefined' || bedrijfId == null) {
							if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
								$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + result.bedrijfId + '/' + searchurl);
							} else {
								$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + result.bedrijfId + '/' + searchurl);
							}
							$location.url($location.path());
						} else { // bedrijf already known
							if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
								$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
							} else {
								$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
							}
							$location.url($location.path());
						}
					}
				});
				//}
				//else {
				//	if (!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO))
				//		$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
				//	else
				//		$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
				//	$location.url($location.path());
				//}
			};

			$scope.isProspect = function() {
				if(typeof $window.sessionStorage.user != 'undefined') {
					return !!JSON.parse($window.sessionStorage.user).prospect;
				} else {
					return false;
				}
			};

			$scope.isAdresOk = function() {
				if(typeof $window.sessionStorage.user != 'undefined') {
					return JSON.parse($window.sessionStorage.user).adresOk;
				} else {
					return false;
				}
			};

			$scope.notAllowedReport = function() {
				if($scope.isProspect() || $scope.isAdresOk() == false) {
					return 'Alleen geverifiëerde klanten kunnen een rapport opvragen';
				} else {
					return '';
				}
			};

			$scope.notAllowedNotification = function() {
				if(!$rootScope.hasRoleUser('registraties_toegestaan')) {
					return 'Geen rechten om registraties in te voeren';
				} else {
					return '';
				}
			};
			
			$scope.notAllowedMonitoring = function(alreadyInMonitoring) {
				if($scope.isProspect() || $scope.isAdresOk() == false) {
					return 'Alleen geverifiëerde klanten kunnen een bedrijf monitoren';
				} else if (alreadyInMonitoring){
					return 'Reeds in monitoring geplaatst'
				} else {
					return '';
				}
			};

			//$scope.requestReport = function (bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf) {
			//	var costModal = $modal.open({
			//		templateUrl: 'reportCostPopup.html',
			//		controller: reportCostPopupController,
			//		size: 'lg'
			//	});

			//	costModal.result.then(function (result) {
			//		if (result.continue)
			//			showReportPopup(bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf);
			//	});
			//};

			$scope.searchCompanies = function() {
				resetFields();
				$scope.currentPage = 1;

				$scope.fetchResult(performSearchCompanies);
			};

			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.start = function() {
				cfpLoadingBar.start();
			};

			getCompaniesOnPage = function(currentPage, itemsOnPage, totalItems, allcompanieslist) {
				var begin = (currentPage - 1) * itemsOnPage;
				var end = Math.min(begin + itemsOnPage, totalItems);

				return allcompanieslist.slice(begin, end);
			};

			$scope.makeReport = function(bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf) {				
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
						if(false) { // !$scope.userCanPay turned off
							var reportNoPaymentModal = $modal.open({
								templateUrl: 'reportNoPayment.html',
								controller: reportNoPaymentController,
								size:'lg'
							});
						} else {
							var bedrijfAanvragerId = userBedrijfId;

							searchkvk = kvkNummer;
							if(typeof subDossier != 'undefined') {
								searchkvk += subDossier;
							}

							//if (typeof bedrijfId === 'undefined' || bedrijfId == null) {
							CompanyService.getOrCreateCompanyData({
								kvknumber: searchkvk,
								hoofd    : hoofd
							}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								}
								else {
									if(bedrijfId == undefined || bedrijfId == null) {
										$location.path('/report/' + bedrijfAanvragerId + '/' + result.bedrijfId + '/' + '0' + '/' + searchurl);
										$location.url($location.path());
									} else { // bedrijf already known
										$location.path('/report/' + bedrijfAanvragerId + '/' + bedrijfId + '/' + '0' + '/' + searchurl);
										$location.url($location.path());
									}
								}
							});
						}
				//	}
				//})
			};

			performSearchCompanies = function() {
				//The request fires correctly but sometimes the ui doesn't update, that's a fix
				if(!jumptopage) {
					$scope.searchcompany.pageNumber = $scope.currentPage;
				} else {
					$scope.currentPage = jumptopage;
					$scope.searchcompany.pageNumber = jumptopage;
				}
				$scope.hasSearchResults = true;

				// set paging objects page 1
				$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);

				searchparturl = '$searchcompany$search$' + $scope.searchcompany.searchValue; // + '$' + $scope.searchcompany.kvknumber;
				pageparturl = '$' + $scope.currentPage;
				searchurl = searchparturl + pageparturl;

				// rootscope range delete
				delete $rootScope.range;

			};

			resetFields = function() {
				delete $scope.monitorCreated;
				delete $scope.error;
				delete $scope.monitorBedrijfSelected;
			};

			//saveShowReportPopup = function () {
			//	delete $scope.error;
			//
			//	$rootScope.saveShowHelpDialog($rootScope.HELP_RAPPORTPURCHASE, $scope.hideReportPopup, function (result) {
			//		if (result.error != null)
			//			$scope.error = result.error;
			//	});
			//};

			if(typeof $scope.initialised == 'undefined') {

				if($routeParams.searchValue) {
					var searchValue = "";
					if($routeParams.searchValue && $routeParams.searchValue != 'undefined') {
						searchValue = $routeParams.searchValue;
					}
					//var kvkNummer = "";
					//if ($routeParams.kvkNummer && $routeParams.kvkNummer != 'undefined')
					//	kvkNummer = $routeParams.kvkNummer;
					var jumptopage = 1;
					if($routeParams.page && $routeParams.page != 'undefined') {
						jumptopage = $routeParams.page;
					}

					$scope.searchcompany = {
						bedrijfId     : userBedrijfId,
						pageNumber    : 1,	// first full set is searched from page 1
						//kvknumber: kvkNummer,
						requestPerPage: false,
						searchValue   : searchValue,
						city          : ""
					};

					$scope.totalItems = 0;
					// currentpage will be set on search
					$scope.itemsPage = 10;
					$scope.maxSize = 5;

					$scope.searchCompanies();
				} else {
					$scope.searchcompany = {
						bedrijfId     : userBedrijfId,
						pageNumber    : 1,
						//kvknumber: "",
						requestPerPage: false,
						searchValue   : "",
						city          : ""
					};

					$scope.totalItems = 0;
					$scope.currentPage = 1;
					$scope.itemsPage = 10;
					$scope.maxSize = 5;
				}

				$rootScope.changeUrlWithoutReload("/searchcompany");

				if(typeof $window.sessionStorage.user != 'undefined') {
					$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

					CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							$scope.error = result.errorCode + " " + result.errorMsg;
						} else {
							$scope.userCanPay = result.val;
						}
					});
				} else {
					$scope.userCanPay = false;
					if ($window.sessionStorage.user === undefined) {
						$rootScope.ipAddress = clientip.ip;
						NewAccountService.getApiKey2(function(result) {
							if (typeof result.errorCode != 'undefined') {
								$scope.error = result.errorMsg;
							} else {
								$rootScope.clientApiKey = result.token;
								$rootScope.ipAddress = clientip.ip;		
							}
						});
					}
				}

				$scope.initialised = true;
			}

		}]);