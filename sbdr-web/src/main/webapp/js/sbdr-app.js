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

		0;

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
						0;
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
						0;
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
						0;
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
						0;
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
						0;
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
							0;
						});

						return resultsPromise2;
						// var results =
						// AccountService.getCompanyAccountData({user:
						// usertransferData});
						// return results;
					}).catch(function(error) {
						0;
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
						0;
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
						0;
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
							0;
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
							0;
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
							0;
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
						0;
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
						0;
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
						0;
												
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
			                0;
			                0;
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
							0;
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
					0;
				
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
		          //0;
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
		//0;
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
		0;
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
			0;
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
			0;	
				
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


	appcontrollers.controller('AccountCreatedController', ['$window', '$scope', '$rootScope', '$location', function ($window, $scope, $rootScope, $location) {
	0;
	
	$scope.returnToLogin = function() {
		$location.path("/login");
		$location.url($location.path());
	}
	
}]);	appcontrollers.controller('ActivateAccountController', ['$window', '$routeParams', '$scope', '$rootScope', '$location', 'maxFieldLengths', '$anchorScroll', '$cookieStore', 'LoginService', 'UserService', 'clientip', function ($window, $routeParams, $scope, $rootScope, $location, maxFieldLengths, $anchorScroll, $cookieStore, LoginService, UserService, clientip) {

	var userId = $routeParams.id;
	$scope.maxFieldLengths = maxFieldLengths;
	
	$scope.activateAccount = function(userId) {
		
		LoginService.activateUser({id : userId}, function(activationResult) {
			if (activationResult.errorCode === undefined)
			{
				0;
				$scope.userActived = true;
				login();
			}
			else {
				0;	
				$scope.error = activationResult.errorCode + " " + activationResult.errorMsg;
				$location.hash("alert");
				$anchorScroll();
			}
		});		
	};
	
	$scope.isInvalid = function(field){
	    result = $scope.formcredentials[field].$invalid && $scope.formcredentials[field].$dirty;
	    
	    if (result)
	    {
			$location.hash($scope.formcredentials[field]);
			$anchorScroll();	    	
	    }
	    
	    return result;
	  };

	  $scope.isValid = function(field){
	    result = $scope.formcredentials[field].$valid && $scope.formcredentials[field].$dirty;
	    
	    return result;
	  };
	
	
	login = function() {
		LoginService.authenticate({username: $scope.username, password: $scope.password, ipaddress: clientip}, function(authenticationResult) {
			var authToken = authenticationResult.token;
			$window.sessionStorage.authToken = authToken;
			//$rootScope.authToken = authToken;
			//alert("MyToken=" + authToken);
			//alert("after remember me...")
			UserService.userdata(function(user) {
				if ($scope.rememberMe) {
					$cookieStore.put('authToken', authToken);
					//alert("Token stored...")
				}
				
				var userJson = JSON.stringify(user);
                $window.sessionStorage.user = userJson;				
				//$rootScope.user = user; 
				
				$rootScope.content_id = "content";
				$location.path("/dashboard");
				$location.url($location.path());
			});
			
			
		});
	};
		
}]);appcontrollers.controller('ActivateUserController', [
	'$window', '$scope', '$rootScope', '$location', '$cookies', '$anchorScroll', '$routeParams', 'maxFieldLengths', 'UserService', 'UserNsService', 'NewAccountService', 'clientip',
	function($window, $scope, $rootScope, $location, $cookies, $anchorScroll, $routeParams, maxFieldLengths, UserService, UserNsService, NewAccountService, clientip) {
		0;

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

	}]);appcontrollers.controller('AlertsTabController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', '$timeout', 'maxFieldLengths', 'ngTableParams',
        'DashboardNumberTags', 'DashboardService', 'AccountService', 'CompanyService', 
        function ($window, $scope, $rootScope, $location, $filter, $timeout, maxFieldLengths, ngTableParams,
                  DashboardNumberTags, DashboardService, AccountService, CompanyService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            $scope.title = "Alerts Tab";
            
            AccountService.getCompanyAccountData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId}, function (data) {
                $scope.account = data;
            });

            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPage = 20;
            $scope.maxSize = 5;
            $scope.thisUser = JSON.parse($window.sessionStorage.user);

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: "",
                    filterValue: "" // text to filter on
                };
            }

            $scope.$watch('filterCriteria.filterValue', function (search) {
                if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

                searchCallbackTimeout = $timeout(function () {
                    if (search) {
                        if (search.length > 2) {
                            // refresh
                            $scope.currentPage = 1;
                            $scope.pageChanged();
                            $scope.oldSearch = search;
                        }
                    }
                    else if (isSearchChanged(search)) {
                        // refresh
                        $scope.currentPage = 1;
                        $scope.pageChanged();
                        $scope.oldSearch = search;
                    }
                }, 1000);
            }, true);

            // The function that is responsible of fetching the result from the
            // server and setting the grid to the new result
            $scope.fetchResult = function (success) {
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                DashboardService.companiesalert($scope.filterCriteria, function (data, headers) {
                    if (typeof data != 'undefined' && data.length > 0) {
                        if (hasRole($scope.thisUser.roles, 'admin_sbdr') || hasRole($scope.thisUser.roles, 'admin_sbdr_hoofd')) {
                            $scope.adminAlertsCollection = data;
                        } else {
                            $scope.alertsCollection = data;
                        }

                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    }
                    else {
                        $scope.adminAlertsCollection = null;
                        $scope.alertsCollection = null;
                        $scope.totalItems = 0;
                    }
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);

                    success();
                }, function () {
                    $scope.adminAlertsCollection = [];
                    $scope.alertsCollection = [];
                    $scope.totalItems = 0;
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);
                });
            };

            // Data table functions
            $scope.filterResult = function () {
                return $scope.fetchResult(function () {
                    //The request fires correctly but sometimes the ui doesn't update, that's a fix
                    $scope.filterCriteria.pageNumber = 1;
                    $scope.currentPage = 1;

                    // rootscope range delete
                    delete $rootScope.range;

                });
            };

            $scope.hasItems = function () {
                return DashboardNumberTags.getNrOfAlerts() > 0;
            };

            $scope.ignorealert = function (type, alertId, bedrijfId) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                } else {
                    deleteAlert(alertId);
                }
            };

            $scope.pageChanged = function () {
                $scope.filterCriteria.pageNumber = $scope.currentPage;
                $scope.fetchResult(function () {
                    //Nothing to do..

                    // rootscope range delete
                    delete $rootScope.range;
                });
            };

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

            //$scope.requestreport = function (bedrijfId, monitoringBedrijfId, indMonitoringBedrijf) {
            //    CompanyService.updateMonitoring({
            //        vanBedrijfId: monitoringBedrijfId,
            //        doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
            //    }, function (result) {
            //        if (typeof result.errorCode !== "undefined")
            //            $scope.error = result.errorCode + " " + result.errorMsg;
            //        else {
            //            searchurl = '$dashboard$alertstab';
            //            $location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/' + searchurl);
            //        }
            //    });
            //};

			$scope.viewAdminDetails = function(refNoPRefix){
				supportAlertDetail(refNoPRefix);
			};

            $scope.viewdetails = function (type, alertId, monitoringId, meldingId, supportId, bedrijfId, refNoPrefix) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                    monitoringDetail(monitoringId);
                }
                else if (type == 'VER') {
                    deleteAlert(alertId);
                    notificationReadOnly(meldingId, bedrijfId);
                }
                else if (type == 'SUP') {
					deleteAlert(alertId);
                    supportAlertDetail(refNoPrefix);
                }
            };

			deleteAlert = function (alertId) {
				// remove alert record
				CompanyService.deleteAlert(alertId, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			monitoringDetail = function (monitoringId) {
				delete $scope.error;
				$location.path('/monitoringdetails/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + monitoringId + '/$dashboard$alertstab');
				$location.url($location.path());
			};

			notificationReadOnly = function (meldingId, bedrijfId) {
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$alertstab/' + meldingId + '/true');
				$location.url($location.path());
			};

			supportAlertDetail = function (refNoPrefix) {
				delete $scope.error;
				$location.path('/support/detail/' + refNoPrefix+'/$dashboard$alertstab');
				$location.url($location.path());
			};

			updateMonitoring = function (bedrijfId) {
				// update monitoring
				CompanyService.updateMonitoring({
					vanBedrijfId: bedrijfId,
					doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				}, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};
			
			$scope.lowercaseFirstLetter = function (string) {
			    return string.charAt(0).toLowerCase() + string.slice(1);
			};

			// fetch initial data for 1st time
			$scope.filterResult();
        }]);/*!
 * async
 * https://github.com/caolan/async
 *
 * Copyright 2010-2014 Caolan McMahon
 * Released under the MIT license
 */
(function () {

    var async = {};
    function noop() {}
    function identity(v) {
        return v;
    }
    function toBool(v) {
        return !!v;
    }
    function notId(v) {
        return !v;
    }

    // global on the server, window in the browser
    var previous_async;

    // Establish the root object, `window` (`self`) in the browser, `global`
    // on the server, or `this` in some virtual machines. We use `self`
    // instead of `window` for `WebWorker` support.
    var root = typeof self === 'object' && self.self === self && self ||
            typeof global === 'object' && global.global === global && global ||
            this;

    if (root != null) {
        previous_async = root.async;
    }

    async.noConflict = function () {
        root.async = previous_async;
        return async;
    };

    function only_once(fn) {
        return function() {
            if (fn === null) throw new Error("Callback was already called.");
            fn.apply(this, arguments);
            fn = null;
        };
    }

    function _once(fn) {
        return function() {
            if (fn === null) return;
            fn.apply(this, arguments);
            fn = null;
        };
    }

    //// cross-browser compatiblity functions ////

    var _toString = Object.prototype.toString;

    var _isArray = Array.isArray || function (obj) {
        return _toString.call(obj) === '[object Array]';
    };

    // Ported from underscore.js isObject
    var _isObject = function(obj) {
        var type = typeof obj;
        return type === 'function' || type === 'object' && !!obj;
    };

    function _isArrayLike(arr) {
        return _isArray(arr) || (
            // has a positive integer length property
            typeof arr.length === "number" &&
            arr.length >= 0 &&
            arr.length % 1 === 0
        );
    }

    function _arrayEach(arr, iterator) {
        var index = -1,
            length = arr.length;

        while (++index < length) {
            iterator(arr[index], index, arr);
        }
    }

    function _map(arr, iterator) {
        var index = -1,
            length = arr.length,
            result = Array(length);

        while (++index < length) {
            result[index] = iterator(arr[index], index, arr);
        }
        return result;
    }

    function _range(count) {
        return _map(Array(count), function (v, i) { return i; });
    }

    function _reduce(arr, iterator, memo) {
        _arrayEach(arr, function (x, i, a) {
            memo = iterator(memo, x, i, a);
        });
        return memo;
    }

    function _forEachOf(object, iterator) {
        _arrayEach(_keys(object), function (key) {
            iterator(object[key], key);
        });
    }

    function _indexOf(arr, item) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === item) return i;
        }
        return -1;
    }

    var _keys = Object.keys || function (obj) {
        var keys = [];
        for (var k in obj) {
            if (obj.hasOwnProperty(k)) {
                keys.push(k);
            }
        }
        return keys;
    };

    function _keyIterator(coll) {
        var i = -1;
        var len;
        var keys;
        if (_isArrayLike(coll)) {
            len = coll.length;
            return function next() {
                i++;
                return i < len ? i : null;
            };
        } else {
            keys = _keys(coll);
            len = keys.length;
            return function next() {
                i++;
                return i < len ? keys[i] : null;
            };
        }
    }

    // Similar to ES6's rest param (http://ariya.ofilabs.com/2013/03/es6-and-rest-parameter.html)
    // This accumulates the arguments passed into an array, after a given index.
    // From underscore.js (https://github.com/jashkenas/underscore/pull/2140).
    function _restParam(func, startIndex) {
        startIndex = startIndex == null ? func.length - 1 : +startIndex;
        return function() {
            var length = Math.max(arguments.length - startIndex, 0);
            var rest = Array(length);
            for (var index = 0; index < length; index++) {
                rest[index] = arguments[index + startIndex];
            }
            switch (startIndex) {
                case 0: return func.call(this, rest);
                case 1: return func.call(this, arguments[0], rest);
            }
            // Currently unused but handle cases outside of the switch statement:
            // var args = Array(startIndex + 1);
            // for (index = 0; index < startIndex; index++) {
            //     args[index] = arguments[index];
            // }
            // args[startIndex] = rest;
            // return func.apply(this, args);
        };
    }

    function _withoutIndex(iterator) {
        return function (value, index, callback) {
            return iterator(value, callback);
        };
    }

    //// exported async module functions ////

    //// nextTick implementation with browser-compatible fallback ////

    // capture the global reference to guard against fakeTimer mocks
    var _setImmediate = typeof setImmediate === 'function' && setImmediate;

    var _delay = _setImmediate ? function(fn) {
        // not a direct alias for IE10 compatibility
        _setImmediate(fn);
    } : function(fn) {
        setTimeout(fn, 0);
    };

    if (typeof process === 'object' && typeof process.nextTick === 'function') {
        async.nextTick = process.nextTick;
    } else {
        async.nextTick = _delay;
    }
    async.setImmediate = _setImmediate ? _delay : async.nextTick;


    async.forEach =
    async.each = function (arr, iterator, callback) {
        return async.eachOf(arr, _withoutIndex(iterator), callback);
    };

    async.forEachSeries =
    async.eachSeries = function (arr, iterator, callback) {
        return async.eachOfSeries(arr, _withoutIndex(iterator), callback);
    };


    async.forEachLimit =
    async.eachLimit = function (arr, limit, iterator, callback) {
        return _eachOfLimit(limit)(arr, _withoutIndex(iterator), callback);
    };

    async.forEachOf =
    async.eachOf = function (object, iterator, callback) {
        callback = _once(callback || noop);
        object = object || [];

        var iter = _keyIterator(object);
        var key, completed = 0;

        while ((key = iter()) != null) {
            completed += 1;
            iterator(object[key], key, only_once(done));
        }

        if (completed === 0) callback(null);

        function done(err) {
            completed--;
            if (err) {
                callback(err);
            }
            // Check key is null in case iterator isn't exhausted
            // and done resolved synchronously.
            else if (key === null && completed <= 0) {
                callback(null);
            }
        }
    };

    async.forEachOfSeries =
    async.eachOfSeries = function (obj, iterator, callback) {
        callback = _once(callback || noop);
        obj = obj || [];
        var nextKey = _keyIterator(obj);
        var key = nextKey();
        function iterate() {
            var sync = true;
            if (key === null) {
                return callback(null);
            }
            iterator(obj[key], key, only_once(function (err) {
                if (err) {
                    callback(err);
                }
                else {
                    key = nextKey();
                    if (key === null) {
                        return callback(null);
                    } else {
                        if (sync) {
                            async.setImmediate(iterate);
                        } else {
                            iterate();
                        }
                    }
                }
            }));
            sync = false;
        }
        iterate();
    };



    async.forEachOfLimit =
    async.eachOfLimit = function (obj, limit, iterator, callback) {
        _eachOfLimit(limit)(obj, iterator, callback);
    };

    function _eachOfLimit(limit) {

        return function (obj, iterator, callback) {
            callback = _once(callback || noop);
            obj = obj || [];
            var nextKey = _keyIterator(obj);
            if (limit <= 0) {
                return callback(null);
            }
            var done = false;
            var running = 0;
            var errored = false;

            (function replenish () {
                if (done && running <= 0) {
                    return callback(null);
                }

                while (running < limit && !errored) {
                    var key = nextKey();
                    if (key === null) {
                        done = true;
                        if (running <= 0) {
                            callback(null);
                        }
                        return;
                    }
                    running += 1;
                    iterator(obj[key], key, only_once(function (err) {
                        running -= 1;
                        if (err) {
                            callback(err);
                            errored = true;
                        }
                        else {
                            replenish();
                        }
                    }));
                }
            })();
        };
    }


    function doParallel(fn) {
        return function (obj, iterator, callback) {
            return fn(async.eachOf, obj, iterator, callback);
        };
    }
    function doParallelLimit(fn) {
        return function (obj, limit, iterator, callback) {
            return fn(_eachOfLimit(limit), obj, iterator, callback);
        };
    }
    function doSeries(fn) {
        return function (obj, iterator, callback) {
            return fn(async.eachOfSeries, obj, iterator, callback);
        };
    }

    function _asyncMap(eachfn, arr, iterator, callback) {
        callback = _once(callback || noop);
        arr = arr || [];
        var results = _isArrayLike(arr) ? [] : {};
        eachfn(arr, function (value, index, callback) {
            iterator(value, function (err, v) {
                results[index] = v;
                callback(err);
            });
        }, function (err) {
            callback(err, results);
        });
    }

    async.map = doParallel(_asyncMap);
    async.mapSeries = doSeries(_asyncMap);
    async.mapLimit = doParallelLimit(_asyncMap);

    // reduce only has a series version, as doing reduce in parallel won't
    // work in many situations.
    async.inject =
    async.foldl =
    async.reduce = function (arr, memo, iterator, callback) {
        async.eachOfSeries(arr, function (x, i, callback) {
            iterator(memo, x, function (err, v) {
                memo = v;
                callback(err);
            });
        }, function (err) {
            callback(err, memo);
        });
    };

    async.foldr =
    async.reduceRight = function (arr, memo, iterator, callback) {
        var reversed = _map(arr, identity).reverse();
        async.reduce(reversed, memo, iterator, callback);
    };

    async.transform = function (arr, memo, iterator, callback) {
        if (arguments.length === 3) {
            callback = iterator;
            iterator = memo;
            memo = _isArray(arr) ? [] : {};
        }

        async.eachOf(arr, function(v, k, cb) {
            iterator(memo, v, k, cb);
        }, function(err) {
            callback(err, memo);
        });
    };

    function _filter(eachfn, arr, iterator, callback) {
        var results = [];
        eachfn(arr, function (x, index, callback) {
            iterator(x, function (v) {
                if (v) {
                    results.push({index: index, value: x});
                }
                callback();
            });
        }, function () {
            callback(_map(results.sort(function (a, b) {
                return a.index - b.index;
            }), function (x) {
                return x.value;
            }));
        });
    }

    async.select =
    async.filter = doParallel(_filter);

    async.selectLimit =
    async.filterLimit = doParallelLimit(_filter);

    async.selectSeries =
    async.filterSeries = doSeries(_filter);

    function _reject(eachfn, arr, iterator, callback) {
        _filter(eachfn, arr, function(value, cb) {
            iterator(value, function(v) {
                cb(!v);
            });
        }, callback);
    }
    async.reject = doParallel(_reject);
    async.rejectLimit = doParallelLimit(_reject);
    async.rejectSeries = doSeries(_reject);

    function _createTester(eachfn, check, getResult) {
        return function(arr, limit, iterator, cb) {
            function done() {
                if (cb) cb(getResult(false, void 0));
            }
            function iteratee(x, _, callback) {
                if (!cb) return callback();
                iterator(x, function (v) {
                    if (cb && check(v)) {
                        cb(getResult(true, x));
                        cb = iterator = false;
                    }
                    callback();
                });
            }
            if (arguments.length > 3) {
                eachfn(arr, limit, iteratee, done);
            } else {
                cb = iterator;
                iterator = limit;
                eachfn(arr, iteratee, done);
            }
        };
    }

    async.any =
    async.some = _createTester(async.eachOf, toBool, identity);

    async.someLimit = _createTester(async.eachOfLimit, toBool, identity);

    async.all =
    async.every = _createTester(async.eachOf, notId, notId);

    async.everyLimit = _createTester(async.eachOfLimit, notId, notId);

    function _findGetResult(v, x) {
        return x;
    }
    async.detect = _createTester(async.eachOf, identity, _findGetResult);
    async.detectSeries = _createTester(async.eachOfSeries, identity, _findGetResult);
    async.detectLimit = _createTester(async.eachOfLimit, identity, _findGetResult);

    async.sortBy = function (arr, iterator, callback) {
        async.map(arr, function (x, callback) {
            iterator(x, function (err, criteria) {
                if (err) {
                    callback(err);
                }
                else {
                    callback(null, {value: x, criteria: criteria});
                }
            });
        }, function (err, results) {
            if (err) {
                return callback(err);
            }
            else {
                callback(null, _map(results.sort(comparator), function (x) {
                    return x.value;
                }));
            }

        });

        function comparator(left, right) {
            var a = left.criteria, b = right.criteria;
            return a < b ? -1 : a > b ? 1 : 0;
        }
    };

    async.auto = function (tasks, concurrency, callback) {
        if (typeof arguments[1] === 'function') {
            // concurrency is optional, shift the args.
            callback = concurrency;
            concurrency = null;
        }
        callback = _once(callback || noop);
        var keys = _keys(tasks);
        var remainingTasks = keys.length;
        if (!remainingTasks) {
            return callback(null);
        }
        if (!concurrency) {
            concurrency = remainingTasks;
        }

        var results = {};
        var runningTasks = 0;

        var hasError = false;

        var listeners = [];
        function addListener(fn) {
            listeners.unshift(fn);
        }
        function removeListener(fn) {
            var idx = _indexOf(listeners, fn);
            if (idx >= 0) listeners.splice(idx, 1);
        }
        function taskComplete() {
            remainingTasks--;
            _arrayEach(listeners.slice(0), function (fn) {
                fn();
            });
        }

        addListener(function () {
            if (!remainingTasks) {
                callback(null, results);
            }
        });

        _arrayEach(keys, function (k) {
            if (hasError) return;
            var task = _isArray(tasks[k]) ? tasks[k]: [tasks[k]];
            var taskCallback = _restParam(function(err, args) {
                runningTasks--;
                if (args.length <= 1) {
                    args = args[0];
                }
                if (err) {
                    var safeResults = {};
                    _forEachOf(results, function(val, rkey) {
                        safeResults[rkey] = val;
                    });
                    safeResults[k] = args;
                    hasError = true;

                    callback(err, safeResults);
                }
                else {
                    results[k] = args;
                    async.setImmediate(taskComplete);
                }
            });
            var requires = task.slice(0, task.length - 1);
            // prevent dead-locks
            var len = requires.length;
            var dep;
            while (len--) {
                if (!(dep = tasks[requires[len]])) {
                    throw new Error('Has nonexistent dependency in ' + requires.join(', '));
                }
                if (_isArray(dep) && _indexOf(dep, k) >= 0) {
                    throw new Error('Has cyclic dependencies');
                }
            }
            function ready() {
                return runningTasks < concurrency && _reduce(requires, function (a, x) {
                    return (a && results.hasOwnProperty(x));
                }, true) && !results.hasOwnProperty(k);
            }
            if (ready()) {
                runningTasks++;
                task[task.length - 1](taskCallback, results);
            }
            else {
                addListener(listener);
            }
            function listener() {
                if (ready()) {
                    runningTasks++;
                    removeListener(listener);
                    task[task.length - 1](taskCallback, results);
                }
            }
        });
    };



    async.retry = function(times, task, callback) {
        var DEFAULT_TIMES = 5;
        var DEFAULT_INTERVAL = 0;

        var attempts = [];

        var opts = {
            times: DEFAULT_TIMES,
            interval: DEFAULT_INTERVAL
        };

        function parseTimes(acc, t){
            if(typeof t === 'number'){
                acc.times = parseInt(t, 10) || DEFAULT_TIMES;
            } else if(typeof t === 'object'){
                acc.times = parseInt(t.times, 10) || DEFAULT_TIMES;
                acc.interval = parseInt(t.interval, 10) || DEFAULT_INTERVAL;
            } else {
                throw new Error('Unsupported argument type for \'times\': ' + typeof t);
            }
        }

        var length = arguments.length;
        if (length < 1 || length > 3) {
            throw new Error('Invalid arguments - must be either (task), (task, callback), (times, task) or (times, task, callback)');
        } else if (length <= 2 && typeof times === 'function') {
            callback = task;
            task = times;
        }
        if (typeof times !== 'function') {
            parseTimes(opts, times);
        }
        opts.callback = callback;
        opts.task = task;

        function wrappedTask(wrappedCallback, wrappedResults) {
            function retryAttempt(task, finalAttempt) {
                return function(seriesCallback) {
                    task(function(err, result){
                        seriesCallback(!err || finalAttempt, {err: err, result: result});
                    }, wrappedResults);
                };
            }

            function retryInterval(interval){
                return function(seriesCallback){
                    setTimeout(function(){
                        seriesCallback(null);
                    }, interval);
                };
            }

            while (opts.times) {

                var finalAttempt = !(opts.times-=1);
                attempts.push(retryAttempt(opts.task, finalAttempt));
                if(!finalAttempt && opts.interval > 0){
                    attempts.push(retryInterval(opts.interval));
                }
            }

            async.series(attempts, function(done, data){
                data = data[data.length - 1];
                (wrappedCallback || opts.callback)(data.err, data.result);
            });
        }

        // If a callback is passed, run this as a controll flow
        return opts.callback ? wrappedTask() : wrappedTask;
    };

    async.waterfall = function (tasks, callback) {
        callback = _once(callback || noop);
        if (!_isArray(tasks)) {
            var err = new Error('First argument to waterfall must be an array of functions');
            return callback(err);
        }
        if (!tasks.length) {
            return callback();
        }
        function wrapIterator(iterator) {
            return _restParam(function (err, args) {
                if (err) {
                    callback.apply(null, [err].concat(args));
                }
                else {
                    var next = iterator.next();
                    if (next) {
                        args.push(wrapIterator(next));
                    }
                    else {
                        args.push(callback);
                    }
                    ensureAsync(iterator).apply(null, args);
                }
            });
        }
        wrapIterator(async.iterator(tasks))();
    };

    function _parallel(eachfn, tasks, callback) {
        callback = callback || noop;
        var results = _isArrayLike(tasks) ? [] : {};

        eachfn(tasks, function (task, key, callback) {
            task(_restParam(function (err, args) {
                if (args.length <= 1) {
                    args = args[0];
                }
                results[key] = args;
                callback(err);
            }));
        }, function (err) {
            callback(err, results);
        });
    }

    async.parallel = function (tasks, callback) {
        _parallel(async.eachOf, tasks, callback);
    };

    async.parallelLimit = function(tasks, limit, callback) {
        _parallel(_eachOfLimit(limit), tasks, callback);
    };

    async.series = function(tasks, callback) {
        _parallel(async.eachOfSeries, tasks, callback);
    };

    async.iterator = function (tasks) {
        function makeCallback(index) {
            function fn() {
                if (tasks.length) {
                    tasks[index].apply(null, arguments);
                }
                return fn.next();
            }
            fn.next = function () {
                return (index < tasks.length - 1) ? makeCallback(index + 1): null;
            };
            return fn;
        }
        return makeCallback(0);
    };

    async.apply = _restParam(function (fn, args) {
        return _restParam(function (callArgs) {
            return fn.apply(
                null, args.concat(callArgs)
            );
        });
    });

    function _concat(eachfn, arr, fn, callback) {
        var result = [];
        eachfn(arr, function (x, index, cb) {
            fn(x, function (err, y) {
                result = result.concat(y || []);
                cb(err);
            });
        }, function (err) {
            callback(err, result);
        });
    }
    async.concat = doParallel(_concat);
    async.concatSeries = doSeries(_concat);

    async.whilst = function (test, iterator, callback) {
        callback = callback || noop;
        if (test()) {
            var next = _restParam(function(err, args) {
                if (err) {
                    callback(err);
                } else if (test.apply(this, args)) {
                    iterator(next);
                } else {
                    callback.apply(null, [null].concat(args));
                }
            });
            iterator(next);
        } else {
            callback(null);
        }
    };

    async.doWhilst = function (iterator, test, callback) {
        var calls = 0;
        return async.whilst(function() {
            return ++calls <= 1 || test.apply(this, arguments);
        }, iterator, callback);
    };

    async.until = function (test, iterator, callback) {
        return async.whilst(function() {
            return !test.apply(this, arguments);
        }, iterator, callback);
    };

    async.doUntil = function (iterator, test, callback) {
        return async.doWhilst(iterator, function() {
            return !test.apply(this, arguments);
        }, callback);
    };

    async.during = function (test, iterator, callback) {
        callback = callback || noop;

        var next = _restParam(function(err, args) {
            if (err) {
                callback(err);
            } else {
                args.push(check);
                test.apply(this, args);
            }
        });

        var check = function(err, truth) {
            if (err) {
                callback(err);
            } else if (truth) {
                iterator(next);
            } else {
                callback(null);
            }
        };

        test(check);
    };

    async.doDuring = function (iterator, test, callback) {
        var calls = 0;
        async.during(function(next) {
            if (calls++ < 1) {
                next(null, true);
            } else {
                test.apply(this, arguments);
            }
        }, iterator, callback);
    };

    function _queue(worker, concurrency, payload) {
        if (concurrency == null) {
            concurrency = 1;
        }
        else if(concurrency === 0) {
            throw new Error('Concurrency must not be zero');
        }
        function _insert(q, data, pos, callback) {
            if (callback != null && typeof callback !== "function") {
                throw new Error("task callback must be a function");
            }
            q.started = true;
            if (!_isArray(data)) {
                data = [data];
            }
            if(data.length === 0 && q.idle()) {
                // call drain immediately if there are no tasks
                return async.setImmediate(function() {
                    q.drain();
                });
            }
            _arrayEach(data, function(task) {
                var item = {
                    data: task,
                    callback: callback || noop
                };

                if (pos) {
                    q.tasks.unshift(item);
                } else {
                    q.tasks.push(item);
                }

                if (q.tasks.length === q.concurrency) {
                    q.saturated();
                }
            });
            async.setImmediate(q.process);
        }
        function _next(q, tasks) {
            return function(){
                workers -= 1;

                var removed = false;
                var args = arguments;
                _arrayEach(tasks, function (task) {
                    _arrayEach(workersList, function (worker, index) {
                        if (worker === task && !removed) {
                            workersList.splice(index, 1);
                            removed = true;
                        }
                    });

                    task.callback.apply(task, args);
                });
                if (q.tasks.length + workers === 0) {
                    q.drain();
                }
                q.process();
            };
        }

        var workers = 0;
        var workersList = [];
        var q = {
            tasks: [],
            concurrency: concurrency,
            payload: payload,
            saturated: noop,
            empty: noop,
            drain: noop,
            started: false,
            paused: false,
            push: function (data, callback) {
                _insert(q, data, false, callback);
            },
            kill: function () {
                q.drain = noop;
                q.tasks = [];
            },
            unshift: function (data, callback) {
                _insert(q, data, true, callback);
            },
            process: function () {
                while(!q.paused && workers < q.concurrency && q.tasks.length){

                    var tasks = q.payload ?
                        q.tasks.splice(0, q.payload) :
                        q.tasks.splice(0, q.tasks.length);

                    var data = _map(tasks, function (task) {
                        return task.data;
                    });

                    if (q.tasks.length === 0) {
                        q.empty();
                    }
                    workers += 1;
                    workersList.push(tasks[0]);
                    var cb = only_once(_next(q, tasks));
                    worker(data, cb);
                }
            },
            length: function () {
                return q.tasks.length;
            },
            running: function () {
                return workers;
            },
            workersList: function () {
                return workersList;
            },
            idle: function() {
                return q.tasks.length + workers === 0;
            },
            pause: function () {
                q.paused = true;
            },
            resume: function () {
                if (q.paused === false) { return; }
                q.paused = false;
                var resumeCount = Math.min(q.concurrency, q.tasks.length);
                // Need to call q.process once per concurrent
                // worker to preserve full concurrency after pause
                for (var w = 1; w <= resumeCount; w++) {
                    async.setImmediate(q.process);
                }
            }
        };
        return q;
    }

    async.queue = function (worker, concurrency) {
        var q = _queue(function (items, cb) {
            worker(items[0], cb);
        }, concurrency, 1);

        return q;
    };

    async.priorityQueue = function (worker, concurrency) {

        function _compareTasks(a, b){
            return a.priority - b.priority;
        }

        function _binarySearch(sequence, item, compare) {
            var beg = -1,
                end = sequence.length - 1;
            while (beg < end) {
                var mid = beg + ((end - beg + 1) >>> 1);
                if (compare(item, sequence[mid]) >= 0) {
                    beg = mid;
                } else {
                    end = mid - 1;
                }
            }
            return beg;
        }

        function _insert(q, data, priority, callback) {
            if (callback != null && typeof callback !== "function") {
                throw new Error("task callback must be a function");
            }
            q.started = true;
            if (!_isArray(data)) {
                data = [data];
            }
            if(data.length === 0) {
                // call drain immediately if there are no tasks
                return async.setImmediate(function() {
                    q.drain();
                });
            }
            _arrayEach(data, function(task) {
                var item = {
                    data: task,
                    priority: priority,
                    callback: typeof callback === 'function' ? callback : noop
                };

                q.tasks.splice(_binarySearch(q.tasks, item, _compareTasks) + 1, 0, item);

                if (q.tasks.length === q.concurrency) {
                    q.saturated();
                }
                async.setImmediate(q.process);
            });
        }

        // Start with a normal queue
        var q = async.queue(worker, concurrency);

        // Override push to accept second parameter representing priority
        q.push = function (data, priority, callback) {
            _insert(q, data, priority, callback);
        };

        // Remove unshift function
        delete q.unshift;

        return q;
    };

    async.cargo = function (worker, payload) {
        return _queue(worker, 1, payload);
    };

    function _console_fn(name) {
        return _restParam(function (fn, args) {
            fn.apply(null, args.concat([_restParam(function (err, args) {
                if (typeof console === 'object') {
                    if (err) {
                        if (console.error) {
                            console.error(err);
                        }
                    }
                    else if (console[name]) {
                        _arrayEach(args, function (x) {
                            console[name](x);
                        });
                    }
                }
            })]));
        });
    }
    async.log = _console_fn('log');
    async.dir = _console_fn('dir');
    /*async.info = _console_fn('info');
    async.warn = _console_fn('warn');
    async.error = _console_fn('error');*/

    async.memoize = function (fn, hasher) {
        var memo = {};
        var queues = {};
        var has = Object.prototype.hasOwnProperty;
        hasher = hasher || identity;
        var memoized = _restParam(function memoized(args) {
            var callback = args.pop();
            var key = hasher.apply(null, args);
            if (has.call(memo, key)) {   
                async.setImmediate(function () {
                    callback.apply(null, memo[key]);
                });
            }
            else if (has.call(queues, key)) {
                queues[key].push(callback);
            }
            else {
                queues[key] = [callback];
                fn.apply(null, args.concat([_restParam(function (args) {
                    memo[key] = args;
                    var q = queues[key];
                    delete queues[key];
                    for (var i = 0, l = q.length; i < l; i++) {
                        q[i].apply(null, args);
                    }
                })]));
            }
        });
        memoized.memo = memo;
        memoized.unmemoized = fn;
        return memoized;
    };

    async.unmemoize = function (fn) {
        return function () {
            return (fn.unmemoized || fn).apply(null, arguments);
        };
    };

    function _times(mapper) {
        return function (count, iterator, callback) {
            mapper(_range(count), iterator, callback);
        };
    }

    async.times = _times(async.map);
    async.timesSeries = _times(async.mapSeries);
    async.timesLimit = function (count, limit, iterator, callback) {
        return async.mapLimit(_range(count), limit, iterator, callback);
    };

    async.seq = function (/* functions... */) {
        var fns = arguments;
        return _restParam(function (args) {
            var that = this;

            var callback = args[args.length - 1];
            if (typeof callback == 'function') {
                args.pop();
            } else {
                callback = noop;
            }

            async.reduce(fns, args, function (newargs, fn, cb) {
                fn.apply(that, newargs.concat([_restParam(function (err, nextargs) {
                    cb(err, nextargs);
                })]));
            },
            function (err, results) {
                callback.apply(that, [err].concat(results));
            });
        });
    };

    async.compose = function (/* functions... */) {
        return async.seq.apply(null, Array.prototype.reverse.call(arguments));
    };


    function _applyEach(eachfn) {
        return _restParam(function(fns, args) {
            var go = _restParam(function(args) {
                var that = this;
                var callback = args.pop();
                return eachfn(fns, function (fn, _, cb) {
                    fn.apply(that, args.concat([cb]));
                },
                callback);
            });
            if (args.length) {
                return go.apply(this, args);
            }
            else {
                return go;
            }
        });
    }

    async.applyEach = _applyEach(async.eachOf);
    async.applyEachSeries = _applyEach(async.eachOfSeries);


    async.forever = function (fn, callback) {
        var done = only_once(callback || noop);
        var task = ensureAsync(fn);
        function next(err) {
            if (err) {
                return done(err);
            }
            task(next);
        }
        next();
    };

    function ensureAsync(fn) {
        return _restParam(function (args) {
            var callback = args.pop();
            args.push(function () {
                var innerArgs = arguments;
                if (sync) {
                    async.setImmediate(function () {
                        callback.apply(null, innerArgs);
                    });
                } else {
                    callback.apply(null, innerArgs);
                }
            });
            var sync = true;
            fn.apply(this, args);
            sync = false;
        });
    }

    async.ensureAsync = ensureAsync;

    async.constant = _restParam(function(values) {
        var args = [null].concat(values);
        return function (callback) {
            return callback.apply(this, args);
        };
    });

    async.wrapSync =
    async.asyncify = function asyncify(func) {
        return _restParam(function (args) {
            var callback = args.pop();
            var result;
            try {
                result = func.apply(this, args);
            } catch (e) {
                return callback(e);
            }
            // if result is Promise object
            if (_isObject(result) && typeof result.then === "function") {
                result.then(function(value) {
                    callback(null, value);
                })["catch"](function(err) {
                    callback(err.message ? err : new Error(err));
                });
            } else {
                callback(null, result);
            }
        });
    };

    // Node.js
    if (typeof module === 'object' && module.exports) {
        module.exports = async;
    }
    // AMD / RequireJS
    else if (typeof define === 'function' && define.amd) {
        define([], function () {
            return async;
        });
    }
    // included directly via <script> tag
    else {
        root.async = async;
    }

}());
appcontrollers.controller('CompaniesNotifiedTabController',
	['$window', '$modal', '$routeParams', '$scope', '$rootScope', '$location', '$filter', 'maxFieldLengths',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function($window, $modal, $routeParams, $scope, $rootScope, $location, $filter, maxFieldLengths,
				 $timeout, ngTableParams, DashboardNumberTags, DashboardService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;

			$scope.title = "Companies Notified Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			
			var donatePopupController = ['$window', '$scope', '$rootScope', '$modal', 'maxFieldLengths', '$location', '$routeParams', 'CompanyService', '$modalInstance',
				function($window, $scope, $rootScope, $modal, maxFieldLengths, $location, $routeParams, CompanyService, $modalInstance) {

					$scope.maxFieldLengths = maxFieldLengths;
					
					// to access dialog forms
					$scope.form = {};
					$scope.donationAmount;

					$scope.NUMBERINTERNAL_NODECIMALS_REGEXP = /^(\d*|\d+)$/;

					if($scope.initialised == null) {
						$scope.maxDonationLength = 7;
						$scope.success = false;
						// set first to true due actual payable check at the end
						$scope.userCanPay = true;

						$scope.initialised = true;
					}

					$scope.donationIsInvalid = function(donationAmount) {
						if(typeof $scope.form.formdonation.donationform != 'undefined' && $scope.form.formdonation.donationform.$dirty) {
							if(typeof donationAmount != 'undefined' && donationAmount != null && donationAmount != '') {
								if (!$scope.NUMBERINTERNAL_NODECIMALS_REGEXP.test(donationAmount)) {
									return true;
								} else if(donationAmount < 5 || donationAmount > 1000000) {
									return true;
								} else 
									return false;										
							} else 
								return true;																				
						} else {
							return false;
						}
					};
					
					$scope.donateDisabled = function(donationAmount) {
						if (typeof donationAmount != 'undefined' && donationAmount!=null && donationAmount!=''&&!$scope.donationIsInvalid(donationAmount))
							return false;
						else
							return true;
					};

					$scope.initiateDonation = function(amount) {
						CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
							if(typeof result.errorCode != 'undefined') {
								$scope.error = result.errorCode + " " + result.errorMsg;
							} else {
								$scope.userCanPay = result.val;

								if(false) { // !$scope.userCanPay turned off
									// Cannot donate because cannot pay
									$scope.noDonationInfo = 'U kunt helaas geen donaties doen omdat u nog geen betalingsgegevens hebt opgegeven. Dit kunt u aanpassen onder \'Mijn Account\'';
								} else {
									// DO Donation
									if(!$scope.donationIsInvalid(amount)) {
										0;
									}
									CompanyService.donate({donationAmount: amount}, function(result) {
										if(result.errorCode != null) {
											$scope.error = result.errorCode + ' ' + result.errorMsg;
										} else {
											if(result.val) {
												$scope.success = true;
											}
										}
									});
									
								}
							}
						});

					};

					$scope.cancel = function() {
						$modalInstance.close();
					}
				}];

			var RemoveNotificationController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function(reason) {
					$scope.reason = reason;
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function() {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];	

			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function() {
				0;
			};

			$scope.documentRequested = {
				action   : 'notified_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			isSearchChanged = function(search) {
				if(typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else {
					return $scope.oldSearch != search;
				}
			};

			$scope.$watch('filterCriteria.filterValue', function(search) {
				if(typeof searchCallbackTimeout != 'undefined') {
					$timeout.cancel(searchCallbackTimeout);
				}

				searchCallbackTimeout = $timeout(function() {
					if(search) {
						if(search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if(isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			if(typeof $scope.filterCriteria == 'undefined') {

				$scope.filterCriteria = {
					bedrijfId  : JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber : 1,
					sortDir    : "", // asc, desc
					sortedBy   : "",
					filterValue: "" // text to filter on
				};
			}

			$scope.nrOfItems = function() {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function() {
				return DashboardNumberTags.getNrOfNotifications() > 0;
			};

			// Data table functions
			$scope.filterResult = function() {

				return $scope.fetchResult(function() {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function(success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.companiesnotified($scope.filterCriteria, function(data, headers) {
					0;
					//$scope.companies = data.companyNotified;
					if(typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfNotifications($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function() {
					0;
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfNotifications($scope.totalItems);
					$scope.firstfetch = true;
				});

			};

			// Pagination functions
			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function() {
				delete $scope.notificationRemoved;
				delete $scope.error;
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function() {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.notificationChange = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$companiesnotifiedtab/' + meldingId);
			};

			$scope.notificationReadOnly = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$companiesnotifiedtab/' + meldingId + '/true');
			};

			removeNotification = function(meldingId, bedrijfId, reason) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				CompanyService.removeNotificationCompany({
					meldingId  : meldingId,
					bedrijfId  : bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden      : reason
				}, function(result) {
					if(typeof result.errorCode !== "undefined") {
						$scope.error = result.errorMsg;
					} else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.notificationRemove = function(meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller : RemoveNotificationController,
					size       : 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function(result) {
					if(result.remove) {
						$scope.initiateDonation(meldingId, bedrijfId, result.reason);
					}
				}, function() {
					0;
				});
			};

			$scope.notAllowedChangeNotification = function(statusCode) {
				if(statusCode != null) {
					if(statusCode == 'NOK' || statusCode == 'INI' || statusCode == 'INB') {
						return 'Vermelding in behandeling';
					} else if(statusCode == 'AFW' || statusCode == 'DEL') {
						return 'Vermelding is verwijderd';
					}
				}
			};

			$scope.showDonatePopup = function() {
				var donatePopup = $modal.open({
					templateUrl: 'donationPopup.html',
					controller : donatePopupController,
					size       : 'lg'
				});

				//donatePopup.result.then(function (result) {
				//	if (result.continue) {
				//		searchurl = '/donation/$dashboard$companiesnotifiedtab$search$' + $scope.filterCriteria.filterValue + '$' + $scope.currentPage;
				//
				//		$location.path(searchurl);
				//		$location.url($location.path());
				//	}
				//})
			};

			$scope.initiateDonation = function(meldingId, bedrijfId, reason) {
				removeNotification(meldingId, bedrijfId, reason);
				if(reason == 1 && (hasRole(JSON.parse($window.sessionStorage.user).roles, 'hoofd_klant') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_klant'))) {
					var donatePopup = $modal.open({
						templateUrl: 'donationPopup.html',
						controller : donatePopupController,
						size       : 'lg'
					});
				}
			};

			// fetch initial data for 1st time
			$scope.filterResult();
			
			if (typeof $window.sessionStorage.user !== 'undefined') {
				CompanyService.companyDataExtra({bedrijfId : JSON.parse($window.sessionStorage.user).bedrijfId}, function(result) {
	        		if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.companyAccount = result;
					}
	        	});
			}

			$scope.showRemoveNotification = function(userId, status) {
				//	    	 if (userId == JSON.parse($window.sessionStorage.user).userId)
				//	    		 return true;
				//	    	 else if (status == 'ACT')
				return true;

			}

		}]);appcontrollers.controller('CompaniesRemovedTabController',
	['$window', '$scope', '$rootScope', '$filter', '$timeout', 'maxFieldLengths',
		'ngTableParams', 'DashboardNumberTags', 'DashboardService',
		function ($window, $scope, $rootScope, $filter, $timeout, maxFieldLengths,
				  ngTableParams, DashboardNumberTags, DashboardService) {
		
			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.title = "Companies Removed Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			$scope.documentRequested = {
				action: 'removed_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			if (typeof $scope.filterCriteria == 'undefined') {
				$scope.sortedOrder = true;
				$scope.reverseSort = true;
				$scope.orderByField = 'datumEinde';

				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					view: 'all',
					sortDir: 'DESC', // asc, desc
					sortedBy: 'datumEinde',
					filterValue: '' // text to filter on
				};

				$scope.selectieMonitoring = true;
				$scope.selectieVermelding = true;
			}

			$scope.nrOfItems = function () {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfRemovedCompanies() > 0;
			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.companiesremoved($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfRemovedCompanies($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					0;
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfRemovedCompanies($scope.totalItems);
					$scope.firstfetch = true;
				});

			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.fetchOrder = function (field) {
				$scope.orderByField = field;
				$scope.reverseSort = !$scope.reverseSort;

				if ($scope.filterCriteria.sortDir == 'ASC')
					$scope.filterCriteria.sortDir = 'DESC';
				else
					$scope.filterCriteria.sortDir = 'ASC';

				$scope.filterCriteria.sortedBy = $scope.orderByField;
				$scope.filterCriteria.sortedOrder = $scope.reverseSort;

				$scope.currentPage = 1;
				$scope.pageChanged();
			};

			$scope.fetchSelectie = function (field) {
				if ($scope.selectieMonitoring == false &&
					$scope.selectieVermelding == false) {
					if (field == 'selMonitoring')
						$scope.selectieVermelding = true;
					else
						$scope.selectieMonitoring = true;
				}

				var oldval = $scope.filterCriteria.view;

				if ($scope.selectieMonitoring && $scope.selectieVermelding)
					$scope.filterCriteria.view = 'all';
				else if ($scope.selectieMonitoring)
					$scope.filterCriteria.view = 'monitoring';
				else if ($scope.selectieVermelding)
					$scope.filterCriteria.view = 'melding';

				if (oldval != $scope.filterCriteria.view) {
					$scope.currentPage = 1;
					$scope.pageChanged();
				}
			};

			$scope.createPeriod = function (from, to) {
				if (from && to)
					return from + ' - ' + to;
				else
					return 'nvt';
			};

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);appcontrollers.controller('ReportController', ['$routeParams', '$scope', '$rootScope', '$location', '$anchorScroll', 'reportData', 'monitorRequestData', 'monitoringBijBedrijf', 'CompanyService', 'DocumentService', function ($routeParams, $scope, $rootScope, $location, $anchorScroll, reportData, monitorRequestData, monitoringBijBedrijf, CompanyService, DocumentService) {
	0;

	if ($routeParams.searchurl)
		searchurl = $routeParams.searchurl;
	else
		searchurl = '$dashboard';
	
	if (typeof monitoringBijBedrijf != 'undefined')
		$scope.monitoringBijBedrijf = monitoringBijBedrijf;
	
	$scope.documentRequested = {action: 'report', bedrijfId: reportData.bedrijf.bedrijfId, meldingId: 0, referentie: reportData.referentieNummer};
	$scope.report = reportData;
	
	if ($scope.report && $scope.report.meldingen) {
		if (Object.prototype.toString.call( $scope.report.meldingen ) === '[object Array]')
			$scope.meldingenOverview =$scope.report.meldingen;
		else
			$scope.meldingenOverview = [$scope.report.meldingen];
	}
	else
		delete $scope.meldingenOverview;
	
	$scope.getReport = function(referentie) {
		DocumentService.report({referentie: referentie}, function(result) {
			//var file = new Blob([result], { type: 'application/pdf' });
            //var fileURL = URL.createObjectURL(file);
            //window.open(fileURL);
			
			$scope.$emit('downloaded', result);
		});
	}
	
  	$scope.monitoring = function() {  	
  		delete $scope.error;
  		delete $scope.monitorCreated;
  		
  		CompanyService.monitoringCompany(monitorRequestData, function(result) {
			if (typeof result.errorCode != 'undefined')
				$scope.error = result.errorCode + ' ' + result.errorMsg;
			else {
				$scope.monitorCreated = true;				
			}
		});  		
  	};
  	
  	$scope.showMonitoring = function() {
  		if ($scope.monitorCreated)
  			return false;
  		
  		if ($scope.monitoringBijBedrijf == 'false')
  			return true
  	}
  	
  	$scope.nrOfMeldingen = function(meldingen) {
  		var result = 0;
  		
  		if (typeof meldingen != 'undefined' )
  			result = meldingen.length;

  		return result;  		
  	}
  	
  	$scope.hideCompanyInfo = function(bedrijfId) {
  		if (typeof bedrijfId === 'undefined' || bedrijfId == null)
  			return true;
  		else
  			return false;
  	}
  	
  	$scope.getBedragText = function(bedrag) {
  		if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
  			return 'hoger dan \u20AC 100,-';
  		else
  			return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
  	}
  	
  	$scope.back = function() {
		url = gotoDurl(searchurl);
		if (url != null) {
			$location.path(url);
			$location.url($location.path());
		}

  	}
	
}]);appcontrollers.controller('CreateAccountController',
	['$window', '$scope', '$rootScope', '$location', '$anchorScroll', '$routeParams', '$timeout', 'maxFieldLengths',
		'$cookies', 'NewAccountService', 'CompanyService', 'clientip', 'recaptchasitekey', 'KortingsCodeService',
		function($window, $scope, $rootScope, $location, $anchorScroll, $routeParams, $timeout, maxFieldLengths,
				 $cookies, NewAccountService, CompanyService, clientip, recaptchasitekey, KortingsCodeService) { // 'vcRecaptchaService',
			0;
			
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
			
			0;

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

					0;

					if(typeof data.errorCode == 'undefined') {
						if(data != null && data.klant != null) {
							data.klant.nietBtwPlichtig = data.klant.nietBtwPlichtig == 'true';
						}
						$scope.account = data;
						$scope.bedrijfdataOk = true;

						$scope.masteraccount = angular.copy($scope.account);
					} else {
						0;
						$scope.error = data.errorMsg;
						gotoAnchor('alert');

						$scope.masteraccount = null;
					}

					$scope.kortingsCode = {};

					$scope.initialised = true;
					//success();

				}, function() {
					0;
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

				0;
				0;

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
						0;
					}
					else {
						//vcRecaptchaService.reload();
						resetNoCaptcha();
						0;
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
					0;
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
				//0; //vcRecaptchaService.data());

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
		}]);appcontrollers.controller('CreateCustomNotificationController', ['$window', '$scope', '$rootScope', '$routeParams', '$location', '$filter', 'maxFieldLengths', 'CompanyService', 'NotificationsToSend', function($window, $scope, $rootScope, $routeParams, $location, $filter, maxFieldLengths, CompanyService, NotificationsToSend) {
	0;

	$scope.maxFieldLengths = maxFieldLengths;
	
	var searchurl = $routeParams.searchurl;
	var companyidentifier = $routeParams.bedrijfId;
	var owncompanyidentifier = $routeParams.eigenBedrijfId;

	//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
	$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

	$scope.isInvalid = function(targetForm, targetField) {
		var result_invalid = targetForm[targetField].$invalid;
		var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

		//if(targetForm[targetField].$dirty) {
		//	delete $scope.error;
		//}

		if(targetField == 'incorrectGegeven' || targetField == 'faillissementVraag') {

			var anyReasonsAreDirty = targetForm['incorrectGegeven'].$dirty || targetForm['faillissementVraag'].$dirty || $scope.formSubmitted;

			if(anyReasonsAreDirty && $scope.melding.incorrectGegeven == false && $scope.melding.faillissementVraag == false) {
				return true;
			}
		}

		return result_dirty && result_invalid;
	};

	$scope.send = function() {

		0;
		$scope.formSubmitted = true;

		if(!($scope.formcreatecustomnotification.$invalid || $scope.buttonDisabled())) {
			delete $scope.formSubmitted;

			delete $scope.error;
			delete $scope.meldingSavedOk;
			delete $scope.vermeldingResultaat;

			var customMelding = {
				bedrijfId             : companyidentifier,
				bedrijfIdGerapporteerd: owncompanyidentifier,
				incorrectGegeven      : $scope.melding.incorrectGegeven,
				meldingDetails        : $scope.melding.meldingDetails,
				faillissementVraag    : $scope.melding.faillissementVraag,
				voornaam              : $scope.melding.naam,
				achternaam            : $scope.melding.achternaam,
				telefoonnummer        : $scope.melding.telefoonnummer,
				afdeling              : $scope.melding.afdeling,
				wachtwoord            : $scope.melding.wachtwoord,
				meldingen             : NotificationsToSend.getNotifications()
			};
			CompanyService.createCustomMelding(customMelding, function(response) {
				if(typeof response.errorCode !== 'undefined') {
					$scope.error = response.errorMsg;
				}
				else {
					$scope.meldingSavedOk = true;
				}
			});
		}
	};

	$scope.buttonDisabled = function() {
		return !!((!$scope.melding.incorrectGegeven && !$scope.melding.faillissementVraag) ||
		($scope.melding.meldingDetails == null || $scope.melding.meldingDetails == ''));
	};

	$scope.back = function() {
		url = gotoDurl(searchurl);
		if(url != null) {
			$location.path(url);
			$location.url($location.path());
		}
	};

	if(typeof $scope.init === "undefined") {
		$scope.melding = {
			incorrectGegeven     : false,
			faillissementVraag   : false,
			meldingDetails       : '',
			voornaam             : JSON.parse($window.sessionStorage.user).firstName,
			achternaam           : JSON.parse($window.sessionStorage.user).lastName,
			telefoonnummer       : JSON.parse($window.sessionStorage.user).gebruikerTelefoonNummer,
			afdeling             : null
		};
		$scope.formSubmitted = false;

		$scope.init = true;
	}
}]);appcontrollers.controller('CustomersAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$filter', 'maxFieldLengths',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService',
		'AccountService', 'LoginService', 'InternalProcessService', 'NewAccountService',
		function ($window, $modal, $scope, $rootScope, $location, $filter, maxFieldLengths,
				  $timeout, ngTableParams, DashboardNumberTags, DashboardService,
				  AccountService, LoginService, InternalProcessService, NewAccountService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.title = "Customers Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			$scope.noNewCustomerLetterBatch = false;
			$scope.batchStarted = false;
			$scope.sizeOfLetterBatch = 0;

			var companies = [];

			var createBatchPopupController = ['$scope', '$modalInstance', 'sizeOfBatch', function ($scope, $modalInstance, sizeOfBatch) {
				$scope.continue = false;
				$scope.sizeOfBatch = sizeOfBatch;

				$scope.acceptPopup = function () {
					$scope.continue = true;
					$scope.discardPopup();
				};

				$scope.discardPopup = function () {
					var result = {continue: $scope.continue};
					$modalInstance.close(result);
				};
			}];

			var RemoveCustomerController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeCustomerOk = function (reason) {
					$scope.remove = true;
					$scope.closeRemoveCustomerModal();
				};

				$scope.closeRemoveCustomerModal = function () {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];
			
			var ActivateCustomerController = ['$scope', '$modalInstance', 'regpro', 'company', function ($scope, $modalInstance, regpro, company) {
				$scope.activate = false;
				$scope.regpro = regpro;

				$scope.activateCustomerOk = function (reason) {
					$scope.activate = true;
					$scope.closeActivateCustomerModal();
				};

				$scope.closeActivateCustomerModal = function () {
					var result = {
						activate: $scope.activate,
						regpro: regpro,
						company: company
					};

					$modalInstance.close(result); 
				};
			}];			

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			doActivatePro = function (activationid, userid) {
				cleanUp();

				AccountService.activateCustomerBriefCode({
					activationcode: activationid,
					userid: userid
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.prospectActivated = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			doActivateReg = function (activationid, userid) {
				cleanUp();

				LoginService.activateCustomer({activationid: activationid, username: userid}, function (result) {
					if (typeof result.errorCode !== 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					}
					else {
						$scope.registrationActivated = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.createDocumentRequested = function (bedrijfId) {
				return {
					action: 'letter_customer',
					bedrijfId: bedrijfId,
					meldingId: 0,
					referentie: ''
				};
			};

			$scope.getDagenSinds = function(d) {
				if (typeof d !== 'undefined' && d != null && d.length >= 10) {
					date = new Date(d.substring(6, 10), d.substring(3, 5) - 1, d.substring(0, 2));
					diffc = new Date() - date;
					//getTime() function used to convert a date into milliseconds. This is needed in order to perform calculations.
					 
					days = Math.round(Math.abs(diffc/(1000*60*60*24)));
					//this is the actual equation that calculates the number of days.
					 
					return days;
				} else
					return 'onbekend';
			}
			
			$scope.detailsCustomer = function (bedrijfId) {
				$location.path('/klantaccount/' + bedrijfId + '/$dashboard$customersadmintab');
				$location.url($location.path());
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.customersadmin($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfCustomersAdmin($scope.totalItems);

					success();
				}, function () {
					0;
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfCustomersAdmin($scope.totalItems);
				});

			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.hasAdminRole = function () {
				return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr');
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfCustomersAdmin() > 0;
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.printCustomerLetter = function (klantId, arrayIndex) {
				if (!$scope.companies[arrayIndex].briefGedownload) {
					InternalProcessService.printCustomerLetter({klantId: klantId}, function (result) {
						if (result.errorCode != null) {
							$scope.error = result.errorCode + ' ' + result.errorMsg;
						} else {
							$scope.companies[arrayIndex].briefStatus = 'DWL';
						}
					});
				}
			};

			$scope.removeCustomer = function (userid) {
				var modalInstance = $modal.open({
					templateUrl: 'removecustomer.html',
					controller: RemoveCustomerController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveCustomer(userid);
					}
				}, function () {
					0;
				});

			};
			
			$scope.activateCustomer = function (regpro, company) {
				var modalInstance = $modal.open({
					templateUrl: 'activatecustomer.html',
					controller: ActivateCustomerController,
					resolve       : {regpro: function() { return regpro; }, company: function(){ return company; }},
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.activate) {
						if (result.regpro == 'REG')
							doActivateReg(result.company.activationCode, result.company.klantGebruikersNaam);
						else if (result.regpro == 'PRO')
							doActivatePro(result.company.activationCode, result.company.klantId);
					} 
				}, function () {
					0;
				});

			};

			$scope.requestreport = function (bedrijfId) {
				$location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/$dashboard$customersadmintab');
			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			cleanUp = function () {
				delete $scope.error;
				delete $scope.registrationActivated;
				delete $scope.prospectActivated;
				delete $scope.batchStarted;
				delete $scope.customerRemoved;
			};

			doRemoveCustomer = function (userid) {
				cleanUp();

				AccountService.deleteAccount(userid, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.customerRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			if ($scope.init == undefined) {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};

				$scope.init = true;
			}

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);appcontrollers.controller('DashboardController',
    ['$window', '$route', '$routeParams', '$scope', '$rootScope', '$q', '$location',
        'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'UserService', 'InternalProcessService',
        function ($window, $route, $routeParams, $scope, $rootScope, $q, $location,
                  ngTableParams, DashboardNumberTags, DashboardService, UserService, InternalProcessService) {
            0;

            if ($routeParams.showtab)
                var showtab = $routeParams.showtab;

			$scope.tabClicked = function(){
				delete $scope.error;
				delete $rootScope.error;
			};

            $scope.edit = function (editobject) {
                $location.path(editobject.htmlUrl);
            };

            $scope.fullreport = function () {
                $location.path("/fullreport");
                $location.url("/fullreport");
            };

            $scope.hasRole1 = function (roles, role) {
                return hasRole(roles, role);
            };

            $scope.messages = function () {
                $location.path("/messages");
            };

            $scope.nrOfItems = function (tab) {
            	var roles = null;
            	
            	if (typeof $window.sessionStorage.user !== 'undefined')
            		roles = JSON.parse($window.sessionStorage.user).roles;
            	
                if (tab == 'alerts') {
                    if (DashboardNumberTags.getNrOfAlerts() == -1) // initialize 1st time
                    {
                        DashboardNumberTags.setNrOfAlerts(0);
                        $scope.itemsPage = 20;
                        $scope.filterCriteriaAlerts = {
                            bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                            pageNumber: 1,
                            sortDir: "", // asc, desc
                            sortedBy: "",
                            filterValue: "" // text to filter on
                        };
                        $rootScope.range = "items=" + (($scope.filterCriteriaAlerts.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteriaAlerts.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                        //var companiesalertData = DashboardService.companiesalert($scope.filterCriteriaAlerts);
                        //
                        //var resultsPromise = $q.all({
                        //    companiesalertdata: companiesalertData.$promise
                        //}).then(function (data, headers) {
                        //    if (typeof data.alertOverviewTransfer != 'undefined') {
                        //        DashboardNumberTags.setNrOfAlerts(paging_totalItems(headers("Content-Range")));
                        //    }
                        //    else {
                        //        DashboardNumberTags.setNrOfAlerts(0);
                        //    }
                        //}).catch(function (error) {
                        //    0;
                        //})

                        DashboardService.companiesalert($scope.filterCriteriaAlerts, function (data, headers) {
                            0;
                            if (typeof data != 'undefined') {
                                DashboardNumberTags.setNrOfAlerts(paging_totalItems(headers("Content-Range")));
                            }
                            else {
                                DashboardNumberTags.setNrOfAlerts(0);
                            }

                        }, function () {
                            0;
                        });
                    }

                    return Math.min(99, DashboardNumberTags.getNrOfAlerts());
                } else if(tab=='internalprocessadmin' && roles != null && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'))){
					if(DashboardNumberTags.getNrOfProcesses()==-1){
						DashboardNumberTags.setNrOfProcesses(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=1-" + $scope.itemsPage;

						InternalProcessService.getInternalProcessRows(function(data, headers){
							if(data!=null){
								DashboardNumberTags.setNrOfProcesses(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfProcesses(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfProcesses());
                } else if(tab=='objectionsadmin' && roles != null && hasRole(roles, 'admin_sbdr_hoofd')){
					if(DashboardNumberTags.getNrOfObjectionsAdmin()==-1){
						DashboardNumberTags.setNrOfObjectionsAdmin(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=0-" + $scope.itemsPage;

						DashboardService.objectionsadmin($scope.filterCriteriaAlerts, function (data, headers) {
							if(data!=null){
								DashboardNumberTags.setNrOfObjectionsAdmin(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfObjectionsAdmin(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfObjectionsAdmin());
                } else if(tab=='supportticketsadmin' && roles != null && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'))){
					if(DashboardNumberTags.getNrOfSupportTicketsAdmin()==-1){
						DashboardNumberTags.setNrOfSupportTicketsAdmin(0);

						$scope.totalItems = 0;
						$scope.currentPage = 1;
						$scope.itemsPage = 20;
						$scope.maxSize = 5;

						$rootScope.range = "items=0-" + $scope.itemsPage;

						DashboardService.supportticketsadmin($scope.filterCriteriaAlerts, function (data, headers) {
							if(data!=null){
								DashboardNumberTags.setNrOfSupportTicketsAdmin(paging_totalItems(headers("Content-Range")));
							} else {
								DashboardNumberTags.setNrOfSupportTicketsAdmin(0);
							}
						});
					}
					return Math.min(99, DashboardNumberTags.getNrOfSupportTicketsAdmin());					
                } else
                    return 0; // default no items
            };

            $scope.searchCompany = function () {
                $location.path("/searchcompany");
            };
            
			$scope.notAllowedNotification = function() {
				if(!$rootScope.hasRoleUser('registraties_toegestaan')) {
					return 'Geen rechten om registraties in te bewerken';
				} else {
					return '';
				}
			};            

            $scope.showTab = function (tab) {
                if (typeof $window.sessionStorage.user != 'undefined') {
                    var roles = JSON.parse($window.sessionStorage.user).roles;

                    // admin_sbdr, admin_klant, hoofd_klant, gebruiker_klant
                    if (typeof roles != 'undefined') {
                        //if (tab == 'overview') {
                        //    if ($scope.showtab == 'overviewtab') {
                        //        $scope.tabs[0].select();
                        //        resetShowTab();
                        //    }
                        //    return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant');
                        //} else
                        if (tab == 'alerts') {
                            if ($scope.showtab == 'alertstab' || (typeof $scope.showtab === 'undefined' && !isUserTabSelected() && (hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager')))) {
                                $scope.tabs[0].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager'); 
                        } else if (tab == 'reportrequested') {
                            if ($scope.showtab == 'reportrequestedtab') {
                                $scope.tabs[1].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'monitoring') {
                            if ($scope.showtab == 'portfoliomonitoringtab') {
                                $scope.tabs[2].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'notifications') {
                            if ($scope.showtab == 'companiesnotifiedtab') {
                                $scope.tabs[3].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'removedcompanies') {
                            if ($scope.showtab == 'companiesremovedtab') {
                                $scope.tabs[4].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_klant') || hasRole(roles, 'hoofd_klant') || hasRole(roles, 'gebruiker_klant') || hasRole(roles, 'bedrijf_manager');
                        } else if (tab == 'generaladmin') {
                            if ($scope.showtab == 'generaladmintab') { // || (typeof $scope.showtab == 'undefined' && hasRole(roles, 'admin_sbdr')  && !isAdminTabSelected())) { // for showtab or isAdmin
                                $scope.tabs[5].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'objectionsadmin') {
                        	if ($scope.showtab =='objectionsadmintab') {
                                $scope.tabs[6].select();
                                resetShowTab();                        		
                        	}
                        	return hasRole(roles, 'admin_sbdr_hoofd');
                        } else if (tab == 'supportticketsadmin') {
                        	if ($scope.showtab =='supportticketsadmintab') {
                                $scope.tabs[7].select();
                                resetShowTab();                        		
                        	}
                        	return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
                    	} else if (tab == 'searchresultsadmin') {
                            if ($scope.showtab == 'searchresultsadmintab'  || (typeof $scope.showtab == 'undefined' && (hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd')) && !isAdminTabSelected())) { // || (hasRole(roles, 'admin_sbdr_hoofd') ) {
                                $scope.tabs[8].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'customersadmin') {
                            if ($scope.showtab == 'customersadmintab') {
                                $scope.tabs[9].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'notificationsadmin') {
                            if ($scope.showtab == 'notificationsadmintab') {
                                $scope.tabs[10].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'notificationsofprospectadmin') {
                            if ($scope.showtab == 'notificationsofprospectadmintab') {
                                $scope.tabs[11].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if (tab == 'exceptioncompaniesadmin') {
                            if ($scope.showtab == 'exceptioncompaniesadmintab') {
                                $scope.tabs[12].select();
                                resetShowTab();
                            }
                            return hasRole(roles, 'admin_sbdr_hoofd'); // || hasRole(roles, 'admin_klant');
                        } else if(tab=='internalprocessadmin'){
							if($scope.showTab == 'internalprocessadmin'){
								$scope.tabs[13].select();
								resetShowTab();
							}
							return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
						}
                    }
                }

                return false;
            };
            
            $scope.updateCompany = function(bedrijfId) {
            	UserService.userdataOfCompany({bedrijfId: bedrijfId },function(user) {
					$window.sessionStorage.user = JSON.stringify(user); 
					// change bedrijfId in authtoken to force from now on service calls for 'the other' company
					if (typeof $window.sessionStorage.authToken !== 'undefined') {
						var res = $window.sessionStorage.authToken.split(":");
						if (res.length == 5) {
							res[2] = user.bedrijfId;
							 $window.sessionStorage.authToken = res[0] + ":" + res[1] + ":" + res[2] + ":" + res[3] + ":" + res[4];
						}
					}

					$route.reload();
            	});
            }

            isAdminTabSelected = function () {
                return $scope.tabs[5].selected || $scope.tabs[6].selected || $scope.tabs[7].selected || $scope.tabs[8].selected || $scope.tabs[9].selected || $scope.tabs[10].selected || $scope.tabs[11].selected || $scope.tabs[12].selected || $scope.tabs[13].selected ;
            };
            
            isUserTabSelected = function() {
            	return $scope.tabs[0].selected || $scope.tabs[1].selected || $scope.tabs[2].selected || $scope.tabs[3].selected || $scope.tabs[4].selected; 
            }

            resetShowTab = function () {
                //delete showtab;
                delete $scope.showtab;
                //$location.path("/dashboard");
                //$location.url("/dashboard");
                $rootScope.changeUrlWithoutReload("/dashboard");
            };
            
            resetAllCompaniesOfUser = function() { 
	            $scope.gebruikerBedrijfId = null;
	        	$scope.gebruikerBedrijven = null;
	        	if (typeof $window.sessionStorage.user !== 'undefined')
	        		$scope.gebruikerBedrijfId =  JSON.parse($window.sessionStorage.user).bedrijfId;
	        	
	        	var allCompaniesOfUserCall = DashboardService.allCompaniesOfUser({bedrijfId : $scope.gebruikerBedrijfId});
	        	
	        	// default
	        	$scope.aantalGebruikerBedrijven = 1;
	        	
				var resultsPromise2 = $q.all({
					allCompaniesOfUser: allCompaniesOfUserCall.$promise
				}).then(function(data) {
					if (typeof data !== 'undefined' && data.allCompaniesOfUser !== 'undefined') {	
						$scope.gebruikerBedrijven = data.allCompaniesOfUser;
						delete $scope.gebruikerBedrijfId;
						$scope.aantalGebruikerBedrijven = $scope.gebruikerBedrijven.length;
						for (var i = 0; i < $scope.aantalGebruikerBedrijven; i++) {
						    if (data.allCompaniesOfUser[i].currentBedrijf)
						    	$scope.gebruikerBedrijfId = data.allCompaniesOfUser[i].bedrijfId;
						}
						if (typeof $scope.gebruikerBedrijfId === 'undefined')
							$scope.gebruikerBedrijfId = data.allCompaniesOfUser[0].bedrijfId;
						
						return data;
					} else
						return 'unknown';
				}).catch(function(error) {
					0;
					$scope.error = error;
				});
            }
            
            if (typeof $scope.initDashboard === 'undefined') {
            	resetAllCompaniesOfUser();
            	
            	$scope.initDashboard = true;
            }

            if (showtab) {           	           	
                //$scope.tabs[showtab].select();
                //$scope.select(showtab);
                $scope.showtab = showtab;
                showtab = null;
            }
        }]);appcontrollers.controller('DocumentController', ['$scope', '$routeParams', '$location', '$fileUploader', function ($scope, $routeParams, $location, $fileUploader) { 
    'use strict';
    
	0;

	$scope.shipmentId = $routeParams.id;
	
	$scope.attachments = [  ]; // { vnr: "", description: "", country: "", weight: "", netweight: "" }
	
	$scope.countries = [ "Belgium", "Netherlands"  ];
		
	// expose a function to add new (blank) rows to the model/table
	$scope.addGoods = function() { 
	    // push a new object with some defaults
	    $scope.attachments.push({ vnr: "", description: "", country: $scope.countries[0], weight: "", netweight: "" }); 
	};
	
	$scope.removeGoods = function() {
		var index = this.$index;
		var item_to_delete = $scope.attachments[index];
		
		//server side deletion, on success remove from array API.DeleteGoods({ id: item_to_delete.id }, function (success) {
		    $scope.attachments.splice(index, 1);
		//  });		
		
	};
	
	$scope.save = function() {
		0;
		
		$location.path('/dashboard');
	};
	

    // create a uploader with options
    var uploader = $scope.uploader = $fileUploader.create({
        scope: $scope,                          // to automatically update the html. Default: $rootScope
        url: 'upload.php',
        formData: [
            { key: 'value' }
        ],
        filters: [
            function (item) {                    // first user filter
                0;
                return true;
            }
        ]
    });


    // FAQ #1
    var item = {
        file: {
            name: 'Previously uploaded file',
            size: 1e6
        },
        progress: 100,
        isUploaded: true,
        isSuccess: true
    };
    item.remove = function() {
        uploader.removeFromQueue(this);
    };
    //uploader.queue.push(item);
    //uploader.progress = 100;


    // ADDING FILTERS

    uploader.filters.push(function (item) { // second user filter
        0;
        return true;
    });

    // REGISTER HANDLERS

    uploader.bind('afteraddingfile', function (event, item) {
        0;
    });

    uploader.bind('whenaddingfilefailed', function (event, item) {
        0;
    });

    uploader.bind('afteraddingall', function (event, items) {
        0;
    });

    uploader.bind('beforeupload', function (event, item) {
        0;
    });

    uploader.bind('progress', function (event, item, progress) {
        0;
    });

    uploader.bind('success', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('cancel', function (event, xhr, item) {
        0;
    });

    uploader.bind('error', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('complete', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('progressall', function (event, progress) {
        0;
    });

    uploader.bind('completeall', function (event, items) {
        0;
    });
	
	
}]);
appcontrollers.controller('DocumentDownloadController', ['$routeParams', '$rootScope', '$scope', '$attrs', '$window', '$location', '$sce', '$resource', '$modal', 'DocumentService', function ($routeParams, $rootScope, $scope, $attrs, $window, $location, $sce, $resource, $modal, DocumentService) {

		$scope.documentFetched = function (documentresult) {
			var octetStreamMime = 'application/octet-stream';
			var filename = 'document.bin';
			var success = false;

			// Get the headers
			headers = documentresult.headers();

			// Get the filename from the x-filename header or default to "download.bin"
			var filename = headers['x-filename'] || filename;

			// Determine the content type from the header or default to "application/octet-stream"
			var contentType = headers['content-type'] || octetStreamMime;

			try {
				// Try using msSaveBlob if supported
				0;
				var blob = new Blob([documentresult.data], {type: contentType});
				if ($scope.openDocumentInWindow == true) {
					if (navigator.msSaveOrOpenBlob)//if(navigator.msSaveBlob)
						navigator.msSaveOrOpenBlob(blob, filename);
					else {
						// Try using other saveBlob implementations, if available
						var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
						if (saveBlob === undefined) throw "Not supported";
						saveBlob(blob, filename);
					}
				}
				else {
					if (navigator.msSaveBlob)//if(navigator.msSaveBlob)
						navigator.msSaveBlob(blob, filename);
					else {
						// Try using other saveBlob implementations, if available
						var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
						if (saveBlob === undefined) throw "Not supported";
						saveBlob(blob, filename);
					}
				}
				0;
				success = true;
			} catch (ex) {
				0;
				0;
			}

			if (!success) {
				// Get the blob url creator
				var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
				if (urlCreator) {
					// Try to use a download link
					var link = document.createElement('a');
					if ('download' in link) {
						// Try to simulate a click
						try {
							// Prepare a blob URL
							0;
							var blob = new Blob([documentresult.data], {type: contentType});
							var url = urlCreator.createObjectURL(blob, filename);
							if ($scope.openDocumentInWindow == true) {
								window.open(url);
							} else {
								link.setAttribute('href', url);

								// Set the download attribute (Supported in Chrome 14+ / Firefox 20+)
								link.setAttribute("download", filename);

								// Simulate clicking the download link
								var event = document.createEvent('MouseEvents');
								event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
								link.dispatchEvent(event);
							}
							0;
							success = true;

						} catch (ex) {
							0;
							0;
						}
					}
					
					if (!success) {
						// Fallback to window.location method
						try {
							// Prepare a blob URL
							// Use application/octet-stream when using window.location to force download
							0;
							var blob = new Blob([documentresult.data], {type: contentType});
							window.location = urlCreator.createObjectURL(blob);
							0;
							success = true;
						} catch (ex) {
							0;
							0;
						}
					}					

					if (!success) {
						// Final Fallback to window.location method
						try {
							// Prepare a blob URL
							// Use application/octet-stream when using window.location to force download
							0;
							var blob = new Blob([documentresult.data], {type: octetStreamMime});
							window.location = urlCreator.createObjectURL(blob);
							0;
							success = true;
						} catch (ex) {
							0;
							0;
						}
					}
				}
			}

			if (!success) {
				// Fallback to window.open method
				0;
				// window.open(httpPath, '_blank', '');
			}
		};

		// Based on an implementation here: web.student.tuwien.ac.at/~e0427417/jsdownload.html
		$scope.downloadDocument = function (documentrequest) {

			var method = documentrequest.action;
			var bedrijfId = documentrequest.bedrijfId;
			var meldingId = documentrequest.meldingId;
			var referentie = documentrequest.referentie;
			var supportAttachmentId = documentrequest.supportBestandId;
			var factuurId = documentrequest.factuurId;
			var monitorId = documentrequest.monitorId;
			var batchDocumentId = documentrequest.batchDocumentId;
			if (typeof meldingId == 'undefined' || meldingId == null)
				melding = 0;
			if (typeof referentie == 'undefined' || referentie == null || referentie == '')
				referentie = 'unknown';

			if (method == 'removed_companies' || method == 'monitored_companies' || method == 'notified_companies' || method == 'reported_companies') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadExcel({
					method: method,
					bedrijfid: bedrijfId
				}, $scope.documentFetched);
			} else if (method == 'support_attachment') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadAttachment({
					supportattachmentid: supportAttachmentId
				}, $scope.documentFetched);
			} else if (method == 'factuur') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadFactuur({
					factuurid: factuurId
				}, $scope.documentFetched);
			} else if (method == 'letter_batch') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadBatchDocument({
					batchDocumentId: batchDocumentId
				}, $scope.documentFetched);
			} else {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadDocument({
					method: method,
					bedrijfid: bedrijfId,
					meldingid: meldingId,
					referentie: referentie,
					monitorid: monitorId
				}, $scope.documentFetched);
			}
//		.error(function(data, status) {
//	        0;
			//
//	        // Optionally write the error out to scope
//	        $scope.errorDetails = "Request failed with status: " + status;
//	    });

		};

	}]
);appcontrollers.controller('EditController', ['$scope', '$routeParams', '$location',  '$fileUploader', function ($scope, $routeParams, $location,  $fileUploader) { 
	0;

	$scope.shipmentId = $routeParams.id;
	
	//$scope.newsEntry = NewsService.get({id: $routeParams.id});
	
	$scope.save = function() {
		0;
		
		$location.path('/dashboard');
	};
	//$scope.save = function() {
	//	$scope.newsEntry.$save(function() {
	//		$location.path('/');
	//	});
	//};
	
    // create a uploader with options
    var uploader = $scope.uploader = $fileUploader.create({
        scope: $scope,                          // to automatically update the html. Default: $rootScope
        url: 'upload.php',
        formData: [
            { key: 'value' }
        ],
        filters: [
            function (item) {                    // first user filter
                0;
                return true;
            }
        ]
    });


    // FAQ #1
    var item = {
            file: {
                name: 'dummy',
                size: 1e6
            },
            progress: 100,
            documentType: 'CVO',
            isProcessDocument: true,
            isUploaded: false,
            isSuccess: true,
            isAttachment: false,
            isProve: false,
            isLegaliseRequest: false
        };
    
    var item1 = {
        file: {
            name: 'CVO',
            size: 1e6
        },
        progress: 100,
        documentType: 'CVO',
        isProcessDocument: true,
        isUploaded: false,
        isSuccess: true,
        isAttachment: false,
        isProve: false,
        isLegaliseRequest: false
    };
    
    var item2 = {
            file: {
                name: 'An uploaded file',
                size: 1e6
            },
            progress: 100,
            documentType: 'Attachment',
            isProcessDocument: false,
            isUploaded: true,
            isSuccess: true,
            isAttachment: true,
            isProve: false,
            isLegaliseRequest: false
        };

    var item3 = {
            file: {
                name: 'Another uploaded file',
                size: 1e6
            },
            progress: 100,
            documentType: 'Attachment',
            isProcessDocument: false,
            isUploaded: true,
            isSuccess: true,
            isAttachment: true,
            isProve: false,
            isLegaliseRequest: false
        };
    
    item.remove = function() {
        uploader.removeFromQueue(this);
    };
    
    uploader.queue.push(item1);
    uploader.queue.push(item2);
    uploader.queue.push(item3);
    
    uploader.progress = 100;


    // ADDING FILTERS

    uploader.filters.push(function (item) { // second user filter
        0;
        return true;
    });

    // REGISTER HANDLERS

    uploader.bind('afteraddingfile', function (event, item) {
        0;
    });

    uploader.bind('whenaddingfilefailed', function (event, item) {
        0;
    });

    uploader.bind('afteraddingall', function (event, items) {
        0;
    });

    uploader.bind('beforeupload', function (event, item) {
        0;
    });

    uploader.bind('progress', function (event, item, progress) {
        0;
    });

    uploader.bind('success', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('cancel', function (event, xhr, item) {
        0;
    });

    uploader.bind('error', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('complete', function (event, xhr, item, response) {
        0;
    });

    uploader.bind('progressall', function (event, progress) {
        0;
    });

    uploader.bind('completeall', function (event, items) {
        0;
    });
	
}]);appcontrollers.controller('EditCompanyController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$anchorScroll',
		'$routeParams', '$cookies', 'ExceptionCompanyDataStorage', 'maxFieldLengths',
		'companyData', 'AccountService', 'CompanyService',
		function($window, $modal, $scope, $rootScope, $location, $anchorScroll,
				 $routeParams, $cookies, ExceptionCompanyDataStorage, maxFieldLengths, companyData,
				 AccountService, CompanyService) {
			0;

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
					0;
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

		}]);appcontrollers.controller('ExactOnlineController', ['$window', '$modal', '$scope', '$rootScope', '$location', '$anchorScroll', '$routeParams', '$cookies', 'exactonlineparamData', function ($window, $modal, $scope, $rootScope, $location, $anchorScroll, $routeParams, $cookie, exactonlineparamData) {
	0;
	
	 
	if (typeof $scope.init === "undefined")
	{
		$scope.exactonlineparam = exactonlineparamData.exactonlineparam;
		
		$scope.init = true;
	}			     


			  			      
}]);appcontrollers.controller('ExceptionCompaniesAdminTabController',
	['$window', '$scope', '$rootScope', '$location', '$filter',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', '$modal', 'maxFieldLengths',
		'ExceptionCompanyDataStorage', 'DashboardService', 'CompanyService',
		function ($window, $scope, $rootScope, $location, $filter,
				  $timeout, ngTableParams, DashboardNumberTags, $modal, maxFieldLengths,
				  ExceptionCompanyDataStorage, DashboardService, CompanyService) {
			
			$scope.title = "ExceptionCompanies Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			var companies = [];

			var removeNotificationController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.deleteNotification = false;
				$scope.Continue = function () {
					$scope.deleteNotification = true;
					$scope.Discard();
				};

				$scope.Discard = function () {
					var result = {
						deleteNotification: $scope.deleteNotification
					};

					$modalInstance.close(result);
				}
			}];

			if (typeof $scope.filterCriteria == 'undefined') {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};
			}

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			$scope.detailsCompany = function (customMeldingId, bedrijfId) {
				if ($scope.companies && typeof customMeldingId !== 'undefined' && customMeldingId !== null) {
					var customMelding = null;
					for (var i in $scope.companies) {
						if ($scope.companies[i].customMeldingId == customMeldingId) {
							customMelding = $scope.companies[i];
							break; //Stop this loop, we found it!
						}
					}

					if (customMelding != null) {
						ExceptionCompanyDataStorage.setExceptionCompanyData(customMelding);

						$location.path('/editcompany/' + bedrijfId + '/$dashboard$exceptioncompaniesadmintab' + '/true');
						$location.url($location.path());
					}
				}

			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.exceptioncompaniesadmin($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfExceptionCompaniesAdmin($scope.totalItems);

					success();
				}, function () {
					0;
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfExceptionCompaniesAdmin($scope.totalItems);
				});

			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfExceptionCompaniesAdmin() > 0;
			};

			$scope.ignoreException = function (customMeldingId, bedrijfId) {
				var confirmationModal = $modal.open({
					templateUrl: 'removeCustomNotificationAdmin.html',
					controller: removeNotificationController,
					size: 'lg'
				});

				confirmationModal.result.then(function (result) {
					if (result.deleteNotification) {
						ignoreNotification(customMeldingId, bedrijfId);
					}
				});
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.resolveException = function (customMeldingId, bedrijfId) {
				var confirmationModal = $modal.open({
					templateUrl: 'removeCustomNotificationAdmin.html',
					controller: removeNotificationController,
					size: 'lg'
				});

				confirmationModal.result.then(function (result) {
					if (result.deleteNotification) {
						resolveNotification(customMeldingId, bedrijfId);
					}
				});
			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			cleanUp = function () {
				delete $scope.error;
				delete $scope.exceptionResolved;
				delete $scope.exceptionIgnored;
			};

			ignoreNotification = function (customMeldingId, bedrijfId) {
				delete $scope.exceptionResolved;
				CompanyService.ignoreException({
					customMeldingId: customMeldingId,
					bedrijfId: bedrijfId
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.exceptionIgnored = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			resolveNotification = function (customMeldingId, bedrijfId) {
				delete $scope.exceptionResolved;
				CompanyService.resolveException({
					customMeldingId: customMeldingId,
					bedrijfId: bedrijfId
				}, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else
						$scope.exceptionResolved = true;
					// refresh
					$scope.currentPage = 1;
					$scope.pageChanged();
				});
			};

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);
appcontrollers.controller('FaillissementenOverzichtController',
	['$window', '$modal', '$scope', '$rootScope', '$location',
		'$anchorScroll', '$routeParams', '$cookies', 'AccountService',
		function ($window, $modal, $scope, $rootScope, $location,
				  $anchorScroll, $routeParams, $cookies, AccountService) {
			0;

			$scope.userIdSelected = null;
			$scope.setSelected = function (userId) {
				$scope.userIdSelected = userId;
			};

			delete $scope.error;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
			};

			if (typeof $scope.init === "undefined") {

				$scope.filterCriteria = {
					userId: JSON.parse($window.sessionStorage.user).userId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20;
				$scope.maxSize = 5;

				var users = [];

				AccountService.getFaillissementStats(function (result) {
					if (result.errorCode != null)
						$scope.error = result.errorCode + " " + result.errorMsg;
					else
						$scope.stats = result;
				});

				$scope.init = true;
			}

			$scope.showKvKNumber = function(kvkNr){
				var text;
				if(kvkNr==null||typeof kvkNr== 'undefined'){
					text = 'Natuurlijk persoon';
				} else {
					text = kvkNr;
				}

				return text;
			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// selected userId delete
					$scope.userIdSelected = null;
					// rootscope range delete
					delete $scope.range;
				});
			};
			0;

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				AccountService.faillissementenafgelopenweek($scope.filterCriteria, function (data, headers) {
					0;

					$scope.faillissementen = data;

					$scope.totalItems = paging_totalItems(headers("Content-Range"));
					success();
				}, function () {
					0;
					$scope.faillissementen = [];
					$scope.totalItems = 0;
				});

			};
			0;

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);appcontrollers.controller('ForgotPasswordController',
	['$scope', '$rootScope', '$location', '$anchorScroll', 'UserNsService', 'NewAccountService', 'clientip', 'maxFieldLengths',
		function ($scope, $rootScope, $location, $anchorScroll, UserNsService, NewAccountService, clientip, maxFieldLengths) {
			0;

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
appcontrollers.controller('FullReportController', ['$routeParams', '$scope', '$rootScope', '$location', '$anchorScroll', 'CompanyService', 'DocumentService', function ($routeParams, $scope, $rootScope, $location, $anchorScroll, CompanyService, DocumentService) {
	0;

	if ($routeParams.searchurl)
		searchurl = $routeParams.searchurl;
	else
		searchurl = '$dashboard';
	
	$scope.chart_options = {
			resize: true,
			element: 'donut-vermeldingen',
		    data: [
		      {label: "Actief", value: 2},
		      {label: "Verwijderd", value: 0},
		      {label: "Recent", value: 2}
		    ]
		  };

	$scope.chart_options = {
			resize: true,
			element: 'donut-vermeldingen',
		    data: [
		      {label: "Monitoring", value: 2},
		      {label: "Verwijderd", value: 0},
		      {label: "Recent", value: 2}
		    ]
		  };	
}]);appcontrollers.controller('GeneralAdminTabController',
	['$routeParams', '$scope', '$rootScope', '$location', 'DashboardService',
		function ($routeParams, $scope, $rootScope, $location, DashboardService) {
			0;

			initAllData = function () {
				$scope.totaalKlanten = 0;
				$scope.totaalActieKlanten = 0;
				$scope.totaalProspectKlanten = 0;
				$scope.totaalNieuweKlanten = 0;
				$scope.totaalVerwijderdeKlanten = 0;

				$scope.totaalMeldingen = 0;
				$scope.totaalActieMeldingen = 0;
				$scope.totaalNieuweMeldingen = 0;
				$scope.totaalVerwijderdeMeldingen = 0;

				$scope.totaalMonitoring = 0;
				$scope.totaalNieuweMonitoring = 0;
				$scope.totaalVerwijderdeMonitoring = 0;

				$scope.totaalRapporten = 0;
				$scope.totaalNieuweRapporten = 0;

				$scope.dataPointsNieuweKlanten = [];
				$scope.dataPointsNieuweMeldingen = [];
				$scope.dataPointsNieuweMonitoring = [];
				$scope.dataPointsNieuweRapporten = [];

				//$scope.dataPoints = [2,3,4,6,2,1,5,3,2,4];
			};

			correctValues = function (array, descriptionToCorrect) {
				var total = 0;
				var item = null;
				for (var i in array) {
					total += array[i].value;
					if (array[i].description == descriptionToCorrect)
						item = array[i];
				}
				if (total > 100.00)
					item.value = item.value - (total - 100.00);

			};

			getProgressKlantStacked = function () {
				$scope.stackedKlanten = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalActieKlanten / $scope.totaalKlanten) * 100),
					description: 'In behandeling!',
					type: 'danger'
				});

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalProspectKlanten / $scope.totaalKlanten) * 100),
					description: 'Prospect',
					type: 'warning'
				});

				$scope.stackedKlanten.push({
					value: Math.round((($scope.totaalNieuweKlanten - $scope.totaalActieKlanten) / $scope.totaalKlanten) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedKlanten.push({
					value: Math.round((($scope.totaalKlanten - $scope.totaalActieKlanten - $scope.totaalProspectKlanten - ($scope.totaalNieuweKlanten - $scope.totaalActieKlanten) - $scope.totaalVerwijderdeKlanten) / $scope.totaalKlanten) * 100),
					description: 'Overig actief',
					type: 'success'
				});

				$scope.stackedKlanten.push({
					value: Math.round(($scope.totaalVerwijderdeKlanten / $scope.totaalKlanten) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedKlanten, 'Overig actief');

			};

			getProgressMeldingStacked = function () {
				$scope.stackedMeldingen = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedMeldingen.push({
					value: Math.round(($scope.totaalActieMeldingen / $scope.totaalMeldingen) * 100),
					description: 'In behandeling of wachten!',
					type: 'danger'
				});

				$scope.stackedMeldingen.push({
					value: Math.round((($scope.totaalNieuweMeldingen - $scope.totaalActieMeldingen) / $scope.totaalMeldingen) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedMeldingen.push({
					value: Math.round((($scope.totaalMeldingen - $scope.totaalActieMeldingen - ($scope.totaalNieuweMeldingen - $scope.totaalActieMeldingen) - $scope.totaalVerwijderdeMeldingen) / $scope.totaalMeldingen) * 100),
					description: 'Overig actief',
					type: 'success'
				});

				$scope.stackedMeldingen.push({
					value: Math.round(($scope.totaalVerwijderdeMeldingen / $scope.totaalMeldingen) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedMeldingen, 'Overig actief');

			};

			getProgressMonitoringStacked = function () {
				$scope.stackedMonitoring = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedMonitoring.push({
					value: Math.round(($scope.totaalNieuweMonitoring / $scope.totaalMonitoring) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedMonitoring.push({
					value: Math.round((($scope.totaalMonitoring - $scope.totaalNieuweMonitoring - $scope.totaalVerwijderdeMonitoring) / $scope.totaalMonitoring) * 100),
					description: 'Actief',
					type: 'success'
				});

				$scope.stackedMonitoring.push({
					value: Math.round(($scope.totaalVerwijderdeMonitoring / $scope.totaalMonitoring) * 100),
					description: 'Verwijderd',
					type: 'info'
				});

				correctValues($scope.stackedMonitoring, 'Actief');

			};

			getProgressRapportStacked = function () {
				$scope.stackedRapporten = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedRapporten.push({
					value: Math.round(($scope.totaalNieuweRapporten / $scope.totaalRapporten) * 100),
					description: 'Nieuw',
					type: 'success'
				});

				$scope.stackedRapporten.push({
					value: Math.round((($scope.totaalRapporten - $scope.totaalNieuweRapporten) / $scope.totaalRapporten) * 100),
					description: 'Overig',
					type: 'info'
				});

				correctValues($scope.stackedRapporten, 'Overig');

			};

			getProgressGebruikersStacked = function () {
				$scope.stackedGebruikers = [];
				var types = ['success', 'info', 'warning', 'danger'];

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalNonActivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Nog niet geactiveerd!',
					type: 'danger'
				});

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalActivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Actief',
					type: 'success'
				});

				$scope.stackedGebruikers.push({
					value: Math.round((($scope.totaalGebruikers - $scope.totaalNonActivatedGebruikers - $scope.totaalActivatedGebruikers - $scope.totaalDeactivatedGebruikers) / $scope.totaalGebruikers) * 100),
					description: 'Overig',
					type: 'success'
				});

				$scope.stackedGebruikers.push({
					value: Math.round(($scope.totaalDeactivatedGebruikers / $scope.totaalGebruikers) * 100),
					description: 'Niet actief',
					type: 'info'
				});

				correctValues($scope.stackedGebruikers, 'Overig');
			};

			fetchData = function () {
				initAllData();

				DashboardService.generaladmin(function (data) {
					0;
					if (typeof data.errorCode == 'undefined') {
						if (data) {
							//if (Object.prototype.toString.call(data.klantenPerStatus) === '[object Array]')
							$scope.klantenPerStatus = data.klantenPerStatus;
							//else
							//	$scope.klantenPerStatus = [data.klantenPerStatus];
							//if (Object.prototype.toString.call(data.activeKlantenLast24h) === '[object Array]')
							$scope.klantenLast24h = data.activeKlantenLast24h;
							//else
							//	$scope.klantenLast24h = [data.activeKlantenLast24h];

							//if (Object.prototype.toString.call(data.meldingenPerStatus) === '[object Array]')
							$scope.meldingenPerStatus = data.meldingenPerStatus;
							//else
							$scope.meldingenPerStatus = data.meldingenPerStatus;
							//if (Object.prototype.toString.call(data.activeMeldingenLast24h) === '[object Array]')
							$scope.meldingenLast24h = data.activeMeldingenLast24h;
							//else
							//	$scope.meldingenLast24h = [data.activeMeldingenLast24h];

							//if (Object.prototype.toString.call(data.monitoringPerStatus) === '[object Array]')
							$scope.monitoringPerStatus = data.monitoringPerStatus;
							//else
							//	$scope.monitoringPerStatus = [data.monitoringPerStatus];
							//if (Object.prototype.toString.call(data.activeMonitoringLast24h) === '[object Array]')
							$scope.monitoringLast24h = data.activeMonitoringLast24h;
							//else
							//	$scope.monitoringLast24h = [data.activeMonitoringLast24h];

							if (data.totaalAantalRapporten)
								$scope.totaalRapporten = parseInt(data.totaalAantalRapporten);
							//if (Object.prototype.toString.call(data.rapportenLast24h) === '[object Array]')
							$scope.rapportenLast24h = data.rapportenLast24h;
							//else
							//	$scope.rapportenLast24h = [data.rapportenLast24h];

						}

						if ($scope.klantenPerStatus) {
							for (var i in $scope.klantenPerStatus) {
								if ($scope.klantenPerStatus[i].statusCode == 'NOK') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalActieKlanten = $scope.totaalActieKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'REG') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalActieKlanten = $scope.totaalActieKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'PRO') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalProspectKlanten = $scope.totaalProspectKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalNieuweKlanten = $scope.totaalNieuweKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else if ($scope.klantenPerStatus[i].statusCode == 'ACT') {
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeKlanten = $scope.totaalVerwijderdeKlanten + parseInt($scope.klantenPerStatus[i].aantal);
									$scope.totaalKlanten = $scope.totaalKlanten + parseInt($scope.klantenPerStatus[i].aantal);
								}

							}
						}

						if ($scope.klantenLast24h) {
							for (var i in $scope.klantenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.klantenLast24h[i].aantal) + parseInt($scope.klantenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweKlanten.push(aantal);
								}
							}
						}

						if ($scope.meldingenPerStatus) {
							for (var i in $scope.meldingenPerStatus) {
								if ($scope.meldingenPerStatus[i].statusCode == 'NOK') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'INI') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'INB') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalActieMeldingen = $scope.totaalActieMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalNieuweMeldingen = $scope.totaalNieuweMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else if ($scope.meldingenPerStatus[i].statusCode == 'ACT') {
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeMeldingen = $scope.totaalVerwijderdeMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
									$scope.totaalMeldingen = $scope.totaalMeldingen + parseInt($scope.meldingenPerStatus[i].aantal);
								}

							}
						}

						if ($scope.meldingenLast24h) {
							for (var i in $scope.meldingenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.meldingenLast24h[i].aantal) + parseInt($scope.meldingenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweMeldingen.push(aantal);
								}
							}
						}

						if (data.totaalAantalNieuweMonitoring)
							$scope.totaalNieuweMonitoring = parseInt(data.totaalAantalNieuweMonitoring);
						if ($scope.monitoringPerStatus) {
							for (var i in $scope.monitoringPerStatus) {
								if ($scope.monitoringPerStatus[i].statusCode == 'ACT') {
									$scope.totaalMonitoring = $scope.totaalMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
								}
								else {
									$scope.totaalVerwijderdeMonitoring = $scope.totaalVerwijderdeMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
									$scope.totaalMonitoring = $scope.totaalMonitoring + parseInt($scope.monitoringPerStatus[i].aantal);
								}

							}
						}

						if ($scope.monitoringLast24h) {
							for (var i in $scope.monitoringLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.monitoringLast24h[i].aantal) + parseInt($scope.monitoringLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweMonitoring.push(aantal);
								}
							}
						}

						if (data.totaalAantalRapporten)
							$scope.totaalRapporten = parseInt(data.totaalAantalRapporten);
						if (data.totaalAantalNieuweRapporten)
							$scope.totaalNieuweRapporten = parseInt(data.totaalAantalNieuweRapporten);

						if ($scope.rapportenLast24h) {
							for (var i in $scope.rapportenLast24h) {
								if (i % 2 == 0) {
									var aantal = parseInt($scope.rapportenLast24h[i].aantal) + parseInt($scope.rapportenLast24h[parseInt(i) + 1].aantal);
									$scope.dataPointsNieuweRapporten.push(aantal);
								}
							}

						}

						if (data.nrOfGebruikersIngelogd)
							$scope.gebruikersIngelogd = data.nrOfGebruikersIngelogd;
						else
							$scope.gebruikersIngelogd = 0;
						if (data.totaalAantalGebruikers)
							$scope.totaalGebruikers = data.totaalAantalGebruikers;
						else
							$scope.totaalGebruikers = 0;
						if (data.totaalAantalActivatedGebruikers)
							$scope.totaalActivatedGebruikers = data.totaalAantalActivatedGebruikers;
						else
							$scope.totaalActivatedGebruikers = 0;
						if (data.totaalAantalDeactivatedGebruikers)
							$scope.totaalDeactivatedGebruikers = data.totaalAantalDeactivatedGebruikers;
						else
							$scope.totaalDeactivatedGebruikers = 0;
						if (data.totaalAantalNonActivatedGebruikers)
							$scope.totaalNonActivatedGebruikers = data.totaalAantalNonActivatedGebruikers;
						else
							$scope.totaalNonActivatedGebruikers = 0;

						getProgressKlantStacked();
						getProgressMeldingStacked();
						getProgressMonitoringStacked();
						getProgressRapportStacked();
						getProgressGebruikersStacked();
					} else {
						$scope.error = data.errorCode + " " + data.errorMsg;
					}

				}, function () {
					0;
				});
			};

//    setInterval(function(){
//        $scope.$apply(function() {
//            $scope.valuespark = getRandomInt(1, 10);
//        });
//    }, 1000);

			fetchData();

		}]);appcontrollers.controller('IndexController', ['$scope', function ($scope) {
	
	// $scope.newsEntries = NewsService.query();
	
	//$scope.deleteEntry = function(newsEntry) {
	//	newsEntry.$remove(function() {
	//		$scope.newsEntries = NewsService.query();
	//	});
	//};
}]);appcontrollers.controller('LoginController', ['$window', '$routeParams', '$scope', '$rootScope', '$location', '$q', 'maxFieldLengths', '$anchorScroll', '$cookies', 'LoginService', 'UserService', 'clientip', 'ExactOnlineService', 'WebSocketService', function ($window, $routeParams, $scope, $rootScope, $location, $q, maxFieldLengths, $anchorScroll, $cookies, LoginService, UserService, clientip, ExactOnlineService, WebSocketService) {

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
				0;
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
//						0;						
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
                        0;
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
			0;
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
//				0;
//				var userData = UserService.get();
//				0;
//				var resultsPromise = $q.all({
//					userdata: userData.$promise
//				}).then(function(data) {
//					if ($scope.rememberMe) {
//						$cookieStore.put('authToken', authToken);
//					}
//					
//					0;
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
//					0;
			
				
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
}]);appcontrollers.controller('MessagesController', ['$scope', '$routeParams', '$location', function ($scope, $routeParams, $location) { //, NewsService
	0;

}]);appcontrollers.controller('MonitoringDetailsController',
	['$window', '$modal', '$scope', '$rootScope', '$routeParams', '$location', 'maxFieldLengths',
		'$filter', '$q', 'monitoringDetailData', 'GebruikersServicePath', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, $routeParams, $location, maxFieldLengths,
				  $filter, $q, monitoringDetailData, GebruikersServicePath, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			var allCompanies = [];
			var companies = [];
			var companyidentifier = $routeParams.eigenBedrijfId;
			var monitoringId = $routeParams.monitoringId;
			var searchurl = $routeParams.searchurl;

			$scope.monitorData = monitoringDetailData.monitoring;
			$scope.hasSearchResults = false;

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			var RemoveNotificationController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.reason = reason;
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function () {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result);
				};
			}];

			$scope.documentRequested = {
				action: 'monitorDetails',
				bedrijfId: companyidentifier,
				monitorId: monitoringId
			};

			$scope.back = function () {
				url = gotoDurl(searchurl);
				if (url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			$scope.bedragVermelding = function (bedrag) {
				if (bedrag == undefined || bedrag == null)
					return 'afgeschermd';
				else
					return $filter('currency')(bedrag);
			};

			$scope.bedrijfdoorgegevens = function (bedrijfId, bedrijfsnaam, adres, sbiOmschrijving) {
				if (bedrijfId != undefined && bedrijfId != null) {
					var bedrijfsgegevens = '';
					if (bedrijfsnaam != undefined && bedrijfsnaam != null)
						bedrijfsgegevens = bedrijfsgegevens + bedrijfsnaam;
					if (adres != undefined && adres != null)
						bedrijfsgegevens = bedrijfsgegevens + ', ' + adres;

					return bedrijfsgegevens;
				} else
					return sbiOmschrijving;
			};

			$scope.fetchResult = function (success) {
				if (monitoringDetailData.meldingen != undefined && monitoringDetailData.meldingen != null) {
					$scope.allCompanies = monitoringDetailData.meldingen;
					$scope.totalItems = $scope.allCompanies.length;
					success();
				} else {
					$scope.allCompanies = [];
					$scope.totalItems = 0;
				}
			};

			$scope.hasVermeldingen = function () {
				if ($scope.companies != undefined && $scope.companies != null) {
					return $scope.companies.length > 0;
				} else
					return false;
			};

			$scope.notificationChange = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$monitoringdetails$' + companyidentifier + '$' + monitoringId + escapeSearchUrl(searchurl) + '/' + meldingId);
			};

			$scope.notificationReadOnly = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$monitoringdetails$' + companyidentifier + '$' + monitoringId + escapeSearchUrl(searchurl) + '/' + meldingId + '/true');
			};

			$scope.notificationRemove = function (meldingId, bedrijfId) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				var modalInstance = $modal.open({
					templateUrl: 'removenotificationmon.html',
					controller: RemoveNotificationController,
					size: 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function (result) {
					if (result.remove)
						removeNotification(meldingId, bedrijfId, result.reason);
				}, function () {
					0;
				});
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			getCompaniesOnPage = function (currentPage, itemsOnPage, totalItems, allcompanieslist) {
				var begin = (currentPage - 1) * itemsOnPage;
				var end = Math.min(begin + itemsOnPage, totalItems);

				return allcompanieslist.slice(begin, end);
			};
			
			$scope.pageChanged = function () {
				if ($scope.companies) {
					0;
					$scope.searchcompany.pageNumber = $scope.currentPage;

					pageparturl = '$' + $scope.currentPage;
					searchurl = searchparturl + pageparturl;

					$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);
				}
			};

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.showRemoveNotification = function (userId, status) {
//	    	 if (userId == JSON.parse($window.sessionStorage.user).userId)
//	    		 return true;
//	    	 else if (status == 'ACT')
				return true;
			};

			$scope.getAfdeling = function(afdeling) {
				// afdeling name fill with default if unknown
				if(typeof afdeling == 'undefined' || afdeling == null || afdeling == '') {
					return 'Vermeldingen';
				} else {
					return afdeling;
				}
			};

			$scope.vermeldingenPagingOn = function () {
				if ($scope.hasVermeldingen()) {
					return $scope.totalItems - $scope.itemsPage > 1;
				} else
					return false;
			};

			performSearchCompanies = function () {
				$scope.hasSearchResults = true;

				$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);

			};

			removeNotification = function (meldingId, bedrijfId, reason) {
				delete $scope.notificationRemoved;
				delete $scope.error;

				CompanyService.removeNotificationCompany({
					meldingId: meldingId,
					bedrijfId: bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				}, function (result) {
					if (typeof result.errorCode != 'undefined')
						$scope.error = result.errorMsg;
					else {
						$scope.notificationRemoved = true;
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.fetchResult(performSearchCompanies);

			if ($scope.monitorData != undefined && $scope.monitorData.gebruikerAangemaaktId != undefined) {

				$scope.acties = [];

				//Add actions concerning the addition/edits/removals of the monitoring
				var gebruikerLaatsteMutatie = null;
				var gebruikerVerwijderd = null;
				var datumLaatsteMutatie = null;
				var datumVerwijderd = null;
				if ($scope.monitorData.gebruikerVerwijderdId != undefined && $scope.monitorData.gebruikerVerwijderdId != null)
					gebruikerVerwijderd = $scope.monitorData.gebruikerVerwijderdId;
				if ($scope.monitorData.gewijzigd != undefined && $scope.monitorData.gewijzigd != null)
					datumLaatsteMutatie = $scope.monitorData.gewijzigd;
				if ($scope.monitorData.gebruikerLaatsteMutatieId != undefined && $scope.monitorData.gebruikerLaatsteMutatieId != null)
					gebruikerLaatsteMutatie = $scope.monitorData.gebruikerLaatsteMutatieId;
				if ($scope.monitorData.verwijderd != undefined && $scope.monitorData.verwijderd != null)
					datumVerwijderd = $scope.verwijderd;
				$scope.gebruikerIds = [
					$scope.monitorData.gebruikerAangemaaktId.toString(),
					gebruikerLaatsteMutatie != null ? gebruikerLaatsteMutatie.toString() : 'null',
					gebruikerVerwijderd != null ? gebruikerVerwijderd.toString() : 'null'];

				var gebruikerIdsJson = {gebruikerIds: $scope.gebruikerIds};

				GebruikersServicePath.gebruikerdata(gebruikerIdsJson, function (result) {
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.gebruikers = result;

						$scope.acties.push({
							actie: 'Monitoring gestart',
							datum: monitoringDetailData.toegevoegd,
							gebruiker: $scope.gebruikers[0].fullName,
							functie: $scope.gebruikers[0].functie,
							afdeling: $scope.gebruikers[0].afdeling,
							email: $scope.gebruikers[0].emailAdres
						});

						//i = 1;
						//
						//if (datumVerwijderd != null) {
						//	if (gebruikerVerwijderd != null) {
						//		$scope.acties.push({
						//			actie: 'Verwijderd op',
						//			datum: monitoringDetailData.verwijderd,
						//			gebruiker: $scope.gebruikers[i].fullName,
						//			functie: $scope.gebruikers[i].functie,
						//			afdeling: $scope.gebruikers[i].afdeling,
						//			email: $scope.gebruikers[i].emailAdres
						//		});
						//		i++;
						//	} else {
						//		$scope.acties.push({
						//			actie: 'Verwijderd op',
						//			datum: monitoringDetailData.verwijderd,
						//			gebruiker: '-',
						//			functie: '-',
						//			afdeling: '-',
						//			email: '-'
						//		});
						//	}
						//}
						//
						//if (datumLaatsteMutatie != null) {
						//	if (gebruikerLaatsteMutatie != null) {
						//		$scope.acties.push({
						//			actie: 'Laatst bijgewerkt op',
						//			datum: monitoringDetailData.gewijzigd,
						//			gebruiker: $scope.gebruikers[i].fullName,
						//			functie: $scope.gebruikers[i].functie,
						//			afdeling: $scope.gebruikers[i].afdeling,
						//			email: $scope.gebruikers[i].emailAdres
						//		});
						//		i++;
						//	} else {
						//		$scope.acties.push({
						//			actie: 'Laatst bijgewerkt op',
						//			datum: monitoringDetailData.gewijzigd,
						//			gebruiker: '-',
						//			functie: '-',
						//			afdeling: '-',
						//			email: '-'
						//		});
						//	}
						//}
					}

					//Add actions of notifications about the company in monitoring
					if ($scope.companies != undefined) {
						GebruikersServicePath.sbdrgebruiker(function(result){
							if (typeof result.errorCode != 'undefined') {
								$scope.error = result.errorCode + " " + result.errorMsg;
							} else {
								$scope.companies.forEach(function (company) {
									if (company.toegevoegd != undefined) {
										$scope.acties.push({
											actie: 'Betalingsachterstand ' + company.referentieIntern + ' opgenomen',
											datum: company.datumGeaccordeerd,
											gebruiker: result.fullName.trim(),
											functie: result.functie,
											afdeling: result.afdeling,
											email: result.emailAdres
										})
									}									

									if (company.datumVerwijderd != undefined) {
										$scope.acties.push({
											actie: 'Betalingsachterstand ' + company.referentieIntern + ' verwijderd',
											datum: company.datumVerwijderd,
											gebruiker: result.fullName.trim(),
											functie: result.functie,
											afdeling: result.afdeling,
											email: result.emailAdres
										})
									}
								});
								
								//Sort the actions list on date, newest first
								$scope.acties.sort(function (x, y) {
									var dateTimeRegex = /(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2})/;
									var xDateArray = dateTimeRegex.exec(x.datum);
									var yDateArray = dateTimeRegex.exec(y.datum);
									var xDate = new Date((+xDateArray[3]), (+xDateArray[2]) - 1, (+xDateArray[1]), (+xDateArray[4]), (+xDateArray[5]));
									var yDate = new Date((+yDateArray[3]), (+yDateArray[2]) - 1, (+yDateArray[1]), (+yDateArray[4]), (+yDateArray[5]));
									if (xDate.getTime() > yDate.getTime())
										return 1;
									else if (xDate.getTime() === yDate.getTime())
										return 0;
									else
										return -1;
								});
							
							}
						});
					}

				});
			}
		}]);appcontrollers.controller('MonitoringQuestionController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', '$modal', 'CompanyService',
		function($window, $routeParams, $scope, $rootScope,
				 $location, $modal, CompanyService) {

			var bedrijfIdentifier;

			if($scope.init == undefined) {
				$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});

				$scope.init = true;
			}

			if(!$routeParams.bedrijfId || $routeParams.bedrijfId == null || $routeParams.bedrijfId == undefined) {
				bedrijfIdentifier = null;
			} else {
				bedrijfIdentifier = $routeParams.bedrijfId;
			}

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}

			$scope.kvkNummer = $routeParams.kvkNummer;
			$scope.kvkNummerPrefix = $routeParams.kvkNummer;
			$scope.hoofd = $routeParams.hoofd;
			$scope.bedrijfNaam = decodeURIComponent($routeParams.bedrijfNaam);
			$scope.bekendBijSbdr = "true"; //$routeParams.bekendBijSbdr;
			$scope.monitoringBijBedrijf = $routeParams.monitoringBijBedrijf;
			$scope.adres = decodeURIComponent($routeParams.adres);

			if(typeof $scope.kvkNummerPrefix !== 'undefined' && $scope.kvkNummerPrefix != null && $scope.kvkNummerPrefix.length == 12) {
				$scope.kvkNummerPrefix = $scope.kvkNummerPrefix.substring(0, 8);
			}

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function(){
					$modalInstance.close();
				}
			}];

			var reportPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doReport = false;

				CompanyService.getTariefOfProduct({ProductCode: 'RAP'}, function(result) {
					if(result.errorCode == undefined) {
						$scope.reportCost = result.tarief;
					}
				});

				$scope.createReport = function() {
					$scope.doReport = true;
					$scope.closeReportPopup();
				};

				$scope.closeReportPopup = function() {
					var result = {
						doReport : $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			$scope.returnToSearch = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			callMonitoring = function(request) {
				CompanyService.monitoringCompany(request, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					} else {
						$scope.monitorCreated = true;
					}
				});
			};

			callReport = function(bedrijfIds) {
				$location.path('/report/' + bedrijfIds.bedrijfIdGerapporteerd + '/' + bedrijfIds.bedrijfId + '/' + '0' + '/' + searchurl);
				$location.url($location.path());
			};

			$scope.gotoReport = function() {
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
						if(false){ // !$scope.userCanPay turned off
							var reportNoPaymentModal = $modal.open({
								templateUrl: 'reportNoPayment.html',
								controller: reportNoPaymentController,
								size:'lg'
							});
						} else {
							delete $scope.monitoringSuccessful;

							//if (typeof bedrijfIdentifier == 'undefined' || bedrijfIdentifier == null) {
							CompanyService.getOrCreateCompanyData({
								kvknumber: $scope.kvkNummer,
								hoofd    : $scope.hoofd
							}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								}
								else {
									if(bedrijfIdentifier == "undefined" || bedrijfIdentifier == null) {
										//monitoringdata
										bedrijfIds = {
											bedrijfId             : result.bedrijfId,
											bedrijfIdGerapporteerd: JSON.parse($window.sessionStorage.user).bedrijfId
										};
										//callMonitoring(monitoringdata);
										//$scope.monitoringSuccessful = true;
										callReport(bedrijfIds);
									} else { // bedrijf already known
										bedrijfIds = {
											bedrijfId             : bedrijfIdentifier,
											bedrijfIdGerapporteerd: JSON.parse($window.sessionStorage.user).bedrijfId
										};
										callReport(bedrijfIds);
									}
								}
							});
						}
				//	}
				//});
			};
		}]);appcontrollers.controller('MyAccountController', [
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
				0;
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
				0;
			});
		};

		$scope.deleteUserDel = function(userId) {
			resetPage();

			$scope.userIdSelected = userId;

			// do delete
			GebruikerService.delete({id: userId}, function(deleteGebruikerResult) {
				if(deleteGebruikerResult.errorCode === undefined) {
					0;
					// after delete
					$scope.userIdSelected = null;
					delete $scope.userDetails;
					$scope.userRemovedOk = true;

					$scope.filterResult();
				} else {
					0;
					$scope.error = deleteGebruikerResult.errorMsg;
					var old = $location.hash();
					$location.hash('alert');
					$anchorScroll();
					$location.hash(old);
					//$location.hash('alert');
					//$anchorScroll();
				}
			}, function() {
				0;
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
				0;

				$scope.users = data;

				$scope.totalItems = paging_totalItems(headers('Content-Range'));
				success();
			}, function(error) {
				0;
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
			0;
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
							0;
						});

					} // else {
						// no modal message
						// no logout, is other user... 
						// so do nothing then
					// }
					
					$scope.emailadrescontrole = '';
					$scope.obj.formgebruikergegevens.$setPristine();
					
					0;
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

					0;
					$scope.error = updateGebruikerResult.errorMsg;
					//var old = $location.hash();
					//$location.hash('alert');
					//$anchorScroll();
					//$location.hash(old);					
				}
			}, function() {
				0;
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
							0;
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
							0;
							$scope.error = createGebruikerResult.errorMsg;
							var old = $location.hash();
							$location.hash('alert');
							$anchorScroll();
							$location.hash(old);
							//$location.hash('alert');
							//$anchorScroll();
						}
					}, function() {
						0;
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
				0;
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

	}]);appcontrollers.controller('MyUserAccountController',
	['$window', '$scope', '$rootScope', '$location', '$cookies', '$anchorScroll', '$routeParams', 'maxFieldLengths', 'AccountService', 'UserService',
		function($window, $scope, $rootScope, $location, $cookies, $anchorScroll, $routeParams, maxFieldLengths, AccountService, UserService) {
			0;

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

		}]);appcontrollers.controller('NotificationsAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', 'maxFieldLengths',
		'$filter', '$timeout', 'ngTableParams', 'DashboardNumberTags',
		'DashboardService', 'CompanyService', 'InternalProcessService',
		function ($window, $modal, $scope, $rootScope, $location, maxFieldLengths,
				  $filter, $timeout, ngTableParams, DashboardNumberTags,
				  DashboardService, CompanyService, InternalProcessService) {
			$scope.title = "Meldingen Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;
			$scope.noNewNotificationLetterBatch = false;
			$scope.sizeOfLetterBatch = 0;

			var createBatchPopupController = ['$scope', '$modalInstance', 'sizeOfBatch', function ($scope, $modalInstance, sizeOfBatch) {
				$scope.continue = false;
				$scope.sizeOfBatch = sizeOfBatch;

				$scope.acceptPopup = function () {
					$scope.continue = true;
					$scope.discardPopup();
				};

				$scope.discardPopup = function () {
					var result = {continue: $scope.continue};
					$modalInstance.close(result);
				};
			}];

			var createMeldingBriefPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.continue = false;
				$scope.doAllNotifications = false;

				$scope.continueAllNotifications = function(){
					$scope.continue = true;
					$scope.doAllNotifications = true;
					$scope.discard();
				};

				$scope.continueThisNotification = function(){
					$scope.continue = true;
					$scope.discard();
				};

				$scope.discard = function(){
					var result = {
						continue: $scope.continue,
						doAllNotifications: $scope.doAllNotifications
					};

					$modalInstance.close(result);
				};
			}];

			var notifications = [];

			var RemoveNotificationAdminController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.remove = true;
					$scope.reason = reason;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function () {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];
			
			var HoldNotificationCompanyController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.onhold = false;				

				$scope.Continue = function (reason) {
					$scope.onhold = true;
					closeRemoveNotificationModal();
				};
				
				$scope.Discard = function (reason) {
					closeRemoveNotificationModal();
				};				

				closeRemoveNotificationModal = function () {
					var result = {
						onhold: $scope.onhold
					};

					$modalInstance.close(result); // $scope.onhold
				};
			}];			

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			$scope.createDocumentRequested = function (meldingId) {
				return {action: 'letter_notification', bedrijfId: 0, meldingId: meldingId, referentie: ''};
			};

			$scope.createNewNotificationLetter = function (meldingId) {
				var modalInstance = $modal.open({
					templateUrl:'createMeldingBrief.html',
					controller: createMeldingBriefPopupController,
					size:'lg'
				});

				modalInstance.result.then(function(result){
					if(result.continue){
						cleanUp();
						CompanyService.createNewNotificationLetter({meldingId: meldingId, allNotificationsInLetter: result.doAllNotifications}, function (result) {
							if (typeof result.errorCode != 'undefined') {
								$scope.error = result.errorCode + ' ' + result.errorMsg;
								$scope.letterCreatedOk = false;
							} else {
								$scope.letterCreatedOk = true;

								$scope.filterResult();
							}
						});
					}
				});
			};

			$scope.createInitialNotificationLetter = function(meldingId){
				cleanUp();
				CompanyService.createInitialNotificationLetter({meldingId:meldingId}, function(result){
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
						$scope.letterCreatedOk = false;
					} else {
						$scope.letterCreatedOk = true;

						$scope.filterResult();
					}
				});
			};

			$scope.detailsCompany = function (bedrijfId) {
				$location.path('/editcompany/' + bedrijfId + '/' + '$dashboard$notificationsadmintab');
				$location.url($location.path());
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				cleanUp();

				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.notificationsadmin($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.notifications = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.notifications = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);

					success();
				}, function () {
					0;
					$scope.notifications = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);
				});

			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.hasAdminRole = function () {
				if (typeof $window.sessionStorage.user !== 'undefined')
					return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd') || hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr');
				else
					return false;
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfNotificationsAdmin() > 0;
			};
			
			doHoldNotificationCompany = function(notificationId, companyId) {
				var request = {
						meldingId: notificationId,
						bedrijfId: companyId,
						gebruikerId: JSON.parse($window.sessionStorage.user).userId
					};

					CompanyService.holdNotificationCompany(request, function (response, error) {
						if (typeof response.errorCode != 'undefined') {
							$scope.error = response.errorCode + ' ' + response.errorMsg;
						}
						else {
							$scope.notificationOnHoldSavedOk = true;
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
						}
					}, function (error) {
						0;
					});				
			}

			$scope.holdNotificationCompany = function (notificationId, companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'holdnotificationcompany.html',
					controller: HoldNotificationCompanyController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.onhold) {
						doHoldNotificationCompany(notificationId, companyId);
					}
				}, function () {
					0;
				});
												
			};

			$scope.isSbdrHoofd = function () {
				if (typeof $window.sessionStorage.user !== 'undefined')
					return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd');
				else
					return false;
			};
			
			$scope.getDagenSinds = function(d) {
				if (typeof d !== 'undefined' && d != null && d.length >= 10) {
					date = new Date(d.substring(6, 10), d.substring(3, 5) - 1, d.substring(0, 2));
					diffc = new Date() - date;
					//getTime() function used to convert a date into milliseconds. This is needed in order to perform calculations.
					 
					days = Math.round(Math.abs(diffc/(1000*60*60*24)));
					//this is the actual equation that calculates the number of days.
					 
					return days;
				} else
					return 'onbekend';
			}
			
			$scope.setShowActive = function () {				
				var oldval = $scope.filterCriteria.showActive;

				$scope.filterCriteria.showActive = $scope.selectieShowActive;
				$scope.filterCriteria.filterValue = "";

				if (oldval != $scope.filterCriteria.showActive) {
					$scope.currentPage = 1;
					$scope.pageChanged();
				}
			};			

			$scope.notificationChange = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				0;

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsadmintab/' + meldingId);
			};

			$scope.notificationReadOnly = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				0;

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsadmintab/' + meldingId + '/true');
			};

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.printNotificationLetter = function (meldingId, arrayIndex) {
				if (!$scope.notifications[arrayIndex].briefGedownload) {
					InternalProcessService.printNotificationLetter({meldingId: meldingId}, function (result) {
						if (result.errorCode != null) {
							$scope.error = result.errorCode + ' ' + result.errorMsg;
						} else {
							$scope.notifications[arrayIndex].briefGedownload = true;

							$scope.filterResult();
						}
					});
				}
			};

			$scope.removeHoldNotificationCompany = function (notificationId, companyId) {
				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				};

				CompanyService.removeHoldNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.notificationOnHoldSavedOk = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};

			$scope.removeNotification = function (notificationId, companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller: RemoveNotificationAdminController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveNotification(notificationId, companyId, result.reason);
					}
				}, function () {
					0;
				});
			};

			$scope.requestreport = function (bedrijfId) {
				$location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/$dashboard$notificationsadmintab');
			};

			doRemoveNotification = function (notificationId, companyId, reason) {
				cleanUp();

				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				};

				CompanyService.removeNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorMsg;
					}
					else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			cleanUp = function () {
				delete $scope.error;
				delete $scope.notificationRemoved;
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			if ($scope.init == undefined) {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "", // text to filter on
					showActive: false
				};

				$scope.selectieShowActive = false;
				
				$scope.init = true;
			}

			// fetch initial data for 1st time
			$scope.filterResult();
		}]);appcontrollers.controller('NotificationsOfProspectAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', 'maxFieldLengths',
		'$filter', '$timeout', 'ngTableParams', 'DashboardNumberTags',
		'DashboardService', 'CompanyService', 'InternalProcessService',
		function ($window, $modal, $scope, $rootScope, $location, maxFieldLengths,
				  $filter, $timeout, ngTableParams, DashboardNumberTags,
				  DashboardService, CompanyService, InternalProcessService) {
			0;
			$scope.title = "Meldingen of Prospect Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
			};

			$scope.createDocumentRequested = function (meldingId) {
				return {action: 'letter_notification', bedrijfId: 0, meldingId: meldingId, referentie: ''};
			};

			0;

			$scope.detailsCompany = function (bedrijfId) {
				$location.path('/editcompany/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab');
				$location.url($location.path());
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			if (typeof $scope.filterCriteria == 'undefined') {
				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};
			}
			0;

			$scope.nrOfItems = function () {
				return 0;
			};

			var notifications = [];

			cleanUp = function () {
				delete $scope.error;
				delete $scope.notificationRemoved;
			};

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfNotificationsAdmin() > 0;
			};

			$scope.createNewNotificationLetter = function (meldingId) {
				cleanUp();
				CompanyService.createNewNotificationLetter({meldingId: meldingId}, function (result) {
					if (typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
						$scope.letterCreatedOk = false;
					}
					else {
						$scope.letterCreatedOk = true;
					}
				});
			};
			
			$scope.getDagenSinds = function(d) {
				if (typeof d !== 'undefined' && d != null && d.length >= 10) {
					date = new Date(d.substring(6, 10), d.substring(3, 5) - 1, d.substring(0, 2));
					diffc = new Date() - date;
					//getTime() function used to convert a date into milliseconds. This is needed in order to perform calculations.
					 
					days = Math.round(Math.abs(diffc/(1000*60*60*24)));
					//this is the actual equation that calculates the number of days.
					 
					return days;
				} else
					return 'onbekend';
			}			

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};
			0;

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				cleanUp();

				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.notificationsofprospectadmin($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.notifications = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.notifications = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);

					success();
				}, function () {
					0;
					$scope.notifications = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfNotificationsAdmin($scope.totalItems);
				});

			};
			0;

			$scope.isSbdrHoofd = function () {
				return hasRole(JSON.parse($window.sessionStorage.user).roles, 'admin_sbdr_hoofd');
			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.requestreport = function (bedrijfId) {
				$location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/$dashboard$notificationsofprospectadmintab');
			};

			doRemoveNotification = function (notificationId, companyId, reason) {
				cleanUp();

				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				};

				CompanyService.removeNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorMsg;
					}
					else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};

			$scope.removeNotification = function (notificationId, companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller: RemoveNotificationAdminController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveNotification(notificationId, companyId, result.reason);
					}
				}, function () {
					0;
				});
			};

			$scope.holdNotificationCompany = function (notificationId, companyId) {
				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				};

				CompanyService.holdNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.notificationOnHoldSavedOk = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};

			$scope.removeHoldNotificationCompany = function (notificationId, companyId) {
				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				};

				CompanyService.removeHoldNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.notificationOnHoldSavedOk = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};
			
			// Extended with bedrijfDoorId, was 'own company' = admin
			$scope.notificationChange = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				0;

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab/' + meldingId);
			};

			// Extended with bedrijfDoorId, was 'own company' = admin
			$scope.notificationReadOnly = function (meldingId, bedrijfDoorId, bedrijfId) {
				cleanUp();
				0;

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + '$dashboard$notificationsofprospectadmintab/' + meldingId + '/true');
			};			

			$scope.printNotificationLetter = function (meldingId, arrayIndex) {
				if (!$scope.notifications[arrayIndex].briefGedownload) {
					InternalProcessService.printNotificationLetter({meldingId: meldingId}, function (result) {
						if (result.errorCode != null) {
							$scope.error = result.errorCode + ' ' + result.errorMsg;
						} else {
							$scope.notifications[arrayIndex].briefGedownload = true;
						}
					});
				}
			};

			// fetch initial data for 1st time
			$scope.filterResult();

			var RemoveNotificationAdminController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.remove = true;
					$scope.reason = reason;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function () {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];
		}]);appcontrollers.controller('NotificationsSendController',
	['$window', '$scope', '$rootScope', '$routeParams', 'maxFieldLengths',
		'$location', '$filter', 'CompanyService', 'NotificationsToSend',
		function($window, $scope, $rootScope, $routeParams, maxFieldLengths,
				 $location, $filter, CompanyService, NotificationsToSend) {
			0;

			$scope.maxFieldLengths = maxFieldLengths;
			
			var searchurl = $routeParams.searchurl;
			var companyidentifier = $routeParams.bedrijfId;
			var owncompanyidentifier = $routeParams.eigenBedrijfId;

			//$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;
			$scope.PHONE_REGEXP = /^0[1-9](?:(?:-)?[0-9]){8}$|^0[1-9][0-9](?:(?:-)?[0-9]){7}$|^0[1-9](?:[0-9]){2}(?:(?:-)?[0-9]){6}$|^((?:0900|0800|0906|0909)(?:(?:-)?[0-9]){4,7}$)/;

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || wasFormSubmitted;

				//if(targetForm[targetField].$dirty) {
				//	delete $scope.error;
				//}

				return result_invalid && result_dirty;
			};

			// Controls the 'send' button's active state based on the validity of the form
			$scope.buttonDisabled = function (targetForm, targetField) {
				// if form is invalid set submit to false, so that send can continue processing on resend
				if (targetForm.$invalid && $scope.formSubmitted) {
					wasFormSubmitted = $scope.formSubmitted;
					$scope.formSubmitted = false;
				}
				
				return targetForm.$invalid && wasFormSubmitted;
			};
			
			$scope.send = function() {
				// if already in submit, return
				if ($scope.formSubmitted == true)
					return;
				
				$scope.formSubmitted = true;

				if(!($scope.formnotificationssend.$invalid)) {					

					delete $scope.error;
					delete $scope.meldingenSavedOk;
					delete $scope.vermeldingResultaat;
					0;

					if (typeof NotificationsToSend.getNotifications() !== 'undefined') {
						for(var m in NotificationsToSend.getNotifications()) {
							NotificationsToSend.getNotifications()[m].meldingId = !isNaN(parseFloat(NotificationsToSend.getNotifications()[m].meldingId)) && isFinite(NotificationsToSend.getNotifications()[m].meldingId) ? null : NotificationsToSend.getNotifications()[m].meldingId;
							if (typeof NotificationsToSend.getTelefoonNummerDebiteur() !== 'undefined' && NotificationsToSend.getTelefoonNummerDebiteur() != null)
								NotificationsToSend.getNotifications()[m].telefoonNummerDebiteur = NotificationsToSend.getTelefoonNummerDebiteur();
							if (typeof NotificationsToSend.getEmailAdresDebiteur() !== 'undefined' && NotificationsToSend.getEmailAdresDebiteur() != null)
							NotificationsToSend.getNotifications()[m].emailAdresDebiteur = NotificationsToSend.getEmailAdresDebiteur();
						}
					}
					
					var batch = {
						voornaam      : $scope.akkoord.naam,
						achternaam    : $scope.akkoord.achternaam,
						telefoonnummer: $scope.akkoord.telefoonnummer,
						afdeling      : JSON.parse($window.sessionStorage.user).afdeling,
						wachtwoord    : $scope.akkoord.wachtwoord,
						bedrijfIdDoor : owncompanyidentifier,
						bedrijfIdOver : companyidentifier,
						meldingen     : NotificationsToSend.getNotifications()
					};
					

					CompanyService.notifyCompanyBatch(batch, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							$scope.error = response.errorMsg;
							delete $scope.formSubmitted;
						} else {
							NotificationsToSend.setNotifications([]);
							$scope.vermeldingResultaat = response.result; //'Uw vermelding is ontvangen en wordt door ons behandeld.'; //response;
							$scope.meldingenSavedOk = true;
						}
					});
				} //else
				//	delete $scope.formSubmitted;
				
			};

			$scope.back = function() {
				$location.path('/notifycompany/' + owncompanyidentifier + '/' + companyidentifier + '/' + searchurl);
				$location.url($location.path());
			};

			$scope.sendok = function() {
				url = gotoDurl('$dashboard$companiesnotifiedtab');
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			if(typeof $scope.init === "undefined") {
				var user = JSON.parse($window.sessionStorage.user);
				var telefoonNr = null;
				if(typeof user.gebruikerTelefoonNummer != 'undefined') {
					telefoonNr = user.gebruikerTelefoonNummer;
				} else {
					telefoonNr = user.bedrijfTelefoonNummer;
				}
				
				$scope.aantalVermeldingen = NotificationsToSend.getNotifications().length;
				delete $scope.vermeldingenTotaalbedrag;
				
				CompanyService.getTariefOfProduct({ProductCode: 'VER'}, function(response) {
					if(typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorMsg;
					} else {
						vermeldingTarief = response.tarief;
						$scope.vermeldingenTotaalbedrag = $filter('currency')($scope.aantalVermeldingen * vermeldingTarief);
					}					
				});

				$scope.akkoord = {
					voornaam      : user.firstName,
					achternaam    : user.lastName,
					telefoonnummer: telefoonNr,
					afdeling      : user.afdeling
				};

				var wasFormSubmitted = false;
				$scope.formSubmitted = false;
				
				$scope.init = true;
			}
		}]);appcontrollers.controller('NotificationsStatusTabController', [
	'$scope', '$rootScope', '$filter', 'maxFieldLengths', 'ngTableParams', 'DashboardService',
	function($scope, $rootScope, $filter, maxFieldLengths, ngTableParams, DashboardService) {
		0;

		$scope.title = "Notifications Status Tab";

		$scope.maxFieldLengths = maxFieldLengths;
		
		$scope.totalItems = 0;
		$scope.currentPage = 1;
		$scope.itemsPage = 20;
		$scope.maxSize = 5;

		$scope.setPage = function(pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			0;
		};

		0;

		$scope.filterCriteria = {
			pageNumber : 1,
			sortDir    : "", // asc, desc
			sortedBy   : "",
			filterValue: "" // text to filter on
		};
		0;

		$scope.nrOfItems = function() {
			return 0;
		};

		var notifications = [];

		// Data table functions
		$scope.filterResult = function() {

			return $scope.fetchResult(function() {
				//The request fires correctly but sometimes the ui doesn't update, that's a fix
				$scope.filterCriteria.pageNumber = 1;
				$scope.currentPage = 1;

				// rootscope range delete
				delete $rootScope.range;
			});
		};
		0;

		// The function that is responsible of fetching the result from the
		// server and setting the grid to the new result
		$scope.fetchResult = function(success) {
			$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
			DashboardService.notificationsstatus($scope.filterCriteria, function(data, headers) {
				0;
				$scope.notifications = data.notificationStatus;
				$scope.totalItems = paging_totalItems(headers("Content-Range"));
				success();
			}, function() {
				0;
				$scope.notifications = [];
				$scope.totalItems = 0;
			});

		};
		0;

		// Pagination functions
		$scope.setPage = function(pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			0;
			$scope.filterCriteria.pageNumber = $scope.currentPage;
			$scope.fetchResult(function() {
				//Nothing to do..

				// rootscope range delete
				delete $rootScope.range;
			});
		};

		// fetch initial data for 1st time
		$scope.filterResult();
	}]);appcontrollers.controller('NotifyController',
	['$window', '$modal', '$scope', '$rootScope', '$routeParams', '$http', 'maxFieldLengths',
		'$location', '$filter', '$q', 'ngTableParams', 'ownCompanyData',
		'companyData', 'CompanyService', 'GebruikersServicePath', 'NotificationsToSend',
		function($window, $modal, $scope, $rootScope, $routeParams, $http, maxFieldLengths,
				 $location, $filter, $q, ngTableParams, ownCompanyData,
				 companyData, CompanyService, GebruikersServicePath, NotificationsToSend) {
			0;
			
			//$scope.time = new Date(1970, 0, 1, 10, 30, 40);
			//  $scope.selectedTimeAsNumber = 10 * 36e5 + 30 * 6e4 + 40 * 1e3;
			//  $scope.selectedTimeAsString = '10:00';
			//  $scope.sharedDate = new Date(new Date().setMinutes(0, 0));			

			$scope.maxFieldLengths = maxFieldLengths;
			
			//tmhDynamicLocale.set('nl-nl');

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}
			orgsearchurl = searchurl;

			$scope.sliderValue = 8;

			$scope.htmlTooltip = 'Wanneer u deze optie geselecteerd houdt zullen uw bedrijfsgegevens worden weergeven binnen het register.';
			//\nUw melding zal dan openbaar zijn.\nWenst u liever anoniem een melding te verrichten?\nZorg er dan voor dat het u het vinkje weghaalt.\nDe anonimiteit strekt zich uit tot derden die niet bij uw vordering betrokken zijn.\nDe partij waarvan u melding maakt zal uiteraard wel op de hoogte gesteld worden van uw melding.';

			$scope.dosomething = function() {
				0;
			};

			// whole numbers only $scope.NUMBER_REGEXP = /^[0-9]+$/;
			$scope.NUMBERINTERNAL_REGEXP = /^(\d*\.\d{1,2}|\d+)$/; // number '.' sep
			$scope.dateFormat = 'ddMMyyyy';
			$scope.timeFormat = 'HHmm'

			$scope.minDateInvoice = new Date(2010, 1, 1);
			$scope.maxDateInvoice = new Date();

			var bedragHelpSpan = $("#bedraghelp");
			var datumfactuurHelpSpan = $("#datumfactuurhelp");
			var referenceHelpSpan = $("#referentiehelp");

			var companyidentifier = $routeParams.bedrijfId;
			var notificationidentifier = $routeParams.meldingId || undefined;
			var readonly = $routeParams.readonly || undefined;

			0;

			$scope.isInvalid = function(targetForm, targetField) {
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(targetForm[targetField].$dirty) {
					delete $scope.error;
					delete $scope.meldingVerwerkt;
					delete $scope.meldingSavedOk;
					delete $scope.contactMomentSavedOk;
					delete $scope.notitieSavedOk;
					delete $scope.meldingRemovedOk;
					// Save old hash (might be no hash), set location, scroll to it, and set old hash back(most common result: no hash in url)
					// $anchorScroll() works best when an id is provided
					//var old = $location.hash();
					//$location.hash(targetForm[targetField].$id);
					//$anchorScroll();
					//$location.hash(old);
				}

				if((targetField == 'akkoordBedrijf' || targetField == 'geenAkkoordBedrijf') && result_dirty) {
					result_invalid = !($scope.controle.bedrijfsgegevensok || $scope.controle.bedrijfsgegevensNietJuist);
				}

				//500
				//check
				//if ((targetField == 'bedrag' || targetField == 'vorderingHoogGenoeg') && result_dirty) {
				//	result_invalid = $scope.bedragAanwezig($scope.melding.bedrag) == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false);
				//	if (result_invalid) {
				//		if (targetField == 'bedrag')
				//			bedragHelpSpan.text('Indien er geen bedrag ingevuld wordt, hoger dan 500 Euro, dient bevestigd te worden dat het vorderingbedrag van de vermelding hoger is dan 500 Euro.');
				//	}
				//
				//}

				return result_dirty && result_invalid;

			};

			$scope.owncompany = ownCompanyData;
			//{
			//	name: "De Voorkant B.V.",
			//		address
			//:
			//	"Astrounaut 22d",
			//		zipcode
			//:
			//	"3824 MJ",
			//		city
			//:
			//	"Amersfoort",
			//		telephone
			//:
			//	"033-4557485",
			//		kvknumber
			//:
			//	"38024739"
			//}

			//$scope.company = CompanyCrudService.get({id: companyidentifier}, function() {
			//0;

			//});
			$scope.company = companyData;
			//{
			//	name: "Wanbetaler B.V.",
			//		address
			//:
			//	"Croeselaan 32 UTRECHT",
			//		kvkNumber
			//:
			//	"00021345"
			//}

			if(typeof $scope.melding === 'undefined' && typeof notificationidentifier !== 'undefined') {
				0;
				$scope.editExistingMelding = true;
				$scope.meldingDetails = true;
			}

			$scope.hideConfirm = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.hidePlaatsVermelding = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.isUnchanged = function(melding) {
				return angular.equals(melding, $scope.mastermelding);
			};
			
			$scope.isUnchangedAdminNote = function() {
				return (!$scope.gebruikerAllowed() || 
							(typeof $scope.masterMeldingNotitiesAdmin === 'undefined' && typeof $scope.meldingNotitiesAdmin === 'undefined') || 
							(typeof $scope.masterMeldingNotitiesAdmin !== 'undefined' && angular.equals($scope.masterMeldingNotitiesAdmin, $scope.meldingNotitiesAdmin)));
			};

			$scope.showAanpassenVermelding = function() {
				return !!($scope.editExistingMelding == true && typeof $scope.meldingVerwerkt === 'undefined');
			};

			$scope.hideAnnuleren = function() {
				return !!$scope.editExistingMelding;
			};

			$scope.showTerugDetail = function() {
				return !!$scope.editExistingMelding;
			};

			/// START Melding

			$scope.meldingIdSelected = null;
			$scope.setSelected = function(meldingId) {
				$scope.meldingIdSelected = meldingId;
			};

			$scope.buttonUserDisabled = function() {
				return false;
			};

			$scope.checkMeldingFormInvalid = function(targetform) {
				// TODO Fields!!
				return targetform['voornaam'].$invalid ||
					targetform['naam'].$invalid ||
					targetform['emailadres'].$invalid ||
					targetform['functie'].$invalid ||
					$scope.emailCheckInvalid(targetform, 'emailadres', 'emailadrescontrole');
			};

			cleanMeldingForm = function() {
				delete $rootScope.error;
				delete $scope.error;
				delete $scope.meldingSavedOk;
				delete $scope.meldingRemovedOk;

				// set form fields not dirty
				if($scope.formmeldinggegevens) {
					$scope.formmeldinggegevens.$setPristine();
				}

				if($scope.formbedrijfsgegevens) {
					$scope.formbedrijfsgegevens.$setPristine();
				}
				
				if($scope.formmeldingnotes) {
					$scope.formmeldingnotes.$setPristine();
				}
				
				if($scope.formmeldingnotesadmin) {
					$scope.formmeldingnotesadmin.$setPristine();
				}
			};

			$scope.newMelding = function() {
				cleanMeldingForm();

				delete $scope.meldingIdSelected;
				$scope.meldingDetails = true;

				// TODO Fields!!
				$scope.controle = {
					bedrijfsgegevensok       : $scope.controle.bedrijfsgegevensok, //false,
					vorderingbedragok        : false,
					akkoord                  : true,
					bedrijfsgegevensNietJuist: $scope.controle.bedrijfsgegevensNietJuist // false
				};
				$scope.melding = {
					meldingId                : $scope.newMeldingId,
					gebruikerAangemaaktId    : JSON.parse($window.sessionStorage.user).userId,
					bedrijfIdGerapporteerd   : $scope.owncompany.bedrijfId,
					bedrijfId                : $scope.company.bedrijfId,
					bedrijfsgegevensNietJuist: false,
					doorBedrijfWeergeven     : true,
					bedragWeergeven          : true,
					debiteurSommatieOntvangen: false,
					debiteurBetwistNiet      : false
				};
				$scope.newMeldingId++;

				$scope.mastermelding = angular.copy($scope.melding);

				$scope.mandatoryChecks();
			};

			$scope.changeMelding = function(meldingId) {
				cleanMeldingForm();
				$scope.meldingIdSelected = meldingId;
				$scope.meldingDetails = true;

				melding = $.grep($scope.meldingen, function(e) {
					return e.meldingId == meldingId;
				});

				$scope.mastermelding = angular.copy($scope.melding);

				// TODO Fields!!!
				$scope.controle = {
					bedrijfsgegevensok       : $scope.controle.bedrijfsgegevensok, //true,
					vorderingbedragok        : false, // 500 check true
					akkoord                  : true,
					bedrijfsgegevensNietJuist: $scope.controle.bedrijfsgegevensNietJuist // false
				};

				// for editing validation purpose
				var convertedDatumFactuur = null;
				if(melding[0].datumFactuur) {
					convertedDatumFactuur = melding[0].datumFactuur ? melding[0].datumFactuur.replace(/-/g, '') : null;
				}

				$scope.melding = {
					meldingId                : melding[0].meldingId,
					gebruikerAangemaaktId    : melding[0].gebruikerAangemaaktId,
					gebruikerLaatsteMutatieId: JSON.parse($window.sessionStorage.user).userId,
					bedrijfIdGerapporteerd   : melding[0].bedrijfIdGerapporteerd,
					bedrijfId                : melding[0].bedrijfId,
					bedrijfsgegevensNietJuist: melding[0].bedrijfsgegevensNietJuist,
					doorBedrijfWeergeven     : melding[0].doorBedrijfWeergeven,
					bedragWeergeven          : melding[0].bedragWeergeven,
					referentie               : melding[0].referentie,
					datumFactuur             : convertedDatumFactuur,
					bedrag                   : melding[0].bedrag,
					debiteurSommatieOntvangen: melding[0].debiteurSommatieOntvangen,
					debiteurBetwistNiet      : melding[0].debiteurBetwistNiet
				};

				$scope.mandatoryChecks();

				//$scope.filterResult();
			};

			$scope.deleteMelding = function(meldingId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller : RemoveNotificationController,
					size       : 'lg'
				});

				modalInstance.result.then(function(result) {
					if(result.remove) {
						cleanMeldingForm();
						$scope.meldingIdSelected = meldingId;

						// do delete
						removeNotification(meldingId, companyData.bedrijfId);
					}
				}, function() {
					0;
				});

			};

			$scope.getAfdeling = function(afdeling) {
				// afdeling name fill with default if unknown
				if(typeof afdeling == 'undefined' || afdeling == null || afdeling == '') {
					return 'Vermeldingen';
				} else {
					return afdeling;
				}
			};

			removeNotification = function(meldingId, bedrijfId) {
				$scope.removeMeldingId = meldingId;

				//CompanyService.removeNotificationCompany({
				//	meldingId: meldingId,
				//	bedrijfId: bedrijfId,
				//	gebruikerId: JSON.parse($window.sessionStorage.user).userId
				//}, function (result) {
				//	if (typeof result.errorCode !== "undefined")
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	else {
				//		$scope.meldingIdSelected = null;
				//		delete $scope.meldingDetails;
				//
				//		$scope.deleteMeldingCallback();
				//	}
				//});

				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;

				$scope.deleteMeldingCallback();
			};

			$scope.cancelMeldingDetails = function() {
				cleanMeldingForm();
				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;
				delete $scope.melding;
				delete $scope.duplicateFactuurnummer;

				// for verzendlijst
				$scope.controle.vermeldingenJuist = false;
			};

			$scope.saveMeldingCallback = function() {
				$scope.meldingSavedOk = true;

				$scope.filterResult();
				//refresh
				//$scope.currentPage = 1;
				//$scope.pageChanged();
				//
				//$scope.meldingIdSelected = null;
				//delete $scope.meldingDetails;
				//delete $scope.melding;
				//
				//save + new
				$scope.newMelding();
			};

			$scope.updateMeldingCallback = function() {
				$scope.meldingVerwerkt = true;
				
				// obsolete
				//$scope.meldingSavedOk = true;

				// set form fields not dirty
				$scope.formmeldingnotes.$setPristine();
				
				$scope.formmeldinggegevens.$setPristine();

				$scope.formbedrijfsgegevens.$setPristine();
			};

			$scope.deleteMeldingCallback = function() {
				$scope.meldingRemovedOk = true;

				$scope.filterResult();
				// refresh
				//$scope.currentPage = 1;
				//$scope.pageChanged();

				$scope.meldingIdSelected = null;
				delete $scope.meldingDetails;
				delete $scope.melding;
			};

			validateNotificationList = function() {
				var result = true;

				if($scope.melding && typeof $scope.melding.referentie !== 'undefined' && $scope.melding.referentie !== null) {

					for(var i in $scope.meldingen) {
						if($scope.meldingen[i].meldingId != $scope.melding.meldingId && $scope.meldingen[i].referentie.toLowerCase().replace(/[^a-zA-Z 0-9]+/g, '') == $scope.melding.referentie.toLowerCase().replace(/[^a-zA-Z 0-9]+/g, '')) {
							result = false;
							break; //Stop this loop, we found it!
						}
					}
				}

				return result;
			};

			$scope.saveMeldingPassTwo = function(callback) {
				//delete $scope.error;
				//delete $scope.duplicateFactuurnummer;
				cleanMeldingForm();

				if(validateNotificationList()) {
					$scope.meldingToValidate = {
						meldingId                : (!isNaN(parseFloat($scope.melding.meldingId)) && isFinite($scope.melding.meldingId) ? null : $scope.melding.meldingId),
						gebruikerAangemaaktId    : $scope.melding.gebruikerAangemaaktId,
						gebruikerLaatsteMutatieId: JSON.parse($window.sessionStorage.user).userId,
						bedrijfIdGerapporteerd   : $scope.melding.bedrijfIdGerapporteerd,
						bedrijfId                : $scope.melding.bedrijfId,
						bedrijfsgegevensNietJuist: $scope.melding.bedrijfsgegevensNietJuist,
						doorBedrijfWeergeven     : $scope.melding.doorBedrijfWeergeven,
						bedragWeergeven          : $scope.melding.bedragWeergeven,
						referentie               : $scope.melding.referentie,
						datumFactuur             : convertDate($scope.melding.datumFactuur),
						bedrag                   : $scope.melding.bedrag,
						debiteurSommatieOntvangen: $scope.melding.debiteurSommatieOntvangen,
						debiteurBetwistNiet      : $scope.melding.debiteurBetwistNiet
					};

					CompanyService.validateNotification($scope.meldingToValidate, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							if(response.errorCode == 118) // Duplicate referenceNumber
							{
								$scope.duplicateFactuurnummer = true;
							}
							else {
								$scope.error = response.errorCode + " " + response.errorMsg;
							}
						}
						else {
							$scope.preregister($scope.saveMeldingCallback); // create or update!

							callback();
							//$scope.saveMeldingCallback();
						}
					});
				}
				else {
					$scope.duplicateFactuurnummer = true;
				}
			};

			// form button
			$scope.updateMelding = function() {
				$scope.formSubmitted = true;

				if(!($scope.formmeldinggegevens.$invalid || $scope.formbedrijfsgegevens.$invalid || ($scope.isUnchanged($scope.melding) && $scope.isUnchangedAdminNote()) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;
					//delete $scope.error;
					if (!$scope.isUnchanged($scope.melding)) {
						cleanMeldingForm();
						$scope.register($scope.updateMeldingCallback); // create or update!
					}
					
					// admin note
					if ($scope.gebruikerAllowed && !$scope.isUnchangedAdminNote()) {
						var notitieAdmin = {
								meldingId : $scope.melding.meldingId,
								notitie : $scope.meldingNotitiesAdmin
						}
						
						CompanyService.addAdminNotitieNotification(notitieAdmin, function(response) {
							if(typeof response.errorCode !== 'undefined') {
								$scope.error = response.errorCode + " " + response.errorMsg;
							} else {	
								$scope.notitieSavedOk = true;
								
								$scope.formmeldingnotesadmin.$setPristine();
							}
						});
					}
				}
			};
			
			//Form update note
			$scope.updateNote = function() {
				$scope.formSubmitted = true;
				
				if(!(($scope.isUnchanged($scope.melding) && $scope.isUnchangedAdminNote()) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;
					delete $scope.error;
					if (!$scope.isUnchanged($scope.melding)) {
						cleanMeldingForm();
						$scope.register($scope.updateMeldingCallback); // create or update!
					}
					
					// admin note
					if ($scope.gebruikerAllowed && !$scope.isUnchangedAdminNote()) {
						var notitieAdmin = {
								meldingId : $scope.melding.meldingId,
								notitie : $scope.meldingNotitiesAdmin
						}
						
						CompanyService.addAdminNotitieNotification(notitieAdmin, function(response) {
							if(typeof response.errorCode !== 'undefined') {
								$scope.error = response.errorCode + " " + response.errorMsg;
							} else {	
								$scope.notitieSavedOk = true;
								
								$scope.formmeldingnotesadmin.$setPristine();
							}
						});
					}
				}
			};
			
			

			// form button
			$scope.saveMelding = function() {
				$scope.formSubmitted = true;
				if(!($scope.formmeldinggegevens.$invalid || ($scope.formbedrijfsgegevens.$invalid || (!$scope.controle.bedrijfsgegevensok && !$scope.controle.bedrijfsgegevensNietJuist)) || $scope.isUnchanged($scope.melding) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;

					$scope.saveMeldingPassTwo(function() {
					});
				}
			};

			// form button
			$scope.saveMeldingAndSend = function() {
				//$scope.saveMeldingPassTwo($scope.sendMeldingen);
				$scope.formSubmitted = true;
				if(!($scope.formmeldinggegevens.$invalid || ($scope.formbedrijfsgegevens.$invalid || (!$scope.controle.bedrijfsgegevensok && !$scope.controle.bedrijfsgegevensNietJuist)) || $scope.isUnchanged($scope.melding) || $scope.buttonMeldingDisabled())) {
					delete $scope.formSubmitted;

					$scope.saveMeldingPassTwo($scope.cancelMeldingDetails);
				}
			};

			// form button
			$scope.sendMeldingen = function() {
				$scope.formSubmitted = true;

				if($scope.controle.vermeldingenJuist) {
					//searchurl = '$notifycompany';

					// set bedrijfsgegevensjuist
					for(var i in $scope.meldingen) {
						$scope.meldingen[i].bedrijfsgegevensNietJuist = $scope.controle.bedrijfsgegevensNietJuist;
					}

					NotificationsToSend.setNotifications($scope.meldingen);

					$location.path('/notificationssend/' + $scope.owncompany.bedrijfId + '/' + $scope.company.bedrijfId + '/' + searchurl);
					$location.url($location.path());
				}
			};

			$scope.$on("$destroy", function() {
				// empty notification cache if not sending them
				if($location.path().lastIndexOf('/notificationssend', 0) !== 0) {
					NotificationsToSend.setNotifications([]);
				}
			});

			// Data table functions
			$scope.filterResult = function() {

				return $scope.fetchResult(function() {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.currentPage = 1;
				});
			};

			$scope.pageChanged = function() {
				if($scope.meldingDetails == true) {
					$scope.cancelMeldingDetails();
				}
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();

				0;
				$scope.fetchResult(function() {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function(success) {

				// no server call needed

				// add new melding to array
				if($scope.meldingSavedOk == true) {
					if(typeof $scope.meldingIdSelected != 'undefined' && $scope.meldingIdSelected != null) {
						for(var i in $scope.meldingen) {
							if($scope.meldingen[i].meldingId == $scope.meldingIdSelected) {
								$scope.meldingen[i] = $scope.melding;
								break; //Stop this loop, we found it!
							}
						}
					}
					else {
						if(typeof $scope.meldingen != 'undefined') {
							if(Object.prototype.toString.call($scope.meldingen) === '[object Array]') {
								$scope.meldingen.push($scope.melding);
							} else {
								$scope.meldingen = [$scope.melding];
							}
						}
						else {
							$scope.meldingen = [$scope.melding];
						}

						if($scope.totalItems) {
							$scope.totalItems++;
						} else {
							$scope.totalItems = 1;
						}

						// if new items added, goto companiesnotified dashboard afterwards
						//searchurl = '$dashboard$companiesnotifiedtab';
					}
				}
				// remove melding from array
				else if($scope.meldingRemovedOk == true) {
					$scope.meldingen = $scope.meldingen
						.filter(function(el) {
							return el.meldingId !== $scope.removeMeldingId;
						});
					$scope.totalItems--;

					// if no new items added anymore, goto back to original
					if($scope.totalItems == 0) {
						searchurl = orgsearchurl;
						//delete $scope.totalItems;
					}
				}
			};
			
			$scope.contactMomentPageChanged = function(page) {
				$scope.contactMomentCurrentPage = page;
				$scope.contactMomentFilterCriteria.pageNumber = $scope.contactMomentCurrentPage;
				$scope.fetchContactMomenten(function() {
					delete $rootScope.contactMomentRange;
				});
			};
			
			$scope.fetchContactMomenten = function(success) {
				$rootScope.range = 'items=' + (($scope.contactMomentFilterCriteria.pageNumber - 1) * $scope.contactMomentItemsPage + 1) + '-' + (($scope.contactMomentFilterCriteria.pageNumber - 1) * $scope.contactMomentItemsPage + $scope.contactMomentItemsPage);
				CompanyService.getContactMomentsOfNotification($scope.contactMomentFilterCriteria, function(data, headers) {
					if(data != undefined && data.length > 0) {
						$scope.contactMomenten = data;

						$scope.contactMomentTotalItems = paging_totalItems(headers('Content-Range'));
					} else {
						$scope.contactMomenten = [];
						$scope.contactMomentTotalItems = 0;
					}
					success();
				}, function(error) {
					$scope.contactMomenten = [];
					$scope.contactMomentTotalItems = 0;

					$scope.error = error;
				})
			};

			/// END Melding

			$scope.datePattern = /^[0-9]{2}-[0-9]{2}-[0-9]{4}$/i;

			$scope.pickDateFactuur = function(dateval) {

				$scope.melding.datumFactuur = $filter('date')(dateval, $scope.dateFormat);
			};
			
			$scope.dateChange = function(dateval) {
			    var selectedDate = dateval;
			    var min = new Date(selectedDate.getTime());
			    min.setHours(2);
			    min.setMinutes(0);
			    $scope.min = min;

			    var max = new Date(selectedDate.getTime());
			    max.setHours(4);
			    max.setMinutes(0);
			    $scope.max = max;
			}			

		    $scope.initTimePicker = function(selectedDate) {
		         var min = new Date(selectedDate.getTime());
		         min.setHours(0);
		         min.setMinutes(0);
		         $scope.min = min;

		         var max = new Date(selectedDate.getTime());
		         max.setHours(11);
		         max.setMinutes(59);
		         $scope.max = max;
		    };	    
		    
			$scope.pickDateContact = function(dateval) {
				$scope.contactMoment.datumContact = $filter('date')(dateval, $scope.dateFormat);				
			};
			
			$scope.pickTimeContact = function(timeval) {
				$scope.contactMoment.timeContact = $filter('date')(timeval, $scope.timeFormat);
			}
			
			$scope.pickDateContactTerug = function(dateval) {

				$scope.contactMoment.datumContactTerug = $filter('date')(dateval, $scope.dateFormat);
			};		
			
			
			convertDate = function(date) {
				if(date && date.length == 8) {
					return date.substring(0, 2) + '-' + date.substring(2, 4) + '-' + date.substring(4, 8);
				}
				else if(date) {
					return date;
				} else {
					return null;
				}
			};
			
			convertDatetime = function(date, time) {
				var datetime = convertDate(date);
				
				if (datetime && time && time.length == 4) {
					datetime +=  ' ' + time.substring(0,2) + ':' + time.substring(2,4) + ':00';
				}
				
				if (datetime)
					return datetime;
				else
					return null;
			}	
			
			$scope.toggleContactwijze = function(contactwijze) {
				if (contactwijze != 'Anders') {
					$scope.contactMoment.contactGegevens = null;
				}
			}
			
			$scope.contactMomentComplete = function() {
				var result = false;
				if (typeof $scope.contactMoment != 'undefined') {
					if ($scope.contactMoment.datumContact != null &&
							$scope.contactMoment.timeContact != null &&
							$scope.contactMoment.contactwijze != null &&
							$scope.contactMoment.beantwoord != null &&
							$scope.contactMoment.notitie != null)
						return true;				
				}
				
				return result;
			}
			
			$scope.removeContactMoment = function(contactMomentId) {
				CompanyService.removeContactMomentNotification({contactMomentId : contactMomentId, bedrijfId : $scope.melding.bedrijfIdGerapporteerd}, function(response) {
					if (typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorCode + " " + response.errorMsg;
					} else {
						$scope.meldingVerwerkt = true;
						$scope.fetchContactMomenten(null);
					}
				});
			}

			$scope.saveContactMoment = function() {
				if ($scope.contactMomentComplete())
				{
					$scope.contactMomentTransfer = {
							datumContact : convertDatetime($scope.contactMoment.datumContact, $scope.contactMoment.timeContact),
							datumContactTerug : convertDate($scope.contactMoment.dateContactTerug),
							contactWijze : $scope.contactMoment.contactwijze,
							contactGegevens : $scope.contactMoment.contactGegevens,
							beantwoord : $scope.contactMoment.beantwoord,
							notitie : $scope.contactMoment.notitie,
							notitieIntern : $scope.contactMoment.notitieIntern,
							alert : $scope.contactMoment.alert,
							meldingId : $scope.melding.meldingId,
							bedrijfIdGerapporteerd : $scope.melding.bedrijfIdGerapporteerd,
							notitieType : 'CMO'
					};
					
					CompanyService.addContactMomentNotification($scope.contactMomentTransfer, function(response) {
						if(typeof response.errorCode !== 'undefined') {
							$scope.error = response.errorCode + " " + response.errorMsg;
						} else {
							$scope.contactMoment = response.contactMoment;
							$scope.contactMoment = {
									datumContact : $filter('date')(dateNow, $scope.dateFormat),
									timeContact : $filter('date')(dateNow, $scope.timeFormat),
							};							
							$scope.formcontactmoment.$setPristine();
							
							$scope.contactMomentSavedOk = true;
							
							$scope.fetchContactMomenten(function() {
								delete $rootScope.contactMomentRange;
							});							
						}
					});
				}
			}
			
			$scope.preregister = function(callback) {
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				0;
				//if (typeof $scope.datumFactuur !== 'undefined' )
				//{
				//$scope.melding.datumFactuur = $scope.datumFactuur;
				//$scope.melding.datumFactuur = $filter('date')($scope.melding.datumFactuur,'dd-MM-yyyy');
				//}
				$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

				callback();
			};

			$scope.register = function(callback) {
				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				0;
				//if (typeof $scope.datumFactuur !== 'undefined' )
				//{
				//$scope.melding.datumFactuur = $scope.datumFactuur;
				//$scope.melding.datumFactuur = $filter('date')($scope.melding.datumFactuur,'dd-MM-yyyy');
				//}
				$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

				CompanyService.notifyCompany($scope.melding, function(response) {
					if(typeof response.errorCode !== 'undefined') {
						$scope.error = response.errorCode + " " + response.errorMsg;
					} else {
						//response.doorBedrijfWeergeven = response.doorBedrijfWeergeven == 'true' ? true : false;
						//response.bedragWeergeven = response.bedragWeergeven == 'true' ? true : false;
						//response.bedrijfsgegevensNietJuist = response.bedrijfsgegevensNietJuist  == 'true' ? true : false;

						$scope.controle.bedrijfsgegevensNietJuist = response.bedrijfsgegevensNietJuist;
						$scope.bedrijfgegevensJuistOnLoad = !response.bedrijfsgegevensNietJuist;

						if(response.bedrag) {
							response.bedrag = Number(response.bedrag);
						}

						// for form validation/editing
						if(response.datumFactuur) {
							response.datumFactuur = response.datumFactuur.replace(/-/g, '');
						}

						response.debiteurSommatieOntvangen = true;
						response.debiteurBetwistNiet = true;

						$scope.melding = response;
						$scope.melding.datumFactuur = convertDate($scope.melding.datumFactuur);

						callback();
					}
				});
			};

			$scope.open = function($event, indexCtrl) {
				$event.preventDefault();
				$event.stopPropagation();

				if (indexCtrl == 1)
					$scope.opened_1 = true;
				else if (indexCtrl == 2)
					$scope.opened_2 = true;
					
			};

			$scope.dateOptions = {
				formatYear : 'yyyy',
				startingDay: 1
			};

			$scope.clearDateInvoice = function() {
				$scope.melding.datumFactuur = null;
			};

			$scope.back = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			addMandatoryText = function(type, text) {
				var item = {type: type, text: text};

				if(typeof $scope.mandatoryText != 'undefined') {
					if(Object.prototype.toString.call($scope.mandatoryText) === '[object Array]') {
						$scope.mandatoryText.push(item);
					} else {
						$scope.mandatoryText = [item];
					}
				}
				else {
					$scope.mandatoryText = [item];
				}
			};

			$scope.maakbedragop = function(bedrag) {
				if(bedrag) {
					return '&euro; ' + bedrag;
				} else {
					return '-'
				}
			};

			$scope.mandatoryChecks = function() {
				$scope.mandatoryText = [];

//				if($scope.controle.bedrijfsgegevensok == false) {
//					addMandatoryText('warning', 'Er moet aangegeven worden dat de bedrijfsgegevens van de vermelding juist zijn.');
//				}

				var debiteurText = null;
				if(typeof $scope.melding.debiteurSommatieOntvangen == 'undefined' || $scope.melding.debiteurSommatieOntvangen == false) {
					debiteurText = 'U dient aan te geven dat de debiteur reeds op de hoogte is van de vordering.';
				}
				if(typeof $scope.melding.debiteurBetwistNiet == 'undefined' || $scope.melding.debiteurBetwistNiet == false) {
					if(debiteurText == null) {
						addMandatoryText('warning', 'U dient aan te geven dat de debiteur de vordering niet betwist.');
					} else {
						addMandatoryText('warning', 'U dient aan te geven dat de debiteur reeds op de hoogte is van de vordering en dat deze niet betwist wordt.');
					}
				} else if(debiteurText != null) {
					addMandatoryText('warning', debiteurText);
				}

				// 500 check if ($scope.bedragAanwezig($scope.melding.bedrag) == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false) )
				//	addMandatoryText('warning', 'Indien er geen bedrag ingevuld wordt, hoger dan 500 Euro, dient bevestigd te worden dat het factuurbedrag van de vermelding hoger is dan 500 Euro.');

				if($scope.controle.akkoord == false) {
					addMandatoryText('warning', 'U moet akkoord gaan met onze algemene voorwaarden.');
				}

				if($scope.mandatoryText.length == 0 && !($scope.isBedragInvalid($scope.melding.bedrag) || $scope.isDatumFactuurInvalid($scope.melding.datumFactuur)) && !($scope.formmeldinggegevens.$invalid || $scope.formbedrijfsgegevens.$invalid || $scope.buttonMeldingDisabled())) {
					if(!$scope.wijziging) {
						addMandatoryText('success', ' U kunt kiezen om deze vermelding direct te verzenden of hier nog een vermelding aan toe te voegen.');
					}
				}
			};

			$scope.buttonMeldingDisabled = function() {

				if(typeof $scope.melding != 'undefined') {
					if($scope.isBedragInvalid($scope.melding.bedrag) || $scope.isDatumFactuurInvalid($scope.melding.datumFactuur)) {
						return true;
					} else if($scope.bedragAanwezig == false && (typeof $scope.controle.vorderingbedragok == 'undefined' || $scope.controle.vorderingbedragok == false)) {
						return true;
					}
				}

				//if (!controle.akkoord)
				//	return true;

				return false;
			};

			$scope.bedragAanwezig = function(bedrag) {
				return !!(typeof bedrag != 'undefined' && bedrag != null && bedrag != "");
			};

			$scope.isReferentieInvalid = function(referentie) {
				var result = false;
				var referencetext = 'Indien u geen factuurnummer hanteert geeft u een dossiernummer of ander kenmerk op.';

				if($scope.duplicateFactuurnummer) {
					referencetext = 'Het ingevoerde factuurnummer is al gebruikt voor een bestaande of ingevoerde vermelding. Voer een ander factuurnummer in.';
					result = true;
				}

				referenceHelpSpan.text(referencetext);

				return result;
			};

			$scope.isBedragInvalid = function(bedrag) {
				if($scope.bedragAanwezig(bedrag)) {
					$scope.controle.vorderingbedragok = false;
				}

				//var bedragtext = "Voer een geldig bedrag (afgerond op hele Euro's) in";
				var bedragtext = "Voer het exacte bedrag in.";
				var result = false;

				if(!$scope.wijziging && typeof bedrag != 'undefined' && bedrag != null && bedrag != "" && $scope.NUMBERINTERNAL_REGEXP.test(bedrag)) {
					if(bedrag < 100) {
						bedragtext = "Het openstaande bedrag moet minimaal 100,- Euro bedragen.";
						result = true; // invalid
					}
					else {
						result = false;
					} // valid
				} else {
					result = false;
				} // valid

				if($scope.wijziging && typeof bedrag != 'undefined') {
					if(typeof $scope.mastermelding.bedrag !== 'undefined' && $scope.mastermelding.bedrag != null) {
						if(typeof bedrag != 'undefined' && bedrag != null) {
							if($scope.mastermelding.bedrag < bedrag) {
								bedragtext = "Het bedrag mag alleen worden verlaagd.";
								result = true; // invalid
							}
						} else {
							bedrag = $scope.mastermelding.bedrag; // this may not happen
						}
					} else {
						bedragtext = "Bedrag kan niet worden gewijzigd"; // this may not happen
						result = true; // invalid
					}
				}

				if(bedrag > 100000000) {
					bedragtext = "Het ingevoerde bedrag mag maximaal 100.000.000,- zijn.";
					result = true; // invalid					
				}

				bedragHelpSpan.text(bedragtext);

				return result;
			};

			$scope.isDatumFactuurInvalid = function(datumFactuur) {
				var datumFactuurtext = 'Vul de vervaldatum van de factuur in (dd-mm-jjjj).';
				var result = false;

				if(typeof datumFactuur != 'undefined' || datumFactuur != null) {
					var datumFactuurdate = new Date();
					var datumVandaag = new Date();

					if(typeof datumFactuur == 'string') {
						if(datumFactuur.length == 8) {
							datumFactuurdate = new Date(datumFactuur.substring(4, 8), datumFactuur.substring(2, 4) - 1, datumFactuur.substring(0, 2));
						} else if(datumFactuur.length == 10) {
							datumFactuurdate = new Date(datumFactuur.substring(6, 10), datumFactuur.substring(3, 5) - 1, datumFactuur.substring(0, 2));
						} else {
							result = true;
						} // invalid
					}
					else {
						datumFactuurdate = datumFactuur;
					}

					if(!result) { // if valid
						if (datumVandaag !== null && datumFactuurdate !== null) {
							if(dates.compare(datumVandaag, datumFactuurdate) < 0) {
								datumFactuurtext = 'Factuurdatum mag niet in de toekomst liggen.';
								result = true; // invalid
							}
							else {
	
								var timeDiff = Math.abs(datumVandaag.getTime() - datumFactuurdate.getTime());
								var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
	
								if(diffDays < 30) {
									datumFactuurtext = 'Vervaldatum van factuur moet minimaal 30 dagen zijn.';
									result = true; // invalid
								}
								else {
									result = false;
								} // valid
							}
						} 
					}
				}

				datumfactuurHelpSpan.text(datumFactuurtext);
				return result;
			};

			$scope.gebruikerAllowed = function() {
				if (typeof $window.sessionStorage.user !== 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;
	
					return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd') 
				} else
					return false;
			};

			$scope.meldingVerwijderd = function() {
				if(typeof $scope.melding !== 'undefined' && $scope.melding != null) {
					return !!($scope.melding.meldingstatusCode == 'DEL' || $scope.melding.meldingstatusCode == 'AFW');
				} else {
					return false;
				}
			};

			$scope.redenVerwijderen = function() {
				if(typeof $scope.melding !== 'undefined' && $scope.melding != null) {
					if(typeof $scope.melding.redenVerwijderenOmschrijving !== 'undefined' && $scope.melding.redenVerwijderenOmschrijving != null) {
						if($scope.melding.redenVerwijderenCode == 'AND') {
							return 'Onbekend';
						} else {
							return $scope.melding.redenVerwijderenOmschrijving;
						}
					}
					else {
						return 'Onbekend';
					}
				} else {
					return 'Onbekend';
				}

			};

			$scope.changeAdresCheck = function(field) {
				if($scope.controle.bedrijfsgegevensok == true &&
					$scope.controle.bedrijfsgegevensNietJuist == true) {
					if(field == 'selBedrijfAdresOk') {
						$scope.controle.bedrijfsgegevensNietJuist = false;
					} else {
						$scope.controle.bedrijfsgegevensok = false;
					}
				}

				$scope.mandatoryChecks();
			};

			if(typeof $scope.init === "undefined") {
				$scope.readonly = !!(typeof readonly !== 'undefined' && readonly == 'true' || readonly == true);

				$scope.wijziging = !!(typeof notificationidentifier !== 'undefined' && $scope.readonly == false);

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20; // 2
				$scope.maxSize = 5;
				$scope.newMeldingId = 0;

				$scope.formSubmitted = false;

				//delete $scope.error;
				//delete $scope.meldingVerwerkt;
				//delete $scope.meldingSavedOk;
				cleanMeldingForm();
				delete $scope.meldingen;

				if(typeof notificationidentifier === 'undefined') {
					$scope.controle = {
						bedrijfsgegevensok       : false,
						vorderingbedragok        : false,
						akkoord                  : true,
						bedrijfsgegevensNietJuist: false
					};

					$scope.meldingen = NotificationsToSend.getNotifications();
					if(typeof $scope.meldingen !== 'undefined' && $scope.meldingen.length > 0) {
						// for verzendlijst
						$scope.controle.vermeldingenJuist = false;
					}

					$scope.totalItems = $scope.meldingen.length;

					if($scope.totalItems == 0) {
						$scope.newMelding();
					}
					else {
						$scope.melding = {
							gebruikerAangemaaktId    : JSON.parse($window.sessionStorage.user).userId,
							bedrijfIdGerapporteerd   : $scope.owncompany.bedrijfId,
							bedrijfId                : $scope.company.bedrijfId,
							doorBedrijfWeergeven     : true,
							bedragWeergeven          : true,
							bedrijfsgegevensNietJuist: false,
							debiteurSommatieOntvangen: false,
							debiteurBetwistNiet      : false
						};
					}
				}
				else {
					$scope.controle = {
						bedrijfsgegevensok: true,
						vorderingbedragok : false,
						akkoord           : true
					};

					$scope.controle.bedrijfsgegevensok = true;

					var notificationData = CompanyService.companyNotificationData({
						meldingId: notificationidentifier,
						bedrijfId: companyidentifier
					});

					var resultsNotificationPromise = $q.all({
						notificationdata: notificationData.$promise
					}).then(function(data) {
						if(typeof data.notificationdata.errorCode !== "undefined") {
							$scope.error = data.errorCode + " " + data.errorMsg;
						} else {

							//data.notificationdata.doorBedrijfWeergeven = data.notificationdata.doorBedrijfWeergeven == 'true' ? true : false;
							//data.notificationdata.bedragWeergeven = data.notificationdata.bedragWeergeven == 'true' ? true : false;
							//data.notificationdata.bedrijfsgegevensNietJuist = data.notificationdata.bedrijfsgegevensNietJuist  == 'true' ? true : false;

							$scope.controle.bedrijfsgegevensNietJuist = data.notificationdata.bedrijfsgegevensNietJuist;
							if(data.notificationdata.bedrijfsgegevensNietJuist) {
								bedrijfgegevensJuistOnLoad = false;
							} else {
								$scope.bedrijfgegevensJuistOnLoad = true;
							}

							if(data.notificationdata.bedrag) {
								data.notificationdata.bedrag = Number(data.notificationdata.bedrag);
							}

							if((typeof $scope.readonly == 'undefined' || $scope.readonly == false) && !$scope.editExistingMelding) {
								// for form validation/editing
								if(data.notificationdata.datumFactuur) {
									data.notificationdata.datumFactuur = data.notificationdata.datumFactuur.replace(/-/g, '');
								}
							}

							// 500 check if (typeof data.notificationdata.bedrag == 'undefined' || data.notificationdata.bedrag == null || (data.notificationdata.bedrag && data.notificationdata.bedrag >= 500))
							//	$scope.controle.vorderingbedragok = true;

							data.notificationdata.debiteurSommatieOntvangen = true;
							data.notificationdata.debiteurBetwistNiet = true;

							$scope.melding = data.notificationdata;

							$scope.mastermelding = angular.copy($scope.melding);

							$scope.mandatoryChecks();

							if(typeof $scope.melding !== 'undefined' && typeof $scope.melding.gebruikerAangemaaktId !== 'undefined') {
								var gebruikerLaatsteMutatie = 'null';
								var gebruikerVerwijderd = 'null';
								var gebruikerGeaccordeerd = 'null';
								var datumLaatsteMutatie = 'null';
								var datumAfgewezen = 'null';
								var datumVerwijderd = 'null';
								var datumGeaccordeerd = 'null';
								if(typeof $scope.melding.gebruikerLaatsteMutatieId !== 'undefined') {
									gebruikerLaatsteMutatie = $scope.melding.gebruikerLaatsteMutatieId;
								}
								if(typeof $scope.melding.gebruikerVerwijderdId !== 'undefined') {
									gebruikerVerwijderd = $scope.melding.gebruikerVerwijderdId;
								}
								if(typeof $scope.melding.gebruikerGeaccordeerdId !== 'undefined') {
									gebruikerGeaccordeerd = $scope.melding.gebruikerGeaccordeerdId;
								}
								if(typeof $scope.melding.datumLaatsteMutatie !== 'undefined') {
									datumLaatsteMutatie = $scope.melding.datumLaatsteMutatie;
								}
								if(typeof $scope.melding.datumGeaccordeerd !== 'undefined') {
									datumGeaccordeerd = $scope.melding.datumGeaccordeerd;
								}
								if(typeof $scope.melding.datumVerwijderd !== 'undefined' && $scope.melding.meldingstatusCode == 'AFW') {
									datumAfgewezen = $scope.melding.datumVerwijderd;
								}
								if(typeof $scope.melding.datumVerwijderd !== 'undefined' && $scope.melding.meldingstatusCode == 'DEL') {
									datumVerwijderd = $scope.melding.datumVerwijderd;
								}
								$scope.gebruikerIds = [$scope.melding.gebruikerAangemaaktId.toString(), gebruikerGeaccordeerd.toString(), gebruikerVerwijderd.toString(), gebruikerLaatsteMutatie.toString()];
								var gebruikerIdsJson = {gebruikerIds: $scope.gebruikerIds};
								var gebruikerData = GebruikersServicePath.gebruikerdata(gebruikerIdsJson);

								var resultsGebruikerPromise = $q.all({
									gebruikerdata: gebruikerData.$promise
								}).then(function(data) {
									if(typeof data.gebruikerdata.errorCode !== "undefined") {
										$scope.error = data.errorCode + " " + data.errorMsg;
									} else {
										$scope.gebruiker = data.gebruikerdata;

										$scope.acties = [{
											actie    : 'Ingediend',
											datum    : $scope.melding.datumAangemaakt,
											gebruiker: $scope.gebruiker[0].fullName,
											functie  : $scope.gebruiker[0].functie,
											afdeling : $scope.gebruiker[0].afdeling,
											email    : $scope.gebruiker[0].emailAdres
										}];
										i = 1;
										if(datumGeaccordeerd != 'null') {
											if(gebruikerGeaccordeerd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Geaccordeerd',
														datum    : $scope.melding.datumGeaccordeerd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Geaccordeerd',
													datum    : $scope.melding.datumGeaccordeerd,
													gebruiker: '-',
													functie  : '-',
													afdeling : '-',
													email    : '-'
												});
											}
										}
										if(datumVerwijderd != 'null') {
											if(gebruikerVerwijderd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Verwijderd',
														datum    : $scope.melding.datumVerwijderd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Verwijderd',
													datum    : $scope.melding.datumVerwijderd,
													gebruiker: 'CRZB',
													email    : '-'
												});
											}
										}
										if(datumAfgewezen != 'null') {
											if(gebruikerVerwijderd != 'null') {
												if($scope.gebruiker[i] != null) {
													$scope.acties.push({
														actie    : 'Afgewezen',
														datum    : $scope.melding.datumVerwijderd,
														gebruiker: $scope.gebruiker[i].fullName,
														functie  : $scope.gebruiker[i].functie,
														afdeling : $scope.gebruiker[i].afdeling,
														email    : $scope.gebruiker[i].emailAdres
													});
													i++;
												}
											} else {
												$scope.acties.push({
													actie    : 'Afgewezen',
													datum    : $scope.melding.datumVerwijderd,
													gebruiker: 'CRZB',
													email    : '-'
												});
											}
										}
										//if (datumLaatsteMutatie != 'null') {
										//	if (gebruikerLaatsteMutatie != 'null') {
										//		if($scope.gebruiker[i]!=null) {
										//			$scope.acties.push({
										//				actie: 'Laatst gewijzigd',
										//				datum: $scope.melding.datumLaatsteMutatie,
										//				gebruiker: $scope.gebruiker[i].fullName,
										//				functie: $scope.gebruiker[i].functie,
										//				afdeling: $scope.gebruiker[i].afdeling,
										//				email: $scope.gebruiker[i].emailAdres
										//			});
										//			i++;
										//		}
										//	} else
										//		$scope.acties.push({
										//			actie: 'Laatst gewijzigd',
										//			datum: $scope.melding.datumLaatsteMutatie,
										//			gebruiker: 'crzb',
										//			functie: '-',
										//			afdeling: '-',
										//			email: '-'
										//		});
										//}
									}
								}).catch(function(error) {
									0;
									$scope.error = 'Er is een fout opgetreden.';
								});
							}
							
							$scope.contactwijzen = [
								{id: 'Telefoon', description: 'Telefoon' + (($scope.melding.telefoonNummerDebiteur !== 'undefined' && $scope.melding.telefoonNummerDebiteur != null) ? ' | ' + $scope.melding.telefoonNummerDebiteur : '')},
								{id: 'Email', description: 'Email' + (($scope.melding.emailAdresDebiteur !== 'undefined' && $scope.melding.emailAdresDebiteur != null) ? ' | ' + $scope.melding.emailAdresDebiteur : '')},
								{id: 'Brief', description: 'Brief | ' + $scope.melding.adres},
								{id: 'Aangetekend', description: 'Aangetekend | ' + $scope.melding.adres},
								{id: 'Persoonlijk', description: 'Persoonlijk'},
								{id: 'Anders', description: 'Anders'}
							];
							
							$scope.contactMomentFilterCriteria = {
									userId     : JSON.parse($window.sessionStorage.user).userId,
									meldingId  : $scope.melding.meldingId,
									pageNumber : 1,
									sortDir    : '', // asc, desc
									sortedBy   : '',
									filterValue: '' // text to filter on
								};
							
							$scope.contactMomentCurrentPage = 1;
							$scope.contactMomentItemsPage = 10;
							$scope.contactMomentMaxSize = 5;
							$scope.contactMomentTotalItems = 0;

							if ($scope.gebruikerAllowed()) {
								CompanyService.getAdminNoteOfNotification({meldingId : $scope.melding.meldingId}, function(response) {
									if (typeof response !== 'undefined') {
										if (typeof response.errorCode !== 'undefined') {
											$scope.error = response.errorMsg;
										} else {
											if (response != null) {
												$scope.meldingNotitiesAdmin = response.notitie;
												$scope.masterMeldingNotitiesAdmin = $scope.meldingNotitiesAdmin;
											}
										}
									}
								});
							}
							
							$scope.fetchContactMomenten(function(){});								
						}
					}).catch(function(error) {
						0;
						$scope.error = 'Er is een fout opgetreden.';
					});
									

					//$scope.melding = CompanyService.companyNotificationData({
					//	meldingId: notificationidentifier,
					//	bedrijfId: companyidentifier
					//}, function (data) {
					//	if (typeof data.errorCode !== "undefined") {
					//		$scope.error = data.errorCode + " " + data.errorMsg;
					//	} else {
					//
					//		data.doorBedrijfWeergeven = data.doorBedrijfWeergeven == 'true' ? true : false;
					//		data.bedragWeergeven = data.bedragWeergeven == 'true' ? true : false;
					//
					//		data.debiteurSommatieOntvangen = false,
					//			data.debiteurBetwistNiet = false
					//
					//		return data;
					//	}
					//});

				}

				$scope.formSubmitted = false;
				
				$scope.beantwoordTypes = [
					{id: 'Ja', description: 'Ja'},
					{id: 'Nee', description: 'Nee'},
					{id: 'Terugbellen', description: 'Terugbellen'},
					{id: 'BuitenGebruik', description: 'Buiten gebruik'},
					{id: 'Nvt', description: 'n.v.t.'},
					{id: 'Bezwaar', description: 'Bezwaar'},
					{id: 'Anders', description: 'Anders'}
				];
				
				$scope.EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
				$scope.PHONE_REGEXP = /(^\+[0-9]{2}|^\+[0-9]{2}\(0\)|^\(\+[0-9]{2}\)\(0\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\-\s]{10}$)/;				
				
				var dateNow = new Date();
				
				$scope.initTimePicker(dateNow);
				
				$scope.contactMoment = {
						datumContact : $filter('date')(dateNow, $scope.dateFormat),
						timeContact : $filter('date')(dateNow, $scope.timeFormat),
						//contactwijze : "",
						//contactGegevens : "",
						//beantwoord : "",
						//datumContactTerug : $filter('date')(dateNow, $scope.dateFormat),
						//notitie : "",
						//notitieIntern : ""
				};
				$scope.timeContact = dateNow;
				
				$scope.meldingVerwerkt = false;
				
				$scope.init = true;
			}

			// fetch initial data for 1st time
			$scope.filterResult();

			$scope.createDocumentRequested = function (meldingId) {
				return {action: 'letter_notification', bedrijfId: 0, meldingId: meldingId, referentie: ''};
			};
			
			$scope.annulerenall = function() {

				if($scope.totalItems > 0) {
					var modalInstance = $modal.open({
						templateUrl: 'removeallnotifications.html',
						controller : RemoveAllNotificationsController,
						size       : 'lg'
						//resolve: { removeNotification: removeNotification() }
					});

					modalInstance.result.then(function(result) {
						if(result.remove) {
							$scope.back();
						}
					}, function() {
						0;
					});

				}
				else {
					$scope.back();
				}
			};

			var RemoveAllNotificationsController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeAllNotificationsOk = function(reason) {
					$scope.remove = true;
					$scope.closeRemoveAllNotificationsModal();
				};

				$scope.closeRemoveAllNotificationsModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			$scope.annuleren = function(terug) {

				if(!$scope.meldingVerwerkt && !$scope.meldingSavedOk && 
						($scope.formmeldinggegevens.$dirty || $scope.formmeldingnotes.$dirty || $scope.formmeldingnotesadmin.$dirty)) {
					var modalInstance = $modal.open({
						templateUrl: 'removechanges.html',
						controller : RemoveChangesController,
						size       : 'lg'
						//resolve: { removeNotification: removeNotification() }
					});

					modalInstance.result.then(function(result) {
						if(result.remove) {
							if(terug) {
								$scope.back();
							} else {
								$scope.cancelMeldingDetails();
							}
						}
					}, function() {
						0;
					});
				} else {
					if(terug) {
						$scope.back();
					} else {
						$scope.cancelMeldingDetails();
					}
				}

			};

			var RemoveChangesController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeChangesOk = function(reason) {
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

			var RemoveNotificationController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeNotificationOk = function(reason) {
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function() {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];
		}]);appcontrollers.controller('ObjectionsAdminTabController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', '$timeout', 'maxFieldLengths', 'ngTableParams',
        'DashboardNumberTags', 'DashboardService', 'AccountService', 'CompanyService',
        function ($window, $scope, $rootScope, $location, $filter, $timeout, maxFieldLengths, ngTableParams,
                  DashboardNumberTags, DashboardService, AccountService, CompanyService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            $scope.title = "Bezwaren Tab";

            AccountService.getCompanyAccountData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId}, function (data) {
                $scope.account = data;
            });

            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPage = 20;
            $scope.maxSize = 5;
            $scope.thisUser = JSON.parse($window.sessionStorage.user);

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: "",
                    filterValue: "" // text to filter on
                };
            }

            $scope.$watch('filterCriteria.filterValue', function (search) {
                if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

                searchCallbackTimeout = $timeout(function () {
                    if (search) {
                        if (search.length > 2) {
                            // refresh
                            $scope.currentPage = 1;
                            $scope.pageChanged();
                            $scope.oldSearch = search;
                        }
                    }
                    else if (isSearchChanged(search)) {
                        // refresh
                        $scope.currentPage = 1;
                        $scope.pageChanged();
                        $scope.oldSearch = search;
                    }
                }, 1000);
            }, true);

            // The function that is responsible of fetching the result from the
            // server and setting the grid to the new result
            $scope.fetchResult = function (success) {
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                DashboardService.objectionsadmin($scope.filterCriteria, function (data, headers) {
                    if (typeof data != 'undefined' && data.length > 0) {
                        $scope.objectionsAdminCollection = data;
                        
                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    }
                    else {
                        $scope.objectionsAdminCollection = null;
                        $scope.totalItems = 0;
                    }
                    DashboardNumberTags.setNrOfObjectionsAdmin($scope.totalItems);

                    success();
                }, function () {
                    $scope.objectionsAdminCollection = [];
                    $scope.totalItems = 0;
                    DashboardNumberTags.setNrOfObjectionsAdmin($scope.totalItems);
                });
            };

            // Data table functions
            $scope.filterResult = function () {
                return $scope.fetchResult(function () {
                    //The request fires correctly but sometimes the ui doesn't update, that's a fix
                    $scope.filterCriteria.pageNumber = 1;
                    $scope.currentPage = 1;

                    // rootscope range delete
                    delete $rootScope.range;

                });
            };

            $scope.hasItems = function () {
                return DashboardNumberTags.getNrOfObjectionsAdmin() > 0;
            };

            $scope.ignorealert = function (type, alertId, bedrijfId) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                } else {
                    deleteAlert(alertId);
                }
            };

            $scope.pageChanged = function () {
                $scope.filterCriteria.pageNumber = $scope.currentPage;
                $scope.fetchResult(function () {
                    //Nothing to do..

                    // rootscope range delete
                    delete $rootScope.range;
                });
            };

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

			$scope.viewAdminDetails = function(refNoPRefix){
				supportObjectionDetail(refNoPRefix);
			};

            $scope.viewdetails = function (type, alertId, monitoringId, meldingId, supportId, bedrijfId, refNoPrefix) {
            	if (type == 'SUP') {
					deleteAlert(alertId);
                    supportObjectionDetail(refNoPrefix);
                }
            };

			deleteAlert = function (alertId) {
				// remove alert record
				CompanyService.deleteAlert(alertId, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};


			supportObjectionDetail = function (refNoPrefix) {
				delete $scope.error;
				$location.path('/support/detail/' + refNoPrefix+'/$dashboard$objectionsadmintab');
				$location.url($location.path());
			};

			
			$scope.lowercaseFirstLetter = function (string) {
			    return string.charAt(0).toLowerCase() + string.slice(1);
			};

			// fetch initial data for 1st time
			$scope.filterResult();
        }]);appcontrollers.controller('OverviewTabController', ['$scope', '$rootScope', '$filter', 'ngTableParams', 'DashboardService', function($scope, $rootScope, $filter, ngTableParams, DashboardService) {
	//var myColors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"];
	var myColors = ["#1f77b4", "#064877", "#65a4d1", "#085e9a", "#3f8abf", "#043a60", "#266ea1", "#012844", "#095891", "#6aa2c9"];
	d3.scale.myColors = function() {
		return d3.scale.ordinal().range(myColors);
	};

	$scope.nrOfItems2 = 7;

	$scope.piechartoptions = {
		chart: {
			type     : 'pieChart',
			height   : 350,
			x        : function(d) {
				return d.key;
			},
			y        : function(d) {
				return d.y;
			},
			labelType: 'percent'
		}
	};

	$scope.piechartexampleData = [{key: "One", y: 5},
		{key: "Two", y: 2},
		{key: "Three", y: 9},
		{key: "Four", y: 7},
		{key: "Five", y: 4},
		{key: "Six", y: 3},
		{key: "Seven", y: 9}
	];

	nv.addGraph(function() {
		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			.y(function(d) {
				return d.y
			})
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height);

		d3.select("#test1")
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		//         chart.color(function(d) {
		//        	 return "#1f77b4";
		//         });

		chart.dispatch.on('stateChange', function(e) {
			nv.log('New State:', JSON.stringify(e));
		});

		return chart;
	});

	nv.addGraph(function() {
		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			.y(function(d) {
				return d.y
			})
			//.color(d3.scale.category10().range())
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height);

		d3.select("#test3")
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		chart.dispatch.on('stateChange', function(e) {
			nv.log('New State:', JSON.stringify(e));
		});

		return chart;
	});

	nv.addGraph(function() {

		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			//.y(function(d) { return d.value })
			//.labelThreshold(.08)
			//.showLabels(false)
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height)
			.donut(true);

		chart.pie
			.startAngle(function(d) {
				return d.startAngle / 2 - Math.PI / 2
			})
			.endAngle(function(d) {
				return d.endAngle / 2 - Math.PI / 2
			});

		//chart.pie.donutLabelsOutside(true).donut(true);

		d3.select("#test2")
			//.datum(historicalBarChart)
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		return chart;
	});

	$scope.xFunction = function() {
		return function(d) {
			return d.key;
		};
	};
	$scope.yFunction = function() {
		return function(d) {
			return d.y;
		};
	};

	$scope.descriptionFunction = function() {
		return function(d) {
			return d.key;
		}
	};

	var data = [{
		shipmentId        : 100,
		htmlUrl           : "/edit/100",
		nameCreator       : "L.T. Late",
		nameLastEditor    : "P.W. Herman",
		exportOrganisation: "WijVerzendenAlles B.V.",
		importOrganisation: "Qatar Juwels",
		description       : "10k watches",
		dateShipment      : "30-06-2014",
		dateCreated       : "10-06-2014",
		dateLastChange    : "15-06-2014",
		documents         : [{
			documentId      : 123,
			htmlUrl         : "/document/123",
			documentType    : "CVO",
			documentStatus  : "Application",
			dateCreated     : "10-06-2014",
			personLastChange: "M. op den Oever",
			indEditable     : true
		},
			{
				documentId      : 124,
				htmlUrl         : "/document/124",
				documentType    : "Attachment",
				documentStatus  : "Uploaded",
				dateCreated     : "14-06-2014",
				personLastChange: "M. op den Oever",
				indEditable     : true
			}]
	},
		{
			shipmentId        : 101,
			htmlUrl           : "/edit/101",
			nameCreator       : "O. Lovely",
			nameLastEditor    : "P.W. Herman",
			exportOrganisation: "Tulip B.V.",
			importOrganisation: "Flowers from Russia",
			description       : "10k watches",
			dateShipment      : "30-06-2014",
			dateCreated       : "10-06-2014",
			dateLastChange    : "15-06-2014",
			documents         : [{
				documentId      : 125,
				htmlUrl         : "/document/125",
				documentType    : "CVO",
				documentStatus  : "Application",
				dateCreated     : "10-06-2014",
				personLastChange: "M. op den Oever",
				indEditable     : true
			},
				{
					documentId      : 126,
					htmlUrl         : "/document/126",
					documentType    : "Attachment",
					documentStatus  : "Uploaded",
					dateCreated     : "14-06-2014",
					personLastChange: "M. op den Oever",
					indEditable     : true
				}]
		}];

	$scope.tableParams = new ngTableParams({
		page : 1,            // show first page
		count: 10           // count per page
	}, {
		total  : data.length, // length of data
		getData: function($defer, params) {
			$defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		}
	});

}]);appcontrollers.controller('PortfolioMonitoringTabController',
	['$window', '$modal', '$scope', '$rootScope', 'maxFieldLengths',
		'$location', '$filter', '$timeout', 'ngTableParams',
		'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, maxFieldLengths,
				  $location, $filter, $timeout, ngTableParams,
				  DashboardNumberTags, DashboardService, CompanyService) {

			$scope.title = "Portfolio Monitoring Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
			};

			$scope.documentRequested = {
				action: 'monitored_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			if (typeof $scope.filterCriteria == 'undefined') {

				$scope.sortedOrder = true;
				$scope.reverseSort = true;
				$scope.orderByField = 'aantalMeldingen';

				$scope.filterCriteria = {
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: 'DESC', // asc, desc
					sortedBy: 'aantalMeldingen',
					filterValue: '' // text to filter on
				};
			}
			0;

			$scope.nrOfItems = function () {
				return 7;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfPortfolio() > 0;
			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.portfoliomonitoring($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.companies = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.companies = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfPortfolio($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					0;
					$scope.companies = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfPortfolio($scope.totalItems);
					$scope.firstfetch = true;
				});

			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};
			
			$scope.gebruikerRegistratiesAllowed = function() {
				if (typeof $window.sessionStorage.user !== 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;
	
					return hasRole(roles, 'registraties_toegestaan') || hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
				} else
					return false;
			};

			$scope.notifyCompany = function (bedrijfId) {
				0;

				if (!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO))
					$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/$dashboard$portfoliomonitoringtab');
				else
					$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/$dashboard$portfoliomonitoringtab');
			};

			$scope.monitoringDetail = function (monitoringId) {
				$location.path('/monitoringdetails/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + monitoringId + '/$dashboard$portfoliomonitoringtab');
			};

			removeMonitoring = function (monitoringId, bedrijfId) {
				CompanyService.removeMonitoringCompany({
					monitoringId: monitoringId,
					bedrijfId: bedrijfId
				}, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.removeFromMonitoring = function (monitoringId, bedrijfId) {

				var modalInstance = $modal.open({
					templateUrl: 'removemonitoring.html',
					controller: RemoveMonitoringController,
					size: 'lg'
					//resolve: { removeNotification: removeNotification() }
				});

				modalInstance.result.then(function (result) {
					if (result.remove)
						removeMonitoring(monitoringId, bedrijfId);
				}, function () {
					0;
				});
			};

			$scope.fetchMonitoring = function(field){
				$scope.orderByField = field;
				$scope.reverseSort = !$scope.reverseSort;

				if ($scope.filterCriteria.sortDir == 'ASC')
					$scope.filterCriteria.sortDir = 'DESC';
				else
					$scope.filterCriteria.sortDir = 'ASC';

				$scope.filterCriteria.sortedBy = $scope.orderByField;
				$scope.filterCriteria.sortderOrder = $scope.reverseSort;

				$scope.currentPage = 1;
				$scope.pageChanged();
			};

			var RemoveMonitoringController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeMonitoringOk = function () {
					$scope.remove = true;
					$scope.closeRemoveMonitoringModal();
				};

				$scope.closeRemoveMonitoringModal = function () {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);appcontrollers.controller('ProcessesAdminTabController',
	['$window', '$modal', '$scope', '$rootScope', '$location', '$filter',
		'$timeout', 'ngTableParams', 'DashboardNumberTags', 'DashboardService', 'InternalProcessService',
		function ($window, $modal, $scope, $rootScope, $location, $filter,
				  $timeout, ngTableParams, DashboardNumberTags, DashboardService, InternalProcessService) {

			$scope.title = "Internal processes Tab";

			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.createBatchDocument = function(processRowId){
				delete $scope.error;
				return {
					action: 'letter_batch',
					batchDocumentId: processRowId
				};
			};

			$scope.fetchData = function () {
				$scope.processes = [];
				
				$rootScope.range = "items=" + (($scope.currentPage - 1) * $scope.itemsPage + 1) + "-" + (($scope.currentPage - 1) * $scope.itemsPage + $scope.itemsPage);

				InternalProcessService.getInternalProcessRows(function (data, headers) {
					if (data.errorCode != null) {
						$scope.error = data.errorCode + ' ' + data.errorMsg;
					} else {
						if(data.length>0){
							$scope.processes = data;
							$scope.totalItems = paging_totalItems(headers("Content-Range"));

							$scope.noData = false;
						} else {
							$scope.totalItems = 0;
							$scope.noData = true;
						}
					}
					DashboardNumberTags.setNrOfProcesses($scope.totalItems);
				});
			};

			var promptProcessLetterController = ['$scope', '$modalInstance', 'briefOrBatch', function ($scope, $modalInstance, briefOrBatch) {
				$scope.continue = false;
				$scope.briefOrBatch = briefOrBatch;

				$scope.sendLetter = function () {
					$scope.continue = true;
					$scope.closeLetterPrompt();
				};

				$scope.closeLetterPrompt = function () {
					var result = {continue: $scope.continue};
					$modalInstance.close(result);
				};
			}];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfProcesses() > 0;
			};
			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.pageChanged = function () {
				delete $scope.error;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchData(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.fetchData();

			$scope.promptSendBatch = function(id){
				delete $scope.error;
				var modalInstance = $modal.open({
					templateUrl: 'processLetter.html',
					controller: promptProcessLetterController,
					size:'lg',
					resolve:{briefOrBatch:function(){return 'batch';}}
				});

				modalInstance.result.then(function(result){
					if(result.continue){
						processRow(id);
					}
				})
			};

			$scope.promptSendLetter = function (id) {
				delete $scope.error;
				var modalInstance = $modal.open({
					templateUrl: 'processLetter.html',
					controller: promptProcessLetterController,
					size: 'lg',
					resolve:{briefOrBatch:function(){return 'brief';}}
				});

				modalInstance.result.then(function (result) {
					if (result.continue) {
						processRow(id);
					}
				});
			};

			processRow = function (id) {
				InternalProcessService.setProcessRowAsSent({processRowId: id, gebruikerId: JSON.parse($window.sessionStorage.user).userId}, function (data) {
					if (data.errorCode != null) {
						$scope.error = data.errorCode + ' ' + data.errorMsg;
					} else {
						$scope.fetchData();
					}
				})
			};
		}]);appcontrollers.controller('ReportController',
		['$window', '$modal', '$route', '$routeParams', '$scope', '$rootScope',
		'$location', '$anchorScroll', '$filter', '$q', 'reportData',
		'monitorRequestData', 'ReportDataStorage',
		'CompanyService', 'DocumentService', 'cfpLoadingBar', 
		function($window, $modal, $route, $routeParams, $scope, $rootScope,
				 $location, $anchorScroll, $filter, $q, reportData,
				 monitorRequestData, ReportDataStorage,
				 CompanyService, DocumentService, cfpLoadingBar)			 {
			
			$scope.start = function() {
				cfpLoadingBar.start();
			};
			
			$scope.complete = function() {
				cfpLoadingBar.complete();
			};

			// report
			if($scope.init == undefined) {
				$scope.hideMonitorPopup = true; //!$rootScope.isShowHelp($rootScope.HELP_MONITORINGPURCHASE);

				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});

				if($routeParams.searchurl) {
					searchurl = $routeParams.searchurl;
				} else {
					searchurl = '$dashboard';
				}

				CompanyService.bedrijfHasMonitor({doorBedrijfId:JSON.parse($window.sessionStorage.user).bedrijfId, overBedrijfId:reportData.bedrijf.bedrijfId}, function(result){
					if(typeof result.errorCode != 'undefined'){
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.monitoringBijBedrijf = result.val;
					}
				});

				//if(typeof monitoringBijBedrijf != 'undefined') {
				//	$scope.monitoringBijBedrijf = monitoringBijBedrijf;
				//} else {
				//	$scope.monitoringBijBedrijf = false;
				//}

				if($scope.report && $scope.report.meldingen) {
					//if (Object.prototype.toString.call( $scope.report.meldingen ) === '[object Array]')
					$scope.meldingenOverview = $scope.report.meldingen;
					//else
					//	$scope.meldingenOverview = [$scope.report.meldingen];
				} else {
					delete $scope.meldingenOverview;
				}

				$scope.documentRequested = {
					action    : 'report',
					bedrijfId : reportData.bedrijf.bedrijfId,
					meldingId : 0,
					referentie: reportData.referentieNummer
				};

				$scope.report = reportData;

				ReportDataStorage.setReportData($scope.report);
				
				if ($scope.report.ratingScore > 0) {
					$scope.showRating = true;
					$scope.ratingScoreIndicatorMessage = $scope.report.ratingScoreIndicatorMessage;
					
					if ($scope.report.ratingScore <= 30)
						$scope.ratingDanger = "ratingDangerSelected";
					else
						$scope.ratingDanger = "ratingDanger";
					if ($scope.report.ratingScore > 30 && $scope.report.ratingScore <= 45)
						$scope.ratingWarning = "ratingWarningSelected";
					else
						$scope.ratingWarning = "ratingWarning";
					if ($scope.report.ratingScore > 45 && $scope.report.ratingScore <= 79)
						$scope.ratingGood = "ratingGoodSelected";
					else
						$scope.ratingGood = "ratingGood";
					if ($scope.report.ratingScore > 79 && $scope.report.ratingScore <= 100)
						$scope.ratingVeryGood = "ratingVeryGoodSelected";
					else
						$scope.ratingVeryGood = "ratingVeryGood";
				}
				else
					$scope.showRating = false;
				

				//CompanyService.search({bedrijfId: userBedrijfId, pageNumber: 1,searchValue: $scope.report.kvkDossierTransfer.parentKvKNummer, city: ""}, function(result){
				//	if(result.errorCode!=null){
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	} else {
				//		$scope.parent = result[0];
				//	}
				//});
				//
				//CompanyService.search({bedrijfId: userBedrijfId, pageNumber: 1,searchValue: $scope.report.kvkDossierTransfer.ultimateParentKvKNummer, city: ""}, function(result){
				//	if(result.errorCode!=null){
				//		$scope.error = result.errorCode + " " + result.errorMsg;
				//	} else {
				//		$scope.ultimateParent = result[0];
				//	}
				//});
				
				document.getElementById("rating").innerHTML=$scope.report.ratingScore

				$scope.init = true;
			}

			var NoMonitoringController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.remove = false;
				$scope.monitoring = false;
				$scope.closeReport = false;

				$scope.noMonitoringOk = function() {
					$scope.monitoring = false;
					$scope.closeReport = true;
					$scope.closeNoMonitoringModal();
				};

				$scope.closeNoMonitoringModal = function() {
					var result = {
						monitoring : $scope.monitoring,
						closeReport: $scope.closeReport

					};

					$modalInstance.close(result); // $scope.remove
				};

				$scope.closeAndMonitoringOk = function() {
					$scope.monitoring = true;
					$scope.closeReport = true;
					$scope.closeNoMonitoringModal();
				}
			}];

			var monitorNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeMonitorPopup = function() {
					$modalInstance.close();
				}
			}];

			var monitorPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doMonitor = false;

				//CompanyService.getTariefOfProduct({ProductCode: 'MON'}, function(result) {
				//	if(result.errorCode == undefined) {
				//		$scope.monitorCost = result.tarief;
				//	}
				//});

				$scope.createMonitor = function() {
					$scope.doMonitor = true;
					$scope.closeMonitorPopup();
				};

				$scope.closeMonitorPopup = function() {
					var result = {
						doMonitor: $scope.doMonitor
					};

					$modalInstance.close(result);
				}
			}];

			////if (typeof reportData.opmerkingen !== 'undefined' && Object.prototype.toString.call( reportData.opmerkingen ) !== '[object Array]')
			//reportData.opmerkingen = reportData.opmerkingen;
			////if (typeof reportData.historie !== 'undefined' && Object.prototype.toString.call( reportData.historie ) !== '[object Array]')
			//reportData.historie = reportData.historie;
			////if (typeof reportData.meldingen !== 'undefined' && Object.prototype.toString.call( reportData.meldingen ) !== '[object Array]')
			//reportData.meldingen = reportData.meldingen;
			////if (typeof reportData.reportsLastTwoWeeks !== 'undefined' && Object.prototype.toString.call( reportData.reportsLastTwoWeeks ) !== '[object Array]')
			//reportData.reportsLastTwoWeeks = reportData.reportsLastTwoWeeks;
			////if (typeof reportData.meldingenLastYear !== 'undefined' && Object.prototype.toString.call( reportData.meldingenLastYear ) !== '[object Array]')
			//reportData.meldingenLastYear = reportData.meldingenLastYear;

			$scope.aantalMeldingen = function() {
				return $scope.report.aantalMeldingenActief;
			};

//			$scope.aantalMeldingenResolved = function() {
//				return $scope.report.aantalMeldingenResolved;
//			};

			$scope.back = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			$scope.monitoring = function(goback) {
				var monitorModal = $modal.open({
					templateUrl: 'monitorPopup.html',
					controller : monitorPopupController,
					size       : 'lg'
				});

				monitorModal.result.then(function(result) {
					if(result.doMonitor) {
						if(false) { // !$scope.userCanPay turned off
							var monitorNoPaymentModal = $modal.open({
								templateUrl: 'monitorNoPayment.html',
								controller : monitorNoPaymentController,
								size       : 'lg'
							});

							monitorNoPaymentModal.result.then(function(result) {
								if(typeof goback !== 'undefined' && goback) {
									$scope.back();
								}
							})
						} else {
							makeMonitor();

							if(typeof goback !== 'undefined' && goback) {
								$scope.back();
							}
						}
					}
				});
			};

			$scope.backWithNoMonitoringQuestion = function() {

				if($scope.showMonitoring()) {
					var modalInstance = $modal.open({
						templateUrl: 'nomonitoring.html',
						controller : NoMonitoringController,
						size       : 'lg'
					});

					modalInstance.result.then(function(result) {
						//dummy TESTSTS
						//$scope.hideMonitorPopup = false;
						//$scope.userCanPay = true;

						if(result.monitoring) {
							$scope.monitoring(result.closeReport);
						} else if(result.closeReport) {
							$scope.back();
						}

					}, function() {
						0;
					});
				} else {
					$scope.back();
				}
			};

			$scope.bedragOpenstaand = function() {
				if(typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null) {
					return '-';
				} else {
					return $filter('currency')($scope.report.bedragOpenstaand);
				} //'\u20AC  ' + $scope.report.bedragOpenstaand | currency;
			};

			$scope.bedragResolved = function() {
				if(typeof $scope.report.bedragResolved == 'undefined' || $scope.report.bedragResolved == null) {
					return '-';
				} else {
					return $filter('currency')($scope.report.bedragResolved);
				}
			};

			$scope.getBedragText = function(bedrag) {
				if(typeof bedrag == 'undefined' || bedrag == null || bedrag == 0) {
					return 'hoger dan \u20AC 100,-';
				} else {
					return '\u20AC ' + bedrag.toLocaleString('nl-NL', {style: 'currency', currency: 'EUR'});
				}//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.getReport = function(referentie) {
				$scope.start();
				DocumentService.report({referentie: referentie}, function(result) {
					//var file = new Blob([result], { type: 'application/pdf' });
					//var fileURL = URL.createObjectURL(file);
					//window.open(fileURL);
					
					$scope.$emit('downloaded', result);

					$scope.complete();
				});
			};

			$scope.hideCompanyInfo = function(bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.isBedragSuccess = function() {
				return !!(typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null);
			};

			$scope.isBedragWarning = function() {
				return !!(typeof $scope.report.bedragOpenstaand != 'undefined' && $scope.report.bedragOpenstaand != null && $scope.report.bedragOpenstaand > 0);
			};

			$scope.isStatusDanger = function() {
				return !!($scope.report.kvkDossierTransfer.actief == 'Niet actief' || $scope.report.kvkDossierTransfer.actief == '' ||
				$scope.report.kvkDossierTransfer.actief == 'Ontbonden' ||
				$scope.report.kvkDossierTransfer.actief == 'Opgeheven');
			};

			$scope.isStatusSuccess = function() {
				return $scope.report.kvkDossierTransfer.actief == 'Actief';
			};

			$scope.isStatusWarning = function() {
				return $scope.report.kvkDossierTransfer.actief == 'Uitgeschreven';
			};

			$scope.isFaillietSurceanceDanger = function() {
				return false;
			};

			$scope.isFaillietSurceanceSuccess = function() {
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Nee';
			};

			$scope.isFaillietSurceanceWarning = function() {
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Surseance';
			};

			$scope.isFaillietSurceanceDanger = function() {
				//return ($scope.report.kvkDossierTransfer.faillietSurseance == 'Surseance + Faillissement' ||
				//$scope.report.kvkDossierTransfer.faillietSurseance == 'Faillissement');
				return $scope.report.kvkDossierTransfer.faillietSurseance == 'Ja' || $scope.report.kvkDossierTransfer.faillietSurseance == ''
			};

			$scope.isNevenVestiging = function() {
				return !!(typeof $scope.report.bedrijfHoofd == 'undefined' || $scope.report.bedrijfHoofd == null);
			};

			$scope.meldingmaken = function() {
				$location.path('/createcustomnotification/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl) ); //+ '$' + !$scope.showMonitoring());
				$location.url($location.path());
			};

			$scope.notifyCompany = function() {
				0;

				if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
					$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl));
				} else {
					$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + reportData.bedrijf.bedrijfId + '/$report$' + JSON.parse($window.sessionStorage.user).bedrijfId + '$' + reportData.bedrijf.bedrijfId + '$' + reportData.referentieNummer + escapeSearchUrl(searchurl));
				}
				$location.url($location.path());
			};

			$scope.nrOfHistorie = function(histories) {
				var result = 0;

				if(typeof histories != 'undefined') {
					result = histories.length;
				}

				return result;
			};

			$scope.nrOfMeldingen = function(meldingen) {
				var result = 0;

				if(typeof meldingen != 'undefined') {
					result = meldingen.length;
				}

				return result;
			};
			
			$scope.isOwnCompany = function() {
				if (typeof reportData != 'undefined' && reportData.bedrijfaanvrager!=null && reportData.bedrijf!=null) {
					return reportData.bedrijfaanvrager.kvKnummer == reportData.bedrijf.kvKnummer;
				} else
					return false;
			};			

			$scope.showMonitoring = function() {
				if(($scope.monitorCreated != 'undefined' && $scope.monitorCreated != null && $scope.monitorCreated) || noMonitoringAllowed()) {
					return false;
				}

				return $scope.monitoringBijBedrijf == false;
			};

			makeMonitor = function() {
				delete $scope.error;
				delete $scope.monitorCreated;

				CompanyService.monitoringCompany(monitorRequestData, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					} else {
						$scope.monitorCreated = true;
					}
				});
			};

			noMonitoringAllowed = function() {
				if(typeof ($window.sessionStorage.user) != 'undefined') {
					var roles = JSON.parse($window.sessionStorage.user).roles;

					return hasRole(roles, 'admin_sbdr') || hasRole(roles, 'admin_sbdr_hoofd');
				} else {
					return false;
				}
			};

			saveShowMonitorPopup = function() {
				delete $scope.error;

				$rootScope.saveShowHelpDialog($rootScope.HELP_MONITORINGPURCHASE, $scope.hideMonitorPopup, function(result) {
					if(result.error != null) {
						$scope.error = result.error;
					}
				});
			};			
			
			// reportcompany
			$scope.report = ReportDataStorage.getReportData();

			$scope.getBedragText = function (bedrag) {
				if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
					return 'hoger dan \u20AC 100,-';
				else
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.getDatum = function (timestamp) {
				if (typeof timestamp != 'undefined' && timestamp != null)
					return timestamp;
				else
					return 'n.v.t.';
			};

			$scope.hideCompanyInfo = function (bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.nrOfHistorie = function (histories) {
				var result = 0;

				if (typeof histories != 'undefined')
					result = histories.length;

				return result;
			};

			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
			};
			
			// reportoverview
			$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

			$scope.nrOfMeldingen = function(meldingen) {
				var result = 0;

				if(typeof meldingen != 'undefined') {
					result = meldingen.length;
				}

				return result;
			};

			var maxY = 0;
			var maxRange = 0;

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function() {
					$modalInstance.close();
				}
			}];

			var reportPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doReport = false;

				CompanyService.getTariefOfProduct({ProductCode: 'RAP'}, function(result) {
					if(result.errorCode == undefined) {
						$scope.reportCost = result.tarief;
					}
				});

				$scope.createReport = function() {
					$scope.doReport = true;
					$scope.closeReportPopup();
				};

				$scope.closeReportPopup = function() {
					var result = {
						doReport: $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			if(typeof $scope.report.reportsLastTwoWeeks != 'undefined' && $scope.report.reportsLastTwoWeeks != null) {
				var values = [];

				for(var i in $scope.report.reportsLastTwoWeeks) {
					var item = [];
					item.push(Number(i));
					item.push(Number($scope.report.reportsLastTwoWeeks[i].aantal));
					values.push(item);

					if(item[1] > maxY) {
						maxY = item[1];
					}
				}

				$scope.exampleDataRapportAanvragen = [
					{
						"key"   : "Rapporten opgevraagd",
						"values": values
					}
				];

				maxRange = maxY / 5;
				maxRange++;
				maxRange = maxRange * 5;
			} else {
				$scope.exampleDataRapportAanvragen = [
					{
						"key"   : "Rapporten opgevraagd",
						"values": []
					}
				];
			}

			//$scope.getYTickValues = [0, maxRange];

			//$scope.getForceY = [0, 5];

			//$scope.getForceYmin = 0;
			//$scope.getForceYmax = 5;

			$scope.getBedragText = function(bedrag) {
				if(typeof bedrag == 'undefined' || bedrag == null || bedrag == 0) {
					return 'hoger dan \u20AC 100,-';
				} else {
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');
				}//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

			$scope.hideCompanyInfo = function(bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.nrOfHistorie = function(histories) {
				var result = 0;

				if(typeof histories != 'undefined') {
					result = histories.length;
				}

				return result;
			};
			
			$scope.reportHoofdVestiging = function() {
				var report = ReportDataStorage.getReportData();
				if (report !== 'undefined' && report != null &&
						report.bedrijfHoofd !== 'undefined' && report.bedrijfHoofd != null) {
					var hoofdKvK = report.bedrijfHoofd.kvKnummer;
					if(typeof report.bedrijfHoofd.subDossier != 'undefined') {
						hoofdKvK += report.bedrijfHoofd.subDossier;
					}
					ReportDataStorage.getReportData(hoofdKvK);
				}
				makeReport(report.bedrijfHoofd.kvKnummer);
			};

			$scope.createReport = function(isLink, kvkNr) {
				if(isLink) {
					makeReport(kvkNr);
					//requestReport(kvkNr);
				}
			};

			$scope.yAxisTickFormatFunctionReportRequests = function(d) {
				return function(d) {
					//0;
					return d3.round(d, 0);
				}
			};

			$scope.xAxisTickFormatFunctionReportRequests = function(d) {
				return function(d) {
					//0;
					return $scope.report.reportsLastTwoWeeks[d].label; //d3.time.format('%x')(new Date(d)); //uncomment for date format
				}
			};

			$scope.yAxisRangeFunctionReportRequests = function() {
				return [0, 5];
			};
			
			$scope.optionsReportRequests = {
		            chart: {
		            	showLegend: false,
		                type: 'lineChart',
		                height: 300,
		                margin : {left:50,top:25,bottom:25,right:50},
		                x: function(d){ return d[0]; },
		                y: function(d){ return d[1]; },
		                useInteractiveGuideline: false,		                
		                xAxis: {
		                    axisLabel: 'Dag',
		                    tickFormat: $scope.xAxisTickFormatFunctionReportRequests()
		                },
		                yAxis: {
		                    axisLabel: 'Aantal',
		                    tickFormat: $scope.yAxisTickFormatFunctionReportRequests()
		                },
		                lines: { // for line chart
		                    forceY: $scope.getForceY,
		                    yDomain: $scope.getYTickValues
		                },		                
		                callback: function(chart){
		                    0;
		                }
		            }					
			};			

			makeReport = function(kvkNummer) {
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
//						if(!$scope.userCanPay){
//							var reportNoPaymentModal = $modal.open({
//								templateUrl: 'reportNoPayment.html',
//								controller: reportNoPaymentController,
//								size:'lg'
//							});
//						} else {
							////var bedrijfAanvragerId = $route.current.params.bedrijfAanvragerId;

							CompanyService.getOrCreateCompanyData({kvknumber: kvkNummer}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								} else {
									$location.path('/report/' + $route.current.params.bedrijfAanvragerId + '/' + result.bedrijfId + '/0/' + searchurl);
									$location.url($location.path());
								}
							});
						//}
				//	}
				//});
			};

			if(typeof $scope.init == 'undefined'){
				CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
					if(typeof result.errorCode != 'undefined') {
						$scope.error = result.errorCode + " " + result.errorMsg;
					} else {
						$scope.userCanPay = result.val;
					}
				});
			}			
			
			// reportnotificationstab
			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
			};

			var maxY = 0;
			var maxRange = 0;
			if (typeof $scope.report.meldingenLastYear != 'undefined' && $scope.report.meldingenLastYear != null) {
				var values = [];

				for (var i in $scope.report.meldingenLastYear) {
					var item = [];
					item.push(Number(i));
					item.push(Number($scope.report.meldingenLastYear[i].aantal));
					values.push(item);

					if (item[1] > maxY)
						maxY = item[1];
				}

				$scope.exampleDataRegistratieOverzicht = [
					{
						"key": "Vermeldingen",
						"values": values
					}
				];

				maxRange = maxY / 5;
				maxRange++;
				maxRange = maxRange * 5;
			} else {
				$scope.exampleDataRegistratieOverzicht = [
					{
						"key": "Vermeldingen",
						"values": []
					}
				];
			}

			//$scope.getYTickValues = [0, maxRange];

			//$scope.getForceY = [0, 5];

			//$scope.getForceYmin = 0;
			//$scope.getForceYmax = 5;

			$scope.yAxisRangeFunctionNotifications = function () {
				return [0, 5];
			};

			$scope.xAxisTickFormatFunctionNotifications = function (d) {
				return function (d) {
					0;
					return $scope.report.meldingenLastYear[d].label; //d3.time.format('%x')(new Date(d)); //uncomment for date format
				}
			};

			$scope.yAxisTickFormatFunctionNotifications = function (d) {
				return function (d) {
					0;
					return d3.round(d, 0);
				}
			};
			
			$scope.optionsNotifications = {
		            chart: {
		            	showLegend: false,
		                type: 'lineChart',
		                height: 300,
		                margin : {left:50,top:25,bottom:25,right:50},
		                x: function(d){ return d[0]; },
		                y: function(d){ return d[1]; },
		                useInteractiveGuideline: false,		                
		                xAxis: {
		                    axisLabel: 'Dag',
		                    tickFormat: $scope.xAxisTickFormatFunctionNotifications()
		                },
		                yAxis: {
		                    axisLabel: 'Aantal',
		                    tickFormat: $scope.yAxisTickFormatFunctionNotifications()
		                },
		                lines: { // for line chart
		                    forceY: $scope.getForceY,
		                    yDomain: $scope.getYTickValues
		                },		                
		                callback: function(chart){
		                    0;
		                }
		            },
		            caption: {
		                enable: true,
		                html: '<span style="text-decoration: underline;">Let op</span>, genoemde aantallen omvatten niet de reeds opgeloste betalingsachterstanden.<br/>Deze worden definitief verwijderd uit het register en zullen niet worden weergegeven.',
		                css: {
		                    'text-align': 'justify',
		                    'margin': '10px 13px 0px 7px'
		                }
		            }					
			};

			checkIfExists = function (array, value) {
				for (var i in array) {
					if (array[i] == value)
						return true;
				}

				return false;
			};

			$scope.aantalMeldingDoorBedrijven = function () {
				return $scope.report.aantalCrediteuren;			
			};

			$scope.aantalMeldingen = function () {
				if (typeof $scope.report.meldingen != 'undefined' && $scope.report.meldingen != null)
					return $scope.report.meldingen.length;
				else
					return 0;
			};

			$scope.bedragOpenstaand = function () {
				if ($scope.aantalMeldingen() == 0 && (typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null))
					return 'Geen';
				else if (typeof $scope.report.bedragOpenstaand == 'undefined' || $scope.report.bedragOpenstaand == null)
					return 'bedrag afgeschermd';
				else
					return $filter('currency')($scope.report.bedragOpenstaand); 
				// + ' (exclusief afgeschermde bedragen)';  //'&eur; ' + $scope.report.bedragOpenstaand.toLocaleString('nl-NL');
			};

			$scope.bedragGesloten = function () {
				if (typeof $scope.report.bedragResolved == 'undefined' || $scope.report.bedragResolved == null)
					return 'afgeschermd';
				else
					return $filter('currency')($scope.report.bedragResolved);  //'&eur; ' + $scope.report.bedragOpenstaand.toLocaleString('nl-NL');
			};

			$scope.nrOfHistorie = function (histories) {
				var result = 0;

				if (typeof histories != 'undefined')
					result = histories.length;

				return result;
			};

			$scope.hideCompanyInfo = function (bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.getBedragText = function (bedrag) {
				if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
					return 'hoger dan \u20AC 100,-';
				else
					return $filter('currency')(bedrag); //'\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};			
	}]);appcontrollers.controller('ReportHistoryTabController',
	['$scope', 'ReportDataStorage',
		function ($scope, ReportDataStorage) {
			$scope.report = ReportDataStorage.getReportData();
			$scope.nrOfMeldingen = function (meldingen) {
				var result = 0;

				if (typeof meldingen != 'undefined')
					result = meldingen.length;

				return result;
			};

			$scope.nrOfHistorie = function (histories) {
				var result = 0;

				if (typeof histories != 'undefined')
					result = histories.length;

				return result;
			};

			$scope.hideCompanyInfo = function (bedrijfId) {
				return !!(typeof bedrijfId === 'undefined' || bedrijfId == null);
			};

			$scope.getBedragText = function (bedrag) {
				if (typeof bedrag == 'undefined' || bedrag == null || bedrag == 0)
					return 'hoger dan \u20AC 100,-';
				else
					return '\u20AC ' + bedrag.toLocaleString('nl-NL');//parseFloat(Math.round(bedrag * 100) / 100).toFixed(2);
			};

		}]);appcontrollers.controller('ReportRequestedTabController',
	['$window', '$modal', '$scope', '$rootScope', 'maxFieldLengths',
		'$location', '$filter', '$timeout', 'ngTableParams',
		'DashboardNumberTags', 'DashboardService', 'CompanyService',
		function ($window, $modal, $scope, $rootScope, maxFieldLengths,
				  $location, $filter, $timeout, ngTableParams,
				  DashboardNumberTags, DashboardService, CompanyService) {
			0;
			
			$scope.title = "Report Requested Tab";

			$scope.maxFieldLengths = maxFieldLengths;
			
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPage = 20;
			$scope.maxSize = 5;

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
			};

			0;

			$scope.createDocumentRequested = function (bedrijfId, referentieNummer) {
				return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope.pageChanged();
							$scope.oldSearch = search;
						}
					}
					else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			if (typeof $scope.filterCriteria == 'undefined') {

				$scope.filterCriteria = {
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					pageNumber: 1,
					sortDir: "", // asc, desc
					sortedBy: "",
					filterValue: "" // text to filter on
				};
			}
			0;

			$scope.nrOfItems = function () {
				return 0;
			};

			var companies = [];

			$scope.hasItems = function () {
				return DashboardNumberTags.getNrOfReportRequested() > 0;
			};

			// Data table functions
			$scope.filterResult = function () {

				return $scope.fetchResult(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};
			0;

			// The function that is responsible of fetching the result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.reportrequested($scope.filterCriteria, function (data, headers) {
					0;
					if (typeof data != 'undefined') {
						$scope.reports = data;

						$scope.totalItems = paging_totalItems(headers("Content-Range"));
					}
					else {
						$scope.reports = null;
						$scope.totalItems = 0;
					}

					DashboardNumberTags.setNrOfReportRequested($scope.totalItems);

					success();
					$scope.firstfetch = true;
				}, function () {
					0;
					$scope.reports = [];
					$scope.totalItems = 0;
					DashboardNumberTags.setNrOfReportRequested($scope.totalItems);
					$scope.firstfetch = true;
				});

			};
			0;

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					//Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.documentRequested = {
				action: 'reported_companies',
				bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
			};

			//$scope.documentRequested = function(bedrijfId, referentieNummer) {
			//  return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			//}

			// fetch initial data for 1st time
			$scope.filterResult();

		}]);appcontrollers.controller('ResetPasswordController', ['$window', '$scope', '$rootScope', '$location', 'maxFieldLengths', '$cookies', '$anchorScroll', '$routeParams', 'UserService', 'UserNsService', 'NewAccountService', 'clientip', function($window, $scope, $rootScope, $location, maxFieldLengths, $cookies, $anchorScroll, $routeParams, UserService, UserNsService, NewAccountService, clientip) {
	0;

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

}]);appcontrollers.controller('SearchCompanyController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', '$filter', 'ngTableParams', '$modal', 'maxFieldLengths',
		'cfpLoadingBar', 'companyData', 'clientip', 'CompanyService', 'NewAccountService',
		function($window, $routeParams, $scope, $rootScope,
				$location, $filter, ngTableParams, $modal, maxFieldLengths,
				 cfpLoadingBar, companyData, clientip, CompanyService, NewAccountService) {
		    
			// redirect if mobile
			//if ($rootScope.isMobileResolution()) {
			//    window.location.hef = "/informatie.php";
			//}		
			
			$scope.hasSearchResults = false;
			$scope.maxFieldLengths = maxFieldLengths;
			
			var allCompanies = [];

			var infoPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closePopup = function() {
					$modalInstance.close();
				}
			}];

			var reportNoPaymentController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.closeReportPopup = function(){
					$modalInstance.close();
				}
			}];

			var reportPopupController = ['$scope', '$modalInstance', function($scope, $modalInstance) {
				$scope.doReport = false;

				CompanyService.getTariefOfProduct({ProductCode: 'RAP'}, function(result) {
					if(result.errorCode == undefined) {
						$scope.reportCost = result.tarief;
					}
				});

				$scope.createReport = function() {
					$scope.doReport = true;
					$scope.closeReportPopup();
				};

				$scope.closeReportPopup = function() {
					var result = {
						doReport : $scope.doReport
					};

					$modalInstance.close(result);
				}
			}];

			var searchurl = '';

			var userBedrijfId = undefined;
			if($window.sessionStorage.user) {
				userBedrijfId = JSON.parse($window.sessionStorage.user).bedrijfId || undefined;
				$rootScope.content_id = 'content';
			} else {
				userBedrijfId = undefined;
				$rootScope.content_id = 'nscontent';
			}

			$scope.complete = function() {
				cfpLoadingBar.complete();
			};

			$scope.showInfoPopup = function() {
				var infoModal = $modal.open({
					templateUrl: 'searchInfoPopup.html',
					controller : infoPopupController,
					size       : 'lg'
				});
			};

			$scope.createaccount = function(kvknumber, hoofdvestiging, subdossier) {
				resetFields();
				0;

				if(typeof subdossier != 'undefined') {
					kvknumber += subdossier;
				}
				
				$location.path('/createaccount/' + kvknumber + '/' + hoofdvestiging + '/' + searchurl);
			};

			$scope.fetchResult = function(success) {
				resetFields();
				//$httpProvider.defaults.headers.put['Range'] = $scope.range;
				$rootScope.range = "items=" + (($scope.searchcompany.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.searchcompany.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

				if($window.sessionStorage.user !== undefined) {
					CompanyService.search($scope.searchcompany, function(data, headers) {
						$scope.totalItems = paging_totalItems(headers("Content-Range"));
						0;
						0;
						// The request results are managed client side so store all results in overall object
						$scope.allCompanies = data;
						success();
					}, function() {
						0;
						$scope.companies = [];
						$scope.totalItems = 0;
					});
				}
				else {
					//$httpProvider.defaults.headers.put['ipAddress'] = clientip;
					//$httpProvider.defaults.headers.put['X-Auth-ApiKey'] = $scope.apikey;								
					NewAccountService.search($scope.searchcompany, function(data, headers) {
						0;
						0;
						// The request results are managed client side so store all results in overall object
						$scope.totalItems = paging_totalItems(headers("Content-Range"));

						$scope.allCompanies = data;
						success();
					}, function() {
						0;

						$scope.companies = [];
						$scope.totalItems = 0;
					});
				}
			};

			$scope.isSearchFieldsOk = function(searchcompany) {
				return searchcompany.searchValue != "";
			};

			$scope.monitoringcompany = function(bedrijfId, bedrijfsNaam, adres, kvkNummer, subDossier, hoofd, bekendBijSbdr, monitoringBijBedrijf) {
				resetFields();
				$scope.monitorBedrijfSelected = bedrijfsNaam;

				searchkvk = kvkNummer;
				if(typeof subDossier != 'undefined') {
					searchkvk += subDossier;
				}

				if(typeof bedrijfId != 'undefined') {
					$location.path('/monitoringquestion/' + bedrijfId + '/' + searchkvk + '/' + hoofd + '/' + encodeURIComponent($rootScope.escapeParam(bedrijfsNaam)) + '/' + encodeURIComponent($rootScope.escapeParam(adres)) + '/' + searchurl + '/' + bekendBijSbdr + '/' + monitoringBijBedrijf);
				} else {
					$location.path('/monitoringquestion/' + 'undefined' + '/' + searchkvk + '/' + hoofd + '/' + encodeURIComponent($rootScope.escapeParam(bedrijfsNaam)) + '/' + encodeURIComponent($rootScope.escapeParam(adres)) + '/' + searchurl + '/' + bekendBijSbdr + '/' + monitoringBijBedrijf);
				}
				$location.url($location.path());
			};
			
			$scope.isOwnCompany = function(kvknumber) {
				if (typeof companyData != 'undefined' && companyData!=null) {
					return companyData.kvkNummer == kvknumber;
				} else
					return false;
			};

			$scope.opmerkingen = function(meldingBijBedrijf, monitoringBijBedrijf, rapportCreatedToday) {
				var result = '';
				var nr = 1;
				if(meldingBijBedrijf) {
					result = 'Er is al eerder een melding gedaan door uw bedrijf.';
					nr = nr + 1;

				}
				if(monitoringBijBedrijf) {
					if(nr > 1) {
						result += '\n' + nr + ') ';
					} 
					result += 'Reeds in monitoring geplaatst';
					nr = nr + 1;
				}
				if(rapportCreatedToday) {
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1 ){
						result = nr + ') ';
					}
					result += 'Er is vandaag al een rapport opgevraagd door uw bedrijf.';
					nr = nr + 1;
				}
				if($scope.isProspect()==true){
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1) {
						result = nr + ') ';
					}
					result += 'Uw accountregistratie is nog in behandeling. U kunt nog geen gebruik maken van rapport of monitoring. Dit wordt mogelijk na verificatie, u ontvangt hierover schriftelijk bericht.';
					nr = nr + 1;
				}
				if (!$scope.isAdresOk()) {
					if(result != '') {
						result += '\n' + nr + ') ';
					} else if (nr > 1) {
						result = nr + ') ';
					}
					result += 'Uw accountregistratie is nog in behandeling. U kunt nog geen gebruik maken van rapport of monitoring. Dit wordt mogelijk na verificatie, u ontvangt hierover schriftelijk bericht.';
					nr = nr + 1;					
				}
				if (nr > 2)
					result = '1) ' + result; 

				return result;
			};

			$scope.pageChanged = function() {
				if($scope.companies) {
					0;
					$scope.searchcompany.pageNumber = $scope.currentPage;
					// all items are managed client side, so no new fetch server side needed here

					pageparturl = '$' + $scope.currentPage;
					searchurl = searchparturl + pageparturl;

					$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);
				}
			};

			$scope.registercompany = function(bedrijfId, kvkNummer, subDossier, hoofd) {
				0;
				resetFields();

				searchkvk = kvkNummer;
				if(typeof subDossier != 'undefined') {
					searchkvk += subDossier;
				}

				//if (typeof bedrijfId === 'undefined' || bedrijfId == null) {
				CompanyService.getOrCreateCompanyData({kvknumber: searchkvk, hoofd: hoofd}, function(result) {
					if(typeof result.errorCode !== 'undefined') {
						$scope.error = result.errorCode + ' ' + result.errorMsg;
					}
					else {
						if(typeof bedrijfId === 'undefined' || bedrijfId == null) {
							if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
								$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + result.bedrijfId + '/' + searchurl);
							} else {
								$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + result.bedrijfId + '/' + searchurl);
							}
							$location.url($location.path());
						} else { // bedrijf already known
							if(!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO)) {
								$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
							} else {
								$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
							}
							$location.url($location.path());
						}
					}
				});
				//}
				//else {
				//	if (!$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO))
				//		$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
				//	else
				//		$location.path('/vermeldingeninfo/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + searchurl);
				//	$location.url($location.path());
				//}
			};

			$scope.isProspect = function() {
				if(typeof $window.sessionStorage.user != 'undefined') {
					return !!JSON.parse($window.sessionStorage.user).prospect;
				} else {
					return false;
				}
			};

			$scope.isAdresOk = function() {
				if(typeof $window.sessionStorage.user != 'undefined') {
					return JSON.parse($window.sessionStorage.user).adresOk;
				} else {
					return false;
				}
			};

			$scope.notAllowedReport = function() {
				if($scope.isProspect() || $scope.isAdresOk() == false) {
					return 'Alleen geverifiëerde klanten kunnen een rapport opvragen';
				} else {
					return '';
				}
			};

			$scope.notAllowedNotification = function() {
				if(!$rootScope.hasRoleUser('registraties_toegestaan')) {
					return 'Geen rechten om registraties in te voeren';
				} else {
					return '';
				}
			};
			
			$scope.notAllowedMonitoring = function(alreadyInMonitoring) {
				if($scope.isProspect() || $scope.isAdresOk() == false) {
					return 'Alleen geverifiëerde klanten kunnen een bedrijf monitoren';
				} else if (alreadyInMonitoring){
					return 'Reeds in monitoring geplaatst'
				} else {
					return '';
				}
			};

			//$scope.requestReport = function (bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf) {
			//	var costModal = $modal.open({
			//		templateUrl: 'reportCostPopup.html',
			//		controller: reportCostPopupController,
			//		size: 'lg'
			//	});

			//	costModal.result.then(function (result) {
			//		if (result.continue)
			//			showReportPopup(bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf);
			//	});
			//};

			$scope.searchCompanies = function() {
				resetFields();
				$scope.currentPage = 1;

				$scope.fetchResult(performSearchCompanies);
			};

			$scope.setPage = function(pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.start = function() {
				cfpLoadingBar.start();
			};

			getCompaniesOnPage = function(currentPage, itemsOnPage, totalItems, allcompanieslist) {
				var begin = (currentPage - 1) * itemsOnPage;
				var end = Math.min(begin + itemsOnPage, totalItems);

				return allcompanieslist.slice(begin, end);
			};

			$scope.makeReport = function(bedrijfId, kvkNummer, subDossier, hoofd, monitoringBijBedrijf) {				
				// Comment reportPopup (payment) for now
				//var reportModal = $modal.open({
				//	templateUrl: 'reportPopup.html',
				//	controller : reportPopupController,
				//	size       : 'lg'
				//});

				//reportModal.result.then(function(result) {
				//	if(result.doReport) {
						if(false) { // !$scope.userCanPay turned off
							var reportNoPaymentModal = $modal.open({
								templateUrl: 'reportNoPayment.html',
								controller: reportNoPaymentController,
								size:'lg'
							});
						} else {
							var bedrijfAanvragerId = userBedrijfId;

							searchkvk = kvkNummer;
							if(typeof subDossier != 'undefined') {
								searchkvk += subDossier;
							}

							//if (typeof bedrijfId === 'undefined' || bedrijfId == null) {
							CompanyService.getOrCreateCompanyData({
								kvknumber: searchkvk,
								hoofd    : hoofd
							}, function(result) {
								if(typeof result.errorCode !== 'undefined') {
									$scope.error = result.errorCode + ' ' + result.errorMsg;
								}
								else {
									if(bedrijfId == undefined || bedrijfId == null) {
										$location.path('/report/' + bedrijfAanvragerId + '/' + result.bedrijfId + '/' + '0' + '/' + searchurl);
										$location.url($location.path());
									} else { // bedrijf already known
										$location.path('/report/' + bedrijfAanvragerId + '/' + bedrijfId + '/' + '0' + '/' + searchurl);
										$location.url($location.path());
									}
								}
							});
						}
				//	}
				//})
			};

			performSearchCompanies = function() {
				//The request fires correctly but sometimes the ui doesn't update, that's a fix
				if(!jumptopage) {
					$scope.searchcompany.pageNumber = $scope.currentPage;
				} else {
					$scope.currentPage = jumptopage;
					$scope.searchcompany.pageNumber = jumptopage;
				}
				$scope.hasSearchResults = true;

				// set paging objects page 1
				$scope.companies = getCompaniesOnPage($scope.currentPage, $scope.itemsPage, $scope.totalItems, $scope.allCompanies);

				searchparturl = '$searchcompany$search$' + $scope.searchcompany.searchValue; // + '$' + $scope.searchcompany.kvknumber;
				pageparturl = '$' + $scope.currentPage;
				searchurl = searchparturl + pageparturl;

				// rootscope range delete
				delete $rootScope.range;

			};

			resetFields = function() {
				delete $scope.monitorCreated;
				delete $scope.error;
				delete $scope.monitorBedrijfSelected;
			};

			//saveShowReportPopup = function () {
			//	delete $scope.error;
			//
			//	$rootScope.saveShowHelpDialog($rootScope.HELP_RAPPORTPURCHASE, $scope.hideReportPopup, function (result) {
			//		if (result.error != null)
			//			$scope.error = result.error;
			//	});
			//};

			if(typeof $scope.initialised == 'undefined') {

				if($routeParams.searchValue) {
					var searchValue = "";
					if($routeParams.searchValue && $routeParams.searchValue != 'undefined') {
						searchValue = $routeParams.searchValue;
					}
					//var kvkNummer = "";
					//if ($routeParams.kvkNummer && $routeParams.kvkNummer != 'undefined')
					//	kvkNummer = $routeParams.kvkNummer;
					var jumptopage = 1;
					if($routeParams.page && $routeParams.page != 'undefined') {
						jumptopage = $routeParams.page;
					}

					$scope.searchcompany = {
						bedrijfId     : userBedrijfId,
						pageNumber    : 1,	// first full set is searched from page 1
						//kvknumber: kvkNummer,
						requestPerPage: false,
						searchValue   : searchValue,
						city          : ""
					};

					$scope.totalItems = 0;
					// currentpage will be set on search
					$scope.itemsPage = 10;
					$scope.maxSize = 5;

					$scope.searchCompanies();
				} else {
					$scope.searchcompany = {
						bedrijfId     : userBedrijfId,
						pageNumber    : 1,
						//kvknumber: "",
						requestPerPage: false,
						searchValue   : "",
						city          : ""
					};

					$scope.totalItems = 0;
					$scope.currentPage = 1;
					$scope.itemsPage = 10;
					$scope.maxSize = 5;
				}

				$rootScope.changeUrlWithoutReload("/searchcompany");

				if(typeof $window.sessionStorage.user != 'undefined') {
					$scope.hideReportPopup = !$rootScope.isShowHelp($rootScope.HELP_RAPPORTPURCHASE);

					CompanyService.checkIfUserCanPay({userId: JSON.parse($window.sessionStorage.user).userId}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							$scope.error = result.errorCode + " " + result.errorMsg;
						} else {
							$scope.userCanPay = result.val;
						}
					});
				} else {
					$scope.userCanPay = false;
					if ($window.sessionStorage.user === undefined) {
						$rootScope.ipAddress = clientip.ip;
						NewAccountService.getApiKey2(function(result) {
							if (typeof result.errorCode != 'undefined') {
								$scope.error = result.errorMsg;
							} else {
								$rootScope.clientApiKey = result.token;
								$rootScope.ipAddress = clientip.ip;		
							}
						});
					}
				}

				$scope.initialised = true;
			}

		}]);appcontrollers.controller('SearchResultsAdminTabController',
	['$window', '$modal', '$routeParams', '$scope', '$rootScope', '$location', 'maxFieldLengths',
		'$filter', '$timeout', 'ngTableParams', 'DashboardNumberTags',
		'DashboardService', 'AccountService', 'CompanyService',
		function ($window, $modal, $routeParams, $scope, $rootScope, $location, maxFieldLengths,
				  $filter, $timeout, ngTableParams, DashboardNumberTags,
				  DashboardService, AccountService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			var RemoveCustomerController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;

				$scope.removeCustomerOk = function (reason) {
					$scope.remove = true;
					$scope.closeRemoveCustomerModal();
				};

				$scope.closeRemoveCustomerModal = function () {
					var result = {
						remove: $scope.remove
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			var RemoveNotificationController = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
				$scope.remove = false;
				$scope.reason = '1';

				$scope.removeNotificationOk = function (reason) {
					$scope.reason = reason;
					$scope.remove = true;
					$scope.closeRemoveNotificationModal();
				};

				$scope.closeRemoveNotificationModal = function () {
					var result = {
						remove: $scope.remove,
						reason: $scope.reason
					};

					$modalInstance.close(result); // $scope.remove
				};
			}];

			$scope.maxSearchLength = 100;

			$scope.title = "SearchResults Tab";

			$scope.createDocumentRequested = function (bedrijfId, referentieNummer) {
				return {action: 'report', bedrijfId: bedrijfId, meldingId: 0, referentie: referentieNummer};
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			$scope.infoBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'AFW' || status == 'DEL' || status == 'INV')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'AFW' || status == 'DEL')
						result = true;
				}

				return result;
			};

			$scope.successBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'ACT')
						result = true;
				} else if (type == 'Monitoring') {
					if (status == 'ACT')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'ACT')
						result = true;
				}

				return result
			};

			$scope.warningBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'PRO' || status == 'REG')
						result = true;
				} else if (type == 'Vermelding') {
					if (status == 'INI' || status == 'INB' || status == 'NOK')
						result = true;
				}

				return result;
			};

			$scope.dangerBadge = function (type, status) {
				var result = false;
				// type: KLA, MON, VER, RAP

				if (type == 'Klant') {
					if (status == 'NOK')
						result = true;
				}

				return result;
			};

			$scope.$watch('filterCriteria.filterValue', function (search) {
				if (typeof searchCallbackTimeout != 'undefined')
					$timeout.cancel(searchCallbackTimeout);

				searchCallbackTimeout = $timeout(function () {
					if (search) {
						if (search.length > 2) {
							// refresh
							$scope.currentPage = 1;
							$scope
								.pageChanged();
							$scope.oldSearch = search;
						}
					} else if (isSearchChanged(search)) {
						// refresh
						$scope.currentPage = 1;
						$scope
							.pageChanged();
						$scope.oldSearch = search;
					}
				}, 1000);
			}, true);

			$scope.nrOfItems = function () {
				return 0;
			};

			$scope.hasItems = function () {
				// if
				// (DashboardNumberTags.getNrOfCustomersAdmin()
				// <= 0)
				// return false;
				// else
				return true;
			};

			// Data table functions
			$scope.filterResult = function () {
				return $scope.fetchResult(function () {
					// The request fires correctly but sometimes
					// the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;
				});
			};
			
			$scope.setVermelder = function () {				
				var oldval = $scope.filterCriteria.vermelder;

				$scope.filterCriteria.vermelder = $scope.selectieVermelder;
				
				if (oldval != $scope.filterCriteria.vermelder) {
					$scope.currentPage = 1;
					$scope.pageChanged();
				}
			};

			// The function that is responsible of fetching the
			// result from the
			// server and setting the grid to the new result
			$scope.fetchResult = function (success) {
				searchparturl = '$dashboard$searchresultsadmintab$search$'
					+ $scope.filterCriteria.filterValue; // +
				// '$'
				// +
				// $scope.searchcompany.kvknumber;
				pageparturl = '$' + $scope.currentPage;
				searchurl = searchparturl + pageparturl;

				$rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
				DashboardService.searchresultsadmin(
					$scope.filterCriteria,
					function (data, headers) {
						0;
						if (typeof data != 'undefined') { //.searchResultsOverviewTransfer
							$scope.searchresults = data;

							$scope.totalItems = paging_totalItems(headers("Content-Range"));
						} else {
							$scope.searchresults = null;
							$scope.totalItems = 0;
						}

						success();
					},
					function () {
						0;
						$scope.searchresults = [];
						$scope.totalItems = 0;
					});

			};

			// Pagination functions
			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$scope.pageChanged = function () {
				0;
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.fetchResult(function () {
					// Nothing to do..

					// rootscope range delete
					delete $rootScope.range;
				});
			};

			$scope.searchresults = function () {
				return $scope.totalItems > 0;
			};

			$scope.detailsCustomer = function (bedrijfId) {
				$location.path('/klantaccount/' + bedrijfId + '/' + searchurl); // '/$dashboard$searchresultsadmintab');
				$location.url($location.path());
			};

			$scope.detailsCompany = function (bedrijfId) {
				$location.path('/editcompany/' + bedrijfId + '/' + searchurl); // '/' +
				// '$dashboard$searchresultsadmintab');
				$location.url($location.path());
			};

			$scope.notificationChange = function (meldingId, bedrijfDoorId, bedrijfId) {
				0;
				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + searchurl + '/' + meldingId);
				// '$dashboard$searchresultsadmintab/'
				// +
				// meldingId);
			};

			$scope.notificationReadOnly = function (meldingId, bedrijfDoorId, bedrijfId) {
				0;

				$location.path('/notifycompany/' + bedrijfDoorId + '/' + bedrijfId + '/' + searchurl + '/' + meldingId + '/true');
			};

			if (typeof $scope.initialised == 'undefined') {
				if (typeof $scope.filterCriteria == 'undefined') {
					$scope.filterCriteria = {
						bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
						pageNumber: 1,
						sortDir: "", // asc, desc
						sortedBy: "",
						filterValue: "", // text to filter on
						vermelder: true
					};
					$scope.selectieVermelder = true;
				}

				var searchresults = [];

				$scope.totalItems = 0;
				$scope.currentPage = 1;
				$scope.itemsPage = 20;
				$scope.maxSize = 5;

				if ($routeParams.searchValue) {

					$scope.filterCriteria.filterValue = $routeParams.searchValue;

					var jumptopage = 1;
					if ($routeParams.page != 'undefined')
						jumptopage = parseInt($routeParams.page);

					$scope.currentPage = jumptopage;

				}

				// fetch initial data for 1st time
				$scope.filterResult();
			}

			doRemoveNotification = function (notificationId, companyId, reason) {

				var request = {
					meldingId: notificationId,
					bedrijfId: companyId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId,
					reden: reason
				};

				CompanyService.removeNotificationCompany(request, function (response, error) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorMsg;
					}
					else {
						$scope.notificationRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				}, function (error) {
					0;
				});
			};

			$scope.removeNotification = function (notificationId, companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removenotification.html',
					controller: RemoveNotificationController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveNotification(notificationId, companyId, result.reason);
					}
				}, function () {
					0;
				});

			};

			doRemoveCustomer = function (companyId) {
				AccountService.deleteAccountOfBedrijf(companyId, function (response) {
					if (typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					}
					else {
						$scope.customerRemoved = true;
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			$scope.removeCustomer = function (companyId) {
				var modalInstance = $modal.open({
					templateUrl: 'removecustomer.html',
					controller: RemoveCustomerController,
					size: 'lg'
				});

				modalInstance.result.then(function (result) {
					if (result.remove) {
						doRemoveCustomer(companyId);
					}
				}, function () {
					0;
				});

			};
		}]);appservices.factory('AccountService', ['$resource', function($resource) {
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
					0;
				});
				
				dataStream.onClose(function(event) {
					0;
				});
				
				dataStream.onOpen(function() {
					0;					
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
function adBlockDetected() {
	var elem = angular.element(document.querySelector('[data-ng-app]'));
	var injector = elem.injector();
	if (typeof injector !== 'undefined' && injector != null) {
	 	var $rootScope = injector.get('$rootScope');  
	    $rootScope.$apply(function(){
	       $rootScope.adbEnabled = true ;
	    });
	}
	$('#main').hide();
}
function adBlockNotDetected() {
	var elem = angular.element(document.querySelector('[data-ng-app]'));
	var injector = elem.injector();
 	var $rootScope = injector.get('$rootScope');  
 	$rootScope.$apply(function(){
       $rootScope.adbEnabled = false ;
    });
	$('#main').show();
}  
	appcontrollers.controller('SupportController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', 'SupportService', 'CompanyService', 
        function ($window, $scope, $rootScope, $location, $filter, SupportService, CompanyService) {
            0;

            $scope.totalItems = 0;
            $scope.userSupportTickets = [];
            $scope.currentPage = 1;
            $scope.itemsPage = 10;
            $scope.maxSize = 5;

			CompanyService.companyHasNotifications({gebruikerId: JSON.parse($window.sessionStorage.user).userId}, function(result){
				delete $scope.error;
				if(result.errorCode != null){
					$scope.error = result.errorCode + " " + result.errorMsg;
				} else {
					$scope.hasNotificationsToObject = result.val;
				}
			});

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

			$scope.pageChanged = function () {
				$scope.filterCriteria.pageNumber = $scope.currentPage;
				$scope.getSupportTicketsOfUser(function () {
					delete $rootScope.range;
				});
			};

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    gebruikerId: JSON.parse($window.sessionStorage.user).userId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: ""
                };
            }
			$scope.filterResult = function () {
				return $scope.getSupportTicketsOfUser(function () {
					//The request fires correctly but sometimes the ui doesn't update, that's a fix
					$scope.filterCriteria.pageNumber = 1;
					$scope.currentPage = 1;

					// rootscope range delete
					delete $rootScope.range;

				});
			};

            //Navigational functions
            $scope.gotoFAQ = function () {
                $location.path('/support/faq');
                $location.url($location.path());
            };

            $scope.gotoProblem = function () {
                $location.path('/support/problem');
                $location.url($location.path());
            };

            $scope.gotoObjection = function () {
                $location.path('/support/objection');
                $location.url($location.path());
            };

            $scope.gotoDetails = function (refNo) {
                $location.path('/support/detail/' + refNo+'/$support');
                $location.url($location.path());
            };

            $scope.getSupportTicketsOfUser = function () {
            	delete $scope.error;
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);
                SupportService.findsupportticketlistforuser($scope.filterCriteria, function (data, headers) {
                    if ( data != undefined && data.length > 0) {
                        $scope.userSupportTickets = data;
                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    } else {
                        $scope.userSupportTickets = [];
                        $scope.totalItems = 0;
                    }
                }, function () {
                    0;
                    $scope.error = "Fout bij ophalen supporttickets";
                    $scope.userSupportTickets = [];
                    $scope.totalItems = 0;
                });
            };

			$scope.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

            $scope.getSupportTicketsOfUser();
        }]);appcontrollers.controller('SupportDetailController',
	['$window', '$routeParams', '$scope', '$rootScope', '$location', 'supportTicketsDetails',
		'SupportService', 'CompanyService', 'backToAlerts',
		function($window, $routeParams, $scope, $rootScope, $location, supportTicketDetails,
				 SupportService, CompanyService, backToAlerts) {

			if($routeParams.searchurl) {
				searchurl = $routeParams.searchurl;
			} else {
				searchurl = '$dashboard';
			}
		
			if(typeof $scope.init == 'undefined') {

				$scope.supportredenen = [
					{id: 'BBV', description: 'Betwist de vordering'},
					{id: 'BFO', description: 'Factuur is ongegrond'},
					{id: 'BOO', description: 'Organisatie is onbekend'},
					{id: 'BRG', description: 'Regeling is getroffen'},
					{id: 'BAN', description: 'Overig'}
				];

				if(supportTicketDetails.supportTicketChain[0].errorCode != null) {
					$scope.showDetailsNotAllowed = true;
					$scope.topError = supportTicketDetails.supportTicketChain[0].errorCode + ' ' + supportTicketDetails.supportTicketChain[0].errorMsg;
				} else {
					$scope.showDetailsNotAllowed = false;
					$scope.supportchain = supportTicketDetails.supportTicketChain;
				}

				if(!$scope.showDetailsNotAllowed) {
					$scope.reaction = {
						betwistBezwaar : null,
						supportReden   : null,
						bericht        : null,
						gebruiker      : {gebruikersId: JSON.parse($window.sessionStorage.user).userId},
						supportParentId: $scope.supportchain[$scope.supportchain.length - 1].supportId
					};

					$scope.attachments = [];

					$scope.supportTicket = $scope.supportchain[0];

					$scope.thisUser = JSON.parse($window.sessionStorage.user);

					if($scope.supportTicket.melding) {
						$scope.meldingVanBedrijf = CompanyService.companyData({bedrijfId: $scope.supportTicket.melding.bedrijfIdGerapporteerd});
					}

					$scope.supportSavedOk = false;

					$scope.obj = {};
				}

				$scope.init = true;
			}

			$scope.$on('attachmentControllerEvent', function(event, data) {
				$scope.attachmenterror = true;
				$scope.error = data;
			});

			$scope.downloadAttachment = function(sBId) {
				return {action: 'support_attachment', supportBestandId: sBId};
			};

			$scope.checkObjectionReactionFormInvalid = function(thisForm) {
				return thisForm.$invalid || $scope.isInvalid(thisForm, 'rbobjection');
			};

			//$scope.fetchAttachments = function(supportTicket){
			//    return SupportService.findsupportticketbestandenbysupportid({supportId: supportTicket.supportId});
			//};

			$scope.getStaticSuccessMsg = function() {
				if($scope.supportTicket.supportStatus == 'ARC') {
					if($scope.supportTicket.supportType == 'BZW') {
						if(typeof $scope.supportTicket.melding != 'undefined') {
							if($scope.supportTicket.melding.meldingstatusCode == 'ACT') {
								return 'Bezwaar is afgewezen, melding is opgenomen';
							} else if($scope.supportTicket.melding.meldingstatusCode == 'AFW' || $scope.supportTicket.melding.meldingstatusCode == 'DEL') {
								return 'Bezwaar is geaccepteerd of kwestie is gearchiveerd, vermelding is verwijderd';
							} else if($scope.supportTicket.melding.meldingstatusCode == 'INB'){
								return 'Bezwaar is afgewezen. De vermelding blijft opgenomen in het register.';
							}
						}
					} else {
						return 'Support ticket is reeds afgehandeld';
					}
				} else if($scope.supportTicket.supportStatus == 'GST'&&!$scope.hasAdminRole($scope.thisUser.roles)){
					return 'Het bezwaar is in behandeling bij de afdeling bezwaar. U wordt op de hoogte gehouden van het verloop en indien nodig gevraagd uw kant van het verhaal te belichten.';
				}
			};

			$scope.gotoSupport = function() {
				url = gotoDurl(searchurl);
				if(url != null) {
					$location.path(url);
					$location.url($location.path());
				}
				
//				if(backToAlerts == 'true') {
//					$location.path('/dashboard');
//					$location.url($location.path());
//				} else {
//					$location.path('/support');
//					$location.url($location.path());
//				}
			};

			$scope.handleObjectionSbdrReaction = function(discardObjection) {
				if(!this.formobjectionsbdrreaction.$invalid) {
					this.formobjectionsbdrreaction.$setPristine();

					//Set the correct supportType
					$scope.reaction.supportType = 'BZW';

					if(discardObjection) {
						SupportService.sbdrdiscardobjection($scope.reaction, function(response) {
							if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
								$scope.error = response.errorCode + ' ' + response.errorMsg;
							} else {
								$scope.supportSavedOk = true;
								$scope.successMsg = 'Bezwaar is afgewezen, melding wordt opgenomen';

								SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
									$scope.supportchain = newChain;

									$scope.supportTicket = $scope.supportchain[0];
								});
							}
						});
					} else {
						SupportService.sbdracceptobjection($scope.reaction, function(response) {
							if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
								$scope.error = response.errorCode + ' ' + response.errorMsg;
							} else {
								$scope.supportSavedOk = true;
								$scope.successMsg = 'Bezwaar is geaccepteerd, melding wordt verwijderd';

								SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
									$scope.supportchain = newChain;

									$scope.supportTicket = $scope.supportchain[0];
								});
							}
						});
					}
				}
			};

			$scope.hasAdminRole = function(roles) {
				return hasRole(roles, 'SBDR') || hasRole(roles, 'SBDRHOOFD') || hasRole(roles, 'admin_sbdr_hoofd') || hasRole(roles, 'admin_sbdr');
			};

			$scope.isInvalid = function(targetForm, targetField) {
				if(targetForm[targetField] != undefined) {
					var result_invalid = targetForm[targetField].$invalid;
					var result_dirty = targetForm[targetField].$dirty;

					if(result_dirty  && (typeof $scope.attachmenterror == 'undefined' || !$scope.attachmenterror)) {
						delete $scope.error;
						delete $scope.successMsg;
					}

					if(targetField == 'rbobjection') {
						result_invalid = targetForm['rbobjection'].$modelValue != 'true' && targetForm['rbobjection'].$modelValue != 'false';
					}

					return result_dirty && result_invalid;
				}
				else {
					return true;
				}
			};

			$scope.pickUpSupportTicket = function() {
				var supportInfo = {
					refNo : $scope.supportTicket.referentieNummer,
					userId: $scope.thisUser.userId
				};

				SupportService.pickupsupportticket(supportInfo, function(response) {
					if(typeof response.errorCode != 'undefined') {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					} else {
						$scope.supportTicketPickUpOK = true;

						SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
							$scope.supportchain = newChain;

							$scope.supportTicket = $scope.supportchain[0];
						});
					}
				});
			};

			$scope.showStaticSuccessMsg = function() {
				if($scope.supportTicket.supportStatus == 'ARC') {
					if($scope.supportTicket.supportType == 'BZW') {
						if(typeof $scope.supportTicket.melding != 'undefined') {
							if($scope.supportTicket.melding.meldingstatusCode == 'ACT' || $scope.supportTicket.melding.meldingstatusCode == 'INB') {
								return true;
							} else if($scope.supportTicket.melding.meldingstatusCode == 'AFW' || $scope.supportTicket.melding.meldingstatusCode == 'DEL') {
								return true;
							}
						}
					} else {
						return true;
					}
				} else if($scope.supportTicket.supportStatus == 'GST'&&!$scope.hasAdminRole($scope.thisUser.roles)){
					return true;
				}

				return false;
			};

			$scope.submitObjectionReaction = function() {
				if(!$scope.checkObjectionReactionFormInvalid(this.formobjectionreaction)) {
					this.formobjectionreaction.$setPristine();

					//BOTH sbdrdiscardobjection AND sbdracceptobjection can be called,
					//The boolean that is used in the webservice won't be used when a user discards an objection
					SupportService.createsupportticketreaction($scope.reaction, function(newSupport) {
						if(typeof newSupport.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = newSupport.errorCode + ' ' + newSupport.errorMsg;
						} else {
							var fileResults = [];
							var continueLoop = true;
							async.each($scope.attachments, function(attachment, callback) {
								if(continueLoop) {
									var formData = createFormData(attachment, newSupport.supportId, newSupport.referentieNummer);

									SupportService.createsupportticketbestand(formData, function(response) {
										if(typeof response.errorCode != 'undefined') {
											continueLoop = false;
											callback(response.errorCode + ' ' + response.errorMsg);
										} else {
											fileResults.push(response);
											callback();
										}
									})
								}
							}, function(result) {
								if(result) {
									$scope.error = result;
								} else {
									$scope.supportSavedOk = true;
									$scope.successMsg = 'Reactie op ticket met referentie ' + newSupport.referentieNummer + ' wordt behandeld';

									SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
										$scope.supportchain = newChain;

										$scope.supportTicket = $scope.supportchain[0];
									});
								}
							});
						}
					});
				}
			};

			$scope.submitSupportSbdrReaction = function() {
				delete $scope.successMsg;

				if(!this.formsupportsbdrreaction.$invalid) {
					this.formsupportsbdrreaction.$setPristine();
					SupportService.createsupportticket($scope.reaction, function(response) {
						if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = response.errorCode + ' ' + response.errorMsg;
						} else {
							$scope.supportSavedOk = true;
							$scope.successMsg = 'Reactie is met succes geplaatst';

							SupportService.findsupportticketsbyreferenceno({refNo: $scope.supportTicket.referentieNummer}, function(newChain) {
								$scope.supportchain = newChain;

								$scope.supportTicket = $scope.supportchain[0];
							});
						}
					})
				}
			};

			createFormData = function(newFile, supportId, supportRef) {
				var fd = new FormData();
				fd.append('file', newFile);
				fd.append('BestandsNaam', newFile.name);
				fd.append('SupportId', supportId);
				fd.append('SupportReferentie', supportRef);

				return fd;
			};
		}]);

appcontrollers.controller('SupporAttachmentController', ['$scope', function($scope) {
	$scope.$watch('createFileAttachment', function(newFile, oldFile) {
		delete $scope.attachmenterror;
		if(newFile != undefined) {
			if(newFile.type == 'application/gzip'
				|| newFile.type == 'application/msword'
				|| newFile.type == 'application/pdf'
				|| newFile.type == 'application/vnd.ms-excel'
				|| newFile.type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
				|| newFile.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
				|| newFile.type == 'application/zip'
				|| newFile.type == 'text/csv'
				|| newFile.type == 'text/plain'
				|| newFile.type == 'text/rtf') {
				if(newFile != undefined && typeof $scope.attachments !== 'undefined' && $scope.attachments.length <= 2) {
					var fileExists = false;

					$scope.attachments.some(function(elem, index, array) {
						if(elem.name == newFile.name) {
							fileExists = true;
						}
					});

					if(!fileExists) {
						$scope.attachments.push(newFile);
					}
				}
			} else {
				$scope.$emit('attachmentControllerEvent', 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv');
				//$scope.error = 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv';
			}
		}
	});

	$scope.removeAttachment = function(index) {
		$scope.attachments.splice(index, 1);
	}
}]);appcontrollers.controller('SupportFaqController',
    ['$scope', '$location',
        function ($scope, $location) {

            $scope.gotoSupport = function () {
                $location.path('/support');
                $location.url($location.path());
            };
        }]);appcontrollers.controller('SupportObjectionController',
	['$window', '$scope', '$rootScope', '$location', '$anchorScroll', 'maxFieldLengths',
		'$routeParams', '$q', 'SupportService', 'CompanyService',
		function($window, $scope, $rootScope, $location, $anchorScroll, maxFieldLengths,
				 $routeParams, $q, SupportService, CompanyService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			if($scope.init == undefined) {
				$scope.init = true;

				$scope.supportredenen = [
					{id: 'BBV', description: 'Betwist de vordering'},
					{id: 'BFO', description: 'Factuur is ongegrond'},
					{id: 'BOO', description: 'Organisatie is onbekend'},
					{id: 'BRG', description: 'Regeling is getroffen'},
					{id: 'BVV', description: 'Vordering is voldaan'},
					{id: 'BAN', description: 'Overig'}
				];

				$scope.support = {
					supportType : 'BZW',
					supportReden: null,
					bericht     : null,
					melding     : {meldingId: null},
					gebruiker   : {gebruikersId: JSON.parse($window.sessionStorage.user).userId}
				};

				$scope.attachments = [];

				$scope.companymeldingen = [];

				$scope.selectedCompany = {};
				$scope.selectedNotification = {};

				$scope.meldingBedrijfNaam = '-';
				$scope.meldingReferentieIntern = '-';

				$scope.controle = {
					akkoord: false
				};
			}

			$scope.$watch('createFileAttachment', function(newFile, oldFile) {
				delete $scope.attachmenterror;
				if(newFile != undefined) {
					if(newFile.type == 'application/gzip'
						|| newFile.type == 'application/msword'
						|| newFile.type == 'application/pdf'
						|| newFile.type == 'application/vnd.ms-excel'
						|| newFile.type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
						|| newFile.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
						|| newFile.type == 'application/zip'
						|| newFile.type == 'text/csv'
						|| newFile.type == 'text/plain'
						|| newFile.type == 'text/rtf') {
						if(newFile != undefined && $scope.attachments != undefined && $scope.attachments.length <= 2) {
							var fileExists = false;

							$scope.attachments.some(function(elem, index, array) {
								if(elem.name == newFile.name) {
									fileExists = true;
								}
							});

							if(!fileExists) {
								$scope.attachments.push(newFile);
							}
						}
					} else {
						$scope.attachmenterror = true;
						$scope.error = 'Dit type bestand is niet toegestaan, alleen de volgende extensies zijn toegestaan: doc, docx, xls, xlsx, pdf, rtf, gzip, zip en csv';
					}
				}
			});

			$scope.checkSupportFormInvalid = function() {
				//als er een fout is, moet er true teruggegeven worden
				return $scope.formsupportobjection.$invalid || $scope.controle.akkoord == false;
			};

			$scope.fetchMeldingen = function() {
				$scope.args = {gebruikerId: JSON.parse($window.sessionStorage.user).userId};
				CompanyService.getNotificationsOfCompany($scope.args, function(response) {
					if(response.errorCode != undefined) {
						$scope.error = response.errorCode + ' ' + response.errorMsg;
					} else {
						$scope.companymeldingen = response;
					}
				});
			};

			$scope.gotoSupport = function() {
				$location.path('/support');
				$location.url($location.path());
			};

			$scope.isInvalid = function(targetForm, targetField) {
				//als er een fout is, moet er true teruggegeven worden
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(result_dirty && (typeof $scope.attachmenterror == 'undefined' || !$scope.attachmenterror)) {
					delete $scope.error;
				}

				return result_dirty && result_invalid;
			};

			$scope.removeAttachment = function(index) {
				$scope.attachments.splice(index, 1);
			};

			$scope.submitSupport = function() {
				$scope.formSubmitted = true;

				delete $scope.supportSavedOk;

				if(!$scope.checkSupportFormInvalid()) {
					$scope.formsupportobjection.$setPristine();

					SupportService.createsupportticket($scope.support, function(newSupport) {
						if(newSupport.errorCode != undefined) {
							$scope.error = newSupport.errorCode + ' ' + newSupport.errorMsg;
						} else {
							var fileResults = [];
							var continueLoop = true;

							async.each($scope.attachments, function(attachment, callback) {
								if(continueLoop) {
									var formData = createFormData(attachment, newSupport.supportId, newSupport.referentieNummer);

									SupportService.createsupportticketbestand(formData, function(response) {
										if(response.errorCode != undefined) {
											continueLoop = false;
											callback(response.errorCode + ' ' + response.errorMsg);
										} else {
											fileResults.push(response);
											callback();
										}
									})
								}
							}, function(result) {
								if(result) {
									$scope.error = result;
								} else {
									$scope.supportSavedOk = true;
									$scope.supportReferentie = newSupport.referentieNummer;

									if(fileResults.length == 1) {
										$scope.supportBijlagenMsg = ' met 1 bijlage';
									} else if(fileResults.length > 1) {
										$scope.supportBijlagenMsg = ' met ' + fileResults.length + ' bijlagen';
									}
								}
							});
						}
					});

					$scope.formSubmitted = false;
				}
			};

			createFormData = function(newFile, supportId, supportRef) {
				var fd = new FormData();
				fd.append('file', newFile);
				fd.append('BestandsNaam', newFile.name);
				fd.append('SupportId', supportId);
				fd.append('SupportReferentie', supportRef);

				return fd;
			};

			meldingChanged = function(index) {
				if (index > 0) {
					CompanyService.companyData({bedrijfId: $scope.companymeldingen[index-1].bedrijfIdGerapporteerd}, function(result) {
						if(typeof result.errorCode != 'undefined') {
							$scope.error = result.errorCode + " " + result.errorMsg;
						} else {
							$scope.selectedCompany = result;

							CompanyService.companyNotificationData({
								meldingId: $scope.companymeldingen[index].meldingId,
								bedrijfId: $scope.companymeldingen[index].bedrijfId
							}, function(notificationResult) {
								if(typeof result.errorCode != 'undefined') {
									$scope.error = notificationResult.errorCode + " " + notificationResult.errorMsg;
								} else {
									$scope.selectedNotification = notificationResult;
								}
							});
						}
					});
				} else {
					$scope.selectedCompany = {};
					$scope.selectedNotification = {};
				}
			};

			$scope.fetchMeldingen();
		}]);appcontrollers.controller('SupportProblemController',
	['$window', '$scope', '$rootScope', '$location', 'maxFieldLengths', 'SupportService',
		function($window, $scope, $rootScope, $location, maxFieldLengths, SupportService) {

			$scope.maxFieldLengths = maxFieldLengths;
			
			if(typeof $scope.init == 'undefined') {
				$scope.init = true;

				$scope.supporttypes = [
					{id: 'KLT', description: 'Klacht'},
					{id: 'PRB', description: 'Probleem'},
					{id: 'VRG', description: 'Vraag'},
					{id: 'SGT', description: 'Suggestie'}
				];

				$scope.supportredenen = [
					{id: 'PAN', description: 'Overig'},
					{id: 'PBA', description: 'Kan geen melding doen van betalingsachterstand'},
					{id: 'PBD', description: 'Kan geen bedrijf opzoeken'},
					{id: 'PBM', description: 'Kan geen monitor op bedrijf plaatsen'},
					{id: 'PGB', description: 'Kan geen gebruiker toevoegen'}
				];

				$scope.support = {
					supportType : null,
					supportReden: null,
					bericht     : null,
					gebruiker   : {gebruikersId: JSON.parse($window.sessionStorage.user).userId}
				};
			}

			$scope.$watch('support.supportType', function(newType) {
				if(newType == 'KLT' || newType == 'VRG') {
					$scope.support.supportReden = null;
				}
			});

			$scope.gotoSupport = function() {
				$location.path('/support');
				$location.url($location.path());
			};

			$scope.submitSupport = function() {
				$scope.formSubmitted = true;

				delete $scope.supportSavedOk;

				if(!$scope.checkSupportFormInvalid()) {
					$scope.formsupportkpv.$setPristine();
					SupportService.createsupportticket($scope.support, function(response) {
						if(typeof response.errorCode != 'undefined' || typeof $rootScope.error != 'undefined') {
							$scope.error = response.errorCode + ' ' + response.errorMsg;
						} else {
							$scope.supportSavedOk = true;
							$scope.supportReferentie = response.referentieNummer
						}
					});
					$scope.formSubmitted = false;
				}
			};

			$scope.isInvalid = function(targetForm, targetField) {
				//als er een fout is, moet er true teruggegeven worden
				var result_invalid = targetForm[targetField].$invalid;
				var result_dirty = targetForm[targetField].$dirty || $scope.formSubmitted;

				if(result_dirty) {
					delete $scope.error;
				}

				if(targetField == 'supportreden') {
					if(($scope.support.supportType == 'PRB' && $scope.support.supportReden == null) ||
						($scope.support.supportType != 'PRB' && $scope.support.supportReden != null)) {
						return true;
					}
				}

				return result_dirty && result_invalid;
			}
			;

			$scope.checkSupportFormInvalid = function() {
				//als er een fout is, moet er true teruggegeven worden
				return $scope.formsupportkpv.$invalid || $scope.isInvalid($scope.formsupportkpv, 'supportreden');
			}
		}
	])
;appcontrollers.controller('SupportTicketsAdminTabController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', '$timeout', 'maxFieldLengths', 'ngTableParams',
        'DashboardNumberTags', 'DashboardService', 'AccountService', 'CompanyService',
        function ($window, $scope, $rootScope, $location, $filter, $timeout, maxFieldLengths, ngTableParams,
                  DashboardNumberTags, DashboardService, AccountService, CompanyService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            $scope.title = "Support Tickets Tab";

            AccountService.getCompanyAccountData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId}, function (data) {
                $scope.account = data;
            });

            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPage = 20;
            $scope.maxSize = 5;
            $scope.thisUser = JSON.parse($window.sessionStorage.user);

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: "",
                    filterValue: "" // text to filter on
                };
            }

            $scope.$watch('filterCriteria.filterValue', function (search) {
                if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

                searchCallbackTimeout = $timeout(function () {
                    if (search) {
                        if (search.length > 2) {
                            // refresh
                            $scope.currentPage = 1;
                            $scope.pageChanged();
                            $scope.oldSearch = search;
                        }
                    }
                    else if (isSearchChanged(search)) {
                        // refresh
                        $scope.currentPage = 1;
                        $scope.pageChanged();
                        $scope.oldSearch = search;
                    }
                }, 1000);
            }, true);

            // The function that is responsible of fetching the result from the
            // server and setting the grid to the new result
            $scope.fetchResult = function (success) {
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                DashboardService.supportticketsadmin($scope.filterCriteria, function (data, headers) {
                    if (typeof data != 'undefined' && data.length > 0) {
                        $scope.supportTicketsAdminCollection = data;
                        
                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    }
                    else {
                        $scope.supportTicketsAdminCollection = null;
                        $scope.totalItems = 0;
                    }
                    DashboardNumberTags.setNrOfSupportTicketsAdmin($scope.totalItems);

                    success();
                }, function () {
                    $scope.supportTicketsAdminCollection = [];
                    $scope.totalItems = 0;
                    DashboardNumberTags.setNrOfSupportTicketsAdmin($scope.totalItems);
                });
            };

            // Data table functions
            $scope.filterResult = function () {
                return $scope.fetchResult(function () {
                    //The request fires correctly but sometimes the ui doesn't update, that's a fix
                    $scope.filterCriteria.pageNumber = 1;
                    $scope.currentPage = 1;

                    // rootscope range delete
                    delete $rootScope.range;

                });
            };

            $scope.hasItems = function () {
                return DashboardNumberTags.getNrOfSupportTicketsAdmin() > 0;
            };

            $scope.ignorealert = function (type, alertId, bedrijfId) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                } else {
                    deleteAlert(alertId);
                }
            };

            $scope.pageChanged = function () {
                $scope.filterCriteria.pageNumber = $scope.currentPage;
                $scope.fetchResult(function () {
                    //Nothing to do..

                    // rootscope range delete
                    delete $rootScope.range;
                });
            };

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

			$scope.viewAdminDetails = function(refNoPRefix){
				supportDetail(refNoPRefix);
			};

            $scope.viewdetails = function (type, alertId, monitoringId, meldingId, supportId, bedrijfId, refNoPrefix) {
            	if (type == 'SUP') {
					deleteAlert(alertId);
                    supportDetail(refNoPrefix);
                }
            };

			deleteAlert = function (alertId) {
				// remove alert record
				CompanyService.deleteAlert(alertId, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};


			supportDetail = function (refNoPrefix) {
				delete $scope.error;
				$location.path('/support/detail/' + refNoPrefix+'/$dashboard$supportticketsadmintab');
				$location.url($location.path());
			};

			
			$scope.lowercaseFirstLetter = function (string) {
			    return string.charAt(0).toLowerCase() + string.slice(1);
			};

			// fetch initial data for 1st time
			$scope.filterResult();
        }]);appcontrollers.controller('AlertsTabController',
    ['$window', '$scope', '$rootScope', '$location', '$filter', '$timeout', 'maxFieldLengths', 'ngTableParams',
        'DashboardNumberTags', 'DashboardService', 'AccountService', 'CompanyService',
        function ($window, $scope, $rootScope, $location, $filter, $timeout, maxFieldLengths, ngTableParams,
                  DashboardNumberTags, DashboardService, AccountService, CompanyService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            $scope.title = "Alerts Tab";

            AccountService.getCompanyAccountData({bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId}, function (data) {
                $scope.account = data;
            });

            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.itemsPage = 20;
            $scope.maxSize = 5;
            $scope.thisUser = JSON.parse($window.sessionStorage.user);

            if (typeof $scope.filterCriteria == 'undefined') {
                $scope.filterCriteria = {
                    bedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
                    pageNumber: 1,
                    sortDir: "", // asc, desc
                    sortedBy: "",
                    filterValue: "" // text to filter on
                };
            }

            $scope.$watch('filterCriteria.filterValue', function (search) {
                if (typeof searchCallbackTimeout != 'undefined') $timeout.cancel(searchCallbackTimeout);

                searchCallbackTimeout = $timeout(function () {
                    if (search) {
                        if (search.length > 2) {
                            // refresh
                            $scope.currentPage = 1;
                            $scope.pageChanged();
                            $scope.oldSearch = search;
                        }
                    }
                    else if (isSearchChanged(search)) {
                        // refresh
                        $scope.currentPage = 1;
                        $scope.pageChanged();
                        $scope.oldSearch = search;
                    }
                }, 1000);
            }, true);

            // The function that is responsible of fetching the result from the
            // server and setting the grid to the new result
            $scope.fetchResult = function (success) {
                $rootScope.range = "items=" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + 1) + "-" + (($scope.filterCriteria.pageNumber - 1) * $scope.itemsPage + $scope.itemsPage);

                DashboardService.companiesalert($scope.filterCriteria, function (data, headers) {
                    if (typeof data != 'undefined' && data.length > 0) {
                        if (hasRole($scope.thisUser.roles, 'admin_sbdr') || hasRole($scope.thisUser.roles, 'admin_sbdr_hoofd')) {
                            $scope.adminAlertsCollection = data;
                        } else {
                            $scope.alertsCollection = data;
                        }

                        $scope.totalItems = paging_totalItems(headers("Content-Range"));
                    }
                    else {
                        $scope.adminAlertsCollection = null;
                        $scope.alertsCollection = null;
                        $scope.totalItems = 0;
                    }
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);

                    success();
                }, function () {
                    $scope.adminAlertsCollection = [];
                    $scope.alertsCollection = [];
                    $scope.totalItems = 0;
                    DashboardNumberTags.setNrOfAlerts($scope.totalItems);
                });
            };

            // Data table functions
            $scope.filterResult = function () {
                return $scope.fetchResult(function () {
                    //The request fires correctly but sometimes the ui doesn't update, that's a fix
                    $scope.filterCriteria.pageNumber = 1;
                    $scope.currentPage = 1;

                    // rootscope range delete
                    delete $rootScope.range;

                });
            };

            $scope.hasItems = function () {
                return DashboardNumberTags.getNrOfAlerts() > 0;
            };

            $scope.ignorealert = function (type, alertId, bedrijfId) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                } else {
                    deleteAlert(alertId);
                }
            };

            $scope.pageChanged = function () {
                $scope.filterCriteria.pageNumber = $scope.currentPage;
                $scope.fetchResult(function () {
                    //Nothing to do..

                    // rootscope range delete
                    delete $rootScope.range;
                });
            };

            $scope.setPage = function (pageNo) {
                $scope.currentPage = pageNo;
            };

            //$scope.requestreport = function (bedrijfId, monitoringBedrijfId, indMonitoringBedrijf) {
            //    CompanyService.updateMonitoring({
            //        vanBedrijfId: monitoringBedrijfId,
            //        doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId
            //    }, function (result) {
            //        if (typeof result.errorCode !== "undefined")
            //            $scope.error = result.errorCode + " " + result.errorMsg;
            //        else {
            //            searchurl = '$dashboard$alertstab';
            //            $location.path('/report/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '0' + '/' + searchurl);
            //        }
            //    });
            //};

			$scope.viewAdminDetails = function(refNoPRefix){
				supportAlertDetail(refNoPRefix);
			};

            $scope.viewdetails = function (type, alertId, monitoringId, meldingId, supportId, bedrijfId, refNoPrefix) {
                if (type == 'MON') {
                    updateMonitoring(bedrijfId);
                    monitoringDetail(monitoringId);
                }
                else if (type == 'VER') {
                    deleteAlert(alertId);
                    notificationReadOnly(meldingId, bedrijfId);
                }
                else if (type == 'SUP') {
					deleteAlert(alertId);
                    supportAlertDetail(refNoPrefix);
                }
            };

			deleteAlert = function (alertId) {
				// remove alert record
				CompanyService.deleteAlert(alertId, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};

			isSearchChanged = function (search) {
				if (typeof $scope.oldSearch == 'undefined') {
					$scope.oldSearch = search;
					return false;
				} else return $scope.oldSearch != search;
			};

			monitoringDetail = function (monitoringId) {
				delete $scope.error;
				$location.path('/monitoringdetails/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + monitoringId + '/$dashboard$alertstab');
				$location.url($location.path());
			};

			notificationReadOnly = function (meldingId, bedrijfId) {
				delete $scope.error;
				0;

				$location.path('/notifycompany/' + JSON.parse($window.sessionStorage.user).bedrijfId + '/' + bedrijfId + '/' + '$dashboard$alertstab/' + meldingId + '/true');
				$location.url($location.path());
			};

			supportAlertDetail = function (refNoPrefix) {
				delete $scope.error;
				$location.path('/support/detail/' + refNoPrefix+'/$dashboard$supportticketstab');
				$location.url($location.path());
			};

			updateMonitoring = function (bedrijfId) {
				// update monitoring
				CompanyService.updateMonitoring({
					vanBedrijfId: bedrijfId,
					doorBedrijfId: JSON.parse($window.sessionStorage.user).bedrijfId,
					gebruikerId: JSON.parse($window.sessionStorage.user).userId
				}, function (result) {
					if (typeof result.errorCode !== "undefined")
						$scope.error = result.errorCode + " " + result.errorMsg;
					else {
						// refresh
						$scope.currentPage = 1;
						$scope.pageChanged();
					}
				});
			};
			
			$scope.lowercaseFirstLetter = function (string) {
			    return string.charAt(0).toLowerCase() + string.slice(1);
			};

			// fetch initial data for 1st time
			$scope.filterResult();
        }]);appcontrollers.controller('VermeldingenInfoController',
	['$window', '$routeParams', '$scope', '$rootScope',
		'$location', 'UserService',
		function ($window, $routeParams, $scope, $rootScope,
				  $location, UserService) {

			if (typeof $scope.init == 'undefined') {
				$scope.init = true;

				if (!$routeParams.eigenBedrijfId || !$routeParams.eigenBedrijfId || $routeParams.eigenBedrijfId == null || $routeParams.eigenBedrijfId == 'undefined')
					eigenBedrijfIdentifier = null;
				else
					eigenBedrijfIdentifier = $routeParams.eigenBedrijfId;

				if (!$routeParams.bedrijfId || !$routeParams.bedrijfId || $routeParams.bedrijfId == null || $routeParams.bedrijfId == 'undefined')
					bedrijfIdentifier = null;
				else
					bedrijfIdentifier = $routeParams.bedrijfId;

				if (!$routeParams.meldingId || !$routeParams.meldingId || $routeParams.meldingId == null || $routeParams.meldingId == 'undefined')
					meldingId = null;
				else
					meldingId = $routeParams.meldingId;

				if ($routeParams.searchurl)
					searchurl = $routeParams.searchurl;
				else
					searchurl = '$dashboard';

				$scope.nietMeerWeergeven = !$rootScope.isShowHelp($rootScope.HELP_VERMELDINGINFO);

				if ($scope.nietMeerWeergeven)
					notifyCompany();
			}

			var bedrijfIdentifier;
			var eigenBedrijfIdentifier;
			var meldingId;

			$scope.gotoVermeldingen = function () {
				if ($scope.nietMeerWeergeven)
					saveShowHelp(notifyCompany);

				notifyCompany();
			};

			$scope.returnToSearch = function () {
				url = gotoDurl(searchurl);
				if (url != null) {
					$location.path(url);
					$location.url($location.path());
				}
			};

			notifyCompany = function () {
				if (meldingId != null)
					$location.path('/notifycompany/' + eigenBedrijfIdentifier + '/' + bedrijfIdentifier + '/' + searchurl + '/' + meldingId);
				else
					$location.path('/notifycompany/' + eigenBedrijfIdentifier + '/' + bedrijfIdentifier + '/' + searchurl);
				$location.url($location.path());
			};

			saveShowHelp = function (callback) {
				delete $scope.error;

				$rootScope.saveShowHelpDialog($rootScope.HELP_VERMELDINGINFO, $scope.nietMeerWeergeven, function (result) {
					if (result.error != null) {
						$scope.error = result.error;
					} else {
						callback();
					}
				});
			};
		}]);
appcontrollers.controller('WebsiteparamController',
    ['$window', '$scope', '$rootScope', '$location', '$anchorScroll', '$routeParams', 'maxFieldLengths', 'websiteparamData', 'WebsiteService',
        function ($window, $scope, $rootScope, $location, $anchorScroll, $routeParams, maxFieldLengths, websiteparamData, WebsiteService) {

    		$scope.maxFieldLengths = maxFieldLengths;
    		
            if (typeof $scope.init == 'undefined') {
                $scope.init = true;

                $scope.websiteparam = websiteparamData.websiteparam;
            }

            $scope.submitWebsiteparam = function () {
                delete $scope.websiteparamSavedOk;

                if (!$scope.checkWebsiteparamFormInvalid()) {
                    $scope.formwebsiteparam.$setPristine();
                    WebsiteService.saveWebsiteparam($scope.websiteparam, function (response) {
                        if (typeof response.errorCode != 'undefined' || typeof $rootScope.error !='undefined') {
                            $scope.error = response.errorCode + ' ' + response.errorMsg;
                        } else {
                            $scope.websiteparamSavedOk = true;
                        }
                    })
                }
            };

            $scope.isInvalid = function (targetForm, targetField) {
                //als er een fout is, moet er true teruggegeven worden
                var result_invalid = targetForm[targetField].$invalid;
                var result_dirty = targetForm[targetField].$dirty;

                if (result_dirty) {
                    delete $scope.error;
                }                

                return result_dirty && result_invalid;
            }
            ;

            $scope.checkWebsiteparamFormInvalid = function () {
                //als er een fout is, moet er true teruggegeven worden
                return $scope.formwebsiteparam.$invalid || 
                	$scope.isInvalid($scope.formwebsiteparam, 'startupslastweek') ||
                	$scope.isInvalid($scope.formwebsiteparam, 'startupsthisyear') ||
                	$scope.isInvalid($scope.formwebsiteparam, 'vermeldingenlastweek') ||
                	$scope.isInvalid($scope.formwebsiteparam, 'vermeldingenthisyear') ||
                	$scope.isInvalid($scope.formwebsiteparam, 'storingen');
            }
        }
    ])
;