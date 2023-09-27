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