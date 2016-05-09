package com.jobcrawler.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import com.jobcrawler.dto.JobDTO;
import com.jobcrawler.dto.RegisterDTO;

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

				jobDto.setDataFrom("http://www.indeed.com/");
				
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
		
		jobs.addAll(getCareerJetJobs(jobDTO));
		
		jobs.addAll(getCareerBuilderJobs(jobDTO));

		// return jobDto;
		return jobs;
	}
	
	@RequestMapping(value = "/getIndeedJobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<JobDTO> getJobsFromIndeed(@RequestBody JobDTO jobDTO) throws IOException {

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

				jobDto.setDataFrom("http://www.indeed.com/");
				
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
		
		// return jobDto;
		return jobs;
	}
	
	@RequestMapping(value = "/getDiceJobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<JobDTO> getJobsFromDice(@RequestBody JobDTO jobDTO) throws IOException {

		System.out.println(jobDTO.getTitle() + " " + jobDTO.getLocation());

		List<JobDTO> jobs = new ArrayList<JobDTO>();
		
		jobs.addAll(getDiceJobs(jobDTO));
		
		
		// return jobDto;
		return jobs;
	}
	
	@RequestMapping(value = "/getCareerJetJobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<JobDTO> getJobsFromCareerJet(@RequestBody JobDTO jobDTO) throws IOException {

		System.out.println(jobDTO.getTitle() + " " + jobDTO.getLocation());

		List<JobDTO> jobs = new ArrayList<JobDTO>();
		
		jobs.addAll(getCareerJetJobs(jobDTO));
		
		return jobs;
	}
	
	@RequestMapping(value = "/getCareerBuilderJobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<JobDTO> getJobsFromCareerBuilder(@RequestBody JobDTO jobDTO) throws IOException {

		System.out.println(jobDTO.getTitle() + " " + jobDTO.getLocation());

		List<JobDTO> jobs = new ArrayList<JobDTO>();
		
		jobs.addAll(getCareerBuilderJobs(jobDTO));
		
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
		webClient.getOptions().setJavaScriptEnabled(false);

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
			
			jobDto.setDataFrom("http://www.dice.com/");
			
			diceJobDto.add(jobDto);
			
		}
		
		//System.out.println(companyAndlocationDivs);
		
		// System.out.println(jobsPage);
		
		return diceJobDto;
		
	}
	
	public List<JobDTO> getCareerJetJobs(JobDTO inputJobDto) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		List<JobDTO> jobDtos = new ArrayList<JobDTO>();
		WebClient webClient = new WebClient();
		HtmlPage htmlPage = webClient.getPage("http://www.careerjet.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("sb_s");
		htmlTextInput1.setValueAttribute(inputJobDto.getTitle());
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("sb_l");
		htmlTextInput2.setValueAttribute(inputJobDto.getLocation());
		HtmlForm submitForm = (HtmlForm) htmlPage.getElementById("search_form");
		//List<HtmlButton> searchSubmitButtons = (List<HtmlButton>) htmlPage.getByXPath("//button[@type='submit']");
		//System.out.println(jobsPage);
		
		System.out.println("submitForm:" + submitForm);
		
		HtmlElement button = (HtmlElement) htmlPage.createElement("button");
		button.setAttribute("type", "submit");
		
		submitForm.appendChild(button);
		
		HtmlPage jobsPage = button.click();
		
		System.out.println(" jobsPage : " + jobsPage.getUrl());
		
		//HTMLDivElement divResultsContainer = (HTMLDivElement) jobsPage.getElementById("serp");
		List<HtmlDivision> resultDivs = (List<HtmlDivision>) jobsPage.getByXPath( "//div[@class='job']");
		
		System.out.println(resultDivs);
		
		for(int i=0; i<resultDivs.size(); i++) {
			
			JobDTO jobDto = new JobDTO();
			
			HtmlDivision htmlDivElement = resultDivs.get(i);
			
			System.out.println("Position : " + htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
			jobDto.setTitle(htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
			System.out.println("Location : " + "http://www.careerjet.com" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			
			jobDto.setLink("http://www.careerjet.com" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			
			System.out.println("Description : " + ((HtmlElement)htmlDivElement.getByXPath("//div[@class='advertise_compact']").get(i)).getTextContent());
			
			jobDto.setDescription(((HtmlElement)htmlDivElement.getByXPath("//div[@class='advertise_compact']").get(i)).getTextContent());
			
			System.out.println("Location : " + ((HtmlElement)htmlDivElement.getByXPath("//span[@itemprop='jobLocation']").get(i)).getTextContent().replace("\n", ""));
			
			jobDto.setLocation(((HtmlElement)htmlDivElement.getByXPath("//span[@itemprop='jobLocation']").get(i)).getTextContent().replace("\n", ""));
			
			System.out.println("Company Name : " + ((HtmlElement)htmlDivElement.getByXPath("//span[@class='company_compact']").get(i)).getTextContent());
			
			jobDto.setCompany(((HtmlElement)htmlDivElement.getByXPath("//span[@class='company_compact']").get(i)).getTextContent());
			
			jobDto.setDataFrom("http://www.careerjet.com/");
			
			jobDtos.add(jobDto);
			
		}
		
		webClient.closeAllWindows();
		
		return jobDtos;
	}

	public List<JobDTO> getCareerBuilderJobs(JobDTO inputJobDto) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		List<JobDTO> jobDtos = new ArrayList<JobDTO>();
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage htmlPage = webClient.getPage("http://www.careerbuilder.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("keywords");
		htmlTextInput1.setValueAttribute("Java Developer");
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("location");
		htmlTextInput2.setValueAttribute("Akron, OH");
		//HtmlForm submitForm = (HtmlForm) htmlPage.getElementById("search_form");
		//List<HtmlButton> searchSubmitButtons = (List<HtmlButton>) htmlPage.getByXPath("//button[@type='submit']");
		//System.out.println(jobsPage);
		
		HtmlForm submitForm = htmlTextInput2.getEnclosingForm();
		
		
		System.out.println("submitForm:" + submitForm);
		
		HtmlElement button = (HtmlElement) htmlPage.createElement("button");
		button.setAttribute("type", "submit");
		
		submitForm.appendChild(button);
		
		HtmlPage jobsPage = button.click();
		
		System.out.println(" jobsPage : " + jobsPage.getUrl());
		
		//HTMLDivElement divResultsContainer = (HTMLDivElement) jobsPage.getElementById("serp");
		List<HtmlDivision> resultDivs = (List<HtmlDivision>) jobsPage.getByXPath( "//div[@class='job-row']");
		
		System.out.println(resultDivs);
		
		for(int i=0; i<resultDivs.size(); i++) {
			
			JobDTO jobDto = new JobDTO();
			
			HtmlDivision htmlDivElement = resultDivs.get(i);
			
			System.out.println("Position : " + htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			jobDto.setTitle(htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
			System.out.println("Location : " + "http://www.careerbuilder.com/" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			jobDto.setLink("http://www.careerbuilder.com/" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			
			System.out.println("Description : " + ((HtmlElement)htmlDivElement.getByXPath("//div[@class='job-description show-for-medium-up']").get(i)).getTextContent());
			jobDto.setDescription(((HtmlElement)htmlDivElement.getByXPath("//div[@class='job-description show-for-medium-up']").get(i)).getTextContent());
			
			
			System.out.println("Location : " + ((HtmlElement)htmlDivElement.getByXPath("//h4[@class='job-text']").get(i)).getTextContent().replace("\n", ""));
			jobDto.setLocation(((HtmlElement)htmlDivElement.getByXPath("//h4[@class='job-text']").get(i)).getTextContent().replace("\n", ""));
			
			System.out.println("Company Name : " + htmlDivElement.getElementsByTagName("a").item(1).getTextContent());
			jobDto.setCompany(htmlDivElement.getElementsByTagName("a").item(1).getTextContent());
			
			jobDto.setDataFrom("http://www.careerbuilder.com/");
			
			jobDtos.add(jobDto);
		}
		
		webClient.closeAllWindows();
		
		return jobDtos;
	}
	
	@RequestMapping(value = "/doRegister", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String doRegister(@RequestBody RegisterDTO registerDto) {
		
		System.out.println("Inside DoRegister : " + registerDto.getEmailId() + " : " + registerDto.getPhoneNumber() + " : " + registerDto.getKeywords());
		
		try {
			
			  Class.forName("com.mysql.jdbc.Driver");

		      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/jobsCrawlerDB","root","password");

		      Statement  stmt = conn.createStatement();
		      
		      stmt.executeUpdate("INSERT INTO userDetails (emailId, phoneNumber, jobKeywords, location) values('" + registerDto.getEmailId() + "', '" + registerDto.getPhoneNumber() + "', '" + registerDto.getKeywords() + "', '" + registerDto.getLocation() +  "')");
		      
		      stmt.close();
		      
		      conn.close();
		      
		 
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "{\"response\" : \"Registered Successfully\"}";
		//return new JobDTO();
	}
	
}

