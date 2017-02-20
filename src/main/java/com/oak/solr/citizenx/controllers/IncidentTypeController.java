package com.oak.solr.citizenx.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.oak.solr.citizenx.vo.IncidentTypeVO;
import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;
import com.oak.solr.vo.ContentResponseVO;

@RestController
public class IncidentTypeController {

	@CrossOrigin
	@RequestMapping(value = "/incidentTypes/{id}", produces = "application/json", method = RequestMethod.GET)
	public IncidentTypeVO getIncidentTypeById(@PathVariable String id) throws SolrServerException, IOException {

		ContentResponseVO cvo = ContentService.searchById(id);
		Content content = cvo.getResults().get(0);
		return new IncidentTypeVO(content);
	}

	@CrossOrigin
	@RequestMapping(value = "/incidentTypes", produces = "application/json", method = RequestMethod.GET)
	public List<IncidentTypeVO> getIncidentTypes() throws SolrServerException, IOException {

		ContentResponseVO cvo = ContentService.getAllOfType("incidentTypes", 10);
		List<IncidentTypeVO> vos = new ArrayList<IncidentTypeVO>();

		for (Content content : cvo.getResults()) {
			vos.add(new IncidentTypeVO(content));
		}

		return vos;
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
}
