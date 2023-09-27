appcontrollers.controller('ReportRequestedTabController',
	['$window', '$modal', '$scope', '$rootScope', 'maxFieldLengths',
		'$location', '$filter', '$timeout', 'ngTableParams',
		'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, maxFieldLengths,
				  $location, $filter, $timeout, ngTableParams,
				  DashboardNumberTags, DashboardService, CompanyService) {
			console.log("init 1");
			
			$scope.title = "Report Requested Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				console.log('Page changed to: ' + $scope.currentPage);
			};

			console.log("init 2");

			$scope.createDocumentRequested = function (bedrijfId, referentieNummer) {
				return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			};

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

			if (typeof $scope.filterCriteria == 'undefined') {

				$scope.filterCriteria = {
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};
			}
			console.log("init 3");

			$scope.nrOfItems = function () {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfReportRequested() > 0;
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
			console.log("init 4");

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.reportrequested($scope.filterCriteria, function (data, headers) {
					console.log("ReportRequested fetched");
					if (typeof data != 'undefined') {
						$scope.reports = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.reports = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfReportRequested($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					console.log("Error fetching reportRequested");
					$scope.reports = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfReportRequested($scope.totalItems);
					$scope.firstfetch = true;
				});

			};
			console.log("init 5");

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

			$scope.documentRequested = {
				action: 'reported_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			//$scope.documentRequested = function(bedrijfId, referentieNummer) {
			//  return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			//}

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);