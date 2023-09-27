appcontrollers.controller('CreateAccountController',
	['$window', '$scope', '$rootScope', '$location', '$anchorScroll', '$routeParams', '$timeout', 'maxFieldLengths',
		'$cookies', 'NewAccountService', 'CompanyService', 'clientip', 'recaptchasitekey', 'KortingsCodeService',
		function($window, $scope, $rootScope, $location, $anchorScroll, $routeParams, $timeout, maxFieldLengths,
				 $cookies, NewAccountService, CompanyService, clientip, recaptchasitekey, KortingsCodeService) { // 'vcRecaptchaService',
			console.log("createAccountController");
			
			$scope.siteKey = recaptchasitekey;
			$scope.maxFieldLengths = maxFieldLengths;
			
			$rootScope.content_id = 'nscontent';

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$login';
			}

			//$scope.recaptchakey = '6LdvWvsSAAAAAGgUzkE2pMq5U-0_Jj9UfMxcJAfg';

			$scope.ipaddress =  clientip.ip;

			var kvknumber = $routeParams.kvknumber;
			var hoofdvestiging = $routeParams.hoofdvestiging;
			
			console.log("kvkNumber: " + kvknumber);

			$scope.EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
			//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
			$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

			gotoAnchor = function(x) {
		        var newHash = x;
		        if ($location.hash() !== newHash) {
		          // set the $location.hash to `newHash` and
		          // $anchorScroll will automatically scroll to it
		          $location.hash(x);
		        } else {
		          // call $anchorScroll() explicitly,
		          // since $location.hash hasn't changed
		          $anchorScroll();
		        }
		    };			
			
			$scope.functies = [
				{id: 'Directeur', description: 'Directeur'},
				{id: 'Bestuurder', description: 'Bestuurder'},
				{id: 'Eigenaar/Vennoot', description: 'Eigenaar/Vennoot'},
				{id: 'Bedrijfsleider', description: 'Bedrijfsleider'},
				{id: 'Financieel beheerder', description: 'Financieel beheerder'},
				{id: 'Aministrateur', description: 'Administrateur'},
				{id: 'Credit risk manager', description: 'Credit risk manager'},
				{id: 'Financieel directeur', description: 'Financieel directeur'},
				{id: 'Financieel manager', description: 'Financieel manager'},
				{id: 'Hoofd debiteurenbeheer', description: 'Hoofd debiteurenbeheer'},
				{id: 'Inkoopmanager', description: 'Inkoopmanager'},
				{id: 'Manager collections', description: 'Manager collections'},
				{id: 'Medewerker debiteurenbeheer', description: 'Medewerker debiteurenbeheer'},
				{id: 'Risk manager', description: 'Risk manager'},
				{id: 'Treasury manager', description: 'Treasury manager'},
				{id: 'Overig', description: 'Overig'}
			];

			$scope.$watch('kortingsCode.code', function(search) {
				if(typeof searchCallbackTimeout != 'undefined') {
					$timeout.cancel(searchCallbackTimeout);
				}

				searchCallbackTimeout = $timeout(function() {
					$scope.showDiscountError = false;
					if(search) {
						if(search.length >= 5) {

							KortingsCodeService.checkIfCodeIsValid({code: $scope.kortingsCode.code}, function(result) {
								if(typeof result.errorCode != 'undefined') {
									$scope.showDiscountError = true;
								} else {
									$scope.showDiscountError = !result.val;
								}
							});
						}
					}
				}, 1000);
			}, true);

			// callback ReCaptcha (hidden)
			$scope.goReCaptcha = function() {
				$scope.gRecaptchaResponse = grecaptcha.getResponse();
				
				// Call new account
				createNewAccount($scope.attachDiscountCode);
			}
			
			$scope.isInvalidCaptcha = function() {
				return !!(typeof $scope.gRecaptchaResponse == 'undefined' && $scope.formSubmitted);
			};

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var formSubmitted = $scope.formSubmitted;

				var result_dirty = targetForm[targetField].$dirty || formSubmitted;

				if(targetForm[targetField].$dirty && $scope.formSubmitted == false) {
					delete $scope.error;

				}

				if(targetField == 'email' && result_dirty) {
					delete $scope.emailInUse;
					emailHelpSpan.text("Voer een geldig e-mailadres in.");
				}

				if((targetField == 'akkoordBedrijf' || targetField == 'geenAkkoordBedrijf') && result_dirty) {
					result_invalid = !($scope.controle.bedrijfsgegevensok || $scope.controle.bedrijfsgegevensnotok);
				}

				return result_invalid && result_dirty;
			};

			var passwordHelpSpan = $("#passwordhelp");
			var emailHelpSpan = $("#emailhelp");

			// Checks if the password is invalid based on various validation checks
			// Returns false when all validations checks are false (meaning: the password conforms to all validations)
			$scope.passwordInvalid = function(targetForm, targetField) {
				var allValidationValid = !passLengthInvalid(targetForm, targetField, $scope.formSubmitted) && !passLetterInvalid(targetForm, targetField, $scope.formSubmitted) && !passNumberInvalid(targetForm, targetField, $scope.formSubmitted) && !passCapitalInvalid(targetForm, targetField, $scope.formSubmitted) && !passSpacesInvalid(targetForm, targetField, $scope.formSubmitted);

				if(allValidationValid) {
					return false;
				} else {
					var len = passLengthInvalid(targetForm, targetField, $scope.formSubmitted) ? lengthString : '';
					var letter = passLetterInvalid(targetForm, targetField, $scope.formSubmitted) ? letterString : '';
					var num = passNumberInvalid(targetForm, targetField, $scope.formSubmitted) ? numberString : '';
					var cap = passCapitalInvalid(targetForm, targetField, $scope.formSubmitted) ? capitalString : '';
					var spa = passSpacesInvalid(targetForm, targetField, $scope.formSubmitted) ? spacesString : '';
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

			$scope.emailCheckInvalid = function(targetForm, targetField1, targetField2) {
				return targetForm[targetField1].$modelValue != targetForm[targetField2].$modelValue;

			};

			//if ($scope.error === undefined)
			//{
			$scope.init = function() {

				$scope.controle = {
					bedrijfsgegevensok   : false,
					bedrijfsgegevensnotok: false,
					akkoord              : false
				};
				
				NewAccountService.getCompanyNewAccountData({
					kvknumber     : kvknumber,
					hoofdvestiging: hoofdvestiging
				}, function(data, headers) {

					console.log("getNewAccountData: " + data.bedrijf);

					if(typeof data.errorCode == 'undefined') {
						if(data != null && data.klant != null) {
							data.klant.nietBtwPlichtig = data.klant.nietBtwPlichtig == 'true';
						}
						$scope.account = data;
						$scope.bedrijfdataOk = true;

						$scope.masteraccount = angular.copy($scope.account);
					} else {
						console.log("getCompanyNewAccountData result: " + data.errorCode + " " + data.errorMsg);
						$scope.error = data.errorMsg;
						gotoAnchor('alert');

						$scope.masteraccount = null;
					}

					$scope.kortingsCode = {};

					$scope.initialised = true;
					//success();

				}, function() {
					console.log("Error getNewAccountData");
				});
			};
			//}

			$scope.changeAdresCheck = function(field) {
				if($scope.controle.bedrijfsgegevensok == true &&
					$scope.controle.bedrijfsgegevensnotok == true) {
					if(field == 'selBedrijfAdresOk') {
						$scope.controle.bedrijfsgegevensnotok = false;
					} else {
						$scope.controle.bedrijfsgegevensok = false;
					}
				}

				//if ($scope.controle.bedrijfsgegevensnotok == false)
				//	$scope.controle.opmerkingenadres = null;
			};

			createNewAccount = function(attachDiscountCode) {
				//if ($scope.isControleOk() && !account.$invalid)
				//{
				delete $scope.error;
				delete $scope.accountCreated;

				$scope.formaccountgegevens.$setPristine();
				$scope.formbedrijfsgegevens.$setPristine();

				$scope.account.klant.gebruikersNaam = $scope.account.klant.emailAdres;

				if(attachDiscountCode) {
					$scope.account.kortingsCode = $scope.kortingsCode;
				}
				delete $scope.attachDiscountCode;

				// no btwnummer hard set
				$scope.account.klant.btwnummer = "";

				console.log($scope.account.bedrijf);
				console.log($scope.account.klant);

				//$scope.account.klant.bankrekeningNummer = "321";
				$scope.account.verifyRecaptcha = {
					ipaddress: clientip.ip,
					response : $scope.gRecaptchaResponse,
					challenge: ''
				};
				$scope.account.adresOk = !$scope.controle.bedrijfsgegevensnotok;

				NewAccountService.createAccount($scope.account, function(createAccountResult) {
					if(createAccountResult.errorCode == undefined) {
						//$scope.accountCreated = true;
						$location.path("/accountcreated");
						$location.url($location.path());
						console.log("account succesfully created");
					}
					else {
						//vcRecaptchaService.reload();
						resetNoCaptcha();
						console.log("createAccount result: " + createAccountResult.errorCode + " " + createAccountResult.errorMsg);
						if(createAccountResult.errorCode == "102") {
							$scope.emailInUse = true;
							emailHelpSpan.text(createAccountResult.errorMsg);
						}
						if(createAccountResult.errorCode == '113') {
							$scope.error = createAccountResult.errorMsg + '\r\nIndien dit niet klopt neem contact op met 0900-0400567.';
						} else if(createAccountResult.errorCode == '134' || createAccountResult.errorCode == '139'){
							//$scope.error = createAccountResult.errorMsg + '\r\nUw gebruikersaccount is vanwege administratieve redenen geblokkeerd. Wilt u meer informatie hierover? Mail ons dan op support@betalingsachterstanden.nl';
							$scope.error = 'Er kan geen account worden aangemaakt vanwege administratieve redenen. Wilt u meer informatie hierover? Mail ons dan op support@crzb.nl';
						} else {
							$scope.error = createAccountResult.errorMsg;
						}

						gotoAnchor('alert');
					}

				}, function() {
					//vcRecaptchaService.reload();
					resetNoCaptcha();
					console.log("Error createAccount");
				});
				//}

			};

			resetNoCaptcha = function() {
				//grecaptcha.reset($scope.gRecaptchaWidgetId);
				grecaptcha.reset();
				delete $scope.gRecaptchaResponse;

			};

			$scope.btwNummerAanwezig = function(btwNummer) {
				if(typeof btwNummer != 'undefined' && btwNummer != null && btwNummer != "") {
					$scope.account.klant.nietBtwPlichtig = false;
					$scope.formaccountgegevens.nietBtwPlichtig.$setPristine();
					return true;
				}
				else {
					return false;
				}
			};

			$scope.isNietBtwPlichtig = function() {
				if($scope.account && $scope.account.klant && $scope.account.klant.nietBtwPlichtig) {
					return $scope.account.klant.nietBtwPlichtig;
				} else {
					return false;
				}
			};

			$scope.nietBtwPlichtig = function() {
				if($scope.account && $scope.account.klant) { // && $scope.account.klant.nietBtwPlichtig) {
					$scope.account.klant.btwnummer = null;
					$scope.formaccountgegevens.btwnummer.$setPristine();
					delete $scope.btwnummerInvalid;
				}
			};

			$scope.isUnchanged = function(account) {
				return angular.equals(account, $scope.masteraccount);
			};

			$scope.newAccountRequest = function() {				
				
				$scope.showDiscountError = false;

				$scope.formSubmitted = true;

				//if (typeof $scope.gRecaptchaResponse != 'undefined' && $scope.captcha)
				/**
				 * SERVER SIDE VALIDATION
				 *
				 * You need to implement your server side validation here.
				 * Send the model.captcha object to the server and use some of the server side APIs to validate it
				 * See https://developers.google.com/recaptcha/docs/
				 */
				//console.log('sending the captcha response to the server', $scope.gRecaptchaResponse); //vcRecaptchaService.data());

				if(!($scope.formaccountgegevens.$invalid || $scope.isUnchanged($scope.account) || $scope.buttonDisabled())) {
					if(typeof $scope.kortingsCode.code != 'undefined' && $scope.kortingsCode.code != '') {
						KortingsCodeService.checkIfCodeIsValid({code: $scope.kortingsCode.code}, function(result) {
							if(typeof result.errorCode != 'undefined') {
								$scope.error = result.errorMsg;
								gotoAnchor('alert');
							} else {
								if(result.val) {
									//createNewAccount(true);
									$scope.attachDiscountCode = true;
								} else {
									$scope.showDiscountError = true;
								}
							}
						});
					} else {
						$scope.attachDiscountCode = false;
						//createNewAccount(false);						
					}
					// perform captcha + in callback createNewAccount
					grecaptcha.execute();
					
					$scope.formSubmitted = false;
				}
			};

			$scope.buttonDisabled = function() {
				if($scope.controle.bedrijfsgegevensok == false && $scope.controle.bedrijfsgegevensnotok == false) {
					return true;
				} // disabled

				if($scope.passwordCheckInvalid(formaccountgegevens, 'wachtwoord', 'wachtwoord2')) {
					return true;
				} // disabled;

				if($scope.emailCheckInvalid(formaccountgegevens, 'email', 'email2') || $scope.emailInUse) {
					return true;
				} // disabled

				if($scope.btwnummerInvalid) {
					return true;
				} // disabled;

				//if(typeof $scope.gRecaptchaResponse == 'undefined') {
				//	return true;
				//} // disabled;

				//if (!$scope.account.klant ||
				//		(typeof $scope.account.klant.nietBtwPlichtig === 'undefined' && typeof $scope.btwnummerInvalid === 'undefined') ||
				//		(!$scope.account.klant.nietBtwPlichtig && (typeof $scope.account.klant.btwnummer == 'undefined' || $scope.account.klant.btwnummer == null)))
				//	return true; // disabled

				return false;
			};

			$scope.isControleOk = function() {
				result = true;

				if(typeof $scope.controle != 'undefined') {
					if(typeof $scope.controle.wachtwoord != 'undefined') {
						if($scope.controle.wachtwoord != $scope.account.wachtwoord.wachtwoord) {
							result = false;
						}
					}
					else {
						result = false;
					}

					if(typeof $scope.controle.emailAdres != 'undefined') {
						if($scope.controle.emailAdres != $scope.account.klant.emailAdres) {
							result = false;
						}
					}
					else {
						result = false;
					}

					if(typeof $scope.controle.akkoord == 'undefined') {
						result = false;
					}

					if(typeof $scope.controle.akkoordTekst == 'undefined') {
						result = false;
					}
				}
				else {
					result = false;
				}

				return result;
			};

			$scope.returnToSearch = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			if(typeof $scope.initialised == 'undefined') {
				$scope.formSubmitted = false;
				$scope.showDiscountError = false;

				$scope.init();
				$scope.initialised = true;
			}
		}]);