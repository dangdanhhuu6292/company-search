appcontrollers.controller('MyUserAccountController',
	['$window', '$scope', '$rootScope', '$location', '$cookies', '$anchorScroll', '$routeParams', 'maxFieldLengths', 'AccountService', 'UserService',
		function($window, $scope, $rootScope, $location, $cookies, $anchorScroll, $routeParams, maxFieldLengths, AccountService, UserService) {
			console.log("resetPassCtrl");

			$scope.maxFieldLengths = maxFieldLengths;
			
			var passwordHelpSpan = $("#passwordhelp");

			$scope.formSubmitted = false;

			$scope.isInvalid = function(targetForm, targetField) {
				var fieldInvalid = targetForm[targetField].$invalid;
				var fieldDirty = targetForm[targetField].$dirty || $scope.formSubmitted;
				var result = fieldInvalid && fieldDirty;

				if(result) {
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

			// Checks if the password is invalid based on various validation checks
			// Returns false when all validations checks are false (meaning: the password conforms to all validations)
			$scope.passwordInvalid = function(targetForm, targetField) {
				var allValidationValid = !passLengthInvalid(targetForm, targetField, $scope.formSubmitted) && !passLetterInvalid(targetForm, targetField, $scope.formSubmitted) && !passNumberInvalid(targetForm, targetField, $scope.formSubmitted) && !passCapitalInvalid(targetForm, targetField, $scope.formSubmitted);

				if(allValidationValid) {
					return false;
				} else {
					var len = passLengthInvalid(targetForm, targetField, $scope.formSubmitted) ? lengthString : '';
					var letter = passLetterInvalid(targetForm, targetField, $scope.formSubmitted) ? letterString : '';
					var num = passNumberInvalid(targetForm, targetField, $scope.formSubmitted) ? numberString : '';
					var cap = passCapitalInvalid(targetForm, targetField, $scope.formSubmitted) ? capitalString : '';
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
				if(!targetForm[targetField2].$dirty && $scope.formSubmitted) {
					return true;
				}
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

			$scope.returnToDashboard = function() {
				$location.path("/dashboard");
				$location.url($location.path());
			};

			// This function gets called by the send button on the resetPassword page
			// The button can only be pressed if the new password is valid and checked (see: passwordValid & passwordCheckValid)
			$scope.reset = function(currentPasswordField, newPasswordField) {
				$scope.formSubmitted = true;
				var userId = JSON.parse($window.sessionStorage.user).userId;
				var currentPassword = $scope.formpassworddata[currentPasswordField].$modelValue;
				var newPassword = $scope.formpassworddata[newPasswordField].$modelValue;

				changepassword = {
					userId         : JSON.parse($window.sessionStorage.user).userId,
					username       : JSON.parse($window.sessionStorage.user).userName,
					currentPassword: $scope.formpassworddata[currentPasswordField].$modelValue,
					newPassword    : $scope.formpassworddata[newPasswordField].$modelValue
				};
				// returns: Response.error or Response.LoginTokenTransfer
				AccountService.changePassword(changepassword, function(resetResult) {
					if(resetResult.errorCode != null) { //resetPassword returns an error object
						delete $scope.userSavedOk;
						$scope.error = resetResult.errorCode + " " + resetResult.errorMsg;
					}
					else {
						delete $scope.error;
						$scope.userSavedOk = true;
						var authToken = resetResult.token;
						$window.sessionStorage.authToken = authToken;

						UserService.userdata(function(user) {
							$cookieStore.put('authToken', authToken);

							$window.sessionStorage.user = JSON.stringify(user);
							//$rootScope.user = user;
						});

						delete $scope.formSubmitted;
					}
				});
			};

			$scope.buttonDisabled = function(targetForm, targetField1, targetField2, targetField3) {
				if(targetForm[targetField1].$dirty && targetForm[targetField2].$dirty && targetForm[targetField3].$dirty) {
					if(!$scope.passwordCheckInvalid(targetForm, targetField2, targetField3)) {
						return false; // enable the button
					}
					else {
						delete $scope.userSavedOk;
					}
				}

				return true;
			};

		}]);