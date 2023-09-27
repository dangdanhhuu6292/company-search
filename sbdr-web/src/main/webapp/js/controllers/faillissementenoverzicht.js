appcontrollers.controller('FaillissementenOverzichtController',
	['$window', '$modal', '$scope', '$rootScope', '$location',
		'$anchorScroll', '$routeParams', '$cookies', 'AccountService',
		function ($window, $modal, $scope, $rootScope, $location,
				  $anchorScroll, $routeParams, $cookies, AccountService) {
			console.log("faillissementenOverzichtController");

			$scope.userIdSelected = null;
			$scope.setSelected = function (userId) {
				$scope.userIdSelected = userId;
			};

			delete $scope.error;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				console.log('Page changed to: ' + $scope.currentPage);
			};

			if (typeof $scope.init === "undefined") {

				$scope.filterCriteria = {
					userId: JSON.parse($window.sessionStorage.user).userId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20;
				$scope.maxSize = 5;

				var users = [];

				AccountService.getFaillissementStats(function (result) {
					if (result.errorCode != null)
						$scope.error = result.errorCode + " " + result.errorMsg;
					else
						$scope.stats = result;
				});

				$scope.init = true;
			}

			$scope.showKvKNumber = function(kvkNr){
				var text;
				if(kvkNr==null||typeof kvkNr== 'undefined'){
					text = 'Natuurlijk persoon';
				} else {
					text = kvkNr;
				}

				return text;
			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// selected userId delete
					$scope.userIdSelected = null;
					// rootscope range delete
					delete $scope.range;
				});
			};
			console.log("init 4");

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				AccountService.faillissementenafgelopenweek($scope.filterCriteria, function (data, headers) {
					console.log("Faillissementen fetched");

					$scope.faillissementen = data;

					$scope.totalItems = paging_totalItems(headers("Content-Range"));
					success();
				}, function () {
					console.log("Error fetching faillissementen");
					$scope.faillissementen = [];
					$scope.totalItems = 0;
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

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);