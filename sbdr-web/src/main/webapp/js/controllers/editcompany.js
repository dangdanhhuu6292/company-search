appcontrollers.controller('EditCompanyController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$anchorScroll',
		'$routeParams', '$cookies', 'ExceptionCompanyDataStorage', 'maxFieldLengths',
		'companyData', 'AccountService', 'CompanyService',
		function($window, $modal, $scope, $rootScope, $location, $anchorScroll,
				 $routeParams, $cookies, ExceptionCompanyDataStorage, maxFieldLengths, companyData,
				 AccountService, CompanyService) {
			console.log("editCompanyController");

			$scope.maxFieldLengths = maxFieldLengths;
			
			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}

			if($routeParams.exceptionCompany && $routeParams.exceptionCompany == 'true') {
				$scope.isCustomMelding = true;
				$scope.exceptionCompany = ExceptionCompanyDataStorage.getExceptionCompanyData();
			}
			else {
				delete $scope.exceptionCompany;
				$scope.isCustomMelding = false;
			}

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty;

				if(result_dirty) {
					delete $scope.error;
				}

				if(targetField == 'telefoonnummer') {
					if(typeof $scope.account.klant != 'undefined') {
						if($scope.account.bedrijf.telefoonnummer == '') {
							result_invalid = true;
						}
					}
				}

				return result_dirty && result_invalid;
			};

			$scope.back = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			$scope.addressEditAllowed = function() {
				var roles = JSON.parse($window.sessionStorage.user).roles;

				return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd')
			};

			$scope.editCompanyData = function() {
				cleanAddressForm();

				$scope.editAddressOn = $scope.editAddressOn != true;
				// set disableAddressOn to false to disable address editing for now. Due sync issue with KvK provider data
				if ($scope.editAddressOn)
					 $scope.disableAddressOn = false;
				else
					delete $scope.disableAddressOn;

			};

			cleanAddressForm = function() {
				delete $scope.error;
				delete $scope.accountSavedOk;

				// set form fields not dirty
				$scope.formbedrijfsgegevens.$setPristine();
			};

			if(typeof $scope.init == "undefined") {
				if(typeof companyData.company.errorCode != 'undefined') {
					$scope.error = companyData.company.errorMsg;

					$scope.account = {
						bedrijf         : null,
						referentieIntern: 0
					};
					$scope.account.bedrijf.adresOk = true;
					$scope.adresOkFalseOnLoad = !$scope.account.bedrijf.adresOk;
				} else {
					$scope.account = {
						bedrijf         : companyData.company.bedrijf,
						referentieIntern: companyData.company.referentieIntern,
						klant           : companyData.company.klant
					};

					$scope.account.bedrijf.adresOk = $scope.account.bedrijf.adresOk == true;
					$scope.adresOkFalseOnLoad = !$scope.account.bedrijf.adresOk;
				}

				//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
				$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

				$scope.init = true;
			}

			$scope.resolveException = function(customMeldingId, bedrijfId) {
				delete $scope.exceptionResolved;
				CompanyService.resolveException({
					customMeldingId: customMeldingId,
					bedrijfId      : bedrijfId
				}, function(response) {
					if(typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.exceptionResolved = true;
					}
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			$scope.ignoreException = function(customMeldingId, bedrijfId) {
				delete $scope.exceptionIgnored;
				CompanyService.ignoreException({
					customMeldingId: customMeldingId,
					bedrijfId      : bedrijfId
				}, function(response) {
					if(typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.exceptionIgnored = true;
					}
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			$scope.updateCompanyData = function() {
				delete $scope.error;

				AccountService.updateBedrijfData($scope.account.bedrijf, function(result) {
					if(typeof result.errorCode != 'undefined') {
						// set form fields not dirty
						$scope.formbedrijfsgegevens.$setPristine();
						$scope.error = result.errorCode + ' ' + result.errorMsg;
						$scope.accountSavedOk = false;
					}
					else {
						$scope.editCompanyData();
						$scope.accountSavedOk = true;
						if($scope.account.bedrijf.adresOk) {
							$scope.adresOkFalseOnLoad = false;
						}
					}
				});
			};

			$scope.annuleren = function() {

				var modalInstance = $modal.open({
					templateUrl: 'removechanges.html',
					controller : RemoveChangesController,
					size       : 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function(result) {
					if(result.remove) {
						$scope.cancelUserDetails();
					}
				}, function() {
					console.log('Modal dismissed at: ' + new Date());
				});
			};

			var RemoveChangesController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeChangesOk = function() {
					$scope.remove = true;
					$scope.closeRemoveChangesModal();
				};

				$scope.closeRemoveChangesModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

		}]);