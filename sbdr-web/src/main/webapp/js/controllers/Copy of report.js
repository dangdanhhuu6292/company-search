appcontrollers.controller('ReportController', ['$routeParams', '$scope', '$rootScope', '$location', '$anchorScroll', 'reportData', 'monitorRequestData', 'monitoringBijBedrijf', 'CompanyService', 'DocumentService', function ($routeParams, $scope, $rootScope, $location, $anchorScroll, reportData, monitorRequestData, monitoringBijBedrijf, CompanyService, DocumentService) {
	console.log("createAccountController");

	if ($routeParams.searchurl)
		searchurl = $routeParams.searchurl;
	else
		searchurl = '$dashboard';
	
	if (typeof monitoringBijBedrijf != 'undefined')
		$scope.monitoringBijBedrijf = monitoringBijBedrijf;
	
	$scope.documentRequested = {action: 'report', bedrijfId: reportData.bedrijf.bedrijfId, meldingId: 0, referentie: reportData.referentieNummer};
	$scope.report = reportData;
	
	if ($scope.report && $scope.report.meldingen) {
		if (Object.prototype.toString.call( $scope.report.meldingen ) === '[object Array]')
			$scope.meldingenOverview =$scope.report.meldingen;
		else
			$scope.meldingenOverview = [$scope.report.meldingen];
	}
	else
		delete $scope.meldingenOverview;
	
	$scope.getReport = function(referentie) {
		DocumentService.report({referentie: referentie}, function(result) {
			//var file = new Blob([result], { type: 'application/pdf' });
            //var fileURL = URL.createObjectURL(file);
            //window.open(fileURL);
			
			$scope.$emit('downloaded', result);
		});
	}
	
  	$scope.monitoring = function() {  	
  		delete $scope.error;
  		delete $scope.monitorCreated;
  		
  		CompanyService.monitoringCompany(monitorRequestData, function(result) {
			if (typeof result.errorCode != 'undefined')
				$scope.error = result.errorCode + ' ' + result.errorMsg;
			else {
				$scope.monitorCreated = true;				
			}
		});  		
  	};
  	
  	$scope.showMonitoring = function() {
  		if ($scope.monitorCreated)
  			return false;
  		
  		if ($scope.monitoringBijBedrijf == 'false')
  			return true
  	}
  	
  	$scope.nrOfMeldingen = function(meldingen) {
  		var result = 0;
  		
  		if (typeof meldingen != 'undefined' )
  			result = meldingen.length;

  		return result;  		
  	}
  	
  	$scope.hideCompanyInfo = function(bedrijfId) {
  		if (typeof bedrijfId === 'undefined' || bedrijfId == null)
  			return true;
  		else
  			return false;
  	}
  	
  	$scope.getBedragText = function(bedrag) {
  		if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
  			return 'hoger dan \u20AC 100,-';
  		else
  			return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
  	}
  	
  	$scope.back = function() {
		url = gotoDurl(searchurl);
		if (url != null) {
			$location.path(url);
			$location.url($location.path());
		}

  	}
	
}]);