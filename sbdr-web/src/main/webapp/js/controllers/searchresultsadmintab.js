appcontrollers.controller('SearchResultsAdminTabController',
	['$window', '$modal', '$routeParams', '$scope', '$rootScope', '$location', 'maxFieldLengths',
		'$filter', '$timeout', 'ngTableParams', 'DashboardNumberTags',
		'DashboardService', 'AccountService', 'CompanyService',
		function ($window, $modal, $routeParams, $scope, $rootScope, $location, maxFieldLengths,
				  $filter, $timeout, ngTableParams, DashboardNumberTags,
				  DashboardService, AccountService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			var RemoveCustomerController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeCustomerOk = function (reason) {
					$scope.remove = true;
					$scope.closeRemoveCustomerModal();
				};

				$scope.closeRemoveCustomerModal = function () {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

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

					$modalInstance.close(result); // $scope.remove
				};
			}];

			$scope.maxSearchLength = 100;

			$scope.title = "SearchResults Tab";

			$scope.createDocumentRequested = function (bedrijfId, referentieNummer) {
				return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.infoBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'AFW' || status == 'DEL' || status == 'INV')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'AFW' || status == 'DEL')
						result = true;
				}

				return result;
			};

			$scope.successBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'ACT')
						result = true;
				} else if (type == 'Monitoring') {
					if (status == 'ACT')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'ACT')
						result = true;
				}

				return result
			};

			$scope.warningBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'PRO' || status == 'REG')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'INI' || status == 'INB' || status == 'NOK')
						result = true;
				}

				return result;
			};

			$scope.dangerBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'NOK')
						result = true;
				}

				return result;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined')
					$timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope
								.pageChanged();
							$scope.oldSearch = search;
						}
					} else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope
							.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.hasItems = function () {
				// if
				// (DashboardNumberTags.getNrOfCustomersAdmin()
				// <= 0)
				// return false;
				// else
				return true;
			};

			// Data table functions
			$scope.filterResult = function () {
				return $scope.fetchResult(function () {
					// The request fires correctly but sometimes
					// the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};
			
			$scope.setVermelder = function () {				
				var oldval = $scope.filterCriteria.vermelder;

				$scope.filterCriteria.vermelder = $scope.selectieVermelder;
				
				if (oldval != $scope.filterCriteria.vermelder) {
					$scope.currentPage = 1;
					$scope.pageChanged();
				}
			};

			// The function that is responsible of fetching the
			// result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				searchparturl = '$dashboard$searchresultsadmintab$search$'
					+ $scope.filterCriteria.filterValue; // +
				// '$'
				// +
				// $scope.searchcompany.kvknumber;
				pageparturl = '$' + $scope.currentPage;
				searchurl = searchparturl + pageparturl;

				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.searchresultsadmin(
					$scope.filterCriteria,
					function (data, headers) {
						console.log("Companies fetched");
						if (typeof data != 'undefined') { //.searchResultsOverviewTransfer
							$scope.searchresults = data;

							$scope.totalItems = paging_totalItems(headers("Content-Range"));
						} else {
							$scope.searchresults = null;
							$scope.totalItems = 0;
						}

						success();
					},
					function () {
						console.log("Error fetching companies");
						$scope.searchresults = [];
						$scope.totalItems = 0;
					});

			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				console.log('Page changed to: ' + $scope.currentPage);
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					// Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.searchresults = function () {
				return $scope.totalItems > 0;
			};

			$scope.detailsCustomer = function (bedrijfId) {
				$location.path('/klantaccount/' + bedrijfId + '/' + searchurl); // '/$dashboard$searchresultsadmintab');
				$location.url($location.path());
			};

			$scope.detailsCompany = function (bedrijfId) {
				$location.path('/editcompany/' + bedrijfId + '/' + searchurl); // '/' +
				// '$dashboard$searchresultsadmintab');
				$location.url($location.path());
			};

			$scope.notificationChange = function (meldingId, bedrijfDoorId, bedrijfId) {
				console.log("notifyCompany function: " + meldingId + " " + bedrijfId);
				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + searchurl + '/' + meldingId);
				// '$dashboard$searchresultsadmintab/'
				// +
				// meldingId);
			};

			$scope.notificationReadOnly = function (meldingId, bedrijfDoorId, bedrijfId) {
				console.log("notifyCompany function: " + meldingId + " " + bedrijfId);

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + searchurl + '/' + meldingId + '/true');
			};

			if (typeof $scope.initialised == 'undefined') {
				if (typeof $scope.filterCriteria == 'undefined') {
					$scope.filterCriteria = {
						bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
						pageNumber: 1,
						sortDir: "", // asc, desc
						sortedBy: "",
						filterValue: "", // text to filter on
						vermelder: true
					};
					$scope.selectieVermelder = true;
				}

				var searchresults = [];

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20;
				$scope.maxSize = 5;

				if ($routeParams.searchValue) {

					$scope.filterCriteria.filterValue = $routeParams.searchValue;

					var jumptopage = 1;
					if ($routeParams.page != 'undefined')
						jumptopage = parseInt($routeParams.page);

					$scope.currentPage = jumptopage;

				}

				// fetch initial data for 1st time
				$scope.filterResult();
			}

			doRemoveNotification = function (notificationId, companyId, reason) {

				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				};

				CompanyService.removeNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorMsg;
					}
					else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					console.log("ERROR!!!");
				});
			};

			$scope.removeNotification = function (notificationId, companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller: RemoveNotificationController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveNotification(notificationId, companyId, result.reason);
					}
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});

			};

			doRemoveCustomer = function (companyId) {
				AccountService.deleteAccountOfBedrijf(companyId, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.customerRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.removeCustomer = function (companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removecustomer.html',
					controller: RemoveCustomerController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveCustomer(companyId);
					}
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});

			};
		}]);