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

import com.oak.solr.citizenx.vo.StateVO;
import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;
import com.oak.solr.vo.ContentResponseVO;

@RestController
public class StatesController {

	@CrossOrigin
	@RequestMapping(value = "/states/{id}", produces = "application/json", method = RequestMethod.GET)
	public StateVO getStateById(@PathVariable String id) throws SolrServerException, IOException {

		ContentResponseVO cvo = ContentService.searchById(id);
		Content content = cvo.getResults().get(0);
		return new StateVO(content);
	}

	@CrossOrigin
	@RequestMapping(value = "/states", produces = "application/json", method = RequestMethod.GET)
	public List<StateVO> getStates() throws SolrServerException, IOException {

		ContentResponseVO cvo = ContentService.getAllOfType("states", 50);
		List<StateVO> svos = new ArrayList<StateVO>();

		for (Content content : cvo.getResults()) {
			svos.add(new StateVO(content));
		}

		return svos;
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
