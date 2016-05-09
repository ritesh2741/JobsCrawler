package com.jobcrawler.batch.mail;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jobcrawler.dto.JobDTO;
import com.jobcrawler.dto.RegisterDTO;
import com.jobcrawler.rest.JobCrawlerRestService;


public class MailSender {

	public static void main(String[] args) throws IOException {
		
			MailSender mailSender = new MailSender();
			
			List<RegisterDTO> registerDtoList = mailSender.getDatabaseRecords();
			
			JobCrawlerRestService jobCrawlerRestService = new JobCrawlerRestService();
			
			
			for(RegisterDTO registerDto : registerDtoList) {
				
				String[] keyWordSplits = registerDto.getKeywords().split(",");
				
				for(String jobKeyWord : keyWordSplits) {
					
					jobKeyWord = jobKeyWord.trim();
					
					if(jobKeyWord.length() > 0) {
					
						System.out.println("Send email to : " + registerDto.getEmailId() + " Keyword : " + jobKeyWord + ", location : " + registerDto.getLocation());
					
						JobDTO jobDto = new JobDTO();
						jobDto.setTitle(jobKeyWord);
						jobDto.setLocation(registerDto.getLocation());;
						
						List<JobDTO> indeedJobDtos = new ArrayList<JobDTO>();
						List<JobDTO> diceJobDtos = new ArrayList<JobDTO>();
						List<JobDTO> careerJetJobDtos = new ArrayList<JobDTO>();
						List<JobDTO> careerBuilderJobDtos = new ArrayList<JobDTO>();
						
						try {
							indeedJobDtos = jobCrawlerRestService.getJobsFromIndeed(jobDto);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						try {
							diceJobDtos = jobCrawlerRestService.getJobsFromDice(jobDto);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						try {
							careerJetJobDtos = jobCrawlerRestService.getJobsFromCareerJet(jobDto);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						try {
							careerBuilderJobDtos = jobCrawlerRestService.getJobsFromCareerBuilder(jobDto);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						String content = "Hi,<br><br>";
						
						if(indeedJobDtos.size() > 0) {
							content += "<span style='font-size: 20px'><b>Jobs matching from indeed : </b></span><br><br>" + mailSender.getContentFromJobDtoList(indeedJobDtos);
						}
						
						if(diceJobDtos.size() > 0) {
							content += "<span style='font-size: 20px'><b>Jobs matching from dice : </b></span><br><br>" + mailSender.getContentFromJobDtoList(diceJobDtos);
						}
						
						if(careerJetJobDtos.size() > 0) {
							content += "<span style='font-size: 20px'><b>Jobs matching from career jet : </b></span><br><br>" + mailSender.getContentFromJobDtoList(careerJetJobDtos);
						}
						
						if(careerBuilderJobDtos.size() > 0) {
							content += "<span style='font-size: 20px'><b>Jobs matching from career builder : </b></span><br><br>" + mailSender.getContentFromJobDtoList(careerBuilderJobDtos);
						}
						
						
						
						content += "Thanks,<br>Job Crawler Team";
						
						if(indeedJobDtos.size() > 0 || 
								diceJobDtos.size() > 0 ||
								careerJetJobDtos.size() > 0 ||
								careerBuilderJobDtos.size() > 0
								) {
							
							mailSender.sendEmail(registerDto.getEmailId(), "JobsCrawler Notification for " + jobKeyWord + " @ " + registerDto.getLocation(), content);
							
						}
						
						
					}
				}
			
		}
		
	}
	
	public String getContentFromJobDtoList(List<JobDTO> jobDtosList) {
		
		String content = "";
		
		for(JobDTO jobDTO : jobDtosList) {
			
			content += "<b>Job Title : </b><a href='" + jobDTO.getLink() + "' target='_blank'>" + jobDTO.getTitle() + "</a><br>";
			content += "<b>Company : </b>" + jobDTO.getCompany() + "<br>";
			content += "<b>Location : </b>" + jobDTO.getLocation() + "<br>";
			content += "<b>Description : </b>" + jobDTO.getDescription() + "<br>";
			content += "<br>";
			
		}
		
		return content;
		
	}
	
	public void sendEmail(String toMailId, String subject, String content) {
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication("jobscrawlerteam@gmail.com", "d.saigoud");
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("jobscrawlerteam@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toMailId));
			message.setSubject(subject);
			//message.setText(content);
			message.setContent(content, "text/html; charset=utf-8");

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<RegisterDTO> getDatabaseRecords() {
		
		List<RegisterDTO> registerDtosList = new ArrayList<RegisterDTO>();
		
		try {
			
			  Class.forName("com.mysql.jdbc.Driver");

		      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/jobsCrawlerDB","root","password");

		      Statement  stmt = conn.createStatement();
		      
		      ResultSet rs = stmt.executeQuery("SELECT emailId, phoneNumber, jobKeywords, location FROM userDetails");
		      
		      while(rs.next()) {
		    	  
		    	  RegisterDTO registerDto = new RegisterDTO();
		    	  registerDto.setEmailId(rs.getString(1));
		    	  registerDto.setPhoneNumber(rs.getString(2));
		    	  registerDto.setKeywords(rs.getString(3));
		    	  registerDto.setLocation(rs.getString(4));
		    	  
		    	  registerDtosList.add(registerDto);
		    	  
		      }
		      
		      stmt.close();
		      
		      conn.close();
		 
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return registerDtosList;
	}
	
}
