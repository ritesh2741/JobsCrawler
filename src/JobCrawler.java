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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;

public class JobCrawler {

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		JobCrawler j = new JobCrawler();
		j.extractFromDice();
		
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

}
