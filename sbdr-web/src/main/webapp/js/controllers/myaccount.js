appcontrollers.controller('MyAccountController', [
	'$window', '$modal', '$scope', '$rootScope', '$location', '$anchorScroll', '$base64', 'maxFieldLengths',
	'$routeParams', '$cookies', 'companyAccountData', 'AccountService',
	'GebruikersService', 'GebruikerService', 'UserService',
	function($window, $modal, $scope, $rootScope, $location, $anchorScroll, $base64, maxFieldLengths,
			 $routeParams, $cookies, companyAccountData, AccountService,
			 GebruikersService, GebruikerService, UserService) {

		$scope.maxFieldLengths = maxFieldLengths;
		
		$scope.obj = {};

		var emailHelpSpan;
		var passwordHelpSpan;

		if($routeParams.showtab) {
			var showtab = $routeParams.showtab;
		}
		
		$scope.showTab = function(tab) {
			if(typeof $window.sessionStorage.user != 'undefined') {
				var roles = JSON.parse($window.sessionStorage.user).roles;

				// admin_sbdr, admin_klant, hoofd_klant, gebruiker_klant
				if(typeof roles != 'undefined') {
					//if (tab == 'overview') {
					//    if ($scope.showtab == 'overviewtab') {
					//        $scope.tabs[0].select();
					//        resetShowTab();
					//    }
					//    return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant');
					//} else
					if(tab == 'bedrijfsgegevenstab') {
						if($scope.showtab == 'bedrijfsgegevenstab') {
							$scope.tabs[0].select();
							resetShowTab();
						}
						return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'admin_sbdr_hoofd');
					} else if(tab == 'userstab') {
						if($scope.showtab == 'userstab') {
							$scope.tabs[1].select();
							resetShowTab();
						}
						emailHelpSpan = $('#emailhelp');
						passwordHelpSpan = $('#passwordhelp');

						return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'admin_sbdr_hoofd');
					} else if(tab == 'factuurtab') {
						if($scope.showtab == 'factuurtab') {
							$scope.tabs[2].select();
							resetShowTab();
						}
						return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'admin_sbdr_hoofd');
					}
				}
			}

			return false;
		};

		resetShowTab = function() {
			delete $scope.showtab;
			$rootScope.changeUrlWithoutReload("/myaccount");
		};

		if(showtab) {
			$scope.showtab = showtab;
			showtab = null;
		}

		if($scope.init == undefined) {
			$scope.formsubmitted = false;
			$scope.editBankOn = false;
			$scope.editFacturatieOn = false;

			if(typeof companyAccountData !== undefined) {
				$scope.account = {
					bedrijf         : companyAccountData.companyAccount.bedrijf,
					klant           : companyAccountData.companyAccount.klant,
					referentieIntern: companyAccountData.companyAccount.referentieIntern
				};
				
				
				if (typeof $scope.account !== 'undefined' && typeof $scope.account.klant !== 'undefined') {
					$scope.facturatie = {
							emailAdresFacturatie : $scope.account.klant.emailAdresFacturatie
					};
				} 
			}

			if(typeof $scope.account.klant != 'undefined') {
				if(typeof $scope.account.klant.akkoordIncasso == 'undefined') {
					$scope.account.klant.akkoordIncasso = false;
				}
			}

			// for isChanged check
			$scope.masterklant = angular.copy($scope.account.klant);
			$scope.masterbedrijf = angular.copy($scope.account.bedrijf);

			//$scope.account.bedrijf.adresOk = $scope.account.bedrijf.adresOk == 'true' ? true : false;

			if($scope.account && $scope.account.bedrijf) {
				$scope.adresOkFalseOnLoad = !$scope.account.bedrijf.adresOk;
			}

			var bedrijfid = null;
			if(typeof $scope.account.bedrijf) {
				bedrijfid = $scope.account.bedrijf.bedrijfId;
			}

			$scope.factuurFilterCriteria = {
				userId     : JSON.parse($window.sessionStorage.user).userId,
				bedrijfId  : bedrijfid,
				pageNumber : 1,
				sortDir    : '', // asc, desc
				sortedBy   : '',
				filterValue: '' // text to filter on
			};

			$scope.filterCriteria = {
				userId     : JSON.parse($window.sessionStorage.user).userId,
				bedrijfId  : bedrijfid,
				pageNumber : 1,
				sortDir    : '', // asc, desc
				sortedBy   : '',
				filterValue: '' // text to filter on
			};
			
			$scope.factuurCurrentPage = 1;
			$scope.factuurItemsPage = 20;
			$scope.factuurMaxSize = 5;
			$scope.factuurTotalItems = 0;

			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			$scope.totalItems = 0;

			$scope.gNewUser = false;

			//if ($scope.account.klant)
			//	$scope.account.klant.nietBtwPlichtig = $scope.account.klant.nietBtwPlichtig == 'true' ? true : false;

			var users = [];

			$scope.masteraccount = angular.copy($scope.account);

			$scope.init = true;
		}

		$scope.EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;

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
		
		$scope.gebruikerrollen = [
			{id: 'Gebruiker', description: 'Standaard gebruiker'},
			{id: 'HoofdGebruiker', description: 'Hoofdgebruiker'},
			{id: 'BedrijfManaged', description: 'Bedrijf manager (extern)'},			
		]

		//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
		$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

		if($routeParams.searchurl) {
			searchurl = $routeParams.searchurl;
		} else {
			searchurl = '$dashboard';
		}

		$scope.userIdSelected = null;

		var bankeditIcon = $('#bankeditIcon');

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

		var RemoveUserController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
			$scope.remove = false;

			$scope.removeUserOk = function() {
				$scope.remove = true;
				$scope.closeRemoveUserModal();
			};

			$scope.closeRemoveUserModal = function() {
				var result = {
					remove: $scope.remove
				};

				$modalInstance.close(result); // $scope.remove
			};
		}];

		$scope.$watch('account.klant.bankrekeningNummer', function(bankrekeningNummer) {
			if(bankrekeningNummer) {
				if(bankrekeningNummer.length == 18) {
					AccountService.ibanCheck({ibannummer: bankrekeningNummer}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							delete $scope.bankrekeningNummerInvalid;
							$scope.error = result.errorMsg;
						} else {
							if(result.result == false) {
								$scope.bankrekeningNummerInvalid = true;
							} else {
								delete $scope.bankrekeningNummerInvalid;
							}
						}

					});
				} else {
					$scope.bankrekeningNummerInvalid = true;
				}
			}
		}, true);

		$scope.$watch('account.klant.btwnummer', function(btwnummer) {
			validateBtwNummer(btwnummer);
		}, true);

		$scope.isProspect = function() {
			if(typeof $window.sessionStorage.user != 'undefined') {
				return !!JSON.parse($window.sessionStorage.user).prospect;
			} else {
				return false;
			}
		};

		$scope.encoded = function(astring) { return $base64.encode(astring) };


		$scope.isAdresOk = function() {
			if(typeof $window.sessionStorage.user != 'undefined') {
				return JSON.parse($window.sessionStorage.user).adresOk;
			} else {
				return false;
			}
		};

		$scope.notAllowedNewUser = function() {
			if($scope.isProspect() || $scope.isAdresOk() == false) {
				return 'Alleen geverifiëerde klanten kunnen nieuwe gebruikers aanmaken';
			} else {
				return '';
			}
		};

		$scope.activateKlant = function() {
			delete $scope.error;
			delete $scope.verificationError;
			delete $scope.klantActivated;
			if($scope.obj.formactivatiegegevens != undefined) {
				$scope.obj.formactivatiegegevens.$setPristine();
			}

			var userId = JSON.parse($window.sessionStorage.user).userId;
			AccountService.activateCustomerBriefCode({
				activationcode: $scope.activatiecode,
				userid        : userId
			}, function(response) {
				if(response.errorCode != undefined) {
					$scope.verificationError = response.errorMsg;

					// for isChanged check
					$scope.masteractivatiecode = angular.copy($scope.activatiecode);
				} else {
					// fetch new user details with changed klantstatus = ACT
					UserService.userdata(function(user) {

						$window.sessionStorage.user = JSON.stringify(user);

						$scope.klantActivated = true;
					});
				}
			});
		};

		$scope.isAdmin = function() {
			if($window.sessionStorage.user != undefined) {
				var roles = JSON.parse($window.sessionStorage.user).roles;
				return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
			} else {
				return false;
			}
		};

		$scope.addressEditAllowed = function() {
			if($window.sessionStorage.user != undefined) {
				var roles = JSON.parse($window.sessionStorage.user).roles;

				return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
			} else {
				return false;
			}
		};

		$scope.annuleren = function(showChangePwd) {

			var modalInstance = $modal.open({
				templateUrl: 'removechanges.html',
				controller : RemoveChangesController,
				size       : 'lg'
				//resolve: { removeNotification: removeNotification() }
			});

			modalInstance.result.then(function(result) {
				if(result.remove) {
					delete $scope.userDetails;
					resetPage();

					if(showChangePwd) {
						$scope.userChangePassword = true;
					}
				}
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		};

		$scope.back = function() {
			url = gotoDurl(searchurl);
			if(url != null) {
				$location.path(url);
				$location.url($location.path());
			}
		};

		$scope.btwNummerAanwezig = function(klant) {
			if(klant != null && typeof klant != 'undefined') {
				var btwNummer = klant.btwnummer;
				if(btwNummer != undefined && btwNummer != null && btwNummer != '') {
					$scope.account.klant.nietBtwPlichtig = false;
					if($scope.addressEditAllowed()) {
						if($scope.obj.formaccountgegevensadmin != undefined) {
							if($scope.obj.formaccountgegevensadmin.nietBtwPlichtig != undefined) {
								$scope.obj.formaccountgegevensadmin.nietBtwPlichtig.$setPristine();
							}
						}
					} else {
						if($scope.obj.formaccountgegevens != undefined) {
							if($scope.obj.formaccountgegevens.nietBtwPlichtig != undefined) {
								$scope.obj.formaccountgegevens.nietBtwPlichtig.$setPristine();
							}
						}
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		};

		$scope.tabClicked = function() {
			if($scope.userDetails) {
				$scope.annuleren();
			} else {
				resetPage();
			}
		};

		$scope.buttonBankDisabled = function() {
			if($scope.bankrekeningNummerInvalid) {
				return true;
			} // disabled;
			if((!$scope.isNietBtwPlichtig() && !$scope.isAdmin()) && $scope.btwnummerInvalid) {
				return true;
			} // disabled;
			if(typeof $scope.account.klant.tenaamstelling == 'undefined' || $scope.account.klant.tenaamstelling == null || $scope.account.klant.tenaamstelling == '') {
				return true;
			} // disabled;

			return false;
		};

		$scope.buttonUserDisabled = function() {
			return false;
		};

		$scope.changePassword = function() {
			resetPage();

			delete $scope.passwordhuidig
			delete $scope.password
			delete $scope.passwordcheck;

			if($scope.userDetails) {
				$scope.annuleren(true);
			} else {
				$scope.userChangePassword = true;
			}
		};

		$scope.changeUser = function(userId) {
			resetPage();

			$scope.userIdSelected = userId;
			$scope.userDetails = true;

			disableNameFields();

			user = $.grep($scope.users, function(e) {
				return e.userId == userId;
			});

			$scope.gebruikerEdit = {
				geslacht                   : user[0].geslacht,
				bedrijfId                  : user[0].bedrijfId,
				userId                     : user[0].userId,
				voornaam                   : user[0].voornaam,
				naam                       : user[0].naam,
				emailAdres                 : user[0].emailAdres,
				gebruikerTelefoonNummer    : user[0].gebruikerTelefoonNummer,
				functie                    : user[0].functie,
				roles                      : user[0].roles,
				afdeling                   : user[0].afdeling,
				hoofdgebruiker             : isHoofdgebruiker(user[0].roles),
				bedrijfManager			   : isBedrijfManager(user[0].roles),
				registratiesToegestaan	   : isRegistratiesToegestaan(user[0].roles),
				apiToegestaan			   : $scope.isApiToegestaan(user[0].roles),
				userName                   : user[0].userName,
				verantwoordelijkheidAkkoord: true
			};
			if ($scope.gebruikerEdit != '') {
				var userroles = $scope.gebruikerEdit.roles;
				if (hasRole(userroles, 'hoofd_klant') || hasRole(userroles, 'admin_sbdr_hoofd'))
					$scope.gebruikerEdit.gebruikerrol = 'HoofdGebruiker';
				else if (hasRole(userroles, 'gebruiker_klant'))
					$scope.gebruikerEdit.gebruikerrol = 'Gebruiker';
				else if (hasRole(userroles, 'bedrijf_manager'))
					$scope.gebruikerEdit.gebruikerrol = 'BedrijfManaged';
				if (hasRole(userroles, 'registraties_toegestaan'))
					$scope.gebruikerEdit.registratiesToegestaan = true;
				else
					$scope.gebruikerEdit.registratiesToegestaan = false;
				if (hasRole(userroles, 'api_toegestaan'))
					$scope.gebruikerEdit.apiToegestaan = true;
				else
					$scope.gebruikerEdit.apiToegestaan = false;
							
			}	
			if (typeof $scope.gebruikerEdit.gebruikerrol === 'undefined') {				
				$scope.gebruikerEdit.gebruikerrol = 'Gebruiker';
			}
			
			$scope.emailadrescontrole = '';
			

			//$scope.filterResult();
		};

		$scope.checkGebruikerFormInvalid = function(targetform) {
			if(targetform != undefined) {
				return targetform['voornaam'].$invalid ||
					targetform['naam'].$invalid ||
					(targetform['emailadres'].$invalid || $scope.emailInUse) ||
					targetform['functie'].$invalid ||
					$scope.emailCheckInvalid(targetform, 'emailadres', 'emailadrescontrole') ||
					targetform['telefoonnummer'].$invalid || !$scope.gebruikerEdit.verantwoordelijkheidAkkoord;
			} else {
				return true;
			}
		};

		$scope.downloadFactuur = function(fId) {
			return {action: 'factuur', factuurId: fId};
		};

		$scope.deleteUser = function(userId) {
			resetPage();
			delete $scope.userChangePassword;

			var modalInstance = $modal.open({
				templateUrl: 'removeuser.html',
				controller : RemoveUserController,
				size       : 'lg'
				//resolve: { removeNotification: removeNotification() }
			});

			modalInstance.result.then(function(result) {
				if(result.remove) {
					$scope.deleteUserDel(userId);
				}
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		};

		$scope.deleteUserDel = function(userId) {
			resetPage();

			$scope.userIdSelected = userId;

			// do delete
			GebruikerService.delete({id: userId}, function(deleteGebruikerResult) {
				if(deleteGebruikerResult.errorCode === undefined) {
					console.log('gebruiker succesfully removed');
					// after delete
					$scope.userIdSelected = null;
					delete $scope.userDetails;
					$scope.userRemovedOk = true;

					$scope.filterResult();
				} else {
					console.log('deleteGebruiker result: ' + deleteGebruikerResult.errorCode + ' ' + deleteGebruikerResult.errorMsg);
					$scope.error = deleteGebruikerResult.errorMsg;
					var old = $location.hash();
					$location.hash('alert');
					$anchorScroll();
					$location.hash(old);
					//$location.hash('alert');
					//$anchorScroll();
				}
			}, function() {
				console.log('Error updateUser');
			});

		};

		$scope.editAddressAndBank = function() {
			resetPage();

			//Address never may be updated for now. May be in future this needs to be enabled again. Relates to address NOK issue
			//$scope.editAddressOn = $scope.editAddressOn != true;

			$scope.editBank();
		};

		$scope.editBank = function() {
			$scope.account = angular.copy($scope.masteraccount);
			cleanBankForm();

			$scope.editBankOn = $scope.editBankOn != true;

		};

		$scope.editFacturatie = function() {
			$scope.facturatie = {
					emailAdresFacturatie : $scope.account.klant.emailAdresFacturatie
			};
			cleanFacturatieForm();

			$scope.editFacturatieOn = $scope.editFacturatieOn != true;

		};
		
		// Checks if the first email adres field is different than the second email adres field
		$scope.emailCheckInvalid = function(targetForm, targetField1, targetField2) {
			if(!targetForm[targetField1].$dirty && (targetForm[targetField2].$modelValue == null || targetForm[targetField2].$modelValue == '')) {
				return false;
			}

			return targetForm[targetField1].$modelValue != targetForm[targetField2].$modelValue;

		};

		$scope.fetchFacturen = function(success) {
			$rootScope.range = 'items=' + (($scope.factuurFilterCriteria.pageNumber - 1) * $scope.factuurItemsPage + 1) + '-' + (($scope.factuurFilterCriteria.pageNumber - 1) * $scope.factuurItemsPage + $scope.factuurItemsPage);
			AccountService.getFacturen($scope.factuurFilterCriteria, function(data, headers) {
				if(data != undefined && data.length > 0) {
					$scope.facturen = data;

					$scope.factuurTotalItems = paging_totalItems(headers('Content-Range'));
				} else {
					$scope.facturen = [];
					$scope.factuurTotalItems = 0;
				}
				success();
			}, function(error) {
				$scope.facturen = [];
				$scope.factuurTotalItems = 0;

				$scope.error = error;
			})
		};

		// The function that is responsible of fetching the result from the
		// server and setting the grid to the new result
		$scope.fetchResult = function(success) {
			$rootScope.range = 'items=' + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + '-' + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
			AccountService.gebruikers($scope.filterCriteria, function(data, headers) {
				console.log('Users fetched');

				$scope.users = data;

				$scope.totalItems = paging_totalItems(headers('Content-Range'));
				success();
			}, function(error) {
				console.log('Error fetching companies');
				$scope.users = [];
				$scope.totalItems = 0;
			});

		};

		// Data table functions
		$scope.filterResult = function() {

			return $scope.fetchResult(function() {
				//The request fires correctly but sometimes the ui doesn't update, that's a fix
				$scope.filterCriteria.pageNumber = 1;
				$scope.currentPage = 1;

				// selected userId delete
				$scope.userIdSelected = null;
				// rootscope range delete
				delete $scope.range;
			});
		};

		$scope.hasRole1 = function(roles, role) {
			return hasRole(roles, role);
		};

		$scope.isCurrentUser = function(userId, email) {
			if (typeof $window.sessionStorage.user !== 'undefined')
				return !!(userId == JSON.parse($window.sessionStorage.user).userId &&
				email == JSON.parse($window.sessionStorage.user).emailAdres);
			else
				return false;
		};

		$scope.isIncassoAkkoord = function() {
			if($scope.account.klant != undefined) {
				if($scope.account.klant.akkoordIncasso != undefined) {
					return $scope.account.klant.akkoordIncasso;
				} else {
					return false;
				}
			} else {
				return false;
			}
		};

		$scope.isInvalid = function(targetForm, targetField) {
			
			if(targetForm != undefined) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formsubmitted;

				if(result_dirty) {
					//delete $scope.error;
					delete $scope.verificationError;
					delete $scope.accountSavedOk;
					delete $scope.letterCreatedOk;
					delete $scope.emailInUse;

					if(targetField == 'emailadres') {
						//delete $scope.emailInUse;
						emailHelpSpan.text('Voer een geldig email adres in');
					}

					if(targetField == 'bankrekeningNummer') {
						delete $scope.bankrekeningNummerInvalid;
					}

					if((targetField == 'nietBtwPlichtig' || targetField == 'btwnummer')) {
						if(!$scope.isNietBtwPlichtig()) {
							result_invalid = !$scope.btwNummerAanwezig($scope.account.klant) || $scope.btwnummerInvalid == true;
							if(!result_invalid) {
								delete $scope.btwNrError;
							}
						} else {
							delete $scope.btwNrError;
							result_invalid = false;
						}
					}

					// only for new user!				
					if (targetField == 'verantwoordelijkheidAkkoord' && result_dirty) {
						if(typeof $scope.gebruikerEdit != 'undefined' && $scope.gebruikerEdit.userId==null)
							result_invalid = !$scope.gebruikerEdit.verantwoordelijkheidAkkoord;
						else {
							result_invalid = false;
						}
					}
				}

				return result_dirty && result_invalid;
			} else {
				return true;
			}
		};

		$scope.isKlantActivated = function() {
			if($scope.klantActivated && $scope.klantActivated == true) {
				return true;
			}

			if($window.sessionStorage.user != undefined) {
				return !JSON.parse($window.sessionStorage.user).prospect; // klantActivated == false/undefined + prospect == false. Already active
			}
			else {
				return false;
			} // no klantActivated + no user (may not occur)
		};

		$scope.isNietBtwPlichtig = function() {
			if($scope.account && $scope.account.klant && $scope.account.klant.nietBtwPlichtig == true) {
				return $scope.account.klant.nietBtwPlichtig;
			}
			else {
				return false;
			}
		};

		$scope.isUnchangedActivatieCode = function(activatiecode) {
			if($scope.masteractivatiecode != undefined) {
				return angular.equals(activatiecode, $scope.masteractivatiecode);
			} else {
				return !!( activatiecode == undefined || activatiecode == null);
			}
		};

		$scope.isUnchangedBedrijf = function(bedrijf) {
			if($scope.masterbedrijf != undefined) {
				return angular.equals(bedrijf, $scope.masterbedrijf);
			} else {
				return true;
			}
		};

		$scope.isUnchangedKlant = function(klant) {
			if($scope.masterklant != undefined) {
				return angular.equals(klant, $scope.masterklant);
			} else {
				return true;
			}
		};

		$scope.newUser = function() {
			resetPage();

			enableNameFields();

			delete $scope.userIdSelected;
			$scope.userDetails = true;

			$scope.gebruikerEdit = {
				bedrijfId                  : $scope.account.bedrijf.bedrijfId,
				userId                     : null,
				voornaam                   : null,
				geslacht                   : 'M',
				naam                       : null,
				emailAdres                 : null,
				functie                    : null,
				roles                      : createUserRole(),
				hoofdgebruiker             : false,
				bedrijfManager			   : false,
				registratiesToegestaan	   : false,
				userName                   : null,
				verantwoordelijkheidAkkoord: false
			};
			if ($scope.gebruikerEdit  != '') {
				var userroles = $scope.gebruikerEdit.roles;
				if (hasRole(userroles, 'hoofd_klant') || hasRole(userroles, 'admin_sbdr_hoofd'))
					$scope.gebruikerEdit.gebruikerrol = 'HoofdGebruiker';
				else if (hasRole(userroles, 'gebruiker_klant'))
					$scope.gebruikerEdit.gebruikerrol = 'Gebruiker';
				else if (hasRole(userroles, 'bedrijf_manager'))
					$scope.gebruikerEdit.gebruikerrol = 'BedrijfManaged';
				if (hasRole(userroles, 'registraties_toegestaan'))
					$scope.gebruikerEdit.registratiesToegestaan = true;
				else
					$scope.gebruikerEdit.registratiesToegestaan = false;
							
			}		
			if (typeof $scope.gebruikerEdit.gebruikerrol === 'undefined')
				$scope.gebruikerEdit.gebruikerrol = 'Gebruiker';
			$scope.emailadrescontrole = '';

			//$scope.filterResult();

		};

		$scope.nietBtwPlichtig = function() {
			if($scope.account && $scope.account.klant) { // && $scope.account.klant.nietBtwPlichtig) {
				$scope.account.klant.btwnummer = null;
				delete $scope.btwnummerInvalid;
				if($scope.account.klant.nietBtwPlichtig == true) {
					if($scope.addressEditAllowed()) {
						if($scope.obj.formaccountgegevensadmin.btwnummer != undefined) {
							$scope.obj.formaccountgegevensadmin.btwnummer.$dirty = true;
						}
					} else {
						if($scope.obj.formaccountgegevens.btwnummer != undefined) {
							$scope.obj.formaccountgegevens.btwnummer.$dirty = true;
						}
					}
					validateBtwNummer($scope.account.klant.btwnummer);
				}
			}
		};

		$scope.pageChanged = function(page) {
			$scope.currentPage = page;
			console.log('Page changed to: ' + $scope.currentPage);
			$scope.filterCriteria.pageNumber = $scope.currentPage;
			$scope.fetchResult(function() {
				//Nothing to do..

				//rootscope range delete
				delete $rootScope.range;
			});

		};

		$scope.factuurPageChanged = function(page) {
			$scope.factuurCurrentPage = page;
			$scope.factuurFilterCriteria.pageNumber = $scope.factuurCurrentPage;
			$scope.fetchFacturen(function() {
				delete $rootScope.factuurRange;
			});
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

		// Checks if the password is invalid based on various validation checks
		// Returns false when all validations checks are false (meaning: the password conforms to all validations)
		$scope.passwordInvalid = function(targetForm, targetField) {
			var allValidationValid = !passLengthInvalid(targetForm, targetField) && !passLetterInvalid(targetForm, targetField) && !passNumberInvalid(targetForm, targetField) && !passCapitalInvalid(targetForm, targetField);

			if(allValidationValid) {
				return false;
			} else {
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

		$scope.pwdButtonDisabled = function(targetForm, targetField1, targetField2, targetField3) {
			if(targetForm[targetField1].$dirty && targetForm[targetField2].$dirty && targetForm[targetField3].$dirty) {
				if(!$scope.passwordCheckInvalid(targetForm, targetField2, targetField3)) {
					return false; // enable the button
				}
				//else
				//	delete $scope.passwordSavedOk;
			}
			return true;
		};

		$scope.pwdCancel = function() {
			delete $scope.userChangePassword;
			resetPage();
		};

		// This function gets called by the send button on the resetPassword page
		// The button can only be pressed if the new password is valid and checked (see: passwordValid & passwordCheckValid)
		$scope.pwdReset = function(currentPasswordField, newPasswordField) {
			$scope.formsubmitted = true;

			var userId = JSON.parse($window.sessionStorage.user).userId;
			var currentPassword = $scope.obj.formpassworddata[currentPasswordField].$modelValue;
			var newPassword = $scope.obj.formpassworddata[newPasswordField].$modelValue;

			if($scope.obj.formpassworddata != undefined) {
				changepassword = {
					userId         : JSON.parse($window.sessionStorage.user).userId,
					userName       : JSON.parse($window.sessionStorage.user).userName,
					currentPassword: $scope.obj.formpassworddata[currentPasswordField].$modelValue,
					newPassword    : $scope.obj.formpassworddata[newPasswordField].$modelValue
				};

				// returns: Response.error or Response.LoginTokenTransfer
				AccountService.changePassword(changepassword, function(resetResult) {
					if(resetResult.errorCode != null) { //resetPassword returns an error object
						delete $scope.passwordSavedOk;
						$scope.error = resetResult.errorMsg;
					} else {
						$scope.passwordSavedOk = true;
						delete $scope.userChangePassword;
						var authToken = resetResult.token;
						$window.sessionStorage.authToken = authToken;

						UserService.userdata(function(user) {
							$cookieStore.put('authToken', authToken);

							$window.sessionStorage.user = JSON.stringify(user);
							//$rootScope.user = user;
						});
					}
				});
			}
		};

		updateUser = function(showModal) {
			$scope.gebruikerEdit.roles = updateRoles($scope.gebruikerEdit.roles, $scope.gebruikerEdit.gebruikerrol, $scope.gebruikerEdit.registratiesToegestaan, $scope.gebruikerEdit.apiToegestaan);

			//$scope.gebruikerEdit.userId = 10;
			GebruikersService.update($scope.gebruikerEdit, function(updateGebruikerResult) {
				if(updateGebruikerResult.errorCode == undefined) {
					if(showModal && JSON.parse($window.sessionStorage.user).userId == $scope.gebruikerEdit.userId) {
						var modalInstance = $modal.open({
							templateUrl: 'logoutafteredit.html',
							controller : logoutAfterEditController,
							size       : 'lg'
						});

						modalInstance.result.then(function() {
							$rootScope.logout();
						}, function() {
							console.log('Modal dismissed at: ' + new Date());
						});

					} // else {
						// no modal message
						// no logout, is other user... 
						// so do nothing then
					// }
					
					$scope.emailadrescontrole = '';
					$scope.obj.formgebruikergegevens.$setPristine();
					
					console.log('gebruiker succesfully updated');
					$scope.userSavedOk = true;

					$scope.saveUserCallback();
				} else {
					$scope.userSavedOk = false;
					if(updateGebruikerResult.errorCode == '102') {
						$scope.emailInUse = true;
						emailHelpSpan.text(updateGebruikerResult.errorMsg);
					} else if($scope.gebruikerEdit.userId == JSON.parse($window.sessionStorage.user).userId) {
						// fetch new user details with changed userdetails
						UserService.userdata(function(user) {

							$window.sessionStorage.user = JSON.stringify(user);
						});
					}

					console.log('updateGebruiker result: ' + updateGebruikerResult.errorCode + ' ' + updateGebruikerResult.errorMsg);
					$scope.error = updateGebruikerResult.errorMsg;
					//var old = $location.hash();
					//$location.hash('alert');
					//$anchorScroll();
					//$location.hash(old);					
				}
			}, function() {
				console.log('Error updateUser');
			});
		};

		$scope.saveUser = function() {
			
			//set form fields not dirty
			//if ($scope.obj.formgebruikergegevens != undefined)
			//	$scope.obj.formgebruikergegevens.$setPristine();

			$scope.formsubmitted = true;
			
			if (!($scope.obj.formgebruikergegevens.$invalid || $scope.checkGebruikerFormInvalid($scope.obj.formgebruikergegevens) || $scope.buttonUserDisabled())) {
				delete $scope.emailInUse;
				delete $scope.error;
				delete $scope.verificationError;
				delete $scope.email

				//new user
				if($scope.gebruikerEdit.userId == null) {
					//Email adres = userName
					$scope.gebruikerEdit.userName = $scope.gebruikerEdit.emailAdres;

					$scope.gebruikerEdit.roles = updateRoles($scope.gebruikerEdit.roles, $scope.gebruikerEdit.gebruikerrol, $scope.gebruikerEdit.registratiesToegestaan, $scope.gebruikerEdit.apiToegestaan);

					GebruikersService.create($scope.gebruikerEdit, function(createGebruikerResult) {
						if(createGebruikerResult.errorCode == undefined) {
							console.log('gebruiker succesfully created');
							$scope.userSavedOk = true;
							$scope.saveUserCallback();
							
							$scope.pageChanged(1);
							
							$scope.emailadrescontrole = '';
							$scope.obj.formgebruikergegevens.$setPristine();
														
						} else {
							$scope.userSavedOk = false;
							if(createGebruikerResult.errorCode == '102') {
								$scope.emailInUse = true;
								emailHelpSpan.text(createGebruikerResult.errorMsg);
							}
							console.log('createGebruiker result: ' + createGebruikerResult.errorMsg);
							$scope.error = createGebruikerResult.errorMsg;
							var old = $location.hash();
							$location.hash('alert');
							$anchorScroll();
							$location.hash(old);
							//$location.hash('alert');
							//$anchorScroll();
						}
					}, function() {
						console.log('Error createUser');
					});
				} else {
					if($scope.gebruikerEdit.userName !== $scope.gebruikerEdit.emailAdres) {
						changeUsername();

					} else {
						updateUser(true);
					}
					
				}
				
				$scope.formsubmitted = false;
			}
			
		};

		$scope.saveUserCallback = function() {
			delete $scope.userDetails;

			$scope.filterResult();
		};

		$scope.setPage = function(pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.setFactuurPage = function(pageNo) {
			$scope.factuurCurrentPage = pageNo;
		};
		
		$scope.setSelected = function(userId) {
			$scope.userIdSelected = userId;
		};

		$scope.updateAccountBedrijfData = function() {
			//setAllFormsPristine();
			$scope.formsubmitted = true;

			accountBedrijfUpdate(updateBedrijfAndNoLetter)
		};

		$scope.updateAccountBedrijfDataLetter = function() {
			//setAllFormsPristine();
			$scope.formsubmitted = true;

			accountBedrijfUpdate(createLetter);
		};
		
		$scope.updateFacturatieData = function() {
			$scope.formsubmitted = true;

			$scope.account.klant.emailAdresFacturatie = $scope.facturatie.emailAdresFacturatie;
			
			accountUpdate(updateFacturatie)
		};	
		
		$scope.emptyFacturatieEmailAndUpdate = function() {
			$scope.formsubmitted = true;
			
			delete $scope.account.klant.emailAdresFacturatie;
			
			accountUpdate(updateFacturatie);
		}

		$scope.miscGegevensFormIsValid = function() {
			var result = true;

			if($scope.account.bedrijf.telefoonnummer == null || $scope.account.bedrijf.telefoonnummer == '') {
				result = false;
			}

			if($scope.account.klant.nietBtwPlichtig == false && $scope.btwnummerInvalid == true) {
				result = false;
			}

			if($scope.account.klant.bankrekeningNummer == null || $scope.account.klant.bankrekeningNummer == '') {
				result = false;
			}

			if($scope.account.klant.tenaamstelling == null || $scope.account.klant.tenaamstelling == '') {
				result = false;
			}

			if($scope.account.klant.akkoordIncasso == false) {
				result = false;
			}

			return result;
		};

		$scope.updateAccountData = function() {
			$scope.formsubmitted = true;

			if($scope.buttonBankDisabled() == false) {
				if($scope.miscGegevensFormIsValid()) {
					var companyAccount = {klant: $scope.account.klant, bedrijf: $scope.account.bedrijf};
					AccountService.updateAccountData(companyAccount, function(result) {
						if(typeof result.errorCode != 'undefined') {
							$scope.error = result.errorMsg;
							$scope.accountSavedOk = false;
						} else {
							if($scope.account.bedrijf.adresOk) {
								$scope.adresOkFalseOnLoad = false;
							}
							$scope.masteraccount = angular.copy($scope.account);
							$scope.editBank();
							$scope.accountSavedOk = true;
						}
					});
				}
			}
		};
		
		accountBedrijfUpdate = function(callback) {
			var companyaccount = {
				klant  : $scope.account.klant,
				bedrijf: $scope.account.bedrijf
			};

			AccountService.updateAccountBedrijfData(companyaccount, function(result) {
				if(typeof result.errorCode != 'undefined') {
					$scope.error = result.errorMsg;
					$scope.accountSavedOk = false;
				} else {
					$scope.masteraccount = angular.copy($scope.account);
					callback();
					if($scope.account.bedrijf.adresOk) {
						$scope.adresOkFalseOnLoad = false;
					}
				}
			});
		};
		
		accountUpdate = function(callback) {
			var companyAccount = {klant: $scope.account.klant, bedrijf: $scope.account.bedrijf};
			AccountService.updateAccountData(companyAccount, function(result) {
				if(typeof result.errorCode != 'undefined') {
					$scope.error = result.errorMsg;
					$scope.accountSavedOk = false;
				} else {
					if($scope.account.bedrijf.adresOk) {
						$scope.adresOkFalseOnLoad = false;
					}
					$scope.masteraccount = angular.copy($scope.account);
					callback();
				}
			});
		};

		createLetter = function() {
			AccountService.createNewAccountLetter({bedrijfId: $scope.account.bedrijf.bedrijfId}, function(result) {
				if(typeof result.errorCode != 'undefined') {
					$scope.error = result.errorMsg;
					$scope.letterCreatedOk = false;
				} else {
					if($scope.account.bedrijf.adresOk) {
						$scope.adresOkFalseOnLoad = false;
					}

					$scope.editAddressAndBank();
					$scope.letterCreatedOk = true;
					$scope.accountSavedOk = true;
				}
			});
		};

		createUserRole = function() {
			return JSON.parse('{"item": {"key": "gebruiker_klant", "value": true}}');
		};

		cleanBankForm = function() {
			//Depending on whether this piece of commented code should stay this method is to be replaced with resetPage()
			//if (typeof $scope.account != 'undefined') {
			//    $scope.bankdata = {
			//        tenaamstelling: $scope.account.klant.tenaamstelling,
			//        bankrekeningnummer: $scope.account.klant.bankrekeningNummer
			//    }
			//}

			resetPage();
		};

		cleanFacturatieForm = function() {
			resetPage();
		};
		
		resetPage = function() {
			$scope.userIdSelected = null;

			clearAllMessages();
			
			setAllFormsPristine();
		};

		clearAllMessages = function() {
			$scope.formsubmitted = false;
			delete $scope.error;
			delete $scope.verificationError;
			delete $scope.userSavedOk;
			delete $scope.passwordSavedOk;
			delete $scope.userRemovedOk;
			delete $scope.accountSavedOk;
			delete $scope.facturatieSavedOk;
			delete $scope.letterCreatedOk;

			delete $scope.klantActivated;
			delete $scope.userChangePassword;
		};

		disableNameFields = function() {
			$scope.gNewUser = false;
		};

		enableNameFields = function() {
			$scope.gNewUser = true;
		};

		setAllFormsPristine = function() {
			if($scope.obj.formaccountgegevens != undefined) {
				$scope.obj.formaccountgegevens.$setPristine();
			}

			if($scope.obj.formaccountgegevensadmin != undefined) {
				$scope.obj.formaccountgegevensadmin.$setPristine();
			}

			if($scope.obj.formbedrijfsgegevens != undefined) {
				$scope.obj.formbedrijfsgegevens.$setPristine();
			}

			if($scope.obj.formgebruikerstabel != undefined) {
				$scope.obj.formgebruikerstabel.$setPristine();
			}

			if($scope.obj.formgebruikergegevens != undefined) {
				$scope.obj.formgebruikergegevens.$setPristine();
			}

			if($scope.obj.formpassworddata != undefined) {
				$scope.obj.formpassworddata.$setPristine();
			}

			if($scope.obj.formfacturatie != undefined) {
				$scope.obj.formfacturatie.$setPristine();
			}			
		};

		isHoofdgebruiker = function(roles) {
			return hasRole(roles, 'hoofd_klant') || hasRole(roles, 'admin_sbdr_hoofd');
		};

		isBedrijfManager = function(roles) {
			return hasRole(roles, 'bedrijf_manager');
		};

		isRegistratiesToegestaan = function(roles) {
			return hasRole(roles, 'registraties_toegestaan');
		};

		$scope.isApiToegestaan = function(roles) {
			if (hasRole(roles, 'api_toegestaan')) {
				$scope.apiUserAvailable = true;
				return true;
			} else 
				return false;
		}

		updateBedrijfAndNoLetter = function() {
			$scope.editAddressAndBank();
			$scope.accountSavedOk = true;
		};
		
		updateFacturatie = function() {
			$scope.editFacturatie();
			$scope.facturatieSavedOk = true;
		}

		updateRoles = function(roles, gebruikerrol, registratiesToegestaan, apiToegestaan) {
			var userType = '';
			var roleBedrijfManager = '';
			var roleRegistratiesToegestaan = '';
			var roleApiToegestaan = '';
			
			if (registratiesToegestaan) {
				roleRegistratiesToegestaan = '{"key": "registraties_toegestaan", "value": true}';
			}	
			
			if (apiToegestaan) {
				roleApiToegestaan = '{"key": "api_toegestaan", "value": true}';
			}
			
			if(roles != undefined && roles != null) {				
				if (hasRole(roles, 'admin_klant')) {
					userType = '{"key": "admin_klant","value":true}';
				} else if (hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd')) {
					if (gebruikerrol == 'HoofdGebruiker') {
						userType = '{"key": "admin_sbdr_hoofd", "value": true}';
					} else {
						userType = '{"key": "admin_sbdr", "value": true}';
					}
				} else if(gebruikerrol == 'HoofdGebruiker') {
					userType = '{"key": "hoofd_klant", "value": true}';
				} else if (gebruikerrol == 'Gebruiker' ) {
					userType = '{"key": "gebruiker_klant", "value": true}';
				}else if (gebruikerrol == 'BedrijfManaged') {
					userType = '{"key": "bedrijf_manager","value":true}';
				} 
			} else {
				userType = '{"key": "gebruiker_klant", "value": true}';
			}
		
			
			var result = '';
			if (roleBedrijfManager)
				result = '"{item": [' + roleBedrijfManager;
			else if (userType != '')
				result = '{"item": [' + userType;
			if (roleRegistratiesToegestaan)
				result += ',' + roleRegistratiesToegestaan;
			if (result != '') {
				if (roleApiToegestaan != '')
					result += ',' + roleApiToegestaan + ']}';
				else
					result += ']}';
			}
			else 
				result += ']}';
			
			return JSON.parse(result);
			//return JSON.parse('{' + '}');
		};

		validateBtwNummer = function(btwnummer) {
			if(btwnummer) {
				var btwnummerclean = btwnummer.toUpperCase().replace(/[^a-zA-Z 0-9]+/g, '');

				if(btwnummerclean.substr(0, 2) != 'NL' && btwnummerclean.length == 12) {
					btwnummer = 'NL' + btwnummer;
					$scope.account.klant.btwnummer = btwnummer;
				} else if(btwnummerclean.length == 14) {
					var btwnummercheck = btwnummerclean;
					if(btwnummercheck.substr(0, 2) == 'NL') {
						btwnummercheck = btwnummercheck.substr(2, btwnummercheck.length);
					}

					// validate btwnummer converted to alphanum only
					AccountService.viesCheck({btwNummer: btwnummercheck}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							//$scope.error = result.errorMsg;
							delete $scope.btwnummerInvalid;
							$scope.btwNrError = result.errorMsg;
						} else {
							if(result.result == false) {
								$scope.btwnummerInvalid = true;
							} else {
								delete $scope.btwnummerInvalid;
								delete $scope.btwNrError;
								$scope.account.klant.btwnummer = btwnummerclean;
							}
						}

					});
				} else {
					$scope.btwnummerInvalid = true;
				}
			} else {
				$scope.btwnummerInvalid = true;
			}
		};

		$scope.filterResult();
		$scope.fetchFacturen(function(){});

		changeUsername = function(meldingId) {
			var modalInstance = $modal.open({
				templateUrl: 'changeusername.html',
				controller : ChangeUsernameController,
				size       : 'lg'
			});

			modalInstance.result.then(function(result) {
				if(result.changeUsername) {
					// do user update
					// Email adres = userName
					$scope.gebruikerEdit.userName = $scope.gebruikerEdit.emailAdres;

					updateUser(false);
				}
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		};

		var logoutAfterEditController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
			$scope.closeChangeUsernameModal = function() {
				$modalInstance.close();
			};
		}];

		var ChangeUsernameController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
			$scope.changeUsername = false;

			$scope.changeUsernameOk = function() {
				$scope.changeUsername = true;
				$scope.closeChangeUsernameModal();
			};

			$scope.closeChangeUsernameModal = function() {
				var result = {
					changeUsername: $scope.changeUsername
				};

				$modalInstance.close(result); // $scope.remove
			};
		}];

	}]);