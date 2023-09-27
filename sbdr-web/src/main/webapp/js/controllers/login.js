appcontrollers.controller('LoginController', ['$window', '$routeParams', '$scope', '$rootScope', '$location', '$q', 'maxFieldLengths', '$anchorScroll', '$cookies', 'LoginService', 'UserService', 'clientip', 'ExactOnlineService', 'WebSocketService', function ($window, $routeParams, $scope, $rootScope, $location, $q, maxFieldLengths, $anchorScroll, $cookies, LoginService, UserService, clientip, ExactOnlineService, WebSocketService) {

	$scope.maxFieldLengths = maxFieldLengths;
		
	if ($window.sessionStorage.user) {
		var old = $window.sessionStorage.user;
		delete $rootScope.clientApiKey;
		delete $window.sessionStorage.user;	
		$window.sessionStorage.user = old;
		$rootScope.content_id = 'content';
		//$location.path('\dashboard');
		$location.path("/searchcompany");
		$location.url($location.path());
	}
	else {
		$rootScope.content_id = 'nscontent';
	}
	
	if(typeof fuckAdBlock === 'undefined') {
		adBlockDetected();
	} else {
		fuckAdBlock.onDetected(adBlockDetected).onNotDetected(adBlockNotDetected);
	} 
	
	// Route all mobile traffic to information page
	// no activation or login is possible
	//if (typeof $scope.activateAccount == 'undefined' && typeof $scope.error == 'undefined') {
		// if no activation AND no error then check mobile redirect
		// redirect if mobile
	//	if ($rootScope.isMobileResolution()) {
	//	   window.location.href = "/informatie.php";
	//	}			
	//}			
		
	if (typeof $routeParams.userid != 'undefined') {
		var activationid = $routeParams.activationid || undefined;
		var userid = $routeParams.userid || undefined;
		var isklant = $routeParams.bbset || undefined;
		
		if (activationid && isklant) {			
			delete $scope.activateAccount;
			delete $scope.error;
			
			LoginService.activateCustomer({activationid: activationid, username: userid}, function(result) {
				if (typeof result.errorCode !== 'undefined') {
					$scope.error = result.errorCode + ' ' + result.errorMsg;
				}
				else {
					$scope.activateAccount = true; 
					if (userid)
						$scope.username = userid;
				}
			});				
		} //else {
		//	if (typeof $scope.activateAccount == 'undefined' && typeof $scope.error == 'undefined') {
				// if no activation AND no error then check mobile redirect
				// redirect if mobile
		//		if ($rootScope.isMobileResolution()) {
			//	   window.location.href = "/informatie.php";
		//		}			
		//	}			
		//}
	}
	
	$scope.rememberMe = false; // false
	
    // This function checks a field in a form on two conditions: $invalid and $dirty
    // The HTML input tag requires the REQUIRED attribute in order for $invalid to work
    $scope.isInvalid = function(targetForm, targetField){
    	 var result = targetForm[targetField].$invalid && targetForm[targetField].$dirty;

        if (result) {
        	delete $scope.error; 
            // Save old hash (might be no hash), set location, scroll to it, and set old hash back(most common result: no hash in url)
            // $anchorScroll() works best when an id is provided
            var old = $location.hash();
            $location.hash(targetForm[targetField].$id);
            $anchorScroll();
            $location.hash(old);
        }

        return result;
    };
	
    $scope.websocketStatus = function() {
    	return WebSocketService.status();
    }
	
	$scope.login = function() {
		delete $scope.activateAccount;
		delete $scope.error;
		
		var tokenData = LoginService.authenticate({username: $scope.username, password: $scope.password, ipaddress: clientip.ip});
		
		var resultsAuthPromise = $q.all({
			tokendata: tokenData.$promise
		}).then(function(data) {
	    	delete $scope.error; 
			var authToken = data.tokendata.token;
			var refreshToken = data.tokendata.refreshToken;
			var basicAuth = data.tokendata.basicAuth;

			if (typeof authToken != "undefined" && authToken != null) {
				$window.sessionStorage.authToken = authToken;
				$window.sessionStorage.refreshToken = refreshToken;
				$window.sessionStorage.basicAuth = basicAuth;
				console.log("USER 1");
				UserService.userdata(function(user) {
					if ($scope.rememberMe) {
						$cookies.put('authToken', authToken, {
                        secure: true,
                        samesite: 'strict'
                    });
					}

					delete $rootScope.clientApiKey;
					$window.sessionStorage.user = JSON.stringify(user);
					
					// WEBSOCKETS disabled for now!
					// open websocket
//					var websocket = LoginService.websocketdata();
//					var resultWebSocketUriPromise = $q.all({
//						websocketData: websocket.$promise
//					}).then(function(result) {
//						WebSocketService.openConnection(result.websocketData.webSocketUri, user.userName, $window.sessionStorage.authToken);		                
//					}).catch(function(error) {
//						console.log('Error fetching/opening websocketuri');						
//					});					
					
	                //alert("LOGIN NEW USER: " + $window.sessionStorage.user);
					
					$rootScope.content_id = "content";
					
					var roles = JSON.parse($window.sessionStorage.user).roles;
					var isActionsPresent = JSON.parse($window.sessionStorage.user).actionsPresent;
					
					// rootscope var to enable/disable exactonline login
//				    $rootScope.isExactOnlineAccess = ExactOnlineService.exactOnlineAccess(function(result) {
//						if (typeof result.errorCode !== 'undefined') {
//							$rootScope.error = result.errorCode + ' ' + result.errorMsg;
//							return false;
//						}
//						else {
//							if (result == 'notConnected')
//								return true;
//							else
//								return false;
//						}
//					});		
				    
				    
				    var exactOnlineAccessData = ExactOnlineService.exactOnlineAccess();

				    $q.all({
                    	exactOnlineAccess: exactOnlineAccessData.$promise
                    }).then(function (data) {
                        //return exactOnlineAccessData;
                        if (typeof exactOnlineAccessData.errorCode !== 'undefined') {
							$rootScope.error = result.errorCode + ' ' + result.errorMsg;
							$rootScope.isExactOnlineAccess =  false;
						}
						else {
							// only when notConnected show ExactOnline login, otherwise may be no rights or already connected
							if (exactOnlineAccessData.result == 'notConnected')
								$rootScope.isExactOnlineAccess =  false;
							else // also when notAllowed. So that EO login is not shown
								$rootScope.isExactOnlineAccess =  true;
						}                        
                    }).catch(function (error) {
                        console.log('Error fetching ExactOnlineAccess');
                    });
					
					if (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd')) 
						$location.path("/dashboard");
					else if (isActionsPresent == true)
						$location.path("/dashboard/alertstab");
					else
						$location.path("/searchcompany")
					$location.url($location.path());
				}, function(error) {
					//alert("error: " + error);
					$scope.error = 'Er is een fout opgetreden.';
				});
			}
			else {
				if (typeof data.tokendata.errorMsg != 'undefined' && data.tokendata.errorMsg != null) {
					$scope.error = data.tokendata.errorMsg;
				} else				
					$scope.error = "Er ging iets fout bij het inloggen. Probeert u het nog een keer.";
				
			}			
		}).catch(function(error) { 
			console.log('Error fetching token data');
			//alert("Error fetching token");
			$scope.error = 'Er is een fout opgetreden.';
		});
		
		
//		LoginService.authenticate({username: $scope.username, password: $scope.password}, function(authenticationResult) {
//			//alert("LOGIN ATTEMPT: " + $scope.username);
//			
//	    	delete $scope.error; 
//			var authToken = authenticationResult.token;
//
//			if (typeof authToken != "undefined") {
//				$window.sessionStorage.authToken = authToken;
//				console.log("USER 1");
//				var userData = UserService.get();
//				console.log("USER 2");
//				var resultsPromise = $q.all({
//					userdata: userData.$promise
//				}).then(function(data) {
//					if ($scope.rememberMe) {
//						$cookieStore.put('authToken', authToken);
//					}
//					
//					console.log("USER 3: " + userData);
//					var userJson = JSON.stringify(userData);
//	                $window.sessionStorage.user = userJson;	   
//	                
//	                //alert("LOGIN NEW USER: " + $window.sessionStorage.user);
//					
//					$rootScope.content_id = "content";
//					$location.path("/dashboard");
//					$location.url($location.path());								
//					
//					return userData;
//				}).catch(function(error) { 
//					console.log('Error fetching user data')
//				});
			
				
//				$window.sessionStorage.authToken = authToken;
//				
//				UserService.get(function(user) {
//					if ($scope.rememberMe) {
//						$cookieStore.put('authToken', authToken);
//					}
//					
//					var userJson = JSON.stringify(user);
//	                $window.sessionStorage.user = userJson;	   
//	                
//	                alert("LOGIN NEW USER: " + $window.sessionStorage.user);
//					
//					$rootScope.content_id = "content";
//					$location.path("/dashboard");
//					$location.url($location.path());
//				});
//			}
//			else
//				$scope.error = "U heeft uw gebruikersnaam en wachtwoord verkeerd ingevuld. Probeert u het nog een keer.";
			
//		});
	};
	
	$scope.createaccount = function() {
		$rootScope.content_id = "content";
		$location.path("/searchcompany");
		$location.url($location.path());
	};
	
	$scope.forgotPass = function(){
        $location.path("/forgotpassword");
        $location.url($location.path());
    }	
}]);