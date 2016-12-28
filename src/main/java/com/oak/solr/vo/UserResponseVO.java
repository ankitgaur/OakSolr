package com.oak.solr.vo;

import java.util.List;

import com.oak.solr.entities.User;

public class UserResponseVO {

	private long total;
	private String status;
	private int status_code;
	private List<User> results;

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

	public List<User> getResults() {
		return results;
	}

	public void setResults(List<User> results) {
		this.results = results;
	}

}
