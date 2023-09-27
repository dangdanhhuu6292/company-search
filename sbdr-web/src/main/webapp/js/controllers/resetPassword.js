appcontrollers.controller('ResetPasswordController', ['$window', '$scope', '$rootScope', '$location', 'maxFieldLengths', '$cookies', '$anchorScroll', '$routeParams', 'UserService', 'UserNsService', 'NewAccountService', 'clientip', function($window, $scope, $rootScope, $location, maxFieldLengths, $cookies, $anchorScroll, $routeParams, UserService, UserNsService, NewAccountService, clientip) {
	console.log("resetPassCtrl");

	var passwordHelpSpan = $("#passwordhelp");
	$scope.maxFieldLengths = maxFieldLengths;
	
	// Checks if the password is invalid based on various validation checks
	// Returns false when all validations checks are false (meaning: the password conforms to all validations)
	$scope.passwordInvalid = function(targetForm, targetField) {
		var allValidationValid = !passLengthInvalid(targetForm, targetField, false) && !passLetterInvalid(targetForm, targetField, false) && !passNumberInvalid(targetForm, targetField, false) && !passCapitalInvalid(targetForm, targetField, false) && !passSpacesInvalid(targetForm, targetField, false);

		if(allValidationValid) {
			return false;
		}
		else {
			var len = passLengthInvalid(targetForm, targetField, false) ? lengthString : '';
			var letter = passLetterInvalid(targetForm, targetField, false) ? letterString : '';
			var num = passNumberInvalid(targetForm, targetField, false) ? numberString : '';
			var cap = passCapitalInvalid(targetForm, targetField, false) ? capitalString : '';
			var spa = passSpacesInvalid(targetForm, targetField, false) ? spacesString : '';
			var errorString = 'Vereist: ';
			errorString += len;
			errorString += letter;
			errorString += num;
			errorString += cap;
			errorString += spa;

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

	// This function gets called by the send button on the resetPassword page
	// The button can only be pressed if the new password is valid and checked (see: passwordValid & passwordCheckValid)
	$scope.reset = function(newPasswordField) {
		var activationId = $routeParams.activationid;
		var userId = $routeParams.userid;
		var newPassword = $scope.formresetpassword[newPasswordField].$modelValue;

		// returns: Response.error or Response.LoginTokenTransfer
		
		UserNsService.resetPassword({
			activationId: activationId,
			userId      : userId,
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
						$cookieStore.put('authToken', authToken);

						$window.sessionStorage.user = JSON.stringify(user);
						//$rootScope.user = user;
						$rootScope.content_id = "content";
						$location.path("/dashboard");
						$location.url($location.path()); // Clears query parameters from url
					});
				}
			}
		});
	};

	$scope.buttonDisabled = function(targetForm, targetField1, targetField2) {
		if(targetForm[targetField1].$dirty && targetForm[targetField2].$dirty) {
			if(!$scope.passwordCheckInvalid(targetForm, targetField1, targetField2)) {
				return false; // enable the button
			}
		}

		return true;
	};
	
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
		
		$scope.initialised = true;
	}	

}]);