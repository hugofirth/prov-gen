'use strict';

/**
 * @ngdoc function
 * @name provGenApp.controller:GeneratorCtrl
 * @description
 * # GeneratorCtrl
 * Controller of the provGenApp
 */
angular.module('provGenApp')
    .controller('GeneratorCtrl', ['$scope', '$q', '$http', '$window', 'usSpinnerService', function ($scope, $q, $http, $window, usSpinnerService) {
        $scope.generator = {};
        $scope.generatedFiles = [];

        $scope.getGeneratedFile = function () {
            usSpinnerService.spin('spinner-1');
            var promise = generate();
            promise.then(function (result) {
                $window.open('/api/v1/graphs/'+result.name);
                console.log(result);
                usSpinnerService.stop('spinner-1');
            });

            return promise;
        };

        var generate = function () {
            var deferred = $q.defer();
            $http({ method: 'POST', url: '/api/v1/graphs', data: $scope.generator})
                .success(function (data) {
                    deferred.resolve(data);
                });
            return deferred.promise;
        };

    }]);
