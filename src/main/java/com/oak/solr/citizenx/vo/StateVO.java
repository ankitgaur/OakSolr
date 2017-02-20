package com.oak.solr.citizenx.vo;

import org.codehaus.jackson.map.ObjectMapper;

import com.oak.solr.entities.Content;

public class StateVO {
	private String id;
	private String name;
	private String abbr;
	private String capital;
	private String []govts;
	
	public StateVO(){
		
	}
	
	public StateVO(Content content){
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			StateVO svo = mapper.readValue(content.getDescription(), StateVO.class);
			this.id = content.getId();
			this.name = svo.getName();
			this.abbr = svo.getAbbr();
			this.capital = svo.getCapital();
			this.govts = svo.getGovts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public String[] getGovts() {
		return govts;
	}
	public void setGovts(String[] govts) {
		this.govts = govts;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
