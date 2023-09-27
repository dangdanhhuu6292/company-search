appcontrollers.controller('SupportFaqController',
    ['$scope', '$location',
        function ($scope, $location) {

            $scope.gotoSupport = function () {
                $location.path('/support');
                $location.url($location.path());
            };
        }]);