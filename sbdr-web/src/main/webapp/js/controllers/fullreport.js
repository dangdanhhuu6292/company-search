appcontrollers.controller('FullReportController', ['$routeParams', '$scope', '$rootScope', '$location', '$anchorScroll', 'CompanyService', 'DocumentService', function ($routeParams, $scope, $rootScope, $location, $anchorScroll, CompanyService, DocumentService) {
	console.log("fullReportController");

	if ($routeParams.searchurl)
		searchurl = $routeParams.searchurl;
	else
		searchurl = '$dashboard';
	
	$scope.chart_options = {
			resize: true,
			element: 'donut-vermeldingen',
		    data: [
		      {label: "Actief", value: 2},
		      {label: "Verwijderd", value: 0},
		      {label: "Recent", value: 2}
		    ]
		  };

	$scope.chart_options = {
			resize: true,
			element: 'donut-vermeldingen',
		    data: [
		      {label: "Monitoring", value: 2},
		      {label: "Verwijderd", value: 0},
		      {label: "Recent", value: 2}
		    ]
		  };	
}]);