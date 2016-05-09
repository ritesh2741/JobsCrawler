var app = angular.module('jobsCrawlerApp', ['ngAnimate', 'ui.bootstrap']);


app.controller('jobsSearch', function($scope, $http) {
	$scope.jobs = [];
	$scope.indeedjobs = [];
	$scope.dicejobs = [];
	$scope.careerjetjobs = [];
	$scope.careerbuilderjobs = [];
	$scope.jobTitle = "";
	$scope.jobLocation = "";
	$scope.registerUserName = "";
	$scope.registerPhone = "";
	$scope.registerKeywords = "";
	$scope.loadingJobResults = false;
	
	//$scope.serverAddress = '192.168.0.10';
	//$scope.serverAddress = '10.240.32.167';
	$scope.serverAddress = 'localhost';
	$scope.showModal = false;
	$scope.status ={open : false};
	 
	
	$scope.initiatedSearchesCount = 0;
	  
	  $scope.doRegister = function() {
	    	console.log("Do Register : " + $scope);
	    	
	    	//alert("User Registered Successfully");
	    	
	    	$http({
	            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/doRegister',
	            dataType: 'json',
	            method: 'POST',
	            data: JSON.stringify( {
	            	emailId : $scope.registerUserName,
	    	    	phoneNumber : $scope.registerPhone,
	    	    	keywords : $scope.registerKeywords,
	    	    	location : $scope.registerLocation
	            }),
	            headers: {
	                "Content-Type": "application/json"
	            }
	        }).success(function(response){
	        	
	        	$scope.status ={open : false};
	        	
	        	$scope.registerUserName = "";
		    	$scope.registerPhone = "";
		    	$scope.registerKeywords = "";
		    	$scope.registerLocation = "";
	        	
	            //$scope.registerResponse = response;
	        	
	        	alert("Notification set successfully.");
	            
	        }).error(function(error){
	        	
	            //$scope.registerResponse = error;
	        	
	        	alert("An error has occurred. Please try again later.");
	            
	        });
	    	
	    	
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
    	   	
//    	$http({
//            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getJobs',
//            dataType: 'json',
//            method: 'POST',
//            data: JSON.stringify( {
//            	title: $scope.jobTitle,
//            	location: $scope.jobLocation
//            }),
//            headers: {
//                "Content-Type": "application/json"
//            }
//        }).success(function(response){
//            //$scope.response = response;
//            $scope.jobs = response;
//            console.log(response);
//            
//            $scope.loadingJobResults = false;
//            
//        }).error(function(error){
//            $scope.error = error;
//            $scope.loadingJobResults = false;
//        });
    	
    	$scope.initiatedSearchesCount++;
    	$http({
            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getIndeedJobs',
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
            $scope.indeedjobs = response;
            console.log(response);
            
            //$scope.loadingJobResults = false;
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
            
        }).error(function(error){
            $scope.error = error;
            //$scope.loadingJobResults = false;
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
        });
    	
    	$scope.initiatedSearchesCount++;
    	$http({
            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getDiceJobs',
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
            $scope.dicejobs = response;
            console.log(response);
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
            //$scope.loadingJobResults = false;
            
        }).error(function(error){
            $scope.error = error;
            //$scope.loadingJobResults = false;
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
        });
    	
    	$scope.initiatedSearchesCount++;
    	$http({
            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getCareerJetJobs',
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
            $scope.careerjetjobs = response;
            console.log(response);
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
            //$scope.loadingJobResults = false;
            
        }).error(function(error){
            $scope.error = error;
            //$scope.loadingJobResults = false;
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
        });
    	
    	$scope.initiatedSearchesCount++;
    	$http({
            url: 'http://' + $scope.serverAddress + ':8080/JobsCrawler/getCareerBuilderJobs',
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
            $scope.careerbuilderjobs = response;
            console.log(response);
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
            
            
            
        }).error(function(error){
            $scope.error = error;
            
            $scope.initiatedSearchesCount--;
            
            if($scope.initiatedSearchesCount == 0) {
            	$scope.loadingJobResults = false;
            }
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