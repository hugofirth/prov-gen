'use strict';

/**
 * @ngdoc function
 * @name provGenApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the provGenApp
 */
angular.module('provGenApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
