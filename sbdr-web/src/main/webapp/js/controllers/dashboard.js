appcontrollers.controller('DashboardController',
    ['$window', '$route', '$routeParams', '$scope', '$rootScope', '$q', '$location',
        'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'UserService', 'InternalProcessService',
        function ($window, $route, $routeParams, $scope, $rootScope, $q, $location,
                  ngTableParams, DashboardNumberTags, DashboardService, UserService, InternalProcessService) {
            console.log("dashboardCtrl");

            if ($routeParams.showtab)
                var showtab = $routeParams.showtab;

			$scope.tabClicked = function(){
				delete $scope.error;
				delete $rootScope.error;
			};

            $scope.edit = function (editobject) {
                $location.path(editobject.htmlUrl);
            };

            $scope.fullreport = function () {
                $location.path("/fullreport");
                $location.url("/fullreport");
            };

            $scope.hasRole1 = function (roles, role) {
                return hasRole(roles, role);
            };

            $scope.messages = function () {
                $location.path("/messages");
            };

            $scope.nrOfItems = function (tab) {
            	var roles = null;
            	
            	if (typeof $window.sessionStorage.user !== 'undefined')
            		roles = JSON.parse($window.sessionStorage.user).roles;
            	
                if (tab == 'alerts') {
                    if (DashboardNumberTags.getNrOfAlerts() == -1) // initialize 1st time
                    {
                        DashboardNumberTags.setNrOfAlerts(0);
                        $scope.itemsPage = 20;
                        $scope.filterCriteriaAlerts = {
                            bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                            pageNumber: 1,
                            sortDir: "", // asc, desc
                            sortedBy: "",
                            filterValue: "" // text to filter on
                        };
                        $rootScope.range = "items=" + (($scope.filterCriteriaAlerts.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteriaAlerts.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                        //var companiesalertData = DashboardService.companiesalert($scope.filterCriteriaAlerts);
                        //
                        //var resultsPromise = $q.all({
                        //    companiesalertdata: companiesalertData.$promise
                        //}).then(function (data, headers) {
                        //    if (typeof data.alertOverviewTransfer != 'undefined') {
                        //        DashboardNumberTags.setNrOfAlerts(paging_totalItems(headers("Content-Range")));
                        //    }
                        //    else {
                        //        DashboardNumberTags.setNrOfAlerts(0);
                        //    }
                        //}).catch(function (error) {
                        //    console.log('Error fetching companiesalert');
                        //})

                        DashboardService.companiesalert($scope.filterCriteriaAlerts, function (data, headers) {
                            console.log("Companies fetched");
                            if (typeof data != 'undefined') {
                                DashboardNumberTags.setNrOfAlerts(paging_totalItems(headers("Content-Range")));
                            }
                            else {
                                DashboardNumberTags.setNrOfAlerts(0);
                            }

                        }, function () {
                            console.log("Error fetching companies");
                        });
                    }

                    return Math.min(99, DashboardNumberTags.getNrOfAlerts());
                } else if(tab=='internalprocessadmin' && roles != null && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'))){
					if(DashboardNumberTags.getNrOfProcesses()==-1){
						DashboardNumberTags.setNrOfProcesses(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=1-" + $scope.itemsPage;

						InternalProcessService.getInternalProcessRows(function(data, headers){
							if(data!=null){
								DashboardNumberTags.setNrOfProcesses(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfProcesses(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfProcesses());
                } else if(tab=='objectionsadmin' && roles != null && hasRole(roles, 'admin_sbdr_hoofd')){
					if(DashboardNumberTags.getNrOfObjectionsAdmin()==-1){
						DashboardNumberTags.setNrOfObjectionsAdmin(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=0-" + $scope.itemsPage;

						DashboardService.objectionsadmin($scope.filterCriteriaAlerts, function (data, headers) {
							if(data!=null){
								DashboardNumberTags.setNrOfObjectionsAdmin(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfObjectionsAdmin(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfObjectionsAdmin());
                } else if(tab=='supportticketsadmin' && roles != null && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'))){
					if(DashboardNumberTags.getNrOfSupportTicketsAdmin()==-1){
						DashboardNumberTags.setNrOfSupportTicketsAdmin(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=0-" + $scope.itemsPage;

						DashboardService.supportticketsadmin($scope.filterCriteriaAlerts, function (data, headers) {
							if(data!=null){
								DashboardNumberTags.setNrOfSupportTicketsAdmin(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfSupportTicketsAdmin(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfSupportTicketsAdmin());					
                } else
                    return 0; // default no items
            };

            $scope.searchCompany = function () {
                $location.path("/searchcompany");
            };
            
			$scope.notAllowedNotification = function() {
				if(!$rootScope.hasRoleUser('registraties_toegestaan')) {
					return 'Geen rechten om registraties in te bewerken';
				} else {
					return '';
				}
			};            

            $scope.showTab = function (tab) {
                if (typeof $window.sessionStorage.user != 'undefined') {
                    var roles = JSON.parse($window.sessionStorage.user).roles;

                    // admin_sbdr, admin_klant, hoofd_klant, gebruiker_klant
                    if (typeof roles != 'undefined') {
                        //if (tab == 'overview') {
                        //    if ($scope.showtab == 'overviewtab') {
                        //        $scope.tabs[0].select();
                        //        resetShowTab();
                        //    }
                        //    return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant');
                        //} else
                        if (tab == 'alerts') {
                            if ($scope.showtab == 'alertstab' || (typeof $scope.showtab === 'undefined' && !isUserTabSelected() && (hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager')))) {
                                $scope.tabs[0].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager'); 
                        } else if (tab == 'reportrequested') {
                            if ($scope.showtab == 'reportrequestedtab') {
                                $scope.tabs[1].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'monitoring') {
                            if ($scope.showtab == 'portfoliomonitoringtab') {
                                $scope.tabs[2].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'notifications') {
                            if ($scope.showtab == 'companiesnotifiedtab') {
                                $scope.tabs[3].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'removedcompanies') {
                            if ($scope.showtab == 'companiesremovedtab') {
                                $scope.tabs[4].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'generaladmin') {
                            if ($scope.showtab == 'generaladmintab') { // || (typeof $scope.showtab == 'undefined' && hasRole(roles, 'admin_sbdr')  && !isAdminTabSelected())) { // for showtab or isAdmin
                                $scope.tabs[5].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'objectionsadmin') {
                        	if ($scope.showtab =='objectionsadmintab') {
                                $scope.tabs[6].select();
                                resetShowTab();                        		
                        	}
                        	return hasRole(roles, 'admin_sbdr_hoofd');
                        } else if (tab == 'supportticketsadmin') {
                        	if ($scope.showtab =='supportticketsadmintab') {
                                $scope.tabs[7].select();
                                resetShowTab();                        		
                        	}
                        	return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
                    	} else if (tab == 'searchresultsadmin') {
                            if ($scope.showtab == 'searchresultsadmintab'  || (typeof $scope.showtab == 'undefined' && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd')) && !isAdminTabSelected())) { // || (hasRole(roles, 'admin_sbdr_hoofd') ) {
                                $scope.tabs[8].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'customersadmin') {
                            if ($scope.showtab == 'customersadmintab') {
                                $scope.tabs[9].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'notificationsadmin') {
                            if ($scope.showtab == 'notificationsadmintab') {
                                $scope.tabs[10].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'notificationsofprospectadmin') {
                            if ($scope.showtab == 'notificationsofprospectadmintab') {
                                $scope.tabs[11].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'exceptioncompaniesadmin') {
                            if ($scope.showtab == 'exceptioncompaniesadmintab') {
                                $scope.tabs[12].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if(tab=='internalprocessadmin'){
							if($scope.showTab == 'internalprocessadmin'){
								$scope.tabs[13].select();
								resetShowTab();
							}
							return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
						}
                    }
                }

                return false;
            };
            
            $scope.updateCompany = function(bedrijfId) {
            	UserService.userdataOfCompany({bedrijfId: bedrijfId },function(user) {
					$window.sessionStorage.user = JSON.stringify(user); 
					// change bedrijfId in authtoken to force from now on service calls for 'the other' company
					if (typeof $window.sessionStorage.authToken !== 'undefined') {
						var res = $window.sessionStorage.authToken.split(":");
						if (res.length == 5) {
							res[2] = user.bedrijfId;
							 $window.sessionStorage.authToken = res[0] + ":" + res[1] + ":" + res[2] + ":" + res[3] + ":" + res[4];
						}
					}

					$route.reload();
            	});
            }

            isAdminTabSelected = function () {
                return $scope.tabs[5].selected || $scope.tabs[6].selected || $scope.tabs[7].selected || $scope.tabs[8].selected || $scope.tabs[9].selected || $scope.tabs[10].selected || $scope.tabs[11].selected || $scope.tabs[12].selected || $scope.tabs[13].selected ;
            };
            
            isUserTabSelected = function() {
            	return $scope.tabs[0].selected || $scope.tabs[1].selected || $scope.tabs[2].selected || $scope.tabs[3].selected || $scope.tabs[4].selected; 
            }

            resetShowTab = function () {
                //delete showtab;
                delete $scope.showtab;
                //$location.path("/dashboard");
                //$location.url("/dashboard");
                $rootScope.changeUrlWithoutReload("/dashboard");
            };
            
            resetAllCompaniesOfUser = function() { 
	            $scope.gebruikerBedrijfId = null;
	        	$scope.gebruikerBedrijven = null;
	        	if (typeof $window.sessionStorage.user !== 'undefined')
	        		$scope.gebruikerBedrijfId =  JSON.parse($window.sessionStorage.user).bedrijfId;
	        	
	        	var allCompaniesOfUserCall = DashboardService.allCompaniesOfUser({bedrijfId : $scope.gebruikerBedrijfId});
	        	
	        	// default
	        	$scope.aantalGebruikerBedrijven = 1;
	        	
				var resultsPromise2 = $q.all({
					allCompaniesOfUser: allCompaniesOfUserCall.$promise
				}).then(function(data) {
					if (typeof data !== 'undefined' && data.allCompaniesOfUser !== 'undefined') {	
						$scope.gebruikerBedrijven = data.allCompaniesOfUser;
						delete $scope.gebruikerBedrijfId;
						$scope.aantalGebruikerBedrijven = $scope.gebruikerBedrijven.length;
						for (var i = 0; i < $scope.aantalGebruikerBedrijven; i++) {
						    if (data.allCompaniesOfUser[i].currentBedrijf)
						    	$scope.gebruikerBedrijfId = data.allCompaniesOfUser[i].bedrijfId;
						}
						if (typeof $scope.gebruikerBedrijfId === 'undefined')
							$scope.gebruikerBedrijfId = data.allCompaniesOfUser[0].bedrijfId;
						
						return data;
					} else
						return 'unknown';
				}).catch(function(error) {
					console.log('Error fetching allCompaniesOfUser');
					$scope.error = error;
				});
            }
            
            if (typeof $scope.initDashboard === 'undefined') {
            	resetAllCompaniesOfUser();
            	
            	$scope.initDashboard = true;
            }

            if (showtab) {           	           	
                //$scope.tabs[showtab].select();
                //$scope.select(showtab);
                $scope.showtab = showtab;
                showtab = null;
            }
        }]);