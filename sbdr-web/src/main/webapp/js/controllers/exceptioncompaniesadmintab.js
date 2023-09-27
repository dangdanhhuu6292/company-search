appcontrollers.controller('ExceptionCompaniesAdminTabController',
	['$window', '$scope', '$rootScope', '$location', '$filter',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', '$modal', 'maxFieldLengths',
		'ExceptionCompanyDataStorage', 'DashboardService', 'CompanyService',
		function ($window, $scope, $rootScope, $location, $filter,
				  $timeout, ngTableParams, DashboardNumberTags, $modal, maxFieldLengths,
				  ExceptionCompanyDataStorage, DashboardService, CompanyService) {
			
			$scope.title = "ExceptionCompanies Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			var companies = [];

			var removeNotificationController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.deleteNotification = false;
				$scope.Continue = function () {
					$scope.deleteNotification = true;
					$scope.Discard();
				};

				$scope.Discard = function () {
					var result = {
						deleteNotification: $scope.deleteNotification
					};

					$modalInstance.close(result);
				}
			}];

			if (typeof $scope.filterCriteria == 'undefined') {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};
			}

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

			$scope.detailsCompany = function (customMeldingId, bedrijfId) {
				if ($scope.companies && typeof customMeldingId !== 'undefined' && customMeldingId !== null) {
					var customMelding = null;
					for (var i in $scope.companies) {
						if ($scope.companies[i].customMeldingId == customMeldingId) {
							customMelding = $scope.companies[i];
							break; //Stop this loop, we found it!
						}
					}

					if (customMelding != null) {
						ExceptionCompanyDataStorage.setExceptionCompanyData(customMelding);

						$location.path('/editcompany/' + bedrijfId + '/$dashboard$exceptioncompaniesadmintab' + '/true');
						$location.url($location.path());
					}
				}

			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.exceptioncompaniesadmin($scope.filterCriteria, function (data, headers) {
					console.log("Companies fetched");
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfExceptionCompaniesAdmin($scope.totalItems);

					success();
				}, function () {
					console.log("Error fetching companies");
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfExceptionCompaniesAdmin($scope.totalItems);
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

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfExceptionCompaniesAdmin() > 0;
			};

			$scope.ignoreException = function (customMeldingId, bedrijfId) {
				var confirmationModal = $modal.open({
					templateUrl: 'removeCustomNotificationAdmin.html',
					controller: removeNotificationController,
					size: 'lg'
				});

				confirmationModal.result.then(function (result) {
					if (result.deleteNotification) {
						ignoreNotification(customMeldingId, bedrijfId);
					}
				});
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

			$scope.resolveException = function (customMeldingId, bedrijfId) {
				var confirmationModal = $modal.open({
					templateUrl: 'removeCustomNotificationAdmin.html',
					controller: removeNotificationController,
					size: 'lg'
				});

				confirmationModal.result.then(function (result) {
					if (result.deleteNotification) {
						resolveNotification(customMeldingId, bedrijfId);
					}
				});
			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			cleanUp = function () {
				delete $scope.error;
				delete $scope.exceptionResolved;
				delete $scope.exceptionIgnored;
			};

			ignoreNotification = function (customMeldingId, bedrijfId) {
				delete $scope.exceptionResolved;
				CompanyService.ignoreException({
					customMeldingId: customMeldingId,
					bedrijfId: bedrijfId
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.exceptionIgnored = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			resolveNotification = function (customMeldingId, bedrijfId) {
				delete $scope.exceptionResolved;
				CompanyService.resolveException({
					customMeldingId: customMeldingId,
					bedrijfId: bedrijfId
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.exceptionResolved = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);
