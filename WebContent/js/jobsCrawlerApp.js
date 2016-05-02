var app = angular.module('jobsCrawlerApp', ['ngAnimate', 'ui.bootstrap']);


app.controller('jobsSearch', function($scope, $http) {
	$scope.jobs = [];
	$scope.jobTitle = "";
	$scope.jobLocation = "";
	$scope.loadingJobResults = false;
	
	$scope.serverAddress = '10.240.32.167';
	//$scope.serverAddress = 'localhost';
	$scope.showModal = false;
	 
	$scope.registerUserName = "";
	$scope.registerPassword = "";
	$scope.registerPhone = "";
	$scope.registerKeywords = "";
	  
	  $scope.doRegister = function() {
	    	console.log("get jobs");
	    	
	    	alert("User Registered Successfully");
	    	
	    	$scope.registerUserName = "";
	    	$scope.registerPassword = "";
	    	$scope.registerPhone = "";
	    	$scope.registerKeywords = "";
	    	
	    	$scope.status.open = false;
	    	
	    	
	    };
	
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

app.controller('ModalInstanceCtrl', function ($scope, $modalInstance, items) {

	  $scope.items = items;
	  $scope.selected = {
	    item: $scope.items[0]
	  };

	  $scope.ok = function () {
	    $modalInstance.close($scope.selected.item);
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };
	});