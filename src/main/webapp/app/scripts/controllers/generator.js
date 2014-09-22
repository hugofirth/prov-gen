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
        $scope.examples = [
            {
                "description":"Simple usage and generation chain",
                "seed":
                "document" + "\n" +
                "    entity(e1, [version=\"original\"])" + "\n" +
                "    activity(a1, [fct=\"edit\"])" + "\n" +
                "    entity(e2)" + "\n" +
                "    activity(a2,[fct=\"edit\"])" + "\n" +
                "    entity(e3)" + "\n" +
                "    used(r1;a1,e1)" + "\n" +
                "    used(r2;a2,e2)" + "\n" +
                "    wasGeneratedBy(r3;e2,a1)" + "\n" +
                "    wasGeneratedBy(r4;e3,a2)" + "\n" +
                "endDocument",
                "constraints":
                "an Entity must have property(\"version\"=\"original\") with probability 0.1;" + "\n" +
                "an Entity must have relationship \"WasGeneratedBy\" exactly 1 times unless it has property(\"version\"=\"original\");" + "\n" +
                "an Activity must have degree at most 2;an Entity must have relationship \"Used\" exactly 1 times;"
            },
            {
                "description":"Complex usage and generation chain with Agents",
                "seed":
                "document" + "\n" +
                "    entity(e1, [type=\"Document\", version=\"original\"])" + "\n" +
                "    entity(e2, [type=\"Document\"])" + "\n" +
                "    entity(e3, [type=\"Document\"])" + "\n" +
                "    activity(a1, [type=\"create\"])" + "\n" +
                "    activity(a2, [type=\"edit\"])" + "\n" +
                "    activity(a3, [type=\"edit\"])" + "\n" +
                "    agent(ag, [type=\"Person\"])" + "\n" +
                "    used(a2, e1)" + "\n" +
                "    used(a3, e2)" + "\n" +
                "    wasGeneratedBy(e2, a2, [fct=\"save\"])" + "\n" +
                "    wasGeneratedBy(e1, a1, [fct=\"publish\"])" + "\n" +
                "    wasGeneratedBy(e3, a3, [fct=\"save\"])" + "\n" +
                "    wasAssociatedWith(a3, ag, [role=\"contributor\"])" + "\n" +
                "    wasAssociatedWith(a2, ag, [role=\"contributor\"])" + "\n" +
                "    wasAssociatedWith(a1, ag, [role=\"creator\"])" + "\n" +
                "    wasDerivedFrom(e2, e1)" + "\n" +
                "    wasDerivedFrom(e3, e2)" + "\n" +
                "endDocument",
                "constraints":
                "an Entity must have relationship \"WasDerivedFrom\" exactly 2 times unless it has property(\"version\"=\"original\");" + "\n" +
                "the Entity(e1) must not have relationship \"WasDerivedFrom\" with the Entity(e2) unless e1 has relationship \"Used\" with the Activity(a) and e2 has the relationship \"WasGeneratedBy\" with the Activity(a);" + "\n" +
                "an Entity must have relationship \"WasGeneratedBy\" exactly 1 times;" + "\n" +
                "an Entity must have property(\"version\"=\"original\") with probability 0.05;" + "\n" +
                "an Entity must have out degree at most 2;" + "\n" +
                "an Activity must have relationship \"Used\" at most 1 times;" + "\n" +
                "an Activity must have property(\"type\"=\"create\") with probability 0.01;" + "\n" +
                "an Activity must have relationship \"WasAssociatedWith\" exactly 1 times;" + "\n" +
                "an Activity must have relationship \"Used\" exactly 1 times unless it has property(\"type\"=\"create\");" + "\n" +
                "an Activity must have relationship \"WasGeneratedBy\" exactly 1 times;" + "\n" +
                "an Agent must have relationship \"WasAssociatedWith\" with probability 0.1;" + "\n" +
                "an Agent must have relationship \"WasAssociatedWith\" between 1, 120 times with distribution gamma(1.3, 2.4);"
            }
        ];

        $scope.generator = {};

        $scope.selectedExample = {};
        $scope.selectExample = function() {
            $scope.generator = $scope.selectedExample;
        };

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
            //Initiate default value for includeDB
            if (typeof $scope.generator.includeDB === 'undefined')
            {
                $scope.generator.includeDB = false;
            }
            var deferred = $q.defer();
            $http({ method: 'POST', url: '/api/v1/graphs', data: $scope.generator})
                .success(function (data) {
                    deferred.resolve(data);
                });
            return deferred.promise;
        };

    }]);
