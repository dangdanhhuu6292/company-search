appcontrollers.controller('CompaniesRemovedTabController',
	['$window', '$scope', '$rootScope', '$filter', '$timeout', 'maxFieldLengths',
		'ngTableParams', 'DashboardNumberTags', 'DashboardService',
		function ($window, $scope, $rootScope, $filter, $timeout, maxFieldLengths,
				  ngTableParams, DashboardNumberTags, DashboardService) {
		
			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.title = "Companies Removed Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

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

			$scope.documentRequested = {
				action: 'removed_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			if (typeof $scope.filterCriteria == 'undefined') {
				$scope.sortedOrder = true;
				$scope.reverseSort = true;
				$scope.orderByField = 'datumEinde';

				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					view: 'all',
					sortDir: 'DESC', // asc, desc
					sortedBy: 'datumEinde',
					filterValue: '' // text to filter on
				};

				$scope.selectieMonitoring = true;
				$scope.selectieVermelding = true;
			}

			$scope.nrOfItems = function () {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfRemovedCompanies() > 0;
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

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.companiesremoved($scope.filterCriteria, function (data, headers) {
					console.log("Companies fetched");
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfRemovedCompanies($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					console.log("Error fetching companies");
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfRemovedCompanies($scope.totalItems);
					$scope.firstfetch = true;
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
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.fetchOrder = function (field) {
				$scope.orderByField = field;
				$scope.reverseSort = !$scope.reverseSort;

				if ($scope.filterCriteria.sortDir == 'ASC')
					$scope.filterCriteria.sortDir = 'DESC';
				else
					$scope.filterCriteria.sortDir = 'ASC';

				$scope.filterCriteria.sortedBy = $scope.orderByField;
				$scope.filterCriteria.sortedOrder = $scope.reverseSort;

				$scope.currentPage = 1;
				$scope.pageChanged();
			};

			$scope.fetchSelectie = function (field) {
				if ($scope.selectieMonitoring == false &&
					$scope.selectieVermelding == false) {
					if (field == 'selMonitoring')
						$scope.selectieVermelding = true;
					else
						$scope.selectieMonitoring = true;
				}

				var oldval = $scope.filterCriteria.view;

				if ($scope.selectieMonitoring && $scope.selectieVermelding)
					$scope.filterCriteria.view = 'all';
				else if ($scope.selectieMonitoring)
					$scope.filterCriteria.view = 'monitoring';
				else if ($scope.selectieVermelding)
					$scope.filterCriteria.view = 'melding';

				if (oldval != $scope.filterCriteria.view) {
					$scope.currentPage = 1;
					$scope.pageChanged();
				}
			};

			$scope.createPeriod = function (from, to) {
				if (from && to)
					return from + ' - ' + to;
				else
					return 'nvt';
			};

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);