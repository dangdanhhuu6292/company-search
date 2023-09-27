appcontrollers.controller('ActivateUserController', [
	'$window', '$scope', '$rootScope', '$location', '$cookies', '$anchorScroll', '$routeParams', 'maxFieldLengths', 'UserService', 'UserNsService', 'NewAccountService', 'clientip',
	function($window, $scope, $rootScope, $location, $cookies, $anchorScroll, $routeParams, maxFieldLengths, UserService, UserNsService, NewAccountService, clientip) {
		console.log("activateUserCtrl");

		$scope.maxFieldLengths = maxFieldLengths;
		
		var passwordHelpSpan = $("#passwordhelp");
		

		// Checks if the password is invalid based on various validation checks
		// Returns false when all validations checks are false (meaning: the password conforms to all validations)
		$scope.passwordInvalid = function(targetForm, targetField) {
			var allValidationValid = !passLengthInvalid(targetForm, targetField) && !passLetterInvalid(targetForm, targetField) && !passNumberInvalid(targetForm, targetField) && !passCapitalInvalid(targetForm, targetField);

			if(allValidationValid) {
				return false;
			}
			else {
				var len = passLengthInvalid(targetForm, targetField) ? lengthString : '';
				var letter = passLetterInvalid(targetForm, targetField) ? letterString : '';
				var num = passNumberInvalid(targetForm, targetField) ? numberString : '';
				var cap = passCapitalInvalid(targetForm, targetField) ? capitalString : '';
				var errorString = 'Vereist: ';
				errorString += len;
				errorString += letter;
				errorString += num;
				errorString += cap;

				passwordHelpSpan.text(errorString);
				return true;
			}
		};

		// Checks if the first password field is different than the second password field
		$scope.passwordCheckInvalid = function(targetForm, targetField1, targetField2) {
			if(!targetForm[targetField2].$dirty) {
				return false;
			}

			if(!$scope.passwordInvalid(targetForm, targetField1)) {
				if(targetForm[targetField1].$modelValue == targetForm[targetField2].$modelValue) {
					return false;
				}
			}

			return true;
		};

		$scope.returnToLogin = function() {
			$location.path("/login");
			$location.url($location.path());
		};
		
		doActivateUser = function(activationId, userId, newPassword) {
			// returns: Response.error or Response.LoginTokenTransfer
			UserNsService.activateUser({
				activationId: activationId,
				userId      : userId,
				bedrijfId   : $scope.bedrijfId,
				newPassword : newPassword
			}, function(resetResult) {
				if(resetResult.errorCode != null) { //resetPassword returns an error object
					$scope.error = resetResult.errorCode + " " + resetResult.errorMsg;
				}
				else { // resetPassword returns a LoginTokenTransfer object
					/*if (resetResult.logIn == "false") { //TODO: LoginTokenTransfer.logIn cannot be an actual boolean? and is always false
					 if (resetResult.logInMessage != null) {
					 $rootScope.error = resetResult.logInMessage;
					 }
					 else {
					 $rootScope.error = "Password reset gelukt maar inloggen mislukt";
					 }
					 }
					 else*/
					if(typeof resetResult.token !== "undefined") { // logIn is successfull // TODO: uncomment the above section when resetResult.logIn has function
						// log in using the new password
						var authToken = resetResult.token;
						//$rootScope.authToken = authToken;
						$window.sessionStorage.authToken = authToken;

						UserService.userdata(function(user) {
							$cookies.put('authToken', authToken, {
								secure: true,
								samesite: 'strict'
							});

							$window.sessionStorage.user = JSON.stringify(user);
							//$rootScope.user = user;
							$rootScope.content_id = "content";
							$location.path("/dashboard");
							$location.url($location.path()); // Clears query parameters from url
						});
					}
				}
			});			
		}

		// This function gets called by the send button on the resetPassword page
		// The button can only be pressed if the new password is valid and checked (see: passwordValid & passwordCheckValid)
		$scope.reset = function(newPasswordField) {
			var activationId = $routeParams.activationid;
			var userId = $routeParams.userid;
			var newPassword = $scope.forminitpassword[newPasswordField].$modelValue;

			doActivateUser(activationId, userId, newPassword);
		};

		$scope.buttonDisabled = function(targetForm, targetField1, targetField2) {
			if(targetForm[targetField1].$dirty && targetForm[targetField2].$dirty) {
				if(!$scope.passwordCheckInvalid(targetForm, targetField1, targetField2)) {
					return false; // enable the button
				}
			}

			return true;
		};
		
		$scope.activateUser = function() {
			var activationId = $routeParams.activationid;
			var userId = $routeParams.userid;
			
			doActivateUser(activationId, userId, '');
		}
		
		if (typeof $scope.initialised == 'undefined') {
			$rootScope.ipAddress = clientip.ip;
			NewAccountService.getApiKey(function(result) {
				if (typeof result.errorCode != 'undefined') {
					$scope.error = result.errorMsg;
				} else {
					$rootScope.clientApiKey = result.token;
					$rootScope.ipAddress = clientip.ip;
				}
			});
			
			$scope.bedrijfManagedUser = false;
			
			var activationId = $routeParams.activationid;
			var userId = $routeParams.userid;
			$scope.bedrijfId = null;
			
			UserNsService.findActivationCode({
				activationId: activationId,
				userId      : userId,
				newPassword : ''
			}, function(resetResult) {
				if(resetResult.errorCode != null) { 
					$scope.error = resetResult.errorCode + " " + resetResult.errorMsg;
				}
				else { //  returns a ActivatieCodeTransfer object
					if(typeof resetResult !== "undefined") { 
						if (resetResult.bedrijfManaged) {
							$scope.bedrijfManagedUser = true;
							$scope.bedrijfId = resetResult.bedrijfId;
						}
					}
				}
			});					
			
			$scope.initialised = true;
		}		

	}]);