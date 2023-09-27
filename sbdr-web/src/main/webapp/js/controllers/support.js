appcontrollers.controller('SupportController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', 'SupportService', 'CompanyService', 
        function ($window, $scope, $rootScope, $location, $filter, SupportService, CompanyService) {
            console.log("SupportController");

            $scope.totalItems = 0;
            $scope.userSupportTickets = [];
            $scope.currentPage = 1;
            $scope.itemsPage = 10;
            $scope.maxSize = 5;

			CompanyService.companyHasNotifications({gebruikerId: JSON.parse($window.sessionStorage.user).userId}, function(result){
				delete $scope.error;
				if(result.errorCode != null){
					$scope.error = result.errorCode + " " + result.errorMsg;
				} else {
					$scope.hasNotificationsToObject = result.val;
				}
			});

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

			$scope.pageChanged = function () {
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.getSupportTicketsOfUser(function () {
					delete $rootScope.range;
				});
			};

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    gebruikerId: JSON.parse($window.sessionStorage.user).userId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: ""
                };
            }
			$scope.filterResult = function () {
				return $scope.getSupportTicketsOfUser(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;

				});
			};

            //Navigational functions
            $scope.gotoFAQ = function () {
                $location.path('/support/faq');
                $location.url($location.path());
            };

            $scope.gotoProblem = function () {
                $location.path('/support/problem');
                $location.url($location.path());
            };

            $scope.gotoObjection = function () {
                $location.path('/support/objection');
                $location.url($location.path());
            };

            $scope.gotoDetails = function (refNo) {
                $location.path('/support/detail/' + refNo+'/$support');
                $location.url($location.path());
            };

            $scope.getSupportTicketsOfUser = function () {
            	delete $scope.error;
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
                SupportService.findsupportticketlistforuser($scope.filterCriteria, function (data, headers) {
                    if ( data != undefined && data.length > 0) {
                        $scope.userSupportTickets = data;
                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    } else {
                        $scope.userSupportTickets = [];
                        $scope.totalItems = 0;
                    }
                }, function () {
                    console.log("Error fetching supporttickets of user");
                    $scope.error = "Fout bij ophalen supporttickets";
                    $scope.userSupportTickets = [];
                    $scope.totalItems = 0;
                });
            };

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

            $scope.getSupportTicketsOfUser();
        }]);