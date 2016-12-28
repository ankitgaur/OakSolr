package com.oak.solr.controllers;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.oak.exceptions.JwtTokenMissingException;
import com.oak.solr.entities.User;
import com.oak.solr.services.UserService;
import com.oak.solr.vo.LoginVO;
import com.oak.utils.JWTUtil;

@RestController
public class LoginController {

	@CrossOrigin
	@RequestMapping(value = "/whoami", produces = "application/json", method = RequestMethod.POST)
	public LoginVO whoami(HttpServletRequest request, HttpServletResponse response) throws SolrServerException, IOException {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			System.out.println("Where i expected");
			throw new JwtTokenMissingException("No JWT token found in request headers");
		}

		String token = header.substring(7);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		LoginVO lvo = new LoginVO();
		lvo.setName(user.getName());
		lvo.setUsername(user.getUsername());
		lvo.setGroups(user.getGroups());
		lvo.setToken(token);

		return lvo;

	}

	@CrossOrigin
	@RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.GET)
	public LoginVO login(HttpServletRequest request, HttpServletResponse response) throws SolrServerException, IOException {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Basic ")) {
			return null;
		}

		String encrypted = header.substring(6);

		String decrypted = new String(Base64.getDecoder().decode(encrypted));

		String[] credentials = decrypted.split(":");

		String email = credentials[0].trim();
		String passwd = credentials[1].trim();

		User user = UserService.searchByEmail(email).getResults().get(0);

		boolean activated = user.getStatus().equals(UserService.ACTIVATED);
		
		String encoded = user.getPassword();
		String decoded = new String(Base64.getDecoder().decode(encoded));
		
		if (user != null &&  activated && decoded.equals(passwd)) {

			String ip = request.getRemoteAddr();
			String token = JWTUtil.generateToken(user, ip);

			LoginVO lvo = new LoginVO();
			lvo.setName(user.getName());
			lvo.setUsername(user.getUsername());
			lvo.setGroups(user.getGroups());
			lvo.setToken(token);

			UserService.update(user.getId(), "jwt", token);

			return lvo;
		}

		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/logoff", produces = "application/json", method = RequestMethod.GET)
	public String logoff(HttpServletRequest request, HttpServletResponse response) throws SolrServerException, IOException {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			return null;
		}

		String token = header.substring(7);

		UserService.deleteToken(token.trim());

		return "{\"status\":\"loggedoff\"}";
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleException(Exception ex) {
		if (ex.getMessage().contains("UNIQUE KEY"))
			return "The submitted item already exists.";
		else
			System.out.println(this.getClass() + ": need handleException for: " + ex.getMessage());
		return ex.getMessage();
	}

}
