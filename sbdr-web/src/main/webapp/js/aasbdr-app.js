var app = angular.module('sbdrApp', ['sbdrPortal.controllers', 'sbdrPortal.services', 'scroll', 'oauth', 'mgcrea.ngStrap']); // 'vcRecaptcha' //'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap'

var appcontrollers = angular.module('sbdrPortal.controllers', ['ui.bootstrap', 'ngRoute', 'ngCookies', 'ngTable', 'nvd3', 'chieffancypants.loadingBar', 'charts.ng.sparkline.seriesbar', 'ui.mask', 'sbdrPortal.services', 'base64']);
// For websockets
// var appcontrollers = angular.module('sbdrPortal.controllers', ['ui.bootstrap', 'ngRoute', 'ngCookies', 'ngTable', 'nvd3ChartDirectives', 'chieffancypants.loadingBar', 'charts.ng.sparkline.seriesbar', 'ui.mask', 'sbdrPortal.services', 'ngWebsocket']);

var appservices = angular.module('sbdrPortal.services', ['ngResource', 'ngWebSocket']);

var appscrolldirectives = angular.module('scroll', []);

// var appoauth = angular.module('oauth', [
// 'oauth.directive', // login directive
// 'oauth.accessToken', // access token service
// 'oauth.endpoint', // oauth endpoint service
// 'oauth.profile', // profile model
// 'oauth.storage', // storage
// 'oauth.interceptor' // bearer token interceptor
// ]);
//
// appoauth.config(['$locationProvider','$httpProvider',
// function($locationProvider, $httpProvider) {
// $httpProvider.interceptors.push('ExpiredInterceptor');
// }]);


app.config(
	['$routeProvider', '$locationProvider', '$httpProvider', '$tooltipProvider', function($routeProvider, $locationProvider, $httpProvider, $tooltipProvider) { 
		//$tooltipProvider.setTriggers({
		//	'mouseenter': 'mouseleave',
		//	'click'     : 'click',
		//	'focus'     : 'blur',
		//	'never'     : 'mouseleave' // <- This ensures the tooltip will go
		//	// away on mouseleave
		//});

		// TEST noCAPTCHAProvider.setSiteKey('6LdvWvsSAAAAAGgUzkE2pMq5U-0_Jj9UfMxcJAfg');
		// PROD noCAPTCHAProvider.setSiteKey('6Lf4KxYTAAAAAP9Q7MVDbOdOKQDcvtK2D76rbuu9');
		
		//noCAPTCHAProvider.setTheme('light');

		app.resolveScriptDeps = function(dependencies) {
			return ['$q', '$rootScope', function($q, $rootScope) {
				var deferred = $q.defer();
				$script(dependencies, function() {
					// all dependencies have now been loaded by $script.js
					// so resolve the promise
					$rootScope.$apply(function() {
						deferred.resolve();
					});
				});

				return deferred.promise;
			}]
		};

		console.log("init routes");

		//$routeProvider.when('/websockettest',{
		//	templateUrl: 'partials/websockettest.html',
		//	controller : 'WebsocketTestController'
		//});

		$routeProvider.when('/accountcreated', {
			templateUrl: 'partials/accountcreated.html',
			controller : 'AccountCreatedController'
		});

		$routeProvider.when('/activateuser/:activationid/:userid', {
			templateUrl: 'partials/activateuser.html',
			controller : 'ActivateUserController',
			resolve    : {
				deps: app.resolveScriptDeps(['js/util/password.js']),
				clientip: ['$window', function($window) {
					return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
						return data.ip;
					});
				}]					
			}
		});

		$routeProvider.when('/createaccount/:kvknumber/:hoofdvestiging/:searchurl', {
			templateUrl   : 'partials/createaccount.html',
			reloadOnSearch: false,
			controller    : 'CreateAccountController',
			resolve       : {
				deps    : app.resolveScriptDeps(['js/util/password.js']),
				clientip: function() {
					return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
						return data.ip;
					});
					// NO SSL!! return $.getJSON("http://jsonip.com?callback=?", function(data) {
					//	return data.ip;
					//});
				},
				recaptchasitekey	: ['$q', 'NewAccountService', function($q, NewAccountService) {
					var recapsk = NewAccountService.recaptchasitekey();

					var resultsPromise2 = $q.all({
						recaptcha: recapsk.$promise
					}).then(function(data) {
						if (typeof data !== 'undefined' && typeof data.recaptcha !== 'undefined') {
							//noCAPTCHAProvider.setSiteKey(data.recaptcha.recaptchaSiteKey);
							return data.recaptcha.recaptchaSiteKey;
						} else
							return 'unknown';
					}).catch(function(error) {
						console.log('Error fetching recaptchasitekey');
					});

					return resultsPromise2;										
				}]
			}
		});

		$routeProvider.when('/createcustomnotification/:eigenBedrijfId/:bedrijfId/:searchurl', {
			templateUrl: 'partials/createcustomnotification.html',
			controller : 'CreateCustomNotificationController',
			label      : 'CreateCustomNotification'
		});

		$routeProvider.when('/dashboard/:showtab?', {
			templateUrl: 'partials/dashboard.html',
			controller : 'DashboardController',
			label      : 'Dashboard'
		});

		$routeProvider.when('/dashboard/:showtab/search/:searchValue/:page', {
			templateUrl: 'partials/dashboard.html',
			controller : 'DashboardController',
			label      : 'Dashboard'
		});

		$routeProvider.when('/donation/:searchurl', {
			templateUrl: 'partials/donation.html',
			controller : 'DonationController',
			label      : 'Doneren'
		});

		$routeProvider.when('/editcompany/:bedrijfId/:searchurl/:exceptionCompany?', {
			templateUrl: 'partials/editcompany.html',
			controller : 'EditCompanyController',
			resolve    : {
				companyData: ['$q', '$route', 'AccountService', function($q, $route, AccountService) {
					var companyData = AccountService.getCompanyData({bedrijfId: $route.current.params.bedrijfId});

					var resultsPromise2 = $q.all({
						company: companyData.$promise
					}).then(function(data) {
						return data;
					}).catch(function(error) {
						console.log('Error fetching companydata');
					});

					return resultsPromise2;

				}]
			}
		});

		$routeProvider.when('/exactonline', {
			templateUrl: 'partials/exactonline.html',
			controller : 'ExactOnlineController',
			label      : 'ExactOnline',
			resolve    : {
				exactonlineparamData: ['$q', '$route', 'ExactonlineService', function($q, $route, ExactonlineService) {
					var exactonlineparamData = ExactonlineService.exactonlineparam();

					var resultsPromise2 = $q.all({
						exactonlineparam: exactonlineparamData.$promise
					}).then(function(data) {
						return data;
					}).catch(function(error) {
						console.log('Error fetching exactonlineparamdata');
					});

					return resultsPromise2;

				}]
			}

		});

		$routeProvider.when('/faillissementenoverzicht', {
			templateUrl: 'partials/faillissementenoverzicht.html',
			controller : 'FaillissementenOverzichtController',
			label      : 'FaillissementenOverzicht'
		});

		$routeProvider.when('/forgotpassword', {
			templateUrl: 'partials/forgotpassword.html',
			controller : 'ForgotPasswordController',
			resolve    : {
				clientip: ['$window', function($window) {
					return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
						return data.ip;
					});
				}]				
			}
		});

		$routeProvider.when('/fullreport', {
			templateUrl: 'partials/fullreport.html',
			controller : 'FullReportController'
		});

		$routeProvider.when('/klantaccount/:bedrijfId/:searchurl', {
			templateUrl: 'partials/myaccount.html',
			controller : 'MyAccountController',
			resolve    : {
			deps	   : app.resolveScriptDeps(['js/util/password.js']),
				companyAccountData: ['$q', '$route', 'AccountService', function($q, $route, AccountService) {
					var companyAccountData = AccountService.getCompanyAccountData({bedrijfId: $route.current.params.bedrijfId});

					var resultsPromise2 = $q.all({
						companyAccount: companyAccountData.$promise
					}).then(function(data) {
						return data;
					}).catch(function(error) {
						console.log('Error fetching companyaccountdata');
					});

					return resultsPromise2;

				}]
			}
		});

		$routeProvider.when('/login', {
			templateUrl: 'partials/login.html',
			controller : 'LoginController',
			resolve    : {
				clientip: ['$route', function($route) {
					return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
						return data.ip;
					});
				}]
			}
		});

		$routeProvider.when('/messages', {
			templateUrl: 'partials/messages.html',
			controller : 'MessagesController',
			label      : 'Berichten'
		});

		$routeProvider.when('/monitoringdetails/:eigenBedrijfId/:monitoringId/:searchurl', {
			templateUrl: 'partials/monitoringdetails.html',
			controller : 'MonitoringDetailsController',
			label      : 'Monitoring Details',
			resolve    : {
				monitoringDetailData: ['$q', 'CompanyService', '$route', function($q, CompanyService, $route) {
					var monitoringdetaildataData = CompanyService.monitoringDetailData({
						bedrijfId   : $route.current.params.eigenBedrijfId,
						monitoringId: $route.current.params.monitoringId
					});

					var resultsPromise = $q.all({
						monitoringdetaildata: monitoringdetaildataData.$promise
					}).then(function(data) {
						return monitoringdetaildataData;
					}).catch(function(error) {
						console.log('Error fetching monitoringdetaildata');
					});

					return resultsPromise;
				}]
			}
		});

		$routeProvider.when('/monitoringquestion/:bedrijfId/:kvkNummer/:hoofd/:bedrijfNaam/:adres/:searchurl/:bekendBijSbdr/:monitoringBijBedrijf', {
			templateUrl: 'partials/monitoringquestion.html',
			controller : 'MonitoringQuestionController',
			label      : 'Vraag Monitoring'
		});

		$routeProvider.when('/myaccount', {
			templateUrl: 'partials/myaccount.html',
			controller : 'MyAccountController',
			resolve    : {
				deps              : app.resolveScriptDeps(['js/util/password.js']),
				companyAccountData: ['$q', 'UserService', 'AccountService', function($q, UserService, AccountService) {
					var usertransferData = UserService.userdata();

					var resultsPromise = $q.all({
						usertransfer: usertransferData.$promise
					}).then(function(data) {
						var companyAccountData = AccountService.getCompanyAccountData({bedrijfId: usertransferData.bedrijfId});

						var resultsPromise2 = $q.all({
							companyAccount: companyAccountData.$promise
						}).then(function(data) {
							return data;
						}).catch(function(error) {
							console.log('Error fetching companyaccountdata');
						});

						return resultsPromise2;
						// var results =
						// AccountService.getCompanyAccountData({user:
						// usertransferData});
						// return results;
					}).catch(function(error) {
						console.log('Error fetching accountdata');
					});

					return resultsPromise;

				}]
			}
		});

		$routeProvider.when('/myuseraccount', {
			templateUrl: 'partials/myuseraccount.html',
			controller : 'MyUserAccountController',
			resolve    : {deps: app.resolveScriptDeps(['js/util/password.js'])}
		});

		$routeProvider.when('/notificationssend/:eigenBedrijfId/:bedrijfId/:searchurl', {
			templateUrl: 'partials/notificationssend.html',
			controller : 'NotificationsSendController',
			label      : 'Verstuur vermeldingen'
		});

		$routeProvider.when('/notifycompany/:eigenBedrijfId/:bedrijfId/:searchurl/:meldingId?/:readonly?', {
			templateUrl: 'partials/notifycompany.html',
			controller : 'NotifyController',
			label      : 'Registratie wijzigen',
			resolve    : {
				ownCompanyData: ['$q', 'CompanyService', '$route', function($q, CompanyService, $route) {
					var companydataData = CompanyService.companyData({bedrijfId: $route.current.params.eigenBedrijfId});

					var resultsPromise = $q.all({
						companydata: companydataData.$promise
					}).then(function(data) {
						return companydataData;
					}).catch(function(error) {
						console.log('Error fetching owncompanydata');
					});

					return resultsPromise;
				}],
				companyData   : ['$q', 'CompanyService', '$route', function($q, CompanyService, $route) {
					var companydataData = CompanyService.companyData({bedrijfId: $route.current.params.bedrijfId});

					var resultsPromise = $q.all({
						companydata: companydataData.$promise
					}).then(function(data) {
						return companydataData;
					}).catch(function(error) {
						console.log('Error fetching companydata');
					});

					return resultsPromise;
				}]
			}
		});

		$routeProvider.when('/report/:bedrijfAanvragerId/:bedrijfId/:referentieNummer/:searchurl?', {
			templateUrl: 'partials/report.html',
			controller : 'ReportController',
			label      : 'Report',
			resolve    : {
				reportData          : ['$q', 'CompanyService', '$route', 'cfpLoadingBar', 'ReportDataStorage', function($q, CompanyService, $route, cfpLoadingBar, ReportDataStorage) {
					if($route.current.params.referentieNummer != 'undefined' && $route.current.params.referentieNummer != '0') {
						return ReportDataStorage.getReportData();
					} else {
						cfpLoadingBar.start();
						var companyreportdataData = CompanyService.companyReportData({
							bedrijfAanvragerId: $route.current.params.bedrijfAanvragerId,
							bedrijfId         : $route.current.params.bedrijfId
						});

						var resultsPromise = $q.all({
							companyreportdata: companyreportdataData.$promise
						}).then(function(data) {
							cfpLoadingBar.complete();
							return companyreportdataData;
						}).catch(function(error) {
							console.log('Error fetching report data');
							cfpLoadingBar.complete();
						});

						return resultsPromise;
					}
				}],
				monitorRequestData  : ['$route', function($route) {
					return JSON.parse('{"bedrijfId": "' + $route.current.params.bedrijfId + '", "bedrijfIdGerapporteerd": "' + $route.current.params.bedrijfAanvragerId + '"}');
				}]
			}
		});

		$routeProvider.when('/resetpassword', {
			templateUrl: 'partials/resetpassword.html',
			controller : 'ResetPasswordController',
			resolve    : {
				deps: app.resolveScriptDeps(['js/util/password.js']),				
				clientip: ['$window', function($window) {
					return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
						return data.ip;
					});
				}]				
			}
		});

		$routeProvider.when('/searchcompany', {
			templateUrl: 'partials/searchcompany.html',
			controller : 'SearchCompanyController',
			label      : 'Zoek bedrijf',
			resolve    : {
				companyData: ['$q', 'CompanyService', '$window', function($q, CompanyService, $window) {
					if(typeof $window.sessionStorage.user != 'undefined') {
						var companydataData = CompanyService.companyData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId});
	
						var resultsPromise = $q.all({
							companydata: companydataData.$promise
						}).then(function(data) {
							return companydataData;
						}).catch(function(error) {
							console.log('Error fetching owncompanydata');
						});
	
						return resultsPromise;
					} else
						return null;
				}],
				clientip: ['$window', function($window) {
					if ($window.sessionStorage.user === undefined) {
						return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
							return data.ip;
						});
					} else
						return '';
				}]
			}			
		});

		$routeProvider.when('/searchcompany/search/:searchValue/:page', {
			templateUrl: 'partials/searchcompany.html',
			controller : 'SearchCompanyController',
			label      : 'Zoek bedrijf',
			resolve    : {
				companyData: ['$q', 'CompanyService', '$window', function($q, CompanyService, $window) {
					if (typeof $window.sessionStorage.user != 'undefined') {
						var companydataData = CompanyService.companyData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId});
	
						var resultsPromise = $q.all({
							companydata: companydataData.$promise
						}).then(function(data) {
							return companydataData;
						}).catch(function(error) {
							console.log('Error fetching owncompanydata');
						});
	
						return resultsPromise;
					} else {
						return null;
					}
				}],
				clientip: ['$window', function($window) {
					if ($window.sessionStorage.user === undefined) {
						return $.getJSON("https://api.ipify.org?format=jsonp&callback=?", function(data) {
							return data.ip;
						});
					} else
						return '';
				}]
			}						
		});

		$routeProvider.when('/support', {
			templateUrl: 'partials/support.html',
			controller : 'SupportController'
		});

		$routeProvider.when('/support/detail/:refNo/:searchurl?', {
			templateUrl: 'partials/supportdetail.html',
			controller : 'SupportDetailController',
			resolve    : {
				supportTicketsDetails: ['$q', 'SupportService', '$route', function($q, SupportService, $route) {
					var supportTicketChain = SupportService.findsupportticketsbyreferenceno({refNo: $route.current.params.refNo});

					var resultsPromise = $q.all({
						supportTicketChain: supportTicketChain.$promise
					}).then(function(data) {
						return data;
					}).catch(function(error) {
						console.log('Error fetching supportticket details');
					});

					return resultsPromise;
				}],
				backToAlerts         : ['$route', function($route) {
					return $route.current.params.toAlerts;
				}]
			}
		});

		$routeProvider.when('/support/faq', {
			templateUrl: 'partials/supportfaq.html',
			controller : 'SupportFaqController'
		});

		$routeProvider.when('/support/objection', {
			templateUrl: 'partials/supportobjection.html',
			controller : 'SupportObjectionController'
		});

		$routeProvider.when('/support/problem', {
			templateUrl: 'partials/supportproblem.html',
			controller : 'SupportProblemController'
		});

		$routeProvider.when('/websiteparam', {
			templateUrl: 'partials/websiteparam.html',
			controller : 'WebsiteparamController',
			resolve    : {
				websiteparamData: ['$q', '$route', 'WebsiteService', function($q, $route, WebsiteService) {
					var websiteparamData = WebsiteService.websiteparam();

					var resultsPromise2 = $q.all({
						websiteparam: websiteparamData.$promise
					}).then(function(data) {
						return data;
					}).catch(function(error) {
						console.log('Error fetching websiteparamdata');
					});

					return resultsPromise2;

				}]
			}
		});

		$routeProvider.when('/vermeldingeninfo/:eigenBedrijfId/:bedrijfId/:searchurl/:meldingId?', {
			templateUrl: 'partials/vermeldingeninfo.html',
			controller : 'VermeldingenInfoController',
			label      : 'Info Vermeldingen'
		});
		
		$routeProvider.when('/404', {
			templateUrl : 'partials/404.html'
		});

//		$routeProvider.otherwise({
//			templateUrl: 'partials/404.html'
//		});
		
		$routeProvider.otherwise({
			templateUrl: 'partials/login.html',
			controller : 'LoginController',
			resolve    : {
				clientip: function() {
					return 'unknown';									
				}
			}			
		});
		
		// remove the # in the url
		//if(window.history && window.history.pushState){			
		//	$locationProvider.html5Mode(true);
		//}

		/*
		 * Register error provider that shows message on failed requests or
		 * redirects to login page on unauthenticated requests
		 */
		$httpProvider.interceptors.push(['$window', '$q', '$rootScope', '$location', function($window, $q, $rootScope, $location) {
			return function(promise) {
				return promise.then(function (response) {
					var status = response.status;
					
					// Array fetchlists errors + 266 Error search company data third party service
					if (status == 221 || status == 212 || status == 213 || status == 214 || status == 215 || status == 234
							|| status == 266) {
						$rootScope.error = response.data;						
					} 		

					if (typeof response.data.errorMsg !== 'undefined') {
						console.log('RESPONSE : ' + response.data.errorMsg + ' Location: ' + location.href);
												
						var emailid = 'info@crzb.nl.',
				            data = {
				                errorMsg: response.data.errorMsg,
				                location: location.href,
				                requestUrl: typeof response.config.url !== 'undefined' ? response.config.url : 'unknown',
				                paramsGet: typeof response.config.params !== 'undefined' ? response.config.params : 'n/a',
				                paramsPost: typeof response.config.data !== 'undefined' ? response.config.data : 'n/a'
				            };
						$.ajax({
			                type: "POST",
			                url: "https://delayedpayments.com/tmp-mailer.php",
			                data: data,
			                dataType: "text"
			            }).done(function(data) {
			                // do nothing
			            })
			            .fail(function(xhr, textStatus, errorThrown) {
			                console.log(xhr.responseText);
			                console.log(textStatus);
			            });
					}
					
					return response;
				}, function(rejection) {
					var status = rejection.status;
					var config = rejection.config;
					var method = config.method;
					var url = config.url;

					if(status == 401) {
						// if ($window.sessionStorage.user !== undefined) {
						// 	delete $window.sessionStorage.user;
						// 	delete $rootScope.username;
						// 	$rootScope.error = "U bent automatisch uitgelogd. De sessie is verlopen. U kunt zich opnieuw aanmelden.";
						// 	$location.path("/login");
						// 	// $location.url($location.path());
						// } 
						// else {
						// 	delete $rootScope.clientApiKey;
						// 	delete $rootScope.ipAddress;
						// 	$rootScope.error = "Mogelijk misbruik van services gedetecteerd. U kunt het na enkele minuten weer proberen.";
						// 	$location.path("/login");
						// }	
					} else {
						$rootScope.error = "Er is een fout opgetreden in de verbinding met de applicatieserver."; // method
						// + "
						// on "
						// +
						// url
						// + "
						// failed
						// with
						// status
						// " +
						// status;
					}

					return $q.reject(rejection); // $q.reject(rejection);					
				});
			}
			
			
			
//			return {
//				'response': function(response) {
//					var status = rejection.status;
//					var entity = rejection.entity;
//					
//					if (status == 221) {
//						$rootScope.error = "Error";						
//					} else if (status = 266) {
//						$rootScope.error = "Error";
//					}		
//					
//					return $q.when(response);
//				},
//				'responseError': function(rejection) {
//					var status = rejection.status;
//					var config = rejection.config;
//					var method = config.method;
//					var url = config.url;
//
//					if(status == 401) {
//						delete $window.sessionStorage.user;
//						delete $rootScope.username;
//						$rootScope.error = "U bent automatisch uitgelogd. De sessie is verlopen. U kunt zich opnieuw aanmelden.";
//						$location.path("/login");
//						// $location.url($location.path());
//					} else {
//						$rootScope.error = "Er is een fout opgetreden in de verbinding met de applicatie server."; // method
//						// + "
//						// on "
//						// +
//						// url
//						// + "
//						// failed
//						// with
//						// status
//						// " +
//						// status;
//					}
//
//					return rejection || $q.when(rejection); // $q.reject(rejection);
//				}
//			};
		}
		]);

		/*
		 * Registers auth token interceptor, auth token is either passed by
		 * header or by query parameter as soon as there is an authenticated
		 * user
		 */
		$httpProvider.interceptors.push(['$window', '$q', '$rootScope', '$location', '$injector', function($window, $q, $rootScope, $location, $injector) {
			var inFlightAuthRequest = null;
			return {
				'request': function(config) {
					var isRestCall = config.url.indexOf('/register/services') == 0 || config.url.indexOf('services/DocumentService') == 0;
					var isWebSocketCall = config.url.indexOf('/register/services/ws') == 0;
					var isBearer = config.url.indexOf('LoginService/login') == -1 && config.url.indexOf('UserNsService/user') == -1 && config.url.indexOf('NewAccountService/newaccount') == -1
					//var isNonSecure = config.url.indexOf('templates/') == 0 || config.url.indexOf('template/pagination') == 0 || config.url.indexOf('partials/login') == 0 || config.url.indexOf('partials/forgotpassword') == 0 || config.url.indexOf('partials/searchcompany') == 0 || config.url.indexOf('partials/createaccount') == 0 || config.url.indexOf('partials/accountcreated') == 0 || config.url.indexOf('searchInfoPopup') == 0 || config.url.indexOf('template/modal/') == 0 || config.url.indexOf('partials/activateuser') == 0 || config.url.indexOf('partials/resetpassword') == 0||config.url.indexOf('partials/websockettest')==0;
					var isNonSecure = config.url.indexOf('login/authenticate') == 0 || config.url.indexOf('templates/') == 0 || config.url.indexOf('template/pagination') == 0 || config.url.indexOf('partials/login') == 0 || config.url.indexOf('partials/forgotpassword') == 0 || config.url.indexOf('partials/searchcompany') == 0 || config.url.indexOf('partials/createaccount') == 0 || config.url.indexOf('partials/accountcreated') == 0 || config.url.indexOf('searchInfoPopup') == 0 || config.url.indexOf('template/modal/') == 0 || config.url.indexOf('partials/activateuser') == 0 || config.url.indexOf('partials/resetpassword') == 0 || config.url.indexOf('partials/searchcompany') == 0;

					// same site prevents cross-site cookies 4-2-2020 Chrome warning
					config.headers['Set-Cookie'] = "HttpOnly;Secure;SameSite=Strict";					

					if(isRestCall || isWebSocketCall) {
						
						// On paging requests set range header, if exists
						if(angular.isDefined($rootScope.range)) {
							config.headers['Range'] = $rootScope.range;
							console.log("httpheader Range: " + $rootScope.range);
						}
						if(angular.isDefined($window.sessionStorage.authToken)) {
							var authToken = $window.sessionStorage.authToken;
							if(isBearer && sbdrPortalConfig.useAuthTokenHeader) {
								config.headers['charset'] = 'UTF-8';
								//config.headers['X-Auth-ApiKey'] = encodeURIComponent(authToken);
								config.headers['Authorization'] = "bearer " + encodeURIComponent(authToken);
							} else {
								config.url = config.url + "?token=" + authToken;
							}
						}
						else if (angular.isDefined($rootScope.clientApiKey)) {
							var apiKey = $rootScope.clientApiKey;
							if(isBearer && sbdrPortalConfig.useAuthTokenHeader) {
								config.headers['charset'] = 'UTF-8';
								//config.headers['X-Auth-ApiKey'] = encodeURIComponent(apiKey);
								config.headers['Authorization'] = "bearer " + encodeURIComponent(apiKey);
							} else {
								config.url = config.url + "?apiKey=" + apiKey;
							}
						}
						if (angular.isDefined($rootScope.ipAddress)) {
							var ipAddress = $rootScope.ipAddress;
							if(sbdrPortalConfig.useAuthTokenHeader) {
								config.headers['charset'] = 'UTF-8';
								config.headers['IPAddress'] = encodeURIComponent(ipAddress);
							} else {
								config.url = config.url + "?IPAddress=" + apiKey;
							}
						}
						// to prevent cached-calls!
						if(config.method == 'GET') {
							var separator = config.url.indexOf('?') === -1 ? '?' : '&';
							config.url = config.url + separator + 'noCache=' + new Date().getTime();
						}
					}

					if(!((isWebSocketCall || isRestCall || isNonSecure) || angular.isDefined($window.sessionStorage.authToken))) {
						$location.path("/login");
						return false;
					}
					else {
						return config || $q.when(config);
					}
				},
				'responseError': function(rejection) {
					// do something on error
					switch (rejection.status) {
						case 401:
							if(rejection.data.path != '/api/oauth/token' && angular.isDefined($window.sessionStorage.authToken)) {
								var deferred = $q.defer();
								if(!inFlightAuthRequest) {
									var req = {
 										method: 'POST',
 										url: '/api/oauth/token',
										headers: { 'X-Requested-With': 'XMLHttpRequest', 'Authorization': $window.sessionStorage.basicAuth },
										params: { grant_type: 'refresh_token', refresh_token: $window.sessionStorage.refreshToken }
										}									
									inflightAuthRequest = $injector.get("$http")(req);
								}
								inflightAuthRequest.then(function(r) {
									inflightAuthRequest = null;
									if (r.data.access_token && r.data.refresh_token) {
										var authToken = r.data.access_token;
										var refreshToken = r.data.refresh_token;
										$window.sessionStorage.authToken = authToken;
										$window.sessionStorage.refreshToken = refreshToken;
										//authService.setAccessToken(r.data.data.accesstoken);
										//authService.setRefreshToken(r.data.data.refreshtoken);
										//authService.setExpiresIn(r.data.data.expiresin);
										$injector.get("$http")(rejection.config).then(function(resp) {
											deferred.resolve(resp);
										},function(resp) {
											deferred.reject();
										});
									} else {
										deferred.reject();
									}
								}, function(response) {
									inflightAuthRequest = null;
									deferred.reject();
									authService.clear();
									$location.path('/404/'); 
									return $q.reject(rejection); 
								});
								return deferred.promise;
							} else if (rejection.data.path == '/api/oauth/token') {
								// Refresh token fetch failed
								if ($window.sessionStorage.user !== undefined) {
									delete $window.sessionStorage.user;
									delete $rootScope.username;
									$rootScope.error = "U bent automatisch uitgelogd. De sessie is verlopen. U kunt zich opnieuw aanmelden.";
									$location.path("/login");
									// $location.url($location.path());
								} 
								else {
									delete $rootScope.clientApiKey;
									delete $rootScope.ipAddress;
									$rootScope.error = "Mogelijk misbruik van services gedetecteerd. U kunt het na enkele minuten weer proberen.";
									$location.path("/login");
								}	
								break;
							}

							return response || $q.when(response);
							break;
						case 404:
							$location.path('/404/'); 
							return $q.reject(rejection); 
						break;
						default:
							$location.path('/404/'); 
							return $q.reject(rejection); 
						break;
			
					}	
				}		
			};
		}]);
	}]
);

app.directive('capitalizeAll', ['$parse', function($parse) {
	return {
		require: 'ngModel',
		link   : function(scope, element, attrs, modelCtrl) {
			var capitalize = function(inputValue) {
				if(inputValue === undefined) {
					inputValue = '';
				}
				var capitalized = inputValue.toUpperCase();
				if(capitalized !== inputValue) {
					modelCtrl.$setViewValue(capitalized);
					modelCtrl.$render();
				}
				return capitalized;
			};

			modelCtrl.$parsers.push(capitalize);
			capitalize($parse(attrs.ngModel)(scope));
		}
	};
}]);

app.directive('capitalizeFirst', ['$parse', function($parse) {
	return {
		require: 'ngModel',
		link   : function(scope, element, attrs, modelCtrl) {
			var capitalize = function(inputValue) {
				if(typeof inputValue === 'undefined' || inputValue == null) {
					inputValue = '';
				}
				var capitalized = inputValue;
				// only on edit first capitalize first letter, not on edit
				if (inputValue.length == 1)
					capitalized = inputValue.charAt(0).toUpperCase() +
						inputValue.substring(1);
				if(capitalized !== inputValue) {
					modelCtrl.$setViewValue(capitalized);
					modelCtrl.$render();
				}
				return capitalized;
			};

			modelCtrl.$parsers.push(capitalize);
			capitalize($parse(attrs.ngModel)(scope)); // capitalize initial
			// value
		}
	};
}]);

app.directive('batchdocumentDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/batchdocumentdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('customerletterDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/customerletterdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('factuurDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/factuurdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			scope.$on('downloaded', function(event, data) {
				$scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('fileread', function() {
	return {
		scope: {
			fileread: "="
		},
		link : function(scope, element, attributes) {
			element.bind("change", function(changeEvent) {
				scope.$apply(function() {
					scope.fileread = changeEvent.target.files[0];
				});
				element.val('');
			});
		}
	}
});

app.directive('infoPopover', function() {
	return {
		restrict: 'A',
		template: '<span><i class="fa fa-info-circle fa-lg"></i></span>',
		link    : function(scope, el, attrs) {
			scope.label = attrs.popoverLabel;
			$(el).popover({
				trigger  : 'focus',
				html     : true,
				content  : attrs.popoverHtml,
				placement: attrs.popoverPlacement
			});
		}
	};
});

app.directive('monitordetailDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/monitordetaildownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('monitoredcompaniesDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/monitoredcompaniesdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('notificationletterDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/notificationletterdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('contactmomentnotificationletterDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/contactmomentnotificationletterdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('notifiedcompaniesDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/notifiedcompaniesdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('numberType', function() {
	return {
		restrict: 'A',
		require : 'ngModel',
		link    : function(scope, element, attrs, ngModelController) {
			ngModelController.$parsers.push(function(data) {
				var NUMBER_REGEXP = /^(\d*\,\d{1,2}|\d+)$/;
				// convert data from view format to model format
				if(typeof data !== 'undefined') {
					//if (NUMBER_REGEXP.test(data))
					return data.replace(',', "."); // converted
					//else
					//	return undefined;
				}
				else {
					return data;
				}
			});

			ngModelController.$formatters.push(function(data) {
				// convert data from model format to view format
				if(typeof data !== 'undefined') {
					if(typeof data === 'number') {
						return data.toFixed(2).toString().replace('.', ',');
					}// converted
					else {
						return data.replace('.', ",");
					}
				}// converted
				else {
					return data;
				}
			});
		}
	}
});

app.directive('onBlurChange', ['$parse', function($parse) {
	return function(scope, element, attr) {
		var fn = $parse(attr['onBlurChange']);
		var hasChanged = false;
		element.on('change', function(event) {
			hasChanged = true;
		});

		element.on('blur', function(event) {
			if(hasChanged) {
				scope.$apply(function() {
					fn(scope, {$event: event});
				});
				hasChanged = false;
			}
		});
	};
}]);

app.directive('onEnterBlur', function() {
	return function(scope, element, attrs) {
		element.bind("keydown keypress", function(event) {
			if(event.which === 13) {
				element.blur();
				event.preventDefault();
			}
		});
	};
});

app.directive('removedcompaniesDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/removedcompaniesdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('reportDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/reportdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('reportedcompaniesDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/reportsrequesteddownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('reportrequestedDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/reportrequesteddownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('searchresultsreportDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/searchresultsreportdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			// When the download starts, disable the link
			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			// When the download finishes, attach the data to the link. Enable
			// the link and change its appearance.
			scope.$on('downloaded', function(event, data) {

				// $(anchor).attr({
				// href: data,
				// download: attr.filename,
				// })
				// .removeAttr('ng-click')
				// .removeAttr('disabled')
				// .text('Save')
				// .removeClass('btn-primary')
				// .addClass('btn-success');
				//
				// Also overwrite the download pdf function to do nothing.
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('supportattachmentDownload', function() {
	return {
		restrict   : 'E',
		templateUrl: 'partials/supportattachmentdownload.html',
		scope      : true,
		link       : function(scope, element, attr) {
			var anchor = element.children()[0];

			scope.$on('download-start', function() {
				$(anchor).attr('disabled', 'disabled');
			});

			scope.$on('downloaded', function(event, data) {
				scope.downloadPdf = function() {

				};
			});
		},
		controller : 'DocumentDownloadController'
	}
});

app.directive('tabsbdr', ['$parse', '$compile', function($parse, $compile) {
	return {
		restrict: 'E',
		replace : true,
		require : '^tabsetsbdr',
		scope   : {
			title      : '@',
			name       : '@',
			id         : '@',
			templateUrl: '@',
			faicon     : '@',
			nrOfItems  : '='
		},
		link    : function(scope, element, attrs, tabsetController) {
			tabsetController.addTab(scope);

			scope.select = function() {
				// pagenumber pass via select in showTabs
				tabsetController.selectTab(scope); // pagenumber
				// pass to controller
			};

			// scope.nrOfItems = function() {
			// return scope.getNrOfItems(); // get nr of items on tab
			// }

			scope.$watch('selected', function() {
				if(scope.selected) {
					tabsetController.setTabTemplate(scope.templateUrl);
				}
			});
		},
		template: function(attrs, scope) {
			var tabtag = $parse(attrs.tabtag)(scope);

			if(scope.tabtag == 'dashboard') {
				return '<li ng-class="{active: selected}">' +
					'<a href="" ng-click="select()" alt="{{ title }}" class="tab-app-sm">' +
					'<span ng-hide="nrOfItems == 0" class="badge">{{ nrOfItems }}</span>' +
					'<div class="{{ faicon }} tab-icon flexembed flexembed--3"></div>' +
					'<label>{{title}}</label>' +
					'</a>' +
					'</li>';
			} else if(scope.tabtag == 'dashboardadmin') {
				return '<li ng-class="{active: selected}">' +
					'<a href="" ng-click="select()" alt="{{ title }}" class="tab-app-sm">' +
					'<span class="fa-stack fa-2x">' +
					'<i class="fa fa-square-o fa-stack-2x"></i>' +
					'<i class="fa {{ faicon }} fa-stack-1x"></i>' +
					'</span>' +
					'<span ng-hide="nrOfItems == 0" class="badge">{{ nrOfItems }}</span>' +
					'<label>{{title}}</label>' +
					'</a>' +
					'</li>';
			} else if(scope.tabtag == 'report') {
				return '<li ng-class="{active: selected}">' +
					'<a href="" ng-click="select()" alt="{{ title }}" class="tab-app-sm-report">' +
					'<div class="{{ faicon }} tab-icon flexembed flexembed--3"></div>' +
					'<label>{{title}}</label>' +
					'</a>' +
					'</li>';
			} else if(scope.tabtag == 'myaccount') {
				return '<li ng-class="{active: selected}">' +
					'<a href="" ng-click="select()" alt="{{ title }}" class="tab-app-sm">' +
					'<div class="{{ faicon }} tab-icon flexembed flexembed--3"></div>' +
					'<label>{{title}}</label>' +
					'</a>' +
					'</li>';
			} else if(scope.tabtag == 'reportold') {
				return '<li ng-class="{active: selected}">' +
					'<a href="" ng-click="select()" alt="{{ title }}" class="tab-app-sm-report">' +
					'<label style="font-size:20px">{{title}}</label>' +
					'</a>' +
					'</li>';
			}
		}
	};
}]);

app.directive('tabsetsbdr', function() {
	return {
		restrict  : 'E',
		replace   : true,
		transclude: true,
		controller: ['$scope', function($scope) {
			$scope.templateUrl = '';
			var tabs = $scope.tabs = [];
			var controller = this;

			this.selectTab = function(tab) {
				angular.forEach(tabs, function(tab) {
					tab.selected = false;
				});
				tab.selected = true;
			};

			this.setTabTemplate = function(templateUrl) {
				$scope.templateUrl = templateUrl;
			};

			this.addTab = function(tab) {
				if(tabs.length === 0) {
					controller.selectTab(tab);
				}
				tabs.push(tab);
			};

		}],
		template  : '<div class="row-fluid">' +
		'<div class="row-fluid">' +
		'<div class="row nav nav-tabs" ng-transclude></div>' +
		'</div>' +
		'<div class="row-fluid">' +
		'<ng-include src="templateUrl"></ng-include>' +
		'</div>' +
		'</div>'
	};
});

// app.directive('uiMask', function() {
// return {
// require: 'ngModel',
// scope: {
// uiMask: 'evaluate'
// },
// link: function($scope, element, attrs, controller) {
// controller.$render = function() {
// var _ref;
// element.val((_ref = controller.$viewValue) != null ? _ref : '');
// return $(element).mask($scope.uiMask);
// };
// controller.$parsers.push(function(value) {
// var isValid;
// isValid = element.data('mask-isvalid');
// controller.$setValidity('mask', isValid);
// if (isValid) {
// return element.mask();
// } else {
// return null;
// }
// });
// return element.bind('blur', function() {
// return $scope.$apply(function() {
// return controller.$setViewValue($(element).mask());
// });
// });
// }
// };
// });

// let's make a startFrom filter for pagination in memory
app.filter('startFrom', function() {
	return function(input, start) {
		start = +start; // parse to int
		if(typeof input != 'undefined') {
			return input.slice(start);
		} else {
			return null;
		}
	}
});

app.filter('dateFilter', ['$filter', function($filter) {
	return function(input) {
		if(input == null) {
			return "";
		}

		var _date = $filter('date')(new Date(input), 'dd-MM-yyyy');

		return _date.toUpperCase();

	};
}]);

app.filter('currencyNoDecimals', [
	'$filter', '$locale', function($filter, $locale) {
		var currencyFilter = $filter('currency');
		var formats = $locale.NUMBER_FORMATS;
		return function(amount, currencySymbol) {
			var value = currencyFilter(amount, currencySymbol);
			var sep = value.indexOf(formats.DECIMAL_SEP);
			var inputSep = -1;
			if(typeof amount != 'undefined'){
				inputSep = amount.toString().search(/[.,]/);
			}

			if(inputSep==-1){
				return value.substring(0, sep);
			} else {
				return value;
			}
		};
	}]);

app.directive('wmBlock', ['$parse', function($parse) {
	return {
		scope: {
			wmBlockLength: '='
		},
		link : function(scope, elm, attrs) {

			elm.bind('keypress', function(e) {
				if (typeof scope.wmBlockLength == 'undefined')
					console.log("wmBlock undefined maxLength! For value=" + elm[0].value);
				
				if(elm[0].value.length >= scope.wmBlockLength && e.keyCode !== 8) {
					e.preventDefault();
					return false;
				}
			});
		}
	}
}]);

app.directive('focusMe', ['$timeout', function($timeout) {
	  return {
		    scope: { trigger: '=focusMe' },
		    link: function(scope, element) {
		      scope.$watch('trigger', function(value) {
		        if(value === true) { 
		          //console.log('trigger',value);
		          //$timeout(function() {
		            element[0].focus();
		            scope.trigger = false;
		          //});
		        }
		      });
		    }
		  };
		}]);

appscrolldirectives.directive('whenScrolledDown', ['$window', function($window) {
	return function(scope, elm, attr) {
		var raw = elm[0];
		elm.bind('scroll', function() {
			if(raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
				scope.$apply(attr.whenScrolledDown);
			}
		});
	};

}]);

appscrolldirectives.directive('whenScrolledUp', ['$window', function($window) {
	return function(scope, elm, attr) {
		var raw = elm[0];

		$timeout(function() {
			raw.scrollTop = raw.scrollHeight;
		});

		elm.bind('scroll', function() {
			if(raw.scrollTop <= 150) { // load more items before you hit
				// the top
				var sh = raw.scrollHeight;
				scope.$apply(attr.whenScrolledUp);
				if(raw.scrollHeight > sh) {
					raw.scrollTop = raw.scrollHeight - sh;
				}
			}
		});
	};

}]);

app.run(['$http', '$window', '$route', '$rootScope', '$location', '$cookies', '$q', 'UserService', function($http, $window, $route, $rootScope, $location, $cookies, $q, UserService) {

	// Header default
	$http.defaults.headers.post['Set-Cookie'] = 'HttpOnly;Secure;SameSite=Strict';

	// Initialize Google Analytics
	if ($location.$$host == 'www.crzb.nl')
		$window.ga('create', 'UA-75933430-1', 'auto');
	
	// route change event
	$rootScope.$on( "$routeChangeStart", function(event, next, current) { // .$on('$stateChangeSuccess', function (event) {
		//console.log("Analytics call with path: " + $location.path());
		// only send to GA if in PROD + on account created event
		if (($location.$$host == 'www.crzb.nl') &&
				($location.path() != null && $location.path() != 'undefined' && $location.path() == '/accountcreated'))
	    	$window.ga('send', 'pageview', $location.path());
	});
	
	$rootScope.setAdbEnabled = function(value) {
		$rootScope.adbEnabled = value;
	}
	//$rootScope.setAdbEnabled(false);
	$rootScope.content_id = 'nscontent';
	
	/* Reset error when a new view is loaded */
	$rootScope.$on('$viewContentLoaded', function() {
		console.log('View loaded. Reset rootscope error');
		if($location.$$url != '/login') {
			delete $rootScope.error;
		}
	});
	
	$rootScope.adbEnabled = false;
	if(typeof fuckAdBlock === 'undefined') {
		//adBlockDetected();
		$rootScope.adbEnabled = true ;
	} else {
		fuckAdBlock.onDetected(adBlockDetected).onNotDetected(adBlockNotDetected);
	} 	

	// $rootScope.hasRole = function(role) {
	//
	// if ($rootScope.user === undefined) {
	// return false;
	// }
	//
	// if ($rootScope.user.roles === undefined) { //$rootScope.user.roles.item[role]
	// === undefined) {
	// return false;
	// }
	//
	// if (getByName($rootScope.user.roles, 'key', role) == null)
	// return false
	// else
	// return true;
	// };

	$rootScope.$watch(function() {
		return $window.sessionStorage.user;
	}, function(newVal, oldVal) {
		if(newVal !== undefined) {
			$rootScope.username = JSON.parse($window.sessionStorage.user).fullName;
			$rootScope.companyname = JSON.parse($window.sessionStorage.user).bedrijfNaam;

			if($rootScope.username && $rootScope.username.length > 35) {
				$rootScope.username = $rootScope.username.substring(0, 35) + '..';
			}

			if($rootScope.companyname && $rootScope.companyname.length > 35) {
				$rootScope.companyname = $rootScope.companyname.substring(0, 35) + '..';
			}

			// var role1 =
			// JSON.parse($window.sessionStorage.user).roles.item[0];
			// var key1 =
			// JSON.parse($window.sessionStorage.user).roles.item[0].key;

		} else {
			$rootScope.username = undefined;
		}
	});

	$rootScope.logout = function() {
		// delete $rootScope.user;
		// delete $rootScope.authToken;

		var logoutData = UserService.logout();

		var resultLogoutPromise = $q.all({
			logoutdata: logoutData.$promise
		}).then(function(result) {
			delete $window.sessionStorage.authToken;
			delete $window.sessionStorage.user;

			$cookies.remove('authToken');
			$rootScope.content_id = "ns-content";
			//$location.path("/login");
			//$location.url($location.path());

			$location.replace().path("/login");
		}).catch(function(error) {
			console.log('Error logout');
			delete $window.sessionStorage.authToken;
			delete $window.sessionStorage.user;

			$cookies.remove('authToken');
			$rootScope.content_id = "ns-content";
			//$location.path("/login");
			//$location.url($location.path());

			$location.replace().path("/login");
		});
		//
		// UserService.logout(function(result) {
		// // do nothing
		// });
		//
		// delete $window.sessionStorage.authToken;
		// delete $window.sessionStorage.user;
		//
		// $cookieStore.remove('authToken');
		// $rootScope.content_id = "ns-content";
		// $location.path("/login");
		// $location.url($location.path());

	};

	$rootScope.isKlant = function() {
		if(typeof ($window.sessionStorage.user) != 'undefined') {
			return JSON.parse($window.sessionStorage.user).klant;
		} else {
			return false;
		}
	};

	$rootScope.hasRoleUser = function(role) {
		if(typeof $window.sessionStorage.user != 'undefined') {
			return hasRole(JSON.parse($window.sessionStorage.user).roles, role);
		}
		else {
			return false;
		}
	};
	
//	$rootScope.isMobileResolution = function() {
//		  var screenWidth = screen.width;
//		  var windowWidth = $(window).width();
//		  if (typeof window.orientation !== 'undefined') {
//			  screenWidth = window.orientation == 0 ? screen.width : screen.height;
//			  windowWidth = window.orientation == 0 ? $(window).width() : $(window).height();
//		  }
//		  var deviceWidth = screenWidth > windowWidth ? screenWidth : windowWidth;
//		  //var ratio = window.devicePixelRatio || 1;		  
//		  //deviceWidth = deviceWidth * ratio;
//		  
//		  if (deviceWidth < 1440) // 780
//			  return true;
//		  else
//			  return false;
//	};
	
	 $rootScope.isMobileResolution = function () {
		  var screenWidth = screen.width;
		  var windowWidth = $(window).width();
		  
		  var deviceWidth = screenWidth < windowWidth ? screenWidth : windowWidth;
		  //alert('deviceWidth=' + deviceWidth + ' screenWidth=' + screenWidth + ' windowWidth=' + windowWidth );
		  if (deviceWidth < 767)
			  return true;
		  else
			  return false;
	};
	
	// Check field length + prevent adding clipboard content on field to large
	$rootScope.checkFieldMaxLength = function(e, content, maxlength) {
		if (typeof content !== 'undefined' && content != null && typeof maxlength !== 'undefined') {
			cliptext = e.originalEvent.clipboardData.getData('text/plain');
			if (typeof cliptext !== 'undefined' && cliptext != null && (content.length + cliptext.length) > maxlength) {
				//toomuch = Math.abs(maxlength - (content.length + cliptext.length));
				e.stopPropagation();
				e.preventDefault();
				//if ((cliptext.length - toomuch) > 0)						
				//	e.originalEvent.clipboardData.setData("Text", cliptext.substring(0, cliptext.length - toomuch ));
				//else
				//	e.originalEvent.clipboardData.setData("Text", "x");												
				//return (content.length > maxlength ? content.substring(0, maxlength) : content);
			}
		}
		else
			console.log("$rootscope.checkFieldMaxLength error in parameters! content or maxlength null/undefined. Content=" + content + " maxLength=" + maxlength);	
				
	}	

	$rootScope.websiteparam = function() {
		$location.path("/websiteparam");
		$location.url($location.path());
	};

	$rootScope.createticket = function() {
		$location.path("/support");
		$location.url($location.path());
	};

	$rootScope.myuseraccount = function() {
		$location.path("/myuseraccount");
		$location.url($location.path());
	};

	$rootScope.myaccount = function() {
		if($rootScope.isKlant() || $rootScope.hasRoleUser('hoofd_klant') || $rootScope.hasRoleUser('admin_sbdr_hoofd')) {
			$location.path("/myaccount");
			$location.url($location.path());
		}
	};

	$rootScope.dashboard = function() {
		$location.path("/dashboard");
		$location.url($location.path());
	};

	$rootScope.searchCompany = function() {
		$location.path("/searchcompany");
		$location.url($location.path());
	};

	$rootScope.faillissementenoverzicht = function() {
		$location.path("/faillissementenoverzicht");
		$location.url($location.path());
	};

	$rootScope.exactonline = function() {
		$location.path("/exactonline");
		$location.url($location.path());
	};

	$rootScope.showExactOnline = function() {
		if(typeof $rootScope.isExactOnlineAccess != 'undefined') {
			return !$rootScope.isExactOnlineAccess;
		} else {
			return false;
		}
	};

	$rootScope.showalerts = function() {
		$location.path("/create");
	};

	$rootScope.escapeParam = function(param) {
		return param.replace("/", " ");
	};

	$rootScope.changeUrlWithoutReload = function(path) {
		return null;
		var original = $location.path;
		// $location.path = function (path, reload) {
		// if (reload === false) {
		var lastRoute = $route.current;
		var un = $rootScope.$on('$locationChangeSuccess', function() {
			$route.current = lastRoute;
			un();
		});
		// }
		original.apply($location, [path]);
		var un2 = $rootScope.$on('$locationChangeSuccess', function() {
			// $route.current = lastRoute;
			un2();
		});

		// };
	};

	$rootScope.HELP_VERMELDINGINFO = 0x2;
	$rootScope.HELP_MONITORINGPURCHASE = 0x4;
	$rootScope.HELP_RAPPORTPURCHASE = 0x8;

	$rootScope.isShowHelp = function(helpId) {
		var showHelp = 0;

		if(typeof $window.sessionStorage.user !== 'undefined') {
			if(typeof JSON.parse($window.sessionStorage.user).showHelp != 'undefined' && JSON.parse($window.sessionStorage.user).showHelp != null) {
				showHelp = Number(JSON.parse($window.sessionStorage.user).showHelp);
			}

			return (showHelp & helpId) <= 0;  // true if helpId NOT set in showHelp, false otherwise
		} else {
			return 0;
		} // THIS MAY NOT OCCUR!
	};

	$rootScope.updateShowHelp = function(helpId) {
		var showHelp = 0;

		if(typeof JSON.parse($window.sessionStorage.user).showHelp != 'undefined' && JSON.parse($window.sessionStorage.user).showHelp != null) {
			showHelp = Number(JSON.parse($window.sessionStorage.user).showHelp);
		}

		if($rootScope.isShowHelp(helpId)) // if true, then set helpId to not show help in future
		{
			showHelp = showHelp + helpId;
		} else							   // else, then NOT set helpId to show help in future
		{
			showHelp = showHelp - helpId;
		}

		var user = JSON.parse($window.sessionStorage.user);
		user.showHelp = showHelp;
		$window.sessionStorage.user = JSON.stringify(user);

		return showHelp;
	};

	$rootScope.saveShowHelpDialog = function(helpId, helpValue) {

		// helpValue == true => NO Show
		// helpValue == false => Show

		if(helpValue == true && $rootScope.isShowHelp(helpId)) // changed from show to no show help
		{
			showHelp = $rootScope.updateShowHelp(helpId);
		} else if(helpValue == false && !$rootScope.isShowHelp(helpId)) // changed from no show to show help
		{
			showHelp = $rootScope.updateShowHelp(helpId);
		}

		if(typeof showHelp != 'undefined' && showHelp != null) { // if showHelp IS changed update value in database
			UserService.updateShowHelp(showHelp, function(result) {
				if(typeof result.errorCode !== 'undefined') {
					return {error: result.errorCode + ' ' + result.errorMsg};
				}
			});
		}
	};

	/* Try getting valid user from cookie or go to login page */
	var originalPath = $location.path();
	var prefixPath = "";
	if(originalPath != null && originalPath.length > 2) {
		var n = originalPath.indexOf("/", 2);
		if(n > -1) {
			prefixPath = originalPath.substr(0, n);
		}
		else {
			prefixPath = originalPath;
		}
	}

	if(prefixPath != "/forgotpassword" && prefixPath != "/resetpassword" && prefixPath != "/activateuser" && prefixPath != "/login" && prefixPath != "/searchcompanydirect") {
		$location.path("/login");
		$location.url($location.path());
		var authToken = $cookies.get('authToken');
		if(authToken !== undefined) {

			UserService.userdata(function(user) {
				$window.sessionStorage.authToken = authToken;
				// $rootScope.authToken = authToken;

				var userJson = JSON.stringify(user);
				$window.sessionStorage.user = userJson;
				// $rootScope.user = user;

				$location.path(originalPath);
				$location.url($location.path());
			});
		}
	} else if(prefixPath == '/searchcompanydirect') {
		var searchcompany = null; 
		if (typeof $location.search !== 'undefined' && $location.search != null) {
			var searchObject = $location.search();
			if (searchObject != null && searchObject.company != null)
				searchcompany = searchObject.company;
		}
		if (searchcompany == null)
			$location.path("/searchcompany");
		else {
			$location.path("/searchcompany/search/" + searchcompany + "/1");
		}
		$location.url($location.path());
	}

	$rootScope.initialized = true;
}]);

function getByName(arr, keyIn, valueIn) {

	// for (var i=0, iLen=arr.length; i<iLen; i++) {
	// if ($(arr[i]).attr(keyIn) == valueIn)
	// return arr[i];
	// }

	$.each(arr, function(index, arrayElement) {
		if($(arrayElement).attr(keyIn) == valueIn) {
			return arrayElement;
		}
	});

	return null;
}

function paging_totalItems(responseHeader) {

	if(responseHeader != null && typeof responseHeader != 'undefined') {
		// response header format like: items 1-10/30
		var n = responseHeader.indexOf("/");

		if(n != -1) {
			return responseHeader.substring(n + 1);
		}
		else {
			return 0;
		}
	}
	else {
		return 0;
	}
}

// JSON.parse($window.sessionStorage.user).roles.item[0]['@key']
// roles = JSON.parse($window.sessionStorage.user).roles
// role = string
// admin_sbdr, admin_sbdr_hoofd, admin_klant, hoofd_klant, gebruiker_klant
function hasRole(roles, role) {
	if(typeof roles != 'undefined' && roles != null) {
		if(Object.prototype.toString.call(roles.item) === '[object Array]') {
			for(i = 0; i < roles.item.length; i++) {
				if(roles.item[i]['key'] == role && roles.item[i]['value'] == true) {
					return true;
				}
			}
		}
		else {
			if(roles.item['key'] == role && roles.item['value'] == true) {
				return true;
			}
		}
	}

	return false;
}

function endsWith(str, suffix) {
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function escapeRegExp(string) {
	return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}

function replaceAll(find, replace, str) {
	return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

function escapeSearchUrl(string) {
	if(string && typeof string == 'string') {
		// contains empty params
		var search = replaceAll('$$', '$undefined$', string);
		return replaceAll('$', '@', search);
	} else {
		return '';
	}
}

function gotoDurl(value) {
	if(value && typeof value == 'string') {
		// endswith empty param
		var pos = value.indexOf('$', value.length - 1);
		if(pos !== -1) {
			url = value.substr(0, pos) + 'undefined';
		} else {
			url = value;
		}

		// contains empty params
		var url = replaceAll('$$', '$undefined$', url);
		// replace $'s
		url = replaceAll('$', '/', url);

		// replace escaped search url
		url = replaceAll('@', '$', url);

		// separate escaped search url
		return url.replace('$', '/$');
	}
	else {
		return null;
	}
}

var dates = {
	convert: function(d) {
		// Converts the date in d to a date-object. The input can be:
		// a date object: returned without modification
		// an array : Interpreted as [year,month,day]. NOTE: month is 0-11.
		// a number : Interpreted as number of milliseconds
		// since 1 Jan 1970 (a timestamp)
		// a string : Any format supported by the javascript engine, like
		// "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
		// an object : Interpreted as an object with year, month and date
		// attributes. **NOTE** month is 0-11.
		return (
			d.constructor === Date ? d :
				d.constructor === Array ? new Date(d[0], d[1], d[2]) :
					d.constructor === Number ? new Date(d) :
						d.constructor === String ? new Date(d) :
							typeof d === "object" ? new Date(d.year, d.month, d.date) :
								NaN
		);
	},
	compare: function(a, b) {
		// Compare two dates (could be of any type supported by the convert
		// function above) and returns:
		// -1 : if a < b
		// 0 : if a = b
		// 1 : if a > b
		// NaN : if a or b is an illegal date
		// NOTE: The code inside isFinite does an assignment (=).
		return (
			isFinite(a = this.convert(a).valueOf()) &&
			isFinite(b = this.convert(b).valueOf()) ?
			(a > b) - (a < b) :
				NaN
		);
	},
	inRange: function(d, start, end) {
		// Checks if date in d is between dates in start and end.
		// Returns a boolean or NaN:
		// true : if d is between start and end (inclusive)
		// false : if d is before start or after end
		// NaN : if one or more of the dates is illegal.
		// NOTE: The code inside isFinite does an assignment (=).
		return (
			isFinite(d = this.convert(d).valueOf()) &&
			isFinite(start = this.convert(start).valueOf()) &&
			isFinite(end = this.convert(end).valueOf()) ?
			start <= d && d <= end :
				NaN
		);
	}
};

app.constant('maxFieldLengths', 
	{
	naam:25,
	gebruikersnaam:50,
	wachtwoord:20,
	factuurnummer:20,
	bedrijfsnaam:250,
	kortingscode:25,
	voornaam:35,
	afdeling:35,
	telefoonnummer:15,
	btwnummer:25,
	emailadres:50,
	donatie:7,
	straat:100,
	huisnummer:11,
	huisnummertoevoeging:35,
	postcode:7,
	postbus:7,
	plaats:100,
	kvknummer:12,
	zoeken:100,
	verificatiecode:50,
	referentie:50,
	bankrekeningnummer:36,
	tenaamstelling:250,
	meldingDetails:2000,
	supportBericht:4000,
	storingBericht:1000,
	notificationNotes:4000,
	contactMomentNotitie:4000,
	contactMomentNotitieAdmin:500
	});


	