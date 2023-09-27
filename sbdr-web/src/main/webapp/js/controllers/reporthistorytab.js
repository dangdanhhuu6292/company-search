appcontrollers.controller('ReportHistoryTabController',
	['$scope', 'ReportDataStorage',
		function ($scope, ReportDataStorage) {
			$scope.report = ReportDataStorage.getReportData();
			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
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
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

		}]);