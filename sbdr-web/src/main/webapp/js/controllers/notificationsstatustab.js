appcontrollers.controller('NotificationsStatusTabController', [
	'$scope', '$rootScope', '$filter', 'maxFieldLengths', 'ngTableParams', 'DashboardService',
	function($scope, $rootScope, $filter, maxFieldLengths, ngTableParams, DashboardService) {
		console.log("init 1");

		$scope.title = "Notifications Status Tab";

		$scope.maxFieldLengths = maxFieldLengths;
		
		$scope.totalItems = 0;
		$scope.currentPage = 1;
		$scope.itemsPage = 20;
		$scope.maxSize = 5;

		$scope.setPage = function(pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			console.log('Page changed to: ' + $scope.currentPage);
		};

		console.log("init 2");

		$scope.filterCriteria = {
			pageNumber : 1,
			sortDir    : "", // asc, desc
			sortedBy   : "",
			filterValue: "" // text to filter on
		};
		console.log("init 3");

		$scope.nrOfItems = function() {
			return 0;
		};

		var notifications = [];

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
		console.log("init 4");

		// The function that is responsible of fetching the result from the
		// server and setting the grid to the new result
		$scope.fetchResult = function(success) {
			$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
			DashboardService.notificationsstatus($scope.filterCriteria, function(data, headers) {
				console.log("Companies fetched");
				$scope.notifications = data.notificationStatus;
				$scope.totalItems = paging_totalItems(headers("Content-Range"));
				success();
			}, function() {
				console.log("Error fetching companies");
				$scope.notifications = [];
				$scope.totalItems = 0;
			});

		};
		console.log("init 5");

		// Pagination functions
		$scope.setPage = function(pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			console.log('Page changed to: ' + $scope.currentPage);
			$scope.filterCriteria.pageNumber = $scope.currentPage;
			$scope.fetchResult(function() {
				//Nothing to do..

				// rootscope range delete
				delete $rootScope.range;
			});
		};

		// fetch initial data for 1st time
		$scope.filterResult();
	}]);