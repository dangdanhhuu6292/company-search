appcontrollers.controller('PortfolioMonitoringTabController',
	['$window', '$modal', '$scope', '$rootScope', 'maxFieldLengths',
		'$location', '$filter', '$timeout', 'ngTableParams',
		'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, maxFieldLengths,
				  $location, $filter, $timeout, ngTableParams,
				  DashboardNumberTags, DashboardService, CompanyService) {

			$scope.title = "Portfolio Monitoring Tab";

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

			$scope.documentRequested = {
				action: 'monitored_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
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

				$scope.sortedOrder = true;
				$scope.reverseSort = true;
				$scope.orderByField = 'aantalMeldingen';

				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: 'DESC', // asc, desc
					sortedBy: 'aantalMeldingen',
					filterValue: '' // text to filter on
				};
			}
			console.log("init 3");

			$scope.nrOfItems = function () {
				return 7;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfPortfolio() > 0;
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
				DashboardService.portfoliomonitoring($scope.filterCriteria, function (data, headers) {
					console.log("Companies fetched");
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfPortfolio($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					console.log("Error fetching companies");
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfPortfolio($scope.totalItems);
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
			
			$scope.gebruikerRegistratiesAllowed = function() {
				if (typeof $window.sessionStorage.user !== 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;
	
					return hasRole(roles, 'registraties_toegestaan') || hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
				} else
					return false;
			};

			$scope.notifyCompany = function (bedrijfId) {
				console.log("notifyCompany function");

				if (!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO))
					$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/$dashboard$portfoliomonitoringtab');
				else
					$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/$dashboard$portfoliomonitoringtab');
			};

			$scope.monitoringDetail = function (monitoringId) {
				$location.path('/monitoringdetails/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + monitoringId + '/$dashboard$portfoliomonitoringtab');
			};

			removeMonitoring = function (monitoringId, bedrijfId) {
				CompanyService.removeMonitoringCompany({
					monitoringId: monitoringId,
					bedrijfId: bedrijfId
				}, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.removeFromMonitoring = function (monitoringId, bedrijfId) {

				var modalInstance = $modal.open({
					templateUrl: 'removemonitoring.html',
					controller: RemoveMonitoringController,
					size: 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function (result) {
					if (result.remove)
						removeMonitoring(monitoringId, bedrijfId);
				}, function () {
					console.log('Modal dismissed at: ' + new Date());
				});
			};

			$scope.fetchMonitoring = function(field){
				$scope.orderByField = field;
				$scope.reverseSort = !$scope.reverseSort;

				if ($scope.filterCriteria.sortDir == 'ASC')
					$scope.filterCriteria.sortDir = 'DESC';
				else
					$scope.filterCriteria.sortDir = 'ASC';

				$scope.filterCriteria.sortedBy = $scope.orderByField;
				$scope.filterCriteria.sortderOrder = $scope.reverseSort;

				$scope.currentPage = 1;
				$scope.pageChanged();
			};

			var RemoveMonitoringController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeMonitoringOk = function () {
					$scope.remove = true;
					$scope.closeRemoveMonitoringModal();
				};

				$scope.closeRemoveMonitoringModal = function () {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);