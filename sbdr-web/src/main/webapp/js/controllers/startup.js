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
	