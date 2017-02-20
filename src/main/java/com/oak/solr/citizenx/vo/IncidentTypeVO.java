package com.oak.solr.citizenx.vo;

import org.codehaus.jackson.map.ObjectMapper;

import com.oak.solr.entities.Content;

public class IncidentTypeVO {
	private String id;
	private String name;
	private Question[] questions;

	public IncidentTypeVO(){}
	
	public IncidentTypeVO(Content content){

			ObjectMapper mapper = new ObjectMapper();
		
			this.id = content.getId();
			this.name = content.getName();
			try {
				this.questions = mapper.readValue(content.getDescription(), Question[].class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Question[] getQuestions() {
		return questions;
	}

	public void setQuestions(Question[] questions) {
		this.questions = questions;
	}

	@Override
	public String toString() {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return null;
		}
	}

}
