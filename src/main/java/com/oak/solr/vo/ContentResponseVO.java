package com.oak.solr.vo;

import java.util.List;

import com.oak.solr.entities.Content;

public class ContentResponseVO {

	private long total;
	private String status;
	private int status_code;
	private List<Content> results;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public List<Content> getResults() {
		return results;
	}

	public void setResults(List<Content> results) {
		this.results = results;
	}

}
