'use strict';

/**
 * @ngdoc function
 * @name provGenApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the provGenApp
 */
angular.module('provGenApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
