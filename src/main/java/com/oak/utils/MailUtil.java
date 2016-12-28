package com.oak.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	
	public static void sendMail(String to,String subject,String mailbody){

	      String host="mail.ipledge2nigeria.com";
	      String from="info@ipledge2nigeria.com";
		
		
			// Get system properties
	      Properties properties = new Properties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject(subject);
	         
	         message.setContent(mailbody, "text/html; charset=utf-8");

	         // Send message
	         Transport.send(message);
	        
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }

		
		
	}
}
