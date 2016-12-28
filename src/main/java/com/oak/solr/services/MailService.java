package com.oak.solr.services;

import java.util.Base64;

import com.oak.solr.entities.User;
import com.oak.utils.MailUtil;

public class MailService {

	public static void sendWelcomeMail(User user) {

		String sub = "Your Ipledge2nigeria.com account is created - Activation required";
		String activationUrl = "http://www.ipledge2nigeria.com/service/activate/" + user.getId() + "/"
				+ user.getJwt();

		String mailbody = "Hi,<br>Thank you for joining. Please click the below link to activate your account :- <br><a href='"
				+ activationUrl + "'>" + activationUrl + "</a>";

		MailUtil.sendMail(user.getEmail(), sub, mailbody);
	}

	public static void sendForgotPasswordMail(User user) {
		String sub = "Your Ipledge2nigeria.com password";

		String mailbody = "Hi,<br>Your Password is :- " + new String(Base64.getDecoder().decode(user.getPassword()));

		MailUtil.sendMail(user.getEmail(), sub, mailbody);
	}

}
