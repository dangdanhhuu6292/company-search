appcontrollers.controller('VermeldingenInfoController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', 'UserService',
		function ($window, $routeParams, $scope, $rootScope,
				  $location, UserService) {

			if (typeof $scope.init == 'undefined') {
				$scope.init = true;

				if (!$routeParams.eigenBedrijfId || !$routeParams.eigenBedrijfId || $routeParams.eigenBedrijfId == null || $routeParams.eigenBedrijfId == 'undefined')
					eigenBedrijfIdentifier = null;
				else
					eigenBedrijfIdentifier = $routeParams.eigenBedrijfId;

				if (!$routeParams.bedrijfId || !$routeParams.bedrijfId || $routeParams.bedrijfId == null || $routeParams.bedrijfId == 'undefined')
					bedrijfIdentifier = null;
				else
					bedrijfIdentifier = $routeParams.bedrijfId;

				if (!$routeParams.meldingId || !$routeParams.meldingId || $routeParams.meldingId == null || $routeParams.meldingId == 'undefined')
					meldingId = null;
				else
					meldingId = $routeParams.meldingId;

				if ($routeParams.searchurl)
					searchurl = $routeParams.searchurl;
				else
					searchurl = '$dashboard';

				$scope.nietMeerWeergeven = !$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO);

				if ($scope.nietMeerWeergeven)
					notifyCompany();
			}

			var bedrijfIdentifier;
			var eigenBedrijfIdentifier;
			var meldingId;

			$scope.gotoVermeldingen = function () {
				if ($scope.nietMeerWeergeven)
					saveShowHelp(notifyCompany);

				notifyCompany();
			};

			$scope.returnToSearch = function () {
				url = gotoDurl(searchurl);
				if (url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			notifyCompany = function () {
				if (meldingId != null)
					$location.path('/notifycompany/' + eigenBedrijfIdentifier + '/' + bedrijfIdentifier + '/' + searchurl + '/' + meldingId);
				else
					$location.path('/notifycompany/' + eigenBedrijfIdentifier + '/' + bedrijfIdentifier + '/' + searchurl);
				$location.url($location.path());
			};

			saveShowHelp = function (callback) {
				delete $scope.error;

				$rootScope.saveShowHelpDialog($rootScope.HELP_VERMELDINGINFO, $scope.nietMeerWeergeven, function (result) {
					if (result.error != null) {
						$scope.error = result.error;
					} else {
						callback();
					}
				});
			};
		}]);
