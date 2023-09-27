appcontrollers.controller('CompaniesNotifiedTabController',
	['$window', '$modal', '$routeParams', '$scope', '$rootScope', '$location', '$filter', 'maxFieldLengths',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function($window, $modal, $routeParams, $scope, $rootScope, $location, $filter, maxFieldLengths,
				 $timeout, ngTableParams, DashboardNumberTags, DashboardService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;

			$scope.title = "Companies Notified Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			
			var donatePopupController = ['$window', '$scope', '$rootScope', '$modal', 'maxFieldLengths', '$location', '$routeParams', 'CompanyService', '$modalInstance',
				function($window, $scope, $rootScope, $modal, maxFieldLengths, $location, $routeParams, CompanyService, $modalInstance) {

					$scope.maxFieldLengths = maxFieldLengths;
					
					// to access dialog forms
					$scope.form = {};
					$scope.donationAmount;

					$scope.NUMBERINTERNAL_NODECIMALS_REGEXP = /^(\d*|\d+)$/;

					if($scope.initialised == null) {
						$scope.maxDonationLength = 7;
						$scope.success = false;
						// set first to true due actual payable check at the end
						$scope.userCanPay = true;

						$scope.initialised = true;
					}

					$scope.donationIsInvalid = function(donationAmount) {
						if(typeof $scope.form.formdonation.donationform != 'undefined' && $scope.form.formdonation.donationform.$dirty) {
							if(typeof donationAmount != 'undefined' && donationAmount != null && donationAmount != '') {
								if (!$scope.NUMBERINTERNAL_NODECIMALS_REGEXP.test(donationAmount)) {
									return true;
								} else if(donationAmount < 5 || donationAmount > 1000000) {
									return true;
								} else 
									return false;										
							} else 
								return true;																				
						} else {
							return false;
						}
					};
					
					$scope.donateDisabled = function(donationAmount) {
						if (typeof donationAmount != 'undefined' && donationAmount!=null && donationAmount!=''&&!$scope.donationIsInvalid(donationAmount))
							return false;
						else
							return true;
					};

					$scope.initiateDonation = function(amount) {
						CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
							if(typeof result.errorCode != 'undefined') {
								$scope.error = result.errorCode + " " + result.errorMsg;
							} else {
								$scope.userCanPay = result.val;

								if(false) { // !$scope.userCanPay turned off
									// Cannot donate because cannot pay
									$scope.noDonationInfo = 'U kunt helaas geen donaties doen omdat u nog geen betalingsgegevens hebt opgegeven. Dit kunt u aanpassen onder \'Mijn Account\'';
								} else {
									// DO Donation
									if(!$scope.donationIsInvalid(amount)) {
										console.log('donation is made, amount is ' + amount);
									}
									CompanyService.donate({donationAmount: amount}, function(result) {
										if(result.errorCode != null) {
											$scope.error = result.errorCode + ' ' + result.errorMsg;
										} else {
											if(result.val) {
												$scope.success = true;
											}
										}
									});
									
								}
							}
						});

					};

					$scope.cancel = function() {
						$modalInstance.close();
					}
				}];

			var RemoveNotificationController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function(reason) {
					$scope.reason = reason;
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function() {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];	

			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function() {
				console.log('Page changed to: ' + $scope.currentPage);
			};

			$scope.documentRequested = {
				action   : 'notified_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			isSearchChanged = function(search) {
				if(typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else {
					return $scope.oldSearch != search;
				}
			};

			$scope.$watch('filterCriteria.filterValue', function(search) {
				if(typeof searchCallbackTimeout != 'undefined') {
					$timeout.cancel(searchCallbackTimeout);
				}

				searchCallbackTimeout = $timeout(function() {
					if(search) {
						if(search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if(isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			if(typeof $scope.filterCriteria == 'undefined') {

				$scope.filterCriteria = {
					bedrijfId  : JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber : 1,
					sortDir    : "", // asc, desc
					sortedBy   : "",
					filterValue: "" // text to filter on
				};
			}

			$scope.nrOfItems = function() {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function() {
				return DashboardNumberTags.getNrOfNotifications() > 0;
			};

			// Data table functions
			$scope.filterResult = function() {

				return $scope.fetchResult(function() {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function(success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.companiesnotified($scope.filterCriteria, function(data, headers) {
					console.log("Companies fetched");
					//$scope.companies = data.companyNotified;
					if(typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfNotifications($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function() {
					console.log("Error fetching companies");
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfNotifications($scope.totalItems);
					$scope.firstfetch = true;
				});

			};

			// Pagination functions
			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function() {
				delete $scope.notificationRemoved;
				delete $scope.error;
				console.log('Page changed to: ' + $scope.currentPage);
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function() {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.notificationChange = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$companiesnotifiedtab/' + meldingId);
			};

			$scope.notificationReadOnly = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$companiesnotifiedtab/' + meldingId + '/true');
			};

			removeNotification = function(meldingId, bedrijfId, reason) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				CompanyService.removeNotificationCompany({
					meldingId  : meldingId,
					bedrijfId  : bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden      : reason
				}, function(result) {
					if(typeof result.errorCode !== "undefined") {
						$scope.error = result.errorMsg;
					} else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.notificationRemove = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller : RemoveNotificationController,
					size       : 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function(result) {
					if(result.remove) {
						$scope.initiateDonation(meldingId, bedrijfId, result.reason);
					}
				}, function() {
					console.log('Modal dismissed at: ' + new Date());
				});
			};

			$scope.notAllowedChangeNotification = function(statusCode) {
				if(statusCode != null) {
					if(statusCode == 'NOK' || statusCode == 'INI' || statusCode == 'INB') {
						return 'Vermelding in behandeling';
					} else if(statusCode == 'AFW' || statusCode == 'DEL') {
						return 'Vermelding is verwijderd';
					}
				}
			};

			$scope.showDonatePopup = function() {
				var donatePopup = $modal.open({
					templateUrl: 'donationPopup.html',
					controller : donatePopupController,
					size       : 'lg'
				});

				//donatePopup.result.then(function (result) {
				//	if (result.continue) {
				//		searchurl = '/donation/$dashboard$companiesnotifiedtab$search$' + $scope.filterCriteria.filterValue + '$' + $scope.currentPage;
				//
				//		$location.path(searchurl);
				//		$location.url($location.path());
				//	}
				//})
			};

			$scope.initiateDonation = function(meldingId, bedrijfId, reason) {
				removeNotification(meldingId, bedrijfId, reason);
				if(reason == 1 && (hasRole(JSON.parse($window.sessionStorage.user).roles, 'hoofd_klant') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_klant'))) {
					var donatePopup = $modal.open({
						templateUrl: 'donationPopup.html',
						controller : donatePopupController,
						size       : 'lg'
					});
				}
			};

			// fetch initial data for 1st time
			$scope.filterResult();
			
			if (typeof $window.sessionStorage.user !== 'undefined') {
				CompanyService.companyDataExtra({bedrijfId : JSON.parse($window.sessionStorage.user).bedrijfId}, function(result) {
	        		if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.companyAccount = result;
					}
	        	});
			}

			$scope.showRemoveNotification = function(userId, status) {
				//	    	 if (userId == JSON.parse($window.sessionStorage.user).userId)
				//	    		 return true;
				//	    	 else if (status == 'ACT')
				return true;

			}

		}]);