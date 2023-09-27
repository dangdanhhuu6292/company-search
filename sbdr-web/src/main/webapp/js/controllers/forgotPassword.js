appcontrollers.controller('ForgotPasswordController',
	['$scope', '$rootScope', '$location', '$anchorScroll', 'UserNsService', 'NewAccountService', 'clientip', 'maxFieldLengths',
		function ($scope, $rootScope, $location, $anchorScroll, UserNsService, NewAccountService, clientip, maxFieldLengths) {
			console.log("forgotPassCtrl");

			$scope.maxFieldLengths = maxFieldLengths;
			$scope.requestSent = false;
			
			// This function checks a field in a form on two conditions: $invalid and $dirty
			// The HTML input tag requires the REQUIRED attribute in order for $invalid to work
			$scope.isInvalid = function (targetForm, targetField) {
				var result = targetForm[targetField].$invalid && targetForm[targetField].$dirty;

				if (result) {
					// Save old hash (might be no hash), set location, scroll to it, and set old hash back(most common result: no hash in url)
					// $anchorScroll() works best when an id is provided
					var old = $location.hash();
					$location.hash(targetForm[targetField].$id);
					$anchorScroll();
					$location.hash(old);
				}

				return result;
			};

			// Controls the 'send email' button's active state based on the validity of the username field
			$scope.buttonDisabled = function (targetForm, targetField) {
				return targetForm[targetField].$invalid || $scope.requestSent;
			};

			$scope.sendPasswordToEmail = function (userId) {
				//$scope.passwordReset = false;
				
				// if already in processing request, return
				if ($scope.requestSent == true)
					return;

				$scope.requestSent = true;
								
				UserNsService.forgotPassword(userId, function (result) {
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorMsg;
						delete $scope.passwordReset;
						if (result.errorCode != '136')
							$scope.requestSent = false;
					}
					else {
						$scope.passwordReset = true;
					}
				});

				// $location.path("/login");
			};

			$scope.return = function () {
				$location.path("/login");
				$location.url($location.path());
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
				
				$scope.initialised = true;
			}

		}]);
