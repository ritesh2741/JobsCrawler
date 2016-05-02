import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender {

	public static void main(String[] args) {
		

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
				InternetAddress.parse("jobscrawlerteam@gmail.com"));
			message.setSubject("Job Crawler Notification");
			message.setText("Hi ,"
				+ "\n\nBelow are Jobs posted matching your criteria :\n\n"
					+ "Thanks,"
					+ "Jobs Crawler Team"
					);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
