appcontrollers.controller('ReportController',
		['$window', '$modal', '$route', '$routeParams', '$scope', '$rootScope',
		'$location', '$anchorScroll', '$filter', '$q', 'reportData',
		'monitorRequestData', 'ReportDataStorage',
		'CompanyService', 'DocumentService', 'cfpLoadingBar', 
		function($window, $modal, $route, $routeParams, $scope, $rootScope,
				 $location, $anchorScroll, $filter, $q, reportData,
				 monitorRequestData, ReportDataStorage,
				 CompanyService, DocumentService, cfpLoadingBar)			 {
			
			$scope.start = function() {
				cfpLoadingBar.start();
			};
			
			$scope.complete = function() {
				cfpLoadingBar.complete();
			};

			// report
			if($scope.init == undefined) {
				$scope.hideMonitorPopup = true; //!$rootScope.isShowHelp($rootScope.HELP_MONITORINGPURCHASE);

				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});

				if($routeParams.searchurl) {
					searchurl = $routeParams.searchurl;
				} else {
					searchurl = '$dashboard';
				}

				CompanyService.bedrijfHasMonitor({doorBedrijfId:JSON.parse($window.sessionStorage.user).bedrijfId, overBedrijfId:reportData.bedrijf.bedrijfId}, function(result){
					if(typeof result.errorCode != 'undefined'){
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.monitoringBijBedrijf = result.val;
					}
				});

				//if(typeof monitoringBijBedrijf != 'undefined') {
				//	$scope.monitoringBijBedrijf = monitoringBijBedrijf;
				//} else {
				//	$scope.monitoringBijBedrijf = false;
				//}

				if($scope.report && $scope.report.meldingen) {
					//if (Object.prototype.toString.call( $scope.report.meldingen ) === '[object Array]')
					$scope.meldingenOverview = $scope.report.meldingen;
					//else
					//	$scope.meldingenOverview = [$scope.report.meldingen];
				} else {
					delete $scope.meldingenOverview;
				}

				$scope.documentRequested = {
					action    : 'report',
					bedrijfId : reportData.bedrijf.bedrijfId,
					meldingId : 0,
					referentie: reportData.referentieNummer
				};

				$scope.report = reportData;

				ReportDataStorage.setReportData($scope.report);
				
				if ($scope.report.ratingScore > 0) {
					$scope.showRating = true;
					$scope.ratingScoreIndicatorMessage = $scope.report.ratingScoreIndicatorMessage;
					
					if ($scope.report.ratingScore <= 30)
						$scope.ratingDanger = "ratingDangerSelected";
					else
						$scope.ratingDanger = "ratingDanger";
					if ($scope.report.ratingScore > 30 && $scope.report.ratingScore <= 45)
						$scope.ratingWarning = "ratingWarningSelected";
					else
						$scope.ratingWarning = "ratingWarning";
					if ($scope.report.ratingScore > 45 && $scope.report.ratingScore <= 79)
						$scope.ratingGood = "ratingGoodSelected";
					else
						$scope.ratingGood = "ratingGood";
					if ($scope.report.ratingScore > 79 && $scope.report.ratingScore <= 100)
						$scope.ratingVeryGood = "ratingVeryGoodSelected";
					else
						$scope.ratingVeryGood = "ratingVeryGood";
				}
				else
					$scope.showRating = false;
				

				//CompanyService.search({bedrijfId: userBedrijfId, pageNumber: 1,searchValue: $scope.report.kvkDossierTransfer.parentKvKNummer, city: ""}, function(result){
				//	if(result.errorCode!=null){
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	} else {
				//		$scope.parent = result[0];
				//	}
				//});
				//
				//CompanyService.search({bedrijfId: userBedrijfId, pageNumber: 1,searchValue: $scope.report.kvkDossierTransfer.ultimateParentKvKNummer, city: ""}, function(result){
				//	if(result.errorCode!=null){
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	} else {
				//		$scope.ultimateParent = result[0];
				//	}
				//});
				
				document.getElementById("rating").innerHTML=$scope.report.ratingScore

				$scope.init = true;
			}

			var NoMonitoringController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;
				$scope.monitoring = false;
				$scope.closeReport = false;

				$scope.noMonitoringOk = function() {
					$scope.monitoring = false;
					$scope.closeReport = true;
					$scope.closeNoMonitoringModal();
				};

				$scope.closeNoMonitoringModal = function() {
					var result = {
						monitoring : $scope.monitoring,
						closeReport: $scope.closeReport

					};

					$modalInstance.close(result); // $scope.remove
				};

				$scope.closeAndMonitoringOk = function() {
					$scope.monitoring = true;
					$scope.closeReport = true;
					$scope.closeNoMonitoringModal();
				}
			}];

			var monitorNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeMonitorPopup = function() {
					$modalInstance.close();
				}
			}];

			var monitorPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doMonitor = false;

				//CompanyService.getTariefOfProduct({ProductCode: 'MON'}, function(result) {
				//	if(result.errorCode == undefined) {
				//		$scope.monitorCost = result.tarief;
				//	}
				//});

				$scope.createMonitor = function() {
					$scope.doMonitor = true;
					$scope.closeMonitorPopup();
				};

				$scope.closeMonitorPopup = function() {
					var result = {
						doMonitor: $scope.doMonitor
					};

					$modalInstance.close(result);
				}
			}];

			////if (typeof reportData.opmerkingen !== 'undefined' && Object.prototype.toString.call( reportData.opmerkingen ) !== '[object Array]')
			//reportData.opmerkingen = reportData.opmerkingen;
			////if (typeof reportData.historie !== 'undefined' && Object.prototype.toString.call( reportData.historie ) !== '[object Array]')
			//reportData.historie = reportData.historie;
			////if (typeof reportData.meldingen !== 'undefined' && Object.prototype.toString.call( reportData.meldingen ) !== '[object Array]')
			//reportData.meldingen = reportData.meldingen;
			////if (typeof reportData.reportsLastTwoWeeks !== 'undefined' && Object.prototype.toString.call( reportData.reportsLastTwoWeeks ) !== '[object Array]')
			//reportData.reportsLastTwoWeeks = reportData.reportsLastTwoWeeks;
			////if (typeof reportData.meldingenLastYear !== 'undefined' && Object.prototype.toString.call( reportData.meldingenLastYear ) !== '[object Array]')
			//reportData.meldingenLastYear = reportData.meldingenLastYear;

			$scope.aantalMeldingen = function() {
				return $scope.report.aantalMeldingenActief;
			};

//			$scope.aantalMeldingenResolved = function() {
//				return $scope.report.aantalMeldingenResolved;
//			};

			$scope.back = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			$scope.monitoring = function(goback) {
				var monitorModal = $modal.open({
					templateUrl: 'monitorPopup.html',
					controller : monitorPopupController,
					size       : 'lg'
				});

				monitorModal.result.then(function(result) {
					if(result.doMonitor) {
						if(false) { // !$scope.userCanPay turned off
							var monitorNoPaymentModal = $modal.open({
								templateUrl: 'monitorNoPayment.html',
								controller : monitorNoPaymentController,
								size       : 'lg'
							});

							monitorNoPaymentModal.result.then(function(result) {
								if(typeof goback !== 'undefined' && goback) {
									$scope.back();
								}
							})
						} else {
							makeMonitor();

							if(typeof goback !== 'undefined' && goback) {
								$scope.back();
							}
						}
					}
				});
			};

			$scope.backWithNoMonitoringQuestion = function() {

				if($scope.showMonitoring()) {
					var modalInstance = $modal.open({
						templateUrl: 'nomonitoring.html',
						controller : NoMonitoringController,
						size       : 'lg'
					});

					modalInstance.result.then(function(result) {
						//dummy TESTSTS
						//$scope.hideMonitorPopup = false;
						//$scope.userCanPay = true;

						if(result.monitoring) {
							$scope.monitoring(result.closeReport);
						} else if(result.closeReport) {
							$scope.back();
						}

					}, function() {
						console.log('Modal dismissed at: ' + new Date());
					});
				} else {
					$scope.back();
				}
			};

			$scope.bedragOpenstaand = function() {
				if(typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null) {
					return '-';
				} else {
					return $filter('currency')($scope.report.bedragOpenstaand);
				} //'\u20AC  ' + $scope.report.bedragOpenstaand | currency;
			};

			$scope.bedragResolved = function() {
				if(typeof $scope.report.bedragResolved == 'undefined' || $scope.report.bedragResolved == null) {
					return '-';
				} else {
					return $filter('currency')($scope.report.bedragResolved);
				}
			};

			$scope.getBedragText = function(bedrag) {
				if(typeof bedrag == 'undefined' || bedrag == null || bedrag == 0) {
					return 'hoger dan \u20AC 100,-';
				} else {
					return '\u20AC ' + bedrag.toLocaleString('nl-NL', {style: 'currency', currency: 'EUR'});
				}//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.getReport = function(referentie) {
				$scope.start();
				DocumentService.report({referentie: referentie}, function(result) {
					//var file = new Blob([result], { type: 'application/pdf' });
					//var fileURL = URL.createObjectURL(file);
					//window.open(fileURL);
					
					$scope.$emit('downloaded', result);

					$scope.complete();
				});
			};

			$scope.hideCompanyInfo = function(bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.isBedragSuccess = function() {
				return !!(typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null);
			};

			$scope.isBedragWarning = function() {
				return !!(typeof $scope.report.bedragOpenstaand != 'undefined' && $scope.report.bedragOpenstaand != null && $scope.report.bedragOpenstaand > 0);
			};

			$scope.isStatusDanger = function() {
				return !!($scope.report.kvkDossierTransfer.actief == 'Niet actief' || $scope.report.kvkDossierTransfer.actief == '' ||
				$scope.report.kvkDossierTransfer.actief == 'Ontbonden' ||
				$scope.report.kvkDossierTransfer.actief == 'Opgeheven');
			};

			$scope.isStatusSuccess = function() {
				return $scope.report.kvkDossierTransfer.actief == 'Actief';
			};

			$scope.isStatusWarning = function() {
				return $scope.report.kvkDossierTransfer.actief == 'Uitgeschreven';
			};

			$scope.isFaillietSurceanceDanger = function() {
				return false;
			};

			$scope.isFaillietSurceanceSuccess = function() {
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Nee';
			};

			$scope.isFaillietSurceanceWarning = function() {
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Surseance';
			};

			$scope.isFaillietSurceanceDanger = function() {
				//return ($scope.report.kvkDossierTransfer.faillietSurseance == 'Surseance + Faillissement' ||
				//$scope.report.kvkDossierTransfer.faillietSurseance == 'Faillissement');
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Ja' || $scope.report.kvkDossierTransfer.faillietSurseance == ''
			};

			$scope.isNevenVestiging = function() {
				return !!(typeof $scope.report.bedrijfHoofd == 'undefined' || $scope.report.bedrijfHoofd == null);
			};

			$scope.meldingmaken = function() {
				$location.path('/createcustomnotification/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl) ); //+ '$' + !$scope.showMonitoring());
				$location.url($location.path());
			};

			$scope.notifyCompany = function() {
				console.log("notifyCompany function");

				if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
					$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl));
				} else {
					$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl));
				}
				$location.url($location.path());
			};

			$scope.nrOfHistorie = function(histories) {
				var result = 0;

				if(typeof histories != 'undefined') {
					result = histories.length;
				}

				return result;
			};

			$scope.nrOfMeldingen = function(meldingen) {
				var result = 0;

				if(typeof meldingen != 'undefined') {
					result = meldingen.length;
				}

				return result;
			};
			
			$scope.isOwnCompany = function() {
				if (typeof reportData != 'undefined' && reportData.bedrijfaanvrager!=null && reportData.bedrijf!=null) {
					return reportData.bedrijfaanvrager.kvKnummer == reportData.bedrijf.kvKnummer;
				} else
					return false;
			};			

			$scope.showMonitoring = function() {
				if(($scope.monitorCreated != 'undefined' && $scope.monitorCreated != null && $scope.monitorCreated) || noMonitoringAllowed()) {
					return false;
				}

				return $scope.monitoringBijBedrijf == false;
			};

			makeMonitor = function() {
				delete $scope.error;
				delete $scope.monitorCreated;

				CompanyService.monitoringCompany(monitorRequestData, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					} else {
						$scope.monitorCreated = true;
					}
				});
			};

			noMonitoringAllowed = function() {
				if(typeof ($window.sessionStorage.user) != 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;

					return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
				} else {
					return false;
				}
			};

			saveShowMonitorPopup = function() {
				delete $scope.error;

				$rootScope.saveShowHelpDialog($rootScope.HELP_MONITORINGPURCHASE, $scope.hideMonitorPopup, function(result) {
					if(result.error != null) {
						$scope.error = result.error;
					}
				});
			};			
			
			// reportcompany
			$scope.report = ReportDataStorage.getReportData();

			$scope.getBedragText = function (bedrag) {
				if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
					return 'hoger dan \u20AC 100,-';
				else
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.getDatum = function (timestamp) {
				if (typeof timestamp != 'undefined' && timestamp != null)
					return timestamp;
				else
					return 'n.v.t.';
			};

			$scope.hideCompanyInfo = function (bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.nrOfHistorie = function (histories) {
				var result = 0;

				if (typeof histories != 'undefined')
					result = histories.length;

				return result;
			};

			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
			};
			
			// reportoverview
			$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

			$scope.nrOfMeldingen = function(meldingen) {
				var result = 0;

				if(typeof meldingen != 'undefined') {
					result = meldingen.length;
				}

				return result;
			};

			var maxY = 0;
			var maxRange = 0;

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function() {
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
						doReport: $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			if(typeof $scope.report.reportsLastTwoWeeks != 'undefined' && $scope.report.reportsLastTwoWeeks != null) {
				var values = [];

				for(var i in $scope.report.reportsLastTwoWeeks) {
					var item = [];
					item.push(Number(i));
					item.push(Number($scope.report.reportsLastTwoWeeks[i].aantal));
					values.push(item);

					if(item[1] > maxY) {
						maxY = item[1];
					}
				}

				$scope.exampleDataRapportAanvragen = [
					{
						"key"   : "Rapporten opgevraagd",
						"values": values
					}
				];

				maxRange = maxY / 5;
				maxRange++;
				maxRange = maxRange * 5;
			} else {
				$scope.exampleDataRapportAanvragen = [
					{
						"key"   : "Rapporten opgevraagd",
						"values": []
					}
				];
			}

			//$scope.getYTickValues = [0, maxRange];

			//$scope.getForceY = [0, 5];

			//$scope.getForceYmin = 0;
			//$scope.getForceYmax = 5;

			$scope.getBedragText = function(bedrag) {
				if(typeof bedrag == 'undefined' || bedrag == null || bedrag == 0) {
					return 'hoger dan \u20AC 100,-';
				} else {
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');
				}//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.hideCompanyInfo = function(bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.nrOfHistorie = function(histories) {
				var result = 0;

				if(typeof histories != 'undefined') {
					result = histories.length;
				}

				return result;
			};
			
			$scope.reportHoofdVestiging = function() {
				var report = ReportDataStorage.getReportData();
				if (report !== 'undefined' && report != null &&
						report.bedrijfHoofd !== 'undefined' && report.bedrijfHoofd != null) {
					var hoofdKvK = report.bedrijfHoofd.kvKnummer;
					if(typeof report.bedrijfHoofd.subDossier != 'undefined') {
						hoofdKvK += report.bedrijfHoofd.subDossier;
					}
					ReportDataStorage.getReportData(hoofdKvK);
				}
				makeReport(report.bedrijfHoofd.kvKnummer);
			};

			$scope.createReport = function(isLink, kvkNr) {
				if(isLink) {
					makeReport(kvkNr);
					//requestReport(kvkNr);
				}
			};

			$scope.yAxisTickFormatFunctionReportRequests = function(d) {
				return function(d) {
					//console.log("yaxis: " + d + " " + d3.round(d, 0));
					return d3.round(d, 0);
				}
			};

			$scope.xAxisTickFormatFunctionReportRequests = function(d) {
				return function(d) {
					//console.log("xaxis: " + d + " " + $scope.report.reportsLastTwoWeeks[d].label);
					return $scope.report.reportsLastTwoWeeks[d].label; //d3.time.format('%x')(new Date(d)); //uncomment for date format
				}
			};

			$scope.yAxisRangeFunctionReportRequests = function() {
				return [0, 5];
			};
			
			$scope.optionsReportRequests = {
		            chart: {
		            	showLegend: false,
		                type: 'lineChart',
		                height: 300,
		                margin : {left:50,top:25,bottom:25,right:50},
		                x: function(d){ return d[0]; },
		                y: function(d){ return d[1]; },
		                useInteractiveGuideline: false,		                
		                xAxis: {
		                    axisLabel: 'Dag',
		                    tickFormat: $scope.xAxisTickFormatFunctionReportRequests()
		                },
		                yAxis: {
		                    axisLabel: 'Aantal',
		                    tickFormat: $scope.yAxisTickFormatFunctionReportRequests()
		                },
		                lines: { // for line chart
		                    forceY: $scope.getForceY,
		                    yDomain: $scope.getYTickValues
		                },		                
		                callback: function(chart){
		                    console.log("!!! lineChart callback !!!");
		                }
		            }					
			};			

			makeReport = function(kvkNummer) {
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
//						if(!$scope.userCanPay){
//							var reportNoPaymentModal = $modal.open({
//								templateUrl: 'reportNoPayment.html',
//								controller: reportNoPaymentController,
//								size:'lg'
//							});
//						} else {
							////var bedrijfAanvragerId = $route.current.params.bedrijfAanvragerId;

							CompanyService.getOrCreateCompanyData({kvknumber: kvkNummer}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								} else {
									$location.path('/report/' + $route.current.params.bedrijfAanvragerId + '/' + result.bedrijfId + '/0/' + searchurl);
									$location.url($location.path());
								}
							});
						//}
				//	}
				//});
			};

			if(typeof $scope.init == 'undefined'){
				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});
			}			
			
			// reportnotificationstab
			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
			};

			var maxY = 0;
			var maxRange = 0;
			if (typeof $scope.report.meldingenLastYear != 'undefined' && $scope.report.meldingenLastYear != null) {
				var values = [];

				for (var i in $scope.report.meldingenLastYear) {
					var item = [];
					item.push(Number(i));
					item.push(Number($scope.report.meldingenLastYear[i].aantal));
					values.push(item);

					if (item[1] > maxY)
						maxY = item[1];
				}

				$scope.exampleDataRegistratieOverzicht = [
					{
						"key": "Vermeldingen",
						"values": values
					}
				];

				maxRange = maxY / 5;
				maxRange++;
				maxRange = maxRange * 5;
			} else {
				$scope.exampleDataRegistratieOverzicht = [
					{
						"key": "Vermeldingen",
						"values": []
					}
				];
			}

			//$scope.getYTickValues = [0, maxRange];

			//$scope.getForceY = [0, 5];

			//$scope.getForceYmin = 0;
			//$scope.getForceYmax = 5;

			$scope.yAxisRangeFunctionNotifications = function () {
				return [0, 5];
			};

			$scope.xAxisTickFormatFunctionNotifications = function (d) {
				return function (d) {
					console.log("xaxis: " + d + " " + $scope.report.meldingenLastYear[d].label);
					return $scope.report.meldingenLastYear[d].label; //d3.time.format('%x')(new Date(d)); //uncomment for date format
				}
			};

			$scope.yAxisTickFormatFunctionNotifications = function (d) {
				return function (d) {
					console.log("yaxis: " + d + " " + d3.round(d, 0));
					return d3.round(d, 0);
				}
			};
			
			$scope.optionsNotifications = {
		            chart: {
		            	showLegend: false,
		                type: 'lineChart',
		                height: 300,
		                margin : {left:50,top:25,bottom:25,right:50},
		                x: function(d){ return d[0]; },
		                y: function(d){ return d[1]; },
		                useInteractiveGuideline: false,		                
		                xAxis: {
		                    axisLabel: 'Dag',
		                    tickFormat: $scope.xAxisTickFormatFunctionNotifications()
		                },
		                yAxis: {
		                    axisLabel: 'Aantal',
		                    tickFormat: $scope.yAxisTickFormatFunctionNotifications()
		                },
		                lines: { // for line chart
		                    forceY: $scope.getForceY,
		                    yDomain: $scope.getYTickValues
		                },		                
		                callback: function(chart){
		                    console.log("!!! lineChart callback !!!");
		                }
		            },
		            caption: {
		                enable: true,
		                html: '<span style="text-decoration: underline;">Let op</span>, genoemde aantallen omvatten niet de reeds opgeloste betalingsachterstanden.<br/>Deze worden definitief verwijderd uit het register en zullen niet worden weergegeven.',
		                css: {
		                    'text-align': 'justify',
		                    'margin': '10px 13px 0px 7px'
		                }
		            }					
			};

			checkIfExists = function (array, value) {
				for (var i in array) {
					if (array[i] == value)
						return true;
				}

				return false;
			};

			$scope.aantalMeldingDoorBedrijven = function () {
				return $scope.report.aantalCrediteuren;			
			};

			$scope.aantalMeldingen = function () {
				if (typeof $scope.report.meldingen != 'undefined' && $scope.report.meldingen != null)
					return $scope.report.meldingen.length;
				else
					return 0;
			};

			$scope.bedragOpenstaand = function () {
				if ($scope.aantalMeldingen() == 0 && (typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null))
					return 'Geen';
				else if (typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null)
					return 'bedrag afgeschermd';
				else
					return $filter('currency')($scope.report.bedragOpenstaand); 
				// + ' (exclusief afgeschermde bedragen)';  //'&eur; ' + $scope.report.bedragOpenstaand.toLocaleString('nl-NL');
			};

			$scope.bedragGesloten = function () {
				if (typeof $scope.report.bedragResolved == 'undefined' || $scope.report.bedragResolved == null)
					return 'afgeschermd';
				else
					return $filter('currency')($scope.report.bedragResolved);  //'&eur; ' + $scope.report.bedragOpenstaand.toLocaleString('nl-NL');
			};

			$scope.nrOfHistorie = function (histories) {
				var result = 0;

				if (typeof histories != 'undefined')
					result = histories.length;

				return result;
			};

			$scope.hideCompanyInfo = function (bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.getBedragText = function (bedrag) {
				if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
					return 'hoger dan \u20AC 100,-';
				else
					return $filter('currency')(bedrag); //'\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};			
	}]);