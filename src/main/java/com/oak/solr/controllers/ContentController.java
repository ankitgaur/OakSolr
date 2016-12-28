package com.oak.solr.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oak.solr.entities.Content;
import com.oak.solr.entities.User;
import com.oak.solr.services.ContentService;
import com.oak.solr.services.UserService;
import com.oak.solr.vo.ContentResponseVO;

@RestController
public class ContentController {

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getContentById(@PathVariable String id) throws SolrServerException, IOException {
		
		try{
			ContentService.incrHits(id);
		}
		catch(Exception e){
			//TODO : Logging
		}
		
		return ContentService.searchById(id);
	}

	@CrossOrigin
	@RequestMapping(value = "/image/{id}", produces = "application/json", method = RequestMethod.GET)
	public byte[] getImageById(@PathVariable String id) throws SolrServerException, IOException {

		String encoded = ContentService.searchById(id).getResults().get(0).getDescription();
		byte[] content = Base64.getDecoder().decode(encoded);
		return content;
	}

	@CrossOrigin
	@RequestMapping(value = "/content/type/{type}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getContentByTypeAndTags(@PathVariable String type,
			@RequestParam(name = "tags", required = false) String tags,
			@RequestParam(name = "start", required = false) Integer start) throws SolrServerException, IOException {

		if(start==null){
			start=0;
		}
		
		if (tags == null || tags.isEmpty()) {
			return ContentService.searchByType(type, start);
		}

		return ContentService.searchByTypeAndTag(type, new ArrayList<String>(Arrays.asList(tags.split(" "))), start);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/popular/{type}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getPopularContentByTypeAndTags(@PathVariable String type,
			@RequestParam(name = "tags", required = false) String tags,
			@RequestParam(name = "start", required = false) Integer start) throws SolrServerException, IOException {

		if(start==null){
			start=0;
		}
		
		if (tags == null || tags.isEmpty()) {
			return ContentService.searchPopularByType(type, start);
		}
		
		return ContentService.searchPopularByTypeAndTag(type, new ArrayList<String>(Arrays.asList(tags.split(" "))),
				start);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContent(@PathVariable("id") String id) throws SolrServerException, IOException {
		ContentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@CrossOrigin
	@RequestMapping(value = "/content", method = RequestMethod.POST)
	public ResponseEntity<Void> createArticle(@RequestParam("tags") String tags, @RequestParam("title") String title,
			@RequestParam("content_type") String content_type, @RequestParam("content") String content,
			@RequestParam("intro") String intro,
			@RequestParam(name = "displayImage", required = false) MultipartFile displayImage)
			throws ParseException, IOException, SolrServerException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;

		if (tags != null && !tags.isEmpty()) {
			taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		}

		String image_id = null;

		if (displayImage != null && displayImage.getBytes() != null && displayImage.getBytes().length > 0) {
			image_id = ContentService.uploadImage(user, taglist, displayImage);
		}

		Content ct = new Content();
		ct.setContent_type(content_type);
		ct.setName(title);
		ct.setDescription(content);
		ct.setIntro(intro);
		ct.setCreatedby(user.getId());
		ct.setAuthor(user.getUsername());
		ct.setCreatedon(new Date().getTime());
		ct.setTags(taglist);
		ct.setImage_id(image_id);

		ContentService.add(ct.createSolrInputDoc());
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@CrossOrigin
	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadImage(@RequestParam("tags") String tags,
			@RequestParam(name = "displayImage", required = true) MultipartFile displayImage)
			throws ParseException, IOException, SolrServerException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;

		if (tags != null && !tags.isEmpty()) {
			taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		}

		ContentService.uploadImage(user, taglist, displayImage);

		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", consumes = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateArticle(@PathVariable("id") String id, @RequestParam("field") String field,
			@RequestParam("value") String value) throws ParseException, SolrServerException, IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String uid = authentication.getName();

		// User user = UserService.searchById(id).getResults().get(0);

		ContentService.update(id, field, value);

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
