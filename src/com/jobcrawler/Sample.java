package com.jobcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.jobcrawler.dto.JobDTO;

public class Sample {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		List<JobDTO> diceJobDto = new ArrayList<>();
		
		WebClient webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);

		HtmlPage htmlPage = webClient.getPage("http://www.dice.com/");
		HtmlTextInput htmlTextInput1 = (HtmlTextInput) htmlPage.getElementById("search-field-keyword");
		htmlTextInput1.setValueAttribute("Java Developer");
		HtmlTextInput htmlTextInput2 = (HtmlTextInput) htmlPage.getElementById("search-field-location");
		htmlTextInput2.setValueAttribute("Akron, OH");
		
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
		
	     
	}
	
}
