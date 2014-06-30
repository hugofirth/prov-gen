'use strict';

/**
 * @ngdoc overview
 * @name provGenApp
 * @description
 * # provGenApp
 *
 * Main module of the application.
 */
angular
  .module('provGenApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/demo', {
        templateUrl: 'views/generator.html',
        controller: 'GeneratorCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
