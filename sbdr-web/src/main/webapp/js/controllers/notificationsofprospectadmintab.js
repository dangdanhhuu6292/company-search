appcontrollers.controller('NotificationsOfProspectAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', 'maxFieldLengths',
		'$filter', '$timeout', 'ngTableParams', 'DashboardNumberTags',
		'DashboardService', 'CompanyService', 'InternalProcessService',
		function ($window, $modal, $scope, $rootScope, $location, maxFieldLengths,
				  $filter, $timeout, ngTableParams, DashboardNumberTags,
				  DashboardService, CompanyService, InternalProcessService) {
			console.log("init 1");
			$scope.title = "Meldingen of Prospect Tab";

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

			$scope.createDocumentRequested = function (meldingId) {
				return {action: 'letter_notification', bedrijfId: 0, meldingId: meldingId, referentie: ''};
			};

			console.log("init 2");

			$scope.detailsCompany = function (bedrijfId) {
				$location.path('/editcompany/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab');
				$location.url($location.path());
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

			var notifications = [];

			cleanUp = function () {
				delete $scope.error;
				delete $scope.notificationRemoved;
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfNotificationsAdmin() > 0;
			};

			$scope.createNewNotificationLetter = function (meldingId) {
				cleanUp();
				CompanyService.createNewNotificationLetter({meldingId: meldingId}, function (result) {
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
						$scope.letterCreatedOk = false;
					}
					else {
						$scope.letterCreatedOk = true;
					}
				});
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
				cleanUp();

				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.notificationsofprospectadmin($scope.filterCriteria, function (data, headers) {
					console.log("Meldingen fetched");
					if (typeof data != 'undefined') {
						$scope.notifications = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.notifications = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);

					success();
				}, function () {
					console.log("Error fetching meldingen");
					$scope.notifications = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);
				});

			};
			console.log("init 5");

			$scope.isSbdrHoofd = function () {
				return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd');
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

			$scope.requestreport = function (bedrijfId) {
				$location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/$dashboard$notificationsofprospectadmintab');
			};

			doRemoveNotification = function (notificationId, companyId, reason) {
				cleanUp();

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
					controller: RemoveNotificationAdminController,
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

			$scope.holdNotificationCompany = function (notificationId, companyId) {
				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				};

				CompanyService.holdNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.notificationOnHoldSavedOk = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					console.log("ERROR!!!");
				});
			};

			$scope.removeHoldNotificationCompany = function (notificationId, companyId) {
				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				};

				CompanyService.removeHoldNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.notificationOnHoldSavedOk = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					console.log("ERROR!!!");
				});
			};
			
			// Extended with bedrijfDoorId, was 'own company' = admin
			$scope.notificationChange = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab/' + meldingId);
			};

			// Extended with bedrijfDoorId, was 'own company' = admin
			$scope.notificationReadOnly = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab/' + meldingId + '/true');
			};			

			$scope.printNotificationLetter = function (meldingId, arrayIndex) {
				if (!$scope.notifications[arrayIndex].briefGedownload) {
					InternalProcessService.printNotificationLetter({meldingId: meldingId}, function (result) {
						if (result.errorCode != null) {
							$scope.error = result.errorCode + ' ' + result.errorMsg;
						} else {
							$scope.notifications[arrayIndex].briefGedownload = true;
						}
					});
				}
			};

			// fetch initial data for 1st time
			$scope.filterResult();

			var RemoveNotificationAdminController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.remove = true;
					$scope.reason = reason;
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
		}]);