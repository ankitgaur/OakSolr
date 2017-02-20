package com.oak.solr.citizenx.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

import com.oak.solr.citizenx.vo.IncidentVO;
import com.oak.solr.entities.Content;
import com.oak.solr.entities.User;
import com.oak.solr.services.ContentService;
import com.oak.solr.services.UserService;

@RestController
public class IncidentController {

	@CrossOrigin
	@RequestMapping(value = "/incidents", produces = "application/json", method = RequestMethod.GET)
	public List<IncidentVO> getIncidents() throws JsonGenerationException,
			JsonMappingException, IOException, SolrServerException {
		List<Content> incidents = ContentService.searchByType("incidents", 0).getResults();

		List<IncidentVO> ivos = new ArrayList<IncidentVO>();
		for (Content incident : incidents) {
			IncidentVO vo = new IncidentVO(incident);
			Date createOn = new Date(incident.getCreatedon());
			//Date reportDate = new Date(incident.getReportDate());
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm:ss");
			String createDateText = dateFormatter.format(createOn);
			//String reportDateText = dateFormatter.format(reportDate);
			vo.setCreatedOnStr(createDateText);
			//vo.setReportDateStr(reportDateText);
			ivos.add(vo);
		}

		return ivos;
	}

	@CrossOrigin
	@RequestMapping(value = "/incidents/{id}", produces = "application/json", method = RequestMethod.GET)
	public IncidentVO getIncidentById(
			@PathVariable("id") String id)
			throws JsonParseException, JsonMappingException, IOException, SolrServerException {

		Content incident = ContentService.searchById(id).getResults().get(0);
		if (incident == null) {
			return null;
		}
		IncidentVO incidentVO = new IncidentVO(incident);
		Date createOn = new Date(incident.getCreatedon());
		//Date reportDate = new Date(incident.getReportDate());
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss");
		String createDateText = dateFormatter.format(createOn);
		//String reportDateText = dateFormatter.format(reportDate);
		incidentVO.setCreatedOnStr(createDateText);
		//incidentVO.setReportDateStr(reportDateText);

		return incidentVO;
	}

	@CrossOrigin
	@RequestMapping(value = "/incidents", method = RequestMethod.POST)
	public ResponseEntity<Void> createIncident(
			@RequestParam("type") String type,
			@RequestParam("state") String state,
			@RequestParam("govt") String govt,
			@RequestParam(name = "displayImage", required = false) MultipartFile displayImage,
			@RequestParam("category") String category,
			@RequestParam("description") String description,
			@RequestParam("questions") String questions)
			throws JsonGenerationException, JsonMappingException, IOException,
			ParseException, SolrServerException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;
		//
		// if (tags != null && !tags.isEmpty()) {
		// taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		// }

		String image_id = null;

		if (displayImage != null && displayImage.getBytes() != null && displayImage.getBytes().length > 0) {
			image_id = ContentService.uploadImage(user, taglist, displayImage);
		}

		
		questions = addStateGovt(questions,state,govt);
		
		Content ct = new Content();
		ct.setContent_type("incidents");
		ct.setName(description);
		ct.setDescription(questions);
		//ct.setIntro(intro);
		ct.setCreatedby(user.getId());
		ct.setAuthor(user.getUsername());
		ct.setCreatedon(new Date().getTime());
		ct.setTags(taglist);
		ct.setImage_id(image_id);
		
		//ct.setParent_id(parent_id);
		ct.setParent_name(type);

		ContentService.add(ct.createSolrInputDoc());
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@CrossOrigin
	@RequestMapping(value = "/incidents/{id}", produces = "application/json", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteIncident(@PathVariable("id") String id)
			throws JsonParseException, JsonMappingException, IOException, SolrServerException {
		ContentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody
	String handleException(Exception ex) {
		if (ex.getMessage().contains("UNIQUE KEY"))
			return "The submitted item already exists.";
		else
			System.out.println(this.getClass() + ": need handleException for: "
					+ ex.getMessage());
		return ex.getMessage();
	}
	
	private String addStateGovt(String questions,String state,String govt){
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			HashMap<String, String> map = mapper.readValue(questions,
					new TypeReference<HashMap<String, String>>() {
					});
			map.put("state", state);
			map.put("govt", govt);
			questions = mapper.writeValueAsString(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return questions;
	}
}
