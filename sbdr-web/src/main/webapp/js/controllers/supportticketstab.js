appcontrollers.controller('AlertsTabController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', '$timeout', 'maxFieldLengths', 'ngTableParams',
        'DashboardNumberTags', 'DashboardService', 'AccountService', 'CompanyService',
        function ($window, $scope, $rootScope, $location, $filter, $timeout, maxFieldLengths, ngTableParams,
                  DashboardNumberTags, DashboardService, AccountService, CompanyService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            $scope.title = "Alerts Tab";

            AccountService.getCompanyAccountData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId}, function (data) {
                $scope.account = data;
            });

            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPage = 20;
            $scope.maxSize = 5;
            $scope.thisUser = JSON.parse($window.sessionStorage.user);

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

            // The function that is responsible of fetching the result from the
            // server and setting the grid to the new result
            $scope.fetchResult = function (success) {
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                DashboardService.companiesalert($scope.filterCriteria, function (data, headers) {
                    if (typeof data != 'undefined' && data.length > 0) {
                        if (hasRole($scope.thisUser.roles, 'admin_sbdr') || hasRole($scope.thisUser.roles, 'admin_sbdr_hoofd')) {
                            $scope.adminAlertsCollection = data;
                        } else {
                            $scope.alertsCollection = data;
                        }

                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    }
                    else {
                        $scope.adminAlertsCollection = null;
                        $scope.alertsCollection = null;
                        $scope.totalItems = 0;
                    }
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);

                    success();
                }, function () {
                    $scope.adminAlertsCollection = [];
                    $scope.alertsCollection = [];
                    $scope.totalItems = 0;
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);
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
                return DashboardNumberTags.getNrOfAlerts() > 0;
            };

            $scope.ignorealert = function (type, alertId, bedrijfId) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                } else {
                    deleteAlert(alertId);
                }
            };

            $scope.pageChanged = function () {
                $scope.filterCriteria.pageNumber = $scope.currentPage;
                $scope.fetchResult(function () {
                    //Nothing to do..

                    // rootscope range delete
                    delete $rootScope.range;
                });
            };

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

            //$scope.requestreport = function (bedrijfId, monitoringBedrijfId, indMonitoringBedrijf) {
            //    CompanyService.updateMonitoring({
            //        vanBedrijfId: monitoringBedrijfId,
            //        doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
            //    }, function (result) {
            //        if (typeof result.errorCode !== "undefined")
            //            $scope.error = result.errorCode + " " + result.errorMsg;
            //        else {
            //            searchurl = '$dashboard$alertstab';
            //            $location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/' + searchurl);
            //        }
            //    });
            //};

			$scope.viewAdminDetails = function(refNoPRefix){
				supportAlertDetail(refNoPRefix);
			};

            $scope.viewdetails = function (type, alertId, monitoringId, meldingId, supportId, bedrijfId, refNoPrefix) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                    monitoringDetail(monitoringId);
                }
                else if (type == 'VER') {
                    deleteAlert(alertId);
                    notificationReadOnly(meldingId, bedrijfId);
                }
                else if (type == 'SUP') {
					deleteAlert(alertId);
                    supportAlertDetail(refNoPrefix);
                }
            };

			deleteAlert = function (alertId) {
				// remove alert record
				CompanyService.deleteAlert(alertId, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			monitoringDetail = function (monitoringId) {
				delete $scope.error;
				$location.path('/monitoringdetails/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + monitoringId + '/$dashboard$alertstab');
				$location.url($location.path());
			};

			notificationReadOnly = function (meldingId, bedrijfId) {
				delete $scope.error;
				console.log("notifyCompany function");

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$alertstab/' + meldingId + '/true');
				$location.url($location.path());
			};

			supportAlertDetail = function (refNoPrefix) {
				delete $scope.error;
				$location.path('/support/detail/' + refNoPrefix+'/$dashboard$supportticketstab');
				$location.url($location.path());
			};

			updateMonitoring = function (bedrijfId) {
				// update monitoring
				CompanyService.updateMonitoring({
					vanBedrijfId: bedrijfId,
					doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
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
			
			$scope.lowercaseFirstLetter = function (string) {
			    return string.charAt(0).toLowerCase() + string.slice(1);
			};

			// fetch initial data for 1st time
			$scope.filterResult();
        }]);