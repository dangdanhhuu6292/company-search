appcontrollers.controller('MonitoringQuestionController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', '$modal', 'CompanyService',
		function($window, $routeParams, $scope, $rootScope,
				 $location, $modal, CompanyService) {

			var bedrijfIdentifier;

			if($scope.init == undefined) {
				$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});

				$scope.init = true;
			}

			if(!$routeParams.bedrijfId || $routeParams.bedrijfId == null || $routeParams.bedrijfId == undefined) {
				bedrijfIdentifier = null;
			} else {
				bedrijfIdentifier = $routeParams.bedrijfId;
			}

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}

			$scope.kvkNummer = $routeParams.kvkNummer;
			$scope.kvkNummerPrefix = $routeParams.kvkNummer;
			$scope.hoofd = $routeParams.hoofd;
			$scope.bedrijfNaam = decodeURIComponent($routeParams.bedrijfNaam);
			$scope.bekendBijSbdr = "true"; //$routeParams.bekendBijSbdr;
			$scope.monitoringBijBedrijf = $routeParams.monitoringBijBedrijf;
			$scope.adres = decodeURIComponent($routeParams.adres);

			if(typeof $scope.kvkNummerPrefix !== 'undefined' && $scope.kvkNummerPrefix != null && $scope.kvkNummerPrefix.length == 12) {
				$scope.kvkNummerPrefix = $scope.kvkNummerPrefix.substring(0, 8);
			}

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function(){
					$modalInstance.close();
				}
			}];

			var reportPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doReport = false;

				CompanyService.getTariefOfProduct({ProductCode: 'RAP'}, function(result) {
					if(result.errorCode == undefined) {
						$scope.reportCost = result.tarief;
					}
				});

				$scope.createReport = function() {
					$scope.doReport = true;
					$scope.closeReportPopup();
				};

				$scope.closeReportPopup = function() {
					var result = {
						doReport : $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			$scope.returnToSearch = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			callMonitoring = function(request) {
				CompanyService.monitoringCompany(request, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					} else {
						$scope.monitorCreated = true;
					}
				});
			};

			callReport = function(bedrijfIds) {
				$location.path('/report/' + bedrijfIds.bedrijfIdGerapporteerd + '/' + bedrijfIds.bedrijfId + '/' + '0' + '/' + searchurl);
				$location.url($location.path());
			};

			$scope.gotoReport = function() {
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
						if(false){ // !$scope.userCanPay turned off
							var reportNoPaymentModal = $modal.open({
								templateUrl: 'reportNoPayment.html',
								controller: reportNoPaymentController,
								size:'lg'
							});
						} else {
							delete $scope.monitoringSuccessful;

							//if (typeof bedrijfIdentifier == 'undefined' || bedrijfIdentifier == null) {
							CompanyService.getOrCreateCompanyData({
								kvknumber: $scope.kvkNummer,
								hoofd    : $scope.hoofd
							}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								}
								else {
									if(bedrijfIdentifier == "undefined" || bedrijfIdentifier == null) {
										//monitoringdata
										bedrijfIds = {
											bedrijfId             : result.bedrijfId,
											bedrijfIdGerapporteerd: JSON.parse($window.sessionStorage.user).bedrijfId
										};
										//callMonitoring(monitoringdata);
										//$scope.monitoringSuccessful = true;
										callReport(bedrijfIds);
									} else { // bedrijf already known
										bedrijfIds = {
											bedrijfId             : bedrijfIdentifier,
											bedrijfIdGerapporteerd: JSON.parse($window.sessionStorage.user).bedrijfId
										};
										callReport(bedrijfIds);
									}
								}
							});
						}
				//	}
				//});
			};
		}]);