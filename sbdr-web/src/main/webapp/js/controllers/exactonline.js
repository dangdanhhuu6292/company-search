appcontrollers.controller('ExactOnlineController', ['$window', '$modal', '$scope', '$rootScope', '$location', '$anchorScroll', '$routeParams', '$cookies', 'exactonlineparamData', function ($window, $modal, $scope, $rootScope, $location, $anchorScroll, $routeParams, $cookie, exactonlineparamData) {
	console.log("exactOnlineController");
	
	 
	if (typeof $scope.init === "undefined")
	{
		$scope.exactonlineparam = exactonlineparamData.exactonlineparam;
		
		$scope.init = true;
	}			     


			  			      
}]);