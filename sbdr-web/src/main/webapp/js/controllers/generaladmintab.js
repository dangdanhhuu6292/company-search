appcontrollers.controller('GeneralAdminTabController',
	['$routeParams', '$scope', '$rootScope', '$location', 'DashboardService',
		function ($routeParams, $scope, $rootScope, $location, DashboardService) {
			console.log("createAccountController");

			initAllData = function () {
				$scope.totaalKlanten = 0;
				$scope.totaalActieKlanten = 0;
				$scope.totaalProspectKlanten = 0;
				$scope.totaalNieuweKlanten = 0;
				$scope.totaalVerwijderdeKlanten = 0;

				$scope.totaalMeldingen = 0;
				$scope.totaalActieMeldingen = 0;
				$scope.totaalNieuweMeldingen = 0;
				$scope.totaalVerwijderdeMeldingen = 0;

				$scope.totaalMonitoring = 0;
				$scope.totaalNieuweMonitoring = 0;
				$scope.totaalVerwijderdeMonitoring = 0;

				$scope.totaalRapporten = 0;
				$scope.totaalNieuweRapporten = 0;

				$scope.dataPointsNieuweKlanten = [];
				$scope.dataPointsNieuweMeldingen = [];
				$scope.dataPointsNieuweMonitoring = [];
				$scope.dataPointsNieuweRapporten = [];

				//$scope.dataPoints = [2,3,4,6,2,1,5,3,2,4];
			};

			correctValues = function (array, descriptionToCorrect) {
				var total = 0;
				var item = null;
				for (var i in array) {
					total += array[i].value;
					if (array[i].description == descriptionToCorrect)
						item = array[i];
				}
				if (total > 100.00)
					item.value = item.value - (total - 100.00);

			};

			getProgressKlantStacked = function () {
				$scope.stackedKlanten = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalActieKlanten / $scope.totaalKlanten) * 100),
					description: 'In behandeling!',
					type: 'danger'
				});

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalProspectKlanten / $scope.totaalKlanten) * 100),
					description: 'Prospect',
					type: 'warning'
				});

				$scope.stackedKlanten.push({
					value: Math.round((($scope.totaalNieuweKlanten - $scope.totaalActieKlanten) / $scope.totaalKlanten) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedKlanten.push({
					value: Math.round((($scope.totaalKlanten - $scope.totaalActieKlanten - $scope.totaalProspectKlanten - ($scope.totaalNieuweKlanten - $scope.totaalActieKlanten) - $scope.totaalVerwijderdeKlanten) / $scope.totaalKlanten) * 100),
					description: 'Overig actief',
					type: 'success'
				});

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalVerwijderdeKlanten / $scope.totaalKlanten) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedKlanten, 'Overig actief');

			};

			getProgressMeldingStacked = function () {
				$scope.stackedMeldingen = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedMeldingen.push({
					value: Math.round(($scope.totaalActieMeldingen / $scope.totaalMeldingen) * 100),
					description: 'In behandeling of wachten!',
					type: 'danger'
				});

				$scope.stackedMeldingen.push({
					value: Math.round((($scope.totaalNieuweMeldingen - $scope.totaalActieMeldingen) / $scope.totaalMeldingen) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedMeldingen.push({
					value: Math.round((($scope.totaalMeldingen - $scope.totaalActieMeldingen - ($scope.totaalNieuweMeldingen - $scope.totaalActieMeldingen) - $scope.totaalVerwijderdeMeldingen) / $scope.totaalMeldingen) * 100),
					description: 'Overig actief',
					type: 'success'
				});

				$scope.stackedMeldingen.push({
					value: Math.round(($scope.totaalVerwijderdeMeldingen / $scope.totaalMeldingen) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedMeldingen, 'Overig actief');

			};

			getProgressMonitoringStacked = function () {
				$scope.stackedMonitoring = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedMonitoring.push({
					value: Math.round(($scope.totaalNieuweMonitoring / $scope.totaalMonitoring) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedMonitoring.push({
					value: Math.round((($scope.totaalMonitoring - $scope.totaalNieuweMonitoring - $scope.totaalVerwijderdeMonitoring) / $scope.totaalMonitoring) * 100),
					description: 'Actief',
					type: 'success'
				});

				$scope.stackedMonitoring.push({
					value: Math.round(($scope.totaalVerwijderdeMonitoring / $scope.totaalMonitoring) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedMonitoring, 'Actief');

			};

			getProgressRapportStacked = function () {
				$scope.stackedRapporten = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedRapporten.push({
					value: Math.round(($scope.totaalNieuweRapporten / $scope.totaalRapporten) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedRapporten.push({
					value: Math.round((($scope.totaalRapporten - $scope.totaalNieuweRapporten) / $scope.totaalRapporten) * 100),
					description: 'Overig',
					type: 'info'
				});

				correctValues($scope.stackedRapporten, 'Overig');

			};

			getProgressGebruikersStacked = function () {
				$scope.stackedGebruikers = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalNonActivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Nog niet geactiveerd!',
					type: 'danger'
				});

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalActivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Actief',
					type: 'success'
				});

				$scope.stackedGebruikers.push({
					value: Math.round((($scope.totaalGebruikers - $scope.totaalNonActivatedGebruikers - $scope.totaalActivatedGebruikers - $scope.totaalDeactivatedGebruikers) / $scope.totaalGebruikers) * 100),
					description: 'Overig',
					type: 'success'
				});

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalDeactivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Niet actief',
					type: 'info'
				});

				correctValues($scope.stackedGebruikers, 'Overig');
			};

			fetchData = function () {
				initAllData();

				DashboardService.generaladmin(function (data) {
					console.log("Data fetched");
					if (typeof data.errorCode == 'undefined') {
						if (data) {
							//if (Object.prototype.toString.call(data.klantenPerStatus) === '[object Array]')
							$scope.klantenPerStatus = data.klantenPerStatus;
							//else
							//	$scope.klantenPerStatus = [data.klantenPerStatus];
							//if (Object.prototype.toString.call(data.activeKlantenLast24h) === '[object Array]')
							$scope.klantenLast24h = data.activeKlantenLast24h;
							//else
							//	$scope.klantenLast24h = [data.activeKlantenLast24h];

							//if (Object.prototype.toString.call(data.meldingenPerStatus) === '[object Array]')
							$scope.meldingenPerStatus = data.meldingenPerStatus;
							//else
							$scope.meldingenPerStatus = data.meldingenPerStatus;
							//if (Object.prototype.toString.call(data.activeMeldingenLast24h) === '[object Array]')
							$scope.meldingenLast24h = data.activeMeldingenLast24h;
							//else
							//	$scope.meldingenLast24h = [data.activeMeldingenLast24h];

							//if (Object.prototype.toString.call(data.monitoringPerStatus) === '[object Array]')
							$scope.monitoringPerStatus = data.monitoringPerStatus;
							//else
							//	$scope.monitoringPerStatus = [data.monitoringPerStatus];
							//if (Object.prototype.toString.call(data.activeMonitoringLast24h) === '[object Array]')
							$scope.monitoringLast24h = data.activeMonitoringLast24h;
							//else
							//	$scope.monitoringLast24h = [data.activeMonitoringLast24h];

							if (data.totaalAantalRapporten)
								$scope.totaalRapporten = parseInt(data.totaalAantalRapporten);
							//if (Object.prototype.toString.call(data.rapportenLast24h) === '[object Array]')
							$scope.rapportenLast24h = data.rapportenLast24h;
							//else
							//	$scope.rapportenLast24h = [data.rapportenLast24h];

						}

						if ($scope.klantenPerStatus) {
							for (var i in $scope.klantenPerStatus) {
								if ($scope.klantenPerStatus[i].statusCode == 'NOK') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalActieKlanten = $scope.totaalActieKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'REG') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalActieKlanten = $scope.totaalActieKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'PRO') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalProspectKlanten = $scope.totaalProspectKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'ACT') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeKlanten = $scope.totaalVerwijderdeKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}

							}
						}

						if ($scope.klantenLast24h) {
							for (var i in $scope.klantenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.klantenLast24h[i].aantal) + parseInt($scope.klantenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweKlanten.push(aantal);
								}
							}
						}

						if ($scope.meldingenPerStatus) {
							for (var i in $scope.meldingenPerStatus) {
								if ($scope.meldingenPerStatus[i].statusCode == 'NOK') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'INI') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'INB') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'ACT') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeMeldingen = $scope.totaalVerwijderdeMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}

							}
						}

						if ($scope.meldingenLast24h) {
							for (var i in $scope.meldingenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.meldingenLast24h[i].aantal) + parseInt($scope.meldingenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweMeldingen.push(aantal);
								}
							}
						}

						if (data.totaalAantalNieuweMonitoring)
							$scope.totaalNieuweMonitoring = parseInt(data.totaalAantalNieuweMonitoring);
						if ($scope.monitoringPerStatus) {
							for (var i in $scope.monitoringPerStatus) {
								if ($scope.monitoringPerStatus[i].statusCode == 'ACT') {
									$scope.totaalMonitoring = $scope.totaalMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeMonitoring = $scope.totaalVerwijderdeMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
									$scope.totaalMonitoring = $scope.totaalMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
								}

							}
						}

						if ($scope.monitoringLast24h) {
							for (var i in $scope.monitoringLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.monitoringLast24h[i].aantal) + parseInt($scope.monitoringLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweMonitoring.push(aantal);
								}
							}
						}

						if (data.totaalAantalRapporten)
							$scope.totaalRapporten = parseInt(data.totaalAantalRapporten);
						if (data.totaalAantalNieuweRapporten)
							$scope.totaalNieuweRapporten = parseInt(data.totaalAantalNieuweRapporten);

						if ($scope.rapportenLast24h) {
							for (var i in $scope.rapportenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.rapportenLast24h[i].aantal) + parseInt($scope.rapportenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweRapporten.push(aantal);
								}
							}

						}

						if (data.nrOfGebruikersIngelogd)
							$scope.gebruikersIngelogd = data.nrOfGebruikersIngelogd;
						else
							$scope.gebruikersIngelogd = 0;
						if (data.totaalAantalGebruikers)
							$scope.totaalGebruikers = data.totaalAantalGebruikers;
						else
							$scope.totaalGebruikers = 0;
						if (data.totaalAantalActivatedGebruikers)
							$scope.totaalActivatedGebruikers = data.totaalAantalActivatedGebruikers;
						else
							$scope.totaalActivatedGebruikers = 0;
						if (data.totaalAantalDeactivatedGebruikers)
							$scope.totaalDeactivatedGebruikers = data.totaalAantalDeactivatedGebruikers;
						else
							$scope.totaalDeactivatedGebruikers = 0;
						if (data.totaalAantalNonActivatedGebruikers)
							$scope.totaalNonActivatedGebruikers = data.totaalAantalNonActivatedGebruikers;
						else
							$scope.totaalNonActivatedGebruikers = 0;

						getProgressKlantStacked();
						getProgressMeldingStacked();
						getProgressMonitoringStacked();
						getProgressRapportStacked();
						getProgressGebruikersStacked();
					} else {
						$scope.error = data.errorCode + " " + data.errorMsg;
					}

				}, function () {
					console.log("Error fetching companies");
				});
			};

//    setInterval(function(){
//        $scope.$apply(function() {
//            $scope.valuespark = getRandomInt(1, 10);
//        });
//    }, 1000);

			fetchData();

		}]);