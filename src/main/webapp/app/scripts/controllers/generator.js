'use strict';

/**
 * @ngdoc function
 * @name provGenApp.controller:GeneratorCtrl
 * @description
 * # GeneratorCtrl
 * Controller of the provGenApp
 */
angular.module('provGenApp')
  .controller('GeneratorCtrl', function ($scope, $q, $http) {

    $scope.generator = {};

    $scope.generatedFiles = [];

    $scope.getGeneratedFile = function () {
        console.log("Hello :D");
        var promise = generate();

        promise.then(function (result) {
            $scope.generatedFiles.push(result);
            console.log(result);
        });

        return promise;
    };

    var generate = function () {
        var deferred = $q.defer();

        $http({ method: 'POST', url: '/demo/graphs', data: $scope.generator})
            .success(function (data) {
                deferred.resolve(data);
            });
        return deferred.promise;
    };

  });
