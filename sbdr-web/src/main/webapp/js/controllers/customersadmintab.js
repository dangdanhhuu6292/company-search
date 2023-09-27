appcontrollers.controller('CustomersAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$filter', 'maxFieldLengths',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService',
		'AccountService', 'LoginService', 'InternalProcessService', 'NewAccountService',
		function ($window, $modal, $scope, $rootScope, $location, $filter, maxFieldLengths,
				  $timeout, ngTableParams, DashboardNumberTags, DashboardService,
				  AccountService, LoginService, InternalProcessService, NewAccountService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.title = "Customers Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			$scope.noNewCustomerLetterBatch = false;
			$scope.batchStarted = false;
			$scope.sizeOfLetterBatch = 0;

			var companies = [];

			var createBatchPopupController = ['$scope', '$modalInstance', 'sizeOfBatch', function ($scope, $modalInstance, sizeOfBatch) {
				$scope.continue = false;
				$scope.sizeOfBatch = sizeOfBatch;

				$scope.acceptPopup = function () {
					$scope.continue = true;
					$scope.discardPopup();
				};

				$scope.discardPopup = function () {
					var result = {continue: $scope.continue};
					$modalInstance.close(result);
				};
			}];

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
			
			var ActivateCustomerController = ['$scope', '$modalInstance', 'regpro', 'company', function ($scope, $modalInstance, regpro, company) {
				$scope.activate = false;
				$scope.regpro = regpro;

				$scope.activateCustomerOk = function (reason) {
					$scope.activate = true;
					$scope.closeActivateCustomerModal();
				};

				$scope.closeActivateCustomerModal = function () {
					var result = {
						activate: $scope.activate,
						regpro: regpro,
						company: company
					};

					$modalInstance.close(result); 
				};
			}];			

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			doActivatePro = function (activationid, userid) {
				cleanUp();

				AccountService.activateCustomerBriefCode({
					activationcode: activationid,
					userid: userid
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.prospectActivated = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			doActivateReg = function (activationid, userid) {
				cleanUp();

				LoginService.activateCustomer({activationid: activationid, username: userid}, function (result) {
					if (typeof result.errorCode !== 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					}
					else {
						$scope.registrationActivated = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.createDocumentRequested = function (bedrijfId) {
				return {
					action: 'letter_customer',
					bedrijfId: bedrijfId,
					meldingId: 0,
					referentie: ''
				};
			};

			$scope.getDagenSinds = function(d) {
				if (typeof d !== 'undefined' && d != null && d.length >= 10) {
					date = new Date(d.substring(6, 10), d.substring(3, 5) - 1, d.substring(0, 2));
					diffc = new Date() - date;
					//getTime() function used to convert a date into milliseconds. This is needed in order to perform calculations.
					 
					days = Math.round(Math.abs(diffc/(1000*60*60*24)));
					//this is the actual equation that calculates the number of days.
					 
					return days;
				} else
					return 'onbekend';
			}
			
			$scope.detailsCustomer = function (bedrijfId) {
				$location.path('/klantaccount/' + bedrijfId + '/$dashboard$customersadmintab');
				$location.url($location.path());
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.customersadmin($scope.filterCriteria, function (data, headers) {
					console.log("Companies fetched");
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfCustomersAdmin($scope.totalItems);

					success();
				}, function () {
					console.log("Error fetching companies");
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfCustomersAdmin($scope.totalItems);
				});

			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.hasAdminRole = function () {
				return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr');
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfCustomersAdmin() > 0;
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				console.log('Page changed to: ' + $scope.currentPage);
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.printCustomerLetter = function (klantId, arrayIndex) {
				if (!$scope.companies[arrayIndex].briefGedownload) {
					InternalProcessService.printCustomerLetter({klantId: klantId}, function (result) {
						if (result.errorCode != null) {
							$scope.error = result.errorCode + ' ' + result.errorMsg;
						} else {
							$scope.companies[arrayIndex].briefStatus = 'DWL';
						}
					});
				}
			};

			$scope.removeCustomer = function (userid) {
				var modalInstance = $modal.open({
					templateUrl: 'removecustomer.html',
					controller: RemoveCustomerController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveCustomer(userid);
					}
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});

			};
			
			$scope.activateCustomer = function (regpro, company) {
				var modalInstance = $modal.open({
					templateUrl: 'activatecustomer.html',
					controller: ActivateCustomerController,
					resolve       : {regpro: function() { return regpro; }, company: function(){ return company; }},
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.activate) {
						if (result.regpro == 'REG')
							doActivateReg(result.company.activationCode, result.company.klantGebruikersNaam);
						else if (result.regpro == 'PRO')
							doActivatePro(result.company.activationCode, result.company.klantId);
					} 
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});

			};

			$scope.requestreport = function (bedrijfId) {
				$location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/$dashboard$customersadmintab');
			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			cleanUp = function () {
				delete $scope.error;
				delete $scope.registrationActivated;
				delete $scope.prospectActivated;
				delete $scope.batchStarted;
				delete $scope.customerRemoved;
			};

			doRemoveCustomer = function (userid) {
				cleanUp();

				AccountService.deleteAccount(userid, function (response) {
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

			if ($scope.init == undefined) {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};

				$scope.init = true;
			}

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);