appservices.factory('AccountService', ['$resource', function($resource) {
	return $resource('/register/services/AccountService/account/:action', {}, {
		getFaillissementStats       : {
			method : 'GET',
			params : {'action': 'faillissementenstats'},
			headers: {'Content-Type': 'application/json'}
		},
		getCompanyAccountData       : {
			method : 'GET',
			params : {'action': 'getCompanyAccountData'},
			headers: {'Content-Type': 'application/json'}
		},
		getCompanyData              : {
			method : 'GET',
			params : {'action': 'getCompanyData'},
			headers: {'Content-Type': 'application/json'}
		},
		getFacturen                 : {
			method : 'GET',
			params : {'action': 'getFacturen'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		gebruikers                  : {
			method : 'GET',
			params : {'action': 'gebruikers'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		updateBedrijfData           : {
			method : 'POST',
			params : {'action': 'updateBedrijfData'},
			headers: {'Content-Type': 'application/json'}
		},
		updateAccountData           : {
			method : 'POST',
			params : {'action': 'updateAccountData'},
			headers: {'Content-Type': 'application/json'}
		},
		updateAccountBedrijfData    : {
			method : 'POST',
			params : {'action': 'updateAccountBedrijfData'},
			headers: {'Content-Type': 'application/json'}
		},
		changePassword              : {
			method : 'POST',
			params : {'action': 'changepassword'},
			headers: {'Content-Type': 'application/json'}
		},
		ibanCheck                   : {
			method : 'GET',
			params : {'action': 'ibanCheck'},
			headers: {'Content-Type': 'application/json'}
		},
		createNewAccountLetter      : {
			method : 'GET',
			params : {'action': 'createNewAccountLetter'},
			headers: {'Content-Type': 'application/json'}
		},
		deleteAccount               : {
			method : 'POST',
			params : {'action': 'deleteAccount'},
			headers: {'Content-Type': 'application/json'}
		},
		deleteAccountOfBedrijf      : {
			method : 'POST',
			params : {'action': 'deleteAccountOfBedrijf'},
			headers: {'Content-Type': 'application/json'}
		},
		faillissementenafgelopenweek: {
			method : 'GET',
			params : {'action': 'faillissementenafgelopenweek'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		activateCustomerBriefCode: {
			method : 'GET',
			params : {'action': 'activateCustomerBriefCode'},
			headers: {'Content-Type': 'application/json'}
		},
		viesCheck                : {
			method : 'GET',
			params : {'action': 'viesCheck'},
			headers: {'Content-Type': 'application/json'}
		}		
	});
}]);

appservices.factory('CompanyCrudService', ['$resource', function($resource) {
	return $resource('/register/services/CompanyService/company/:id', {id: '@id'});
}]);

appservices.factory('CompanyService', ['$resource', function($resource) {
	return $resource('/register/services/CompanyService/company/:action', {}, {
		bedrijfHasMonitor              : {
			method : 'GET',
			params : {'action': 'bedrijfHasMonitor'},
			headers: {'Content-Type': 'application/json'}
		},
		donate                         : {
			method : 'GET',
			params : {'action': 'donate'},
			headers: {'Content-Type': 'application/json'}
		},
		search                         : {
			method : 'GET',
			params : {'action': 'search'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		checkIfUserCanPay              : {
			method : 'GET',
			params : {'action': 'checkIfUserCanPay'},
			headers: {'Content-Type': 'application/json'}
		},
		companyData                    : {
			method : 'GET',
			params : {'action': 'companyData'},
			headers: {'Content-Type': 'application/json'},
			isArray: false
		},
		companyDataExtra                   : {
			method : 'GET',
			params : {'action': 'companyDataExtra'},
			headers: {'Content-Type': 'application/json'},
			isArray: false
		},		
		//find: { method: 'GET', params: {'action':'find/:id'}, headers: {'Content-Type': 'application/json'} },
		save                           : {
			method : 'POST',
			params : {'action': 'save'},
			headers: {'Content-Type': 'application/json'}
		},
		getOrCreateCompanyData         : {
			method : 'GET',
			params : {'action': 'getOrCreateCompanyData'},
			headers: {'Content-Type': 'application/json'}
		},
		companyHasNotifications        : {
			method : 'GET',
			params : {'action': 'companyHasNotifications'},
			headers: {'Content-Type': 'application/json'}
		},
		getTariefOfProduct             : {
			method : 'GET',
			params : {'action': 'getTariefOfProduct'},
			headers: {'Content-Type': 'application/json'}
		},
		companyNotificationData        : {
			method : 'GET',
			params : {'action': 'companyNotificationData'},
			headers: {'Content-Type': 'application/json'}
		},
		validateNotification           : {
			method : 'POST',
			params : {'action': 'validateNotification'},
			headers: {'Content-Type': 'application/json'}
		},
		notifyCompany                  : {
			method : 'POST',
			params : {'action': 'notifyCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		notifyCompanyBatch             : {
			method : 'POST',
			params : {'action': 'notifyCompanyBatch'},
			headers: {'Content-Type': 'application/json'}
		},
		removeNotificationCompany      : {
			method : 'POST',
			params : {'action': 'removeNotificationCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		updateMonitoring               : {
			method : 'POST',
			params : {'action': 'updateMonitoring'},
			headers: {'Content-Type': 'application/json'}
		},
		deleteAlert                    : {
			method : 'POST',
			params : {'action': 'deleteAlert'},
			headers: {'Content-Type': 'application/json'}
		},
		monitoringCompany              : {
			method : 'POST',
			params : {'action': 'monitoringCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		removeMonitoringCompany        : {
			method : 'POST',
			params : {'action': 'removeMonitoringCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		createNewNotificationLetter    : {
			method : 'GET',
			params : {'action': 'createNewNotificationLetter'},
			headers: {'Content-Type': 'application/json'}
		},
		companyReportData              : {
			method : 'GET',
			params : {'action': 'companyReportData'},
			headers: {'Content-Type': 'application/json'}
		},
		monitoringDetailData           : {
			method : 'GET',
			params : {'action': 'monitoringDetailData'},
			headers: {'Content-Type': 'application/json'}
		},
		createCustomMelding            : {
			method : 'POST',
			params : {'action': 'createCustomMelding'},
			headers: {'Content-Type': 'application/json'}
		},
		resolveException               : {
			method : 'GET',
			params : {'action': 'resolveException'},
			headers: {'Content-Type': 'application/json'}
		},
		ignoreException                : {
			method : 'GET',
			params : {'action': 'ignoreException'},
			headers: {'Content-Type': 'application/json'}
		},
		getNotificationsOfCompany      : {
			method : 'GET',
			params : {'action': 'getNotificationsOfCompany'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		holdNotificationCompany        : {
			method : 'POST',
			params : {'action': 'holdNotificationCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		removeHoldNotificationCompany  : {
			method : 'POST',
			params : {'action': 'removeHoldNotificationCompany'},
			headers: {'Content-Type': 'application/json'}
		},
		createInitialNotificationLetter: {
			method : 'GET',
			params : {'action': 'createInitialNotificationLetter'},
			headers: {'Content-Type': 'application/json'}
		},
		getContactMomentsOfNotification      : {
			method : 'GET',
			params : {'action': 'getContactMomentsOfNotification'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		addContactMomentNotification: {
			method : 'POST',
			params : {'action': 'addContactMomentNotification'},
			headers: {'Content-Type': 'application/json'}
		},
		removeContactMomentNotification: {
			method : 'POST',
			params : {'action': 'removeContactMomentNotification'},
			headers: {'Content-Type': 'application/json'}
		},
		addAdminNotitieNotification: {
			method : 'POST',
			params : {'action': 'addAdminNotitieNotification'},
			headers: {'Content-Type': 'application/json'}
		},
		getAdminNoteOfNotification      : {
			method : 'GET',
			params : {'action': 'getAdminNoteOfNotification'},
			headers: {'Content-Type': 'application/json'}
		}		
	});
}]);

appservices.factory('WebsiteService', ['$resource', function($resource) {

	return $resource('/register/services/DashboardService/overview/:action', {}, {
		websiteparam    : {
			method : 'GET',
			params : {'action': 'websiteparam'},
			headers: {'Content-Type': 'application/json'},
			isArray: false
		},
		saveWebsiteparam: {
			method : 'POST',
			params : {'action': 'saveWebsiteparam'},
			headers: {'Content-Type': 'application/json'}
		}
	});
}]);

appservices.factory('ExactonlineService', ['$resource', function($resource) {

	return $resource('/register/services/DashboardService/overview/:action', {}, {
		exactonlineparam: {
			method : 'GET',
			params : {'action': 'exactonlineparam'},
			headers: {'Content-Type': 'application/json'},
			isArray: false
		}
	});
}]);

appservices.factory('DashboardService', ['$resource', function($resource) {

	return $resource('/register/services/DashboardService/overview/:action', {}, {
		allCompaniesOfUser  : {
			method : 'POST',
			params : {'action': 'allCompaniesOfUser'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		companiesalert              : {
			method : 'GET',
			params : {'action': 'companiesalert'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		portfoliomonitoring         : {
			method : 'GET',
			params : {'action': 'companiesmonitor'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		reportrequested             : {
			method : 'GET',
			params : {'action': 'reportrequested'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		companiesnotified           : {
			method : 'GET',
			params : {'action': 'companiesnotified'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		notificationsstatus         : {
			method : 'GET',
			params : {'action': 'notificationsstatus'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		companiesremoved            : {
			method : 'GET',
			params : {'action': 'companiesremoved'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		objectionsadmin          : {
			method : 'GET',
			params : {'action': 'objectionsadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		supportticketsadmin          : {
			method : 'GET',
			params : {'action': 'supportticketsadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},		
		notificationsadmin          : {
			method : 'GET',
			params : {'action': 'notificationsadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		notificationsofprospectadmin: {
			method : 'GET',
			params : {'action': 'notificationsofprospectadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		customersadmin              : {
			method : 'GET',
			params : {'action': 'customersadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		generaladmin                : {
			method : 'GET',
			params : {'action': 'generaladmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: false
		},
		searchresultsadmin          : {
			method : 'GET',
			params : {'action': 'searchresultsadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		exceptioncompaniesadmin     : {
			method : 'GET',
			params : {'action': 'exceptioncompaniesadmin'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		}
	});
}]);

appservices.factory('DocumentService', ['$resource', function($resource) {
	return $resource('/register/services/DocumentService/document/:action', {}, {
		downloadAttachment   : {
			method           : 'GET',
			params           : {action: 'downloadAttachment'},
			headers          : {'Content-Type': 'application/json'},
			responseType     : 'arraybuffer',
			transformResponse: function(data, headersGetter) {
				return {data: data, headers: headersGetter};
			}
		},
		downloadDocument     : {
			method           : 'GET',
			params           : {action: 'downloadFile'},
			headers          : {'Content-Type': 'application/json'},
			responseType     : 'arraybuffer',
			transformResponse: function(data, headersGetter) {
				return {data: data, headers: headersGetter};
			}
		},
		downloadExcel        : {
			method           : 'GET',
			params           : {action: 'downloadExcel'},
			headers          : {'Content-Type': 'application/json'},
			responseType     : 'arraybuffer',
			transformResponse: function(data, headersGetter) {
				return {data: data, headers: headersGetter};
			}
		},
		downloadFactuur      : {
			method           : 'GET',
			params           : {action: 'downloadFactuur'},
			headers          : {'Content-Type': 'application/json'},
			responseType     : 'arraybuffer',
			transformResponse: function(data, headersGetter) {
				return {data: data, headers: headersGetter};
			}
		},
		downloadBatchDocument: {
			method           : 'GET',
			params           : {action: 'downloadBatchDocument'},
			headers          : {'Content-Type': 'application/json'},
			responseType     : 'arraybuffer',
			transformResponse: function(data, headersGetter) {
				return {data: data, headers: headersGetter};
			}
		}
	})
}]);

appservices.factory('DoeietsService', ['$resource', function($resource) {
	//alert("activititest call");
	return $resource('/register/services/UserService/user/doeiets', {}, {
		doeiets: {method: 'GET'}
	});
}]);

appservices.factory('ExactOnlineService', ['$resource', function($resource) {
	return $resource('/register/services/ExactOnlineService/services/:action', {}, {
		exactOnlineAccess: {
			method : 'GET',
			params : {action: 'exactOnlineAccess'},
			headers: {'Content-Type': 'application/json'}
		}
	});
}]);

appservices.factory('GebruikersService', ['$resource', function($resource) {
	return $resource('/register/services/GebruikerService/gebruiker', {}, {
		create: {method: 'POST'},
		update: {method: 'PUT'}
	})
}]);

appservices.factory('GebruikerService', ['$resource', function($resource) {
	return $resource('/register/services/GebruikerService/gebruiker/:id', {}, {
		//        update: { method: 'PUT', params: {id: '@gebruiker.userId'} },
		delete: {method: 'DELETE', params: {id: '@id'}}
	})
}]);

//appservices.factory('GebruikerService', function($resource) {
//	return $resource('/register/services/GebruikerService/gebruiker/:id', {id: '@id'});
//});
appservices.factory('GebruikersServicePath', ['$resource', function($resource) {
	return $resource('/register/services/GebruikerService/gebruiker/:action', {}, {
		gebruikerdata: {
			method : 'POST',
			params : {action: 'gebruikerdata'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		sbdrgebruiker: {
			method : 'GET',
			params : {'action': 'sbdruser'},
			headers: {'Content-Type': 'application/json'}
		}
	})
}]);

appservices.factory('InternalProcessService', ['$resource', function($resource) {
	return $resource('/register/services/InternalProcessService/internal/:action', {},
		{
			getInternalProcessRows : {
				method : 'GET',
				params : {'action': 'getInternalProcessRows'},
				headers: {'Content-Type': 'application/json'},
				isArray: true
			},
			getLetterBatchRows     : {
				method : 'GET',
				params : {'action': 'getLetterBatchRows'},
				headers: {'Content-Type': 'application/json'},
				isArray: true
			},
			printCustomerLetter    : {
				method : 'GET',
				params : {'action': 'printCustomerLetter'},
				headers: {'Content-Type': 'application/json'}
			},
			printNotificationLetter: {
				method : 'GET',
				params : {'action': 'printNotificationLetter'},
				headers: {'Content-Type': 'application/json'}
			},
			setProcessRowAsSent    : {
				method : 'GET',
				params : {'action': 'setProcessRowAsSent'},
				headers: {'Content-Type': 'application/json'}
			}
		})
}]);

appservices.factory('KortingsCodeService', ['$resource', function($resource) {
	return $resource('/register/services/KortingsCodeService/kortingscode/:action', {},
		{
			checkIfCodeIsExpired: {
				method : 'GET',
				params : {'action': 'checkIfCodeIsExpired'},
				headers: {'Content-Type': 'application/json'}
			},
			checkIfCodeIsValid  : {
				method : 'GET',
				params : {'action': 'checkIfCodeIsValid'},
				headers: {'Content-Type': 'application/json'}
			}
		})
}]);

appservices.factory('LoginService', ['$resource', function($resource) {

	return $resource('/register/services/LoginService/login/:action', {},
		{
			authenticate    : {
				method : 'POST',
				params : {'action': 'authenticate'},
				headers: {'Content-Type': 'application/json'}
			},
			activateUser    : {
				method : 'GET',
				params : {'action': 'activateUser'},
				headers: {'Content-Type': 'application/json'}
			},
			activateCustomer: {
				method : 'GET',
				params : {'action': 'activateCustomer'},
				headers: {'Content-Type': 'application/json'}
			},
			websocketdata: {
				method : 'GET',
				params : {'action': 'websocketdata'},
				headers: {'Content-Type': 'application/json'}
			}
		}
	)
}]);

appservices.factory('NewAccountService', ['$resource', function($resource) {

	return $resource('/register/services/NewAccountService/newaccount/:action', {}, {
		search                   : {
			method : 'GET',
			params : {'action': 'searchCompany'},
			headers: {'Content-Type': 'application/json'},
			isArray: true
		},
		getApiKey : {
			method : 'GET',
			params : {'action': 'getApiKey'},
			headers: {'Content-Type': 'application/json'}
		},
		getApiKey2 : {
			method : 'GET',
			params : {'action': 'getApiKey2'},
			headers: {'Content-Type': 'application/json'}
		},		
		getCompanyNewAccountData : {
			method : 'GET',
			params : {'action': 'getCompanyNewAccountData'},
			headers: {'Content-Type': 'application/json'}
		},
		recaptchasitekey         : {
			method : 'GET',
			params : {'action': 'recaptchasitekey'},
			headers: {'Content-Type': 'application/json'}
		},
		verifyRecaptcha          : {
			method : 'POST',
			params : {'action': 'verifyRecaptcha'},
			headers: {'Content-Type': 'application/json'}
		},
		createAccount            : {
			method : 'POST',
			params : {'action': 'createAccount'},
			headers: {'Content-Type': 'application/json'}
		}
	});
}]);

appservices.factory('NewsService', ['$resource', function($resource) {

	return $resource('rest/news/:id', {id: '@id'});
}]);

appservices.factory('SbdrService', ['$resource', function($resource) {
	//alert("activititest call");
	return $resource('/register/services/ActivititestService/rest/activititest', {}, {
		doit: {method: 'GET'}
	});
}]);

appservices.factory('SupportService', ['$resource', function($resource) {

	return $resource('/register/services/SupportService/support/:action', {},
		{
			createsupportticket            : {
				method : 'POST',
				params : {'action': 'createSupportTicket'},
				headers: {'Content-Type': 'application/json'}
			},
			createsupportticketbestand     : {
				method          : 'POST',
				params          : {'action': 'createSupportTicketBestand'},
				transformRequest: angular.identity,
				headers         : {'Content-Type': undefined}
			},
			createsupportticketreaction    : {
				method : 'POST',
				params : {'action': 'createSupportTicketReaction'},
				headers: {'Content-Type': 'application/json'}
			},
			findsupportticketlistforuser   : {
				method : 'GET',
				params : {'action': 'findSupportTicketListForUser'},
				headers: {'Content-Type': 'application/json'},
				isArray: true
			},
			findsupportticketsbyreferenceno: {
				method : 'GET',
				params : {'action': 'findSupportTicketsByReferenceNo'},
				headers: {'Content-Type': 'application/json'},
				isArray: true
			},
			pickupsupportticket            : {
				method : 'GET',
				params : {'action': 'pickUpSupportTicket'},
				headers: {'Content-Type': 'application/json'}
			},
			sbdracceptobjection            : {
				method : 'POST',
				params : {'action': 'sbdrAcceptObjection'},
				headers: {'Content-Type': 'application/json'}
			},
			sbdrdiscardobjection           : {
				method : 'POST',
				params : {'action': 'sbdrDiscardObjection'},
				headers: {'Content-Type': 'application/json'}
			}
		}
	);
}]);

appservices.factory('UserNsService', ['$resource', function($resource) {

	return $resource('/register/services/UserNsService/user/:action', {}, {
		findActivationCode: {
			method : 'POST',
			params : {'action': 'findActivationCode'},
			headers: {'Content-Type': 'application/json'}
		},
		forgotPassword: {
			method : 'POST',
			params : {'action': 'forgotpassword'},
			headers: {'Content-Type': 'application/json'}
		},
		resetPassword : {
			method : 'POST',
			params : {'action': 'resetpassword'},
			headers: {'Content-Type': 'application/json'}
		},
		activateUser  : {
			method : 'POST',
			params : {'action': 'activateuser'},
			headers: {'Content-Type': 'application/json'}
		}
	});
}]);

appservices.factory('UserService', ['$resource', function($resource) {

	return $resource('/register/services/UserService/user/:action', {}, {
		userdata      : {
			method : 'GET',
			params : {'action': 'userdata'},
			headers: {'Content-Type': 'application/json'}
		},
		userdataOfCompany      : {
			method : 'GET',
			params : {'action': 'userdataOfCompany'},
			headers: {'Content-Type': 'application/json'}
		},		
		changePassword: {
			method : 'POST',
			params : {'action': 'changepassword'},
			headers: {'Content-Type': 'application/json'}
		},
		logout        : {
			method : 'GET',
			params : {'action': 'logout'},
			headers: {'Content-Type': 'application/json'}
		},
		updateShowHelp: {
			method : 'POST',
			params : {'action': 'updateShowHelp'},
			headers: {'Content-Type': 'application/json'}
		}
	});
}]);

appservices.factory('DashboardNumberTags', function() {
	var nrOfAlerts = -1;
	var nrOfReportRequested = -1;
	var nrOfPorfolio = -1;
	var nrOfNotifications = -1;
	var nrOfRemovedCompanies = -1;
	var nrOfObjectionsAdmin = -1;
	var nrOfSupportTicketsAdmin = -1;
	var nrOfCustomersAdmin = -1;
	var nrOfNotificationsAdmin = -1;
	var nrOfNotificationsOfProspectAdmin = -1;
	var nrOfExceptionCompaniesAdmin = -1;
	var nrOfProcesses = -1;

	return {
		getNrOfAlerts: function() {
			return nrOfAlerts;
		},
		setNrOfAlerts: function(nr) {
			nrOfAlerts = nr;
		},

		getNrOfReportRequested: function() {
			return nrOfReportRequested;
		},
		setNrOfReportRequested: function(nr) {
			nrOfReportRequested = nr;
		},

		getNrOfPortfolio: function() {
			return nrOfPorfolio;
		},
		setNrOfPortfolio: function(nr) {
			nrOfPorfolio = nr;
		},

		getNrOfNotifications: function() {
			return nrOfNotifications;
		},
		setNrOfNotifications: function(nr) {
			nrOfNotifications = nr;
		},

		getNrOfRemovedCompanies: function() {
			return nrOfRemovedCompanies;
		},
		setNrOfRemovedCompanies: function(nr) {
			nrOfRemovedCompanies = nr;
		},

		getNrOfObjectionsAdmin: function() {
			return nrOfObjectionsAdmin;
		},
		setNrOfObjectionsAdmin: function(nr) {
			nrOfObjectionsAdmin = nr;
		},

		getNrOfSupportTicketsAdmin: function() {
			return nrOfSupportTicketsAdmin;
		},
		setNrOfSupportTicketsAdmin: function(nr) {
			nrOfSupportTicketsAdmin = nr;
		},
		
		getNrOfCustomersAdmin: function() {
			return nrOfCustomersAdmin;
		},
		setNrOfCustomersAdmin: function(nr) {
			nrOfCustomersAdmin = nr;
		},

		getNrOfNotificationsAdmin: function() {
			return nrOfNotificationsAdmin;
		},
		setNrOfNotificationsAdmin: function(nr) {
			nrOfNotificationsAdmin = nr;
		},

		getNrOfNotificationsOfProspectAdmin: function() {
			return nrOfNotificationsOfProspectAdmin;
		},
		setNrOfNotificationsOfProspectAdmin: function(nr) {
			nrOfNotificationsOfProspectAdmin = nr;
		},

		getNrOfExceptionCompaniesAdmin: function() {
			return nrOfExceptionCompaniesAdmin;
		},
		setNrOfExceptionCompaniesAdmin: function(nr) {
			nrOfExceptionCompaniesAdmin = nr;
		},

		getNrOfProcesses: function() {
			return nrOfProcesses;
		},
		setNrOfProcesses: function(nr) {
			nrOfProcesses = nr;
		}
	}
});

appservices.factory('NotificationsToSend', function() {
	var notificationstosend = [];
	var telefoonNummerDebiteur = null;
	var emailAdresDebiteur = null;

	return {
		getNotifications: function() {
			return notificationstosend;
		},
		setNotifications: function(notifications) {
			notificationstosend = notifications;
		},
		getTelefoonNummerDebiteur: function() {
			return telefoonNummerDebiteur;
		},
		getEmailAdresDebiteur: function() {
			return emailAdresDebiteur;
		},
		setDebiteurGegevens: function(telefoonDeb, emailDeb) {
			telefoonNummerDebiteur = telefoonDeb;
			emailAdresDeb = emailDeb;
		}
	}
});

appservices.factory('ReportDataStorage', function() {
	var reportData = null;

	return {
		getReportData: function() {
			return reportData;
		},
		setReportData: function(repData) {
			reportData = repData;
		}
	}
});

appservices.factory('ExceptionCompanyDataStorage', function() {
	var exceptionCompanyData = null;

	return {
		getExceptionCompanyData: function() {
			return exceptionCompanyData;
		},
		setExceptionCompanyData: function(exceptCompData) {
			exceptionCompanyData = exceptCompData;
		}
	}
});

appservices.factory('WebSocketService', ['$websocket', 'DashboardNumberTags', function($websocket, DashboardNumberTags) {
	var dataStream = null;

	var collection = [];
	
	var methods = {
		  openConnection: function(url, username, token) {
			  	//dataStream = $websocket('ws://localhost:8081/register/ws/WebSocketResource');
			  	///dataStream = $websocket('ws://localhost:8081/register/ws/WebSocketResource')
				dataStream = $websocket(url + '/' + username + '/' + token);
				
				dataStream.onMessage(function(message) {
				  var json = JSON.parse(message.data);
				  if (typeof json['alerts'] != 'undefined' && !isNaN(json['alerts'])) {
					  DashboardNumberTags.setNrOfAlerts(parseInt(json['alerts']));
				  } else
					  collection.push(JSON.parse(message.data));				  				  
				});
				
				dataStream.onError(function(event) {
					console.log('connection Error', event);
				});
				
				dataStream.onClose(function(event) {
					console.log('connection closed', event);
				});
				
				dataStream.onOpen(function() {
					console.log('connection open');					
				});			  
		  },
	      collection: collection,
	      status: function() {
	          return dataStream.readyState;
	      },
	      get: function() {
	        dataStream.send(JSON.stringify({ action: 'get' }));
	      },
	      send: function(message) {
	        if (angular.isString(message)) {
	        	dataStream.send(message);
	        }
	        else if (angular.isObject(message)) {
	        	dataStream.send(JSON.stringify(message));
	        }
	      }
	};

    return methods;	

	// Open a WebSocket connection
//    var dataStream = $websocket('/register/services/ws'); // wss://website.com/data
//
//    var collection = [];
//
//    dataStream.onMessage(function(message) {
//      collection.push(JSON.parse(message.data));
//    });
//
//    var methods = {
//      collection: collection,
//      get: function() {
//        dataStream.send(JSON.stringify({ action: 'get' }));
//      }
//    };
//
//    return methods;
}]);
