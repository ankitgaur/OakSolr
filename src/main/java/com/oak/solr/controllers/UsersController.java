package com.oak.solr.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.oak.solr.entities.User;
import com.oak.solr.services.ContentService;
import com.oak.solr.services.MailService;
import com.oak.solr.services.UserService;
import com.oak.solr.vo.UserResponseVO;
import com.oak.utils.JWTUtil;
import com.oak.utils.MailUtil;

@RestController
public class UsersController {

	@CrossOrigin
	@RequestMapping(value = "/users", produces = "application/json", method = RequestMethod.GET)
	public UserResponseVO getAllUsers(int start)
			throws JsonParseException, JsonMappingException, IOException, SolrServerException {
		return UserService.listAll(start);
	}

	@CrossOrigin
	@RequestMapping(value = "/users/{id}", produces = "application/json", method = RequestMethod.GET)
	public UserResponseVO getUserById(@PathVariable("id") String id)
			throws JsonParseException, JsonMappingException, IOException, SolrServerException {
		return UserService.searchById(id);

	}

	@CrossOrigin
	@RequestMapping(value = "/users", consumes = "application/json", method = RequestMethod.POST)
	public ResponseEntity<Void> createUser(@RequestParam("name") String name, @RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("captcha") String captcha, @RequestParam("intro") String intro,
			@RequestParam(name = "displayImage", required = false) MultipartFile displayImage)
			throws JsonParseException, JsonMappingException, IOException, SolrServerException {

		// TODO recaptcha logic
		if (captcha != null && !captcha.isEmpty()) {

			UUID uuid = UUID.randomUUID();
			String token = uuid.toString();

			List<String> groups = new ArrayList<String>();
			groups.add("registered");

			User user = new User();
			user.setStatus(UserService.NEW);
			user.setJwt(token);
			user.setName(name);
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
			user.setIntro(intro);

			String id = UserService.add(user.createSolrInputDoc());

			user.setId(id);

			String image_id = null;

			if (displayImage != null && displayImage.getBytes() != null && displayImage.getBytes().length > 0) {
				image_id = ContentService.uploadImage(user, null, displayImage);
			}

			user.setImage_id(image_id);

			UserService.update(id, "image_id", image_id);

			MailService.sendWelcomeMail(user);

			HttpHeaders headers = new HttpHeaders();
			return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		}

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);

	}

	@CrossOrigin
	@RequestMapping(value = "/users/{id}", consumes = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateRole(@PathVariable("id") String id, @RequestParam("field") String field,
			@RequestParam("value") String value) throws JsonGenerationException, JsonMappingException, IOException, SolrServerException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String uid = authentication.getName();
		
		UserService.update(id, field, value);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	@CrossOrigin
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteRole(@PathVariable("id") String id) throws SolrServerException, IOException {
		System.out.println("Fetching & Deleting User with id " + id);

		User user = UserService.searchById(id).getResults().get(0);
		if (user == null) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		UserService.update(user.getId(), "status", UserService.DELETED);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@CrossOrigin
	@RequestMapping(value = "/activate/{id}/{code}", method = RequestMethod.GET)
	public ResponseEntity<Void> activateUser(@PathVariable("id") String id, @PathVariable("code") String code)
			throws URISyntaxException, SolrServerException, IOException {

		User user = UserService.searchById(id).getResults().get(0);

		System.out.println("Activating " + id + " " + code);

		if (user.getJwt().equals(code)) {

			UserService.update(user.getId(), "status", UserService.ACTIVATED);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(new URI("http://www.ipledge2nigeria.com"));
			return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
		}

		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}

	@CrossOrigin
	@RequestMapping(value = "/forgotPassword/{emailID:.+}", method = RequestMethod.GET)
	public ResponseEntity<Void> forgotPassword(@PathVariable("emailID") String emailID)
			throws URISyntaxException, IOException, SolrServerException {

		try {

			User user = UserService.searchByEmail(emailID).getResults().get(0);

			// String token = JWTUtil.generateToken(user, "");

			// UserService.update(user.getId(), "jwt", token);

			MailService.sendForgotPasswordMail(user);

		} catch (Exception e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

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
