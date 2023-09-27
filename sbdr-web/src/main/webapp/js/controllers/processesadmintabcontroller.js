appcontrollers.controller('ProcessesAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$filter',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'InternalProcessService',
		function ($window, $modal, $scope, $rootScope, $location, $filter,
				  $timeout, ngTableParams, DashboardNumberTags, DashboardService, InternalProcessService) {

			$scope.title = "Internal processes Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.createBatchDocument = function(processRowId){
				delete $scope.error;
				return {
					action: 'letter_batch',
					batchDocumentId: processRowId
				};
			};

			$scope.fetchData = function () {
				$scope.processes = [];
				
				$rootScope.range = "items=" + (($scope.currentPage - 1) * $scope.itemsPage + 1) + "-" + (($scope.currentPage - 1) * $scope.itemsPage + $scope.itemsPage);

				InternalProcessService.getInternalProcessRows(function (data, headers) {
					if (data.errorCode != null) {
						$scope.error = data.errorCode + ' ' + data.errorMsg;
					} else {
						if(data.length>0){
							$scope.processes = data;
							$scope.totalItems = paging_totalItems(headers("Content-Range"));

							$scope.noData = false;
						} else {
							$scope.totalItems = 0;
							$scope.noData = true;
						}
					}
					DashboardNumberTags.setNrOfProcesses($scope.totalItems);
				});
			};

			var promptProcessLetterController = ['$scope', '$modalInstance', 'briefOrBatch', function ($scope, $modalInstance, briefOrBatch) {
				$scope.continue = false;
				$scope.briefOrBatch = briefOrBatch;

				$scope.sendLetter = function () {
					$scope.continue = true;
					$scope.closeLetterPrompt();
				};

				$scope.closeLetterPrompt = function () {
					var result = {continue: $scope.continue};
					$modalInstance.close(result);
				};
			}];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfProcesses() > 0;
			};
			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				delete $scope.error;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchData(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.fetchData();

			$scope.promptSendBatch = function(id){
				delete $scope.error;
				var modalInstance = $modal.open({
					templateUrl: 'processLetter.html',
					controller: promptProcessLetterController,
					size:'lg',
					resolve:{briefOrBatch:function(){return 'batch';}}
				});

				modalInstance.result.then(function(result){
					if(result.continue){
						processRow(id);
					}
				})
			};

			$scope.promptSendLetter = function (id) {
				delete $scope.error;
				var modalInstance = $modal.open({
					templateUrl: 'processLetter.html',
					controller: promptProcessLetterController,
					size: 'lg',
					resolve:{briefOrBatch:function(){return 'brief';}}
				});

				modalInstance.result.then(function (result) {
					if (result.continue) {
						processRow(id);
					}
				});
			};

			processRow = function (id) {
				InternalProcessService.setProcessRowAsSent({processRowId: id, gebruikerId: JSON.parse($window.sessionStorage.user).userId}, function (data) {
					if (data.errorCode != null) {
						$scope.error = data.errorCode + ' ' + data.errorMsg;
					} else {
						$scope.fetchData();
					}
				})
			};
		}]);