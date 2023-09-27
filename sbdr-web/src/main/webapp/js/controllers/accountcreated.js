appcontrollers.controller('AccountCreatedController', ['$window', '$scope', '$rootScope', '$location', function ($window, $scope, $rootScope, $location) {
	console.log("accountCreatedController");
	
	$scope.returnToLogin = function() {
		$location.path("/login");
		$location.url($location.path());
	}
	
}]);	