package com.jobcrawler.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import com.jobcrawler.dto.JobDTO;

@RestController
public class JobCrawlerRestService {

	@RequestMapping(value = "/getJobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<JobDTO> getAllJobsFromIndeed(@RequestBody JobDTO jobDTO) throws IOException {

		System.out.println(jobDTO.getTitle() + " " + jobDTO.getLocation());

		List<JobDTO> jobs = new ArrayList<JobDTO>();
		WebClient webClient = new WebClient();
		// webClient.setJavaScriptErrorListener(null);
		// webClient.setCssErrorHandler(null);

		HtmlPage htmlPage = webClient.getPage("http://www.indeed.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("what");
		htmlTextInput1.setValueAttribute(jobDTO.getTitle());
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("where");
		htmlTextInput2.setValueAttribute(jobDTO.getLocation());
		HtmlSubmitInput findJobButton = (HtmlSubmitInput) htmlPage.getElementById("fj");
		HtmlPage jobsPage = findJobButton.click();
		// System.out.println(jobsPage);

		List<HtmlAnchor> anchors = jobsPage.getAnchors();
		DomNodeList<DomElement> companyAndlocationSpans = jobsPage.getElementsByTagName("span");

		for (HtmlAnchor anchor : anchors) {

			// System.out.println(anchor.getAttribute("class"));

			if (anchor.getAttribute("class").equals("jobtitle turnstileLink")) {
				System.out.println("Title : " + anchor.getTextContent());
				System.out.println("Link : " + anchor.getAttribute("href"));

				JobDTO jobDto = new JobDTO();
				jobDto.setTitle(anchor.getTextContent());
				jobDto.setLink("http://www.indeed.com/" + anchor.getAttribute("href"));

				jobs.add(jobDto);
			}

		}

		int i = 0;

		for (DomElement companyAndlocationSpan : companyAndlocationSpans) {

			// System.out.println(anchor.getAttribute("class"));

			if (companyAndlocationSpan.getAttribute("class").equals("company")) {
				System.out.println("Company : " + companyAndlocationSpan.getTextContent());
				jobs.get(i).setCompany(companyAndlocationSpan.getTextContent());
			} else if (companyAndlocationSpan.getAttribute("class").equals("location")) {
				System.out.println("Location : " + companyAndlocationSpan.getTextContent());
				jobs.get(i).setLocation(companyAndlocationSpan.getTextContent());
			} else if (companyAndlocationSpan.getAttribute("class").equals("summary")) {
				System.out.println("Description : " + companyAndlocationSpan.getTextContent());
				jobs.get(i).setDescription(companyAndlocationSpan.getTextContent());
				i++;

				if (i == jobs.size()) {
					break;
				}
			}

		}

		webClient.closeAllWindows();
		
		jobs.addAll(getDiceJobs(jobDTO));

		// return jobDto;
		return jobs;
	}

	@RequestMapping(value = "/getJobTitleSuggestions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getJobTitleSuggestions(@RequestBody JobDTO jobDTO)
			throws IOException, InterruptedException {

		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		System.out.println(jobDTO.getTitle());

		String title = jobDTO.getTitle();

		title = title.trim().replace("[ ]+", " ").replace(" ", "+");

		List<String> jobSuggestionsList = new ArrayList<String>();

		// jobSuggestionsList.add("Job1");
		// jobSuggestionsList.add("Job2");
		// jobSuggestionsList.add("Job3");

		String formedRequestUrl = "http://www.indeed.com/rpc/suggest?what=true&from=hp&tk=1ahgavqn85t33bq8&cb=what_ac.cb&q="
				+ title + "&caret=" + title.length();

		System.out.println("formedRequestUrl : " + formedRequestUrl);
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(formedRequestUrl, String.class);

		result = result.replace("\\u003Cb", "");

		result = result.replace("/b\\u003E", "");

		result = result.replace("\\u003C", "");

		result = result.replace("\\u003E", "");

		System.out.println(result);

		String newString = result.substring(20 + title.length() + 1, result.indexOf("]"));

		String[] splits = newString.split("\",\"");

		for (int i = 0; i < splits.length; i++) {

			String receivedTitle = splits[i].replace("\"", "");
			receivedTitle = receivedTitle.trim();

			if (receivedTitle.length() > 0) {
				System.out.println("Received Title : " + receivedTitle);
				jobSuggestionsList.add(splits[i].replace("\"", ""));
			}

		}

		return jobSuggestionsList;
	}
	
	@RequestMapping(value = "/getJobLocations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getJobLocations(@RequestBody JobDTO jobDTO)
			throws IOException, InterruptedException {

		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		System.out.println(jobDTO.getLocation());

		String location = jobDTO.getLocation();

		location = location.trim().replace("[ ]+", " ").replace(" ", "+");

		List<String> jobLocationsList = new ArrayList<String>();

		// jobSuggestionsList.add("Job1");
		// jobSuggestionsList.add("Job2");
		// jobSuggestionsList.add("Job3");
		//http://www.indeed.com/rpc/suggest?from=hp&tk=1ahgavqn85t33bq8&cb=where_ac.cb&q=Akro&caret=4
		//http://www.indeed.com/rpc/suggest?from=hp&tk=1ahgavqn85t33bq8&cb=where_ac.cb&q=Akron%2C+&caret=7
		String formedRequestUrl = "http://www.indeed.com/rpc/suggest?from=hp&tk=1ahgavqn85t33bq8&cb=where_ac.cb&q="
				+ location + "&caret=" + location.length();

		System.out.println("formedRequestUrl : " + formedRequestUrl);
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(formedRequestUrl, String.class);

		result = result.replace("\\u003Cb", "");

		result = result.replace("/b\\u003E", "");

		result = result.replace("\\u003C", "");

		result = result.replace("\\u003E", "");

		System.out.println(result);

		String newString = result.substring(21 + location.length() + 1, result.indexOf("]"));

		String[] splits = newString.split("\",\"");

		for (int i = 0; i < splits.length; i++) {

			String receivedTitle = splits[i].replace("\"", "");
			receivedTitle = receivedTitle.trim();

			if (receivedTitle.length() > 0) {
				System.out.println("Received Title : " + receivedTitle);
				jobLocationsList.add(splits[i].replace("\"", ""));
			}

		}

		return jobLocationsList;
	}
	
	public List<JobDTO> getDiceJobs(JobDTO inputJobDto) throws IOException {
List<JobDTO> diceJobDto = new ArrayList<>();
		
		WebClient webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);

		HtmlPage htmlPage = webClient.getPage("http://www.dice.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("search-field-keyword");
		htmlTextInput1.setValueAttribute(inputJobDto.getTitle());
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("search-field-location");
		htmlTextInput2.setValueAttribute(inputJobDto.getLocation());
		
		System.out.println("After Setting Text");
		
		List<HtmlButton> buttons = (ArrayList<HtmlButton>)htmlPage.getByXPath("//button[@type='submit']");
		
		System.out.println(buttons);
		
		HtmlButton findJobButton =  buttons.get(1);
		
		System.out.println(findJobButton);
		
		HtmlPage jobsPage = findJobButton.click();
		
		System.out.println(jobsPage.getUrl());
		
		List<DomElement> companyAndlocationDivs = (ArrayList<DomElement>) jobsPage.getByXPath("//div[@class='serp-result-content']");
		
		for(DomElement companyAndlocationDiv: companyAndlocationDivs) {
			
			DomNodeList<HtmlElement> anchors = companyAndlocationDiv.getElementsByTagName("a");
			
			JobDTO jobDto = new JobDTO();
			
			for(HtmlElement anchor : anchors) {
				
				if(anchor.getAttribute("id").startsWith("position")) {
					
					jobDto.setTitle(anchor.getTextContent().trim().replace("\n", ""));
					System.out.println("Title : " + jobDto.getTitle());
					
					jobDto.setLink(anchor.getAttribute("href"));
					System.out.println("Link : " + jobDto.getLink());
				}
				else if(anchor.getAttribute("id").startsWith("company")) {
					
					jobDto.setCompany(anchor.getTextContent().trim().replace("\n", ""));
					System.out.println("Company : " + jobDto.getCompany());
				}
				
			}
			
			DomNodeList<HtmlElement> divs = companyAndlocationDiv.getElementsByTagName("div");
			
			for(HtmlElement div : divs) {
				
				if(div.getAttribute("class").startsWith("shortdesc")) {
					jobDto.setDescription(div.getTextContent().trim().replace("\n", ""));
					System.out.println("Description : " + jobDto.getDescription());
					break;
				}
				
			}
			
			DomNodeList<HtmlElement> lis = companyAndlocationDiv.getElementsByTagName("li");
			
			for(HtmlElement li : lis) {
				
				if(li.getAttribute("class").equals("location")) {
					jobDto.setLocation(li.getTextContent().trim().replace("\n", ""));
					System.out.println("Location : " + jobDto.getLocation());
					break;
				}
				
			}	
			
			
			//System.out.println("Title : " + ((DomElement)companyAndlocationDiv.getElementsByTagName("a[@class='dice-btn-link']")).getTextContent().trim().replace("\n", ""));
			
			//System.out.println("Text Content : " + companyAndlocationDiv.getTextContent());
			
			diceJobDto.add(jobDto);
			
		}
		
		//System.out.println(companyAndlocationDivs);
		
		// System.out.println(jobsPage);
		
		return diceJobDto;
		
	}

}

