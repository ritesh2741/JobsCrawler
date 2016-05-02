import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.w3c.dom.html.HTMLDivElement;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;

public class JobCrawler {

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		JobCrawler j = new JobCrawler();
		j.extractFromCareerBuilder();;
		
	}
	
	public void extractFromIndeed() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		HtmlPage htmlPage = webClient.getPage("http://www.indeed.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("what");
		htmlTextInput1.setValueAttribute("Java Developer");
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("where");
		htmlTextInput2.setValueAttribute("Akron, OH");
		HtmlSubmitInput findJobButton = (HtmlSubmitInput) htmlPage.getElementById("fj");
		HtmlPage jobsPage = findJobButton.click();
		//System.out.println(jobsPage);
		
		List<HtmlAnchor> anchors = jobsPage.getAnchors();
		DomNodeList<DomElement> companyAndlocationSpans = jobsPage.getElementsByTagName("span");
		
		for(HtmlAnchor anchor : anchors) {
			
			//System.out.println(anchor.getAttribute("class"));
			
			if(anchor.getAttribute("class").equals("jobtitle turnstileLink")) {
				System.out.println("Title : " + anchor.getTextContent());
				System.out.println("Link : " + anchor.getAttribute("href"));
			}
			
		}
		
for(DomElement companyAndlocationSpan : companyAndlocationSpans) {
			
			//System.out.println(anchor.getAttribute("class"));
			
			if(companyAndlocationSpan.getAttribute("class").equals("company")) {
				System.out.println("Company : " + companyAndlocationSpan.getTextContent());
			}
			else if(companyAndlocationSpan.getAttribute("class").equals("location")) {
				System.out.println("Location : " + companyAndlocationSpan.getTextContent());
			}
			else if(companyAndlocationSpan.getAttribute("class").equals("summary")) {
				System.out.println("Description : " + companyAndlocationSpan.getTextContent());
			}
			
		}
		
		webClient.closeAllWindows();
	}
	
	public void extractFromDice() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		HtmlPage htmlPage = webClient.getPage("http://www.dice.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("search-field-keyword");
		htmlTextInput1.setValueAttribute("Java Developer");
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("search-field-location");
		htmlTextInput2.setValueAttribute("Akron, OH");
		HtmlForm submitForm = (HtmlForm) htmlPage.getElementById("search-form");
		List<HtmlButton> searchSubmitButtons = (List<HtmlButton>) htmlPage.getByXPath("//button[@type='submit']");
		//System.out.println(jobsPage);
		
		HtmlPage jobsPage = searchSubmitButtons.get(0).click();
		
		//HTMLDivElement divResultsContainer = (HTMLDivElement) jobsPage.getElementById("serp");
		List<HTMLDivElement> resultDivs = (List<HTMLDivElement>) jobsPage.getByXPath( "//div[@class='serp-result-content']");
		
		System.out.println(resultDivs);
		
		for(HTMLDivElement htmlDivElement : resultDivs) {
			
			System.out.println("Position : " + htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
		}
		
		webClient.closeAllWindows();
	}
	
	public void extractFromCareerJet() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		HtmlPage htmlPage = webClient.getPage("http://www.careerjet.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("sb_s");
		htmlTextInput1.setValueAttribute("Java Developer");
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("sb_l");
		htmlTextInput2.setValueAttribute("Akron, OH");
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
			
			HtmlDivision htmlDivElement = resultDivs.get(i);
			
			System.out.println("Position : " + htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
			System.out.println("Location : " + "http://www.careerjet.com" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			
			System.out.println("Description : " + ((HtmlElement)htmlDivElement.getByXPath("//div[@class='advertise_compact']").get(i)).getTextContent());
			
			System.out.println("Location : " + ((HtmlElement)htmlDivElement.getByXPath("//span[@itemprop='jobLocation']").get(i)).getTextContent().replace("\n", ""));
			
			System.out.println("Company Name : " + ((HtmlElement)htmlDivElement.getByXPath("//span[@class='company_compact']").get(i)).getTextContent());
			
			
		}
		
		webClient.closeAllWindows();
	}
	
	public void extractFromCareerBuilder() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
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
			
			HtmlDivision htmlDivElement = resultDivs.get(i);
			
			System.out.println("Position : " + htmlDivElement.getElementsByTagName("a").item(0).getTextContent());
			
			System.out.println("Location : " + "http://www.careerbuilder.com/" + ((HtmlAnchor)htmlDivElement.getElementsByTagName("a").item(0)).getAttribute("href"));
			
			System.out.println("Description : " + ((HtmlElement)htmlDivElement.getByXPath("//div[@class='job-description show-for-medium-up']").get(i)).getTextContent());
			
			System.out.println("Location : " + ((HtmlElement)htmlDivElement.getByXPath("//h4[@class='job-text']").get(i)).getTextContent().replace("\n", ""));
			
			System.out.println("Company Name : " + htmlDivElement.getElementsByTagName("a").item(1).getTextContent());
			
			
		}
		
		webClient.closeAllWindows();
	}

}
