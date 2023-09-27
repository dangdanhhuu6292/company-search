appcontrollers.controller('ActivateAccountController', ['$window', '$routeParams', '$scope', '$rootScope', '$location', 'maxFieldLengths', '$anchorScroll', '$cookieStore', 'LoginService', 'UserService', 'clientip', function ($window, $routeParams, $scope, $rootScope, $location, maxFieldLengths, $anchorScroll, $cookieStore, LoginService, UserService, clientip) {

	var userId = $routeParams.id;
	$scope.maxFieldLengths = maxFieldLengths;
	
	$scope.activateAccount = function(userId) {
		
		LoginService.activateUser({id : userId}, function(activationResult) {
			if (activationResult.errorCode === undefined)
			{
				console.log("gebruiker succesfully activated");
				$scope.userActived = true;
				login();
			}
			else {
				console.log("activateGebruiker result: " + activationResult.errorCode + " " + activationResult.errorMsg);	
				$scope.error = activationResult.errorCode + " " + activationResult.errorMsg;
				$location.hash("alert");
				$anchorScroll();
			}
		});		
	};
	
	$scope.isInvalid = function(field){
	    result = $scope.formcredentials[field].$invalid && $scope.formcredentials[field].$dirty;
	    
	    if (result)
	    {
			$location.hash($scope.formcredentials[field]);
			$anchorScroll();	    	
	    }
	    
	    return result;
	  };

	  $scope.isValid = function(field){
	    result = $scope.formcredentials[field].$valid && $scope.formcredentials[field].$dirty;
	    
	    return result;
	  };
	
	
	login = function() {
		LoginService.authenticate({username: $scope.username, password: $scope.password, ipaddress: clientip}, function(authenticationResult) {
			var authToken = authenticationResult.token;
			$window.sessionStorage.authToken = authToken;
			//$rootScope.authToken = authToken;
			//alert("MyToken=" + authToken);
			//alert("after remember me...")
			UserService.userdata(function(user) {
				if ($scope.rememberMe) {
					$cookieStore.put('authToken', authToken);
					//alert("Token stored...")
				}
				
				var userJson = JSON.stringify(user);
                $window.sessionStorage.user = userJson;				
				//$rootScope.user = user; 
				
				$rootScope.content_id = "content";
				$location.path("/dashboard");
				$location.url($location.path());
			});
			
			
		});
	};
		
}]);