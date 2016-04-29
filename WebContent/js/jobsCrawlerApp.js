var app = angular.module('jobsCrawlerApp', ['ngAnimate', 'ui.bootstrap']);
app.controller('jobsSearch', function($scope, $http) {
	$scope.jobs = [];
	$scope.jobTitle = "";
	$scope.jobLocation = "";
	$scope.loadingJobResults = false;
	
	$scope.serverAddress = '10.240.32.167';
		  
	$scope.getJobSuggestions = function(val) {
		    return $http.post('http://' + $scope.serverAddress + ':8080/JobsCrawler/getJobTitleSuggestions', 
		    JSON.stringify( {
	            	title: $scope.jobTitle
	            })
		    ).then(function(response){
		    	console.log(response);
		      return response.data;
		    });
		  };  
		  
	$scope.getJobLocations = function(val) {
			    return $http.post('http://' + $scope.serverAddress + ':8080/JobsCrawler/getJobLocations', 
			    JSON.stringify( {
	            		location: $scope.jobLocation
		            })
			    ).then(function(response){
			    	console.log(response);
			      return response.data;
			    });
			  };  
    
    $scope.getJobs = function() {
    	console.log("get jobs");
    	
    	$scope.loadingJobResults = true;
    	   	
    	$http({
            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getJobs',
            dataType: 'json',
            method: 'POST',
            data: JSON.stringify( {
            	title: $scope.jobTitle,
            	location: $scope.jobLocation
            }),
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response){
            //$scope.response = response;
            $scope.jobs = response;
            console.log(response);
            
            $scope.loadingJobResults = false;
            
        }).error(function(error){
            $scope.error = error;
            $scope.loadingJobResults = false;
        });
    	
    };
});